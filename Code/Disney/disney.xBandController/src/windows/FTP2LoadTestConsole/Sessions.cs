using System;
using System.Collections.Generic;
using System.Text;

namespace TestConsole
{
    public class Sessions 
    {
        public Dictionary<string, TestSession> sessions = new Dictionary<string, TestSession>();

        public Sessions()
        {
        }

        public int Count
        {
            get
            {
                lock (sessions)
                {
                    return sessions.Count;
                }
            }
        }

        public TestSession AddSession(string sName)
        {
            lock (sessions)
            {
                if (sessions.ContainsKey(sName))
                    throw new ApplicationException("Session is already running");

                TestSession ts = new TestSession();
                sessions.Add(sName, ts);

                return ts;
            }
        }

        public void RemoveSession(string sName)
        {
            lock (sessions)
            {
                if (!sessions.ContainsKey(sName))
                    throw new ApplicationException("Session does not exist");

                sessions.Remove(sName);
            }
        }

        public TestSession FindSession(string sName)
        {
            lock (sessions)
            {
                if (!sessions.ContainsKey(sName))
                    return null;
                else
                    return sessions[sName];
            }
        }
    }
}
