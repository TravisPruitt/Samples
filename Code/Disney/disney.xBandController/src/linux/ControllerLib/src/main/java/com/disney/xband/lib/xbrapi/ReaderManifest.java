package com.disney.xband.lib.xbrapi;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder={"repos"})
public class ReaderManifest
{
	private ReaderRepo[] repos;
	private List<String> hwids;
	
	public ReaderManifest()
	{
	}

	public ReaderRepo[] getRepos()
	{
		return repos;
	}

	public void setRepos(ReaderRepo[] repos)
	{
		this.repos = repos;
	}

	public List<String> getHwids()
	{
		return hwids;
	}

	public void setHwids(List<String> hwids)
	{
		this.hwids = hwids;
	}

	public ReaderManifest clone()
	{
		ReaderManifest ret = new ReaderManifest();
		if (repos != null)
		{
			ReaderRepo[] clone = new ReaderRepo[repos.length]; 
			for (int i = 0; i < repos.length; i++)
				clone[i] = repos[i].clone();
			ret.setRepos(clone);
		}
		
		if (hwids != null)
		{
			ret.setHwids(new LinkedList<String>());
			
			for (String hwid : hwids)
				ret.getHwids().add(hwid);
		}
		
		return ret;
	}
}
