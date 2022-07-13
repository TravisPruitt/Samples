CREATE VIEW [dbo].[vw_xband]
AS
SELECT     x.xbandId, x.bandId, x.longRangeId, x.tapId, x.secureId, x.UID, x.bandFriendlyName, x.printedName, x.active, i.IDMSTypeName AS BandType, x.createdBy, 
                      x.createdDate, x.updatedBy, x.updatedDate, x.publicId
FROM         dbo.xband AS x INNER JOIN
                      dbo.IDMS_Type AS i ON i.IDMSTypeId = x.IDMSTypeId

