package com.disney.xband.common.lib;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.disney.xband.common.lib.health.StatusType;

public abstract class ConnectionPool {
	
	private static Map<String,String> sqlStateCodes;
	
	static {
		// this is NOT an exhaustive list
		sqlStateCodes = Collections.synchronizedMap(new HashMap<String, String>());
		sqlStateCodes.put("00000", "Success");	
		sqlStateCodes.put("01000", "General warning");
		sqlStateCodes.put("01001", "Cursor operation conflict");	
		sqlStateCodes.put("01002", "Disconnect error");
		sqlStateCodes.put("01004", "Data truncated");	
		sqlStateCodes.put("01006", "Privilege not revoked");
		sqlStateCodes.put("01007", "Privilege not granted");
		sqlStateCodes.put("01S00", "Invalid connection string attribute");
		sqlStateCodes.put("01S01", "Error in row");
		sqlStateCodes.put("01S02", "Option value changed");
		sqlStateCodes.put("01S06", "Attempt to fetch before the result set returned the first rowset");
		sqlStateCodes.put("01S07", "Fractional truncation");
		sqlStateCodes.put("01S08", "Error saving DSN File");
		sqlStateCodes.put("01S09", "Invalid keyword");
		sqlStateCodes.put("07001", "Wrong number of parameters");
		sqlStateCodes.put("07002", "Mismatching parameters");
		sqlStateCodes.put("07003", "Cursor specification cannot be executed");
		sqlStateCodes.put("07004", "Missing parameters");
		sqlStateCodes.put("07005", "Invalid cursor state");
		sqlStateCodes.put("07006", "Restricted data type attribute violation");
		sqlStateCodes.put("07009", "Invalid descriptor count");
		sqlStateCodes.put("07S01", "Invalid use of default parameter");
		sqlStateCodes.put("08000", "Connection exception");
		sqlStateCodes.put("08001", "Unable to connect to the data source, e.g. invalid license key");
		sqlStateCodes.put("08002", "Connection already in use");
		sqlStateCodes.put("08003", "Connection not open");
		sqlStateCodes.put("08004", "Data source rejected establishment of connection");
		sqlStateCodes.put("08007", "Connection failure during transaction");
		sqlStateCodes.put("08900", "Server lookup failed");
		sqlStateCodes.put("08S01", "Communication link failure");
		sqlStateCodes.put("21000", "Cardinality violation");
		sqlStateCodes.put("21S01", "Insert value list does not match column list");
		sqlStateCodes.put("21S02", "Degree of derived table does not match column list");
		sqlStateCodes.put("22000", "Data exception");
		sqlStateCodes.put("22001", "String data, right truncation");
		sqlStateCodes.put("22003", "Numeric value out of range");
		sqlStateCodes.put("22007", "Invalid datetime format");
		sqlStateCodes.put("22012", "Division by zero");
		sqlStateCodes.put("22018", "Error in assignment");
		sqlStateCodes.put("22026", "String data, length mismatch");
		sqlStateCodes.put("23000", "Integrity constraint violation");
		sqlStateCodes.put("25000", "Invalid transaction state");
		sqlStateCodes.put("25S02", "Transaction is still active");
		sqlStateCodes.put("25S03", "Transaction has been rolled back");
		sqlStateCodes.put("26000", "Invalid SQL statement identifier");
		sqlStateCodes.put("28000", "Invalid authorization specification");
		sqlStateCodes.put("34000", "Invalid cursor name");
		sqlStateCodes.put("3C000", "Duplicate cursor name");
		sqlStateCodes.put("40000", "Commit transaction resulted in rollback transaction");
		sqlStateCodes.put("40001", "Serialization failure, e.g. timeout or deadlock");
		sqlStateCodes.put("42000", "Syntax error or access rule violation");
		sqlStateCodes.put("42S01", "Base table or view already exists");
		sqlStateCodes.put("42S02", "Base table or view not found");
		sqlStateCodes.put("42S11", "Index already exists");
		sqlStateCodes.put("42S12", "Index not found");
		sqlStateCodes.put("42S21", "Column already exists");
		sqlStateCodes.put("42S22", "Column not found");
		sqlStateCodes.put("42S23", "No default for column");
		sqlStateCodes.put("44000", "WITH CHECK OPTION violation");
		sqlStateCodes.put("HY000", "General error");
		sqlStateCodes.put("HY001", "Storage allocation failure");
		sqlStateCodes.put("HY002", "Invalid column number");
		sqlStateCodes.put("HY003", "Invalid application buffer type");
		sqlStateCodes.put("HY004", "Invalid SQL Data type");
		sqlStateCodes.put("HY008", "Operation cancelled");
		sqlStateCodes.put("HY009", "Invalid use of null pointer");
		sqlStateCodes.put("HY010", "Function sequence error");
		sqlStateCodes.put("HY011", "Operation invalid at this time");
		sqlStateCodes.put("HY012", "Invalid transaction operation code");
		sqlStateCodes.put("HY015", "No cursor name avilable");
		sqlStateCodes.put("HY018", "Server declined cancel request");
		sqlStateCodes.put("HY090", "Invalid string or buffer length");
		sqlStateCodes.put("HY091", "Descriptor type out of range");
		sqlStateCodes.put("HY092", "Attribute or Option type out of range");
		sqlStateCodes.put("HY093", "Invalid parameter number");
		sqlStateCodes.put("HY095", "Function type out of range");
		sqlStateCodes.put("HY096", "Information type out of range");
		sqlStateCodes.put("HY097", "Column type out of range");
		sqlStateCodes.put("HY098", "Scope type out of range");
		sqlStateCodes.put("HY099", "Nullable type out of range");
		sqlStateCodes.put("HY100", "Uniqueness option type out of range");
		sqlStateCodes.put("HY101", "Accuracy option type out of range");
		sqlStateCodes.put("HY103", "Direction option out of range");
		sqlStateCodes.put("HY104", "Invalid precision or scale value");
		sqlStateCodes.put("HY105", "Invalid parameter type");
		sqlStateCodes.put("HY106", "Fetch type out of range");
		sqlStateCodes.put("HY107", "Row value out of range");
		sqlStateCodes.put("HY108", "Concurrency option out of range");
		sqlStateCodes.put("HY109", "Invalid cursor position");
		sqlStateCodes.put("HY110", "Invalid driver completion");
		sqlStateCodes.put("HY111", "Invalid bookmark value");
		sqlStateCodes.put("HYC00", "Driver not capable");
		sqlStateCodes.put("HYT00", "Timeout expired");
		sqlStateCodes.put("HYT01", "Connection timeout expired");
		sqlStateCodes.put("HZ010", "RDA error: Access control violation");
		sqlStateCodes.put("HZ020", "RDA error: Bad repetition count");
		sqlStateCodes.put("HZ080", "RDA error: Resource not available");
		sqlStateCodes.put("HZ090", "RDA error: Resource already open");
		sqlStateCodes.put("HZ100", "RDA error: Resource unknown");
		sqlStateCodes.put("HZ380", "RDA error: SQL usage violation");
		sqlStateCodes.put("IM001", "Driver does not support this function");
		sqlStateCodes.put("IM002", "Data source name not found and no default driver specified");
		sqlStateCodes.put("IM003", "Specified driver could not be loaded");
		sqlStateCodes.put("IM004", "Driver's AllocEnv failedDriver's AllocEnv failed");
		sqlStateCodes.put("IM005", "Driver's AllocConnect failed");
		sqlStateCodes.put("IM006", "Driver's SetConnectOption failed");
		sqlStateCodes.put("IM007", "No data source or driver specified, dialog prohibited");
		sqlStateCodes.put("IM008", "Dialog failed");
		sqlStateCodes.put("IM009", "Unable to load translation DLL");
		sqlStateCodes.put("IM010", "Data source name too long");
		sqlStateCodes.put("IM011", "Driver name too long");
		sqlStateCodes.put("IM012", "DRIVER keyword syntax error");
		sqlStateCodes.put("IM013", "Trace file error");
		sqlStateCodes.put("S1000", "Can not write to the database.");
	}
	
	public abstract Connection getConnection() throws SQLException;	
	public abstract void releaseConnection(Connection conn);
	
	public static String translateSqlState(String code)
	{
		return sqlStateCodes.get(code);
	}
}
