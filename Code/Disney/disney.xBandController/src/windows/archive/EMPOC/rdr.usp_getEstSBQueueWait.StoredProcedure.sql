/****** Object:  StoredProcedure [rdr].[usp_getEstSBQueueWait]    Script Date: 08/31/2011 14:16:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 08/15/2011
-- Description:	Pulls the predicted wait time (minutes) of guests in the standby (SB) queue
--              Actual wait is period between ENTRY event time and LOAD event time. 
-- =============================================
CREATE PROCEDURE [rdr].[usp_getEstSBQueueWait] 
	 @VenueName nvarchar(20)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
    Declare @vID int
    Declare @mins int, @i int, @accum int
    
    select @vID = a.AttractionID
    from rdr.Attraction as a 
    where a.AttractionName = @VenueName
    
    -- As of 8/15/2011 this is a static, hardcoded number until we have a formula for a dynamice model.
	select 5.0 as EstSBWait
	
END
GO
