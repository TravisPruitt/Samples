package com.disney.xband.xbrc.spacemodel.junit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import org.junit.Test;

public class ConfidenceEventAggregateTests {
	private int LocationEventRatio = 70;
	private long ChirpRate = 1000;

	@Test
	public void testSingleLocation() {
		// Initialize 
		GuestStatus gs = new GuestStatus();
		
		// Create a single reader.
		ReaderInfo ri = new ReaderInfo( "Location 1");
		Calendar cal = Calendar.getInstance();
		
		// Subtract 10 seconds from the seed date.
		cal.add(Calendar.SECOND, -10);
		
		// Add 10 events. 
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "2", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "3", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "4", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "5", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "6", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "7", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "8", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "9", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "10", -70 ));
		
		ConfidenceEventAggregate cev = determineState(gs);
		assertNotNull(cev);
		assertEquals(29, cev.getConfidence());
	}

	@Test
	public void testMultipleLocation() {
		// Initialize 
		GuestStatus gs = new GuestStatus();
		
		// Create a single reader.
		ReaderInfo ri = new ReaderInfo( "Location 1");
		ReaderInfo ri2 = new ReaderInfo( "Location 2");
		
		// Subtract 10 seconds from the seed date.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -10);
		
		// Add 10 events. 
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "2", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "3", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "4", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "5", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "6", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "7", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "8", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "9", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "10", -70 ));
		
		ConfidenceEventAggregate cev = determineState(gs);
		assertNotNull(cev);
		assertEquals(29, cev.getConfidence());
		assertEquals("Location 1", cev.getReaderInfo().getLocation());
	}

	@Test
	public void testMultipleLocationAndGet2ndLocation() {
		// Initialize 
		GuestStatus gs = new GuestStatus();
		
		// Create a single reader.
		ReaderInfo ri = new ReaderInfo( "Location 1");
		ReaderInfo ri2 = new ReaderInfo( "Location 2");
		
		// Subtract 10 seconds from the seed date.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -10);
		
		// Add 10 events. 
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "2", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "3", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "4", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "5", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "6", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "7", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "8", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "9", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "10", -70 ));
		
		ConfidenceEventAggregate cev = determineState(gs);
		assertNotNull(cev);
		assertEquals(29, cev.getConfidence());
		assertEquals("Location 2", cev.getReaderInfo().getLocation());
	}

	@Test
	public void testTooFewEventsAtSingleLocation() {
		// Initialize 
		GuestStatus gs = new GuestStatus();
		
		// Create a single reader.
		ReaderInfo ri = new ReaderInfo( "Location 1");
		Calendar cal = Calendar.getInstance();
		
		// Subtract 10 seconds from the seed date.
		cal.add(Calendar.SECOND, -10);
		
		// Add only 6 events. We should fail since we haven't hit 70%. 
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		
		ConfidenceEventAggregate cev = determineState(gs);
		assertNull(cev);
	}

	@Test
	public void testTooFewMultipleLocation() {
		// Initialize 
		GuestStatus gs = new GuestStatus();
		
		// Create a single reader.
		ReaderInfo ri = new ReaderInfo( "Location 1");
		ReaderInfo ri2 = new ReaderInfo( "Location 2");
		
		// Subtract 10 seconds from the seed date.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -10);
		
		// Add 10 events. 
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "2", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "3", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "4", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "5", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "6", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "7", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "8", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "9", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri2, cal.getTime(), "10", -70 ));
		
		ConfidenceEventAggregate cev = determineState(gs);
		assertNull(cev);
	}

	@Test
	public void testChirpRatio() {
		// Initialize 
		GuestStatus gs = new GuestStatus();
		
		// Create a single reader.
		ReaderInfo ri = new ReaderInfo( "Location 1");
		
		// Subtract 10 seconds from the seed date.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -10);
		
		// Adjust the chirp rate.
		ChirpRate = 500;
		
		// Add 10 events. 
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "1", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "2", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "3", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "4", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "5", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "6", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "7", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "8", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "9", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "10", -70 ));
		
		ConfidenceEventAggregate cev = determineState(gs);
		assertNull(cev);

		// We should need another 10 events to make enough events for our threshold.
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "11", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "12", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "13", -70 ));
		cal.add(Calendar.SECOND, 1);
		gs.addEAEvent(new EventAggregate( ri, cal.getTime(), "14", -70 ));
		
		cev = determineState(gs);
		assertNotNull(cev);

		// Reset the chirp rate.
		ChirpRate = 1000;
	}

	@Test
	public void testConfidenceFormula() {
		EventAggregate ev = new EventAggregate(-100);
		assertEquals(0, ev.getConfidence());
		ev = new EventAggregate(-90);
		assertEquals(0, ev.getConfidence());
		ev = new EventAggregate(-80);
		assertEquals(13, ev.getConfidence());
		ev = new EventAggregate(-70);
		assertEquals(29, ev.getConfidence());
		ev = new EventAggregate(-60);
		assertEquals(52, ev.getConfidence());
		ev = new EventAggregate(-50);
		assertEquals(83, ev.getConfidence());
		ev = new EventAggregate(-40);
		assertEquals(100, ev.getConfidence());
		ev = new EventAggregate(-30);
		assertEquals(100, ev.getConfidence());
	}

	private class ReaderInfo
	{
		private String location;

		public ReaderInfo( String location )
		{
			this.location = location;
		}
		
		public String getLocation() {
			return location;
		}
	}
	
	private class ConfidenceEventAggregate extends EventAggregate
	{
		private int compositeConfidence;
		
		public ConfidenceEventAggregate(String id, String location,
				Date timestamp, int confidence) {
			super( new ReaderInfo(location), timestamp, id, 0);
			compositeConfidence = confidence;
		}
		
		public int getConfidence() {
			return compositeConfidence;
		}
	}
	
	private class EventAggregate
	{
		private ReaderInfo ri = new ReaderInfo("Unknown");
		private Date ts = new Date();
		private String id;
		private int maxSignalStrength;
		
		public EventAggregate( ReaderInfo ri, Date timestamp, String id, int maxSignalStrength)
		{
			this.ri = ri;
			this.ts = timestamp;
			this.id = id;
			this.maxSignalStrength = maxSignalStrength;
		}
		
		public EventAggregate(int maxSignalStrength)
		{
			this.maxSignalStrength = maxSignalStrength;
		}
		
		public Date getTimestamp() {
			return ts;
		}

		public ReaderInfo getReaderInfo() {
			return ri;
		}

		public String getID() {
			return id;
		}

		public int getConfidence() {
	    	int confidence = 0;

	    	// See TLP about how this formula has been calculated.
	    	confidence = (int)((-9399/(double)maxSignalStrength) - 104.4);
	    	
	    	// Ensure we limit to mins and maxs.
	    	confidence = Math.min(100, confidence);
	    	confidence = Math.max(0, confidence);
	    	
	    	return confidence;
		}
	}
	
	private class GuestStatus
	{
	    private List<EventAggregate> liEAEvents = new ArrayList<EventAggregate>();
		private String guestType = "Guest";
		public long GuestDelay = 10000;
		public long CastMemberDelay = 1000;
		public long PuckDelay = 1000;
		
	    public EventAggregate getOldestEvent() {
	        EventAggregate ev = null;

	        if ( liEAEvents.size() > 0 )
	        {
	            ev = liEAEvents.get(0);
	        }

	        return ev;
	    }

	    public List<EventAggregate> getEAEvents() {
	        return liEAEvents;
	    }

	    // Add events through this interface as this is a FILO queue.
	    public void addEAEvent(EventAggregate ea)
	    {
	        liEAEvents.add(ea);
	    }

	    public void clearEAEvents()
	    {
	        liEAEvents.clear();
	    }

	    public long getRoleDetectDelay()
	    {
	        long delay = 0;

	        if ( guestType.equals("Guest"))
	            delay = GuestDelay;
	        else if ( guestType.equals("CastMember"))
	            delay = CastMemberDelay;
	        else if (guestType.equals("Puck"))
	            delay = PuckDelay;

	        return delay;
	    }
	}
	
    // Loop through the given events and calculate a weighted average. Currently, we're assuming
    // that the event properties are seeded with a likely candidate.
    public ConfidenceEventAggregate determineState(GuestStatus gs) {
        ConfidenceEventAggregate cev = null;

        EventAggregate evOldest = gs.getOldestEvent();
        if ( evOldest != null )
        {
            Date dtNow = new Date();
            long millSecDiff = dtNow.getTime() - evOldest.getTimestamp().getTime();

            // Get the preferred detect delay based on the band's role. These are hard-coded strings
            // as of current, which seems brittle. TLP.
            if ( millSecDiff >= gs.getRoleDetectDelay() )
            {
                // We have enough data, now calculate a probability and find the most likely
                // representative event.
                cev = calculateProbability(gs.getEAEvents(), gs.getRoleDetectDelay());
                if ( cev != null )
                {
                    // Clear out the events now that they've been consumed.
                    gs.clearEAEvents();
                }
            }
        }

        return cev;
    }

    // We calculate a probability based on the frequency of events at a given location. We may
    // want to refactor this to include signal strength at some period of time.
    private ConfidenceEventAggregate calculateProbability(List<EventAggregate> events, long msecRoleDetectDelay)
    {
        ConfidenceEventAggregate cev = null;
        int locationEventRatio = LocationEventRatio ;

        // Current assuming that we get one sample per second. This needs
        // to be refactored to expect different chirp to time ratios.
        int requiredEvents = (int)(((double)locationEventRatio * (msecRoleDetectDelay / ChirpRate)) / 100);

        // Determine if we have enough events to cover a minimum threshold and calculate a probability.
        // This is the first sanity check.
        if ( events.size() >= requiredEvents )
        {
            Hashtable<String, LocationEvents> locations = new Hashtable<String, LocationEvents>();

            // Count the events by location and keep a running total.
            for (EventAggregate currentEvent : events)
            {
                LocationEvents locEvents = locations.get(currentEvent.getReaderInfo().getLocation());
                if ( locEvents == null )
                {
                    locEvents = new LocationEvents();
                    locations.put(currentEvent.getReaderInfo().getLocation(), locEvents);
                }

                locEvents.Count++;
                locEvents.CumulativeConfidence += currentEvent.getConfidence();
                locEvents.MostRecent = currentEvent;
            }

            // Find the event with the highest frequency.
            LocationEvents bestLocationEvents = null;
            Enumeration<String> keys = locations.keys();
            while ( keys.hasMoreElements() )
            {
                String key = keys.nextElement();
                LocationEvents currentEvents = locations.get(key);
                
                if ( bestLocationEvents == null || 
                		currentEvents.Count > bestLocationEvents.Count)
                {
                	bestLocationEvents = currentEvents;
                }
            }

            // Determine if the event with the highest frequency exceeds or matches our
            // minimum threshold. If so, create a new event aggregate. Calculate the probability
            // based on the average signal strength of all the events.
            if ( bestLocationEvents != null && 
            		bestLocationEvents.Count >= requiredEvents)
            {
            	
                // Initialize a new ConfidenceAggregate based on the found info. Currently, this is a simple
                // average of the normalize signal strength of the event.
                int confidence = (int) ((double)bestLocationEvents.CumulativeConfidence / (double)bestLocationEvents.Count);

                // TODO: Check with Manny on the 2nd argument.
                cev = new ConfidenceEventAggregate(bestLocationEvents.MostRecent.getID(), 
                		bestLocationEvents.MostRecent.getReaderInfo().getLocation(), 
                		bestLocationEvents.MostRecent.getTimestamp(), 
                		confidence);
            }
        }

        return cev;
    }

    private static class LocationEvents
    {
    	public int Count = 0;
    	public int CumulativeConfidence = 0;
    	public EventAggregate MostRecent = null;
    }
}
