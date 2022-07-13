DECLARE @currentversion varchar(12)
DECLARE @updateversion varchar(12)
DECLARE @previousversion varchar(12)

set @updateversion = '1.0.0.0001'
set @previousversion = '1.0.0.0000'

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)

	
IF (@currentversion <> @previousversion and @currentversion <> @updateversion) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + @previousversion + ' or ' + @updateversion
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	GOTO update_end
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + @updateversion + ' started.'	
END

SET IDENTITY_INSERT [dbo].[IDMS_Type] ON

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 50)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(50,'Guest',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 51)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(51,'Puck',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

IF NOT EXISTS(SELECT 'X' FROM [dbo].[IDMS_Type] WHERE [IDMSTypeId] = 52)
	INSERT INTO [dbo].[IDMS_Type]
		([IDMSTypeID],[IDMSTypeName],[IDMSTypeValue],[IDMSkey],[createdBy],[createdDate],[updatedBy],[updatedDate])
	VALUES
		(52,'Cast Member',NULL,'BANDTYPE','IDMS',GETUTCDATE(),'IDMS',GETUTCDATE())

SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'IDMSTypeId'  and Object_ID = Object_ID(N'[xband]') )
	ALTER TABLE [dbo].[xband] ADD [IDMSTypeId] INT NOT NULL DEFAULT (50)

IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_xband_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[xband]'))
BEGIN
	ALTER TABLE [dbo].[xband]  WITH CHECK ADD  CONSTRAINT [FK_xband_IDMS_Type] FOREIGN KEY([IDMSTypeId])
	REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])

	ALTER TABLE [dbo].[xband] CHECK CONSTRAINT [FK_xband_IDMS_Type]
END

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_encrypted_secureid')
	DROP INDEX [IX_xband_encrypted_secureid] ON [dbo].[xband] WITH ( ONLINE = OFF )


IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_bandid')
	DROP INDEX [IX_xband_bandid] ON [dbo].[xband] WITH ( ONLINE = OFF )


IF  EXISTS (SELECT * from sys.columns where Name = N'secureid_encrypted'  and Object_ID = Object_ID(N'[xband]') )
	ALTER TABLE [dbo].[xband] DROP COLUMN [secureid_encrypted]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'AK_xband_bandId')
	ALTER TABLE [dbo].[xband] DROP CONSTRAINT [AK_xband_bandId]

ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [AK_xband_bandId] UNIQUE NONCLUSTERED 
(
	[bandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'PK_guest_xband')
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [PK_guest_xband]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest__addresss_create]') AND type in (N'P', N'PC'))
	DROP PROCEDURE [dbo].[usp_guest__addresss_create]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'PK_guest_xband')
	ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [PK_guest_xband]

ALTER TABLE [dbo].[guest_xband] ADD  CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC,
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

IF  EXISTS (SELECT * from sys.columns where Name = 'guest_xband_id' and Object_ID = Object_ID(N'[guest_xband]'))
	ALTER TABLE [dbo].[guest_xband] DROP COLUMN [guest_xband_id]

IF  NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'AK_guest_xband_xbandId')
	ALTER TABLE [dbo].[guest_xband] ADD  CONSTRAINT [AK_guest_xband_xbandId] UNIQUE NONCLUSTERED 
	(
		[xbandId] ASC
	)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'PK_source_type')
	ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [PK_source_type]

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'PK_source_system_link')
	ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [PK_source_system_link]

IF  EXISTS (SELECT * from sys.columns where Name = 'sourceTypeId' and Object_ID = Object_ID(N'[source_system_link]'))
	ALTER TABLE dbo.source_system_link
		DROP COLUMN sourceTypeId

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_sourceSystemIdValue')
DROP INDEX [IX_source_system_link_sourceSystemIdValue] ON [dbo].[source_system_link] WITH ( ONLINE = OFF )


ALTER TABLE dbo.source_system_link ALTER COLUMN sourceSystemIdValue NVARCHAR(200) NOT NULL

CREATE NONCLUSTERED INDEX [IX_source_system_link_sourceSystemIdValue] ON [dbo].[source_system_link] 
(
	[sourceSystemIdValue] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]


IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_guestid')
DROP INDEX [IX_source_system_link_guestid] ON [dbo].[source_system_link] WITH ( ONLINE = OFF )

ALTER TABLE dbo.source_system_link ALTER COLUMN IDMSTypeId INT NOT NULL

