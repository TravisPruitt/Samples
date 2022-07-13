using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    public class BatchStep : ModelBase<BatchStep>
    {
        public String JobName { get; set; }

        public String StepName { get; set; }

        public String LastCompleted { get; set; }
    }
}
