using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using AutoMapper;

namespace WDW.NGE.Support.Models.Services
{
    public class IDMSServiceAgent : ServiceAgent, IIDMSServiceAgent
    {
        static IDMSServiceAgent()
        {
            Mapper.CreateMap<Dto.Common.GuestIdentifier, Models.Common.GuestIdentifier>();
            Mapper.CreateMap<Dto.Common.GuestIdentifierResult, Models.Common.GuestIdentifierResult>();
            Mapper.CreateMap<Dto.IDMS.GuestProfile, Models.IDMS.GuestProfile>();
            Mapper.CreateMap<Dto.IDMS.GuestLocators, Models.IDMS.GuestLocators>();
            Mapper.CreateMap<Dto.IDMS.Xband, Models.IDMS.Xband>();
            Mapper.CreateMap<Dto.Common.Link, Models.Common.Link>();
        }

        private string connectionString;

        public IDMSServiceAgent()
            : this("http://nge-prod-idms.wdw.disney.com:8080/IDMS/", "nm-fldi-00223.wdw.disney.com",
                "IDMS", "deletemeon112712", "Mayhem2012")
        {
        }

        public IDMSServiceAgent(string rootUrl, string server, string database, string user, string password)
            : base(rootUrl, "IDMS Services")
        {
            this.connectionString =
                String.Format("Server={0};Database={1};User Id={2};Password={3};",
                server, database, user, password);
        }

