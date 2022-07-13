use [$(databasename)]

/****** Object:  StoredProcedure [dbo].[usp_IDMSHello]    Script Date: 09/07/2012 12:56:43 ******/
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
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SELECT N'HELLO';
END
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 09/07/2012 12:56:43 ******/
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
/****** Object:  Table [dbo].[schema_version]    Script Date: 09/07/2012 12:56:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[schema_version](
	[schema_version_id] [int] IDENTITY(1,1) NOT NULL,
	[version] [varchar](12) NOT NULL,
	[script_name] [varchar](50) NOT NULL,
	[date_applied] [datetime] NOT NULL,
 CONSTRAINT [PK_SchemaVersion] PRIMARY KEY CLUSTERED 
(
	[schema_version_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 09/07/2012 12:56:44 ******/
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
/****** Object:  Table [dbo].[guest]    Script Date: 09/07/2012 12:56:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest](
	[guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[IDMSID] [uniqueidentifier] ROWGUIDCOL  NOT NULL,
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
CREATE UNIQUE NONCLUSTERED INDEX [AK_guest_IDMSID] ON [dbo].[guest] 
(
	[IDMSID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
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
/****** Object:  Table [dbo].[celebration]    Script Date: 09/07/2012 12:56:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[celebration](
	[celebrationId] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](200) NOT NULL,
	[milestone] [nvarchar](200) NOT NULL,
	[IDMSTypeId] [int] NOT NULL,
	[date] [date] NOT NULL,
	[startDate] [datetime] NOT NULL,
	[endDate] [datetime] NOT NULL,
	[recognitionDate] [datetime] NOT NULL,
	[surpriseIndicator] [bit] NOT NULL,
	[comment] [nvarchar](200) NULL,
	[active] [bit] NULL,
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
/****** Object:  Table [dbo].[xband]    Script Date: 09/07/2012 12:56:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[xband](
	[xbandId] [bigint] IDENTITY(1,1) NOT NULL,
	[bandId] [nvarchar](200) NOT NULL,
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
	[IDMSTypeId] [int] NOT NULL,
	[publicId] [nvarchar](200) NOT NULL,
	[xbmsId] [uniqueidentifier] NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_publicId] UNIQUE NONCLUSTERED 
(
	[publicId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_secureId] UNIQUE NONCLUSTERED 
(
	[secureId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_tapId] UNIQUE NONCLUSTERED 
(
	[tapId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_xband_bandId] UNIQUE NONCLUSTERED 
(
	[bandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_longRangeId] ON [dbo].[xband] 
(
	[longRangeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_xband_IDMSTypeId] ON [dbo].[xband] 
(
	[IDMSTypeId] ASC
)
INCLUDE ( [xbandId]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_update]    Script Date: 09/07/2012 12:56:44 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_schema_version_retrieve]    Script Date: 09/07/2012 12:56:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
GO
/****** Object:  View [dbo].[vw_celebration]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_celebration]
AS
SELECT c.[celebrationId]
	  ,c.[name]
	  ,c.[milestone]
      ,i.[IDMSTypeName] as [type]
	  ,DATEPART(mm,c.[date]) as [month]
	  ,DATEPART(dd,c.[date]) as [day]
	  ,DATEPART(yyyy,c.[date]) as [year]
	  ,c.[startDate]
	  ,c.[endDate]
	  ,c.[recognitionDate]
	  ,c.[surpriseIndicator]
      ,c.[comment]
  FROM [dbo].[celebration] c
  JOIN [dbo].[IDMS_Type] i on i.[IDMSTypeId] = c.[IDMSTypeId]
  WHERE c.[active] = 1
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_create]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates an xband.
-- Update date: 08/17/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0003
-- Description:	Convert tapId to hex.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_create] 
	@xbandId bigint OUTPUT,
	@bandid nvarchar(200),
	@longRangeId nvarchar(200),
	@tapId nvarchar(200),
	@secureId nvarchar(200),
	@UID nvarchar(200),
	@PublicID nvarchar(200),
	@bandFriendlyName nvarchar(50) = NULL,
	@printedName nvarchar(255) = NULL,
	@xbmsId nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--Convert to Hex
		DECLARE @HexValue BINARY(7)
		DECLARE @table as table ([HexValue] BINARY(7) )
		DECLARE @HexTapId nvarchar(200)
		DECLARE @BandTypeID int

		INSERT INTO  @table ([HexValue]) VALUES  (CONVERT(binary(7),Convert(bigint,@tapid)))

		SELECT @HexTapId = CAST('' AS XML).value('xs:hexBinary(sql:column("hexvalue"))', 'VARCHAR(MAX)')
		FROM @table
  
		--Reverse byte order
		SET @HexTapId = SUBSTRING(@HexTapId,13,2) + 
				SUBSTRING(@HexTapId,11,2) +
				SUBSTRING(@HexTapId,9,2) +
				SUBSTRING(@HexTapId,7,2) +
				SUBSTRING(@HexTapId,5,2) +
				SUBSTRING(@HexTapId,3,2) + 
				SUBSTRING(@HexTapId,1,2)
				
		--Code for FPT2
		--Determine if guest or test band.
		IF EXISTS (SELECT 'X' FROM [dbo].[test_bands] WHERE [publicId] = @PublicID)
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type]
			WHERE [IDMSkey] = 'BANDTYPE'
			AND [IDMSTypeName] = 'TEST'
		END
		ELSE IF EXISTS (SELECT 'X' FROM [dbo].[test_cards] WHERE [secureId] = @secureId)
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type]
			WHERE [IDMSkey] = 'BANDTYPE'
			AND [IDMSTypeName] = 'TEST'
		END
		ELSE
		BEGIN
			SELECT @BandTypeID = [IDMSTypeID] 
			FROM [dbo].[IDMS_Type]
			WHERE [IDMSkey] = 'BANDTYPE'
			AND [IDMSTypeName] = 'Guest'
		END

 		INSERT INTO [dbo].[xband]
			([bandId]
			,[longRangeId]
			,[tapId]
			,[secureId]
			,[UID]
			,[publicId]
			,[bandFriendlyName]
			,[printedName]
			,[active]
			,[xbmsId]
			,[IDMSTypeId]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate])
		VALUES
			(@bandId
			,NULL --Make null for FPT2, since everything is a card @longRangeId
			,@HexTapId
			,@secureId
			,@UID
			,@PublicID
			,@bandFriendlyName
			,@printedName
			,1
			,CONVERT(uniqueidentifier,@xbmsId)
			,@BandTypeID
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
/****** Object:  StoredProcedure [dbo].[usp_celebration_delete]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
				[updatedBy] = 'IDMS',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_update]    Script Date: 09/07/2012 12:56:45 ******/
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
/****** Object:  View [dbo].[vw_xband]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_xband]
AS
SELECT     x.xbandId, x.bandId, x.longRangeId, x.tapId, x.secureId, x.UID, x.bandFriendlyName, x.printedName, x.active, i.IDMSTypeName AS BandType, x.createdBy, 
                      x.createdDate, x.updatedBy, x.updatedDate, x.publicId
FROM         dbo.xband AS x INNER JOIN
                      dbo.IDMS_Type AS i ON i.IDMSTypeId = x.IDMSTypeId
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_update]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
			AND		[IDMSKey] = 'BANDTYPE'
		END
		
		UPDATE [dbo].[xband]
			SET [active] = @active,
				[IDMSTypeID] = 	@IDMSTypeID,
				[updatedby] = 'IDMS',
				[updateddate] = GETUTCDATE()
		WHERE [xbandid] = @xbandId
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
GO
/****** Object:  Table [dbo].[party]    Script Date: 09/07/2012 12:56:45 ******/
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
/****** Object:  Table [dbo].[celebration_guest]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[celebration_guest](
	[celebrationId] [bigint] NOT NULL,
	[guestId] [bigint] NOT NULL,
	[primaryGuest] [bit] NOT NULL,
	[IDMSTypeId] [int] NOT NULL,
	[createdBy] [nvarchar](200) NOT NULL,
	[createdDate] [datetime] NOT NULL,
	[updatedBy] [nvarchar](200) NOT NULL,
	[updatedDate] [datetime] NOT NULL,
 CONSTRAINT [PK_celebration_guest] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC,
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_xband](
	[guestId] [bigint] NOT NULL,
	[xbandId] [bigint] NOT NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[active] [bit] NOT NULL,
 CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC,
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_guest_xband_xbandId] UNIQUE NONCLUSTERED 
(
	[xbandId] ASC
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
/****** Object:  StoredProcedure [dbo].[usp_celebration_update]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Updates a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_update] 
	 @celebrationId BIGINT
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
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
		WHERE	[IDMSTypeName] = @type
		AND		[IDMSKey] = 'CELEBRATION'
		
		--Update celebration
		UPDATE [dbo].[celebration]
			SET [name] = @name,
				[milestone] = @milestone,
				[date] = @date,
				[startDate] = @startDate,
				[endDate] = @endDate,
				[recognitionDate] = @recognitionDate,
				[surpriseIndicator] = @surpriseIndicator,
				[comment] = @comment,
				[IDMSTypeId] = @IDMSTypeId,
				[updatedBy] = 'IDMS',
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
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[source_system_link](
	[guestId] [bigint] NOT NULL,
	[sourceSystemIdValue] [nvarchar](200) NOT NULL,
	[IDMSTypeId] [int] NOT NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_source_system_link] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC,
	[sourceSystemIdValue] ASC,
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE UNIQUE NONCLUSTERED INDEX [AK_source_system_link_guestId_IDMSTypeId] ON [dbo].[source_system_link] 
(
	[guestId] ASC,
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
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
/****** Object:  StoredProcedure [dbo].[usp_party_update]    Script Date: 09/07/2012 12:56:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
				[updatedBy] = 'IDMS',
				[updatedDate] = GETUTCDATE()
		WHERE [partyId] = @partyID
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 09/07/2012 12:56:45 ******/
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
CREATE NONCLUSTERED INDEX [IX_party_guest_guestID] ON [dbo].[party_guest] 
(
	[guestId] ASC
)
INCLUDE ( [partyId]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  UserDefinedFunction [dbo].[ufn_GetGuestId]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and xid.
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
	
	IF @identifierType = 'guestid'
	BEGIN
		SET @Result = CONVERT(BIGINT,@identifierValue)
	END
	ELSE IF @identifierType = 'xbandid'
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest_xband] gx
		WHERE gx.[xbandid] = @identifierValue

	END
	ELSE IF @identifierType = 'swid'
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest] g
		WHERE g.[IDMSID] = @identifierValue

	END
	ELSE
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[source_system_link] s
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE s.[sourceSystemIdValue] = @identifierValue
		AND	  i.[IDMSTypeName] = @identifierType
		AND   i.[IDMSKey] = 'SOURCESYSTEM'
	END
	
	-- Return the result of the function
	RETURN @Result

