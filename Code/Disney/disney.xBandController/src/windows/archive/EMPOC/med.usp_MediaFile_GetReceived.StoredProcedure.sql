/****** Object:  StoredProcedure [med].[usp_MediaFile_GetReceived]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 08/04/2011
-- Description:	Creates a Media File Entry
-- =============================================
CREATE PROCEDURE [med].[usp_MediaFile_GetReceived] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	SELECT m.* 
	FROM  [med].[MediaFile] m
	WHERE m.[MediaFileStatusID] = 1

END
GO
