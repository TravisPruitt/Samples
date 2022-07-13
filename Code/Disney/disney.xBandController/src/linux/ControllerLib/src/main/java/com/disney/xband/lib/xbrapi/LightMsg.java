package com.disney.xband.lib.xbrapi;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LightMsg {
	
	private static Pattern rgbPattern = Pattern
			.compile("[0-9]+,[0-9]+,[0-9]+");

	public enum ColorValue {
		off,
		white,
		inner_white,
		outer_white,
		yellow,
		inner_yellow,
		outer_yellow,
		green,
		inner_green,
		outer_green,
		blue,
		inner_blue,
		outer_blue,
		red,
		inner_red,
		outer_red
	}
	
	private static Set<String> colors;
	
	static {
		colors = new HashSet<String>();
		for (ColorValue cv : LightMsg.ColorValue.values())
			colors.add(cv.name());
	}
	
	public static boolean isColorValue(String value)
	{
		// first check for R,G,B
		Matcher m = rgbPattern.matcher(value);
		if (m.matches())
			return true;
		
		return colors.contains(value);
	}
	
	public static Set<String> getColors()
	{
		return colors;
	}
	
	public static String formatColor(int r, int g, int b)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(Integer.toString(r));
		sb.append(",");
		sb.append(Integer.toString(g));
		sb.append(",");
		sb.append(Integer.toString(b));
		
		return sb.toString();
	}
}
