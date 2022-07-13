package com.disney.xband.xbrc.ui;

import java.util.LinkedList;
import java.util.List;

import com.disney.xband.xbrc.ui.bean.GridItem;
import com.disney.xband.xbrc.ui.db.Data;

/*
 * AttractionViewGrid is a container that keeps all the information necessary to draw a diagram of the attraction.
 * It is a singleton so that the information does not need to be continuously re-read from the database for each web request.
 */
public class AttractionViewGrid {
	
	private List<GridItem> gridItemList;

	private static class SingletonHolder { 
		public static final AttractionViewGrid instance = new AttractionViewGrid();
	}
	
	public static AttractionViewGrid getInstance() {
		return SingletonHolder.instance;
	}
	
	private AttractionViewGrid() {
		Refresh();
	}
	
	public void Refresh() {
		synchronized(this) {
			gridItemList = Data.GetGridItems();
		}
	}

	/*
	 * Returns a clone of the list.
	 */
	public List<GridItem> getGridItemList() {
		synchronized(this) {
			List<GridItem> gridItemListClone = new LinkedList<GridItem>();
			for (GridItem o : gridItemList)
				gridItemListClone.add(o);
			return gridItemListClone;
		}
	}
}
