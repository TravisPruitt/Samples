using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace TestConsole
{
    public partial class MetricsDetails : Form
    {
        public enum DetailTypeEnum
        {
            Period,
            Cumulative
        }

        private DetailTypeEnum dt;

        public MetricsDetails()
        {
            InitializeComponent();
            dt = DetailTypeEnum.Period;
        }

        public MetricsDetails(DetailTypeEnum dt) : this()
        {
            this.dt = dt;

            switch (this.dt)
            {
                case DetailTypeEnum.Cumulative:
                    this.Text += ": Cumulative";
                    break;

                case DetailTypeEnum.Period:
                    this.Text += ": Current";
                    break;
            }
        }

        public void Update(PeriodMetrics metrics)
        {
            dg.Rows.Clear();

            // if have no data, don't show ugly stuff
            if (metrics.ProcessedSessions == 0)
                return;

            for (int i=0; i<metrics.metrics.Length; i++)
            {
                dg.Rows.Add(new object[] {  Parameters.Instance.SplitNames[i], 
                                            metrics.metrics[i].Average,
                                            metrics.metrics[i].Max,
                                            metrics.metrics[i].Min });
            }
        }

    }
}
