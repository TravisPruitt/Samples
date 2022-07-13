/****** Object:  StoredProcedure [med].[usp_MediaFile_Sent]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 08/04/2011
-- Description:	Sets a Media File Entry to sent
-- =============================================
CREATE PROCEDURE [med].[usp_MediaFile_Sent] 
	@MediaFileID int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	UPDATE [med].[MediaFile]
	   SET [MediaFileStatusID] = 2
	 WHERE [MediaFileID] = @MediaFileID


END
GO
