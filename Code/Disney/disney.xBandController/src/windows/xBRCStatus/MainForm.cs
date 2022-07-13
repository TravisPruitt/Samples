using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;

namespace com.disney.xband.xbrc.xBRCStatus
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();

            xsc.statusProgress += new XbrcStatControl.XbrcStatusProgressHandler(xsc_statusProgress);

            // fetch preferences
            RegistryKey rk = Registry.CurrentUser.CreateSubKey("Software\\Disney\\xBRCStatus\\Preferences");
            string sXbrcVal = (string)rk.GetValue("XBrcAddress");
            string sShownMetrics = (string)rk.GetValue("ShownMetrics");
            rk.Close();

            // set metrics
            if (sShownMetrics != null)
            {
                string[] asMetric = sShownMetrics.Split(new char[] { ',' });
                List<XbrcStatControl.Metric> liShown = new List<XbrcStatControl.Metric>();
                foreach (string sMetric in asMetric)
                {
                    XbrcStatControl.Metric m = XbrcStatControl.ParseMetric(sMetric);
                    if (m != null)
                        liShown.Add(m);
                }
                if (liShown.Count > 0)
                    xsc.ShownMetrics = liShown;
            }
            if (sXbrcVal != null)
                tbxBRC.Text = sXbrcVal;


        }

        void xsc_statusProgress(int nPercentage)
        {
            pBar.Value = nPercentage;
            if (nPercentage == 100)
            {
                lblStatus.Text = xsc.Status;
                lblStatusMessage.Text = xsc.StatusMessage;
            }
        }

        private void tbxBRC_Leave(object sender, EventArgs e)
        {
            if (tbxBRC.Text.Trim().Length > 0)
                selectXBRC(tbxBRC.Text.Trim());
        }

        private void tbxBRC_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Enter && tbxBRC.Text.Trim().Length > 0)
                selectXBRC(tbxBRC.Text.Trim());
        }

        private void selectXBRC(string s)
        {
            xsc.XbrcURL = s;

            // store in registry
            RegistryKey rk = Registry.CurrentUser.CreateSubKey("Software\\Disney\\xBRCStatus\\Preferences");
            rk.SetValue("XBrcAddress", s);
            rk.Close();
        }

        private void btnConfig_Click(object sender, EventArgs e)
        {
            List<XbrcStatControl.Metric> liShow = xsc.ShownMetrics;
            MetricChooser ms = new MetricChooser(liShow);
            if (ms.ShowDialog(this) == DialogResult.OK)
            {
                xsc.ShownMetrics = ms.SelectedMetrics;

                string sShownMetrics = "";
                foreach (XbrcStatControl.Metric m in xsc.ShownMetrics)
                {
                    if (sShownMetrics.Length > 0)
                        sShownMetrics += "," + m.ToString();
                    else
                        sShownMetrics = m.ToString();
                }

                // store in registry
                RegistryKey rk = Registry.CurrentUser.CreateSubKey("Software\\Disney\\xBRCStatus\\Preferences");
                rk.SetValue("ShownMetrics", sShownMetrics);
                rk.Close();

            }

        }


    }
}
