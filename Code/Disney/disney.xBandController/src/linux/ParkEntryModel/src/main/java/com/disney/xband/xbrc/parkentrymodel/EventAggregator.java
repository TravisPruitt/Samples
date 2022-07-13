package com.disney.xband.xbrc.parkentrymodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.disney.xband.xbrc.lib.model.LocationInfo;
import com.disney.xband.xbrc.lib.model.LocationStats;
import com.disney.xband.xbrc.lib.model.ReaderInfo;
import com.disney.xband.xbrc.lib.model.Singulator;
import com.disney.xband.xbrc.lib.model.TapEventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;

public class EventAggregator implements Comparator<XbrEvent> {
	private static Logger logger = Logger.getLogger(EventAggregator.class);

	private XBRCController controller;
	private Singulator sing;

	public EventAggregator(XBRCController controller, IXBRCModel model) {
		this.controller = controller;
		sing = new Singulator(controller, model);
	}

	public List<EventAggregate> Analyze(List<XbrEvent> alEvents) {
		// sort the data: band id, time, packet #, reader location, ss
		Collections.sort(alEvents, this);

		// holds statistics for the same sequence # read by different reader
		// locations
		List<LocationStats> liLocStats = new ArrayList<LocationStats>();

		// peg the current data being processed
		LocationStats lsCur = null;

		// stages the return value
		List<EventAggregate> liAggregated = new ArrayList<EventAggregate>();

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

			if (!(ev instanceof LrrEvent)) {
				GenericEventAggregate tea = new GenericEventAggregate(
						ev.getID(), ev.getReaderName(), ev.getTime(), ev);
				liAggregated.add(tea);
				continue;
			}

			LrrEvent le = (LrrEvent) ev;			

			if (lsCur != null && controller.isVerbose()) {
				logger.debug("**** Comparing band ids:  " + lsCur.getID()
						+ " vs " + ev.getID());
				logger.debug("**** Comparing pnos:      "
						+ lsCur.getPacketSequence() + " vs " + le.getPno());
				logger.debug("**** Comparing locations: "
						+ lsCur.getLocationInfo().getName() + " vs "
						+ sLocationNew);
			}

			// see if break
			if ((lsCur == null)
					|| (lsCur.getID().compareTo(ev.getID()) != 0)
					|| (lsCur.getPacketSequence() != le.getPno())
					|| (lsCur.getLocationInfo().getName()
							.compareTo(sLocationNew) != 0)) {
				// if it's the 1st event, just set up for that
				if (lsCur == null) {
					lsCur = new LocationStats(le.getID(), le.getPno(), 
							controller.getReader(le.getReaderName()), le.getTime(), le.getXband(), le.getGuest());
					lsCur.setCount(1);
					lsCur.setMaxSS(le.getSs());
					lsCur.setSumSS(le.getSs());
					liLocStats.add(lsCur);
				} else if ((lsCur.getID().compareTo(le.getID()) != 0)
						|| (lsCur.getPacketSequence() != le.getPno())) {

					// band id has changed or packet sequence has changed - emit
					// best location
					AddAggregateForBestLocation(liAggregated, liLocStats);

					// start anew
					liLocStats.clear();
					lsCur = new LocationStats(le.getID(), le.getPno(),
							controller.getReader(le.getReaderName()),
							le.getTime(), le.getXband(), le.getGuest());
					lsCur.setCount(1);
					lsCur.setMaxSS(le.getSs());
					lsCur.setSumSS(le.getSs());
					liLocStats.add(lsCur);

				} else {
					// the only case that's left is that the location info has
					// changed for the same band id and packet sequence #
					// In this case, we start collecting data for the new
					// location
					lsCur = new LocationStats(le.getID(), le.getPno(),
							controller.getReader(le.getReaderName()),
							le.getTime(), le.getXband(), le.getGuest());
					lsCur.setCount(1);
					lsCur.setMaxSS(le.getSs());
					lsCur.setSumSS(le.getSs());
					liLocStats.add(lsCur);
				}
			} else {
				// aggregate
				lsCur.setCount(lsCur.getCount() + 1);
				if (le.getSs() > lsCur.getMaxSS())
					lsCur.setMaxSS(le.getSs());
				lsCur.setSumSS(lsCur.getSumSS() + le.getSs());
			}

		}

		// emit the last one
		if (liLocStats.size() > 0)
			AddAggregateForBestLocation(liAggregated, liLocStats);

		// now, sort the aggregate list. Note that we're conveniently using the
		// last instance
		// of liCur as our comparator. If liCur is null, that means that there's
		// nothing that
		// needs to be sorted so it works out.
		if (lsCur != null)
			Collections.sort(liAggregated, lsCur);

		return liAggregated;
	}

	private void AddAggregateForBestLocation(List<EventAggregate> liAggregated,
			List<LocationStats> liLocStats) {
		// singulate the aggregate events
		EventAggregate ea = sing.Singulate(liLocStats,
				controller.getSingulationAlgorithm());
		liAggregated.add(ea);
	}

	@Override
	public int compare(XbrEvent arg0, XbrEvent arg1) {
		int d = 0;

		// deal with null cases
		if (arg0 != null && arg1 == null)
			return -1;
		else if (arg0 == null && arg1 != null)
			return 1;
		else if (arg0 == null && arg1 == null)
			return 0;

		// first, sort by bandid but only if we have them (bio scan events
		// don't)
		if (arg0.getID() != null && arg1.getID() != null) {
			d = arg0.getID().compareTo(arg1.getID());
			if (d != 0)
				return d;
		}

		// second, sort by time, rounded to the nearest minute
		long t0 = arg0.getTime().getTime() / 60000;
		long t1 = arg1.getTime().getTime() / 60000;
		if (t0 < t1)
			return -1;
		else if (t0 > t1)
			return 1;

		// next, sort by packet number
		if (arg0 instanceof LrrEvent && arg1 instanceof LrrEvent) {
			long n0 = ((LrrEvent) arg0).getPno();
			long n1 = ((LrrEvent) arg1).getPno();
			if (n0 < n1)
				return -1;
			else if (n0 > n1)
				return 1;
		}

		// next, by reader location
		ReaderInfo ri0 = controller.getReader(arg0.getReaderName());
		ReaderInfo ri1 = controller.getReader(arg1.getReaderName());
		if (ri0 == null || ri0.getLocation() == null || ri1 == null || ri1.getLocation() == null)
			return d;
		String sLocation0 = ri0.getLocation().getName();
		String sLocation1 = ri1.getLocation().getName();
		d = sLocation0.compareTo(sLocation1);
		if (d != 0)
			return d;

		// else, they're equal
		return 0;
	}
}
