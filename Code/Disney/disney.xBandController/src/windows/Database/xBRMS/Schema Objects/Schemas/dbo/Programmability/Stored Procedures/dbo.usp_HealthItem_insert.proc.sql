
-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Creates or activates a health item.
-- =============================================
CREATE PROCEDURE [dbo].[usp_HealthItem_insert]
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
