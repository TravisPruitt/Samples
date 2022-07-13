using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AutoMapper;

namespace WDW.NGE.Support.Models.Services
{
    public class OneViewServiceAgent: ServiceAgent, IOneViewServiceAgent
    {
        static OneViewServiceAgent()
        {
            Mapper.CreateMap<Dto.OneView.Entitlement, Models.OneView.Entitlement>();
            Mapper.CreateMap<Dto.OneView.EntitlementGuest, Models.OneView.EntitlementGuest>();
            Mapper.CreateMap<Dto.OneView.EntitlementLinks, Models.OneView.EntitlementLinks>();
            Mapper.CreateMap<Dto.Common.GuestIdentifier, Models.Common.GuestIdentifier>();
            Mapper.CreateMap<Dto.Common.GuestIdentifierResult, Models.Common.GuestIdentifierResult>();
            Mapper.CreateMap<Dto.OneView.GuestProfile, Models.OneView.GuestProfile>();
            Mapper.CreateMap<Dto.Common.Link, Models.Common.Link>();
            Mapper.CreateMap<Dto.OneView.GuestEligibility, Models.OneView.GuestEligibility>();
            Mapper.CreateMap<Dto.OneView.GuestEligibilityLinks, Models.OneView.GuestEligibilityLinks>();
        }

        public OneViewServiceAgent(string rootUrl)
            : base(rootUrl, "OneView Services")
        {
        }

        public OneViewServiceAgent()
            : this("http://nge-prod-asm.wdw.disney.com:8080/assembly/")
        {
        }

        public ServiceResult<Models.Common.IGuestProfile> GetGuestProfile(Models.Common.GuestIdentifier guestIdentifier)
        {
            ServiceResult<Dto.OneView.GuestProfile> dtoServiceResult =
                GetRequest<Dto.OneView.GuestProfile>(
                    String.Format(String.Concat(this.RootUrl, "guest/id;{0}={1}/profile"),
                    guestIdentifier.IdentifierType, guestIdentifier.IdentifierValue));

            ServiceResult<Models.Common.IGuestProfile> serviceResult = new ServiceResult<Common.IGuestProfile>();

            serviceResult.Status = dtoServiceResult.Status;

            if (dtoServiceResult.Status == ServiceCallStatus.OK)
            {
                serviceResult.Result = Mapper.Map<Dto.OneView.GuestProfile, Models.OneView.GuestProfile>(dtoServiceResult.Result);
            }

            return serviceResult;
        }


        public Models.OneView.GuestProfile GetGuestProfile(string guestIdType, string guestIdValue)
        {
            ServiceResult<Dto.OneView.GuestProfile> serviceResult =
                GetRequest<Dto.OneView.GuestProfile>(
                    String.Format(String.Concat(this.RootUrl, "guest/id;{0}={1}/profile"), guestIdType, guestIdValue));


            if (serviceResult.Status == ServiceCallStatus.OK)
            {
                return Mapper.Map<Dto.OneView.GuestProfile, Models.OneView.GuestProfile>(serviceResult.Result);
            }

            return null;
        }


        public Models.Common.GuestIdentifierResult GetGuestIdentifiers(string guestIdType, string guestIdValue)
        {
             ServiceResult<Dto.Common.GuestIdentifierResult> serviceResult = 
                GetRequest<Dto.Common.GuestIdentifierResult>(
                    String.Format(String.Concat(this.RootUrl, "guest/id;{0}={1}/identifiers"), guestIdType, guestIdValue));

             if (serviceResult.Status == ServiceCallStatus.OK)
             {
                 return Mapper.Map<Dto.Common.GuestIdentifierResult, Models.Common.GuestIdentifierResult>(serviceResult.Result);
             }

             return null;
        }


        public Models.OneView.Entitlement GetEntitlement(string reservationId)
        {
            ServiceResult<Dto.OneView.EntitlementResult> serviceResult =
                GetRequest<Dto.OneView.EntitlementResult>(
                    String.Format(String.Concat(this.RootUrl, "guests-on-entitlement?entitlement-id-type=resort-reservation-id&entitlement-id-value={0}"), reservationId));

            if (serviceResult.Status == ServiceCallStatus.OK)
             {
                return Mapper.Map<Dto.OneView.Entitlement, Models.OneView.Entitlement>(serviceResult.Result.Entitlement);
             }

             return null;
        }
    }
}
