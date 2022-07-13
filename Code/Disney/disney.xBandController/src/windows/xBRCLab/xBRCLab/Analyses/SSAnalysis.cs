using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Windows.Forms.DataVisualization.Charting;

namespace com.disney.xband.xbrc.xBRCLab.Analyses
{
    public partial class SSAnalysis : UserControl
    {
        private XBrcDataSet ds;

        public SSAnalysis()
        {
            InitializeComponent();
        }

        public SSAnalysis(XBrcDataSet ds) : this()
        {
            this.ds = ds;
        }

        private void SSAnalysis_Load(object sender, EventArgs e)
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

        private void lbReaders_SelectedIndexChanged(object sender, EventArgs e)
        {
            // get the selected readername
            if (lbReaders.SelectedIndex >= 0)
            {
                string sReader = lbReaders.Items[lbReaders.SelectedIndex] as string;
                showReaderInfo(sReader);
            }
        }

        private void showReaderInfo(string sReader)
        {
            DataTable dt = ds.getLrrTable();
            DataRow[] aRow = dt.Select("Reader='" + sReader + "'", "Timestamp ASC");

            // iterate, collecting stats
            int cRows = aRow.Length;
            int[,,] anSSHisto = new int[2,4,65];     // 2 channels of 4 radios * 65 signal strengths
            anSSHisto.Initialize();
            int nSSHistoMax = int.MinValue;
            foreach (DataRow row in aRow)
            {
                // grab data
                int nFrequency = (int) row["Freq"];
                int iChan = (int) row["Channel"];
                int iSS = (int) row["SS"];

                // map the frequency to a slot
                int iFrequency = XBrcDataSet.mapFrequencyToSlot(nFrequency);

                // map signal strength to bucket

                int iBucket = iSS - -100;

                // bump
                anSSHisto[iChan,iFrequency,iBucket]++;
                if (anSSHisto[iChan, iFrequency, iBucket] > nSSHistoMax)
                    nSSHistoMax = anSSHisto[iChan, iFrequency, iBucket];
            }

            // display
            populateChart(f2401, anSSHisto, 0, nSSHistoMax);
            populateChart(f2424, anSSHisto, 1, nSSHistoMax);
            populateChart(f2450, anSSHisto, 2, nSSHistoMax);
            populateChart(f2476, anSSHisto, 3, nSSHistoMax);
            
        }

        private void populateChart(Chart ch, int[, ,] anSSHisto, int iFrequency, int nSSHistoMax)
        {
            ch.ChartAreas[0].AxisY.Maximum = nSSHistoMax + 1;

            ch.Series[0].Points.Clear();
            ch.Series[1].Points.Clear();

            // display
            for (int iSS = 0; iSS < 65; iSS++)
            {
                ch.Series[0].Points.AddXY(iSS, anSSHisto[0, iFrequency, iSS]);
                ch.Series[1].Points.AddXY(iSS, anSSHisto[1, iFrequency, iSS]);
            }
        }
    }
}
