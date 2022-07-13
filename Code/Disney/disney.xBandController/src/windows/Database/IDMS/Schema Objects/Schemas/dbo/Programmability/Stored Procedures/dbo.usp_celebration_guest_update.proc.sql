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
