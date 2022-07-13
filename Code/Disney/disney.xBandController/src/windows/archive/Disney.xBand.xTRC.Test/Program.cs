using System;
using System.Collections.Generic;
using System.Text;
using System.Threading;
using System.IO;

namespace Disney.xBand.xTRC.Test
{
    class Program
    {
        static void Main(string[] args)
        {
            xBRC xbrcEntry = new xBRC(@"c:\xtrcEntry");
//            xBRC xbrcMerge = new xBRC(@"c:\xtrcMerge");
            Random rand = new Random();

            Queue<string> atEntry = new Queue<string>();

            int numberOfCalls = int.Parse(args[0]);
            int calls = 0;

            using (StreamReader sr = File.OpenText("SourceFiles\\Bands.csv"))
            {
                String input = sr.ReadLine();

                //Skip header line
                input = sr.ReadLine();
                while (input != null && calls < numberOfCalls)
                {
                    string[] fields = input.Split(',');

                    string xbandId = fields[0];
                    string bandId = fields[1];
                    string longRangeId = fields[2];
                    string tapId = fields[3];
                    string secureId = fields[4];

                    try
                    {
                        //Tap a few at entry
                        xbrcEntry.SendTap(secureId,tapId);
                        atEntry.Enqueue(secureId);
                    }
                    catch (Exception)
                    {
                        //Ignore
                    }

/*                    if (atEntry.Count > 10)
                    {
                        //Do a random number of taps at merge.
                        int count = rand.Next(5);

                        for (int index=0; index < count; index++)
                        {
                            //Tap then sleep 2 seconds
                            xbrcMerge.SendTap(atEntry.Dequeue());

                            Thread.Sleep(2000);
                        }
                    }*/

                    //Make sure next entry tap doesn't happen for at least two seconds.
                    Thread.Sleep(2000);
                    input = sr.ReadLine();
                }
            }
        }
    }
}
