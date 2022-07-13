package com.disney.xband.xbrc.ui.action;

import java.util.LinkedList;
import java.util.List;

import org.softwareforge.struts2.breadcrumb.BreadCrumb;

import com.disney.xband.xbrc.lib.entity.Image;
import com.disney.xband.xbrc.ui.AttractionViewConfig;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.ImageUtils;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.db.Data;
import com.opensymphony.xwork2.ActionSupport;

public class AttractionViewAction extends BaseAction {
	
	// Grid cell size in pixels.	
	
	private List<GridItemDisplay> gridItems;
	private Image backgroundImage = null;
	
	private AttractionViewConfig avConfig;
	
	@Override
	public String execute() throws Exception {		
		
		/* TODO: uncomment the following to enable caching for speed improvement */
		List<GridItem> gridItemList = /*AttractionViewGrid.getInstance().getGridItemList()*/Data.GetGridItems();
		gridItems = new LinkedList<GridItemDisplay>();
		for (GridItem gridItem : gridItemList) {
			gridItems.add(new GridItemDisplay(gridItem));
		}
		
		if (!UIProperties.getInstance().getUiConfig().getAttractionViewImageFilename().isEmpty())
	    	backgroundImage = ImageUtils.getInstance().ensureImageFileExists(UIProperties.getInstance().getUiConfig().getAttractionViewImageFilename());
		
		avConfig = UIProperties.getInstance().getAttractionViewConfig();
		
		return super.execute();
	}
	
	public List<GridItemDisplay> getGridItems() {
		return gridItems;
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
	
	public AttractionViewConfig getAvConfig()
	{
		return avConfig;
	}

	class GridItemDisplay {
		
		private GridItem gridItem;
		private Integer topPos;
		private Integer leftPos;
		
		public GridItemDisplay(GridItem gridItem)  {
			this.gridItem = gridItem;
			this.topPos = gridItem.getyGrid() * UIProperties.getInstance().getAttractionViewConfig().getGridSize();
			this.leftPos = gridItem.getxGrid() * UIProperties.getInstance().getAttractionViewConfig().getGridSize();
		}

		public GridItem getGridItem() {
			return gridItem;
		}

		public Integer getTopPos() {
			return topPos;
		}

		public Integer getLeftPos() {
			return leftPos;
		}
		
		public String getImage() {
			if (gridItem.getImage() != null && !gridItem.getImage().isEmpty())
				return gridItem.getImage();
			
			return gridItem.getItemType().toString() + ".png";
		}
	}

}
