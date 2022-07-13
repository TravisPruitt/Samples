using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Threading;

namespace FPT2LoadTestConsoleTest
{
    public partial class MainForm : Form
    {
        private List<Session> li = new List<Session>();

        public MainForm()
        {
            InitializeComponent();
        }

        private void btnStart_Click(object sender, EventArgs e)
        {
            int count = Convert.ToInt32(numSessions.Value);
            numSessions.Enabled = false;

            // start all the sessions
            for (int i = 0; i < count; i++)
            {
                Session sess = new Session("Session" + i);
                sess.Open();
                li.Add(sess);
            }

            btnStart.Visible = false;
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            timer.Stop();

            int count = Convert.ToInt32(numSessions.Value);

            int cStarted = 0;
            double dSum = 0;
            foreach (Session sess in li)
            {
                if (!double.IsNaN(sess.Duration))
                {
                    cStarted++;
                    dSum += sess.Duration;
                }
            }

            lblSessions.Text = cStarted.ToString();
            if (cStarted>0)
                lblAverageTime.Text = (dSum / cStarted).ToString();

            if (cStarted == count)
            {
                btnClose.Visible = true;
            }
            timer.Start();

        }

        private void btnClose_Click(object sender, EventArgs e)
        {
            foreach (Session sess in li)
            {
                sess.Close();
            }

            li.Clear();
            btnClose.Visible = false;
            btnStart.Visible = true;
        }
    }
}
