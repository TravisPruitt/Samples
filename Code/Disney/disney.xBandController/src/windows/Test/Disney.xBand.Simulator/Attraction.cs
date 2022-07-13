using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Collections;
using System.Threading.Tasks;
using System.Threading;
using System.Net;
using System.Net.Sockets;
using System.Diagnostics;
using System.Collections.Concurrent;

namespace Disney.xBand.Simulator
{
    public class ReaderTaskData
    {
        public Dto.Reader Reader { get; set; }
        public string ControllerUrl { get; set; }
        public Dto.Repositories.IReaderRepository ReaderRepository { get; set; }
        public CancellationTokenSource TokenSource { get; set; }
    }

    public class Attraction
    {

        private const double COMFORTZONE = 2.2;

        private const double X_VELOCITY = 4.0;

        private System.Timers.Timer guestTimer;

        private Dto.Attraction attraction;

        private Dto.Repositories.IGuestRepository guestRepository;

        private CancellationTokenSource tokenSource;

        private Dto.Repositories.IReaderEventRepository readerEventRepository;

        private DateTime lastSignalTime;

        private DateTime lastArrivalUpdate;

        private const int LOAD_FREQUENCY_IN_MILLISECONDS = 30000;

        private DateTime lastLoad;

        private int guestsToLoad;

        private DateTime startTime;

        private DateTime lastEvent;

        public List<Dto.Guest> guestQueue;

        private double xPositionExitReader;

        private int maxSequenceNumber;

        public Attraction(Dto.Attraction attraction, Dto.Repositories.IGuestRepository guestRepository, CancellationTokenSource tokenSource)
        {
            this.guestRepository = guestRepository;

            this.guestQueue = this.guestRepository.Initialize(attraction);

            this.attraction = attraction;
            this.tokenSource = tokenSource;

            this.guestTimer = new System.Timers.Timer(1000);
            this.guestTimer.Elapsed += new System.Timers.ElapsedEventHandler(GuestTimerElapsed);
            this.guestTimer.AutoReset = true;

            this.readerEventRepository = new Dto.Repositories.ReaderEventRepository();

            this.lastSignalTime = DateTime.Now;

            this.lastLoad = DateTime.Now;

            this.guestsToLoad = (this.attraction.GuestsPerHour * LOAD_FREQUENCY_IN_MILLISECONDS) / 3600000;

            this.lastEvent = DateTime.Now;
            this.startTime = DateTime.Now;

            this.lastEvent = DateTime.MinValue;

            foreach (Dto.Reader reader in this.attraction.Controller.Readers)
            {
                if (reader.ReaderName.Contains("exit"))
                {
                    if (reader.xCoordinate > this.xPositionExitReader)
                    {
                        this.xPositionExitReader = reader.xCoordinate;
                    }
                }
            }

            AddArrivingGuests();
        }

        public void Start()
        {
            this.guestTimer.Enabled = true;
            StartReaders();
        }

        public void Stop()
        {
            this.guestTimer.Enabled = false;
        }

        private void AddArrivingGuests()
        {
            Random rand = new Random();

            this.lastArrivalUpdate = DateTime.Now;

            int newMaxSequenceNumber = this.maxSequenceNumber + this.attraction.FastPassPlusArrivalRate;

            foreach (Dto.Guest guest in this.guestQueue.Where(g => g.SequenceNumber >= this.maxSequenceNumber &&
                g.SequenceNumber <= newMaxSequenceNumber))
            {
                guest.HasFastPassPlus = true;
                guest.yPosition = rand.NextDouble() * 20.0;
                guest.GuestState = Dto.GuestState.Arriving;
            }

            if (!this.attraction.TapOnly)
            {
                foreach (Dto.Guest guest in this.guestQueue.Where(g => g.SequenceNumber > newMaxSequenceNumber &&
                g.SequenceNumber <= newMaxSequenceNumber + this.attraction.StandByArrivalRate))
                {
                    guest.HasFastPassPlus = false;
                    guest.yPosition = rand.NextDouble() * 20.0;
                    guest.GuestState = Dto.GuestState.Arriving;
                }
                
                newMaxSequenceNumber += this.attraction.StandByArrivalRate;
            }

            this.maxSequenceNumber = newMaxSequenceNumber;
        }

