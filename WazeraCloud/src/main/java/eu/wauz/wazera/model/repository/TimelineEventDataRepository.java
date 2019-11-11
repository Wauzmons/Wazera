package eu.wauz.wazera.model.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.wauz.wazera.model.entity.TimelineEventData;

public interface TimelineEventDataRepository extends CrudRepository<TimelineEventData, Integer> {
	
	public List<TimelineEventData> findByDateAfterOrWeekly(Date date, boolean weekly);

}
