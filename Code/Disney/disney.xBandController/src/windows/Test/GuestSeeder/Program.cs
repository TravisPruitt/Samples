using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
//using EM.xBMS;
using System.Net;
using System.Configuration;
using System.Runtime.Serialization.Json;
using System.Text.RegularExpressions;
using System.Globalization;
using System.Diagnostics;

namespace GuestSeeder
{
    class Program
    {
        static void Main(string[] args)
        {
            Stopwatch stopwatch = new Stopwatch();

            stopwatch.Start();

            List<string> firstNames = new List<string>();
            List<string> lastNames = new List<string>();

            //Create more users with common last names.
            int lastNameCount = 10;

            // Read the file as one string.
            using (StreamReader firstNamesReader =
               new StreamReader(File.OpenRead("..\\..\\SourceFiles\\FirstNames.txt")))
            {
                string firstName = firstNamesReader.ReadLine();

                while (firstName != null)
                {
                    if (!firstNames.Contains(firstName))
                    {
                        firstNames.Add(firstName);
                    }

                    firstName = firstNamesReader.ReadLine();
                }
            }

            // Read the file as one string.
            using (StreamReader lastNamesReader =
               new StreamReader(File.OpenRead("..\\..\\SourceFiles\\LastNames.txt")))
            {
                string lastName = lastNamesReader.ReadLine();

                while (lastName != null)
                {
                    for (int index = 0; index < lastNameCount; index++)
                    {
                        lastNames.Add(lastName);
                    }

                    lastName = lastNamesReader.ReadLine();
                    if (lastNameCount > 1)
                    {
                        lastNameCount--;
                    }

                }
            }

            Random random = new Random(DateTime.Now.Millisecond);

            for (int index = 0; index < 1000; index++)
            {
                try
                {
                    int firstNameIndex = random.Next(firstNames.Count);
                    int lastNameIndex = random.Next(lastNames.Count);

                    string firstName = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(firstNames[firstNameIndex].ToLower());
                    string lastName = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(lastNames[lastNameIndex].ToLower());

                    int lrID1 = random.Next();
                    int lrID2 = random.Next();
                    string lrID = string.Format("{0:X8}{1:X8}",lrID1,lrID2);

                    int tapID1 = random.Next();
                    int tapID2 = random.Next();
                    string tapID = string.Format("{0:X8}{1:X8}", tapID1, tapID2);

                    int bandID1 = random.Next();
                    int bandID2 = random.Next();
                    string bandID = string.Format("{0:X8}{1:X8}", bandID1, bandID2);

                    if (bandID.Length < 16)
                    {
                        Debug.WriteLine(String.Format("BandID: {0}",bandID));
                    }

                    if (tapID.Length < 16)
                    {
                        Debug.WriteLine(String.Format("TapID: {0}",tapID));
                    }

                    if (lrID.Length < 16)
                    {
                        Debug.WriteLine(String.Format("LRID: {0}", lrID));
                    }

                    using (MayhemEntities context = new MayhemEntities())
                    {

                        guest guest = new guest()
                        {
                            active = true,
                            createdBy = "simulator",
                            createdDate = DateTime.UtcNow,
                            firstName = firstName,
                            lastName = lastName,
                            updatedBy = "simulator",
                            updatedDate = DateTime.UtcNow,
                            xBMSId = 0
                        };

                        xband xband = new xband()
                        {
                            active = true,
                            bandId = bandID.ToLower(),
                            bandFriendlyName = String.Format("{0} {1}'s Band", firstName, lastName),
                            createdBy = "simulator",
                            createdDate = DateTime.UtcNow,
                            lRId = lrID.ToLower(),
                            tapId = tapID.ToLower(),
                            updatedBy = "simulator",
                            updatedDate = DateTime.UtcNow,
                            xBandId = 0
                        };

                        context.guests.AddObject(guest);
                        context.xbands.AddObject(xband);

                        context.SaveChanges();

                        guest_xband guestxband = new guest_xband()
                        {
                            guestId = guest.guestId,
                            xBandId = xband.xBandId,
                            createdBy = "simulator",
                            createdDate = DateTime.UtcNow,
                            updatedBy = "simulator",
                            updatedDate = DateTime.UtcNow
                        };

                        context.guest_xband.AddObject(guestxband);
                        context.SaveChanges();

                    }
                }
                catch (Exception ex)
                {
                    Debug.WriteLine("Error: {0}", ex.Message);
                }
            }


            Debug.WriteLine("Created 1000 random guests in {0} milliseconds.", new object[] { stopwatch.ElapsedMilliseconds });
        }
    }
#if  OLD
        static void Main_Save(string[] args)
        {
            List<string> firstNames = new List<string>();
            List<string> lastNames = new List<string>();

            //Create more users with common last names.
            int lastNameCount = 10;

            // Read the file as one string.
            using (StreamReader firstNamesReader =
               new StreamReader(File.OpenRead("..\\..\\SourceFiles\\FirstNames.txt")))
            {
                string firstName = firstNamesReader.ReadLine();

                while (firstName != null)
                {
                    if (!firstNames.Contains(firstName))
                    {
                        firstNames.Add(firstName);
                    }

                    firstName = firstNamesReader.ReadLine();
                }
            }

            // Read the file as one string.
            using (StreamReader lastNamesReader =
               new StreamReader(File.OpenRead("..\\..\\SourceFiles\\LastNames.txt")))
            {
                string lastName = lastNamesReader.ReadLine();

                while (lastName != null)
                {
                    for (int index = 0; index < lastNameCount; index++)
                    {
                        lastNames.Add(lastName);
                    }

                    lastName = lastNamesReader.ReadLine();
                    if (lastNameCount > 1)
                    {
                        lastNameCount--;
                    }

                }
            }


            DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(Guest));

