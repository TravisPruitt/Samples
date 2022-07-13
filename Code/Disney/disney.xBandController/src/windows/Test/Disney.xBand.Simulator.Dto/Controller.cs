using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Controller : DtoObjectBase
    {
        private int controllerID;
        private string controllerName;
        private string controllerUrl;

        [DataMember(Name = "id", Order = 1)]
        public int ControllerID
        {
            get { return this.controllerID; }
            set
            {
                this.controllerID = value;
                OnPropertyChanged("ControllerID");
            }
        }

        [DataMember(Name = "name", Order = 2)]
        public string ControllerName
        {
            get { return this.controllerName; }
            set
            {
                this.controllerName = value;
                OnPropertyChanged("ControllerName");
            }
        }

        [DataMember(Name = "controllerurl", Order = 3)]
        public string ControllerURL
        {
            get { return this.controllerUrl; }
            set
            {
                this.controllerUrl = value;
                OnPropertyChanged("ControllerUrl");
            }
        }

        [DataMember(Name = "attraction", Order = 5)]
        public Attraction Attraction { get; set; }

        [DataMember(Name = "Readers", Order = 6)]
        public List<Reader> Readers { get; set; }
    }
}
