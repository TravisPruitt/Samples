using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace xTRC
{
    [ComVisible(false)]
    public class TapMessage
    {
        public string MacAddress { get; set; }
        public int EventNumber { get; set; }
        public string SecureID { get; set; }
        public DateTime Time { get; set; }
        public string UID { get; set; }

        public override string ToString()
        {
            return string.Format(
                    @"{{
	                    ""reader name"" : ""{0}"",
	                    ""events"" :
                        [
                            {{
                                ""type"" : ""RFID"",
                                ""eno"" : {1},
                                ""time"" : ""{2}"",
                                ""uid"" : ""{3}"",
                                ""sid"" : ""{4}""
                            }}
                        ]
                    }}",
                        this.MacAddress,
                        this.EventNumber++,
                        DateTime.UtcNow.ToString("yyyy-MM-ddTHH:mm:ss.FFF"),
                        this.UID,
                        this.SecureID);
        }
    }
}
