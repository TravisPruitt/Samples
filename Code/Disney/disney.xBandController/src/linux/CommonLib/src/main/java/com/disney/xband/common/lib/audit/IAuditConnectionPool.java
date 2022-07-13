package com.disney.xband.common.lib.audit;

import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: slavam
 * Date: 5/13/13
 * Time: 9:39 PM
 */
public interface IAuditConnectionPool {
    Connection getConnection() throws Exception;
    void releaseConnection(Connection conn);
}
