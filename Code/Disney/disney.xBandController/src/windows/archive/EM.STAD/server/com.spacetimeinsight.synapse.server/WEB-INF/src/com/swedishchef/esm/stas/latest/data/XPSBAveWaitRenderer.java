package com.swedishchef.esm.stas.latest.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enterprisehorizons.chartdata.model.Chartdata;
import com.enterprisehorizons.chartdata.model.Chartdataroot;
import com.enterprisehorizons.chartdata.model.Dateseriesdata;
import com.enterprisehorizons.magma.renderer.chart.ChartDataXMLRenderer;
import com.swedishchef.esm.stas.latest.data.DataManager;
import com.swedishchef.esm.stas.latest.artifacts.QueueCountArtifact;
import com.enterprisehorizons.util.DateUtils;
import com.swedishchef.esm.stas.latest.artifacts.AttractionArtifact;


public class XPSBAveWaitRenderer extends ChartDataXMLRenderer {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss,SSS");
	
	@Override
	public Object render(Map params){
		//System.out.println("In QueueAveWaitDataRenderer " + params.get("attractionName").toString());
		List<QueueCountArtifact> queueList =  DataManager.getInstance().attractionLookup(params.get("attractionName").toString());
		Chartdataroot root = new Chartdataroot(); //root tag
	    HashMap<String, Chartdata> hm = new HashMap<String, Chartdata>();
		Date minDate = DateUtils.getFirstDateOfTheMonth(DateUtils.getCurrentDate());
		
	    int i = 0;
	    Date timestamp = null;
	    //for all artifacts in list
	    
	    
		for (QueueCountArtifact queueArtifact : queueList) 
		{	
			
			if(queueArtifact.getLocation().equals(AttractionArtifact.AVERAGE_STANDBY_WAIT_COUNT) ||	queueArtifact.getLocation().equals(AttractionArtifact.AVERAGE_XPASS_WAIT_COUNT))
			{
				//proceed 
				List<Object[]> timeTrend = queueArtifact.getTimeTrend();
				for (Object[] timeTrendItem : timeTrend)
				{
				    timestamp = (Date)timeTrendItem[0];
					if(i==0){
						root.setMindate(sdf.format(timestamp));
						i++;
					}
					
					Chartdata vChartdata = null;
					String location = queueArtifact.getLocation();
					vChartdata = hm.get(location);
					
							if(vChartdata==null){
								vChartdata  = new Chartdata();
								vChartdata.setName(queueArtifact.getLocation()); //setting name
								hm.put(location, vChartdata);
								root.addChartdata(vChartdata);
							}
							Dateseriesdata dsd= new Dateseriesdata();
							dsd.setDate(sdf.format(timestamp));
							dsd.setValue(Double.parseDouble(timeTrendItem[1].toString()));
							vChartdata.addDateseriesdata(dsd);
					}
			}
			else
			{
				//do nothing
			}
		}	
		
	    root.setMinvalue(0);
		root.setMaxvalue(1000);
	    root.setMaxdate(sdf.format(timestamp));
		return root;
		
	}
	
	


}
