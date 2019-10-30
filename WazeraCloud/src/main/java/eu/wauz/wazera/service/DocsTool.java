package eu.wauz.wazera.service;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.wauz.wazera.model.entity.Document;
import eu.wauz.wazera.model.entity.Folder;

public class DocsTool {

	public void checkForValidFileName(String fileName) throws Exception {
		if(StringUtils.containsAny(fileName, new char[] {'|', '/', '\\', ':', '*', '?', '"', '<', '>'})) {
			throw new Exception("The input contains invalid characters!");
		}
	}

	public List<Document> sortDocuments(List<Document> sortDocs) throws Exception {
		for(Document doc : sortDocs)
			if(doc.getSortOrder() == null)
				doc.setSortOrder(0);

		sortDocs.sort(Comparator.comparingInt(Document::getSortOrder));
		for(int i = 0; i < sortDocs.size(); i++)
			sortDocs.get(i).setSortOrder(i);

		return sortDocs;
	}

	public List<Folder> sortFolders(List<Folder> sortFolders) throws Exception {
		for(Folder folder : sortFolders)
			if(folder.getSortOrder() == null)
				folder.setSortOrder(0);

		sortFolders.sort(Comparator.comparingInt(Folder::getSortOrder));
		for(int i = 0; i < sortFolders.size(); i++)
			sortFolders.get(i).setSortOrder(i);

		return sortFolders;
	}
}
