using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.Serialization;

namespace Disney.xBand.Simulator.Dto
{
    [DataContract]
    public class Controller
    {
        [DataMember(Name = "id", Order = 1)]
        public int ControllerID { get; set; }

        [DataMember(Name = "name", Order = 2)]
        public string ControllerName { get; set; }

        [DataMember(Name = "controllerurl", Order = 3)]
        public string ControllerURL { get; set; }

        //[DataMember(Name = "xviewlocation", Order = 4)]
        //public xViewLocation xViewLocation { get; set; }

        [DataMember(Name = "Readers", Order = 5)]
        public List<Reader> Readers { get; set; }
    }
}
