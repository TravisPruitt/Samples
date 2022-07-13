

-- =============================================
-- Author:		Ted Crane
-- Create date: 05/30/2012
-- Description:	Deletes a source system 
--              link record.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_delete] 
	@guestId bigint,
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		--TODO: Check IDMSTypeID to make sure type is source system type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = 'SOURCESYSTEM'

		DELETE FROM [dbo].[source_system_link]
		WHERE [guestId] = @guestid
		AND	  [sourceSystemIdValue] = @sourceSystemIdValue
		AND   [IDMSTypeId] = @IDMSTypeID

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


