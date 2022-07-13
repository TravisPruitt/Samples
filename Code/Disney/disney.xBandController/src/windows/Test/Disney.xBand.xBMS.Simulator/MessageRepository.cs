using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Collections.Concurrent;
using System.Xml.Serialization;

namespace Disney.xBand.xBMS.Simulator
{
    public class MessageRepository
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog log = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public String DATE_FORMAT { get { return "yyyy-MM-ddThh:mm:ssZ"; } }

        private Dto.SeedData seedData;

        private ConcurrentQueue<Dto.xBandRequest> xBandRequestMessageQueue;
        private ConcurrentDictionary<String, Dto.xBandRequest> xBandRequests;

        private ConcurrentQueue<Dto.xBand> xBandMessageQueue;
        private ConcurrentDictionary<String, Dto.xBand> xBands;

        public Dto.SimulationState xBandMessageState { get; private set; }

        public Dto.SimulationState xBandRequestMessageState { get; private set; }

        public Dto.SimulationStatus Status
        {
            get
            {
                lock (this)
                {
                    return new Dto.SimulationStatus()
                    {
                        xBandMessagesRemaining = this.xBandMessagesRemaining,
                        xBandMessagesProcessed = this.xBands.Count(),
                        xBandRequestMessagesRemaining = this.xBandRequestMessagesRemaining,
                        xBandRequestMessagesProcessed = this.xBandRequests.Count(),
                        xBandMessagesValidated = this.xBands.Where(x => x.Value.MessageState == Dto.MessageState.Success).Count(),
                        xBandMessageErrors = this.xBands.Where(x => x.Value.MessageState == Dto.MessageState.Error).Count(),
                        xBandRequestStatus = this.xBandRequestMessageState.GetDescription(),
                        xBandRequestMessagesValidated = this.xBandRequests.Where(x => x.Value.MessageState == Dto.MessageState.Success).Count(),
                        xBandRequestMessageErrors = this.xBandRequests.Where(x => x.Value.MessageState == Dto.MessageState.Error).Count(),
                        xBandStatus = this.xBandMessageState.GetDescription()
                    };
                }
            }
        }

        public MessageRepository()
        {
            this.xBandRequests = new ConcurrentDictionary<String, Dto.xBandRequest>();

            this.xBands = new ConcurrentDictionary<String, Dto.xBand>();

            this.xBandMessageQueue = new ConcurrentQueue<Dto.xBand>();
            this.xBandRequestMessageQueue = new ConcurrentQueue<Dto.xBandRequest>();

            this.xBandMessageState = Dto.SimulationState.NotStarted;
            this.xBandRequestMessageState = Dto.SimulationState.NotStarted;
        }

