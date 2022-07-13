-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	add subway diagram to db
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_AddSubwayDiagram]
@FacilityName int = NULL,
@data nvarchar(max) = NULL 
AS
BEGIN
    DECLARE @facilityId int

    SELECT	@FacilityID = [FacilityID] 
    FROM	[rdr].[Facility] 
    WHERE	[FacilityName] = @FacilityName
            
    IF @FacilityID IS NULL
    BEGIN
        INSERT INTO [rdr].[Facility]
               ([FacilityName]
               ,[FacilityTypeID])
        VALUES 
                (@FacilityName
                ,NULL)
        SET @FacilityID = @@IDENTITY
    END

    INSERT INTO [gxp].[XiSubwayDiagrams]
    VALUES(@FacilityID, @data, getdate());

    SELECT @@IDENTITY
END
