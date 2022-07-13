


-- =============================================
-- Author:		Ted Crane
-- Create date: 06/21/2012
-- Description:	Retrieves guest data information.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_data_retrieve]
	@identifierType NVARCHAR(50),
	@identifierValue NVARCHAR(200),
	@startDate NVARCHAR(50),
	@endDate NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		EXECUTE [dbo].[usp_source_system_link_retrieve] 
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_xbands_retrieve]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_celebration_retrieve_by_identifier]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



