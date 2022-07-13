using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Xml.Serialization;
using System.Xml;

namespace GFFSimulator
{
    class Program
    {
        static void Main(string[] args)
        {
            CLOptions clo = CLOptions.Parse(args);
            Simulator tor = new Simulator();
            Sender send = new Sender(clo);

            // generate a day's worth of events
            List<businessEvent> liEvents = tor.GenerateEvents();

            // now send these out in a paced fashion
            
            send.Send(liEvents, clo);
        }
    }
}
