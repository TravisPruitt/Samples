-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates the mapping between Tables, Guests, and Orders
-- Update Version: 1.3.0.0005
-- =============================================
CREATE PROCEDURE [dbo].[usp_TableGuestOrderMap_Create] 
    @OrderNumber nvarchar(50),
    @TableId int,
	@BusinessEventID int
AS
BEGIN
    SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @OrderId int

        SELECT @OrderId = [OrderId]
        FROM [gxp].[RestaurantOrder]
        WHERE [OrderNumber] = @OrderNumber;

        INSERT INTO [gxp].[TableGuestOrderMap]
                   (
                   [RestaurantTableId]
                   ,[OrderId]
                   ,[BusinessEventId]
                   )
             VALUES
                   (
                   @TableId
                   ,@OrderId
                   ,@BusinessEventID
                   )

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END