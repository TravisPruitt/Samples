USE [$(databasename)]

SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;
GO

CREATE TABLE [dbo].[messageBatch](
	[messageBatchId] [int] IDENTITY(1,1) NOT NULL,
	[messageBatchDescription] [nvarchar](50) NOT NULL,
	[startDateTime] [datetime] NOT NULL,
	[finishDateTime] [datetime] NULL,
 CONSTRAINT [PK_messageBatch] PRIMARY KEY CLUSTERED 
(
	[messageBatchId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[LastNames](
	[LastNameId] [int] IDENTITY(1,1) NOT NULL,
	[Name] [varchar](50) NOT NULL,
 CONSTRAINT [PK_LastNames] PRIMARY KEY CLUSTERED 
(
	[LastNameId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_LastNames_Name] UNIQUE NONCLUSTERED 
(
	[LastNameId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[FirstNames](
	[FirstNameId] [int] IDENTITY(1,1) NOT NULL,
	[Name] [varchar](50) NOT NULL,
 CONSTRAINT [PK_FirstNames] PRIMARY KEY CLUSTERED 
(
	[FirstNameId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_FirstNames_Name] UNIQUE NONCLUSTERED 
(
	[Name] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 01/25/2012
-- Description:	Rethrows an Error.
-- Version: 1.0.0.0001
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

CREATE TABLE [dbo].[xbandRequest](
	[xbandRequestId] [uniqueidentifier] NOT NULL,
	[primaryGuestOwnerId] [uniqueidentifier] NOT NULL,
	[acquisitionId] [int] NOT NULL,
	[acquisitionIdType] [nvarchar](50) NOT NULL,
	[acquisitionStartDate] [datetime] NOT NULL,
	[acquisitionUpdateDate] [datetime] NOT NULL,
	[createDate] [datetime] NOT NULL,
	[updateDate] [datetime] NOT NULL,
	[messageState] [int] NOT NULL,
 CONSTRAINT [PK_xbandRequest] PRIMARY KEY CLUSTERED 
(
	[xbandRequestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE TABLE [dbo].[xbandOwner](
	[xbandOwnerId] [uniqueidentifier] NOT NULL,
	[createDate] [datetime] NOT NULL,
	[firstName] [nvarchar](50) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[guestIdType] [nvarchar](50) NOT NULL,
	[lastName] [nvarchar](50) NOT NULL,
	[primaryGuest] [bit] NOT NULL,
	[updateDate] [datetime] NOT NULL,
	[customizationSelectionId] [uniqueidentifier] NOT NULL,
	[xbandRequestId] [uniqueidentifier] NOT NULL,
 CONSTRAINT [PK_xbandOwner] PRIMARY KEY CLUSTERED 
(
	[xbandOwnerId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AX_xbandOwner_guestId] UNIQUE NONCLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/13/2013
-- Description:	Set a xband request message as sent.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xbandRequest_sent] 
	 @xbandRequestId uniqueidentifier
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		UPDATE [dbo].[xbandRequest]
		SET [messageState] = 2
		WHERE [xbandRequestId] = @xbandRequestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/15/2013
-- Description:	Gets a message batch.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_messageBatch_finish] 
	 @messageBatchId int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		UPDATE [dbo].[messageBatch]
		SET [finishDateTime] = GETUTCDATE()
		WHERE [messageBatchId] = @messageBatchId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

CREATE TABLE [dbo].[messageBatch_xbandOwner](
	[messageBatchId] [int] NOT NULL,
	[xbandOwnerId] [uniqueidentifier] NOT NULL,
 CONSTRAINT [PK_messageBatch_xbandOwner] PRIMARY KEY CLUSTERED 
(
	[messageBatchId] ASC,
	[xbandOwnerId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2013
-- Description:	Gets an xbandRequest.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xbandRequest_retrieve] 
	 @xbandRequestId uniqueidentifier
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
			SELECT x.[xbandRequestId]
				  ,x.[primaryGuestOwnerId]
				  ,x.[acquisitionId]
				  ,x.[acquisitionIdType]
				  ,LEFT(CONVERT(nvarchar,x.[acquisitionStartDate],127),LEN(x.[acquisitionStartDate])) + 'Z' [acquisitionStartDate]
				  ,LEFT(CONVERT(nvarchar,x.[acquisitionUpdateDate],127),LEN(x.[acquisitionUpdateDate])) + 'Z' [acquisitionUpdateDate]
				  ,LEFT(CONVERT(nvarchar,x.[createDate],127),LEN(x.[createDate])) + 'Z' [createDate]
				  ,LEFT(CONVERT(nvarchar,x.[updateDate],127),LEN(x.[updateDate])) + 'Z' [updateDate]
				  ,x.[messageState]
			  FROM [dbo].[xbandRequest] x WITH(NOLOCK)
			  WHERE x.[xbandRequestId] = @xbandRequestId

			SELECT   x.[xbandOwnerId]
					,x.[xbandRequestId]
				    ,LEFT(CONVERT(nvarchar,x.[createDate],127),LEN(x.[createDate])) + 'Z' [createDate]
					,x.[firstName]
					,x.[guestId]
					,x.[guestIdType]
					,x.[lastName]
					,x.[primaryGuest]
				    ,LEFT(CONVERT(nvarchar,x.[updateDate],127),LEN(x.[updateDate])) + 'Z' [updateDate]
					,x.[customizationSelectionId]
			  FROM [dbo].[xbandOwner] x WITH(NOLOCK)
			  WHERE x.[xbandRequestId] = @xbandRequestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

CREATE TABLE [dbo].[xband](
	[xbandId] [uniqueidentifier] NOT NULL,
	[publicId] [bigint] NOT NULL,
	[productId] [nvarchar](50) NOT NULL,
	[xBandOwnerId] [uniqueidentifier] NOT NULL,
	[state] [nvarchar](50) NOT NULL,
	[secondaryState] [nvarchar](50) NOT NULL,
	[externalNumber] [nvarchar](50) NOT NULL,
	[secureId] [bigint] NOT NULL,
	[shortRangeTag] [bigint] NOT NULL,
	[assignmentDateTime] [datetime] NOT NULL,
	[messageState] [int] NOT NULL,
	[bandRole] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_xband_publicId] UNIQUE NONCLUSTERED 
(
	[publicId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_xband_secureId] UNIQUE NONCLUSTERED 
(
	[secureId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_xband_shortRangeTag] UNIQUE NONCLUSTERED 
(
	[shortRangeTag] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/24/2013
-- Description:	Populates simulated data.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_populate_data] 
	 @count int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @Counter int
	DECLARE @acquisitionId int
	DECLARE @acquisitionStartDate datetime
	DECLARE @LastNameTotalCount int
	DECLARE @FirstNameTotalCount int
	DECLARE @guestid bigint
	DECLARE @publicId bigint
	DECLARE @secureId bigint
	DECLARE @productId int
	DECLARE @shortRangeTag bigint
	DECLARE @externalNumber bigint
	DECLARE @xbandRequestId uniqueidentifier
	DECLARE @primaryGuestOwnerId uniqueidentifier
	DECLARE @xbandOwnerId uniqueidentifier
	DECLARE @primaryGuest bit
	DECLARE @lastName nvarchar(50)
	DECLARE @firstName nvarchar(50)
	DECLARE @partySize int
	DECLARE @partyCount int
	
	SET @Counter = 0
	SET @partySize = CONVERT(int,RAND() * 5) + 1
	SET @partyCount = 0

	SELECT @acquisitionId = ISNULL(MAX([acquisitionId]),15476999) + 1 FROM [dbo].[xbandRequest]

	SELECT @FirstNameTotalCount = COUNT(*) FROM [dbo].[FirstNames]
	SELECT @LastNameTotalCount = COUNT(*) FROM [dbo].[LastNames]

	SELECT @guestId = ISNULL(MAX([guestid]),8736599999) + 1 FROM [dbo].[xbandOwner]

	SELECT @publicId = ISNULL(MAX([publicId]),9991999999999) + 1 FROM [dbo].[xband]
	SELECT @secureId = ISNULL(MAX([secureId]),9999995599999999) + 1 FROM [dbo].[xband]
	SELECT @shortRangeTag = ISNULL(MAX([shortRangeTag]),360667299999834) + 1 FROM [dbo].[xband]
	SELECT @externalNumber = 9796833216927
	SET @productId = 10101
	

	BEGIN TRY
	
		WHILE @Counter < @count
		BEGIN
		
			SET @acquisitionStartDate = DATEADD(DAY,-CONVERT(int,RAND() * 365),GETDATE())

			SELECT @firstName = [Name] 
			FROM [dbo].[FirstNames]
			WHERE [FirstNameId] = Abs(Checksum(NEWID())) % @FirstNameTotalCount

			
			IF @partyCount = 0
			BEGIN

				SELECT @lastName = [Name] 
				FROM [dbo].[LastNames]
				WHERE [LastNameId] = Abs(Checksum(NEWID())) % @LastNameTotalCount

				SET @acquisitionId = @acquisitionId + 1
				SET @xbandRequestId = NEWID()
				SET @primaryGuestOwnerId = NEWID()
				SET @xbandOwnerId = @primaryGuestOwnerId
				SET @primaryGuest = 1

				INSERT INTO [dbo].[xbandRequest]
					([xbandRequestId]
					,[primaryGuestOwnerId]
					,[acquisitionId]
					,[acquisitionIdType]
					,[acquisitionStartDate]
					,[acquisitionUpdateDate]
					,[createDate]
					,[updateDate]
					,[messageState])
				VALUES
					(@xbandRequestId
					,NEWID()
					,@acquisitionId
					,'travel-plan-id'
					,@acquisitionStartDate
					,DATEADD(DAY,CONVERT(int,RAND() * 10),@acquisitionStartDate)
					,GETUTCDATE()
					,GETUTCDATE()
					,0)			
				END
			ELSE
			BEGIN
				SET @primaryGuest = 0
				SET @xbandOwnerId = NEWID()
				
				IF @Counter % 10 = 0 
				BEGIN
					SELECT @lastName = [Name] 
					FROM [dbo].[LastNames]
					WHERE [LastNameId] = Abs(Checksum(NEWID())) % @LastNameTotalCount

				END
				
			END


			INSERT INTO [dbo].[xbandOwner]
					   ([xbandOwnerId]
					   ,[createDate]
					   ,[firstName]
					   ,[guestId]
					   ,[guestIdType]
					   ,[lastName]
					   ,[primaryGuest]
					   ,[updateDate]
					   ,[customizationSelectionId]
					   ,[xbandRequestId])
			VALUES
				(@xbandOwnerId
				,GETUTCDATE()
				,@firstName
				,@guestid
				,'transactional-guest-id'
				,@lastName
				,@primaryGuest
				,GETUTCDATE()
				,NEWID()
				,@xbandRequestId)

			SET @Counter = @Counter + 1
			SET @guestid = @guestid + 1
			SET @partyCount = @partyCount + 1
			
			IF @partyCount > @partySize
			BEGIN
				SET @partySize = CONVERT(int,RAND() * 5) + 1
				SET @partyCount = 0
			END
				
				
		END   

		INSERT INTO [dbo].[xband]
				   ([xbandId]
				   ,[productId]
				   ,[publicId]
				   ,[xBandOwnerId]
				   ,[state]
				   ,[secondaryState]
				   ,[externalNumber]
				   ,[secureId]
				   ,[shortRangeTag]
				   ,[assignmentDateTime]
				   ,[bandRole])
		SELECT   NEWID()
				,'B' + CONVERT(nvarchar,@productId + ROW_NUMBER() OVER( ORDER BY [xBandOwnerId]))
				,@publicId + ROW_NUMBER() OVER( ORDER BY [xBandOwnerId])
				,xo.[xbandOwnerId]
				,'ACTIVE'
				,'ORIGINAL'
				,RIGHT(UPPER(master.dbo.fn_varbintohexstr(CONVERT(varbinary,@externalNumber + ROW_NUMBER() OVER( ORDER BY [xBandOwnerId])))),11)
				,@secureId + ROW_NUMBER() OVER( ORDER BY [xBandOwnerId])
				,@shortRangeTag + ROW_NUMBER() OVER( ORDER BY [xBandOwnerId])
				,GETUTCDATE()
				,'Guest'
		  FROM  [dbo].[xbandOwner] xo
		  WHERE NOT EXISTS
		  (SELECT 'X'
		   FROM [dbo].[xband] x
		   WHERE x.[xbandOwnerId] = xo.[xbandOwnerId])

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/15/2013
-- Description:	Start a message batch.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_messageBatch_start] 
	 @messageBatchId int OUTPUT
	 ,@count int
	 ,@puckCount int = 0
	 ,@castMemberCount int = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
			INSERT INTO [dbo].[messageBatch]
			   ([messageBatchDescription]
			   ,[startDateTime])
			 VALUES
			   (CONVERT(nvarchar,@count) + ' xband Requests ' + CONVERT(nvarchar,GETUTCDATE(),127)
			   ,GETUTCDATE())

			SELECT @messageBatchId = @@IDENTITY
			
			INSERT INTO [dbo].[messageBatch_xbandOwner]
				([messageBatchId]
				,[xbandOwnerId])
			SELECT @messageBatchId,
				xo.[xbandOwnerId] 
			FROM
				(SELECT DISTINCT TOP(@count)
					xr.[xbandRequestId]
				FROM [dbo].[xbandRequest] xr 
				WHERE NOT EXISTS
				(SELECT 'X'
				FROM [dbo].[messageBatch_xbandOwner] mbxo
				JOIN [dbo].[xbandOwner] xo ON xo.[xbandOwnerId] = mbxo.[xbandOwnerId]
				JOIN [dbo].[xband] x ON x.[xBandOwnerId] = xo.[xbandOwnerId]
				WHERE xo.[xbandRequestId] = xr.[xbandRequestId]
				AND x.[messageState] = 0
				AND xr.[messageState] = 0)
				ORDER BY xr.[xbandRequestId]) as t
			JOIN [dbo].[xbandOwner] xo on xo.[xbandRequestId] = t.[xbandRequestId]

			IF @puckCount > 0
			BEGIN
				DECLARE @Pucks TABLE ([xbandid] uniqueidentifier)
			 
				INSERT INTO @Pucks ([xbandid])
					SELECT TOP(@puckCount)
					x.[xbandId]
				FROM [dbo].[xband] x 
				WHERE x.[messageState] = 0
				AND NOT EXISTS
				(SELECT 'X'
				FROM [dbo].[messageBatch_xbandOwner] mbxo
				WHERE mbxo.[xbandOwnerId] = x.[xbandOwnerId])
				ORDER BY x.[xbandId]
			 
				INSERT INTO [dbo].[messageBatch_xbandOwner]
					([messageBatchId]
					,[xbandOwnerId])
				SELECT @messageBatchId
					,x.[xbandOwnerId]
				FROM @Pucks p 
				JOIN [dbo].[xband] x ON x.[xbandid] = p.[xbandid]

				--SET all the requests to sent, as they won't be used
				UPDATE [dbo].[xbandRequest]
				SET [messageState] = 2
				WHERE [xbandRequestId] = 
				(SELECT xo.[xbandRequestId]
				FROM @Pucks p 
				JOIN [dbo].[xband] x ON x.[xbandid] = p.[xbandid]
				JOIN [dbo].[xbandOwner] xo on xo.xbandOwnerId = x.[xBandOwnerId])

				--SET all the requests to sent, as they won't be used
				UPDATE [dbo].[xband]
				SET [bandRole] = 'Puck'
				WHERE [xbandId] = 
				(SELECT [xbandId]
				FROM @Pucks p)
			
			END
			
			IF @castMemberCount > 0
			
			BEGIN

				DECLARE @CastMembers TABLE ([xbandid] uniqueidentifier)

				INSERT INTO @CastMembers([xbandid])
					SELECT TOP(@castMemberCount)
					x.[xbandId]
				FROM [dbo].[xbandOwner] xo 
				JOIN [dbo].[xbandRequest] xr ON xr.[xbandRequestId] = xo.[xbandRequestId]
				JOIN [dbo].[xband] x ON x.[xBandOwnerId] = xo.[xbandOwnerId]
				WHERE x.[messageState] = 0
				AND NOT EXISTS
				(SELECT 'X'
				FROM [dbo].[messageBatch_xbandOwner] mbxo
				WHERE mbxo.[xbandOwnerId] = xo.[xbandOwnerId])
				ORDER BY xo.[xbandOwnerId]
			 
				INSERT INTO [dbo].[messageBatch_xbandOwner]
					([messageBatchId]
					,[xbandOwnerId])
				SELECT @messageBatchId
					,x.[xbandOwnerId]
				FROM @CastMembers c
				JOIN [dbo].[xband] x ON x.[xbandid] = c.[xbandid]

				--SET all the requests to sent, as they won't be used
				UPDATE [dbo].[xbandRequest]
				SET [messageState] = 2
				WHERE [xbandRequestId] = 
				(SELECT [xbandRequestId]
				FROM @CastMembers c 
				JOIN [dbo].[xband] x ON x.[xbandid] = c.[xbandid]
				JOIN [dbo].[xbandOwner] xo on xo.xbandOwnerId = x.[xBandOwnerId])

				--SET all the requests to sent, as they won't be used
				UPDATE [dbo].[xband]
				SET [bandRole] = 'Cast Member'
				WHERE [xbandId] = 
				(SELECT [xbandId]
				FROM @CastMembers c)
			
			END

		 COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

GO



-- =============================================
-- Author:		Ted Crane
-- Create date: 03/15/2013
-- Description:	Gets a message batch.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_messageBatch_retrieve] 
	 @messageBatchId int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		SELECT	 [messageBatchId]
				,[messageBatchDescription]
				,[startDateTime]
				,[finishDateTime]
		FROM [dbo].[messageBatch]
		WHERE [messageBatchId] = @messageBatchId
		
		SELECT	 xo.[firstName]
				,xo.[lastName]
				,xo.[xbandOwnerId]
				,x.[xbandId]
				,xr.[xbandRequestId]
				,xr.[messageState] as [xbandRequestMessageState]
				,x.[messageState] as [xbandMessageState]
		FROM [dbo].[messageBatch_xbandOwner] mbxo
		JOIN [dbo].[xbandOwner] xo on xo.[xbandOwnerId] = mbxo.[xbandOwnerId]
		JOIN [dbo].[xband] x on x.[xBandOwnerId] = xo.[xbandOwnerId]
		JOIN [dbo].[xbandRequest] xr on xr.[xbandRequestId] = xo.[xbandRequestId]
		WHERE mbxo.[messageBatchId] = @messageBatchId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/15/2013
-- Description:	Get the next xBand to be sent
--              as a message.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xbandRequest_next] 
	 @messageBatchId int 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @xbandRequestId uniqueidentifier

	BEGIN TRY
	
		SELECT @xbandRequestId = xr.[xbandRequestId]
		FROM [dbo].[messageBatch] mb
		JOIN [dbo].[messageBatch_xbandOwner] mbxo WITH(NOLOCK) ON mbxo.[messageBatchId] = mb.[messageBatchId]
		JOIN [dbo].[xbandOwner] xo WITH(NOLOCK) ON xo.[xbandOwnerId] = mbxo.[xbandOwnerId]
		JOIN [dbo].[xbandRequest] xr WITH(NOLOCK) ON xr.[xbandRequestId] = xo.[xbandRequestId]
		WHERE mb.[messageBatchId] = @messageBatchId
		AND xr.[messageState] = 0
		ORDER BY xr.[xbandRequestId]	
		 
		IF @xbandRequestId IS NOT NULL
		BEGIN

			UPDATE [dbo].[xbandRequest]
			SET [messageState] = 1
			WHERE [xBandRequestId] = @xbandRequestId

		END
		 		
		EXECUTE [dbo].[usp_xbandRequest_retrieve]
			@xbandRequestId = @xbandRequestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/13/2013
-- Description:	Set a xband message as sent.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_sent] 
	 @xbandId uniqueidentifier
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		UPDATE [dbo].[xband]
		SET [messageState] = 2
		WHERE [xbandId] = @xbandId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2013
-- Description:	Gets an xband.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_retrieve] 
	 @xbandId uniqueidentifier
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
			SELECT x.[xbandId]
				  ,x.[publicId]
				  ,x.[productId]
				  ,x.[xBandOwnerId]
				  ,x.[state]
				  ,x.[secondaryState]
				  ,x.[externalNumber]
				  ,x.[secureId]
				  ,x.[shortRangeTag]
				  ,x.[assignmentDateTime]
				  ,x.[messageState]
				  ,xo.[guestId]
				  ,xo.[guestIdType]
				  ,x.[bandRole]
				  ,xo.[xbandRequestId]
			  FROM [dbo].[xband] x WITH(NOLOCK)
			  LEFT OUTER JOIN [dbo].[xbandOwner] xo WITH(NOLOCK) on xo.[xbandOwnerId] = x.[xBandOwnerId]
			  WHERE [xBandId] = @xbandId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/15/2013
-- Description:	Get the next xBand to be sent
--              as a message.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_next] 
	 @messageBatchId int 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @xbandid uniqueidentifier

	BEGIN TRY
	
		SELECT @xbandId = x.[xbandId]
		FROM [dbo].[messageBatch] mb
		JOIN [dbo].[messageBatch_xbandOwner] mbxo WITH(NOLOCK) ON mbxo.[messageBatchId] = mb.[messageBatchId]
		JOIN [dbo].[xbandOwner] xo WITH(NOLOCK) ON xo.[xbandOwnerId] = mbxo.[xbandOwnerId]
		JOIN [dbo].[xband] x WITH(NOLOCK) ON x.[xBandOwnerId] = xo.[xbandOwnerId]
		WHERE mb.[messageBatchId] = @messageBatchId
		AND x.[messageState] = 0
		ORDER BY x.[xbandId]	
		 
		 IF @xbandid IS NOT NULL
		 BEGIN
		 
			UPDATE [dbo].[xband]
			SET [messageState] = 1
			WHERE [xBandId] = @xbandid
		 
		 END
		 		
		 EXECUTE [dbo].[usp_xband_retrieve]
			@xbandId = @xbandid

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
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


-- =============================================
-- Author:		Ted Crane
-- Create date: 03/24/2013
-- Description:	Retrieves the current schema
--              version.
-- Version: 1.0.0.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_schema_version_retrieve]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
	SELECT TOP 1 [version]		
	FROM [dbo].[schema_version] WITH(NOLOCK)
	ORDER BY [schema_version_id] DESC 

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO


ALTER TABLE [dbo].[xbandRequest] ADD  CONSTRAINT [DF_xbandRequest_messageState]  DEFAULT ((0)) FOR [messageState]
GO

ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [DF_xband_messageState]  DEFAULT ((0)) FOR [messageState]
GO

ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [DF_xband_bandRole]  DEFAULT ('Guest') FOR [bandRole]
GO

ALTER TABLE [dbo].[xbandOwner]  WITH CHECK ADD  CONSTRAINT [FK_xbandOwner_xbandRequest] FOREIGN KEY([xbandRequestId])
REFERENCES [dbo].[xbandRequest] ([xbandRequestId])
GO
ALTER TABLE [dbo].[xbandOwner] CHECK CONSTRAINT [FK_xbandOwner_xbandRequest]
GO

ALTER TABLE [dbo].[messageBatch_xbandOwner]  WITH CHECK ADD  CONSTRAINT [FK_messageBatch_xbandOwner_messageBatch] FOREIGN KEY([messageBatchId])
REFERENCES [dbo].[messageBatch] ([messageBatchId])
GO
ALTER TABLE [dbo].[messageBatch_xbandOwner] CHECK CONSTRAINT [FK_messageBatch_xbandOwner_messageBatch]
GO

ALTER TABLE [dbo].[messageBatch_xbandOwner]  WITH CHECK ADD  CONSTRAINT [FK_messageBatch_xbandOwner_xbandOwner] FOREIGN KEY([xbandOwnerId])
REFERENCES [dbo].[xbandOwner] ([xbandOwnerId])
GO
ALTER TABLE [dbo].[messageBatch_xbandOwner] CHECK CONSTRAINT [FK_messageBatch_xbandOwner_xbandOwner]
GO

ALTER TABLE [dbo].[xband]  WITH CHECK ADD  CONSTRAINT [FK_xband_xbandOwner] FOREIGN KEY([xBandOwnerId])
REFERENCES [dbo].[xbandOwner] ([xbandOwnerId])
GO
ALTER TABLE [dbo].[xband] CHECK CONSTRAINT [FK_xband_xbandOwner]
GO

CREATE NONCLUSTERED INDEX [IX_xband_xbandOwnerId_messageState] ON [dbo].[xband] 
(
	[xBandOwnerId] ASC,
	[messageState] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

CREATE NONCLUSTERED INDEX [IX_xbandOwner_xbandRequestId] ON [dbo].[xbandOwner] 
(
	[xbandRequestId] ASC
)
INCLUDE ( [xbandOwnerId]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
