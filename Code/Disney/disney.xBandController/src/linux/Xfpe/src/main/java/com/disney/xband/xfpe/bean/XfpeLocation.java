package com.disney.xband.xfpe.bean;

import java.util.LinkedList;
import java.util.List;

import com.disney.xband.xbrc.lib.entity.Location;
import com.disney.xband.xfpe.model.XfpeReader;

public class XfpeLocation {
	private Location location;
	private List<XfpeReader> readers;
	
	public XfpeLocation() {
		this.readers = new LinkedList<XfpeReader>();
	}
	
	public XfpeLocation(Location l) {
		this.readers = new LinkedList<XfpeReader>();
		this.location = l;
	}
	
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public List<XfpeReader> getReaders() {
		return readers;
	}
	public void setReaders(List<XfpeReader> readers) {
		this.readers = readers;
	}	
}
