/****** Object:  StoredProcedure [tst].[usp_StartSimulation]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Starts a simulation of users through an attraction.
-- Modified by: Neal Oman - added the ability to specify start time with an input parameter.
-- =============================================
CREATE PROCEDURE [tst].[usp_StartSimulation] 
	 @AttractionName nvarchar(200)
	,@GuestCount int
	,@QueueWait int
	,@xPass bit
	,@inStartTime datetime = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @AttractionID int
	
	SELECT @AttractionID = [AttractionID] FROM [rdr].[Attraction]
		 WHERE [AttractionName] = @AttractionName
		 
	IF @AttractionID IS NULL
	BEGIN
	
		INSERT INTO [rdr].[Attraction]
			   ([AttractionName])
		VALUES
			(@AttractionName)

		SELECT @AttractionID = @@IDENTITY
	
	END
	
	if @inStartTime is NULL
	BEGIN
	   select @inStartTime = GETDATE();
	END
		 
	INSERT INTO [tst].[Configuration]
           ([AttractionID]
           ,[GuestCount]
           ,[QueueWait]
           ,[StartTime]
           ,[IsExecuting]
           ,[LastUpdated])
     VALUES
           (@AttractionID
           ,@GuestCount
           ,@QueueWait
           ,@inStartTime
           ,1
           ,GETDATE())
           
	DECLARE @ConfigurationID int

	SELECT @ConfigurationID = @@IDENTITY
	
	EXEC [tst].[usp_SeedGuests] 
		 @GuestCount = @GuestCount
		,@ConfigurationID = @ConfigurationID
		,@StartTime = @inStartTime
		,@QueueWait = @QueueWait
		,@xPass = @xPass

END
GO
