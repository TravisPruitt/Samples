package com.disney.xbrc.parksimulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.disney.xband.common.lib.security.InputValidator;
import com.disney.xbrc.parksimulator.entity.Attraction;
import com.disney.xbrc.parksimulator.entity.Guests;
import com.disney.xbrc.parksimulator.entity.Message;
import com.disney.xbrc.parksimulator.entity.Park;
import com.disney.xbrc.parksimulator.entity.SimulationAction;
import com.disney.xbrc.parksimulator.entity.SimulationQueue;

public class ParkSimulator
{
	// some useful constants
	private static final int msecPerHour = 3600000;
	private static final int msecPerMinute = 60000;
	private static final int msecPerSecond = 1000;

	// list of pending simulation actions
	private SimulationQueue sq = new SimulationQueue();

	// next unallocated guest id
	// private LinkedList<Integer> liAvailableGuest = new LinkedList<Integer>();

	public static void main(String[] args)
	{
		try
		{
			// read the park info file
			Park.INSTANCE.readParkdata("parkdata.dat");
		}
		catch (Exception e)
		{
			System.err.println("Failed to read parkdata.dat: " + e);
			return;
		}

		CLOptions clo = CLOptions.parse(args);
		if (clo == null)
			return;

		if (clo.hasDisplayUsage())
		{
			CLOptions.usage();
			return;
		}

		if (clo.useIdms())
		{
			try
			{
				// read the guest info file
				Guests.INSTANCE.readGuests("guests.dat");
			}
			catch (Exception e)
			{
				// If guests.dat doesn't exist then the guest Ids will just
				// start at 0.
			}
		}

		System.out.println("Simulating from: " + clo.getStartHour() + ":" + clo.getStartMin() +" to " + clo.getEndHour() +":" + clo.getEndMin());
		System.out.println("Base time:       " + formatTime(clo.getCalBase().getTime()) );
		if (clo.isFast())
			System.out.format("Time factor:     Fast%n");
		else
			System.out.format("Time factor:     Real-time%n");
		if (clo.getLogFile() != null)
			System.out.println("Logging to:      " + clo.getLogFile());
		if (clo.getJMSBroker() != null)
		{
			System.out.println("JMS broker:      " + clo.getJMSBroker());
			System.out.println("JMS topic:       " + clo.getJMSTopic());
			System.out.println("JMS user:        " + clo.getJMSUser());
		}
		ParkSimulator ps = new ParkSimulator();
		ps.Simulate(clo);
		System.out.println("Done.");
	}

