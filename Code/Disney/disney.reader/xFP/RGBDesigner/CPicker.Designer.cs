namespace RGBDesigner
{
    partial class CPicker
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
            this.pbPickerBox = new System.Windows.Forms.PictureBox();
            this.tbSaturation = new System.Windows.Forms.TrackBar();
            this.tbAlpha = new System.Windows.Forms.TrackBar();
            this.pbPreview = new System.Windows.Forms.PictureBox();
            this.label1 = new System.Windows.Forms.Label();
            this.txtA = new System.Windows.Forms.NumericUpDown();
            this.txtR = new System.Windows.Forms.NumericUpDown();
            this.label2 = new System.Windows.Forms.Label();
            this.txtG = new System.Windows.Forms.NumericUpDown();
            this.label3 = new System.Windows.Forms.Label();
            this.txtB = new System.Windows.Forms.NumericUpDown();
            this.label4 = new System.Windows.Forms.Label();
            this.txtH = new System.Windows.Forms.NumericUpDown();
            this.label5 = new System.Windows.Forms.Label();
            this.label6 = new System.Windows.Forms.Label();
            this.label7 = new System.Windows.Forms.Label();
            this.btnOK = new System.Windows.Forms.Button();
            this.btnCancel = new System.Windows.Forms.Button();
            this.label8 = new System.Windows.Forms.Label();
            this.txtName = new System.Windows.Forms.TextBox();
            this.tbValue = new System.Windows.Forms.TrackBar();
            ((System.ComponentModel.ISupportInitialize)(this.pbPickerBox)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.tbSaturation)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.tbAlpha)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.pbPreview)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtA)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtR)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtG)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtB)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtH)).BeginInit();
            ((System.ComponentModel.ISupportInitialize)(this.tbValue)).BeginInit();
            this.SuspendLayout();
            // 
            // pbPickerBox
            // 
            this.pbPickerBox.BorderStyle = System.Windows.Forms.BorderStyle.FixedSingle;
            this.pbPickerBox.Location = new System.Drawing.Point(12, 12);
            this.pbPickerBox.Name = "pbPickerBox";
            this.pbPickerBox.Size = new System.Drawing.Size(360, 360);
            this.pbPickerBox.TabIndex = 0;
            this.pbPickerBox.TabStop = false;
            this.pbPickerBox.Paint += new System.Windows.Forms.PaintEventHandler(this.pbPickerBox_Paint);
            this.pbPickerBox.MouseDown += new System.Windows.Forms.MouseEventHandler(this.pbPickerBox_MouseDown);
            this.pbPickerBox.MouseMove += new System.Windows.Forms.MouseEventHandler(this.pbPickerBox_MouseMove);
            this.pbPickerBox.MouseUp += new System.Windows.Forms.MouseEventHandler(this.pbPickerBox_MouseUp);
            // 
            // tbSaturation
            // 
            this.tbSaturation.Location = new System.Drawing.Point(382, 178);
            this.tbSaturation.Maximum = 359;
            this.tbSaturation.Name = "tbSaturation";
            this.tbSaturation.Orientation = System.Windows.Forms.Orientation.Vertical;
            this.tbSaturation.Size = new System.Drawing.Size(45, 194);
            this.tbSaturation.TabIndex = 11;
            this.tbSaturation.TickFrequency = 36;
            this.tbSaturation.TickStyle = System.Windows.Forms.TickStyle.Both;
            this.tbSaturation.Value = 359;
            this.tbSaturation.ValueChanged += new System.EventHandler(this.tbSaturation_ValueChanged);
            // 
            // tbAlpha
            // 
            this.tbAlpha.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.tbAlpha.Location = new System.Drawing.Point(12, 380);
            this.tbAlpha.Maximum = 255;
            this.tbAlpha.Name = "tbAlpha";
            this.tbAlpha.Size = new System.Drawing.Size(339, 45);
            this.tbAlpha.TabIndex = 14;
            this.tbAlpha.TickFrequency = 20;
            this.tbAlpha.TickStyle = System.Windows.Forms.TickStyle.Both;
            this.tbAlpha.Value = 255;
            this.tbAlpha.ValueChanged += new System.EventHandler(this.tbAlpha_ValueChanged);
            // 
            // pbPreview
            // 
            this.pbPreview.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.pbPreview.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.pbPreview.Location = new System.Drawing.Point(357, 380);
            this.pbPreview.Name = "pbPreview";
            this.pbPreview.Size = new System.Drawing.Size(45, 45);
            this.pbPreview.TabIndex = 3;
            this.pbPreview.TabStop = false;
            // 
            // label1
            // 
            this.label1.AutoSize = true;
            this.label1.Location = new System.Drawing.Point(379, 12);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(17, 13);
            this.label1.TabIndex = 0;
            this.label1.Text = "A:";
            // 
            // txtA
            // 
            this.txtA.Location = new System.Drawing.Point(402, 10);
            this.txtA.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.txtA.Name = "txtA";
            this.txtA.Size = new System.Drawing.Size(83, 22);
            this.txtA.TabIndex = 1;
            this.txtA.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            this.txtA.Value = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.txtA.ValueChanged += new System.EventHandler(this.valueChanged);
            this.txtA.Enter += new System.EventHandler(this.txt_Enter);
            this.txtA.MouseClick += new System.Windows.Forms.MouseEventHandler(this.valueChanged);
            // 
            // txtR
            // 
            this.txtR.Location = new System.Drawing.Point(402, 38);
            this.txtR.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.txtR.Name = "txtR";
            this.txtR.Size = new System.Drawing.Size(83, 22);
            this.txtR.TabIndex = 3;
            this.txtR.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            this.txtR.Value = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.txtR.ValueChanged += new System.EventHandler(this.valueChanged);
            this.txtR.Enter += new System.EventHandler(this.txt_Enter);
            // 
            // label2
            // 
            this.label2.AutoSize = true;
            this.label2.Location = new System.Drawing.Point(379, 40);
            this.label2.Name = "label2";
            this.label2.Size = new System.Drawing.Size(17, 13);
            this.label2.TabIndex = 2;
            this.label2.Text = "R:";
            // 
            // txtG
            // 
            this.txtG.Location = new System.Drawing.Point(402, 66);
            this.txtG.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.txtG.Name = "txtG";
            this.txtG.Size = new System.Drawing.Size(83, 22);
            this.txtG.TabIndex = 5;
            this.txtG.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            this.txtG.ValueChanged += new System.EventHandler(this.valueChanged);
            this.txtG.Enter += new System.EventHandler(this.txt_Enter);
            // 
            // label3
            // 
            this.label3.AutoSize = true;
            this.label3.Location = new System.Drawing.Point(379, 68);
            this.label3.Name = "label3";
            this.label3.Size = new System.Drawing.Size(18, 13);
            this.label3.TabIndex = 4;
            this.label3.Text = "G:";
            // 
            // txtB
            // 
            this.txtB.Location = new System.Drawing.Point(402, 94);
            this.txtB.Maximum = new decimal(new int[] {
            255,
            0,
            0,
            0});
            this.txtB.Name = "txtB";
            this.txtB.Size = new System.Drawing.Size(83, 22);
            this.txtB.TabIndex = 7;
            this.txtB.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            this.txtB.ValueChanged += new System.EventHandler(this.valueChanged);
            this.txtB.Enter += new System.EventHandler(this.txt_Enter);
            // 
            // label4
            // 
            this.label4.AutoSize = true;
            this.label4.Location = new System.Drawing.Point(379, 96);
            this.label4.Name = "label4";
            this.label4.Size = new System.Drawing.Size(17, 13);
            this.label4.TabIndex = 6;
            this.label4.Text = "B:";
            // 
            // txtH
            // 
            this.txtH.Location = new System.Drawing.Point(402, 132);
            this.txtH.Maximum = new decimal(new int[] {
            359,
            0,
            0,
            0});
            this.txtH.Name = "txtH";
            this.txtH.Size = new System.Drawing.Size(83, 22);
            this.txtH.TabIndex = 9;
            this.txtH.TextAlign = System.Windows.Forms.HorizontalAlignment.Right;
            this.txtH.ValueChanged += new System.EventHandler(this.valueChanged);
            this.txtH.Enter += new System.EventHandler(this.txt_Enter);
            // 
            // label5
            // 
            this.label5.AutoSize = true;
            this.label5.Location = new System.Drawing.Point(379, 134);
            this.label5.Name = "label5";
            this.label5.Size = new System.Drawing.Size(18, 13);
            this.label5.TabIndex = 8;
            this.label5.Text = "H:";
            // 
            // label6
            // 
            this.label6.AutoSize = true;
            this.label6.Location = new System.Drawing.Point(396, 160);
            this.label6.Name = "label6";
            this.label6.Size = new System.Drawing.Size(16, 13);
            this.label6.TabIndex = 10;
            this.label6.Text = "S:";
            // 
            // label7
            // 
            this.label7.AutoSize = true;
            this.label7.Location = new System.Drawing.Point(456, 160);
            this.label7.Name = "label7";
            this.label7.Size = new System.Drawing.Size(17, 13);
            this.label7.TabIndex = 12;
            this.label7.Text = "V:";
            // 
            // btnOK
            // 
            this.btnOK.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.btnOK.Location = new System.Drawing.Point(411, 402);
            this.btnOK.Name = "btnOK";
            this.btnOK.Size = new System.Drawing.Size(75, 23);
            this.btnOK.TabIndex = 17;
            this.btnOK.Text = "OK";
            this.btnOK.UseVisualStyleBackColor = true;
            // 
            // btnCancel
            // 
            this.btnCancel.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btnCancel.Location = new System.Drawing.Point(411, 431);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 18;
            this.btnCancel.Text = "Cancel";
            this.btnCancel.UseVisualStyleBackColor = true;
            // 
            // label8
            // 
            this.label8.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left)));
            this.label8.AutoSize = true;
            this.label8.Location = new System.Drawing.Point(12, 436);
            this.label8.Name = "label8";
            this.label8.Size = new System.Drawing.Size(39, 13);
            this.label8.TabIndex = 15;
            this.label8.Text = "Name:";
            // 
            // txtName
            // 
            this.txtName.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Left) 
            | System.Windows.Forms.AnchorStyles.Right)));
            this.txtName.Location = new System.Drawing.Point(57, 433);
            this.txtName.Name = "txtName";
            this.txtName.Size = new System.Drawing.Size(345, 22);
            this.txtName.TabIndex = 16;
            // 
            // tbValue
            // 
            this.tbValue.Location = new System.Drawing.Point(442, 178);
            this.tbValue.Maximum = 359;
            this.tbValue.Name = "tbValue";
            this.tbValue.Orientation = System.Windows.Forms.Orientation.Vertical;
            this.tbValue.Size = new System.Drawing.Size(45, 194);
            this.tbValue.TabIndex = 13;
            this.tbValue.TickFrequency = 36;
            this.tbValue.TickStyle = System.Windows.Forms.TickStyle.Both;
            this.tbValue.Value = 359;
            this.tbValue.ValueChanged += new System.EventHandler(this.tbValue_ValueChanged);
            // 
            // CPicker
            // 
            this.AcceptButton = this.btnOK;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.btnCancel;
            this.ClientSize = new System.Drawing.Size(497, 465);
            this.Controls.Add(this.tbValue);
            this.Controls.Add(this.txtName);
            this.Controls.Add(this.label8);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.btnOK);
            this.Controls.Add(this.label7);
            this.Controls.Add(this.label6);
            this.Controls.Add(this.txtH);
            this.Controls.Add(this.label5);
            this.Controls.Add(this.txtB);
            this.Controls.Add(this.label4);
            this.Controls.Add(this.txtG);
            this.Controls.Add(this.label3);
            this.Controls.Add(this.txtR);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.txtA);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.pbPreview);
            this.Controls.Add(this.tbAlpha);
            this.Controls.Add(this.tbSaturation);
            this.Controls.Add(this.pbPickerBox);
            this.Font = new System.Drawing.Font("Segoe UI", 8.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Name = "CPicker";
            this.Text = "Color Picker";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.CPicker_FormClosing);
            this.Load += new System.EventHandler(this.CPicker_Load);
            ((System.ComponentModel.ISupportInitialize)(this.pbPickerBox)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.tbSaturation)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.tbAlpha)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.pbPreview)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtA)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtR)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtG)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtB)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.txtH)).EndInit();
            ((System.ComponentModel.ISupportInitialize)(this.tbValue)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.PictureBox pbPickerBox;
        private System.Windows.Forms.TrackBar tbSaturation;
        private System.Windows.Forms.TrackBar tbAlpha;
        private System.Windows.Forms.PictureBox pbPreview;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.NumericUpDown txtA;
        private System.Windows.Forms.NumericUpDown txtR;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.NumericUpDown txtG;
        private System.Windows.Forms.Label label3;
        private System.Windows.Forms.NumericUpDown txtB;
        private System.Windows.Forms.Label label4;
        private System.Windows.Forms.NumericUpDown txtH;
        private System.Windows.Forms.Label label5;
        private System.Windows.Forms.Label label6;
        private System.Windows.Forms.Label label7;
        private System.Windows.Forms.Button btnOK;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.Label label8;
        private System.Windows.Forms.TextBox txtName;
        private System.Windows.Forms.TrackBar tbValue;
    }
}