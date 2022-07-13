using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Simulator.Dto
{
    public class MagicBand
    {
        public long MagicBandID { get; set; }

        public string BandID { get; set; }

        public string LongRangeID { get; set; }

        public string TapID { get; set; }

        public DateTime NextTransmit { get; set; }

        public int PacketSequence { get; set; }

        public int Frequency { get; set; }

        public int Channel { get; set; }

        public MagicBand()
        {
            Random random = new Random();
            this.NextTransmit = DateTime.Now.AddMilliseconds(random.Next(1250));
            this.PacketSequence = 0;
            this.Frequency = random.Next(15);
            this.Channel = random.Next() & 1;
        }
    }
}
