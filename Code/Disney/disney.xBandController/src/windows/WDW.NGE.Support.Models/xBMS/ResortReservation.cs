using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.xBMS
{
    public class ResortReservation : ModelBase<ResortReservation>
    {
        private long facilityId;
        private long travelSegmentId;
        private long travelComponentId;
        private String arrivalDate;
        private String departureDate;

        public long FacilityId
        {
            get { return this.facilityId; }
            set
            {
                this.facilityId = value;
                NotifyPropertyChanged(m => m.FacilityId);

            }
        }

        public long TravelSegmentId
        {
            get { return this.travelSegmentId; }
            set
            {
                this.travelSegmentId = value;
                NotifyPropertyChanged(m => m.TravelSegmentId);

            }
        }

        public long TravelComponentId
        {
            get { return this.travelComponentId; }
            set
            {
                this.travelComponentId = value;
                NotifyPropertyChanged(m => m.TravelComponentId);

            }
        }

        public String ArrivalDate
        {
            get { return this.arrivalDate; }
            set
            {
                this.arrivalDate = value;
                NotifyPropertyChanged(m => m.ArrivalDate);

            }
        }

        public String DepartureDate
        {
            get { return this.departureDate; }
            set
            {
                this.departureDate = value;
                NotifyPropertyChanged(m => m.DepartureDate);

            }
        }
    }
}
