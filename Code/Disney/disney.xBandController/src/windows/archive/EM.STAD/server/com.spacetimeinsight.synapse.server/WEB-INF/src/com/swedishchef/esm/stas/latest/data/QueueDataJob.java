package com.swedishchef.esm.stas.latest.data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.spacetimeinsight.config.scheduler.Parameters;
import com.spacetimeinsight.magma.job.CustomJobTask;

public class QueueDataJob implements CustomJobTask {

	public boolean executeCustomTask(Parameters params){


		try 
		{
			DataManager dataManager  = DataManager.getInstance();
			if(!dataManager.hasAttractionConstants())
			{
				System.out.println("Attraction Constants are null");
				dataManager.instantiateAttractionConstants();
				DBAccessHelper.updateAttractionConstants();
			}
			DBAccessHelper.loadParks();
			
			Map<String, ArrayList<Object>> data = DBAccessHelper.getSensorData() ;
			for (Iterator it = data.keySet().iterator(); it.hasNext();) 
			{
				Object currentAttraction = it.next();
				ArrayList<Object> dataList = data.get(currentAttraction);
				dataManager.buildAttraction(currentAttraction.toString(),
													Integer.parseInt(dataList.get(0).toString()), 
													Integer.parseInt(dataList.get(1).toString()), 
													Integer.parseInt(dataList.get(2).toString()), 
													Integer.parseInt(dataList.get(3).toString()),
													Double.parseDouble(dataList.get(4).toString()),
													Double.parseDouble(dataList.get(5).toString()),
													Integer.parseInt(dataList.get(6).toString()), 
													Integer.parseInt(dataList.get(7).toString()),
													Double.parseDouble(dataList.get(8).toString()),
													Double.parseDouble(dataList.get(9).toString()));			
				} 
		}catch (Exception e) 
		{ 
			e.printStackTrace();
		}
		return true;
	}
}
