using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.Linq;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using Disney.xBand.Messages.JMS;
using System.Configuration;

namespace Disney.xBand.MessageService
{
    public partial class MessageService : ServiceBase
    {
        // This is a flag to indicate the service has been started.
        private bool serviceStarted = false;

        // Worker thread to listen to JMS messages.
        Thread listenerThread;

        public MessageService()
        {
            InitializeComponent();
        }

        protected override void OnStart(string[] args)
        {
            // Create worker thread; this will invoke the ListenerFunction
            // when we start it.
            // Since we use a separate worker thread, the main service
            // thread will return quickly, telling Windows that service has started
            ThreadStart st = new ThreadStart(ListenerFunction);
            listenerThread = new Thread(st);

            // set flag to indicate worker thread is active
            serviceStarted = true;

            // start the thread
            listenerThread.Start();

            eventLog.WriteEntry("Service started successfully", EventLogEntryType.Information);
        }

        protected override void OnStop()
        {
            // flag to tell the worker process to stop
            serviceStarted = false;

            // give it a little time to finish any pending work
            listenerThread.Join(new TimeSpan(0, 2, 0));

            eventLog.WriteEntry("Service stopped successfully", EventLogEntryType.Information);
        }

        private void ListenerFunction()
        {
            string topicsSetting = ConfigurationManager.AppSettings["ESB_Topics"];

            string[] topics = topicsSetting.Split(';');

            List<Listener> listeners = new List<Listener>();

            try
            {
                foreach (string topic in topics)
                {
                    string[] topicInfo = topic.Split(',');

                    Listener listener = new Listener(eventLog, topicInfo[0], topicInfo[1]);
                    listener.Start();
                    listeners.Add(listener);
                }


                // start an endless loop; loop will abort only when "serviceStarted"
                // flag = false
                while (serviceStarted)
                {
                    // yield
                    if (serviceStarted)
                    {
                        Thread.Sleep(new TimeSpan(0, 0, 30));
                    }
                }

                foreach (Listener listener in listeners)
                {
                    listener.Stop();
                }

                // time to end the thread
                Thread.CurrentThread.Abort();
            }
            catch (Exception ex)
            {

                eventLog.WriteEntry(String.Format("Message: {0}{2}Stack Trace: {1}", ex.Message, ex.StackTrace, Environment.NewLine), EventLogEntryType.Error);

                //Rethrow so so listenter won't attempt to start.
                throw;
            }


        }
    }
}
