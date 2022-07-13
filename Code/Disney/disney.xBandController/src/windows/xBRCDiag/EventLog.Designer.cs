namespace xBRCDiag
{
    partial class EventLog
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(EventLog));
            this.tbEventLog = new System.Windows.Forms.TextBox();
            this.timer = new System.Windows.Forms.Timer(this.components);
            this.SuspendLayout();
            // 
            // tbEventLog
            // 
            this.tbEventLog.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tbEventLog.Location = new System.Drawing.Point(0, 0);
            this.tbEventLog.Multiline = true;
            this.tbEventLog.Name = "tbEventLog";
            this.tbEventLog.ReadOnly = true;
            this.tbEventLog.ScrollBars = System.Windows.Forms.ScrollBars.Both;
            this.tbEventLog.Size = new System.Drawing.Size(518, 325);
            this.tbEventLog.TabIndex = 0;
            // 
            // timer
            // 
            this.timer.Interval = 1000;
            this.timer.Tick += new System.EventHandler(this.timer_Tick);
            // 
            // EventLog
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(518, 325);
            this.Controls.Add(this.tbEventLog);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "EventLog";
            this.Text = "Event Log";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.EventLog_FormClosing);
            this.Load += new System.EventHandler(this.EventLog_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox tbEventLog;
        private System.Windows.Forms.Timer timer;
    }
}