:setvar previousversion '1.7.1.0002'
:setvar updateversion '1.7.1.0003'

USE [$(databasename)]

:on error exit

GO

DECLARE @currentversion varchar(12)

SET @currentversion = 
	(SELECT TOP 1 [version]		
	 FROM [dbo].[schema_version]
	 ORDER BY [schema_version_id] DESC)
	 
IF (@currentversion <> $(previousversion)) OR @currentversion IS NULL
BEGIN
	PRINT 'Current database version needs to be ' + $(previousversion)
	PRINT 'Current version is ' + @currentversion
	PRINT 'No changes made.'
	RAISERROR ('Incorrect database version.',16,1);
END
ELSE
BEGIN
	PRINT 'Updates for database version ' + $(updateversion) + ' started.'	
END
GO

/**
** Remove xi and jms listener tables and stored procedures from XBRMS database, since xi
** is now using a separate databse.
**/

/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_Guest]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_Guest]
GO
/****** Object:  ForeignKey [FK__GuestOrde__Guest__65C116E7]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__GuestOrde__Guest__65C116E7]') AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
ALTER TABLE [gxp].[GuestOrderMap] DROP CONSTRAINT [FK__GuestOrde__Guest__65C116E7]
GO
/****** Object:  ForeignKey [FK__GuestOrde__Order__67A95F59]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__GuestOrde__Order__67A95F59]') AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
ALTER TABLE [gxp].[GuestOrderMap] DROP CONSTRAINT [FK__GuestOrde__Order__67A95F59]
GO
/****** Object:  ForeignKey [FK_GuestOrderMap_BusEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_GuestOrderMap_BusEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
ALTER TABLE [gxp].[GuestOrderMap] DROP CONSTRAINT [FK_GuestOrderMap_BusEvent]
GO
/****** Object:  ForeignKey [FK__OrderEven__Order__5772F790]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Order__5772F790]') AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
ALTER TABLE [gxp].[OrderEvent] DROP CONSTRAINT [FK__OrderEven__Order__5772F790]
GO
/****** Object:  ForeignKey [FK__OrderEven__Order__58671BC9]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Order__58671BC9]') AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
ALTER TABLE [gxp].[OrderEvent] DROP CONSTRAINT [FK__OrderEven__Order__58671BC9]
GO
/****** Object:  ForeignKey [FK__OrderEven__Table__595B4002]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Table__595B4002]') AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
ALTER TABLE [gxp].[OrderEvent] DROP CONSTRAINT [FK__OrderEven__Table__595B4002]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_AppointmentReason]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_AppointmentReason]') AND parent_object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]'))
ALTER TABLE [gxp].[RedemptionEvent] DROP CONSTRAINT [FK_RedemptionEvent_AppointmentReason]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_AppointmentStatus]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_AppointmentStatus]') AND parent_object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]'))
ALTER TABLE [gxp].[RedemptionEvent] DROP CONSTRAINT [FK_RedemptionEvent_AppointmentStatus]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_Facility]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_Facility]') AND parent_object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]'))
ALTER TABLE [gxp].[RedemptionEvent] DROP CONSTRAINT [FK_RedemptionEvent_Facility]
GO
/****** Object:  ForeignKey [FK__Restauran__Facil__436BFEE3]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__436BFEE3]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]'))
ALTER TABLE [gxp].[RestaurantEvent] DROP CONSTRAINT [FK__Restauran__Facil__436BFEE3]
GO
/****** Object:  ForeignKey [FK__Restauran__Resta__4277DAAA]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Resta__4277DAAA]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]'))
ALTER TABLE [gxp].[RestaurantEvent] DROP CONSTRAINT [FK__Restauran__Resta__4277DAAA]
GO
/****** Object:  ForeignKey [FK__Restauran__Facil__558AAF1E]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__558AAF1E]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantOrder]'))
ALTER TABLE [gxp].[RestaurantOrder] DROP CONSTRAINT [FK__Restauran__Facil__558AAF1E]
GO
/****** Object:  ForeignKey [FK__Restauran__Facil__361203C5]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__361203C5]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantTable]'))
ALTER TABLE [gxp].[RestaurantTable] DROP CONSTRAINT [FK__Restauran__Facil__361203C5]
GO
/****** Object:  ForeignKey [FK__TableEven__Facil__38EE7070]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Facil__38EE7070]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
ALTER TABLE [gxp].[TableEvent] DROP CONSTRAINT [FK__TableEven__Facil__38EE7070]
GO
/****** Object:  ForeignKey [FK__TableEven__Table__37FA4C37]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Table__37FA4C37]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
ALTER TABLE [gxp].[TableEvent] DROP CONSTRAINT [FK__TableEven__Table__37FA4C37]
GO
/****** Object:  ForeignKey [FK__TableEven__Table__39E294A9]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Table__39E294A9]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
ALTER TABLE [gxp].[TableEvent] DROP CONSTRAINT [FK__TableEven__Table__39E294A9]
GO
/****** Object:  ForeignKey [FK__TableGues__Busin__6C6E1476]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Busin__6C6E1476]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
ALTER TABLE [gxp].[TableGuestOrderMap] DROP CONSTRAINT [FK__TableGues__Busin__6C6E1476]
GO
/****** Object:  ForeignKey [FK__TableGues__Order__6B79F03D]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Order__6B79F03D]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
ALTER TABLE [gxp].[TableGuestOrderMap] DROP CONSTRAINT [FK__TableGues__Order__6B79F03D]
GO
/****** Object:  ForeignKey [FK__TableGues__Resta__6A85CC04]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Resta__6A85CC04]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
ALTER TABLE [gxp].[TableGuestOrderMap] DROP CONSTRAINT [FK__TableGues__Resta__6A85CC04]
GO
/****** Object:  ForeignKey [FK__TapEvent__Facili__314D4EA8]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TapEvent__Facili__314D4EA8]') AND parent_object_id = OBJECT_ID(N'[gxp].[TapEvent]'))
ALTER TABLE [gxp].[TapEvent] DROP CONSTRAINT [FK__TapEvent__Facili__314D4EA8]
GO
/****** Object:  ForeignKey [FK__TapEvent__TapEve__30592A6F]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TapEvent__TapEve__30592A6F]') AND parent_object_id = OBJECT_ID(N'[gxp].[TapEvent]'))
ALTER TABLE [gxp].[TapEvent] DROP CONSTRAINT [FK__TapEvent__TapEve__30592A6F]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] DROP CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_Event_BandType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_BandType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_BandType]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_Event_Guest]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] DROP CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_PerformanceMetric_Facility]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[dbo].[FK_PerformanceMetric_Facility]') AND parent_object_id = OBJECT_ID(N'[dbo].[PerformanceMetric]'))
ALTER TABLE [dbo].[PerformanceMetric] DROP CONSTRAINT [FK_PerformanceMetric_Facility]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] DROP CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_InVehicleEvent_Event]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_InVehicleEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[InVehicleEvent]'))
ALTER TABLE [rdr].[InVehicleEvent] DROP CONSTRAINT [FK_InVehicleEvent_Event]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] DROP CONSTRAINT [FK_LoadEvent_Event]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] DROP CONSTRAINT [FK_ReaderEvent_Event]
GO
/****** Object:  ForeignKey [AK_PE_ReasonTypeID_ReasonName]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[AK_PE_ReasonTypeID_ReasonName]') AND parent_object_id = OBJECT_ID(N'[rdr].[ParkEntryReasonType]'))
ALTER TABLE [rdr].[ParkEntryReasonType] DROP CONSTRAINT [AK_PE_ReasonTypeID_ReasonName]
GO
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ExitEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ExitEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_AbandonEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_AbandonEvent_Create]
GO
/****** Object:  StoredProcedure [xgs].[usp_GuestLocation_Retrieve]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[xgs].[usp_GuestLocation_Retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [xgs].[usp_GuestLocation_Retrieve]
GO
/****** Object:  StoredProcedure [dbo].[usp_GuestOrderMap_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GuestOrderMap_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GuestOrderMap_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_InVehicleEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_InVehicleEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_InVehicleEvent_Create]
GO
/****** Object:  StoredProcedure [gff].[usp_OrderEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gff].[usp_OrderEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gff].[usp_OrderEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_LoadEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_LoadEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ReaderEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ReaderEvent_Create]
GO
/****** Object:  StoredProcedure [gff].[usp_TableEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gff].[usp_TableEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gff].[usp_TableEvent_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_TableGuestOrderMap_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_TableGuestOrderMap_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_TableGuestOrderMap_Create]
GO
/****** Object:  StoredProcedure [gff].[usp_TapEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gff].[usp_TapEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gff].[usp_TapEvent_Create]
GO
/****** Object:  StoredProcedure [gxp].[usp_RedemptionEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_RedemptionEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_RedemptionEvent_Create]
GO
/****** Object:  StoredProcedure [gff].[usp_RestaurantEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gff].[usp_RestaurantEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gff].[usp_RestaurantEvent_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitGetVisits]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitGetVisits]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitGetVisits]
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Metric_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Metric_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_ProjectedData]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ProjectedData]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ProjectedData]
GO
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetric_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PerformanceMetric_create]
GO
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetric_retrieve]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetric_retrieve]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PerformanceMetric_retrieve]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetQueueCountForAttraction]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetQueueCountForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetQueueCountForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOverrideOffersets]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverrideOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayGuestsForReader]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayGuestsForReader]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayGuestsForReader]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayQueueCountForAttraction]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayQueueCountForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayQueueCountForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetFacilityGuestsByReader]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetFacilityGuestsByReader]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetFacilityGuestsByReader]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuest]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuest]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuest]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestReads]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestReads]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestReads]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestsForAttraction]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestsForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestsForSearch]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForSearch]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestsForSearch]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestWaitTimeFP]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestWaitTimeFP]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestWaitTimeFP]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetHourlyRedemptions]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetHourlyRedemptions]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetHourlyRedemptions]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneForAttraction]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneReasonCodes]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneReasonCodes]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementAll]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementAll]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementAll]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummary]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementSummary]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummaryHourly]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummaryHourly]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementSummaryHourly]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummary]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetExecSummary]
GO
/****** Object:  Table [gxp].[TableEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Facil__38EE7070]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
ALTER TABLE [gxp].[TableEvent] DROP CONSTRAINT [FK__TableEven__Facil__38EE7070]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Table__37FA4C37]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
ALTER TABLE [gxp].[TableEvent] DROP CONSTRAINT [FK__TableEven__Table__37FA4C37]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableEven__Table__39E294A9]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableEvent]'))
ALTER TABLE [gxp].[TableEvent] DROP CONSTRAINT [FK__TableEven__Table__39E294A9]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[TableEvent]') AND type in (N'U'))
DROP TABLE [gxp].[TableEvent]
GO
/****** Object:  Table [gxp].[TableGuestOrderMap]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Busin__6C6E1476]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
ALTER TABLE [gxp].[TableGuestOrderMap] DROP CONSTRAINT [FK__TableGues__Busin__6C6E1476]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Order__6B79F03D]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
ALTER TABLE [gxp].[TableGuestOrderMap] DROP CONSTRAINT [FK__TableGues__Order__6B79F03D]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TableGues__Resta__6A85CC04]') AND parent_object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]'))
ALTER TABLE [gxp].[TableGuestOrderMap] DROP CONSTRAINT [FK__TableGues__Resta__6A85CC04]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[TableGuestOrderMap]') AND type in (N'U'))
DROP TABLE [gxp].[TableGuestOrderMap]
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BlueLaneEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BlueLaneEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Event_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Event_Create]
GO
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] DROP CONSTRAINT [FK_AbandonEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[AbandonEvent]') AND type in (N'U'))
DROP TABLE [rdr].[AbandonEvent]
GO
/****** Object:  Table [gxp].[GuestOrderMap]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__GuestOrde__Guest__65C116E7]') AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
ALTER TABLE [gxp].[GuestOrderMap] DROP CONSTRAINT [FK__GuestOrde__Guest__65C116E7]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__GuestOrde__Order__67A95F59]') AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
ALTER TABLE [gxp].[GuestOrderMap] DROP CONSTRAINT [FK__GuestOrde__Order__67A95F59]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_GuestOrderMap_BusEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]'))
ALTER TABLE [gxp].[GuestOrderMap] DROP CONSTRAINT [FK_GuestOrderMap_BusEvent]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[GuestOrderMap]') AND type in (N'U'))
DROP TABLE [gxp].[GuestOrderMap]
GO
/****** Object:  Table [rdr].[InVehicleEvent]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_InVehicleEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[InVehicleEvent]'))
ALTER TABLE [rdr].[InVehicleEvent] DROP CONSTRAINT [FK_InVehicleEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[InVehicleEvent]') AND type in (N'U'))
DROP TABLE [rdr].[InVehicleEvent]
GO
/********* Object: Tabel [rdr].[ParkEntryReasonType] ********/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ParkEntryReasonType]') AND type in (N'U'))
DROP TABLE [rdr].[ParkEntryReasonType]
GO
/****** Object:  Table [rdr].[ParkEntryEvent]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ParkEntryEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ParkEntryEvent]
GO
/****** Object:  Table [gxp].[OrderEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Order__5772F790]') AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
ALTER TABLE [gxp].[OrderEvent] DROP CONSTRAINT [FK__OrderEven__Order__5772F790]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Order__58671BC9]') AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
ALTER TABLE [gxp].[OrderEvent] DROP CONSTRAINT [FK__OrderEven__Order__58671BC9]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__OrderEven__Table__595B4002]') AND parent_object_id = OBJECT_ID(N'[gxp].[OrderEvent]'))
ALTER TABLE [gxp].[OrderEvent] DROP CONSTRAINT [FK__OrderEven__Table__595B4002]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[OrderEvent]') AND type in (N'U'))
DROP TABLE [gxp].[OrderEvent]
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] DROP CONSTRAINT [FK_LoadEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[LoadEvent]') AND type in (N'U'))
DROP TABLE [rdr].[LoadEvent]
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] DROP CONSTRAINT [FK_ReaderEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ReaderEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ReaderEvent]
GO
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] DROP CONSTRAINT [FK_ExitEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ExitEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ExitEvent]
GO
/****** Object:  Table [rdr].[Event]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_BandType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_BandType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_EventType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Facility]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]
GO
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF__Event__BandTypeI__395884C4]') AND type = 'D')
BEGIN
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [DF__Event__BandTypeI__395884C4]
END
GO
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_Event_CreatedDate]') AND type = 'D')
BEGIN
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [DF_Event_CreatedDate]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND type in (N'U'))
DROP TABLE [rdr].[Event]
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]') AND type in (N'U'))
DROP TABLE [gxp].[BlueLaneEvent]
GO
/****** Object:  Table [gxp].[RedemptionEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_AppointmentReason]') AND parent_object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]'))
ALTER TABLE [gxp].[RedemptionEvent] DROP CONSTRAINT [FK_RedemptionEvent_AppointmentReason]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_AppointmentStatus]') AND parent_object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]'))
ALTER TABLE [gxp].[RedemptionEvent] DROP CONSTRAINT [FK_RedemptionEvent_AppointmentStatus]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_Facility]') AND parent_object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]'))
ALTER TABLE [gxp].[RedemptionEvent] DROP CONSTRAINT [FK_RedemptionEvent_Facility]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RedemptionEvent]') AND type in (N'U'))
DROP TABLE [gxp].[RedemptionEvent]
GO
/****** Object:  Table [gxp].[RestaurantEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__436BFEE3]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]'))
ALTER TABLE [gxp].[RestaurantEvent] DROP CONSTRAINT [FK__Restauran__Facil__436BFEE3]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Resta__4277DAAA]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]'))
ALTER TABLE [gxp].[RestaurantEvent] DROP CONSTRAINT [FK__Restauran__Resta__4277DAAA]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RestaurantEvent]') AND type in (N'U'))
DROP TABLE [gxp].[RestaurantEvent]
GO
/****** Object:  Table [gxp].[RestaurantOrder]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__558AAF1E]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantOrder]'))
ALTER TABLE [gxp].[RestaurantOrder] DROP CONSTRAINT [FK__Restauran__Facil__558AAF1E]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RestaurantOrder]') AND type in (N'U'))
DROP TABLE [gxp].[RestaurantOrder]
GO
/****** Object:  Table [gxp].[RestaurantTable]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__Restauran__Facil__361203C5]') AND parent_object_id = OBJECT_ID(N'[gxp].[RestaurantTable]'))
ALTER TABLE [gxp].[RestaurantTable] DROP CONSTRAINT [FK__Restauran__Facil__361203C5]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[RestaurantTable]') AND type in (N'U'))
DROP TABLE [gxp].[RestaurantTable]
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_Facility]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_MetricType]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Metric]') AND type in (N'U'))
DROP TABLE [rdr].[Metric]
GO
/****** Object:  Table [gxp].[TapEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TapEvent__Facili__314D4EA8]') AND parent_object_id = OBJECT_ID(N'[gxp].[TapEvent]'))
ALTER TABLE [gxp].[TapEvent] DROP CONSTRAINT [FK__TapEvent__Facili__314D4EA8]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK__TapEvent__TapEve__30592A6F]') AND parent_object_id = OBJECT_ID(N'[gxp].[TapEvent]'))
ALTER TABLE [gxp].[TapEvent] DROP CONSTRAINT [FK__TapEvent__TapEve__30592A6F]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[TapEvent]') AND type in (N'U'))
DROP TABLE [gxp].[TapEvent]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestEntitlements]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestEntitlements]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestEntitlements]
GO
/****** Object:  StoredProcedure [dbo].[usp_AddSubwayDiagram]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_AddSubwayDiagram]') AND type in (N'P', N'PC'))

DROP PROCEDURE [dbo].[usp_AddSubwayDiagram]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayDiagramListForAttraction]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayDiagramListForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayDiagramListForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedForCal]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedForCal]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOffersets]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPerfValuesForName]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetPerfValuesForName]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetPerfValuesForName]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedForDate]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedForDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSelectedForDate]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedOffersets]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSelectedOffersets]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayDiagramForAttraction]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayDiagramForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayDiagramForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_PreArrivalData]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PreArrivalData]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PreArrivalData]
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitedDaily]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedDaily]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitedDaily]
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitedEligible]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitedEligible]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitedEligible]
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitEngagedToDate]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitEngagedToDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitEngagedToDate]
GO
/****** Object:  StoredProcedure [dbo].[usp_PerformanceMetricDesc_create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_PerformanceMetricDesc_create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_PerformanceMetricDesc_create]
GO
/****** Object:  StoredProcedure [gff].[usp_KitchenInfo_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gff].[usp_KitchenInfo_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gff].[usp_KitchenInfo_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSubwayDiagramForID]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSubwayDiagramForID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSubwayDiagramForID]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetProgramStartDate]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetProgramStartDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetProgramStartDate]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOverridesForCal]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOverridesForCal]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]
GO
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Guest_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Guest_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_Guest_update]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_Guest_update]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_Guest_update]
GO
/****** Object:  StoredProcedure [dbo].[usp_AddFacility]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_AddFacility]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_AddFacility]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetHTMLPage]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetHTMLPage]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetHTMLPage]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetFacilitiesList]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetFacilitiesList]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetFacilitiesList]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetCurrentGUID]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetCurrentGUID]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetCurrentGUID]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReport]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetDailyReport]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetDailyReport]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReportTodate]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetDailyReportTodate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetDailyReportTodate]
GO
/****** Object:  StoredProcedure [dbo].[usp_CheckGUIDForHTMLUpdate]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_CheckGUIDForHTMLUpdate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_CheckGUIDForHTMLUpdate]
GO
/****** Object:  StoredProcedure [dbo].[usp_ConfigGetParameter]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ConfigGetParameter]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ConfigGetParameter]
GO
/****** Object:  StoredProcedure [dbo].[usp_ConfigPersistParam]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_ConfigPersistParam]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_ConfigPersistParam]
GO
/****** Object:  StoredProcedure [gxp].[usp_CreateEntitlementStatus]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_CreateEntitlementStatus]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_CreateEntitlementStatus]
GO
/****** Object:  StoredProcedure [dbo].[usp_DeleteFacility]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_DeleteFacility]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_DeleteFacility]
GO
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BusinessEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BusinessEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ParkEntryEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ParkEntryEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ParkEntryEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ParkEntryTappedEvent_Create]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ParkEntryTappedEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ParkEntryTappedEvent_Create]
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_Guest]
GO
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF_BusinessEvent_CreatedDate]') AND type = 'D')
BEGIN
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [DF_BusinessEvent_CreatedDate]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEvent]
GO
/****** Object:  Table [rdr].[Facility]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] DROP CONSTRAINT [FK_Facility_FacilityType]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Facility]') AND type in (N'U'))
DROP TABLE [rdr].[Facility]
GO
/****** Object:  StoredProcedure [dbo].[usp_RecruitTotalRecruited]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RecruitTotalRecruited]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RecruitTotalRecruited]
GO
/****** Object:  StoredProcedure [dbo].[usp_SetDailyReport]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_SetDailyReport]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_SetDailyReport]
GO
/****** Object:  StoredProcedure [dbo].[usp_UpdateHTMLPage]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_UpdateHTMLPage]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_UpdateHTMLPage]
GO
/****** Object:  StoredProcedure [dbo].[usp_UpdateProgramStartDate]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_UpdateProgramStartDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_UpdateProgramStartDate]
GO
/****** Object:  StoredProcedure [dbo].[usp_XbrcPerf_Insert_1]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_XbrcPerf_Insert_1]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_XbrcPerf_Insert_1]
GO
/****** Object:  Table [dbo].[xBRCEventsForSTI]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xBRCEventsForSTI]') AND type in (N'U'))
DROP TABLE [dbo].[xBRCEventsForSTI]
GO
/****** Object:  Table [dbo].[XbrcPerf]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcPerf]') AND type in (N'U'))
DROP TABLE [dbo].[XbrcPerf]
GO
/****** Object:  Table [dbo].[xiFacilities]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xiFacilities]') AND type in (N'U'))
DROP TABLE [dbo].[xiFacilities]
GO
/****** Object:  Table [gxp].[XiPageGUIDs]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiPageGUIDs]') AND type in (N'U'))
DROP TABLE [gxp].[XiPageGUIDs]
GO
/****** Object:  Table [gxp].[XiPageSource]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiPageSource]') AND type in (N'U'))
DROP TABLE [gxp].[XiPageSource]
GO
/****** Object:  Table [gxp].[XiSubwayDiagrams]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[XiSubwayDiagrams]') AND type in (N'U'))
DROP TABLE [gxp].[XiSubwayDiagrams]
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[FacilityType]') AND type in (N'U'))
DROP TABLE [rdr].[FacilityType]
GO
/****** Object:  Table [rdr].[Guest]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF__Guest__GuestType__37703C52]') AND type = 'D')
BEGIN
ALTER TABLE [rdr].[Guest] DROP CONSTRAINT [DF__Guest__GuestType__37703C52]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Guest]') AND type in (N'U'))
DROP TABLE [rdr].[Guest]
GO
/****** Object:  Table [gxp].[EventLocation]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EventLocation]') AND type in (N'U'))
DROP TABLE [gxp].[EventLocation]
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[EventType]') AND type in (N'U'))
DROP TABLE [rdr].[EventType]
GO
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventSubType]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEventSubType]
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventType]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEventType]
GO
/****** Object:  Table [dbo].[DailyPilotReport]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF__DailyPilo__creat__1E6F845E]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[DailyPilotReport] DROP CONSTRAINT [DF__DailyPilo__creat__1E6F845E]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DailyPilotReport]') AND type in (N'U'))
DROP TABLE [dbo].[DailyPilotReport]
GO
/****** Object:  Table [dbo].[DAYS_OF_YEAR]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DAYS_OF_YEAR]') AND type in (N'U'))
DROP TABLE [dbo].[DAYS_OF_YEAR]
GO
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EntitlementStatus]') AND type in (N'U'))
DROP TABLE [gxp].[EntitlementStatus]
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[MetricType]') AND type in (N'U'))
DROP TABLE [rdr].[MetricType]
GO
/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[OffersetWindow]') AND type in (N'U'))
DROP TABLE [dbo].[OffersetWindow]
GO
/****** Object:  Table [gxp].[KitchenInfo]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[KitchenInfo]') AND type in (N'U'))
DROP TABLE [gxp].[KitchenInfo]
GO
/****** Object:  Table [gxp].[AppointmentReason]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[AppointmentReason]') AND type in (N'U'))
DROP TABLE [gxp].[AppointmentReason]
GO
/****** Object:  Table [gxp].[AppointmentStatus]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[AppointmentStatus]') AND type in (N'U'))
DROP TABLE [gxp].[AppointmentStatus]
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Attraction]') AND type in (N'U'))
DROP TABLE [rdr].[Attraction]
GO
/****** Object:  Table [dbo].[AttractionCapacity]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AttractionCapacity]') AND type in (N'U'))
DROP TABLE [dbo].[AttractionCapacity]
GO
/****** Object:  Table [rdr].[BandType]    Script Date: 06/11/2013 17:48:47 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[BandType]') AND type in (N'U'))
DROP TABLE [rdr].[BandType]
GO
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 06/11/2013 17:48:46 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[ReasonCode]') AND type in (N'U'))
DROP TABLE [gxp].[ReasonCode]
GO
/****** Object:  Table [dbo].[LoadxBRCTimetable]    Script Date: 06/11/2013 17:48:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoadxBRCTimetable]') AND type in (N'U'))
DROP TABLE [dbo].[LoadxBRCTimetable]
GO
/****** Object:  Schema [gff]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'gff')
DROP SCHEMA [gff]
GO
/****** Object:  Schema [gxp]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'gxp')
DROP SCHEMA [gxp]
GO
/****** Object:  Schema [rdr]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'rdr')
DROP SCHEMA [rdr]
GO
/****** Object:  Schema [xgs]    Script Date: 06/11/2013 17:48:48 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'xgs')
DROP SCHEMA [xgs]
GO

/**
** Update schema version
**/

IF NOT EXISTS (SELECT 'X' FROM [dbo].[schema_version] WHERE [version] = $(updateversion))
BEGIN
        INSERT INTO [dbo].[schema_version]
                           ([Version]
                           ,[script_name]
                           ,[date_applied])
                 VALUES
                           ($(updateversion)
                           ,'update-xbrms-' + $(updateversion) + '.sql'
                           ,GETUTCDATE())
END
ELSE
BEGIN
        UPDATE [dbo].[schema_version]
        SET [date_applied] = GETUTCDATE()
        WHERE [version] = $(updateversion)
END

PRINT 'Updates for database version '  + $(updateversion) + ' completed.' 

GO
