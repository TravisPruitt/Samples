using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Disney.xBand.Provisionator.Dto
{
    public enum OffersetStatus
    {
        Success = 0,
        Failed = 1,
        CallFailed = 2,
        CommunicationsError = 3,
        NoOffersetFound = 4
    }

    public class GenerateOffersetResult
    {
        public int OffersetID { get; set; }

        public OffersetStatus Status { get; set; }

        public string Message { get; set; }

    }
    public class BookOffersetResult
    {
        public int OffersetID { get; set; }

        public OffersetStatus Status { get; set; }

        public string Message { get; set; }

    }

    public class AddDemoEntitlementsResult
    {
        public bool Successful
        {
            get
            {
                return (this.BookOffersetResult.Status == Dto.OffersetStatus.Success && 
                    this.GenerateOffersetResult.Status == Dto.OffersetStatus.Success);
            }
        }

        public BookOffersetResult BookOffersetResult { get; set; }
        public GenerateOffersetResult GenerateOffersetResult { get; set; }
    }
}