        public void InitializexBMS(int numberOfGuests)
        {
            log.Info("Starting Initialization");

            ReadSeedData();

            this.xBandMessageQueue = new ConcurrentQueue<Dto.xBand>();
            this.xBandRequestMessageQueue = new ConcurrentQueue<Dto.xBandRequest>();
            
            this.xBandRequests.Clear();
            this.xBands.Clear();

            this.xBandMessageState = Dto.SimulationState.NotStarted;
            this.xBandRequestMessageState = Dto.SimulationState.NotStarted;

            Random random = new Random();
            DateTime epoch = new DateTime(1970, 1, 1);

            for (int index = 0; index < numberOfGuests; index++)
            {
                Dto.xBandRequest request = new Dto.xBandRequest();
                DateTime seedDateTime = DateTime.UtcNow.AddDays(random.Next(10));
                String xbandRequestId = Guid.NewGuid().ToString();
                String primaryGuestOwnerId = Guid.NewGuid().ToString();
                DateTime arrivalDate = seedDateTime.AddDays(random.Next(100));

                request.Options = String.Format("/reorder-options/{0}", xbandRequestId);
                request.Order = String.Format("/orders/{0}", xbandRequestId);
                request.State = "FULFILLMENT";
                request.PrimaryGuestOwnerId = primaryGuestOwnerId;
                request.xBandRequestId = xbandRequestId;
                request.AcquisitionId = this.seedData.TravelPlanId.ToString();
                request.AcquisitionIdType = "travel-plan-id";
                request.AcquisitionStartDate = seedDateTime.ToString(DATE_FORMAT);

                request.ResortReservations = new List<Dto.ResortReservation>();

                Dto.ResortReservation resortReservation = new Dto.ResortReservation()
                {

                    TravelSegmentId = this.seedData.TravelSegmentId,
                    TravelComponentId = this.seedData.TravelComponentId,
                    ArrivalDate = arrivalDate.ToString(DATE_FORMAT),
                    DepartureDate = arrivalDate.AddDays(7).ToString(DATE_FORMAT),
                    FacilityId = 80010388
                };

                request.ResortReservations.Add(resortReservation);

                request.RequestAddress = new Dto.RequestAddress()
                {
                    Address = new Dto.Address()
                    {
                        Address1 = "1511 6th Avenue",
                        City = "Seattle",
                        State = "WA",
                        PostalCode = "98101",
                        Country = "US"
                    },
                    confirmedAddress = true
                };

                request.Shipment = new Dto.Shipment()
                {
                    State = "FL",
                    Country = "US",
                    Address1 = "51 W South St",
                    City = "Orlando",
                    PostalCode = "32801-3337",
                    Address2 = "Bay Lake Tower at Disney's Contemporary Resort"
                };


                request.CustomizationSelections = new List<Dto.CustomizationSelection>();

                string lastName = GetLastName(random);

                for (int csIndex = 0; csIndex < 2; csIndex++)
                {
                    String xbandOwnerId = csIndex == 0 ? primaryGuestOwnerId : Guid.NewGuid().ToString();
                    string firstName = GetFirstName(random);
                    string customizationSelectionId = Guid.NewGuid().ToString();

                    Dto.QualifyingId qualifyingId = new Dto.QualifyingId()
                    {
                        QualifyingIdType = "travel-component-id",
                        QualifyingIdValue = this.seedData.TravelComponentId.ToString()
                    };

                    List<Dto.QualifyingId> qualifyingIds = new List<Dto.QualifyingId>();

                    qualifyingIds.Add(qualifyingId);
                    //CustomizationSelection
                    var cs = new Dto.CustomizationSelection()
                    {
                        BandAccessories = new List<Dto.BandAccessory>(),
                        BandProductCode = "B11313",
                        BirthDate = epoch.AddDays(-random.Next(7300)).ToString("MM/dd/yyyy"),
                        CreateDate = DateTime.UtcNow.ToString(DATE_FORMAT),
                        CustomizationSelectionId = customizationSelectionId,
                        Entitlements = new List<string>(new string[] { "STANDARD" }),
                        FirstName = firstName,
                        GuestId = this.seedData.TransactionalGuestId.ToString(),
                        GuestTypeId = "transactional-guest-id",
                        LastName = lastName,
                        PrimaryGuest = true,
                        PrintedName = firstName,
                        QualifyingIds = qualifyingIds,
                        Self = String.Format("/customization-selections/{0}", customizationSelectionId),
                        UpdateDate = DateTime.UtcNow.ToString(DATE_FORMAT),
                        xBandOwnerId = xbandOwnerId,
                        xBandRequestId = request.xBandRequestId
                    };

                    String xbandId = Guid.NewGuid().ToString();

                    Dto.xBand xband = new Dto.xBand()
                    {
                        AssignmentDateTime = seedDateTime.AddDays(random.Next(10)).ToString(DATE_FORMAT),
                        BandRole = "Guest",
                        ExternalNumber = this.seedData.ExternalNumber.ToString("X"),
                        GuestId = this.seedData.TransactionalGuestId.ToString(),
                        GuestIdType = "transactional-guest-id",
                        History = String.Format("/xband-history/{0}", xbandId),
                        Options = String.Format("/xband-options/{0}", xbandId),
                        PrintedName = firstName,
                        ProductId = "C00000",
                        PublicId = this.seedData.PublicId,
                        SecondaryState = "TRANSFER",
                        SecureId = this.seedData.SecureId,
                        Self = String.Format("/xband/{0}", xbandId),
                        ShortRangeTag = this.seedData.ShortRangeTag,
                        State = "ACTIVE",
                        XBandId = xbandId,
                        XBandOwnerId = xbandOwnerId,
                        XBandRequestId = request.xBandRequestId
                    };

                    this.xBandMessageQueue.Enqueue(xband);

                    this.seedData.TransactionalGuestId++;
                    this.seedData.PublicId++;
                    this.seedData.SecureId++;
                    this.seedData.ShortRangeTag++;
                    this.seedData.ExternalNumber++;

                    request.CustomizationSelections.Add(cs);
                }

                request.AcquisitionUpdateDate = seedDateTime.ToString(DATE_FORMAT);
                request.CreateDate = seedDateTime.ToString(DATE_FORMAT);
                request.UpdateDate = seedDateTime.ToString(DATE_FORMAT);
                request.Reorder = String.Format("xband-requests/{0}/reorder", xbandRequestId);
                request.CustomizationEndDate = seedDateTime.ToString(DATE_FORMAT);
                request.Self = String.Format("/xband-requests/{0}", xbandRequestId);

                this.xBandRequestMessageQueue.Enqueue(request);

                this.seedData.TravelComponentId++;
                this.seedData.TravelPlanId++;
                this.seedData.TravelSegmentId++;
                this.seedData.TravelComponentId++;


            }

            WriteSeedData();

            log.Info("Initialization completed");
        }

