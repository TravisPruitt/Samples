using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    public class AttractionHeartbeat : ModelBase<AttractionHeartbeat>
    {
        public String Name { get; set; }

        public HeartbeatStatus Status { get; set; }


        
    }
}
