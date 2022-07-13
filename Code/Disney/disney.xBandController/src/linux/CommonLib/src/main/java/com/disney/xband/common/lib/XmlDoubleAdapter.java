package com.disney.xband.common.lib;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XmlAdapter mechanism in JAXB allows for defining custom mapping classes for JAXB marshaling and unmarshaling methods.
 * This particular implementation rounds a <CODE>Double</CODE> value to four significant digits before serializing it
 * to xml.
 * 
 * @author Iwona Glabek
 */
public class XmlDoubleAdapter extends XmlAdapter<Double, Double> {

	@Override
	public Double marshal(Double v) throws Exception {
		
		if (v == null)
			return null;
		
		long factor = (long) Math.pow(10, 4);
		double value = v * factor;
		long tmp = Math.round(value);
		
		return new Double((double)tmp / factor);
	}

	@Override
	public Double unmarshal(Double v) throws Exception {
		return v;
	}
}