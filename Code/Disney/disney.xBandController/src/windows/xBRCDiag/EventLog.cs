using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace xBRCDiag
{
    public partial class EventLog : Form
    {
        private XBrcChannel channel;
        private long lPos;

        public EventLog()
        {
            InitializeComponent();
        }

        public void Initialize(XBrcChannel channel)
        {
            this.channel = channel;
        }

        private void EventLog_Load(object sender, EventArgs e)
        {
            // first get the current position
            string sPos = channel.get("ekgposition");
            lPos = long.Parse(sPos);
            timer.Enabled = true;
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            string s = string.Format("ekg?position={0}&max=10000", lPos);
            string sLog = channel.get(s);
            string[] asLines = sLog.Split(new char[] { '\n' });
            if (asLines.Length > 0)
            {
                string sPos = asLines[0];
                lPos = long.Parse(sPos);
                for (int i=1; i<asLines.Length; i++)
                    tbEventLog.AppendText(tbEventLog.Text + asLines[i] + Environment.NewLine);
            }
        }

        private void EventLog_FormClosing(object sender, FormClosingEventArgs e)
        {
            timer.Enabled = false;
        }


    }
}
