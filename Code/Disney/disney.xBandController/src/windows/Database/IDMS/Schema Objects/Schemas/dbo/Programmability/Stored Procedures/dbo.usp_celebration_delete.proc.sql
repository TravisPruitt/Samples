-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Delete a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_delete] 
	@celebrationId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		--Deactivate (Soft delete) celebration
		UPDATE [dbo].[celebration]
			SET [active] = 0,
				[updatedBy] = 'IDMS',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
