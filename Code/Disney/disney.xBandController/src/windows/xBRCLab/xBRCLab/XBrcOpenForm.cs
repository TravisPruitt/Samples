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
    public partial class XBrcOpenForm : Form
    {
        public XBrcOpenForm()
        {
            InitializeComponent();
        }

        public XBrcOpenForm(string sAddress)
            : this()
        {
            tbAddress.Text = sAddress;
        }

        public string getAddress()
        {
            return tbAddress.Text;
        }

        private void btnOK_Click(object sender, EventArgs e)
        {
            // validate
            string sAddress = getAddress();
            if (string.IsNullOrEmpty(sAddress.Trim()))
            {
                error.SetError(tbAddress, "Must be computer name or ip address");
                this.DialogResult = DialogResult.None;
                return;
            }

            // see if it's an ip address
            string[] aParts = getAddress().Split(new char[] { '.' });
            if (aParts.Length == 4)
            {
                bool bGood = true;
                for (int i=0; i<4; i++)
                {
                    int nValue;
                    if (!int.TryParse(aParts[i], out nValue))
                    {
                        bGood = false;
                        break;
                    }
                    if ((i==0 || i==3) && nValue<1)
                    {
                        bGood = false;
                        break;
                    }
                    if (nValue<0 || nValue>255)
                    {
                        bGood = false;
                        break;
                    }
                }

                if (!bGood)
                {
                    error.SetError(tbAddress, "Invalid IP address");
                    DialogResult = DialogResult.None;
                    return;
                }
            }

            DialogResult = DialogResult.OK;
            this.Close();
        }

        private void tbAddress_TextChanged(object sender, EventArgs e)
        {
            error.Clear();
        }

    }
}
