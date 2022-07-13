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
