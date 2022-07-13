using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace xTRC
{
    [ComVisible(true)]
    internal class EventMessageQueue
    {
        private static EventMessageQueue instance;

        private Queue<TapMessage> eventMessages;

        private EventMessageQueue()
        {
            this.eventMessages = new Queue<TapMessage>();
        }

        public static EventMessageQueue Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new EventMessageQueue();
                }
                return instance;
            }
        }

        public void Enqueue(TapMessage eventMessage)
        {
            lock (this)
            {
                this.eventMessages.Enqueue(eventMessage);
            }
        }

        public TapMessage Dequeue()
        {
            lock (this)
            {
                return this.eventMessages.Dequeue();
            }
        }

        public bool HasEvents
        {
            get
            {
                lock (this)
                {
                    return this.eventMessages.Count > 0;
                }
            }
        }
    }
}
