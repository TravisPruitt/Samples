

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


