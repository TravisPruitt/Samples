using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Windows.Forms.DataVisualization.Charting;

namespace TestConsole
{
    public partial class MainForm : Form
    {
        private WebServer web = null;
        private Sessions sessions = null;
        private PeriodMetrics periodMetrics = null;
        private PeriodMetrics cumMetrics = null;
        private MetricsHistory history = null;

        private MetricsDetails periodDetails = null;
        private MetricsDetails cumDetails = null;
        private string sConfigFile = "defaultconfig.json";

        public MainForm()
        {
            InitializeComponent();
        }

        public MainForm(string[] args) : this()
        {
            if (args.Length==1)
                sConfigFile = args[0];
        }

#region Event Handlers

        private void MainForm_Load(object sender, EventArgs e)
        {
            if (System.IO.File.Exists(sConfigFile))
            {
                Parameters.Instance.LoadConfiguration(sConfigFile);
            }
            this.Text = Parameters.Instance.ConfigurationName;

            // allocation sessions and other data structures
            tbInterval.Text = Parameters.Instance.SamplingInterval.ToString();
            sessions = new Sessions();
            periodMetrics = new PeriodMetrics();
            cumMetrics = new PeriodMetrics();
            history = new MetricsHistory();

            // start up the web server
            web = new WebServer();
            web.SetMetrics(cumMetrics, periodMetrics);
            web.Start(sessions, history);

            // delete old stuff
            System.IO.File.Delete(Parameters.Instance.CaptureFile);
        }

        private void timer_Tick(object sender, EventArgs e)
        {
            // handle any workitems from the web server
            HandleWorkItems();

            lblTime.Text = DateTime.Now.ToString();
            lblUsers.Text = sessions.Count.ToString();
            if (cumMetrics.TimeOfLastSessionStart != DateTime.MinValue)
                lblSecsSinceLastSession.Text = (DateTime.Now - cumMetrics.TimeOfLastSessionStart).TotalSeconds.ToString("N1");
            lblMaxUsers.Text = periodMetrics.MaxSessions.ToString();
            lblStatus.Text = periodMetrics.Status;

            if (periodMetrics.ProcessedSessions > 0)
            {
                lblUsersPerSec.Text = (periodMetrics.ProcessedSessions / (DateTime.Now - periodMetrics.PeriodTime).TotalSeconds).ToString("N2");
                lblCompleted.Text = periodMetrics.ProcessedSessions.ToString();
                lblErrors.Text = periodMetrics.Errors.ToString();

                // if there were no successful sessions, then don't bother
                if (periodMetrics.ProcessedSessions > periodMetrics.Errors)
                {
                    if (periodMetrics.metrics[Parameters.Instance.TotalTime].HaveValue())
                    {
                        lblMaxTotalTime.Text = periodMetrics.metrics[Parameters.Instance.TotalTime].Max.ToString("N2");
                        lblMinTotalTime.Text = periodMetrics.metrics[Parameters.Instance.TotalTime].Min.ToString("N2");
                        lblAvgTotalTime.Text = periodMetrics.metrics[Parameters.Instance.TotalTime].Average.ToString("N2");
                    }
                    else
                    {
                        lblMaxTotalTime.Text = "N/A";
                        lblMinTotalTime.Text = "N/A";
                        lblAvgTotalTime.Text = "N/A";
                    }
                }
                else
                {
                    lblMaxTotalTime.Text = "";
                    lblMinTotalTime.Text = "";
                    lblAvgTotalTime.Text = "";
                }
            }
            else
            {
                lblUsersPerSec.Text = "";
                lblCompleted.Text = "";
                lblErrors.Text = "";
                lblMaxTotalTime.Text = "";
                lblMinTotalTime.Text = "";
                lblAvgTotalTime.Text = "";
            }

            // handle cumulative values
            lblCumUsers.Text = sessions.Count.ToString();
            lblCumMaxUsers.Text = cumMetrics.MaxSessions.ToString();
            if (cumMetrics.ProcessedSessions > 0)
            {
                lblCumUsersPerSec.Text = (cumMetrics.ProcessedSessions / (DateTime.Now - cumMetrics.PeriodTime).TotalSeconds).ToString("N2");
                lblCumCompleted.Text = cumMetrics.ProcessedSessions.ToString();
                lblCumErrors.Text = cumMetrics.Errors.ToString();

                // if only errors, don't show
                if (cumMetrics.ProcessedSessions > cumMetrics.Errors)
                {
                    if (cumMetrics.metrics[Parameters.Instance.TotalTime].HaveValue())
                    {
                        lblCumMaxTotalTime.Text = cumMetrics.metrics[Parameters.Instance.TotalTime].Max.ToString("N2");
                        lblCumMinTotalTime.Text = cumMetrics.metrics[Parameters.Instance.TotalTime].Min.ToString("N2");
                        lblCumAvgTotalTime.Text = cumMetrics.metrics[Parameters.Instance.TotalTime].Average.ToString("N2");
                    }
                    else
                    {
                        lblCumMaxTotalTime.Text = "N/A";
                        lblCumMinTotalTime.Text = "N/A";
                        lblCumAvgTotalTime.Text = "N/A";
                    }
                }
                else
                {
                    lblCumMaxTotalTime.Text = "";
                    lblCumMinTotalTime.Text = "";
                    lblCumAvgTotalTime.Text = "";
                }
            }
            else
            {
                lblCumUsersPerSec.Text = "";
                lblCumCompleted.Text = "";
                lblCumErrors.Text = "";
                lblCumMaxTotalTime.Text = "";
                lblCumMinTotalTime.Text = "";
                lblCumAvgTotalTime.Text = "";
            }

            // update the details windows if present
            if (periodDetails!=null)
                periodDetails.Update(periodMetrics);
            if (cumDetails != null)
                cumDetails.Update(cumMetrics);

            // is it time to start a new sampling interval?
            int secs = (int) (DateTime.Now - periodMetrics.PeriodTime).TotalSeconds;
            if (secs > Parameters.Instance.SamplingInterval)
            {
                // new metrics
                ClearPeriodMetrics();
            }
        }

