/** 
** Check schema version 
**/

DECLARE @currentversion varchar(12)

SET @currentversion = (
	SELECT TOP 1 [version]
	FROM [dbo].[schema_version]
	ORDER BY [schema_version_id] DESC
	)

IF @currentversion <> '1.3.0.0002'
BEGIN
	PRINT 'Current database version needs to be 1.3.0.0002'
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RETURN
END

/**
** Fix for bug #4249
**/

/****** Object:  StoredProcedure [dbo].[usp_HealthItem_insert]    Script Date: 07/09/2012 16:11:02 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Creates or activates a health item.
-- =============================================
ALTER PROCEDURE [dbo].[usp_HealthItem_insert]
	@ip varchar(255),
	@port int,
	@className varchar(255),
	@name varchar(255),
	@version varchar(128),
	@lastDiscovery datetime,
	@nextDiscovery datetime
AS
BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
		
		DECLARE @id int
		
		IF @ip = 'localhost'
			BEGIN
				SELECT @id = hi.[id]
				FROM [dbo].[HealthItem] AS hi
				WHERE [ip] = 'localhost' AND
					  [port] = @port
			END
		ELSE
			BEGIN
				SELECT @id = hi.[id]
				FROM [dbo].[HealthItem] AS hi
				WHERE [ip] = @ip AND
					[className] = @className AND
					[name] = @name AND
					[port] = @port
			END
			
		IF @id IS NOT NULL /** activate existing **/
			BEGIN
				UPDATE [dbo].[HealthItem]
				SET [active] = 1,
					[lastDiscovery] = @lastDiscovery,
					[nextDiscovery] = @nextdiscovery,
					[port] = @port,
					[version] = @version
				WHERE [id] = @id
			END
		ELSE /** create a new one **/
			BEGIN
				INSERT INTO [dbo].[HealthItem]
					([ip]
					,[port]
					,[className]
					,[name]
					,[version]
					,[lastDiscovery]
					,[nextDiscovery]
					,[active])
				VALUES
					(@ip
					,@port
					,@className
					,@name
					,@version
					,@lastDiscovery
					,@nextDiscovery
					,1)
				
				SELECT @id = @@IDENTITY
			END
		
		COMMIT TRANSACTION
		
		SELECT id = @id
		
	END TRY
	BEGIN CATCH
		ROLLBACK TRANSACTION
		-- Call the procedure to raise the original error.
		EXEC usp_RethrowError;
	END CATCH
END
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

/**
** Update schema version
**/
IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.3.0.0003')
BEGIN
	INSERT INTO [dbo].[schema_version]
				([version]
				 ,[script_name]
				 ,[date_applied])
			VALUES
				('1.3.0.0003'
				,'1.3.0.0003-Update.sql'
				,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version] SET [date_applied] = GETUTCDATE()
END
