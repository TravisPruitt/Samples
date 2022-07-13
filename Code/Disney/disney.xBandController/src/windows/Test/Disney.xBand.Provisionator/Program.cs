using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Provisionator
{
    class Program
    {
        static void Main(string[] args)
        {
            foreach (string arg in args)
            {
                switch (args[0])
                {
                    case "--bands":
                        {
                            if (args.Length == 2)
                            {
                                //Import bands from file.

                            }
                            else
                            {
                                //Show usage.
                            }
                            break;
                        }
                    case "--demo":
                        {
                            if (args.Length == 3)
                            {
                               

                            }
                            else
                            {
                                //Show usage.
                            }
                            break;
                        }
                }
            }

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
