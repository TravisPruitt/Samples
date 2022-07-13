using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Xml;

namespace GFFSimulator
{
    class Simulator : IComparer<businessEvent>
    {
        private const string sFacilityOneSourceId = "80007936";
        private const int uiTerminal = 611;
        private const string sReferenceId = "80007936-20120623192048-201237251737";

        private const int nOpeningHour = 10 + 5;    // 10am EST = 15 GMT
        private const int nHoursOpen = 11;          // open for 11 hours

        // control constants
        private const int nFastpassPerHour = 40;
        private const int nRockStarPerHour = 20;
        private const int nStandbyPerHour = 100;
        private const int nAveragePartySize = 4;
        private const double stdDevPartySize = 1.2;

        private const int secStandbyWaitTime = 10 * 60;     // 10 min
        private const int secFastpassWaitTime = 1 * 60;     // 1 min
        private const int secOrderTime = 3 * 60;            // 3
        private const int secSeatTime = 1 * 60;             // 1
        private const int secWaitForFoodTime = 3 * 60;      // 3
        private const int secEatTime = 20 * 60;             // 20
        private const int secCleanup = 1 * 60;              // 1

        private Random rand = new Random();

        // list of events
        private List<businessEvent> liEvents = new List<businessEvent>();

        // allocated guest count
        private int iNextGuest = 0;

        // table manager
        private TableManager tm = new TableManager();

        public Simulator()
        {
            for (int i = 1; i <= 17; i++)
                tm.AddTable("Table" + i.ToString(), 2);

            for (int i=18; i<=22; i++)
                tm.AddTable("Table" + i.ToString(), 6);

            for (int i = 23; i <= 32; i++)
                tm.AddTable("Table" + i.ToString(), 4);

            for (int i = 33; i <= 37; i++)
                tm.AddTable("Table" + i.ToString(), 8);

            for (int i = 38; i <= 40; i++)
                tm.AddTable("Table" + i.ToString(), 4);
        }

        public List<businessEvent> GenerateEvents()
        {

            // schedule guest activity
            DateTime dtBase = new DateTime(DateTime.Now.Year, DateTime.Now.Month, DateTime.Now.Day, nOpeningHour, 0, 0);
            for (int iHour = 0; iHour < nHoursOpen; iHour++)
            {
                // calculate current hour
                DateTime dtHour = dtBase + TimeSpan.FromHours(iHour);

                // guest count
                int cGuestTotal;
                long cmsecPeriod;

                // schedule rock star guests
                cGuestTotal = 0;
                cmsecPeriod = (3600 * 1000) / nRockStarPerHour;
                for (int ig = 0; ig < nRockStarPerHour; )
                {
                    // determine party size
                    int cGuests = CalculatePartySize();
                    if (ScheduleGuests(true, true, dtHour + TimeSpan.FromMilliseconds(ig * cmsecPeriod), cGuests))
                        cGuestTotal += cGuests;
                    ig += cGuests;
                }

                if (cGuestTotal < nRockStarPerHour)
                    Console.WriteLine("Could only schedule " + cGuestTotal + " rockstar guests out of " + nRockStarPerHour + " at " + iHour);

                // schedule fastpass guests
                cGuestTotal = 0;
                cmsecPeriod = (3600 * 1000) / nFastpassPerHour;
                for (int ig = 0; ig < nFastpassPerHour; )
                {
                    // determine party size
                    int cGuests = CalculatePartySize();
                    if (ScheduleGuests(true, false, dtHour + TimeSpan.FromMilliseconds(ig * cmsecPeriod), cGuests))
                        cGuestTotal += cGuests;
                    ig += cGuests;
                }

                if (cGuestTotal < nFastpassPerHour)
                    Console.WriteLine("Could only schedule " + cGuestTotal + " fastpass guests out of " + nFastpassPerHour + " at " + iHour);

                // schedule standby guests
                cGuestTotal = 0;
                cmsecPeriod = (3600 * 1000) / nStandbyPerHour;
                for (int ig=0; ig < nStandbyPerHour;)
                {
                    // determine party size
                    int cGuests = CalculatePartySize();
                    if (ScheduleGuests(false, false, dtHour + TimeSpan.FromMilliseconds(ig * cmsecPeriod), cGuests))
                        cGuestTotal += cGuests;
                    ig += cGuests;
                }

                if (cGuestTotal < nStandbyPerHour)
                    Console.WriteLine("Could only schedule " + cGuestTotal + " standby guests out of " + nStandbyPerHour + " at " + iHour);
                
            }

            // sort
            liEvents.Sort(this);

            return liEvents;

        }

