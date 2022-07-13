namespace xTPManufacturerTest
{
    partial class OptionsDialog
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
            this.box_testLoop = new System.Windows.Forms.CheckBox();
            this.box_xbio = new System.Windows.Forms.CheckBox();
            this.button_okay = new System.Windows.Forms.Button();
            this.button_cancel = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // box_testLoop
            // 
            this.box_testLoop.AutoSize = true;
            this.box_testLoop.Location = new System.Drawing.Point(13, 32);
            this.box_testLoop.Name = "box_testLoop";
            this.box_testLoop.Size = new System.Drawing.Size(110, 17);
            this.box_testLoop.TabIndex = 0;
            this.box_testLoop.Text = "Enable Test Loop";
            this.box_testLoop.UseVisualStyleBackColor = true;
            this.box_testLoop.CheckedChanged += new System.EventHandler(this.box_testLoop_CheckedChanged);
            // 
            // box_xbio
            // 
            this.box_xbio.AutoSize = true;
            this.box_xbio.Location = new System.Drawing.Point(30, 55);
            this.box_xbio.Name = "box_xbio";
            this.box_xbio.Size = new System.Drawing.Size(71, 17);
            this.box_xbio.TabIndex = 3;
            this.box_xbio.Text = "Use xBIO";
            this.box_xbio.UseVisualStyleBackColor = true;
            // 
            // button_okay
            // 
            this.button_okay.Location = new System.Drawing.Point(107, 110);
            this.button_okay.Name = "button_okay";
            this.button_okay.Size = new System.Drawing.Size(75, 23);
            this.button_okay.TabIndex = 4;
            this.button_okay.Text = "Okay";
            this.button_okay.UseVisualStyleBackColor = true;
            this.button_okay.Click += new System.EventHandler(this.button_okay_Click);
            // 
            // button_cancel
            // 
            this.button_cancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.button_cancel.Location = new System.Drawing.Point(201, 110);
            this.button_cancel.Name = "button_cancel";
            this.button_cancel.Size = new System.Drawing.Size(75, 23);
            this.button_cancel.TabIndex = 5;
            this.button_cancel.Text = "Cancel";
            this.button_cancel.UseVisualStyleBackColor = true;
            this.button_cancel.Click += new System.EventHandler(this.button_cancel_Click);
            // 
            // OptionsDialog
            // 
            this.AcceptButton = this.button_okay;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.button_cancel;
            this.ClientSize = new System.Drawing.Size(297, 152);
            this.ControlBox = false;
            this.Controls.Add(this.button_cancel);
            this.Controls.Add(this.button_okay);
            this.Controls.Add(this.box_xbio);
            this.Controls.Add(this.box_testLoop);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "OptionsDialog";
            this.ShowIcon = false;
            this.ShowInTaskbar = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "OptionsDialog";
            this.Load += new System.EventHandler(this.OptionsDialog_Load);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.CheckBox box_testLoop;
        private System.Windows.Forms.CheckBox box_xbio;
        private System.Windows.Forms.Button button_okay;
        private System.Windows.Forms.Button button_cancel;
    }
}