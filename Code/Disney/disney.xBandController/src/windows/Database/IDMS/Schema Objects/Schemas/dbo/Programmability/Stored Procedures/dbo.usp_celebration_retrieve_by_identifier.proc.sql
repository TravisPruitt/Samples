-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Retreive all the celebrations
--              for a guest.
-- Update date: 06/14/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Changed to use view.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Update to include guests
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_retrieve_by_identifier] 
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

		SELECT   v.*
		FROM	[dbo].[vw_celebration] v
		JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = v.[celebrationId]
		WHERE	cg.[guestId]= @guestId
	
		SELECT   v.*
		FROM	[dbo].[vw_celebration_guest] v
		WHERE	v.[guestId] = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   
END
