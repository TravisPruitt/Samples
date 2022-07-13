using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class MagicBand
    {
        [DataMember(Name = "xbandId")]
        public long MagicBandID { get; set; }

        [DataMember(Name = "bandId")]
        public string BandID { get; set; }

        [DataMember(Name = "longRangeTag")]
        public string LongRangeID { get; set; }

        [DataMember(Name = "shortRangeTag")]
        public string TapID { get; set; }

        public DateTime NextTransmit { get; set; }

        public int PacketSequence { get; set; }

        public int Frequency { get; set; }

        public int Channel { get; set; }

        public MagicBand()
        {
            Random random = new Random();
            this.NextTransmit = DateTime.UtcNow.AddMilliseconds(random.Next(1250));
            this.PacketSequence = 0;
            this.Frequency = random.Next(15);
            this.Channel = random.Next() & 1;
        }
    }
}
