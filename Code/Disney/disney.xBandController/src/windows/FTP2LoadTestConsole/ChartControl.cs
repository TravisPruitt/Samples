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
    public partial class ChartControl : Form
    {
        public ChartControl()
        {
            InitializeComponent();
        }

        private void ChartControl_Load(object sender, EventArgs e)
        {
            clbMetrics.Items.Clear();
            for (int i = 0; i < (Parameters.Instance.CountSplits - 1); i++)
            {
                clbMetrics.Items.Add(Parameters.Instance.SplitNames[i]);
                clbMetrics.SetItemChecked(i, Parameters.Instance.GetIsSplitCharted(i));
            }
        }

        public bool IsSplitCharted(int i)
        {
            return clbMetrics.GetItemChecked(i);
        }
    }
}
