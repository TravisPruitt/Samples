package test;

import com.disney.xband.xi.model.ConfigDAO;
import org.junit.* ;
import static org.junit.Assert.*;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 11/7/12
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestConfig {
    @Test
    public void test_getFacilityList() {
        System.out.println("test retrieval of facilitylist");
        ConfigDAO cd  = new ConfigDAO();
        HashMap m = cd.getFacilityMap();

        assertFalse(m.isEmpty());
    }
}
