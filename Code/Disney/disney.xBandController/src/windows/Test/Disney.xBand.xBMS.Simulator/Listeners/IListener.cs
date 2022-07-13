using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.xBMS.Simulator.Listeners
{
    public interface IListener
    {
        void Start();
        void Stop();

        Boolean IsListening { get; }
    }
}
