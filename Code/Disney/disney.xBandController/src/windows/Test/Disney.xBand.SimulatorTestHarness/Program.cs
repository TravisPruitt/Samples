using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;
using System.Threading;
using System.Diagnostics;
using Disney.xBand.Simulator;
using Disney.xBand.Simulator.Dto.Repositories;


namespace Disney.xBand.SimulatorTestHarness
{
    class Program
    {
        static void Main(string[] args)
        {
            DateTime simulationTime = DateTime.UtcNow;
            string attractionName = String.Empty;

            if (args.Length > 0)
            {
                attractionName = args[0];

                if (args.Length == 2)
                {
                    simulationTime = Convert.ToDateTime(args[1]);
                }

                //Simulation simulation = new Simulation(attractionName, simulationTime);

                //simulation.Start();
            }
        }

    }

}
