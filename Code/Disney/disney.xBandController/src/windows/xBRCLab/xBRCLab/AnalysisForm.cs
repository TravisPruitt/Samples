using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace com.disney.xband.xbrc.xBRCLab
{
    public partial class AnalysisForm : Form
    {
        public AnalysisForm()
        {
            InitializeComponent();
        }

        public AnalysisForm(string sTitle) : this()
        {
            Text = sTitle;
        }
    }
}
