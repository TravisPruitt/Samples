using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Xml.Serialization;
using System.Threading;
using System.Windows.Forms.DataVisualization.Charting;

namespace com.disney.xband.xbrc.xBRCStatus
{
    public partial class XbrcStatControl : UserControl
    {
        // delegates and events
        public delegate void XbrcStatusProgressHandler(int nPercemt);
        public event XbrcStatusProgressHandler statusProgress;

        // xBRC to which this is pointing
        private string sXbrcURL;
        private XBrcChannel channel = null;

        // latest status 
        private XbrcStatus xbrcStatus = null;

        // statistics
        public enum Metric
        {
            Events,
            EventAge,
            IDMSQuery,
            EKGWrite,
            Singulation,
            PreModeling,
            Modeling,
            PostModeling,
            External,
            WriteToReader,
            SaveGST,
            Upstream,
            MainLoopUtilization,
            Model1,
            Model2,
            Model3
        };

        public static Dictionary<Metric, string> dicTitles = new Dictionary<Metric, string>();

        private List<Metric> liMetricsToShow = new List<Metric>();

        static XbrcStatControl()
        {
            dicTitles.Add(Metric.Events, "Pending Events");
            dicTitles.Add(Metric.EventAge, "Age of Oldest Pending Event(msec)");
            dicTitles.Add(Metric.IDMSQuery, "IDMS Queries (msec/call)");
            dicTitles.Add(Metric.EKGWrite, "Writing to EKG Log (msec/event)");
            dicTitles.Add(Metric.Singulation, "Singulation (msec/event)");
            dicTitles.Add(Metric.PreModeling, "Model Preprocessing (msec/call)");
            dicTitles.Add(Metric.Modeling, "Modeling (msec/aggregated-event)");
            dicTitles.Add(Metric.PostModeling, "Model Postprocessing (msec/call)");
            dicTitles.Add(Metric.External, "External Processing (msec/call)");
            dicTitles.Add(Metric.WriteToReader, "Writing to Readers (msec/send)");
            dicTitles.Add(Metric.SaveGST, "Saving GST (msec/save)");
            dicTitles.Add(Metric.Upstream, "Sending Messages Upstream (msec/message)");
            dicTitles.Add(Metric.MainLoopUtilization, "Main Loop Utilization (%)");
            dicTitles.Add(Metric.Model1, "Model Metric 1");
            dicTitles.Add(Metric.Model2, "Model Metric 2");
            dicTitles.Add(Metric.Model3, "Model Metric 3");
        }

        public XbrcStatControl()
        {
            InitializeComponent();

            // set defaults
            liMetricsToShow.Add(Metric.Events);
            liMetricsToShow.Add(Metric.MainLoopUtilization);
            liMetricsToShow.Add(Metric.IDMSQuery);
            liMetricsToShow.Add(Metric.Upstream);

            setupCharts();
        }

        [Browsable(false), DesignerSerializationVisibility(
                                    DesignerSerializationVisibility.Hidden)]

        public List<Metric> ShownMetrics
        {
            get
            {
                // make a copy of the list
                List<Metric> li = new List<Metric>();
                foreach (Metric m in liMetricsToShow)
                    li.Add(m);
                return li;
            }
            set
            {
                liMetricsToShow.Clear();
                foreach (Metric m in value)
                    liMetricsToShow.Add(m);
                setupCharts();
            }
        }

        public string XbrcURL
        {
            set
            {
                if (value == null)
                {
                    this.sXbrcURL = value;
                }
                else if (value != sXbrcURL || !bw.IsBusy)
                {
                    this.sXbrcURL = value;
                    startPoll();
                }
            }
            get
            {
                return this.sXbrcURL;
            }
        }

        public string Status
        {
            get
            {
                lock (this)
                {
                    if (xbrcStatus == null)
                        return "";
                    else
                        return xbrcStatus.status;
                }
            }
        }

        public string StatusMessage
        {
            get
            {
                lock (this)
                {
                    if (xbrcStatus == null)
                        return "";
                    else
                        return xbrcStatus.statusMessage;
                }
            }
        }

        public XbrcStatus XbrcStatus
        {
            get
            {
                lock (this)
                {
                    return xbrcStatus;
                }
            }
        }

        public static Metric ParseMetric(string s)
        {
            return (Metric) Enum.Parse(typeof(Metric), s);
        }

        private void startPoll()
        {
            // stop the background worker (if active)
            if (bw.IsBusy)
                bw.CancelAsync();

            channel = new XBrcChannel(sXbrcURL);

            // clear out any old data
            foreach(Control c in table.Controls)
            {
                if (c is Chart)
                {
                    Chart ch = c as Chart;
                    ch.Series[0].Points.Clear();
                    ch.Series[1].Points.Clear();
                }
            }

            // start the background worker
            bw.RunWorkerAsync();
        }

