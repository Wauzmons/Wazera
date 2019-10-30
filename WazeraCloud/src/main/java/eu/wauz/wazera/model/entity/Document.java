package eu.wauz.wazera.model.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Document")
public class Document implements Serializable {

	private static final long serialVersionUID = 5477340625500758152L;
	
	@Transient
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Document.class);

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private int type;

	@Column
	private String name;

	@Column
	private String user;

	@Column
	private String absoluteFilename;

	@Column
	private Integer treeId;

	@Column
	private Integer folderId;

	@Column
	private String content;

	@Column
	private String textContent;

	@Column
	private Integer sortOrder;

	@Column
	private Date creationDate;

	@Column
	private Integer versionNumber;

	@Column
	private Boolean deleted;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Integer getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(Integer versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbsoluteFilename() {
		return absoluteFilename;
	}

	public void setAbsoluteFilename(String absoluteFilename) {
		this.absoluteFilename = absoluteFilename;
	}

	public Integer getTreeId() {
		return treeId;
	}

	public void setTreeId(Integer treeId) {
		this.treeId = treeId;
	}

	public Integer getFolderId() {
		return folderId;
	}

	public void setFolderId(Integer folderId) {
		this.folderId = folderId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "Document(" + (getId() != null ? String.valueOf(getId()) : "transient") + ") " + getName();
	}

}
