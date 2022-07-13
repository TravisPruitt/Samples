using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Attraction : DtoObjectBase
    {
        private int attractionID;
        private string attractionName;
        private decimal mergeRatio;
        private int guestsPerHour;
        private bool tapOnly;
        private decimal standByBandRatio;
        private int standByArrivalRate;
        private int fastPassPlusArrivalRate;

        [DataMember(Name="id", Order=1)]
        public int AttractionID 
        {
            get { return this.attractionID; }
            set 
            {
                this.attractionID = value;
                OnPropertyChanged("AttractionID");
            }
        }

        [DataMember(Name = "name", Order = 2)]
        public string AttractionName 
        {
            get { return this.attractionName; }
            set
            {
                this.attractionName = value;
                OnPropertyChanged("AttractionName");
            }
        }

        [DataMember(Name = "mergeratio", Order = 3)]
        public decimal MergeRatio
        {
            get { return this.mergeRatio; }
            set
            {
                this.mergeRatio = value;
                OnPropertyChanged("MergeRatio");
            }
        }

        [DataMember(Name = "guestsperhour", Order = 4)]
        public int GuestsPerHour
        {
            get { return this.guestsPerHour; }
            set
            {
                this.guestsPerHour = value;
                OnPropertyChanged("GuestsPerHour");
            }
        }

        [DataMember(Name = "TapOnly", Order = 8)]
        public bool TapOnly
        {
            get { return this.tapOnly; }
            set
            {
                this.tapOnly = value;
                OnPropertyChanged("TapOnly");
            }
        }

        [DataMember(Name = "StandByBandRatio", Order = 9)]
        public decimal StandByBandRatio
        {
            get { return this.standByBandRatio; }
            set
            {
                this.standByBandRatio = value;
                OnPropertyChanged("StandByBandRatio");
            }
        }

        [DataMember(Name = "StandByArrivalRate", Order = 10)]
        public int StandByArrivalRate
        {
            get { return this.standByArrivalRate; }
            set
            {
                this.standByArrivalRate = value;
                OnPropertyChanged("StandByArrivalRate");
            }
        }

        [DataMember(Name = "FastPassPlusArrivalRate", Order = 11)]
        public int FastPassPlusArrivalRate
        {
            get { return this.fastPassPlusArrivalRate; }
            set
            {
                this.fastPassPlusArrivalRate = value;
                OnPropertyChanged("FastPassPlusArrivalRate");
            }
        }

        [DataMember(Name = "controller", Order = 12)]
        public Controller Controller { get; set; }

    }
}