        private int CalculatePartySize()
        {
            return (int)GetNormal((double)nAveragePartySize, stdDevPartySize) + 1;
        }

        private int CalculateTableSize(int cGuests)
        {
            if (cGuests <= 2)
                return 2;
            else if (cGuests <= 4)
                return 4;
            else if (cGuests <= 6)
                return 6;
            else
                return 8;
        }

        private bool ScheduleGuests(bool bFastpass, bool bPreOrder, DateTime dt, int cGuests)
        {
            DateTime dtEntry;
            DateTime dtMerge;
            DateTime dtOrder;
            DateTime dtSeated;
            DateTime dtFoodDelivery;
            DateTime dtLeaveTable;
            DateTime dtCleanup;

            // Calculate event times
            dtEntry = dt;

            if (!bPreOrder)
            {
                if (bFastpass)
                {
                    dtMerge = dtEntry + TimeSpan.FromSeconds(secFastpassWaitTime);
                }
                else
                {
                    dtMerge = dtEntry + TimeSpan.FromSeconds(secStandbyWaitTime);
                }
                dtOrder = dtMerge + TimeSpan.FromSeconds(secOrderTime);
                dtSeated = dtOrder + TimeSpan.FromSeconds(secSeatTime);
            }
            else
            {
                dtMerge = DateTime.MinValue;
                dtOrder = dtEntry;
                dtSeated = dtEntry + TimeSpan.FromSeconds(secSeatTime);
            }

            // make sure a table will be available for this party size. If not, skip the group
            Table t = tm.FindAvailableTable(dtSeated, CalculateTableSize(cGuests));
            if (t == null)
                return false;
            
            // calculate the rest of the times
            dtFoodDelivery = dtOrder + TimeSpan.FromSeconds(secWaitForFoodTime);
            dtLeaveTable = dtFoodDelivery + TimeSpan.FromSeconds(secEatTime);
            dtCleanup = dtLeaveTable + TimeSpan.FromSeconds(secCleanup);

            // reserve the table
            t.Occupy(dtLeaveTable);

            // select the primary and secondary guests
            string[] aguests = new string[cGuests];
            for (int ig = 0; ig < cGuests; ig++)
            {
                aguests[ig] = AllocateGuest();
            }


            // generate order number and course number
            int nOrder = rand.Next();
            int nCourse = rand.Next(1,100);

            // generate entry events
            businessEvent be = null;
            for (int ig = 0; ig < cGuests; ig++)
            {
                if (bFastpass)
                    be = GenerateGuestTappedAtMobileDAPMessage(dtEntry, nOrder.ToString(), aguests[ig]);
                else
                    be = GenerateGuestDetectStandbyMessage(dtEntry, nOrder.ToString(), aguests[ig]);
                liEvents.Add(be);
            }

            // patch the envelope timestamp so it's not sent until the OrderPlaced message
            be.timestamp = FormatTime(dtOrder);

            // generate merge event (only for lead guest)
            if (!bPreOrder)
            {
                be = GenerateGuestTapMergeMessage(dtMerge, nOrder.ToString(), aguests[0], cGuests);
                liEvents.Add(be);
            }

            // patch the envelope timestamp so it's not sent until the OrderPlaced message
            be.timestamp = FormatTime(dtOrder);

            // generate the tap at POS message
            be = GenerateTappedAtPosMessage(dtOrder, nOrder.ToString(), aguests[0], cGuests);
            liEvents.Add(be);

            // patch the envelope timestamp so it's not sent until the OrderPlaced message
            be.timestamp = FormatTime(dtOrder);

            // generate order
            be = GenerateOrderPlacedMessage(dtOrder, nOrder.ToString(), nCourse.ToString(), aguests);
            liEvents.Add(be);

            // generate order received
            be = GenerateOrderReceivedMessage(dtOrder, nOrder.ToString(), nCourse.ToString());
            liEvents.Add(be);

            // generate seating
            string sLocation = rand.Next().ToString();
            string sTableId = t.Name;
            string sTableName = sTableId;
            for (int ig = 0; ig < aguests.Length; ig++)
            {
                be = GenerateTableGuestDetectedMessage(dtSeated, aguests[ig], sLocation, sTableId, sTableName, new string[] { nOrder.ToString() });
                liEvents.Add(be);
            }

            // generate food delivery
            be = GenerateOrderBumpedMessage(dtFoodDelivery, nOrder.ToString());
            liEvents.Add(be);
            be = GenerateOrderClearedMessage(dtFoodDelivery, nOrder.ToString(), nCourse.ToString());
            liEvents.Add(be);

            // departure
            be = GenerateTableDirtyMessage(dtLeaveTable, sLocation, sTableId, sTableName, new string[] { nOrder.ToString() }, "Runner");
            liEvents.Add(be);

            // cleanup
            be = GenerateTableCleanedMessage(dtCleanup, sLocation, sTableId, sTableName, new string[] { nOrder.ToString() }, "Runner");
            liEvents.Add(be);

            return true;

        }

