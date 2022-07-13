using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Provisionator.Dto
{
    public class Demo
    {
        public int DemoID { get; set; }
        public string DemoDescription { get; set; }
        public int DemoOrder { get; set; }
        public TimeSpan ScheduledTime { get; set; }
    }
}
