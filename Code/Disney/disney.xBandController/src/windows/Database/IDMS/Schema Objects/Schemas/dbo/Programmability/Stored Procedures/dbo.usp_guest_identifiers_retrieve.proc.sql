-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retreive all the identifiers 
--              for a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_identifiers_retrieve] 
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId](@identifierType,@identifierValue)

		SELECT   i.[IDMSTypeName] AS [type]
				,s.[sourceSystemIdValue] AS [value]
				,s.[guestId]
		FROM	[dbo].[source_system_link] s
		JOIN	[dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE	s.[guestid] = @guestId


	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
