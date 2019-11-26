package eu.wauz.wazera.model.data.docs;

import java.util.Date;
import java.util.List;

public class DocumentData {

	private Integer id;

	private String name;
	
	private String user;

	private String content;

	private FolderData parent;

	private List<String> tags;

	private Date creationDate;

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

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public FolderData getParent() {
		return parent;
	}

	public void setParent(FolderData parent) {
		this.parent = parent;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

}
