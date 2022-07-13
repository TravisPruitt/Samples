using System;
using System.Runtime.Serialization.Json;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;

namespace TestConsole
{
    class WebServer
    {
        // To enable this so that it can be run in a non-administrator account:
        // Open an Administrator command prompt.
        //  netsh http add urlacl http://+:8008/ user=Everyone listen=true
        private const string Prefix = "http://+:8008/";

        private HttpListener listener;
        private Sessions sessions;
        private PeriodMetrics cumMetrics;
        private PeriodMetrics periodMetrics;
        private MetricsHistory history;
        private StreamWriter swLog = null;
        private string swLogFilename = null;

        private Queue<WorkItem> qTasksForMainThread = new Queue<WorkItem>();

        public void Start(Sessions sessions, MetricsHistory history)
        {
            this.sessions = sessions;
            this.history = history;

            if (!HttpListener.IsSupported)
                throw new ApplicationException("HttpListener is not supported on this platform.");

            listener = new HttpListener();
            listener.Prefixes.Add(Prefix);
            listener.Start();

            // Begin waiting for requests.
            listener.BeginGetContext(GetContextCallback, null);

        }

        public void Stop()
        {
            if (listener != null)
            {
                listener.Stop();
            }

        }

        public void SetMetrics(PeriodMetrics cumMetrics, PeriodMetrics periodMetrics)
        {
            this.cumMetrics = cumMetrics;
            this.periodMetrics = periodMetrics;
        }

        public WorkItem[] GetWorkItems()
        {
            lock (qTasksForMainThread)
            {
                WorkItem[] aTasks = qTasksForMainThread.ToArray();
                qTasksForMainThread.Clear();
                return aTasks;
            }
        }

        private void GetContextCallback(IAsyncResult ar)
        {
            // Get the context
            HttpListenerContext context = listener.EndGetContext(ar);

            // listen for the next request
            listener.BeginGetContext(GetContextCallback, null);

            // handle the request
            HandleRequest(context);
        }

        private void HandleRequest(HttpListenerContext context)
        {
            // split out the path and args
            string sPath = context.Request.Url.PathAndQuery;

            // mete out
            if (sPath.StartsWith("/log/"))
                HandleLogRequest(context);
            else if (sPath.StartsWith("/status"))
                HandleStatusRequest(context);
            else if (sPath == "/data")
                HandleDataRequest(context);
            else if (sPath == "/log")
                HandleSessionsLogRequest(context);
            else if (sPath == "/sessions")
                HandleSessionsRequest(context);
            else if (sPath == "/clear")
                HandleClearRequest(context);
            else if (sPath.StartsWith("/configure"))
                HandleConfigureRequest(context);
            else
                HandleOtherRequest(context);
        }

        private void HandleConfigureRequest(HttpListenerContext context)
        {
            string sTitle = context.Request.QueryString.Get("title");
            string sSplits = context.Request.QueryString.Get("splits");
            string[] asSplits=null;
            if (sSplits!=null)
                asSplits = sSplits.Split(new char[] { ',' });
            WorkItem wi = new WorkItem();
            wi.Task = WorkItem.TaskEnum.configure;
            wi.Args = sTitle;
            wi.Args2 = asSplits;
            QueueWorkItem(wi);
            ReturnSuccess(context, "OK");
        }

        private void HandleClearRequest(HttpListenerContext context)
        {
            WorkItem wi = new WorkItem();
            wi.Task = WorkItem.TaskEnum.clear;
            QueueWorkItem(wi); 
            ReturnSuccess(context, "OK");
        }

        private void QueueWorkItem(WorkItem wi)
        {
            lock (qTasksForMainThread)
            {
                qTasksForMainThread.Enqueue(wi);
            }
        }

        private void HandleSessionsRequest(HttpListenerContext context)
        {
            // read and return session data
            MemoryStream ms = new MemoryStream();
            StreamWriter sw = new StreamWriter(ms);
            lock (sessions)
            {
                sw.WriteLine("<pre>");
                foreach (string s in sessions.sessions.Keys)
                {
                    string sLine = string.Format("{0} {1}", sessions.sessions[s].SessionStartTime, s);
                    sw.WriteLine(sLine);
                }
                sw.WriteLine("</pre>");
            }

            // and send it
            sw.Flush();
            ms.Flush();

            try
            {
                HttpListenerResponse response = context.Response;
                response.ContentType = "text/html";
                response.ContentLength64 = ms.Length;
                response.StatusCode = 200;
                ms.Seek(0, SeekOrigin.Begin);
                ms.CopyTo(response.OutputStream);
                response.OutputStream.Close();
                ms.Close();
            }
            catch (Exception ex)
            {
                ReturnError(context, 500, ex.Message);
            }
        }


