
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Get all the identifier key
--              value pairs for a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestID BIGINT

		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)
	
		SELECT	 i.[IDMSTypeName] as [type]
				,s.[sourceSystemIdValue] as [value]
				,s.[guestId] as [guestId] 
		FROM [dbo].[source_system_link] s 
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId] 
		WHERE s.[guestId] = @guestId 
		AND i.[IDMSKEY] = 'SOURCESYSTEM'

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
	
END

