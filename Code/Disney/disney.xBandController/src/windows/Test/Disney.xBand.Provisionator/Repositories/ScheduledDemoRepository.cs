using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;
using Oracle.DataAccess.Client;
using System.Configuration;
using System.Net;
using System.Runtime.Serialization.Json;
using System.IO;
using System.Data;
using System.Diagnostics;
using System.Data.Common;

namespace Disney.xBand.Provisionator.Repositories
{
    public interface IScheduledDemoRepository
    {
        List<Dto.Demo> GetScheduledDemos();
        Dto.Demo GetScheduledDemo(int scheduledDemoID);
        List<Dto.DemoGuest> GetGuests(int scheduledDemoID);
        Dto.AddDemoEntitlementsResult AddGuests(int scheduledDemoID, int guestsPerOfferset);
        void RemoveGuests(int scheduledDemoID);
        void ResetInventory(TimeSpan inventoryTime);
    }

    public class ScheduledDemoRepository : IScheduledDemoRepository
    {
        private string gxpOffersetUrl;
        private string gxpBookOffersetUrlFormat;

        static ScheduledDemoRepository()
        {
            Mapper.Initialize(cfg =>
            {
                cfg.CreateMap<Data.demo, Dto.Demo>()
                    .ForMember(dest => dest.DemoID, opt => opt.MapFrom(src => src.demoId))
                    .ForMember(dest => dest.DemoDescription, opt => opt.MapFrom(src => src.demoDescription))
                    .ForMember(dest => dest.DemoOrder, opt => opt.MapFrom(src => src.demoOrder))
                    .ForMember(dest => dest.ScheduledTime, opt => opt.MapFrom(src => src.scheduledTime));
                cfg.CreateMap<Data.GetGuests_Result, Dto.DemoGuest>()
                    .ForMember(dest => dest.GuestID, opt => opt.MapFrom(src => src.guestid))
                    .ForMember(dest => dest.LastName, opt => opt.MapFrom(src => src.Lastname))
                    .ForMember(dest => dest.FirstName, opt => opt.MapFrom(src => src.FirstName))
                    .ForMember(dest => dest.DemoID, opt => opt.MapFrom(src => src.demoid))
                    .ForMember(dest => dest.xBandID, opt => opt.MapFrom(src => src.xbandid))
                    .ForMember(dest => dest.xID, opt => opt.MapFrom(src => src.xid))
                    .ForMember(dest => dest.GxpLinkID, opt => opt.MapFrom(src => src.gxplinkid))
                    .ForMember(dest => dest.TapID, opt => opt.MapFrom(src => src.tapid))
                    .ForMember(dest => dest.BandID, opt => opt.MapFrom(src => src.bandid))
                    .ForMember(dest => dest.IsLeadGuest, opt => opt.MapFrom(src => src.IsLeadGuest));

            });

            Mapper.AssertConfigurationIsValid();
        }

        public ScheduledDemoRepository()
        {
           this.gxpOffersetUrl = ConfigurationManager.AppSettings["GxpOffersetUrl"];
           this.gxpBookOffersetUrlFormat = ConfigurationManager.AppSettings["GxpBookOffersetUrl"] + "/{0}";
        }
 
        public List<Dto.Demo> GetScheduledDemos()
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                var result = (from g in context.demos
                              select g).OrderBy(g => g.demoOrder).ToList();

