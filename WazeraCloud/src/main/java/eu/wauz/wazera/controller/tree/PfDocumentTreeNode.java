package eu.wauz.wazera.controller.tree;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.DocumentData;

public class PfDocumentTreeNode extends DefaultTreeNode {

	private static final long serialVersionUID = -2541631707902646133L;

	private DocumentData documentData;

	public PfDocumentTreeNode(DocumentData documentData, TreeNode parent) {
		super("documentNode", documentData.getName(), parent);
		this.documentData = documentData;
	}

    public String getName() {
    	return String.valueOf(getData());
    }

	public DocumentData getDocumentData() {
		return documentData;
	}

}
