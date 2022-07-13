using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.Common
{
    public interface IGuestProfile
    {
        String LastName { get; set; }

        String FirstName { get; set; }

        List<Common.GuestIdentifier> GuestIdentifiers { get; set; }
    }
}
