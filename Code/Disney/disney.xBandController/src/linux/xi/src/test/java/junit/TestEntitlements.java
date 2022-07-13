package test;

import com.disney.xband.xi.model.*;
import com.google.gson.Gson;

import java.util.*;

import org.junit.* ;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 11/8/12
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestEntitlements {

    private ConfigDAO configDAO;
    private EntitlementDAO ed;
    private int parkId;

    @Before
    public void setup() {
        configDAO = new ConfigDAO();
        ed = new EntitlementDAO();
        List parks = configDAO.getParks();
        parkId = (Integer)parks.get(0);
    }

    @Test
    public void test_getEntitlementSummary() {

        Date now = new Date();
        Date nowDayStart = DateUtil.setDayStartForDate(now);

        String s = ed.getEntitlementSummary(parkId, nowDayStart, now, "label", "buster");

        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        System.out.println(s);
        ReturnMessage r = gson.fromJson(s, ReturnMessage.class);

        Map<String, Object> data = r.getData();
        List l = (List)data.get("data");

        assertTrue("retrieved all facilities", l.size() == configDAO.getFacilityMapByPark(parkId).size());
        assertTrue(r.getStatus().equals("success"));
    }

    @Test
    public void test_getAttractionSummary() {
        HashMap<Integer, Facility> facilities = configDAO.getFacilityMapByPark(parkId);
        Iterator<Integer> iterator = facilities.keySet().iterator();

        // if it doesn't have next then we're all broken
        //if(iterator.hasNext())
        Facility f = facilities.get(iterator.next());

        Date now = new Date();
        Date nowDayStart = DateUtil.setDayStartForDate(now);

        String s = ed.getAttractionSummary(f.getFacilityId(), nowDayStart, now, "", "");

        System.out.println(s);
        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        //System.out.println(s);
        ReturnMessage r = gson.fromJson(s, ReturnMessage.class);
        Map<String, Object> data = r.getData();
        List l = (List)data.get("data");

        // hours in day 9-24
        assertTrue("retrieved all hours", l.size() == 15);
        assertTrue(r.getStatus().equals("success"));
    }
}
