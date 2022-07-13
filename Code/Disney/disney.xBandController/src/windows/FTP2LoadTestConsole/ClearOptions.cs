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
    public partial class ClearOptions : Form
    {
        [Flags]
        public enum ClearItemsEnum
        {
            Sessions = 0x01,
            History = 0x02,
            Metrics = 0x04,
            Chart = 0x08,
            All = 0x0F
        }

        public ClearOptions()
        {
            InitializeComponent();
        }

        public ClearItemsEnum ClearItems
        {
            get
            {
                ClearItemsEnum cie = new ClearItemsEnum();
                if (cbSessions.Checked)
                    cie |= ClearItemsEnum.Sessions;
                if (cbMetrics.Checked)
                    cie |= ClearItemsEnum.Metrics;
                if (cbHistory.Checked)
                    cie |= ClearItemsEnum.History;
                if (cbChart.Checked)
                    cie |= ClearItemsEnum.Chart;
                return cie;
            }
        }

    }
}
