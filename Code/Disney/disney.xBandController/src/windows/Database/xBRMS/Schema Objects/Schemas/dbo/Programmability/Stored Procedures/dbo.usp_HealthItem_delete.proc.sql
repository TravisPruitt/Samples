
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Retrieves the performance metrics
--              using the specified parameters.
-- =============================================
CREATE PROCEDURE [dbo].[usp_HealthItem_delete]
	@Id int
	
AS
BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
		
		DELETE FROM [dbo].[PerformanceMetric] 
		WHERE [HealthItemID] = @Id
		
		DELETE FROM [dbo].[HealthItem]
		WHERE [id] = @Id
		
		COMMIT TRANSACTION
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
		-- Call the procedure to raise the original error.
		EXEC usp_RethrowError;
	END CATCH
END
