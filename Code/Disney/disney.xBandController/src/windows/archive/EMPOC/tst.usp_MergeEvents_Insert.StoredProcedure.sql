/****** Object:  StoredProcedure [tst].[usp_MergeEvents_Insert]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Inserts Merge Events
-- =============================================
CREATE PROCEDURE [tst].[usp_MergeEvents_Insert] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRANSACTION
	
	BEGIN TRY
		
	INSERT INTO [rdr].[Event]
           ([GuestID]
           ,[xPass]
           ,[AttractionID]
           ,[EventTypeID]
           ,[ReaderLocation]
           ,[Timestamp])
 	SELECT g.[GuestID]
		  ,g.[xPass]
		  ,c.[AttractionID]
		  ,2 [EventTypeID] -- MERGE
		  ,''
		  ,DATEADD(SECOND,g.[MergeTime],g.[StartTime])
	  FROM [tst].[Guest] g
	  JOIN [tst].Configuration c ON c.[ConfigurationID] = g.[ConfigurationID]
	  WHERE c.[IsExecuting] = 1
	  AND	g.[xPass] = 1
	  AND	DATEDIFF(SECOND,DATEADD(SECOND,g.[MergeTime],g.[StartTime]), GETDATE()) > 0
	  AND NOT EXISTS
	  (SELECT 'X'
	   FROM [rdr].[Event] e
	   WHERE e.[GuestID] = g.[GuestID]
	   AND	 e.[xPass] = g.[xPass]
	   AND	 e.[AttractionID] = c.[AttractionID]
	   AND	 e.EventTypeID = 2)
	  AND EXISTS
	  (SELECT 'X'
	   FROM [rdr].[Event] e
	   WHERE e.[GuestID] = g.[GuestID]
	   AND	 e.[xPass] = g.[xPass]
	   AND	 e.[AttractionID] = c.[AttractionID]
	   AND	 e.EventTypeID = 1)
	   
		COMMIT TRANSACTION
   
	   END TRY
	   BEGIN CATCH
	   
			ROLLBACK TRANSACTION
	   
	   END CATCH
	   
	   
END
GO
