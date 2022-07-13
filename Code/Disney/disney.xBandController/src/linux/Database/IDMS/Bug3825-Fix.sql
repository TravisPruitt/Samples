use [IDMS]

IF EXISTS ( SELECT * FROM [sys].[symmetric_keys] WHERE name = 'SecureID_Key' )
	DROP SYMMETRIC KEY SecureID_Key;
GO

IF EXISTS ( SELECT * FROM [sys].[certificates] WHERE name = 'SecureID_Certificate' )
	DROP CERTIFICATE SecureID_Certificate;
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HMACKeys]') AND type in (N'U'))
	DROP TABLE [dbo].[HMACKeys];

--Create Certificate
CREATE CERTIFICATE SecureID_Certificate
FROM FILE = 'C:\temp\SecureID_Certificate.cer'
WITH PRIVATE KEY 
  ( 
    FILE = 'C:\Temp\SecureID_Certificate.pvk' ,
    ENCRYPTION BY PASSWORD = '+=#D3AD_B33F=+'
    ,DECRYPTION BY PASSWORD = '~Gxp_Cert_P1lot~'
  ) 
GO


CREATE SYMMETRIC KEY SecureID_Key 
WITH ALGORITHM = AES_256
ENCRYPTION BY CERTIFICATE SecureID_Certificate;
GO

CREATE TABLE [dbo].[HMACKeys](
	[HMACKeyID] [int] NOT NULL,
	[HMACKey] [varbinary](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[HMACKeyID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

OPEN SYMMETRIC KEY SecureID_Key 
DECRYPTION BY CERTIFICATE SecureID_Certificate
WITH PASSWORD = '+=#D3AD_B33F=+';

--Insert key to be used for encrypting the salt value
INSERT INTO HMACKeys (HMACKeyID, HMACKey)
VALUES( 1, CONVERT(varbinary(255),
	ENCRYPTBYKEY(KEY_GUID('SecureID_Key'),N'+GxP_P!lot_Salt#1+')));

CLOSE SYMMETRIC KEY SecureID_Key; 

IF  NOT EXISTS (SELECT * from sys.columns where Name = N'secureid_hashed'  and Object_ID = Object_ID(N'[xband]') )
	ALTER TABLE [dbo].[xband] ADD [secureid_hashed] varbinary(255);
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ufn_CreateHMAC]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
	DROP FUNCTION [dbo].[ufn_CreateHMAC]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		Ted Crane
-- Create date: 04/06/2012
-- Description:	Generates an HMAC Value for the specified message.
-- =============================================
CREATE FUNCTION [dbo].[ufn_CreateHMAC] 
(
	@salt varbinary(255),
	@message nvarchar(MAX)
)
RETURNS varbinary(255)
AS
BEGIN
	DECLARE @Result varbinary(255)

	DECLARE @Key1 varbinary(20)
	DECLARE @Key2 varbinary(20)
	
	SET @Key1 = @salt
	SET @Key2 = @salt

	--USING SHA1_512 hash so key should be 20 bytes.
	IF LEN(@salt) < 20
	BEGIN
	
		--Pad key 1 with 0x5C
		SET @Key1 = CONVERT(varbinary(64),REPLICATE(0x5C, 20 - LEN(@salt))) + @salt;
		
		--Pad Key 2 with 0x36
		SET @Key2 = CONVERT(varbinary(64),REPLICATE(0x36, 20 - LEN(@salt))) + @salt;
	
	END
	
	SET @Result = HASHBYTES('SHA1',@Key1 + 
		HASHBYTES('SHA1', @Key2 + 
			CONVERT(varbinary(MAX),@message))) 

	RETURN @Result

END


GO


OPEN SYMMETRIC KEY SecureID_Key 
DECRYPTION BY CERTIFICATE SecureID_Certificate
WITH PASSWORD = '+=#D3AD_B33F=+';

DECLARE @salt varbinary(255); 
SELECT @salt = DECRYPTBYKEY(HMACKey) 
FROM HMACKeys WHERE HMACKeyID = 1;

--Make sure to update the hashed values
UPDATE [dbo].[xband]
SET [secureid_hashed] = [dbo].[ufn_CreateHMAC](@salt,[secureid])

CLOSE SYMMETRIC KEY SecureID_Key;

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'AK_secureId')
ALTER TABLE [dbo].[xband] DROP CONSTRAINT [AK_secureId]
GO

ALTER TABLE [dbo].[xband] ADD  CONSTRAINT [AK_secureId] UNIQUE NONCLUSTERED 
(
	[secureId_hashed] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_saveXband]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_saveXband]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Robert
