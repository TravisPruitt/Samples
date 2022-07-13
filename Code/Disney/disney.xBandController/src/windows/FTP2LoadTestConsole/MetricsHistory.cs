using System;
using System.Runtime.Serialization;
using System.Collections.Generic;
using System.Text;

namespace TestConsole
{
    [DataContract]
    class MetricsHistory
    {
        [DataMember(Order=0)]
        public string[] metricNames;

        [DataMember(Order = 1)]
        public List<PeriodMetrics> history = new List<PeriodMetrics>();

        public MetricsHistory()
        {
            metricNames = Parameters.Instance.SplitNames;
        }

        public void AddMetrics(PeriodMetrics pm)
        {
            history.Add(pm);
        }
    }
}
