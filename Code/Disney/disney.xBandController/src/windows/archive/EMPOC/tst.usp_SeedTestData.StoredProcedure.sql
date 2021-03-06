/****** Object:  StoredProcedure [tst].[usp_SeedTestData]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/27/2011
-- Description:	Seed test data
-- =============================================
CREATE PROCEDURE [tst].[usp_SeedTestData] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

		DECLARE @AttractionName nvarchar(200)
		DECLARE @GuestCount int
		DECLARE @QueueWait int
		DECLARE @xPass bit

		SET @AttractionName = 'xCoaster'
		SET @GuestCount = 15
		SET @QueueWait = 60
		SET @xPass = 1

		EXECUTE [tst].[usp_StartSimulation] 
		   @AttractionName
		  ,@GuestCount
		  ,@QueueWait
		  ,@xPass

		SET @GuestCount = 85
		SET @QueueWait = 120
		SET @xPass = 0

		EXECUTE [tst].[usp_StartSimulation] 
		   @AttractionName
		  ,@GuestCount
		  ,@QueueWait
		  ,@xPass

		SET @GuestCount = 30
		SET @QueueWait = 180
		SET @xPass = 1

		EXECUTE [tst].[usp_StartSimulation] 
		   @AttractionName
		  ,@GuestCount
		  ,@QueueWait
		  ,@xPass

		SET @GuestCount = 170
		SET @QueueWait = 360
		SET @xPass = 0

		EXECUTE [tst].[usp_StartSimulation] 
		   @AttractionName
		  ,@GuestCount
		  ,@QueueWait
		  ,@xPass

		SET @GuestCount = 60
		SET @QueueWait = 600
		SET @xPass = 1

		EXECUTE [tst].[usp_StartSimulation] 
		   @AttractionName
		  ,@GuestCount
		  ,@QueueWait
		  ,@xPass

		SET @GuestCount = 340
		SET @QueueWait = 1200
		SET @xPass = 0

		EXECUTE [tst].[usp_StartSimulation] 
		   @AttractionName
		  ,@GuestCount
		  ,@QueueWait
		  ,@xPass


END
GO
