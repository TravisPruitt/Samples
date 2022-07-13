-- =============================================
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Inserts a performance metric meta data.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetricDesc_create] 
	@Name nvarchar(50),
	@DisplayName nvarchar(50),
	@Description nvarchar(MAX),
	@Units nvarchar(20),
	@Version nvarchar(20),
	@Source nvarchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @ExistingDescID int
		
		SELECT @ExistingDescID = pmd.[PerformanceMetricDescID]
		FROM [dbo].[PerformanceMetricDesc] pmd
		WHERE pmd.[PerformanceMetricName] = @Name AND 
				pmd.[PerformanceMetricVersion] = @Version AND
				pmd.[PerformanceMetricSource] = @Source
		
		IF @ExistingDescID IS NULL
			BEGIN
				INSERT INTO [dbo].[PerformanceMetricDesc](
						[PerformanceMetricName],
						[PerformanceMetricDisplayName],
						[PerformanceMetricDescription],
						[PerformanceMetricUnits],
						[PerformanceMetricVersion],
						[PerformanceMetricCreateDate],
						[PerformanceMetricSource]) 
					VALUES (
						@Name,
						@DisplayName,
						@Description,
						@Units,
						@Version,
						GETDATE(),
						@Source )
			END
		ELSE
			BEGIN
				UPDATE [dbo].[PerformanceMetricDesc]
				SET [PerformanceMetricDisplayName] = @DisplayName,
					[PerformanceMetricDescription] = @Description,
					[PerformanceMetricUnits] = @Units
				WHERE [PerformanceMetricName] = @Name AND 
						[PerformanceMetricVersion] = @Version AND
						[PerformanceMetricSource] = @Source
			END  
		
		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