        private void HandleSessionsLogRequest(HttpListenerContext context)
        {
            try
            {
                if (swLog==null)
                {
                    ReturnError(context, 404, "No log file");
                    return;
                }

                MemoryStream ms = new MemoryStream();
                StreamWriter sw = new StreamWriter(ms);
                sw.WriteLine("<pre>");

                // read and return sessions log
                FileStream fs = new FileStream(swLogFilename, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);

                // and send it
                HttpListenerResponse response = context.Response;
                response.ContentType = "text/html";
                response.ContentLength64 = fs.Length;
                response.StatusCode = 200;
                ms.Seek(0, SeekOrigin.Begin);
                ms.CopyTo(response.OutputStream);
                fs.CopyTo(response.OutputStream);
                ms.Seek(0, SeekOrigin.Begin);
                sw.WriteLine("/<pre>");
                ms.Seek(0, SeekOrigin.Begin);
                ms.CopyTo(response.OutputStream);
                response.OutputStream.Close();
                fs.Close();
                ms.Close();
            }
            catch (Exception ex)
            {
                ReturnError(context, 500, ex.Message);
            }


        }

        private void HandleOtherRequest(HttpListenerContext context)
        {
            try
            {
                // split out the path and args
                string sPath = context.Request.Url.PathAndQuery;

                if (sPath != ("/" + Parameters.Instance.CaptureFile))
                {
                    ReturnError(context, 404, sPath + " not found");
                    return;
                }

                // read and return screen.png
                FileStream fs = new FileStream(Parameters.Instance.CaptureFile, FileMode.Open);

                // and send it
                HttpListenerResponse response = context.Response;
                response.ContentType = "image/png";
                response.ContentLength64 = fs.Length;
                response.StatusCode = 200;
                fs.CopyTo(response.OutputStream);
                response.OutputStream.Close();
                fs.Close();
            }
            catch (Exception ex)
            {
                ReturnError(context, 500, ex.Message);
            }

        }

        private void HandleDataRequest(HttpListenerContext context)
        {

            try
            {
                MemoryStream ms = new MemoryStream();
                DataContractJsonSerializer ser = new DataContractJsonSerializer(typeof(MetricsHistory));
                ser.WriteObject(ms, history);
                byte[] buffer = Encoding.UTF8.GetBytes(Environment.NewLine);
                ms.Write(buffer, 0, buffer.Length);
                ms.Flush();

                HttpListenerResponse response = context.Response;
                response.ContentType = "application/json";
                response.ContentLength64 = ms.Length;
                response.StatusCode = 200;
                ms.WriteTo(response.OutputStream);
                response.OutputStream.Close();
                ms.Close();
            }
            catch (Exception ex)
            {
                ReturnError(context, 500, ex.Message);
                return;
            }

        }

