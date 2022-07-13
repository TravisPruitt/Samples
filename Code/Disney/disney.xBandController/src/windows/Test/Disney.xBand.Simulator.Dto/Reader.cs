using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Reader : DtoObjectBase
    {
        private int readerID;
        private string readerName;
        private int webPort;
        private bool isActive;
        private string upstreamUrl;
        private int eventsInterval;
        private int maximumEvents;
        private int lastUpstreamEvent;
        private int xcoordinate;
        private int ycoordinate;
        private int range;

        [DataMember(Name = "ReaderId", Order = 1)]
        public int ReaderID
        {
            get { return this.readerID; }
            set
            {
                this.readerID = value;
                OnPropertyChanged("ReaderID");
            }
        }
        [DataMember(Name = "ReaderName", Order = 2)]
        public string ReaderName
        {
            get { return this.readerName; }
            set
            {
                this.readerName = value;
                OnPropertyChanged("ReaderName");
            }
        }

        [DataMember(Name = "Webport", Order = 3)]
        public int WebPort
        {
            get { return this.webPort; }
            set
            {
                this.webPort = value;
                OnPropertyChanged("WebPort");
            }
        }

        [DataMember(Name = "Controller", Order = 4)]
        public Controller Controller { get; set; }

        [DataMember(Name = "IsActive", Order = 5)]
        public bool IsActive
        {
            get { return this.isActive; }
            set
            {
                this.isActive = value;
                OnPropertyChanged("IsActive");
            }
        }

        [DataMember(Name = "UpstreamUrl", Order = 6)]
        public string UpstreamUrl
        {
            get { return this.upstreamUrl; }
            set
            {
                this.upstreamUrl = value;
                OnPropertyChanged("UpstreamUrl");
            }
        }

        [DataMember(Name = "ReaderType", Order = 7)]
        public ReaderType ReaderType { get; set; }

        [DataMember(Name = "EventsInterval", Order = 8)]
        public int EventsInterval
        {
            get { return this.eventsInterval; }
            set
            {
                this.eventsInterval = value;
                OnPropertyChanged("EventsInterval");
            }
        }

        [DataMember(Name = "MaximumEvents", Order = 9)]
        public int MaximumEvents
        {
            get { return this.maximumEvents; }
            set
            {
                this.maximumEvents = value;
                OnPropertyChanged("MaximumEvents");
            }
        }

        [DataMember(Name = "LastUpstreamEvent", Order = 10)]
        public int LastUpstreamEvent
        {
            get { return this.lastUpstreamEvent; }
            set
            {
                this.lastUpstreamEvent = value;
                OnPropertyChanged("LastUpstreamEvent");
            }
        }

        [DataMember(Name = "ReaderLocationType", Order = 11)]
        public ReaderLocationType ReaderLocationType { get; set; }

        [DataMember(Name = "xCoordinate", Order = 12)]
        public int xCoordinate
        {
            get { return this.xcoordinate; }
            set
            {
                this.xcoordinate = value;
                OnPropertyChanged("xCoordinate");
            }
        }
        
        [DataMember(Name = "yCoordinate", Order = 13)]
        public int yCoordinate
        {
            get { return this.ycoordinate; }
            set
            {
                this.ycoordinate = value;
                OnPropertyChanged("yCoordinate");
            }
        }

        [DataMember(Name = "Range", Order = 14)]
        public int Range
        {
            get { return this.range; }
            set
            {
                this.range = value;
                OnPropertyChanged("Range");
            }
        }
    }
}
