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
    public partial class SSvDistance : UserControl
    {
        private const double AVERAGE_METERS_PER_SECOND = 1.2;
        private XBrcDataSet ds;

        public SSvDistance()
        {
            InitializeComponent();
        }

        public SSvDistance(XBrcDataSet ds)
            : this()
        {
            this.ds = ds;
        }

        private void SSvDistance_Load(object sender, EventArgs e)
        {
            // verify data set type
            if (ds.getDataSetType() != XBrcDataSet.DataSetType.Spatial)
            {
                MessageBox.Show(this, "This analysis requires a Spatial data set. The chosen data set is of type 'Raw'.", "xBRC Lab", MessageBoxButtons.OK, MessageBoxIcon.Error);
                ((Form)this.Parent).Close();
            }

            // verify we have some fiegs data

            // get the list of readers
            DataTable dt = ds.getLrrTable();
            DataTable dtReaders = dt.DefaultView.ToTable(true, new string[] { "Reader" });
            int cFeigs = 0;
            foreach (DataRow dr in dtReaders.Rows)
            {
                string sReader = dr["Reader"] as string;
                if (sReader.StartsWith("Feig"))
                    cFeigs++;
            }

            if (cFeigs == 0 || ds.getTapTable().Rows.Count==0)
            {
                MessageBox.Show(this, "This Spatial data set does not contain any Feigs reader data.", "xBRC Lab", MessageBoxButtons.OK, MessageBoxIcon.Error);
                ((Form)this.Parent).Close();
            }

            updateAnalysis();
        }

        private void updateAnalysis()
        {
            // remove all data from the chart
            for (int i = 0; i < chSS.Series.Count; i++)
                chSS.Series[i].Points.Clear();

            // walk through the LRR events, associating them with distances inferred from contemporary tap events
            DataView dv = ds.getLrrTable().DefaultView;
            string sLRIDCur = null;
            string sReaderCur = null;
            int pnoCur = int.MinValue;
            int nMeanSSSum = 0;
            int nMeanSSCount = 0;
            int cRadioCount = 0;
            int nPeakSS = int.MinValue;
            int iChannelCur = int.MinValue;
            for (int i = 0; i < dv.Count; i++)
            {
                DataRowView row = dv[i];

                // these are sorted in this order: Timestamp ASC, LRID ASC, Pno ASC, Reader ASC, Channel AS
                DateTime dt = (DateTime)row["Timestamp"];
                string sGuest = row["Guest"] as string;
                string sLRID = row["LRID"] as string;
                int pno = (int)row["Pno"];
                string sReader = row["Reader"] as string;
                int ss = (int) row["SS"];
                int iChannel = (int)row["Channel"];

                // get the distance associated with this time
                double x = getDistanceAtTimestamp(dt, sGuest);

                // emit a raw data point
                chSS.Series["Raw"].Points.AddXY(x, ss);

                // if there's a break, emit a data point
                if (sLRIDCur!=sLRID || pnoCur!=pno)
                {
                    if (pnoCur!=int.MinValue)
                    {
                        // emit mean data
                        chSS.Series["MeanSS"].Points.AddXY(x, (double) nMeanSSSum/nMeanSSCount);

                        // emit peak data
                        chSS.Series["PeakSS"].Points.AddXY(x, (double)nPeakSS);

                        // emit radio count
                        chSS.Series["RadioCount"].Points.AddXY(x, (double)cRadioCount);
                    }

                    // zero
                    sLRIDCur = sLRID;
                    pnoCur = pno;
                    nMeanSSSum = nMeanSSCount = 0;
                    cRadioCount = 0;
                    nPeakSS = int.MinValue;
                }

                // append to means and peaks
                nMeanSSSum += ss;
                nMeanSSCount++;
                if (ss > nPeakSS)
                    nPeakSS = ss;

                if (sReaderCur != sReader)
                {
                    cRadioCount++;
                    sReaderCur = sReader;
                    iChannelCur = iChannel;
                }
                else if (iChannelCur != iChannel)
                {
                    cRadioCount++;
                    iChannelCur = iChannel;
                }
            }
        }

        private double getDistanceAtTimestamp(DateTime dt, string sGuest)
        {
            // search through taps looking for neighboring times, then interpolate
            DateTime dtBefore = DateTime.MinValue;
            DateTime dtAfter = DateTime.MaxValue;
            double xBefore = int.MinValue;
            double xAfter = int.MaxValue;
            DataView dv = ds.getTapTable().DefaultView;
            foreach (DataRowView row in dv)
            {
                string sGuestRow = row["Guest"] as string;

                // discard if wrong guest
                if (sGuestRow != sGuest)
                    continue;

                DateTime dtRow = (DateTime)row["Timestamp"];
                string sReader = row["Reader"] as string;
                string sNum = sReader.Substring("Feig".Length);
                int nNum=int.MinValue;
                if (!int.TryParse(sNum, out nNum))
                    throw new Exception("Invalid Feig reader name");

                if (dtRow <= dt)
                {
                    dtBefore = dtRow;
                    xBefore = nNum * ds.getFeigSpacing();
                }
                else if (dtRow > dt)
                {
                    dtAfter = dtRow;
                    xAfter = nNum * ds.getFeigSpacing();
                    break;
                }
            }

            // interpolate according to the data we have
            if (xBefore > int.MinValue && xAfter < int.MaxValue)
            {
                // have both
                return xBefore + getTimeRatio(dt, dtBefore, dtAfter) * ds.getFeigSpacing();

            }
            else if (xBefore == int.MinValue && xAfter < int.MaxValue)
            {
                // have after but no before - assume a speed
                // TODO: figure out speed based on other values!
                double dblSeconds = (dtAfter - dt).TotalMilliseconds / 1000.0;
                double xDelta = dblSeconds * AVERAGE_METERS_PER_SECOND;
                return xAfter - xDelta;
            }
            else if (xBefore > int.MinValue && xAfter == int.MaxValue)
            {
                // have before but not after
                // TODO: figure out speed based on other values!
                double dblSeconds = (dt - dtBefore).TotalMilliseconds / 1000.0;
                double xDelta = dblSeconds * AVERAGE_METERS_PER_SECOND;
                return xBefore + xDelta;
            }
            else
            {
                // have neither!
                throw new Exception("No tap data present!");
            }
        }

        private double getReaderDistance(string sReader)
        {
            if (!sReader.StartsWith("Feig"))
                throw new Exception("Not a Feigs reader: " + sReader);

            string sNum = sReader.Substring("Feig".Length);
            int nNum = int.MinValue;
            if (!int.TryParse(sNum, out nNum))
                throw new Exception("Invalid Feig reader name");

            return nNum * ds.getFeigSpacing();
        }

        private double getTimeRatio(DateTime dt, DateTime dtBefore, DateTime dtAfter)
        {
            double dTotalSpan = (double) (dtAfter - dtBefore).TotalMilliseconds;
            double dBeforeSpan = (double)(dt - dtBefore).TotalMilliseconds;
            return dBeforeSpan / dTotalSpan;
        }

        private List<DataRow> findLrrEvents(DateTime dt)
        {
            DataTable dtLrr = ds.getLrrTable();
            DataRow[] aRow = dtLrr.Select("Timestamp >= '" + (dt - TimeSpan.FromMilliseconds(500.0)).ToString() + "' AND Timestamp <='" + (dt + TimeSpan.FromMilliseconds(500.0)).ToString() + "'");

            return aRow.ToList<DataRow>();
        }

    }
}
