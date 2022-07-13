using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TestConsole
{
    public class TestSession
    {
        public DateTime SessionStartTime { get; set; }

        public TestSession()
        {
            SessionStartTime = DateTime.Now;
        }
    }
}
