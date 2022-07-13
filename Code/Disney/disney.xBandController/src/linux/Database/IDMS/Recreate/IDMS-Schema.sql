USE [Synaps_IDMS]
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 04/26/2012 01:50:45 ******/
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 04/26/2012 01:50:45 ******/
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 04/26/2012 01:50:46 ******/
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 04/26/2012 01:50:50 ******/
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 04/26/2012 01:50:50 ******/
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestByEmail]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestByEmail]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByBandId]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getXBandByBandId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByLRId]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getXBandByLRId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandBySecureId]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getXBandBySecureId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByTapId]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getXBandByTapId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByUID]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getXBandByUID]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByXBandId]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_getXBandByXBandId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestProfileById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestProfileById]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_create]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsByXbandId]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestsByXbandId]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetIdentifiersByIdentifier]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_GetIdentifiersByIdentifier]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByGuestID]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_GetXBandsByGuestID]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByIdentifier]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_GetXBandsByIdentifier]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest__addresss_create]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest__addresss_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_create]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_source_system_link_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestCelebrationsById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestCelebrationsById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdentifiersById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestIdentifiersById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdFromSourceTypeId]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestIdFromSourceTypeId]
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateGuestIdentifier]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_CreateGuestIdentifier]
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_CreateTestUser]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_create]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest_xband_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_delete]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest_xband_delete]
GO
/****** Object:  StoredProcedure [dbo].[getGuestCelebrationsById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[getGuestCelebrationsById]
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]
GO
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]
GO
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]
GO
DROP TABLE [dbo].[party_guest]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 04/26/2012 01:50:45 ******/
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_guest]
GO
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]
GO
DROP TABLE [dbo].[celebration]
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]
GO
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]
GO
DROP TABLE [dbo].[guest_address]
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]
GO
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
DROP TABLE [dbo].[guest_phone]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]
GO
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]
GO
DROP TABLE [dbo].[guest_xband]
GO
/****** Object:  StoredProcedure [dbo].[usp_deactivateGuestById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_deactivateGuestById]
GO
/****** Object:  Table [dbo].[party]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]
GO
DROP TABLE [dbo].[party]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameByEmail]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestNameByEmail]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameById]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestNameById]
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 04/26/2012 01:50:50 ******/
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]
GO
DROP TABLE [dbo].[source_system_link]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_retrieve]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest_retrieve]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_update]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest_update]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsForScheduledItems]    Script Date: 04/26/2012 01:51:01 ******/
DROP PROCEDURE [dbo].[usp_getGuestsForScheduledItems]
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_create]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_xband_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_update]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_guest_xband_update]
GO
/****** Object:  Table [dbo].[guest]    Script Date: 04/26/2012 01:50:46 ******/
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [FK_guest_IDMS_Type]
GO
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [DF_guest_GGID]
GO
DROP TABLE [dbo].[guest]
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 04/26/2012 01:50:47 ******/
DROP TABLE [dbo].[guest_scheduledItem]
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 04/26/2012 01:50:49 ******/
DROP TABLE [dbo].[scheduledItem]
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 04/26/2012 01:50:49 ******/
DROP TABLE [dbo].[scheduleItemDetail]
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 04/26/2012 01:50:48 ******/
DROP TABLE [dbo].[IDMS_Type]
GO
/****** Object:  StoredProcedure [dbo].[usp_IDMSHello]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_IDMSHello]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_RethrowError]
GO
/****** Object:  StoredProcedure [dbo].[usp_saveXband]    Script Date: 04/26/2012 01:51:02 ******/
DROP PROCEDURE [dbo].[usp_saveXband]
GO
/****** Object:  Table [dbo].[xband]    Script Date: 04/26/2012 01:50:50 ******/
DROP TABLE [dbo].[xband]
GO
/****** Object:  User [EMUser]    Script Date: 04/26/2012 01:51:02 ******/
DROP USER [EMUser]
GO
/****** Object:  User [EMUser]    Script Date: 04/26/2012 01:51:02 ******/
CREATE USER [EMUser] FOR LOGIN [EMUser] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  Table [dbo].[xband]    Script Date: 04/26/2012 01:50:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[xband](
	[xbandId] [bigint] IDENTITY(1,1) NOT NULL,
	[bandId] [nvarchar](200) NULL,
	[longRangeId] [nvarchar](200) NULL,
	[tapId] [nvarchar](200) NULL,
	[secureId] [nvarchar](200) NULL,
	[UID] [nvarchar](200) NULL,
	[bandFriendlyName] [nvarchar](50) NULL,
	[printedName] [nvarchar](255) NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[secureid_encrypted] [varbinary](128) NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_secureId] UNIQUE NONCLUSTERED 
