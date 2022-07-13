package com.disney.xband.common.lib.health;

import javax.xml.bind.annotation.XmlTransient;

public enum StatusType 
{
	Green,
	Yellow,
	Red;
	
	transient boolean fromReaderDiagnostics = false;
	
	public boolean isParameterMoreSevere(StatusType parameter) 
	{
		return parameter.ordinal() > this.ordinal();
	}

	@XmlTransient
	public boolean isFromReaderDiagnostics()
	{
		return fromReaderDiagnostics;
	}

	public void setFromReaderDiagnostics(boolean fromReaderDiagnostics)
	{
		this.fromReaderDiagnostics = fromReaderDiagnostics;
	}
	
	/**
	 * Natural order of this enum: Red, Yellow, Green, null
	 */
	public static int compare(StatusType st1, StatusType st2)
	{
		if (st1 == null && st2 == null)
			return 0;
		if (st1 != null && st2 == null)
			return -1;
		if (st1 == null && st2 != null)
			return 1;
		
		if (st1 == st2)
			return 0;
		
		switch (st1)
		{
			case Red: return -1;
			case Yellow:
				if (st2 == Red)
					return 1;
				if (st2 == Green)
					return -1;
			case Green: return 1;
			default: return 1;
		}
	}
}

