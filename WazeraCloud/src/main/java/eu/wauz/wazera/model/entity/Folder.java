package eu.wauz.wazera.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "Folder")
public class Folder implements Serializable {

	private static final long serialVersionUID = 4186709479708958436L;

	@Transient
	private static final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory.getLog(Folder.class);

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String name;

	@Column
	private String directory;

	@Column
	private Integer treeId;

	@Column
	private Integer folderId;

	@Column
	private Integer sortOrder;

	@Column
	private Boolean deleted;

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

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
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
		return "Folder(" + (getId() != null ? String.valueOf(getId()) : "transient") + ")";
	}
}
