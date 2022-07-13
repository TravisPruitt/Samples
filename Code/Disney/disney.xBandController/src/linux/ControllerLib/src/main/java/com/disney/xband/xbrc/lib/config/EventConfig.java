package com.disney.xband.xbrc.lib.config;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.disney.xband.common.lib.Config;
import com.disney.xband.common.lib.ConnectionPool;
import com.disney.xband.common.lib.PersistName;

@PersistName("EventConfig")
public class EventConfig {
	
	@PersistName("amplifiedSignalStrengthMin")
	private int ssa_min = -90;
	//maximum amplified signal strength
	@PersistName("amplifiedSignalStrengthMax")
	private int ssa_max = -35;
	@PersistName("deltaTime")
	private long delta_time = 3000;
	
	private transient double ssa_multiplier;
	
	public static ConnectionPool connPool;
	public static Logger logger = Logger.getLogger(EventConfig.class);
	
	public static EventConfig getInstance(){
		return EventConfigHolder.instance;
	}
	
	public synchronized void initialize(ConnectionPool cp){
		if (connPool != null)
			return;	//already initialized
		
		//replace the defaults with DB configuration, if exists.
		connPool = cp;
		Connection conn = null;
		try {
			conn = connPool.getConnection();
			
			Config.getInstance().read(conn, this);
			
			double range = Math.abs(ssa_max - ssa_min);
			if (range == 0.0)
				ssa_multiplier = 0.0;
			else
				ssa_multiplier = 100.0 / range;
			
		} catch (Exception e) {
			logger.error("Failed to grab configuration values from DB. Using class defaults [ssa_min:" + ssa_min + ",ssa_max:" + ssa_max + "]");
		} finally {
			connPool.releaseConnection(conn);
		}	
	}
	
	/**
	 * @return the ssa_min
	 */
	public int getSsaMin() {
		return ssa_min;
	}

	/**
	 * @return the ssa_max
	 */
	public int getSsaMax() {
		return ssa_max;
	}
	
	public long getDeltaTime() {
		return delta_time;
	}

	public double getSsaMultiplier() {
		return ssa_multiplier;
	}

	private EventConfig(){}
	
	private static class EventConfigHolder {
		private static EventConfig instance = new EventConfig();
	}
}