	private void Simulate(CLOptions clo)
	{
        FileWriter fw = null;
        BufferedWriter bw = null;

		try
		{
			// get the set of attractions to process
			List<String> liAttractions = null;
			if (clo.getAttractions() != null)
				liAttractions = clo.getAttractions();
			else
				liAttractions = Park.INSTANCE.getAttractions();

			// set up the JMS Agent
			JMSAgent jms = null;
			if (clo.getJMSBroker() != null)
			{
				jms = new JMSAgent(clo.getJMSBroker(), clo.getJMSTopic(), clo.getJMSUser(), clo.getJMSPassword());
				jms.initialize();
			}

			// set up the log file
			if (clo.getLogFile() != null)
			{
				// delete
				new File(InputValidator.validateFilePath(clo.getLogFile())).delete();
				fw = new FileWriter(InputValidator.validateFilePath(clo.getLogFile()));
				bw = new BufferedWriter(fw);
			}

			// in

			// step through time
			boolean bPrefilling = false;
			int nStartHour = clo.getStartHour();
			if (clo.isPrefill())
			{
				bPrefilling = true;
				nStartHour = 10;
			}
			int nEndHour = clo.getEndHour();

			for (int nHour = nStartHour; nHour <= nEndHour; nHour++)
			{
				int nInitialMin = 0;
				if (!bPrefilling && nHour==nStartHour)
					nInitialMin = clo.getStartMin();
			
				// calculate time in msec since midnight
				long msecSim = nHour * msecPerHour + nInitialMin * msecPerMinute;

				// stats
				int cMessages = 0;

				// step through each attraction
				for (String sAttraction : liAttractions)
				{
					// get the attraction
					Attraction attr = Park.INSTANCE.getAttraction(sAttraction);
					if (attr == null)
						throw new Exception("Invalid attraction name");

					// get the relevant metrics for the hour
					double ratio = (double) (60.0-nInitialMin)/60;
					int nStandbyGC = (int) (attr.getStandbyGC(nHour) * ratio);
					int nxPassGC = (int) (attr.getxPassGC(nHour) * ratio);
					int nStandbyWait = (int) (attr.getStandbyWait(nHour) * ratio);
					int nxPassWait = (int) (attr.getxPassWait(nHour) * ratio);
					
					// generate a metrics message
					// synthesize a time
					Calendar calm = (Calendar) clo.getCalBase().clone();
					calm.set(Calendar.HOUR_OF_DAY, nHour + calm.get(Calendar.HOUR_OF_DAY));
					calm.set(Calendar.MINUTE, 0);
					calm.set(Calendar.SECOND, 0);
					Message msg = createMetricsMessage(attr.getName(), calm, nStandbyGC, nStandbyWait,
							nStandbyWait + 90, nxPassGC, nxPassWait, (int) (0.95 * nxPassWait), nxPassWait + 90);
					if (jms != null)
						jms.publishMessage(msg);
					if (bw != null)
					{
						bw.write(msg.getPayload());
						bw.flush();
					}

					// calculate guest creation periods in msec
					long msecCreateStandby = 0;
					long msecCreatexPass = 0;

					if (nStandbyGC > 0)
						msecCreateStandby = msecPerHour / attr.getStandbyGC(nHour);
					if (nxPassGC > 0)
						msecCreatexPass = msecPerHour / attr.getxPassGC(nHour);

					// schedule Standby guest creation
					for (int i = 0; i < nStandbyGC; i++)
					{
						// calculate entry, merge, load and exit
						// merge time is 95% of wait time; load time is wait
						// time; exit time is wait time + 2.5 minutes
						long lWhenEnter = msecSim + i * msecCreateStandby;
						
						// interpolate the wait time
						int nMin = (int) ((float) (i * msecCreateStandby) / msecPerMinute);
						long msecCalculatedWaitTime = InterpolateWaitTime(false, attr, nHour, nMin);
						long msecMergeToLoad = 7 * msecPerSecond;
						if (msecMergeToLoad > msecCalculatedWaitTime)
							msecMergeToLoad = (long) (0.25 * msecCalculatedWaitTime);
						long lWhenLoad = lWhenEnter + msecCalculatedWaitTime;
						long lWhenMerge = lWhenLoad - msecMergeToLoad;
						long lWhenExit = lWhenLoad + (long) (2.0 * msecPerMinute);

						// schedule actions
						long nGuest = Guests.INSTANCE.getAvailableGuest();
						sq.add(new SimulationAction(attr, lWhenEnter, SimulationAction.Action.enter, nGuest, false));
						sq.add(new SimulationAction(attr, lWhenMerge, SimulationAction.Action.merge, nGuest, false));
						sq.add(new SimulationAction(attr, lWhenLoad, SimulationAction.Action.load, nGuest, false));
						sq.add(new SimulationAction(attr, lWhenExit, SimulationAction.Action.exit, nGuest, false));

					}

					// schedule xPass guest creation
					for (int i = 0; i < nxPassGC; i++)
					{
						// calculate entry, merge, load and exit
						// merge time is 95% of wait time; load time is wait
						// time; exit time is wait time + 2.5 minutes
						long lWhenEnter = msecSim + i * msecCreatexPass;

						// interpolate the wait time
						int nMin = (int) ((float) (i * msecCreatexPass) / msecPerMinute);

						long msecCalculatedWaitTime = InterpolateWaitTime(false, attr, nHour, nMin);
						long msecMergeToLoad = 7 * msecPerSecond;
						if (msecMergeToLoad > msecCalculatedWaitTime)
							msecMergeToLoad = (long) (0.25 * msecCalculatedWaitTime);
						long lWhenLoad = lWhenEnter + msecCalculatedWaitTime;
						long lWhenMerge = lWhenLoad - msecMergeToLoad;
						long lWhenExit = lWhenLoad + (long) (2.0 * msecPerMinute);

						// schedule actions
						long nGuest = Guests.INSTANCE.getAvailableGuest();
						sq.add(new SimulationAction(attr, lWhenEnter, SimulationAction.Action.enter, nGuest, true));
						sq.add(new SimulationAction(attr, lWhenMerge, SimulationAction.Action.merge, nGuest, true));
						sq.add(new SimulationAction(attr, lWhenLoad, SimulationAction.Action.load, nGuest, true));
						sq.add(new SimulationAction(attr, lWhenExit, SimulationAction.Action.exit, nGuest, true));

					}

				} // each attraction

				// iterate through hour, sending messages as needed
				for (int nMin = nInitialMin; nMin < 60; nMin++)
				{
					// see if it's time to stop prefilling
					if (bPrefilling)
					{
						if (nHour == clo.getStartHour() && nMin == clo.getStartMin())
							bPrefilling = false;
					}
					
					for (int nSec = 0; nSec < 60; nSec++)
					{
						long lmsecStart = new Date().getTime();

						long lNow = msecSim + (nMin - nInitialMin) * msecPerMinute + nSec * msecPerSecond;

						// get the list of what's due
						List<SimulationAction> liDue = sq.getDueList(lNow);
						cMessages += liDue.size();

						// generate JMS message
						for (SimulationAction sac : liDue)
						{
							// synthesize a time
							Calendar cal = (Calendar) clo.getCalBase().clone();
							cal.set(Calendar.HOUR_OF_DAY, nHour + cal.get(Calendar.HOUR_OF_DAY));
							cal.set(Calendar.MINUTE, nMin + cal.get(Calendar.MINUTE));
							cal.set(Calendar.SECOND, nSec + cal.get(Calendar.SECOND));
							
							Message msg = sac.createMessage(cal);
							if (jms != null)
								jms.publishMessage(msg);

							if (bw != null)
							{
								bw.write(msg.getPayload());
								bw.flush();
							}

							// if it's an exit, recycle the guest
							if (sac.getAction() == SimulationAction.Action.exit)
								Guests.INSTANCE.setAvailableGuest(sac.getGuestId());
						}
						sq.remove(liDue);

						if (!bPrefilling && !clo.isFast())
						{
							long lmsecEnd = new Date().getTime();
							long lmsecDuration = lmsecEnd - lmsecStart;
							long lmsecSleep = 1000 - lmsecDuration;

							if (lmsecSleep > 0)
								Thread.sleep(lmsecSleep);
						}
						
					} // each second

					// Per minute processing

					// let the messages flush
					if (jms != null)
					{
						int cJMS = jms.getMessageCount();
						if (cJMS > 0)
						{
							// System.out.println("Waiting for " + cJMS + " messages to be sent out");
							jms.waitForMessagesToSend();
						}
							
					}

					// mark each minute with a .
					System.out.print(".");

				} // each minute

				// Per hour processing

				// display some statistics
				if (jms != null)
				{
					System.out.format(
							"%nFrom %d:00 to %d:59 simulated %d messages. mps=%.3f mean send time=%.1f msec%n", nHour,
							nHour, cMessages, cMessages / 3600.0, jms.getAverageMsecPerSend());
					jms.clearStats();
				}
				else
					System.out.format("%nFrom %d:00 to %d:59 simulated %d messages. mps=%.3f%n", nHour, nHour,
							cMessages, cMessages / 3600.0);

			} // each simulation hour

			if (jms != null)
				jms.terminate();
		}
		catch (Exception ex)
		{
			System.err.println(ex);
			ex.printStackTrace();
		}
        finally {
            if(bw != null) {
                try {
                    bw.close();
                }
                catch(Exception ignore) {
                }
            }

            if(fw != null) {
                try {
                    fw.close();
                }
                catch(Exception ignore) {
                }
            }
        }
	}