                return Mapper.Map<List<Data.demo>, List<Dto.Demo>>(result);
            }
        }

        public Dto.Demo GetScheduledDemo(int demoid)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                var result = (from g in context.demos
                              where g.demoId == demoid
                              select g).Single();

                return Mapper.Map<Data.demo, Dto.Demo>(result);
            }
        }

        public List<Dto.DemoGuest> GetGuests(int scheduledDemoID)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                var result = context.GetGuests(scheduledDemoID).ToList();

                return Mapper.Map<List<Data.GetGuests_Result>, List<Dto.DemoGuest>>(result);
            }
        }

        public void RemoveGuests(int demoId)
        {
            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {
                //Get the guest data to delete
                var result = context.GetGuests(demoId).ToList();

                var walkthroughGuests = Mapper.Map<List<Data.GetGuests_Result>, List<Dto.DemoGuest>>(result);

                var walkthroughData = (from w in context.demos
                                       where w.demoId == demoId
                                       select w).Single();

                Dto.Demo walkthrough = Mapper.Map<Data.demo, Dto.Demo>(walkthroughData);

                DeleteDapEntitlements(walkthroughGuests);

                DeleteSorEntitlements(walkthroughGuests);

                context.RemoveDemo(demoId);
            }
        }

        public Dto.AddDemoEntitlementsResult AddGuests(int demoID, int guestsPerOfferset)
        {
            Dto.AddDemoEntitlementsResult response = new Dto.AddDemoEntitlementsResult();

            using (Data.IDMSEntities context = new Data.IDMSEntities())
            {

                var walkthroughData = (from w in context.demos
                                       where w.demoId == demoID
                                       select w).Single();

                Dto.Demo walkthrough = Mapper.Map<Data.demo, Dto.Demo>(walkthroughData);

                context.SetupDemo(demoID);

                //Get the guest data to update
                var result = context.GetGuests(demoID).ToList();

                var allDemoGuests = Mapper.Map<List<Data.GetGuests_Result>, List<Dto.DemoGuest>>(result);

                int startIndex = 0;
                int endIndex = guestsPerOfferset - 1;

                ResetInventory(walkthroughData.scheduledTime);

                List<Dto.DemoGuest> offersetGuests = new List<Dto.DemoGuest>();

                do
                {
                    offersetGuests.Clear();

                    for (int index = startIndex; index <= endIndex; index++)
                    {
                        offersetGuests.Add(allDemoGuests[index]);
                    }

                    DeleteDapEntitlements(offersetGuests);

                    DeleteSorEntitlements(offersetGuests);

                    response.GenerateOffersetResult = GetOfferSetID(offersetGuests, walkthrough, walkthroughData.scheduledTime);

                    if (response.GenerateOffersetResult.Status == Dto.OffersetStatus.Success)
                    {
                        response.BookOffersetResult = BookOfferset(response.GenerateOffersetResult.OffersetID);
                    }
                    else
                    {
                        response.BookOffersetResult = new Dto.BookOffersetResult()
                        {
                            OffersetID = 0,
                            Status = Dto.OffersetStatus.NoOffersetFound,
                            Message = "No Offerset for valid time period received from GXP"
                        };
                    }

                    endIndex += guestsPerOfferset;
                    startIndex += guestsPerOfferset;

                    if ((endIndex >= allDemoGuests.Count))
                    {
                        endIndex = allDemoGuests.Count - 1;
                    }
                   
                }
                while (startIndex < allDemoGuests.Count && response.Successful);

            }

            return response;
        }

        private void DeleteDapEntitlements(List<Dto.DemoGuest> walkthroughGuests)
        {
            StringBuilder bandIDs = new StringBuilder();
            bool first = true;

            foreach (Dto.DemoGuest walkthroughGuest in walkthroughGuests)
            {
                if (first)
                {
                    bandIDs.AppendFormat("'{0}'", walkthroughGuest.BandID);
                    first = false;
                }
                else
                {
                    bandIDs.AppendFormat(",'{0}'", walkthroughGuest.BandID);
                }
            }

            //Delete entitlements and cache
            if (bandIDs.Length > 0)
            {
                int numberOfRowsAffected = 0;
                StringBuilder commandText = new StringBuilder();

                commandText.Append("BEGIN ");
                commandText.AppendFormat("delete from xpass_actvy_log where cache_xpass_enttl_id IN (select cache_xpass_enttl_id from cache_xpass_enttl where exprnc_band_id IN ( {0} ) );", bandIDs.ToString());
                commandText.AppendLine();

                commandText.AppendFormat("delete from cache_xpass_enttl where exprnc_band_id IN ( {0} );", bandIDs.ToString());
                commandText.AppendLine();
                commandText.Append("END;");
                numberOfRowsAffected = ExecuteOracleCommand(commandText.ToString());
            }
        }

        private void DeleteSorEntitlements(List<Dto.DemoGuest> walkthroughGuests)
        {
            StringBuilder gxpLinkIDs = new StringBuilder();
            bool first = true;

            foreach (Dto.DemoGuest walkthroughGuest in walkthroughGuests)
            {
                if (!String.IsNullOrEmpty(walkthroughGuest.GxpLinkID))
                {
                    if (first)
                    {
                        gxpLinkIDs.AppendFormat("'{0}'", walkthroughGuest.GxpLinkID);
                        first = false;
                    }
                    else
                    {
                        gxpLinkIDs.AppendFormat(",'{0}'", walkthroughGuest.GxpLinkID);
                    }
                }
            }

            //Delete entitlements at SOR (by gxp-link-id)
            if (gxpLinkIDs.Length > 0)
            {
                StringBuilder commandText = new StringBuilder();
                int numberOfRowsAffected = 0;

                commandText.Append("BEGIN ");
                commandText.AppendFormat("delete from enttl_chng_req where xpass_enttl_id IN ( select xpass_enttl_id from xpass_enttl where gxp_lnk_id IN ( {0} ) );", gxpLinkIDs.ToString());
                commandText.AppendLine();
                commandText.AppendFormat("delete from xpass_enttl_actvy where xpass_enttl_id IN ( select xpass_enttl_id from xpass_enttl where gxp_lnk_id IN ( {0} ) );", gxpLinkIDs.ToString());
                commandText.AppendLine();
                commandText.AppendFormat("delete from xpass_enttl where gxp_lnk_id IN ( {0} );", gxpLinkIDs.ToString());
                commandText.AppendLine();
                commandText.Append("END;");

                numberOfRowsAffected = ExecuteOracleCommand(commandText.ToString());
            }
        }

        private int ExecuteOracleCommand(string commandText)
        {
            //Update to close all the inventory for Buzz
            using (OracleConnection con = new OracleConnection(ConfigurationManager.ConnectionStrings["GXP"].ConnectionString))
            {
                int numberOfRowsAffected = 0;
                con.Open();

                OracleCommand cmd = con.CreateCommand();
                cmd.CommandType = CommandType.Text;
                cmd.CommandText = commandText;
                cmd.CommandTimeout = 10;

                try
                {
                    numberOfRowsAffected = cmd.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    Debug.WriteLine("Error executing Oracle Command: {0}", ex.Message);
                    numberOfRowsAffected = - 1;
                }

                return numberOfRowsAffected;
            }
        }

        public void ResetInventory(TimeSpan inventoryTime)
        {
            int result = 0;
            StringBuilder commandText = new StringBuilder();
            string currentDate = DateTime.Now.Date.ToString("yyyy-MM-dd");

            string startTime = DateTime.Now.Date.Add(inventoryTime).ToString("yyyy-MM-dd:HH:mm");
            string endTime = DateTime.Now.Date.Add(inventoryTime).AddHours(2).AddMinutes(-1).ToString("yyyy-MM-dd:HH:mm");
            
            commandText.Append("BEGIN ");
            commandText.AppendLine();
            //Update to close all the inventory for Buzz
            commandText.AppendFormat("update xpass_invtry set xpass_invtry_bk_cn = xpass_invtry_max_avail_cn where fac_oper_dt = TO_DATE('{0}', 'YYYY-MM-DD') AND gxp_entrtn_id = 80010114; ", currentDate);
            commandText.AppendLine();
 
            //Update to open up the blocks we want to make inventory available for Buzz
            commandText.AppendFormat("update xpass_invtry set xpass_invtry_bk_cn = 0 where gxp_entrtn_id = 80010114 AND fac_oper_dt = TO_DATE('{0}', 'YYYY-MM-DD') AND( ( xpass_invtry_rtrn_strt_dts BETWEEN TO_DATE('{1}', 'YYYY-MM-DD HH24:MI') AND TO_DATE('{2}', 'YYYY-MM-DD HH24:MI') ) ); ", currentDate, startTime, endTime);
            commandText.AppendLine();
            commandText.Append("END;");
            result = ExecuteOracleCommand(commandText.ToString());
        }

        private Dto.GenerateOffersetResult GetOfferSetID(List<Dto.DemoGuest> walkthroughGuests, Dto.Demo walkthrough, TimeSpan scheduledDemoTime)
        {
            Dto.GenerateOffersetResult result = new Dto.GenerateOffersetResult();

            int offersetID = 0;
            List<string> xIDs = new List<string>();
            try
            {


                foreach (Dto.DemoGuest demoGuest in walkthroughGuests)
                {
                    if (!String.IsNullOrEmpty(demoGuest.xID) && !demoGuest.IsLeadGuest)
                    {
                        xIDs.Add(demoGuest.xID);
                    }
                }

                if (xIDs.Count > 0)
                {
                    //Generate Offerset by xID
                    WebRequest offersetWebRequest = WebRequest.Create(this.gxpOffersetUrl);
                    offersetWebRequest.Method = "POST";

                    Dto.OffersetRequest offersetRequest = new Dto.OffersetRequest()
                    {
                        ScheduleDate = DateTime.Now.Date.ToString("yyyy-MM-dd"),
                        ParkID = "80007944",
                        PreferredEntertainments = new int[] { 80010114, 90002635, 80010177, 80010149 },
                        RequestingXID = xIDs[0],
                        XIDs = xIDs.ToArray()
                    };

                    DataContractJsonSerializer serializer = new DataContractJsonSerializer(typeof(Dto.OffersetRequest));
                    MemoryStream ms = new MemoryStream();
                    serializer.WriteObject(ms, offersetRequest);

                    string json = Encoding.Default.GetString(ms.ToArray());
                    ms.Close();
                    byte[] byteArray = Encoding.UTF8.GetBytes(json);

                    offersetWebRequest.ContentType = "application/json; charset=utf-8";
                    offersetWebRequest.ContentLength = byteArray.Length;
                    Stream dataStream = offersetWebRequest.GetRequestStream();
                    dataStream.Write(byteArray, 0, byteArray.Length);
                    dataStream.Close();

                    using (HttpWebResponse offersetWebResponse = offersetWebRequest.GetResponse() as HttpWebResponse)
                    {
                        if (offersetWebResponse.StatusCode == HttpStatusCode.OK)
                        {
                            using (StreamReader reader = new StreamReader(offersetWebResponse.GetResponseStream()))
                            {

                                string responseJson = reader.ReadToEnd();

                                using (MemoryStream memStream = new MemoryStream(Encoding.UTF8.GetBytes(responseJson)))
                                {

                                    // deserialize the response 
                                    DataContractJsonSerializer deserializer = new DataContractJsonSerializer(typeof(Dto.OffersetResponse));
                                    Dto.OffersetResponse offersetResponse = deserializer.ReadObject(memStream) as Dto.OffersetResponse;

                                    foreach (Dto.Offerset offerset in offersetResponse.Offerset)
                                    {
                                        if (offerset.OffersetAppointments.Appointments != null)
                                        {
                                            foreach (Dto.Appointment appointment in offerset.OffersetAppointments.Appointments)
                                            {
                                                if (appointment.EntertainmentID == 80010114)
                                                {
                                                    DateTime startTime = DateTime.Parse(appointment.ReturnWindow.StartTime);
                                                    DateTime endTime = DateTime.Parse(appointment.ReturnWindow.EndTime);

                                                    TimeSpan endTimeSpan = endTime.AddHours(2).TimeOfDay;

                                                    if (endTimeSpan.Hours < 9)
                                                    {
                                                        endTimeSpan = new TimeSpan(23, 59, 0);
                                                    }

                                                    if (scheduledDemoTime >= startTime.AddHours(-2).TimeOfDay &&
                                                        scheduledDemoTime <= endTimeSpan)
                                                    {
                                                        offersetID = offerset.OffersetAppointments.OfferSetID;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (offersetID > 0)
                                            {
                                                break;
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (offersetID > 0)
                {
                    result.OffersetID = offersetID;
                }
                else
                {
                    result.Status = Dto.OffersetStatus.NoOffersetFound;
                    result.Message = "No offerset found.";
                }
            }
            catch (Exception ex)
            {
                result.Status = Dto.OffersetStatus.CallFailed;
                StringBuilder error = new StringBuilder();
                Exception ex1 = ex;

                while (ex1 != null)
                {
                    error.Append(ex.Message);
                    error.AppendLine();
                    ex1 = ex.InnerException;
                }
            }

            return result;
        }

        private Dto.BookOffersetResult BookOfferset(int offersetID)
        {
            Dto.BookOffersetResult result = new Dto.BookOffersetResult();

            try
            {
                WebRequest webRequest = WebRequest.Create(String.Format(this.gxpBookOffersetUrlFormat, offersetID));
                webRequest.Method = "POST";

                string json = "{\"tradeDatesMap\":[]}";//Encoding.Default.GetString(ms.ToArray());
                byte[] byteArray = Encoding.UTF8.GetBytes(json);

                webRequest.ContentType = "application/json; charset=utf-8";
                webRequest.ContentLength = byteArray.Length;
                Stream dataStream = webRequest.GetRequestStream();
                dataStream.Write(byteArray, 0, byteArray.Length);
                dataStream.Close();

                HttpWebResponse offersetWebResponse = webRequest.GetResponse() as HttpWebResponse;

                if (offersetWebResponse.StatusCode == HttpStatusCode.OK)
                {
                    result.Status = Dto.OffersetStatus.Success;
                    result.Message = "Offerset successfully selected.";
                }
                else
                {
                    result.Status = Dto.OffersetStatus.CallFailed;
                    result.Message = offersetWebResponse.StatusDescription;

                }
            }
            catch (System.Net.WebException ex)
            {
                result.Status = Dto.OffersetStatus.CommunicationsError;
                StringBuilder error = new StringBuilder();
                Exception ex1 = ex;

                while (ex1 != null)
                {
                    error.Append(ex.Message);
                    error.AppendLine();
                    ex1 = ex.InnerException;
                }
            }

            return result;
        }
    }
}
