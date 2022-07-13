using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace com.disney.xband.xbrc.xBRCLab
{
    public partial class StartCollectForm : Form
    {
        public StartCollectForm()
        {
            InitializeComponent();
        }

        public StartCollectForm(string sDataSetName)
            : this()
        {
            tbDataSet.Text = sDataSetName;
        }

        public string getDataSetName()
        {
            return tbDataSet.Text.Trim();
        }

        public String getDescription()
        {
            return tbDescription.Text;
        }

        public XBrcDataSet.DataSetType getDataSetType()
        {
            if (rbRaw.Checked)
                return XBrcDataSet.DataSetType.Raw;
            else if (rbSpatial.Checked)
                return XBrcDataSet.DataSetType.Spatial;
            else
                return XBrcDataSet.DataSetType.Raw;         // default
        }

        public int getFeigSpacing()
        {
            if (!rbSpatial.Checked)
                throw new Exception("Feig spacing only valid for Spatial data sets");
            return int.Parse(tbSpacing.Text);
        }

        private void tbDataSet_TextChanged(object sender, EventArgs e)
        {
            btnOk.Enabled = tbDataSet.Text.Trim().Length != 0;
        }

        private void rbType_CheckedChanged(object sender, EventArgs e)
        {
            tbSpacing.Enabled = rbSpatial.Checked;
        }

        private void tbSpacing_Validating(object sender, CancelEventArgs e)
        {
            int n;
            btnOk.Enabled = int.TryParse(tbSpacing.Text, out n);
        }

        private void tbSpacing_TextChanged(object sender, EventArgs e)
        {
            this.Validate();
        }
    }
}
