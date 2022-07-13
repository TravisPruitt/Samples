

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


