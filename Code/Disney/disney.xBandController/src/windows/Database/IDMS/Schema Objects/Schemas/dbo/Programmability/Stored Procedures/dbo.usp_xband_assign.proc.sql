-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Assigns an xband to a guest.
-- Update date: 06/11/2012
-- Updated By:	Ted Crane
-- Description:	Changed call to source system link
--              to use key value pair.
-- Updated By:	Ted Crane
-- Update Version: 1.0.3.0001
-- Description:	Remove processing of xband identifier.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign] 
	@xbandId BIGINT,
	@guestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--Make sure band is only assigned to one guest.
		DELETE FROM [dbo].[guest_xband]
		WHERE [xbandId] = @xbandId 
		
		--Create new association.
		INSERT INTO [dbo].[guest_xband] ([guestID], [xbandId], [active], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@guestId, @xbandId, 1, N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())		

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END