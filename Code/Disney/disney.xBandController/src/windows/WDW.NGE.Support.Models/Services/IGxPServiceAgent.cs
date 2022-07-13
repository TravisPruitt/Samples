using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace WDW.NGE.Support.Models.Services
{
    public interface IGxPServiceAgent
    {
        Models.GxP.IndividualEligibility CheckEligibility(string date, string xid);
        
        Models.GxP.GroupEligibility CheckGroupEligibility(String date, List<String> xids);

        Models.GxP.FastPassIntegration GetIntegrationStatus();
    }
}
