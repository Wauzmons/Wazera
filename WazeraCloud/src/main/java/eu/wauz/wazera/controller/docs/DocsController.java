package eu.wauz.wazera.controller.docs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.faces.context.FacesContext;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.docs.DocumentData;
import eu.wauz.wazera.model.data.docs.FolderData;
import eu.wauz.wazera.service.DocsTool;
import eu.wauz.wazera.service.DocumentsDataService;
import eu.wauz.wazera.service.FoldersDataService;

@Controller
@Scope("view")
public class DocsController implements Serializable {

	private static final long serialVersionUID = -7261056043638925780L;

	@Autowired
	private DocumentsDataService documentsService;

	@Autowired
	private FoldersDataService foldersService;

	private TreeNode documentTree;
	
	private TreeNode selectedNode;

	private FolderData rootNodeData;
	
	private List<String> documentTags;
	
	private List<String> searchTags;
	
	private String inputName;
	
	private String content;
	
	private boolean allowEditing;
	
	private boolean allowSorting;

	private Integer docId;
	
	private DocsTool docsTool;

	public DocsController() {
		docsTool = new DocsTool();
		
		Object doctIdObject = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("docId");
		if(doctIdObject != null)
		{
			String docIdString = (String) doctIdObject;
			docId = Integer.valueOf(docIdString);
		}
	}

	public TreeNode getDocumentTree() {
		if (documentTree == null) {
			selectTree();
		}
		return documentTree;
	}

	public String getUsername() {
		return docsTool.getUsername();
	}

	private void addFolderNodes(FolderData folderNode, TreeNode treeNode, boolean isRootNode) {
		FolderTreeNode node = null;
		if (isRootNode) {
			node = new RootFolderTreeNode(folderNode, treeNode);
			node.setExpanded(true);
		}
		else {
			node = new FolderTreeNode(folderNode, treeNode);
			node.setExpanded(folderNode.isExpanded() != null ? folderNode.isExpanded() : false);
		}
		
		for (FolderData childNode : folderNode.getFolders()) {
			addFolderNodes(childNode, node, false);
		}
		for (DocumentData childNode : folderNode.getDocuments()) {
			addDocumentNodes(childNode, node);
		}
	}

	private void addDocumentNodes(DocumentData documentNode, TreeNode treeNode) {
		DocumentTreeNode node = new DocumentTreeNode(documentNode, treeNode);
		node.setExpanded(true);
		
		if(Objects.equals(documentNode.getId(), docId)) {
			node.setSelected(true);
			setSelectedNode(node);
			docId = null;
		}
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;

		if (selectedNode == null) {
			return;
		}
		
		if (selectedNode instanceof DocumentTreeNode) {
			inputName = ((DocumentTreeNode) selectedNode).getName();
			content = ((DocumentTreeNode) selectedNode).getDocumentData().getContent();
			documentTags = ((DocumentTreeNode) selectedNode).getDocumentData().getTags();
		}
		else {
			inputName = ((FolderTreeNode) selectedNode).getName();
			content = "";
			documentTags = new ArrayList<>();
		}
	}

	public void addDirectoryNode() {
		FolderTreeNode parent = (FolderTreeNode) selectedNode;

		FolderData folderData = new FolderData();
		folderData.setName(inputName);
		folderData.setParent(parent.getFolderData());

		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new FolderTreeNode(folderData, selectedNode);
		newNode.setExpanded(true);

		documentTree = null;
		inputName = "";
	}
	
