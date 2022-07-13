-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	checks if GUID is next in sequence and thus valid
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_CheckGUIDForHTMLUpdate] 
    @InputGUID nvarchar(40)
AS
DECLARE @DateCreated datetime,
	@MaxPageSourceGUID int,
    @NextGUID nvarchar(40),
    @NextGUIDId int,
    @ReturnValue int 
BEGIN
    -- need to first check if the GUID is next in sequence
    -- assumes we have sequence, no gaps, in GUID numbering

    SET @ReturnValue = -1;

	SELECT Top 1 @MaxPageSourceGUID=XiGUIDId
        FROM [gxp].[XiPageSource]
        ORDER BY XiPageId DESC

	if @MaxPageSourceGUID IS NULL
	BEGIN
		SET @MaxPageSourceGUID=0
	END

    SELECT @NextGUID=GUID,
        @NextGUIDId=XiGUIDId
    FROM [gxp].[XiPageGUIDs]
    WHERE XiGUIDId = @MaxPageSourceGUID + 1;

    IF @InputGUID = @NextGUID
    BEGIN
        SET @ReturnValue=1
    END
    select @ReturnValue
END
