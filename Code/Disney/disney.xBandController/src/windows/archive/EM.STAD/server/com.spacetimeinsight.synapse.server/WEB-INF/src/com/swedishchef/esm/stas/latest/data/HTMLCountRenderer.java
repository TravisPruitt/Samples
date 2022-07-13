package com.swedishchef.esm.stas.latest.data;

import java.util.List;
import java.util.Map;

import com.enterprisehorizons.magma.renderer.chart.ChartDataXMLRenderer;
import com.swedishchef.esm.stas.latest.artifacts.AttractionArtifact;
import com.swedishchef.esm.stas.latest.artifacts.QueueCountArtifact;


public class HTMLCountRenderer extends ChartDataXMLRenderer {
		
	@Override
	public Object render(Map params){
		String attractionName = (String)params.get("attractionName");
		//System.out.println("Inside of HTMLCountRenderer " + attractionName);
		List<QueueCountArtifact> queueList =  DataManager.getInstance().attractionLookup(attractionName);
		
		String xml = new String("<root>");
	
		//System.out.println(xml);

		for(QueueCountArtifact extractedArtifact : queueList)
		{
			xml+="<values ";
			xml+="name=\"" + extractedArtifact.getLocation() + "\" ";
			if(extractedArtifact.getLocation().equals(AttractionArtifact.AVERAGE_STANDBY_WAIT_COUNT) ||	extractedArtifact.getLocation().equals(AttractionArtifact.AVERAGE_XPASS_WAIT_COUNT))
				xml+=" value=\"" + extractedArtifact.getAvewait() + "\"";
			else
				xml+=" value=\"" + extractedArtifact.getCount() + "\"";
			xml+="/>";
		}
		
		xml+="</root>";
		//System.out.println(xml);
		return xml;
	}
}
