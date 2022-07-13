using System;
using System.Collections.Generic;
using System.Text;

namespace Disney.xBand.xTRC
{
    internal class EventMessageQueue
    {
        private static EventMessageQueue instance;

        private Queue<TapEvent> eventMessages;

        private EventMessageQueue()
        {
            this.eventMessages = new Queue<TapEvent>();
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

        public void Enqueue(TapEvent eventMessage)
        {
            lock (this)
            {
                this.eventMessages.Enqueue(eventMessage);
            }
        }

        public TapEvent Dequeue()
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
