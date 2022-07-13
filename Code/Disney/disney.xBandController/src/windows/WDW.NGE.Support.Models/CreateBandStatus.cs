using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models
{
    [Flags]
    public enum CreateBandStatus
    {
        [Description("Created")]
        Created = 0,

        [Description("xBMS Timeout")]
        xBMSTimeout = 1,
        
        [Description("Guest Not Foud in IDMS")]
        GuestNotFound = 2,

        [Description("Band Not Foud in xBMS")]
        BandNotFound = 4,

        [Description("Band Already in IDMS")]
        BandExists = 8,

    }
}
