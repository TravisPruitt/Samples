package com.disney.xband.xbrc.ui.action;

import java.sql.Connection;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xbrc.lib.db.ReaderService;
import com.disney.xband.xbrc.lib.entity.Reader;
import com.disney.xband.xbrc.lib.entity.ReaderType;
import com.disney.xband.xbrc.lib.entity.Wall;
import com.disney.xband.xbrc.ui.db.Data;
import com.disney.xband.xbrc.ui.db.UIConnectionPool;

public class ShowQueueAction extends BaseAction {
	
	/* horizontal scale factor */
	public static final Double hMult = 960.0 / 200.0;	
	/* vertical scale factor */
	public static final Double vMult = 200.0 / 20.0;
	/* horizontal offset from the left */
	public static final Integer hOffset = 40;
	/* vertical offset from the top */
	public static final Integer vOffset = 90;
	
	class WallDisplay
	{
		private Wall wall;
		private Integer x0,y0,x1,y1;
		
		public WallDisplay(Wall wall, Double hMult, Double vMult, Integer vOffset, Integer hOffset) {
			this.wall = wall;
			this.x0 = (int)(wall.getX0() * hMult + hOffset);
			this.y0 = (int)(wall.getY0() * vMult + vOffset);
			this.x1 = (int)(wall.getX1() * hMult + hOffset);
			this.y1 = (int)(wall.getY1() * vMult + vOffset);
		}

		public Integer getX0() {
			return x0;
		}

		public Integer getY0() {
			return y0;
		}

		public Integer getX1() {
			return x1;
		}

		public Integer getY1() {
			return y1;
		}
	}
	
	public class ReaderDisplay {
		Reader reader;
		Integer topPos;
		Integer leftPos;
		String image;
		
		public ReaderDisplay(Reader reader, Double hMult, Double vMult, Integer vOffset, Integer hOffset) {
			this.reader = reader;
			this.leftPos = (int)(reader.getX() * hMult) + hOffset;
			this.topPos = (int)(reader.getY() * vMult) + vOffset;
			if (ReaderType.isTapReader(reader.getType()))
				this.image = "tap_reader.png";
			else
				this.image = "reader.png";
		}
			
		public Reader getReader() {
			return reader;
		}

		public Integer getTopPos() {
			return topPos;
		}		

		public Integer getLeftPos() {
			return leftPos;
		}
		
		public String getImage() {
			return image;
		}
	}
	
	private List<WallDisplay> wallList;
	private List<ReaderDisplay> readerList;
	
	@Override
	public String execute() throws Exception {
		
		List<Wall> walls = Data.GetWalls();
		wallList = new LinkedList<WallDisplay>();
		for (Wall wall : walls) {
			wallList.add(new WallDisplay(wall, hMult, vMult, vOffset, hOffset));
		}
		
		Collection<Reader> readers = null;
		
		Connection conn = UIConnectionPool.getInstance().getConnection();
		try
		{
			readers = ReaderService.findAll(conn);
		}
		catch (Exception e)
		{
			this.addActionError("Failed to retrieve a list of readers.");
		}
		finally
		{
			UIConnectionPool.getInstance().releaseConnection(conn);
		}
		
		readerList = new LinkedList<ReaderDisplay>();

        if(readers != null) {
		    for (Reader reader : readers) {
			    if (reader.getX() != null && reader.getY() != null)
				    readerList.add(new ReaderDisplay(reader, hMult, vMult, vOffset, hOffset));
		    }
        }
		
		return super.execute();
	}
	
	public List<WallDisplay> getWallList() {
		return wallList;
	}

	public List<ReaderDisplay> getReaderList() {
		return readerList;
	}
}
