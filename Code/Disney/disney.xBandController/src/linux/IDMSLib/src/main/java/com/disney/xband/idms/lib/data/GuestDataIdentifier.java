package com.disney.xband.idms.lib.data;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class GuestDataIdentifier {
	private String type;
	private String value;
	
	public GuestDataIdentifier(String type, String value)
	{
		this.type = type;
		this.value = value;
	}
	
	public GuestDataIdentifier(ResultSet rs) throws SQLException
	{
		type = rs.getString("type");
		value = rs.getString("value");
	}
	
	public String getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
}
