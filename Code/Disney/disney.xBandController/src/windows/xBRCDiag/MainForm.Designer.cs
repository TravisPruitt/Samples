namespace xBRCDiag
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.label1 = new System.Windows.Forms.Label();
            this.tbXBrc = new System.Windows.Forms.TextBox();
            this.btnConnect = new System.Windows.Forms.Button();
            this.btnDisconnect = new System.Windows.Forms.Button();
            this.gbStatus = new System.Windows.Forms.GroupBox();
            this.lblEventCount = new System.Windows.Forms.Label();
            this.lblIDMSTime = new System.Windows.Forms.Label();
            this.lblLastMsgToHTTP = new System.Windows.Forms.Label();
            this.lblLastMsgToJMS = new System.Windows.Forms.Label();
            this.lblMainLoop = new System.Windows.Forms.Label();
            this.lblVersion = new System.Windows.Forms.Label();
            this.label9 = new System.Windows.Forms.Label();
            this.lblLastMsgSeq = new System.Windows.Forms.Label();
            this.label5 = new System.Windows.Forms.Label();
            this.label8 = new System.Windows.Forms.Label();
            this.lblStatusMessage = new System.Windows.Forms.Label();
            this.label4 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.lblCondition = new System.Windows.Forms.Label();
            this.label3 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.timer = new System.Windows.Forms.Timer(this.components);
            this.btnEventLog = new System.Windows.Forms.Button();
            this.gbStatus.SuspendLayout();
            this.SuspendLayout();
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(33, 13);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(75, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "xBRC Address";
            // 
            // tbXBrc
            // 
            this.tbXBrc.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbXBrc.Location = new System.Drawing.Point(125, 10);
            this.tbXBrc.Name = "tbXBrc";
            this.tbXBrc.Size = new System.Drawing.Size(395, 20);
            this.tbXBrc.TabIndex = 1;
            this.tbXBrc.TextChanged += new System.EventHandler(this.tbXBrc_TextChanged);
            // 
            // btnConnect
            // 
            this.btnConnect.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnConnect.Enabled = false;
            this.btnConnect.Location = new System.Drawing.Point(536, 8);
            this.btnConnect.Name = "btnConnect";
            this.btnConnect.Size = new System.Drawing.Size(75, 23);
            this.btnConnect.TabIndex = 2;
            this.btnConnect.Text = "&Connect";
            this.btnConnect.UseVisualStyleBackColor = true;
            this.btnConnect.Click += new System.EventHandler(this.btnConnect_Click);
            // 
            // btnDisconnect
            // 
            this.btnDisconnect.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnDisconnect.Location = new System.Drawing.Point(542, 8);
            this.btnDisconnect.Name = "btnDisconnect";
            this.btnDisconnect.Size = new System.Drawing.Size(75, 23);
            this.btnDisconnect.TabIndex = 3;
            this.btnDisconnect.Text = "&Disconnect";
            this.btnDisconnect.UseVisualStyleBackColor = true;
            this.btnDisconnect.Visible = false;
            this.btnDisconnect.Click += new System.EventHandler(this.btnDisconnect_Click);
            // 
            // gbStatus
            // 
            this.gbStatus.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom) 
            | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.gbStatus.Controls.Add(this.btnEventLog);
            this.gbStatus.Controls.Add(this.lblEventCount);
            this.gbStatus.Controls.Add(this.lblIDMSTime);
            this.gbStatus.Controls.Add(this.lblLastMsgToHTTP);
            this.gbStatus.Controls.Add(this.lblLastMsgToJMS);
            this.gbStatus.Controls.Add(this.lblMainLoop);
            this.gbStatus.Controls.Add(this.lblVersion);
            this.gbStatus.Controls.Add(this.label9);
            this.gbStatus.Controls.Add(this.lblLastMsgSeq);
            this.gbStatus.Controls.Add(this.label5);
            this.gbStatus.Controls.Add(this.label8);
            this.gbStatus.Controls.Add(this.lblStatusMessage);
            this.gbStatus.Controls.Add(this.label4);
            this.gbStatus.Controls.Add(this.label6);
            this.gbStatus.Controls.Add(this.label7);
            this.gbStatus.Controls.Add(this.lblCondition);
            this.gbStatus.Controls.Add(this.label3);
            this.gbStatus.Controls.Add(this.label2);
            this.gbStatus.Enabled = false;
            this.gbStatus.Location = new System.Drawing.Point(16, 49);
            this.gbStatus.Name = "gbStatus";
            this.gbStatus.Size = new System.Drawing.Size(601, 303);
            this.gbStatus.TabIndex = 3;
            this.gbStatus.TabStop = false;
            this.gbStatus.Text = "Status";
            // 
            // lblEventCount
            // 
            this.lblEventCount.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblEventCount.Location = new System.Drawing.Point(109, 235);
            this.lblEventCount.Name = "lblEventCount";
            this.lblEventCount.Size = new System.Drawing.Size(60, 23);
            this.lblEventCount.TabIndex = 16;
            this.lblEventCount.Text = "0";
            this.lblEventCount.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblIDMSTime
            // 
            this.lblIDMSTime.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblIDMSTime.Location = new System.Drawing.Point(109, 212);
            this.lblIDMSTime.Name = "lblIDMSTime";
            this.lblIDMSTime.Size = new System.Drawing.Size(60, 23);
            this.lblIDMSTime.TabIndex = 14;
            this.lblIDMSTime.Text = "0";
            this.lblIDMSTime.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblLastMsgToHTTP
            // 
            this.lblLastMsgToHTTP.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblLastMsgToHTTP.Location = new System.Drawing.Point(109, 157);
            this.lblLastMsgToHTTP.Name = "lblLastMsgToHTTP";
            this.lblLastMsgToHTTP.Size = new System.Drawing.Size(60, 23);
            this.lblLastMsgToHTTP.TabIndex = 10;
            this.lblLastMsgToHTTP.Text = "0";
            this.lblLastMsgToHTTP.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblLastMsgToJMS
            // 
            this.lblLastMsgToJMS.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblLastMsgToJMS.Location = new System.Drawing.Point(109, 134);
            this.lblLastMsgToJMS.Name = "lblLastMsgToJMS";
            this.lblLastMsgToJMS.Size = new System.Drawing.Size(60, 23);
            this.lblLastMsgToJMS.TabIndex = 8;
            this.lblLastMsgToJMS.Text = "0";
            this.lblLastMsgToJMS.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblMainLoop
            // 
            this.lblMainLoop.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblMainLoop.Location = new System.Drawing.Point(109, 189);
            this.lblMainLoop.Name = "lblMainLoop";
            this.lblMainLoop.Size = new System.Drawing.Size(60, 23);
            this.lblMainLoop.TabIndex = 12;
            this.lblMainLoop.Text = "0";
            this.lblMainLoop.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // lblVersion
            // 
            this.lblVersion.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.lblVersion.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblVersion.Location = new System.Drawing.Point(109, 16);
            this.lblVersion.Name = "lblVersion";
            this.lblVersion.Size = new System.Drawing.Size(373, 23);
            this.lblVersion.TabIndex = 1;
            this.lblVersion.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // label9
            // 
            this.label9.AutoSize = true;
            this.label9.Location = new System.Drawing.Point(6, 240);
            this.label9.Name = "label9";
            this.label9.Size = new System.Drawing.Size(66, 13);
            this.label9.TabIndex = 15;
            this.label9.Text = "Event Count";
            // 
            // lblLastMsgSeq
            // 
            this.lblLastMsgSeq.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblLastMsgSeq.Location = new System.Drawing.Point(109, 111);
            this.lblLastMsgSeq.Name = "lblLastMsgSeq";
            this.lblLastMsgSeq.Size = new System.Drawing.Size(60, 23);
            this.lblLastMsgSeq.TabIndex = 6;
            this.lblLastMsgSeq.Text = "0";
            this.lblLastMsgSeq.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(6, 162);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(94, 13);
            this.label5.TabIndex = 9;
            this.label5.Text = "Last Msg to HTTP";
            // 
            // label8
            // 
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(6, 217);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(68, 13);
            this.label8.TabIndex = 13;
            this.label8.Text = "IDMS (msec)";
            // 
            // lblStatusMessage
            // 
            this.lblStatusMessage.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.lblStatusMessage.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.lblStatusMessage.Location = new System.Drawing.Point(109, 70);
            this.lblStatusMessage.Name = "lblStatusMessage";
            this.lblStatusMessage.Size = new System.Drawing.Size(373, 23);
            this.lblStatusMessage.TabIndex = 4;
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(6, 139);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(86, 13);
            this.label4.TabIndex = 7;
            this.label4.Text = "Last Msg to JMS";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(6, 21);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(42, 13);
            this.label6.TabIndex = 0;
            this.label6.Text = "Version";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(6, 194);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(68, 13);
            this.label7.TabIndex = 11;
            this.label7.Text = "Main Loop %";
            // 
            // lblCondition
            // 
            this.lblCondition.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.lblCondition.ForeColor = System.Drawing.Color.White;
            this.lblCondition.Location = new System.Drawing.Point(109, 47);
            this.lblCondition.Name = "lblCondition";
            this.lblCondition.Size = new System.Drawing.Size(60, 23);
            this.lblCondition.TabIndex = 3;
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(6, 116);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(72, 13);
            this.label3.TabIndex = 5;
            this.label3.Text = "Last Msg Seq";
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(7, 51);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(51, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "Condition";
            // 
            // timer
            // 
            this.timer.Interval = 1000;
            this.timer.Tick += new System.EventHandler(this.timer_Tick);
            // 
            // btnEventLog
            // 
            this.btnEventLog.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Right)));
            this.btnEventLog.Location = new System.Drawing.Point(520, 19);
            this.btnEventLog.Name = "btnEventLog";
            this.btnEventLog.Size = new System.Drawing.Size(75, 23);
            this.btnEventLog.TabIndex = 4;
            this.btnEventLog.Text = "&Event Log";
            this.btnEventLog.UseVisualStyleBackColor = true;
            this.btnEventLog.Click += new System.EventHandler(this.btnEventLog_Click);
            // 
            // MainForm
            // 
            this.AcceptButton = this.btnConnect;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(629, 364);
            this.Controls.Add(this.gbStatus);
            this.Controls.Add(this.btnConnect);
            this.Controls.Add(this.btnDisconnect);
            this.Controls.Add(this.tbXBrc);
            this.Controls.Add(this.label1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "MainForm";
            this.Text = "xBRC Diagnostic Tool";
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.MainForm_FormClosed);
            this.gbStatus.ResumeLayout(false);
            this.gbStatus.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.TextBox tbXBrc;
        private System.Windows.Forms.Button btnConnect;
        private System.Windows.Forms.Button btnDisconnect;
        private System.Windows.Forms.GroupBox gbStatus;
        private System.Windows.Forms.Label lblCondition;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Label lblStatusMessage;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.Label lblLastMsgToHTTP;
        private System.Windows.Forms.Label lblLastMsgToJMS;
        private System.Windows.Forms.Label lblLastMsgSeq;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.Timer timer;
        private System.Windows.Forms.Label lblVersion;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label lblEventCount;
        private System.Windows.Forms.Label lblIDMSTime;
        private System.Windows.Forms.Label lblMainLoop;
        private System.Windows.Forms.Label label9;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Button btnEventLog;
    }
}

