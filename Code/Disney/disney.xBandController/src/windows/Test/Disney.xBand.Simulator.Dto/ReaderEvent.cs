using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Simulator.Dto
{
    public class ReaderEvent
    {
        public long ReaderID { get; set; }
        public string BandID { get; set; }
        public int SignalStrength { get; set; }
        public int Channel { get; set; }
        public int PacketSequence { get; set; }
        public int Frequency { get; set; }
    }

}
