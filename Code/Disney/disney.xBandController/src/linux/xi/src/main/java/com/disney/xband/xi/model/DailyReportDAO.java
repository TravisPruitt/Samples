package com.disney.xband.xi.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DailyReportDAO extends DAO
{
	public DailyReportDAO()
	{
		super();
	}

	public String setReportData(int guestCount, int guestCountTarget,
			int recruitedCount, String reportDate)
	{
		Connection connection = null;
		ReturnMessage rmsg = new ReturnMessage();
		CallableStatement statement = null;
		ResultSet rs = null;
        int selectedEntitlements=0;

		try
		{
			String sQuery = "{call dbo.usp_GetSelectedForDate(?,?)}";
			connection = this.getConnection();
			String s[] = DateUtil.strDayToStrTimestamp(reportDate);
			statement = connection.prepareCall(sQuery);
			statement.setString(1, s[0]);
			statement.setString(2, s[1]);
			rs = statement.executeQuery();

			if (rs.next())
			{
				selectedEntitlements = rs.getInt(1);
			}		
        }
        catch (SQLException sqle)
        {
            return this.errorMessage(sqle.getMessage(), "set dailyreport");
        }
        catch (DAOException dao)
        {
            return this.errorMessage(dao.getMessage(), "set dailyreport");
        }
        finally
        {
            if (rs != null)
                try
                {
                    rs.close();
                }
                catch (SQLException sqle)
                {
                }
            if (statement != null)
                try
                {
                    statement.close();
                }
                catch (SQLException sqle)
                {
                }
        }

        statement = null;
        rs = null;

        try {
			String sQuery = "{call dbo.usp_SetDailyReport(?,?,?,?,?)}";
			statement = connection.prepareCall(sQuery);
			statement.setInt(1, guestCount);
			statement.setInt(2, guestCountTarget);
			statement.setInt(3, recruitedCount);
			statement.setInt(4, selectedEntitlements);
			statement.setString(5, reportDate);
			rs = statement.executeQuery();

			rs.next();
			rmsg.addData("createdReportId", rs.getInt(1));
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "set dailyreport");
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException sqle)
				{
				}
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException sqle)
				{
				}
			if (connection != null)
				try
				{
					connection.close();
				}
				catch (SQLException sqle)
				{
				}
		}
	}

	public String getReportData(String reportDate, String label)
	{
		ReturnMessage rmsg = new ReturnMessage();
		rmsg.addData("date", label);
		try
		{

			/*
			 * Timestamp ts = new Timestamp(System.currentTimeMillis()); int
			 * reportId, int guestCount, int guestCountTarget, int
			 * recruitedCount, int selectedEntitlements, String reportDate,
			 * Timestamp createdAt DailyReport todayDR = new DailyReport(1, 230,
			 * 300, 500, 720, "2012-04-12 01:02:23", ts); DailyReport ydayDR =
			 * new DailyReport(2, 332, 517, 823, 1020, "2012-04-13 8:12:23",
			 * ts); DailyReport todateDR = new DailyReport(3, 402, 489, 710,
			 * 1201, "2012-04-14 11:23:23", ts);
			 */

			// need to pull 3 report objects and load them into rmsg
			Date startDate = DateUtil.convertStringToSQLDate(reportDate);

			String sQuery = "{call dbo.usp_GetDailyReport(?)}";
			DailyReport today = getReport(reportDate, sQuery);
			rmsg.addData("day", today);

			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(startDate);

			cal.roll(Calendar.DAY_OF_MONTH, -1);
			DailyReport yesterday = getReport(
					DateUtil.dateformatter.format(cal.getTime()), sQuery);
			rmsg.addData("day before", yesterday);

			sQuery = "{call dbo.usp_GetDailyReportToDate(?)}";
			DailyReport todate = getReport(reportDate, sQuery);
			rmsg.addData("todate", todate);
			return "remoteData(" + gson.toJson(rmsg) + ");";
		}
		catch (SQLException sqle)
		{
			return this.errorMessage(sqle.getMessage(), "get dailyreport");
		}
		catch (DAOException dao)
		{
			return this.errorMessage(dao.getMessage(), "get dailyreport");
		}
	}

	private DailyReport getReport(String reportDate, String sQuery)
			throws SQLException, DAOException
	{

		Connection connection = null;
		CallableStatement statement = null;
		ResultSet rs = null;
		try
		{
			connection = this.getConnection();
			statement = connection.prepareCall(sQuery);
			statement.setString(1, reportDate);
			rs = statement.executeQuery();

			if (rs.next())
			{
				return new DailyReport(rs.getInt(1), rs.getInt(2),
						rs.getInt(3), rs.getInt(4), rs.getInt(5),
						rs.getString(6), rs.getTimestamp(7));
			}
			else
			{
				logger.debug("No data found for:" + reportDate);
				return new DailyReport(reportDate);
			}
		}
		finally
		{
			if (rs != null)
				try
				{
					rs.close();
				}
				catch (SQLException sqle)
				{
				}
			if (statement != null)
				try
				{
					statement.close();
				}
				catch (SQLException sqle)
				{
				}
			if (connection != null)
				try
				{
					connection.close();
				}
				catch (SQLException sqle)
				{
				}
		}

	}

	class DailyReport
	{
		private int reportId = 0;
		private int guestCount = 0;
		private int guestCountTarget = 0;
		private int recruitedCount = 0;
		private int selectedEntitlements = 0;
		private String reportDate = null;
		private Timestamp createdAt = null;

		private String reportStatus = "success";

		public DailyReport(String reportDate)
		{
			this.reportStatus = "no data";
			this.reportDate = reportDate;
		}

		public DailyReport(int reportId, int guestCount, int guestCountTarget,
				int recruitedCount, int selectedEntitlements,
				String reportDate, Timestamp createdAt)
		{
			this.reportId = reportId;
			this.guestCount = guestCount;
			this.guestCountTarget = guestCountTarget;
			this.recruitedCount = recruitedCount;
			this.selectedEntitlements = selectedEntitlements;
			this.reportDate = reportDate;
			this.createdAt = createdAt;
		}

		/**
		 * @return the dailyId
		 */
		public int getReportId()
		{
			return reportId;
		}

		/**
		 * @param reportId
		 *            the dailyId to set
		 */
		public void setReportId(int reportId)
		{
			this.reportId = reportId;
		}

		/**
		 * @return the guestCount
		 */
		public int getGuestCount()
		{
			return guestCount;
		}

		/**
		 * @param guestCount
		 *            the guestCount to set
		 */
		public void setGuestCount(int guestCount)
		{
			this.guestCount = guestCount;
		}

		/**
		 * @return the guestCountTarget
		 */
		public int getGuestCountTarget()
		{
			return guestCountTarget;
		}

		/**
		 * @param guestCountTarget
		 *            the guestCountTarget to set
		 */
		public void setGuestCountTarget(int guestCountTarget)
		{
			this.guestCountTarget = guestCountTarget;
		}

		/**
		 * @return the recruitedCount
		 */
		public int getRecruitedCount()
		{
			return recruitedCount;
		}

		/**
		 * @param recruitedCount
		 *            the recruitedCount to set
		 */
		public void setRecruitedCount(int recruitedCount)
		{
			this.recruitedCount = recruitedCount;
		}

		/**
		 * @return the selectedEntitlements
		 */
		public int getSelectedEntitlements()
		{
			return selectedEntitlements;
		}

		/**
		 * @param selectedEntitlements
		 *            the selectedEntitlements to set
		 */
		public void setSelectedEntitlements(int selectedEntitlements)
		{
			this.selectedEntitlements = selectedEntitlements;
		}

		/**
		 * @return the reportDate
		 */
		public String getReportDate()
		{
			return reportDate;
		}

		/**
		 * @param reportDate
		 *            the reportDate to set
		 */
		public void setReportDate(String reportDate)
		{
			this.reportDate = reportDate;
		}

		/**
		 * @return the createdAt
		 */
		public Timestamp getCreatedAt()
		{
			return createdAt;
		}

		/**
		 * @param createdAt
		 *            the createdAt to set
		 */
		public void setCreatedAt(Timestamp createdAt)
		{
			this.createdAt = createdAt;
		}

		public String getReportStatus()
		{
			return reportStatus;
		}

		public void setReportStatus(String reportStatus)
		{
			this.reportStatus = reportStatus;
		}
	}
}
