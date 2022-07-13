//*****************************************************************************
// simBands.cpp
//
//	Simulate band pings being received.
//*****************************************************************************
//
//	Written by Mike Wilson
//	Copyright 2012, Synapse.com
//
//*****************************************************************************

namespace simBand
{

	typedef uint64_t PINGTIME;	// time that a ping should be generated.
	typedef uint64_t BANDID;	// a band ID

	typedef struct
	{
		BANDID id;

		// average signal strength
		int8_t			avgSS;						// what is band's average signal strength
		// signal strength variation
		uint8_t			varSS;						// how much does the band vary in it's signal strength
		// signal received certainty
		uint8_t			percentRcvd;				// how many of the band's pings are received right now?
		// current sequenceNumber
		uint8_t			sequenceNumber;				// which sequenceNumber the band xmits next
		// current frequency (0-3)
		uint8_t			frequency;					// which frequency in the sequence the band is on.
		// Band clock deviation
		uint8_t			percentBandClockDeviation;	// how much this band's clock is off
		// Band clock variation
		uint8_t			percentBandClockVariation;	// how much the band's clock varies from ping to ping

		// band programming:
		// current mode (0-3)
		uint8_t			bandMode;					// mode that the band is operating in.
		// mode 0 - 3 parameters:
		// 		interval
		uint16_t		bandInterval;				// interval settings for each mode.
		//		# of pings
		uint8_t			bandPingCount[];			// ping count left to send for each mode.
	}BAND_INFO;

	std::vector <BAND_INFO> bandInfo;

	// The band timeout table - times at which a ping should be generated and the associated band that expires.
	std::map <PINGTIME, *BAND_INFO> pingTimes;

	// the iterator type for the band id table
	typedef std::map <PINGTIME, *BAND_INFO>::iterator pingTimeI;

/////////////////////////////////////////////////

	// add a band
	add(BANDID band);

	// remove a band
	remove(BANDID band);

	// change characteristic that might indicate a change in physical location.
	changeParameters(BANDID band, int8_t ss, uint8_t percentrRcvd);

	// change programming; this is the same as the transmitter sending a command to the band.
	changeProgramming(BANDID band, uint8_t mode, uint16_t int1, uint16_t pings1, uint16_t int2, uint16_t pings2, uint16_t int3, uint16_t pings3);

} // namespace bsim

namespace bandLocationSim
{
	// simulate a band moving around?


}










		// runs as a separate thread.
		void * pingReadThread(void *cradio)
		{
			if (cradio == 0) // init the ping thread.
			{
				// loop through the list of bands in the config file.
				// for each band, start a timer.
				return NULL;
			}

			// When the timer expires: (cradio has the index in the band table)
			// - Reset the timer
			// - use callback to record a ping event.

			// filter out GetPings with signal_strength == 0.
/*			if( 	rxtx->cmd != GetPing
				||	rxtx->packet.signal_strength
			  )
				cback(rmsg->radio,rmsg->msg,rmsg->length);
*/
			return NULL;
		} // pingReadThread()

		void start( void(*callback)(uint8_t radio, uint8_t * buff, uint8_t len))
		{
			cback = callback; // called by pingReadThread().

// TODO: Write the following. pingReadThread failed to start last time I
//	ran the code below.

			// loop through the list of bands in the config file.
			// for each band, start a timer.


//			int priority = 50;
//			void *arg = NULL;
//			tid = thread::create(pingReadThread, priority, arg);
		}

	} // namespace rxThread
