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
import com.enterprisehorizons.util.DateUtils;
import com.swedishchef.esm.stas.latest.artifacts.AttractionArtifact;
import com.swedishchef.esm.stas.latest.artifacts.QueueCountArtifact;



public class SBXPRenderer extends ChartDataXMLRenderer {
	
	private static final String ELEMENT_ATTRIBUTES = "attributes";
	private static final String ELEMENT_ATTRIBUTE = "attribute";
	private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss,SSS");
	
	@Override
	public Object render(Map params){

		//System.out.println("In QueueCountDataRenderer " + params.get("attractionName").toString());
		List<QueueCountArtifact> queueList =  DataManager.getInstance().attractionLookup(params.get("attractionName").toString());
		Chartdataroot root = new Chartdataroot(); //root tag
	    HashMap<String, Chartdata> hm = new HashMap<String, Chartdata>();
		Date minDate = DateUtils.getFirstDateOfTheMonth(DateUtils.getCurrentDate());
		
	    int i = 0;
	    Date timestamp = null;
		for (QueueCountArtifact queueArtifact : queueList) {	
			if(queueArtifact.getLocation().equals(AttractionArtifact.SB_QUEUE_COUNT) ||	queueArtifact.getLocation().equals(AttractionArtifact.XP_QUEUE_COUNT)){
			List<Object[]> timeTrend = queueArtifact.getTimeTrend();
			for (Object[] timeTrendItem : timeTrend){
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
						dsd.setValue(Integer.parseInt(timeTrendItem[1].toString()));
						vChartdata.addDateseriesdata(dsd);
				}
		}
		}	
		
	    root.setMinvalue(0);
		root.setMaxvalue(200);
	    root.setMaxdate(sdf.format(timestamp));
		return root;
		
	}
	
	


}
