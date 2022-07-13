package com.disney.xband.common.lib.audit;

import com.disney.xband.common.lib.DateUtils;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.DbField;
import com.disney.xband.common.lib.security.UserContext;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 4/30/13
 * Time: 4:04 PM
 */
/*
 * All events belong to one of two classes: audit or health.
 * Audit events contain security information related to access to xConnect components by different types of users.
 * All user actions will be audited to make it possible to later find out who did what and when. The events will be
 * marked with user Keystone ID and session ID. The latter can be used to correlate user-initiated operations spanning
 * multiple systems. Health events carry data related to the system operational status. They are not associated with
 * specific users and do not have user ID.
 */
public abstract class AuditBase implements IAudit, IAuditEventsProvider {
    protected Logger logger = Logger.getLogger(AuditBase.class);
    protected AuditConfig conf;
    
    protected static final String EXPECTED_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public AuditBase(final AuditConfig conf) {
        this.conf = conf;
    }

    //////////////////////////////////////////////////////////////////////////////
    //                          IAudit implementation                           //
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public abstract boolean audit(AuditEvent event);

    @Override
    public abstract boolean audit(Collection<AuditEvent> events);

    @Override
    public AuditEvent create(
        final AuditEvent.Type type, final AuditEvent.Category category,
        final String desc, final String resourceId, final String resourceData,
        final String host, final String vHost
    ) {
        final AuditEvent event = new AuditEvent();

        event.setAppClass(this.conf.getAppClass());
        event.setAppId(this.conf.getAppId());
        event.setCategory(category == null ? "" : category.toString());
        event.setDateTime(DateUtils.toString(
                Calendar.getInstance().getTime(),
                EXPECTED_DATE_FORMAT,
                Locale.ENGLISH,
                Calendar.getInstance().getTimeZone()));

        if(host == null) {
            event.setHost(this.isEmpty(this.conf.getHost()) ? this.conf.getvHost() : this.conf.getHost());
        }
        else {
            event.setHost(host);
        }

        if(vHost == null) {
            event.setvHost(this.isEmpty(this.conf.getvHost()) ? this.conf.getHost() : this.conf.getvHost());
        }
        else {
            event.setvHost(vHost);
        }

        event.setrData(resourceData);
        event.setRid(resourceId);
        event.setType(type == null ? "" : type.toString());
        event.setCollectorHost("");

        final UserContext uc = (UserContext) UserContext.instance.get();

        if(uc != null) {
            if(uc.getAppClass() != null) {
                event.setAppClass(uc.getAppClass());
            }

            event.setUid(uc.getLogonName() == null ? "" : uc.getLogonName());
            event.setSid(uc.getSid() == null ? "" : uc.getSid());
            event.setClient(uc.getClient());

            if((category == AuditEvent.Category.LOGIN) && (uc.getSerToken() != null)) {
                event.setrData(uc.getSerToken());
            }
        }
        else {
            event.setUid("");
            event.setSid("");
            event.setClient("");
        }

        event.setDesc(this.processDesc(desc, event));
        return event;
    }

    @Override
    public AuditEvent create(
        final AuditEvent.Type type, final AuditEvent.Category category,
        final String desc, final String resourceId, final String resourceData
    ) {
        return create(type, category, desc, resourceId, resourceData, null, null);
    }

    @Override
    public boolean isAuditEnabled() {
        return this.conf.isEnabled();
    }

    @Override
    public AuditEvent create(final AuditEvent.Type type, final AuditEvent.Category category, final String desc) {
        return this.create(type, category, desc, null, null);
    }

