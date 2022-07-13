using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.IDMS;
using Disney.xBand.IDMS.xBand;
using Disney.xBand.IDMS.Guests;

namespace Disney.xBand.xBMS.Simulator.JMS
{
    public class xBandPublisher : Publisher
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog xbandLog = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public xBandPublisher(String topicName, MessageRepository messageRepository)
            : base(topicName, messageRepository)
        {
        }

        protected override void PublishTimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            if (!this.IsConnected)
            {
                this.Start();
            }

            if (this.IsConnected)
            {
                Dto.xBand message = null;

                if (this.MessageRepository.GetNextxBandMessage(out message))
                {
                    Sonic.Jms.TextMessage textMessage = this.PublisherSession.createTextMessage();

                    Dto.BusinessEvent businessEvent = new Dto.BusinessEvent()
                    {
                        Location = "XBMS.XBAND",
                        EventType = "BOOK",
                        SubType = String.Empty,
                        ReferenceId = message.XBandId,
                        GuestIdentifier = String.Empty,
                        TimeStamp = DateTime.UtcNow.ToString(MessageRepository.DATE_FORMAT),
                        Payload = String.Empty,
                        CorrelationId = Guid.NewGuid().ToString()
                    };

                    String xml = businessEvent.ToXml();

                    textMessage.setText(xml);

                    //Add to sent messages before send, so callback won't fail
                    this.MessageRepository.MessageSent(message);

                    try
                    {
                        //Publish Message
                        this.MessageProducer.send(textMessage);
                    }
                    catch (Exception)
                    {
                        message.MessageState = Dto.MessageState.Error;
                    }
                }
            }
        }

        protected override void ValidationTimerElapsed(object sender, System.Timers.ElapsedEventArgs e)
        {
            if (this.MessageRepository.xBandMessageState == Dto.SimulationState.Validating)
            {
                Dto.xBand xBand = this.MessageRepository.GetNextUnvalidatedxBandMessage();

                if (xBand != null)
                {
                    Validate(xBand);
                }
            }
        }

        private void Validate(Dto.xBand xband)
        {
            Metrics validateMetrics = new Metrics("Validate xBand Message");

            IXbandDao dao = new XbandDao(this.IdmsRootUrl);
            IGuestDao guestDao = new GuestDao(this.IdmsRootUrl);

            try
            {
                try
                {
                    //Check all IDs?
                    Xband xbandCheck = dao.FindByPublicId(xband.PublicId.ToString(), validateMetrics);
                }
                catch (Exception)
                {
                    //xband.MessageState = Dto.MessageState.xBandNotFound;
                    xband.MessageState = Dto.MessageState.Error;
                    xbandLog.ErrorFormat("xBand with publicId {0} not found.", xband.PublicId);
                    return;
                }

                GuestProfile guest = null;

                //Check guest
                try
                {
                    guest = guestDao.FindByGuestIdentifier(xband.GuestIdType, xband.GuestId, validateMetrics);
                }
                catch (Exception)
                {
                    //xband.MessageState = Dto.MessageState.GuestNotFound;
                    xband.MessageState = Dto.MessageState.Error;
                    xbandLog.ErrorFormat("xBand with publicId {0} not assigned to guest {1}:{2}.", xband.PublicId, xband.GuestIdType, xband.GuestId);
                    return;
                }

                bool xBandAssigned = false;

                foreach (Xband guestXband in guest.xBands)
                {
                    if (String.Compare(guestXband.PublicId, xband.PublicId.ToString(), true) == 0)
                    {
                        xBandAssigned = true;
                    }
                }

                if (xBandAssigned)
                {
                    xband.MessageState = Dto.MessageState.Success;
                    xbandLog.InfoFormat("xBand with publicId {0} validated.", xband.PublicId);
                }
                else
                {
                    //xband.MessageState = Dto.MessageState.AssignmentNotFound;
                    xband.MessageState = Dto.MessageState.Error;
                    xbandLog.ErrorFormat("xBand with publicId {0} assignment to guest {1}:{2} not found.", xband.PublicId, xband.GuestIdType, xband.GuestId);
                }

            }
            catch (Exception)
            {
                xband.MessageState = Dto.MessageState.Error;
            }
        }
    }
}