        private void btnClear_Click(object sender, EventArgs e)
        {
            ClearOptions co = new ClearOptions();
            co.Text = this.Text;
            if (co.ShowDialog(this) == System.Windows.Forms.DialogResult.Cancel)
                return;

            ClearInformation(co.ClearItems);
        }

        private void tbInterval_TextChanged(object sender, EventArgs e)
        {
            string sText = tbInterval.Text.Trim();
            if (sText.Length > 0)
            {
                int n;
                if (!int.TryParse(sText, out n))
                    error.SetError(tbInterval, "Invalid number");
            }
            else
                error.Clear();
        }

        private void tbInterval_KeyPress(object sender, KeyPressEventArgs e)
        {
            if (e.KeyChar == (char)Keys.Return)
            {
                int n;
                string sText = tbInterval.Text.Trim();
                if (int.TryParse(sText, out n))
                {
                    Parameters.Instance.SamplingInterval = n;
                    ClearPeriodMetrics();
                }

                // send focus elsewhere
                lblStatus.Focus();
            }
        }

        private void btnUsers_Click(object sender, EventArgs e)
        {
            UserList ul = new UserList(sessions);
            ul.ShowDialog(this);
        }

        private void btnPeriodDetails_Click(object sender, EventArgs e)
        {
            if (periodDetails == null)
            {
                periodDetails = new MetricsDetails(MetricsDetails.DetailTypeEnum.Period);
                periodDetails.FormClosed += new FormClosedEventHandler(periodDetails_FormClosed);
                periodDetails.Show();
            }

        }

        private void btnCumDetails_Click(object sender, EventArgs e)
        {
            if (cumDetails == null)
            {
                cumDetails = new MetricsDetails(MetricsDetails.DetailTypeEnum.Cumulative);
                cumDetails.FormClosed += new FormClosedEventHandler(cumDetails_FormClosed);
                cumDetails.Show();
            }


        }

        private void btnChart_Click(object sender, EventArgs e)
        {
            ChartControl cc = new ChartControl();
            if (cc.ShowDialog(this) == System.Windows.Forms.DialogResult.OK)
            {
                for (int i = 0; i < Parameters.Instance.CountSplits - 1; i++)
                {
                    Parameters.Instance.SetIsSplitCharted(i, cc.IsSplitCharted(i));
                }

                // reset chart data series

                // delete anything past the first 3 series
                int cSeries = chart.Series.Count;
                if (cSeries > 3)
                {
                    for (int i = 3; i < cSeries; i++)
                        chart.Series.RemoveAt(3);
                }

                // add in the new ones
                for (int i = 0; i < Parameters.Instance.CountSplits - 1; i++)
                {
                    if (Parameters.Instance.GetIsSplitCharted(i))
                    {
                        Series series = new Series();
                        series.ChartArea = "ChartArea1";
                        series.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
                        series.Color = System.Drawing.Color.Empty;
                        series.Legend = "Legend1";
                        series.Name = Parameters.Instance.SplitNames[i];
                        chart.Series.Add(series);

                        // add empty points for alignment
                        for (int ip = 0; ip < chart.Series[0].Points.Count; ip++)
                        {
                            DataPoint dp = new DataPoint();
                            dp.IsEmpty = true;
                            series.Points.Add(dp);
                        }
                    }
                }

            }
        }


