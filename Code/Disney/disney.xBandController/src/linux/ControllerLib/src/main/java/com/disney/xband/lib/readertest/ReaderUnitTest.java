package com.disney.xband.lib.readertest;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class ReaderUnitTest {
	private String name;	
	private List<ReaderActionContainer> actions;
	
	public String getName() {
		return name;
	}
			
	public void setName(String name) {
		this.name = name;
	}	
	
	@XmlElementWrapper(name="actions")
	@XmlElement(name="readerActionContainer")
	public List<ReaderActionContainer> getActions() {
		return actions;
	}
	public void setActions(List<ReaderActionContainer> actions) {
		this.actions = actions;
	}
}
