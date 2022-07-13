using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace com.disney.xband.xbrc.xBRCLab.Analyses
{
    public partial class PacketAnalysis : UserControl
    {
        class Packet
        {
            public string sBand;
            public DateTime dt;
            public int Pno;

            public Packet(string sBand, DateTime dt, int Pno)
            {
                this.sBand = sBand;
                this.dt = dt;
                this.Pno = Pno;
            }
        };

        private XBrcDataSet ds;
        private int cLostPackets = 0;

        // selections
        private List<Packet> lipSel;
        private List<string> lirSel;

        // used in analysis function
        private enum AnalysisState
        {
            Searching,
            Processing,
            Done
        };

        public PacketAnalysis()
        {
            InitializeComponent();
        }

        public PacketAnalysis(XBrcDataSet ds)
            : this()
        {
            this.ds = ds;
        }

        private void PacketAnalysis_Load(object sender, EventArgs e)
        {
            // get the list of readers
            DataTable dt = ds.getLrrTable();
            DataTable dtReaders = dt.DefaultView.ToTable(true, new string[] { "Reader" });
            foreach (DataRow dr in dtReaders.Rows)
            {
                lbReaders.Items.Add(dr["Reader"]);
            }

            // select the first
            if (lbReaders.Items.Count > 0)
                lbReaders.SelectedIndex = 0;

        }

        private void lbBands_SelectedIndexChanged(object sender, EventArgs e)
        {
            // get the list of selected bands
            List<string> liBands = new List<string>();
            foreach (int i in lbBands.SelectedIndices)
                liBands.Add(lbBands.Items[i] as string);

            updateAnalysis();
        }

        private void dgPackets_SelectionChanged(object sender, EventArgs e)
        {
            // get list of selected packets
            lipSel = new List<Packet>();
            foreach (DataGridViewRow row in dgPackets.SelectedRows)
            {
                Packet p = row.Tag as Packet;
                if (p != null)
                    lipSel.Add(row.Tag as Packet);
            }
            if (lipSel.Count == 0)
                return;

            // get list of bands
            List<string> lib = new List<string>();
            foreach (Packet p in lipSel)
            {
                if (!lib.Contains(p.sBand))
                    lib.Add(p.sBand);
            }
            lbBands.DataSource = lib;

            // select the first band
            lbBands.SelectedItem = 0;
        }


        private void lbReaders_SelectedIndexChanged(object sender, EventArgs e)
        {
            // get the selected readers
            lirSel = new List<string>();
            foreach(int i in lbReaders.SelectedIndices)
                lirSel.Add(lbReaders.Items[i] as string);

            // populate the packets for the selected readers
            DataTable dtLrr = ds.getLrrTable();
            DataView dv = dtLrr.DefaultView;

            string sLRIDCur = null;
            int pnoCur = -1;
            dgPackets.Rows.Clear();
            foreach (DataRowView dr in dv)
            {
                string sReader = dr["Reader"] as string;
                string sLRID = dr["LRID"] as string;
                int pno = (int) dr["Pno"];

                // is this a band we're interested in?
                if (!lirSel.Contains(sReader))
                    continue;

                // change in band?
                if (sLRID != sLRIDCur ||
                    pno != pnoCur)
                {
                    DateTime dt = (DateTime)dr["Timestamp"];
                    double dsec = (dt - ds.getEarliestTime()).TotalMilliseconds / 1000.0;
                    DataGridViewRow row = new DataGridViewRow();
                    DataGridViewCell cell = new DataGridViewTextBoxCell();
                    cell.Value = dsec.ToString("f3");
                    row.Cells.Add(cell);
                    cell = new DataGridViewTextBoxCell();
                    cell.Value = sLRID;
                    row.Cells.Add(cell);
                    cell = new DataGridViewTextBoxCell();
                    cell.Value = pno.ToString();
                    row.Cells.Add(cell);
                    row.Tag = new Packet(sLRID, dt, pno);
                    dgPackets.Rows.Add(row);
                    pnoCur = pno;
                    sLRIDCur = sLRID;
                }
            }

            // select the first
            if (dgPackets.Rows.Count > 0)
                dgPackets.Rows[0].Selected = true;

        }

        private void updateAnalysis()
        {
            // clear everything
            dgAnalysis.Rows.Clear();

            // anything to do?
            if (lirSel.Count == 0 || lipSel.Count == 0)
                return;

            // for each packet selected, find all events from selected readers
            cLostPackets = 0;
            foreach (Packet p in lipSel)
                updatePacketInfo(p);
            lblLostPackets.Text = cLostPackets.ToString();
        }

        private void updatePacketInfo(Packet p)
        {
            // iterate through the data until we find the packet
            DataTable dtLrr = ds.getLrrTable();
            DataView dv = dtLrr.DefaultView;

            int[,] aSS = new int [2, 4];       // two channels times 4 frequencies
            aSS.Initialize();
            string sReaderCur = null;
            AnalysisState state = AnalysisState.Searching;
            foreach (DataRowView dr in dv)
            {
                DateTime dt = (DateTime)dr["Timestamp"];
                string sLRID = dr["LRID"] as string;
                int pno = (int)dr["Pno"];
                string sReader = dr["Reader"] as string;
                int iFrequency = XBrcDataSet.mapFrequencyToSlot((int)dr["Freq"]);
                int iChannel = (int)dr["Channel"];

                switch(state)
                {
                    case AnalysisState.Searching:
                    {
                        if (dt == p.dt && sLRID == p.sBand && pno == p.Pno && lirSel.Contains(sReader))
                        {
                            // intitialize
                            aSS.Initialize();
                            aSS[iChannel, iFrequency] = (int)dr["SS"];
                            sReaderCur = sReader;

                            // change state
                            state = AnalysisState.Processing;
                        }
                        break;
                    }

                    case AnalysisState.Processing:
                    {
                        if (dt == p.dt && sLRID == p.sBand && pno == p.Pno)
                        {
                            // reader break?
                            if (sReader != sReaderCur)
                            {
                                // emit row
                                double dTime = (dt - ds.getEarliestTime()).TotalMilliseconds / 1000.0;
                                updateLostPackets(aSS);
                                dgAnalysis.Rows.Add(new object[] {  dTime.ToString("f2"), 
                                                                    p.sBand, 
                                                                    pno.ToString(),
                                                                    sReaderCur,
                                                                    aSS[0, 0].ToString(),
                                                                    aSS[1, 0].ToString(),
                                                                    aSS[0, 1].ToString(),
                                                                    aSS[1, 1].ToString(),
                                                                    aSS[0, 2].ToString(),
                                                                    aSS[1, 2].ToString(),
                                                                    aSS[0, 3].ToString(),
                                                                    aSS[1, 3].ToString()
                                                                 });

                                // is the next reader on the list?
                                if (lirSel.Contains(sReader))
                                {
                                    // yes, initialize and process
                                    aSS.Initialize();
                                    aSS[iChannel, iFrequency] = (int)dr["SS"];
                                    sReaderCur = sReader;
                                }
                                else
                                {
                                    // go back to searching
                                    state = AnalysisState.Searching;
                                }

                            }
                            else
                            {
                                // tally and keep processing
                                aSS[iChannel, iFrequency] = (int)dr["SS"];
                            }
                        }
                        else
                        {
                            // emit row
                            double dTime = (dt - ds.getEarliestTime()).TotalMilliseconds / 1000.0;
                            updateLostPackets(aSS);
                            dgAnalysis.Rows.Add(new object[] {  dTime.ToString("f2"), 
                                                                    p.sBand, 
                                                                    p.Pno.ToString(),
                                                                    sReaderCur,
                                                                    aSS[0, 0].ToString(),
                                                                    aSS[1, 0].ToString(),
                                                                    aSS[0, 1].ToString(),
                                                                    aSS[1, 1].ToString(),
                                                                    aSS[0, 2].ToString(),
                                                                    aSS[1, 2].ToString(),
                                                                    aSS[0, 3].ToString(),
                                                                    aSS[1, 3].ToString()
                                                                 });

                            // change state
                            state = AnalysisState.Done;
                        }

                        break;
                    }

                    case AnalysisState.Done:
                    {
                        break;
                    }
                }

                // if we're done, break out
                if (state == AnalysisState.Done)
                    break;
            }
        }

        private void updateLostPackets(int[,] aSS)
        {
            for (int i=0; i<4; i++)
                if ( (aSS[0,i]==0 && aSS[1,i]!=0) ||
                     (aSS[0,i]!=0 && aSS[1,i]==0))
                {
                    cLostPackets++;
                }
        }

    }
}