        private businessEvent GenerateTappedAtPosMessage(DateTime dtOrder, string sOrder, string sXBandId, int cPartySize)
        {
            tapevent te = GenerateTapEvent(dtOrder, "Guest Tapped at POS", "4", false, sOrder, "POS", "Fixed", null, sXBandId, "111" + sXBandId, cPartySize.ToString());
            return PlaceInBusinessEvent(dtOrder, "GFF.XI.POINTOFSALE", sXBandId, te);
        }


        private businessEvent GenerateOrderPlacedMessage(DateTime dtOrder, string sOrderNumber, string sCourseNumber, string[] aguests)
        {
            orderevent oe = GenerateOrderEvent(dtOrder, "Order Placed", "5", sOrderNumber, sCourseNumber, "POS", "Fixed", aguests);
            return PlaceInBusinessEvent(dtOrder, "GFF.XI.KITCHENMANAGEMENT", null, oe);
        }

        private businessEvent GenerateOrderReceivedMessage(DateTime dt, string sOrderNumber, string sCourseNumber )
        {
            orderevent oe = GenerateOrderEvent(dt, "Order Received", "6", sOrderNumber, sCourseNumber, "Kitchen", null, null);
            return PlaceInBusinessEvent(dt, "GFF.XI.KITCHENMANAGEMENT", null, oe);
        }

        private businessEvent GenerateOrderClearedMessage(DateTime dt, string sOrderNumber, string sCourseNumber)
        {
            orderevent oe = GenerateOrderEvent(dt, "Order Cleared", "21", sOrderNumber, sCourseNumber, "Kitchen", null, null);
            return PlaceInBusinessEvent(dt, "GFF.XI.KITCHENMANAGEMENT", null, oe);
        }

        private businessEvent GenerateOrderBumpedMessage(DateTime dt, string sOrderNumber)
        {
            orderevent oe = GenerateOrderEvent(dt, "Order Bumped", "17", sOrderNumber, null, "Kitchen", null, null);
            return PlaceInBusinessEvent(dt, "GFF.XI.KITCHENMANAGEMENT", null, oe);
        }