	private long InterpolateWaitTime(boolean bFastpass, Attraction attr, int nHour, int nMin) throws Exception
	{
		// calculate interpolation segment
		long x0, y0, x1, y1;
		
		// treat 1st half hour in a special fashion
		if (nHour==10 && nMin < 30)
		{
			x0 = 0;
			if (bFastpass)
				y0 = 20 * msecPerSecond;				// minimum wait time (20 seconds)
			else
				y0 = 24 * msecPerSecond;				// minimum wait time (24 seconds)
				
			x1 = 30;
			y1 = attr.getStandbyWait(10) * msecPerMinute;
		}
		else if (nHour==24 && nMin >=30)
		{
			// treat last half hour in a special fashion (flat)
			x0 = 30;
			y0 = attr.getStandbyWait(24) * msecPerMinute;
			x1 = 60;
			y1 = y0;
		}
		else
		{
			if (nMin<30)
			{
				x0 = -30;
				y0 = attr.getStandbyWait(nHour - 1) * msecPerMinute;
				x1 = 30;
				y1 = attr.getStandbyWait(nHour) * msecPerMinute;
			}
			else
			{
				x0 = 30;
				y0 = attr.getStandbyWait(nHour) * msecPerMinute;
				x1 = 90;
				y1 = attr.getStandbyWait(nHour + 1) * msecPerMinute;
			}
		}
		
		// return interpolated value
		long lValue = y0 + (long)( ((float) (y1-y0) * (nMin-x0)) / (x1 - x0));
		return lValue;
	}

