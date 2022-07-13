-- =============================================
-- Author:		Ted Crane
-- Create date: 05/11/2012
-- Description:	Gets the guestId for an identifier key/value pair.
-- Update date: 06/13/2012
-- Updated By:	Ted Crane
-- Update Version: 1.0.2.0001
-- Description:	Added SWID and xid.
-- =============================================
CREATE FUNCTION [dbo].[ufn_GetGuestId] 
(
	@identifierType NVARCHAR(200),
	@identifierValue NVARCHAR(50)
)
RETURNS BIGINT
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Result BIGINT
	
	IF @identifierType = 'guestid'
	BEGIN
		SET @Result = CONVERT(BIGINT,@identifierValue)
	END
	ELSE IF @identifierType = 'xbandid'
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest_xband] gx
		WHERE gx.[xbandid] = @identifierValue

	END
	ELSE IF @identifierType = 'swid'
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[guest] g
		WHERE g.[IDMSID] = @identifierValue

	END
	ELSE
	BEGIN

		SELECT @Result = [guestid]
		FROM [dbo].[source_system_link] s
		JOIN [dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
		WHERE s.[sourceSystemIdValue] = @identifierValue
		AND	  i.[IDMSTypeName] = @identifierType
		AND   i.[IDMSKey] = 'SOURCESYSTEM'
	END
	
	-- Return the result of the function
	RETURN @Result

END
