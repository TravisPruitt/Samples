using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace WDW.NGE.Support.Models.Services
{
    public enum ServiceCallStatus
    {
        OK = 0,

        Timeout = 1,

        NotFound = 2,

        Unknown = 3
    }
}
