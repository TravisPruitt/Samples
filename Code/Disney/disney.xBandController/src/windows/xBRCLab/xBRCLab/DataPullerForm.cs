using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Threading;
using System.Windows.Forms.DataVisualization.Charting;

namespace com.disney.xband.xbrc.xBRCLab
{
    public partial class DataPullerForm : Form
    {
        // define delegate and event
        public delegate void DataPullerDataHandler(string[] aData);
        public delegate void DataPullerDoneHandler(bool bCancelled);

        public event DataPullerDataHandler dataHandler;
        public event DataPullerDoneHandler doneHandler;

        private bool bPaused = false;
        private long lRecords = 0;

        private String sLRIDSelected = null;
        private int nPnoCurrent = -1;
        private Dictionary<string, DataGridViewRow> mapLRID2Row = new Dictionary<string, DataGridViewRow>();
        private Dictionary<string, int> mapReader2SS = null;
        private Dictionary<string, int> mapReader2SSThreshold = null;

        public DataPullerForm()
        {
            InitializeComponent();
        }

        public DataPullerForm(string sXbrcIPAddress) : this()
        {
            lblAddress.Text = sXbrcIPAddress;
        }

        private void start()
        {
            if (bw.IsBusy)
                throw new Exception("Data puller is already busy!");

            bw.RunWorkerAsync(lblAddress.Text);
        }

        private void stop(bool bHalt)
        {
            if (!bw.IsBusy)
                return;

            // set the flag
            bPaused = !bHalt;
            if (bHalt)
                lblStatus.Text = "Stopping";
            else
                lblStatus.Text = "Paused";

            bw.CancelAsync();

            // wait for the thread to die
            DateTime dtStart = DateTime.Now;
            for (; ; )
            {
                if (!bw.IsBusy)
                    break;

                // out of time?
                if ((DateTime.Now - dtStart) > TimeSpan.FromSeconds(5))
                    break;

                // yield
                Application.DoEvents();
                // Thread.Sleep(100);
            }
        }

        private void btnStop_Click(object sender, EventArgs e)
        {
            stop(true);
            doneHandler(false);
            this.Close();
        }

        #region background worker work function (runs on separate thread)
        private void bw_DoWork(object sender, DoWorkEventArgs e)
        {
            XBrcChannel chan = new XBrcChannel(lblAddress.Text);

            bw.ReportProgress(0, "Getting reader info");
            List<string> liReaders = MainForm.readerInfo.getReaderNames();

            liReaders.Sort();
            mapReader2SS = new Dictionary<string, int>();
            mapReader2SSThreshold = new Dictionary<string, int>();
            foreach (string s in liReaders)
            {
                mapReader2SS.Add(s + "-0", 0);
                mapReader2SS.Add(s + "-1", 0);
                mapReader2SSThreshold.Add(s, MainForm.readerInfo.getReaderInfo(s).threshold);
            }

            // get the current EKG position
            bw.ReportProgress(0, "Marking");
            string sPosition = chan.get("ekgposition");
            if (string.IsNullOrEmpty(sPosition))
            {
                bw.ReportProgress(0, "Failed!");
                return;
            }
            long lPosition = long.Parse(sPosition);

            for (; ; )
            {
                if (bw.CancellationPending)
                    break;

                // get some data
                bw.ReportProgress(0, "Reading");
                string sData = chan.get("ekg?position=" + lPosition.ToString() + "&max=1000");

                // parse out the next location
                if (!string.IsNullOrEmpty(sData))
                {
                    string[] aLines = sData.Split(new char[] { '\n' }, 2);
                    if (aLines.Length > 0)
                        lPosition = long.Parse(aLines[0]);  // this will just parse the first line

                    // push all data up
                    if (aLines.Length == 2)
                        bw.ReportProgress(1, aLines[1]);
                }

                // yield
                bw.ReportProgress(0, "Waiting");
                Thread.Sleep(100);
            }

        }
        #endregion

        #region background worker event handler functions (run on separate thread)

        private void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            string sData = e.UserState as string;
            if (e.ProgressPercentage == 0)
                lblStatus.Text = sData;
            else if (e.ProgressPercentage == 1)
            {
                string[] aLines = sData.Split(new char[] { '\n' });
                lRecords += aLines.Length;
                lblRecords.Text = lRecords.ToString();
                RealTimeDisplay(aLines);
                dataHandler(aLines);
            }
      }

        private void RealTimeDisplay(string[] aLines)
        {
            foreach (string sLine in aLines)
            {
                // crack the fields
                string[] aParts = sLine.Split(new char[] { ',' });

                if (aParts.Length < 3)
                    continue;               // ignore bad lines

                // handle various types
                string sRecType = aParts[1];
                switch (sRecType)
                {
                    case "LRR":
                        {
                            handleLrrRecord(aParts);
                            break;
                        }

                    case "PROCESS":
                        {
                            handleProcess();
                            break;
                        }

                    case "SNG":
                        {
                            handleSngRecords(aParts);
                            break;
                        }

                    default:
                        // ignore
                        break;
                }
            }
        }

