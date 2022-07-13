using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.xBMS.Simulator.Listeners;
using System.Net;
using Disney.xBand.xBMS.Simulator.JMS;
using System.Configuration;
using System.Threading;

namespace Disney.xBand.xBMS.Simulator
{
    public class WebServer
    {
        private MessageRepository repository;
        private RestartListener restartListener;
        private StatusListener statusListener;
        private xBandListener xBandListener;
        private xBandRequestListener xBandRequestListener;

        private xBandRequestPublisher xBandRequestPublisher;
        private xBandPublisher xBandPublisher;

        public WebServer()
        {
            if (!HttpListener.IsSupported)
            {
                throw new NotSupportedException(
                    "Needs Windows XP SP2, Server 2003 or later.");
            }

            this.repository = new MessageRepository();

            this.restartListener = new RestartListener(this.repository, this);
            this.statusListener = new StatusListener(this.repository);
            this.xBandListener = new xBandListener(this.repository);
            this.xBandRequestListener = new xBandRequestListener(this.repository);

            this.xBandRequestPublisher = new JMS.xBandRequestPublisher(ConfigurationManager.AppSettings["ESB_Request_Topic"], repository);
            this.xBandPublisher = new JMS.xBandPublisher(ConfigurationManager.AppSettings["ESB_xBand_Topic"], repository);

            this.restartListener.Start();
            this.statusListener.Start();
            this.xBandListener.Start();
            this.xBandRequestListener.Start();
        }
    
        public void Start()
        {
            ThreadPool.QueueUserWorkItem((o) =>
            {
                Console.WriteLine("Webserver running...");
                try
                {
                    while (this.restartListener.IsListening &&
                           this.statusListener.IsListening &&
                           this.xBandListener.IsListening &&
                           this.xBandRequestListener.IsListening)
                    {
                        Thread.Sleep(1000);

                        if (this.repository.xBandRequestMessageState == Dto.SimulationState.Completed)
                        {
                            this.xBandRequestPublisher.Stop();
                            this.xBandPublisher.Start();
                        }
                    }
                }
                catch { } // suppress any exceptions
            });
        }

        public void Stop()
        {
            StopPublishing();
            this.restartListener.Stop();
            this.statusListener.Stop();
            this.xBandListener.Stop();
            this.xBandRequestListener.Stop();
        }

        public void StartPublishing()
        {
            this.xBandRequestPublisher.Start();
        }

        public void StopPublishing()
        {
            this.xBandRequestPublisher.Stop();
            this.xBandPublisher.Stop();
        }
    }
}
