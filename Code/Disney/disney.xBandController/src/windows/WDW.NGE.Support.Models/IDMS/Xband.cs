using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models.IDMS
{
    public class Xband
    {
        public long xBandID { get; set; }

        public String PublicId { get; set; }

        public String xBandRequestID { get; set; }

        public String SecureId { get; set; }

        public String TapID { get; set; }

        public String LongRangeID { get; set; }

        public String PrintedName { get; set; }

        public String State { get; set; }

        public String SecondaryState { get; set; }

        public String ProductId { get; set; }

        public String BandId { get; set; }

        public String AssignmentDateTime { get; set; }

        public List<Common.Link> oneViewLinks;
    }
}
