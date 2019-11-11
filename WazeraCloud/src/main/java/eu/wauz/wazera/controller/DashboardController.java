package eu.wauz.wazera.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import eu.wauz.wazera.model.data.TimelineEventDataData;
import eu.wauz.wazera.service.TimelineDataService;

@Controller
@Scope("view")
public class DashboardController implements Serializable {

	private static final long serialVersionUID = -4314266036201867674L;
	
	@Autowired
	private TimelineDataService timelineService;
	
	private TimelineEventDataData selectedTimelineEvent;
	
	private String timelineEventName;
	
	private Date timelineEventDate;
	
	private boolean timelineEventWeekly;
	
	public String ping() {
		return "Connected to Dashboard Controller!";
	}
	
	public TimelineModel getTimeline() {
		TimelineModel timelineModel = null;
		try {
			timelineModel = timelineService.getModel();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return timelineModel;
	}
	
	public Date getTimelineStart() {
		Calendar timelineStart = timelineService.getCalendar();
		timelineStart.add(Calendar.DAY_OF_YEAR, - 1);
		return timelineStart.getTime();
	}
	
	public Date getTimelineEnd() {
		Calendar timelineEnd = timelineService.getCalendarInTwoWeeks();
		timelineEnd.add(Calendar.DAY_OF_YEAR, + 1);
		return timelineEnd.getTime();
	}
	
	public void saveTimelineEvent() {
		timelineService.saveTimelineEvent(timelineEventName, timelineEventDate, timelineEventWeekly);
	}
	
	public void deleteTimelineEvent() {
		timelineService.deleteTimelineEvent(selectedTimelineEvent.getId());
	}
	
	public void onTimelineEventSelect(TimelineSelectEvent e) {
        selectedTimelineEvent = (TimelineEventDataData) e.getTimelineEvent();
    }

	public TimelineEventDataData getSelectedTimelineEvent() {
		return selectedTimelineEvent;
	}

	public void setSelectedTimelineEvent(TimelineEventDataData selectedTimelineEvent) {
		this.selectedTimelineEvent = selectedTimelineEvent;
	}

	public String getTimelineEventName() {
		return timelineEventName;
	}

	public void setTimelineEventName(String timelineEventName) {
		this.timelineEventName = timelineEventName;
	}

	public Date getTimelineEventDate() {
		return timelineEventDate;
	}

	public void setTimelineEventDate(Date timelineEventDate) {
		this.timelineEventDate = timelineEventDate;
	}

	public boolean isTimelineEventWeekly() {
		return timelineEventWeekly;
	}

	public void setTimelineEventWeekly(boolean timelineEventWeekly) {
		this.timelineEventWeekly = timelineEventWeekly;
	}

}