        private void HandleLogRequest(HttpListenerContext context)
        {
            // parse out the session name and verb
            string sPath = context.Request.Url.PathAndQuery.Substring("/log/".Length);
            int iPos = sPath.IndexOf('/');
            if (iPos < 0)
                ReturnError(context, 500, "Missing session name");

            string sName = sPath.Substring(0, iPos);
            sPath = sPath.Substring(iPos + 1);

            if (sPath == "start")
            {
                try
                {
                    cumMetrics.TimeOfLastSessionStart = periodMetrics.TimeOfLastSessionStart = DateTime.Now;
                    LogSession(sName, sPath);

                    // if session doesn't exist, add it
                    TestSession ts = sessions.FindSession(sName);
                    if (ts == null)
                    {
                        // create a new session
                        sessions.AddSession(sName);
                    }
                    else
                    {
                        ts.SessionStartTime = DateTime.Now;
                    }
                }
                catch (Exception ex)
                {
                    ReturnError(context, 500, ex.Message);
                    return;
                }
            }
            else if (sPath.StartsWith("end?") || sPath.StartsWith("error?"))
            {
                if (sPath.StartsWith("error?"))
                {
                    periodMetrics.Errors = periodMetrics.Errors + 1;
                    cumMetrics.Errors = cumMetrics.Errors + 1;
                }

                // process the parameters
                System.Collections.Specialized.NameValueCollection qs = context.Request.QueryString;
                if (qs.Count > Parameters.Instance.CountSplits)
                {
                    ReturnError(context, 500, "Expected no more than" + Parameters.Instance.CountSplits.ToString() + " split values");
                    return;
                }
                float[] splits = new float[Parameters.Instance.CountSplits];
                for (int i = 0; i < Parameters.Instance.CountSplits; i++)
                {
                    try
                    {
                        string sValue = qs.Get("split" + i);
                        if (sValue != null)
                            splits[i] = float.Parse(sValue);
                        else
                            splits[i] = float.NaN;
                    }
                    catch (Exception ex)
                    {
                        ReturnError(context, 500, ex.Message);
                        return;
                    }
                }
                periodMetrics.ProcessSplits(splits);
                cumMetrics.ProcessSplits(splits);
                periodMetrics.ProcessedSessions = periodMetrics.ProcessedSessions + 1;
                cumMetrics.ProcessedSessions = cumMetrics.ProcessedSessions + 1;
                periodMetrics.TimeOfLastSessionEnd = cumMetrics.TimeOfLastSessionEnd = DateTime.Now;

                // this isn't an actual "set". It only sets if greater than the current max
                periodMetrics.MaxSessions = sessions.Count;
                cumMetrics.MaxSessions = sessions.Count;

                // remove session (ignore error if sessions doesn't exist)
                try
                {
                    LogSession(sName, sPath);
                    sessions.RemoveSession(sName);
                }
                catch (Exception)
                {
                }

            }
            else
            {
                LogSession(sName, sPath);
            }
            ReturnSuccess(context, "OK");
        }

        public void CloseLogFile()
        {
            if (swLog != null)
            {
                swLog.Close();
                swLog = null;
            }
        }

        private void LogSession(string sName, string sPath)
        {
            lock (sessions)
            {
                if (swLog == null)
                {
                    // compose log name
                    swLogFilename = string.Format(Parameters.Instance.SessionLogFile, DateTime.Now.ToString("yyyyMMdd-HHmm"));
                    FileStream fs = new FileStream(swLogFilename, FileMode.Create, FileAccess.Write, FileShare.Read);
                    swLog = new StreamWriter(fs);
                }

                // fetch the start time
                string sStartTime = "?";
                TestSession ts = sessions.FindSession(sName);
                if (ts != null)
                    sStartTime = ts.SessionStartTime.ToString();

                swLog.WriteLine(string.Format("\"{0}\",\"{1}\",\"{2}\",\"{3}\"", DateTime.Now, sStartTime, sName, sPath));
                swLog.Flush();
            }
        }

