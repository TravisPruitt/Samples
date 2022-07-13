package com.disney.xband.xbrc.ui.edit.action;

import java.io.File;
import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xbrc.lib.db.ImageService;
import com.disney.xband.xbrc.lib.entity.AttractionType;
import com.disney.xband.xbrc.lib.entity.Image;
import com.disney.xband.xbrc.lib.entity.LocationType;
import com.disney.xband.xbrc.lib.model.XbrcPublicModel;
import com.disney.xband.xbrc.ui.AttractionViewConfig;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.ImageUtils;
import com.disney.xband.xbrc.ui.action.BaseAction;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.db.Data;

public class AttractionEditAction extends BaseAction {

	private List<ToolboxItemLocationDisplay> toolboxLocations;
	private List<ToolboxItemDisplay> toolboxItems;
	private Image backgroundImage = null;
	
	private File image;
	private String imageContentType;
	private String imageFileName;
	private boolean removeImage = false;
	
	private AttractionViewConfig avConfig;
	
	@Override
	public String execute() throws Exception {		
		
		/* Build a list of toolbox items */
		toolboxLocations = new LinkedList<ToolboxItemLocationDisplay>();
		Map<LocationType,Integer> locationTypes = XbrcPublicModel.getInstance().GetAllowedLocationTypes(AttractionType.Ride);
		Iterator<Map.Entry<LocationType,Integer>> it = locationTypes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<LocationType,Integer> pairs = it.next();
	        toolboxLocations.add(new ToolboxItemLocationDisplay(pairs.getKey()));
	    }
	    
	    toolboxItems = new LinkedList<ToolboxItemDisplay>();
	    for (GridItem.ItemType itemType : GridItem.ItemType.values()) {
	    	/* All gates are in loolboxLocations */
	    	if (itemType != GridItem.ItemType.Gate) {
	    		toolboxItems.add(new ToolboxItemDisplay(itemType, false));
	    		toolboxItems.add(new ToolboxItemDisplay(itemType, true));
	    	}
	    }	    
	    
	    if (image != null || removeImage)
	    	processImage(image, imageContentType, imageFileName, removeImage);	    
	    else if (!UIProperties.getInstance().getUiConfig().getAttractionViewImageFilename().isEmpty())
	    	backgroundImage = ImageUtils.getInstance().ensureImageFileExists(UIProperties.getInstance().getUiConfig().getAttractionViewImageFilename());
		
	    avConfig = UIProperties.getInstance().getAttractionViewConfig();
	    
		return super.execute();
	}
	
	public List<ToolboxItemLocationDisplay>getToolboxLocations() {		
	    return toolboxLocations;
	}
	
	public List<ToolboxItemDisplay>getToolboxItems() {		
	    return toolboxItems;
	}
	
	public boolean getShowSubwayMap() {
		return UIProperties.getInstance().getUiConfig().isShowSubwayMap();
	}
	
	public boolean getHasBackground() {
		return backgroundImage != null;
	}
	
	public String getBackgroundImageUrl()
	{
		return ImageUtils.getImageUrl(backgroundImage); 
	}
	
	class ToolboxItemLocationDisplay {
		LocationType locationType;		
		String description;
		
		public ToolboxItemLocationDisplay(LocationType locationType) {
			this.locationType = locationType;
		}
		
		/* TODO: this mapping could live in a DB table */
		public String getImage() {
			switch(locationType) {
			case xPassEntry:
				return "GateGreen.png";
			case Queue:
				return "GateRed.png";
			case Load:
				return "GateYellow.png";
			}
			return "Gate.png";
		}
		
		public String getLabel() {
			if (locationType == LocationType.xPassEntry)
				return "FasPass Entry";
			
			return locationType.toString();
		}
		
		public LocationType getLocationType() {
			return locationType;
		}
		
		public String getItemTypeName() {
			return GridItem.ItemType.Gate.toString();
		}
	}
	
	class ToolboxItemDisplay {
		GridItem.ItemType itemType;
		boolean xpass;
		
		public ToolboxItemDisplay(GridItem.ItemType itemType, boolean xpass) {
			this.itemType = itemType;
			this.xpass = xpass;
		}
		
		/* TODO: this mapping could live in a DB table */
		public String getImage() {
			if (xpass)
				return itemType.toString() + ".png";
			else
				return itemType.toString() + "-xpass.png";
		}
		
		public String getItemTypeName() {
			return this.itemType.toString();
		}
	}
	
	
	//
	// IMAGE PROCESSING
	//
	
	private void processImage(File imageFile, String imageContentType, String imageFileName, boolean removeImage)
	{
		if (imageFile != null || removeImage)
		{
			//
			// Remove existing image if one exists
			//
			
			String filename = UIProperties.getInstance().getUiConfig().getAttractionViewImageFilename();
			if (!filename.isEmpty())
			{
				Connection conn = null;
				try
				{
					conn = Data.GetConnection();
					Image img = ImageService.findByFilename(conn, filename);
					if (img != null)
					{
						// Delete the image from the disk
						ImageUtils.getInstance().deleteImageFile(img);
						// Delete the image from the database
						ImageUtils.getInstance().deleteImageFromDatabase(img);
						// Update the config value
						UIProperties.getInstance().getUiConfig().setAttractionViewImageFilename("");
						UIProperties.getInstance().saveUIConfig();
						backgroundImage = null;
					}
				}
				catch (Exception e)
				{
					LOG.error("Failed to read Image record with filename = " + filename + " from the database", e);
				}
				finally
				{
					Data.ReleaseConnection(conn);
				}
			}
		}
		
		if (imageFile != null)
		{
			//
			// Create new image if one was uploaded
			//
			
			try
			{
				
				Image img = new Image();
				img.setDescription("Facility view background image");
				img.setTitle("Background image");
				img.setHeight(0);
				img.setWidth(0);
				ImageUtils.getInstance().saveImage(imageFile, img, getImageContentType());
				UIProperties.getInstance().getUiConfig().setAttractionViewImageFilename(img.getFilename());
				UIProperties.getInstance().saveUIConfig();
				backgroundImage = img;
			}
			catch (Exception e)
			{
				LOG.error("Failed to create image file", e);
				this.addActionError("Server failed to process the image file: " + e.getLocalizedMessage());				
			}
			
			imageFile.delete();
		}
	}
	
	public File getImage()
	{
		return image;
	}
	public void setImage(File image)
	{
		this.image = image;
	}
	public String getImageContentType()
	{
		return imageContentType;
	}
	public void setImageContentType(String imageContentType)
	{
		this.imageContentType = imageContentType;
	}
	public String getImageFileName()
	{
		return imageFileName;
	}
	public void setImageFileName(String imageFileName)
	{
		this.imageFileName = imageFileName;
	}

	public boolean isRemoveImage()
	{
		return removeImage;
	}

	public void setRemoveImage(boolean removeImage)
	{
		this.removeImage = removeImage;
	}

	public AttractionViewConfig getAvConfig()
	{
		return avConfig;
	}
}
