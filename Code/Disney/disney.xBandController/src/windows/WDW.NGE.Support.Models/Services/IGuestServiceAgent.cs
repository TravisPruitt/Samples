using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models.Services
{
    public interface IGuestServiceAgent
    {
        ServiceResult<Models.Common.IGuestProfile> GetGuestProfile(Models.Common.GuestIdentifier guestIdentifier);
    }
}