    @Override
    public AuditEvent createLogonSuccess(final String desc) {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.LOGIN, desc, null, null);
    }

    @Override
    public AuditEvent createLogonFailure(final String desc) {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.LOGIN, desc, null, null);
    }

    @Override
    public AuditEvent createLogonSuccess() {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.LOGIN, null, null, null);
    }

    @Override
    public AuditEvent createLogonFailure() {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.LOGIN, null, null, null);
    }

    @Override
    public AuditEvent createLogoutSuccess() {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.LOGOUT, null, null, null);
    }

    @Override
    public AuditEvent createLogoutFailure() {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.LOGOUT, null, null, null);
    }

    @Override
    public AuditEvent createLogoutSuccess(final String desc) {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.LOGOUT, desc, null, null);
    }

    @Override
    public AuditEvent createLogoutFailure(final String desc) {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.LOGOUT, desc, null, null);
    }

    @Override
    public AuditEvent createAccessSuccess(final String resourceId) {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.ACCESS, null, resourceId, null);
    }

    @Override
    public AuditEvent createAccessFailure(final String resourceId) {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.ACCESS, null, resourceId, null);
    }

    @Override
    public AuditEvent createReadSuccess(final String resourceId) {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.READ, null, resourceId, null);
    }

    @Override
    public AuditEvent createReadFailure(final String resourceId) {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.READ, null, resourceId, null);
    }

    @Override
    public AuditEvent createWriteSuccess(final String desc, final String resourceId, final String resourceData) {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.WRITE, desc, resourceId, resourceData);
    }

    @Override
    public AuditEvent createWriteFailure(final String desc, final String resourceId, final String resourceData) {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.WRITE, desc, resourceId, resourceData);
    }

    @Override
    public AuditEvent createWriteSuccess(final String resourceId, final String resourceData) {
        return this.create(AuditEvent.Type.AUDIT_SUCCESS, AuditEvent.Category.WRITE, null, resourceId, resourceData);
    }

    @Override
    public AuditEvent createWriteFailure(final String resourceId, final String resourceData) {
        return this.create(AuditEvent.Type.AUDIT_FAILURE, AuditEvent.Category.WRITE, null, resourceId, resourceData);
    }

    @Override
    public AuditEvent createFatal(final AuditEvent.Category category, final String desc) {
        return this.create(AuditEvent.Type.FATAL, category, desc, null, null);
    }

    @Override
    public AuditEvent createError(final AuditEvent.Category category, final String desc) {
        return this.create(AuditEvent.Type.ERROR, category, desc, null, null);
    }

    @Override
    public AuditEvent createWarn(final AuditEvent.Category category, final String desc) {
        return this.create(AuditEvent.Type.WARN, category, desc, null, null);
    }

    @Override
    public AuditEvent createInfo(final AuditEvent.Category category, final String desc) {
        return this.create(AuditEvent.Type.INFO, category, desc, null, null);
    }

    @Override
    public AuditEvent createFatal(final String desc) {
        return this.create(AuditEvent.Type.FATAL, null, desc, null, null);
    }

    @Override
    public AuditEvent createError(final String desc) {
        return this.create(AuditEvent.Type.ERROR, null, desc, null, null);
    }

    @Override
    public AuditEvent createWarn(final String desc) {
        return this.create(AuditEvent.Type.WARN, null, desc, null, null);
    }

    @Override
    public AuditEvent createInfo(final String desc) {
        return this.create(AuditEvent.Type.INFO, null, desc, null, null);
    }

    //////////////////////////////////////////////////////////////////////////////
    //                     IAuditEventsProvider implementation                  //
    //////////////////////////////////////////////////////////////////////////////

    @Override
    public List<AuditEvent> getAllEvents() {
        return new ArrayList<AuditEvent>();
    }

    @Override
    public List<AuditEvent> getEvents(long afterEventId) {
        return new ArrayList<AuditEvent>();
    }

    @Override
    public void deleteEvents(long upToEventId) {
    }

    @Override
    public void deleteAllEvents() {
    }

    @Override
    public void cleanup(boolean useDates) {
    }

    @Override
    public long getLastAuditIdForHost(final String host, final boolean isCollectorHost) {
        return -100; // Error
    }

    //////////////////////////////////////////////////////////////////////////////
    //                             Private stuff                                //
    //////////////////////////////////////////////////////////////////////////////

    private boolean isEmpty(final String s) {
        return (s == null) || (s.length() == 0);
    }

    private String toEmpty(final String s) {
        return s == null ? "" : s;
    }

    private String processDesc(final String s, final AuditEvent event) {
        final Map<String, String> map = this.conf.getDescTemplate();

        if(map == null) {
            return s;
        }

        String template = map.get(event.getType() + "/" + event.getCategory());

        if(template == null) {
            return s;
        }

        template = template.replaceAll(DbField.ID.getTemplateVar(), event.getId() + "");
        template = template.replaceAll(DbField.APPCLASS.getTemplateVar(), toEmpty(event.getAppClass()));
        template = template.replaceAll(DbField.APPID.getTemplateVar(), toEmpty(event.getAppId()));
        template = template.replaceAll(DbField.HOST.getTemplateVar(), toEmpty(event.getHost()));
        template = template.replaceAll(DbField.VHOST.getTemplateVar(), toEmpty(event.getvHost()));
        template = template.replaceAll(DbField.UID.getTemplateVar(), toEmpty(event.getUid()));
        template = template.replaceAll(DbField.SID.getTemplateVar(), toEmpty(event.getSid()));
        template = template.replaceAll(DbField.RID.getTemplateVar(), toEmpty(event.getRid()));
        template = template.replaceAll(DbField.RDATA.getTemplateVar(), toEmpty(event.getrData()));
        template = template.replaceAll(DbField.DATETIME.getTemplateVar(), event.getDateTime());
        template = template.replaceAll(DbField.CLIENT.getTemplateVar(), toEmpty(event.getClient()));
        template = template.replaceAll("%message", toEmpty(s));

        if(
            (
                AuditEvent.Type.ERROR.toString().equals(event.getType()) ||
                AuditEvent.Type.WARN.toString().equals(event.getType()) ||
                AuditEvent.Type.INFO.toString().equals(event.getType()) ||
                AuditEvent.Type.FATAL.toString().equals(event.getType())
            )
            &&
            (
                AuditEvent.Category.STATUS_CHANGE.toString().equals(event.getCategory()) ||
                AuditEvent.Category.STATUS.toString().equals(event.getCategory())
            )
            &&
            (
                !isEmpty(event.getrData())
            )
        ) {
            template = template + " Reason: " + event.getrData();
        }

        return template;
    }
}