END
GO
/****** Object:  View [dbo].[vw_xi_guest]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_xi_guest]
AS
SELECT g.[guestId] 
		,g.[firstname]
		,g.[lastName] 
		,g.[emailaddress]
		,ISNULL(i.[IDMStypeName],'None') AS [CelebrationType]
		,c.[recognitionDate]
		,CASE WHEN i2.[IDMSTypeName] = 'TEST' AND i2.[IDMSkey] = 'BANDTYPE' THEN 'TEST' ELSE 'GUEST' END AS [GuestType]
FROM	[dbo].[guest] AS g WITH(NOLOCK)
LEFT OUTER JOIN [dbo].[celebration_guest] AS cg WITH(NOLOCK) on cg.[guestId] = g.[guestId]
LEFT OUTER JOIN [dbo].[celebration] AS c WITH(NOLOCK) on c.[celebrationId] = cg.[celebrationId]
LEFT OUTER JOIN [dbo].[IDMS_Type] AS i WITH(NOLOCK) on i.[IDMSTypeID] = c.[IDMSTypeID]
LEFT OUTER JOIN	[dbo].[guest_xband] AS gx WITH(NOLOCK) ON gx.[guestId] = g.[guestId] 
LEFT OUTER JOIN	[dbo].[xband] AS x WITH(NOLOCK) ON x.[xbandId] = gx.[xbandId]
LEFT OUTER JOIN	[dbo].[IDMS_Type] as i2 WITH(NOLOCK) ON i2.[IDMSTypeId] = x.[IDMSTypeId]
WHERE NOT EXISTS
(SELECT 'X'
 FROM [dbo].[source_system_link] s WITH(NOLOCK)
 WHERE s.[guestId] = g.[guestId]
	AND	s.[IDMSTypeId] = 102
	AND	s.[sourceSystemIdValue] BETWEEN '86000001' AND '86010000'
	and s.[guestId] NOT IN (36078670, 36078673, 
36078671, 36078675, 36078614, 36078617, 36078619, 36078613, 36078677, 36078678,
36078680, 36078679, 36078608, 36078610, 36078612, 36078605, 36078689, 36078691,
36078685, 36078687, 36078622, 36078624, 36078626, 36078628, 36078700, 36078699,
36078698, 36078697, 36078639, 36078641, 36078643, 36078637, 36078662)
)
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_unassign]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Unassigns an xband.
-- Update date: 07/18/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Remove deleting xband identifier.
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
	
		--If there's no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandId] = @xbandId
			           
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
GO
/****** Object:  View [dbo].[vw_test_guest]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_test_guest]
AS
SELECT	DISTINCT g.[guestId]
FROM	[dbo].[guest] AS g WITH(NOLOCK)
JOIN	[dbo].[guest_xband] AS gx WITH(NOLOCK) ON gx.[guestId] = g.[guestId] 
JOIN	[dbo].[xband] AS x WITH(NOLOCK) ON x.[xbandId] = gx.[xbandId]
JOIN	[dbo].[IDMS_Type] as i WITH(NOLOCK) ON i.[IDMSTypeId] = x.[IDMSTypeId]
WHERE	i.[IDMSTypeName] = 'TEST'
AND		i.[IDMSkey] = 'BANDTYPE'
GO
/****** Object:  View [dbo].[vw_registered_guests]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_registered_guests]
AS
SELECT COUNT([guestId]) [GuestsWithGlobalRegID], 
	   CONVERT(date,[CreatedDate]) [Date]
  FROM [dbo].[source_system_link] WITH(NOLOCK)
  WHERE [IDMSTypeId] = 101
  GROUP BY CONVERT(date,[CreatedDate])
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_delete]    Script Date: 09/07/2012 12:56:46 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_create]    Script Date: 09/07/2012 12:56:46 ******/
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
/****** Object:  StoredProcedure [dbo].[usp_xband_assign]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Assigns an xband to a guest.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to source system link
--              to use key value pair.
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Remove processing of xband identifier.
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
		
		--Create new association.
		INSERT INTO [dbo].[guest_xband] ([guestID], [xbandId], [active], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@guestId, @xbandId, 1, N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())		

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_delete]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
		AND		[IDMSKey] = 'SOURCESYSTEM'

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
GO
/****** Object:  View [dbo].[vw_eligible_guests]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_eligible_guests]
AS
SELECT COUNT(DISTINCT g.[guestId]) AS [EligibleGuestCount]
FROM	[dbo].[guest] AS g WITH(NOLOCK)
JOIN	[dbo].[guest_xband] AS gx WITH(NOLOCK) ON gx.[guestId] = g.[guestId] 
JOIN	[dbo].[xband] AS x WITH(NOLOCK) ON x.[xbandId] = gx.[xbandId]
JOIN	[dbo].[IDMS_Type] as i WITH(NOLOCK) ON i.[IDMSTypeId] = x.[IDMSTypeId]
WHERE	i.[IDMSTypeName] <> 'TEST'
AND		i.[IDMSkey] = 'BANDTYPE'
AND NOT EXISTS
(SELECT 'X'
 FROM [dbo].[source_system_link] s WITH(NOLOCK)
 WHERE s.[guestId] = g.[guestId]
	AND	s.[IDMSTypeId] = 102
	AND	s.[sourceSystemIdValue] BETWEEN '86000001' AND '86010000')
GO
/****** Object:  View [dbo].[vw_celebration_guest]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_celebration_guest]  
AS
SELECT c.[celebrationId], s.[sourceSystemIdValue] as [xid]
      ,i2.[IDMSTypeName] as [role]
      ,CASE WHEN cg.[primaryGuest] = 1 THEN 'OWNER,PARTICIPANT' ELSE 'PARTICIPANT' END as [relationship]
      ,g.[guestId]
      ,g.[firstName] as [firstname]
      ,g.[lastName] as [lastname]
FROM	[dbo].[celebration] c
JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = c.[celebrationId]
JOIN	[dbo].[guest] g ON g.[guestId] = cg.[guestId]
JOIN	[dbo].[source_system_link] s ON s.[guestId] = cg.[guestId]
JOIN	[dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
	AND i.[IDMSTypeName] = 'xid'
JOIN	[dbo].[IDMS_Type] i2 ON i2.[IDMSTypeId] = cg.[IDMSTypeId]
WHERE	c.[active] = 1
GO
/****** Object:  StoredProcedure [dbo].[usp_xbands_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_assign_by_identifier]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/13/2012
-- Description:	Assigns an xband to a guest.
-- Version: 1.0.3.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign_by_identifier] 
	@xbandId BIGINT,
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

		EXECUTE [dbo].[usp_xband_assign] 
		   @xbandId = @xbandId
		  ,@guestId = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_create]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a source system 
--              link record.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to use key value 
--              pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Handle swid and xid.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_create] 
	@identifierType nvarchar(50),
	@identifierValue nvarchar(200),
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		--SWID is currently stored in guest record. 
		--TODO: Change to stored swid in source_system_link?
		IF @sourceSystemIdType = 'swid'
			RETURN
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = 'SOURCESYSTEM'

		INSERT INTO [dbo].[source_system_link]
			([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestid, @sourceSystemIdValue, @IDMSTypeID,N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
		AND i.[IDMSKEY] = 'SOURCESYSTEM'

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
	
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_search]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
		WHERE	[IDMSTypeName] = 'HOME ADDRESS'
	
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
		WHERE (g.firstName + ' ' + g.lastName) LIKE '%' + @searchString + '%'
		ORDER BY g.[createdDate] DESC
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_name_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_identifiers_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
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
GO
/****** Object:  View [dbo].[vw_guest_xband]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[vw_guest_xband]
AS
SELECT     g.guestId, g.IDMSID AS swid, g.IDMSTypeId, g.lastName, g.firstName, g.middleName, g.title, g.suffix, g.DOB AS dateOfBirth, g.VisitCount, g.AvatarName AS avatar, 
                      g.active, g.emailAddress, g.parentEmail, g.countryCode, g.languageCode, g.userName, g.createdBy, g.createdDate, g.updatedBy, g.updatedDate, x.xbandId, x.bandId, 
                      x.longRangeId, x.secureId, x.UID, x.tapId, x.publicId, dbo.party_guest.partyId, CASE WHEN g.[active] = 1 THEN 'Active' ELSE 'InActive' END AS status, 
                      CASE WHEN g.[gender] = 'M' THEN 'MALE' ELSE 'FEMALE' END AS gender
FROM         dbo.guest AS g INNER JOIN
                      dbo.guest_xband AS gx ON gx.guestId = g.guestId INNER JOIN
                      dbo.xband AS x ON x.xbandId = gx.xbandId LEFT OUTER JOIN
                      dbo.party_guest ON g.guestId = dbo.party_guest.guestId
GO
/****** Object:  StoredProcedure [dbo].[usp_celebration_retrieve_by_identifier]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
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

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v
		JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = v.[celebrationId]
		WHERE	cg.[guestId]= @guestId
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v
		WHERE	v.[guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
GO
/****** Object:  StoredProcedure [dbo].[usp_celebration_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retrieve all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_retrieve] 
	@celebrationId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v
		WHERE	v.[celebrationId] = @celebrationId
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v
		WHERE	v.[celebrationId] = @celebrationId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
GO
/****** Object:  StoredProcedure [dbo].[usp_celebration_guest_update]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Updates a guest's celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_guest_update] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
	,@role NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId]('xid',@xid)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @role
		AND		[IDMSKey] = 'CELEBRATION ROLE'

		UPDATE [dbo].[celebration_guest]
		SET [IDMSTypeId] = @IDMSTypeID,
			[updatedBy] = 'IDMS',
			[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		AND	  [guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_celebration_guest_delete]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Deletes a guest from a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_guest_delete] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId]('xid',@xid)

		DELETE FROM [dbo].[celebration_guest]
		WHERE [celebrationId] = @celebrationId
		AND	  [guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_celebration_guest_add]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/19/2012
-- Description:	Adds a guest to a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_guest_add] 
	 @celebrationId BIGINT
	,@xid NVARCHAR(200)
	,@role NVARCHAR(50)
	,@primaryGuest bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId]('xid',@xid)
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @role
		AND		[IDMSKey] = 'CELEBRATION ROLE'

		INSERT INTO [dbo].[celebration_guest]
			([celebrationId],[guestId],[primaryGuest],[IDMSTypeId],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@celebrationId,@guestId,@primaryGuest,@IDMSTypeID,
			 N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_party_retrieve_by_name]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Scott Salley
-- Create date: 06/01/2012
-- Description:	Retrieves a party using the 
--              party name.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Corrected party count.
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
GO
/****** Object:  StoredProcedure [dbo].[usp_party_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a party using the 
--              party ID.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Corrected party count.
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
GO
/****** Object:  StoredProcedure [dbo].[usp_party_guest_create]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
		
		IF NOT EXISTS(SELECT 'X' FROM [dbo].[party_guest] WHERE [partyId] = @partyId AND [guestId] = @guestId)
		BEGIN
				
			DECLARE @IDMSTypeId INT
		
			SELECT @IDMSTypeId = [IDMSTypeId]
			FROM	[dbo].[IDMS_Type]
			WHERE	[IDMSTypeName] = 'guest party'
			AND		[IDMSkey] = 'PARTYTYPE'

			INSERT INTO [dbo].[party_guest]	([partyId], [guestId], [IDMSTypeId], 
				[createdBy], [createdDate], [updatedBy], [updatedDate])
			VALUES (@partyId, @guestId, @IDMSTypeId, 'IDMS', GETUTCDATE(), 'IDMS', GETUTCDATE())
		
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_party_create]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
		VALUES (@primaryGuestId, @partyName, 0, 'IDMS', GETUTCDATE(), 'IDMS', GETUTCDATE())
		
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
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves an xband.
-- Update date: 07/16/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0001
-- Description:	Add retrieval by publicId.
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
	
		DECLARE @xbandid bigint

		-- Must match BandLookupType in Java code
		IF @bandLookupType = 0
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[xbandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[xbandid] = @id
		END
		ELSE IF @bandLookupType = 1
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[bandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[bandid] = @id
		END	
		ELSE IF @bandLookupType = 2
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[longrangeid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[longrangeid] = @id
		END	
		ELSE IF @bandLookupType = 3
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[tapid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[tapid] = @id
		END	
		ELSE IF @bandLookupType = 4
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[secureid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[secureid] = @id
		END	
		ELSE IF @bandLookupType = 5
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[uid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[uid] = @id
		END	
		ELSE IF @bandLookupType = 6
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[publicId] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[publicId] = @id
		END	
	           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a guest using and
--              identifier type and value.
-- Update date: 06/22/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Move celebrations to the end.
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
			  ,CASE WHEN g.[active] = 1 THEN 'Active' ELSE 'InActive' END AS [status]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,CASE WHEN g.[gender] = 'M' THEN 'MALE' ELSE 'FEMALE' END AS [gender]
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

		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_celebration_retrieve_by_identifier]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_celebration_create]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Create a celebration.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Updates for new schema. Only
--              Only creates primary guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_create] 
	 @celebrationId BIGINT OUTPUT
	,@xid NVARCHAR(200)
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@role NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId]('xid',@xid)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @Type
		AND		[IDMSKey] = 'CELEBRATION'
		
		--Create celebration
		INSERT INTO [dbo].[celebration]
			([name],[milestone],[IDMSTypeId],[date],
			 [startDate],[endDate],[recognitionDate],
			 [surpriseIndicator],[comment],[active],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@name,@milestone,@IDMSTypeID,@date,
			CONVERT(datetime,@startDate,127),CONVERT(datetime,@endDate,127),CONVERT(datetime,@recognitionDate,127),
			@surpriseIndicator,@comment,1,
			 N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())
			
		--Capture id
		SELECT @celebrationid = @@IDENTITY 
		
		EXECUTE [dbo].[usp_celebration_guest_add]
			@celebrationId = @celebrationId
			,@xid = @xid
			,@primaryGuest = 1
			,@role = @role

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_data_retrieve]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/21/2012
-- Description:	Retrieves guest data information.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_data_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200),
	@startDate NVARCHAR(50),
	@endDate NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_xbands_retrieve]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_celebration_retrieve_by_identifier]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_create]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to source system link
--              to use key value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and mapped to xid.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_create] 
	@guestId bigint OUTPUT,
	@swid uniqueidentifier,
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
		AND		[IDMSKey] = 'GUESTTYPE'
		
		--If type not found create as park guest.
		IF @IDMSTypeID IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] 
			WHERE	[IDMSTypeName] = 'Park Guest'
			AND		[IDMSKey] = 'GUESTTYPE'
		END
		

		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSID],[IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@swid,@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		--Create the XID
		DECLARE @sourceSystemIdValue nvarchar(200)
		DECLARE @sourceSystemIdType nvarchar(50)
		
		SET @sourceSystemIdValue = REPLACE(@swid,'-','')
		SET @sourceSystemIdType = 'xid'

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @identifierType = 'guestId'
		  ,@identifierValue = @guestId
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
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_retrieve_by_email]    Script Date: 09/07/2012 12:56:46 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
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
			@identifierType = 'guestId', 
			@identifierValue = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  Default [DF_guest_GGID]    Script Date: 09/07/2012 12:56:44 ******/
