-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Creates a party.
-- =============================================
CREATE PROCEDURE [dbo].[usp_party_update]
	@partyId BIGINT,
	@partyName NVARCHAR(200),
	@primaryGuestId BIGINT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		UPDATE [dbo].[party]
			SET [partyName] = @partyName,
				[primaryGuestId] = @primaryGuestId,
				[updatedBy] = 'IDMS',
				[updatedDate] = GETUTCDATE()
		WHERE [partyId] = @partyID
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
