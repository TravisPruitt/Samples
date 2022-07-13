-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Creates a Guest Order Mapping
-- Update Version: 1.3.0.0005
-- =============================================

CREATE PROCEDURE [dbo].[usp_GuestOrderMap_Create] 
	@OrderNumber nvarchar(50)
    ,@GuestId bigint
	,@BusinessEventID int
AS
BEGIN
    SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
        DECLARE @OrderId int

		SELECT	@OrderID = [OrderID] 
		FROM	[gxp].[RestaurantOrder] 
		WHERE	[OrderNumber] = @OrderNumber
				
        INSERT INTO [gxp].[GuestOrderMap]
                   (
                   [BusinessEventId]
                   ,[GuestId]
                   ,[OrderId]
                   )
             VALUES
                   (
                   @BusinessEventID
                   ,@GuestId
                   ,@OrderID
                   )

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
                EXEC usp_RethrowError;

	END CATCH	   

END