-- Create date: 3/28/2012
-- Description:	Save a new xband
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description:	Simplify insert.
--              Incorporated secure ID hashing.
-- =============================================
CREATE PROCEDURE [dbo].[usp_saveXband] (
	 @bandId nvarchar(200) = NULL,
	 @longRangeId nvarchar(200) = NULL,
	 @tapId nvarchar(200) = NULL,
	 @secureId nvarchar(200) = NULL,
	 @uid nvarchar(200) = NULL,
	 @bandFriendlyName nvarchar(200) = NULL,
	 @printedName nvarchar(200) = NULL,
	 @active bit = 1,
	 @createdBy nvarchar(50) = NULL)
	   
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	---- interfering with SELECT statements.
	SET NOCOUNT ON;

	OPEN SYMMETRIC KEY SecureID_Key 
	DECRYPTION BY CERTIFICATE SecureID_Certificate
	WITH PASSWORD = '+=#D3AD_B33F=+';

	DECLARE @salt varbinary(255); 
	SELECT @salt = DECRYPTBYKEY(HMACKey) 
	FROM HMACKeys WHERE HMACKeyID = 1;

	INSERT INTO [dbo].[xband]
           ([bandId]
           ,[longRangeId]
           ,[tapId]
           ,[UID]
           ,[bandFriendlyName]
           ,[printedName]
           ,[active]
           ,[createdBy]
           ,[createdDate]
           ,[updatedBy]
           ,[updatedDate]
           ,[secureid_hashed])
     VALUES
           (@bandID
           ,@longRangeId
           ,@tapId
           ,@uid
           ,@bandFriendlyName
           ,@printedName
           ,1
           ,'IDMS'
           ,GETUTCDATE()
           ,'IDMS'
           ,GETUTCDATE()
           ,[dbo].[ufn_CreateHMAC](@salt,@secureId))


	CLOSE SYMMETRIC KEY SecureID_Key;
	
	RETURN @@IDENTITY;
	
END

GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandBySecureId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandBySecureId]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandBySecureId] 
	-- Add the parameters for the stored procedure here
	@secureId nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	OPEN SYMMETRIC KEY SecureID_Key 
	DECRYPTION BY CERTIFICATE SecureID_Certificate
	WITH PASSWORD = '+=#D3AD_B33F=+';

	DECLARE @salt varbinary(255); 
	SELECT @salt = DECRYPTBYKEY(HMACKey) 
	FROM HMACKeys WHERE HMACKeyID = 1;
	
	DECLARE @secureId_hashed varbinary(255)
	
	SET @secureId_hashed = [dbo].[ufn_CreateHMAC](@salt,@secureId);
   		
	CLOSE SYMMETRIC KEY SecureID_Key;

	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,NULL as [secureid]
		  ,x.[tapId]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	  where x.[secureid_hashed] = @secureId_hashed
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.[secureid_hashed] = @secureId_hashed

END


GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByBandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByBandId]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Robert
-- Create date: 3/27/2012
-- Description:	Get an XBand by BandId
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description: Change secureid to NULL, as it
--              is one way hashed.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByBandId] 
	-- Add the parameters for the stored procedure here
	@bandId nvarchar(200) 

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,NULL as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	 where x.[bandId] = @bandId;
    	
	Select 
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.bandId=@bandId 
END

GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByLRId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByLRId]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description: Change secureid to NULL, as it
--              is one way hashed.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByLRId] 
	-- Add the parameters for the stored procedure here
	@lrid nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	 SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,NULL as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	  where x.[longRangeId] = @lrid;
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.longRangeId=@lrid;
END

GO


IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByTapId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByTapId]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get an xband by a tap (short range) id.
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description: Change secureid to NULL, as it
--              is one way hashed.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByTapId] 
	-- Add the parameters for the stored procedure here
	@tapId nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	 SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,NULL as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	where x.[tapId] = @tapId;
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.tapId=@tapId
END

GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByUID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByUID]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description: Change secureid to NULL, as it
--              is one way hashed.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByUID] 
	-- Add the parameters for the stored procedure here
	@uid nvarchar(200)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT x.[xbandId]
	,x.[bandId]
	,x.[longRangeId]
	,x.[tapId]
	,NULL as [secureid]
	,x.[UID]
	,x.[bandFriendlyName]
	,x.[printedName]
	,x.[active]
	,x.[createdBy]
	,x.[createdDate]
	,x.[updatedBy]
	,x.[updatedDate]
	FROM [dbo].[xband] x
	WHERE x.[uid] = @uid;
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where xband.uid=@uid 
END

GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByXBandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByXBandId]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get and XBand by it's XBandId.
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description: Change secureid to NULL, as it
--              is one way hashed.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getXBandByXBandId] 
	-- Add the parameters for the stored procedure here
	@xbandId bigint
	  
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
    
    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,NULL as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	 where x.[xbandId] = @xbandId;
    	
	Select 
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		where guest_xband.xbandId=@xbandId
END

GO

IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetXBandsByIdentifier]
GO

SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using and indentifier value.
-- Update date: 04/11/2012
-- Author:		Ted Crane
-- Description: Change secureid to NULL, as it
--              is one way hashed.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetXBandsByIdentifier] 
	@sourceSystemIDValue nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,NULL as [secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	FROM (SELECT [guestid]
	FROM	[dbo].[source_system_link]
	WHERE [sourceSystemIdValue] = @Sourcesystemidvalue) t
	JOIN [dbo].[guest_xband] gx on gx.[guestid] = t.[guestid]
	JOIN [dbo].[xband] x on x.[xbandid] = gx.[xbandid]

END

GO

GRANT VIEW DEFINITION ON SYMMETRIC KEY::SecureID_Key TO EMUser 
GRANT CONTROL ON CERTIFICATE::SecureID_Certificate TO EMUser 

IF  EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_encrypted_secureid')
	DROP INDEX [IX_xband_encrypted_secureid] ON [dbo].[xband] WITH ( ONLINE = OFF )
GO

IF  EXISTS (SELECT * from sys.columns where Name = N'secureid_encrypted'  and Object_ID = Object_ID(N'[xband]') )
	ALTER TABLE [dbo].[xband] DROP COLUMN [secureid_encrypted];
GO

IF  EXISTS (SELECT * from sys.columns where Name = N'secureid'  and Object_ID = Object_ID(N'[xband]') )
	ALTER TABLE [dbo].[xband] DROP COLUMN [secureid];
GO