        private void GuestTimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            //Make sure we finish the last call before the next one.
            lock (this)
            {
                if (!tokenSource.IsCancellationRequested)
                {
                    Stopwatch stopwatch = new Stopwatch();
                    stopwatch.Start();

                    if (e.SignalTime.Subtract(this.lastArrivalUpdate).TotalSeconds >= 60)
                    {
                        AddArrivingGuests();
                    }

                    //Animate guests
                    AnimateGuests();

                    //Animate Ride
                    AnimateRide();

                    this.lastEvent = e.SignalTime;

                    Debug.WriteLine(
                        String.Format("One pass of processing guests took {0} milliseconds.",
                        stopwatch.ElapsedMilliseconds));
                }
            }
        }



        public void StartReaders()
        {

            List<Task> tasks = new List<Task>();

            // Establish the local endpoint for the socket.
            // The DNS name of the computer
            // running the listener
            IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
            IPAddress ipAddress = null;

            foreach (IPAddress addressListEntry in ipHostInfo.AddressList)
            {
                if (addressListEntry.AddressFamily == AddressFamily.InterNetwork)
                {
                    ipAddress = addressListEntry;
                    break;
                }
            }

            if (attraction.Controller != null)
            {
                Dto.Repositories.IReaderRepository readerRepository = new Dto.Repositories.ReaderRepository();

                foreach (var reader in attraction.Controller.Readers)
                {
                    Task task = new Task((obj) =>
                    {
                        ReaderTaskData readerTaskData = (ReaderTaskData)obj;
                        Reader taskReader = new Reader(readerTaskData.Reader, readerTaskData.ControllerUrl,
                            readerTaskData.ReaderRepository, readerTaskData.TokenSource);

                        taskReader.Start();
                    }, new ReaderTaskData()
                    {
                        Reader = reader,
                        ControllerUrl = reader.Controller.ControllerURL,
                        ReaderRepository = readerRepository,
                        TokenSource = this.tokenSource
                    }, tokenSource.Token);

                    task.Start();

                    tasks.Add(task);

                }

                Task.WaitAll(tasks.ToArray());
            }

        }

        private void AnimateGuests()
        {
            Random rand = new Random();

            // iterate through all the guests
            foreach (Dto.Guest guest in this.guestQueue.Where(g => g.GuestState != Dto.GuestState.Indeterminate && 
                g.GuestState != Dto.GuestState.OutOfRange))
            {
                // current time
                DateTime tsNow = DateTime.Now;

                // only move people who are not loading or riding
                if (guest.GuestState != Dto.GuestState.Loading && guest.GuestState != Dto.GuestState.Riding)
                {

                    // calculate elapsed time between simulation steps
                    double seconds = DateTime.Now.Subtract(this.lastEvent).TotalMilliseconds / 1000.0;


                    if (seconds > 1.0)
                    {
                        //if (bVerbose)
                        //    printf("!! Simulation time (%g) is greater than 1.0. Clamped to 1.0\n", dSeconds);

                        // clamp this to avoid skipping over readers if simulation bogs down
                        seconds = 1.0;
                    }

                    // calculate a random velocity for the guest based on the attraction
                    double vxg;

                    // start with the attraction's x value
                    vxg = X_VELOCITY * seconds; 

                    // test proposed location
                    double xNew = guest.xPosition + vxg;

                    if (!PositionOccupied(guest, xNew, guest.yPosition))
                    {
                        // advance the guest's location
                        guest.xPosition = xNew;
                    }
                    else
                    {
                        //Debug.WriteLine(String.Format("Position: {0},{1} occupied", xNew, guest.yPosition));
                    }

                }


                //// is it time for the guest's band to chirp?
                foreach (Dto.MagicBand magicBand in guest.MagicBands)
                {
                    if (DateTime.Now > magicBand.NextTransmit)
                    {
                        // yes!
                        ChirpGuest(guest, magicBand);

                        //Set next transmit interval
                        magicBand.NextTransmit.AddMilliseconds(750 + rand.Next() % 500);
                    }

                }


                // special case: deal with guests who somehow got past the exit reader but didn't register
                if ((guest.xPosition > this.xPositionExitReader) &&
                     guest.GuestState != Dto.GuestState.Exited &&
                    guest.GuestState != Dto.GuestState.OutOfRange)
                {
                    //if (bVerbose)
                    //    printf("!! Guest %d at location (%g, %g) got past the exit\n", ig, guest.xPosition, pgi->y);

                    // send belated EXIT message?
                    guest.GuestState = Dto.GuestState.Exited;
                }


                guestRepository.UpdateGuestPosition(guest, this.attraction.AttractionID);
            }
        }

