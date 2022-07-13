namespace com.disney.xband.xbrc.xBRCLab
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
            System.Windows.Forms.DataVisualization.Charting.Series series1 = new System.Windows.Forms.DataVisualization.Charting.Series();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.setDataDirectoryToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.connectToXBRCToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.exitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.dataToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.miCollect = new System.Windows.Forms.ToolStripMenuItem();
            this.analyzeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.miSignalStrengthHistogram = new System.Windows.Forms.ToolStripMenuItem();
            this.miRadioHealth = new System.Windows.Forms.ToolStripMenuItem();
            this.signalStrengthVDistanceToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.aboutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.dgDataSets = new System.Windows.Forms.DataGridView();
            this.DataSetName = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Date = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.DataSetType = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Description = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.panel1 = new System.Windows.Forms.Panel();
            this.label1 = new System.Windows.Forms.Label();
            this.fbd = new System.Windows.Forms.FolderBrowserDialog();
            this.cms = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.analyizeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.miRadioAnalysis = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.miDelete = new System.Windows.Forms.ToolStripMenuItem();
            this.pnlDSStats = new System.Windows.Forms.Panel();
            this.chHistogram = new System.Windows.Forms.DataVisualization.Charting.Chart();
            this.lblTapReads = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.lblLatest = new System.Windows.Forms.Label();
            this.lblEarliest = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.lblBucketSize = new System.Windows.Forms.Label();
            this.lblSingulation = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.lblLRReads = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.lblReaders = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.deleteToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.menuStrip1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgDataSets)).BeginInit();
            this.panel1.SuspendLayout();
            this.cms.SuspendLayout();
            this.pnlDSStats.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.chHistogram)).BeginInit();
            this.SuspendLayout();
            // 
            // menuStrip1
            // 
            this.menuStrip1.BackColor = System.Drawing.SystemColors.ControlLight;
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.dataToolStripMenuItem,
            this.analyzeToolStripMenuItem,
            this.helpToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(1000, 24);
            this.menuStrip1.TabIndex = 0;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.setDataDirectoryToolStripMenuItem,
            this.connectToXBRCToolStripMenuItem,
            this.exitToolStripMenuItem});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(37, 20);
            this.fileToolStripMenuItem.Text = "&File";
            // 
            // setDataDirectoryToolStripMenuItem
            // 
            this.setDataDirectoryToolStripMenuItem.Name = "setDataDirectoryToolStripMenuItem";
            this.setDataDirectoryToolStripMenuItem.Size = new System.Drawing.Size(177, 22);
            this.setDataDirectoryToolStripMenuItem.Text = "Set Data &Directory...";
            this.setDataDirectoryToolStripMenuItem.Click += new System.EventHandler(this.setDataDirectoryToolStripMenuItem_Click);
            // 
            // connectToXBRCToolStripMenuItem
            // 
            this.connectToXBRCToolStripMenuItem.Name = "connectToXBRCToolStripMenuItem";
            this.connectToXBRCToolStripMenuItem.Size = new System.Drawing.Size(177, 22);
            this.connectToXBRCToolStripMenuItem.Text = "&Connect to xBRC...";
            this.connectToXBRCToolStripMenuItem.Click += new System.EventHandler(this.connectToXBRCToolStripMenuItem_Click);
            // 
            // exitToolStripMenuItem
            // 
            this.exitToolStripMenuItem.Name = "exitToolStripMenuItem";
            this.exitToolStripMenuItem.Size = new System.Drawing.Size(177, 22);
            this.exitToolStripMenuItem.Text = "E&xit";
            this.exitToolStripMenuItem.Click += new System.EventHandler(this.exitToolStripMenuItem_Click);
            // 
            // dataToolStripMenuItem
            // 
            this.dataToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miCollect,
            this.deleteToolStripMenuItem});
            this.dataToolStripMenuItem.Name = "dataToolStripMenuItem";
            this.dataToolStripMenuItem.Size = new System.Drawing.Size(43, 20);
            this.dataToolStripMenuItem.Text = "&Data";
            // 
            // miCollect
            // 
            this.miCollect.Enabled = false;
            this.miCollect.Name = "miCollect";
            this.miCollect.Size = new System.Drawing.Size(152, 22);
            this.miCollect.Text = "&Collect...";
            this.miCollect.Click += new System.EventHandler(this.miCollect_Click);
            // 
            // analyzeToolStripMenuItem
            // 
            this.analyzeToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miSignalStrengthHistogram,
            this.miRadioHealth,
            this.signalStrengthVDistanceToolStripMenuItem});
            this.analyzeToolStripMenuItem.Name = "analyzeToolStripMenuItem";
            this.analyzeToolStripMenuItem.Size = new System.Drawing.Size(60, 20);
            this.analyzeToolStripMenuItem.Text = "&Analyze";
            // 
            // miSignalStrengthHistogram
            // 
            this.miSignalStrengthHistogram.Name = "miSignalStrengthHistogram";
            this.miSignalStrengthHistogram.Size = new System.Drawing.Size(214, 22);
            this.miSignalStrengthHistogram.Text = "&Signal Strength Histogram";
            this.miSignalStrengthHistogram.Click += new System.EventHandler(this.miSignalStrengthHistogram_Click);
            // 
            // miRadioHealth
            // 
            this.miRadioHealth.Name = "miRadioHealth";
            this.miRadioHealth.Size = new System.Drawing.Size(214, 22);
            this.miRadioHealth.Text = "&Reader Radio Health";
            this.miRadioHealth.Click += new System.EventHandler(this.miRadioHealth_Click);
            // 
            // signalStrengthVDistanceToolStripMenuItem
            // 
            this.signalStrengthVDistanceToolStripMenuItem.Name = "signalStrengthVDistanceToolStripMenuItem";
            this.signalStrengthVDistanceToolStripMenuItem.Size = new System.Drawing.Size(214, 22);
            this.signalStrengthVDistanceToolStripMenuItem.Text = "Signal Strength v. &Distance";
            this.signalStrengthVDistanceToolStripMenuItem.Click += new System.EventHandler(this.signalStrengthVDistanceToolStripMenuItem_Click);
            // 
            // helpToolStripMenuItem
            // 
            this.helpToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.aboutToolStripMenuItem});
            this.helpToolStripMenuItem.Name = "helpToolStripMenuItem";
            this.helpToolStripMenuItem.Size = new System.Drawing.Size(44, 20);
            this.helpToolStripMenuItem.Text = "&Help";
            // 
            // aboutToolStripMenuItem
            // 
            this.aboutToolStripMenuItem.Name = "aboutToolStripMenuItem";
            this.aboutToolStripMenuItem.Size = new System.Drawing.Size(107, 22);
            this.aboutToolStripMenuItem.Text = "&About";
            // 
            // dgDataSets
            // 
            this.dgDataSets.AllowUserToAddRows = false;
            this.dgDataSets.AllowUserToDeleteRows = false;
            this.dgDataSets.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.dgDataSets.BackgroundColor = System.Drawing.SystemColors.Window;
            this.dgDataSets.BorderStyle = System.Windows.Forms.BorderStyle.None;
            this.dgDataSets.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgDataSets.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.DataSetName,
            this.Date,
            this.DataSetType,
            this.Description});
            this.dgDataSets.Location = new System.Drawing.Point(12, 20);
            this.dgDataSets.MultiSelect = false;
            this.dgDataSets.Name = "dgDataSets";
            this.dgDataSets.ReadOnly = true;
            this.dgDataSets.RowHeadersVisible = false;
            this.dgDataSets.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgDataSets.Size = new System.Drawing.Size(976, 222);
            this.dgDataSets.TabIndex = 0;
            this.dgDataSets.SelectionChanged += new System.EventHandler(this.dgDataSets_SelectionChanged);
            // 
            // DataSetName
            // 
            this.DataSetName.DataPropertyName = "DataSetName";
            this.DataSetName.FillWeight = 50F;
            this.DataSetName.HeaderText = "Name";
            this.DataSetName.Name = "DataSetName";
            this.DataSetName.ReadOnly = true;
            // 
            // Date
            // 
            this.Date.DataPropertyName = "Date";
            this.Date.HeaderText = "Date";
            this.Date.Name = "Date";
            this.Date.ReadOnly = true;
            this.Date.Width = 125;
            // 
            // DataSetType
            // 
            this.DataSetType.DataPropertyName = "DataSetType";
            this.DataSetType.HeaderText = "Type";
            this.DataSetType.Name = "DataSetType";
            this.DataSetType.ReadOnly = true;
            // 
            // Description
            // 
            this.Description.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.Description.DataPropertyName = "Description";
            this.Description.HeaderText = "Description";
            this.Description.Name = "Description";
            this.Description.ReadOnly = true;
            // 
            // panel1
            // 
            this.panel1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.panel1.Controls.Add(this.label1);
            this.panel1.Controls.Add(this.dgDataSets);
            this.panel1.Location = new System.Drawing.Point(0, 25);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(1000, 250);
            this.panel1.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(13, 4);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(54, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "Data Sets";
            // 
            // cms
            // 
            this.cms.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.analyizeToolStripMenuItem,
            this.toolStripSeparator1,
            this.miDelete});
            this.cms.Name = "cms";
            this.cms.ShowImageMargin = false;
            this.cms.Size = new System.Drawing.Size(91, 54);
            // 
            // analyizeToolStripMenuItem
            // 
            this.analyizeToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.miRadioAnalysis});
            this.analyizeToolStripMenuItem.Name = "analyizeToolStripMenuItem";
            this.analyizeToolStripMenuItem.Size = new System.Drawing.Size(90, 22);
            this.analyizeToolStripMenuItem.Text = "Analyze";
            // 
            // miRadioAnalysis
            // 
            this.miRadioAnalysis.Name = "miRadioAnalysis";
            this.miRadioAnalysis.Size = new System.Drawing.Size(150, 22);
            this.miRadioAnalysis.Text = "Radio Analysis";
            this.miRadioAnalysis.Click += new System.EventHandler(this.miRadioAnalysis_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            this.toolStripSeparator1.Size = new System.Drawing.Size(87, 6);
            // 
            // miDelete
            // 
            this.miDelete.Name = "miDelete";
            this.miDelete.Size = new System.Drawing.Size(90, 22);
            this.miDelete.Text = "&Delete";
            this.miDelete.Click += new System.EventHandler(this.miDelete_Click);
            // 
            // pnlDSStats
            // 
            this.pnlDSStats.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.pnlDSStats.Controls.Add(this.chHistogram);
            this.pnlDSStats.Controls.Add(this.lblTapReads);
            this.pnlDSStats.Controls.Add(this.label5);
            this.pnlDSStats.Controls.Add(this.lblLatest);
            this.pnlDSStats.Controls.Add(this.lblEarliest);
            this.pnlDSStats.Controls.Add(this.label7);
            this.pnlDSStats.Controls.Add(this.lblBucketSize);
            this.pnlDSStats.Controls.Add(this.lblSingulation);
            this.pnlDSStats.Controls.Add(this.label8);
            this.pnlDSStats.Controls.Add(this.label6);
            this.pnlDSStats.Controls.Add(this.label4);
            this.pnlDSStats.Controls.Add(this.lblLRReads);
            this.pnlDSStats.Controls.Add(this.label3);
            this.pnlDSStats.Controls.Add(this.lblReaders);
            this.pnlDSStats.Controls.Add(this.label2);
            this.pnlDSStats.Location = new System.Drawing.Point(0, 272);
            this.pnlDSStats.Name = "pnlDSStats";
            this.pnlDSStats.Size = new System.Drawing.Size(1000, 247);
            this.pnlDSStats.TabIndex = 2;
            // 
            // chHistogram
            // 
            this.chHistogram.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            chartArea1.AxisX.Crossing = -1.7976931348623157E+308D;
            chartArea1.AxisX.IntervalAutoMode = System.Windows.Forms.DataVisualization.Charting.IntervalAutoMode.VariableCount;
            chartArea1.AxisX.LabelAutoFitStyle = ((System.Windows.Forms.DataVisualization.Charting.LabelAutoFitStyles)(((((System.Windows.Forms.DataVisualization.Charting.LabelAutoFitStyles.IncreaseFont | System.Windows.Forms.DataVisualization.Charting.LabelAutoFitStyles.DecreaseFont)
                        | System.Windows.Forms.DataVisualization.Charting.LabelAutoFitStyles.LabelsAngleStep30)
                        | System.Windows.Forms.DataVisualization.Charting.LabelAutoFitStyles.LabelsAngleStep45)
                        | System.Windows.Forms.DataVisualization.Charting.LabelAutoFitStyles.WordWrap)));
            chartArea1.AxisX.LabelStyle.Angle = -45;
            chartArea1.AxisX.LabelStyle.Format = "f3";
            chartArea1.AxisX.LabelStyle.Interval = 0D;
            chartArea1.AxisX.MajorGrid.Interval = 0D;
            chartArea1.AxisX.MajorTickMark.Interval = 0D;
            chartArea1.AxisX.Minimum = 0D;
            chartArea1.AxisX.Title = "Time (seconds from start)";
            chartArea1.AxisY.TextOrientation = System.Windows.Forms.DataVisualization.Charting.TextOrientation.Stacked;
            chartArea1.AxisY.Title = "Events";
            chartArea1.Name = "ChartArea1";
            this.chHistogram.ChartAreas.Add(chartArea1);
            this.chHistogram.Location = new System.Drawing.Point(237, 20);
            this.chHistogram.Name = "chHistogram";
            series1.ChartArea = "ChartArea1";
            series1.Name = "Series1";
            series1.XValueType = System.Windows.Forms.DataVisualization.Charting.ChartValueType.Double;
            this.chHistogram.Series.Add(series1);
            this.chHistogram.Size = new System.Drawing.Size(751, 215);
            this.chHistogram.TabIndex = 2;
            this.chHistogram.Text = "chart1";
            this.chHistogram.SizeChanged += new System.EventHandler(this.chHistogram_SizeChanged);
            // 
            // lblTapReads
            // 
            this.lblTapReads.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblTapReads.Location = new System.Drawing.Point(118, 47);
            this.lblTapReads.Name = "lblTapReads";
            this.lblTapReads.Size = new System.Drawing.Size(100, 20);
            this.lblTapReads.TabIndex = 1;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(16, 48);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(55, 13);
            this.label5.TabIndex = 0;
            this.label5.Text = "Tap reads";
            // 
            // lblLatest
            // 
            this.lblLatest.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblLatest.Location = new System.Drawing.Point(118, 167);
            this.lblLatest.Name = "lblLatest";
            this.lblLatest.Size = new System.Drawing.Size(100, 32);
            this.lblLatest.TabIndex = 1;
            // 
            // lblEarliest
            // 
            this.lblEarliest.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblEarliest.Location = new System.Drawing.Point(118, 128);
            this.lblEarliest.Name = "lblEarliest";
            this.lblEarliest.Size = new System.Drawing.Size(100, 32);
            this.lblEarliest.TabIndex = 1;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(16, 167);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(66, 13);
            this.label7.TabIndex = 0;
            this.label7.Text = "Latest event";
            // 
            // lblBucketSize
            // 
            this.lblBucketSize.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblBucketSize.Location = new System.Drawing.Point(118, 205);
            this.lblBucketSize.Name = "lblBucketSize";
            this.lblBucketSize.Size = new System.Drawing.Size(100, 20);
            this.lblBucketSize.TabIndex = 1;
            // 
            // lblSingulation
            // 
            this.lblSingulation.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblSingulation.Location = new System.Drawing.Point(118, 101);
            this.lblSingulation.Name = "lblSingulation";
            this.lblSingulation.Size = new System.Drawing.Size(100, 20);
            this.lblSingulation.TabIndex = 1;
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(16, 208);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(93, 13);
            this.label8.TabIndex = 0;
            this.label8.Text = "Bar size (seconds)";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(16, 128);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(71, 13);
            this.label6.TabIndex = 0;
            this.label6.Text = "Earliest event";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(16, 104);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(94, 13);
            this.label4.TabIndex = 0;
            this.label4.Text = "Singulation events";
            // 
            // lblLRReads
            // 
            this.lblLRReads.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblLRReads.Location = new System.Drawing.Point(118, 74);
            this.lblLRReads.Name = "lblLRReads";
            this.lblLRReads.Size = new System.Drawing.Size(100, 20);
            this.lblLRReads.TabIndex = 1;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(16, 76);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(90, 13);
            this.label3.TabIndex = 0;
            this.label3.Text = "Long range reads";
            // 
            // lblReaders
            // 
            this.lblReaders.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblReaders.Location = new System.Drawing.Point(118, 20);
            this.lblReaders.Name = "lblReaders";
            this.lblReaders.Size = new System.Drawing.Size(100, 20);
            this.lblReaders.TabIndex = 1;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(16, 20);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(47, 13);
            this.label2.TabIndex = 0;
            this.label2.Text = "Readers";
            // 
            // deleteToolStripMenuItem
            // 
            this.deleteToolStripMenuItem.Name = "deleteToolStripMenuItem";
            this.deleteToolStripMenuItem.ShortcutKeys = System.Windows.Forms.Keys.Delete;
            this.deleteToolStripMenuItem.Size = new System.Drawing.Size(152, 22);
            this.deleteToolStripMenuItem.Text = "&Delete";
            this.deleteToolStripMenuItem.Click += new System.EventHandler(this.deleteToolStripMenuItem_Click);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1000, 519);
            this.Controls.Add(this.panel1);
            this.Controls.Add(this.pnlDSStats);
            this.Controls.Add(this.menuStrip1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MainMenuStrip = this.menuStrip1;
            this.MinimumSize = new System.Drawing.Size(1016, 557);
            this.Name = "MainForm";
            this.Text = "xBRC Data Analyzer";
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgDataSets)).EndInit();
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.cms.ResumeLayout(false);
            this.pnlDSStats.ResumeLayout(false);
            this.pnlDSStats.PerformLayout();
            ((System.ComponentModel.ISupportInitialize)(this.chHistogram)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem connectToXBRCToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem exitToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem dataToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem miCollect;
        private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem;
        private System.Windows.Forms.DataGridView dgDataSets;
        private System.Windows.Forms.ToolStripMenuItem setDataDirectoryToolStripMenuItem;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.FolderBrowserDialog fbd;
        private System.Windows.Forms.ContextMenuStrip cms;
        private System.Windows.Forms.ToolStripMenuItem analyizeToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem miRadioAnalysis;
        private System.Windows.Forms.DataGridViewTextBoxColumn DataSetName;
        private System.Windows.Forms.DataGridViewTextBoxColumn Date;
        private System.Windows.Forms.DataGridViewTextBoxColumn DataSetType;
        private System.Windows.Forms.DataGridViewTextBoxColumn Description;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripMenuItem miDelete;
        private System.Windows.Forms.Panel pnlDSStats;
        private System.Windows.Forms.DataVisualization.Charting.Chart chHistogram;
        private System.Windows.Forms.Label lblTapReads;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label lblSingulation;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label lblLRReads;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblReaders;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblLatest;
        private System.Windows.Forms.Label lblEarliest;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.ToolStripMenuItem analyzeToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem miSignalStrengthHistogram;
        private System.Windows.Forms.ToolStripMenuItem aboutToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem miRadioHealth;
        private System.Windows.Forms.Label lblBucketSize;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.ToolStripMenuItem signalStrengthVDistanceToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem deleteToolStripMenuItem;
    }
}

