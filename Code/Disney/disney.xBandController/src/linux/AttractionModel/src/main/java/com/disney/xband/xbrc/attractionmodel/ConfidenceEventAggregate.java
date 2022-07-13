package com.disney.xband.xbrc.attractionmodel;


import com.disney.xband.xbrc.lib.entity.EventsLocationConfig;
import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

import java.util.*;

import org.apache.log4j.Logger;

public class ConfidenceEventAggregate extends LRREventAggregate {
    private int confidence;
    private Date lastPingTime;
    private static Logger logger = Logger.getLogger(ConfidenceEventAggregate.class);    

    public ConfidenceEventAggregate(String BandId, String PidDecimal, long PacketSequence, String sReaderName,
                                    Date Timestamp, double Average, long Max, long Count, int confidence, Xband xb, Guest guest, Date lastPingTime) {
        super(BandId, PidDecimal, PacketSequence, sReaderName, Timestamp, Average, Max, Count, xb, guest);
        this.confidence = confidence;
        this.lastPingTime = lastPingTime;
    }

    // Iterate over all contained events and calculate a weighted
    // average
    public int getConfidence()
    {
        return this.confidence;
    }

    
    // Loop through the given events and calculate a weighted average. Currently, we're assuming
    // that the event properties are seeded with a likely candidate.
    public static ConfidenceEventAggregate determineState(ConfidenceEventAggregate lastConfidenceState, GuestStatus<GuestStatusState> gs,
                                                          HashMap<String, EventsLocationConfig> eventConfigurations ) {
        ConfidenceEventAggregate cev = null;
        
        Date dtNow = new Date();
        
        EventAggregate evOldest = gs.getOldestConfidenceEvent();
        if ( evOldest != null )
        {           
            long millSecDiff = dtNow.getTime() - evOldest.getTimestamp().getTime();

            EventsLocationConfig eventsConfig = eventConfigurations.get(evOldest
                    .getReaderInfo()
                    .getLocation()
                    .getName());

            if ( eventsConfig != null )
            {
                // Get the preferred detect delay based on the band's role. These are hard-coded strings
                // as of current, which seems brittle. TLP.
                if ( millSecDiff >= gs.getRoleDetectDelay(eventsConfig) )
                {
                	if (XBRCController.getInstance().isVerbose())
                		logger.trace("RoleDetectDelay has been exceeded for guest" + gs + " Calculating confidence.");

                    // We have enough data, now calculate a probability and find the most likely
                    // representative event.
                    cev = ConfidenceEventAggregate.calculateProbability(gs, eventConfigurations);
                    gs.clearConfidenceEvents();
                }
                else
                {
                	if (XBRCController.getInstance().isVerbose())
	                    logger.trace("Events do not exceed role detect delay: Guest=" + gs +
	                            ";dtNow=" + dtNow.getTime() + ";evOldest=" +
	                            evOldest.getTimestamp().getTime()+";millSecDiff=" + millSecDiff);
                    
                    cev = lastConfidenceState;
                }
            }
        }
        else
        {        		
        	if (lastConfidenceState != null)
        	{
	    		EventsLocationConfig eventsConfig = eventConfigurations.get(lastConfidenceState.getReaderInfo().getLocation().getName());
        	
        		// Check if enough time has passed to determine that the guest has left the location.
        		long millSecDiff = dtNow.getTime() - (lastConfidenceState.getTimestamp().getTime() + 500);
        		if (millSecDiff < eventsConfig.getAbandonmentTimeout())
        			cev = lastConfidenceState;
        		else
        			logger.trace("Guest " + gs + " is no longer detected at location " + lastConfidenceState.getReaderInfo().getLocation().getName());
        	}        	
        }

        return cev;
    }

