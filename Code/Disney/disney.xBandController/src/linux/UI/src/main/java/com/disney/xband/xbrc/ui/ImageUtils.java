package com.disney.xband.xbrc.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;

import com.disney.xband.common.lib.security.InputValidator;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.disney.xband.xbrc.lib.db.ImageService;
import com.disney.xband.xbrc.lib.entity.Image;
import com.disney.xband.xbrc.lib.entity.ImageBlob;
import com.disney.xband.xbrc.ui.db.Data;

public class ImageUtils
{
	private static Logger logger = Logger.getLogger(ImageUtils.class);
	
	private static class SingletonHolder { 
		public static final ImageUtils instance = new ImageUtils();
	}
	
	public static ImageUtils getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private ImageUtils()
	{
		// Do some once-only initialization..
		String userImagesPath = getRealPath(getUserImagesUrl());
		createDirectory(userImagesPath);
	}
	
	public static String getUserImagesUrl() 
	{
		return "userimages";
	}
	
	public static String getImageUrl(Image img)
	{
		return  getUserImagesUrl() + "/" + img.getFilename();
	}
	
	public static String getRealPath(String resourcePath) 
	{		
		try
		{
			return ServletActionContext.getServletContext().getRealPath(resourcePath);
		} catch (IllegalArgumentException e) {
			logger.error("Failed to get real path from resource path: " + resourcePath, e);
		}		
		return "";
	}
	
	public static String getExtensionFromContentType(String contentType)
	{
		int i = contentType.lastIndexOf('/');
		if (i > 0) {
			return contentType.substring(i+1);
		}
		return "tmp";
	}
	
	public void deleteImageFile(Image img)
	{
		String userImagesPath = getRealPath("userimages");
		File imgFile = new File(userImagesPath + "/" + InputValidator.validateFileName(img.getFilename()));
		if (imgFile.exists())
		{
			imgFile.delete();
		}
	}
	
	public void deleteImageFromDatabase(Image img)
	{
		Connection conn = null;
		
		try
		{
			conn = Data.GetConnection();
			ImageService.deleteImageBlob(conn, img.getId());
			ImageService.delete(conn, img.getId());
		}
		catch(Exception e)
		{
			logger.error("Failed to delte image with id " + img.getId() + " from the database", e);
		}
		finally
		{
			if (conn != null)
				Data.ReleaseConnection(conn);
		}
	}
	
	public static String getImagePath(String filename)
	{
		String userImagesPath = getRealPath("userimages");
		return userImagesPath + "/" + filename;
	}
	
	public static void createDirectory(String path)
	{
		File dir = new File(path);
		if (!dir.isDirectory())
			dir.mkdir();
	}
	
	public void saveImage(File temp, Image img, String contentType) throws Exception
	{
		String userImagesPath = getRealPath("userimages");
		int i = temp.getName().lastIndexOf('.');
		if (i <= 0)
			throw new Exception("Image file " + temp.getName() + " does not have an file extension.");
		
		String ext = "." + getExtensionFromContentType(contentType);
		File imgFile = File.createTempFile("IMG", ext, new File(InputValidator.validateFilePath(userImagesPath)));
		imgFile.delete();
		temp.renameTo(imgFile);
		img.setFilename(imgFile.getName());
		
		Connection conn = null;
		try
		{			
			conn = Data.GetConnection();
						
			ImageService.save(conn, img);
			
			ImageBlob blob = new ImageBlob();
			blob.setBytes(readFile(imgFile));
			blob.setImageId(img.getId());
			ImageService.saveImageBlob(conn, blob);
			img.setBlob(blob);
		}
		catch(Exception e)
		{
			imgFile.delete();
		}
		finally
		{
			Data.ReleaseConnection(conn);
		}
	}
	
	public Image ensureImageFileExists(String filename)
	{
		Connection conn = null;
		try
		{
			conn = Data.GetConnection();
			Image img = ImageService.findByFilename(conn, filename);
			String path = getImagePath(InputValidator.validateFileName(img.getFilename()));
			File file = new File(path);
			if (!file.exists())
			{
				ImageBlob blob = ImageService.findImageBlob(conn, img.getId());
				if (blob == null)
					throw new Exception("Missing ImageBlob record for Image with id " + img.getId());
				writeFile(blob.getBytes(), path);
			}
			return img;
		}
		catch (Exception e)
		{
			logger.error("Failed to load image with filename " + filename + " from the database", e);
			return null;
		}
		finally 
		{
			Data.ReleaseConnection(conn);
		}
	}
	
	public static byte[] readFile(File file) throws IOException
	{
		FileInputStream is = null;

        try {
		    is = new FileInputStream(file);
		    byte[] buffer = new byte[(int)file.length()];
		    int totalRead = is.read(buffer, 0, buffer.length);
		    while (totalRead < buffer.length)
		    {
			    totalRead += is.read(buffer, totalRead, buffer.length-totalRead);
		    }

            return buffer;
        }
        finally {
            if (is != null) {
                try {
                    is.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
	
	public static void writeFile(byte[] bytes, String filePath) throws IOException
	{
		File file = new File(filePath); // The calling method validates the file path.
		FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
		    os.write(bytes);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (Exception ignore) {
                }
            }
        }
	}
}
