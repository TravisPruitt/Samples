using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace TestConsole
{
    public partial class UserList : Form
    {
        private Sessions sessions;

        public UserList()
        {
            InitializeComponent();
        }

        public UserList(Sessions sessions) : this()
        {
            this.sessions = sessions;
        }

        private void UserList_Load(object sender, EventArgs e)
        {
            lock (sessions)
            {
                foreach (string s in sessions.sessions.Keys)
                {
                    DateTime dt = sessions.sessions[s].SessionStartTime;
                    string sLine = string.Format("{0} {1}", dt, s);
                    lbSessions.Items.Add(sLine);
                }
            }

        }
    }
}
