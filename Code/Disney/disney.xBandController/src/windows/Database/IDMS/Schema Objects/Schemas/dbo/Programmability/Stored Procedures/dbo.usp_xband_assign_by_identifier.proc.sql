-- =============================================
-- Author:		Ted Crane
-- Create date: 07/13/2012
-- Description:	Assigns an xband to a guest.
-- Version: 1.0.3.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_assign_by_identifier] 
	@xbandId BIGINT,
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

		EXECUTE [dbo].[usp_xband_assign] 
		   @xbandId = @xbandId
		  ,@guestId = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END