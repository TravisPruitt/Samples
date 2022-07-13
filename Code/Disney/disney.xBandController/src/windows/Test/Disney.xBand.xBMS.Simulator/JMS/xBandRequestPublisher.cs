using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Disney.xBand.IDMS;
using Disney.xBand.IDMS.xBand;
using Disney.xBand.IDMS.Guests;
using Disney.xBand.IDMS.GuestIdentifiers;

namespace Disney.xBand.xBMS.Simulator.JMS
{
    public class xBandRequestPublisher : Publisher
    {
        // Create a logger for use in this class
        private static readonly log4net.ILog requestLog = log4net.LogManager.GetLogger(System.Reflection.MethodBase.GetCurrentMethod().DeclaringType);

        public xBandRequestPublisher(String topicName, MessageRepository messageRepository)
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
                Dto.xBandRequest message = null;

                if (this.MessageRepository.GetNextxBandRequestMessage(out message))
                {
                    Sonic.Jms.TextMessage textMessage = this.PublisherSession.createTextMessage();

                    Dto.BusinessEvent businessEvent = new Dto.BusinessEvent()
                    {
                        Location = "XBMS.XBANDREQUEST",
                        EventType = "BOOK",
                        SubType = String.Empty,
                        ReferenceId = message.xBandRequestId,
                        GuestIdentifier = String.Empty,
                        TimeStamp = DateTime.UtcNow.ToString(MessageRepository.DATE_FORMAT),
                        Payload = null,
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
            if (this.MessageRepository.xBandRequestMessageState == Dto.SimulationState.Validating)
            {
                Dto.xBandRequest xBandRequest = this.MessageRepository.GetNextUnvalidatedxBandRequestMessage();

                if (xBandRequest != null)
                {
                    Validate(xBandRequest);
                }
            }
        }

        private void Validate(Dto.xBandRequest xbandRequest)
        {
            Metrics validateMetrics = new Metrics("Validate xBandRequest Message");

            IXbandDao dao = new XbandDao(this.IdmsRootUrl);
            IGuestDao guestDao = new GuestDao(this.IdmsRootUrl);

            try
            {
                foreach (Dto.CustomizationSelection customizationSelection in xbandRequest.CustomizationSelections)
                {
                    try
                    {
                        GuestProfile guestCheck = guestDao.FindByGuestIdentifier(customizationSelection.GuestTypeId,
                            customizationSelection.GuestId, validateMetrics);

                        if (String.Compare(guestCheck.Name.LastName, customizationSelection.LastName, false) != 0 ||
                            String.Compare(guestCheck.Name.FirstName, customizationSelection.FirstName, false) != 0)
                        {
                            //xbandRequest.MessageState = Dto.MessageState.GuestNameMismatch;
                            xbandRequest.MessageState = Dto.MessageState.Error;
                            requestLog.ErrorFormat("Guest name mismatch for {0}:{1}", customizationSelection.GuestTypeId, customizationSelection.GuestId);
                            requestLog.ErrorFormat("Expected {0} {1}", customizationSelection.FirstName, customizationSelection.LastName);
                            requestLog.ErrorFormat("Actual {0} {1}", guestCheck.Name.LastName, guestCheck.Name.FirstName);
                            return;
                        }

                        //Should have orignal identifier and xbms-linkid
                        GuestIdentifier guestIdentifier = guestCheck.GuestIdentifiers.Where(g => g.TypeName == customizationSelection.GuestTypeId).FirstOrDefault();

                        if (guestIdentifier == null)
                        {
                            requestLog.ErrorFormat("Guest Identifier not found {0}:{1}", customizationSelection.GuestTypeId, customizationSelection.GuestId);
                            //xbandRequest.MessageState = Dto.MessageState.GuestIdentifierNotFound;
                            xbandRequest.MessageState = Dto.MessageState.Error;

                        }

                        //Should have orignal identifier and xbms-linkid
                        guestIdentifier = guestCheck.GuestIdentifiers.Where(g => g.TypeName == "xbms-link-id").FirstOrDefault();

                        if (guestIdentifier == null)
                        {
                            requestLog.ErrorFormat("xbms-link-id not found for guest {0}:{1}", customizationSelection.GuestTypeId, customizationSelection.GuestId);
                            //xbandRequest.MessageState = Dto.MessageState.xbmsLinkIdNotFound;
                            xbandRequest.MessageState = Dto.MessageState.Error;

                        }

                        xbandRequest.MessageState = Dto.MessageState.Success;
                        requestLog.InfoFormat("Guest {0}:{1} validated.", customizationSelection.GuestTypeId, customizationSelection.GuestId);
                    }
                    catch (Exception)
                    {
                        requestLog.ErrorFormat("Guest not found {0}:{1}", customizationSelection.GuestTypeId, customizationSelection.GuestId);
                        //xbandRequest.MessageState = Dto.MessageState.GuestNotFound;
                        xbandRequest.MessageState = Dto.MessageState.Error;
                        return;
                    }
                }
            }
            catch (Exception ex)
            {
                requestLog.Error("Unexpected Error", ex);
                xbandRequest.MessageState = Dto.MessageState.Error;
            }

        }
    }
}