        public Boolean GetNextxBandMessage(out Dto.xBand message)
        {
            Boolean result = true;

            if (!this.xBandMessageQueue.TryDequeue(out message))
            {
                this.xBandMessageState = Dto.SimulationState.Validating;
                result = false;
            }
            else
            {
                //If request message hasn't been sent, then requeue
                if (!this.xBandRequests.ContainsKey(message.XBandRequestId))
                {
                    this.xBandMessageQueue.Enqueue(message);
                    return false;
                }
            }

            return result;
        }

        public void Requeue(Dto.xBand message)
        {
            this.xBandMessageQueue.Enqueue(message);
            this.xBandMessageState = Dto.SimulationState.Running;
        }

        public void MessageSent(Dto.xBand message)
        {
            this.xBands.GetOrAdd(message.XBandId, message);
            this.xBandMessageState = Dto.SimulationState.Running;
        }

        public int xBandMessagesRemaining
        {
            get
            {
                return this.xBandMessageQueue.Count;
            }
        }

        public bool ContainsxBandMessage(string xbandId)
        {
            return this.xBands.ContainsKey(xbandId);
        }

        public Dto.xBand GetxBandMessage(string xbandId)
        {
            return this.xBands[xbandId];
        }

        public Boolean GetNextxBandRequestMessage(out Dto.xBandRequest message)
        {
            Boolean result = true;
            
            if (!this.xBandRequestMessageQueue.TryDequeue(out message))
            {
                this.xBandRequestMessageState = Dto.SimulationState.Validating;
                result = false;
            }

            return result;
        }

        public Dto.xBandRequest GetNextUnvalidatedxBandRequestMessage()
        {
            Dto.xBandRequest result = this.xBandRequests
                .Where(x => x.Value.MessageState == Dto.MessageState.Sent).Select(x => x.Value).FirstOrDefault();

            if (result == null)
            {
                this.xBandRequestMessageState = Dto.SimulationState.Completed;
            }

            return result;
        }

        public bool ContainsxBandRequestMessage(string xbandRequestId)
        {
            return this.xBandRequests.ContainsKey(xbandRequestId);
        }

        public Dto.xBandRequest GetxBandRequestMessage(string xbandRequestId)
        {
            return this.xBandRequests[xbandRequestId];
        }

        public void Requeue(Dto.xBandRequest message)
        {
            this.xBandRequestMessageQueue.Enqueue(message);
            this.xBandRequestMessageState = Dto.SimulationState.Running;
        }

        public void MessageSent(Dto.xBandRequest message)
        {
            this.xBandRequests.GetOrAdd(message.xBandRequestId, message);
            this.xBandRequestMessageState = Dto.SimulationState.Running;
        }

        public int xBandRequestMessagesRemaining
        {
            get
            {
                return this.xBandRequestMessageQueue.Count;
            }
        }

        public Dto.xBand GetNextUnvalidatedxBandMessage()
        {
            Dto.xBand result = this.xBands
                .Where(x => x.Value.MessageState == Dto.MessageState.Sent).Select(x => x.Value).FirstOrDefault();

            if (result == null)
            {
                this.xBandMessageState = Dto.SimulationState.Completed;
            }

            return result;
        }

        private void ReadSeedData()
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Dto.SeedData));

            using (TextReader reader = new StreamReader("SourceFiles\\SeedData.xml"))
            {
                this.seedData = (Dto.SeedData)serializer.Deserialize(reader);
            }
        }


        private void WriteSeedData()
        {
            XmlSerializer serializer = new XmlSerializer(typeof(Dto.SeedData));

            using (TextWriter writer = new StreamWriter("SourceFiles\\SeedData.xml"))
            {
                serializer.Serialize(writer, this.seedData);
            }
        }

        private String GetFirstName(Random random)
        {
            String firstname = String.Empty;

            using (StreamReader firstNames = File.OpenText("SourceFiles\\FirstNames.txt"))
            {
                int nextFirstName = random.Next(5494);
                for (int index = 0; index < nextFirstName; index++)
                {
                    firstname = firstNames.ReadLine();
                }
            }

            return firstname;
        }

        private String GetLastName(Random random)
        {
            String lastname = String.Empty;

            using (StreamReader lastNames = File.OpenText("SourceFiles\\LastNames.txt"))
            {
                int nextLastName = random.Next(88800);
                for (int index = 0; index < nextLastName; index++)
                {
                    lastname = lastNames.ReadLine();
                }
            }

            return lastname;
        }
    }
}
