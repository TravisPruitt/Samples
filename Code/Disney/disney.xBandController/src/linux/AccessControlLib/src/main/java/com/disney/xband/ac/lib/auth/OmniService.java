package com.disney.xband.ac.lib.auth;

import org.apache.log4j.Logger;

import java.sql.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 10/29/12
 * Time: 5:32 PM
 */
public class OmniService {
    final long CACHE_UPDATE_INTERVAL = 60000; // 1 minute

    protected Logger logger;
    protected HashMap<String, String> props;
    long lastUpdated;
    Map<String, OmniUser> usersCache;

    public void init(HashMap<String, String> props) {
        this.logger = Logger.getLogger(this.getClass());
        this.props = props;
        this.usersCache = new HashMap<String, OmniUser>();
    }

    public OmniUser getOmniUser(final String name) {
        try {
            if((System.currentTimeMillis() - this.lastUpdated) > CACHE_UPDATE_INTERVAL) {
                synchronized (this.usersCache) {
                    this.usersCache = OmniService.findAll(this.getConnection());
                    this.lastUpdated = System.currentTimeMillis();
                }
            }

            return this.usersCache.get(name);
        }
        catch(Exception e) {
        }

        return new OmniUser(name, "", "");
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException
    {
        // Open database connection
        final String url = this.props.get("nge.xconnect.xbrc.dbserver.service");
        final String user = this.props.get("nge.xconnect.xbrc.dbserver.uid");
        final String password = this.props.get("nge.xconnect.xbrc.dbserver.pwd");

        Class.forName("com.mysql.jdbc.Driver");
        final Properties connProps = new Properties();
        connProps.put("user", user);
        connProps.put("password", password);

        final Connection conn = DriverManager.getConnection(url, connProps);
        return conn;
    }

    private static Map<String, OmniUser> findAll(Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try
        {
            final String query = "select * from CastMember order by name";
            stmt = conn.prepareStatement(query);
            stmt.execute();
            rs = stmt.getResultSet();
            final Map<String, OmniUser> results = new HashMap<String, OmniUser>();

            while(rs.next()) {
                final OmniUser user = OmniService.instantiateCastMember(rs);
                results.put(user.getName(), user);
            }

            return results;
        }
        finally
        {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
            } catch(Exception e) {}
        }
    }

    private static OmniUser instantiateCastMember(ResultSet rs) throws SQLException {
        return new OmniUser(
            rs.getString("name"),
            rs.getString("omniUsername"),
            rs.getString("omniPassword")
        );
    }

    public static class OmniUser {
        private String name;
        private String omniUsername;
        private String omniPassword;

        public OmniUser(final String name, final String omniUsername, final String omniPassword) {
            this.name = name;
            this.omniUsername = omniUsername;
            this.omniPassword = omniPassword;
        }

        public String getName() {
            return this.name;
        }

        public String getOmniUsername() {
            return this.omniUsername;
        }

        public String getOmniPassword() {
            return this.omniPassword;
        }
    }
}
