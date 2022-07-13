use [master]

IF NOT EXISTS (SELECT loginname FROM master.dbo.syslogins WHERE name = N'EMUser')
	CREATE LOGIN EMUser WITH PASSWORD = 'Mayhem*23'
GO

USE [Synaps_IDMS]
GO

IF  EXISTS (SELECT * FROM sys.database_principals WHERE name = N'EMUser')
	DROP USER [EMUser]
GO

CREATE USER EMUser FOR LOGIN EMUser WITH DEFAULT_SCHEMA = dbo
GO

sp_addrolemember @rolename = 'db_datareader', @membername = 'EMUser'
GO
sp_addrolemember @rolename = 'db_datawriter', @membername = 'EMUser'
GO

GRANT CONNECT TO [EMUser] AS [dbo]
GO

GRANT EXECUTE TO [EMUser] AS [dbo]
GO

/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 05/10/2012 10:27:39 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 05/10/2012 10:27:44 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 05/10/2012 10:27:44 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 05/10/2012 10:27:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 05/10/2012 10:27:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 05/10/2012 10:27:50 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 05/10/2012 10:27:53 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 05/10/2012 10:27:53 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 05/10/2012 10:27:56 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 05/10/2012 10:27:56 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 05/10/2012 10:28:00 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 05/10/2012 10:28:00 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestByEmail]    Script Date: 05/10/2012 10:28:07 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestByEmail]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestByEmail]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestById]    Script Date: 05/10/2012 10:28:07 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestProfileById]    Script Date: 05/10/2012 10:28:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestProfileById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestProfileById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByBandId]    Script Date: 05/10/2012 10:28:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByBandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByBandId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByLRId]    Script Date: 05/10/2012 10:28:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByLRId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByLRId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandBySecureId]    Script Date: 05/10/2012 10:28:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandBySecureId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandBySecureId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByTapId]    Script Date: 05/10/2012 10:28:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByTapId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByTapId]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByUID]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByUID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByUID]
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByXBandId]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByXBandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getXBandByXBandId]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_create]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetIdentifiersByIdentifier]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetIdentifiersByIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetIdentifiersByIdentifier]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByGuestID]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByGuestID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetXBandsByGuestID]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByIdentifier]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetXBandsByIdentifier]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest__addresss_create]    Script Date: 05/10/2012 10:28:05 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest__addresss_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest__addresss_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsByXbandId]    Script Date: 05/10/2012 10:28:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestsByXbandId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestsByXbandId]
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 05/10/2012 10:28:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestUser]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateTestUser]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestCelebrationsById]    Script Date: 05/10/2012 10:28:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestCelebrationsById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestCelebrationsById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdentifiersById]    Script Date: 05/10/2012 10:28:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestIdentifiersById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestIdentifiersById]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdFromSourceTypeId]    Script Date: 05/10/2012 10:28:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestIdFromSourceTypeId]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestIdFromSourceTypeId]
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateGuestIdentifier]    Script Date: 05/10/2012 10:28:04 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateGuestIdentifier]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateGuestIdentifier]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_create]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_xband_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_delete]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_delete]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_xband_delete]
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_create]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_source_system_link_create]
GO
/****** Object:  StoredProcedure [dbo].[getGuestCelebrationsById]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getGuestCelebrationsById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[getGuestCelebrationsById]
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 05/10/2012 10:28:03 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] DROP CONSTRAINT [FK_party_guest_party]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party_guest]') AND type in (N'U'))
DROP TABLE [dbo].[party_guest]
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 05/10/2012 10:28:00 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] DROP CONSTRAINT [FK_guest_info_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_address]') AND type in (N'U'))
DROP TABLE [dbo].[guest_address]
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 05/10/2012 10:27:56 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] DROP CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_phone]') AND type in (N'U'))
DROP TABLE [dbo].[guest_phone]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 05/10/2012 10:27:53 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] DROP CONSTRAINT [FK_guest_xband_xband]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND type in (N'U'))
DROP TABLE [dbo].[guest_xband]
GO
/****** Object:  Table [dbo].[party]    Script Date: 05/10/2012 10:27:50 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party] DROP CONSTRAINT [FK_party_guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party]') AND type in (N'U'))
DROP TABLE [dbo].[party]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 05/10/2012 10:27:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_guest]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] DROP CONSTRAINT [FK_celebration_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration]') AND type in (N'U'))
DROP TABLE [dbo].[celebration]
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 05/10/2012 10:27:44 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] DROP CONSTRAINT [FK_source_type_guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND type in (N'U'))
DROP TABLE [dbo].[source_system_link]
GO

/****** Object:  Table [dbo].[schema_version]    Script Date: 05/14/2012 12:50:33 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[schema_version]') AND type in (N'U'))
DROP TABLE [dbo].[schema_version]
GO

/****** Object:  StoredProcedure [dbo].[usp_getGuestNameByEmail]    Script Date: 05/10/2012 10:27:41 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestNameByEmail]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestNameByEmail]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameById]    Script Date: 05/10/2012 10:27:40 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestNameById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestNameById]
GO
/****** Object:  StoredProcedure [dbo].[usp_deactivateGuestById]    Script Date: 05/10/2012 10:27:40 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_deactivateGuestById]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_deactivateGuestById]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_retrieve]    Script Date: 05/10/2012 10:27:40 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_retrieve]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_update]    Script Date: 05/10/2012 10:27:40 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_update]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsForScheduledItems]    Script Date: 05/10/2012 10:27:40 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestsForScheduledItems]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestsForScheduledItems]
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestBands]    Script Date: 05/10/2012 10:27:40 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestBands]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CreateTestBands]
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_create]    Script Date: 05/10/2012 10:27:39 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_xband_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_update]    Script Date: 05/10/2012 10:27:39 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_guest_xband_update]
GO
/****** Object:  Table [dbo].[guest]    Script Date: 05/10/2012 10:27:39 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [FK_guest_IDMS_Type]
GO
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_guest_GGID]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[guest] DROP CONSTRAINT [DF_guest_GGID]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND type in (N'U'))
DROP TABLE [dbo].[guest]
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 05/10/2012 10:27:35 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_scheduledItem]') AND type in (N'U'))
DROP TABLE [dbo].[guest_scheduledItem]
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 05/10/2012 10:27:28 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduledItem]') AND type in (N'U'))
DROP TABLE [dbo].[scheduledItem]
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 05/10/2012 10:27:25 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduleItemDetail]') AND type in (N'U'))
DROP TABLE [dbo].[scheduleItemDetail]
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 05/10/2012 10:27:22 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IDMS_Type]') AND type in (N'U'))
DROP TABLE [dbo].[IDMS_Type]
GO
/****** Object:  StoredProcedure [dbo].[usp_IDMSHello]    Script Date: 05/10/2012 10:27:18 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_IDMSHello]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_IDMSHello]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 05/10/2012 10:27:18 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RethrowError]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RethrowError]
GO
/****** Object:  StoredProcedure [dbo].[usp_saveXband]    Script Date: 05/10/2012 10:27:18 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_saveXband]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_saveXband]
GO
/****** Object:  Table [dbo].[xband]    Script Date: 05/10/2012 10:26:55 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND type in (N'U'))
DROP TABLE [dbo].[xband]
GO
/****** Object:  Table [dbo].[xband]    Script Date: 05/10/2012 10:26:55 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[xband](
	[xbandId] [bigint] IDENTITY(1,1) NOT NULL,
	[bandId] [nvarchar](200) NULL,
	[longRangeId] [nvarchar](200) NULL,
	[tapId] [nvarchar](200) NULL,
	[secureId] [nvarchar](200) NULL,
	[UID] [nvarchar](200) NULL,
	[bandFriendlyName] [nvarchar](50) NULL,
	[printedName] [nvarchar](255) NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[secureid_encrypted] [varbinary](128) NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_secureId] UNIQUE NONCLUSTERED 
(
	[secureId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_tapId] UNIQUE NONCLUSTERED 
(
	[tapId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_longRangeId')
CREATE NONCLUSTERED INDEX [IX_longRangeId] ON [dbo].[xband] 
(
	[longRangeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_bandid')
CREATE NONCLUSTERED INDEX [IX_xband_bandid] ON [dbo].[xband] 
(
	[bandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[xband]') AND name = N'IX_xband_encrypted_secureid')
CREATE NONCLUSTERED INDEX [IX_xband_encrypted_secureid] ON [dbo].[xband] 
(
	[secureid_encrypted] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_saveXband]    Script Date: 05/10/2012 10:27:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_saveXband]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/28/2012
-- Description:	Save a new xband
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

	DECLARE @Fields varchar(MAX)
	DECLARE @Values varchar(MAX)
	
	SET @Fields = ''''
	SET @Values = ''''
	
	if @bandId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + ''bandId, ''
		SET @Values = @Values + '''''''' + @bandId + '''''', ''
	END
	
	
	if @longRangeId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + ''longRangeId, ''
		SET @Values = @Values + '''''''' + @longRangeId + '''''', ''
	END
	
	if @tapId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + ''tapId, ''
		SET @Values = @Values +  '''''''' +@tapId + '''''', ''
	END
	
	if @secureId IS NOT NULL
	BEGIN
		SET @Fields = @Fields + ''secureId, ''
		SET @Values = @Values + '''''''' + @secureId + '''''', ''
	END
	
	if @uid IS NOT NULL
	begin
		SET @FIELDS = @Fields + ''[UID], ''
		SET @Values = @Values + '''''''' + @uid + '''''', ''
	end
	
	if @bandFriendlyName IS NOT NULL
	begin
		SET @Fields = @Fields + ''bandFriendlyName, ''
		SET @Values = @Values + '''''''' + @bandFriendlyName + '''''', ''
	end
	
	if @printedName IS NOT NULL
	begin
		SET @Fields = @Fields + ''printedName, ''
		SET @Values = @Values +  '''''''' +@printedName + '''''', ''
	end
	
	
	if @active IS NOT NULL
	BEGIN
		if  @active = 1
		BEGIN
		SET @Fields = @Fields + ''active, ''
		SET @Values = @Values + ''1, ''
		END
		else
		BEGIN
		SET @Fields = @Fields + ''active, ''
		SET @Values = @Values + ''0, ''
		END
		
	END
	ELSE
	BEGIN
		SET @Fields = @Fields + ''active, ''
		SET @Values = @Values + ''1, ''
	END
	
	
	if @createdBy IS NOT NULL
	BEGIN
		SET @Fields = @Fields + ''createdBy, ''
		SET @Fields = @Fields + ''createdDate, ''
		SET @Fields = @Fields + ''updatedBy, ''
		SET @Fields = @Fields + ''updatedDate ''
		SET @Values = @Values + '''''''' + @createdBy + '''''', ''
		SET @Values = @Values + '''''''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + '''''', ''
		SET @Values = @Values + '''''''' + @createdBy + '''''', ''
		SET @Values = @Values + '''''''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + ''''''''
	END
	ELSE
	BEGIN
		SET @createdBy = ''IDMS''
		SET @Fields = @Fields + ''createdBy, ''
		SET @Fields = @Fields + ''createdDate, ''
		SET @Fields = @Fields + ''updatedBy, ''
		SET @Fields = @Fields + ''updatedDate ''
		SET @Values = @Values + '''''''' + @createdBy + '''''', ''
		SET @Values = @Values + '''''''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + '''''', ''
		SET @Values = @Values + '''''''' + @createdBy + '''''', ''
		SET @Values = @Values + '''''''' + CAST(CURRENT_TIMESTAMP as nvarchar(30)) + ''''''''
	END
	
	
	print ''Output:''
	--DECLARE @PrintMessage nvarchar(200)
	--SET @PrintMessage = ''Test''
	--SET @PrintMessage = @PrintMessage + '' again.''
	--print @PrintMessage
	

	print @Fields
	print @Values
	
	DECLARE @execStatement nvarchar(MAX)
	SET @execStatement = ''INSERT INTO xband ('' + @Fields + '') VALUES ('' + @Values + '')''
	print @execStatement
    
	exec (@execStatement)
	
	return @@IDENTITY
	
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 05/10/2012 10:27:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RethrowError]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 01/25/2012
-- Description:	Rethrows an Error.
-- =============================================
CREATE PROCEDURE [dbo].[usp_RethrowError] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Return if there is no error information to retrieve.
    IF ERROR_NUMBER() IS NULL
        RETURN;

    DECLARE 
        @ErrorMessage    NVARCHAR(4000),
        @ErrorNumber     INT,
        @ErrorSeverity   INT,
        @ErrorState      INT,
        @ErrorLine       INT,
        @ErrorProcedure  NVARCHAR(200);

    -- Assign variables to error-handling functions that 
    -- capture information for RAISERROR.
    SELECT 
        @ErrorNumber = ERROR_NUMBER(),
        @ErrorSeverity = ERROR_SEVERITY(),
        @ErrorState = ERROR_STATE(),
        @ErrorLine = ERROR_LINE(),
        @ErrorProcedure = ISNULL(ERROR_PROCEDURE(), ''-'');

    -- Build the message string that will contain original
    -- error information.
    SELECT @ErrorMessage = 
        N''Error %d, Level %d, State %d, Procedure %s, Line %d, '' + 
            ''Message: ''+ ERROR_MESSAGE();

    -- Raise an error: msg_str parameter of RAISERROR will contain
    -- the original error information.
    RAISERROR 
        (
        @ErrorMessage, 
        @ErrorSeverity, 
        1,               
        @ErrorNumber,    -- parameter: original error number.
        @ErrorSeverity,  -- parameter: original error severity.
        @ErrorState,     -- parameter: original error state.
        @ErrorProcedure, -- parameter: original error procedure name.
        @ErrorLine       -- parameter: original error line number.
        );

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_IDMSHello]    Script Date: 05/10/2012 10:27:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_IDMSHello]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/21/2012
-- Description:	A Ping Hello
-- =============================================
CREATE PROCEDURE [dbo].[usp_IDMSHello] 
	-- Add the parameters for the stored procedure here

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SELECT ''HELLO'';
END
' 
END
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 05/10/2012 10:27:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[IDMS_Type]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[IDMS_Type](
	[IDMSTypeId] [int] IDENTITY(1,1) NOT NULL,
	[IDMSTypeName] [nvarchar](50) NULL,
	[IDMSTypeValue] [nvarchar](50) NULL,
	[IDMSkey] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_IDMS_Type] PRIMARY KEY CLUSTERED 
(
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 05/10/2012 10:27:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduleItemDetail]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[scheduleItemDetail](
	[itemDetailId] [bigint] IDENTITY(1,1) NOT NULL,
	[scheduledItemId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduleItemDetail] PRIMARY KEY CLUSTERED 
(
	[itemDetailId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 05/10/2012 10:27:28 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[scheduledItem]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[scheduledItem](
	[scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[externalId] [nvarchar](50) NULL,
	[IDMSTypeId] [int] NULL,
	[startDateTime] [datetime] NULL,
	[endDateTime] [datetime] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 05/10/2012 10:27:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_scheduledItem]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_scheduledItem](
	[guest_scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[scheduledItemId] [bigint] NULL,
	[isOwner] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[guest_scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest]    Script Date: 05/10/2012 10:27:39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest](
	[guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[IDMSID] [uniqueidentifier] ROWGUIDCOL  NOT NULL CONSTRAINT [DF_guest_GGID]  DEFAULT (newid()),
	[IDMSTypeId] [int] NULL,
	[lastName] [nvarchar](200) NULL,
	[firstName] [nvarchar](200) NULL,
	[middleName] [nvarchar](200) NULL,
	[title] [nvarchar](50) NULL,
	[suffix] [nvarchar](50) NULL,
	[DOB] [date] NULL,
	[VisitCount] [int] NULL,
	[AvatarName] [nvarchar](50) NULL,
	[active] [bit] NULL,
	[emailAddress] [nvarchar](200) NULL,
	[parentEmail] [nvarchar](200) NULL,
	[countryCode] [nvarchar](3) NULL,
	[languageCode] [nvarchar](3) NULL,
	[gender] [nvarchar](1) NULL,
	[userName] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_emailAddress')
CREATE NONCLUSTERED INDEX [IX_guest_emailAddress] ON [dbo].[guest] 
(
	[emailAddress] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_firstname')
CREATE NONCLUSTERED INDEX [IX_guest_firstname] ON [dbo].[guest] 
(
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_lastname')
CREATE NONCLUSTERED INDEX [IX_guest_lastname] ON [dbo].[guest] 
(
	[lastName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest]') AND name = N'IX_guest_lastname_firstname')
CREATE NONCLUSTERED INDEX [IX_guest_lastname_firstname] ON [dbo].[guest] 
(
	[lastName] ASC,
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_update]    Script Date: 05/10/2012 10:27:39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_update]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest band association.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_update] 
	@guestId bigint,
	@xbandid bigint,
	@active bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		UPDATE [IDMS].[dbo].[guest_xband]
		   SET [updatedBy] = ''IDMS''
			  ,[updatedDate] = GETUTCDATE()
			  ,[active] = @active
		 WHERE	[guestid] = @guestid
		 AND	[xbandid] = @xbandid

	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_xband_create]    Script Date: 05/10/2012 10:27:39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_xband_create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates an xband.
-- =============================================
CREATE PROCEDURE [dbo].[usp_xband_create] 
	@xbandId bigint OUTPUT,
	@bandid nvarchar(200),
	@longRangeId nvarchar(200),
	@tapId nvarchar(200),
	@secureId nvarchar(200),
	@UID nvarchar(200),
	@bandFriendlyName nvarchar(50) = NULL,
	@printedName nvarchar(255) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		INSERT INTO [dbo].[xband]
			([bandId]
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
		VALUES
			(@bandId
			,@longRangeId
			,@tapId
			,@secureId
			,@UID
			,@bandFriendlyName
			,@printedName
			,1
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
			,GETUTCDATE())

			
		SELECT @xbandId = @@IDENTITY 
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestBands]    Script Date: 05/10/2012 10:27:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestBands]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateTestBands] 
	@NumberOfBands int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @xBandID bigint
	DECLARE @BandID nvarchar(16)
	DECLARE @TapID nvarchar(16)
	DECLARE @LongRangeID nvarchar(16)
	DECLARE @SecureID nvarchar(16)
	DECLARE @IntVal bigint
	
	DECLARE @Index int

	SET @Index = 0
	
	WHILE @Index < @NumberOfBands 
	BEGIN
	
		DECLARE @HexString nvarchar(16)
		DECLARE @NumericString nvarchar(16)
		DECLARE @IntValue bigint
		SET @HexString = ''0123456789ABCDEF''
		SET @NumericString = ''0123456789''
		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @BandID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @LongRangeID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   ''FF'' +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @TapID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   ''F0'' + 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		SELECT @SecureID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   ''00'' + 
			   SUBSTRING( @NumericString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 10 + 1, 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 10 + 1, 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 10 + 1, 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 10 + 1, 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 7 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 6 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 5 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 4 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 3 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 2 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 1 ) ) % 10 + 1 , 1 ) +
			   SUBSTRING( @NumericString , ( @IntVal / POWER( 16 , 0 ) ) % 10 + 1 , 1 )

		IF NOT EXISTS (SELECT ''X'' FROM [dbo].[xband] where [longRangeId] = @LongRangeID OR [tapId] = @tapID)
		BEGIN
			BEGIN TRANSACTION

			BEGIN TRY
					
				INSERT INTO [dbo].[xband]
				   ([bandId]
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
				VALUES
				   (@BandID
				   ,@LongRangeID
				   ,@TapID
				   ,@SecureID
				   ,NULL
				   ,NULL
				   ,NULL
				   ,1
				   ,''UIE Test''
				   ,GETUTCDATE()
				   ,''UIE Test''
				   ,GETUTCDATE())

				SELECT @xBandID = @@IDENTITY
			
				COMMIT TRANSACTION
				
			END TRY
			BEGIN CATCH
			
				ROLLBACK TRANSACTION

			END CATCH
			
		
			SET @Index = @Index + 1
		END

	END

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsForScheduledItems]    Script Date: 05/10/2012 10:27:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestsForScheduledItems]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		usp_getGuestScheduledItems
-- Create date: 3/20/2012
-- Description:	Get scheduled items by guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestsForScheduledItems] 
	-- Add the parameters for the stored procedure here
	@scheduledItemId bigint 

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select * from guest_scheduledItem  Where scheduledItemId=@scheduledItemId;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_update]    Script Date: 05/10/2012 10:27:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_update]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/02/2012
-- Description:	Updates a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_update] (
	@guestId bigint,
	@guestType nvarchar(50) = NULL,
	@lastname nvarchar(200) = NULL,
	@firstname nvarchar(200) = NULL,
	@DOB date = NULL,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL,
	@active bit = NULL)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
	
		--TODO: Check IDMSTypeID to make sure type is guest type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	([IDMSTypeName] = @guestType
		AND		[IDMSKey] = ''GUESTTYPE'')
		
		IF @guestType IS NOT NULL
		BEGIN
		
			UPDATE [dbo].[guest]
			   SET [IDMSTypeId] = @IDMSTypeID
			 WHERE [guestId] = @guestId

		END
		
		IF @lastname IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [lastname] = @lastname
			 WHERE [guestId] = @guestId
		END

		IF @firstname IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [firstname] = @firstname
			 WHERE [guestId] = @guestId
		END

		IF @middlename IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [middlename] = @middlename
			 WHERE [guestId] = @guestId
		END

		IF @title IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [title] = @title
			 WHERE [guestId] = @guestId
		END

		IF @suffix IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [suffix] = @suffix
			 WHERE [guestId] = @guestId
		END
		
		IF @DOB IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [DOB] = @DOB
			 WHERE [guestId] = @guestId
		END

		IF @visitCount IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [visitCount] = @visitCount
			 WHERE [guestId] = @guestId
		END

		IF @avatarName IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [avatarName] = @avatarName
			 WHERE [guestId] = @guestId
		END

		IF @emailAddress IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [emailAddress] = @emailAddress
			 WHERE [guestId] = @guestId
		END

		IF @parentEmail IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [parentEmail] = @parentEmail
			 WHERE [guestId] = @guestId
		END

		IF @countryCode IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [countryCode] = @countryCode
			 WHERE [guestId] = @guestId
		END

		IF @languageCode IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [languageCode] = @languageCode
			 WHERE [guestId] = @guestId
		END

		IF @gender IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [gender] = @gender
			 WHERE [guestId] = @guestId
		END

		IF @userName IS NOT NULL
		BEGIN

			UPDATE [dbo].[guest]
			   SET [userName] = @userName
			 WHERE [guestId] = @guestId
		END


		UPDATE [dbo].[guest]
		SET  [updatedBy] = ''IDMS''
			,[updatedDate] = GETUTCDATE()
		WHERE [guestId] = @guestId

		COMMIT TRANSACTION
			
	END TRY
	BEGIN CATCH
	
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_retrieve]    Script Date: 05/10/2012 10:27:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_retrieve]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/02/2012
-- Description:	Retrieves a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_retrieve] 
	@guestId bigint = NULL,
	@emailAddress nvarchar(200) = NULL,
	@search nvarchar(200) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
		
		SELECT g.[guestId]
			  ,g.[IDMSID]
			  ,g.[IDMSTypeId]
			  ,g.[lastName]
			  ,g.[firstName]
			  ,g.[middleName]
			  ,g.[title]
			  ,g.[suffix]
			  ,g.[DOB]
			  ,g.[VisitCount]
			  ,g.[AvatarName]
			  ,g.[active]
			  ,g.[emailAddress]
			  ,g.[parentEmail]
			  ,g.[countryCode]
			  ,g.[languageCode]
			  ,g.[gender]
			  ,g.[userName]
			  ,g.[createdBy]
			  ,g.[createdDate]
			  ,g.[updatedBy]
			  ,g.[updatedDate]
		  FROM [dbo].[guest] g
		  WHERE (g.[guestId] = @guestId AND @guestID IS NOT NULL)
		  OR (g.[emailAddress] = @emailAddress AND @emailAddress IS NOT NULL)
		  OR ((g.[lastname] Like ''%'' + @search + ''%'' OR
		        g.[firstname] Like ''%'' + @search + ''%'') AND @search IS NOT NULL)

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_deactivateGuestById]    Script Date: 05/10/2012 10:27:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_deactivateGuestById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Delete (mark as inactive) a guest object by ObjectId
-- =============================================
CREATE PROCEDURE [dbo].[usp_deactivateGuestById] 
	-- Add the parameters for the stored procedure here
	@guestId bigint

AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	UPDATE guest SET active=0 where guestId=@guestId;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameById]    Script Date: 05/10/2012 10:27:40 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestNameById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	get a GuestName object by guestId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestNameById]
	-- Add the parameters for the stored procedure here
	@guestId bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT top 1 firstName, lastName, middleName, guestId FROM guest where guestId=@guestId and active=1 order by createdDate desc
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestNameByEmail]    Script Date: 05/10/2012 10:27:41 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestNameByEmail]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	get a GuestName object by guestId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestNameByEmail] 
	-- Add the parameters for the stored procedure here
	@emailAddress nvarchar(250)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT TOP 1 firstName, lastName, middleName, guestId FROM guest where emailAddress=@emailAddress and active=1 order by createdDate desc
END

' 
END
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 05/10/2012 10:27:44 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[source_system_link](
	[sourceTypeId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[sourceSystemIdValue] [nvarchar](200) NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_source_type] PRIMARY KEY CLUSTERED 
(
	[sourceTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_guestid')
CREATE NONCLUSTERED INDEX [IX_source_system_link_guestid] ON [dbo].[source_system_link] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[source_system_link]') AND name = N'IX_source_system_link_sourceSystemIdValue')
CREATE NONCLUSTERED INDEX [IX_source_system_link_sourceSystemIdValue] ON [dbo].[source_system_link] 
(
	[sourceSystemIdValue] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 05/10/2012 10:27:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[celebration]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[celebration](
	[celebrationId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[message] [nvarchar](max) NULL,
	[dateStart] [date] NULL,
	[dateEnd] [date] NULL,
	[active] [bit] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_celebration] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[party]    Script Date: 05/10/2012 10:27:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[party](
	[partyId] [bigint] IDENTITY(1,1) NOT NULL,
	[primaryGuestId] [bigint] NULL,
	[partyName] [nvarchar](200) NULL,
	[count] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party] PRIMARY KEY CLUSTERED 
(
	[partyId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 05/10/2012 10:27:53 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_xband](
	[guest_xband_id] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[xbandId] [bigint] NOT NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
	[active] [bit] NOT NULL,
 CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guest_xband_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'IX_guest_xband_guestid')
CREATE NONCLUSTERED INDEX [IX_guest_xband_guestid] ON [dbo].[guest_xband] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[guest_xband]') AND name = N'IX_guest_xband_xbandid')
CREATE NONCLUSTERED INDEX [IX_guest_xband_xbandid] ON [dbo].[guest_xband] 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 05/10/2012 10:27:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_phone]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_phone](
	[guest_phoneId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[extension] [nvarchar](50) NULL,
	[phonenumber] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_phone] PRIMARY KEY CLUSTERED 
(
	[guest_phoneId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 05/10/2012 10:28:00 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guest_address]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[guest_address](
	[guest_addressId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[IDMStypeId] [int] NULL,
	[address1] [nvarchar](200) NULL,
	[address2] [nvarchar](200) NULL,
	[address3] [nvarchar](200) NULL,
	[city] [nvarchar](100) NULL,
	[state] [nvarchar](3) NULL,
	[countryCode] [nvarchar](3) NULL,
	[postalCode] [nvarchar](12) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_info] PRIMARY KEY CLUSTERED 
(
	[guest_addressId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 05/10/2012 10:28:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[party_guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[party_guest](
	[party_guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[partyId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party_guest] PRIMARY KEY CLUSTERED 
(
	[party_guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[dbo].[party_guest]') AND name = N'IX_party_guest_guestID')
CREATE NONCLUSTERED INDEX [IX_party_guest_guestID] ON [dbo].[party_guest] 
(
	[guestId] ASC
)
INCLUDE ( [partyId]) WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO

/****** Object:  Table [dbo].[schema_version]    Script Date: 05/14/2012 12:50:33 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[schema_version](
	[schema_version_id] [int] IDENTITY(1,1) NOT NULL,
	[version] [varchar](12) NOT NULL,
	[script_name] [varchar](50) NOT NULL,
	[date_applied] [datetime] NOT NULL,
 CONSTRAINT [PK_SchemaVersion] PRIMARY KEY CLUSTERED 
(
	[schema_version_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO


/****** Object:  StoredProcedure [dbo].[getGuestCelebrationsById]    Script Date: 05/10/2012 10:28:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[getGuestCelebrationsById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/15/2012
-- Description:	Get the guest celebrations by guestId
-- =============================================
CREATE PROCEDURE [dbo].[getGuestCelebrationsById] 
	-- Add the parameters for the stored procedure here
	@guestId BigInt = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select * from celebration Join IDMS_Type on celebration.IDMSTypeId = IDMS_Type.IDMSTypeId where celebration.guestId =@guestId;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_source_system_link_create]    Script Date: 05/10/2012 10:28:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_source_system_link_create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a source system 
--              link record.
-- =============================================
CREATE PROCEDURE [dbo].[usp_source_system_link_create] 
	@guestId bigint,
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		--TODO: Check IDMSTypeID to make sure type is source system type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = ''SOURCESYSTEM''

	     --,19 -- XID 
	     --REPLACE(NEWID(),''-'','''')
		INSERT INTO [dbo].[source_system_link]
			([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(@guestid, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_delete]    Script Date: 05/10/2012 10:28:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_delete]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Deletes a guest band association.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_delete] 
	@guestId bigint,
	@xbandid bigint,
	@active bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DELETE FROM [dbo].[guest_xband]
		WHERE	[guestid] = @guestid
		AND		[xbandid] = @xbandid
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_xband_create]    Script Date: 05/10/2012 10:28:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_xband_create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest band association.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_xband_create] 
	@guestId bigint,
	@xbandid bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		INSERT INTO [dbo].[guest_xband]
			([guestId]
			,[xbandId]
			,[createdBy]
			,[createdDate]
			,[updatedBy]
			,[updatedDate]
			,[active])
		VALUES
			(@guestid
			,@xbandid
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
			,GETUTCDATE()
			,1)
	           
		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateGuestIdentifier]    Script Date: 05/10/2012 10:28:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateGuestIdentifier]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/23/2012
-- Description:	Creates a Guest Identifier 
--              associated with the guestid.
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateGuestIdentifier] 
	@guestId bigint,
	@sourceSystemIdValue nvarchar(200),
	@sourceSystemIdType nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @sourceSystemIdType
		AND		[IDMSKey] = ''SOURCESYSTEM''

		--IF identifier and type already exits for the guest, nothing needs to be done.
		IF NOT EXISTS (SELECT ''X'' FROM [dbo].[source_system_link] WHERE [IDMSTypeID] = @IDMSTypeID AND [guestId] = @guestId AND [sourceSystemIdValue] = @sourceSystemIdValue)
		BEGIN

			INSERT INTO [dbo].[source_system_link]
				([guestId],[sourceSystemIdValue],[IDMSTypeId],[createdBy],[createdDate],[updatedBy],[updatedDate])
			VALUES
				(@guestid, @sourceSystemIdValue, @IDMSTypeID,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
		END

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdFromSourceTypeId]    Script Date: 05/10/2012 10:28:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestIdFromSourceTypeId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	get the guestId for a source system linkId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestIdFromSourceTypeId] 
	-- Add the parameters for the stored procedure here
	@sourceTypeIdValue nvarchar(50),
	@IDMSTypeName nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT guestId FROM source_system_link JOIN IDMS_TYPE ON IDMS_Type.IDMSTypeId = source_system_link.IDMSTypeId WHERE sourceSystemIDValue =@sourceTypeIdValue and IDMS_TYPE.IDMSTypeName=@IDMSTypeName;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestIdentifiersById]    Script Date: 05/10/2012 10:28:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestIdentifiersById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Retrieve all identifiers for a guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestIdentifiersById] 
	-- Add the parameters for the stored procedure here
	@guestId bigint
	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select 
	source_system_link.*,
	 IDMS_Type.IDMSTypeName as [type],
	 source_system_link.sourceSystemIdValue as value,
	 source_system_link.guestId as guestId 
	from source_system_link JOIN IDMS_Type on source_system_link.IDMSTypeId = IDMS_Type.IDMSTypeId where source_system_link.guestId = @guestId AND IDMSKEY= ''SOURCESYSTEM''
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestCelebrationsById]    Script Date: 05/10/2012 10:28:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestCelebrationsById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/15/2012
-- Description:	Get the guest celebrations by guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestCelebrationsById] 
	-- Add the parameters for the stored procedure here
	@guestId BigInt = 0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select * from celebration Join IDMS_Type on celebration.IDMSTypeId = IDMS_Type.IDMSTypeId where celebration.guestId =@guestId;
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 05/10/2012 10:28:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CreateTestUser]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateTestUser] 
	-- Add the parameters for the stored procedure here
	@NumberOfUsers int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @FirstNamesCount int
	DECLARE @LastNamesCount int
	DECLARE @FirstNameID int
	DECLARE @LastNameID int
	DECLARE @LastName nvarchar(200)
	DECLARE @FirstName nvarchar(200)
	DECLARE @GuestID bigint
	DECLARE @xBandID bigint
	DECLARE @BandID nvarchar(16)
	DECLARE @TapID nvarchar(16)
	DECLARE @LongRangeID nvarchar(16)
	DECLARE @IntVal bigint
	
	DECLARE @Index int

	SET @Index = 0
	
	SELECT @FirstNamesCount = COUNT(*) FROM [dbo].[FirstNames]
	
	SELECT @LastNamesCount = COUNT(*) FROM [dbo].[LastNames]
	
	WHILE @Index < @NumberOfUsers 
	BEGIN
	
		SET @FirstNameID = CAST(RAND() * @FirstNamesCount as int) + 1
		SET @LastNameID = CAST(RAND() * @LastNamesCount as int) + 1
		
		SELECT @FirstName = [Name] FROM [dbo].[FirstNames] WHERE [ID] = @FirstNameID
		SELECT @LastName = [Name] FROM [dbo].[LastNames] WHERE [ID] = @LastNameID
		
		IF @FirstName IS NULL
		BEGIN
			print ''First name ID not found''
		END
	
		IF @LastName IS NULL
		BEGIN
			print ''Last name ID not found''
		END
		
		DECLARE @HexString nvarchar(16)
		DECLARE @IntValue bigint
		SET @HexString = ''0123456789ABCDEF''
		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @BandID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @LongRangeID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   ''FF'' +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @TapID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   ''F0'' + 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		IF NOT EXISTS (SELECT ''X'' FROM [dbo].[xband] where [longRangeId] = @LongRangeID OR [tapId] = @tapID)
		BEGIN
			BEGIN TRANSACTION

			BEGIN TRY
					
				INSERT INTO [dbo].[xband]
				   ([bandId]
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
				VALUES
				   (@BandID
				   ,@LongRangeID
				   ,@TapID
				   ,NULL
				   ,NULL
				   ,@FirstName + '' '' + @LastName + ''''''s band''
				   ,NULL
				   ,1
				   ,''simulator''
				   ,GETUTCDATE()
				   ,''simulator''
				   ,GETUTCDATE())

				SELECT @xBandID = @@IDENTITY
			
				INSERT INTO [dbo].[guest]
				   ([IDMSID]
				   ,[IDMSTypeId]
				   ,[lastName]
				   ,[firstName]
				   ,[DOB]
				   ,[VisitCount]
				   ,[AvatarName]
				   ,[active]
				   ,[createdBy]
				   ,[createdDate]
				   ,[updatedBy]
				   ,[updatedDate])
				VALUES
				   (NEWID()
				   ,9
				   ,@FirstName
				   ,@LastName
				   ,NULL
				   ,CAST(RAND() * 100 as int)
				   ,NULL
				   ,1
				   ,''simulator''
				   ,GETUTCDATE()
				   ,''simulator''
				   ,GETUTCDATE())

				SELECT @GuestID = @@IDENTITY
				
				INSERT INTO [IDMS].[dbo].[guest_xband]
					   ([guestId]
					   ,[xbandId]
					   ,[createdBy]
					   ,[createdDate]
					   ,[updatedBy]
					   ,[updatedDate]
					   ,[active])
				 VALUES
					   (@GuestID
					   ,@xBandID
					   ,''simulator''
					   ,GETUTCDATE()
					   ,''simulator''
					   ,GETUTCDATE()
					   ,1)

			INSERT INTO [dbo].[source_system_link]
					   ([guestId]
					   ,[sourceSystemIdValue]
					   ,[IDMSTypeId]
					   ,[createdBy]
					   ,[createdDate]
					   ,[updatedBy]
					   ,[updatedDate])
				 VALUES
					   (@GuestId
					   ,REPLACE(CAST(NEWID() as nvarchar(200)),''-'','''')
					   ,9
					   ,''simulator''
					   ,GETUTCDATE()
					   ,''simulator''
					   ,GETUTCDATE())
			
				INSERT INTO [dbo].[xband_type]
						   ([xbandId]
						   ,[isPhysical])
					 VALUES
						   (@xBandID
						   ,0)

				COMMIT TRANSACTION
				
			END TRY
			BEGIN CATCH
			
				ROLLBACK TRANSACTION

			END CATCH
			
		
			SET @Index = @Index + 1
		END

	END

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestsByXbandId]    Script Date: 05/10/2012 10:28:04 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestsByXbandId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get guests assigned to an Xband by XbandId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestsByXbandId] 
	-- Add the parameters for the stored procedure here
	@xbandId bigint 
	  
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
    
    SELECT * from guest 
    JOIN guest_xband on guest_xband.guestId = guest.guestId
    JOIN xband on guest_xband.xbandId = xband.xbandId
    where xband.xbandId=@xbandId;
    


END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest__addresss_create]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest__addresss_create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Adds an address to a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest__addresss_create] 
	@guestId bigint,
	@addressType nvarchar(50),
	@address1 nvarchar(200),
	@address2 nvarchar(200) = NULL,
	@address3 nvarchar(200) = NULL,
	@city nvarchar(200),
	@state nvarchar(200),
	@countryCode nvarchar(3) = NULL,
	@postalCode nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	--Ignore invalid requests instead of setting at the parameter level
	--so that the stored procedure can be called when creating a guest
	--when no address data is provided.
	IF @addressType IS NULL OR 	@address1 IS NULL OR
	   @city IS NULL OR @state IS NULL OR 
	   @postalCode  IS NULL
	BEGIN
		RETURN;
	END
		   
	BEGIN TRY
	
		--TODO: Only in Caller??
		--BEGIN TRANSACTION
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @addressType
		AND		[IDMSKey] = ''ADDRESSTYPE''
		
		--TODO: Throw error if null or create a new item???
		
		INSERT INTO [dbo].[guest_address]
			([guestId]
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
	     VALUES
			(@guestId
			,@IDMSTypeID
			,@address1
			,@address2
			,@address3
			,@city
			,@state
			,@countryCode
			,@postalCode
			,N''IDMS''
			,GETUTCDATE()
			,N''IDMS''
			,GETUTCDATE())

		--TODO: Only in Caller??
		--COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		--TODO: Only in Caller??
		--ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByIdentifier]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByIdentifier]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using and indentifier value.
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
		  ,x.[secureid]
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
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetXBandsByGuestID]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetXBandsByGuestID]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the xbands for a guest
--              using the guest id.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetXBandsByGuestID] 
	@guestId bigint
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
		  ,x.[UID]
		  ,x.[secureid]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	FROM [dbo].[guest_xband] gx
	JOIN [dbo].[xband] x on x.[xbandid] = gx.[xbandid]
	WHERE gx.[guestid] = @guestid

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetIdentifiersByIdentifier]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetIdentifiersByIdentifier]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 3/15/2012
-- Description:	Gets all the identifiers for a guest
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetIdentifiersByIdentifier] 
	@sourceSystemIDValue nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT
	 it.IDMSTypeName as [type],
	 s.sourceSystemIdValue as value,
	 s.guestId as guestId, 
	  it.[IDMSTypeName], s.[sourceSystemIdValue], s.[guestId]
	FROM (SELECT	[guestid]
	  FROM	[dbo].[source_system_link]
	  WHERE [sourceSystemIdValue] = @Sourcesystemidvalue) t
	JOIN [dbo].[source_system_link] s on s.[guestid] = t.[guestid]
	JOIN [dbo].[IDMS_Type] it on it.[IDMSTypeID] = s.[IDMSTypeID]
	AND it.[IDMSKey] = ''SOURCESYSTEM''

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_guest_create]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_guest_create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 03/01/2012
-- Description:	Creates a guest.
-- =============================================
CREATE PROCEDURE [dbo].[usp_guest_create] 
	@guestId bigint OUTPUT,
	@guestType nvarchar(50),
	@lastname nvarchar(200),
	@firstname nvarchar(200),
	@DOB date,
	@middlename nvarchar(200) = NULL,
	@title nvarchar(50) = NULL,
	@suffix nvarchar(50) = NULL,
	@emailAddress nvarchar(200) = NULL,
	@parentEmail nvarchar(200) = NULL,
	@countryCode nvarchar(3) = NULL,
	@languageCode nvarchar(3) = NULL,
	@gender nvarchar(1) = NULL,
	@userName nvarchar(50) = NULL,
	@visitCount int = NULL,
	@avatarName nvarchar(50) = NULL,
	@addressType nvarchar(50) = NULL,
	@address1 nvarchar(200) = NULL,
	@address2 nvarchar(200) = NULL,
	@address3 nvarchar(200) = NULL,
	@city nvarchar(200) = NULL,
	@state nvarchar(200) = NULL,
	@postalCode nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		--TODO: Check IDMSTypeID to make sure type is guest type, if not throw error.
		
		DECLARE @IDMSTypeID int
		
		SELECT	@IDMSTypeID = [IDMSTypeID] 
		FROM	[dbo].[IDMS_Type] 
		WHERE	[IDMSTypeName] = @guestType
		AND		[IDMSKey] = ''GUESTTYPE''

		--Create guest
		INSERT INTO [dbo].[guest]
			([IDMSID],[IDMSTypeId],[lastName],[firstName],[middleName],[title],[suffix],[DOB],[VisitCount],[AvatarName]
			,[active],[emailAddress],[parentEmail],[countryCode],[languageCode],[gender],[userName],[createdBy],[createdDate],[updatedBy],[updatedDate])
		VALUES
			(NEWID(),@IDMSTypeID,@lastname,@firstname,@middlename,@title,@suffix,@DOB,@visitCount,@avatarName,
			 1,@emailAddress,@parentEmail,@countryCode,@languageCode,@gender,@userName,N''IDMS'',GETUTCDATE(),N''IDMS'',GETUTCDATE())
			
		--Capture id
		SELECT @guestid = @@IDENTITY 
	     
		--Create the XID
		DECLARE @sourceSystemIdValue nvarchar(200)
		DECLARE @sourceSystemIdType nvarchar(50)
		
		SET @sourceSystemIdValue = REPLACE(NEWID(),''-'','''')
		SET @sourceSystemIdType = ''xid''

		EXECUTE [dbo].[usp_source_system_link_create] 
		   @guestId = @guestId
		  ,@sourceSystemIdValue = @sourceSystemIdValue
		  ,@sourceSystemIdType = @sourceSystemIdType

		--Add address
		EXECUTE [dbo].[usp_guest__addresss_create] 
		   @guestId = @guestId
		  ,@addressType = @addressType
		  ,@address1 = @address1
		  ,@address2 = @address2
		  ,@address3 = @address3
		  ,@city = @city 
		  ,@state = @state
		  ,@countryCode = @countryCode
		  ,@postalCode = @postalCode

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByXBandId]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByXBandId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get and XBand by it''s XBandId.
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
		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes(''SHA1'', CONVERT(varbinary, x.[xbandid])))) as [secureid]
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

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByUID]    Script Date: 05/10/2012 10:28:05 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByUID]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
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
	,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes(''SHA1'', CONVERT(varbinary, x.[xbandid])))) as [secureid]
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
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByTapId]    Script Date: 05/10/2012 10:28:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByTapId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	Get an xband by a tap (short range) id.
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
		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes(''SHA1'', CONVERT(varbinary, x.[xbandid])))) as [secureid]
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
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandBySecureId]    Script Date: 05/10/2012 10:28:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandBySecureId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
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

   --F070A6FC07E200
   
	SELECT x.[xbandId]
		  ,x.[bandId]
		  ,x.[longRangeId]
		  ,x.[tapId]
--		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes(''SHA1'', CONVERT(varbinary, x.[xbandid])))) as [secureid]
		  ,x.[secureid]
		  ,x.[UID]
		  ,x.[bandFriendlyName]
		  ,x.[printedName]
		  ,x.[active]
		  ,x.[createdBy]
		  ,x.[createdDate]
		  ,x.[updatedBy]
		  ,x.[updatedDate]
	  FROM [dbo].[xband] x
	  --where CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes(''SHA1'', CONVERT(varbinary, x.[xbandid])))) = @secureId
	  where x.[secureid] = @secureId
    	
	Select DISTINCT
		guest.*,
		guest.DOB as dateOfBirth,
		party_guest.partyId as partyId
		from guest
		LEFT JOIN guest_xband on guest_xband.guestId = guest.guestId
		LEFT JOIN xband on guest_xband.xbandId = xband.xbandId
		JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId
		LEFT JOIN party_guest on guest.guestId = party_guest.guestId
		--where CONVERT(nvarchar,DecryptByKey(secureid_Encrypted, 1, HashBytes(''SHA1'', CONVERT(varbinary, xband.xbandid)))) = @secureId
		where xband.[secureid] = @secureId
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByLRId]    Script Date: 05/10/2012 10:28:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByLRId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/20/2012
-- Description:	
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
		  ,CONVERT(nvarchar,DecryptByKey(secureid_Encrypted, 1, HashBytes(''SHA1'', CONVERT(varbinary, x.xbandid)))) as [secureid]
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
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getXBandByBandId]    Script Date: 05/10/2012 10:28:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getXBandByBandId]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Robert
-- Create date: 3/27/2012
-- Description:	Get an XBand by BandId
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
		  ,CONVERT(nvarchar,DecryptByKey(x.[secureid_Encrypted], 1, HashBytes(''SHA1'', CONVERT(varbinary, x.[xbandid])))) as [secureid]
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
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestProfileById]    Script Date: 05/10/2012 10:28:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestProfileById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Robert
-- Create date: 3/15/2012
-- Description:	Get a guest profile by guestId.
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestProfileById]
	-- Add the parameters for the stored procedure here
	@guestId bigint
AS
BEGIN

	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select top 1
	guest.*,
	guest.DOB as dateOfBirth,
	party_guest.partyId as partyId
	
	
	from guest JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId LEFT JOIN party_guest on guest.guestId = party_guest.guestId where guest.guestId=@guestId and guest.active=1 ORDER BY party_guest.createdDate desc;
	SELECT * FROM xband JOIN guest_xband on guest_xband.xbandId=xband.xbandId where guest_xband.guestId=@guestId;
	SELECT * FROM celebration where guestId=@guestId;
	SELECT
	 source_system_link.*,
	 IDMS_Type.IDMSTypeName as [type],
	 source_system_link.sourceSystemIdValue as value,
	 source_system_link.guestId as guestId
	FROM source_system_link JOIN IDMS_Type on source_system_link.IDMSTypeId = IDMS_Type.IDMSTypeId where source_system_link.guestId=@guestId;
	
END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestById]    Script Date: 05/10/2012 10:28:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestById]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Retrieve a guest record by guestId
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestById] 
	-- Add the parameters for the stored procedure here
	@guestId bigint 
	  
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	Select TOP 1 * from guest JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId LEFT JOIN party_guest on guest.guestId = party_guest.guestId where guest.guestId=@guestId AND guest.active=1 ORDER BY party_guest.createddate desc; 
	SELECT * FROM xband RIGHT JOIN guest_xband on guest_xband.xbandId=xband.xbandId where guest_xband.guestId=@guestId AND guest_xband.active=1;
	
END

' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestByEmail]    Script Date: 05/10/2012 10:28:07 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestByEmail]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Robert
-- Create date: 3/16/2012
-- Description:	Get a guest object by EmailAddress
-- =============================================
CREATE PROCEDURE [dbo].[usp_getGuestByEmail] 
	-- Add the parameters for the stored procedure here
	@emailAddress nvarchar(100)
	 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
    Select Top 1 * 
    from guest 
    JOIN IDMS_Type on guest.IDMSTypeId=IDMS_Type.IDMSTypeId 
    LEFT JOIN party_guest on guest.guestId = party_guest.guestId
     where guest.emailAddress=@emailAddress 
     AND guest.active=1
     ORDER BY party_guest.createddate desc;
END

' 
END
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 05/10/2012 10:27:39 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest]'))
ALTER TABLE [dbo].[guest] CHECK CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 05/10/2012 10:27:44 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_system_id_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_system_id_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 05/10/2012 10:27:44 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link]  WITH NOCHECK ADD  CONSTRAINT [FK_source_type_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_source_type_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[source_system_link]'))
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_type_guest]
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 05/10/2012 10:27:47 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 05/10/2012 10:27:47 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_celebration_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[celebration]'))
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 05/10/2012 10:27:50 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party]  WITH CHECK ADD  CONSTRAINT [FK_party_guest] FOREIGN KEY([primaryGuestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party]'))
ALTER TABLE [dbo].[party] CHECK CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 05/10/2012 10:27:53 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 05/10/2012 10:27:53 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband]  WITH NOCHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_xband_xband]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_xband]'))
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 05/10/2012 10:27:56 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 05/10/2012 10:27:56 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_phone_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_phone]'))
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 05/10/2012 10:28:00 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 05/10/2012 10:28:00 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_IDMS_Type] FOREIGN KEY([IDMStypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_guest_info_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[guest_address]'))
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 05/10/2012 10:28:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_guest]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 05/10/2012 10:28:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_IDMS_Type]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 05/10/2012 10:28:03 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_party] FOREIGN KEY([partyId])
REFERENCES [dbo].[party] ([partyId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_party_guest_party]') AND parent_object_id = OBJECT_ID(N'[dbo].[party_guest]'))
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_party]
GO

/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 01/25/2012 10:40:39 ******/
SET IDENTITY_INSERT [dbo].[IDMS_Type] ON
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (1, N'Home Address', NULL, N'ADDRESSTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (2, N'Billing Address', NULL, N'ADDRESSTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (3, N'Home Phone', NULL, N'PHONENUMBER', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (4, N'Work Phone', NULL, N'PHONENUMBER', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (5, N'Cell Phone', NULL, N'PHONENUMBER', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (6, N'Other Phone', NULL, N'PHONENUMBER', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate])
 VALUES (7, N'Park Guest', NULL, N'GUESTTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (8, N'Simulator Guest', NULL, N'GUESTTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (9, N'Test Guest', NULL, N'GUESTTYPE', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (10, N'gxp-link-id', NULL, N'SOURCESYSTEM', N'IDMS',GETUTCDATE(), N'IDMS',GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (11, N'Birthday', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (12, N'Anniversary', NULL, N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (16, N'IDMS', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (17, N'DME', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (18, N'XBMS', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (19, N'xid', NULL, N'SOURCESYSTEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (20, N'GuestEnteredScheduleItem', NULL, N'SCHEDULEITEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (21, N'NonBookableScheduleItem', NULL, N'SCHEDULEITEM', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (23, N'guest party', NULL, N'PARTYTYPE', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (26, N'Engagement', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (27, N'Graduation', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (28, N'Honeymoon', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (29, N'Personal Triumph', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (30, N'Reunion', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (31, N'Wedding', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (32, N'Other', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
INSERT [dbo].[IDMS_Type] ([IDMSTypeId], [IDMSTypeName], [IDMSTypeValue], [IDMSkey], [createdBy], [createdDate], [updatedBy], [updatedDate]) 
VALUES (33, N'First Visit', N'', N'CELEBRATION', N'IDMS', GETUTCDATE(), N'IDMS', GETUTCDATE())
SET IDENTITY_INSERT [dbo].[IDMS_Type] OFF


IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = '1.0.0.0000')
	INSERT INTO [dbo].[schema_version]
			   ([Version]
			   ,[script_name]
			   ,[date_applied])
		 VALUES
			   ('1.0.0.0000'
			   ,'Baseline.sql'
			   ,GETUTCDATE())
GO

/****** Object:  StoredProcedure [dbo].[usp_schema_version_retrieve]    Script Date: 05/14/2012 13:13:49 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_schema_version_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_schema_version_retrieve]
GO

/****** Object:  StoredProcedure [dbo].[usp_schema_version_retrieve]    Script Date: 05/14/2012 13:13:49 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO



-- =============================================
-- Author:		Ted Crane
-- Create date: 05/14/2012
-- Description:	Retrieves the current schema
--              version.
-- =============================================
CREATE PROCEDURE [dbo].[usp_schema_version_retrieve]
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
	SELECT TOP 1 [version]		
	FROM [dbo].[schema_version]
	ORDER BY [schema_version_id] DESC 

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
