package com.disney.xband.xi;

import com.disney.xband.xi.model.DAO;
import com.disney.xband.xi.model.DateUtil;
import org.apache.log4j.Logger;

import javax.xml.bind.DatatypeConverter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class XiResource
{
    public int getWindowStartDelta() {
        return DAO.getWindowOffsetStart();
    }

    public int getWindowEndDelta() {
        return DAO.getWindowOffsetEnd();
    }

	static Logger logger = Logger.getLogger(XiResource.class);
    public Date parseCurrentDateUtil(String useDate) throws ParseException {
        Date dateObj;
        if(useDate.length() == 0) {
            dateObj = DateUtil.getCurrentDate();
        }
        else {
            dateObj=DateUtil.tstzformatter.parse(useDate);
        }

        return dateObj;
    }

    public static String GetUTCdatetimeAsString(Date date)
    {
        final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(date);

        return utcTime;
    }


    public static String convertHexToString(String inStr)
    {
        return new String(DatatypeConverter.parseBase64Binary(inStr));
    }

}
