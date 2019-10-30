package eu.wauz.wazera.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FolderData {

	private Integer id;

	private String name;

	private String directory;

	private FolderData parent;

	private Boolean expanded;

	private Boolean deleted;

	private List<FolderData> folders = new ArrayList<>();

	private List<DocumentData> documents = new ArrayList<>();

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

	public FolderData getParent() {
		return parent;
	}

	public void setParent(FolderData parent) {
		this.parent = parent;
	}

	public Boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public List<FolderData> getFolders() {
		return folders;
	}

	public void setFolders(List<FolderData> folders) {
		this.folders = folders;
	}

	public List<DocumentData> getDocuments() {
		return documents;
	}

	public void setDocuments(List<DocumentData> documents) {
		this.documents = documents;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FolderData) {
			return Objects.equals(getId(), ((FolderData) obj).getId());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "FolderData(" + (getId() != null ? String.valueOf(getId()) : "transient") + " - '" + name + "')";
	}

}
