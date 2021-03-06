USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_ClearGuestQueue]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	Clears Guest Pool
-- =============================================
CREATE PROCEDURE [sim].[usp_ClearGuestQueue] 
	 @AttractionID int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRANSACTION

	BEGIN TRY
	
		DELETE FROM [sim].[GuestQueue] WHERE [AttractionID] = @AttractionID
			   
		COMMIT TRANSACTION
		
	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
	END CATCH	   
END
GO