CREATE NONCLUSTERED INDEX [IX_source_system_link_guestid] ON [dbo].[source_system_link] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

ALTER TABLE [dbo].[source_system_link] ADD  CONSTRAINT [PK_source_system_link] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC,
	[sourceSystemIdValue] ASC,
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]

/****** Object:  UserDefinedFunction [dbo].[ufn_GetGuestId]    Script Date: 05/24/2012 11:20:35 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ufn_GetGuestId]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [dbo].[ufn_GetGuestId]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- =============================================
CREATE FUNCTION [dbo].[ufn_GetGuestId] 
(
	@identifierType NVARCHAR(200),
	@identifierValue NVARCHAR(50)
)
RETURNS BIGINT
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Result BIGINT
	
	IF @identifierType = ''guestid''
	BEGIN
		SET @Result = CONVERT(BIGINT,@identifierValue)
	END
	ELSE IF @identifierType = ''xbandid''
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest_xband] gx
		WHERE gx.[xbandid] = @identifierValue

	END
	ELSE
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[source_system_link] s
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE s.[sourceSystemIdValue] = @identifierValue
		AND	  i.[IDMSTypeName] = @identifierType
		AND   i.[IDMSKey] = ''SOURCESYSTEM''
	END
	
	-- Return the result of the function
	RETURN @Result

END
'

IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[vw_xband]'))
DROP VIEW [dbo].[vw_xband]

EXEC dbo.sp_executesql @statement = N'CREATE VIEW [dbo].[vw_xband]
AS
SELECT     x.xbandId, x.bandId, x.longRangeId, x.tapId, x.secureId, x.UID, x.bandFriendlyName, x.printedName, x.active, i.IDMSTypeName AS BandType, x.createdBy, 
                      x.createdDate, x.updatedBy, x.updatedDate
FROM         dbo.xband AS x INNER JOIN
                      dbo.IDMS_Type AS i ON i.IDMSTypeId = x.IDMSTypeId
'

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_retrieve_by_email]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_retrieve_by_email]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_search]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_search]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_scheduled_item_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_scheduled_item_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestProfileById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestProfileById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_identifiers_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_identifiers_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_name_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_name_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestByEmail]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestByEmail]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByBandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByBandId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByLRId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByLRId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandBySecureId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandBySecureId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByTapId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByTapId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByUID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByUID]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByXBandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByXBandId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xbands_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xbands_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_source_system_link_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_retrieve_by_guestId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_source_system_link_retrieve_by_guestId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_assign]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_assign]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_retrieve_by_guestId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_retrieve_by_guestId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_unassign]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_unassign]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetIdentifiersByIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetIdentifiersByIdentifier]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByGuestID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetXBandsByGuestID]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetXBandsByIdentifier]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestCelebrationsById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestCelebrationsById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestIdentifiersById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestIdentifiersById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestIdFromSourceTypeId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestIdFromSourceTypeId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateGuestIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateGuestIdentifier]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestsByXbandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestsByXbandId]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestUser]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateTestUser]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_source_system_link_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_xband_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_delete]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_xband_delete]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_xband_update]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getGuestCelebrationsById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[getGuestCelebrationsById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_update]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_deactivateGuestById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_deactivateGuestById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_name_retrieve_by_email]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_name_retrieve_by_email]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestBands]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateTestBands]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestNameByEmail]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestNameByEmail]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestNameById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestNameById]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_update]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestsForScheduledItems]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestsForScheduledItems]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_schema_version_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_schema_version_retrieve]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_IDMSHello]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_IDMSHello]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RethrowError]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RethrowError]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_saveXband]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_saveXband]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_create]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_delete]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_delete]

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_celebration_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_celebration_update]

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/21/2012
-- Description:	A Ping Hello
-- =============================================
CREATE PROCEDURE [dbo].[usp_IDMSHello] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SELECT N''HELLO'';
END
' 


EXEC dbo.sp_executesql @statement = N'-- =============================================
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
        @ErrorProcedure = ISNULL(ERROR_PROCEDURE(), ''-'');

    -- Build the message string that will contain original
    -- error information.
    SELECT @ErrorMessage = 
        N''Error %d, Level %d, State %d, Procedure %s, Line %d, '' + 
            ''Message: ''+ ERROR_MESSAGE();

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
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Creates a party.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_update]
	@partyId BIGINT,
	@partyName NVARCHAR(200),
	@primaryGuestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		UPDATE [dbo].[party]
			SET [partyName] = @partyName,
				[primaryGuestId] = @primaryGuestId,
				[updatedBy] = ''IDMS'',
				[updatedDate] = GETUTCDATE()
		WHERE [partyId] = @partyID
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Scott Salley
-- Create date: 06/01/2012
-- Description:	Retrieves a party using the 
--              party name.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_retrieve_by_name]
	@partyName nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @partyCount INT
		DECLARE @partyId BIGINT

        SELECT  @partyId = p.[partyId]
        FROM    [dbo].party p
        WHERE   p.[partyName] = @partyName
        
        
		SELECT	@partyCount = COUNT(*)
		FROM	[dbo].[party_guest] pg
		WHERE	pg.[partyId] = @partyId	
		
		SELECT @partyCount = @@ROWCOUNT 

		SELECT	 p.[partyId]
				,p.[primaryGuestId]
				,p.[partyName]
				,@partyCount AS [count]
		FROM	[dbo].[party] p
		WHERE	p.[partyId] = @partyId	


		SELECT	 pg.[guestId]
				,CASE WHEN pg.[guestId] = p.[primaryGuestId] THEN 1 ELSE 0 END AS [isPrimary]
		FROM	[dbo].[party_guest] pg
		JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE	pg.[partyId] = @partyId	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a party using the 
--              party ID.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_retrieve]
	@partyId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @partyCount INT
		
		SELECT	@partyCount = COUNT(*)
		FROM	[dbo].[party_guest] pg
		WHERE	pg.[partyId] = @partyId	
		
		SELECT @partyCount = @@ROWCOUNT 

		SELECT	 p.[partyId]
				,p.[primaryGuestId]
				,p.[partyName]
				,@partyCount AS [count]
		FROM	[dbo].[party] p
		WHERE	p.[partyId] = @partyId	


		SELECT	 pg.[guestId]
				,CASE WHEN pg.[guestId] = p.[primaryGuestId] THEN 1 ELSE 0 END AS [isPrimary]
		FROM	[dbo].[party_guest] pg
		JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE	pg.[partyId] = @partyId	

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Adds a guest to a party.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_guest_create]
	@partyId BIGINT,
	@guestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		IF NOT EXISTS(SELECT ''X'' FROM [dbo].[party_guest] WHERE [partyId] = @partyId AND [guestId] = @guestId)
		BEGIN
				
			DECLARE @IDMSTypeId INT
		
			SELECT @IDMSTypeId = [IDMSTypeId]
			FROM	[dbo].[IDMS_Type]
			WHERE	[IDMSTypeName] = ''guest party''
			AND		[IDMSkey] = ''PARTYTYPE''

			INSERT INTO [dbo].[party_guest]	([partyId], [guestId], [IDMSTypeId], 
				[createdBy], [createdDate], [updatedBy], [updatedDate])
			VALUES (@partyId, @guestId, @IDMSTypeId, ''IDMS'', GETUTCDATE(), ''IDMS'', GETUTCDATE())
		
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- Update date: 05/09/2012
-- Author:		Ted Crane
-- Description: Restore returning secureid.
--              Add BandType field.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xbands_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @guestId BIGINT
		
	SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

    SELECT x.*
	FROM [dbo].[vw_xband] x
	JOIN [dbo].[guest_xband] gx on gx.[xbandid] = x.[xbandid]
	WHERE gx.[guestid] = @guestid

END
' 

EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves an xband.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_retrieve] 
	@bandLookupType int,
	@id nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY

		-- Must match BandLookupType in Java code
		IF @bandLookupType = 0
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[xbandid] = @id
		END
		ELSE IF @bandLookupType = 1
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[bandid] = @id
		END	
		ELSE IF @bandLookupType = 2
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[longrangeid] = @id
		END	
		ELSE IF @bandLookupType = 3
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[tapid] = @id
		END	
		ELSE IF @bandLookupType = 4
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[secureid] = @id
		END	
		ELSE IF @bandLookupType = 5
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[uid] = @id
		END	
	           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 

EXEC dbo.sp_executesql @statement = N'

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
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
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


' 

EXEC dbo.sp_executesql @statement = N'

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
		AND		[IDMSKey] = ''SOURCESYSTEM''

	     --,19 -- XID 
	     --REPLACE(NEWID(),''-'','''')
		INSERT INTO [dbo].[source_system_link]
			([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestid, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 

EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Assigns an xband to a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign] 
	@xbandId BIGINT,
	@guestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--Make sure band is only assigned to one guest.
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandId] = @xbandId 
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = ''xband''
		AND		[IDMSKey] = ''SOURCESYSTEM''

		--Remove source system link for xband used by GxP
		DELETE FROM [dbo].[source_system_link]
		WHERE   [sourceSystemIdValue] = CONVERT(NVARCHAR(200),@xbandId)
		AND		[IDMSTypeId] = @IDMSTypeID
		
		--Create new association.
		INSERT INTO [dbo].[guest_xband] ([guestID], [xbandId], [active], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@guestId, @xbandId, 1, ''IDMS'', GETUTCDATE(), ''IDMS'', GETUTCDATE())		

		--Required for GxP, not sure why.
		EXECUTE [dbo].[usp_source_system_link_create] 
			@guestId = @guestId,
			@sourceSystemIdValue = @xbandId,
			@sourceSystemIdType = ''xband''          

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 

EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Get all the identifier key
--              value pairs for a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestID BIGINT

		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
	
		SELECT	 i.[IDMSTypeName] as [type]
				,s.[sourceSystemIdValue] as [value]
				,s.[guestId] as [guestId] 
		FROM [dbo].[source_system_link] s 
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId] 
		WHERE s.[guestId] = @guestId 
		AND i.[IDMSKEY] = ''SOURCESYSTEM''

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
	
END

' 

EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/14/2012
-- Description:	Update xband record
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_update] (
	 @xbandId BIGINT,
	 @active bit = 1,
	 @bandType NVARCHAR(50) = NULL)
	   
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	---- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @IDMSTypeID int
		
		IF @bandType IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[xband]
			WHERE	[xbandid] = @xbandid
		END
		ELSE
		BEGIN
		
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] 
			WHERE	[IDMSTypeName] = @bandType
			AND		[IDMSKey] = ''BANDTYPE''
		END
		
		UPDATE [dbo].[xband]
			SET [active] = @active,
				[IDMSTypeID] = 	@IDMSTypeID,
				[updatedby] = ''IDMS'',
				[updateddate] = GETUTCDATE()
		WHERE [xbandid] = @xbandId
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END

' 

EXEC dbo.sp_executesql @statement = N'

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
		   SET [updatedBy] = ''IDMS''
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


' 

EXEC dbo.sp_executesql @statement = N'

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


' 

EXEC dbo.sp_executesql @statement = N'

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
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
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


' 

EXEC dbo.sp_executesql @statement = N'

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
		AND		[IDMSKey] = ''GUESTTYPE'')
		
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
		SET  [updatedBy] = ''IDMS''
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


' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves guest by searching.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_search]
	@searchString NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @addressTypeId INT
		
		SELECT @addressTypeId = [IDMSTypeID]
		FROM	[dbo].[IDMS_Type] i
		WHERE	[IDMSTypeName] = ''HOME ADDRESS''
	
		SELECT TOP 50
				 g.[guestId]
				,p.[primaryGuestId]
				,p.[partyName]
				,p.[partyId]
				,(SELECT COUNT(*) FROM [dbo].[party_guest] pg1 WHERE pg1.[partyId] = p.[partyID]) AS [count]
				,g.[emailAddress]
				,g.[lastname]
				,g.[firstname]
				,a.[address1]
				,a.[address2]
				,a.[address3]
				,a.[city]
				,a.[state]
				,a.[postalCode]
		FROM	[dbo].[guest] g 
		LEFT JOIN [dbo].[guest_address] a on a.[guestId] = g.[guestId]
			AND a.[IDMSTypeID] = @addressTypeId 
		JOIN	[dbo].[IDMS_Type] i on i.[IDMSTypeId] = g.[IDMSTypeId] 
		LEFT JOIN	[dbo].[party_guest] pg on pg.[guestId] = g.[guestId] 
		LEFT JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE (g.firstName + '' '' + g.lastName) LIKE ''%'' + @searchString + ''%''
		ORDER BY g.[createdDate] DESC
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_retrieve_by_identifier] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
	
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT  c.*, i.* 
		FROM	[dbo].[celebration] c
		JOIN	[dbo].[IDMS_Type] i  on i.[IDMSTypeId] = c.[IDMSTypeId]
		WHERE	c.[guestId]= @guestId
		AND		c.[active] = 1
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a guest using and
--              identifier type and value.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
		
		DECLARE @partyId BIGINT
		
		SELECT	@partyID = [partyId]
		FROM	[dbo].[party_guest] pg
		WHERE   pg.[guestId] = @guestId
		AND		pg.[createdDate] = 
		(SELECT MAX(pg1.[createdDate])
		 FROM   [dbo].[party_guest] pg1
		 WHERE	pg1.[guestid] = @guestId)

		SELECT g.[guestId]
			  ,g.[IDMSID] AS [swid]
			  ,i.[IDMSTypeName] AS [guestType]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB] AS [dateOfBirth]
			  ,g.[VisitCount]
			  ,g.[AvatarName] AS [avatar]
			  ,CASE WHEN g.[active] = 1 THEN ''Active'' ELSE ''InActive'' END AS [status]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,CASE WHEN g.[gender] = ''M'' THEN ''MALE'' ELSE ''FEMALE'' END AS [gender]
			  ,g.[userName]
			  ,@partyId AS [partyId]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g
		  JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = g.[IDMSTypeId]
		  WHERE g.[guestId] = @guestId

		EXECUTE [dbo].[usp_xbands_retrieve]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_celebration_retrieve_by_identifier]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



' 

EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retrieves a guest
--				that has the specified
--              email address.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve_by_email]
	@emailAddress NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		DECLARE @guestId BIGINT

		SELECT  @guestId = g.[guestId]
		FROM	[dbo].[guest] g
		WHERE	g.[emailAddress] = @emailAddress
		AND		g.[active] = 1
		AND		g.[createddate] =
		(SELECT MAX(g1.[createddate])
		 FROM [dbo].[guest] g1
		 WHERE g1.[emailAddress] = g.[emailAddress]
		 AND   g1.[active] = 1)
		
		EXECUTE [dbo].[usp_guest_retrieve] 
			@identifierType = ''guestId'', 
			@identifierValue = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/14/2012
-- Description:	Retrieves the current schema
--              version.
-- =============================================
CREATE PROCEDURE [dbo].[usp_schema_version_retrieve]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
	SELECT TOP 1 [version]		
	FROM [dbo].[schema_version]
	ORDER BY [schema_version_id] DESC 

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves the itineraries for a 
--              guest using an identifier
--              type\value pair. 
-- =============================================
CREATE PROCEDURE [dbo].[usp_scheduled_item_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
				
		SELECT s.*
		  FROM [dbo].[guest_scheduledItem] gs
		  JOIN [dbo].[scheduledItem] s ON s.[scheduledItemId] = gs.[scheduledItemId]
		  JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		  WHERE gs.[guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



' 

EXEC dbo.sp_executesql @statement = N'


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Creates a party.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_create]
	@partyName NVARCHAR(200),
	@primaryGuestId BIGINT,
	@partyId BIGINT OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		INSERT INTO [dbo].[party] ([primaryGuestId], [partyName], [count], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@primaryGuestId, @partyName, 0, ''IDMS'', GETUTCDATE(), ''IDMS'', GETUTCDATE())
		
		SELECT @partyId = @@IDENTITY
		
		EXECUTE [dbo].[usp_party_guest_create]
			@partyId = @partyId,
			@guestId = @primaryGuestId	

		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



' 

EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retrieves the name of a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_name_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   [firstName]
				,[lastName]
				,[middleName]
				,[title]
				,[suffix]
		FROM	[dbo].[guest] g
		WHERE	g.[guestid] = @guestId
		AND		g.[active] = 1

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

' 


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retrieve all the identifiers 
--              for a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_identifiers_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   i.[IDMSTypeName] AS [type]
				,s.[sourceSystemIdValue] AS [value]
				,s.[guestId]
		FROM	[dbo].[source_system_link] s
		JOIN	[dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE	s.[guestid] = @guestId


	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
' 

EXEC dbo.sp_executesql @statement = N'


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
	@avatarName nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @guestType
		AND		[IDMSKey] = ''GUESTTYPE''
		
		--If type not found create as park guest.
		IF @IDMSTypeID IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] 
			WHERE	[IDMSTypeName] = ''Park Guest''
			AND		[IDMSKey] = ''GUESTTYPE''
		END
		

		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSID],[IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(NEWID(),@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		--Create the XID
		DECLARE @sourceSystemIdValue nvarchar(200)
		DECLARE @sourceSystemIdType nvarchar(50)
		
		SET @sourceSystemIdValue = REPLACE(NEWID(),''-'','''')
		SET @sourceSystemIdType = ''xid''

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @guestId = @guestId
		  ,@sourceSystemIdValue = @sourceSystemIdValue
		  ,@sourceSystemIdType = @sourceSystemIdType

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Updates a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_update] 
	@celebrationId BIGINT,
	@name NVARCHAR(200),
	@message NVARCHAR(MAX),
	@active BIT = 1,
	@IDMSTypeId INT,
	@dateStart DATE = NULL,
	@dateEnd DATE = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--TODO: Pass name instead of ID.
		--DECLARE @IDMSTypeID int
		
		--SELECT	@IDMSTypeID = [IDMSTypeID] 
		--FROM	[dbo].[IDMS_Type] 
		--WHERE	[IDMSTypeName] = @celebrationType
		--AND		[IDMSKey] = ''CELEBRATION''
		
		--Update celebration
		UPDATE [dbo].[celebration]
			SET [name] = @name,
				[message] = @message,
				[dateStart] = @dateStart,
				[dateEnd] = @dateEnd,
				[active] = @active,
				[IDMSTypeId] = @IDMSTypeId,
				[updatedBy] = ''IDMS'',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_retrieve] 
	@celebrationId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	

		SELECT  c.*, i.* 
		FROM	[dbo].[celebration] c
		JOIN	[dbo].[IDMS_Type] i  on i.[IDMSTypeId] = c.[IDMSTypeId]
		WHERE	c.[celebrationId] = @celebrationId
		AND		c.[active] = 1
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Delete a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_delete] 
	@celebrationId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		--Deactivate (Soft delete) celebration
		UPDATE [dbo].[celebration]
			SET [active] = 0,
				[updatedBy] = ''IDMS'',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Create a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_create] 
	@celebrationId BIGINT OUTPUT,
	@guestId BIGINT,
	@name NVARCHAR(200),
	@message NVARCHAR(MAX),
	@active BIT = 1,
	@IDMSTypeId INT,
	@dateStart DATE = NULL,
	@dateEnd DATE = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--TODO: Pass name instead of ID.
		--DECLARE @IDMSTypeID int
		
		--SELECT	@IDMSTypeID = [IDMSTypeID] 
		--FROM	[dbo].[IDMS_Type] 
		--WHERE	[IDMSTypeName] = @celebrationType
		--AND		[IDMSKey] = ''CELEBRATION''
		
		--Create celebration
		INSERT INTO [dbo].[celebration]
			([guestId],[name],[message],[dateStart],[dateEnd],[active],[IDMSTypeId],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestId,@name,@message, @dateStart, @dateEnd, @active, @IDMSTypeID,
			 N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @celebrationid = @@IDENTITY 
		
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 05/30/2012
-- Description:	Deletes a source system 
--              link record.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_delete] 
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
		AND		[IDMSKey] = ''SOURCESYSTEM''

		DELETE FROM [dbo].[source_system_link]
		WHERE [guestId] = @guestid
		AND	  [sourceSystemIdValue] = @sourceSystemIdValue
		AND   [IDMSTypeId] = @IDMSTypeID

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 


EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Unassigns an xband.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_unassign] 
	@xbandId BIGINT,
	@guestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0
	
		--If there''s no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandId] = @xbandId
		
		EXECUTE [dbo].[usp_source_system_link_delete]
				@guestId = @guestId,
			@sourceSystemIdValue = @xbandId,
			@sourceSystemIdType = ''xband''
	           
		IF @InternalTransaction = 1
		BEGIN
			COMMIT TRANSACTION
		END

	END TRY
	BEGIN CATCH
	   
		IF @InternalTransaction = 1
		BEGIN
			ROLLBACK TRANSACTION
		END
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = @updateversion)
BEGIN
	INSERT INTO [dbo].[schema_version]
			   ([Version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   (@updateversion
			   ,@updateversion + '-Update.sql'
			   ,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version]
	SET [date_applied] = GETUTCDATE()
	WHERE [version] = @updateversion
END

update_end:

PRINT 'Updates for database version '  + @updateversion + ' completed.'	

GO
