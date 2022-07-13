package com.disney.xband.xbrc.attractionmodel;

import java.util.Comparator;

import com.disney.xband.xbrc.lib.model.LRREventAggregate;

public class LrrEventComparator implements Comparator<LRREventAggregate>
{
	@Override
	public int compare(LRREventAggregate arg0, LRREventAggregate arg1)
	{
		// handle null cases
		if (arg0 == null && arg1 != null)
			return -1;
		else if (arg0 != null && arg1 == null)
			return 1;
		else if (arg0 == null && arg1 == null)
			return 0;

		// sort by timestamp
		return arg0.getTimestamp().compareTo(arg1.getTimestamp());
	}
}
