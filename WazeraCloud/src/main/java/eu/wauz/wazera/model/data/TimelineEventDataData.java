package eu.wauz.wazera.model.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.primefaces.model.timeline.TimelineEvent;

public class TimelineEventDataData extends TimelineEvent {
	
	private static final long serialVersionUID = -4718058938304185563L;
	
	private Integer id;
	
	public TimelineEventDataData(Integer id, String name, Date date) {
		super(name, date);
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDate() {
    	return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(getStartDate());
    }

}