	public void addDocumentNode() {
		FolderTreeNode parent = (FolderTreeNode) selectedNode;

		DocumentData documentData = new DocumentData();
		documentData.setName(inputName);
		documentData.setParent(parent.getFolderData());
		documentData.setContent("");

		try {
			documentData = documentsService.saveDocument(documentData, null, getUsername());
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new DocumentTreeNode(documentData, selectedNode);
		newNode.setExpanded(true);

		documentTree = null;
		inputName = "";
	}

	public void renameDirectoryNode() {
		FolderTreeNode selectedFolderData = (FolderTreeNode) selectedNode;
		selectedFolderData.getFolderData().setName(inputName);

		try {
			foldersService.saveFolder(selectedFolderData.getFolderData(), null);
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		documentTree = null;
		inputName = "";
	}

	public void renameDocumentNode() {
		DocumentTreeNode selectedDocumentData = (DocumentTreeNode) selectedNode;
		selectedDocumentData.getDocumentData().setName(inputName);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null, getUsername());
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		documentTree = null;
		inputName = "";
	}

	public boolean showEditor() {
		return selectedNode != null && selectedNode instanceof DocumentTreeNode;
	}

	public void saveDocument() {
		DocumentTreeNode selectedDocumentData = (DocumentTreeNode) selectedNode;

		selectedDocumentData.getDocumentData().setContent(content);
		selectedDocumentData.getDocumentData().setTags(documentTags);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null, getUsername());
			docsTool.showInfoMessage("Your Document was saved!");
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public boolean isAllowEditing() {
		return allowEditing;
	}

	public void setAllowEditing(boolean allowEditing) {
		this.allowEditing = allowEditing;
	}

	public boolean isAllowSorting() {
		return allowSorting;
	}

	public void setAllowSorting(boolean allowSorting) {
		this.allowSorting = allowSorting;
	}

	public void selectTree() {
		documentTree = new DefaultTreeNode("documentTree", null);

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}
		if(searchTags.size() == 0 && selectedNode instanceof DocumentTreeNode) {
			docId = selectedNode != null ? ((DocumentTreeNode) selectedNode).getDocumentData().getId() : 0;
		}

		try {
			rootNodeData = documentsService.getDocuments(foldersService.getRootFolder().getId(), docId, searchTags);
			addFolderNodes(rootNodeData, documentTree, true);
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void onNodeExpand(NodeExpandEvent event) {
		if(hasSearchTags()) {
			return;
		}

		FolderData folderData = ((FolderTreeNode) event.getTreeNode()).getFolderData();
		folderData.setExpanded(true);
		try {
			if(folderData.getId() != null) {
				foldersService.saveFolder(folderData, null);
			}
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}

		FolderData result = null;
		try {
			result = documentsService.getDocuments(folderData.getId(), null, searchTags);
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		FolderData expandedFolderData = result;
		TreeNode treeNode = event.getTreeNode();

		while(treeNode.getChildCount() != 0) {
			treeNode.getChildren().remove(0);
		}

		for (FolderData childNode : expandedFolderData.getFolders()) {
			addFolderNodes(childNode, treeNode, false);
		}
		for (DocumentData childNode : expandedFolderData.getDocuments()) {
			addDocumentNodes(childNode, treeNode);
		}
	}

	public void onNodeCollapse(NodeCollapseEvent event) {
		collapse(event.getTreeNode());
	}

	public void collapseAll() {
		collapseRecursive(documentTree);
	}

	public void collapseRecursive(TreeNode parentNode) {
		for(TreeNode childNode : parentNode.getChildren()) {
			collapseRecursive(childNode);
		}
		collapse(parentNode);
	}

	public void collapse(TreeNode treeNode) {
		if(hasSearchTags() || !(treeNode instanceof FolderTreeNode)) {
			return;
		}
		
		treeNode.setExpanded(false);
		
		FolderData folderData = ((FolderTreeNode)treeNode).getFolderData();
		folderData.setExpanded(false);
		try {
			if(folderData.getId() != null) {
				foldersService.saveFolder(folderData, null);
			}
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}
	}

	public void deleteFolder() {
		FolderTreeNode node = (FolderTreeNode) selectedNode;

		try {
			foldersService.deleteFolder(node.getFolderData());
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}

		selectTree();
	}

	public void deleteDocument() {
		DocumentTreeNode node = (DocumentTreeNode) selectedNode;
		
		try {
			documentsService.deleteDocument(node.getDocumentData());
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}
		
		selectTree();
	}

	public void onDragDrop(TreeDragDropEvent event) {
		TreeNode dragNode = event.getDragNode();
		TreeNode dropNode = event.getDropNode();
		int dropIndex = event.getDropIndex();

		try {
			if (dropNode instanceof FolderTreeNode) {
				FolderTreeNode dropFolderNode = (FolderTreeNode) dropNode;
				if (dragNode instanceof FolderTreeNode) {
					FolderTreeNode dragFolderNode = (FolderTreeNode) dragNode;
					dragFolderNode.getFolderData().setParent(dropFolderNode.getFolderData());
					foldersService.saveFolder(dragFolderNode.getFolderData(), dropIndex);
				}
				else if (dragNode instanceof DocumentTreeNode) {
					DocumentTreeNode dragDocumentNode = (DocumentTreeNode) dragNode;
					dragDocumentNode.getDocumentData().setParent(dropFolderNode.getFolderData());
					documentsService.saveDocument(dragDocumentNode.getDocumentData(), dropIndex, getUsername());
				}
			}
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}
		selectTree();
	}
	
	public String getName() {
		return inputName;
	}

	public void setName(String name) {
		this.inputName = name;
	}

	private boolean hasSearchTags() {
		return searchTags != null && !searchTags.isEmpty();
	}

	public List<String> getTags() {
		return documentTags;
	}

	public void setTags(List<String> tags) {
		this.documentTags = tags == null ? new ArrayList<>() : tags;
	}

	public List<String> getSearchTags() {
		return searchTags;
	}

	public void setSearchTags(List<String> searchTags) {
		this.searchTags = searchTags;
	}

    public String getDocumentLink() {
    	try {
    		String baseUrl = "http://localhost:8080/";

    		if(selectedNode instanceof DocumentTreeNode) {
				Integer docId = selectedNode != null ? ((DocumentTreeNode) selectedNode).getDocumentData().getId() : 0;
				return baseUrl + "WazeraCloud/docs.xhtml?docId=" + docId;
    		}
		}
		catch (Exception e) {
			docsTool.showErrorMessage(e.getMessage());
		}
    	return "";
    }

}
