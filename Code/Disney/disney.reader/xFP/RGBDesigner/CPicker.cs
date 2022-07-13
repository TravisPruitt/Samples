using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Drawing.Drawing2D;

namespace RGBDesigner
{
    public partial class CPicker : Form
    {
        Bitmap[] pickerBoxImages = new Bitmap[360];
        bool allowAlpha = true;
        bool userChanged = true;
        bool setInitialImage = true;
        bool mouseDown = false;
        Point coords = new Point(0, 0);
        Color color;

        public Color Color
        {
            get
            {
                return color;
            }
            set
            {
                setInitialImage = false;
                UpdateUI(value);
            }
        }

        public String ColorName { get { return txtName.Text; } set { txtName.Text = value; } }

        public bool AllowAlpha
        {
            get { return allowAlpha; }
            set
            {
                allowAlpha = value;
                txtA.Enabled = allowAlpha;
                tbAlpha.Enabled = allowAlpha;
                if (!allowAlpha)
                    txtA.Value = tbAlpha.Value = 255;
            }
        }

        public CPicker()
        {
            InitializeComponent();
            PregenPickerBoxImages();
        }

        private void CPicker_Load(object sender, EventArgs e)
        {
            GeneratePreview();
            if (setInitialImage)
                pbPickerBox.Image = GeneratePickerBox(1d);
        }

        private void CPicker_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (DialogResult == System.Windows.Forms.DialogResult.OK)
            {
                if (txtName.Text.Trim() == string.Empty)
                {
                    MessageBox.Show("Type in a name for this color.", "Enter Name", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
                    e.Cancel = true;
                }
            }
        }

        /// <summary>
        /// Pre-generates the picker box images.
        /// </summary>
        private void PregenPickerBoxImages()
        {
            BackgroundWorker bw = new BackgroundWorker();
            bw.WorkerReportsProgress = true;
            bw.DoWork += new DoWorkEventHandler((sender, e) =>
            {
                for (int i = pickerBoxImages.Length - 1; i >= 0; i--)
                {
                    double sat = (double)(1f / (pickerBoxImages.Length - 1)) * i;
                    bw.ReportProgress(0, new object[] { i, GeneratePickerBox(sat) });
                }
            });
            bw.ProgressChanged += new ProgressChangedEventHandler((sender, e) =>
            {
                int i = (int)((object[])e.UserState)[0];
                Bitmap bmp = (Bitmap)((object[])e.UserState)[1];
                pickerBoxImages[i] = bmp;
            });
            bw.RunWorkerAsync();
        }

        /// <summary>
        /// Generates an image from which the user can select a color.
        /// </summary>
        /// <param name="s">Saturation level (from HSV space) for which to generate the image.</param>
        /// <returns></returns>
        private Bitmap GeneratePickerBox(double s)
        {
            Bitmap bmp = new Bitmap(pbPickerBox.Width, pbPickerBox.Height);
            Graphics gr = Graphics.FromImage(bmp);

            for (int y = 0; y < bmp.Height; y++)
            {
                float v = 1f - ((1f / bmp.Height) * (float)y);
                Point p1 = new Point(0, y);
                Point p2 = new Point(bmp.Width - 1, y);
                ColorBlend cb = new ColorBlend(7);
                for (int i = 0, h = 0; i < 7; i++, h += 60)
                {
                    cb.Colors[i] = HSVToRGB(h, s, v);
                    cb.Positions[i] = h / 360f;
                }
                LinearGradientBrush brush = new LinearGradientBrush(p1, p2, cb.Colors[0], cb.Colors[6]);
                brush.InterpolationColors = cb;
                gr.DrawLine(new Pen(brush), p1, p2);
            }
            return bmp;
        }

        private void SwitchPickerBoxImage()
        {
            Bitmap bmp = pickerBoxImages[tbSaturation.Value];
            if (bmp == null)
                bmp = GeneratePickerBox((double)tbSaturation.Value / tbSaturation.Maximum);
            pbPickerBox.Image = bmp;
        }

