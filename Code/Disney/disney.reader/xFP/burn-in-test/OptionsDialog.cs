using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace xTPManufacturerTest
{
    public partial class OptionsDialog : Form
    {
        public OptionsDialog()
        {
            InitializeComponent();
        }

        private void OptionsDialog_Load(object sender, EventArgs e)
        {
            box_testLoop.Checked = Settings.EnableTestLoop;
            box_xbio.Checked = Settings.UseXbio;

            box_xbio.Enabled = box_testLoop.Checked;
        }

        private void button_cancel_Click(object sender, EventArgs e)
        {
            Close();
        }

        private void button_okay_Click(object sender, EventArgs e)
        {
            Settings.EnableTestLoop = box_testLoop.Checked;
            Settings.UseXbio = box_xbio.Checked;
            Close();
        }

        private void box_testLoop_CheckedChanged(object sender, EventArgs e)
        {
            box_xbio.Enabled = box_testLoop.Checked;
        }

    }
}
