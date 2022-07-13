/**
**	Purpose of this script is to remove tables that were incorrectly introduced by script 1.0.0.0001-Update.sql
**/


/** 
** Verify that the schema version is correct
**/

DECLARE @currentversion varchar(12)

SET @currentversion = (
	SELECT TOP 1 [version]
	FROM [dbo].[schema_version]
	ORDER BY [schema_version_id] DESC
	)

IF @currentversion <> '1.0.0.0001'
BEGIN
	PRINT 'Current database version needs to be 1.0.0.0001'
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RETURN
END

/**
** Remove the unwanted tables
**/

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_Facility]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_Facility]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_PerformanceMetricDescription]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDescription]
GO

IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_PerformanceMetricSource]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_PerformanceMetricSource]
GO

/****** Object:  Table [dbo].[PerformanceMetric]    Script Date: 07/02/2012 15:12:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]') AND type in (N'U'))
DROP TABLE [dbo].[PerformanceMetric]
GO

/****** Object:  Table [dbo].[PerformanceMetricSource]    Script Date: 07/02/2012 15:11:33 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetricSource]') AND type in (N'U'))
DROP TABLE [dbo].[PerformanceMetricSource]
GO

/****** Object:  Table [dbo].[PerformanceMetricDescription]    Script Date: 07/02/2012 15:13:33 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[PerformanceMetricDescription]') AND type in (N'U'))
DROP TABLE [dbo].[PerformanceMetricDescription]
GO

/**
** Drop the unwanted stored procedures
**/
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_create]    Script Date: 07/02/2012 15:46:53 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetric_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PerformanceMetric_create]
GO

/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_retrieve]    Script Date: 07/02/2012 15:47:37 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetric_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PerformanceMetric_retrieve]
GO


/**
** Update schema version
**/
IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.0.0.0002')
BEGIN
	INSERT INTO [dbo].[schema_version]
				([version]
				 ,[script_name]
				 ,[date_applied])
			VALUES
				('1.0.0.0002'
				,'1.0.0.0002-Update.sql'
				,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version] SET [date_applied] = GETUTCDATE()
END




