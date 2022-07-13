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
