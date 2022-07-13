using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.xBMS.Simulator.Dto;
using System.IO;
using System.Threading.Tasks;
using System.Threading;
using Disney.xBand.xBMS.Simulator.Listeners;
using System.Configuration;
using Disney.xBand.xBMS.Simulator.JMS;
using log4net.Config;

namespace Disney.xBand.xBMS.Simulator
{
    class Program
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        // Worker thread to publisher to JMS messages.
        private static CancellationTokenSource tokenSource = new CancellationTokenSource();
        
        static void Main(string[] args)
        {
            // Set up a simple configuration that logs on the console.
            log4net.Config.XmlConfigurator.Configure();
            
            log.Info("Starting application");

            WebServer webServer = new WebServer();
            webServer.Start();

            var tokenSource2 = new CancellationTokenSource();
            CancellationToken ct = tokenSource2.Token;

            var task = Task.Factory.StartNew(() =>
            {
                // Were we already canceled?
                ct.ThrowIfCancellationRequested();

                bool moreToDo = true;
                while (moreToDo)
                {
                    if (ct.IsCancellationRequested)
                    {
                        webServer.Stop();
                        moreToDo = false;
                    }

                    Thread.Sleep(1000);

                }
            }, tokenSource2.Token); // Pass same token to StartNew.

            try
            {
                task.Wait();
            }
            catch (AggregateException e)
            {
                foreach (var v in e.InnerExceptions)
                    Console.WriteLine(e.Message + " " + v.Message);
            }

            Console.WriteLine("Press any key to close application");
            Console.ReadKey();

        }
    }
}
