package com.disney.xband.xbrc.lib.entity;

public class Wall
{
	private double x0, y0;
	private double x1, y1;
	
	public Wall(double x0, double y0, double x1, double y1)
	{
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	public double getX0()
	{
		return x0;
	}

	public double getY0()
	{
		return y0;
	}

	public double getX1()
	{
		return x1;
	}

	public double getY1()
	{
		return y1;
	}
}
