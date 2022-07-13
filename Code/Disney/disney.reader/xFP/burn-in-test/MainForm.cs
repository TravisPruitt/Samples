using System;
using System.Windows.Forms;
using System.IO;
using System.Reflection;
using System.Diagnostics;

namespace xTPManufacturerTest
{
    public partial class MainForm : Form
    {
        public MainForm()
        {
            InitializeComponent();
            Init();
        }

        protected override void OnFormClosing(FormClosingEventArgs e)
        {
            server.Stop();
            base.OnFormClosing(e);
        }

        public ReaderList readerList = new ReaderList();

        private Server server;

        private void Init()
        {
            Version ver = Assembly.GetExecutingAssembly().GetName().Version;
            this.Text = "Burn-in Test v. " + ver.ToString();
            dataGridViewReaderList.DataSource = readerList;
            RefreshReaderPane();

            server = new Server(this);
            server.Start();
        }

        private delegate void OnAddDataCallback(Reader newData);

        public void addData(Reader newReader)
        {
            if (this.InvokeRequired)
            {
                var cb = new OnAddDataCallback(addData);
                this.Invoke(cb, newReader);
                return;
            }
            readerList.Add(new ReaderView(newReader));
        }

        private void buttonAddReader_Click(object sender, System.EventArgs e)
        {
            try
            {
                string url = string.Format("http://{0}:8080/xbrc?url=http://{1}:8080", textBoxAddReaderIP.Text, server.IPAddress);
                if (!Http.sendRequest(url))
                {
                    MessageBox.Show("Unable to connect to reader", "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        public void RefreshList()
        {
            dataGridViewReaderList.Invalidate();
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            RefreshList();
            RefreshReaderPane();
        }

        private void dataGridViewReaderList_SelectionChanged(object sender, EventArgs e)
        {
            RefreshReaderPane();
        }


        private Reader getSelectedReader()
        {
            int index;
            if (dataGridViewReaderList.SelectedRows.Count < 1)
            {
                index = -1;
            }
            else
            {
                index = dataGridViewReaderList.SelectedRows[0].Index;
            }

            Reader reader = (index >= 0) ? readerList.getReader(index) : null;
            return reader;
        }

        private void clearButton_Click(object sender, EventArgs e)
        {
            Reader reader = getSelectedReader();

            if (reader != null)
                reader.clearTestStats();
        }

        private void RefreshReaderPane()
        {
            Reader reader = getSelectedReader();
            if (reader == null)
            {
                label_type.Text = "";
                label_temp.Text = "";
                label_maxTemp.Text = "";
                label_hellos.Text = "";
                label_events.Text = "";
                label_taps.Text = "";
                label_xbioEvents.Text = "";
                label_diagnosticEvents.Text = "";
                label_xbioDiagnosticEvents.Text = "";
                label_RFIDRestarts.Text = "";
                label_errors.Text = "";
                label_testDuration.Text = "";
                text_log.Text = "";
                identifyCheckBox.Enabled = false;
                upgradeButton.Enabled = false;
                reportButton.Enabled = false;
                clearButton.Enabled = false;
                removeButton.Enabled = false;
                button_media.Enabled = false;
                box_logTemperature.Checked = false;
                box_logTemperature.Enabled = false;
            }
            else
            {
                label_type.Text = reader.type;

                label_temp.Text = reader.lastTemp.ToString("F2");
                label_maxTemp.Text = reader.maxTemp.ToString("F2");
                label_hellos.Text = reader.numHellos.ToString();
                label_events.Text = reader.numEvents.ToString();
                label_taps.Text = reader.numTaps.ToString();
                label_xbioEvents.Text = reader.numBioReads.ToString();
                label_diagnosticEvents.Text = reader.numDiagnosticEvents.ToString();
                label_xbioDiagnosticEvents.Text = reader.numXbioDiagnosticEvents.ToString();
                label_RFIDRestarts.Text = reader.numRFIDRestarts.ToString();
                label_errors.Text = reader.numErrors.ToString();
                label_testDuration.Text = reader.testDuration.ToString(@"h\:mm\:ss");
                text_log.Text = reader.Log;
                identifyCheckBox.Enabled = true;
                upgradeButton.Enabled = true;
                reportButton.Enabled = true;
                clearButton.Enabled = true;
                removeButton.Enabled = true;
                button_media.Enabled = true;
                box_logTemperature.Enabled = true;
                box_logTemperature.Checked = reader.logTemperature;
            }
        }


        // Automatically scrolls to the end of the log whenever text is added.
        private void text_log_TextChanged(object sender, EventArgs e)
        {
            text_log.SelectionStart = text_log.Text.Length;
            text_log.ScrollToCaret();
        }

        private void reportButton_Click(object sender, EventArgs e)
        {
            Reader reader = getSelectedReader();
            if (reader == null)
                return;

            SaveFileDialog dialog = new SaveFileDialog();
            dialog.Filter = "txt files (*.txt)|*.txt|All files (*.*)|*.*";
            dialog.FilterIndex = 1;

            if (dialog.ShowDialog() == DialogResult.OK)
            {
                StreamWriter file = new StreamWriter(dialog.FileName, false);
                file.WriteLine(String.Format("Reader type: {0}", reader.type));
                file.WriteLine(String.Format("MAC: {0}", reader.Mac));
                file.WriteLine(String.Format("IP: {0}", reader.IPAddress));
                file.WriteLine(String.Format("Max temperature: {0:F2}", reader.maxTemp));
                file.WriteLine();
                file.WriteLine(String.Format(@"Test duration {0:h\:mm\:ss}", reader.testDuration));
                file.WriteLine(String.Format("Taps: {0}", reader.numTaps));
                file.WriteLine(String.Format("xbio reads: {0}", reader.numBioReads));
                file.WriteLine(String.Format("RFID restarts: {0}", reader.numRFIDRestarts));
                file.WriteLine(String.Format("Errors: {0}", reader.numErrors));
                file.WriteLine();

                file.WriteLine("Log");
                file.WriteLine("-------------------------------------------------------------");
                file.Write(text_log.Text);
                file.Close();
            }
        }

        private void upgradeButton_Click(object sender, EventArgs e)
        {
            Reader reader = getSelectedReader();
            if (reader == null)
                return;

            OpenFileDialog dialog = new OpenFileDialog();
            dialog.CheckFileExists = true;
            dialog.CheckPathExists = true;
            dialog.Multiselect = false;
            dialog.Filter = "manifest files (*.manifest)|*.manifest|All files (*.*)|*.*";

            if (dialog.ShowDialog() == DialogResult.OK)
            {
                UpgradeDialog upgradeDialog = new UpgradeDialog();

                upgradeDialog.ManifestPath = dialog.FileName;
                upgradeDialog.OurIPAddress = server.IPAddress;
                upgradeDialog.reader = reader;
                upgradeDialog.ShowDialog();
            }
        }

        private void removeButton_Click(object sender, EventArgs e)
        {
            if (dataGridViewReaderList.SelectedRows.Count >= 1)
            {
                readerList.RemoveAt(dataGridViewReaderList.SelectedRows[0].Index);
            }
        }

        private void identifyCheckBox_CheckedChanged(object sender, EventArgs e)
        {
            Reader reader = getSelectedReader();

            if (reader != null)
            {
                reader.identify(identifyCheckBox.Checked);
            }
        }

        private void optionsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OptionsDialog dialog = new OptionsDialog();
            dialog.ShowDialog();
        }

        private void button_media_Click(object sender, EventArgs e)
        {
            Reader reader = getSelectedReader();
            if (reader == null)
                return;

            OpenFileDialog dialog = new OpenFileDialog();
            dialog.CheckFileExists = true;
            dialog.CheckPathExists = true;
            dialog.Multiselect = false;
            dialog.Filter = "gzip files (*.gz)|*.gz|All files (*.*)|*.*";

            if (dialog.ShowDialog() == DialogResult.OK)
            {
                DialogResult result;
                do
                {
                    Cursor.Current = Cursors.WaitCursor;
                    int statusCode;
                    string reply;
                    bool success = Http.sendFile(reader.url + "/media/package", dialog.FileName, out statusCode, out reply);
                    Cursor.Current = Cursors.Default;

                    if (success)
                    {
                        result = MessageBox.Show("Media package downloaded successfully", "Success", MessageBoxButtons.OK);
                    }
                    else
                    {
                        Cursor.Current = Cursors.Default;
                        // TODO - show response and implement retry
                        result = MessageBox.Show("Media package download failed, response:\r\n\n" + reply, "Error " + statusCode, MessageBoxButtons.RetryCancel);
                    }
                } while (result == DialogResult.Retry);
            }
        }

        private void box_logTemperature_CheckedChanged(object sender, EventArgs e)
        {
            Reader reader = getSelectedReader();
            if (reader == null)
                return;

            reader.logTemperature = box_logTemperature.Checked;

        }
    }
}
