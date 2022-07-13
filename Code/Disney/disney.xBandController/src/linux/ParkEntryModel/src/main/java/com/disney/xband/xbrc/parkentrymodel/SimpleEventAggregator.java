package com.disney.xband.xbrc.parkentrymodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.disney.xband.lib.xbrapi.BioEvent;
import com.disney.xband.lib.xbrapi.BioImageEvent;
import com.disney.xband.lib.xbrapi.BioScanErrorEvent;
import com.disney.xband.lib.xbrapi.LrrEvent;
import com.disney.xband.lib.xbrapi.TapEvent;
import com.disney.xband.lib.xbrapi.XbrEvent;
import com.disney.xband.xbrc.lib.model.BioEventAggregate;
import com.disney.xband.xbrc.lib.model.BioImageEventAggregate;
import com.disney.xband.xbrc.lib.model.BioScanErrorEventAggregate;
import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.GenericEventAggregate;
import com.disney.xband.xbrc.lib.model.IXBRCModel;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.LocationStats;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.Singulator;
import com.disney.xband.xbrc.lib.model.TapEventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;

/*
 * The goal of the SimpleEventAggregator is to be as fast as possible.
 */
public class SimpleEventAggregator {
	private static Logger logger = Logger.getLogger(SimpleEventAggregator.class);

	private XBRCController controller;
	private IXBRCModel model;

	public SimpleEventAggregator(XBRCController controller, IXBRCModel model) {
		this.controller = controller;
		this.model = model;
	}

	public List<EventAggregate> Analyze(List<XbrEvent> alEvents) {
		
		// stages the return value
		List<EventAggregate> liAggregated = new ArrayList<EventAggregate>();
		
		// we collapse all LRR events from a single band ping into one LRREventAggregate.
		HashMap<String,LRREventAggregate> lrrMap = new HashMap<String, LRREventAggregate>(); 

		// now, walk through the events, breaking every time there's a new
		// bandid or packet # or reader location
		for (XbrEvent ev : alEvents) {
			if (ev == null) {
				logger.warn("Null event in list");
				continue;
			}

			ReaderInfo ri = controller.getReader(ev.getReaderName());
			if (ri == null) {
				logger.error("Received event for unknown reader: "
						+ ev.getReaderName());
				continue;
			}

			LocationInfo liNew = ri.getLocation();
			if (liNew == null) {
				logger.warn("Ignoring event from reader " + ri.getName()
						+ " because it is not assigned to a valid location");
				continue;
			}
			String sLocationNew = liNew.getName();

			// throw away unknown reader locations
			if (sLocationNew.compareTo("UNKNOWN") == 0)
				continue;

			// pass through taps immediately
			if (ev instanceof TapEvent) {
				TapEventAggregate tea = new TapEventAggregate(ev.getID(),((TapEvent) ev).getPidDecimal(),
						ev.getReaderName(), ev.getTime(), (TapEvent) ev);
				liAggregated.add(tea);
				continue;
			}			

			if (ev instanceof LrrEvent) 
			{
				LrrEvent le = (LrrEvent) ev;
				
				LRREventAggregate ea =  lrrMap.get(le.getID() + le.getPno());
				if (ea == null || ea.getAverageStrength() <= le.getSs())
				{
					if (model.isEventLogged(ev.getID()))
					{
						String sLog = le.getTime().getTime() + ",SNG," + liNew.getName() + "," + ev.getID() + "," + "MaxSS";
						controller.logEKG(sLog);
					}
				
					ea = new LRREventAggregate(	le.getID(),
												le.getPidDecimal(),
												le.getPno(),
												ri.getName(),
												le.getTime(), 
												le.getSs(),	// don't bother with calculating mean 
												le.getSs(), 
												ea == null ? 1 : ea.getCount() + 1,
												le.getXband(),
												le.getGuest());
					
					lrrMap.put(le.getID() + le.getPno(), ea);
				}
				
				continue;
			}
		
			if (ev instanceof BioEvent) {
				BioEventAggregate tea = new BioEventAggregate(ev.getID(),
						ev.getReaderName(), ev.getTime(), (BioEvent) ev);
				liAggregated.add(tea);
				continue;
			}
	
			if (ev instanceof BioImageEvent) {
				BioImageEventAggregate tea = new BioImageEventAggregate(
						ev.getID(), ev.getReaderName(), ev.getTime(),
						(BioImageEvent) ev);
				liAggregated.add(tea);
				continue;
			}
	
			if (ev instanceof BioScanErrorEvent) {
				BioScanErrorEventAggregate tea = new BioScanErrorEventAggregate(
						ev.getID(), ev.getReaderName(), ev.getTime(),
						(BioScanErrorEvent) ev);
				liAggregated.add(tea);
				continue;
			}
			
			GenericEventAggregate tea = new GenericEventAggregate(
					ev.getID(), ev.getReaderName(), ev.getTime(), ev);
			liAggregated.add(tea);
		}

		for (EventAggregate ea : lrrMap.values())
			liAggregated.add(ea);
		
		// sort event aggregates according to event time
		Collections.sort(liAggregated, 
				new Comparator<EventAggregate>() { 
					@Override
					public int compare(EventAggregate arg0, EventAggregate arg1)
					{
						return arg0.getTimestamp().compareTo(arg1.getTimestamp());
					}
				});

		return liAggregated;
	}
}
