using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models.GxP
{
    public enum MessageCode
    {
        [Description("No Ticket For Park")]
        NoTicketForPark = 7002,

        [Description("Not NGE Eligible")]
        NotNGEEligible = 7003,

        [Description("Party Not of Appropriate Age")]
        AppropriateAge = 7004,

        [Description("Not in Fastpass+ Selection Window")]
        NotInWindow = 7005,

        [Description("Not All Party Member's Individually Eligible")]
        NotAllEligible = 7006,

        [Description("Eligible")]
        Eligible = 7007
    }
}
