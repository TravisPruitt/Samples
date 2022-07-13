using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Text;

namespace WDW.NGE.Support.Models.Services
{
    public class ServiceResult<T>
    {
        public T Result { get; set; }

        public ServiceCallStatus Status { get; set; } 

        public void SetStatus(HttpStatusCode statusCode)
        {
            switch (statusCode)
            {
                case HttpStatusCode.OK:
                    {
                        this.Status = ServiceCallStatus.OK;
                        break;
                    }

                case HttpStatusCode.NotFound:
                    {
                        this.Status = ServiceCallStatus.NotFound;
                        break;
                    }

                case HttpStatusCode.RequestTimeout:
                    {
                        this.Status = ServiceCallStatus.Timeout;
                        break;
                    }

                default:
                    {
                        this.Status = ServiceCallStatus.Unknown;
                        break;
                    }
            }
        }
    }
}