        private void AnimateRide()
        {
            //Load guests into car every minute
            if (DateTime.Now.Subtract(this.lastLoad).TotalSeconds >= 60)
            {
                int guestsToLoad = this.attraction.GuestsPerHour / 60;

                // iterate through all the guests in the loading area and get enough to load
                foreach (Dto.Guest guest in this.guestQueue.Where(g => g.GuestState == Dto.GuestState.Loading)
                    .Take(guestsToLoad).OrderBy(g => g.SequenceNumber))
                {
                    guest.GuestState = Dto.GuestState.Riding;
                    guest.LoadTime = DateTime.Now.TimeOfDay;
                }

                this.lastLoad = DateTime.Now;
            }

            // iterate through all the guests in the loading area and get enough to load
            foreach (Dto.Guest guest in this.guestQueue.Where(g => g.GuestState == Dto.GuestState.Riding))
            {
                //Ride is two minutes
                if (DateTime.Now.TimeOfDay > guest.LoadTime.Add(new TimeSpan(0, 2, 0)))
                {
                    //Put guest at exit reader so that they get an exit read.
                    guest.GuestState = Dto.GuestState.Exited;
                    guest.ExitTime = DateTime.Now.TimeOfDay;
                    guest.xPosition = this.xPositionExitReader - 2.2;
                }
            }

            // iterate through all the guests in the exit area and get enough to load
            foreach (Dto.Guest guest in this.guestQueue.Where(g => g.GuestState == Dto.GuestState.Exited))
            {
                //Allow guest to linger no longer than 5 minutes at exit
                if (DateTime.Now.TimeOfDay > guest.ExitTime.Add(new TimeSpan(0, 5, 0)))
                {
                    //Put guest at exit reader so that they get an exit read.
                    guest.GuestState = Dto.GuestState.OutOfRange;

                    this.guestRepository.UpdateGuestPosition(guest, this.attraction.AttractionID);
                }
            }
        }
        private void ChirpGuest(Dto.Guest guest, Dto.MagicBand magicBand)
        {
            bool sentLRR = false;

            // iterate through the readers, deciding which ones to write to. Handle special
            // cases (xpass readers, merge readers, etc.)

            foreach (Dto.Reader reader in this.attraction.Controller.Readers)
            {
                // ignore far off LRRs
                if (reader.ReaderType.ReaderTypeName != "Tap")
                {
                    // calculate distance d
                    double xd = (double)guest.xPosition - reader.xCoordinate;
                    double yd = (double)guest.yPosition - reader.yCoordinate;
                    double d = Math.Sqrt(xd * xd + yd * yd);

                    // if too far, ignore it
                    if (d >= reader.Range)
                    {
                        // yep - ignore it
                        continue;
                    }

                    // have LRR in range, Talk to It
                    PingLRR(reader, magicBand, d);
                    sentLRR = true;

                    // handle special readers
                    if (reader.ReaderName.StartsWith("entry", StringComparison.CurrentCultureIgnoreCase) &&
                        guest.GuestState == Dto.GuestState.Arriving)
                    {
                        // only set state here if the guest is not an xpass guest
                        if (!guest.HasFastPassPlus)
                        {
                            guest.GuestState = Dto.GuestState.Entered;
                            guest.EntryTime = DateTime.Now.TimeOfDay;
                        }
                    }
                    else if (reader.ReaderName.StartsWith("load", StringComparison.CurrentCultureIgnoreCase) &&
                            (guest.GuestState == Dto.GuestState.Entered || guest.GuestState == Dto.GuestState.Merged))
                    {
                        // handle load readers in a special fashion since we don't want the
                        // car's to pick up guests prematurely

                        // put them in LOADING state only when they get right under the load readers
                        if (guest.xPosition >= reader.xCoordinate)
                            guest.GuestState = Dto.GuestState.Loading;
                    }
                }
                else
                {
                    // have a tap reader. Ignore it if the guest doesn't have an xpass
                    if (!guest.HasFastPassPlus)
                        continue;

                    // early short circuit
                    if (guest.GuestState == Dto.GuestState.Merged)
                        continue;

                    // handle entry and merge daps

                    // entry dap
                    if (guest.GuestState == Dto.GuestState.Arriving &&
                        reader.ReaderName.StartsWith("xpassentry", StringComparison.CurrentCultureIgnoreCase) &&
                        guest.xPosition > reader.xCoordinate)
                    {
                        // tap the entry dap
                        TapDap(reader, magicBand);

                        guest.GuestState = Dto.GuestState.Entered;
                        guest.EntryTime = DateTime.Now.TimeOfDay;
                    }
                    else if (guest.GuestState == Dto.GuestState.Entered &&
                             reader.ReaderName.StartsWith("merge", StringComparison.CurrentCultureIgnoreCase) &&
                             guest.xPosition > reader.xCoordinate)
                    {
                        // tap the merge dap
                        TapDap(reader, magicBand);

                        guest.GuestState = Dto.GuestState.Merged;
                    }
                    else
                    {
                        // ignore other daps
                        continue;
                    }
                }

            }

            // if we sent anything to an LRR, update the packet sequence number
            if (sentLRR)
            {
                magicBand.PacketSequence = (magicBand.PacketSequence) % 256;

                // bump the frequency
                magicBand.Frequency = (magicBand.Frequency + 1) % 4;
            }
        }

