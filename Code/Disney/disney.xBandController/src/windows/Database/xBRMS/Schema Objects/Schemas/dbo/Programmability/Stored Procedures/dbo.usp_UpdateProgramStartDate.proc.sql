-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Update program start date used as 1st date of "todate" calculations
-- Update Version: 1.3.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_UpdateProgramStartDate]
@pstartdate varchar(25)
AS
BEGIN
DECLARE @psd varchar(25)
   
   		IF NOT EXISTS(SELECT 'X' FROM [dbo].[config] 
            WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig')
		BEGIN
			INSERT INTO [dbo].[config]
                
                ([property],
                [value],
                [class])
	       VALUES
			   ('DATA_START_DATE',
			   @pstartdate,
			   'XiConfig')
		END
        ELSE 
            UPDATE [dbo].[config]
            SET [value] = @pstartdate
            WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
END
