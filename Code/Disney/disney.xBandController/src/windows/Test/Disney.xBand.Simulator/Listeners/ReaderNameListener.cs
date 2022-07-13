using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Simulator.Listeners
{
    public class ReaderNameListener : ReaderHttpListener
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public ReaderNameListener(Dto.Reader reader, Dto.Repositories.IReaderRepository readerRepository)
            : base(reader, readerRepository, "reader/name")
        {
        }

        protected override string ProcessRequest(System.Net.HttpListenerRequest httpListenerRequest)
        {
            string name = httpListenerRequest.QueryString["name"];

            log.Info(String.Format(
                "Reader name {0} received from Controller.",
                String.Format("Reader {0} Port {1}", this.Reader.ReaderName, this.Reader.WebPort)));


            return String.Empty;
        }
    }
}
