using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Disney.xBand.IDMS.Celebrations
{
    public enum CelebrationType
    {
        [Description("Birthday")]
        Birthday = 0,

        [Description("Anniversary")]
        Anniversary,

        [Description("Engagement")]
        Engagement,

        [Description("Graduation")]
        Graduation,

        [Description("Honeymoon")]
        Honeymoon,

        [Description("Personal Triumph")]
        PersonalTriumph,

        [Description("Reunion")]
        Reunion,

        [Description("Wedding")]
        Wedding,

        [Description("Other")]
        Other,

        [Description("First Visit")]
        FirstVisit
    }
}
