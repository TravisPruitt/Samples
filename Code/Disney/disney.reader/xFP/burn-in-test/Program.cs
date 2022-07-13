using System;
using System.Diagnostics;
using System.IO;
using System.Windows.Forms;

namespace xTPManufacturerTest
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        [STAThread]
        static void Main()
        {
            // Create a file for log output
            Stream logFile = File.Open("test.log", FileMode.Append, FileAccess.Write);

            LogListener logListener = new LogListener(logFile);
            Trace.Listeners.Add(logListener);

            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new MainForm());

            logListener.Flush();
        }
    }

    class LogListener : TextWriterTraceListener
    {
        public LogListener(Stream s) : base(s)
        {
        }

        public override void Write(string message)
        {
            message = string.Format("[{0}] {1}\r\n", DateTime.Now.ToLocalTime().ToString(), message);
            base.Write(message);
        }
    }
}
