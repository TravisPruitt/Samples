USE [IDMS]
GO

/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 11/28/2011 10:55:53 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestUser]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateTestUser]
GO

USE [IDMS]
GO

/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 11/28/2011 10:55:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateTestUser] 
	-- Add the parameters for the stored procedure here
	@NumberOfUsers int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @FirstNamesCount int
	DECLARE @LastNamesCount int
	DECLARE @FirstNameID int
	DECLARE @LastNameID int
	DECLARE @LastName nvarchar(200)
	DECLARE @FirstName nvarchar(200)
	DECLARE @GuestID bigint
	DECLARE @xBandID bigint
	DECLARE @BandID nvarchar(16)
	DECLARE @TapID nvarchar(16)
	DECLARE @LongRangeID nvarchar(16)
	DECLARE @IntVal bigint
	
	DECLARE @Index int

	SET @Index = 0
	
	SELECT @FirstNamesCount = COUNT(*) FROM [dbo].[FirstNames]
	
	SELECT @LastNamesCount = COUNT(*) FROM [dbo].[LastNames]
	
	WHILE @Index < @NumberOfUsers 
	BEGIN
	
		SET @FirstNameID = CAST(RAND() * @FirstNamesCount as int) + 1
		SET @LastNameID = CAST(RAND() * @LastNamesCount as int) + 1
		
		SELECT @FirstName = [Name] FROM [dbo].[FirstNames] WHERE [ID] = @FirstNameID
		SELECT @LastName = [Name] FROM [dbo].[LastNames] WHERE [ID] = @LastNameID
		
		IF @FirstName IS NULL
		BEGIN
			print 'First name ID not found'
		END
	
		IF @LastName IS NULL
		BEGIN
			print 'Last name ID not found'
		END
		
		DECLARE @HexString nvarchar(16)
		DECLARE @IntValue bigint
		SET @HexString = '0123456789ABCDEF'
		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @BandID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @LongRangeID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @TapID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		BEGIN TRANSACTION

		BEGIN TRY
				
			INSERT INTO [dbo].[guest]
			   ([IDMSID]
			   ,[IDMSTypeId]
			   ,[lastName]
			   ,[firstName]
			   ,[DOB]
			   ,[VisitCount]
			   ,[AvatarName]
			   ,[active]
			   ,[createdBy]
			   ,[createdDate]
			   ,[updatedBy]
			   ,[updatedDate])
			VALUES
			   (NEWID()
			   ,9
			   ,@FirstName
			   ,@LastName
			   ,NULL
			   ,CAST(RAND() * 100 as int)
			   ,NULL
			   ,1
			   ,'usp_CreateTestUser'
			   ,GETDATE()
			   ,'usp_CreateTestUser'
			   ,GETDATE())

			SELECT @GuestID = @@IDENTITY
			
			INSERT INTO [dbo].[xband]
			   ([bandId]
			   ,[longRangeId]
			   ,[tapId]
			   ,[secureId]
			   ,[UID]
			   ,[bandFriendlyName]
			   ,[printedName]
			   ,[active]
			   ,[createdBy]
			   ,[createdDate]
			   ,[updatedBy]
			   ,[updatedDate])
			VALUES
			   (@BandID
			   ,@LongRangeID
			   ,@TapID
			   ,NULL
			   ,NULL
			   ,@FirstName + ' ' + @LastName + '''s band'
			   ,NULL
			   ,1
			   ,'usp_CreateTestUser'
			   ,GETDATE()
			   ,'usp_CreateTestUser'
			   ,GETDATE())

			SELECT @xBandID = @@IDENTITY
		
			INSERT INTO [IDMS].[dbo].[guest_xband]
				   ([guestId]
				   ,[xbandId]
				   ,[createdBy]
				   ,[createdDate]
				   ,[updatedBy]
				   ,[updatedDate]
				   ,[active])
			 VALUES
				   (@GuestID
				   ,@xBandID
				   ,'usp_CreateTestUser'
				   ,GETDATE()
				   ,'usp_CreateTestUser'
				   ,GETDATE()
				   ,1)
			COMMIT TRANSACTION
			
		END TRY
		BEGIN CATCH
		
			ROLLBACK TRANSACTION

		END CATCH
		
	
		SET @Index = @Index + 1
	END

END

GO

