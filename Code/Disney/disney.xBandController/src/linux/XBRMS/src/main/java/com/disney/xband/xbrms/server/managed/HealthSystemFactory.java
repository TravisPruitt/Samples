package com.disney.xband.xbrms.server.managed;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import com.disney.xband.xbrms.common.model.HealthItemDto;
import com.disney.xband.xbrms.common.model.IHealthSystem;
import com.disney.xband.xbrms.common.model.IdmsDto;
import com.disney.xband.xbrms.common.model.JmsListenerDto;
import com.disney.xband.xbrms.common.model.XbrcDto;

public class HealthSystemFactory
{
	private static Logger logger = Logger.getLogger(HealthSystemFactory.class);
	
	public static IHealthSystem makeHealthSystem(HealthItemDto item)
	{
        if(item == null) {
            return null;
        }

		Constructor constructor = null;

		try
		{
			if (item instanceof XbrcDto)
				constructor = XbrcSystem.class.getDeclaredConstructor(item.getClass());
			else if (item  instanceof IdmsDto)
				constructor = IdmsSystem.class.getDeclaredConstructor(item.getClass());
			else if (item instanceof JmsListenerDto)
				constructor = JmsListenerSystem.class.getDeclaredConstructor(item.getClass());
            else {
                logger.error("Unsupported health item type: " + item.toString());
                return null;
            }
		}
		catch (SecurityException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName(), e);
		}
		catch (NoSuchMethodException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName() + 
					". The one argument constructor on this system object is not defined.", e);
		}

        if(constructor == null) {
            return null;
        }

		try
		{
			constructor.setAccessible(true);
			
			return (IHealthSystem)constructor.newInstance(item);
		}
		catch (SecurityException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName(), e);
		}
		catch (IllegalArgumentException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName() + 
					". System object assiciated with that item is missing a public constructor that takes" +
					" this health item as an argument.", e);
		}
		catch (InstantiationException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName() + 
					". Can't instantiate the system object.", e);
		}
		catch (IllegalAccessException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName() + 
					". The one argument constructor on this system object is not 'public'.", e);
		}
		catch (InvocationTargetException e)
		{
			logger.error("Not able to monitor health status of health item " + item.getClass().getName() + 
					". The one argument constructor on this system object threw an exception.", e);
		}
		
		return null;
	}
}