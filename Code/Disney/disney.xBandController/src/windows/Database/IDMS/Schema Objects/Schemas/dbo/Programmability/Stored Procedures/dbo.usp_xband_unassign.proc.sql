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