        private businessEvent GenerateOrderDeliveredMessage(DateTime dt, string sOrderNumber, string sRunner)
        {
            orderevent oe = GenerateOrderEvent(dt, "OrderDelivered", "23", sOrderNumber, null, "Kitchen", null, null);
            oe.user = sRunner;
            return PlaceInBusinessEvent(dt, "GFF.XI.KITCHENMANAGEMENT", null, oe);
        }

        private businessEvent GenerateTableCleanedMessage(DateTime dt, string sLocation, string sTableId, string sTableName, string[] asOrders, string sRunner)
        {
            tableevent te = GenerateTableEvent(dt, "Table Cleaned", "28", null, sLocation, "Runner", sTableId, sTableName, asOrders);
            te.user = sRunner;
            return PlaceInBusinessEvent(dt, "GFF.XI.TABLEMANAGEMENT", null, te);
        }

        private businessEvent GenerateTableDirtyMessage(DateTime dt, string sLocation, string sTableId, string sTableName, string[] asOrders, string sRunner)
        {
            tableevent te = GenerateTableEvent(dt, "Table Dirty", "27", null, sLocation, "Runner", sTableId, sTableName, asOrders);
            te.user = sRunner;
            return PlaceInBusinessEvent(dt, "GFF.XI.TABLEMANAGEMENT", null, te);
        }


        private businessEvent GenerateGuestDetectStandbyMessage(DateTime dt, string sOrderNumber, string sXBandId)
        {
            tapevent te = GenerateTapEvent( dt,
                                            "Guest Detected at Standby Lane",
                                            "1",
                                            false,
                                            sOrderNumber,
                                            "Long Range Reader",
                                            "Location 1",
                                            null,
                                            sXBandId,
                                            null,
                                            null);

            // create the envelope
            return PlaceInBusinessEvent(dt, "GFF.XI.POINTOFSALE", sXBandId, te);
        }

        private businessEvent GenerateGuestTappedAtMobileDAPMessage(DateTime dt, string sOrderNumber, string sXBandId)
        {
            tapevent te = GenerateTapEvent(dt,
                                            "Guest Tapped at Mobile DAP",
                                            "2",
                                            false,
                                            sOrderNumber,
                                            "Mobile DAP",
                                            null,
                                            null,
                                            sXBandId,
                                            rand.Next().ToString(),
                                            null);

            // create the envelope
            return PlaceInBusinessEvent(dt, "GFF.XI.POINTOFSALE", sXBandId, te);
        }

        private businessEvent GenerateGuestTapMergeMessage(DateTime dt, string sOrderNumber, string sXBandId, int nPartySize)
        {
            tapevent te = GenerateTapEvent(dt,
                                            "Guest Tapped at Merge Point",
                                            "3",
                                            false,
                                            sOrderNumber,
                                            "Merge Point",
                                            null,
                                            null,
                                            sXBandId,
                                            null,
                                            nPartySize.ToString());

            return PlaceInBusinessEvent(dt, "GFF.XI.POINTOFSALE", sXBandId, te);
        }

        private businessEvent GenerateTableGuestDetectedMessage(DateTime dt, string sXBandId,  string sLocation, string sTableId, string sTableName, string[] asOrders)
        {
            tableevent te = GenerateTableEvent(dt, "Guest Detected At Table", "7", sXBandId, sLocation, "Table Service", sTableId, sTableName, asOrders);
            return PlaceInBusinessEvent(dt, "GFF.XI.TABLEMANAGEMENT", null, te);
        }

        private businessEvent PlaceInBusinessEvent(DateTime dt, string sLocation, string sXBandId, tapevent te)
        {
            // create the envelope
            businessEvent be = GenerateBusinessEvent(dt, "CHANGE", "", sXBandId, sLocation, Guid.NewGuid().ToString());

            // serialize tap event into payload
            XmlSerializer ser = new XmlSerializer(typeof(tapevent));
            XmlSerializerNamespaces ns = new XmlSerializerNamespaces();
            ns.Add("", "");
            StringWriter sw = new StringWriter();
            XmlWriter xw = XmlWriter.Create(sw, new XmlWriterSettings { OmitXmlDeclaration = true, Indent = true, IndentChars = "    " });
            ser.Serialize(xw, te, ns);
            be._payLoad = Environment.NewLine + sw.ToString() + Environment.NewLine;
            return be;
        }

