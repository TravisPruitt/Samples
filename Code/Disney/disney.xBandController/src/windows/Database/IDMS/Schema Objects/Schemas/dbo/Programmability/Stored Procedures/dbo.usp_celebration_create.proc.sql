-- =============================================
-- Author:		Ted Crane
-- Create date: 05/18/2012
-- Description:	Create a celebration.
-- Update date: 06/19/2012
-- Updated By:	Ted Crane
-- Update Version: 1.2.0.0001
-- Description:	Updates for new schema. Only
--              Only creates primary guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_celebration_create] 
	 @celebrationId BIGINT OUTPUT
	,@xid NVARCHAR(200)
	,@name NVARCHAR(200)
	,@milestone NVARCHAR(200)
	,@type NVARCHAR(50)
	,@role NVARCHAR(50)
	,@date NVARCHAR(50)
	,@startDate NVARCHAR(50)
	,@endDate NVARCHAR(50)
	,@recognitionDate NVARCHAR(50)
	,@surpriseIndicator bit
	,@comment NVARCHAR(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @guestId BIGINT
		
		SELECT @guestId = [dbo].[ufn_GetGuestId]('xid',@xid)

		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @Type
		AND		[IDMSKey] = 'CELEBRATION'
		
		--Create celebration
		INSERT INTO [dbo].[celebration]
			([name],[milestone],[IDMSTypeId],[date],
			 [startDate],[endDate],[recognitionDate],
			 [surpriseIndicator],[comment],[active],
			 [createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@name,@milestone,@IDMSTypeID,@date,
			CONVERT(datetime,@startDate,127),CONVERT(datetime,@endDate,127),CONVERT(datetime,@recognitionDate,127),
			@surpriseIndicator,@comment,1,
			 N'IDMS',GETUTCDATE(),N'IDMS',GETUTCDATE())
			
		--Capture id
		SELECT @celebrationid = @@IDENTITY 
		
		EXECUTE [dbo].[usp_celebration_guest_add]
			@celebrationId = @celebrationId
			,@xid = @xid
			,@primaryGuest = 1
			,@role = @role

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
