package com.disney.xband.xi.model;

import java.io.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 7/18/12
 * Time: 10:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class XIPageDAO extends DAO
{
    private static String xiPage = null;
    private static String currentPageGUID = null;


    public XIPageDAO() {

    }

    public String setXiPage(String guid, String fileName, String writePath, String pageData) {

        Connection connection = null;
        ReturnMessage rmsg = new ReturnMessage();
        CallableStatement statement = null;
        ResultSet rs = null;
        BufferedWriter outWriter = null;

        try
        {
            connection = this.getConnection();

            String sCheckQuery = "{call  [dbo].[usp_CheckGUIDForHTMLUpdate](?)}";
            statement = connection.prepareCall(sCheckQuery);
            statement.setString(1, guid);
            rs=statement.executeQuery();
            if(rs.next()) {
                int retVal = rs.getInt(1);
                if(retVal == -1) {
                    throw new DAOException("Check failed for guid:" + guid);
                }
            }
            else {
                throw new DAOException("Check query failed for guid:" + guid);
            }
        }
        catch (SQLException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        catch (DAOException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }
        }

        rs = null;
        statement = null;

        try {
            // now try to write file to fs
            /*
            if(writePath.charAt(writePath.length()-1) == fileWriteFai'/') {
                writePath = writePath.substring(0, writePath.length()-2);
            }*/
            try {
                outWriter = new BufferedWriter(new FileWriter(writePath + fileName));
                outWriter.write(pageData);
                outWriter.close();
            }
            catch(IOException e){
                logger.error("Problem writing file as:" + writePath + fileName);
                logger.error(e.getLocalizedMessage());
                return this.errorMessage(e.getMessage(), "fileWriteFail");
            }
            finally {
                if(outWriter != null) {
                    try {
                        outWriter.close();
                    }
                    catch(IOException e) {}
                    }
            }

            // ok, so the guid is ok...
            String sQuery = "{call  [dbo].[usp_UpdateHTMLPage](?,?,?)}";
            statement = connection.prepareCall(sQuery);
            statement.setString(1, pageData);
            statement.setString(2, guid);
            statement.setString(3, "");
            rs=statement.executeQuery();
            int retVal = -1;

            if(rs.next()) {
                retVal = rs.getInt(1);
                if(retVal == -1) {
                    throw new DAOException("Could not persist data for guid:" + guid);
                }
            }
            else {
                throw new DAOException("Check query -- could not persist data for guid:" + guid);
            }
            // set the static version of this
            //xiPage = pageData;

            rmsg.addData("created new XIPage", guid);
            // return
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        catch (DAOException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        finally
        {
            if (outWriter != null)
                try {
                    outWriter.close();
                }
                catch (IOException e) {
                }

            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception e) {
                }
            }
        }
    }

    private String _getXiPage() throws DAOException, SQLException {
        if(xiPage == null) {
            _getXiPageFromDb();
        }

        return xiPage;
    }

    public String propXiPage(String writePath, String filename) {
        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;
        ReturnMessage rmsg = new ReturnMessage();

        try
        {
            String sQuery = "{call  [dbo].[usp_GetHTMLPage](?)}";
            connection = this.getConnection();
            statement = connection.prepareCall(sQuery);
            statement.setString(1, writePath + filename);
            rs=statement.executeQuery();
            if(rs.next()) {
                xiPage = rs.getString(1);

                //use buffering
                Writer output = new BufferedWriter(new FileWriter(writePath + filename));
                try {
                    //FileWriter always assumes default encoding is OK!
                    output.write( xiPage );
                }
                finally {
                    output.close();
                }
                rmsg.addData("file written", filename);
                return "remoteData(" + gson.toJson(rmsg) + ");";
            }
            else {
                throw new DAOException("Could not retrieve xi page");
            }
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return errorMessage(e.getLocalizedMessage());
        } catch (DAOException e) {
            logger.error(e.getLocalizedMessage());
            return errorMessage(e.getLocalizedMessage());
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            return errorMessage(e.getLocalizedMessage());
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception e) {
                }
            }
        }
    }


    private void _getXiPageFromDb() throws DAOException, SQLException {

        Connection connection = null;
        CallableStatement statement = null;
        ResultSet rs = null;

        try
        {
            String sQuery = "{call  [dbo].[usp_GetHTMLPage]}";
            connection = this.getConnection();
            statement = connection.prepareCall(sQuery);

            rs=statement.executeQuery();

            if(rs.next()) {
                xiPage = rs.getString(1);
            }
            else {
                throw new DAOException("Could not retrieve xi page");
            }
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception e) {
                }
            }
        }
    }

    public String getXiPage() {
       try  {
           return _getXiPage();
       }
       catch(SQLException e) {
           logger.error(e.getLocalizedMessage());
           return "sqlerror -- see logs";
       }
       catch(DAOException e) {
           logger.error(e.getLocalizedMessage());
           return "daoerror -- see logs";
       }
    }

    public String getXiPageAsJSON() {
       try
       {
           return "remoteData(" + gson.toJson(_getXiPage()) + ");";
       }
       catch(SQLException e) {
           logger.error(e.getLocalizedMessage());
           return errorMessage(e.getLocalizedMessage());
       }
       catch(DAOException e) {
           logger.error(e.getLocalizedMessage());
           return errorMessage(e.getLocalizedMessage());
       }
    }

    public String uploadEncPage(String fileName, String writePath, String guid, InputStream inputStream) {
        Connection connection = null;
        ReturnMessage rmsg = new ReturnMessage();
        CallableStatement statement = null;
        ResultSet rs = null;
        StringBuilder sb = new StringBuilder();

        try
        {
            connection = this.getConnection();
            String sCheckQuery = "{call  [dbo].[usp_CheckGUIDForHTMLUpdate](?)}";
            statement = connection.prepareCall(sCheckQuery);
            statement.setString(1, guid);
            rs=statement.executeQuery();
            if(rs.next()) {
                int retVal = rs.getInt(1);
                if(retVal == -1) {
                    logger.debug("Check failed for guid:" + guid);
                    throw new DAOException("Check failed for guid:" + guid);
                }
            }
            else {
                logger.debug("Check query failed for guid:" + guid);
                throw new DAOException("Check query failed for guid:" + guid);
            }

            FileOutputStream fout=null;
            //Base64InputStream bis=null;
            List<Byte> bytelist = new ArrayList<Byte>();

            String guidString = System.getProperty("line.separator") +
            "<script>(function(){window.guid = '" + guid + "';})();</script>";

            try {
                byte[] buffer = new byte[1024];
                int len;
                //bis =new Base64InputStream(inputStream);
                fout = new FileOutputStream(new File(writePath + fileName));
                //while ((c = bis.read()) != -1) {
                while ((len = inputStream.read(buffer)) != -1) {
                    fout.write(buffer, 0, len);
                }
                fout.write(guidString.getBytes());
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return e.getLocalizedMessage();
            }
            finally {
                /*
                if(bis != null)    {
                    try { bis.close(); } catch (IOException e) {};
                }*/
                if(fout != null) {
                    try { fout.close(); } catch (IOException e) {};
                }
                if(inputStream != null) {
                    try { inputStream.close(); } catch (IOException e) {};
                }
            }
            logger.debug("file write done");

            // now read back file -- very inefficient but this method shouldn't be called much

            BufferedReader input =  null;
            try  {
                input =  new BufferedReader(new FileReader(new File(writePath + fileName)));

                String line = null;
                /*
                * readLine is a bit quirky :
                * it returns the content of a line MINUS the newline.
                * it returns null only for the END of the stream.
                * it returns an empty String if two newlines appear in a row.
                */
                while (( line = input.readLine()) != null){
                    sb.append(line);
                    sb.append(System.getProperty("line.separator"));
                }
                sb.append(guidString);

            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            finally {
                if(input != null) {
                    try { input.close(); } catch (Exception e) {}
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        catch (DAOException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }
        }

        rs = null;
        statement = null;

        try {
            // ok, so the guid is ok...
            String sQuery = "{call  [dbo].[usp_UpdateHTMLPage](?,?,?)}";
            statement = connection.prepareCall(sQuery);
            statement.setString(1, sb.toString());
            statement.setString(2, guid);
            statement.setString(3, fileName);
            rs=statement.executeQuery();
            int retVal = -1;

            if(rs.next()) {
                retVal = rs.getInt(1);
                if(retVal == -1) {
                    throw new DAOException("Could not persist data for guid:" + guid);
                }
            }
            else {
                throw new DAOException("Check query -- could not persist data for guid:" + guid);
            }



            rmsg.addData("created new XIPage", guid);
            // return
            return "remoteData(" + gson.toJson(rmsg) + ");";
        }
        catch (SQLException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        catch (DAOException e)
        {
            logger.error(e.getLocalizedMessage());
            return this.errorMessage(e.getMessage(), "setXiPage");
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception e) {
                }
            }
        }
    }

    public String guidCheck(String guid, String fileName, String writePath) {
        // hit database and find out what latest guid is
        // if no resultset, then return fast
        // if matched, then return empty function
        // if no match, write out version from the database to disk
        Connection connection = null;
        ReturnMessage rmsg = new ReturnMessage();
        CallableStatement statement = null;
        ResultSet rs = null;
        BufferedWriter outWriter = null;

        try
        {
            connection = this.getConnection();
            String fullPagePath = writePath + fileName;

            logger.debug("getting current guid: " + fileName);
            //String sCheckQuery = "{call  [dbo].[usp_GetCurrentGUID](?)}";
            //statement = connection.prepareCall(sCheckQuery);
            //statement.setString(1, fileName);
            //rs=statement.executeQuery();
            //if(rs.next()) {
            //    String dbGuid = rs.getString(1);
            //    if(dbGuid.equals(guid)) {
            //        // also good
            //        // return empty function
            //        return "(function() {})();";
            //    }
            //    else {
                // write out to disk
            logger.debug("getting html page: " + fileName);
            String sQuery = "{call  [dbo].[usp_GetHTMLPage](?)}";
            statement = connection.prepareCall(sQuery);
            statement.setString(1, fileName);
            rs=statement.executeQuery();

            if(rs.next()) { // is there a copy of the file in the db?
                String pageData = rs.getString(1);


                String guidPatternMatch = "\\(function\\(\\)\\{window\\.guid \\= \\'([^\']+)\\'\\;\\}\\)\\(\\)\\;";

                Pattern pattern = Pattern.compile(guidPatternMatch);
                Matcher matcher = pattern.matcher(pageData);
                String guidMatch = null;

                logger.debug("about to find");
                if (matcher.find()) {
                    logger.debug("found");
                    guidMatch = matcher.group(1);
                }

                logger.debug("guid:" + guidMatch);

                if((guidMatch != null) && guidMatch.equals(guid)) {
                    return "(function() {})();"; // is this the same copy that we have?
                }
                else {
                    try {
                        outWriter = new BufferedWriter(new FileWriter(writePath + fileName));
                        outWriter.write(pageData);
                        outWriter.close();

                        return "(function() {location.reload();})();";
                    }
                    catch(IOException e){
                        logger.error("Problem writing file as:" + writePath + fileName);
                        logger.error(e.getLocalizedMessage());
                        return "";
                    }
                    finally {
                        try {
                            if(outWriter != null) {
                                outWriter.close();
                            }
                        }
                        catch(IOException e) {}
                    }
                }
            }
            else {
                return "(function() {})();";
            }
               // }
            //}
            //else {
                // early return
            //    return "(function() {})();";
            //}
        } catch (SQLException e) {
            logger.error(e.getLocalizedMessage());
            return errorMessage(e.getLocalizedMessage());
        } catch (DAOException e) {
            logger.error(e.getLocalizedMessage());
            return errorMessage(e.getLocalizedMessage());
        }
        finally
        {
            if (rs != null) {
                try {
                    connectionPool.close(rs);
                }
                catch (Exception e) {
                }
            }

            if (statement != null) {
                try {
                    connectionPool.close(statement);
                }
                catch (Exception e) {
                }
            }

            if (connection != null) {
                try {
                    connectionPool.release(connection);
                }
                catch (Exception e) {
                }
            }
        }
    }
}
