using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Diagnostics;
using System.Configuration;

namespace Disney.xBand.xBMS.Simulator.Listeners
{
    public class RestartListener : Listener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        private WebServer webServer;

        public RestartListener(MessageRepository repository, WebServer webServer)
            : base(repository, "restart")
        {
            this.webServer = webServer;
        }

        protected override string ProcessRequest(HttpListenerContext context)
        {
            string name = context.Request.Url.Segments[context.Request.Url.Segments.Length - 1];

            if (String.Compare(name, "xbms", true) == 0)
            {
                try
                {
                    this.webServer.StopPublishing();
                    this.Repository.InitializexBMS(int.Parse(ConfigurationManager.AppSettings["InitialNumberOfGuests"]));
                    this.webServer.StartPublishing();

                    context.Response.ContentType = "text/html";
                    return String.Format("<pre>{0}</pre>", "xBMS simulator restarted.");

                }
                catch (Exception ex)
                {
                    Debug.WriteLine(ex.Message);
                    Debug.WriteLine(ex.StackTrace);

                    log.Error("Error starting xBMS Simulator", ex);

                    context.Response.StatusCode = (int) HttpStatusCode.BadRequest;
                }
            }

            if (String.Compare(name, "pxc", true) == 0)
            {
                try
                {
                }
                catch (Exception ex)
                {
                    Debug.WriteLine(ex.Message);
                    Debug.WriteLine(ex.StackTrace);

                    log.Error("Error starting xBMS Simulator", ex);
                }
            }

            return String.Empty;
        }
    }
}
