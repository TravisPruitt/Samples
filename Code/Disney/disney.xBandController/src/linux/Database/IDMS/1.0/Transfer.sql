--Must be used in SQLCMD Mode in SQL Management Studio or run using the sql command utility
--To set SQLCMD Mode select SQLCMD Mode from the Query menu in SSMS.

--Change database names appropriately
:setvar source IDMS_DATA
:setvar destination IDMS

SET IDENTITY_INSERT [$(destination)].[dbo].[guest] ON

INSERT INTO [$(destination)].[dbo].[guest]
           ([guestId]
           ,[IDMSID]
           ,[IDMSTypeId]
           ,[lastName]
           ,[firstName]
           ,[middleName]
           ,[title]
           ,[suffix]
           ,[DOB]
           ,[VisitCount]
           ,[AvatarName]
           ,[active]
           ,[emailAddress]
           ,[parentEmail]
           ,[countryCode]
           ,[languageCode]
           ,[gender]
           ,[userName]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [guestId]
      ,[IDMSID]
      ,[IDMSTypeId]
      ,[lastName]
      ,[firstName]
      ,[middleName]
      ,[title]
      ,[suffix]
      ,[DOB]
      ,[VisitCount]
      ,[AvatarName]
      ,[active]
      ,[emailAddress]
      ,[parentEmail]
      ,[countryCode]
      ,[languageCode]
      ,[gender]
      ,[userName]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[guest] g
  WHERE NOT EXISTS
  (SELECT 'X'
   FROM [$(destination)].[dbo].[guest] g1
   WHERE g1.[guestId] = g.[guestId])

SET IDENTITY_INSERT [$(destination)].[dbo].[guest] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[xband] ON

INSERT INTO [$(destination)].[dbo].[xband]
           ([xbandid]
           ,[bandId]
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
SELECT [xbandId]
      ,[bandId]
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
      ,[updatedDate]
  FROM [$(source)].[dbo].[xband] x
  WHERE NOT EXISTS (SELECT 'X'
	 FROM [$(destination)].[dbo].[xband] x1
	 WHERE x1.[xbandId] = x.[xbandid])
	 
SET IDENTITY_INSERT [$(destination)].[dbo].[xband] OFF

INSERT INTO [$(destination)].[dbo].[source_system_link]
           ([guestId]
           ,[sourceSystemIdValue]
           ,[IDMSTypeId]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [guestId]
      ,[sourceSystemIdValue]
      ,[IDMSTypeId]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[source_system_link] s
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[source_system_link] s1
   WHERE s1.[guestId] = s.[guestId]
   AND   s1.[IDMSTypeId] = s.[IDMSTypeId]
   AND	 s1.[sourceSystemIdValue] = s.[sourceSystemIdValue])


INSERT INTO [$(destination)].[dbo].[guest_xband]
           ([guestId]
           ,[xbandId]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate]
           ,[active])
SELECT [guestId]
      ,[xbandId]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
      ,[active]
  FROM [$(source)].[dbo].[guest_xband] gx
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[guest_xband] gx1
   WHERE gx1.[guestId] = gx.[guestId]
   AND   gx1.[xbandId] = gx.[xbandId])

SET IDENTITY_INSERT [$(destination)].[dbo].[guest_address] ON

INSERT INTO [$(destination)].[dbo].[guest_address]
	([guest_addressId]
	,[guestId]
	,[IDMStypeId]
	,[address1]
	,[address2]
	,[address3]
	,[city]
	,[state]
	,[countryCode]
	,[postalCode]
	,[createdBy]
	,[createdDate]
	,[updatedBy]
	,[updatedDate])
SELECT [guest_addressId]
      ,[guestId]
      ,[IDMStypeId]
      ,[address1]
      ,[address2]
      ,[address3]
      ,[city]
      ,[state]
      ,[countryCode]
      ,[postalCode]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[guest_address] ga
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[guest_address] ga1
   WHERE ga1.[guest_addressId] = ga.[guest_addressId])

SET IDENTITY_INSERT [$(destination)].[dbo].[guest_address] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[guest_phone] ON

INSERT INTO [$(destination)].[dbo].[guest_phone]
	([guest_phoneId]
      ,[guestId]
      ,[IDMSTypeId]
      ,[extension]
      ,[phonenumber]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate])
