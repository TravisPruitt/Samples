using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.IO;
using System.Xml;
using com.disney.xband.xbrc.xBRCLab.Analyses;

namespace com.disney.xband.xbrc.xBRCLab
{
    public partial class MainForm : Form
    {
        private string sXBrcAddress = "";
        private XBrcDataSet dsCollecting = null;
        private int iDataSet = 0;
        private DataPullerForm dpf = null;
        private DataTable dtDataSets;
        private string sDataDir = "";
        private XBrcDataSet dsSelected = null;

        // singleton
        public static ReaderInfo readerInfo = null;

        // loaded datasets
        private Dictionary<string, XBrcDataSet> mapDataSets = new Dictionary<string, XBrcDataSet>();

        public MainForm()
        {
            InitializeComponent();

            // initialize data sets
            dtDataSets = new DataTable();
            dtDataSets.Columns.Add("DataSetName", typeof(string));
            dtDataSets.Columns.Add("Date", typeof(DateTime));
            dtDataSets.Columns.Add("DataSetType", typeof(string));
            dtDataSets.Columns.Add("Description", typeof(string));
            dgDataSets.DataSource = dtDataSets;

            // fetch preferences
            RegistryKey rk = Registry.CurrentUser.CreateSubKey("Software\\Disney\\xBRCLab\\Preferences");
            string sVal = (string) rk.GetValue("DataSetDirectory");
            if (sVal != null)
                sDataDir = sVal;
            sVal = (string)rk.GetValue("XBrcAddress");
            if (sVal != null)
            {
                sXBrcAddress = sVal;
                miCollect.Enabled = true;
                this.Text += ": " + sXBrcAddress;
                readerInfo = new ReaderInfo(sVal);
            }
            rk.Close();

            // populate dataset table
            populateGrid();

        }

        private void connectToXBRCToolStripMenuItem_Click(object sender, EventArgs e)
        {
            XBrcOpenForm xo = new XBrcOpenForm(sXBrcAddress);
            if (xo.ShowDialog(this) == DialogResult.OK)
            {
                sXBrcAddress = xo.getAddress();
                miCollect.Enabled = true;

                // store in registry
                RegistryKey rk = Registry.CurrentUser.CreateSubKey("Software\\Disney\\xBRCMap\\Preferences");
                rk.SetValue("XBrcAddress", sXBrcAddress);
                rk.Close();

                // patch title
                int iPos = this.Text.IndexOf(':');
                if (iPos > 0)
                    this.Text = this.Text.Substring(0, iPos) + ": " + sXBrcAddress;
                else
                    this.Text = this.Text + ": " + sXBrcAddress;
            }
        }

        private void miCollect_Click(object sender, EventArgs e)
        {
            // find next available filename
            int iDataSetSave = iDataSet;
            string sDatasetName = findNextDatasetName();

            StartCollectForm scf = new StartCollectForm(sDatasetName);
            iDataSet++;
            if (scf.ShowDialog(this) == DialogResult.OK)
            {
                string sName = scf.getDataSetName();
                string sDesc = scf.getDescription();

                if (scf.getDataSetType() == XBrcDataSet.DataSetType.Raw)
                    dsCollecting = new XBrcDataSet(sName, sDesc, XBrcDataSet.DataSetType.Raw);
                else
                    dsCollecting = new XBrcDataSet(sName, sDesc, XBrcDataSet.DataSetType.Spatial, scf.getFeigSpacing());

                // disable the menu item
                miCollect.Enabled = false;

                // start a data pull
                dpf = new DataPullerForm(sXBrcAddress);
                dpf.dataHandler += new DataPullerForm.DataPullerDataHandler(dpf_dataHandler);
                dpf.doneHandler += new DataPullerForm.DataPullerDoneHandler(dpf_doneHandler);
                dpf.Show(this);
            }
            else
            {
                // restore
                iDataSet = iDataSetSave;
            }
        }

        private string findNextDatasetName()
        {
            for (; ; iDataSet++)
            {
                // if the proposed filename doesn't exist, quit
                string sFilename = "DataSet" + iDataSet.ToString();
                string sPath = Path.Combine(sDataDir, sFilename + ".xml");
                if (!File.Exists(sPath))
                    return sFilename;
            }
       }

