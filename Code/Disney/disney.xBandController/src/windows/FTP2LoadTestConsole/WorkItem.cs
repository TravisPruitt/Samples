using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace TestConsole
{
    public class WorkItem
    {
        public enum TaskEnum
        {
            clear,
            configure
        }

        public object Args { get; set; }
        public object Args2 { get; set; }

        public TaskEnum Task { get; set; }
    }
}
