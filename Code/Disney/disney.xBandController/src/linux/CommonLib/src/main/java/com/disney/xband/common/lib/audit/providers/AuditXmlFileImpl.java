package com.disney.xband.common.lib.audit.providers;

import com.disney.xband.common.lib.audit.model.AuditConfig;
import com.disney.xband.common.lib.audit.model.AuditEvent;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/3/13
 * Time: 11:16 AM
 */
public class AuditXmlFileImpl extends AuditFileImpl {
    public AuditXmlFileImpl(final AuditConfig conf) {
        super(conf);
    }

    // Protected methods
    @Override
    protected AuditEvent parseEvent(String s) {
        try {
            final JAXBContext context = JAXBContext.newInstance(AuditEvent.class);
            final Unmarshaller m = context.createUnmarshaller();

            return (AuditEvent) m.unmarshal(new StringReader(s));
        }
        catch (Exception ignore) {
        }

        return null;
    }

    @Override
    protected String prepareForWrite(final AuditEvent event) {
        try {
            final StringWriter writer = new StringWriter();
            final JAXBContext context = JAXBContext.newInstance(AuditEvent.class);
            final Marshaller m = context.createMarshaller();
            m.marshal(event, writer);

            return writer.toString() + "\n";
        }
        catch (Exception ignore) {
        }

        return null;
   }
}
