using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models.Services
{
    public interface IXBMSServiceAgent
    {
        ServiceResult<Models.xBMS.XbandDetails> GetXbandDetails(string xBandId);
        ServiceResult<Models.xBMS.XbandDetails> GetXbandDetailsByVisualId(string visualId);
        ServiceResult<Models.xBMS.XbandRequestDetails> GetXbandRequestDetails(string xbandRequestId);
        ServiceResult<Models.xBMS.XbandRequestDetails> GetXbandRequestDetailsByTravelPlan(string travelPlanId);
    }
}
