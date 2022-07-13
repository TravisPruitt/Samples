using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Disney.xBand.xBMS.Simulator.Listeners
{
    public class StatusListener : Listener
    {

        public StatusListener(MessageRepository repository)
            : base(repository, "status")
        {
        }

        protected override string ProcessRequest(HttpListenerContext context)
        {
            context.Response.ContentType = "application/json;charset=utf-8";
            return this.Repository.Status.ToJson();
        }
    }
}
