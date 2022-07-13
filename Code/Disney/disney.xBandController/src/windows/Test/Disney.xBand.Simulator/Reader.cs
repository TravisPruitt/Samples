using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using Disney.xBand.Simulator.Listeners;
using Disney.xBand.Simulator.Publishers;
using System.Threading;


namespace Disney.xBand.Simulator
{
    public class Reader
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);
        
        private HelloPublisher helloPublisher;

        private EventPublisher eventPublisher;

        private List<IListener> listeners;

        private CancellationTokenSource tokenSource;

        private Dto.Reader reader;

        static Reader()
        {
            log4net.Config.XmlConfigurator.Configure(); 
        }

        public Reader(Dto.Reader reader, string controllerUrl, Dto.Repositories.IReaderRepository readerRepository, CancellationTokenSource tokenSource)
        {
            this.reader = reader;
            this.tokenSource = tokenSource;

            this.helloPublisher = new HelloPublisher(reader, controllerUrl, readerRepository);
            this.helloPublisher.StartStream += new EventHandler(StartEvents);

            this.eventPublisher = new EventPublisher(reader, controllerUrl, readerRepository);

            this.listeners = new List<IListener>();

            UpdateListener updateListener = new UpdateListener(reader, readerRepository);
            updateListener.StreamInfoUpdated += new EventHandler<StreamInfoUpdatedEventArgs>(this.eventPublisher.StreamInfoUpdated);
            this.listeners.Add(updateListener);
            this.listeners.Add(new StatusListener(reader, readerRepository));
            this.listeners.Add(new EventsListener(reader, readerRepository));
            this.listeners.Add(new ReaderNameListener(reader, readerRepository));
        }

        public void StartEvents(object sender, EventArgs e)
        {
            this.eventPublisher.Start();
        }

        public void Start()
        {
            foreach (IListener listener in this.listeners)
            {
                listener.Start();
            }

            this.helloPublisher.Start();

            //if (!String.IsNullOrEmpty(this.reader.UpstreamUrl))
            //{
            //    this.eventPublisher.Start();
            //}
            
            while (true)
            {
                //Wait a second.
                Thread.Sleep(1000);

                if (this.tokenSource.IsCancellationRequested)
                {
                    Stop();
                }
            }
        }

        public void Stop()
        {
            foreach (IListener listener in this.listeners)
            {
                listener.Stop();
            }

            this.helloPublisher.Stop();

            this.eventPublisher.Stop();
        }

    }

}