ALTER TABLE [dbo].[guest] ADD  CONSTRAINT [DF_guest_GGID]  DEFAULT (newid()) FOR [IDMSID]
GO
/****** Object:  Default [DF__xband__IDMSTypeI__4BAC3F29]    Script Date: 09/07/2012 12:56:44 ******/
ALTER TABLE [dbo].[xband] ADD  DEFAULT ((50)) FOR [IDMSTypeId]
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 09/07/2012 12:56:44 ******/
ALTER TABLE [dbo].[guest]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest] CHECK CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 09/07/2012 12:56:44 ******/
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_xband_IDMS_Type]    Script Date: 09/07/2012 12:56:44 ******/
ALTER TABLE [dbo].[xband]  WITH CHECK ADD  CONSTRAINT [FK_xband_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[xband] CHECK CONSTRAINT [FK_xband_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[party]  WITH CHECK ADD  CONSTRAINT [FK_party_guest] FOREIGN KEY([primaryGuestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[party] CHECK CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_celebration_guest_celebration]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_celebration] FOREIGN KEY([celebrationId])
REFERENCES [dbo].[celebration] ([celebrationId])
GO
ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_celebration]
GO
/****** Object:  ForeignKey [FK_celebration_guest_guest]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_guest]
GO
/****** Object:  ForeignKey [FK_celebration_guest_IDMS_Type]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[celebration_guest]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[celebration_guest] CHECK CONSTRAINT [FK_celebration_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_system_id_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_type_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_type_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 09/07/2012 12:56:45 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_party] FOREIGN KEY([partyId])
REFERENCES [dbo].[party] ([partyId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_party]
GO