    // We calculate a probability based on the frequency of events at a given location. We may
    // want to refactor this to include signal strength at some period of time.
    static private ConfidenceEventAggregate calculateProbability(GuestStatus<GuestStatusState> gs,
                                                    HashMap<String, EventsLocationConfig> eventConfigurations )
    {
        ConfidenceEventAggregate cev = null;

        // Determine if we have enough events to cover a minimum threshold and calculate a probability.
        // This is the first sanity check.
        List<EventAggregate> events = gs.getConfidenceEvents();
        Hashtable<String, LocationEvents> locations = new Hashtable<String, LocationEvents>();

        // Count the events by location and keep a running total.
        for (EventAggregate currentEvent : events)
        {
            if ( currentEvent instanceof LRREventAggregate )
            {
                LocationEvents locEvents = locations.get(currentEvent.getReaderInfo().getLocation().getName());
                if ( locEvents == null )
                {
                    locEvents = new LocationEvents();
                    locations.put(currentEvent.getReaderInfo().getLocation().getName(), locEvents);
                }

                locEvents.Events.add((LRREventAggregate)currentEvent);
            }
        }

        // Find the event with the highest frequency.
        LocationEvents bestLocationEvents = null;
        Enumeration<String> keys = locations.keys();
        while ( keys.hasMoreElements() )
        {
            String key = keys.nextElement();
            LocationEvents currentEvents = locations.get(key);

            if ( bestLocationEvents == null ||
                    currentEvents.Events.size() > bestLocationEvents.Events.size())
            {
                bestLocationEvents = currentEvents;
            }
        }

        // Determine if the event with the highest frequency exceeds or matches our
        // minimum threshold. If so, create a new event aggregate. Calculate the probability
        // based on the average signal strength of all the events.
        if ( bestLocationEvents != null )
        {
            EventsLocationConfig eventConfig = eventConfigurations.get(bestLocationEvents
                    .getMostRecentEvent()
                    .getReaderInfo()
                    .getLocation()
                    .getName());

            int locationEventRatio = eventConfig.getLocationEventRatio();
            long msecRoleDetectDelay = gs.getRoleDetectDelay(eventConfig);

            // Current assuming that we get one sample per second. This needs
            // to be refactored to expect different chirp to time ratios.
            int requiredEvents = (int)(((double)locationEventRatio *
                    (msecRoleDetectDelay / eventConfig.getChirpRate())) / 100);

            if ( bestLocationEvents.Events.size() >= requiredEvents )
            {
            	if (XBRCController.getInstance().isVerbose())
            		logger.trace("Enough events for confidence for guest " + gs + ". Calculating...");

                int confidence = bestLocationEvents.getConfidence();

                LRREventAggregate mostRecent = bestLocationEvents.getMostRecentEvent();
                cev = new ConfidenceEventAggregate(mostRecent.getID(),
                        mostRecent.getPidDecimal(),
                        mostRecent.getPacketSequence(),
                        mostRecent.getReaderInfo().getName(),
                        mostRecent.getTimestamp(),
                        mostRecent.getAverageStrength(),
                        mostRecent.getMaxStrength(),
                        mostRecent.getCount(),
                        confidence,
                        mostRecent.getXband(),
                        mostRecent.getGuest(),
                        gs.getNewestConfidenceEvent().getTimestamp());

                if ( XBRCController.getInstance().isVerbose() )
                    logger.trace( new Date().getTime() +
                            ",CONF," +
                            gs + "," +
                            gs.getBandType() + "," +
                            mostRecent.getReaderInfo().getLocation().getName() + "," +
                            confidence
                    );
            }
        }
        else
        {
            logger.trace("Not enough consistent singulation for guest: " + gs);
        }

        return cev;
    }

    private static class LocationEvents
    {
        public List<LRREventAggregate> Events = new ArrayList<LRREventAggregate>();

        public LocationEvents() {
        }

        public LRREventAggregate getMostRecentEvent() {
            LRREventAggregate event = null;

            if ( Events.size() > 0 )
            {
                event = Events.get(Events.size()-1);
            }

            return event;
        }

        public int getConfidence() {
            int confidence = calcConfidenceAverage();
            return confidence;
        }

        private int calcConfidenceAverage() {
            int confidence;

            // Sanity check.
            if ( Events.size() == 0 )
                return 0;

            int totalConfidence = 0;
            for ( LRREventAggregate event : Events ) {
                totalConfidence += event.getConfidence();
            }
            confidence = totalConfidence / Events.size();

            return confidence;
        }
    }

	public Date getLastPingTime() {
		return lastPingTime;
	}
}
