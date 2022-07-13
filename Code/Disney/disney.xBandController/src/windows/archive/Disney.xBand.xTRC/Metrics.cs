using System;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;

namespace Disney.xBand.xTRC
{
    public class Metrics
    {
        public long TotalTicks { get; private set; }
        public long MinimumRequestTicks { get; private set; }
        public long MaximumRequestTicks { get; private set; }
        public long Count { get; private set; }
        private string title;

        public Metrics(string title)
        {
            this.title = title;
            this.TotalTicks = 0;
            this.MinimumRequestTicks = long.MaxValue;
            this.MaximumRequestTicks = 0;
            this.Count = 0;
        }

        public void Update(Stopwatch sw)
        {
            this.TotalTicks += sw.ElapsedTicks;
            if (sw.ElapsedTicks < this.MinimumRequestTicks)
            {
                this.MinimumRequestTicks = sw.ElapsedTicks;
            }
            if (sw.ElapsedTicks > this.MaximumRequestTicks)
            {
                this.MaximumRequestTicks = sw.ElapsedTicks;
            }
            this.Count++;
        }

        public override string ToString()
        {
            StringBuilder sb = new StringBuilder();
            sb.AppendFormat("Processeed {0} {1}", this.Count, this.title);
            sb.AppendLine();
            if (this.Count > 0)
            {
                sb.AppendFormat("Average Request Time: {0} ticks", this.TotalTicks / this.Count);
                sb.AppendLine();
            }
            sb.AppendFormat("Maximum Request Time: {0} ticks", this.MaximumRequestTicks);
            sb.AppendLine();
            sb.AppendFormat("Minimum Request Time: {0} ticks", this.MinimumRequestTicks);
            sb.AppendLine();

            return sb.ToString();
        }
    }
}
