using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Xml.Serialization;
using System.IO;

namespace xBRCDiag
{
    public partial class MainForm : Form
    {
        private XBrcChannel channel = null;

        public MainForm()
        {
            InitializeComponent();
        }

        private void btnConnect_Click(object sender, EventArgs e)
        {
            channel = new XBrcChannel(tbXBrc.Text);
            if (!UpdateStatus())
            {
                MessageBox.Show(this, "Error connecting to xBRC", this.Text, MessageBoxButtons.OK);
                return;
            }
            btnConnect.Hide();
            tbXBrc.Enabled = false;
            btnDisconnect.Show();
            gbStatus.Enabled = true;
            timer.Enabled = true;
        }

        private void EnableUI(bool bEnable)
        {
            if (bEnable)
            {
                btnConnect.Hide();
                tbXBrc.Enabled = false;
                btnDisconnect.Show();
                gbStatus.Enabled = true;
                timer.Enabled = true;
            }
            else
            {
                timer.Enabled = false;
                btnDisconnect.Hide();
                btnConnect.Show();
                gbStatus.Enabled = false;
            }
        }

        private bool UpdateStatus()
        {
            string sStatus = channel.get("status");
            if (sStatus == null)
                return false;

            XmlSerializer ser = new XmlSerializer(typeof(XBrcStatus));
            StringReader sr = new StringReader(sStatus);
            XBrcStatus s = ser.Deserialize(sr) as XBrcStatus;
            if (s.status.ToUpper() == "GREEN")
                lblCondition.BackColor = Color.Green;
            else if (s.status.ToUpper() == "YELLON")
                lblCondition.BackColor = Color.Yellow;
            else if (s.status.ToUpper() == "RED")
                lblCondition.BackColor = Color.Red;
            lblStatusMessage.Text = s.statusMessage;

            // do consistency check for message ids
            if ((s.lastMessageSeq < s.lastMessageToJMS) ||
                (s.lastMessageSeq < s.lastMessageToUpdateStream))
            {
                lblCondition.BackColor = Color.Red;
                lblStatusMessage.Text = "ERROR! xBRC Status table has bad entry, won't send messages";
            }

            lblVersion.Text = s.version;

            lblLastMsgSeq.Text = s.lastMessageSeq.ToString();
            lblLastMsgToJMS.Text = s.lastMessageToJMS.ToString();
            lblLastMsgToHTTP.Text = s.lastMessageToUpdateStream.ToString();

            lblMainLoop.Text = s.perfMainLoopUtilization.mean.ToString();
            lblIDMSTime.Text = s.perfIDMSQueryMsec.mean.ToString();
            lblEventCount.Text = s.perfEvents.mean.ToString();

            return true;
        }

        private void tbXBrc_TextChanged(object sender, EventArgs e)
        {
            btnConnect.Enabled = tbXBrc.Text.Trim().Length > 0;
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            if (!UpdateStatus())
            {
                EnableUI(false);
                this.Text = "xBRC Diagnostic Tool";
            }
            else
                this.Text = "xBRC Diagnostic Tool: " + DateTime.Now.ToLongTimeString();
        }

        private void btnDisconnect_Click(object sender, EventArgs e)
        {
            EnableUI(false);
        }

        private void btnEventLog_Click(object sender, EventArgs e)
        {
            EventLog el = new EventLog();
            el.Initialize(channel);
            el.ShowDialog(this);
        }

        private void MainForm_FormClosed(object sender, FormClosedEventArgs e)
        {
            EnableUI(false);
        }

    }
}
