using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Threading;
using System.Net;
using System.IO;
//using System.Web.Script.Serialization;

namespace xTPManufacturerTest
{
    public class Reader
    {
    #region constants
        // These are the expected intervals between their respective messages
        const int HelloInterval = 60;
        const int DiagnosticsInterval = 60;
        const int TapInterval = 15;
        const int BioInterval = 15;
    #endregion

    #region Private data
        private int firstHelloTime;
        private DateTime _testStart;
        private DateTime _lastHello;
        private DateTime _lastDiagnostics;
        private DateTime _lastTap;
        private DateTime _lastBioRead;

        private string _readerLog;
        private string _testLog;

    #endregion

    #region Public properties
        public bool logTemperature = false;

        private uint _numTaps;
        private uint _numHellos;
        private uint _numBioReads;
        private uint _numDiagnosticEvents;
        private uint _numXbioDiagnosticEvents;
        private uint _numUnknownEvents;
        private int _numRFIDRestarts;
        private uint _numErrors;
        private string _mac;
        private string _ipAddress;
        private string _baseUrl;
        private bool _xbioDevice;
        private string _type;
        private string _version;
        private decimal _lastTemp;
        private decimal _maxTemp;

        public uint numTaps { get { return _numTaps;  } }
        public uint numHellos { get { return _numHellos; } }
        public uint numEvents { get { return _numTaps + _numBioReads + _numDiagnosticEvents + _numUnknownEvents; } }
        public uint numBioReads { get { return _numBioReads; } }
        public uint numDiagnosticEvents { get { return _numDiagnosticEvents; } }
        public uint numXbioDiagnosticEvents { get { return _numXbioDiagnosticEvents; } }
        public string Mac { get { return _mac; } }
        public string IPAddress { get { return _ipAddress; } }
        public uint numErrors { get { return _numErrors; } }
        public int numRFIDRestarts { get { return _numRFIDRestarts; } }
        public string Log { get { return _readerLog + _testLog; } }
        public bool xBioDevice { get { return _xbioDevice; } }
        public string type { get { return _type; } }
        public string version { get { return _version; } }
        public decimal lastTemp { get { return _lastTemp; } }
        public decimal maxTemp { get { return _maxTemp; } }
        public TimeSpan testDuration { get { return DateTime.Now - _testStart; } }
        public int connectedTime { get { return (getEpoch() - firstHelloTime); } }
        public string url { get { return _baseUrl; } }

        public bool okay
        {
            get
            {
                DateTime now = DateTime.Now;
                if (_numErrors > 0)
                    return false;
                else if (now.Subtract(_lastHello).TotalSeconds > (HelloInterval + 10))
                {
                    error("Missed hello message");
                    return false;
                }
                else if (Settings.EnableTestLoop && (now.Subtract(_lastTap).TotalSeconds > (TapInterval + 10)))
                {
                    error("Missed tap");
                    return false;
                }
                else if (Settings.EnableTestLoop && Settings.UseXbio && now.Subtract(_lastBioRead).TotalSeconds > (BioInterval + 10))
                {
                    // TODO -  this error can be incorrectly set when xbio is initially checked
                    error("Missed xbio read");
                    return false;
                }
                else if (now.Subtract(_lastDiagnostics).TotalSeconds > (DiagnosticsInterval + 10))
                {
                    error("Missed diagnostics event");
                    return false;
                }
                return true;
            }
        }

    #endregion

    #region public methods

        /*
            Constructor
        */
        public Reader(dynamic dict, string ipAddress)
        {
            firstHelloTime = getEpoch();
            _readerLog = "";
            clearTestStats(false);
            log(ref _readerLog, "Reader detected");

            _mac = dict["mac"];
            _ipAddress = ipAddress;
            _baseUrl = string.Format("http://{0}:8080", IPAddress);

            _type = dict["reader type"];
            _version = dict["reader version"];
            _xbioDevice = (_type == "xFP+xBIO");

            logMessage("Added new reader");
        }

        /*
         *  Should be called anytime a new reader is constructed.
         *  Any messages to the reader that we want to happen once when the reader is first
         *  discovered should be done here and not in the constructor to keep the constructor
         *  short.
        */
        public void init()
        {
            setIdleEffect("idle");
            setTapEffect("thinking");
        }

