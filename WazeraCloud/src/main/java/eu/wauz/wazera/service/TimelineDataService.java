package eu.wauz.wazera.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import eu.wauz.wazera.model.data.TimelineEventDataData;
import eu.wauz.wazera.model.entity.TimelineEventData;
import eu.wauz.wazera.model.repository.TimelineEventDataRepository;

@Service
@Scope("singleton")
public class TimelineDataService {
	
	@Autowired
	private TimelineEventDataRepository timelineEventDataRepository;
	
	public TimelineModel getModel() throws Exception {
		TimelineModel model = new TimelineModel();
		
		for(TimelineEventData data : timelineEventDataRepository.findByDateAfterOrWeekly(new Date(), true)) {
			model.addAll(getEvents(data));
		}
		return model;
	}
	
	private List<TimelineEvent> getEvents(TimelineEventData data) {
		List<TimelineEvent> events = new ArrayList<>();
		if(data.isWeekly()) {
			Calendar eventData = Calendar.getInstance();
	        eventData.setTime(data.getDate());
	        
	        Calendar currentDate = getCalendar();
	        currentDate.set(Calendar.DAY_OF_WEEK, eventData.get(Calendar.DAY_OF_WEEK));
	        currentDate.set(Calendar.HOUR_OF_DAY, eventData.get(Calendar.HOUR_OF_DAY));
	        currentDate.set(Calendar.MINUTE, eventData.get(Calendar.MINUTE));
	        
	        for(int i = 0; i < 12; i++) {
	        	TimelineEventDataData event = new TimelineEventDataData(data.getId(), data.getName(), currentDate.getTime());
	        	currentDate.add(Calendar.WEEK_OF_YEAR, 1);
	        	events.add(event);
	        }
		}
		else {
			TimelineEventDataData event = new TimelineEventDataData(data.getId(), data.getName(), data.getDate());
			events.add(event);
		}
		return events;
	}
	
	public Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
        return calendar;
	}
	
	public Calendar getCalendarInTwoWeeks() {
		Calendar calendar = getCalendar();
		calendar.add(Calendar.WEEK_OF_YEAR, 2);
		return calendar;
	}
	
	public void saveTimelineEvent(String name, Date date, boolean weekly) {
		TimelineEventData data = new TimelineEventData();
		data.setName(name);
		data.setDate(date);
		data.setWeekly(weekly);
		timelineEventDataRepository.save(data);
	}
	
	public void deleteTimelineEvent(Integer id) {
		timelineEventDataRepository.deleteById(id);
	}

}
