﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml.Serialization;

namespace Disney.xBand.Messages.JMS
{
    public class ExitMessageItem : LoadMessageItem
    {
        [XmlElement("totaltime")]
        public int TotalTime { get; set; }
    }
}
