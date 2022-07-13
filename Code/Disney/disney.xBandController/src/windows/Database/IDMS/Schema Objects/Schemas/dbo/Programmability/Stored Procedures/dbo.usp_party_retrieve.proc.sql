-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a party using the 
--              party ID.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Corrected party count.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_retrieve]
	@partyId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @partyCount INT
		
		SELECT	@partyCount = COUNT(*)
		FROM	[dbo].[party_guest] pg
		WHERE	pg.[partyId] = @partyId	
		
		SELECT	 p.[partyId]
				,p.[primaryGuestId]
				,p.[partyName]
				,@partyCount AS [count]
		FROM	[dbo].[party] p
		WHERE	p.[partyId] = @partyId	


		SELECT	 pg.[guestId]
				,CASE WHEN pg.[guestId] = p.[primaryGuestId] THEN 1 ELSE 0 END AS [isPrimary]
		FROM	[dbo].[party_guest] pg
		JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE	pg.[partyId] = @partyId	

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
