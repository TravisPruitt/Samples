using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using System.Net;

namespace Disney.xBand.xBMS.Simulator.Listeners
{
    public class xBandRequestListener : Listener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public xBandRequestListener(MessageRepository repository)
            : base(repository, "xband-requests")
        {
        }

        protected override string ProcessRequest(HttpListenerContext context)
        {

            string xbandRequestId = context.Request.Url.Segments[context.Request.Url.Segments.Length - 1];

            if (!String.IsNullOrEmpty(xbandRequestId))
            {
                try
                {
                    if(this.Repository.ContainsxBandRequestMessage(xbandRequestId))
                    {
                        Dto.xBandRequest message = this.Repository.GetxBandRequestMessage(xbandRequestId);

                        //message.MessageState = Dto.MessageState.CallbackMade;

                        context.Response.ContentType = "application/json;charset=utf-8";
                        
                        return message.ToJson();
                    }
                    else
                    {

                        Dto.xBandRequestError error = new Dto.xBandRequestError()
                        {
                            Application = "XBand",
                            Code = "5028",
                            Message = String.Format("Request not found: {0}",xbandRequestId.Replace("-",String.Empty)),
                            Self = context.Request.Url.ToString(),
                            xCorrelationId = Guid.NewGuid().ToString(),
                            xSystemId = "XBand"
                        };

                        context.Response.ContentType = "application/json;charset=utf-8";

                        return error.ToJson();
                    }
                }
                catch (Exception ex)
                {
                    Debug.WriteLine(ex.Message);
                    Debug.WriteLine(ex.StackTrace);

                    log.Error(String.Format("Error Processing xband-request for xbandId: {0}",xbandRequestId), ex);

                    Dto.xBandRequestError error = new Dto.xBandRequestError()
                    {
                        Application = "XBand",
                        Code = "5029",
                        Message = ex.Message,
                        Self = context.Request.Url.ToString(),
                        xCorrelationId = Guid.NewGuid().ToString(),
                        xSystemId = "XBand"
                    };

                    context.Response.ContentType = "application/json;charset=utf-8";

                    return error.ToJson();
                }
            }

            context.Response.StatusCode = (int)HttpStatusCode.NotFound;

            return String.Empty;
        }

        protected void CheckIDMS()
        {
            //Check IDMS to see data was created correctly.


        }
    }
}
