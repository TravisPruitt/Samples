-- =============================================
-- Author:		Ted Crane
-- Create date: 05/21/2012
-- Description:	Retrieves an xband.
-- Update date: 07/16/2012
-- Updated By:	Ted Crane
-- Update Version: 1.3.0.0001
-- Description:	Add retrieval by publicId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_retrieve] 
	@bandLookupType int,
	@id nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @xbandid bigint

		-- Must match BandLookupType in Java code
		IF @bandLookupType = 0
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[xbandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[xbandid] = @id
		END
		ELSE IF @bandLookupType = 1
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[bandid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[bandid] = @id
		END	
		ELSE IF @bandLookupType = 2
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[longrangeid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[longrangeid] = @id
		END	
		ELSE IF @bandLookupType = 3
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[tapid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[tapid] = @id
		END	
		ELSE IF @bandLookupType = 4
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[secureid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[secureid] = @id
		END	
		ELSE IF @bandLookupType = 5
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[uid] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[uid] = @id
		END	
		ELSE IF @bandLookupType = 6
		BEGIN
			SELECT	x.*
			FROM	[dbo].[vw_xband] x
			WHERE	x.[publicId] = @id

			SELECT	gx.*
			FROM	[dbo].[vw_guest_xband] gx
			WHERE	gx.[publicId] = @id
		END	
	           
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END