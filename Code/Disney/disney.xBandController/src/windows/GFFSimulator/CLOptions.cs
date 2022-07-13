using System;
using System.Collections.Generic;
using System.Text;

namespace GFFSimulator
{
    class CLOptions
    {
        public bool FastMode { get; set; }
        public string Broker { get; set; }
        public string User { get; set; }
        public string Password { get; set; }

        public CLOptions()
        {
            FastMode = true;
            Broker = "10.75.3.57:2506";
            User = "Administrator";
            Password = "Administrator";
        }

        public static CLOptions Parse(string[] args)
        {
            CLOptions clo = new CLOptions();

            for (int i = 0; i < args.Length; i++)
            {
                switch (args[i])
                {
                    case "-?":
                    case "--help":
                    {
                        Usage();
                        break;
                    }

                    case "--fast":
                    {
                        clo.FastMode = true;
                        break;
                    }

                    case "--broker":
                    {
                        if (i < (args.Length - 1))
                            clo.Broker = args[++i];
                        else
                        {
                            Console.Error.WriteLine("Invalid command line options");
                            Usage();
                        }
                        break;
                    }

                    case "--user":
                    {
                        if (i < (args.Length - 1))
                            clo.User = args[++i];
                        else
                        {
                            Console.Error.WriteLine("Invalid command line options");
                            Usage();
                        }
                        break;
                    }

                    case "--password":
                    {
                        if (i < (args.Length - 1))
                            clo.Password = args[++i];
                        else
                        {
                            Console.Error.WriteLine("Invalid command line options");
                            Usage();
                        }
                        break;
                    }

                    default:
                    {
                        Console.Error.WriteLine("Invalid command line options");
                        Usage();
                        break;
                    }
                }
            }

            return clo;
        }

        private static void Usage()
        {
            Console.WriteLine(
@"gffsimulator - Sends JMS messages simulating a day's worth of GFF activity
USAGE:
         gffsimulator OPTIONS

Where OPTIONS:

  --broker BROKERURL   Specifies the IP address:port of the JMS broker
  --user USER          Specifies the username for the JMS broker
  --password PASSWORD  Specifies the password for the JMS broker
  --fast               Send messages out immediately (default)"); 
        }

    }
}

