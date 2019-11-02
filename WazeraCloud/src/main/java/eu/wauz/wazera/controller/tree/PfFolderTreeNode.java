package eu.wauz.wazera.controller.tree;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.FolderData;

public class PfFolderTreeNode extends DefaultTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;

    private FolderData folderData;

    public PfFolderTreeNode(FolderData folderData, TreeNode parent) {
        super("directoryNode", folderData.getName(), parent);
        this.folderData = folderData;
    }
    
    public PfFolderTreeNode(String nodeType, FolderData folderData, TreeNode parent) {
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
