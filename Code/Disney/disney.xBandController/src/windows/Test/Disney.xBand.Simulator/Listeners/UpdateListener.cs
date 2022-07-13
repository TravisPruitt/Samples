using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;

namespace Disney.xBand.Simulator.Listeners
{
    public class StreamInfoUpdatedEventArgs : EventArgs
    {
        public string Url { get; set; }

        public int EventsInterval { get; set; }

        public int MaximumEvents { get; set; }
    }

    public class UpdateListener : ReaderHttpListener
    {
        public event EventHandler<StreamInfoUpdatedEventArgs> StreamInfoUpdated;

        public UpdateListener(Dto.Reader reader, Dto.Repositories.IReaderRepository readerRepository)
            : base(reader, readerRepository, "update_stream")
        {
        }

        protected override string ProcessRequest(HttpListenerRequest httpListenerRequest)
        {
            string url = httpListenerRequest.QueryString["url"];
            int eventsInterval = 0;
            int maximumEvents = 0;
            if (String.IsNullOrEmpty(url))
            {
                return null;
            }

            string intervalValue = httpListenerRequest.QueryString["interval"];
            if (!String.IsNullOrEmpty(intervalValue))
            {
                if (!int.TryParse(intervalValue, out eventsInterval))
                {
                    eventsInterval = 200;
                }
            }

            string maximumValue = httpListenerRequest.QueryString["max"];
            if (!String.IsNullOrEmpty(maximumValue))
            {
                if (!int.TryParse(maximumValue, out maximumEvents))
                {
                    maximumEvents = -1;
                }
            }
            else
            {
                maximumEvents = -1;
            }

            this.Repository.UpdateStreamInfo(this.Reader.ReaderID, url, eventsInterval, maximumEvents);

            if (this.StreamInfoUpdated != null)
            {
                this.StreamInfoUpdated(this, new StreamInfoUpdatedEventArgs()
                    {
                        Url = url,
                        EventsInterval = eventsInterval,
                        MaximumEvents = maximumEvents
                    });
            }


            return String.Empty;
        }
    }
}
