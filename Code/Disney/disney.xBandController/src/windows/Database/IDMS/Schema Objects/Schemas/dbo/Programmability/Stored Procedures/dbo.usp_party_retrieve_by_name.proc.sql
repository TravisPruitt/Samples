-- =============================================
-- Author:		Scott Salley
-- Create date: 06/01/2012
-- Description:	Retrieves a party using the 
--              party name.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Corrected party count.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_retrieve_by_name]
	@partyName nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @partyCount INT
		DECLARE @partyId BIGINT

        SELECT  @partyId = p.[partyId]
        FROM    [dbo].party p
        WHERE   p.[partyName] = @partyName
        
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
