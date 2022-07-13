
-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Retreives the name of a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_name_retrieve]
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

		SELECT   [firstName]
				,[lastName]
				,[middleName]
				,[title]
				,[suffix]
		FROM	[dbo].[guest] g
		WHERE	g.[guestid] = @guestId
		AND		g.[active] = 1

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

