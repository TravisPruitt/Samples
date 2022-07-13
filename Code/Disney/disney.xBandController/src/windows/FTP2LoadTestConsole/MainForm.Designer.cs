namespace TestConsole
{
    partial class MainForm
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.Windows.Forms.DataVisualization.Charting.ChartArea chartArea1 = new System.Windows.Forms.DataVisualization.Charting.ChartArea();
            System.Windows.Forms.DataVisualization.Charting.Legend legend1 = new System.Windows.Forms.DataVisualization.Charting.Legend();
            System.Windows.Forms.DataVisualization.Charting.Series series1 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series2 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Series series3 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.Windows.Forms.DataVisualization.Charting.Title title1 = new System.Windows.Forms.DataVisualization.Charting.Title();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.label1 = new System.Windows.Forms.Label();
            this.lblTime = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.lblStatus = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.lblUsers = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.lblMaxTotalTime = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.lblAvgTotalTime = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.lblMinTotalTime = new System.Windows.Forms.Label();
            this.chart = new System.Windows.Forms.DataVisualization.Charting.Chart();
            this.btnClear = new System.Windows.Forms.Button();
            this.timer = new System.Windows.Forms.Timer(this.components);
            this.label7 = new System.Windows.Forms.Label();
            this.lblCompleted = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.tbInterval = new System.Windows.Forms.TextBox();
            this.error = new System.Windows.Forms.ErrorProvider(this.components);
            this.btnPeriodDetails = new System.Windows.Forms.Button();
            this.btnUsers = new System.Windows.Forms.Button();
            this.label10 = new System.Windows.Forms.Label();
            this.label11 = new System.Windows.Forms.Label();
            this.lblErrors = new System.Windows.Forms.Label();
            this.lblMaxUsers = new System.Windows.Forms.Label();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabCumulative = new System.Windows.Forms.TabPage();
            this.btnCumDetails = new System.Windows.Forms.Button();
            this.label19 = new System.Windows.Forms.Label();
            this.label9 = new System.Windows.Forms.Label();
            this.lblCumUsersPerSec = new System.Windows.Forms.Label();
            this.lblCumUsers = new System.Windows.Forms.Label();
            this.lblCumAvgTotalTime = new System.Windows.Forms.Label();
            this.label14 = new System.Windows.Forms.Label();
            this.label15 = new System.Windows.Forms.Label();
            this.label16 = new System.Windows.Forms.Label();
            this.label17 = new System.Windows.Forms.Label();
            this.label18 = new System.Windows.Forms.Label();
            this.lblCumMinTotalTime = new System.Windows.Forms.Label();
            this.lblCumErrors = new System.Windows.Forms.Label();
            this.lblCumMaxTotalTime = new System.Windows.Forms.Label();
            this.lblCumMaxUsers = new System.Windows.Forms.Label();
            this.label23 = new System.Windows.Forms.Label();
            this.lblCumCompleted = new System.Windows.Forms.Label();
            this.tabCurrent = new System.Windows.Forms.TabPage();
            this.label13 = new System.Windows.Forms.Label();
            this.lblUsersPerSec = new System.Windows.Forms.Label();
            this.label12 = new System.Windows.Forms.Label();
            this.lblSecsSinceLastSession = new System.Windows.Forms.Label();
            this.btnChart = new System.Windows.Forms.Button();
            ((System.ComponentModel.ISupportInitialize)(this.chart)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.error)).BeginInit();
            this.tabControl1.SuspendLayout();
            this.tabCumulative.SuspendLayout();
            this.tabCurrent.SuspendLayout();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(107, 11);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(33, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Time:";
            // 
            // lblTime
            // 
            this.lblTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblTime.Location = new System.Drawing.Point(146, 10);
            this.lblTime.Name = "lblTime";
            this.lblTime.Size = new System.Drawing.Size(187, 22);
            this.lblTime.TabIndex = 1;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(413, 33);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(40, 13);
            this.label2.TabIndex = 4;
            this.label2.Text = "Status:";
            // 
            // lblStatus
            // 
            this.lblStatus.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.lblStatus.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblStatus.Location = new System.Drawing.Point(459, 32);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(334, 22);
            this.lblStatus.TabIndex = 5;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(15, 9);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(74, 13);
            this.label3.TabIndex = 0;
            this.label3.Text = "Current Users:";
            // 
            // lblUsers
            // 
            this.lblUsers.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblUsers.Location = new System.Drawing.Point(95, 8);
            this.lblUsers.Name = "lblUsers";
            this.lblUsers.Size = new System.Drawing.Size(85, 22);
            this.lblUsers.TabIndex = 1;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(10, 32);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(83, 13);
            this.label4.TabIndex = 6;
            this.label4.Text = "Max Total Time:";
            // 
            // lblMaxSessionTime
            // 
            this.lblMaxTotalTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblMaxTotalTime.Location = new System.Drawing.Point(95, 30);
            this.lblMaxTotalTime.Name = "lblMaxSessionTime";
            this.lblMaxTotalTime.Size = new System.Drawing.Size(85, 22);
            this.lblMaxTotalTime.TabIndex = 7;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(10, 53);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(82, 13);
            this.label5.TabIndex = 9;
            this.label5.Text = "Avg Total Time:";
            // 
            // lblAvgSessionTime
            // 
            this.lblAvgTotalTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblAvgTotalTime.Location = new System.Drawing.Point(95, 52);
            this.lblAvgTotalTime.Name = "lblAvgSessionTime";
            this.lblAvgTotalTime.Size = new System.Drawing.Size(85, 22);
            this.lblAvgTotalTime.TabIndex = 10;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(10, 75);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(80, 13);
            this.label6.TabIndex = 11;
            this.label6.Text = "Min Total Time:";
            // 
            // lblMinSessionTime
            // 
            this.lblMinTotalTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblMinTotalTime.Location = new System.Drawing.Point(95, 74);
            this.lblMinTotalTime.Name = "lblMinSessionTime";
            this.lblMinTotalTime.Size = new System.Drawing.Size(85, 22);
            this.lblMinTotalTime.TabIndex = 12;
            // 
            // chart
            // 
            this.chart.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            chartArea1.AxisY.IntervalAutoMode = System.Windows.Forms.DataVisualization.Charting.IntervalAutoMode.VariableCount;
            chartArea1.AxisY.MajorGrid.Interval = 0D;
            chartArea1.AxisY.MajorGrid.LineDashStyle = System.Windows.Forms.DataVisualization.Charting.ChartDashStyle.Dot;
            chartArea1.AxisY.TextOrientation = System.Windows.Forms.DataVisualization.Charting.TextOrientation.Rotated270;
            chartArea1.AxisY.Title = "Time (sec)";
            chartArea1.AxisY2.MajorGrid.Enabled = false;
            chartArea1.AxisY2.Title = "Users";
            chartArea1.Name = "ChartArea1";
            this.chart.ChartAreas.Add(chartArea1);
            legend1.Name = "Legend1";
            this.chart.Legends.Add(legend1);
            this.chart.Location = new System.Drawing.Point(15, 236);
            this.chart.Name = "chart";
            series1.ChartArea = "ChartArea1";
            series1.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.ErrorBar;
            series1.CustomProperties = "MinPixelPointWidth=10, ErrorBarCenterMarkerStyle=Diamond, PixelPointWidth=10, Max" +
    "PixelPointWidth=10";
            series1.IsVisibleInLegend = false;
            series1.Legend = "Legend1";
            series1.MarkerSize = 8;
            series1.Name = "Total Data";
            series1.YValuesPerPoint = 3;
            series2.ChartArea = "ChartArea1";
            series2.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series2.Color = System.Drawing.Color.Red;
            series2.Legend = "Legend1";
            series2.Name = "Total Time";
            series3.ChartArea = "ChartArea1";
            series3.ChartType = System.Windows.Forms.DataVisualization.Charting.SeriesChartType.Line;
            series3.Color = System.Drawing.Color.Blue;
            series3.Legend = "Legend1";
            series3.Name = "Max Users";
            series3.YAxisType = System.Windows.Forms.DataVisualization.Charting.AxisType.Secondary;
            this.chart.Series.Add(series1);
            this.chart.Series.Add(series2);
            this.chart.Series.Add(series3);
            this.chart.Size = new System.Drawing.Size(877, 234);
            this.chart.TabIndex = 7;
            title1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Bold);
            title1.Name = "Title1";
            title1.Text = "Total Time (secs)";
            this.chart.Titles.Add(title1);
            // 
            // btnClear
            // 
            this.btnClear.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnClear.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.btnClear.Location = new System.Drawing.Point(817, 13);
            this.btnClear.Name = "btnClear";
            this.btnClear.Size = new System.Drawing.Size(75, 23);
            this.btnClear.TabIndex = 8;
            this.btnClear.Text = "&Clear...";
            this.btnClear.UseVisualStyleBackColor = true;
            this.btnClear.Click += new System.EventHandler(this.btnClear_Click);
            // 
            // timer
            // 
            this.timer.Enabled = true;
            this.timer.Interval = 1000;
            this.timer.Tick += new System.EventHandler(this.timer_Tick);
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(202, 54);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(60, 13);
            this.label7.TabIndex = 2;
            this.label7.Text = "Completed:";
            // 
            // lblCompleted
            // 
            this.lblCompleted.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCompleted.Location = new System.Drawing.Point(262, 53);
            this.lblCompleted.Name = "lblCompleted";
            this.lblCompleted.Size = new System.Drawing.Size(85, 22);
            this.lblCompleted.TabIndex = 3;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(339, 13);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(114, 13);
            this.label8.TabIndex = 2;
            this.label8.Text = "Sample Interval (secs):";
            // 
            // tbInterval
            // 
            this.tbInterval.Location = new System.Drawing.Point(459, 10);
            this.tbInterval.Name = "tbInterval";
            this.tbInterval.Size = new System.Drawing.Size(100, 20);
            this.tbInterval.TabIndex = 3;
            this.tbInterval.TextChanged += new System.EventHandler(this.tbInterval_TextChanged);
            this.tbInterval.KeyPress += new System.Windows.Forms.KeyPressEventHandler(this.tbInterval_KeyPress);
            // 
            // error
            // 
            this.error.ContainerControl = this;
            // 
            // btnPeriodDetails
            // 
            this.btnPeriodDetails.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnPeriodDetails.Location = new System.Drawing.Point(428, 59);
            this.btnPeriodDetails.Name = "btnPeriodDetails";
            this.btnPeriodDetails.Size = new System.Drawing.Size(75, 23);
            this.btnPeriodDetails.TabIndex = 8;
            this.btnPeriodDetails.Text = "&Details";
            this.btnPeriodDetails.UseVisualStyleBackColor = true;
            this.btnPeriodDetails.Click += new System.EventHandler(this.btnPeriodDetails_Click);
            // 
            // btnUsers
            // 
            this.btnUsers.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnUsers.Location = new System.Drawing.Point(428, 33);
            this.btnUsers.Name = "btnUsers";
            this.btnUsers.Size = new System.Drawing.Size(75, 23);
            this.btnUsers.TabIndex = 8;
            this.btnUsers.Text = "&Users...";
            this.btnUsers.UseVisualStyleBackColor = true;
            this.btnUsers.Click += new System.EventHandler(this.btnUsers_Click);
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Location = new System.Drawing.Point(219, 76);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(37, 13);
            this.label10.TabIndex = 4;
            this.label10.Text = "Errors:";
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Location = new System.Drawing.Point(202, 31);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(60, 13);
            this.label11.TabIndex = 2;
            this.label11.Text = "Max Users:";
            // 
            // lblErrors
            // 
            this.lblErrors.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblErrors.Location = new System.Drawing.Point(262, 75);
            this.lblErrors.Name = "lblErrors";
            this.lblErrors.Size = new System.Drawing.Size(85, 22);
            this.lblErrors.TabIndex = 5;
            // 
            // lblMaxUsers
            // 
            this.lblMaxUsers.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblMaxUsers.Location = new System.Drawing.Point(262, 30);
            this.lblMaxUsers.Name = "lblMaxUsers";
            this.lblMaxUsers.Size = new System.Drawing.Size(85, 22);
            this.lblMaxUsers.TabIndex = 3;
            // 
            // tabControl1
            // 
            this.tabControl1.Controls.Add(this.tabCumulative);
            this.tabControl1.Controls.Add(this.tabCurrent);
            this.tabControl1.Location = new System.Drawing.Point(16, 68);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(517, 162);
            this.tabControl1.TabIndex = 9;
            // 
            // tabCumulative
            // 
            this.tabCumulative.Controls.Add(this.btnCumDetails);
            this.tabCumulative.Controls.Add(this.label19);
            this.tabCumulative.Controls.Add(this.label9);
            this.tabCumulative.Controls.Add(this.lblCumUsersPerSec);
            this.tabCumulative.Controls.Add(this.lblCumUsers);
            this.tabCumulative.Controls.Add(this.lblCumAvgTotalTime);
            this.tabCumulative.Controls.Add(this.label14);
            this.tabCumulative.Controls.Add(this.label15);
            this.tabCumulative.Controls.Add(this.label16);
            this.tabCumulative.Controls.Add(this.label17);
            this.tabCumulative.Controls.Add(this.label18);
            this.tabCumulative.Controls.Add(this.lblCumMinTotalTime);
            this.tabCumulative.Controls.Add(this.lblCumErrors);
            this.tabCumulative.Controls.Add(this.lblCumMaxTotalTime);
            this.tabCumulative.Controls.Add(this.lblCumMaxUsers);
            this.tabCumulative.Controls.Add(this.label23);
            this.tabCumulative.Controls.Add(this.lblCumCompleted);
            this.tabCumulative.Location = new System.Drawing.Point(4, 22);
            this.tabCumulative.Name = "tabCumulative";
            this.tabCumulative.Padding = new System.Windows.Forms.Padding(3);
            this.tabCumulative.Size = new System.Drawing.Size(509, 136);
            this.tabCumulative.TabIndex = 1;
            this.tabCumulative.Text = "Cumulative";
            this.tabCumulative.UseVisualStyleBackColor = true;
            // 
            // btnCumDetails
            // 
            this.btnCumDetails.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnCumDetails.Location = new System.Drawing.Point(428, 59);
            this.btnCumDetails.Name = "btnCumDetails";
            this.btnCumDetails.Size = new System.Drawing.Size(75, 23);
            this.btnCumDetails.TabIndex = 27;
            this.btnCumDetails.Text = "&Details";
            this.btnCumDetails.UseVisualStyleBackColor = true;
            this.btnCumDetails.Click += new System.EventHandler(this.btnCumDetails_Click);
            // 
            // label19
            // 
            this.label19.AutoSize = true;
            this.label19.Location = new System.Drawing.Point(197, 9);
            this.label19.Name = "label19";
            this.label19.Size = new System.Drawing.Size(59, 13);
            this.label19.TabIndex = 13;
            this.label19.Text = "Users/sec:";
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(15, 9);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(74, 13);
            this.label9.TabIndex = 13;
            this.label9.Text = "Current Users:";
            // 
            // lblCumUsersPerSec
            // 
            this.lblCumUsersPerSec.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumUsersPerSec.Location = new System.Drawing.Point(262, 8);
            this.lblCumUsersPerSec.Name = "lblCumUsersPerSec";
            this.lblCumUsersPerSec.Size = new System.Drawing.Size(85, 22);
            this.lblCumUsersPerSec.TabIndex = 14;
            // 
            // lblCumUsers
            // 
            this.lblCumUsers.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumUsers.Location = new System.Drawing.Point(95, 8);
            this.lblCumUsers.Name = "lblCumUsers";
            this.lblCumUsers.Size = new System.Drawing.Size(85, 22);
            this.lblCumUsers.TabIndex = 14;
            // 
            // lblCumAvgSessionTime
            // 
            this.lblCumAvgTotalTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumAvgTotalTime.Location = new System.Drawing.Point(95, 52);
            this.lblCumAvgTotalTime.Name = "lblCumAvgSessionTime";
            this.lblCumAvgTotalTime.Size = new System.Drawing.Size(85, 22);
            this.lblCumAvgTotalTime.TabIndex = 24;
            // 
            // label14
            // 
            this.label14.AutoSize = true;
            this.label14.Location = new System.Drawing.Point(219, 76);
            this.label14.Name = "label14";
            this.label14.Size = new System.Drawing.Size(37, 13);
            this.label14.TabIndex = 19;
            this.label14.Text = "Errors:";
            // 
            // label15
            // 
            this.label15.AutoSize = true;
            this.label15.Location = new System.Drawing.Point(10, 75);
            this.label15.Name = "label15";
            this.label15.Size = new System.Drawing.Size(80, 13);
            this.label15.TabIndex = 25;
            this.label15.Text = "Min Total Time:";
            // 
            // label16
            // 
            this.label16.AutoSize = true;
            this.label16.Location = new System.Drawing.Point(202, 31);
            this.label16.Name = "label16";
            this.label16.Size = new System.Drawing.Size(60, 13);
            this.label16.TabIndex = 16;
            this.label16.Text = "Max Users:";
            // 
            // label17
            // 
            this.label17.AutoSize = true;
            this.label17.Location = new System.Drawing.Point(10, 53);
            this.label17.Name = "label17";
            this.label17.Size = new System.Drawing.Size(82, 13);
            this.label17.TabIndex = 23;
            this.label17.Text = "Avg Total Time:";
            // 
            // label18
            // 
            this.label18.AutoSize = true;
            this.label18.Location = new System.Drawing.Point(202, 54);
            this.label18.Name = "label18";
            this.label18.Size = new System.Drawing.Size(60, 13);
            this.label18.TabIndex = 15;
            this.label18.Text = "Completed:";
            // 
            // lblCumMinSessionTime
            // 
            this.lblCumMinTotalTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumMinTotalTime.Location = new System.Drawing.Point(95, 74);
            this.lblCumMinTotalTime.Name = "lblCumMinSessionTime";
            this.lblCumMinTotalTime.Size = new System.Drawing.Size(85, 22);
            this.lblCumMinTotalTime.TabIndex = 26;
            // 
            // lblCumErrors
            // 
            this.lblCumErrors.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumErrors.Location = new System.Drawing.Point(262, 75);
            this.lblCumErrors.Name = "lblCumErrors";
            this.lblCumErrors.Size = new System.Drawing.Size(85, 22);
            this.lblCumErrors.TabIndex = 20;
            // 
            // lblCumMaxSessionTime
            // 
            this.lblCumMaxTotalTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumMaxTotalTime.Location = new System.Drawing.Point(95, 30);
            this.lblCumMaxTotalTime.Name = "lblCumMaxSessionTime";
            this.lblCumMaxTotalTime.Size = new System.Drawing.Size(85, 22);
            this.lblCumMaxTotalTime.TabIndex = 22;
            // 
            // lblCumMaxUsers
            // 
            this.lblCumMaxUsers.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumMaxUsers.Location = new System.Drawing.Point(262, 30);
            this.lblCumMaxUsers.Name = "lblCumMaxUsers";
            this.lblCumMaxUsers.Size = new System.Drawing.Size(85, 22);
            this.lblCumMaxUsers.TabIndex = 17;
            // 
            // label23
            // 
            this.label23.AutoSize = true;
            this.label23.Location = new System.Drawing.Point(10, 32);
            this.label23.Name = "label23";
            this.label23.Size = new System.Drawing.Size(83, 13);
            this.label23.TabIndex = 21;
            this.label23.Text = "Max Total Time:";
            // 
            // lblCumCompleted
            // 
            this.lblCumCompleted.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblCumCompleted.Location = new System.Drawing.Point(262, 53);
            this.lblCumCompleted.Name = "lblCumCompleted";
            this.lblCumCompleted.Size = new System.Drawing.Size(85, 22);
            this.lblCumCompleted.TabIndex = 18;
            // 
            // tabCurrent
            // 
            this.tabCurrent.Controls.Add(this.label13);
            this.tabCurrent.Controls.Add(this.lblUsersPerSec);
            this.tabCurrent.Controls.Add(this.btnPeriodDetails);
            this.tabCurrent.Controls.Add(this.label3);
            this.tabCurrent.Controls.Add(this.lblUsers);
            this.tabCurrent.Controls.Add(this.btnUsers);
            this.tabCurrent.Controls.Add(this.lblAvgTotalTime);
            this.tabCurrent.Controls.Add(this.label10);
            this.tabCurrent.Controls.Add(this.label6);
            this.tabCurrent.Controls.Add(this.label11);
            this.tabCurrent.Controls.Add(this.label5);
            this.tabCurrent.Controls.Add(this.label7);
            this.tabCurrent.Controls.Add(this.lblMinTotalTime);
            this.tabCurrent.Controls.Add(this.lblErrors);
            this.tabCurrent.Controls.Add(this.lblMaxTotalTime);
            this.tabCurrent.Controls.Add(this.lblMaxUsers);
            this.tabCurrent.Controls.Add(this.label4);
            this.tabCurrent.Controls.Add(this.lblCompleted);
            this.tabCurrent.Location = new System.Drawing.Point(4, 22);
            this.tabCurrent.Name = "tabCurrent";
            this.tabCurrent.Padding = new System.Windows.Forms.Padding(3);
            this.tabCurrent.Size = new System.Drawing.Size(509, 136);
            this.tabCurrent.TabIndex = 0;
            this.tabCurrent.Text = "Current";
            this.tabCurrent.UseVisualStyleBackColor = true;
            // 
            // label13
            // 
            this.label13.AutoSize = true;
            this.label13.Location = new System.Drawing.Point(197, 9);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(59, 13);
            this.label13.TabIndex = 15;
            this.label13.Text = "Users/sec:";
            // 
            // lblUsersPerSec
            // 
            this.lblUsersPerSec.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblUsersPerSec.Location = new System.Drawing.Point(262, 8);
            this.lblUsersPerSec.Name = "lblUsersPerSec";
            this.lblUsersPerSec.Size = new System.Drawing.Size(85, 22);
            this.lblUsersPerSec.TabIndex = 16;
            // 
            // label12
            // 
            this.label12.AutoSize = true;
            this.label12.Location = new System.Drawing.Point(20, 34);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(115, 13);
            this.label12.TabIndex = 0;
            this.label12.Text = "Secs Since Last Start: ";
            // 
            // lblSecsSinceLastSession
            // 
            this.lblSecsSinceLastSession.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblSecsSinceLastSession.Location = new System.Drawing.Point(146, 33);
            this.lblSecsSinceLastSession.Name = "lblSecsSinceLastSession";
            this.lblSecsSinceLastSession.Size = new System.Drawing.Size(187, 22);
            this.lblSecsSinceLastSession.TabIndex = 1;
            // 
            // btnChart
            // 
            this.btnChart.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnChart.Location = new System.Drawing.Point(817, 203);
            this.btnChart.Name = "btnChart";
            this.btnChart.Size = new System.Drawing.Size(75, 23);
            this.btnChart.TabIndex = 10;
            this.btnChart.Text = "Chart...";
            this.btnChart.UseVisualStyleBackColor = true;
            this.btnChart.Click += new System.EventHandler(this.btnChart_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(904, 482);
            this.Controls.Add(this.btnChart);
            this.Controls.Add(this.tabControl1);
            this.Controls.Add(this.tbInterval);
            this.Controls.Add(this.btnClear);
            this.Controls.Add(this.chart);
            this.Controls.Add(this.lblStatus);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.lblSecsSinceLastSession);
            this.Controls.Add(this.lblTime);
            this.Controls.Add(this.label12);
            this.Controls.Add(this.label8);
            this.Controls.Add(this.label1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MinimumSize = new System.Drawing.Size(920, 520);
            this.Name = "MainForm";
            this.Text = "Test Console";
            this.Load += new System.EventHandler(this.MainForm_Load);
            ((System.ComponentModel.ISupportInitialize)(this.chart)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.error)).EndInit();
            this.tabControl1.ResumeLayout(false);
            this.tabCumulative.ResumeLayout(false);
            this.tabCumulative.PerformLayout();
            this.tabCurrent.ResumeLayout(false);
            this.tabCurrent.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label lblTime;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblStatus;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblUsers;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label lblMaxTotalTime;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label lblAvgTotalTime;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label lblMinTotalTime;
        private System.Windows.Forms.DataVisualization.Charting.Chart chart;
        private System.Windows.Forms.Button btnClear;
        private System.Windows.Forms.Timer timer;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label lblCompleted;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.TextBox tbInterval;
        private System.Windows.Forms.ErrorProvider error;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.Label lblErrors;
        private System.Windows.Forms.Button btnUsers;
        private System.Windows.Forms.Button btnPeriodDetails;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.Label lblMaxUsers;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabCumulative;
        private System.Windows.Forms.Label lblCumAvgTotalTime;
        private System.Windows.Forms.Label label14;
        private System.Windows.Forms.Label label15;
        private System.Windows.Forms.Label label16;
        private System.Windows.Forms.Label label17;
        private System.Windows.Forms.Label label18;
        private System.Windows.Forms.Label lblCumMinTotalTime;
        private System.Windows.Forms.Label lblCumErrors;
        private System.Windows.Forms.Label lblCumMaxTotalTime;
        private System.Windows.Forms.Label lblCumMaxUsers;
        private System.Windows.Forms.Label label23;
        private System.Windows.Forms.Label lblCumCompleted;
        private System.Windows.Forms.TabPage tabCurrent;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Label lblCumUsers;
        private System.Windows.Forms.Button btnCumDetails;
        private System.Windows.Forms.Label label19;
        private System.Windows.Forms.Label lblCumUsersPerSec;
        private System.Windows.Forms.Label lblSecsSinceLastSession;
        private System.Windows.Forms.Label label12;
        private System.Windows.Forms.Button btnChart;
        private System.Windows.Forms.Label label13;
        private System.Windows.Forms.Label lblUsersPerSec;
    }
}

