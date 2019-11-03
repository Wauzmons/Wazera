package eu.wauz.wazera.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.SystemUtils;
import org.primefaces.context.RequestContext;
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

	private String documentTreeName;

	private Map<String, TreeNode> nodeMap;

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
		nodeMap.put(node.getName(), node);
		
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
		nodeMap.put(node.getName(), node);
	}

	public boolean isFile(String name) {
		boolean value = false;
		value = nodeMap.get(name) instanceof PfDocumentTreeNode;
		return value;
	}

	public boolean isDirectory(String name) {
		boolean value = false;
		value = nodeMap.get(name) instanceof PfFolderTreeNode;
		return value;
	}

	private TreeNode selectedNode;

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;

		if (selectedNode == null) {
			return;
		}
		
		if (selectedNode instanceof PfDocumentTreeNode) {
			name = ((PfDocumentTreeNode) selectedNode).getName();
			content = ((PfDocumentTreeNode) selectedNode).getDocumentData().getContent();
			tags = ((PfDocumentTreeNode) selectedNode).getDocumentData().getTags();
		}
		else {
			name = ((PfFolderTreeNode) selectedNode).getName();
			content = "";
			tags = new ArrayList<>();
		}
	}

	public void addDirectoryNode() {
		PfFolderTreeNode parent = (PfFolderTreeNode) selectedNode;

		FolderData folderData = new FolderData();
		folderData.setName(name);
		folderData.setParent(parent.getFolderData());

		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new PfFolderTreeNode(folderData, selectedNode);
		newNode.setExpanded(true);
		nodeMap.put(name, newNode);

		documentTree = null;
		name = "";
	}

	public void renameDirectoryNode() {
		PfFolderTreeNode selectedFolderData = (PfFolderTreeNode) selectedNode;
		selectedFolderData.getFolderData().setName(name);

		try {
			foldersService.saveFolder(selectedFolderData.getFolderData(), null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		documentTree = null;
		name = "";
	}

	public void mergeDirectory() {
		PfFolderTreeNode selectedFolderData = (PfFolderTreeNode) selectedNode;

		try {
			documentsService.mergeDocuments(selectedFolderData.getFolderData(), getUsername());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
    	selectTree();
	}

	public boolean showEditor() {
		return selectedNode != null && selectedNode instanceof PfDocumentTreeNode;
	}

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void saveDocument() {
		PfDocumentTreeNode selectedDocumentData = (PfDocumentTreeNode) selectedNode;

		selectedDocumentData.getDocumentData().setContent(content);
		selectedDocumentData.getDocumentData().setTags(tags);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null, getUsername());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	private String name;

	private boolean validName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isValidName() {
		return validName;
	}

	public void setValidName(boolean validName) {
		this.validName = validName;
	}

	public void showErrorMessage(String errorMessage) {
		errorMessage = errorMessage.replace("- DocsInvalidNameException", "");
		final FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Fehler:", errorMessage);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	public void addDocumentNode() {
		PfFolderTreeNode parent = (PfFolderTreeNode) selectedNode;

		DocumentData documentData = new DocumentData();
		documentData.setName(name);
		documentData.setContent("");
		documentData.setParent(parent.getFolderData());

		try {
			documentData = documentsService.saveDocument(documentData, null, getUsername());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new PfDocumentTreeNode(documentData, selectedNode);
		newNode.setExpanded(true);
		nodeMap.put(name, newNode);

		documentTree = null;
		name = "";
	}

	public void renameDocumentNode() {

		PfDocumentTreeNode selectedDocumentData = (PfDocumentTreeNode) selectedNode;;

		selectedDocumentData.getDocumentData().setName(name);

		try {
			documentsService.saveDocument(selectedDocumentData.getDocumentData(), null, getUsername());
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		documentTree = null;
		name = "";
	}

	public String getDocumentTreeName() {
		return documentTreeName;
	}

	public void setDocumentTreeName(String documentTreeName) {
		this.documentTreeName = documentTreeName;
	}

	public int getTreeId() {
		return treeId;
	}

	public void setTreeId(int treeId) {
		this.treeId = treeId;
		try {
			documentTreeName = foldersService.getFolderName(treeId);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
	}

	public FolderData getRootFolderData() {
		try {
			return foldersService.getRootFolder();
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
			return null;
		}
	}

	public void addTree() {
		FolderData folderData = new FolderData();
		folderData.setName(documentTreeName);
		folderData.setParent(null);

		try {
			foldersService.saveFolder(folderData, null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		TreeNode newNode = new PfFolderTreeNode(folderData, null);
		newNode.setExpanded(true);
		nodeMap = new HashMap<>();
		nodeMap.put(name, newNode);

		documentTree = null;
	}

	public void selectTree() {
		nodeMap = new HashMap<>();
		documentTree = new DefaultTreeNode("documentTree", null);

		if(searchTags == null) {
			searchTags = new ArrayList<String>();
		}

		if(treeId == null) {
			treeId = getRootFolderData().getId();
		}
		try {
			documentTreeName = foldersService.getFolderName(treeId);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		if(searchTags.size() == 0 && selectedNode instanceof PfDocumentTreeNode) {
			docId = ((PfDocumentTreeNode) selectedNode).getDocumentData().getId();
		}

		FolderData result = null;
		try {
			result = documentsService.getDocuments(treeId, docId, searchTags);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		rootNodeData = result;
		addFolderNodes(rootNodeData, documentTree, true);
	}

	private boolean hasSearchTags() {
		return searchTags != null && !searchTags.isEmpty();
	}

	public void onNodeExpand(NodeExpandEvent event) {
		if(hasSearchTags())
			return;

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
		if(treeNode instanceof PfFolderTreeNode) {
			treeNode.setExpanded(false);

			if(hasSearchTags())
				return;

			FolderData folderData = ((PfFolderTreeNode)treeNode).getFolderData();
			folderData.setExpanded(false);
			try {
				foldersService.saveFolder(folderData, null);
			}
			catch (Exception e) {
				showErrorMessage(e.getMessage());
			}
		}
	}

	public void deleteTree() {
		try {
			foldersService.deleteTree(treeId);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
		treeId = getRootFolderData().getId();
		documentTreeName = getRootFolderData().getName();

		selectTree();
	}

	public void deleteNode() {
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

	public void renameTree() {
		PfFolderTreeNode selectedFolderData = (PfFolderTreeNode) selectedNode;
		selectedFolderData.getFolderData().setName(documentTreeName);

		try {
			foldersService.saveFolder(selectedFolderData.getFolderData(), null);
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}

		documentTree = null;
		name = "";
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

	private List<String> tags;

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags == null ? new ArrayList<>() : tags;
	}

	private List<String> searchTags;

	private FolderData rootNodeData;

	private Integer treeId;

	private Integer docId;

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
				return baseUrl + "WazeraCloud/docs.xhtml?docId=" + docId;
    		}
		}
		catch (Exception e) {
			showErrorMessage(e.getMessage());
		}
    	return "";
    }

	public void updateEditorContent() {
		RequestContext requestContext = RequestContext.getCurrentInstance();
		String editorContent = content.replaceAll("\"", "").replaceAll("'", "").replaceAll("\n", "").replaceAll("\r", "");
		requestContext.execute("CKEDITOR.instances.editor.setData('" + editorContent + "')");
	}

}
