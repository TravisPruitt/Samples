/** 
** Check schema version 
**/

DECLARE @currentversion varchar(12)

SET @currentversion = (
	SELECT TOP 1 [version]
	FROM [dbo].[schema_version]
	ORDER BY [schema_version_id] DESC
	)

IF @currentversion <> '1.3.0.0001'
BEGIN
	PRINT 'Current database version needs to be 1.3.0.0001'
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RETURN
END

/**
** Alter the HealthItem table, add Active column
**/

/** Create a temporary table to store the HealthItem's data while it is being altered **/
/****** Object:  Table [dbo].[HealthItemTemp]    Script Date: 06/26/2012 10:34:53 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[HealthItem_Temp](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[ip] [varchar](255) NOT NULL,
	[port] [int] NOT NULL,
	[className] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[version] [varchar](128) NULL,
	[lastDiscovery] [datetime] NOT NULL,
	[nextDiscovery] [datetime] NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/** Move existing HealthItem data to the HealthItem_Temp table **/
INSERT INTO [dbo].[HealthItem_Temp]
	([ip]
	,[port]
	,[className]
	,[name]
	,[version]
	,[lastDiscovery]
	,[nextDiscovery])
SELECT hi.[ip]
	,hi.[port]
	,hi.[className]
	,hi.[name]
	,hi.[version]
	,hi.[lastDiscovery]
	,hi.[nextDiscovery]
FROM [dbo].[HealthItem] hi

