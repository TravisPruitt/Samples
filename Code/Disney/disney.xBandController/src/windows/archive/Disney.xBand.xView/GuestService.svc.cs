using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Web;
using System.Text;
using Disney.xBand.Data;
using System.Runtime.Serialization.Json;
using System.IO;
using System.ServiceModel.Activation;
using System.Net;
using System.Data.SqlTypes;
using System.Web.Script.Serialization;

namespace Disney.xBand.xView
{
    [AspNetCompatibilityRequirements(RequirementsMode = AspNetCompatibilityRequirementsMode.Allowed)]
    public class GuestService : IGuestService
    {
        public Stream GetLastKnowLocations(string id, string seconds)
        {
            int guestID;
            int testSeconds;

            if (!int.TryParse(id, out guestID))
            {
                    throw new WebFaultException(HttpStatusCode.BadRequest);

            }

            if (!int.TryParse(seconds, out testSeconds))
            {
                throw new WebFaultException(HttpStatusCode.BadRequest);

            }

            DateTime since = DateTime.Now.AddSeconds(-testSeconds);
            DateTime epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);

            if (testSeconds <= 0)
            {
                since = epoch;
            }

            using (EventsEntities context = new EventsEntities())
            {
                var temp = from e in context.Events
                         join a in context.Attractions on e.AttractionID equals a.AttractionID
                         join et in context.EventTypes on e.EventTypeID equals et.EventTypeID
                         where e.GuestID == id
                         && e.Timestamp >= since
                         orderby e.Timestamp descending
                         select new { a.AttractionName, et.EventTypeName, e.ReaderLocation, e.Timestamp };



                List<GuestLocation> results = (from x in temp.AsEnumerable()
                               select new GuestLocation()
                               {
                                   attractionname = x.AttractionName,
                                   eventtypename = x.EventTypeName,
                                   readerlocation = x.ReaderLocation,
                                   timestamp = Convert.ToInt64((x.Timestamp - epoch).TotalMilliseconds).ToString()
                               }).ToList();

                System.ServiceModel.Web.WebOperationContext.Current.OutgoingResponse.ContentType = "application/json; charset=utf-8";

                var s = new JavaScriptSerializer(); 
                string jsonClient = s.Serialize(results);
                NetDataContractSerializer s2 = new NetDataContractSerializer();

                return new MemoryStream(Encoding.UTF8.GetBytes(jsonClient)); 
             }
        }
    
       public Stream GetGuests()
        {
            using (EventsEntities context = new EventsEntities())
            {
                var temp = (from e in context.Events
                         join a in context.Attractions on e.AttractionID equals a.AttractionID
                         join et in context.EventTypes on e.EventTypeID equals et.EventTypeID
                         select new { e.GuestID }).Distinct().OrderBy(e => e.GuestID);

                var results = (from x in temp.AsEnumerable() 
                                select new Guest() 
                                { 
                                    guestid = x.GuestID.ToString()
                                }).ToList();

                System.ServiceModel.Web.WebOperationContext.Current.OutgoingResponse.ContentType = "application/json; charset=utf-8";

                var s = new JavaScriptSerializer();
                string jsonClient = s.Serialize(results);

                return new MemoryStream(Encoding.UTF8.GetBytes(jsonClient));
            }
        }
    }
}
