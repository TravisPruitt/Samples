package junit;

import com.disney.xband.xi.model.DateUtil;
import com.disney.xband.xi.model.RecruitmentDAO;
import com.disney.xband.xi.model.ReturnMessage;
import com.google.gson.Gson;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 11/7/12
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestRecruitment {
    @Test
    public void test_recruitment() {
        RecruitmentDAO d = new RecruitmentDAO();
        Date programStartDate = DateUtil.dateAdd(new Date(), d.getWindowOffsetStart());
        long timestart = System.currentTimeMillis();
        String s= d.getRecruitment(programStartDate, DateUtil.dateAdd(new Date(), d.getWindowOffsetEnd()), new Date(), "buster");
        System.out.println("Recruitment get took:" + ((System.currentTimeMillis() - timestart)/1000));
        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        System.out.println(s);
        ReturnMessage rmsg = gson.fromJson(s, ReturnMessage.class);
        assertTrue(rmsg.getStatus().equals("success"));
    }

    @Test
    public void test_recruitmentCache() {
        RecruitmentDAO d = new RecruitmentDAO();
        Date programStartDate = DateUtil.dateAdd(new Date(), d.getWindowOffsetStart());
        long timestart = System.currentTimeMillis();
        String s= d.getRecruitment(programStartDate, DateUtil.dateAdd(new Date(), d.getWindowOffsetEnd()), new Date(), "buster");
        System.out.println("Recruitment get took:" + ((System.currentTimeMillis() - timestart)/1000));
        s=s.replace("remoteData(", "");
        s=s.replace(");","");
        Gson gson = new Gson();

        //System.out.println(s);
        ReturnMessage rmsg = gson.fromJson(s, ReturnMessage.class);
        assertTrue("Recruitment returned success", rmsg.getStatus().equals("success"));

        timestart = System.currentTimeMillis();
        String s1= d.getRecruitment(programStartDate, DateUtil.dateAdd(new Date(), d.getWindowOffsetEnd()), new Date(), "buster");
        s1=s1.replace("remoteData(", "");
        s1=s1.replace(");","");
        System.out.println("Recruitment get took:" + ((System.currentTimeMillis() - timestart)/1000));
        ReturnMessage rmsgCached = gson.fromJson(s1, ReturnMessage.class);

        Map<String, Object> origData = rmsg.getData();
        Map<String, Object> cData = rmsgCached.getData();

        Map mo = (Map)origData.get("recruited");
        Double oCount = (Double)mo.get("count");

        Map co = (Map)cData.get("recruited");
        Double cCount = (Double)co.get("count");

        //System.out.println("values " + cCount + " " + oCount);
        assertTrue("Recruitment cached value equals original", oCount.equals(cCount));
    }

}
