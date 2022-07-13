


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Adds a guest to a party.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_guest_create]
	@partyId BIGINT,
	@guestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		IF NOT EXISTS(SELECT 'X' FROM [dbo].[party_guest] WHERE [partyId] = @partyId AND [guestId] = @guestId)
		BEGIN
				
			DECLARE @IDMSTypeId INT
		
			SELECT @IDMSTypeId = [IDMSTypeId]
			FROM	[dbo].[IDMS_Type]
			WHERE	[IDMSTypeName] = 'guest party'
			AND		[IDMSkey] = 'PARTYTYPE'

			INSERT INTO [dbo].[party_guest]	([partyId], [guestId], [IDMSTypeId], 
				[createdBy], [createdDate], [updatedBy], [updatedDate])
			VALUES (@partyId, @guestId, @IDMSTypeId, 'IDMS', GETUTCDATE(), 'IDMS', GETUTCDATE())
		
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
