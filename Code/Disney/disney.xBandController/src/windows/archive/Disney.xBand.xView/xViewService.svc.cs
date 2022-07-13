using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.IO;
using System.ServiceModel.Web;
using Disney.xBand.Data;
using System.Net;
using System.Web.Script.Serialization;
using System.ServiceModel.Activation;
using System.Reflection;

namespace Disney.xBand.xView
{
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    public class xViewService : IxViewService
    {
        public Stream GetxBand(string id)
        {
            int bandID;

            if (!int.TryParse(id, out bandID))
            {
                throw new WebFaultException(HttpStatusCode.BadRequest);
            }

            using (xViewEntities context = new xViewEntities())
            {
                var temp = (from x in context.xBands
                            where x.xBandId == bandID
                            select new { x.xBandId, x.lRId, x.tapId }).FirstOrDefault();

                xBand result = null;

                if (temp != null)
                {
                    result = new xBand() { xbandid = temp.xBandId.ToString(), lrid = temp.lRId, tapid = temp.tapId };
                }

                System.ServiceModel.Web.WebOperationContext.Current.OutgoingResponse.ContentType = "application/json; charset=utf-8";

                var s = new JavaScriptSerializer();
                string jsonClient = s.Serialize(result);

                return new MemoryStream(Encoding.UTF8.GetBytes(jsonClient));
            }
        }

        public Stream GetxBands()
        {
            using (xViewEntities context = new xViewEntities())
            {
                var temp = (from x in context.xBands
                     select new { x.xBandId, x.lRId, x.tapId });

                List<xBand> result =
                    (from x in temp.AsEnumerable()
                     select new xBand() { xbandid = x.xBandId.ToString(), lrid = x.lRId, tapid = x.tapId } ).ToList();

                System.ServiceModel.Web.WebOperationContext.Current.OutgoingResponse.ContentType = "application/json; charset=utf-8";

                var s = new JavaScriptSerializer();
                string jsonClient = s.Serialize(result);

                return new MemoryStream(Encoding.UTF8.GetBytes(jsonClient));
            }
        }
    }
}
