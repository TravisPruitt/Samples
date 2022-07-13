using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Threading;

namespace xTPManufacturerTest
{
    public partial class UpgradeDialog : Form
    {
        private string _manifestPath;
        private string _repoPath;
        private string _statusText;
        private string _replyText;
        private Thread _thread;
        private bool _stopThread;
        private FileServer _server;
        private bool _finished;

        public UpgradeDialog()
        {
            _stopThread = false;
            InitializeComponent();
        }

        private void UpgradeDialog_Load(object sender, EventArgs e)
        {
            _finished = false;
            _statusText = "Upgrading...";
            exitButton.Enabled = false;
            this.Cursor = Cursors.WaitCursor;

            adjustForSize();

            _server = new FileServer(_repoPath);
            _server.Start();

            _thread = new Thread(upgrade);
            _thread.Start();

            timer1.Interval = 100;
            timer1.Start();
        }

        public string ManifestPath
        {
            get { return _manifestPath; }
            set
            {
                _manifestPath = value;
                _repoPath = Path.GetDirectoryName(value);

                int index = _repoPath.LastIndexOf(Path.DirectorySeparatorChar);
                if (index >= 0)
                {
                    _repoPath = _repoPath.Substring(0, index + 1);
                }
                _repoPath += "repositories";
            }
        }

        public string OurIPAddress
        {
            get;
            set;
        }

        public Reader reader
        {
            get;
            set;
        }

        private void exitButton_Click(object sender, EventArgs e)
        {
            if (_thread != null)
            {
                // TODO - there is currently no clean way to abort upgrade.  Would need to make the
                // HTTP request call asynchronously and add a way to terminate the connection?
                _stopThread = true;
                _thread = null;
            }

            if (_server != null)
            {
                _server.Stop();
                _server = null;
            }

            this.Close();
        }

        private string createManifest()
        {
            StreamReader manifestFile;
            try
            {
                manifestFile = new StreamReader(File.OpenRead(ManifestPath));
            }
            catch (FileNotFoundException)
            {
                _statusText = String.Format("manifest file {0} not found", ManifestPath);
                return "";
            }
            catch (IOException)
            {
                _statusText = String.Format("Unable to open manifest file {0}", ManifestPath);
                return "";
            }

            string manifest = manifestFile.ReadToEnd();
            manifestFile.Close();

            if (manifest.Length <= 0)
            {
                _statusText = "Empty manifest file";
                return "";
            }

            string path = Path.GetDirectoryName(ManifestPath);

            int index = path.LastIndexOf(Path.DirectorySeparatorChar);
            if (index >= 0)
            {
                path = path.Substring(0, index + 1);
            }
            //path += "repositories";
            string url = "http://" + OurIPAddress + ":8000";
            //            url += "/";
            //            url = url.Replace('\\', '/');

            return manifest.Replace("repositories", url);
        }

        private void upgrade()
        {
            string manifest = createManifest();
            if (manifest.Length <= 0)
                return;

            int statusCode;
            string statusDescription;
            string readerReply;
            if (!reader.upgrade(manifest, out statusCode, out statusDescription, out readerReply))
            {
                _statusText = string.Format("Error: {0}\n\n", statusDescription);
                _replyText = readerReply;
                _finished = true;
                return;
            }
            else if (statusCode != 200)
            {
                _statusText = string.Format("Response {0} ({1})\n\n", statusCode, statusDescription);
                _replyText = readerReply;
                _finished = true;
                return;
            }
            else
            {
                _statusText = "rebooting...";
                _replyText = readerReply;
            }


            uint hellos = reader.numHellos;
            while ( (hellos == reader.numHellos) && !_stopThread)
                Thread.Sleep(100);

            _statusText = "Upgrade complete";
            _finished = true;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            label_status.Text = _statusText;
            textBox.Text = _replyText;
            if (_finished)
            {
                this.Cursor = Cursors.Default;
                exitButton.Enabled = true;
                timer1.Stop();
            }
        }

        private void UpgradeDialog_Resize(object sender, EventArgs e)
        {
            adjustForSize();
        }

        private void adjustForSize()
        {
            textBox.Width = this.Width - 40;
            textBox.Left = 10;
            exitButton.Left = this.Width - exitButton.Width - 30;

            exitButton.Top = this.Height - exitButton.Height - 50;
            textBox.Height = exitButton.Top - textBox.Top - 10;
        }
    }
}