        private void setupCharts()
        {
            if (liMetricsToShow.Count==0)
                throw new Exception("Empty metrics list!");

            // set rows
            table.Controls.Clear();
            table.RowCount = liMetricsToShow.Count;
            for (int i=0; i<liMetricsToShow.Count; i++)
            {
                table.Controls.Add(newChart(dicTitles[liMetricsToShow[i]]), 0, i); ;
                table.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F/liMetricsToShow.Count));
            }
        }

        private bool updateStatus()
        {
            // get the current status
            string sXML = channel.get("status");
            if (sXML == null)
                return false;

            StringReader sr = new StringReader(sXML);
            XmlSerializer s = new XmlSerializer(typeof(XbrcStatus));
            lock (this)
            {
                xbrcStatus = (XbrcStatus)s.Deserialize(sr);
            }
            return true;
        }

        private void bw_DoWork(object sender, DoWorkEventArgs e)
        {

            // now, loop, asking for status each time we go through
            for (; ; )
            {
                // are we being cancelled?
                if (bw.CancellationPending)
                    return;

                // update the status variable
                if (!updateStatus())
                {
                    bw.ReportProgress(0, "Unable to communicate with xBRC");
                    return;
                }

                bw.ReportProgress(100);

                // parse the last perf time
                DateTime dtNow = DateTime.Parse(xbrcStatus.time, null, System.Globalization.DateTimeStyles.AssumeUniversal);
                DateTime dtStartPerf = DateTime.Parse(xbrcStatus.startPerfTime, null, System.Globalization.DateTimeStyles.AssumeUniversal);

                // want to sample just before the end of the next perf time (give ourselves two seconds of slop time)
                DateTime dtNextEndPerf = dtStartPerf + TimeSpan.FromSeconds(2 * xbrcStatus.perfMetricsPeriod - 2);
                int csecDelay = (int) (dtNextEndPerf - dtNow).TotalSeconds;

                // wait until next time
                for (int i = 0; i < csecDelay; i++)
                {
                    // are we being cancelled?
                    if (bw.CancellationPending)
                        return;

                    Thread.Sleep(1000);

                    // calculate pctage done, but don't let it be zero since that indicates an error
                    int iPct = (int)((i + 1) * 100.0 / csecDelay);
                    if (iPct == 0)
                        iPct = 1;
                    bw.ReportProgress(iPct);
                }
            }

        }

        private void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            if (e.ProgressPercentage == 0)
            {
                MessageBox.Show(this, e.UserState as string, "xBRC Status", MessageBoxButtons.OK);
            }
            else
            {
                if (e.ProgressPercentage == 100)
                {
                    // prune data if getting too large
                    foreach(Control c in table.Controls)
                    {
                        if (c is Chart)
                        {
                            Chart ch = c as Chart;
                            if (ch.Series[0].Points.Count >= 100)
                            {
                                ch.Series[0].Points.RemoveAt(0);
                                ch.Series[1].Points.RemoveAt(0);
                            }
                        }
                    }

                    // update our charts
                    for (int i = 0; i < liMetricsToShow.Count; i++)
                    {
                        Chart ch = table.GetControlFromPosition(0, i) as Chart;
                        double mean, min, max;
                        getDataValues(liMetricsToShow[i], out mean, out min, out max);
                        ch.Series[0].Points.Add(new double[] { mean, min, max });
                        ch.Series[1].Points.Add(new double[] { mean });
                    }
                }

                statusProgress(e.ProgressPercentage);
            }
        }

        private void getDataValues(Metric metric, out double mean, out double min, out double max)
        {
            switch (metric)
            {
                case Metric.Events:
                    {
                        mean = xbrcStatus.perfEvents.mean;
                        min = xbrcStatus.perfEvents.min;
                        max = xbrcStatus.perfEvents.max;
                        break;
                    }

                case Metric.EventAge:
                    {
                        mean = xbrcStatus.perfEventAgeMsec.mean;
                        min = xbrcStatus.perfEventAgeMsec.min;
                        max = xbrcStatus.perfEventAgeMsec.max;
                        break;
                    }

                case Metric.IDMSQuery:
                    {
                        mean = xbrcStatus.perfIDMSQueryMsec.mean;
                        min = xbrcStatus.perfIDMSQueryMsec.min;
                        max = xbrcStatus.perfIDMSQueryMsec.max;
                        break;
                    }

                case Metric.EKGWrite:
                    {
                        mean = xbrcStatus.perfEKGWriteMsec.mean;
                        min = xbrcStatus.perfEKGWriteMsec.min;
                        max = xbrcStatus.perfEKGWriteMsec.max;
                        break;
                    }

                case Metric.Singulation:
                    {
                        mean = xbrcStatus.perfSingulationMsec.mean;
                        min = xbrcStatus.perfSingulationMsec.min;
                        max = xbrcStatus.perfSingulationMsec.max;
                        break;
                    }

                case Metric.PreModeling:
                    {
                        mean = xbrcStatus.perfPreModelingMsec.mean;
                        min = xbrcStatus.perfPreModelingMsec.min;
                        max = xbrcStatus.perfPreModelingMsec.max;
                        break;
                    }

                case Metric.Modeling:
                    {
                        mean = xbrcStatus.perfModelingMsec.mean;
                        min = xbrcStatus.perfModelingMsec.min;
                        max = xbrcStatus.perfModelingMsec.max;
                        break;
                    }

                case Metric.PostModeling:
                    {
                        mean = xbrcStatus.perfPostModelingMsec.mean;
                        min = xbrcStatus.perfPostModelingMsec.min;
                        max = xbrcStatus.perfPostModelingMsec.max;
                        break;
                    }

                case Metric.External:
                    {
                        mean = xbrcStatus.perfExternalMsec.mean;
                        min = xbrcStatus.perfExternalMsec.min;
                        max = xbrcStatus.perfExternalMsec.max;
                        break;
                    }

                case Metric.WriteToReader:
                    {
                        mean = xbrcStatus.perfWriteToReaderMsec.mean;
                        min = xbrcStatus.perfWriteToReaderMsec.min;
                        max = xbrcStatus.perfWriteToReaderMsec.max;
                        break;
                    }

                case Metric.SaveGST:
                    {
                        mean = xbrcStatus.perfSaveGSTMsec.mean;
                        min = xbrcStatus.perfSaveGSTMsec.min;
                        max = xbrcStatus.perfSaveGSTMsec.max;
                        break;
                    }

                case Metric.Upstream:
                    {
                        mean = xbrcStatus.perfUpstreamMsec.mean;
                        min = xbrcStatus.perfUpstreamMsec.min;
                        max = xbrcStatus.perfUpstreamMsec.max;
                        break;
                    }

                case Metric.MainLoopUtilization:
                    {
                        mean = xbrcStatus.perfMainLoopUtilization.mean;
                        min = xbrcStatus.perfMainLoopUtilization.min;
                        max = xbrcStatus.perfMainLoopUtilization.max;
                        break;
                    }

                case Metric.Model1:
                    {
                        mean = xbrcStatus.perfModel1.mean;
                        min = xbrcStatus.perfModel1.min;
                        max = xbrcStatus.perfModel1.max;
                        break;
                    }

                case Metric.Model2:
                    {
                        mean = xbrcStatus.perfModel2.mean;
                        min = xbrcStatus.perfModel2.min;
                        max = xbrcStatus.perfModel2.max;
                        break;
                    }

                case Metric.Model3:
                    {
                        mean = xbrcStatus.perfModel3.mean;
                        min = xbrcStatus.perfModel3.min;
                        max = xbrcStatus.perfModel3.max;
                        break;
                    }

                default:
                    throw new Exception("Unknown metric!");
            }
        }

        private Chart newChart(string sTitle)
        {
            Chart chart = new Chart();

            ChartArea chartArea = new ChartArea();
            Series series1 = new Series();
            Series series2 = new Series();
            Title title = new Title();

            chartArea.AxisX.MajorGrid.Enabled = false;
            chartArea.AxisY.MajorGrid.Enabled = false;
            chartArea.Name = "ChartArea1";
            chart.ChartAreas.Add(chartArea);
            chart.Dock = System.Windows.Forms.DockStyle.Fill;
            chart.Location = new System.Drawing.Point(3, 3);
            chart.Name = "chart1";
            series1.ChartArea = "ChartArea1";
            series1.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.ErrorBar;
            series1.CustomProperties = "MinPixelPointWidth=10, ErrorBarCenterMarkerStyle=Diamond, PixelPointWidth=10, Max" +
                "PixelPointWidth=10";
            series1.Legend = "Legend1";
            series1.MarkerColor = System.Drawing.Color.FromArgb(((int)(((byte)(0)))), ((int)(((byte)(0)))), ((int)(((byte)(192)))));
            series1.MarkerSize = 10;
            series1.Name = "Series1";
            series1.YValuesPerPoint = 3;
            series2.ChartArea = "ChartArea1";
            series2.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series2.Name = "Series2";
            chart.Series.Add(series1);
            chart.Series.Add(series2);
            chart.Size = new System.Drawing.Size(891, 128);
            chart.TabIndex = 0;
            chart.Text = "chart1";
            title.Font = new System.Drawing.Font("Microsoft Sans Serif", 8F, System.Drawing.FontStyle.Bold);
            title.Name = "Title1";
            title.Text = sTitle;
            chart.Titles.Add(title);

            return chart;
        }

    }
}
