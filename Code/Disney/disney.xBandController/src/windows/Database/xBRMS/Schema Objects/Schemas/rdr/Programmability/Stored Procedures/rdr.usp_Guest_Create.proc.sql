-- =============================================
-- Author:		Ted Crane
-- Create date: 03/09/2012
-- Description:	Creates a Guest.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Guest_Create] 
	@GuestID bigint,
	@FirstName nvarchar(200),
	@LastName nvarchar(200),
	@EmailAddress nvarchar(200) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		IF NOT EXISTS(SELECT 'X' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
		
			BEGIN TRANSACTION
			
			INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress])
	       VALUES
			   (@GuestID
			   ,@FirstName
			   ,@LastName
			   ,@EmailAddress)
			
			COMMIT TRANSACTION
		
		END
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