        void dpf_doneHandler(bool bCancelled)
        {
            dpf = null;
            miCollect.Enabled = true;

            // store the data set
            dsCollecting.store(sDataDir);

            // keep in the map
            mapDataSets.Add(dsCollecting.getName(), dsCollecting);

            dtDataSets.Rows.Add(new object[] { dsCollecting.getName(), dsCollecting.getDate(), dsCollecting.getDataSetType().ToString(), dsCollecting.getDescription() });

            // zap
            dsCollecting = null;
        }

        void dpf_dataHandler(string[] aData)
        {
            dsCollecting.addRecords(aData);
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void setDataDirectoryToolStripMenuItem_Click(object sender, EventArgs e)
        {
            fbd.Description = "Select folder for data sets";
            fbd.SelectedPath = sDataDir;
            if (fbd.ShowDialog(this) == DialogResult.OK)
            {
                sDataDir = fbd.SelectedPath;

                // store in registry
                RegistryKey rk = Registry.CurrentUser.CreateSubKey("Software\\Disney\\xBRCMap\\Preferences");
                rk.SetValue("DataSetDirectory", sDataDir);
                rk.Close();

            }
        }

        private void miRadioAnalysis_Click(object sender, EventArgs e)
        {
            // get the selected row
            if (dgDataSets.SelectedRows.Count == 1)
            {
                DataGridViewRow row = dgDataSets.SelectedRows[0];
                string sName = row.Cells["DataSetName"].Value as string;

                // make sure dataset is loaded
                loadDataSet(sName);

                // bring up and display the RadioAnalysis form
                SSAnalysis ra = new SSAnalysis(mapDataSets[sName]);
                pnlDSStats.Controls.Clear();
                pnlDSStats.Controls.Add(ra);
                ra.Dock = DockStyle.Fill;
                ra.Show();
            }

        }

        private XBrcDataSet loadDataSet(string sName)
        {
            // already present?
            if (mapDataSets.ContainsKey(sName))
                return mapDataSets[sName];

            // nope
            XBrcDataSet ds = XBrcDataSet.loadXML(Path.Combine(sDataDir, sName + ".xml"));

            // add it
            if (ds != null)
                mapDataSets.Add(sName, ds);

            return ds;
        }

        private void populateGrid()
        {
            // flush existing ones
            dtDataSets.Rows.Clear();

            // flush the cache
            mapDataSets.Clear();

            // iterate through the data directory
            foreach (string sFile in Directory.EnumerateFiles(sDataDir, "*.xml"))
            {
                string sName, sType, sDescription;
                DateTime dt;
                if (XBrcDataSet.isDataSet(sFile, out sName, out dt, out sType, out sDescription))
                {
                    dtDataSets.Rows.Add(new object[] { sName, dt, sType, sDescription });
                }
            }
        }


        private void miDelete_Click(object sender, EventArgs e)
        {
            MessageBox.Show("NYI");
        }

        private void dgDataSets_SelectionChanged(object sender, EventArgs e)
        {
            if (dgDataSets.SelectedRows.Count != 1)
            {
                lblLRReads.Text = "";
                lblTapReads.Text = "";
                lblSingulation.Text = "";
                lblEarliest.Text = "";
                lblLatest.Text = "";
                lblReaders.Text = "";
                dsSelected = null;
                displayHistogram();
                return;
            }

            // get the reader
            DataGridViewRow row = dgDataSets.SelectedRows[0];
            string sName = row.Cells["DataSetName"].Value as string;

            // make sure dataset is loaded
            dsSelected = loadDataSet(sName);

            // display below
            lblLRReads.Text = dsSelected.getLrrTable().Rows.Count.ToString();
            lblTapReads.Text = dsSelected.getTapTable().Rows.Count.ToString();
            lblSingulation.Text = dsSelected.getSingulationTable().Rows.Count.ToString();
            lblEarliest.Text = XBrcDataSet.formatDate2(dsSelected.getEarliestTime());
            lblLatest.Text = XBrcDataSet.formatDate2(dsSelected.getLatestTime());

            // add up readers
            DataTable dt = dsSelected.getLrrTable();
            DataTable dtReaders = dt.DefaultView.ToTable(true, new string[] { "Reader" });
            int cReaders = dtReaders.Rows.Count;
            dt = dsSelected.getTapTable();
            dtReaders = dt.DefaultView.ToTable(true, new string[] { "Reader" });
            cReaders += dtReaders.Rows.Count;
            lblReaders.Text = cReaders.ToString();

            displayHistogram();
        }

        private void displayHistogram()
        {
            if (dsSelected == null)
            {
                lblBucketSize.Text = null;
                chHistogram.Series[0].Points.Clear();
                return;
            }

            // get the width of the chart
            int width = chHistogram.Width;

            // calculate number of buckets
            int cBuckets = width / 6;

            // allocate buckets
            int[] aBuckets = new int[cBuckets];
            aBuckets.Initialize();

            // calcuale bucket time span
            DateTime dtEarliest = dsSelected.getEarliestTime();
            double dblBucketSize = ((dsSelected.getLatestTime() - dsSelected.getEarliestTime()).TotalMilliseconds + 1.0) / cBuckets;
            lblBucketSize.Text = (dblBucketSize / 1000.0).ToString("f2");

            // iterate through llr data
            foreach (DataRow row in dsSelected.getLrrTable().Rows)
            {
                // calculate bucket index
                DateTime dtRow = (DateTime)row["Timestamp"];
                int iBucket = (int)((dtRow - dtEarliest).TotalMilliseconds / dblBucketSize);

                // bump
                aBuckets[iBucket]++;
            }

            // iterate through tap data
            foreach (DataRow row in dsSelected.getTapTable().Rows)
            {
                // calculate bucket index
                DateTime dtRow = (DateTime)row["Timestamp"];
                int iBucket = (int)((dtRow - dtEarliest).TotalMilliseconds / dblBucketSize);

                // bump
                aBuckets[iBucket]++;
            }

            // iterate through singulation data
            foreach (DataRow row in dsSelected.getSingulationTable().Rows)
            {
                // calculate bucket index
                DateTime dtRow = (DateTime)row["Timestamp"];
                int iBucket = (int)((dtRow - dtEarliest).TotalMilliseconds / dblBucketSize);

                // bump
                aBuckets[iBucket]++;
            }

            // add the points to the chart
            chHistogram.Series[0].Points.Clear();
            for (int i = 0; i < cBuckets; i++)
            {
                // calculate time offset
                double dblX = (i * dblBucketSize) / 1000;
                chHistogram.Series[0].Points.AddXY(dblX, aBuckets[i]);
            }
                

        }

        private void chHistogram_SizeChanged(object sender, EventArgs e)
        {
            displayHistogram();
        }

        private void miSignalStrengthHistogram_Click(object sender, EventArgs e)
        {
            // create the radio analysis control
            SSAnalysis ra = new SSAnalysis(dsSelected);

            // create the form
            AnalysisForm af = new AnalysisForm("Signal Strength Histogram Analysis: " + dsSelected.getName());
            af.Controls.Add(ra);
            ra.Dock = DockStyle.Fill;
            af.Show(this);

        }

        private void miRadioHealth_Click(object sender, EventArgs e)
        {
            PacketAnalysis pa = new PacketAnalysis(dsSelected);

            // create the form
            AnalysisForm af = new AnalysisForm("Radio Health Analysis: " + dsSelected.getName());
            af.Controls.Add(pa);
            pa.Dock = DockStyle.Fill;
            af.Show(this);
        }

        private void signalStrengthVDistanceToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SSvDistance ss = new SSvDistance(dsSelected);

            // create the form
            AnalysisForm af = new AnalysisForm("Signal Strength v. Distance Analysis: " + dsSelected.getName());
            af.Controls.Add(ss);
            ss.Dock = DockStyle.Fill;
            af.Show(this);
        }

        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (dgDataSets.SelectedRows.Count == 0)
                return;

            // get name of selected dataset
            string sDataset = dgDataSets.SelectedRows[0].Cells["DataSetName"].Value as String;
            if (MessageBox.Show(this, "Delete data set " + sDataset, "xBRCLab", MessageBoxButtons.OKCancel) == System.Windows.Forms.DialogResult.OK)
            {
                string sPath = Path.Combine(sDataDir, sDataset + ".xml");
                File.Delete(sPath);
                populateGrid();
            }
        }

    }
}
