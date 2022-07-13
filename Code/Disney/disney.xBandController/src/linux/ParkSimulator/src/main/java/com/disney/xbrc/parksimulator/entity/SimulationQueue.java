package com.disney.xbrc.parksimulator.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimulationQueue implements Comparator<SimulationAction>
{
	private List<SimulationAction> queueActions = new ArrayList<SimulationAction>();
	
	public void add(SimulationAction sac)
	{
		queueActions.add(sac);
	}
	
	public List<SimulationAction> getDueList(long lWhen)
	{
		List<SimulationAction> liDue = new ArrayList<SimulationAction>();
		for(SimulationAction sac : queueActions)
		{
			if (sac.isDue(lWhen))
				liDue.add(sac);
		}
	
		Collections.sort(liDue, this);
		return liDue;
	}
	
	public void remove(List<SimulationAction> liRemove)
	{
		queueActions.removeAll(liRemove);
	}

	@Override
	public int compare(SimulationAction arg0, SimulationAction arg1)
	{
		if (arg0.getWhen() < arg1.getWhen())
			return -1;
		else if (arg0.getWhen()==arg1.getWhen())
			return 0;
		else 
			return 1;
	}

}
