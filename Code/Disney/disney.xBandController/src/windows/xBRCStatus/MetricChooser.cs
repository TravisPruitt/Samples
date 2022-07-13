using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace com.disney.xband.xbrc.xBRCStatus
{
    public partial class MetricChooser : Form
    {
        private class ListItem
        {
            public XbrcStatControl.Metric m;

            public ListItem(XbrcStatControl.Metric m)
            {
                this.m = m;
            }

            public override string ToString()
            {
                return XbrcStatControl.dicTitles[m];
            }
        }

        public MetricChooser()
        {
            InitializeComponent();
        }

        public MetricChooser(List<XbrcStatControl.Metric> liShown) : this()
        {
            foreach (XbrcStatControl.Metric m in XbrcStatControl.dicTitles.Keys)
                clb.Items.Add(new ListItem(m), liShown.Contains(m));
        }

        public List<XbrcStatControl.Metric> SelectedMetrics
        {
            get
            {
                List<XbrcStatControl.Metric> li = new List<XbrcStatControl.Metric>();
                foreach (object ci in clb.CheckedItems)
                {
                    if (ci is ListItem)
                        li.Add((ci as ListItem).m);
                }
                return li;
            }
        }

        private void clb_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (clb.CheckedItems.Count < 1 || clb.CheckedItems.Count > 5)
            {
                error.SetError(clb, "Must select between 1 and 5 metrics to show");
                btnOK.Enabled = false;
            }
            else
            {
                error.Clear();
                btnOK.Enabled = true;
            }

        }

    }
}
