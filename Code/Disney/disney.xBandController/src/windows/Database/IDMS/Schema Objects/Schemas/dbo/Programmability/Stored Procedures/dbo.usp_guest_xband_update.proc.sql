

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
		
		UPDATE [dbo].[guest_xband]
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


