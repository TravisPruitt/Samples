namespace com.disney.xband.xbrc.xBRCLab.Analyses
{
    partial class PacketAnalysis
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

        #region Component Designer generated code

        /// <summary> 
        /// Required method for Designer support - do not modify 
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.dgPackets = new System.Windows.Forms.DataGridView();
            this.Timestamp = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.LRID = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Pno = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.groupBox2 = new System.Windows.Forms.GroupBox();
            this.lbBands = new System.Windows.Forms.ListBox();
            this.groupBox3 = new System.Windows.Forms.GroupBox();
            this.lbReaders = new System.Windows.Forms.ListBox();
            this.groupBox4 = new System.Windows.Forms.GroupBox();
            this.dgAnalysis = new System.Windows.Forms.DataGridView();
            this.Time = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Band = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.PnoA = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.Reader = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2401C0 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2401C1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2424C0 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2424C1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2450C0 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2450C1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2476C0 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.F2476C1 = new System.Windows.Forms.DataGridViewTextBoxColumn();
            this.groupBox5 = new System.Windows.Forms.GroupBox();
            this.lblLostPackets = new System.Windows.Forms.Label();
            this.label1 = new System.Windows.Forms.Label();
            this.groupBox1.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgPackets)).BeginInit();
            this.groupBox2.SuspendLayout();
            this.groupBox3.SuspendLayout();
            this.groupBox4.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.dgAnalysis)).BeginInit();
            this.groupBox5.SuspendLayout();
            this.SuspendLayout();
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox1.Controls.Add(this.dgPackets);
            this.groupBox1.Location = new System.Drawing.Point(272, 4);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(292, 243);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "Packets";
            // 
            // dgPackets
            // 
            this.dgPackets.AllowUserToAddRows = false;
            this.dgPackets.AllowUserToDeleteRows = false;
            this.dgPackets.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.dgPackets.BackgroundColor = System.Drawing.SystemColors.Control;
            this.dgPackets.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgPackets.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Timestamp,
            this.LRID,
            this.Pno});
            this.dgPackets.Location = new System.Drawing.Point(6, 20);
            this.dgPackets.Name = "dgPackets";
            this.dgPackets.ReadOnly = true;
            this.dgPackets.RowHeadersVisible = false;
            this.dgPackets.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.dgPackets.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgPackets.Size = new System.Drawing.Size(276, 212);
            this.dgPackets.TabIndex = 0;
            this.dgPackets.SelectionChanged += new System.EventHandler(this.dgPackets_SelectionChanged);
            // 
            // Timestamp
            // 
            this.Timestamp.Frozen = true;
            this.Timestamp.HeaderText = "Time (s)";
            this.Timestamp.Name = "Timestamp";
            this.Timestamp.ReadOnly = true;
            this.Timestamp.Width = 70;
            // 
            // LRID
            // 
            this.LRID.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.LRID.FillWeight = 60F;
            this.LRID.HeaderText = "Band";
            this.LRID.Name = "LRID";
            this.LRID.ReadOnly = true;
            // 
            // Pno
            // 
            this.Pno.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.Pno.FillWeight = 40F;
            this.Pno.HeaderText = "Packet #";
            this.Pno.Name = "Pno";
            this.Pno.ReadOnly = true;
            // 
            // groupBox2
            // 
            this.groupBox2.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox2.Controls.Add(this.lbBands);
            this.groupBox2.Location = new System.Drawing.Point(570, 4);
            this.groupBox2.Name = "groupBox2";
            this.groupBox2.Size = new System.Drawing.Size(222, 243);
            this.groupBox2.TabIndex = 0;
            this.groupBox2.TabStop = false;
            this.groupBox2.Text = "Bands";
            // 
            // lbBands
            // 
            this.lbBands.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.lbBands.FormattingEnabled = true;
            this.lbBands.IntegralHeight = false;
            this.lbBands.Location = new System.Drawing.Point(7, 20);
            this.lbBands.Name = "lbBands";
            this.lbBands.SelectionMode = System.Windows.Forms.SelectionMode.MultiExtended;
            this.lbBands.Size = new System.Drawing.Size(209, 212);
            this.lbBands.Sorted = true;
            this.lbBands.TabIndex = 0;
            this.lbBands.SelectedIndexChanged += new System.EventHandler(this.lbBands_SelectedIndexChanged);
            // 
            // groupBox3
            // 
            this.groupBox3.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)));
            this.groupBox3.Controls.Add(this.lbReaders);
            this.groupBox3.Location = new System.Drawing.Point(11, 4);
            this.groupBox3.Name = "groupBox3";
            this.groupBox3.Size = new System.Drawing.Size(255, 243);
            this.groupBox3.TabIndex = 0;
            this.groupBox3.TabStop = false;
            this.groupBox3.Text = "Readers";
            // 
            // lbReaders
            // 
            this.lbReaders.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.lbReaders.FormattingEnabled = true;
            this.lbReaders.IntegralHeight = false;
            this.lbReaders.Location = new System.Drawing.Point(7, 20);
            this.lbReaders.Name = "lbReaders";
            this.lbReaders.SelectionMode = System.Windows.Forms.SelectionMode.MultiExtended;
            this.lbReaders.Size = new System.Drawing.Size(242, 212);
            this.lbReaders.Sorted = true;
            this.lbReaders.TabIndex = 0;
            this.lbReaders.SelectedIndexChanged += new System.EventHandler(this.lbReaders_SelectedIndexChanged);
            // 
            // groupBox4
            // 
            this.groupBox4.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox4.Controls.Add(this.dgAnalysis);
            this.groupBox4.Location = new System.Drawing.Point(4, 253);
            this.groupBox4.Name = "groupBox4";
            this.groupBox4.Size = new System.Drawing.Size(781, 188);
            this.groupBox4.TabIndex = 1;
            this.groupBox4.TabStop = false;
            this.groupBox4.Text = "Packet Analysis";
            // 
            // dgAnalysis
            // 
            this.dgAnalysis.AllowUserToAddRows = false;
            this.dgAnalysis.AllowUserToDeleteRows = false;
            this.dgAnalysis.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.dgAnalysis.BackgroundColor = System.Drawing.SystemColors.Control;
            this.dgAnalysis.ColumnHeadersHeightSizeMode = System.Windows.Forms.DataGridViewColumnHeadersHeightSizeMode.AutoSize;
            this.dgAnalysis.Columns.AddRange(new System.Windows.Forms.DataGridViewColumn[] {
            this.Time,
            this.Band,
            this.PnoA,
            this.Reader,
            this.F2401C0,
            this.F2401C1,
            this.F2424C0,
            this.F2424C1,
            this.F2450C0,
            this.F2450C1,
            this.F2476C0,
            this.F2476C1});
            this.dgAnalysis.Location = new System.Drawing.Point(7, 20);
            this.dgAnalysis.MultiSelect = false;
            this.dgAnalysis.Name = "dgAnalysis";
            this.dgAnalysis.ReadOnly = true;
            this.dgAnalysis.RowHeadersVisible = false;
            this.dgAnalysis.ScrollBars = System.Windows.Forms.ScrollBars.Vertical;
            this.dgAnalysis.SelectionMode = System.Windows.Forms.DataGridViewSelectionMode.FullRowSelect;
            this.dgAnalysis.Size = new System.Drawing.Size(774, 162);
            this.dgAnalysis.TabIndex = 0;
            // 
            // Time
            // 
            this.Time.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.Time.FillWeight = 13F;
            this.Time.HeaderText = "Time (s)";
            this.Time.Name = "Time";
            this.Time.ReadOnly = true;
            // 
            // Band
            // 
            this.Band.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.Band.FillWeight = 16F;
            this.Band.HeaderText = "Band";
            this.Band.Name = "Band";
            this.Band.ReadOnly = true;
            // 
            // PnoA
            // 
            this.PnoA.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.PnoA.FillWeight = 10F;
            this.PnoA.HeaderText = "Packet #";
            this.PnoA.Name = "PnoA";
            this.PnoA.ReadOnly = true;
            // 
            // Reader
            // 
            this.Reader.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.Reader.FillWeight = 13F;
            this.Reader.HeaderText = "Reader";
            this.Reader.Name = "Reader";
            this.Reader.ReadOnly = true;
            // 
            // F2401C0
            // 
            this.F2401C0.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2401C0.FillWeight = 6F;
            this.F2401C0.HeaderText = "2401-0";
            this.F2401C0.Name = "F2401C0";
            this.F2401C0.ReadOnly = true;
            // 
            // F2401C1
            // 
            this.F2401C1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2401C1.FillWeight = 6F;
            this.F2401C1.HeaderText = "2401-1";
            this.F2401C1.Name = "F2401C1";
            this.F2401C1.ReadOnly = true;
            // 
            // F2424C0
            // 
            this.F2424C0.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2424C0.FillWeight = 6F;
            this.F2424C0.HeaderText = "2424-0";
            this.F2424C0.Name = "F2424C0";
            this.F2424C0.ReadOnly = true;
            // 
            // F2424C1
            // 
            this.F2424C1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2424C1.FillWeight = 6F;
            this.F2424C1.HeaderText = "2424-1";
            this.F2424C1.Name = "F2424C1";
            this.F2424C1.ReadOnly = true;
            // 
            // F2450C0
            // 
            this.F2450C0.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2450C0.FillWeight = 6F;
            this.F2450C0.HeaderText = "2450-0";
            this.F2450C0.Name = "F2450C0";
            this.F2450C0.ReadOnly = true;
            // 
            // F2450C1
            // 
            this.F2450C1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2450C1.FillWeight = 6F;
            this.F2450C1.HeaderText = "2450-1";
            this.F2450C1.Name = "F2450C1";
            this.F2450C1.ReadOnly = true;
            // 
            // F2476C0
            // 
            this.F2476C0.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2476C0.FillWeight = 6F;
            this.F2476C0.HeaderText = "2476-0";
            this.F2476C0.Name = "F2476C0";
            this.F2476C0.ReadOnly = true;
            // 
            // F2476C1
            // 
            this.F2476C1.AutoSizeMode = System.Windows.Forms.DataGridViewAutoSizeColumnMode.Fill;
            this.F2476C1.FillWeight = 6F;
            this.F2476C1.HeaderText = "2476-1";
            this.F2476C1.Name = "F2476C1";
            this.F2476C1.ReadOnly = true;
            // 
            // groupBox5
            // 
            this.groupBox5.Controls.Add(this.lblLostPackets);
            this.groupBox5.Controls.Add(this.label1);
            this.groupBox5.Location = new System.Drawing.Point(4, 448);
            this.groupBox5.Name = "groupBox5";
            this.groupBox5.Size = new System.Drawing.Size(775, 140);
            this.groupBox5.TabIndex = 2;
            this.groupBox5.TabStop = false;
            this.groupBox5.Text = "Analysis";
            // 
            // lblLostPackets
            // 
            this.lblLostPackets.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblLostPackets.Location = new System.Drawing.Point(82, 29);
            this.lblLostPackets.Name = "lblLostPackets";
            this.lblLostPackets.Size = new System.Drawing.Size(134, 23);
            this.lblLostPackets.TabIndex = 1;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(7, 30);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(69, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "Lost Packets";
            // 
            // PacketAnalysis
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.groupBox5);
            this.Controls.Add(this.groupBox2);
            this.Controls.Add(this.groupBox4);
            this.Controls.Add(this.groupBox3);
            this.Controls.Add(this.groupBox1);
            this.Name = "PacketAnalysis";
            this.Size = new System.Drawing.Size(800, 600);
            this.Load += new System.EventHandler(this.PacketAnalysis_Load);
            this.groupBox1.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dgPackets)).EndInit();
            this.groupBox2.ResumeLayout(false);
            this.groupBox3.ResumeLayout(false);
            this.groupBox4.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.dgAnalysis)).EndInit();
            this.groupBox5.ResumeLayout(false);
            this.groupBox5.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.GroupBox groupBox2;
        private System.Windows.Forms.ListBox lbBands;
        private System.Windows.Forms.DataGridView dgPackets;
        private System.Windows.Forms.DataGridViewTextBoxColumn Timestamp;
        private System.Windows.Forms.DataGridViewTextBoxColumn LRID;
        private System.Windows.Forms.DataGridViewTextBoxColumn Pno;
        private System.Windows.Forms.GroupBox groupBox3;
        private System.Windows.Forms.ListBox lbReaders;
        private System.Windows.Forms.GroupBox groupBox4;
        private System.Windows.Forms.DataGridView dgAnalysis;
        private System.Windows.Forms.DataGridViewTextBoxColumn Time;
        private System.Windows.Forms.DataGridViewTextBoxColumn Band;
        private System.Windows.Forms.DataGridViewTextBoxColumn PnoA;
        private System.Windows.Forms.DataGridViewTextBoxColumn Reader;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2401C0;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2401C1;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2424C0;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2424C1;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2450C0;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2450C1;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2476C0;
        private System.Windows.Forms.DataGridViewTextBoxColumn F2476C1;
        private System.Windows.Forms.GroupBox groupBox5;
        private System.Windows.Forms.Label lblLostPackets;
        private System.Windows.Forms.Label label1;
    }
}
