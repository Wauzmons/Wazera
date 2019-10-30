package eu.wauz.wazera.model.data;

import java.io.Serializable;
import java.util.Date;

public class HistoryDocumentData implements Serializable {

	private static final long serialVersionUID = -3226589714125303559L;

	private Integer id;

	private Integer documentId;

	private Integer versionNumber;

	private String user;

	private Date lastModified;

	private String content;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "HistoryDocumentData(" + (getId() != null ? String.valueOf(getId()) : "transient") + ")";
	}

}