        private void periodDetails_FormClosed(object sender, FormClosedEventArgs e)
        {
            periodDetails = null;
        }

        private void cumDetails_FormClosed(object sender, FormClosedEventArgs e)
        {
            cumDetails = null;
        }

#endregion

#region Helpers

        private void HandleWorkItems()
        {
            WorkItem[] awi = web.GetWorkItems();
            foreach (WorkItem wi in awi)
            {
                switch (wi.Task)
                {
                    case WorkItem.TaskEnum.clear:
                        {
                            ClearOptions.ClearItemsEnum cie = ClearOptions.ClearItemsEnum.All;
                            ClearInformation(cie);
                            break;
                        }

                    case WorkItem.TaskEnum.configure:
                        {
                            Parameters.Instance.Reconfigure(wi.Args as string, wi.Args2 as string[]);
                            if (wi.Args != null)
                            {
                                this.Text = wi.Args as string;
                            }
                            break;
                        }
                }
            }
        }

        private void ClearInformation(ClearOptions.ClearItemsEnum cie)
        {
            // swap out the metrics to start with a clean slate
            if ((cie & ClearOptions.ClearItemsEnum.Metrics) == ClearOptions.ClearItemsEnum.Metrics)
                ClearPeriodMetrics();

            // clear the history
            if ((cie & ClearOptions.ClearItemsEnum.History) == ClearOptions.ClearItemsEnum.History)
            {
                history.history.Clear();

                // delete old stuff
                web.CloseLogFile();
                System.IO.File.Delete(Parameters.Instance.CaptureFile);

                // delete the cumulative metrics
                cumMetrics = new PeriodMetrics();
            }

            // forget all the sessions
            if ((cie & ClearOptions.ClearItemsEnum.Sessions) == ClearOptions.ClearItemsEnum.Sessions)
            {
                sessions.sessions.Clear();
            }

            // clear the graphics
            if ((cie & ClearOptions.ClearItemsEnum.Chart) == ClearOptions.ClearItemsEnum.Chart)
            {
                chart.Series[0].Points.Clear();
                chart.Series[1].Points.Clear();
                chart.Series[2].Points.Clear();
            }
        }
        private void ClearPeriodMetrics()
        {
            // archive and chart, but only if have some data
            if (periodMetrics.ProcessedSessions > 0)
            {
                // archive current
                history.AddMetrics(periodMetrics);

                // add data to chart

                // prune if getting too big
                foreach (Series series in chart.Series)
                {
                    if (series.Points.Count >= 100)
                        series.Points.RemoveAt(0);
                }

                // peg total metric and add to chart, but only if not bogus
                TestMetric tm = periodMetrics.metrics[Parameters.Instance.TotalTime];
                if (periodMetrics.ProcessedSessions > periodMetrics.Errors)
                {
                    chart.Series[0].Points.Add(new double[] { tm.Average, tm.Min, tm.Max });
                    chart.Series[1].Points.Add(new double[] { tm.Average });
                    chart.Series[2].Points.Add(new double[] { periodMetrics.MaxSessions });

                    // add in other data
                    int iSeries = 3;
                    for (int i = 0; i < Parameters.Instance.CountSplits - 1; i++)
                    {
                        if (Parameters.Instance.GetIsSplitCharted(i))
                        {
                            if (periodMetrics.metrics[i].HaveValue())
                                chart.Series[iSeries].Points.Add(new double[] { periodMetrics.metrics[i].Average });
                            else
                            {
                                DataPoint dp = new DataPoint();
                                dp.IsEmpty = true;
                                chart.Series[iSeries].Points.Add(dp);
                            }
                            iSeries++;
                        }
                    }

                    chart.Update();
                    CreateChartScreenShot();
                }
            }

            // clear the currentmetrics
            periodMetrics = new PeriodMetrics();
            web.SetMetrics(cumMetrics, periodMetrics);
        }

        private void CreateChartScreenShot()
        {
            try
            {
                if (this.WindowState != FormWindowState.Minimized)
                    chart.SaveImage(Parameters.Instance.CaptureFile, System.Drawing.Imaging.ImageFormat.Png);
                else
                    System.IO.File.Delete(Parameters.Instance.CaptureFile);
            }
            catch (Exception)
            {
            }
        }

#endregion


    }
}
