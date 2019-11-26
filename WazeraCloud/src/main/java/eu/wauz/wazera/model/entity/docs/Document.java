package eu.wauz.wazera.model.entity.docs;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "Document")
public class Document implements Serializable {

	private static final long serialVersionUID = 5477340625500758152L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

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

	@Lob
	@Column
	private String content;

	@Column
	private Integer sortOrder;

	@Column
	private Date creationDate;

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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	@Override
	public String toString() {
		return "Document(" + (getId() != null ? String.valueOf(getId()) : "transient") + ") " + getName();
	}

}
