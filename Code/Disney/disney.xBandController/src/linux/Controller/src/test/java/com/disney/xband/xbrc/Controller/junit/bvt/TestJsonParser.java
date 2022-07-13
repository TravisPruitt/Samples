package com.disney.xband.xbrc.Controller.junit.bvt;

import com.disney.xband.lib.xbrapi.XbioDiagEvent;
import com.disney.xband.lib.xbrapi.XbrEvents;
import com.disney.xband.lib.xbrapi.XbrJsonMapperFast;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/8/12
 * Time: 9:47 AM
 */
public class TestJsonParser {

    @Test
    public void testReaderNameFirst() throws Exception
    {
        final String s = "{" +
                "   \"reader name\" : \"joef-buzz-hw\"," +
                "   \"events\" : [" +
                "      {" +
                "         \"eno\" : 34," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }" +
                "   ]" +
                "}";

        final XbrEvents e = XbrJsonMapperFast.mapEvents(s);

        Assert.assertTrue(e.getEvents().size() == 1);
        Assert.assertTrue(e.getEvents().get(0).getReaderName().equals("joef-buzz-hw"));
        Assert.assertTrue((e.getEvents().get(0)).getEno() == 34);
        Assert.assertTrue((e.getEvents().get(0)).getTime().toString().equals("Fri Oct 05 18:36:49 PDT 2012"));
        Assert.assertTrue((e.getEvents().get(0)).getType().toString().equals("xbio-diagnostics"));
        Assert.assertTrue(((XbioDiagEvent) e.getEvents().get(0)).getXbioData().equals("fQBwPyAAHwAdAA=="));
    }

    @Test
    public void testReaderEventsFirst() throws Exception
    {
        final String s = "{" +
                "   \"events\" : [" +
                "      {" +
                "         \"eno\" : 34," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }" +
                "   ]," +
                "   \"reader name\" : \"joef-buzz-hw\"" +
                "}";

        final XbrEvents e = XbrJsonMapperFast.mapEvents(s);

        Assert.assertTrue(e.getEvents().size() == 1);
        Assert.assertTrue(e.getEvents().get(0).getReaderName().equals("joef-buzz-hw"));
        Assert.assertTrue((e.getEvents().get(0)).getEno() == 34);
        Assert.assertTrue((e.getEvents().get(0)).getTime().toString().equals("Fri Oct 05 18:36:49 PDT 2012"));
        Assert.assertTrue((e.getEvents().get(0)).getType().toString().equals("xbio-diagnostics"));
        Assert.assertTrue(((XbioDiagEvent) e.getEvents().get(0)).getXbioData().equals("fQBwPyAAHwAdAA=="));
    }

    @Test
    public void testReaderNameFirstMult() throws Exception
    {
        final String s = "{" +
                "   \"reader name\" : \"joef-buzz-hw\"," +
                "   \"events\" : [" +
                "      {" +
                "         \"eno\" : 34," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }," +
                "      {" +
                "         \"eno\" : 35," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }" +
                "   ]" +
                "}";

        final XbrEvents e = XbrJsonMapperFast.mapEvents(s);

        Assert.assertTrue(e.getEvents().size() == 2);
        Assert.assertTrue(e.getEvents().get(0).getReaderName().equals("joef-buzz-hw"));
        Assert.assertTrue((e.getEvents().get(0)).getEno() == 34);
        Assert.assertTrue((e.getEvents().get(0)).getTime().toString().equals("Fri Oct 05 18:36:49 PDT 2012"));
        Assert.assertTrue((e.getEvents().get(0)).getType().toString().equals("xbio-diagnostics"));
        Assert.assertTrue(((XbioDiagEvent) e.getEvents().get(0)).getXbioData().equals("fQBwPyAAHwAdAA=="));

        Assert.assertTrue(e.getEvents().get(1).getReaderName().equals("joef-buzz-hw"));
        Assert.assertTrue((e.getEvents().get(1)).getEno() == 35);
        Assert.assertTrue((e.getEvents().get(1)).getTime().toString().equals("Fri Oct 05 18:36:49 PDT 2012"));
        Assert.assertTrue((e.getEvents().get(1)).getType().toString().equals("xbio-diagnostics"));
        Assert.assertTrue(((XbioDiagEvent) e.getEvents().get(1)).getXbioData().equals("fQBwPyAAHwAdAA=="));
    }

    @Test
    public void testReaderEventsFirstMult() throws Exception
    {
        final String s = "{" +
                "   \"events\" : [" +
                "      {" +
                "         \"eno\" : 34," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }," +
                "      {" +
                "         \"eno\" : 35," +
                "         \"time\" : \"2012-10-06T01:36:49.47\"," +
                "         \"type\" : \"xbio-diagnostics\"," +
                "         \"xbio-data\" : \"fQBwPyAAHwAdAA==\"" +
                "      }" +
                "   ]," +
                "   \"reader name\" : \"joef-buzz-hw\"" +
                "}";

        final XbrEvents e = XbrJsonMapperFast.mapEvents(s);

        Assert.assertTrue(e.getEvents().size() == 2);
        Assert.assertTrue(e.getEvents().get(0).getReaderName().equals("joef-buzz-hw"));
        Assert.assertTrue((e.getEvents().get(0)).getEno() == 34);
        Assert.assertTrue((e.getEvents().get(0)).getTime().toString().equals("Fri Oct 05 18:36:49 PDT 2012"));
        Assert.assertTrue((e.getEvents().get(0)).getType().toString().equals("xbio-diagnostics"));
        Assert.assertTrue(((XbioDiagEvent) e.getEvents().get(0)).getXbioData().equals("fQBwPyAAHwAdAA=="));

        Assert.assertTrue(e.getEvents().get(1).getReaderName().equals("joef-buzz-hw"));
        Assert.assertTrue((e.getEvents().get(1)).getEno() == 35);
        Assert.assertTrue((e.getEvents().get(1)).getTime().toString().equals("Fri Oct 05 18:36:49 PDT 2012"));
        Assert.assertTrue((e.getEvents().get(1)).getType().toString().equals("xbio-diagnostics"));
        Assert.assertTrue(((XbioDiagEvent) e.getEvents().get(1)).getXbioData().equals("fQBwPyAAHwAdAA=="));
    }
}
