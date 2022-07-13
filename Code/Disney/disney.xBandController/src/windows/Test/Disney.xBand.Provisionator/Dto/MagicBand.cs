using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Provisionator.Dto
{
    public class MagicBand
    {
        public long MagicBandID { get; set; }

        public string LongRangeID { get; set; }

        public string TapID { get; set; }

        public string BandID { get; set; }

        public List<Dto.Guest> Guests { get; set; }
    }
}
