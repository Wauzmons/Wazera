package eu.wauz.wazera.controller.docs;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import eu.wauz.wazera.model.data.docs.DocumentData;

public class DocumentTreeNode extends DefaultTreeNode {

	private static final long serialVersionUID = -2541631707902646133L;

	private DocumentData documentData;

	public DocumentTreeNode(DocumentData documentData, TreeNode parent) {
		super("documentNode", documentData.getName(), parent);
		this.documentData = documentData;
	}

    public String getName() {
    	return String.valueOf(getData());
    }
    
    public String getUser() {
    	return StringUtils.isBlank(documentData.getUser()) ? "unknown" : documentData.getUser();
    }
    
    public String getDate() {
    	return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(documentData.getCreationDate());
    }

	public DocumentData getDocumentData() {
		return documentData;
	}

}
