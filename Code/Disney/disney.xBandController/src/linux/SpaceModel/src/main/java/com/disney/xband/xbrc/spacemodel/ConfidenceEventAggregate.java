package com.disney.xband.xbrc.spacemodel;

import com.disney.xband.xbrc.lib.model.EventAggregate;
import com.disney.xband.xbrc.lib.model.LRREventAggregate;
import com.disney.xband.xbrc.lib.model.XBRCController;
import com.disney.xband.xview.lib.model.Guest;
import com.disney.xband.xview.lib.model.Xband;

import java.util.*;

import org.apache.log4j.Logger;

public class ConfidenceEventAggregate extends LRREventAggregate {
    private int confidence;
	private static Logger logger = Logger.getLogger(ConfidenceEventAggregate.class);

    public ConfidenceEventAggregate(String BandId, String PidDecimal, long PacketSequence, String sReaderName,
			Date Timestamp, double Average, long Max, long Count, int confidence, Xband xb, Guest guest) {
        super(BandId, PidDecimal, PacketSequence, sReaderName, Timestamp, Average, Max, Count, xb, guest);
        this.confidence = confidence;
    }

    // Iterate over all contained events and calculate a weighted
    // average
    public int getConfidence()
    {
        return this.confidence;
    }

    // Loop through the given events and calculate a weighted average. Currently, we're assuming
    // that the event properties are seeded with a likely candidate.
    public static ConfidenceEventAggregate determineState(GuestStatus<GuestStatusState> gs) {
        ConfidenceEventAggregate cev = null;

        if ( gs.getGuest() == null )
            logger.error("!! Invalid Guest");

        EventAggregate evOldest = gs.getOldestEvent();
        if ( evOldest != null )
        {
            Date dtNow = new Date();
            long millSecDiff = dtNow.getTime() - evOldest.getTimestamp().getTime();

            // Get the preferred detect delay based on the band's role. These are hard-coded strings
            // as of current, which seems brittle. TLP.
            if ( millSecDiff >= gs.getRoleDetectDelay() )
            {
                logger.trace("RoleDetectDelay has been exceeded. Calculating confidence.");

                // We have enough data, now calculate a probability and find the most likely
                // representative event.
                cev = ConfidenceEventAggregate.calculateProbability(gs, gs.getRoleDetectDelay());
                
                // Always clear out events if we're not using queues.
                if ( !ConfigOptions.INSTANCE.getControllerInfo().getUseQueue() )
                {
                    gs.clearEAEvents();
                }
            }
            else
            {              
                logger.trace("Events do not exceed role detect delay: Guest=" + gs +
                    ";dtNow=" + dtNow.getTime() + ";evOldest=" +
                    evOldest.getTimestamp().getTime()+";millSecDiff=" + millSecDiff);
            }
        }
        else
        {
        	logger.trace("No events found for guest: " + gs);
        }

        return cev;
    }

    // We calculate a probability based on the frequency of events at a given location. We may
    // want to refactor this to include signal strength at some period of time.
    static private ConfidenceEventAggregate calculateProbability(GuestStatus<GuestStatusState> gs,
                                                                 long msecRoleDetectDelay)
    {
        ConfidenceEventAggregate cev = null;
        int locationEventRatio = ConfigOptions.INSTANCE.getControllerInfo().getLocationEventRatio();

        // Current assuming that we get one sample per second. This needs
        // to be refactored to expect different chirp to time ratios.
        int requiredEvents = (int)(((double)locationEventRatio * 
        		(msecRoleDetectDelay / ConfigOptions.INSTANCE.getControllerInfo().getChirpRate())) / 100);

        // Determine if we have enough events to cover a minimum threshold and calculate a probability.
        // This is the first sanity check.
        List<EventAggregate> events = gs.getEAEvents();
        if ( events.size() >= requiredEvents )
        {
            Hashtable<String, LocationEvents> locations = new Hashtable<String, LocationEvents>();

            // Count the events by location and keep a running total.
            for (EventAggregate currentEvent : events)
            {
            	if ( currentEvent instanceof LRREventAggregate )
            	{
	                LocationEvents locEvents = locations.get(currentEvent.getReaderInfo().getLocation().getName());
	                if ( locEvents == null )
	                {
	                    locEvents = new LocationEvents(ConfigOptions.INSTANCE.getControllerInfo().getUsePassFilters());
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
            if ( bestLocationEvents != null && 
            		bestLocationEvents.Events.size() >= requiredEvents )
            {
                logger.trace("Enough events for confidence. Calculating...");

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
                		mostRecent.getGuest());

				if ( XBRCController.getInstance().isVerbose() )
					logger.trace( new Date().getTime() +
	            			",CONF," +
	            			gs + "," +
	            			gs.getBandType() + "," +
                            mostRecent.getReaderInfo().getLocation().getName() + "," +
	            			confidence
	            			);
            }
            else
            {
                logger.trace("Not enough consistent singulation for guest: guest=" + gs);

            	XBRCController.getInstance().logEKG( new Date().getTime() +
            			",NOCONF," +
            			gs + "," +
            			gs.getBandType() + "," +
            			"TooUnstable" );
            }
        }
        else
        {
            logger.trace("Not enough events to calculate confidence for guest: guest=" + gs);

        	XBRCController.getInstance().logEKG( new Date().getTime() +
        			",NOCONF," +
        			gs + "," +
        			gs.getBandType() + "," +
        			"TooFew" );
        }

        return cev;
    }

    private static class LocationEvents
    {
        public List<LRREventAggregate> Events = new ArrayList<LRREventAggregate>();
        private boolean useFilter = false;

        public LocationEvents( boolean useFilter ) {
            this.useFilter = useFilter;
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
            int confidence;

            if ( useFilter ) {
                confidence = calcConfidenceLoFilter();
            }
            else {
                confidence = calcConfidenceAverage();
            }

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

        private int calcConfidenceLoFilter() {
            // Standard sanity checks.
            if ( Events.size() == 0 )
                return 0;
            if ( Events.size() == 1 )
                return Events.get(0).getConfidence();

            int calculatedConfidence;
            double normalizedConfidences[] = new double[Events.size()];

            // Calculate overall effect based on the number of samples.
            double velocity = 1.0f / (double)Events.size();

            // Smooth out the peaks/valleys of the confidences.
            // Calculate from the tail to weight the calculations towards
            // the latest events.
            normalizedConfidences[Events.size() - 1] = (double) Events.get(Events.size()-1).getConfidence();
            for ( int i = Events.size() - 2; i >= 0; i-- ) {
                normalizedConfidences[ i ] = normalizedConfidences[ i + 1 ] + (velocity *
                                        ((double)Events.get( i ).getConfidence() - normalizedConfidences[ i + 1 ]));
            }

            // Do simple average of all of the smoothed confidence values.
            int totalConfidence = 0;
            for ( double normalizedConfidence : normalizedConfidences ) {
                totalConfidence += normalizedConfidence;
            }
            calculatedConfidence = totalConfidence / normalizedConfidences.length;

            return calculatedConfidence;
        }
    }
}
