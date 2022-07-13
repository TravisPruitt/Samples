using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using com.disney.xband.xbrc.XBRCInfo;

namespace com.disney.xband.xbrc.MobileGxpTest
{
    public partial class MainForm : Form
    {
        XBRCUtil xbrcu = null;

        public MainForm()
        {
            InitializeComponent();
        }

        private void btnTap_Click(object sender, EventArgs e)
        {
            // get the location id
            if (cbLocationId.SelectedItem == null)
            {
                MessageBox.Show(this, "Must specify a location first", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            if (tbBandId.Text.Trim().Length==0)
            {
                MessageBox.Show(this, "Must specify a band id first", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            LocationItem li = cbLocationId.SelectedItem as LocationItem;
            int nLocationId = int.Parse(li.locationInfo.id);
            xbrcu.SetLocationId(nLocationId);

            xbrcu.SendTap(tbBandId.Text.Trim());

        }

        private void Mechanism_CheckedChanged(object sender, EventArgs e)
        {
            if (rbXBRMS.Checked)
            {
                tbXBRMS.Enabled = true;
                cbFacility.Items.Clear();
                cbFacility.Enabled = true;
                tbXBRC.Enabled = false;
            }
            else
            {
                tbXBRMS.Enabled = false;
                cbFacility.Enabled = false;
                tbXBRC.Enabled = true;
            }
        }

        private void cbFacility_DropDown(object sender, EventArgs e)
        {
            // if empty only
            if (cbFacility.Items.Count == 0)
            {
                if (tbXBRMS.Text.Trim().Length == 0)
                {
                    MessageBox.Show(this, "Must specify an xBRMS URL first", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                string sXBRMSUrl = tbXBRMS.Text.Trim();
                if (!sXBRMSUrl.StartsWith("http://"))
                    sXBRMSUrl = "http://" + sXBRMSUrl;
                if (sXBRMSUrl.EndsWith("/"))
                    sXBRMSUrl = sXBRMSUrl.Substring(0, sXBRMSUrl.Length - 1);

                // initialize the library
                xbrcu = new XBRCUtil(sXBRMSUrl);

                // query the xBRMS for the available facilities
                Facility[] af = null;
                try
                {
                    af = xbrcu.GetFacilities();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(this, "Error getting facilities: " + ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                foreach (Facility f in af)
                {
                    FacilityInfo fi = new FacilityInfo(f);
                    cbFacility.Items.Add(fi);
                }
                if (af.Length > 0)
                    cbFacility.SelectedIndex = 0;
            }
        }

        private void cbLocationId_DropDown(object sender, EventArgs e)
        {
            if (cbLocationId.Items.Count == 0)
            {
                string sURL;

                // get the xBRC URL
                if (rbXBRC.Checked)
                {
                    if (xbrcu == null)
                        xbrcu = new XBRCUtil(null);

                    sURL = tbXBRC.Text.Trim();
                    if (!sURL.StartsWith("http://"))
                        sURL = "http://" + sURL;
                    if (sURL.EndsWith("/"))
                        sURL = sURL.Substring(0, sURL.Length - 1);
                }
                else
                {
                    // get from facility drop down
                    if (cbFacility.Text.Trim().Length == 0)
                    {
                        MessageBox.Show(this, "Must select a facility first", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                        return;
                    }
                    FacilityInfo fi = cbFacility.SelectedItem as FacilityInfo;
                    sURL = fi.facility.url;
                }

                // now, get the location's
                xbrcu.Initialize(sURL);

                LocationInfo[] ali = null;
                try
                {
                    ali = xbrcu.GetLocationInfo();
                }
                catch (Exception ex)
                {
                    MessageBox.Show(this, "Error getting location info: " + ex, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                    return;
                }

                foreach (LocationInfo li in ali)
                {
                    LocationItem lit = new LocationItem(li);
                    cbLocationId.Items.Add(lit);
                }

                if (ali.Length > 0)
                    cbLocationId.SelectedIndex = 0;
            }
        }

        private void validate_Tap(object sender, EventArgs e)
        {
            btnTap.Enabled = tbBandId.Text.Trim().Length > 0;
        }

        private void cbLocationId_SelectionChangeCommitted(object sender, EventArgs e)
        {
            tbBandId.Enabled = cbLocationId.SelectedItem != null;
        }

        private void tbXBRC_TextChanged(object sender, EventArgs e)
        {
            cbLocationId.Items.Clear();
        }
    }
}
