using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AutoMapper;

namespace WDW.NGE.Support.Models.Services
{
    public class XBMSServiceAgent : ServiceAgent, IXBMSServiceAgent
    {
        static XBMSServiceAgent()
        {
            Mapper.CreateMap<Dto.xBMS.Address, Models.xBMS.Address>();
            Mapper.CreateMap<Dto.xBMS.CustomizationSelection, Models.xBMS.CustomizationSelection>();
            Mapper.CreateMap<Dto.xBMS.QualifyingId, Models.xBMS.QualifyingId>();
            Mapper.CreateMap<Dto.xBMS.RequestAddress, Models.xBMS.RequestAddress>();
            Mapper.CreateMap<Dto.xBMS.ResortReservation, Models.xBMS.ResortReservation>();
            Mapper.CreateMap<Dto.xBMS.Shipment, Models.xBMS.Shipment>();
            Mapper.CreateMap<Dto.xBMS.XbandDetails, Models.xBMS.XbandDetails>();
            Mapper.CreateMap<Dto.xBMS.XbandRequestDetails, Models.xBMS.XbandRequestDetails>();
        }

        public XBMSServiceAgent()
            : this("http://nge-prod-xbms.wdw.disney.com:8080/xbms-rest/app/")
        {
        }

        public XBMSServiceAgent(String rootUrl)
            : base(rootUrl, "xBMS Services")
        {
        }

        public ServiceResult<Models.xBMS.XbandDetails> GetXbandDetails(string xBandId)
        {
            ServiceResult<Dto.xBMS.XbandDetails> dtoServiceResult =
                GetRequest<Dto.xBMS.XbandDetails>(String.Format(String.Concat(this.RootUrl, "xband/{0}"), xBandId));

            ServiceResult<Models.xBMS.XbandDetails> serviceResult = new ServiceResult<Models.xBMS.XbandDetails>();

            serviceResult.Status = dtoServiceResult.Status;

            if (dtoServiceResult.Status == ServiceCallStatus.OK)
            {
                serviceResult.Result = Mapper.Map<Dto.xBMS.XbandDetails, Models.xBMS.XbandDetails>(dtoServiceResult.Result);
            }

            return serviceResult;
        }

        public ServiceResult<Models.xBMS.XbandDetails> GetXbandDetailsByVisualId(string visualId)
        {
            ServiceResult<Dto.xBMS.XbandDetails> dtoServiceResult =
                GetRequest<Dto.xBMS.XbandDetails>(
                    String.Format(String.Concat(this.RootUrl, "xbands?externalNumber={0}"), visualId));

            ServiceResult<Models.xBMS.XbandDetails> serviceResult = new ServiceResult<Models.xBMS.XbandDetails>();

            serviceResult.Status = dtoServiceResult.Status;

            if (dtoServiceResult.Status == ServiceCallStatus.OK)
            {
                serviceResult.Result = Mapper.Map<Dto.xBMS.XbandDetails, Models.xBMS.XbandDetails>(dtoServiceResult.Result);
            }

            return serviceResult;
        }

        public ServiceResult<Models.xBMS.XbandRequestDetails> GetXbandRequestDetails(string xbandRequestId)
        {
            ServiceResult<Dto.xBMS.XbandRequestDetails> dtoServiceResult =
                GetRequest<Dto.xBMS.XbandRequestDetails>(
                    String.Format(String.Concat(this.RootUrl, "xband-requests/{0}"), xbandRequestId));

            ServiceResult<Models.xBMS.XbandRequestDetails> serviceResult = new ServiceResult<Models.xBMS.XbandRequestDetails>();

            serviceResult.Status = dtoServiceResult.Status;

            if (dtoServiceResult.Status == ServiceCallStatus.OK)
            {
                serviceResult.Result = Mapper.Map<Dto.xBMS.XbandRequestDetails, Models.xBMS.XbandRequestDetails>(dtoServiceResult.Result);
            }

            return serviceResult;
        }

        public ServiceResult<Models.xBMS.XbandRequestDetails> GetXbandRequestDetailsByTravelPlan(string travelPlanId)
        {
            ServiceResult<Dto.xBMS.XbandRequestDetails> dtoServiceResult =
                GetRequest<Dto.xBMS.XbandRequestDetails>(
                    String.Format(String.Concat(this.RootUrl, "request-entitlements/{0};entitlement-type=travel-plan-id"), travelPlanId));

            ServiceResult<Models.xBMS.XbandRequestDetails> serviceResult = new ServiceResult<Models.xBMS.XbandRequestDetails>();

            serviceResult.Status = dtoServiceResult.Status;

            if (dtoServiceResult.Status == ServiceCallStatus.OK)
            {
                serviceResult.Result = Mapper.Map<Dto.xBMS.XbandRequestDetails, Models.xBMS.XbandRequestDetails>(dtoServiceResult.Result);
            }

            return serviceResult;
        }
    }
}
