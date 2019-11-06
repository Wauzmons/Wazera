package eu.wauz.wazera.controller.tree;

import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.FolderData;

public class RootFolderTreeNode extends FolderTreeNode {

    private static final long serialVersionUID = 3852332535514502777L;

    public RootFolderTreeNode(FolderData folderData, TreeNode parent) {
        super("rootNode", folderData, parent);
    }

}
