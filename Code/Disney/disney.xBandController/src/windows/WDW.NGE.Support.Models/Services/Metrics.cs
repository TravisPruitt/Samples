using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Diagnostics;

namespace WDW.NGE.Support.Models.Services
{
    public class Metrics
    {
        public long TotalRequestTime { get; private set; }
        public long MinimumRequestTime { get; private set; }
        public long MaximumRequestTime { get; private set; }
        public long Count { get; private set; }
        private string title;

        public Metrics(string title)
        {
            this.title = title;
            this.TotalRequestTime = 0;
            this.MinimumRequestTime = long.MaxValue;
            this.MaximumRequestTime = 0;
            this.Count = 0;
        }

        public void Update(Stopwatch sw)
        {
            lock (this)
            {
                this.TotalRequestTime += sw.ElapsedMilliseconds;
                if (sw.ElapsedMilliseconds < this.MinimumRequestTime)
                {
                    this.MinimumRequestTime = sw.ElapsedMilliseconds;
                }
                if (sw.ElapsedMilliseconds > this.MaximumRequestTime)
                {
                    this.MaximumRequestTime = sw.ElapsedMilliseconds;
                }
                this.Count++;
            }
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat("Processeed {0} {1}", this.Count, this.title);
            sb.AppendLine();
            if (this.Count > 0)
            {
                sb.AppendFormat("Average Request Time: {0} milliseconds", this.TotalRequestTime / this.Count);
                sb.AppendLine();
            }
            sb.AppendFormat("Maximum Request Time: {0} milliseconds", this.MaximumRequestTime);
            sb.AppendLine();
            sb.AppendFormat("Minimum Request Time: {0} milliseconds", this.MinimumRequestTime);
            sb.AppendLine();

            return sb.ToString();
        }
    }
}
