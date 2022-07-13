using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.ComponentModel;

namespace Disney.xBand.xBMS.Simulator.Dto
{
    public enum SimulationState
    {
        [Description("Not Started")]
        NotStarted = 0,

        [Description("Running")]
        Running = 1,
        
        [Description("Validating")]
        Validating = 2,

        [Description("Completed")]
        Completed = 3,
        
    }
}