        public void clearTestStats(bool addLogMsg = true)
        {
            _lastHello = _lastTap = _lastBioRead = _lastDiagnostics = DateTime.Now;
            _testStart = DateTime.Now;
            _testLog = "";
            _numErrors = 0;
            _numHellos = 0;
            _numDiagnosticEvents = 0;
            _numXbioDiagnosticEvents = 0;
            _numUnknownEvents = 0;
            _numBioReads = 0;
            _numTaps = 0;

            if (addLogMsg)
                log(ref _readerLog, "test results cleared");
        }

        public void identify(bool enable)
        {
            playSequence(enable ? "identify" : "off", 0);
        }


        /*
            Returns true if we got a response from the reader, false otherwise
        */
        public bool upgrade(string manifest, out int statusCode, out string statusDescription, out string readerReply)
        {
            try
            {
                string url = _baseUrl + "/upgrade";

                HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(url);

                request.Method = "POST";
                request.ContentLength = manifest.Length;
                request.ContentType = "application/json";

                StreamWriter stream = new StreamWriter(request.GetRequestStream());
                stream.Write(manifest);
                stream.Close();

                HttpWebResponse response;
                try
                {
                    response = (HttpWebResponse)request.GetResponse();
                }
                catch (WebException ex)
                {
                    response = (HttpWebResponse)ex.Response;
                }

                statusCode = (int)response.StatusCode;
                statusDescription = response.StatusDescription;

                StreamReader input = new StreamReader(response.GetResponseStream());
                readerReply = input.ReadToEnd().Replace("\n", "\r\n");
                input.Close();
                return true;
            }
            catch (Exception ex)
            {
                string msg = "Exception during upgrade, " + ex.Message;
                error(msg);
                statusCode = 0;
                statusDescription = msg;
                readerReply = "";
                return false;
            }
        }

    #endregion

    #region Event and REST call handlers (called by server code)

        public void processHello(string hostIPAddress, int nextEno)
        {
            if (!setxBRCUrl(hostIPAddress))
                error("Unable to set xBRC URL");
            Thread.Sleep(10);

            if (!setRFIDTestMode(Settings.EnableTestLoop))
                    error("Unable to set RFID test mode");
//                Thread.Sleep(10);

            if (!setReaderName())
                error("Unable to set reader name");
//            Thread.Sleep(10);

            setUpdateStream(hostIPAddress, nextEno);

            setTime();

            logMessage("Received hello");
            ++_numHellos;
            _lastHello = DateTime.Now;
        }

        public void processTap(dynamic evt)
        {
            if (!evt.ContainsKey("uid") || evt["uid"] == null || evt["uid"] == string.Empty)
                error("Received tap event with no User ID");

            // TODO - put these back later
            //if (!evt.ContainsKey("pid") || evt["pid"] == null || evt["pid"] == string.Empty)
            //    setTapError("Received tap event with no Public ID");

            //if (!evt.ContainsKey("sid") || evt["sid"] == null || evt["sid"] == string.Empty)
            //    setTapError("Received tap event with no Secure ID");

            ++_numTaps;
            _lastTap = DateTime.Now;

            logMessage("Tap event");

            if (xBioDevice && Settings.UseXbio)
            {
                if (!startBiometricEnroll())
                    error("Unable to start xBio match");
            }
            else
            {
                // Play success sequence
                if (!playSequence("success", 1000))
                    error("Unable to play success sequence");
            }
        }

        public void processBioEnroll(dynamic evt)
        {
            ++_numBioReads;
            if (xBioDevice)
            {
                logMessage("xBio read event");

                Thread.Sleep(500);

                if (!evt.ContainsKey("xbio-template"))
                {
                    error("Blue lane exception");

                    if (!playSequence("exception", 6000))
                        error("unable to play exception sequence");
                }
                else
                {
                    if (!playSequence("success", 0))
                        error("unable to play success sequence");

                    _lastBioRead = DateTime.Now;
                }
            }
        }

