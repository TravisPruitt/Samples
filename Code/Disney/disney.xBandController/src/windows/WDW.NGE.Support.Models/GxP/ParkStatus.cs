using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models.GxP
{
    public class ParkStatus
    {
        public String ParkName { get; set; }

        public List<AttractionHeartbeat> Heartbeats { get; set; }
    }
}
