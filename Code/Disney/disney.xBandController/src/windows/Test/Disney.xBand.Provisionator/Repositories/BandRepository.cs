using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Data.SqlClient;

namespace Disney.xBand.Provisionator.Repositories
{
    public class BandRepository
    {
        public void Import(string filePath, bool invertTapID)
        {
            if (!File.Exists(filePath))
            {
                throw new FileNotFoundException("Import File nof found", filePath);
            }


            List<string> existingBands = new List<string>();
            List<string> errorBands = new List<string>();
            Dictionary<int,string> fileErrors = new Dictionary<int,string>();

            using (StreamReader fileReader = new StreamReader(File.OpenRead(filePath)))
            {
                using (Data.IDMSEntities context = new Data.IDMSEntities())
                {
                    string line = fileReader.ReadLine();
                    int lineNumber = 1;

                    while (line != null)
                    {
                        string[] bandData = line.Split(',');

                        if (bandData.Length == 3)
                        {
                            string bandID = bandData[0];
                            string longRangeID = bandData[1];
                            string tapID = bandData[2];

                            if (invertTapID)
                            {
                                tapID = tapID.Substring(13, 2) + 
                                    tapID.Substring(11, 2) + tapID.Substring(9, 2) + 
                                    tapID.Substring(7, 2) + tapID.Substring(5, 2) + 
                                    tapID.Substring(3, 2) + tapID.Substring(1, 2);
                            }
                            
                            //Check if exists
                            if (!context.xbands.Any(b => b.tapId == tapID && b.longRangeId == longRangeID && b.bandId == bandID))
                            {
                                try
                                {
                                    //Need to Add API Call???
                                    Data.xband band = new Data.xband()
                                    {
                                        bandId = bandData[0],
                                        longRangeId = bandData[1],
                                        tapId = tapID
                                    };

                                    context.AddToxbands(band);
                                    context.SaveChanges();
                                }
                                catch (SqlException ex)
                                {
                                    //Someting failed when saving data to database.
                                    Console.WriteLine("Error!!! Saving xband data failed with the following message: {0}", ex.Message);
                                    errorBands.Add(bandID);
                                }
                                catch (Exception ex)
                                {
                                    errorBands.Add(bandID);
                                }
                            }
                            else
                            {
                                existingBands.Add(bandID);
                            }

                        }
                        else
                        {
                            fileErrors.Add(lineNumber, line);
                        }

                        line = fileReader.ReadLine();
                        lineNumber++;
                    }
                }
            }

            if (errorBands.Count == 0 && existingBands.Count == 0 && fileErrors.Keys.Count == 0)
            {
                Console.WriteLine("Bands imported sucessfully");
            }

        }
    }
}
