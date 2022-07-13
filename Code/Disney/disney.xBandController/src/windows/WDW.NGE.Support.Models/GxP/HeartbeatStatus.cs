using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models.GxP
{
    public enum HeartbeatStatus
    {
        [Description("OK")]
        OK = 0,

        [Description("Failed")]
        Failed = 1

    }
}
