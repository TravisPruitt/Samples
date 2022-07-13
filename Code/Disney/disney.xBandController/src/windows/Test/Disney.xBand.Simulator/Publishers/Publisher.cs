using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Simulator.Publishers
{
    public abstract class Publisher : IPublisher
    {
        protected System.Timers.Timer Timer { get; private set; }

        protected Dto.Reader Reader { get; private set; }

        protected Dto.Repositories.IReaderRepository ReaderRepository { get; private set; }

        protected string ControllerUrl { get; private set; }

        public Publisher(Dto.Reader reader, string controllerUrl, Dto.Repositories.IReaderRepository readerRepository, int interval)
        {
            this.Reader = reader;
            this.Timer = new System.Timers.Timer(interval);
            this.Timer.Elapsed += new System.Timers.ElapsedEventHandler(TimerElapsed);
            this.Timer.AutoReset = true;

            this.ReaderRepository = readerRepository;
            this.ControllerUrl = controllerUrl;
        }

        public void Start()
        {
            this.Timer.Enabled = true;
        }

        public void Stop()
        {
            this.Timer.Enabled = false;
        }

        protected abstract void TimerElapsed(object sender, System.Timers.ElapsedEventArgs e);
    }
}
