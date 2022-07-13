

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


