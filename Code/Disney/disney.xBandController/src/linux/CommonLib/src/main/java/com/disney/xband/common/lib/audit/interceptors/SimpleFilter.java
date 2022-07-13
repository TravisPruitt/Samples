package com.disney.xband.common.lib.audit.interceptors;

import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;
import java.util.regex.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/31/13
 * Time: 10:31 AM
 */

/**
 * This class can be used to filter out audit events based on event type, category, uid, and time interval between
 * the events that satisfy the first 3 criteria for the same client ip. The format of a single rule is:
 *
 * [Type | WILDCARD]:[Category | WILDCARD]:[uid | WILDCARD]:[Interval in secs]
 *
 * Multiple rules must be separated by commas
 */
public class SimpleFilter extends FilterBase {
    private static class Param {
        String type = "";
        String category = "";
        String uid = "";
        long interval = -1;
        Pattern rid;
    }

    private List<Param> params;
    private Map<String, Long> map;

    @Override
    public void init(final AuditInterceptorConfig config) {
        super.init(config);
        this.map = new HashMap<String, Long>();
        this.params = new ArrayList<Param>();

        if(!isEmpty(config.getParams())) {
            final List<String> list = new ArrayList<String>();

            try {
                final StringTokenizer st = new StringTokenizer(config.getParams(), ",", true);

                while(st.hasMoreElements()) {
                    final String param = st.nextToken();

                    if(param.indexOf(",") >= 0) {
                        continue;
                    }

                    list.add(param);
                }

                for(String p : list) {
                    final Param s = new Param();

                    if(p.indexOf(":") < 0) {
                        s.rid = Pattern.compile(p);
                        this.params.add(s);
                        continue;
                    }

                    final StringTokenizer stp = new StringTokenizer(p, ":", true);
                    int num = 0;

                    while(stp.hasMoreElements()) {
                        final String val = stp.nextToken();

                        if(val.indexOf(":") >= 0) {
                            ++num;
                            continue;
                        }

                        switch(num) {
                            case 0:
                                s.type = val.trim();
                                break;

                            case 1:
                                s.category = val.trim();
                                break;

                            case 2:
                                s.uid = val.trim();
                                break;

                            case 3:
                                s.interval = Integer.parseInt(val.trim()) * 1000;
                                break;

                            default:
                                logger.error("Failed to parse SimpleFilter parameters: " + config.getParams());
                                return;
                        }
                    }

                    this.params.add(s);
                }
            }
            catch (Exception e) {
                logger.error("Failed to parse SimpleFilter parameters: " + config.getParams());
            }
        }
    }

    @Override
    public String getId() {
        return this.config.getId();
    }

    @Override
    public AuditEvent intercept(AuditEvent event) {
        if(this.params.size() == 0) {
            return event;
        }

        for(Param p : this.params) {
            if(p.rid != null) {
                final Matcher m = p.rid.matcher(toEmpty(event.getRid()));
                if(m.matches()) {
                    return null;
                }

                continue;
            }

            if(p.type.equalsIgnoreCase(event.getType()) || p.type.equals("*")) {
                if(p.category.equalsIgnoreCase(event.getCategory()) || p.category.equals("*")) {
                    if(p.uid.equalsIgnoreCase(toEmpty(event.getUid())) || p.uid.equals("*")) {
                        if(isEmpty(event.getClient())) {
                            return event;
                        }

                        if(p.interval == -1) {
                            return null;
                        }
                        else {
                            final String key = getKey(p, event);
                            final Long val = this.map.get(key);

                            if(val == null) {
                                this.map.put(key, System.currentTimeMillis());
                            }
                            else {
                                if(System.currentTimeMillis() - val.longValue() < p.interval) {
                                    return null;
                                }
                                else {
                                    this.map.put(key, System.currentTimeMillis());
                                }
                            }
                        }
                    }
                }
            }
        }

        return event;
    }

    public static String getKey(final Param p, final AuditEvent e) {
        final StringBuffer sb = new StringBuffer();
        sb.append(p.type);
        sb.append("-");
        sb.append(p.category);
        sb.append("-");
        sb.append(p.uid);
        sb.append("-");
        sb.append(toEmpty(e.getClient()));
        sb.append("-");
        sb.append(toEmpty(e.getRid()));

        return sb.toString();
    }

    public static boolean isEmpty(final String s) {
        if((s == null) || (s.trim().length() == 0)) {
            return true;
        }

        return false;
    }

    public static String toEmpty(final String s) {
        if((s == null) || (s.trim().length() == 0)) {
            return "";
        }

        return s;
    }
}
