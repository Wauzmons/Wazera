package eu.wauz.wazera.model.entity.docs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "DocumentTag")
public class DocumentTag implements Serializable {

	private static final long serialVersionUID = 4186709479708958436L;

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String value;

	@Column
	private Integer documentId;

	public DocumentTag() {
	}

	public DocumentTag(Integer documentId, String value) {
		this.documentId = documentId;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getDocumentId() {
		return documentId;
	}

	public void setDocumentId(Integer documentId) {
		this.documentId = documentId;
	}

	@Override
	public String toString() {
		return "DocumentTag(" + (getId() != null ? String.valueOf(getId()) : "transient") + ") " + value;
	}
}