	private static String formatTime(Date ts)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		//sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(ts);
	}

	private Message createMetricsMessage(String sAttr, Calendar calEnd, int cSBGuests, int csecSBWait, int csecSBTotal,
			int cFPGuests, int csecFPWait, int csecFPMerge, int csecFPTotal)
	{
		Date dtEnd = calEnd.getTime();

		// TODO: Consider using xml classes
		StringBuilder sb = new StringBuilder();

		sb.append("<venue name=\"" + sAttr + "\" " + "time=\"" + formatTime(dtEnd) + "\">\n");
		sb.append("    <message type=\"METRICS\" time=\"" + formatTime(dtEnd) + "\">\n");

		sb.append("        <starttime>" + formatTime(new Date(dtEnd.getTime() - 3600000)) + "</starttime>\n");
		sb.append("        <endtime>" + formatTime(dtEnd) + "</endtime>\n");
		sb.append("        <standby>\n");
		sb.append("            <guests>" + cSBGuests + "</guests>\n");
		sb.append("            <abandonments>0</abandonments>\n");
		sb.append("            <waittime>" + csecSBWait + "</waittime>\n");
		sb.append("            <totaltime>" + csecSBTotal + "</totaltime>\n");
		sb.append("        </standby>\n");
		sb.append("        <xpass>\n");
		sb.append("            <guests>" + cFPGuests + "</guests>\n");
		sb.append("            <abandonments>0</abandonments>\n");
		sb.append("            <waittime>" + csecFPWait + "</waittime>\n");
		sb.append("            <mergetime>" + csecFPMerge + "</mergetime>\n");
		sb.append("            <totaltime>" + csecFPTotal + "</totaltime>\n");
		sb.append("        </xpass>\n");
		sb.append("    </message>\n");
		sb.append("</venue>\n");

		Message msg = Message.create(sAttr, "METRICS", sb.toString());
		return msg;
	}

}