        private businessEvent PlaceInBusinessEvent(DateTime dt, string sLocation, string sXBandId, orderevent oe)
        {
            // create the envelope
            businessEvent be = GenerateBusinessEvent(dt, "CHANGE", "", sXBandId, sLocation, Guid.NewGuid().ToString());

            // serialize event into payload
            XmlSerializer ser = new XmlSerializer(typeof(orderevent));
            XmlSerializerNamespaces ns = new XmlSerializerNamespaces();
            ns.Add("", "");
            StringWriter sw = new StringWriter();
            XmlWriter xw = XmlWriter.Create(sw, new XmlWriterSettings { OmitXmlDeclaration = true, Indent = true, IndentChars = "    " });
            ser.Serialize(xw, oe, ns);
            be._payLoad = Environment.NewLine + sw.ToString() + Environment.NewLine;
            return be;
        }

        private businessEvent PlaceInBusinessEvent(DateTime dt, string sLocation, string sXBandId, tableevent te)
        {
            // create the envelope
            businessEvent be = GenerateBusinessEvent(dt, "CHANGE", "", sXBandId, sLocation, Guid.NewGuid().ToString());

            // serialize event into payload
            XmlSerializer ser = new XmlSerializer(typeof(tableevent));
            XmlSerializerNamespaces ns = new XmlSerializerNamespaces();
            ns.Add("", "");
            StringWriter sw = new StringWriter();
            XmlWriter xw = XmlWriter.Create(sw, new XmlWriterSettings { OmitXmlDeclaration = true, Indent = true, IndentChars = "    " });
            ser.Serialize(xw, te, ns);
            be._payLoad = Environment.NewLine + sw.ToString() + Environment.NewLine;
            return be;
        }

        private businessEvent GenerateBusinessEvent(DateTime dt,
                                                        string sEventType,
                                                        string sSubType,
                                                        string sGuestIdentifier,
                                                        string sLocation,
                                                        string sReferenceId)
        {
            businessEvent be = new businessEvent();
            be.event_type = sEventType;
            be.subtype = sSubType;
            be.facilityid = sFacilityOneSourceId;
            be.guest_identifier = sGuestIdentifier;
            be.location = sLocation;
            be.reference_id = sReferenceId;
            be.timestamp = FormatTime(dt);
            return be;
        }

        private tableevent GenerateTableEvent(  DateTime dt,
                                                string sEvent,
                                                string sEventTypeId,
                                                string sXBandId,
                                                string sLocation,
                                                string sSource,
                                                string sTableId,
                                                string sTableName,
                                                string[] asOrders
                                                )
        {
            tableevent te = new tableevent();

            te.@event = sEvent;
            te.eventtypeid = sEventTypeId;
            te.facilityonesourceid = sFacilityOneSourceId;
            te.xbandid = sXBandId;
            te.locationid = sLocation;
            te.source = sSource;
            te.timestamp = FormatTime(dt);
            te.timestampgmt = FormatTimeUTC(dt);
            te.tableid = sTableName;
            te.tablename = sTableName;
            te.changeddetails = null;
            for (int i = 0; i < asOrders.Length; i++)
            {
                tableeventOrdernumbers ton = new tableeventOrdernumbers();
                ton.ordernumber = asOrders[i];
                te.ordernumbers.Add(ton);
            }

            return te;
        }

