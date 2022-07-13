using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.Serialization;

namespace TestConsole
{
    [DataContract]
    public class PeriodMetrics
    {
        [IgnoreDataMember]
        public string Status { get; set; }

        [IgnoreDataMember]
        public DateTime PeriodTime { get; set; }

        [DataMember(Order = 0)]
        public string PeriodStartTime
        {
            get
            {
                return PeriodTime.ToString();
            }
            set
            {
                PeriodTime = DateTime.Parse(value);
            }
        }

        [DataMember(Order = 1)]
        public int ProcessedSessions { get; set; }

        [DataMember(Order = 2)]
        public int Errors { get; set; }

        [DataMember(Order = 3)]
        private int maxSessions = 0;
        public int MaxSessions
        {
            get
            {
                return maxSessions;
            }
            set
            {
                if (value > maxSessions)
                    maxSessions = value;
            }
        }

        [DataMember(Order = 4)]
        public TestMetric[] metrics { get; set; }

        public PeriodMetrics()
        {
            PeriodTime = DateTime.Now;
            TimeOfLastSessionEnd = TimeOfLastSessionStart = DateTime.MinValue;
            metrics = new TestMetric[Parameters.Instance.CountSplits];
            for (int i = 0; i < Parameters.Instance.CountSplits; i++)
                metrics[i] = new TestMetric();

            Status = string.Format("Metrics period running from {0} to {1}", PeriodTime, (PeriodTime + TimeSpan.FromSeconds(Parameters.Instance.SamplingInterval)));
        }

        [IgnoreDataMember]
        public DateTime TimeOfLastSessionStart { get; set; }

        [IgnoreDataMember]
        public DateTime TimeOfLastSessionEnd { get; set; } 

        public void ProcessSplits(float[] splits)
        {
            if (splits.Length != metrics.Length)
                throw new ApplicationException("Incorrect split array length");

            for (int i = 0; i < metrics.Length; i++)
                metrics[i].ProcessValue(splits[i]);
        }

    }
}
