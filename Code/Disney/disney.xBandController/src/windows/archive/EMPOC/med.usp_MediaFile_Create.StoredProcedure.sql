/****** Object:  StoredProcedure [med].[usp_MediaFile_Create]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 08/04/2011
-- Description:	Creates a Media File Entry
-- =============================================
CREATE PROCEDURE [med].[usp_MediaFile_Create] 
	-- Add the parameters for the stored procedure here
	@FilePath nvarchar(256),
	@Timestamp datetime
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	IF NOT EXISTS (SELECT 'X' FROM [med].[MediaFile] WHERE [FilePath] = @FilePath AND [MediaFileStatusID] = 1) -- Received)
	BEGIN
	
		INSERT INTO [med].[MediaFile]
			([Timestamp]
			,[FilePath]
			,[MediaFileStatusID])
		VALUES
			(CONVERT(datetime,@Timestamp,126)
			,@FilePath
			,1) -- Received

	END
END
GO