        public void processDiagnosticEvent(dynamic evt)
        {
            logMessage("Diagnostic event");

            _lastTemp = evt["temp"];
            if (logTemperature)
                log(ref _testLog, string.Format("Temp. {0:F2}\n", _lastTemp));

            _maxTemp = evt["max temp"];
            _numRFIDRestarts = evt["RFID restarts"];

            _lastDiagnostics = DateTime.Now;
            ++_numDiagnosticEvents;

            // don't check the first few diagnostic messages as it can
            // contain errors messages related to the xBRC connection
            // that don't really apply after it first connects.
            if (connectedTime < 60 * 3)
                return;

            string status = evt["status"];
            if (status.Equals("Red") || status.Equals("Yellow"))
                error(status + ": " + evt["status msg"]);

            status = evt["RFID status"];
            if (status.Equals("Red") || status.Equals("Yellow"))
                error(status + ": " + evt["RFID msg"]);

            if (evt.ContainsKey("xbio status"))
            {
                status = evt["xbio status"];
                if (status.Equals("Red") || status.Equals("Yellow"))
                    error(status + ": " + evt["xbio msg"]);
            }
        }


        public void processXbioDiagnostics(dynamic evt)
        {
            ++_numXbioDiagnosticEvents;
        }

        public void processUnknownEvent(dynamic evt)
        {
            error("Unknown event type");
            ++_numUnknownEvents;
        }

        public void processBioScanError(dynamic evt)
        {
            error(string.Format("xBio scan error: {0}", evt["reason"]));

            // TODO - Put back?
            //if (!ReaderInterface.cancelBiometricRead(this))
            //    setBioError("Unable to stop xBio sequence");
        }

    #endregion

    #region Interface methods

        private bool setxBRCUrl(string hostIPAddress)
        {
            string url = string.Format("{0}/xbrc?url=http://{1}:8080", _baseUrl, hostIPAddress);
            return sendRequest(url);
        }

        private bool setRFIDTestMode(bool enable)
        {
            string url = string.Format("{0}/rfid/options?test_loop={1}", _baseUrl, enable ? "on" : "off");
            return sendRequest(url);
        }

        private bool setIdleEffect(string name)
        {
            string url = string.Format("{0}/media/idle?name={1}", _baseUrl, "idle");
            return sendRequest(url);
        }

        private bool setTapEffect(string name)
        {
            string url = string.Format("{0}/tap/options?sequence={1}", _baseUrl, "thinking");
            return sendRequest(url);
        }

        private bool setReaderName()
        {
            string url = string.Format("{0}/reader/name?name={1}", _baseUrl, Mac);
            return sendRequest(url);
        }

        private void setUpdateStream(string hostIPAddress, int nextEventNumber)
        {
            string url = string.Format("{0}/update_stream?url=http://{1}:8080/stream&after={2}", _baseUrl, hostIPAddress, nextEventNumber - 1);
            sendRequest(url);
        }

        private void setTime()
        {
            string url = string.Format("{0}/time?time={1}", _baseUrl, DateTime.Now.ToString("o"));
            sendRequest(url);
        }

        private bool startBiometricEnroll()
        {
            string url = string.Format("{0}/media/sequence?name=entry_start_scan", _baseUrl);
            if (!sendRequest(url))
                return false;

            url = string.Format("{0}/biometric/enroll", _baseUrl);
            return sendRequest(url);
        }

        private bool cancelBiometricRead()
        {
            string url = string.Format("{0}/biometric/cancel", _baseUrl);
            return sendRequest(url);
        }

        private bool playSequence(string name, uint timeout)
        {
            string url = string.Format("{0}/media/sequence?name={1}&timeout={2}", _baseUrl, name, timeout);
            return sendRequest(url);
        }

        private bool sendRequest(string url, string method = "PUT")
        {
            HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(url);
            request.Method = method;

            try
            {
                using (HttpWebResponse response = (HttpWebResponse)request.GetResponse())
                {
                    return (response.StatusCode == HttpStatusCode.OK);
                }
            }
            catch (Exception)
            {
                return false;
            }
        }

    #endregion

    #region other private methods

        private void log(ref string theLog, string msg)
        {
            theLog += string.Format("[{0}] ", DateTime.Now.ToLocalTime().ToString());
            if (msg != null)
                theLog += msg;
            theLog += "\r\n";

            logMessage(msg);
        }

        private void error(string msg)
        {
            log(ref _testLog, msg);
            _numErrors++;
        }

        private void logMessage(string msg)
        {
            string m = string.Format("{0} - {1}", Mac, msg);
            Trace.Write(m);
        }

        private static int getEpoch()
        {
            return (int)(DateTime.UtcNow - new DateTime(1970, 1, 1)).TotalSeconds;
        }
    #endregion
    }
}
