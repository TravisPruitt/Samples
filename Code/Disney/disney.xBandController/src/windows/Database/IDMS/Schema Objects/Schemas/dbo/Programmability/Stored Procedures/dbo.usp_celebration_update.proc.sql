-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Updates a celebration.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_update] 
	 @celebrationId BIGINT
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @type
		AND		[IDMSKey] = 'CELEBRATION'
		
		--Update celebration
		UPDATE [dbo].[celebration]
			SET [name] = @name,
				[milestone] = @milestone,
				[date] = @date,
				[startDate] = @startDate,
				[endDate] = @endDate,
				[recognitionDate] = @recognitionDate,
				[surpriseIndicator] = @surpriseIndicator,
				[comment] = @comment,
				[IDMSTypeId] = @IDMSTypeId,
				[updatedBy] = 'IDMS',
				[updatedDate] = GETUTCDATE()
		WHERE [celebrationId] = @celebrationId
		
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
