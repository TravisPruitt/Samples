using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.xBMS.Simulator.JMS
{
    public class SimulatorExceptionListener : Sonic.Jms.ExceptionListener
    {
        private Publisher publisher;

        public SimulatorExceptionListener(Publisher publisher)
        {
            this.publisher = publisher;
        }

        public void onException(Sonic.Jms.JMSException exception)
        {
            this.publisher.Stop();
        }
    }
}
