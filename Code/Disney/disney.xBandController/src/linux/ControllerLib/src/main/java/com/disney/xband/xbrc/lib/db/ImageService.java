package com.disney.xband.xbrc.lib.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedHashMap;

import com.disney.xband.xbrc.lib.entity.Image;
import com.disney.xband.xbrc.lib.entity.ImageBlob;

public class ImageService
{
	public static Collection<Image> getAllImages(Connection conn) throws Exception 
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from Image order by id");
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			LinkedHashMap<Long,Image> images = new LinkedHashMap<Long,Image>(); 
			
			while (rs.next() == true)
			{
				Image image = instantiateImage(rs);
				images.put(image.getId(), image);
			}
			
			rs.close();
			ps.close();
			
			ps = conn.prepareStatement("select * from ImageBlob");
			ps.clearParameters();
			ps.execute();
			rs = ps.getResultSet();
			
			while (rs.next() == true)
			{
				ImageBlob blob = instantiateImageBlob(rs);
				Image image = images.get(blob.getImageId());
				if (image != null)
					image.setBlob(blob);
			}
			
			return images.values();
		} 
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
				
				try
				{
					rs.close();
				}
				catch(SQLException e)
				{					
				}
			}
		}
	}
	
	public static Image find(Connection conn, long id) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from Image where id = ?");
			ps.clearParameters();
			ps.setLong(1, id);
			ps.execute();
			rs = ps.getResultSet();
			
			Image image = null;
			
			if (rs.next() == true)
				image = instantiateImage(rs);
			
			return image;
		} 
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
				
				try
				{
					rs.close();
				}
				catch(SQLException e)
				{					
				}
			}
		}
	}
	
	public static Image findByFilename(Connection conn, String filename) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from Image where filename = ?");
			ps.clearParameters();
			ps.setString(1, filename);
			ps.execute();
			rs = ps.getResultSet();
			
			Image image = null;
			
			if (rs.next() == true)
				image = instantiateImage(rs);
			
			return image;
		} 
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
				
				try
				{
					rs.close();
				}
				catch(SQLException e)
				{					
				}
			}
		}
	}
	
	public static ImageBlob findImageBlob(Connection conn, long id) throws Exception
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{	
			ps = conn.prepareStatement("select * from ImageBlob where imageId = ?");
			ps.clearParameters();
			ps.setLong(1, id);
			ps.execute();
			rs = ps.getResultSet();
			
			ImageBlob blob = null;
			
			if (rs.next() == true)
				blob = instantiateImageBlob(rs);
			
			return blob;
		} 
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
				
				try
				{
					rs.close();
				}
				catch(SQLException e)
				{					
				}
			}
		}
	}
	
	public static void update(Connection conn, Image img) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("update Image set description = ?,filename = ?,title = ?,height = ?, width = ? where id = ?");

			ps.setString(1, img.getDescription());
			ps.setString(2, img.getFilename());
			ps.setString(3, img.getTitle());
			ps.setInt(4, img.getHeight());
			ps.setInt(5, img.getWidth());
			ps.setLong(6, img.getId());
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	
	public static void updateImageBlob(Connection conn, ImageBlob img) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("update ImageBlob byes = ? where imageId = ?");

			ps.setBytes(1, img.getBytes());
			ps.setLong(2, img.getImageId());
			
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	
	public static void delete(Connection conn, Long id) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("delete from Image where id = ?");
			ps.setLong(1, id);
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	
	public static void deleteImageBlob(Connection conn, Long id) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("delete from ImageBlob where imageid = ?");
			ps.setLong(1, id);
			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	
	public static void save(Connection conn, Image img) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("insert into Image (description,filename,title,height,width) values (?,?,?,?,?)",
									   Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, img.getDescription());
			ps.setString(2, img.getFilename());
			ps.setString(3, img.getTitle());
			ps.setInt(4, img.getHeight());
			ps.setInt(5, img.getWidth());
			ps.executeUpdate();
			
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				img.setId(rs.getLong(1));
			}
			rs.close();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}
	
	public static void saveImageBlob(Connection conn, ImageBlob blob) throws SQLException
	{
		PreparedStatement ps = null;

		try
		{
			ps = conn.prepareStatement("insert into ImageBlob (imageId, bytes) values (?,?)");

			ps.setLong(1, blob.getImageId());
			ps.setBytes(2, blob.getBytes());

			ps.executeUpdate();
		}
		finally
		{
			if (ps != null)
			{
				try
				{
					ps.close();
				}
				catch (SQLException e)
				{
				}
			}
		}
	}

	public static Image instantiateImage(ResultSet rs) throws Exception
	{
		Image img = new Image();

		img.setId(rs.getLong("id"));
		img.setDescription(rs.getString("description"));
		img.setFilename(rs.getString("filename"));
		img.setHeight(rs.getInt("height"));
		img.setTitle(rs.getString("title"));
		img.setWidth(rs.getInt("width"));

		return img;
	}
	
	public static ImageBlob instantiateImageBlob(ResultSet rs) throws Exception
	{
		ImageBlob blob = new ImageBlob();

		blob.setImageId(rs.getLong("imageId"));
		blob.setBytes(rs.getBytes("bytes"));

		return blob;
	}
}
