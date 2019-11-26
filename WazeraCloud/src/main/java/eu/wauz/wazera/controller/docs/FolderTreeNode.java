package eu.wauz.wazera.controller.docs;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.FolderData;

public class FolderTreeNode extends DefaultTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;

    private FolderData folderData;

    public FolderTreeNode(FolderData folderData, TreeNode parent) {
        super("directoryNode", folderData.getName(), parent);
        this.folderData = folderData;
    }
    
    public FolderTreeNode(String nodeType, FolderData folderData, TreeNode parent) {
        super(nodeType, folderData.getName(), parent);
        this.folderData = folderData;
    }

	public String getName() {
    	return String.valueOf(getData());
    }

	public FolderData getFolderData() {
		return folderData;
	}

}
