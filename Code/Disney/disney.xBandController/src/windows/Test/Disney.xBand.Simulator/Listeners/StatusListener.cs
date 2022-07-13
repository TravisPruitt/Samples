using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Disney.xBand.Simulator.Listeners
{
    public class StatusListener : ReaderHttpListener
    {
        public StatusListener(Dto.Reader reader, Dto.Repositories.IReaderRepository readerRepository)
            : base(reader, readerRepository, "status")
        {
        }

        protected override string ProcessRequest(HttpListenerRequest httpListenerRequest)
        {
            Dto.Status status = this.Repository.GetStatus(this.Reader.ReaderID);

            return ToJson(typeof(Dto.Status), status);
        }
    }
}
