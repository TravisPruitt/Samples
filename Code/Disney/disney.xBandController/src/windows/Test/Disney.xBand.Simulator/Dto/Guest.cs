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
        Entered = 1,
        InQueue = 2,
        Merging = 3,
        Loading = 4,
        Riding = 5,
        Exited = 6,
        OutOfRange = 7
    }

    public class Guest
    {
        public long GuestID { get; set; }

        public string LastName { get; set; }

        public string FirstName { get; set; }

        public List<MagicBand> MagicBands { get; set; }
    }

    public class GuestQueueItem
    {
        public Guest Guest { get; set; }

        public DateTime QueueDate { get; set; }

        /// <summary>
        ///     Time used to schedule entry from list of available guests.
        /// </summary>
        public DateTime ScheduledEntryTime { get; set; }

        public Dto.Attraction Attraction { get; set; }

        public bool HasFastPassPlus { get; set; }

        public GuestState GuestState { get; set; }

        /// <summary>
        ///     Time guest entered the attraction.
        /// </summary>
        public DateTime? EntryTime { get; set; }

        /// <summary>
        ///     Time guest merged into the load area.
        /// </summary>
        public DateTime? MergeTime { get; set; }

        /// <summary>
        ///     Time the guest loaded onto the ride.
        /// </summary>
        public DateTime? LoadTime { get; set; }

        /// <summary>
        ///     Time guest exited the attraction.
        /// </summary>
        public DateTime? ExitTime { get; set; }

    }

}
