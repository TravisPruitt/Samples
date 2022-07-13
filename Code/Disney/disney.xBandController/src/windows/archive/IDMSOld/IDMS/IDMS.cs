using System;
using System.Collections.Generic;
using System.Linq;
using System.ServiceModel;
using System.ServiceModel.Activation;
using System.ServiceModel.Web;
using System.Text;
using IDMSLib;
using System.ServiceModel.Channels;


namespace IDMS
{
    [ServiceContract]
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.PerCall)]
    public class IDMS
    {

        [OperationContract]
        [WebGet(UriTemplate = "")]
        public String DefaultService()
        {
            // TODO: Replace the current implementation to return a collection of SampleItem instances
            return "Hello IDMS.";
        }


        [OperationContract]
        [WebGet(UriTemplate = "/guests")]
        public Message GetGuests()
        {
            WebOperationContext ctx = WebOperationContext.Current;
            Message retVal = null;
            List<guestPOCO> guests = null;

            try
            {
                guests = guest.GetAllGuests();
                retVal = ctx.CreateJsonResponse<List<guestPOCO>>(guests);
            }
            catch (Exception ex)
            {
                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.BadRequest;
                retVal = ctx.CreateTextResponse(ex.Message);
            }

            return retVal;

        }

        [OperationContract]
        [WebGet(UriTemplate = "/guests/{guestId}")]
        public Message GetGuestById(String guestId)
        {
            long gId = long.Parse(guestId);

            Message retVal = null;

            WebOperationContext ctx = WebOperationContext.Current;
            try
            {
                guestPOCO g =  guest.GetGuestById(gId);
                retVal = ctx.CreateJsonResponse<guestPOCO>(g);
            }
            catch (Exception ex)
            {
                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.NotFound;
                retVal = ctx.CreateTextResponse(ex.Message);
            }

            return retVal;
            
        }


    }
}