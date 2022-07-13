package com.disney.xband.common.lib.junit.audit;

import com.disney.xband.common.lib.audit.*;
import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;
import com.disney.xband.common.lib.audit.model.AuditInterceptorConfig;
import com.disney.xband.common.lib.security.UserContext;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/7/13
 * Time: 12:00 PM
 */
public class TestAudit {
    private static final String AUDIT_FILE_PATH = "/tmp/events.txt";

    @Before
    public void setUp() {
        try {
            new File(TestAudit.AUDIT_FILE_PATH).delete();
        }
        catch (Exception ignore) {
        }
    }

    @Test
    public void testAuditFileImpl() {
        this.doTestAuditImpl("com.disney.xband.common.lib.audit.providers.AuditFileImpl", "/tmp/events.txt");
    }

    @Test
    public void testAuditXmlFileImpl() {
        this.doTestAuditImpl("com.disney.xband.common.lib.audit.providers.AuditXmlFileImpl", "/tmp/events.txt");
    }

    @Test
    public void testSimpleFilter() {
        setUser();
        final AuditConfig config = new AuditConfig();

        config.setEnabled(true);
        config.setImplStack("com.disney.xband.common.lib.audit.providers.AuditFileImpl");
        config.setFileStorePath("/tmp/events.txt");

        config.setAppClass(AuditEvent.AppClass.XBRMS.toString());
        config.setAppId("Magic Kingdom");
        config.setHost("slavam-linuxvm-1");
        config.setvHost("slavam-linuxvm");

        Properties props = new Properties();
        props.put(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_CLASS_BASE + ".0", "com.disney.xband.common.lib.audit.interceptors.SimpleFilter");
        props.put(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_PARAMS_BASE + ".0",
                "AUDIT_SUCCESS:ACCESS::1,AUDIT_FAILURE:ACCESS::1,.*/audit/pull.*,.*/audit/delete.*,.*/getauditevents.*,.*/deleteauditevents.*");

        config.setInterceptors(AuditConfig.createAuditInterceptorConfigsFromProps(props, false));
        config.setDescTemplate(AuditConfig.createDescriptionTemplatesFromProps(props));

        final AuditFactory auditFactory = new AuditFactory(config);
        final IAudit audit = auditFactory.getAudit();
        final IAuditEventsProvider provider = auditFactory.getAuditControl().getEventsProvider("AuditFileImpl");

        AuditEvent event = new AuditEvent();
        List<AuditEvent> list;

        event.setType(AuditEvent.Type.AUDIT_SUCCESS.toString());
        event.setCategory(AuditEvent.Category.ACCESS.toString());
        event.setUid("");
        event.setClient("localhost");

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 1);

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 1);

        try {Thread.sleep(1500);} catch (Exception e) {};

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 2);

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 2);

        event.setType(AuditEvent.Type.AUDIT_FAILURE.toString());
        event.setCategory(AuditEvent.Category.ACCESS.toString());
        event.setUid("");
        event.setClient("localhost");

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 3);

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 3);

        try {Thread.sleep(1500);} catch (Exception e) {};

        audit.audit(event);
        list = provider.getAllEvents();
        assertTrue(list.size() == 4);

        audit.audit(audit.createReadSuccess("rest/audit/pull/9898"));
        list = provider.getAllEvents();
        assertTrue(list.size() == 4);

        audit.audit(audit.createReadSuccess("rest/getauditevents/9898"));
        list = provider.getAllEvents();
        assertTrue(list.size() == 4);

        audit.audit(audit.createReadSuccess("rest/audit/delete/9898"));
        list = provider.getAllEvents();
        assertTrue(list.size() == 4);

        audit.audit(audit.createReadSuccess("rest/deleteauditevents/9898"));
        list = provider.getAllEvents();
        assertTrue(list.size() == 4);

        audit.audit(audit.createReadSuccess("rest/somethingelse/9898"));
        list = provider.getAllEvents();
        assertTrue(list.size() == 5);

    }

    @Test
    public void testInterceptor() {
        setUser();
        final AuditConfig config = new AuditConfig();

        config.setEnabled(true);
        config.setImplStack("com.disney.xband.common.lib.audit.providers.AuditFileImpl");
        config.setFileStorePath("/tmp/events.txt");

        config.setAppClass(AuditEvent.AppClass.XBRMS.toString());
        config.setAppId("Magic Kingdom");
        config.setHost("slavam-linuxvm-1");
        config.setvHost("slavam-linuxvm");

        Properties props = new Properties();
        props.put(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_CLASS_BASE + ".0", "com.disney.xband.common.lib.audit.interceptors.FilterBase");
        props.put(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_PARAMS_BASE + ".0", "param0");

        props.put(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_CLASS_BASE + ".2", "com.disney.xband.common.lib.audit.interceptors.SimpleFilter");
        props.put(PkAuditConstants.PROP_NAME_AUDIT_INTERCEPTOR_PARAMS_BASE + ".2", "param2");

        config.setInterceptors(AuditConfig.createAuditInterceptorConfigsFromProps(props, false));
        config.setDescTemplate(AuditConfig.createDescriptionTemplatesFromProps(props));

        final AuditFactory auditFactory = new AuditFactory(config);
        final IAudit audit = auditFactory.getAudit();
        final IAuditEventsProvider provider = auditFactory.getAuditControl().getEventsProvider("AuditFileImpl");

        IAuditControl ctrl = auditFactory.getAuditControl();

        // Add a test interceptor
        ctrl.addInterceptor(new IAuditEventsInterceptor() {
            private AuditInterceptorConfig ic;

            @Override
            public void init(AuditInterceptorConfig conf) {
                this.ic = conf;
            }

            @Override
            public String getId() {
                return this.ic.getId();
            }

            @Override
            public AuditEvent intercept(AuditEvent event) {
                return event.getCategory() != AuditEvent.Category.ACCESS.toString() ? null : event;
            }
        }, new AuditInterceptorConfig("TestInterceptor1", null));

        audit.audit(audit.createWarn("This message will be filtered out"));
        List<AuditEvent> list = provider.getAllEvents();
        assertTrue(list.size() == 0);

        ctrl.removeInterceptor("Tryint to remove a bogus interceptor");
        ctrl.removeInterceptor("TestInterceptor1");

        audit.audit(audit.createWarn("This message will not be filtered out"));
        list = provider.getAllEvents();
        assertTrue(list.size() == 1);
    }

    private void doTestAuditImpl(String implClassName, String storagePath) {
        setUser();
        final AuditConfig config = new AuditConfig();

        config.setEnabled(true);
        config.setImplStack(implClassName);
        config.setFileStorePath(storagePath);

        config.setAppClass(AuditEvent.AppClass.XBRMS.toString());
        config.setLevel(AuditEvent.Type.INFO);
        config.setAppId("Magic Kingdom");
        config.setHost("slavam-linuxvm-1");
        config.setvHost("slavam-linuxvm");

        final AuditFactory auditFactory = new AuditFactory(config);
        final IAudit audit = auditFactory.getAudit();

        audit.audit(audit.createError("Test error"));
        audit.audit(audit.createWarn("Test warning"));
        audit.audit(audit.createInfo("Test info"));
        audit.audit(audit.createAccessFailure("XBRMS/irest/status"));
        audit.audit(audit.createAccessSuccess("XBRMS/irest/status"));
        audit.audit(audit.createWarn("JMS listener is not configured."));
        audit.audit(audit.createLogonSuccess());
        audit.audit(audit.createReadSuccess("XBRMS/rest/health/type/idms"));
        audit.audit(audit.createWriteFailure("XBRMS/rest/xbrcs/3267", "someVariable=someValue"));

        IAuditEventsProvider provider = auditFactory.getAuditControl().getEventsProvider(implClassName);

        List<AuditEvent> list = provider.getAllEvents();
        assertTrue(list.size() == 9);
        AuditEvent event = list.get(8);
        assertTrue(event.getType().equals(AuditEvent.Type.AUDIT_FAILURE.toString()));
        assertTrue(event.getCategory().equals(AuditEvent.Category.WRITE.toString()));
        assertTrue(AuditEvent.AppClass.XBRMS.toString().equals(event.getAppClass()));
        assertTrue(event.getAppId().equals("Magic Kingdom"));
        assertTrue(event.getHost().equals("slavam-linuxvm-1"));
        assertTrue(event.getvHost().equals("slavam-linuxvm"));
        assertTrue(event.getUid().equals("synapsedev\\slavam"));
        assertTrue(event.getSid().equals("DFAFDAFDAFAFD"));
        assertTrue(event.getRid().equals("XBRMS/rest/xbrcs/3267"));
        assertTrue(event.getrData().equals("someVariable=someValue"));
        assertTrue(event.getClient().equals("client.host.name.com"));

        int a = 4;
        list = provider.getEvents(a);
        assertTrue(list.size() == 5);

        provider.deleteEvents(a);
        list = provider.getAllEvents();
        assertTrue(list.size() == 5);

        list = provider.getEvents(a);
        assertTrue(list.size() == 5);

        provider.deleteEvents(a);
        provider.deleteAllEvents();
        list = provider.getAllEvents();
        assertTrue(list.size() == 0);
    }

    private void setUser() {
        UserContext.instance.set(new UserContext("synapsedev\\slavam", "DFAFDAFDAFAFD", null, null, "client.host.name.com"));
    }
}
