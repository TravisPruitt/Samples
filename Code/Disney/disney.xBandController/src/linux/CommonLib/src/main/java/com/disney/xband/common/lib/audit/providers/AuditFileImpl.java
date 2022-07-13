package com.disney.xband.common.lib.audit.providers;

import com.disney.xband.common.lib.audit.AuditBase;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/1/13
 * Time: 10:38 PM
 */

/**
 * This class simply writes audit events to a file on disk.
 */
public class AuditFileImpl extends AuditBase {
    protected FileWriter fos;
    protected volatile AtomicLong lastId;
    protected int eventsCount;

    public AuditFileImpl(final AuditConfig conf) {
        super(conf);
        init(conf);
    }

    protected void init(final AuditConfig conf) {
        this.lastId = new AtomicLong(0);

        try {
            this.fos = new FileWriter(this.conf.getFileStorePath(), true);
        }
        catch(Exception e) {
            this.logger.error("Failed to open local AUDIT file " + this.conf.getFileStorePath() + " : " + e.getMessage());
            return;
        }

        BufferedReader fis = null;
        long lastId = 0;

        try {
            fis = new BufferedReader(new FileReader(this.conf.getFileStorePath()));
            String line = fis.readLine();

            while(line != null) {
                ++this.eventsCount;

                try {
                    final AuditEvent event = this.parseEvent(line);
                    final long id = event.getId();
                    lastId = lastId > id ? lastId : id;
                    line = fis.readLine();
                }
                catch (Exception ignore) {
                }
            }
        }
        catch(Exception e) {
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        this.lastId.set(lastId);
    }

    @Override
    public boolean audit(AuditEvent event) {
        if(event.getId() > 0) {
            event.setAid(event.getId());
        }

        final long id = this.lastId.incrementAndGet();
        event.setId(id);

        try {
            if(this.fos != null) {
                final String out = this.prepareForWrite(event);

                if(out != null) {
                    this.fos.write(out);
                    this.fos.flush();
                }

                ++this.eventsCount;
            }
        }
        catch (Exception ignore) {
            return false;
        }

        return true;
    }
    
    @Override
    public boolean audit(Collection<AuditEvent> events) {
        if(events != null) {
            for(AuditEvent event : events) {
                this.audit(event);
            }
        }

        return true;
    };

    //////////////////////////////////////////////////////////////////////////////
    //                     IAuditEventsProvider implementation                       //
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public List<AuditEvent> getAllEvents() {
        return this.getEvents(-1);
    }

    @Override
    public List<AuditEvent> getEvents(long afterEventId) {
        final List<AuditEvent> res = new ArrayList<AuditEvent>();
        BufferedReader fis = null;

        try {
            fis = new BufferedReader(new FileReader(this.conf.getFileStorePath()));
            String line = fis.readLine();
            String sid;

            while(line != null) {
                try {
                    final AuditEvent event = this.parseEvent(line);

                    if(event.getId() > afterEventId) {
                        res.add(event);
                    }

                    line = fis.readLine();
                }
                catch (Exception ignore) {
                }
            }
        }
        catch(Exception e) {
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        return res;
    }

    @Override
    public void deleteEvents(long upToEventId) {
        this.deleteEvents(upToEventId, false, false);
    }

    @Override
    public void deleteAllEvents() {
       this.deleteEvents(this.lastId.get());
    }

    @Override
    public void cleanup(boolean useDates) {
        this.deleteEvents(this.conf.getKeepInCacheEventsMax() / 3, true, useDates);
    }

    // Protected methods

    protected AuditEvent parseEvent(String s) {
        if(s == null) {
            return null;
        }

        final String[] ft = s.split("[|]");

        if((ft == null) || (ft.length == 0)) {
            return null;
        }

        final String[] f = new String[16];

        for(int i = 0; i < 16; ++i) {
            if(i < ft.length) {
                f[i] = ft[i];
            }
            else {
                f[i] = "";
            }
        }

        try {
            final AuditEvent event = new AuditEvent();
            event.setId(Long.parseLong(f[0].trim()));

            try {
                event.setAid(Long.parseLong(f[1].trim()));
            }
            catch (Exception ignore) {
            }

            event.setType(f[2].trim());
            event.setCategory(f[3].trim());
            event.setDateTime(f[4].trim());
            event.setAppClass(f[5].trim());
            event.setAppId(f[6].trim());
            event.setHost(f[7].trim());
            event.setvHost(f[8].trim());
            event.setUid(f[9].trim());
            event.setSid(f[10].trim());
            event.setDesc(f[11].trim());
            event.setRid(f[12].trim());
            event.setClient(f[13].trim());
            event.setCollectorHost(f[14].trim());

            if(f.length == 16) {
                event.setrData(f[15].trim());
            }
            else {
                if(f.length > 16) {
                    String f15 = "";

                    for(int i = 0; i < f.length - 15; ++i) {
                        f15 = f15 + f[15 + i];
                    }

                    event.setrData(f15);
                }
            }

            return event;
        }
        catch (Exception e) {
        }

        return null;
    }

    protected String prepareForWrite(final AuditEvent event) {
        return String.format(
            "%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s\n",
            event.getId(), event.getAid(), event.getType(), event.getCategory(), event.getDateTime(),
            event.getAppClass(), event.getAppId(), event.getHost(), event.getvHost(), event.getUid(),
            event.getSid(), event.getDesc(), event.getRid(), event.getClient(), event.getCollectorHost(), event.getrData()
        );
    }

    @Override
    public long getLastAuditIdForHost(final String host, final boolean isCollectorHost) {
        BufferedReader fis = null;
        long lastId = -1;

        try {
            fis = new BufferedReader(new FileReader(this.conf.getFileStorePath()));
            String line = fis.readLine();
            String sid;

            while(line != null) {
                try {
                    final AuditEvent event = this.parseEvent(line);
                    String comp = isCollectorHost ? event.getCollectorHost() : event.getHost();

                    if(comp == null) {
                        comp = "";
                    }

                    if(comp.equals(host)) {
                        lastId = event.getAid();
                    }

                    line = fis.readLine();
                }
                catch (Exception ignore) {
                }
            }
        }
        catch(Exception e) {
            return -100;
        }
        finally {
            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        return lastId;
    }

    private void deleteEvents(long upToEventId, boolean isArgNumToDelete, boolean useDates) {
        FileWriter fos = null;

        try {
            fos = new FileWriter(this.conf.getFileStorePath() + ".tmp", false);
        }
        catch(Exception e) {
            if(fos != null) {
                try {
                    fos.close();
                }
                catch(Exception ignore) {
                }
            }

            return;
        }

        BufferedReader fis = null;

        try {
            fis = new BufferedReader(new FileReader(this.conf.getFileStorePath()));
            String line = fis.readLine();
            String sid;

            this.eventsCount = 0;
            final Date tDate = new Date(new Date().getTime() - this.conf.getKeepInGlobalDbDaysMax() * 60000);

            while(line != null) {
                try {
                    final AuditEvent event = this.parseEvent(line);
                    final long id = event.getId();

                    final String out = this.prepareForWrite(event);

                    if (out != null) {
                        if (!useDates) {
                            if (isArgNumToDelete) {
                                if (this.eventsCount < upToEventId) {
                                    fos.write(out);
                                    ++this.eventsCount;
                                }
                                else {
                                    break;
                                }
                            }
                            else {
                                if (id > upToEventId) {
                                    fos.write(out);
                                    ++this.eventsCount;
                                }
                            }
                        }
                        else {
                            if(new Date(event.getDateTime()).after(tDate)) {
                                fos.write(out);
                                ++this.eventsCount;
                            }
                        }
                    }

                    line = fis.readLine();
                }
                catch (Exception ignore) {
                }
            }
        }
        catch(Exception e) {
        }
        finally {
            if(fos != null) {
                try {
                    fos.close();
                }
                catch(Exception ignore) {
                }
            }

            if(fis != null) {
                try {
                    fis.close();
                }
                catch(Exception ignore) {
                }
            }
        }

        try {
            new File(this.conf.getFileStorePath()).delete();
        }
        catch (Exception ignore) {
        }

        try {
            new File(this.conf.getFileStorePath() + ".tmp").renameTo(new File(this.conf.getFileStorePath()));
        }
        catch (Exception ignore) {
        }

        try {
            this.fos = new FileWriter(this.conf.getFileStorePath(), true);
        }
        catch (Exception ignore) {
        }
    }
}
