-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_ConfigPersistParam]
@paramName varchar(25),
@paramValue varchar(1024)
AS
BEGIN
    DECLARE @value varchar(1024)

    SELECT @value=[value] FROM [dbo].[config]
    WHERE [property] = @paramName and [class] = 'XiConfig'
    
    IF @value IS NULL
    BEGIN
        INSERT INTO [dbo].[config]
        VALUES('XIConfig', @paramName, @paramValue)
    END
    ELSE 
        UPDATE [dbo].[config]
        SET [value] = @paramValue
        WHERE [property] = @paramName and [class] = 'XiConfig'
END