(
	[secureId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_tapId] UNIQUE NONCLUSTERED 
(
	[tapId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
CREATE NONCLUSTERED INDEX [IX_longRangeId] ON [dbo].[xband] 
(
	[longRangeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_xband_encrypted_secureid] ON [dbo].[xband] 
(
	[secureid_encrypted] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_saveXband]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/28/2012
-- Description:	Save a new xband
-- =============================================
CREATE PROCEDURE [dbo].[usp_saveXband] (
	 @bandId nvarchar(200) = NULL,
	 @longRangeId nvarchar(200) = NULL,
	 @tapId nvarchar(200) = NULL,
	 @secureId nvarchar(200) = NULL,
	 @uid nvarchar(200) = NULL,
	 @bandFriendlyName nvarchar(200) = NULL,
	 @printedName nvarchar(200) = NULL,
	 @active bit = 1,
	 @createdBy nvarchar(50) = NULL)
	   
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	---- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @Fields varchar(MAX)
	DECLARE @Values varchar(MAX)
	
	SET @Fields = ''
	SET @Values = ''
	
	if @bandId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + 'bandId, '
		SET @Values = @Values + '''' + @bandId + ''', '
	END
	
	
	if @longRangeId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + 'longRangeId, '
		SET @Values = @Values + '''' + @longRangeId + ''', '
	END
	
	if @tapId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + 'tapId, '
		SET @Values = @Values +  '''' +@tapId + ''', '
	END
	
	if @secureId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + 'secureId, '
		SET @Values = @Values + '''' + @secureId + ''', '
	END
	
	if @uid IS NOT NULL
	begin
		SET @FIELDS = @Fields + '[UID], '
		SET @Values = @Values + '''' + @uid + ''', '
	end
	
	if @bandFriendlyName IS NOT NULL
	begin
		SET @Fields = @Fields + 'bandFriendlyName, '
		SET @Values = @Values + '''' + @bandFriendlyName + ''', '
	end
	
	if @printedName IS NOT NULL
	begin
		SET @Fields = @Fields + 'printedName, '
		SET @Values = @Values +  '''' +@printedName + ''', '
	end
	
	
	if @active IS NOT NULL
	BEGIN
		if  @active = 1
		BEGIN
		SET @Fields = @Fields + 'active, '
		SET @Values = @Values + '1, '
		END
		else
		BEGIN
		SET @Fields = @Fields + 'active, '
		SET @Values = @Values + '0, '
		END
		
	END
	ELSE
	BEGIN
		SET @Fields = @Fields + 'active, '
		SET @Values = @Values + '1, '
	END
	
	
	if @createdBy IS NOT NULL
	BEGIN
		SET @Fields = @Fields + 'createdBy, '
		SET @Fields = @Fields + 'createdDate, '
		SET @Fields = @Fields + 'updatedBy, '
		SET @Fields = @Fields + 'updatedDate '
		SET @Values = @Values + '''' + @createdBy + ''', '
		SET @Values = @Values + '''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + ''', '
		SET @Values = @Values + '''' + @createdBy + ''', '
		SET @Values = @Values + '''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + ''''
	END
	ELSE
	BEGIN
		SET @createdBy = 'IDMS'
		SET @Fields = @Fields + 'createdBy, '
		SET @Fields = @Fields + 'createdDate, '
		SET @Fields = @Fields + 'updatedBy, '
		SET @Fields = @Fields + 'updatedDate '
		SET @Values = @Values + '''' + @createdBy + ''', '
		SET @Values = @Values + '''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + ''', '
		SET @Values = @Values + '''' + @createdBy + ''', '
		SET @Values = @Values + '''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + ''''
	END
	
	
	print 'Output:'
	--DECLARE @PrintMessage nvarchar(200)
	--SET @PrintMessage = 'Test'
	--SET @PrintMessage = @PrintMessage + ' again.'
	--print @PrintMessage
	

	print @Fields
	print @Values
	
	DECLARE @execStatement nvarchar(MAX)
	SET @execStatement = 'INSERT INTO xband (' + @Fields + ') VALUES (' + @Values + ')'
	print @execStatement
    
	exec (@execStatement)
	
	return @@IDENTITY
	
END
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 01/25/2012
-- Description:	Rethrows an Error.
-- =============================================
CREATE PROCEDURE [dbo].[usp_RethrowError] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Return if there is no error information to retrieve.
    IF ERROR_NUMBER() IS NULL
        RETURN;

    DECLARE 
        @ErrorMessage    NVARCHAR(4000),
        @ErrorNumber     INT,
        @ErrorSeverity   INT,
        @ErrorState      INT,
        @ErrorLine       INT,
        @ErrorProcedure  NVARCHAR(200);

    -- Assign variables to error-handling functions that 
    -- capture information for RAISERROR.
    SELECT 
        @ErrorNumber = ERROR_NUMBER(),
        @ErrorSeverity = ERROR_SEVERITY(),
        @ErrorState = ERROR_STATE(),
        @ErrorLine = ERROR_LINE(),
        @ErrorProcedure = ISNULL(ERROR_PROCEDURE(), '-');

    -- Build the message string that will contain original
    -- error information.
    SELECT @ErrorMessage = 
        N'Error %d, Level %d, State %d, Procedure %s, Line %d, ' + 
            'Message: '+ ERROR_MESSAGE();

    -- Raise an error: msg_str parameter of RAISERROR will contain
    -- the original error information.
    RAISERROR 
        (
        @ErrorMessage, 
        @ErrorSeverity, 
        1,               
        @ErrorNumber,    -- parameter: original error number.
        @ErrorSeverity,  -- parameter: original error severity.
        @ErrorState,     -- parameter: original error state.
        @ErrorProcedure, -- parameter: original error procedure name.
        @ErrorLine       -- parameter: original error line number.
        );

END
GO
/****** Object:  StoredProcedure [dbo].[usp_IDMSHello]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/21/2012
-- Description:	A Ping Hello
-- =============================================
CREATE PROCEDURE [dbo].[usp_IDMSHello] 
	-- Add the parameters for the stored procedure here

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SELECT 'HELLO';
END
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 04/26/2012 01:50:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDMS_Type](
	[IDMSTypeId] [int] IDENTITY(1,1) NOT NULL,
	[IDMSTypeName] [nvarchar](50) NULL,
	[IDMSTypeValue] [nvarchar](50) NULL,
	[IDMSkey] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_IDMS_Type] PRIMARY KEY CLUSTERED 
(
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 04/26/2012 01:50:49 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[scheduleItemDetail](
	[itemDetailId] [bigint] IDENTITY(1,1) NOT NULL,
	[scheduledItemId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduleItemDetail] PRIMARY KEY CLUSTERED 
(
	[itemDetailId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 04/26/2012 01:50:49 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[scheduledItem](
	[scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[externalId] [nvarchar](50) NULL,
	[IDMSTypeId] [int] NULL,
	[startDateTime] [datetime] NULL,
	[endDateTime] [datetime] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 04/26/2012 01:50:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_scheduledItem](
	[guest_scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[scheduledItemId] [bigint] NULL,
	[isOwner] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[guest_scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest]    Script Date: 04/26/2012 01:50:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest](
	[guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[IDMSID] [uniqueidentifier] ROWGUIDCOL  NOT NULL CONSTRAINT [DF_guest_GGID]  DEFAULT (newid()),
	[IDMSTypeId] [int] NULL,
	[lastName] [nvarchar](200) NULL,
	[firstName] [nvarchar](200) NULL,
	[middleName] [nvarchar](200) NULL,
	[title] [nvarchar](50) NULL,
	[suffix] [nvarchar](50) NULL,
	[DOB] [date] NULL,
	[VisitCount] [int] NULL,
	[AvatarName] [nvarchar](50) NULL,
	[active] [bit] NULL,
	[emailAddress] [nvarchar](200) NULL,
	[parentEmail] [nvarchar](200) NULL,
	[countryCode] [nvarchar](3) NULL,
	[languageCode] [nvarchar](3) NULL,
	[gender] [nvarchar](1) NULL,
	[userName] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_emailAddress] ON [dbo].[guest] 
(
	[emailAddress] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_firstname] ON [dbo].[guest] 
(
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_lastname] ON [dbo].[guest] 
(
	[lastName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_lastname_firstname] ON [dbo].[guest] 
(
	[lastName] ASC,
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_update]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest band association.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_update] 
	@guestId bigint,
	@xbandid bigint,
	@active bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		UPDATE [IDMS].[dbo].[guest_xband]
		   SET [updatedBy] = 'IDMS'
			  ,[updatedDate] = GETUTCDATE()
			  ,[active] = @active
		 WHERE	[guestid] = @guestid
		 AND	[xbandid] = @xbandid

	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_create]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates an xband.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_create] 
	@xbandId bigint OUTPUT,
	@bandid nvarchar(200),
	@longRangeId nvarchar(200),
	@tapId nvarchar(200),
	@secureId nvarchar(200),
	@UID nvarchar(200),
	@bandFriendlyName nvarchar(50) = NULL,
	@printedName nvarchar(255) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		INSERT INTO [dbo].[xband]
			([bandId]
			,[longRangeId]
			,[tapId]
			,[secureId]
			,[UID]
			,[bandFriendlyName]
			,[printedName]
			,[active]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate])
		VALUES
			(@bandId
			,@longRangeId
			,@tapId
			,@secureId
			,@UID
			,@bandFriendlyName
			,@printedName
			,1
			,N'IDMS'
			,GETUTCDATE()
			,N'IDMS'
			,GETUTCDATE())

			
		SELECT @xbandId = @@IDENTITY 
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsForScheduledItems]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		usp_getGuestScheduledItems
-- Create date: 3/20/2012
-- Description:	Get scheduled items by guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestsForScheduledItems] 
	-- Add the parameters for the stored procedure here
	@scheduledItemId bigint 

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select * from guest_scheduledItem  Where scheduledItemId=@scheduledItemId;
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_update]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/02/2012
-- Description:	Updates a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_update] (
	@guestId bigint,
	@guestType nvarchar(50) = NULL,
	@lastname nvarchar(200) = NULL,
	@firstname nvarchar(200) = NULL,
	@DOB date = NULL,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL,
	@active bit = NULL)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
	
		--TODO: Check IDMSTypeID to make sure type is guest type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	([IDMSTypeName] = @guestType
		AND		[IDMSKey] = 'GUESTTYPE')
		
		IF @guestType IS NOT NULL
		BEGIN
		
			UPDATE [dbo].[guest]
			   SET [IDMSTypeId] = @IDMSTypeID
			 WHERE [guestId] = @guestId

		END
		
		IF @lastname IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [lastname] = @lastname
			 WHERE [guestId] = @guestId
		END

		IF @firstname IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [firstname] = @firstname
			 WHERE [guestId] = @guestId
		END

		IF @middlename IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [middlename] = @middlename
			 WHERE [guestId] = @guestId
		END

		IF @title IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [title] = @title
			 WHERE [guestId] = @guestId
		END

		IF @suffix IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [suffix] = @suffix
			 WHERE [guestId] = @guestId
		END
		
		IF @DOB IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [DOB] = @DOB
			 WHERE [guestId] = @guestId
		END

		IF @visitCount IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [visitCount] = @visitCount
			 WHERE [guestId] = @guestId
		END

		IF @avatarName IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [avatarName] = @avatarName
			 WHERE [guestId] = @guestId
		END

		IF @emailAddress IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [emailAddress] = @emailAddress
			 WHERE [guestId] = @guestId
		END

		IF @parentEmail IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [parentEmail] = @parentEmail
			 WHERE [guestId] = @guestId
		END

		IF @countryCode IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [countryCode] = @countryCode
			 WHERE [guestId] = @guestId
		END

		IF @languageCode IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [languageCode] = @languageCode
			 WHERE [guestId] = @guestId
		END

		IF @gender IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [gender] = @gender
			 WHERE [guestId] = @guestId
		END

		IF @userName IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [userName] = @userName
			 WHERE [guestId] = @guestId
		END


		UPDATE [dbo].[guest]
		SET  [updatedBy] = 'IDMS'
			,[updatedDate] = GETUTCDATE()
		WHERE [guestId] = @guestId

		COMMIT TRANSACTION
			
	END TRY
	BEGIN CATCH
	
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_retrieve]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/02/2012
-- Description:	Retrieves a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve] 
	@guestId bigint = NULL,
	@emailAddress nvarchar(200) = NULL,
	@search nvarchar(200) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		SELECT g.[guestId]
			  ,g.[IDMSID]
			  ,g.[IDMSTypeId]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB]
			  ,g.[VisitCount]
			  ,g.[AvatarName]
			  ,g.[active]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,g.[gender]
			  ,g.[userName]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g
		  WHERE (g.[guestId] = @guestId AND @guestID IS NOT NULL)
		  OR (g.[emailAddress] = @emailAddress AND @emailAddress IS NOT NULL)
		  OR ((g.[lastname] Like '%' + @search + '%' OR
		        g.[firstname] Like '%' + @search + '%') AND @search IS NOT NULL)

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 04/26/2012 01:50:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[source_system_link](
	[sourceTypeId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[sourceSystemIdValue] [nvarchar](200) NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_source_type] PRIMARY KEY CLUSTERED 
(
	[sourceTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_source_system_link_guestid] ON [dbo].[source_system_link] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_source_system_link_sourceSystemIdValue] ON [dbo].[source_system_link] 
(
	[sourceSystemIdValue] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	get a GuestName object by guestId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestNameById]
	-- Add the parameters for the stored procedure here
	@guestId bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT top 1 firstName, lastName, middleName, guestId FROM guest where guestId=@guestId and active=1 order by createdDate desc
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameByEmail]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	get a GuestName object by guestId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestNameByEmail] 
	-- Add the parameters for the stored procedure here
	@emailAddress nvarchar(250)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT TOP 1 firstName, lastName, middleName, guestId FROM guest where emailAddress=@emailAddress and active=1 order by createdDate desc
END
GO
/****** Object:  Table [dbo].[party]    Script Date: 04/26/2012 01:50:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[party](
	[partyId] [bigint] IDENTITY(1,1) NOT NULL,
	[primaryGuestId] [bigint] NULL,
	[partyName] [nvarchar](200) NULL,
	[count] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party] PRIMARY KEY CLUSTERED 
(
	[partyId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_deactivateGuestById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Delete (mark as inactive) a guest object by ObjectId
-- =============================================
CREATE PROCEDURE [dbo].[usp_deactivateGuestById] 
	-- Add the parameters for the stored procedure here
	@guestId bigint

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	UPDATE guest SET active=0 where guestId=@guestId;
END
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 04/26/2012 01:50:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_xband](
	[guest_xband_id] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[xbandId] [bigint] NOT NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[active] [bit] NOT NULL,
 CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guest_xband_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_xband_guestid] ON [dbo].[guest_xband] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_xband_xbandid] ON [dbo].[guest_xband] 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 04/26/2012 01:50:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_phone](
	[guest_phoneId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[extension] [nvarchar](50) NULL,
	[phonenumber] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_phone] PRIMARY KEY CLUSTERED 
(
	[guest_phoneId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 04/26/2012 01:50:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_address](
	[guest_addressId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[IDMStypeId] [int] NULL,
	[address1] [nvarchar](200) NULL,
	[address2] [nvarchar](200) NULL,
	[address3] [nvarchar](200) NULL,
	[city] [nvarchar](100) NULL,
	[state] [nvarchar](3) NULL,
	[countryCode] [nvarchar](3) NULL,
	[postalCode] [nvarchar](12) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_info] PRIMARY KEY CLUSTERED 
(
	[guest_addressId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 04/26/2012 01:50:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[celebration](
	[celebrationId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[message] [nvarchar](max) NULL,
	[dateStart] [date] NULL,
	[dateEnd] [date] NULL,
	[active] [bit] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_celebration] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 04/26/2012 01:50:49 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[party_guest](
	[party_guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[partyId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party_guest] PRIMARY KEY CLUSTERED 
(
	[party_guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[getGuestCelebrationsById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/15/2012
-- Description:	Get the guest celebrations by guestId
-- =============================================
CREATE PROCEDURE [dbo].[getGuestCelebrationsById] 
	-- Add the parameters for the stored procedure here
	@guestId BigInt = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select * from celebration Join IDMS_Type on celebration.IDMSTypeId = IDMS_Type.IDMSTypeId where celebration.guestId =@guestId;
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_delete]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Deletes a guest band association.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_delete] 
	@guestId bigint,
	@xbandid bigint,
	@active bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DELETE FROM [dbo].[guest_xband]
		WHERE	[guestid] = @guestid
		AND		[xbandid] = @xbandid
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_create]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest band association.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_create] 
	@guestId bigint,
	@xbandid bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		INSERT INTO [dbo].[guest_xband]
			([guestId]
			,[xbandId]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate]
			,[active])
		VALUES
			(@guestid
			,@xbandid
			,N'IDMS'
			,GETUTCDATE()
			,N'IDMS'
			,GETUTCDATE()
			,1)
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateTestUser] 
	-- Add the parameters for the stored procedure here
	@NumberOfUsers int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @FirstNamesCount int
	DECLARE @LastNamesCount int
	DECLARE @FirstNameID int
	DECLARE @LastNameID int
	DECLARE @LastName nvarchar(200)
	DECLARE @FirstName nvarchar(200)
	DECLARE @GuestID bigint
	DECLARE @xBandID bigint
	DECLARE @BandID nvarchar(16)
	DECLARE @TapID nvarchar(16)
	DECLARE @LongRangeID nvarchar(16)
	DECLARE @IntVal bigint
	
	DECLARE @Index int

	SET @Index = 0
	
	SELECT @FirstNamesCount = COUNT(*) FROM [dbo].[FirstNames]
	
	SELECT @LastNamesCount = COUNT(*) FROM [dbo].[LastNames]
	
	WHILE @Index < @NumberOfUsers 
	BEGIN
	
		SET @FirstNameID = CAST(RAND() * @FirstNamesCount as int) + 1
		SET @LastNameID = CAST(RAND() * @LastNamesCount as int) + 1
		
		SELECT @FirstName = [Name] FROM [dbo].[FirstNames] WHERE [ID] = @FirstNameID
		SELECT @LastName = [Name] FROM [dbo].[LastNames] WHERE [ID] = @LastNameID
		
		IF @FirstName IS NULL
		BEGIN
			print 'First name ID not found'
		END
	
		IF @LastName IS NULL
		BEGIN
			print 'Last name ID not found'
		END
		
		DECLARE @HexString nvarchar(16)
		DECLARE @IntValue bigint
		SET @HexString = '0123456789ABCDEF'
		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @BandID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @LongRangeID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   'FF' +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @TapID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   'F0' + 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		IF NOT EXISTS (SELECT 'X' FROM [dbo].[xband] where [longRangeId] = @LongRangeID OR [tapId] = @tapID)
		BEGIN
			BEGIN TRANSACTION

			BEGIN TRY
					
				INSERT INTO [dbo].[xband]
				   ([bandId]
				   ,[longRangeId]
				   ,[tapId]
				   ,[secureId]
				   ,[UID]
				   ,[bandFriendlyName]
				   ,[printedName]
				   ,[active]
				   ,[createdBy]
				   ,[createdDate]
				   ,[updatedBy]
				   ,[updatedDate])
				VALUES
				   (@BandID
				   ,@LongRangeID
				   ,@TapID
				   ,NULL
				   ,NULL
				   ,@FirstName + ' ' + @LastName + '''s band'
				   ,NULL
				   ,1
				   ,'simulator'
				   ,GETUTCDATE()
				   ,'simulator'
				   ,GETUTCDATE())

				SELECT @xBandID = @@IDENTITY
			
				INSERT INTO [dbo].[guest]
				   ([IDMSID]
				   ,[IDMSTypeId]
				   ,[lastName]
				   ,[firstName]
				   ,[DOB]
				   ,[VisitCount]
				   ,[AvatarName]
				   ,[active]
				   ,[createdBy]
				   ,[createdDate]
				   ,[updatedBy]
				   ,[updatedDate])
				VALUES
				   (NEWID()
				   ,9
				   ,@FirstName
				   ,@LastName
				   ,NULL
				   ,CAST(RAND() * 100 as int)
				   ,NULL
				   ,1
				   ,'simulator'
				   ,GETUTCDATE()
				   ,'simulator'
				   ,GETUTCDATE())

				SELECT @GuestID = @@IDENTITY
				
				INSERT INTO [IDMS].[dbo].[guest_xband]
					   ([guestId]
					   ,[xbandId]
					   ,[createdBy]
					   ,[createdDate]
					   ,[updatedBy]
					   ,[updatedDate]
					   ,[active])
				 VALUES
					   (@GuestID
					   ,@xBandID
					   ,'simulator'
					   ,GETUTCDATE()
					   ,'simulator'
					   ,GETUTCDATE()
					   ,1)

			INSERT INTO [dbo].[source_system_link]
					   ([guestId]
					   ,[sourceSystemIdValue]
					   ,[IDMSTypeId]
					   ,[createdBy]
					   ,[createdDate]
					   ,[updatedBy]
					   ,[updatedDate])
				 VALUES
					   (@GuestId
					   ,REPLACE(CAST(NEWID() as nvarchar(200)),'-','')
					   ,9
					   ,'simulator'
					   ,GETUTCDATE()
					   ,'simulator'
					   ,GETUTCDATE())
			
				INSERT INTO [dbo].[xband_type]
						   ([xbandId]
						   ,[isPhysical])
					 VALUES
						   (@xBandID
						   ,0)

				COMMIT TRANSACTION
				
			END TRY
			BEGIN CATCH
			
				ROLLBACK TRANSACTION

			END CATCH
			
		
			SET @Index = @Index + 1
		END

	END

END
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateGuestIdentifier]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/23/2012
-- Description:	Creates a Guest Identifier 
--              associated with the guestid.
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateGuestIdentifier] 
	@guestId bigint,
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = 'SOURCESYSTEM'

		--IF identifier and type already exits for the guest, nothing needs to be done.
		IF NOT EXISTS (SELECT 'X' FROM [dbo].[source_system_link] WHERE [IDMSTypeID] = @IDMSTypeID AND [guestId] = @guestId AND [sourceSystemIdValue] = @sourceSystemIdValue)
		BEGIN

			INSERT INTO [dbo].[source_system_link]
				([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
			VALUES
				(@guestid, @sourceSystemIdValue, @IDMSTypeID,N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())
		END

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdFromSourceTypeId]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	get the guestId for a source system linkId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestIdFromSourceTypeId] 
	-- Add the parameters for the stored procedure here
	@sourceTypeIdValue nvarchar(50),
	@IDMSTypeName nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT guestId FROM source_system_link JOIN IDMS_TYPE ON IDMS_Type.IDMSTypeId = source_system_link.IDMSTypeId WHERE sourceSystemIDValue =@sourceTypeIdValue and IDMS_TYPE.IDMSTypeName=@IDMSTypeName;
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdentifiersById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Retrieve all identifiers for a guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestIdentifiersById] 
	-- Add the parameters for the stored procedure here
	@guestId bigint
	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select 
	source_system_link.*,
	 IDMS_Type.IDMSTypeName as [type],
	 source_system_link.sourceSystemIdValue as value,
	 source_system_link.guestId as guestId 
	from source_system_link JOIN IDMS_Type on source_system_link.IDMSTypeId = IDMS_Type.IDMSTypeId where source_system_link.guestId = @guestId AND IDMSKEY= 'SOURCESYSTEM'
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestCelebrationsById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/15/2012
-- Description:	Get the guest celebrations by guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestCelebrationsById] 
	-- Add the parameters for the stored procedure here
	@guestId BigInt = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select * from celebration Join IDMS_Type on celebration.IDMSTypeId = IDMS_Type.IDMSTypeId where celebration.guestId =@guestId;
END
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_create]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a source system 
--              link record.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_create] 
	@guestId bigint,
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		--TODO: Check IDMSTypeID to make sure type is source system type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = 'SOURCESYSTEM'

	     --,19 -- XID 
	     --REPLACE(NEWID(),'-','')
		INSERT INTO [dbo].[source_system_link]
			([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestid, @sourceSystemIdValue, @IDMSTypeID,N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest__addresss_create]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Adds an address to a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest__addresss_create] 
	@guestId bigint,
	@addressType nvarchar(50),
	@address1 nvarchar(200),
	@address2 nvarchar(200) = NULL,
	@address3 nvarchar(200) = NULL,
	@city nvarchar(200),
	@state nvarchar(200),
	@countryCode nvarchar(3) = NULL,
	@postalCode nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	--Ignore invalid requests instead of setting at the parameter level
	--so that the stored procedure can be called when creating a guest
	--when no address data is provided.
	IF @addressType IS NULL OR 	@address1 IS NULL OR
	   @city IS NULL OR @state IS NULL OR 
	   @postalCode  IS NULL
	BEGIN
		RETURN;
	END
		   
	BEGIN TRY
	
		--TODO: Only in Caller??
		--BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @addressType
		AND		[IDMSKey] = 'ADDRESSTYPE'
		
		--TODO: Throw error if null or create a new item???
		
		INSERT INTO [dbo].[guest_address]
			([guestId]
			,[IDMStypeId]
			,[address1]
			,[address2]
			,[address3]
			,[city]
			,[state]
			,[countryCode]
			,[postalCode]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate])
	     VALUES
			(@guestId
			,@IDMSTypeID
			,@address1
			,@address2
			,@address3
			,@city
			,@state
			,@countryCode
			,@postalCode
			,N'IDMS'
			,GETUTCDATE()
			,N'IDMS'
			,GETUTCDATE())

		--TODO: Only in Caller??
		--COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		--TODO: Only in Caller??
		--ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByIdentifier]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using and indentifier value.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetXBandsByIdentifier] 
	@sourceSystemIDValue nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,x.[secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	FROM (SELECT [guestid]
	FROM	[dbo].[source_system_link]
	WHERE [sourceSystemIdValue] = @Sourcesystemidvalue) t
	JOIN [dbo].[guest_xband] gx on gx.[guestid] = t.[guestid]
	JOIN [dbo].[xband] x on x.[xbandid] = gx.[xbandid]

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByGuestID]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetXBandsByGuestID] 
	@guestId bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,x.[UID]
		  ,x.[secureid]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	FROM [dbo].[guest_xband] gx
	JOIN [dbo].[xband] x on x.[xbandid] = gx.[xbandid]
	WHERE gx.[guestid] = @guestid

END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetIdentifiersByIdentifier]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the identifiers for a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetIdentifiersByIdentifier] 
	@sourceSystemIDValue nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT
	 it.IDMSTypeName as [type],
	 s.sourceSystemIdValue as value,
	 s.guestId as guestId, 
	  it.[IDMSTypeName], s.[sourceSystemIdValue], s.[guestId]
	FROM (SELECT	[guestid]
	  FROM	[dbo].[source_system_link]
	  WHERE [sourceSystemIdValue] = @Sourcesystemidvalue) t
	JOIN [dbo].[source_system_link] s on s.[guestid] = t.[guestid]
	JOIN [dbo].[IDMS_Type] it on it.[IDMSTypeID] = s.[IDMSTypeID]
	AND it.[IDMSKey] = 'SOURCESYSTEM'

END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsByXbandId]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get guests assigned to an Xband by XbandId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestsByXbandId] 
	-- Add the parameters for the stored procedure here
	@xbandId bigint 
	  
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
    
    SELECT * from guest 
    JOIN guest_xband on guest_xband.guestId = guest.guestId
    JOIN xband on guest_xband.xbandId = xband.xbandId
    where xband.xbandId=@xbandId;
    


END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_create]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_create] 
	@guestId bigint OUTPUT,
	@guestType nvarchar(50),
	@lastname nvarchar(200),
	@firstname nvarchar(200),
	@DOB date,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL,
	@addressType nvarchar(50) = NULL,
	@address1 nvarchar(200) = NULL,
	@address2 nvarchar(200) = NULL,
	@address3 nvarchar(200) = NULL,
	@city nvarchar(200) = NULL,
	@state nvarchar(200) = NULL,
	@postalCode nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--TODO: Check IDMSTypeID to make sure type is guest type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @guestType
		AND		[IDMSKey] = 'GUESTTYPE'

		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSID],[IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(NEWID(),@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		--Create the XID
		DECLARE @sourceSystemIdValue nvarchar(200)
		DECLARE @sourceSystemIdType nvarchar(50)
		
		SET @sourceSystemIdValue = REPLACE(NEWID(),'-','')
		SET @sourceSystemIdType = 'xid'

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @guestId = @guestId
		  ,@sourceSystemIdValue = @sourceSystemIdValue
		  ,@sourceSystemIdType = @sourceSystemIdType

		--Add address
		EXECUTE [dbo].[usp_guest__addresss_create] 
		   @guestId = @guestId
		  ,@addressType = @addressType
		  ,@address1 = @address1
		  ,@address2 = @address2
		  ,@address3 = @address3
		  ,@city = @city 
		  ,@state = @state
		  ,@countryCode = @countryCode
		  ,@postalCode = @postalCode

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestProfileById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/15/2012
-- Description:	Get a guest profile by guestId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestProfileById]
	-- Add the parameters for the stored procedure here
	@guestId bigint
AS
BEGIN

	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select top 1
	guest.*,
	guest.DOB as dateOfBirth,
	party_guest.partyId as partyId
	
	
	from guest JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId LEFT JOIN party_guest on guest.guestId = party_guest.guestId where guest.guestId=@guestId and guest.active=1 ORDER BY party_guest.createdDate desc;
	SELECT * FROM xband JOIN guest_xband on guest_xband.xbandId=xband.xbandId where guest_xband.guestId=@guestId;
	SELECT * FROM celebration where guestId=@guestId;
	SELECT
	 source_system_link.*,
	 IDMS_Type.IDMSTypeName as [type],
	 source_system_link.sourceSystemIdValue as value,
	 source_system_link.guestId as guestId
	FROM source_system_link JOIN IDMS_Type on source_system_link.IDMSTypeId = IDMS_Type.IDMSTypeId where source_system_link.guestId=@guestId;
	
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByXBandId]    Script Date: 04/26/2012 01:51:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get and XBand by it's XBandId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByXBandId] 
	-- Add the parameters for the stored procedure here
	@xbandId bigint
	  
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
    
    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	 where x.[xbandId] = @xbandId;
    	
	Select 
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where guest_xband.xbandId=@xbandId
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByUID]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByUID] 
	-- Add the parameters for the stored procedure here
	@uid nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT x.[xbandId]
	,x.[bandId]
	,x.[longRangeId]
	,x.[tapId]
	,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) as [secureid]
	,x.[UID]
	,x.[bandFriendlyName]
	,x.[printedName]
	,x.[active]
	,x.[createdBy]
	,x.[createdDate]
	,x.[updatedBy]
	,x.[updatedDate]
	FROM [dbo].[xband] x
	WHERE x.[uid] = @uid;
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.uid=@uid 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByTapId]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get an xband by a tap (short range) id.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByTapId] 
	-- Add the parameters for the stored procedure here
	@tapId nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	 SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	where x.[tapId] = @tapId;
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.tapId=@tapId
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandBySecureId]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandBySecureId] 
	-- Add the parameters for the stored procedure here
	@secureId nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

   --F070A6FC07E200
   
	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
--		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) as [secureid]
		  ,x.[secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	  --where CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) = @secureId
	  where x.[secureid] = @secureId
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		--where CONVERT(nvarchar,DecryptByKey(secureid_Encrypted, 1, HashBytes('SHA1', CONVERT(varbinary, xband.xbandid)))) = @secureId
		where xband.[secureid] = @secureId
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByLRId]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByLRId] 
	-- Add the parameters for the stored procedure here
	@lrid nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	 SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,CONVERT(nvarchar,DecryptByKey(secureid_Encrypted, 1, HashBytes('SHA1', CONVERT(varbinary, x.xbandid)))) as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	  where x.[longRangeId] = @lrid;
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.longRangeId=@lrid;
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByBandId]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/27/2012
-- Description:	Get an XBand by BandId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByBandId] 
	-- Add the parameters for the stored procedure here
	@bandId nvarchar(200) 

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes('SHA1', CONVERT(varbinary, x.[xbandid])))) as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	 where x.[bandId] = @bandId;
    	
	Select 
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.bandId=@bandId 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestById]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Retrieve a guest record by guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestById] 
	-- Add the parameters for the stored procedure here
	@guestId bigint 
	  
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select TOP 1 * from guest JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId LEFT JOIN party_guest on guest.guestId = party_guest.guestId where guest.guestId=@guestId AND guest.active=1 ORDER BY party_guest.createddate desc; 
	SELECT * FROM xband RIGHT JOIN guest_xband on guest_xband.xbandId=xband.xbandId where guest_xband.guestId=@guestId AND guest_xband.active=1;
	
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestByEmail]    Script Date: 04/26/2012 01:51:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Get a guest object by EmailAddress
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestByEmail] 
	-- Add the parameters for the stored procedure here
	@emailAddress nvarchar(100)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
    Select Top 1 * 
    from guest 
    JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId 
    LEFT JOIN party_guest on guest.guestId = party_guest.guestId
     where guest.emailAddress=@emailAddress 
     AND guest.active=1
     ORDER BY party_guest.createddate desc;
END
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 04/26/2012 01:50:45 ******/
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 04/26/2012 01:50:45 ******/
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 04/26/2012 01:50:46 ******/
ALTER TABLE [dbo].[guest]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest] CHECK CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_IDMS_Type] FOREIGN KEY([IDMStypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 04/26/2012 01:50:47 ******/
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 04/26/2012 01:50:48 ******/
ALTER TABLE [dbo].[party]  WITH CHECK ADD  CONSTRAINT [FK_party_guest] FOREIGN KEY([primaryGuestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[party] CHECK CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 04/26/2012 01:50:49 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_party] FOREIGN KEY([partyId])
REFERENCES [dbo].[party] ([partyId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_party]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 04/26/2012 01:50:50 ******/
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_system_id_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 04/26/2012 01:50:50 ******/
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_type_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_type_guest]
GO
