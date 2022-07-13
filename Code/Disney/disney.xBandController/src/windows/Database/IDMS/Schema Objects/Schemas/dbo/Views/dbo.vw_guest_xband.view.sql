CREATE VIEW [dbo].[vw_guest_xband]
AS
SELECT     g.guestId, g.IDMSID AS swid, g.IDMSTypeId, g.lastName, g.firstName, g.middleName, g.title, g.suffix, g.DOB AS dateOfBirth, g.VisitCount, g.AvatarName AS avatar, 
                      g.active, g.emailAddress, g.parentEmail, g.countryCode, g.languageCode, g.userName, g.createdBy, g.createdDate, g.updatedBy, g.updatedDate, x.xbandId, x.bandId, 
                      x.longRangeId, x.secureId, x.UID, x.tapId, x.publicId, dbo.party_guest.partyId, CASE WHEN g.[active] = 1 THEN 'Active' ELSE 'InActive' END AS status, 
                      CASE WHEN g.[gender] = 'M' THEN 'MALE' ELSE 'FEMALE' END AS gender
FROM         dbo.guest AS g INNER JOIN
                      dbo.guest_xband AS gx ON gx.guestId = g.guestId INNER JOIN
                      dbo.xband AS x ON x.xbandId = gx.xbandId LEFT OUTER JOIN
                      dbo.party_guest ON g.guestId = dbo.party_guest.guestId
