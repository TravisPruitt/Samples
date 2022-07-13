using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Provisionator.Dto
{
    public class DemoGuest
    {
        public long GuestID { get; set; }
        public string LastName { get; set; }
        public string FirstName { get; set; }
        public int DemoID { get; set; }
        public long xBandID { get; set; }
        public string xID { get; set; }
        public string GxpLinkID { get; set; }
        public string TapID { get; set; }
        public string BandID { get; set; }
        public bool IsLeadGuest { get; set; }
    }
}
