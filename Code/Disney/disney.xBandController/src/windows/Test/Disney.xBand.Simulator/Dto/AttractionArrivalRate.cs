using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Simulator.Dto
{
    public class AttractionArrivalRate
    {
        public int AttractionID { get; set; }

        public int Hour { get; set; }

        public int Minute { get; set; }

        public int StandBy { get; set; }

        public int FastPassPlus { get; set; }
    }
}
