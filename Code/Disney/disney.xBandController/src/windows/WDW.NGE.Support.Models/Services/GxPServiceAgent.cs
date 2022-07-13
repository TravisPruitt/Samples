using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AutoMapper;
using Oracle.ManagedDataAccess.Client;

namespace WDW.NGE.Support.Models.Services
{
    public class GxPServiceAgent : ServiceAgent, IGxPServiceAgent
    {
        static GxPServiceAgent()
        {
            Mapper.CreateMap<Dto.GxP.BookingWindow, Models.GxP.BookingWindow>();
            Mapper.CreateMap<Dto.GxP.EligibilityResult, Models.GxP.EligibilityResult>();
            Mapper.CreateMap<Dto.GxP.IndividualEligibility, Models.GxP.IndividualEligibility>();
            Mapper.CreateMap<Dto.GxP.GroupEligibility, Models.GxP.GroupEligibility>();
        }
        public GxPServiceAgent(string rootUrl) : base(rootUrl, "GxP Services")
        {
        }

        public GxPServiceAgent()
            : this("http://nge-prod-fpp-sor.wdw.disney.com:8080/gxp-services/services/")
        {
        }

        public Models.GxP.IndividualEligibility CheckEligibility(string date, string xid)
        {
            ServiceResult<Dto.GxP.IndividualEligibility> serviceResult = 
                GetRequest<Dto.GxP.IndividualEligibility>(String.Format(String.Concat(this.RootUrl, "eligibility/date/{0}/guest/{1}"), date, xid));

            if (serviceResult.Status == ServiceCallStatus.OK)
            {
                return Mapper.Map<Dto.GxP.IndividualEligibility, Models.GxP.IndividualEligibility>(serviceResult.Result);
            }

            return null;
        }

        public Models.GxP.GroupEligibility CheckGroupEligibility(String date, List<string> xids)
        {
            StringBuilder xidList = new StringBuilder();
            bool first = true;

            foreach(String xid in xids)
            {
                if (first)
                {
                    xidList.Append(xid);
                    first = false;
                }
                else
                {
                    xidList.AppendFormat(",{0}", xid);
                }
            }

            ServiceResult<Dto.GxP.GroupEligibility> serviceResult =
                GetRequest<Dto.GxP.GroupEligibility>(
                    String.Format(String.Concat(this.RootUrl, "eligibility/date/{0}?guestIds={1}"), date, xidList.ToString()));

            if (serviceResult.Status == ServiceCallStatus.OK)
            {
                return Mapper.Map<Dto.GxP.GroupEligibility, Models.GxP.GroupEligibility>(serviceResult.Result);
            }

            return null;
        }


        public GxP.FastPassIntegration GetIntegrationStatus()
        {
            GxP.FastPassIntegration result = new GxP.FastPassIntegration();

            StringBuilder commandText = new StringBuilder();

            commandText.AppendLine("SELECT MAX(step.end_time) AS Last_Completed");
            commandText.AppendLine(",j.job_name");
            commandText.AppendLine(",step.STEP_NAME");
            commandText.AppendLine("FROM GXPMD.batch_job_instance j");
            commandText.AppendLine("JOIN GXPMD.batch_job_execution ex ON ex.job_instance_id = j.job_instance_id");
            commandText.AppendLine("JOIN GXPMD.batch_step_execution step ON ex.job_execution_id = step.job_execution_id");
            commandText.AppendLine("WHERE step.exit_code = 'COMPLETED'");
            commandText.AppendLine("GROUP BY step.STEP_NAME, j.job_name");
            commandText.AppendLine("ORDER BY MAX(step.end_time) DESC;");

            //Update to close all the inventory for Buzz
            using (OracleConnection con = new OracleConnection(/*ConfigurationManager.ConnectionStrings["GXP"].ConnectionString*/ "GXP"))
            {
                con.Open();

                OracleCommand cmd = con.CreateCommand();
                cmd.CommandType = System.Data.CommandType.Text;
                cmd.CommandText = commandText.ToString();
                cmd.CommandTimeout = 10;

                try
                {
//                    numberOfRowsAffected = cmd.ExecuteNonQuery();
                }
                catch (Exception ex)
                {
                    Debug.WriteLine("Error executing Oracle Command: {0}", ex.Message);
                }

            }

            return result;
        }
    }
}
