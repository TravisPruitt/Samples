-- =============================================
-- Author:		Ted Crane
-- Create date: 08/28/2012
-- Description:	Gets Guest Data from IDMS
-- =============================================
CREATE PROCEDURE [dbo].[usp_Guest_update] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress]
			   ,[CelebrationType]
			   ,[RecognitionDate]
			   ,[GuestType])
	SELECT DISTINCT [guestId]
		  ,[firstname]
		  ,[lastName]
		  ,[emailaddress]
		  ,[CelebrationType]
		  ,[recognitionDate]
		  ,[GuestType]
	  FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	  WHERE NOT EXISTS
	  (SELECT 'X'
	   FROM [rdr].[Guest] g
	   WHERE g.[GuestID] = vg.[guestId])
	   
	UPDATE [rdr].[Guest]
	SET [CelebrationType] = vg.[CelebrationType]
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[CelebrationType] <> [Guest].[CelebrationType] 

	UPDATE [rdr].[Guest]
	SET [RecognitionDate] = vg.[RecognitionDate] 
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[RecognitionDate]  <> [Guest].[RecognitionDate] 

	UPDATE [rdr].[Guest]
	SET [GuestType] = vg.[GuestType] 
	FROM [IDMS_Prod].[dbo].[vw_xi_guest] vg
	WHERE vg.[guestId] = [Guest].[GuestID]
	AND vg.[GuestType]  <> [Guest].[GuestType] 
	
END