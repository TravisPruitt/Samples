/****** Object:  StoredProcedure [rdr].[usp_getGuestLastLoc]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 08/29/2011
-- Description:	Pulls the  of guests in the park. 
--              As of 8/15 this means number of unique xBands in the Event table.
--              Eventually this should count guests rather than xBands and only those who have
--              entered the park and not exited. This should be available via OneView or maybe the xBRMS
-- =============================================
CREATE PROCEDURE [rdr].[usp_getGuestLastLoc]
      @GuestID varchar(16) 
      ,@GuestName varchar(100) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
   select top 1 
        @GuestName as Name
        ,e.GuestID
        ,e.Timestamp as Time
        ,ReaderLocation
        ,AttractionName
   from rdr.[Event] as e
   left join rdr.Attraction as a on e.AttractionID = a.AttractionID
   where e.GuestID = @GuestID
   order by e.Timestamp desc
    
	 
END
GO