        private void TapDap(Dto.Reader reader, Dto.MagicBand magicBand)
        {
            Dto.ReaderEvent readerEvent = new Dto.ReaderEvent()
            {
                ReaderID = reader.ReaderID,
                BandID = magicBand.TapID,
                Channel = magicBand.Channel,
                Frequency = 0,
                PacketSequence = magicBand.PacketSequence,
                SignalStrength = 0
            };

            this.readerEventRepository.WriteEvent(readerEvent);
        }


        private void PingLRR(Dto.Reader reader, Dto.MagicBand magicBand, double d)
        {
            Dto.ReaderEvent readerEvent = new Dto.ReaderEvent()
            {
                ReaderID = reader.ReaderID,
                BandID = magicBand.TapID,
                Channel = magicBand.Channel,
                Frequency = 0,
                PacketSequence = magicBand.PacketSequence,
                // calculate signal strength as the inverse of the square of the distance
                SignalStrength = (int)(17.25 * Math.Log10((0.01) / (d * d)))
            };


            //Clamp Signal strength from -90 to -40
            if (readerEvent.SignalStrength > -40)
            {
                readerEvent.SignalStrength = -40;
            }
            else if (readerEvent.SignalStrength < -90)
            {
                readerEvent.SignalStrength = -90;
            }

            // map the frequency
            switch (magicBand.Frequency)
            {
                case 0:
                    readerEvent.Frequency = 2401;
                    break;

                case 1:
                    readerEvent.Frequency = 2424;
                    break;

                case 2:
                    readerEvent.Frequency = 2450;
                    break;

                case 3:
                default:
                    readerEvent.Frequency = 2476;
                    break;
            }

            this.readerEventRepository.WriteEvent(readerEvent);
        }

        private bool PositionOccupied(Dto.Guest guestToSkip, double x, double y)
        {
            // iterate through all guests (skipping the supplied one) seeing if any are in the proposed position
            foreach (Dto.Guest guest in this.guestQueue.Where(g => 
                g.GuestState != Dto.GuestState.Indeterminate &&
                g.GuestState != Dto.GuestState.OutOfRange &&
                g.GuestState != Dto.GuestState.Loading &&
                g.GuestState != Dto.GuestState.Riding))
            {
                //Guest can only be in queue once at a time.
                if (guest.GuestID != guestToSkip.GuestID)
                {
                    // caculate distance
                    double d = Math.Sqrt((guest.xPosition - x) *
                        (guest.xPosition - x) + (guest.yPosition - y) * (guest.yPosition - y));

                    if (d < COMFORTZONE)
                        return true;
                }
            }

            return false;
        }

    }
}
