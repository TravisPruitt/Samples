
CREATE VIEW [dbo].[vw_celebration]
AS
SELECT c.[celebrationId]
	  ,c.[name]
	  ,c.[milestone]
      ,i.[IDMSTypeName] as [type]
	  ,DATEPART(mm,c.[date]) as [month]
	  ,DATEPART(dd,c.[date]) as [day]
	  ,DATEPART(yyyy,c.[date]) as [year]
	  ,c.[startDate]
	  ,c.[endDate]
	  ,c.[recognitionDate]
	  ,c.[surpriseIndicator]
      ,c.[comment]
  FROM [dbo].[celebration] c
  JOIN [dbo].[IDMS_Type] i on i.[IDMSTypeId] = c.[IDMSTypeId]
  WHERE c.[active] = 1
