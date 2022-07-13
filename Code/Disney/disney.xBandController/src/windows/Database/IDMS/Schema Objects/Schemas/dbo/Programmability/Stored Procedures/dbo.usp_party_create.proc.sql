


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Creates a party.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_create]
	@partyName NVARCHAR(200),
	@primaryGuestId BIGINT,
	@partyId BIGINT OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		INSERT INTO [dbo].[party] ([primaryGuestId], [partyName], [count], [createdBy], [createdDate], [updatedBy], [updatedDate])
		VALUES (@primaryGuestId, @partyName, 0, 'IDMS', GETUTCDATE(), 'IDMS', GETUTCDATE())
		
		SELECT @partyId = @@IDENTITY
		
		EXECUTE [dbo].[usp_party_guest_create]
			@partyId = @partyId,
			@guestId = @primaryGuestId	

		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



