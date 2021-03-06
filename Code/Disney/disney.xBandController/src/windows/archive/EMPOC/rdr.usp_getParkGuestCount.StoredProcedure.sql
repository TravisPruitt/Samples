/****** Object:  StoredProcedure [rdr].[usp_getParkGuestCount]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 08/15/2011
-- Description:	Pulls the count of guests in the park. 
--              As of 8/15 this means number of unique xBands in the Event table.
--              Eventually this should count guests rather than xBands and only those who have
--              entered the park and not exited. This should be available via OneView or maybe the xBRMS
-- =============================================
CREATE PROCEDURE [rdr].[usp_getParkGuestCount] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	declare @ParkName varchar(200) = 'Magic Kingdom Seattle'
	declare @Forecast int = 300
	
   select @ParkName as ParkName
          ,count(distinct GuestID) as CurrAttendance
          ,@Forecast as ForecastAttendance
   from rdr.[Event]   
	 
END
GO
