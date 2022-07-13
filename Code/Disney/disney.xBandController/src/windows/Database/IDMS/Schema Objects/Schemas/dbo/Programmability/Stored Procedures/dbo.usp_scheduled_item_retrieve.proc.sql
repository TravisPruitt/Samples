


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves the itineraries for a 
--              guest using an identifier
--              type\value pair. 
-- =============================================
CREATE PROCEDURE [dbo].[usp_scheduled_item_retrieve]
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
				
		SELECT s.*
		  FROM [dbo].[guest_scheduledItem] gs
		  JOIN [dbo].[scheduledItem] s ON s.[scheduledItemId] = gs.[scheduledItemId]
		  JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		  WHERE gs.[guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



