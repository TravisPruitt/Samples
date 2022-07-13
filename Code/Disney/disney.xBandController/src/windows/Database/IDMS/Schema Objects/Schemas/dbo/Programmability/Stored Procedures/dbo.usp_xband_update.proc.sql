
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/14/2012
-- Description:	Update xband record
-- Update date: 09/27/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.4.0002
-- Description:	Added check to raise an errror
--              when no update was made.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_update] (
	 @xbandId BIGINT,
	 @active bit = 1,
	 @bandType NVARCHAR(50) = NULL)
	   
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	---- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @IDMSTypeID int
		
		IF @bandType IS NULL
		BEGIN
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[xband]
			WHERE	[xbandid] = @xbandid
		END
		ELSE
		BEGIN
		
			SELECT	@IDMSTypeID = [IDMSTypeID] 
			FROM	[dbo].[IDMS_Type] 
			WHERE	[IDMSTypeName] = @bandType
			AND		[IDMSKey] = 'BANDTYPE'
		END
		
		UPDATE [dbo].[xband]
			SET [active] = @active,
				[IDMSTypeID] = 	@IDMSTypeID,
				[updatedby] = 'IDMS',
				[updateddate] = GETUTCDATE()
		WHERE [xbandid] = @xbandId

		IF @@ROWCOUNT = 0
		BEGIN
		
			DECLARE @xbandIdString nvarchar(20)
			
			SET @xbandIdString = CAST(@xbandId as nvarchar(20))

			RAISERROR
				(N'No Band was found for xbandId: %s',
				17, -- Severity.
				1, -- State.
				@xbandIdString);

		END
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END

