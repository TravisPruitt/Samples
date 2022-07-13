CREATE VIEW [dbo].[vw_celebration_guest]  
AS
SELECT c.[celebrationId], s.[sourceSystemIdValue] as [xid]
      ,i2.[IDMSTypeName] as [role]
      ,CASE WHEN cg.[primaryGuest] = 1 THEN 'OWNER,PARTICIPANT' ELSE 'PARTICIPANT' END as [relationship]
      ,g.[guestId]
      ,g.[firstName] as [firstname]
      ,g.[lastName] as [lastname]
FROM	[dbo].[celebration] c
JOIN	[dbo].[celebration_guest] cg ON cg.[celebrationId] = c.[celebrationId]
JOIN	[dbo].[guest] g ON g.[guestId] = cg.[guestId]
JOIN	[dbo].[source_system_link] s ON s.[guestId] = cg.[guestId]
JOIN	[dbo].[IDMS_Type] i ON i.[IDMSTypeId] = s.[IDMSTypeId]
	AND i.[IDMSTypeName] = 'xid'
JOIN	[dbo].[IDMS_Type] i2 ON i2.[IDMSTypeId] = cg.[IDMSTypeId]
WHERE	c.[active] = 1