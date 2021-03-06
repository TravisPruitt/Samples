/****** Object:  StoredProcedure [rdr].[usp_getAttractionsList]    Script Date: 08/31/2011 14:16:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Neal Oman
-- Create date: 08/12/2011
-- Description:	Pulls the current list of attractions with status.
-- =============================================
CREATE PROCEDURE [rdr].[usp_getAttractionsList] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
    
    select 
      atr.AttractionID
      ,atr.AttractionName
      ,atr.AttractionStatus
      ,atr.XPQueueCap
      ,atr.SBQueueCap
    from rdr.Attraction as atr
    
	 
END
GO
