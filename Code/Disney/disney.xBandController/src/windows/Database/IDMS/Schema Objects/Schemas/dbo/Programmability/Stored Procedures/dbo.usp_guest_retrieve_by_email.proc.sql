
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retreives a guest
--				that has the specified
--              email address.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve_by_email]
	@emailAddress NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		DECLARE @guestId BIGINT

		SELECT  @guestId = g.[guestId]
		FROM	[dbo].[guest] g
		WHERE	g.[emailAddress] = @emailAddress
		AND		g.[active] = 1
		AND		g.[createddate] =
		(SELECT MAX(g1.[createddate])
		 FROM [dbo].[guest] g1
		 WHERE g1.[emailAddress] = g.[emailAddress]
		 AND   g1.[active] = 1)
		
		EXECUTE [dbo].[usp_guest_retrieve] 
			@identifierType = 'guestId', 
			@identifierValue = @guestId

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

