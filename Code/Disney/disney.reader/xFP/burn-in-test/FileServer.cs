using System;
using System.IO;
using System.Net;
using System.Web.Script.Serialization;
using System.Windows.Forms;
using System.Diagnostics;
using System.Threading;
using System.Collections.Generic;
using System.Linq;

namespace xTPManufacturerTest
{
    class FileServer
    {
        private HttpListener listener;
        private string _rootPath;

        public FileServer(string rootPath)
        {
            listener = new HttpListener();
            listener.Prefixes.Add("http://*:8000/");
            _rootPath = rootPath;
        }

        public void Start()
        {
            listener.Start();
            ListenForNextRequest();
            Trace.Write("File server started");
        }

        public void Stop()
        {
            listener.Stop();
            Trace.Write("File server stopped");
        }

        private void ListenForNextRequest()
        {
            listener.BeginGetContext(new AsyncCallback(ListenerCallback), listener);
        }

        private void ListenerCallback(IAsyncResult result)
        {
            HttpListener listener = (HttpListener)result.AsyncState;
            if (!listener.IsListening)
                return;

            HttpListenerContext context = listener.EndGetContext(result);
            ListenForNextRequest();

            try
            {
                handleRequest(context);
            }
            catch (Exception e)
            {
                MessageBox.Show(e.ToString());
            }
            finally
            {
                context.Response.Close();
            }
        }

        private void handleRequest(HttpListenerContext context)
        {
            string path = _rootPath + context.Request.Url.AbsolutePath;
            if (File.Exists(path))
            {
                string extension = Path.GetExtension(path).ToLower().TrimStart('.');
                {
                    context.Response.StatusCode = 200;
                    context.Response.ContentType = Http.GetMimeType(extension);
                    FileStream fin = new FileStream(path, FileMode.Open);
                    BinaryReader br = new BinaryReader(fin);
                    BinaryWriter bw = new BinaryWriter(context.Response.OutputStream);

                    byte[] b;
                    do
                    {
                        b = br.ReadBytes(512);
                        bw.Write(b);
                    } while (b.Length > 0);

                    br.Close();
                    bw.Close();
                }
            }
            else
                send404Reply(context.Response);
        }

        private void sendOkReply(HttpListenerResponse response)
        {
            response.StatusCode = 200;
            response.OutputStream.Flush();
            response.OutputStream.Close();
        }

        private void send404Reply(HttpListenerResponse response)
        {
            response.StatusCode = 404;
            sendStringReply(response, "File not found");
        }

        private void send400Reply(HttpListenerResponse response)
        {
            response.StatusCode = 400;
            sendStringReply(response, "Bad Request");
        }

        private void sendStringReply(HttpListenerResponse response, string msg)
        {
            response.ContentType = "text/plain";
            byte[] buf = System.Text.Encoding.UTF8.GetBytes(msg);
            response.ContentLength64 = buf.Length;
            response.OutputStream.Write(buf, 0, buf.Length);
            response.OutputStream.Close();
        }
    }

}

