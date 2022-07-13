namespace com.disney.xband.xbrc.MobileGxpTest
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
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.btnTap = new System.Windows.Forms.Button();
            this.groupBox1 = new System.Windows.Forms.GroupBox();
            this.cbFacility = new System.Windows.Forms.ComboBox();
            this.label4 = new System.Windows.Forms.Label();
            this.tbXBRC = new System.Windows.Forms.TextBox();
            this.tbXBRMS = new System.Windows.Forms.TextBox();
            this.rbXBRC = new System.Windows.Forms.RadioButton();
            this.rbXBRMS = new System.Windows.Forms.RadioButton();
            this.cbLocationId = new System.Windows.Forms.ComboBox();
            this.tbBandId = new System.Windows.Forms.TextBox();
            this.groupBox1.SuspendLayout();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(13, 77);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(84, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "&Entertainment Id";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(13, 197);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(60, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "&Location Id";
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(13, 223);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(44, 13);
            this.label3.TabIndex = 4;
            this.label3.Text = "&Band Id";
            // 
            // btnTap
            // 
            this.btnTap.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.btnTap.Enabled = false;
            this.btnTap.Location = new System.Drawing.Point(-2, 246);
            this.btnTap.Name = "btnTap";
            this.btnTap.Size = new System.Drawing.Size(436, 108);
            this.btnTap.TabIndex = 6;
            this.btnTap.Text = "&Tap";
            this.btnTap.UseVisualStyleBackColor = true;
            this.btnTap.Click += new System.EventHandler(this.btnTap_Click);
            // 
            // groupBox1
            // 
            this.groupBox1.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.groupBox1.Controls.Add(this.cbFacility);
            this.groupBox1.Controls.Add(this.label4);
            this.groupBox1.Controls.Add(this.tbXBRC);
            this.groupBox1.Controls.Add(this.tbXBRMS);
            this.groupBox1.Controls.Add(this.rbXBRC);
            this.groupBox1.Controls.Add(this.rbXBRMS);
            this.groupBox1.Location = new System.Drawing.Point(13, 13);
            this.groupBox1.Name = "groupBox1";
            this.groupBox1.Size = new System.Drawing.Size(409, 175);
            this.groupBox1.TabIndex = 0;
            this.groupBox1.TabStop = false;
            this.groupBox1.Text = "&Connection mechanism";
            // 
            // cbFacility
            // 
            this.cbFacility.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cbFacility.FormattingEnabled = true;
            this.cbFacility.Location = new System.Drawing.Point(66, 61);
            this.cbFacility.Name = "cbFacility";
            this.cbFacility.Size = new System.Drawing.Size(323, 21);
            this.cbFacility.Sorted = true;
            this.cbFacility.TabIndex = 3;
            this.cbFacility.DropDown += new System.EventHandler(this.cbFacility_DropDown);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(22, 64);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(39, 13);
            this.label4.TabIndex = 2;
            this.label4.Text = "&Facility";
            // 
            // tbXBRC
            // 
            this.tbXBRC.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbXBRC.Enabled = false;
            this.tbXBRC.Location = new System.Drawing.Point(25, 135);
            this.tbXBRC.Name = "tbXBRC";
            this.tbXBRC.Size = new System.Drawing.Size(364, 20);
            this.tbXBRC.TabIndex = 5;
            this.tbXBRC.TextChanged += new System.EventHandler(this.tbXBRC_TextChanged);
            // 
            // tbXBRMS
            // 
            this.tbXBRMS.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbXBRMS.Location = new System.Drawing.Point(25, 38);
            this.tbXBRMS.Name = "tbXBRMS";
            this.tbXBRMS.Size = new System.Drawing.Size(364, 20);
            this.tbXBRMS.TabIndex = 1;
            // 
            // rbXBRC
            // 
            this.rbXBRC.AutoSize = true;
            this.rbXBRC.Location = new System.Drawing.Point(7, 116);
            this.rbXBRC.Name = "rbXBRC";
            this.rbXBRC.Size = new System.Drawing.Size(93, 17);
            this.rbXBRC.TabIndex = 4;
            this.rbXBRC.Text = "Specific xBR&C";
            this.rbXBRC.UseVisualStyleBackColor = true;
            // 
            // rbXBRMS
            // 
            this.rbXBRMS.AutoSize = true;
            this.rbXBRMS.Checked = true;
            this.rbXBRMS.Location = new System.Drawing.Point(7, 20);
            this.rbXBRMS.Name = "rbXBRMS";
            this.rbXBRMS.Size = new System.Drawing.Size(61, 17);
            this.rbXBRMS.TabIndex = 0;
            this.rbXBRMS.TabStop = true;
            this.rbXBRMS.Text = "xBR&MS";
            this.rbXBRMS.UseVisualStyleBackColor = true;
            this.rbXBRMS.CheckedChanged += new System.EventHandler(this.Mechanism_CheckedChanged);
            // 
            // cbLocationId
            // 
            this.cbLocationId.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.cbLocationId.FormattingEnabled = true;
            this.cbLocationId.Location = new System.Drawing.Point(79, 194);
            this.cbLocationId.Name = "cbLocationId";
            this.cbLocationId.Size = new System.Drawing.Size(323, 21);
            this.cbLocationId.Sorted = true;
            this.cbLocationId.TabIndex = 3;
            this.cbLocationId.DropDown += new System.EventHandler(this.cbLocationId_DropDown);
            this.cbLocationId.SelectionChangeCommitted += new System.EventHandler(this.cbLocationId_SelectionChangeCommitted);
            // 
            // tbBandId
            // 
            this.tbBandId.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbBandId.Enabled = false;
            this.tbBandId.Location = new System.Drawing.Point(79, 220);
            this.tbBandId.Name = "tbBandId";
            this.tbBandId.Size = new System.Drawing.Size(323, 20);
            this.tbBandId.TabIndex = 5;
            this.tbBandId.TextChanged += new System.EventHandler(this.validate_Tap);
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(434, 352);
            this.Controls.Add(this.cbLocationId);
            this.Controls.Add(this.tbBandId);
            this.Controls.Add(this.groupBox1);
            this.Controls.Add(this.btnTap);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Name = "MainForm";
            this.Text = "Mobile Gxp";
            this.groupBox1.ResumeLayout(false);
            this.groupBox1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Button btnTap;
        private System.Windows.Forms.GroupBox groupBox1;
        private System.Windows.Forms.TextBox tbXBRC;
        private System.Windows.Forms.TextBox tbXBRMS;
        private System.Windows.Forms.RadioButton rbXBRC;
        private System.Windows.Forms.RadioButton rbXBRMS;
        private System.Windows.Forms.ComboBox cbFacility;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.ComboBox cbLocationId;
        private System.Windows.Forms.TextBox tbBandId;
    }
}