        private void handleProcess()
        {
        }

        private void handleSngRecords(string[] aParts)
        {
            if (aParts.Length != 5)
                throw new Exception("Invalid SNG data!");

            long lTIme;
            if (!long.TryParse(aParts[0], out lTIme))
                throw new Exception("Invalid SNG data timestamp!");

            // calculate UTC time
            DateTime dtEpoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            DateTime dt = dtEpoch + TimeSpan.FromMilliseconds(lTIme);
            string sLocation = aParts[2];
            string sLRID = aParts[3];
            string sBasis = aParts[4];

            // update datagrid
            if (mapLRID2Row.ContainsKey(sLRID))
            {
                DataGridViewRow row = mapLRID2Row[sLRID];
                row.Cells["SingulationLocation"].Value = sLocation;
                if (sLRID == sLRIDSelected)
                    lblLocation.Text = sLocation;
            }

        }

        private void handleLrrRecord(string[] aParts)
        {
            if (aParts.Length != 9)
                throw new Exception("Invalid LRR data!");

            long lTIme;
            if (!long.TryParse(aParts[0], out lTIme))
                throw new Exception("Invalid LRR data timestamp!");

            // calculate UTC time
            DateTime dtEpoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            DateTime dt = dtEpoch + TimeSpan.FromMilliseconds(lTIme);

            string sReader = aParts[2];
            string sGuest = aParts[3];
            string sLRID = aParts[4];
            int nPno, nSS, nFreq, nChan;
            if (!int.TryParse(aParts[5], out nPno) ||
                !int.TryParse(aParts[6], out nSS) ||
                !int.TryParse(aParts[7], out nFreq) ||
                !int.TryParse(aParts[8], out nChan))
            {
                throw new Exception("Invalid LRR data!");
            }

            // have we seen this LRID before?
            DataGridViewRow row = null;
            if (!mapLRID2Row.ContainsKey(sLRID))
            {
                // add a data row to the datagrid
                int iRow = dgBands.Rows.Add(new object[] { sLRID, "" });
                row = dgBands.Rows[iRow];
                mapLRID2Row.Add(sLRID, row);
            }
            else
            {
                // clear the location
                row = mapLRID2Row[sLRID];
                row.Cells["SingulationLocation"].Value = "";
                if (sLRID == sLRIDSelected)
                    lblLocation.Text = "";
            }

            // if the data is for the selected band, chart it
            if (sLRID == sLRIDSelected)
            {
                // if a different sequence number clear the data
                if (nPno != nPnoCurrent)
                {
                    chSS.Series[0].Points.Clear();

                    List<string> liKeys = mapReader2SS.Keys.ToList();
                    foreach (String sr in liKeys)
                    {
                        int ss = mapReader2SS[sr];
                        int iPoint = chSS.Series[0].Points.AddXY(sr, new object[] { ss });
                        if (ss < mapReader2SSThreshold[sr.Substring(0, sr.Length-2)])
                            chSS.Series[0].Points[iPoint].Color = Color.LightBlue;
                        else
                            chSS.Series[0].Points[iPoint].Color = Color.Blue;
                        mapReader2SS[sr] = 0;
                    }

                    nPnoCurrent = nPno;
                }

                // ignore readers we don't know
                if (nChan == 0)
                {
                    if (mapReader2SS.ContainsKey(sReader + "-0"))
                        mapReader2SS[sReader + "-0"] = nSS;
                }
                else
                {
                    if (mapReader2SS.ContainsKey(sReader + "-1"))
                        mapReader2SS[sReader + "-1"] = nSS;
                }

            }

        }


        private void bw_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            if (bPaused)
                bPaused = false;
        }

#endregion

        private void btnGo_Click(object sender, EventArgs e)
        {
            // change the  buttons
            btnGo.Visible = false;
            btnPause.Enabled = true;
            btnPause.Visible = true;

            // start the sampling
            start();
        }

        private void btnPause_Click(object sender, EventArgs e)
        {
            // change the  buttons
            btnPause.Enabled = false;
            btnGo.Visible = true;

            // stop sampling
            stop(false);

            // change the  buttons
            btnPause.Enabled = true;
            btnPause.Visible = false;
            btnGo.Visible = true;

        }

        private void dgBands_SelectionChanged(object sender, EventArgs e)
        {
            if (dgBands.SelectedRows.Count == 0)
                sLRIDSelected = null;
            else
            {
                sLRIDSelected = dgBands.SelectedRows[0].Cells["LRID"].Value as string;
                chSS.Series[0].Points.Clear();
            }
        }

        private void DataPullerForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            stop(true);
            doneHandler(false);
        }

    }
}
