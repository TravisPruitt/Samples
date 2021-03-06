/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/20/2011
-- Description:	Creates an Event
-- =============================================
CREATE PROCEDURE [rdr].[usp_Event_Create] 
	@BandID nvarchar(16), 
	@xPass bit,
	@VenueName nvarchar(20),
	@EventType nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@EventId int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	INSERT INTO [rdr].[Attraction]
           ([AttractionName])
    SELECT @VenueName
    WHERE NOT EXISTS
		(SELECT 'X'
		 FROM [rdr].[Attraction]
		 WHERE [AttractionName] = @VenueName)

	INSERT INTO [rdr].[EventType]
           ([EventTypeName])
    SELECT @EventType
    WHERE NOT EXISTS
		(SELECT 'X'
		 FROM [rdr].[EventType]
		 WHERE [EventTypeName] = @EventType)

	IF PATINDEX('%.%',@Timestamp) = 0
	BEGIN
	
		SET @Timestamp = SUBSTRING(@Timestamp,1,19) + '.' + SUBSTRING(@Timestamp,21,3)
	
	END


	INSERT INTO [rdr].[Event]
           ([GuestID]
           ,[xPass]
           ,[AttractionID]
           ,[EventTypeID]
           ,[ReaderLocation]
           ,[Timestamp])
    SELECT	@BandID
			,@xPass
			,a.[AttractionID]
			,et.[EventTypeID]
			,@ReaderLocation
			,CONVERT(datetime,@Timestamp,126)
    FROM	[rdr].[Attraction] a,
			[rdr].[EventType] et
    WHERE	a.[AttractionName] = @VenueName
    AND		et.[EventTypeName] = @EventType
    
    SELECT @EventId = @@IDENTITY

END
GO
