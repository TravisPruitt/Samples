


-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves guest by searching.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_search]
	@searchString NVARCHAR(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @addressTypeId INT
		
		SELECT @addressTypeId = [IDMSTypeID]
		FROM	[dbo].[IDMS_Type] i
		WHERE	[IDMSTypeName] = 'HOME ADDRESS'
	
		SELECT TOP 50
				 g.[guestId]
				,p.[primaryGuestId]
				,p.[partyName]
				,p.[partyId]
				,(SELECT COUNT(*) FROM [dbo].[party_guest] pg1 WHERE pg1.[partyId] = p.[partyID]) AS [count]
				,g.[emailAddress]
				,g.[lastname]
				,g.[firstname]
				,a.[address1]
				,a.[address2]
				,a.[address3]
				,a.[city]
				,a.[state]
				,a.[postalCode]
		FROM	[dbo].[guest] g 
		LEFT JOIN [dbo].[guest_address] a on a.[guestId] = g.[guestId]
			AND a.[IDMSTypeID] = @addressTypeId 
		JOIN	[dbo].[IDMS_Type] i on i.[IDMSTypeId] = g.[IDMSTypeId] 
		LEFT JOIN	[dbo].[party_guest] pg on pg.[guestId] = g.[guestId] 
		LEFT JOIN	[dbo].[party] p ON p.[partyId] = pg.[partyId]
		WHERE (g.firstName + ' ' + g.lastName) LIKE '%' + @searchString + '%'
		ORDER BY g.[createdDate] DESC
	
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END