        /// <summary>
        /// Converts a color from HSV space to RGB space.
        /// </summary>
        /// <param name="h">Hue from HSV space, in the range [0,359] degrees.</param>
        /// <param name="s">Saturation from HSV space, in the range [0,1].</param>
        /// <param name="v">Value from HSV space, in the range [0,1].</param>
        /// <returns></returns>
        public static Color HSVToRGB(double h, double s, double v)
        {
            if (h >= 360) h %= 360; // allow values larger than 359 since degrees are circular.
            if (h < 0) throw new Exception("Hue must be in the range 0 to 359.");
            if (s < 0 || s > 1) throw new Exception("Saturation must be in the range 0.0 to 1.0.");
            if (v < 0 || v > 1) throw new Exception("Value must be in the range 0.0 to 1.0.");

            Func<double, double, double, Color> ScaleRGB = (r, g, b) =>
            {
                return Color.FromArgb((byte)Math.Round(255 * r), (byte)Math.Round(255 * g), (byte)Math.Round(255 * b));
            };

            if (s == 0)
                return ScaleRGB(v, v, v);

            int hi = (int)(h / 60);
            double f = h / 60 - hi;
            double p = v * (1 - s);
            double q = v * (1 - f * s);
            double t = v * (1 - (1 - f) * s);

            switch (hi)
            {
                case 0: return ScaleRGB(v, t, p);
                case 1: return ScaleRGB(q, v, p);
                case 2: return ScaleRGB(p, v, t);
                case 3: return ScaleRGB(p, q, v);
                case 4: return ScaleRGB(t, p, v);
                case 5: return ScaleRGB(v, p, q);
            }
            return Color.White; // impossible!
        }

        /// <summary>
        /// Converts a color from RGB space to HSV space.
        /// </summary>
        /// <param name="c">The RGB color to be converted.</param>
        /// <param name="h">Hue</param>
        /// <param name="s">Saturation</param>
        /// <param name="v">Value</param>
        public static void RGBtoHSV(Color c, out double h, out double s, out double v)
        {
            double r = c.R / 255d;
            double g = c.G / 255d;
            double b = c.B / 255d;
            double min = Math.Min(r, Math.Min(g, b));
            double max = v = Math.Max(r, Math.Max(g, b));
            double delta = max - min;

            if (delta != 0)
            {
                s = (delta / max);
                if (max == r)
                    h = ((g - b) / delta);		// between yellow & magenta
                else if (max == g)
                    h = (2 + (b - r) / delta);	// between cyan & yellow
                else
                    h = (4 + (r - g) / delta);	// between magenta & cyan
                h *= 60;
                if (h < 0)
                    h += 360;
            }
            else
                s = h = 0;
        }

        private void tbSaturation_ValueChanged(object sender, EventArgs e)
        {
            if (!userChanged) return;
            GeneratePreview();
        }

        private void tbValue_ValueChanged(object sender, EventArgs e)
        {
            if (!userChanged) return;
            coords = new Point(coords.X, (int)((1f - ((float)tbValue.Value / (float)tbValue.Maximum)) * (pbPickerBox.Height - 1)));
            GeneratePreview();
        }

        private void pbPickerBox_MouseUp(object sender, MouseEventArgs e)
        {
            mouseDown = false;
        }

        /// <summary>
        /// Generates the color preview image.
        /// </summary>
        /// <param name="useEnteredValues">Whether or not to create the color from values entered 
        /// or to derive them from the currently selected coordinates within the color picker box.</param>
        private void GeneratePreview(bool useEnteredValues = false)
        {
            SwitchPickerBoxImage();

            Color c = Color.FromArgb((int)txtA.Value, (int)txtR.Value, (int)txtG.Value, (int)txtB.Value);
            Double h, s, v;

            if (!useEnteredValues)
            {
                int y = coords.Y;
                h = (360d / pbPickerBox.Width) * coords.X;
                s = (double)tbSaturation.Value / tbSaturation.Maximum;
                v = 1d - ((double)coords.Y / tbValue.Maximum);
                c = HSVToRGB(h, s, v);
                c = Color.FromArgb((byte)tbAlpha.Value, c.R, c.G, c.B);
            }
            else
            {
                RGBtoHSV(c, out h, out s, out v);
            }

            Bitmap bmp = new Bitmap(pbPreview.Width, pbPreview.Height);
            Graphics gr = Graphics.FromImage(bmp);

            // make the preview easier to "see" the transparency by drawing rectangles under it, like Photoshop.
            if (c.A < 255)
            {
                int count = 0;
                for (int y1 = 0; y1 < bmp.Height + 8; y1 += 8)
                {
                    for (int x1 = 0; x1 < bmp.Width + 8; x1 += 8)
                    {
                        Color c1 = count++ % 2 == 0 ? Color.LightGray : Color.White;
                        gr.FillRectangle(new SolidBrush(c1), x1, y1, 8, 8);
                    }
                }
            }

            gr.FillRectangle(new SolidBrush(c), 0, 0, bmp.Width, bmp.Height);
            pbPreview.Image = bmp;

            userChanged = false;
            txtA.Value = c.A;
            txtR.Value = c.R;
            txtG.Value = c.G;
            txtB.Value = c.B;
            txtH.Value = (int)h;
            tbSaturation.Value = (int)(tbSaturation.Maximum * s);
            tbValue.Value = (int)(tbValue.Maximum * v);
            this.color = c;
            userChanged = true;
        }

