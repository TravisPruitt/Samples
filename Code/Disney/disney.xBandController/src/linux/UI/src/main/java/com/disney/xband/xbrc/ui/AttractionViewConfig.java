package com.disney.xband.xbrc.ui;

import com.disney.xband.common.lib.PersistName;

@PersistName("UIAttractionViewConfig")
public class AttractionViewConfig {
	private int gridSize = 50;
	private int gridWidth = 18;
	private int gridHeight = 10;
	private int guestIconSpacing = 10;
	private int guestIconStagger = 4;
	private int guestIconHeight = 24;
	private int guestIconWidth = 24;
	private int maxGuestsPerIcon = 5;
	private int maxIconsPerGridItem = 3;
	
	public int getGridSize() {
		return gridSize;
	}
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
	}
	public int getGridWidth() {
		if (gridWidth > 18)
			return 18;
		else
			return gridWidth;
	}
	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}
	public int getGridHeight() {
		return gridHeight;
	}
	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}
	public int getGuestIconSpacing() {
		return guestIconSpacing;
	}
	public void setGuestIconSpacing(int guestIconSpacing) {
		this.guestIconSpacing = guestIconSpacing;
	}
	public int getGuestIconStagger() {
		return guestIconStagger;
	}
	public void setGuestIconStagger(int guestIconStagger) {
		this.guestIconStagger = guestIconStagger;
	}
	public int getGuestIconHeight() {
		return guestIconHeight;
	}
	public void setGuestIconHeight(int guestIconHeight) {
		this.guestIconHeight = guestIconHeight;
	}
	public int getGuestIconWidth() {
		return guestIconWidth;
	}
	public void setGuestIconWidth(int guestIconWidth) {
		this.guestIconWidth = guestIconWidth;
	}
	public int getMaxGuestsPerIcon() {
		return maxGuestsPerIcon;
	}
	public void setMaxGuestsPerIcon(int maxGuestsPerIcon) {
		this.maxGuestsPerIcon = maxGuestsPerIcon;
	}
	public int getMaxIconsPerGridItem() {
		return maxIconsPerGridItem;
	}
	public void setMaxIconsPerGridItem(int maxIconsPerGridItem) {
		this.maxIconsPerGridItem = maxIconsPerGridItem;
	}
	public int getContainerWidth(){
		if (this.gridWidth > 18)
			this.gridWidth = 18;
		
		return this.gridWidth * this.gridSize + this.gridSize;
	}
	public int getContainerHeight(){
		return this.gridHeight * this.gridSize + this.gridSize * 2;
	}
}
