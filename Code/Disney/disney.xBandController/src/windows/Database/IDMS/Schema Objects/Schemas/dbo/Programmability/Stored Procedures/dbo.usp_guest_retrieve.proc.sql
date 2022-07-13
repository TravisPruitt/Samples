


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/13/2012
-- Description:	Retrieves a guest using and
--              identifier type and value.
-- Update date: 06/22/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Move celebrations to the end.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve]
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
		
		DECLARE @partyId BIGINT
		
		SELECT	@partyID = [partyId]
		FROM	[dbo].[party_guest] pg
		WHERE   pg.[guestId] = @guestId
		AND		pg.[createdDate] = 
		(SELECT MAX(pg1.[createdDate])
		 FROM   [dbo].[party_guest] pg1
		 WHERE	pg1.[guestid] = @guestId)

		SELECT g.[guestId]
			  ,g.[IDMSID] AS [swid]
			  ,i.[IDMSTypeName] AS [guestType]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB] AS [dateOfBirth]
			  ,g.[VisitCount]
			  ,g.[AvatarName] AS [avatar]
			  ,CASE WHEN g.[active] = 1 THEN 'Active' ELSE 'InActive' END AS [status]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,CASE WHEN g.[gender] = 'M' THEN 'MALE' ELSE 'FEMALE' END AS [gender]
			  ,g.[userName]
			  ,@partyId AS [partyId]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g
		  JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = g.[IDMSTypeId]
		  WHERE g.[guestId] = @guestId

		EXECUTE [dbo].[usp_xbands_retrieve]  
			@identifierType = @identifierType,
			@identifierValue = @identifierValue

		EXECUTE [dbo].[usp_source_system_link_retrieve] 
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



