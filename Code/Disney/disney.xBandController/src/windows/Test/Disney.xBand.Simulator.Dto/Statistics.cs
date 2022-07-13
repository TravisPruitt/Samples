using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Simulator.Dto
{
    public class Statistics
    {
        public int Bands { get; set; }
        public int Reads { get; set; }
        public string ReaderTypeName { get; set; }
        public string ReaderLocationTypeName { get; set; }
    }
}
