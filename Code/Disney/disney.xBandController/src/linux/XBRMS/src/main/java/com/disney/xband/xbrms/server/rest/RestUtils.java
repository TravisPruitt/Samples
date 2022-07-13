package com.disney.xband.xbrms.server.rest;

import com.disney.xband.xbrms.common.XbrmsUtils;
import com.disney.xband.xbrms.common.model.DtoWrapper;
import com.disney.xband.xbrms.server.model.XbrmsConfigBo;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 1/17/13
 * Time: 7:31 PM
 */
public class RestUtils {
    public static <T> DtoWrapper<T> wrap(T object, Logger logger, ServletContext context) {
        final DtoWrapper<T> wrapper = new DtoWrapper<T>();
        wrapper.setVersion(XbrmsUtils.getXbrmsVersion(logger, context));
        wrapper.setName(XbrmsConfigBo.getInstance().getDto().getName());
        wrapper.setId(XbrmsConfigBo.getInstance().getDto().getId());
        wrapper.setTime(Calendar.getInstance().getTime());
        wrapper.setContent(object);

        // RestUtils.saveObject(object);

        return wrapper;
    }

    private static long count;

    public static void saveObject(Object o) {
        try {
            int ind = o.getClass().getName().lastIndexOf(".") + 1;
            File file = new File("/tmp/jaxb/" + o.getClass().getName().substring(ind) + "-" + count++ + ".xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(o.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(o, file);

        }
        catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
