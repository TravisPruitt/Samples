/****** Object:  StoredProcedure [tst].[usp_SeedGuests]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Seeds the Guest table for the configuration.
-- =============================================
CREATE PROCEDURE [tst].[usp_SeedGuests]
	 @GuestCount int
	,@ConfigurationID int
	,@StartTime datetime
	,@QueueWait int
	,@xPass bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @Counter int
	DECLARE @EntryTimeMax int
	DECLARE @WaitTimeDelta int
	DECLARE @MergeTimeDelta int
	DECLARE @LoadTimeDelta int
	DECLARE @TotalTimeMax int
	
	--Ride time is 120 seconds
	SET @TotalTimeMax = @QueueWait + 120
		
	SET @EntryTimeMax = @QueueWait / 10

	SET @WaitTimeDelta = 3 * (@QueueWait /10)
	
	SET @MergeTimeDelta = 3 * (@QueueWait / 10)

	SET @LoadTimeDelta = 3 * (@QueueWait / 10)

	SET @Counter = 0

	WHILE @Counter < @GuestCount
	BEGIN

		DECLARE @TempString nvarchar(36)
		DECLARE @GuestID nvarchar(16)
		DECLARE @EntryTime int
		DECLARE @WaitTime int
		DECLARE @MergeTime int
		DECLARE @LoadTime int
		DECLARE @TotalTime int
		
		SET @TempString = CONVERT(nvarchar(36), NEWID())

		SET @GuestID = LOWER(SUBSTRING(@TempString,1,8) + SUBSTRING(@TempString,10,4) + SUBSTRING(@TempString,15,4))

		SELECT @EntryTime = ROUND((@EntryTimeMax * RAND()), 0)

		SELECT @WaitTime = @WaitTimeDelta + @EntryTime
		SELECT @MergeTime = @MergeTimeDelta + @WaitTime
		SELECT @LoadTime = @LoadTimeDelta + @MergeTime
		
		IF @LoadTime > @QueueWait
		BEGIN
		
			SET @LoadTime = @QueueWait
		
		END
		
		SELECT @TotalTime = @LoadTime + 120
		
		INSERT INTO [tst].[Guest]
			   ([GuestID]
			   ,[ConfigurationID]
			   ,[StartTime]
			   ,[xPass]
			   ,[EntryTime]
			   ,[WaitTime]
			   ,[MergeTime]
			   ,[LoadTime]
			   ,[TotalTime])
		 VALUES
			   (@GuestID
			   ,@ConfigurationID
			   ,@StartTime
			   ,@xPass 
			   ,@EntryTime
			   ,@WaitTime
			   ,@MergeTime
			   ,@LoadTime
			   ,@TotalTime)

		SET @Counter = @Counter + 1

	END

END
GO
