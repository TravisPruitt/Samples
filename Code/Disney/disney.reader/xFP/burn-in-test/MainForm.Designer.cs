namespace xTPManufacturerTest
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
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle1 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle2 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle3 = new System.Windows.Forms.DataGridViewCellStyle();
            System.Windows.Forms.DataGridViewCellStyle dataGridViewCellStyle4 = new System.Windows.Forms.DataGridViewCellStyle();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.dataGridViewReaderList = new System.Windows.Forms.DataGridView();
            this.Status = new System.Windows.Forms.DataGridViewImageColumn();
            this.IP = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Column1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Type = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.textBoxAddReaderIP = new System.Windows.Forms.TextBox();
            this.buttonAddReader = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.timer1 = new System.Windows.Forms.Timer(this.components);
            this.readerPanel = new System.Windows.Forms.Panel();
            this.button_media = new System.Windows.Forms.Button();
            this.identifyCheckBox = new System.Windows.Forms.CheckBox();
            this.removeButton = new System.Windows.Forms.Button();
            this.upgradeButton = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.label_RFIDRestarts = new System.Windows.Forms.Label();
            this.label14 = new System.Windows.Forms.Label();
            this.label_xbioDiagnosticEvents = new System.Windows.Forms.Label();
            this.label13 = new System.Windows.Forms.Label();
            this.label_testDuration = new System.Windows.Forms.Label();
            this.label12 = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.label9 = new System.Windows.Forms.Label();
            this.label_diagnosticEvents = new System.Windows.Forms.Label();
            this.label11 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.reportButton = new System.Windows.Forms.Button();
            this.label_hellos = new System.Windows.Forms.Label();
            this.label_taps = new System.Windows.Forms.Label();
            this.clearButton = new System.Windows.Forms.Button();
            this.label_events = new System.Windows.Forms.Label();
            this.label_xbioEvents = new System.Windows.Forms.Label();
            this.label_errors = new System.Windows.Forms.Label();
            this.label10 = new System.Windows.Forms.Label();
            this.label_type = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.text_log = new System.Windows.Forms.TextBox();
            this.label_maxTemp = new System.Windows.Forms.Label();
            this.label_temp = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.optionsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.box_logTemperature = new System.Windows.Forms.CheckBox();
            ((System.ComponentModel.ISupportInitialize)(this.dataGridViewReaderList)).BeginInit();
            this.readerPanel.SuspendLayout();
            this.groupBox1.SuspendLayout();
            this.menuStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // dataGridViewReaderList
            // 
            this.dataGridViewReaderList.AllowUserToAddRows = false;
            this.dataGridViewReaderList.AllowUserToResizeColumns = false;
            this.dataGridViewReaderList.AllowUserToResizeRows = false;
            this.dataGridViewReaderList.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.dataGridViewReaderList.AutoSizeColumnsMode = System.Windows.Forms.DataGridViewAutoSizeColumnsMode.Fill;
            this.dataGridViewReaderList.AutoSizeRowsMode = System.Windows.Forms.DataGridViewAutoSizeRowsMode.AllCells;
            this.dataGridViewReaderList.BackgroundColor = System.Drawing.SystemColors.ControlLight;
            dataGridViewCellStyle1.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleCenter;
            dataGridViewCellStyle1.BackColor = System.Drawing.SystemColors.Control;
            dataGridViewCellStyle1.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            dataGridViewCellStyle1.ForeColor = System.Drawing.SystemColors.WindowText;
            dataGridViewCellStyle1.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle1.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle1.WrapMode = System.Windows.Forms.DataGridViewTriState.False;
            this.dataGridViewReaderList.ColumnHeadersDefaultCellStyle = dataGridViewCellStyle1;
            this.dataGridViewReaderList.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dataGridViewReaderList.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Status,
            this.IP,
            this.Column1,
            this.Type});
            dataGridViewCellStyle2.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleLeft;
            dataGridViewCellStyle2.BackColor = System.Drawing.SystemColors.Window;
            dataGridViewCellStyle2.Font = new System.Drawing.Font("Microsoft Sans Serif", 11.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            dataGridViewCellStyle2.ForeColor = System.Drawing.SystemColors.ControlText;
            dataGridViewCellStyle2.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle2.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle2.WrapMode = System.Windows.Forms.DataGridViewTriState.False;
            this.dataGridViewReaderList.DefaultCellStyle = dataGridViewCellStyle2;
            this.dataGridViewReaderList.Location = new System.Drawing.Point(12, 37);
            this.dataGridViewReaderList.MultiSelect = false;
            this.dataGridViewReaderList.Name = "dataGridViewReaderList";
            this.dataGridViewReaderList.ReadOnly = true;
            dataGridViewCellStyle3.Alignment = System.Windows.Forms.DataGridViewContentAlignment.MiddleLeft;
            dataGridViewCellStyle3.BackColor = System.Drawing.SystemColors.Control;
            dataGridViewCellStyle3.Font = new System.Drawing.Font("Microsoft Sans Serif", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            dataGridViewCellStyle3.ForeColor = System.Drawing.SystemColors.WindowText;
            dataGridViewCellStyle3.SelectionBackColor = System.Drawing.SystemColors.Highlight;
            dataGridViewCellStyle3.SelectionForeColor = System.Drawing.SystemColors.HighlightText;
            dataGridViewCellStyle3.WrapMode = System.Windows.Forms.DataGridViewTriState.True;
            this.dataGridViewReaderList.RowHeadersDefaultCellStyle = dataGridViewCellStyle3;
            this.dataGridViewReaderList.RowHeadersWidthSizeMode = System.Windows.Forms.DataGridViewRowHeadersWidthSizeMode.DisableResizing;
            dataGridViewCellStyle4.WrapMode = System.Windows.Forms.DataGridViewTriState.True;
            this.dataGridViewReaderList.RowsDefaultCellStyle = dataGridViewCellStyle4;
            this.dataGridViewReaderList.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dataGridViewReaderList.Size = new System.Drawing.Size(481, 703);
            this.dataGridViewReaderList.TabIndex = 0;
            this.dataGridViewReaderList.SelectionChanged += new System.EventHandler(this.dataGridViewReaderList_SelectionChanged);
            // 
            // Status
            // 
            this.Status.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.AllCells;
            this.Status.DataPropertyName = "StatusIndicator";
            this.Status.FillWeight = 192.1842F;
            this.Status.HeaderText = "Status";
            this.Status.Name = "Status";
            this.Status.ReadOnly = true;
            this.Status.Resizable = System.Windows.Forms.DataGridViewTriState.False;
            this.Status.Width = 43;
            // 
            // IP
            // 
            this.IP.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
            this.IP.DataPropertyName = "IPAddress";
            this.IP.FillWeight = 48.54588F;
            this.IP.HeaderText = "IP";
            this.IP.Name = "IP";
            this.IP.ReadOnly = true;
            this.IP.Width = 125;
            // 
            // Column1
            // 
            this.Column1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.None;
            this.Column1.DataPropertyName = "Mac";
            this.Column1.FillWeight = 310.6937F;
            this.Column1.HeaderText = "MAC";
            this.Column1.Name = "Column1";
            this.Column1.ReadOnly = true;
            this.Column1.Width = 150;
            // 
            // Type
            // 
            this.Type.DataPropertyName = "Type";
            this.Type.HeaderText = "Type";
            this.Type.Name = "Type";
            this.Type.ReadOnly = true;
            // 
            // textBoxAddReaderIP
            // 
            this.textBoxAddReaderIP.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.textBoxAddReaderIP.Location = new System.Drawing.Point(147, 748);
            this.textBoxAddReaderIP.Name = "textBoxAddReaderIP";
            this.textBoxAddReaderIP.Size = new System.Drawing.Size(188, 20);
            this.textBoxAddReaderIP.TabIndex = 1;
            // 
            // buttonAddReader
            // 
            this.buttonAddReader.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.buttonAddReader.Location = new System.Drawing.Point(341, 746);
            this.buttonAddReader.Name = "buttonAddReader";
            this.buttonAddReader.Size = new System.Drawing.Size(75, 23);
            this.buttonAddReader.TabIndex = 2;
            this.buttonAddReader.Text = "Add Reader";
            this.buttonAddReader.UseVisualStyleBackColor = true;
            this.buttonAddReader.Click += new System.EventHandler(this.buttonAddReader_Click);
            // 
            // label1
            // 
            this.label1.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(83, 751);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(58, 13);
            this.label1.TabIndex = 3;
            this.label1.Text = "Reader IP:";
            // 
            // timer1
            // 
            this.timer1.Enabled = true;
            this.timer1.Interval = 1000;
            this.timer1.Tick += new System.EventHandler(this.timer1_Tick);
            // 
            // readerPanel
            // 
            this.readerPanel.BackColor = System.Drawing.SystemColors.ControlLight;
            this.readerPanel.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.readerPanel.Controls.Add(this.box_logTemperature);
            this.readerPanel.Controls.Add(this.button_media);
            this.readerPanel.Controls.Add(this.identifyCheckBox);
            this.readerPanel.Controls.Add(this.removeButton);
            this.readerPanel.Controls.Add(this.upgradeButton);
            this.readerPanel.Controls.Add(this.groupBox1);
            this.readerPanel.Controls.Add(this.label10);
            this.readerPanel.Controls.Add(this.label_type);
            this.readerPanel.Controls.Add(this.label8);
            this.readerPanel.Controls.Add(this.text_log);
            this.readerPanel.Controls.Add(this.label_maxTemp);
            this.readerPanel.Controls.Add(this.label_temp);
            this.readerPanel.Controls.Add(this.label4);
            this.readerPanel.Controls.Add(this.label3);
            this.readerPanel.Location = new System.Drawing.Point(509, 37);
            this.readerPanel.Name = "readerPanel";
            this.readerPanel.Size = new System.Drawing.Size(698, 738);
            this.readerPanel.TabIndex = 7;
            // 
            // button_media
            // 
            this.button_media.Location = new System.Drawing.Point(224, 312);
            this.button_media.Name = "button_media";
            this.button_media.Size = new System.Drawing.Size(75, 23);
            this.button_media.TabIndex = 33;
            this.button_media.Text = "load media";
            this.button_media.UseVisualStyleBackColor = true;
            this.button_media.Click += new System.EventHandler(this.button_media_Click);
            // 
            // identifyCheckBox
            // 
            this.identifyCheckBox.Appearance = System.Windows.Forms.Appearance.Button;
            this.identifyCheckBox.Location = new System.Drawing.Point(42, 312);
            this.identifyCheckBox.Name = "identifyCheckBox";
            this.identifyCheckBox.Size = new System.Drawing.Size(75, 23);
            this.identifyCheckBox.TabIndex = 32;
            this.identifyCheckBox.Text = "identify";
            this.identifyCheckBox.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            this.identifyCheckBox.UseVisualStyleBackColor = true;
            this.identifyCheckBox.CheckedChanged += new System.EventHandler(this.identifyCheckBox_CheckedChanged);
            // 
            // removeButton
            // 
            this.removeButton.Location = new System.Drawing.Point(133, 312);
            this.removeButton.Name = "removeButton";
            this.removeButton.Size = new System.Drawing.Size(75, 23);
            this.removeButton.TabIndex = 31;
            this.removeButton.Text = "remove";
            this.removeButton.UseVisualStyleBackColor = true;
            this.removeButton.Click += new System.EventHandler(this.removeButton_Click);
            // 
            // upgradeButton
            // 
            this.upgradeButton.Location = new System.Drawing.Point(224, 279);
            this.upgradeButton.Name = "upgradeButton";
            this.upgradeButton.Size = new System.Drawing.Size(75, 23);
            this.upgradeButton.TabIndex = 30;
            this.upgradeButton.Text = "upgrade";
            this.upgradeButton.UseVisualStyleBackColor = true;
            this.upgradeButton.Click += new System.EventHandler(this.upgradeButton_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.BackColor = System.Drawing.SystemColors.ControlLight;
            this.groupBox1.Controls.Add(this.label_RFIDRestarts);
            this.groupBox1.Controls.Add(this.label14);
            this.groupBox1.Controls.Add(this.label_xbioDiagnosticEvents);
            this.groupBox1.Controls.Add(this.label13);
            this.groupBox1.Controls.Add(this.label_testDuration);
            this.groupBox1.Controls.Add(this.label12);
            this.groupBox1.Controls.Add(this.label5);
            this.groupBox1.Controls.Add(this.label6);
            this.groupBox1.Controls.Add(this.label7);
            this.groupBox1.Controls.Add(this.label9);
            this.groupBox1.Controls.Add(this.label_diagnosticEvents);
            this.groupBox1.Controls.Add(this.label11);
            this.groupBox1.Controls.Add(this.label2);
            this.groupBox1.Controls.Add(this.reportButton);
            this.groupBox1.Controls.Add(this.label_hellos);
            this.groupBox1.Controls.Add(this.label_taps);
            this.groupBox1.Controls.Add(this.clearButton);
            this.groupBox1.Controls.Add(this.label_events);
            this.groupBox1.Controls.Add(this.label_xbioEvents);
            this.groupBox1.Controls.Add(this.label_errors);
            this.groupBox1.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.groupBox1.ForeColor = System.Drawing.SystemColors.ControlText;
            this.groupBox1.Location = new System.Drawing.Point(343, 30);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(309, 336);
            this.groupBox1.TabIndex = 29;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Test results";
            // 
            // label_RFIDRestarts
            // 
            this.label_RFIDRestarts.AutoSize = true;
            this.label_RFIDRestarts.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_RFIDRestarts.ForeColor = System.Drawing.Color.Navy;
            this.label_RFIDRestarts.Location = new System.Drawing.Point(174, 229);
            this.label_RFIDRestarts.Name = "label_RFIDRestarts";
            this.label_RFIDRestarts.Size = new System.Drawing.Size(51, 20);
            this.label_RFIDRestarts.TabIndex = 33;
            this.label_RFIDRestarts.Text = "label5";
            // 
            // label14
            // 
            this.label14.AutoSize = true;
            this.label14.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label14.Location = new System.Drawing.Point(38, 229);
            this.label14.Name = "label14";
            this.label14.Size = new System.Drawing.Size(110, 20);
            this.label14.TabIndex = 32;
            this.label14.Text = "RFID restarts:";
            this.label14.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label_xbioDiagnosticEvents
            // 
            this.label_xbioDiagnosticEvents.AutoSize = true;
            this.label_xbioDiagnosticEvents.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_xbioDiagnosticEvents.ForeColor = System.Drawing.Color.Navy;
            this.label_xbioDiagnosticEvents.Location = new System.Drawing.Point(174, 162);
            this.label_xbioDiagnosticEvents.Name = "label_xbioDiagnosticEvents";
            this.label_xbioDiagnosticEvents.Size = new System.Drawing.Size(51, 20);
            this.label_xbioDiagnosticEvents.TabIndex = 31;
            this.label_xbioDiagnosticEvents.Text = "label5";
            // 
            // label13
            // 
            this.label13.AutoSize = true;
            this.label13.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label13.Location = new System.Drawing.Point(23, 162);
            this.label13.Name = "label13";
            this.label13.Size = new System.Drawing.Size(131, 20);
            this.label13.TabIndex = 30;
            this.label13.Text = "xBIO Diag. msgs:";
            this.label13.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label_testDuration
            // 
            this.label_testDuration.AutoSize = true;
            this.label_testDuration.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_testDuration.ForeColor = System.Drawing.Color.Navy;
            this.label_testDuration.Location = new System.Drawing.Point(174, 42);
            this.label_testDuration.Name = "label_testDuration";
            this.label_testDuration.Size = new System.Drawing.Size(51, 20);
            this.label_testDuration.TabIndex = 29;
            this.label_testDuration.Text = "label5";
            // 
            // label12
            // 
            this.label12.AutoSize = true;
            this.label12.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label12.Location = new System.Drawing.Point(46, 42);
            this.label12.Name = "label12";
            this.label12.Size = new System.Drawing.Size(109, 20);
            this.label12.TabIndex = 28;
            this.label12.Text = "Test Duration:";
            this.label12.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label5.Location = new System.Drawing.Point(97, 66);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(57, 20);
            this.label5.TabIndex = 8;
            this.label5.Text = "Hellos:";
            this.label5.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label6.Location = new System.Drawing.Point(54, 186);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(99, 20);
            this.label6.TabIndex = 9;
            this.label6.Text = "Total events:";
            this.label6.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label7.Location = new System.Drawing.Point(105, 90);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(48, 20);
            this.label7.TabIndex = 10;
            this.label7.Text = "Taps:";
            this.label7.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label9.Location = new System.Drawing.Point(69, 114);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(85, 20);
            this.label9.TabIndex = 12;
            this.label9.Text = "xbio reads:";
            this.label9.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label_diagnosticEvents
            // 
            this.label_diagnosticEvents.AutoSize = true;
            this.label_diagnosticEvents.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_diagnosticEvents.ForeColor = System.Drawing.Color.Navy;
            this.label_diagnosticEvents.Location = new System.Drawing.Point(174, 138);
            this.label_diagnosticEvents.Name = "label_diagnosticEvents";
            this.label_diagnosticEvents.Size = new System.Drawing.Size(51, 20);
            this.label_diagnosticEvents.TabIndex = 24;
            this.label_diagnosticEvents.Text = "label5";
            // 
            // label11
            // 
            this.label11.AutoSize = true;
            this.label11.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label11.Location = new System.Drawing.Point(92, 249);
            this.label11.Name = "label11";
            this.label11.Size = new System.Drawing.Size(56, 20);
            this.label11.TabIndex = 14;
            this.label11.Text = "Errors:";
            this.label11.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label2.Location = new System.Drawing.Point(24, 138);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(130, 20);
            this.label2.TabIndex = 23;
            this.label2.Text = "Diagnostic msgs:";
            this.label2.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // reportButton
            // 
            this.reportButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.reportButton.Location = new System.Drawing.Point(175, 296);
            this.reportButton.Name = "reportButton";
            this.reportButton.Size = new System.Drawing.Size(75, 23);
            this.reportButton.TabIndex = 3;
            this.reportButton.Text = "report";
            this.reportButton.UseVisualStyleBackColor = true;
            this.reportButton.Click += new System.EventHandler(this.reportButton_Click);
            // 
            // label_hellos
            // 
            this.label_hellos.AutoSize = true;
            this.label_hellos.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_hellos.ForeColor = System.Drawing.Color.Navy;
            this.label_hellos.Location = new System.Drawing.Point(174, 66);
            this.label_hellos.Name = "label_hellos";
            this.label_hellos.Size = new System.Drawing.Size(51, 20);
            this.label_hellos.TabIndex = 15;
            this.label_hellos.Text = "label5";
            // 
            // label_taps
            // 
            this.label_taps.AutoSize = true;
            this.label_taps.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_taps.ForeColor = System.Drawing.Color.Navy;
            this.label_taps.Location = new System.Drawing.Point(174, 90);
            this.label_taps.Name = "label_taps";
            this.label_taps.Size = new System.Drawing.Size(51, 20);
            this.label_taps.TabIndex = 16;
            this.label_taps.Text = "label5";
            // 
            // clearButton
            // 
            this.clearButton.Font = new System.Drawing.Font("Microsoft Sans Serif", 9.75F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.clearButton.Location = new System.Drawing.Point(60, 296);
            this.clearButton.Name = "clearButton";
            this.clearButton.Size = new System.Drawing.Size(75, 23);
            this.clearButton.TabIndex = 1;
            this.clearButton.Text = "clear";
            this.clearButton.UseVisualStyleBackColor = true;
            this.clearButton.Click += new System.EventHandler(this.clearButton_Click);
            // 
            // label_events
            // 
            this.label_events.AutoSize = true;
            this.label_events.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_events.ForeColor = System.Drawing.Color.Navy;
            this.label_events.Location = new System.Drawing.Point(174, 186);
            this.label_events.Name = "label_events";
            this.label_events.Size = new System.Drawing.Size(51, 20);
            this.label_events.TabIndex = 21;
            this.label_events.Text = "label5";
            // 
            // label_xbioEvents
            // 
            this.label_xbioEvents.AutoSize = true;
            this.label_xbioEvents.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_xbioEvents.ForeColor = System.Drawing.Color.Navy;
            this.label_xbioEvents.Location = new System.Drawing.Point(174, 114);
            this.label_xbioEvents.Name = "label_xbioEvents";
            this.label_xbioEvents.Size = new System.Drawing.Size(51, 20);
            this.label_xbioEvents.TabIndex = 18;
            this.label_xbioEvents.Text = "label5";
            // 
            // label_errors
            // 
            this.label_errors.AutoSize = true;
            this.label_errors.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_errors.ForeColor = System.Drawing.Color.Navy;
            this.label_errors.Location = new System.Drawing.Point(174, 249);
            this.label_errors.Name = "label_errors";
            this.label_errors.Size = new System.Drawing.Size(51, 20);
            this.label_errors.TabIndex = 20;
            this.label_errors.Text = "label5";
            // 
            // label10
            // 
            this.label10.AutoSize = true;
            this.label10.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label10.Location = new System.Drawing.Point(29, 366);
            this.label10.Name = "label10";
            this.label10.Size = new System.Drawing.Size(40, 20);
            this.label10.TabIndex = 27;
            this.label10.Text = "Log:";
            this.label10.TextAlign = System.Drawing.ContentAlignment.TopRight;
            // 
            // label_type
            // 
            this.label_type.AutoSize = true;
            this.label_type.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_type.ForeColor = System.Drawing.Color.Navy;
            this.label_type.Location = new System.Drawing.Point(198, 66);
            this.label_type.Name = "label_type";
            this.label_type.Size = new System.Drawing.Size(51, 20);
            this.label_type.TabIndex = 26;
            this.label_type.Text = "label5";
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label8.Location = new System.Drawing.Point(38, 66);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(100, 20);
            this.label8.TabIndex = 25;
            this.label8.Text = "Reader type:";
            // 
            // text_log
            // 
            this.text_log.BackColor = System.Drawing.Color.Snow;
            this.text_log.Location = new System.Drawing.Point(15, 389);
            this.text_log.Multiline = true;
            this.text_log.Name = "text_log";
            this.text_log.ReadOnly = true;
            this.text_log.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.text_log.Size = new System.Drawing.Size(676, 331);
            this.text_log.TabIndex = 22;
            this.text_log.TextChanged += new System.EventHandler(this.text_log_TextChanged);
            // 
            // label_maxTemp
            // 
            this.label_maxTemp.AutoSize = true;
            this.label_maxTemp.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_maxTemp.ForeColor = System.Drawing.Color.Navy;
            this.label_maxTemp.Location = new System.Drawing.Point(198, 197);
            this.label_maxTemp.Name = "label_maxTemp";
            this.label_maxTemp.Size = new System.Drawing.Size(51, 20);
            this.label_maxTemp.TabIndex = 7;
            this.label_maxTemp.Text = "label5";
            // 
            // label_temp
            // 
            this.label_temp.AutoSize = true;
            this.label_temp.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label_temp.ForeColor = System.Drawing.Color.Navy;
            this.label_temp.Location = new System.Drawing.Point(198, 168);
            this.label_temp.Name = "label_temp";
            this.label_temp.Size = new System.Drawing.Size(51, 20);
            this.label_temp.TabIndex = 6;
            this.label_temp.Text = "label5";
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label4.Location = new System.Drawing.Point(38, 197);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(137, 20);
            this.label4.TabIndex = 5;
            this.label4.Text = "Max Temperature:";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Font = new System.Drawing.Font("Microsoft Sans Serif", 12F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.label3.Location = new System.Drawing.Point(38, 168);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(161, 20);
            this.label3.TabIndex = 4;
            this.label3.Text = "Current Temperature:";
            // 
            // menuStrip1
            // 
            this.menuStrip1.BackColor = System.Drawing.SystemColors.Menu;
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.optionsToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(1216, 24);
            this.menuStrip1.TabIndex = 8;
            this.menuStrip1.Text = "menuStrip1";
            // 
            // optionsToolStripMenuItem
            // 
            this.optionsToolStripMenuItem.Name = "optionsToolStripMenuItem";
            this.optionsToolStripMenuItem.Size = new System.Drawing.Size(61, 20);
            this.optionsToolStripMenuItem.Text = "Options";
            this.optionsToolStripMenuItem.Click += new System.EventHandler(this.optionsToolStripMenuItem_Click);
            // 
            // box_logTemperature
            // 
            this.box_logTemperature.AutoSize = true;
            this.box_logTemperature.Location = new System.Drawing.Point(42, 233);
            this.box_logTemperature.Name = "box_logTemperature";
            this.box_logTemperature.Size = new System.Drawing.Size(103, 17);
            this.box_logTemperature.TabIndex = 34;
            this.box_logTemperature.Text = "Log temperature";
            this.box_logTemperature.UseVisualStyleBackColor = true;
            this.box_logTemperature.CheckedChanged += new System.EventHandler(this.box_logTemperature_CheckedChanged);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackColor = System.Drawing.SystemColors.AppWorkspace;
            this.ClientSize = new System.Drawing.Size(1216, 782);
            this.Controls.Add(this.readerPanel);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.buttonAddReader);
            this.Controls.Add(this.textBoxAddReaderIP);
            this.Controls.Add(this.dataGridViewReaderList);
            this.Controls.Add(this.menuStrip1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "MainForm";
            this.Text = "Reader Burn In Test v x.y";
            ((System.ComponentModel.ISupportInitialize)(this.dataGridViewReaderList)).EndInit();
            this.readerPanel.ResumeLayout(false);
            this.readerPanel.PerformLayout();
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.DataGridView dataGridViewReaderList;
        private System.Windows.Forms.TextBox textBoxAddReaderIP;
        private System.Windows.Forms.Button buttonAddReader;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Timer timer1;
        private System.Windows.Forms.Panel readerPanel;
        private System.Windows.Forms.Button reportButton;
        private System.Windows.Forms.Button clearButton;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label_maxTemp;
        private System.Windows.Forms.Label label_temp;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Label label_hellos;
        private System.Windows.Forms.Label label11;
        private System.Windows.Forms.Label label_events;
        private System.Windows.Forms.Label label_errors;
        private System.Windows.Forms.Label label_xbioEvents;
        private System.Windows.Forms.Label label_taps;
        private System.Windows.Forms.TextBox text_log;
        private System.Windows.Forms.Label label_diagnosticEvents;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label_type;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.DataGridViewImageColumn Status;
        private System.Windows.Forms.DataGridViewTextBoxColumn IP;
        private System.Windows.Forms.DataGridViewTextBoxColumn Column1;
        private System.Windows.Forms.DataGridViewTextBoxColumn Type;
        private System.Windows.Forms.Label label10;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.Label label_testDuration;
        private System.Windows.Forms.Label label12;
        private System.Windows.Forms.Button upgradeButton;
        private System.Windows.Forms.Button removeButton;
        private System.Windows.Forms.CheckBox identifyCheckBox;
        private System.Windows.Forms.Label label_RFIDRestarts;
        private System.Windows.Forms.Label label14;
        private System.Windows.Forms.Label label_xbioDiagnosticEvents;
        private System.Windows.Forms.Label label13;
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem optionsToolStripMenuItem;
        private System.Windows.Forms.Button button_media;
        private System.Windows.Forms.CheckBox box_logTemperature;
    }
}

