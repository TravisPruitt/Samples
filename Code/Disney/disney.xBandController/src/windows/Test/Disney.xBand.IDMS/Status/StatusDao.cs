using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.IDMS.Status
{
    public class StatusDao : Dao.DaoBase, IStatusDao
    {
       public StatusDao(string rootUrl)
            : base(rootUrl)
        {
        }
        
        public StatusResult GetStatus()
        {
            return GetXmlRequest<StatusResult>(String.Concat(this.RootUrl, "status"), new Metrics("Dummy"));
            
        }
    }
}
