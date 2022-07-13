package com.disney.xband.xbrc.ui.db;

import java.util.Collection;

import com.disney.xband.xbrc.ui.bean.GridItem;

public interface GridItemService {
	public GridItem find(Long id) throws Exception;
	public Collection<GridItem> findAll() throws Exception;	
	public void insert(GridItem gi) throws Exception;
	public void update(GridItem gi) throws Exception;
	public void delete(Long id) throws Exception;
}
