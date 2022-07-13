package com.disney;

/**
 * Created with IntelliJ IDEA.
 * User: Fable
 * Date: 6/22/13
 * Time: 11:11 PM
 * To change this template use File | Settings | File Templates.
 */

import com.disney.model.GuestLookup;
import org.apache.log4j.Logger;

import java.lang.Thread;
import java.lang.Runnable;
import java.util.List;
import java.util.LinkedList;
import java.util.Date;


public class DataProcessor implements Runnable {
    private final Logger logger = Logger.getLogger(DataProcessor.class);

    private int retryAttempts = Main.RetryAttempts;

    private int minutesBetweenRetry = Main.MinutesToRetry;

    boolean running = false;

    public DataProcessor() {
        running = true;
    }

    @Override
    public void run() {

        running = true;

        try {
            RunProcess();
        } catch (Exception ex) {
            // The run process failed?
            //System.out.println("A DataProcessor Thread died unexpectedly with error: \n" + ex.getLocalizedMessage());

            logger.error("A DataProcessor Thread died unexpectedly with error:",  ex);
        }


    }


    private void RunProcess() throws InterruptedException {
        while (running) {
            GuestLookup gl = null;

            synchronized (Main.guestListQueue) {
                //System.out.println("Checking the guestListQueue..");
                if (Main.guestListQueue.size() > 0) {
                    // The the record off the top.
                    // if .poll pulls nothing off the top, it returns null, meaning the list is empty.  For some reason.
                    gl = Main.guestListQueue.poll();
                }
                //System.out.println("Completed checking the guestListQueue..");
            }

            if (gl != null) {

                //synchronized (gl) {

                // Our first rule is to check to see if the lookup is within one hour of it's start time.
                // and check to see if IDMS has had a chance to look up the record.
                if ( !gl.getGxpId().isEmpty() && gl.getEventStartDate().getTime() - new Date().getTime() <= (60 * (60 *1000)) && gl.getRetryCount() > 3 )
                {
                    try {


                    //logger.info(gl);

                     // This means that we never found them within IDMS.  We'll ask SFOV one time and then we're done.

                    List<String> publicIDs = Main.GetListOfPublicIdsForGuestSFOV(gl.getGxpId());

                    synchronized (Main.metrics)
                    {
                        int sfova = Main.metrics.getSFOneViewLookupAttempts();
                        sfova++;
                        Main.metrics.setSFOneViewLookupAttempts(sfova);
                    }

                    // We have a list of publicIDs for the guest?
                    if (publicIDs != null && !publicIDs.isEmpty()) {
                        gl.setPublicId(publicIDs);

                        // Save to database.
                        Main.SaveToDatabase(gl);

                         // Save public IDs.
                        for (String s : publicIDs) {
                            // Make a record for each publicID against this gxpID.
                            Main.SavePublicIDToGXPLinkId(gl.getGxpId(), s);
                        }

                        // Remove from the queue object.
                        synchronized (Main.guestListQueue)
                        {
                            // Now permanently remove it from the list.
                            ((LinkedList) Main.guestListQueue).remove(gl);
                            // And we're done with this record.
                        }

                        // Since we found it and saved it, go ahead and remove it from the cache.
                        synchronized (Main.cache)
                        {
                            Main.cache.remove(gl);
                        }

                        synchronized (Main.metrics)
                        {
                            int c = Main.metrics.getLooksupsCompleted();
                            c++;
                            Main.metrics.setLooksupsCompleted(c);

                            int s = Main.metrics.getSFOneViewLooksups();
                            s++;
                            Main.metrics.setSFOneViewLooksups(s);
                        }

                        continue;
                    }
                    else
                    {
                        // Otherwise, we didn't find them in SFOV, we didn't find them  in IDMS.  So stale the record.
                        gl.setStale(true);
                    }
                   }
                    catch (Exception ex)
                    {
                        logger.error(ex);
                    }
                }





                // First, check to see if this object is stale.
                if (gl.getStale())
                {
                    // Update the cache object.

                    synchronized (Main.cache)
                    {
                        Main.cache.remove(gl);  // Remove the old reference copy and add this reference copy.
                        Main.cache.add(gl);
                    }

                    synchronized (Main.metrics)
                    {
                        int s = Main.metrics.getStaleRecords();
                        s++;
                        Main.metrics.setStaleRecords(s);
                    }

                    continue; // Don't process it any more.  Don't add it back to the queue.

                }

                if (gl.getRetryCount() > 0)
                {
                    // See if the timer has elapsed.
                    // (!((new Date().getTime() - startTime.getTime()) > (waitTime * (60 * 1000))))
                    if (!((new Date().getTime() - gl.getLastRetryDateTime().getTime()) > (minutesBetweenRetry * (60 * 1000))))
                    {
                        synchronized (Main.guestListQueue)
                        {
                            ((LinkedList) Main.guestListQueue).addLast(gl);
                        }
                        continue;  // drop out and go on to the next object.
                    }
                }




                //System.out.println("I found a guest to lookup ..");
                // Look for the GxpId.
                if (gl.getGxpId().isEmpty()) {
                    // Lookup the gxpLinkId for this guest.

                    //System.out.println("looking up gxpId.." + gl.getReferenceId());

                    long gxpId = Main.retrieveGuestGXPId(gl.getReferenceId());

                    //System.out.println("I got a gxpid: " + gxpId);

                    if (gxpId != 0) {
                        gl.setGxpId(String.valueOf(gxpId));
                    } else {
                        // If I didn't find a gxpID for this Reference ID, log it and move on.
                        logger.error("GxP lookup failed for referenceID: " + gl.getReferenceId());

                        int r = gl.getRetryCount();
                        r++;
                        gl.setRetryCount(r);

                        gl.setLastRetryDateTime(new Date());
                    }


                }

                if (!gl.getGxpId().isEmpty())
                {
                    // First, look it up in our cache and see if it's already been looked up.
                    if (Main.GxPInCache(gl.getGxpId()))
                    {
                        // It's already in the cache.  Save it to the booking record and move on.
                        Main.SaveToDatabase(gl);
                        gl.setSavedGXP(true);

                        // Remove it from the cache and the queue.
//                        synchronized (Main.guestListQueue)
//                        {
//                            // Now permanently remove it from the list.
//                            ((LinkedList) Main.guestListQueue).remove(gl);
//                            // And we're done with this record.
//                        }

                        // Since we found it and saved it, go ahead and remove it from the cache.
                        synchronized (Main.cache)
                        {
                            Main.cache.remove(gl);
                        }

                        // Update the Metrics counter.

                        synchronized (Main.metrics)
                        {
                            long count = Main.metrics.getGxpInCache();
                            count++;

                            Main.metrics.setGxpInCache(count);

                            count = Main.metrics.getLooksupsCompleted();
                            count++;

                            Main.metrics.setLooksupsCompleted((int)count);
                        }

                        // Move to the next record.
                        continue;
                    }
                }

                if (!gl.getGxpId().isEmpty())
                {
                    if (Main.AlwaysWriteGXPLinkId)
                    {
                        if  (!gl.getSavedGXP())
                            Main.SaveToDatabase(gl);
                        gl.setSavedGXP(true);
                    }
                }

                // Okay, if we have a gxpLinkId, let's get the public ID list for this guest.
                if (!gl.getGxpId().isEmpty()) {
                    List<String> publicIDs = Main.GetListOfPublicIdsForGuestIDMS(gl.getGxpId());

                    // if we found the public ID's, we're good to go.
                    synchronized (Main.metrics)
                    {
                        int ia = Main.metrics.getIDMSLookupAttempts();
                        ia++;
                        Main.metrics.setIDMSLookupAttempts(ia);
                    }

                    if (publicIDs != null && !publicIDs.isEmpty()) {
                        gl.setPublicId(publicIDs);

                        // If we haven't already saved the record.
                        if (!gl.getSavedGXP())
                        {
                            Main.SaveToDatabase(gl);
                        }

                        for (String s : publicIDs) {
                            // Make a record for each publicID against this gxpID.
                            Main.SavePublicIDToGXPLinkId(gl.getGxpId(), s);
                        }
                        synchronized (Main.guestListQueue)
                        {
                        // Now permanently remove it from the list.
                        ((LinkedList) Main.guestListQueue).remove(gl);
                        // And we're done with this record.
                        }

                        // Since we found it and saved it, go ahead and remove it from the cache.
                        synchronized (Main.cache)
                        {
                          Main.cache.remove(gl);
                        }

                        synchronized (Main.metrics)
                        {
                            int c = Main.metrics.getLooksupsCompleted();
                            c++;
                            Main.metrics.setLooksupsCompleted(c);

                            int i = Main.metrics.getIDMSLookups();
                            i++;
                            Main.metrics.setIDMSLookups(i);



                        }



                    } else  // We didn't find any publicID's for this gxpId.  Suck.
                    {

                        int retryCount = gl.getRetryCount();
                        retryCount++;
                        gl.setRetryCount(retryCount);

                        if (retryCount > retryAttempts)
                        {
                            // We maxed out our retry attempts - mark it stale:
                            gl.setStale(true);
                        }

                        gl.setLastRetryDateTime(new Date());

                        // Add it back to the end of the queue for later re-processing.
                        synchronized (Main.guestListQueue) {
                            // now we have to remove it from the list and add it back to the bottom of the list.
                            //((LinkedList) Main.guestListQueue).remove(gl);


                            ((LinkedList) Main.guestListQueue).addLast(gl);
                        }
                    }

                }

            }

            }
            Thread.sleep(100);
        //}
    }


}
