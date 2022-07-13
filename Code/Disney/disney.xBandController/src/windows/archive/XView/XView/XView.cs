using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.ServiceModel.Activation;
using System.ServiceModel.Web;
using XViewLib;
using System.Web.Script.Serialization;
using System.IO;
using System.Text;
using System.Data.Linq;
using System.Runtime.Serialization.Json;

namespace XView
{

    [ServiceContract]
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    [ServiceBehavior(InstanceContextMode = InstanceContextMode.PerCall)]
    public class XView
    {
        [OperationContract]
        [WebGet(UriTemplate = "")]
        public String DefaultService()
        {
            return "Hello XView.";
        }
        
        #region Guest Operations

        [OperationContract]
        [WebGet(UriTemplate = "/guests")]
        public Message GetGuests()
        {
            WebOperationContext ctx = WebOperationContext.Current;
            Message retVal = null;
            List<guest> guests = null;

            try
            {
                guests = Guests.getGuests();
               retVal = ctx.CreateJsonResponse<List<guest>>(guests);
            }
            catch (Exception ex)
            {
                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.BadRequest;
                retVal = ctx.CreateTextResponse(ex.Message);
            }

            return retVal; ;
        }

        /// <summary>
        /// Get a guestObject by guestId.
        /// </summary>
        /// <param name="guestId"></param>
        /// <returns></returns>
        [OperationContract]
        [WebGet(UriTemplate = "/guests/{guestId}")]
        public Message GetGuestbyId(String guestId)
        {
            WebOperationContext ctx = WebOperationContext.Current;
            long guestIdLong = long.Parse(guestId);

            Message retVal = null;

            try
            {
                guest guest = Guests.getGuestbyId(guestId);
                retVal = ctx.CreateJsonResponse<guest>(guest);
            }
            catch (Exception ex)
            {

                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.NotFound;
                retVal = ctx.CreateTextResponse(ex.Message);
            }

            return retVal;
        }

        [OperationContract]
        [WebGet(UriTemplate = "/guests/id/{guestId}")]
        public Message GetGuestbyIdOld(String guestId)
        {
            WebOperationContext ctx = WebOperationContext.Current;
            long guestIdLong = long.Parse(guestId);
            Message retVal = null;
            
            try
            {
                guest guest = Guests.getGuestbyId(guestId);
                retVal = ctx.CreateJsonResponse<guest>(guest);
            }
            catch (Exception ex)
            {
                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.NotFound;
                retVal = ctx.CreateTextResponse(ex.Message);
            }

            return retVal;
        }



        /// <summary>
        /// Update a guest object in the database.
        /// </summary>
        /// <param name="guest"></param>
        [OperationContract]
        [WebInvoke(Method="PUT", UriTemplate="/guests")]
        public void UpdateGuest(guest newguest)
        {
            //SaveGuest(newguest);
        }

        /// <summary>
        /// Save a new guest object to the database.
        /// </summary>
        /// <param name="guest"></param>
        [OperationContract]
        [WebInvoke(Method = "POST", UriTemplate = "/guests", RequestFormat=WebMessageFormat.Json, ResponseFormat=WebMessageFormat.Json)]
        public void SaveGuest(guest newGuest)
        {
            guest newguest = new XViewLib.guest();
            WebOperationContext ctx = WebOperationContext.Current;

            try
            {
                Guests.SaveGuest(newGuest);
                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.Created;
            }
            catch (Exception)
            {
                ctx.OutgoingResponse.StatusCode = System.Net.HttpStatusCode.BadRequest;
                throw;
            }

        }

        
        /// <summary>
        /// Delete a guest object from the database.
        /// </summary>
        /// <param name="guest"></param>
        [OperationContract]
        [WebInvoke(Method="DELETE", UriTemplate="/guests")]
        public void DeleteGuest(guest guest)
        {


        }

        public List<guest> SearchGuests(String guestName)
        {
            List<guest> retVal = null;

            return retVal;
        }

        #endregion





    }
}