        private void tbAlpha_ValueChanged(object sender, EventArgs e)
        {
            GeneratePreview();
        }

        private void UpdateUI(Color c)
        {
            userChanged = false;

            double hue, sat, val;
            RGBtoHSV(c, out hue, out sat, out val);
            txtH.Value = (int)Math.Round(hue);
            tbSaturation.Value = (int)Math.Round((sat * tbSaturation.Maximum));
            tbValue.Value = (int)Math.Round(tbValue.Maximum * val);
            tbAlpha.Value = c.A;
            txtR.Value = c.R;
            txtG.Value = c.G;
            txtB.Value = c.B;

            coords = new Point((int)(hue / (359f / (pbPickerBox.Width - 1))), (int)((1f - val) * (pbPickerBox.Height - 1)));
            userChanged = true;
            GeneratePreview();
        }

        private void UpdateUI(bool userEnteredFromARGB = false)
        {
            if (!userChanged) return;
            userChanged = false;

            double hue, sat, val;
            if (userEnteredFromARGB)
            {
                Color c = Color.FromArgb((int)txtA.Value, (int)txtR.Value, (int)txtG.Value, (int)txtB.Value);
                RGBtoHSV(c, out hue, out sat, out val);
                txtH.Value = (int)Math.Round(hue);
                tbSaturation.Value = (int)Math.Round((sat * tbSaturation.Maximum));
                tbValue.Value = (int)Math.Round(tbValue.Maximum * val);
                tbAlpha.Value = c.A;
            }
            else
            {
                hue = (double)txtH.Value;
                sat = (double)tbSaturation.Value / tbSaturation.Maximum;
                val = (double)tbValue.Value / tbValue.Maximum;
                Color c = HSVToRGB(hue, sat, val);
                c = Color.FromArgb((int)txtA.Value, c.R, c.G, c.B);
                txtR.Value = c.R;
                txtG.Value = c.G;
                txtB.Value = c.B;
            }

            coords = new Point((int)(hue / (359f / (pbPickerBox.Width - 1))), (int)((1f - val) * (pbPickerBox.Height - 1)));
            userChanged = true;
            GeneratePreview(userEnteredFromARGB);
        }

        private void valueChanged(object sender, EventArgs e)
        {
            UpdateUI(sender.Equals(txtA) || sender.Equals(txtR) || sender.Equals(txtG) || sender.Equals(txtB));
        }

        private void pbPickerBox_Paint(object sender, PaintEventArgs e)
        {
            Color c = Color.FromArgb(255, color.R ^ 0xff, color.G ^ 0xff, color.B ^ 0xff);
            DrawCircle(e.Graphics, 5f, coords, c);
        }

        public static void DrawCircle(Graphics gr, float radius, Point location, Color color)
        {
            Rectangle rect = new Rectangle((int)(location.X - radius), (int)(location.Y - radius), (int)(radius * 2), (int)(radius * 2));
            gr.DrawEllipse(new Pen(new SolidBrush(color)), rect);
        }

        private void txt_Enter(object sender, EventArgs e)
        {
            var o = (NumericUpDown)sender;
            o.Select(0, o.Value.ToString().Length);
        }

        private void pbPickerBox_MouseDown(object sender, MouseEventArgs e)
        {
            mouseDown = true;
        }

        private void pbPickerBox_MouseMove(object sender, MouseEventArgs e)
        {
            if (mouseDown)
            {
                coords = new Point((int)Math.Min(pbPickerBox.Width - 1, Math.Max(0, e.X)),
                    (int)Math.Min(pbPickerBox.Height - 1, Math.Max(0, e.Y)));
                GeneratePreview();
            }
        }
    }

    /// <summary>
    /// Privides a class which doesn't unload and instantiated CPicker class.
    /// Useful to keep from having to wait for picker box images to generate.
    /// </summary>
    public static class ColorPicker
    {
        private static CPicker cpicker = new CPicker();

        public static bool AllowAlpha { get { return cpicker.AllowAlpha; } set { cpicker.AllowAlpha = value; } }
        public static Color Color { get { return cpicker.Color; } set { cpicker.Color = value; } }
        public static string ColorName { get { return cpicker.ColorName; } set { cpicker.ColorName = value; } }

        public static DialogResult ShowDialog()
        {
            return cpicker.ShowDialog();
        }
    }
}