        public ServiceResult<Models.Common.IGuestProfile> GetGuestProfile(Models.Common.GuestIdentifier guestIdentifier)
        {
            ServiceResult<Dto.IDMS.GuestProfile> dtoServiceResult =
                GetRequest<Dto.IDMS.GuestProfile>(
                    String.Format(String.Concat(this.RootUrl, "guest/id;{0}={1}/profile"),
                    guestIdentifier.IdentifierType, guestIdentifier.IdentifierValue));

            ServiceResult<Models.Common.IGuestProfile> serviceResult = new ServiceResult<Common.IGuestProfile>();

            serviceResult.Status = dtoServiceResult.Status;

            if (dtoServiceResult.Status == ServiceCallStatus.OK)
            {
                serviceResult.Result = Mapper.Map<Dto.IDMS.GuestProfile, Models.IDMS.GuestProfile>(dtoServiceResult.Result);
            }

            return serviceResult;
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

        public void RemoveGuestIdentifier(long guestId, Models.Common.GuestIdentifier guestIdentifier)
        {
            try
            {
                using (SqlConnection connection = new SqlConnection(this.connectionString))
                {
                    connection.Open();

                    using (SqlCommand command = new SqlCommand("[dbo].[usp_source_system_link_delete]", connection))
                    {
                        command.CommandType = CommandType.StoredProcedure;

                        SqlParameter guestIdParameter = command.Parameters.Add("@guestId", SqlDbType.BigInt);
                        guestIdParameter.Direction = ParameterDirection.Input;
                        guestIdParameter.Value = guestId;

                        SqlParameter sourceSystemIdValueParameter = command.Parameters.Add("@sourceSystemIdValue", SqlDbType.NVarChar, 200);
                        sourceSystemIdValueParameter.Direction = ParameterDirection.Input;
                        sourceSystemIdValueParameter.Value = guestIdentifier.IdentifierValue;

                        SqlParameter sourceSystemIdTypeParameter = command.Parameters.Add("@sourceSystemIdType", SqlDbType.NVarChar, 200);
                        sourceSystemIdTypeParameter.Direction = ParameterDirection.Input;
                        sourceSystemIdTypeParameter.Value = guestIdentifier.IdentifierType;

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch (SqlException ex)
            {
            }
        }

        public void MoveIdentifier(long guestId, Models.Common.GuestIdentifier guestIdentifier)
        {
            try
            {
                using (SqlConnection connection = new SqlConnection(this.connectionString))
                {
                    connection.Open();

                    StringBuilder sb = new StringBuilder();

                    sb.AppendLine("DECLARE @IDMSTypeID int");

                    sb.AppendFormat("SELECT @IDMSTypeID = [IDMSTypeID] FROM [dbo].[IDMS_Type] WHERE [IDMSTypeName] = '{0}' AND [IDMSKey] = 'SOURCESYSTEM'", guestIdentifier.IdentifierType);
                    sb.AppendLine();
                    sb.AppendFormat("DELETE FROM [IDMS].[dbo].[source_system_link] WHERE [IDMSTypeId] = @IDMSTypeID AND [sourceSystemIdValue] = '{0}'", guestIdentifier.IdentifierValue);
                    sb.AppendLine();

                    using (SqlCommand command = new SqlCommand(sb.ToString(), connection))
                    {
                        command.ExecuteNonQuery();
                    }

                    Dto.IDMS.GuestIdentifierPut guestIdentifierPut = new Dto.IDMS.GuestIdentifierPut()
                    {
                        IdentifierType = guestIdentifier.IdentifierType,
                        IdentifierValue = guestIdentifier.IdentifierValue
                    };

                    PostRequest<Dto.IDMS.GuestIdentifierPut>(String.Format(String.Concat(this.RootUrl, "guest/{0}/identifiers"), guestId), guestIdentifierPut);

                }
            }
            catch (SqlException ex)
            {
            }
        }

        public void AssignBand(long guestId, string xbmsId)
        {
            try
            {
                using (SqlConnection connection = new SqlConnection(this.connectionString))
                {
                    connection.Open();

                    String commandText = String.Format("SELECT [xbandId] FROM [IDMS].[dbo].[xband] where [xbmsId] = '{0}'", xbmsId);

                    long xbandId = 0;

                    using (SqlCommand command = new SqlCommand(commandText, connection))
                    {
                        xbandId = (long)command.ExecuteScalar();
                    }

                    StringBuilder sb = new StringBuilder();

                    sb.AppendLine("DECLARE @xbandRowId uniqueidentifier");
                    sb.AppendLine("DECLARE @guestRowId uniqueidentifier");

                    sb.AppendFormat("SELECT @guestRowId = [guestRowId] FROM [IDMS].[dbo].[guest] where [guestId] = {0}", guestId);
                    sb.AppendLine();
                    sb.AppendFormat("SELECT @xbandRowId = [xbandRowId] FROM [IDMS].[dbo].[xband] where [xbandId] = {0}", xbandId);
                    sb.AppendLine();

                    sb.AppendLine("EXECUTE  [IDMS].[dbo].[usp_xband_assign] @xbandRowId ,@guestRowId");

                    using (SqlCommand command = new SqlCommand(sb.ToString(), connection))
                    {
                        command.ExecuteNonQuery();
                    }
                }
            }
            catch (SqlException ex)
            {
                //TODO:
            }
            catch (NullReferenceException ex)
            {
                //TODO: Band not Found.
            }
        }

        public ServiceResult<Models.IDMS.GuestLocators> GetGuestLocators()
        {

            ServiceResult<Models.IDMS.GuestLocators> result = new ServiceResult<Models.IDMS.GuestLocators>()
            {
                Result = null
            };

            ServiceResult<Dto.IDMS.GuestLocators> serviceResult =
                GetRequest<Dto.IDMS.GuestLocators>(String.Concat(this.RootUrl, "guest/locators"));

            result.Status = serviceResult.Status;

            if (serviceResult.Status == ServiceCallStatus.OK)
            {
                result.Result = Mapper.Map<Dto.IDMS.GuestLocators, Models.IDMS.GuestLocators>(serviceResult.Result);
            }

            return result;
        }


        public void UpdateName(long guestId, string firstName, string lastName)
        {
            Dto.IDMS.GuestPut guest = new Dto.IDMS.GuestPut()
            {
                GuestId = guestId.ToString(),
                Name = new Dto.IDMS.GuestName()
                {
                    FirstName = firstName,
                    LastName = lastName
                },
                Status = "Active"
            };

            PutRequest<Dto.IDMS.GuestPut>(String.Concat(this.RootUrl, "guest/"), guest);

        }


        public List<string> GetVisualIds(string date)
        {
            List<string> visualIds = new List<string>();

            try
            {
                using (SqlConnection connection = new SqlConnection(this.connectionString))
                {
                    connection.Open();

                    //String commandText = String.Format("SELECT [xbandId] FROM [IDMS].[dbo].[xband] where [xbmsId] = '{0}'", xbmsId);

                }
            }
            catch (SqlException ex)
            {
                //TODO:
            }
            catch (NullReferenceException ex)
            {
                //TODO: Band not Found.
            }

            return visualIds;
        }


        public void CreateXbandAssociation(IDMS.XbandAssociation xbandAssociation)
        {

            try
            {
                using (SqlConnection connection = new SqlConnection(this.connectionString))
                {
                    connection.Open();

                    using (SqlCommand command = new SqlCommand("[dbo].[usp_xband_association_create]", connection))
                    {
                        command.CommandType = CommandType.StoredProcedure;

                        SqlParameter parameter = command.Parameters.Add("@xbandId", SqlDbType.BigInt);
                        parameter.Direction = ParameterDirection.Output;

                        parameter = command.Parameters.Add("@externalNumber", SqlDbType.NVarChar, 255);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.ExternalNumber;

                        parameter = command.Parameters.Add("@longRangeTag", SqlDbType.NVarChar, 200);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.LongRangeTag;

                        parameter = command.Parameters.Add("@shortRangeTag", SqlDbType.NVarChar, 200);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.ShortRangeTag;

                        parameter = command.Parameters.Add("@secureId", SqlDbType.NVarChar, 200);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.SecureId;

                        parameter = command.Parameters.Add("@uid", SqlDbType.NVarChar, 200);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.Uid;

                        parameter = command.Parameters.Add("@publicId", SqlDbType.NVarChar, 200);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.PublicId;

                        parameter = command.Parameters.Add("@printedName", SqlDbType.NVarChar, 200);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.PrintedName;

                        parameter = command.Parameters.Add("@xbmsId", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.XbmsId;

                        parameter = command.Parameters.Add("@primaryState", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.PrimaryState;

                        parameter = command.Parameters.Add("@secondaryState", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.SecondaryState;

                        parameter = command.Parameters.Add("@guestIdType", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.GuestIdType;

                        parameter = command.Parameters.Add("@guestIdValue", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.GuestIdValue;

                        parameter = command.Parameters.Add("@xbandOwnerId", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;
                        parameter.Value = xbandAssociation.XbandOwnerId;

                        parameter = command.Parameters.Add("@xbandRequestId", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;

                        if (!String.IsNullOrEmpty(xbandAssociation.XbandRequestId))
                        {
                            parameter.Value = xbandAssociation.XbandRequestId;
                        }
                        else
                        {
                            parameter.Value = DBNull.Value;
                        }

                        parameter = command.Parameters.Add("@bandType", SqlDbType.NVarChar, 50);
                        parameter.Direction = ParameterDirection.Input;

                        if (!String.IsNullOrEmpty(xbandAssociation.BandType))
                        {
                            parameter.Value = xbandAssociation.BandType;
                        }
                        else
                        {
                            parameter.Value = DBNull.Value;
                        }

                        command.ExecuteNonQuery();
                    }
                }
            }
            catch (SqlException ex)
            {
                //TODO:
            }
            catch (NullReferenceException ex)
            {
                //TODO: Band not Found.
            }
        }
    }
}
