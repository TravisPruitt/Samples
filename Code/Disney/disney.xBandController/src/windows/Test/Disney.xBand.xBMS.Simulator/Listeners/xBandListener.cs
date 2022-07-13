using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;
using Disney.xBand.IDMS;
using Disney.xBand.IDMS.xBand;
using Disney.xBand.IDMS.Guests;
using System.Configuration;
using System.Net;

namespace Disney.xBand.xBMS.Simulator.Listeners
{
    public class xBandListener : Listener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private string idmsRootUrl;

        public xBandListener(MessageRepository repository)
            : base(repository, "xband")
        {
            this.idmsRootUrl = ConfigurationManager.AppSettings["IdmsRootUrl"];
        }

        protected override string ProcessRequest(HttpListenerContext context)
        {
            string xbandId = context.Request.Url.Segments[context.Request.Url.Segments.Length - 1];

            if (!String.IsNullOrEmpty(xbandId))
            {
                try
                {
                    context.Response.ContentType = "application/json;charset=utf-8";

                    if (this.Repository.ContainsxBandMessage(xbandId))
                    {
                        Dto.xBand message = this.Repository.GetxBandMessage(xbandId);

                        //message.MessageState = Dto.MessageState.CallbackMade;

                        return message.ToJson();
                    }
                    else
                    {

                        Dto.xBandRequestError error = new Dto.xBandRequestError()
                        {
                            Application = "XBand",
                            Code = "5028",
                            Message = String.Format("Request not found: {0}", xbandId.Replace("-", String.Empty)),
                            Self = context.Request.Url.ToString(),
                            xCorrelationId = Guid.NewGuid().ToString(),
                            xSystemId = "XBand"
                        };

                        return error.ToJson();
                    }
                }
                catch (Exception ex)
                {
                    Debug.WriteLine(ex.Message);
                    Debug.WriteLine(ex.StackTrace);

                    log.Error(String.Format("Error Processing xband-request for xbandId: {0}", xbandId), ex);

                    Dto.xBandRequestError error = new Dto.xBandRequestError()
                    {
                        Application = "XBand",
                        Code = "5029",
                        Message = ex.Message,
                        Self = context.Request.Url.ToString(),
                        xCorrelationId = Guid.NewGuid().ToString(),
                        xSystemId = "XBand"
                    };

                    return error.ToJson();
                }
            }

            return String.Empty;
        }
    }
}
