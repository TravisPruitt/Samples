package com.disney.xband.xbrms.server.scheduler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;

import com.disney.xband.common.scheduler.SchedulerItemParameter;
import com.disney.xband.common.scheduler.XconnectSchedulerJob;
import com.disney.xband.common.scheduler.XconnectSchedulerJobContext;
import com.disney.xband.xbrms.common.model.ProblemAreaType;
import com.disney.xband.xbrms.common.model.XbrcDto;
import com.disney.xband.xbrms.server.managed.XbrcPerfService;
import com.disney.xband.xbrms.server.model.HealthItemDao;
import com.disney.xband.xbrms.server.model.ProblemsReportBo;

@DisallowConcurrentExecution
public class DataCorruptionDetectionJob extends XconnectSchedulerJob {

	private static Logger logger = Logger.getLogger(DataCorruptionDetectionJob.class);
	
	@Override
	public void run(XconnectSchedulerJobContext context) throws Exception 
	{
		detectMoreThanTwoXbrcsWithTheSameFacilityId();
	}

	@Override
	public void abort(XconnectSchedulerJobContext context) throws Exception {
		// nothing to do
	}
	
	private void detectMoreThanTwoXbrcsWithTheSameFacilityId()
	{
		Map<String,Collection<String>> facilityIdCount = HealthItemDao.getInstance().countFacilityIds();
		
		if (facilityIdCount == null || facilityIdCount.size() == 0)
			return;
		
		for (Entry<String, Collection<String>> entry : facilityIdCount.entrySet())
		{
			if (entry.getValue() == null)
				continue;
			
			if (entry.getValue().size() <= 2)
				continue;
			
			String error = "Data corruption detected. Facility ID " + entry.getKey() + " set for more than two xBRCs: " 
					+ Arrays.toString(entry.getValue().toArray());
			
			logger.error(error);
			
			ProblemsReportBo.getInstance().setLastError(
					ProblemAreaType.DataCorruption_FacilityId, 
					error
			);
		}
	}

}
