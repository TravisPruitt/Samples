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
