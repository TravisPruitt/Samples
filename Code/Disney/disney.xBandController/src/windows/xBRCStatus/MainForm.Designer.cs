namespace com.disney.xband.xbrc.xBRCStatus
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.tbxBRC = new System.Windows.Forms.TextBox();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.lblStatus = new System.Windows.Forms.Label();
            this.lblStatusMessage = new System.Windows.Forms.Label();
            this.pBar = new System.Windows.Forms.ProgressBar();
            this.btnConfig = new System.Windows.Forms.Button();
            this.xsc = new com.disney.xband.xbrc.xBRCStatus.XbrcStatControl();
            this.SuspendLayout();
            // 
            // tbxBRC
            // 
            this.tbxBRC.Location = new System.Drawing.Point(56, 10);
            this.tbxBRC.Name = "tbxBRC";
            this.tbxBRC.Size = new System.Drawing.Size(173, 20);
            this.tbxBRC.TabIndex = 0;
            this.tbxBRC.KeyDown += new System.Windows.Forms.KeyEventHandler(this.tbxBRC_KeyDown);
            this.tbxBRC.Leave += new System.EventHandler(this.tbxBRC_Leave);
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(13, 13);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(37, 13);
            this.label1.TabIndex = 1;
            this.label1.Text = "xBRC:";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(13, 39);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(40, 13);
            this.label2.TabIndex = 1;
            this.label2.Text = "Status:";
            // 
            // lblStatus
            // 
            this.lblStatus.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblStatus.Location = new System.Drawing.Point(53, 39);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(176, 23);
            this.lblStatus.TabIndex = 3;
            // 
            // lblStatusMessage
            // 
            this.lblStatusMessage.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.lblStatusMessage.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblStatusMessage.Location = new System.Drawing.Point(235, 39);
            this.lblStatusMessage.Name = "lblStatusMessage";
            this.lblStatusMessage.Size = new System.Drawing.Size(613, 23);
            this.lblStatusMessage.TabIndex = 3;
            // 
            // pBar
            // 
            this.pBar.Location = new System.Drawing.Point(235, 10);
            this.pBar.Name = "pBar";
            this.pBar.Size = new System.Drawing.Size(167, 23);
            this.pBar.TabIndex = 4;
            // 
            // btnConfig
            // 
            this.btnConfig.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnConfig.Image = global::com.disney.xband.xbrc.xBRCStatus.Properties.Resources.gear;
            this.btnConfig.Location = new System.Drawing.Point(819, 10);
            this.btnConfig.Name = "btnConfig";
            this.btnConfig.Size = new System.Drawing.Size(29, 26);
            this.btnConfig.TabIndex = 5;
            this.btnConfig.UseVisualStyleBackColor = true;
            this.btnConfig.Click += new System.EventHandler(this.btnConfig_Click);
            // 
            // xsc
            // 
            this.xsc.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.xsc.BackColor = System.Drawing.SystemColors.ControlDark;
            this.xsc.Location = new System.Drawing.Point(8, 67);
            this.xsc.Name = "xsc";
            this.xsc.Size = new System.Drawing.Size(847, 576);
            this.xsc.TabIndex = 6;
            this.xsc.XbrcURL = null;
            // 
            // MainForm
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(854, 640);
            this.Controls.Add(this.xsc);
            this.Controls.Add(this.btnConfig);
            this.Controls.Add(this.pBar);
            this.Controls.Add(this.lblStatusMessage);
            this.Controls.Add(this.lblStatus);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.tbxBRC);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "MainForm";
            this.Text = "xBRC Status";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox tbxBRC;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblStatus;
        private System.Windows.Forms.Label lblStatusMessage;
        private System.Windows.Forms.ProgressBar pBar;
        private System.Windows.Forms.Button btnConfig;
        private XbrcStatControl xsc;
    }
}

