using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models
{
    [Flags]
    public enum GuestCompareStatus
    {
        [Description("Match")]
        Match = 0,

        [Description("Missing from OneView")]
        GuestMissingFromOneView = 1,

        [Description("Missing from IDMS")]
        GuestMissingFromIDMS = 2,

        [Description("Name Mismatch")]
        NameMismatch = 4,

        [Description("Extra Identifiers")]
        ExtraIDMSIdentifiers = 8,

        [Description("Missing Identifiers")]
        MissingIdentifiers = 16,

        [Description("Missing xBands")]
        MissingBands = 32,

        [Description("Band Not Found")]
        BandNotFound = 64,
        
        [Description("Fixed")]
        Fixed = 128,
        
        [Description("IDMS Timeout")]
        IDMSTimeout = 256,

        [Description("OneView Timeout")]
        OneViewTimeout = 512
    }
}
