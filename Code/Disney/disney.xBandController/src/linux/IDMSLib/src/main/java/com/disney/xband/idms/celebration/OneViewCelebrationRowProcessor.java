package com.disney.xband.idms.celebration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.BasicRowProcessor;

import com.disney.xband.idms.lib.model.Link;
import com.disney.xband.idms.lib.model.oneview.Celebration;

public class OneViewCelebrationRowProcessor extends BasicRowProcessor 
{

	public static OneViewCelebrationRowProcessor INSTANCE = new OneViewCelebrationRowProcessor();

	private OneViewCelebrationRowProcessor()
	{
		super();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Celebration> toBeanList(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		List<Celebration> celebrations = 
				new ArrayList<Celebration>();

		while (rs.next())
		{
			Celebration c = (Celebration)this.toBean(rs, type);
			celebrations.add(c);
		}

		return celebrations;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Celebration toBean(ResultSet rs, 
			@SuppressWarnings("rawtypes") Class type) throws SQLException
	{
		Celebration celebration = new Celebration();
		//TODO: Fix for FPT2, name will be something else long term, but this is 
		//what GxP displays and not the milestone.
		//celebration.setName(rs.getString("name"));
		celebration.setName(rs.getString("milestone"));
		celebration.setMilestone(rs.getString("milestone"));
		celebration.setType(rs.getString("type"));
		celebration.setMonth(rs.getString("month"));
		celebration.setDay(rs.getString("day"));
		celebration.setYear(rs.getString("year"));
		celebration.setStartDate(rs.getDate("startDate"));
		celebration.setEndDate(rs.getDate("endDate"));
		celebration.setRecognitionDate(rs.getDate("recognitionDate"));
		celebration.setSurpriseIndicator(rs.getBoolean("surpriseIndicator"));
		celebration.setComment(rs.getString("comment"));
		
		Link self = new Link();
		Link reservation = new Link();

		self.setHref("/celebration/" + rs.getLong("celebrationId"));
		self.setName("self");

		reservation.setHref("/resort-reservation/pid001");
		reservation.setName("reservation");

		celebration.getLinks().setSelf(self);
		celebration.getLinks().setReservation(reservation);

		return celebration;

	}
}
