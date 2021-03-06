USE [Simulator]
GO
/****** Object:  StoredProcedure [sim].[usp_AddGuest]    Script Date: 12/28/2011 09:40:01 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	Adds a Guest
-- =============================================
CREATE PROCEDURE [sim].[usp_AddGuest] 
	-- Add the parameters for the stored procedure here
	@GuestID bigint, 
	@LastName nvarchar(200),
	@FirstName nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	IF NOT EXISTS (SELECT 'X' FROM [sim].[Guest] WHERE [GuestID] = @GuestID)
	BEGIN
	
		INSERT INTO [sim].[Guest]
			   ([GuestID]
			   ,[LastName]
			   ,[FirstName])
		 VALUES
			   (@GuestID
			   ,@LastName
			   ,@FirstName)
	
	END 

END
GO
