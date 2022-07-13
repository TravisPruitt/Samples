using System;
using System.Collections.Generic;
using System.Text;
using System.Runtime.Serialization;

namespace TestConsole
{
    [DataContract]
    public class TestMetric
    {
        [DataMember(Order = 0)]
        public float Average { get; set; }

        [DataMember(Order = 1)]
        public float Max { get; set; }

        [DataMember(Order = 2)]
        public float Min { get; set; }

        [IgnoreDataMember]
        private float Sum { get; set; }

        [IgnoreDataMember]
        private int Count { get; set; }

        public TestMetric()
        {
            Clear();
        }

        public void ProcessValue(float f)
        {
            // ignore nans
            if (float.IsNaN(f))
                return;

            Count = Count + 1;
            Sum += f;

            if (f < Min)
                Min = f;
            if (f > Max)
                Max = f;
            Average = Sum / Count;
        }

        public void Clear()
        {
            Min = float.MaxValue;
            Max = float.MinValue;
            Average = 0.0f;
            Count = 0;
            Sum = 0.0f;
        }

        public bool HaveValue()
        {
            return Count != 0;
        }

    }
}
