package test;


import com.disney.xband.xi.model.ConfigDAO;
import com.disney.xband.xi.model.ExecSumDAO;
import com.disney.xband.xi.model.ReturnMessage;
import org.junit.* ;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 11/7/12
 * Time: 11:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestExecSummary {
    ReturnMessage nowMsg = null;

    @Test
    public void test_execSummaryNow() {
        ConfigDAO configDAO = new ConfigDAO();
        List parks = configDAO.getParks();
        ExecSumDAO d = new ExecSumDAO();
        String s = d.getCurrentExecSummary((Integer)parks.get(0), new Date(), "now", "buster");
        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        //System.out.println(s);
        nowMsg = gson.fromJson(s, ReturnMessage.class);
        assertTrue(nowMsg.getStatus().equals("success"));
    }

    /*
    @Test
    public void test_execSummaryYesterday() {
        ConfigDAO configDAO = new ConfigDAO();
        List parks = configDAO.getParks();
        ExecSumDAO d = new ExecSumDAO();
        String s = d.getCurrentExecSummary((Integer)parks.get(0), new Date(), "now", "buster");
        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        //System.out.println(s);
        nowMsg = gson.fromJson(s, ReturnMessage.class);
        assertTrue(nowMsg.getStatus().equals("success"));
    }*/

    @Test
    public void test_getCal() {
        ConfigDAO configDAO = new ConfigDAO();
        List parks = configDAO.getParks();
        ExecSumDAO d = new ExecSumDAO();
        String s = d.getCalendar((Integer)parks.get(0), new Date(), 15, "buster");
        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        //System.out.println(s);
        ReturnMessage rmsg = gson.fromJson(s, ReturnMessage.class);
        assertTrue(rmsg.getStatus().equals("success"));
    }

    // test caching of key bits by comparing two immediately successive runs

}