        private orderevent GenerateOrderEvent(  DateTime dt,
                                                string sEvent,
                                                string sEventTypeId,
                                                string sOrderNumber,
                                                string sCourseNumber,
                                                string sSource,
                                                string sSourceType,
                                                string[] asGuests)
        {
            orderevent oe = new orderevent();
            oe.@event = sEvent;
            oe.eventtypeid = sEventTypeId;
            oe.facilityonesourceid = sFacilityOneSourceId;
            oe.source = sSource;
            oe.sourcetype = sSourceType;
            oe.timestamp = FormatTime(dt);
            oe.timestampgmt = FormatTimeUTC(dt);
            oe.ordernumber = sOrderNumber;
            oe.orderamount = rand.NextDouble() * 90.0 + 10.0;
            if (asGuests!=null)
                oe.partysize = asGuests.Length.ToString();
            ordereventKitcheninfo oeki = new ordereventKitcheninfo();
            oeki.coursenumber = sCourseNumber;
            oeki.coursename = "Combo #" + oeki.coursenumber;
            oeki.orderstarttime = FormatTime(dt);
            oeki.orderstarttimegmt = FormatTimeUTC(dt);
            oe.kitcheninfo = oeki;
            if (asGuests != null)
            {
                for (int i = 0; i < asGuests.Length; i++)
                {
                    ordereventGuest oeg = new ordereventGuest();
                    oeg.xbandid = asGuests[i];
                    oeg.gxplinkid = "111" + asGuests[i];
                    oe.guests.Add(oeg);
                }
            }
            return oe;
        }

        private tapevent GenerateTapEvent(DateTime dt, 
                                            string sEvent, 
                                            string sEventTypeId, 
                                            bool bIsBlueLaned, 
                                            string sOrderNumber, 
                                            string sSource,
                                            string sSourceType,
                                            string sTerminal,
                                            string sXBandId,
                                            string sGxpLinkId,
                                            string sPartySize)
        {
            tapevent te = new tapevent();
            te.@event = sEvent;
            te.eventtypeid = sEventTypeId;
            te.facilityonesourceid = sFacilityOneSourceId;
            te.isbluelaned = bIsBlueLaned;
            te.ordernumber = sOrderNumber;
            te.source = sSource;
            te.sourcetype = sSourceType;
            te.terminal = sTerminal;
            te.timestamp = FormatTime(dt);
            te.timestampgmt = FormatTimeUTC(dt);
            te.xbandid = sXBandId;
            te.gxplinkid = sGxpLinkId;
            te.partysize = sPartySize;
            return te;
        }

        public static string FormatTime(DateTime dateTime)
        {
            return dateTime.ToString("yyyy-MM-ddTHH:mm:ss");
        }

        private string FormatTimeUTC(DateTime dateTime)
        {
            return dateTime.ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ss");
        }

        private string FormatTimeZ(DateTime dateTime)
        {
            return dateTime.ToUniversalTime().ToString("yyyy-MM-ddTHH:mm:ss.FFFFZ");
        }

        private string FormatTimePlus(DateTime dateTime)
        {
            return dateTime.ToString("yyyy-MM-ddTHH:mm:ss.FFFzzzzzz");
        }

        public static DateTime ParseTime(string sDateTime)
        {
            return DateTime.ParseExact(sDateTime, "yyyy-MM-ddTHH:mm:ss", null);
        }

        // Get normal (Gaussian) random sample with mean 0 and standard deviation 1
        private double GetNormal()
        {
            // Use Box-Muller algorithm
            double u1 = rand.NextDouble();
            double u2 = rand.NextDouble();
            double r = Math.Sqrt(-2.0 * Math.Log(u1));
            double theta = 2.0 * Math.PI * u2;
            return r * Math.Sin(theta);
        }

        // Get normal (Gaussian) random sample with specified mean and standard deviation
        public double GetNormal(double mean, double standardDeviation)
        {
            return mean + standardDeviation * GetNormal();
        }

        private string AllocateGuest()
        {
            return iNextGuest++.ToString();
        }

        public int Compare(businessEvent x, businessEvent y)
        {
            DateTime dt1 = ParseTime(x.timestamp);
            DateTime dt2 = ParseTime(y.timestamp);

            return DateTime.Compare(dt1, dt2);
        }
    }
}
