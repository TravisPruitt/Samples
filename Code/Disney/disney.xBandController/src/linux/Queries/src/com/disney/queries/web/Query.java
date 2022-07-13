package com.disney.queries.web;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.disney.queries.managers.ResourceManager;


@Path("/query")
public class Query {
	private static Logger logger = Logger.getLogger(Query.class);
	
	@GET
	@Path("/{query}/{startDate}/{endDate}")
	@Produces(MediaType.TEXT_HTML  + ";charset=utf-8")
	public String getQueryWithDates(@PathParam("query")String queryName, @PathParam("startDate") Date startDate, @PathParam("endDate") Date endDate)
	{
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement ps = null;
		String result = "";
		
		queryName = cleanQuery(queryName);
		
		try
		{
			conn = ResourceManager.getConnection();
			ps = conn.prepareStatement(queryName);
			

			if (startDate != null)	
			{
				ps.setDate(1, startDate);
			}
			
			if (endDate != null)
			{
				ps.setDate(2, endDate);
			}

			rs = ps.executeQuery();
			
			result = getResultString(rs);
			
			while ( ps.getMoreResults())
			{
				rs = ps.getResultSet();
				result += getResultString(rs);
			}
			
		}
		catch(Exception ex)
		{
			logger.error(ex.getMessage());
			System.out.println(ex.getMessage());
			throw new WebApplicationException(500);
		}
		finally
		{
            if(rs != null) {
			    ResourceManager.close(rs);
            }

            if(ps != null) {
                ResourceManager.close(ps);
            }

            if(conn != null) {
                ResourceManager.close(conn);
            }
		}
		
		return result;
		
	}
	
	/*
	 * Replace the query keywords, creating a safe query?
	 */
	private String cleanQuery(String query)
	{
		String result = "";
		result = query.toLowerCase().replaceAll("delete", "'delete'");
		result = result.toLowerCase().replaceAll("update", "'update'");
		result = result.toLowerCase().replaceAll("drop", "'drop'");
		result = result.toLowerCase().replaceAll("exec", "'exec'");
		
		return result;
	}
	
	@GET
	@Path("/{query}")
	@Produces(MediaType.TEXT_HTML  + ";charset=utf-8")
	public String getQuery(@PathParam("query")String queryName)
	{
		String result = "";
		try
		{
			result = getQueryWithDates(queryName, null, null);
		}
		catch (Exception ex)
		{
			throw new WebApplicationException(500);
		}
		
		return result;
		
	}
	
	@GET
	@Path("/{query}/{startDate}")
	@Produces(MediaType.TEXT_HTML  + ";charset=utf-8")
	public String getQueryStartDate(@PathParam("query")String queryName, @PathParam("startDate") Date startDate)
	{
		String result = "";
		
		try
		{
			result = getQueryWithDates(queryName, startDate, null);
		}
		catch (Exception ex)
		{
			throw new WebApplicationException(500);
		}
		
		return result;
		
	}
	
	protected String getResultString(ResultSet rs)
	{
		String result = "";
		
		try
		{
			ResultSetMetaData rsmd = rs.getMetaData();
			int colCount = rsmd.getColumnCount();
			
			result += "<table class='resultTable'>";
			result += "<tr class='resultHeaderRow'>";
			for (int i = 1; i <= colCount; i++)
			{
				result += "<th class='resultHeaderCell'>";
				result += "<span class='headerLabel'>" + rsmd.getColumnName(i) + "</span>";
				result += "</th>\n";
			}
			result += "</tr>";
			
			boolean even = false;
			
			while (rs.next())
			{
				if (!even)
				{
					result += "<tr class='resultRowOdd'>";
					even = true;
				}
				else
				{
					result += "<tr class='resultRowEven'>";
					even = false;
				}
				
				
				
				for (int i = 1; i <= colCount; i++)
				{
					result += "<td class='resultCell'>";
					String val = "";
					switch(rsmd.getColumnType(i))
					{
						case java.sql.Types.BIGINT:
							val += rs.getInt(i);
						break;
						
						case java.sql.Types.BIT:
							boolean re = rs.getBoolean(i);
							if (re)
							{
								val += "true";
							}
							else
							{
								val += "false";
							}
						break;
						
						case java.sql.Types.VARCHAR:
							val += rs.getString(i);
							break;
						
						case java.sql.Types.NVARCHAR:
							val += rs.getString(i);
							break;
						
						case java.sql.Types.INTEGER:
							val += rs.getInt(i);
							break;
						
						case java.sql.Types.DATE:
							val += rs.getDate(i);
							break;
						
						case java.sql.Types.TIMESTAMP:
							val += rs.getTimestamp(i);
							break;
							
						case java.sql.Types.FLOAT:
							val += rs.getFloat(i);
						
						default:
							System.out.println("Column type not found: " + rsmd.getColumnName(i) + " : " + rsmd.getColumnTypeName(i));
							val += rs.getObject(i);
							break;
					}
					
					if (val.equals("null"))
					{
						val = "";
					}
					
					result += "<span class='cellLabel'>" + val + "</span>";
					result += "</td>\n";
				}
				result += "</tr>\n";
			}
			
			result += "</table>\n";

		}
		catch (Exception ex)
		{
			System.out.println("Error:  " + ex.getMessage());
			// Keep going.
		}
		
		return result;
	}
	
}
