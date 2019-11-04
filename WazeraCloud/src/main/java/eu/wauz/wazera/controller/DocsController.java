package eu.wauz.wazera.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.TreeDragDropEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.controller.tree.PfDocumentTreeNode;
import eu.wauz.wazera.controller.tree.PfFolderTreeNode;
import eu.wauz.wazera.controller.tree.PfRootFolderTreeNode;
import eu.wauz.wazera.model.data.DocumentData;
import eu.wauz.wazera.model.data.FolderData;
import eu.wauz.wazera.service.DocumentsDataService;
import eu.wauz.wazera.service.FoldersDataService;

@Controller
@Scope("view")
public class DocsController {

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

	private Integer docId;

	public DocsController() {
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
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication.getName();
	}

	private void addFolderNodes(FolderData folderNode, TreeNode treeNode, boolean isRootNode) {
		PfFolderTreeNode node = null;
		if (isRootNode) {
			node = new PfRootFolderTreeNode(folderNode, treeNode);
			node.setExpanded(true);
		}
		else {
			node = new PfFolderTreeNode(folderNode, treeNode);
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
		PfDocumentTreeNode node = new PfDocumentTreeNode(documentNode, treeNode);
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
		
		if (selectedNode instanceof PfDocumentTreeNode) {
			inputName = ((PfDocumentTreeNode) selectedNode).getName();
			content = ((PfDocumentTreeNode) selectedNode).getDocumentData().getContent();
			documentTags = ((PfDocumentTreeNode) selectedNode).getDocumentData().getTags();
		}
		else {
			inputName = ((PfFolderTreeNode) selectedNode).getName();
			content = "";
			documentTags = new ArrayList<>();
		}
	}

	public void addDirectoryNode() {
		PfFolderTreeNode parent = (PfFolderTreeNode) selectedNode;

		FolderData folderData = new FolderData();
		folderData.setName(inputName);
		folderData.setParent(parent.getFolderData());

		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new PfFolderTreeNode(folderData, selectedNode);
		newNode.setExpanded(true);

		documentTree = null;
		inputName = "";
	}
	
	public void addDocumentNode() {
		PfFolderTreeNode parent = (PfFolderTreeNode) selectedNode;

		DocumentData documentData = new DocumentData();
		documentData.setName(inputName);
		documentData.setParent(parent.getFolderData());
		documentData.setContent("");

		try {
			documentData = documentsService.saveDocument(documentData, null, getUsername());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new PfDocumentTreeNode(documentData, selectedNode);
		newNode.setExpanded(true);

		documentTree = null;
		inputName = "";
	}

	public void renameDirectoryNode() {
		PfFolderTreeNode selectedFolderData = (PfFolderTreeNode) selectedNode;
		selectedFolderData.getFolderData().setName(inputName);

		try {
			foldersService.saveFolder(selectedFolderData.getFolderData(), null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		documentTree = null;
		inputName = "";
	}

	public void renameDocumentNode() {
		PfDocumentTreeNode selectedDocumentData = (PfDocumentTreeNode) selectedNode;;
		selectedDocumentData.getDocumentData().setName(inputName);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null, getUsername());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		documentTree = null;
		inputName = "";
	}

	public boolean showEditor() {
		return selectedNode != null && selectedNode instanceof PfDocumentTreeNode;
	}

	public void saveDocument() {
		PfDocumentTreeNode selectedDocumentData = (PfDocumentTreeNode) selectedNode;

		selectedDocumentData.getDocumentData().setContent(content);
		selectedDocumentData.getDocumentData().setTags(documentTags);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null, getUsername());
			showInfoMessage("Your Document was saved!");
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
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

	public void selectTree() {
		documentTree = new DefaultTreeNode("documentTree", null);

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}
		if(searchTags.size() == 0 && selectedNode instanceof PfDocumentTreeNode) {
			docId = ((PfDocumentTreeNode) selectedNode).getDocumentData().getId();
		}

		try {
			rootNodeData = documentsService.getDocuments(foldersService.getRootFolder().getId(), docId, searchTags);
			addFolderNodes(rootNodeData, documentTree, true);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}
	
	public void onNodeExpand(NodeExpandEvent event) {
		if(hasSearchTags()) {
			return;
		}

		FolderData folderData = ((PfFolderTreeNode) event.getTreeNode()).getFolderData();
		folderData.setExpanded(true);
		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}

		FolderData result = null;
		try {
			result = documentsService.getDocuments(folderData.getId(), null, searchTags);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
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
		if(hasSearchTags() || !(treeNode instanceof PfFolderTreeNode)) {
			return;
		}
		
		treeNode.setExpanded(false);
		
		FolderData folderData = ((PfFolderTreeNode)treeNode).getFolderData();
		folderData.setExpanded(false);
		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	public void deleteFolder() {
		PfFolderTreeNode node = (PfFolderTreeNode) selectedNode;

		try {
			foldersService.deleteFolder(node.getFolderData());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		selectTree();
	}

	public void deleteDocument() {
		PfDocumentTreeNode node = (PfDocumentTreeNode) selectedNode;
		
		try {
			documentsService.deleteDocument(node.getDocumentData());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
		
		selectTree();
	}

	public void onDragDrop(TreeDragDropEvent event) {
		TreeNode dragNode = event.getDragNode();
		TreeNode dropNode = event.getDropNode();
		int dropIndex = event.getDropIndex();

		if (dropNode instanceof PfFolderTreeNode) {
			PfFolderTreeNode dropFolderNode = (PfFolderTreeNode) dropNode;
			if (dragNode instanceof PfFolderTreeNode) {
				PfFolderTreeNode dragFolderNode = (PfFolderTreeNode) dragNode;
				dragFolderNode.getFolderData().setParent(dropFolderNode.getFolderData());
				try {
					foldersService.saveFolder(dragFolderNode.getFolderData(), dropIndex);
				}
				catch (Exception e) {
					showErrorMessage(e.getMessage());
				}
			}
			else if (dragNode instanceof PfDocumentTreeNode) {
				PfDocumentTreeNode dragDocumentNode = (PfDocumentTreeNode) dragNode;
				dragDocumentNode.getDocumentData().setParent(dropFolderNode.getFolderData());
				try {
					documentsService.saveDocument(dragDocumentNode.getDocumentData(), dropIndex, getUsername());
				}
				catch (Exception e) {
					showErrorMessage(e.getMessage());
				}
			}
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

    		if(selectedNode instanceof PfDocumentTreeNode) {
				Integer docId = selectedNode != null ? ((PfDocumentTreeNode) selectedNode).getDocumentData().getId() : 0;
				showInfoMessage("Link copied to Clipboard!");
				return baseUrl + "WazeraCloud/docs.xhtml?docId=" + docId;
    		}
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
    	return "";
    }
    
    public void showInfoMessage(String infoMessage) {
		final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Info:", infoMessage);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
    
	public void showErrorMessage(String errorMessage) {
		final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler:", errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

}