SELECT [guest_phoneId]
      ,[guestId]
      ,[IDMSTypeId]
      ,[extension]
      ,[phonenumber]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[guest_phone] gp
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[guest_phone] gp1
   WHERE gp1.[guest_phoneId] = gp.[guest_phoneId])

SET IDENTITY_INSERT [$(destination)].[dbo].[guest_phone] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[scheduledItem] ON

INSERT INTO [$(destination)].[dbo].[scheduledItem]
           ([scheduledItemId]
           ,[guestId]
           ,[externalId]
           ,[IDMSTypeId]
           ,[startDateTime]
           ,[endDateTime]
           ,[name]
           ,[location]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [scheduledItemId]
           ,[guestId]
           ,[externalId]
           ,[IDMSTypeId]
           ,[startDateTime]
           ,[endDateTime]
           ,[name]
           ,[location]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate]
FROM [$(source)].[dbo].[scheduledItem] si
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[scheduledItem] si1
   WHERE si1.[scheduledItemId] = si.[scheduledItemId])

SET IDENTITY_INSERT [$(destination)].[dbo].[scheduledItem] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[guest_scheduledItem] ON

INSERT INTO [$(destination)].[dbo].[guest_scheduledItem]
           ([guest_scheduledItemId]
           ,[guestId]
           ,[scheduledItemId]
           ,[isOwner]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [guest_scheduledItemId]
      ,[guestId]
      ,[scheduledItemId]
      ,[isOwner]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[guest_scheduledItem] gsi
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[guest_scheduledItem] gsi1
   WHERE gsi1.[guest_scheduledItemId] = gsi.[guest_scheduledItemId])

SET IDENTITY_INSERT [$(destination)].[dbo].[guest_scheduledItem] OFF


SET IDENTITY_INSERT [$(destination)].[dbo].[celebration] ON

INSERT INTO [$(destination)].[dbo].[celebration]
           ([celebrationId]
           ,[guestId]
           ,[name]
           ,[message]
           ,[dateStart]
           ,[dateEnd]
           ,[active]
           ,[IDMSTypeId]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [celebrationId]
      ,[guestId]
      ,[name]
      ,[message]
      ,[dateStart]
      ,[dateEnd]
      ,[active]
      ,[IDMSTypeId]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[celebration] c
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[celebration] c1
   WHERE c1.[celebrationId] = c.[celebrationId])

SET IDENTITY_INSERT [$(destination)].[dbo].[celebration] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[party] ON

INSERT INTO [$(destination)].[dbo].[party]
           ([partyId]
           ,[primaryGuestId]
           ,[partyName]
           ,[count]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [partyId]
      ,[primaryGuestId]
      ,[partyName]
      ,[count]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[party] p
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[party] p1
   WHERE p1.[partyId] = p.[partyId])

SET IDENTITY_INSERT [$(destination)].[dbo].[party] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[party_guest] ON

INSERT INTO [$(destination)].[dbo].[party_guest]
           ([party_guestId]
           ,[partyId]
           ,[guestId]
           ,[IDMSTypeId]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [party_guestId]
      ,[partyId]
      ,[guestId]
      ,[IDMSTypeId]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[party_guest] pg
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[party_guest] pg1
   WHERE pg1.[party_guestId] = pg.[party_guestId])


SET IDENTITY_INSERT [$(destination)].[dbo].[party_guest] OFF

SET IDENTITY_INSERT [$(destination)].[dbo].[scheduleItemDetail] ON

INSERT INTO [$(destination)].[dbo].[scheduleItemDetail]
           ([itemDetailId]
           ,[scheduledItemId]
           ,[guestId]
           ,[name]
           ,[location]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate])
SELECT [itemDetailId]
      ,[scheduledItemId]
      ,[guestId]
      ,[name]
      ,[location]
      ,[createdBy]
      ,[createdDate]
      ,[updatedBy]
      ,[updatedDate]
  FROM [$(source)].[dbo].[scheduleItemDetail] sdi
  WHERE NOT EXISTS 
  (SELECT 'X'
   FROM  [$(destination)].[dbo].[scheduleItemDetail] sdi1
   WHERE sdi1.[itemDetailId] = sdi.[itemDetailId])

SET IDENTITY_INSERT [$(destination)].[dbo].[scheduleItemDetail] OFF