            Random random = new Random(DateTime.Now.Millisecond);

            for (int index = 0; index < 500; index++)
            {
                int firstNameIndex = random.Next(firstNames.Count);
                int lastNameIndex = random.Next(lastNames.Count);

                string firstName = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(firstNames[firstNameIndex].ToLower());
                string lastName = CultureInfo.CurrentCulture.TextInfo.ToTitleCase(lastNames[lastNameIndex].ToLower());

                Guest guest = new Guest()
                {
                    Active = true,
                    CreatedBy = "simulator",
                    FirstName = firstName,
                    LastName = lastName,
                    UpdatedBy = "simulator",
                    xBMSID = "0"
                };

                
                int lrID1 = random.Next();
                int lrID2 = random.Next();
                string lrID = lrID1.ToString("X") + lrID2.ToString("X");

                int tapID1 = random.Next();
                int tapID2 = random.Next();
                string tapID = tapID1.ToString("X") + tapID2.ToString("X");

                xBand band = new xBand()
                {
                    Active = true,
                    BandID = "",
                    BandFriendlyName = String.Format("{0} {1}'s Band",firstName, lastName),
                    CreatedBy = "simulator",
                    lrID =  lrID.ToLower(),
                    TapID = tapID.ToLower(),
                    UpdatedBy = "simulator",
                    UpdatedDate = DateTime.UtcNow.ToString(),
                    xBandID = "0"
                };

                try
                {
                    string guestUrl = ConfigurationManager.AppSettings["GuestUrl"];

                    HttpWebRequest guestRequest = (HttpWebRequest)WebRequest.Create(guestUrl);
                    guestRequest.Method = "POST";
                    guestRequest.ContentType = "application/json; charset=utf-8";
                    guestRequest.Timeout = 5000;

                    using (MemoryStream ms = new MemoryStream())
                    {
                        ser.WriteObject(ms, guest);
                        String json = Encoding.UTF8.GetString(ms.ToArray());
                        //string json = "{\"active\":\"true\",\"createdBy\":\"simulator\",\"firstName\":\"" +
                        //    CultureInfo.CurrentCulture.TextInfo.ToTitleCase(firstNames[firstNameIndex].ToLower()) +
                        //    "\",\"guestId\":\"1\",\"lastName\":\"" +
                        //    CultureInfo.CurrentCulture.TextInfo.ToTitleCase(lastNames[lastNameIndex].ToLower()) +
                        //    "\",\"updatedBy\":\"simulator\",\"createdBy\":\"simulator\",\"XBMSId\":\"0\"}";
                        guestRequest.ContentLength = json.Length;
                        using (StreamWriter writer = new StreamWriter(guestRequest.GetRequestStream()))
                        {
                            writer.Write(json);
                        }
                    }

                    WebResponse response = guestRequest.GetResponse();

                    //TODO: Get the Guest ID Back??

                    response.Close();

                }
                catch (WebException ex)
                {
                    Debug.WriteLine("Error Message: {0}", ex.Message);
                }

                try
                {
                    string bandUrl = ConfigurationManager.AppSettings["BandUrl"];

                    HttpWebRequest bandRequest = (HttpWebRequest)WebRequest.Create(bandUrl);
                    bandRequest.Method = "POST";
                    bandRequest.ContentType = "application/json; charset=utf-8";
                    bandRequest.Timeout = 5000;

                    using (MemoryStream ms = new MemoryStream())
                    {
                        ser.WriteObject(ms, guest);
                        String json = Encoding.UTF8.GetString(ms.ToArray());
                        bandRequest.ContentLength = json.Length;
                        using (StreamWriter writer = new StreamWriter(bandRequest.GetRequestStream()))
                        {
                            writer.Write(json);
                        }
                    }

                    WebResponse response = bandRequest.GetResponse();

                    response.Close();

                }
                catch (WebException ex)
                {
                    Debug.WriteLine("Error Message: {0}", ex.Message);
                }
            }

        }
    }
#endif
}