/** Alter HealthItem table **/
DROP TABLE [dbo].[HealthItem]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[HealthItem](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[ip] [varchar](255) NOT NULL,
	[port] [int] NOT NULL,
	[className] [varchar](255) NULL,
	[name] [varchar](255) NULL,
	[version] [varchar](128) NULL,
	[lastDiscovery] [datetime] NOT NULL,
	[nextDiscovery] [datetime] NULL,
	[active] [tinyint] DEFAULT 1
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/** Move data back from HealthItem_Temp to HealthItem **/
INSERT INTO [dbo].[HealthItem]
	([ip]
	,[port]
	,[className]
	,[name]
	,[version]
	,[lastDiscovery]
	,[nextDiscovery])
SELECT hit.[ip]
	,hit.[port]
	,hit.[className]
	,hit.[name]
	,hit.[version]
	,hit.[lastDiscovery]
	,hit.[nextDiscovery]
FROM [dbo].[HealthItem_Temp] hit

/** Remove the HelthItem_Temp table **/
DROP TABLE [dbo].[HealthItem_Temp]
GO

/** 
* Create new perf metrics tables
**/

/****** Object:  Table [dbo].[PerformanceMetricDesc]    Script Date: 06/25/2012 15:02:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PerformanceMetricDesc](
	[PerformanceMetricDescID] [int] IDENTITY(1,1) NOT NULL,
	[PerformanceMetricName] [nvarchar](50) NOT NULL,
	[PerformanceMetricDisplayName] [nvarchar](50) NOT NULL,
	[PerformanceMetricDescription] [nvarchar](max) NOT NULL,
	[PerformanceMetricUnits] [nvarchar](20) NOT NULL,
	[PerformanceMetricVersion] [nvarchar](20) NOT NULL,
	[PerformanceMetricCreateDate] [datetime] NOT NULL,
	[PerformanceMetricSource] [varchar](255) NOT NULL,
 CONSTRAINT [PK_PerformanceMetricDesc] PRIMARY KEY CLUSTERED 
(
	[PerformanceMetricDescID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [IX_PerformanceMetricDesc_UniqueNameVersionSource] UNIQUE NONCLUSTERED 
(
	[PerformanceMetricName] ASC,
	[PerformanceMetricVersion] ASC,
	[PerformanceMetricSource] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[PerformanceMetric]    Script Date: 06/25/2012 15:14:36 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE [dbo].[PerformanceMetric](
	[HealthItemID] [int] NOT NULL,
	[PerformanceMetricDescID] [int] NOT NULL,
	[Timestamp] [datetimeoffset](3) NOT NULL,
	[Maximum] [float] NOT NULL,
	[Minimum] [float] NOT NULL,
	[Mean] [float] NOT NULL,
	[FacilityID] [int] NULL,
 CONSTRAINT [PK_PerformanceMetric] PRIMARY KEY CLUSTERED 
(
	[HealthItemID] ASC,
	[PerformanceMetricDescID] ASC,
	[Timestamp] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO

ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_Facility]
GO

ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_HealthItem] FOREIGN KEY([HealthItemID])
REFERENCES [dbo].[HealthItem] ([id])
GO

ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_HealthItem]
GO

ALTER TABLE [dbo].[PerformanceMetric]  WITH CHECK ADD  CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc] FOREIGN KEY([PerformanceMetricDescID])
REFERENCES [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID])
GO

ALTER TABLE [dbo].[PerformanceMetric] CHECK CONSTRAINT [FK_PerformanceMetric_PerformanceMetricDesc]
GO

/**
** Populate/port data
**/

/** Make sure we have at least a default facility type **/
SET IDENTITY_INSERT  [rdr].[FacilityType] ON

IF NOT EXISTS(SELECT 'X' FROM  [rdr].[FacilityType] WHERE [FacilityTypeID] = 0)
	INSERT INTO [rdr].[FacilityType] ([FacilityTypeID],[FacilityTypeName]) VALUES (0, 'None')
GO

SET IDENTITY_INSERT  [rdr].[FacilityType] OFF

/** Make sure facilities from GxP pilot are in the Facility table **/
INSERT INTO [rdr].[Facility]
           ([FacilityName]
           ,[FacilityTypeID])
SELECT DISTINCT x.[name], 0
FROM [dbo].[XbrcPerf] x
WHERE NOT EXISTS
(SELECT 'X'
 FROM [rdr].[Facility] f
 WHERE f.[FacilityName] = x.[name])
 
/** Populate PerformanceMetricDesc table **/
SET IDENTITY_INSERT [dbo].[PerformanceMetricDesc] ON
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (1, N'perfModel3', N'perfModel3', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (2, N'perfSaveGSTMsec', N'perfSaveGSTMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (3, N'perfPostModelingMsec', N'perfPostModelingMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (5, N'perfWriteToReaderMsec', N'perfWriteToReaderMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (6, N'perfExternalMsec', N'perfExternalMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (7, N'perfPreModelingMsec', N'perfPreModelingMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (8, N'perfUpstreamMsec', N'perfUpstreamMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (9, N'perfModel1', N'GxpCallback', N'Round trip to GxP', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (10, N'perfIDMSQueryMsec', N'perfIDMSQueryMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (11, N'perfMainLoopUtilization', N'perfMainLoopUtilization', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (12, N'perfEvents', N'perfEvents', N'', N'numeric', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (13, N'perfModelingMsec', N'perfModelingMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (14, N'perfEKGWriteMsec', N'perfEKGWriteMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (15, N'perfSingulationMsec', N'perfSingulationMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (16, N'perfEventAgeMsec', N'perfEventAgeMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (17, N'perfModel2', N'perfModel2', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (21, N'perfSaveGSTMsec', N'Save To GST', N'', N'milliseconds', N'1.1', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
INSERT [dbo].[PerformanceMetricDesc] ([PerformanceMetricDescID], [PerformanceMetricName], [PerformanceMetricDisplayName], [PerformanceMetricDescription], [PerformanceMetricUnits], [PerformanceMetricVersion], [PerformanceMetricCreateDate], [PerformanceMetricSource]) VALUES (22, N'perfIDSQueryMsec', N'perfIDSQueryMsec', N'', N'milliseconds', N'1.0', GETUTCDATE(), N'com.disney.xband.xbrms.health.Xbrc')
SET IDENTITY_INSERT [dbo].[PerformanceMetricDesc] OFF

/** Create inactive HealthItem entries for the GxP Pilot data **/
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00009.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','15850196','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00014.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','15850198','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00015.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010114','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00016.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010153','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00012.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010170','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00013.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010176','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00010.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010190','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00020.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010192','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00017.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010208','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)
INSERT INTO [dbo].[HealthItem] ([ip],[port],[className],[name],[version],[lastDiscovery],[nextDiscovery],[active]) VALUES ('nl-flmk-00011.disney.dwd.com',8080,'com.disney.xband.xbrms.health.Xbrc','80010213','1.0.0-1494',CAST('2012-05-18 21:43:39.057' AS datetime),CAST('2012-05-18 21:44:39.057' AS datetime),0)

/** Populate PerformanceMetric table **/
INSERT INTO [dbo].[PerformanceMetric]
           ([HealthItemID]
           ,[PerformanceMetricDescID]
           ,[Timestamp]
           ,[Maximum]
           ,[Minimum]
           ,[Mean]
           ,[FacilityID])
SELECT DISTINCT hi.[id]
		,pmd.[PerformanceMetricDescID]
		,CAST(x.[time] AS datetimeoffset)
		,x.[max]
		,x.[min]
		,x.[mean]
		,f.[FacilityID]
FROM	[dbo].[XbrcPerf] x
JOIN	[rdr].[Facility] f on f.[FacilityName] = x.[name]
JOIN	[dbo].[PerformanceMetricDesc] pmd ON pmd.[PerformanceMetricName] = x.[metric]
JOIN	[dbo].[HealthItem] hi ON hi.[name] = f.[FacilityName]
WHERE x.[time] LIKE '%T%' AND
	NOT EXISTS (SELECT 'X'
	 FROM	[dbo].[PerformanceMetric] pm
	 WHERE	pm.[HealthItemID] = hi.[id]
	 AND	pm.[PerformanceMetricDescID] = pmd.[PerformanceMetricDescID]
	 AND	pm.[Timestamp] = CAST(x.[time] AS datetimeoffset)
	 AND	pm.[FacilityID] = f.[FacilityID])
 
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

 /**
 ** Create stored procedures
 **/
 
-- =============================================
-- Author:		Ted Crane
-- Create date: 06/05/2012
-- Description:	Retrieves the performance metrics
--              using the specified parameters.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetric_retrieve] 
	@HealthItem int,
	@StartTime datetime,
	@EndTime datetime,
	@Metric nvarchar(50) = NULL,
	@Version nvarchar(20) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY;
		
		DECLARE @Source NVARCHAR
		
		SELECT @Source = hi.[className]
		FROM [dbo].[HealthItem] AS hi
		WHERE [id] = @HealthItem
	
		DECLARE @Result NVARCHAR(MAX)

		DECLARE @ResultTable AS TABLE ([Description] nvarchar(50), [Metric] nvarchar(MAX) NULL)
		
		IF @Metric IS NULL AND @Version IS NULL
		BEGIN
			SET @Result = '{'

			INSERT INTO @ResultTable ([Description], [Metric])
			SELECT  pmd.[PerformanceMetricName], N'["' + CONVERT(nvarchar(27),pm.[Timestamp], 109) +  
					N'",' + CONVERT(nvarchar(10),ROUND(pm.Maximum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Minimum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Mean,2)) + N']'
			FROM	[dbo].[PerformanceMetric] pm
			JOIN	[dbo].[HealthItem] hi ON hi.[id] = pm.[HealthItemID]
			JOIN	[dbo].[PerformanceMetricDesc] pmd on pmd.[PerformanceMetricDescID] = pm.[PerformanceMetricDescID]
			WHERE	pm.[Timestamp] BETWEEN @StartTime AND @EndTime
			ORDER BY pmd.[PerformanceMetricName], pm.[Timestamp]

			SELECT @Result = @Result + N'"' + [Description] + N'":[' + 
					LEFT([Metrics] , LEN([Metrics])-1) + N'],'
			FROM @ResultTable AS extern 
			CROSS APPLY (     
				SELECT [Metric] + ','     
				FROM @ResultTable  AS intern     
				WHERE extern.[Description] = intern.[Description]
				 FOR XML PATH('') ) pre_trimmed ([Metrics]) 
			GROUP BY [Description], [Metrics]; 

			SELECT LEFT(@Result , LEN(@Result)-1) + '}'
		END
		ELSE
		BEGIN
			SET @Result = '{'

			INSERT INTO @ResultTable ([Description], [Metric])
			SELECT  pmd.[PerformanceMetricName], N'["' + CONVERT(nvarchar(27),pm.[Timestamp], 109) +  
					N'",' + CONVERT(nvarchar(10),ROUND(pm.Maximum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Minimum,2)) + 
				N',' + CONVERT(nvarchar(10),ROUND(pm.Mean,2)) + N']'
			FROM	[dbo].[PerformanceMetric] pm
			JOIN	[dbo].[HealthItem] hi ON hi.[id] = pm.[HealthItemID]
			JOIN	[dbo].[PerformanceMetricDesc] pmd on pmd.[PerformanceMetricDescID] = pm.[PerformanceMetricDescID]
			WHERE	pmd.[PerformanceMetricName] = @Metric AND pmd.[PerformanceMetricVersion] = @Version AND
				pm.[Timestamp] BETWEEN @StartTime AND @EndTime
			ORDER BY pmd.[PerformanceMetricName], pm.[Timestamp]

			SELECT @Result = @Result + N'"' + [Description] + N'":[' + 
					LEFT([Metrics] , LEN([Metrics])-1) + N'],'
			FROM @ResultTable AS extern 
			CROSS APPLY (     
				SELECT [Metric] + ','     
				FROM @ResultTable  AS intern     
				WHERE extern.[Description] = intern.[Description]
				 FOR XML PATH('') ) pre_trimmed ([Metrics]) 
			GROUP BY [Description], [Metrics]; 

			SELECT LEFT(@Result , LEN(@Result)-1) + '}'
		END
		
	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH
END
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		Ted Crane
-- Create date: 06/05/2012
-- Description:	Inserts a performance 
--              metric.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetric_create] 
	@HealthItemIp nvarchar(255),
	@HealthItemName nvarchar(255),
	@Vanue nvarchar(50),
	@Model nvarchar(50),
	@Source nvarchar(255),
	@Time nvarchar(50),
	@Metric nvarchar(50),
	@Version nvarchar(20),
	@Maximum float,
	@Minimum float,
	@Mean float
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @HealthItemId int
		
		SELECT @HealthItemId = hi.[id]
		FROM [dbo].[HealthItem] hi
		WHERE hi.[ip] = @HealthItemIp AND
			  hi.[name] = @HealthItemName AND
			  hi.[className] = @Source
			  
		DECLARE @PerformanceMetricDescriptionID int
			
			SELECT	@PerformanceMetricDescriptionID = p.[PerformanceMetricDescID]
			FROM	[dbo].[PerformanceMetricDesc] p
			WHERE	p.[PerformanceMetricName] = @Metric AND 
					p.[PerformanceMetricVersion] = @Version AND
					p.[PerformanceMetricSource] = @Source
			
			IF @PerformanceMetricDescriptionID IS NULL		
			BEGIN
			
				INSERT INTO [dbo].[PerformanceMetricDesc] (
					[PerformanceMetricName]
					,[PerformanceMetricDisplayName]
					,[PerformanceMetricDescription]
					,[PerformanceMetricUnits]
					,[PerformanceMetricVersion]
					,[PerformanceMetricSource])
				VALUES (@Metric
						,@Metric
						,''
						,'milliseconds'
						,@Version
						,@Source)
				
				SELECT @PerformanceMetricDescriptionID = @@IDENTITY

			END
		
		IF @Model IS NULL /** dealing with health item other than xBRC **/
		BEGIN
			INSERT INTO [dbo].[PerformanceMetric]
			   ([HealthItemID]
			   ,[PerformanceMetricDescID]
			   ,[Timestamp]
			   ,[Maximum]
			   ,[Minimum]
			   ,[Mean])
			VALUES
				(@HealthItemId
				,@PerformanceMetricDescriptionID
				,CONVERT(datetimeoffset, @Time)
				,@Maximum
				,@Minimum
				,@Mean)				   
			
			COMMIT TRANSACTION
		END
		ELSE
		BEGIN
			DECLARE @FacilityID int
			DECLARE @FacilityTypeID int
			
			SELECT	@FacilityTypeID = f.[FacilityTypeID]
			FROM	[rdr].[FacilityType] f
			WHERE	f.[FacilityTypeName] = @Model

			IF @FacilityTypeID IS NULL
			BEGIN
			
				INSERT INTO [rdr].[FacilityType] ([FacilityTypeName]) VALUES (@Model)
				
				SELECT @FacilityTypeID = @@IDENTITY

			END
			
			SELECT	@FacilityID = f.[FacilityID]
			FROM	[rdr].[Facility] f
			WHERE	f.[FacilityName] = @Vanue

			IF @FacilityID IS NULL
			BEGIN
			
				INSERT INTO [rdr].[Facility] ([FacilityName], [FacilityTypeID]) VALUES (@Vanue, @FacilityTypeID)
				
				SELECT @FacilityID = @@IDENTITY

			END

			INSERT INTO [dbo].[PerformanceMetric]
			   ([HealthItemID]
			   ,[PerformanceMetricDescID]
			   ,[Timestamp]
			   ,[Maximum]
			   ,[Minimum]
			   ,[Mean]
			   ,[FacilityID])
			VALUES
				(@HealthItemId
				,@PerformanceMetricDescriptionID
				,CONVERT(datetimeoffset, @Time)
				,@Maximum
				,@Minimum
				,@Mean
				,@FacilityID)				   
			
			COMMIT TRANSACTION
		END
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

-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Inserts a performance metric meta data.
-- =============================================
CREATE PROCEDURE [dbo].[usp_PerformanceMetricDesc_create] 
	@Name nvarchar(50),
	@DisplayName nvarchar(50),
	@Description nvarchar(MAX),
	@Units nvarchar(20),
	@Version nvarchar(20),
	@Source nvarchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @ExistingDescID int
		
		SELECT @ExistingDescID = pmd.[PerformanceMetricDescID]
		FROM [dbo].[PerformanceMetricDesc] pmd
		WHERE pmd.[PerformanceMetricName] = @Name AND 
				pmd.[PerformanceMetricVersion] = @Version AND
				pmd.[PerformanceMetricSource] = @Source
		
		IF @ExistingDescID IS NULL
			BEGIN
				INSERT INTO [dbo].[PerformanceMetricDesc](
						[PerformanceMetricName],
						[PerformanceMetricDisplayName],
						[PerformanceMetricDescription],
						[PerformanceMetricUnits],
						[PerformanceMetricVersion],
						[PerformanceMetricSource]) 
					VALUES (
						@Name,
						@DisplayName,
						@Description,
						@Units,
						@Version,
						@Source )
			END
		ELSE
			BEGIN
				UPDATE [dbo].[PerformanceMetricDesc]
				SET [PerformanceMetricDisplayName] = @DisplayName,
					[PerformanceMetricDescription] = @Description,
					[PerformanceMetricUnits] = @Units
				WHERE [PerformanceMetricName] = @Name AND 
						[PerformanceMetricVersion] = @Version AND
						[PerformanceMetricSource] = @Source
			END  
		
		COMMIT TRANSACTION
		
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

-- =============================================
-- Author:		Iwona Glabek
-- Create date: 06/26/2012
-- Description:	Retrieves the performance metrics
--              using the specified parameters.
-- =============================================
CREATE PROCEDURE [dbo].[usp_HealthItem_delete]
	@Id int
	
AS
BEGIN
	BEGIN TRY
		BEGIN TRANSACTION
		
		DELETE FROM [dbo].[PerformanceMetric] 
		WHERE [HealthItemID] = @Id
		
		DELETE FROM [dbo].[HealthItem]
		WHERE [id] = @Id
		
		COMMIT TRANSACTION
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
				WHERE [ip] = 'localhost'
			END
		ELSE
			BEGIN
				SELECT @id = hi.[id]
				FROM [dbo].[HealthItem] AS hi
				WHERE [ip] = @ip AND
					[className] = @className AND
					[name] = @name
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
IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.3.0.0002')
BEGIN
	INSERT INTO [dbo].[schema_version]
				([version]
				 ,[script_name]
				 ,[date_applied])
			VALUES
				('1.3.0.0002'
				,'1.3.0.0002-Update.sql'
				,GETUTCDATE())
END
ELSE
BEGIN
	UPDATE [dbo].[schema_version] SET [date_applied] = GETUTCDATE()
END