        private void HandleStatusRequest(HttpListenerContext context)
        {
            string sTemplate = Parameters.Instance.StatusPage;
            bool bHavePeriodData = periodMetrics.ProcessedSessions > 0;
            bool bHaveCumData = cumMetrics.ProcessedSessions > 0;

            // generate detail sections
            string sPeriodDetails = "";
            if (bHavePeriodData)
            {
                sPeriodDetails =
@"
<table>
    <tbody>
";
                    sPeriodDetails += "<tr><th>Metric</th><th>Mean</th><th>Max</th><th>Min</th></tr>\n";
                    for (int i = 0; i < periodMetrics.metrics.Length; i++)
                    {
                        string sRow = "";
                        if (periodMetrics.metrics[i].HaveValue())
                        {
                            sRow = string.Format("<tr><td>{0}</td><td>{1:F2}</td><td>{2:F2}</td><td>{3:F2}</td></tr>\n",
                                                    Parameters.Instance.SplitNames[i],
                                                    periodMetrics.metrics[i].Average,
                                                    periodMetrics.metrics[i].Max,
                                                    periodMetrics.metrics[i].Min);
                        }
                        else
                        {
                            sRow = string.Format("<tr><td>{0}</td><td>N/A</td><td>N/A</td><td>N/A</td></tr>\n",
                                                    Parameters.Instance.SplitNames[i]);
                        }
                        sPeriodDetails += sRow;
                    }

                    sPeriodDetails +=
@"    </tbody>
</table>
<br/>
"; 
            }

            string sCumDetails = "";
            if (bHaveCumData)
            {
                    sCumDetails =
@"
<table>
    <tbody>
";
                    sCumDetails += "<tr><th>Metric</th><th>Mean</th><th>Max</th><th>Min</th></tr>\n";
                    for (int i = 0; i < cumMetrics.metrics.Length; i++)
                    {
                        string sRow = "";
                        if (cumMetrics.metrics[i].HaveValue())
                        {
                            sRow = string.Format("<tr><td>{0}</td><td>{1:F2}</td><td>{2:F2}</td><td>{3:F2}</td></tr>\n",
                                                    Parameters.Instance.SplitNames[i],
                                                    cumMetrics.metrics[i].Average,
                                                    cumMetrics.metrics[i].Max,
                                                    cumMetrics.metrics[i].Min);
                        }
                        else
                        {
                            sRow = string.Format("<tr><td>{0}</td><td>N/A</td><td>N/A</td><td>N/A</td></tr>\n",
                                                    Parameters.Instance.SplitNames[i]);
                        }
                        sCumDetails += sRow;
                    }

                    sCumDetails +=
@"    </tbody>
</table>
<br/>
";
            }

            string sChart;
            if (File.Exists(Parameters.Instance.CaptureFile))
                sChart = "<img src='" + Parameters.Instance.CaptureFile + "'/>";
            else
                sChart = "Performance chart not available";


            string sResponse = "";
            try
            {
                sResponse = string.Format(sTemplate,
                                                periodMetrics.PeriodTime,
                                                sessions.Count,
                                                periodMetrics.MaxSessions,
                                                periodMetrics.ProcessedSessions,
                                                periodMetrics.Errors,
                                                bHavePeriodData ? periodMetrics.metrics[Parameters.Instance.TotalTime].Average.ToString("F2") : "",
                                                bHavePeriodData ? periodMetrics.metrics[Parameters.Instance.TotalTime].Max.ToString("F2") : "",
                                                bHavePeriodData ? periodMetrics.metrics[Parameters.Instance.TotalTime].Min.ToString("F2") : "",
                                                sPeriodDetails,
                                                cumMetrics.PeriodTime,
                                                cumMetrics.MaxSessions,
                                                cumMetrics.ProcessedSessions,
                                                cumMetrics.Errors,
                                                bHaveCumData ? cumMetrics.metrics[Parameters.Instance.TotalTime].Average.ToString("F2") : "",
                                                bHaveCumData ? cumMetrics.metrics[Parameters.Instance.TotalTime].Max.ToString("F2") : "",
                                                bHaveCumData ? cumMetrics.metrics[Parameters.Instance.TotalTime].Min.ToString("F2") : "",
                                                sCumDetails,
                                                sChart);
            }
            catch (Exception ex)
            {
                sResponse = ex.Message;
            }
            
            ReturnSuccess(context, sResponse);
        }

        private void ReturnError(HttpListenerContext context, int code, string sText)
        {
            try
            {
                // format response
                string responseString = string.Format("<html>Error {0}. {1}</html>", code, sText);
                byte[] buffer = Encoding.UTF8.GetBytes(responseString);

                // and send it
                HttpListenerResponse response = context.Response;
                response.ContentType = "text/html";
                response.ContentLength64 = buffer.Length;
                response.StatusCode = code;
                response.OutputStream.Write(buffer, 0, buffer.Length);
                response.OutputStream.Close();
            }
            catch (Exception)
            {
                // just swallow this
            }
        }

        private void ReturnSuccess(HttpListenerContext context, string sText)
        {
            try
            {
                // format response
                string responseString = string.Format("<html>{0}</html>", sText);
                byte[] buffer = Encoding.UTF8.GetBytes(responseString);

                // and send it
                HttpListenerResponse response = context.Response;
                response.ContentType = "text/html";
                response.ContentLength64 = buffer.Length;
                response.StatusCode = 200;
                response.OutputStream.Write(buffer, 0, buffer.Length);
                response.OutputStream.Close();

                // TODO: handle large responses
            }
            catch (Exception ex)
            {
                ReturnError(context, 500, ex.Message);
            }
        }
    }
}
