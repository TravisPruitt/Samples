package com.disney.xband.idms.lib.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class GuestSearchCollection {
	
	private String searchString;
	private int count;
	List<GuestSearchItem> results;
	
	
	@XmlElement(name="searchString")
	public String getSearchString() {
		return searchString;
	}
	
	@XmlElement(name="searchString")
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	
	@XmlElement(name="count")
	public int getCount() {
		return count;
	}
	
	@XmlElement(name="count")
	public void setCount(int count) {
		this.count = count;
	}
	
	@XmlElement(name="results")
	public List<GuestSearchItem> getResults() {
		return results;
	}
	
	@XmlElement(name="results")
	public void setResults(List<GuestSearchItem> results) {
		this.results = results;
	}
	

}
