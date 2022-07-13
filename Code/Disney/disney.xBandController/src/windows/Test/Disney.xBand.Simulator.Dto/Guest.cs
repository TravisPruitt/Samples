using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
 
    public enum GuestState
    {
        Indeterminate = 0,
        Arriving = 1,
        Entered = 2,
        InQueue = 3,
        Merged = 4,
        Loading = 5,
        Riding = 6,
        Exited = 7,
        OutOfRange = 8
    }

    public class GuestCountItem
    {
        public string State { get; set; }
        public int GuestCount { get; set; }
    }

    [DataContract]
    public class Guest
    {
        [DataMember(Name = "guestId")]
        public long GuestID { get; set; }

        public string LastName  { get; set; }

        public string FirstName { get; set; }

        public TimeSpan EntryTime { get; set; }

        public TimeSpan LoadTime { get; set; }

        public GuestState GuestState { get; set; }

        public int SequenceNumber { get; set; }

        public bool HasFastPassPlus { get; set; }

        public double xPosition { get; set; }

        public double yPosition { get; set; }

        public TimeSpan ExitTime { get; set; }

        [DataMember(Name = "xbands")]
        public List<MagicBand> MagicBands { get; set; }

        public Guest()
        {
            this.HasFastPassPlus = false;
            this.GuestState = GuestState.Indeterminate;
            this.xPosition = 0.0;
            this.yPosition = 0.0;
            this.EntryTime = new TimeSpan(0, 0, 0);
            this.LoadTime = new TimeSpan(0, 0, 0);
            this.ExitTime = new TimeSpan(0, 0, 0);
        }
    }
}
