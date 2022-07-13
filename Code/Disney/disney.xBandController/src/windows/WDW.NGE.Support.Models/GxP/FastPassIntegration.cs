using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using SimpleMvvmToolkit;

namespace WDW.NGE.Support.Models.GxP
{
    public class FastPassIntegration : ModelBase<FastPassIntegration>
    {
        public List<BatchStep> BatchSteps { get; set; }

        public AttractionHeartbeat MagicKingdomHeartbeat { get; set; }

        public AttractionHeartbeat HollywoodStudiosHeartbeat { get; set; }

        public AttractionHeartbeat EpcotHeartbeat { get; set; }

        public AttractionHeartbeat AnimalKingdomHeartbeat { get; set; }
    }
}
