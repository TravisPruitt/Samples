package com.disney.xband.xbrc.ui.edit.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.disney.xband.xbrc.lib.entity.GuestStatusState;
import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xbrc.ui.AttractionViewConfig;
import com.disney.xband.xbrc.ui.ServiceLocator;
import com.disney.xband.xbrc.ui.UIConfig;
import com.disney.xband.xbrc.ui.UIProperties;
import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.bean.GridItem.ItemType;
import com.disney.xband.xbrc.ui.bean.GridItem.XpassOnlyState;
import com.disney.xband.xbrc.ui.db.Data;
import com.disney.xband.xbrc.ui.db.GridItemService;
import com.disney.xband.xbrc.ui.db.LocationService;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

public class AttractionEditAjaxAction extends ActionSupport implements Preparable {	
	
	private static Logger logger = Logger.getLogger(AttractionEditAjaxAction.class.getName());
	private final AttractionViewConfig avConfig = UIProperties.getInstance().getAttractionViewConfig();		
	private List<GridItemDisplay> gridItems;
	private List<EmptyCellDisplay> emptyCells;
	private Map<Integer, Boolean> usedCells;
	private GridItemService gridItemService;
	private LocationService locationService;
	private Map<Long,Location> locationMap;
	
	private String action;
	private String itemType;
	private String locationType;
	private String itemId;
	private String xgrid;
	private String ygrid;
	private String image;
	private boolean showSubwayMap = true;
	
	@Override
	public void prepare() throws Exception {
		gridItemService = ServiceLocator.getInstance().createService(GridItemService.class);
		locationService = ServiceLocator.getInstance().createService(LocationService.class);
	}
	
	@Override
	public String execute() throws Exception {
		
		if (action != null && !action.isEmpty()) {
			handleAttractionEdit();
		}
		
		locationMap = new HashMap<Long,Location>();
		Collection<Location> locations = locationService.findAll(true);
		for (Location l : locations)
			locationMap.put(l.getId(), l);
		
		List<GridItem> gridItemList = Data.GetGridItems();
		gridItems = new LinkedList<GridItemDisplay>();
		emptyCells = new LinkedList<EmptyCellDisplay>();
		usedCells = new HashMap<Integer, Boolean>();
				
		for (GridItem gridItem : gridItemList) {
			gridItems.add(new GridItemDisplay(gridItem));
			usedCells.put(gridItem.getxGrid() * avConfig.getGridWidth() + gridItem.getyGrid(), true);
		}
		
		for (int x = 0; x < avConfig.getGridWidth(); x++) {
			for (int y = 0; y < avConfig.getGridHeight(); y++) {
				if (!isCellUsed(x,y)) {
					emptyCells.add(new EmptyCellDisplay(x,y));
				}
			}
		}
		
		showSubwayMap = UIProperties.getInstance().getUiConfig().isShowSubwayMap();
		
		return super.execute();
	}
	
	public List<GridItemDisplay> getGridItems() {
		return gridItems;
	}
	
	public List<EmptyCellDisplay> getEmptyCells() {
		return emptyCells;
	}
	
	public boolean isCellUsed(Integer x, Integer y) {
		return usedCells.containsKey(x * avConfig.getGridWidth() + y);
	}
	
	class EmptyCellDisplay {
		private Integer topPos;
		private Integer leftPos;
		private Integer x;
		private Integer y;
		
		public EmptyCellDisplay(Integer x, Integer y) {
			this.topPos = y * avConfig.getGridSize();
			this.leftPos = x * avConfig.getGridSize();
			this.x = x;
			this.y = y;
		}

		public Integer getTopPos() {
			return topPos;
		}

		public void setTopPos(Integer topPos) {
			this.topPos = topPos;
		}

		public Integer getLeftPos() {
			return leftPos;
		}

		public void setLeftPos(Integer leftPos) {
			this.leftPos = leftPos;
		}

		public Integer getX() {
			return x;
		}

		public Integer getY() {
			return y;
		}
	}
	
	class GridItemDisplay {
		
		private GridItem gridItem;
		private Integer topPos;
		private Integer leftPos;
		
		public GridItemDisplay(GridItem gridItem)  {
			this.gridItem = gridItem;
			this.topPos = gridItem.getyGrid() * avConfig.getGridSize();
			this.leftPos = gridItem.getxGrid() * avConfig.getGridSize();
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
		
		public Integer getxGrid() {
			return gridItem.getxGrid();
		}

		public Integer getyGrid() {
			return gridItem.getyGrid();
		}
		
		public String getLabel() {
			return gridItem.getLabel();
		}
		
		public Boolean isUsingState() {
			return gridItem.getState() != GuestStatusState.INDETERMINATE;
		}
		
		public String getLocationName() {
			return gridItem.getLocationId() == null ? null : locationMap.get(gridItem.getLocationId()).getName();
		}
		
		public String getStateName() {
			return gridItem.getState().toString();
		}
	}
	
	private void handleAttractionEdit() {
		try
		{
			if (action.equals("insert")) {
				GridItem gi = new GridItem(
						null, 	// itemId
						ItemType.valueOf(itemType),
						Integer.parseInt(xgrid), 
						Integer.parseInt(ygrid),
						GuestStatusState.INDETERMINATE, 
						ItemType.valueOf(itemType) == ItemType.Gate ? locationType : "", 
						"", 	// description
						image == null ? null : image.isEmpty() ? null : image,
						0,		// sequence
						XpassOnlyState.AllGuests,	// xpass only
						null);	// location Id
				gridItemService.insert(gi);
			}
			else if (action.equals("delete")) {
				gridItemService.delete(Long.parseLong(itemId));
			}
			else if (action.equals("move")) {
				Data.moveGridItem(Long.parseLong(itemId), Integer.parseInt(xgrid), Integer.parseInt(ygrid));
			} else if (action.equals("setshowsubwaymap")) {
				UIProperties.getInstance().getUiConfig().setShowSubwayMap(showSubwayMap);
				UIProperties.getInstance().saveUIConfig();
			}
		}
		catch(Exception e) {
			logger.fatal("Failed to process GridItem modification, action: " + action, e);
		}
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public void setXgrid(String xgrid) {
		this.xgrid = xgrid;
	}

	public void setYgrid(String ygrid) {
		this.ygrid = ygrid;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isShowSubwayMap()
	{
		return showSubwayMap;
	}

	public void setShowSubwayMap(boolean showSubwayMap)
	{
		this.showSubwayMap = showSubwayMap;
	}
}
