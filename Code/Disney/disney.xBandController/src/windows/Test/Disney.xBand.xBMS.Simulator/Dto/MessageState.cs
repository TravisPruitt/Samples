using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    public enum MessageState
    {
        [Description("Sent")]
        Sent = 0,

//        [Description("Callback Made")]
//        CallbackMade = 1,

        [Description("Success")]
        Success = 2,

        [Description("Error")]
        Error = 3,

//        [Description("Guest Not Found")]
//        GuestNotFound = 4,

//        [Description("xBand Assignment Not Found")]
//        AssignmentNotFound = 5,

//        [Description("xBand Not Found")]
//        xBandNotFound = 6,

//        [Description("Guest Name Mismatch")]
//        GuestNameMismatch = 7,

//        [Description("Guest Identifier Not Found")]
//        GuestIdentifierNotFound = 8,

//        [Description("xBMS Link ID Not Found")]
//        xbmsLinkIdNotFound = 9

    }
}
