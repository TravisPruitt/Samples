USE [Synaps_XBRMS]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 05/08/2012 09:17:13 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 05/08/2012 09:17:13 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 05/08/2012 09:17:13 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_Guest]    Script Date: 05/08/2012 09:17:13 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] DROP CONSTRAINT [FK_BusinessEvent_Guest]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 05/08/2012 09:17:17 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] DROP CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 05/08/2012 09:17:20 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 05/08/2012 09:17:20 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_Event_Guest]    Script Date: 05/08/2012 09:17:20 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]
GO
/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 05/08/2012 09:17:23 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 05/08/2012 09:17:23 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 05/08/2012 09:17:23 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] DROP CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 05/08/2012 09:17:36 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 05/08/2012 09:17:36 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 05/08/2012 09:17:39 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] DROP CONSTRAINT [FK_LoadEvent_Event]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 05/08/2012 09:17:43 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] DROP CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 05/08/2012 09:17:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] DROP CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 05/08/2012 09:17:50 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] DROP CONSTRAINT [FK_ReaderEvent_Event]
GO
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_AbandonEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_AbandonEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ExitEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ExitEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_LoadEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_LoadEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ReaderEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_ReaderEvent_Create]
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 05/08/2012 09:17:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Metric_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Metric_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummary]    Script Date: 05/08/2012 09:17:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetEntitlementSummary]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummary]    Script Date: 05/08/2012 09:17:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetExecSummary]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuest]    Script Date: 05/08/2012 09:17:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuest]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuest]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestReads]    Script Date: 05/08/2012 09:17:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestReads]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestReads]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestsForAttraction]    Script Date: 05/08/2012 09:17:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestsForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestWaitTimeFP]    Script Date: 05/08/2012 09:17:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestWaitTimeFP]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_getGuestWaitTimeFP]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneForAttraction]    Script Date: 05/08/2012 09:17:51 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneReasonCodes]    Script Date: 05/08/2012 09:17:50 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneReasonCodes]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
GO
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 05/08/2012 09:17:50 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Event_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Event_Create]
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 05/08/2012 09:17:50 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BlueLaneEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BlueLaneEvent_Create]
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 05/08/2012 09:17:50 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] DROP CONSTRAINT [FK_ReaderEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ReaderEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ReaderEvent]
GO
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 05/08/2012 09:17:47 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] DROP CONSTRAINT [FK_AbandonEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[AbandonEvent]') AND type in (N'U'))
DROP TABLE [rdr].[AbandonEvent]
GO
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 05/08/2012 09:17:43 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] DROP CONSTRAINT [FK_ExitEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ExitEvent]') AND type in (N'U'))
DROP TABLE [rdr].[ExitEvent]
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 05/08/2012 09:17:39 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] DROP CONSTRAINT [FK_LoadEvent_Event]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[LoadEvent]') AND type in (N'U'))
DROP TABLE [rdr].[LoadEvent]
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 05/08/2012 09:17:36 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_Facility]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] DROP CONSTRAINT [FK_Metric_MetricType]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Metric]') AND type in (N'U'))
DROP TABLE [rdr].[Metric]
GO
/****** Object:  StoredProcedure [gxp].[usp_BusinessEvent_Create]    Script Date: 05/08/2012 09:17:25 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BusinessEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_BusinessEvent_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReport]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetDailyReport]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetDailyReport]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReportTodate]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetDailyReportTodate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetDailyReportTodate]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPerfValuesForName]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetPerfValuesForName]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetPerfValuesForName]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedForCal]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedForCal]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOffersets]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetRedeemedOffersets]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedForDate]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedForDate]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSelectedForDate]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedOffersets]    Script Date: 05/08/2012 09:17:24 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetSelectedOffersets]
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestEntitlements]    Script Date: 05/08/2012 09:17:23 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestEntitlements]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_GetGuestEntitlements]
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 05/08/2012 09:17:23 ******/
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
/****** Object:  Table [rdr].[Event]    Script Date: 05/08/2012 09:17:20 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_EventType]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Facility]
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] DROP CONSTRAINT [FK_Event_Guest]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND type in (N'U'))
DROP TABLE [rdr].[Event]
GO
/****** Object:  Table [rdr].[Facility]    Script Date: 05/08/2012 09:17:17 ******/
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] DROP CONSTRAINT [FK_Facility_FacilityType]
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Facility]') AND type in (N'U'))
DROP TABLE [rdr].[Facility]
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 05/08/2012 09:17:13 ******/
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
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEvent]
GO
/****** Object:  UserDefinedFunction [dbo].[udf_GetPerfValuesForMetric]    Script Date: 05/08/2012 09:17:10 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[udf_GetPerfValuesForMetric]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [dbo].[udf_GetPerfValuesForMetric]
GO
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 05/08/2012 09:17:09 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Guest_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [rdr].[usp_Guest_Create]
GO
/****** Object:  StoredProcedure [dbo].[usp_SetDailyReport]    Script Date: 05/08/2012 09:17:09 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_SetDailyReport]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_SetDailyReport]
GO
/****** Object:  StoredProcedure [dbo].[usp_XbrcPerf_Insert_1]    Script Date: 05/08/2012 09:17:09 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_XbrcPerf_Insert_1]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_XbrcPerf_Insert_1]
GO
/****** Object:  StoredProcedure [gxp].[usp_CreateEntitlementStatus]    Script Date: 05/08/2012 09:17:09 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_CreateEntitlementStatus]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_CreateEntitlementStatus]
GO
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 05/08/2012 09:17:09 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[ReasonCode]') AND type in (N'U'))
DROP TABLE [gxp].[ReasonCode]
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 05/08/2012 09:17:06 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[MetricType]') AND type in (N'U'))
DROP TABLE [rdr].[MetricType]
GO
/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 05/08/2012 09:17:03 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[OffersetWindow]') AND type in (N'U'))
DROP TABLE [dbo].[OffersetWindow]
GO
/****** Object:  Table [dbo].[LoadxBRCTimetable]    Script Date: 05/08/2012 09:16:57 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoadxBRCTimetable]') AND type in (N'U'))
DROP TABLE [dbo].[LoadxBRCTimetable]
GO
/****** Object:  UserDefinedFunction [dbo].[ExplodeDates]    Script Date: 05/08/2012 09:16:49 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ExplodeDates]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
DROP FUNCTION [dbo].[ExplodeDates]
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 05/08/2012 09:16:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Attraction]') AND type in (N'U'))
DROP TABLE [rdr].[Attraction]
GO
/****** Object:  Table [dbo].[AttractionCapacity]    Script Date: 05/08/2012 09:16:35 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AttractionCapacity]') AND type in (N'U'))
DROP TABLE [dbo].[AttractionCapacity]
GO
/****** Object:  Table [dbo].[XbrcConfiguration]    Script Date: 05/08/2012 09:16:26 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcConfiguration]') AND type in (N'U'))
DROP TABLE [dbo].[XbrcConfiguration]
GO
/****** Object:  Table [dbo].[xBRCEventsForSTI]    Script Date: 05/08/2012 09:16:22 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xBRCEventsForSTI]') AND type in (N'U'))
DROP TABLE [dbo].[xBRCEventsForSTI]
GO
/****** Object:  Table [dbo].[XbrcPerf]    Script Date: 05/08/2012 09:16:18 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcPerf]') AND type in (N'U'))
DROP TABLE [dbo].[XbrcPerf]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 05/08/2012 09:16:15 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_RethrowError]') AND type in (N'P', N'PC'))
DROP PROCEDURE [dbo].[usp_RethrowError]
GO
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 05/08/2012 09:14:56 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventSubType]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEventSubType]
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 05/08/2012 09:14:52 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventType]') AND type in (N'U'))
DROP TABLE [gxp].[BusinessEventType]
GO
/****** Object:  Table [dbo].[config]    Script Date: 05/08/2012 09:14:45 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[config]') AND type in (N'U'))
DROP TABLE [dbo].[config]
GO
/****** Object:  Table [dbo].[DailyPilotReport]    Script Date: 05/08/2012 09:14:42 ******/
IF  EXISTS (SELECT * FROM dbo.sysobjects WHERE id = OBJECT_ID(N'[DF__DailyPilo__creat__5EBF139D]') AND type = 'D')
BEGIN
ALTER TABLE [dbo].[DailyPilotReport] DROP CONSTRAINT [DF__DailyPilo__creat__5EBF139D]
END
GO
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DailyPilotReport]') AND type in (N'U'))
DROP TABLE [dbo].[DailyPilotReport]
GO
/****** Object:  Table [dbo].[DAYS_OF_YEAR]    Script Date: 05/08/2012 09:14:38 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DAYS_OF_YEAR]') AND type in (N'U'))
DROP TABLE [dbo].[DAYS_OF_YEAR]
GO
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 05/08/2012 09:14:34 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EntitlementStatus]') AND type in (N'U'))
DROP TABLE [gxp].[EntitlementStatus]
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 05/08/2012 09:14:31 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[FacilityType]') AND type in (N'U'))
DROP TABLE [rdr].[FacilityType]
GO
/****** Object:  Table [rdr].[Guest]    Script Date: 05/08/2012 09:14:28 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Guest]') AND type in (N'U'))
DROP TABLE [rdr].[Guest]
GO
/****** Object:  Table [dbo].[HealthItem]    Script Date: 05/08/2012 09:14:23 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HealthItem]') AND type in (N'U'))
DROP TABLE [dbo].[HealthItem]
GO
/****** Object:  Table [gxp].[EventLocation]    Script Date: 05/08/2012 09:14:20 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EventLocation]') AND type in (N'U'))
DROP TABLE [gxp].[EventLocation]
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 05/08/2012 09:14:15 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[EventType]') AND type in (N'U'))
DROP TABLE [rdr].[EventType]
GO
/****** Object:  Schema [gxp]    Script Date: 05/08/2012 09:14:05 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'gxp')
DROP SCHEMA [gxp]
GO
/****** Object:  Schema [rdr]    Script Date: 05/08/2012 09:14:05 ******/
IF  EXISTS (SELECT * FROM sys.schemas WHERE name = N'rdr')
DROP SCHEMA [rdr]
GO
/****** Object:  User [EMUser]    Script Date: 05/08/2012 09:14:05 ******/
IF  EXISTS (SELECT * FROM sys.database_principals WHERE name = N'EMUser')
DROP USER [EMUser]
GO
/****** Object:  User [EMUser]    Script Date: 05/08/2012 09:14:05 ******/
IF NOT EXISTS (SELECT * FROM sys.database_principals WHERE name = N'EMUser')
CREATE USER [EMUser] FOR LOGIN [EMUser] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  Schema [rdr]    Script Date: 05/08/2012 09:14:05 ******/
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = N'rdr')
EXEC sys.sp_executesql N'CREATE SCHEMA [rdr] AUTHORIZATION [dbo]'
GO
/****** Object:  Schema [gxp]    Script Date: 05/08/2012 09:14:05 ******/
IF NOT EXISTS (SELECT * FROM sys.schemas WHERE name = N'gxp')
EXEC sys.sp_executesql N'CREATE SCHEMA [gxp] AUTHORIZATION [dbo]'
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 05/08/2012 09:14:15 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[EventType]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[EventType](
	[EventTypeID] [int] IDENTITY(1,1) NOT NULL,
	[EventTypeName] [nvarchar](100) NOT NULL,
 CONSTRAINT [PK_EventType] PRIMARY KEY CLUSTERED 
(
	[EventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_EventType] UNIQUE NONCLUSTERED 
(
	[EventTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[EventLocation]    Script Date: 05/08/2012 09:14:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EventLocation]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[EventLocation](
	[EventLocationID] [int] IDENTITY(1,1) NOT NULL,
	[EventLocation] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_EventLocation] PRIMARY KEY CLUSTERED 
(
	[EventLocationID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_EventLocation] UNIQUE NONCLUSTERED 
(
	[EventLocation] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[HealthItem]    Script Date: 05/08/2012 09:14:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[HealthItem]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[HealthItem](
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
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[Guest]    Script Date: 05/08/2012 09:14:28 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Guest]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Guest](
	[GuestID] [bigint] NOT NULL,
	[FirstName] [nvarchar](200) NOT NULL,
	[LastName] [nvarchar](200) NOT NULL,
	[EmailAddress] [nvarchar](200) NULL,
 CONSTRAINT [PK_Guest] PRIMARY KEY CLUSTERED 
(
	[GuestID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 05/08/2012 09:14:31 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[FacilityType]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[FacilityType](
	[FacilityTypeID] [int] IDENTITY(1,1) NOT NULL,
	[FacilityTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_FacilityType] PRIMARY KEY CLUSTERED 
(
	[FacilityTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_FacilityType] UNIQUE NONCLUSTERED 
(
	[FacilityTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[EntitlementStatus]    Script Date: 05/08/2012 09:14:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[EntitlementStatus]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[EntitlementStatus](
	[EntitlementStatusID] [int] IDENTITY(1,1) NOT NULL,
	[AppointmentID] [bigint] NOT NULL,
	[CacheXpassAppointmentID] [bigint] NOT NULL,
	[xBandID] [nvarchar](50) NULL,
	[GuestID] [bigint] NOT NULL,
	[EntertainmentID] [bigint] NOT NULL,
	[AppointmentReason] [nvarchar](50) NOT NULL,
	[AppointmentStatus] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
 CONSTRAINT [PK_EntitlementStatus] PRIMARY KEY CLUSTERED 
(
	[EntitlementStatusID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[DAYS_OF_YEAR]    Script Date: 05/08/2012 09:14:38 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DAYS_OF_YEAR]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[DAYS_OF_YEAR](
	[dt] [datetime] NULL
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[DailyPilotReport]    Script Date: 05/08/2012 09:14:42 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[DailyPilotReport]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[DailyPilotReport](
	[reportId] [int] IDENTITY(1,1) NOT NULL,
	[GuestCount] [int] NULL,
	[GuestCountTarget] [int] NULL,
	[Recruited] [int] NULL,
	[SelectedEntitlements] [int] NULL,
	[ReportDate] [datetime] NULL,
	[createdAt] [datetime] NULL DEFAULT (getdate()),
 CONSTRAINT [PK_DailyPilotReport] PRIMARY KEY CLUSTERED 
(
	[reportId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[config]    Script Date: 05/08/2012 09:14:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[config]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[config](
	[class] [varchar](64) NOT NULL,
	[property] [varchar](32) NOT NULL,
	[value] [varchar](1024) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[class] ASC,
	[property] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [gxp].[BusinessEventType]    Script Date: 05/08/2012 09:14:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventType]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[BusinessEventType](
	[BusinessEventTypeID] [int] IDENTITY(1,1) NOT NULL,
	[BusinessEventType] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_BusinessEventType] PRIMARY KEY CLUSTERED 
(
	[BusinessEventTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_BusinessEventType] UNIQUE NONCLUSTERED 
(
	[BusinessEventType] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[BusinessEventSubType]    Script Date: 05/08/2012 09:14:56 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEventSubType]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[BusinessEventSubType](
	[BusinessEventSubTypeID] [int] IDENTITY(1,1) NOT NULL,
	[BusinessEventSubType] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_BusinessEventSubType] PRIMARY KEY CLUSTERED 
(
	[BusinessEventSubTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_BusinessEventSubType] UNIQUE NONCLUSTERED 
(
	[BusinessEventSubType] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 05/08/2012 09:16:15 ******/
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
/****** Object:  Table [dbo].[XbrcPerf]    Script Date: 05/08/2012 09:16:18 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcPerf]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[XbrcPerf](
	[name] [varchar](255) NULL,
	[time] [varchar](255) NULL,
	[metric] [varchar](255) NULL,
	[max] [float] NULL,
	[mean] [float] NULL,
	[min] [float] NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[xBRCEventsForSTI]    Script Date: 05/08/2012 09:16:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xBRCEventsForSTI]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[xBRCEventsForSTI](
	[EventID] [bigint] NOT NULL,
	[GuestID] [int] NOT NULL,
	[RideID] [int] NULL,
	[xPass] [bit] NULL,
	[AttractionID] [int] NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [varchar](128) NULL,
	[Timestamp] [datetime] NULL,
	[WaitTime] [int] NULL,
	[MergeTime] [int] NULL,
	[EventTypeName] [varchar](128) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[XbrcConfiguration]    Script Date: 05/08/2012 09:16:26 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[XbrcConfiguration]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[XbrcConfiguration](
	[id] [bigint] IDENTITY(1,1) NOT NULL,
	[name] [nvarchar](32) NOT NULL,
	[description] [nvarchar](1024) NOT NULL,
	[model] [nvarchar](256) NOT NULL,
	[xml] [text] NOT NULL,
	[venuecode] [nvarchar](64) NOT NULL,
	[venuename] [nvarchar](256) NOT NULL,
	[createTime] [datetime] NOT NULL
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
END
GO
/****** Object:  Table [dbo].[AttractionCapacity]    Script Date: 05/08/2012 09:16:35 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[AttractionCapacity]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[AttractionCapacity](
	[Attraction] [varchar](64) NULL,
	[GuestsPerHour] [int] NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[Attraction]    Script Date: 05/08/2012 09:16:45 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Attraction]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Attraction](
	[AttractionID] [int] IDENTITY(1,1) NOT NULL,
	[AttractionName] [nvarchar](200) NOT NULL,
	[AttractionStatus] [smallint] NULL,
	[SBQueueCap] [int] NULL,
	[XPQueueCap] [int] NULL,
	[DisplayName] [nvarchar](100) NULL,
	[AttractionCapacity] [int] NULL,
 CONSTRAINT [PK_Attraction] PRIMARY KEY CLUSTERED 
(
	[AttractionID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_Attraction] UNIQUE NONCLUSTERED 
(
	[AttractionName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  UserDefinedFunction [dbo].[ExplodeDates]    Script Date: 05/08/2012 09:16:49 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[ExplodeDates]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
BEGIN
execute dbo.sp_executesql @statement = N'
CREATE FUNCTION [dbo].[ExplodeDates](@startdate datetime, @enddate datetime)
returns table as
return (
with 
 N0 as (SELECT 1 as n UNION ALL SELECT 1)
,N1 as (SELECT 1 as n FROM N0 t1, N0 t2)
,N2 as (SELECT 1 as n FROM N1 t1, N1 t2)
,N3 as (SELECT 1 as n FROM N2 t1, N2 t2)
,N4 as (SELECT 1 as n FROM N3 t1, N3 t2)
,N5 as (SELECT 1 as n FROM N4 t1, N4 t2)
,N6 as (SELECT 1 as n FROM N5 t1, N5 t2)
,nums as (SELECT ROW_NUMBER() OVER (ORDER BY (SELECT 1)) as num FROM N6)
SELECT DATEADD(day,num-1,@startdate) as thedate
FROM nums
WHERE num <= DATEDIFF(day,@startdate,@enddate) + 1
);
' 
END
GO
/****** Object:  Table [dbo].[LoadxBRCTimetable]    Script Date: 05/08/2012 09:16:57 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[LoadxBRCTimetable]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[LoadxBRCTimetable](
	[id] [int] NOT NULL,
	[TimePart] [varchar](12) NULL
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 05/08/2012 09:17:03 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[OffersetWindow]') AND type in (N'U'))
BEGIN
CREATE TABLE [dbo].[OffersetWindow](
	[windowId] [int] IDENTITY(1,1) NOT NULL,
	[label] [varchar](30) NOT NULL,
	[hourStart] [int] NOT NULL,
	[hourEnd] [int] NOT NULL,
	[dateActive] [datetime] NULL,
 CONSTRAINT [PK_OffersetWindow] PRIMARY KEY CLUSTERED 
(
	[windowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [rdr].[MetricType]    Script Date: 05/08/2012 09:17:06 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[MetricType]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[MetricType](
	[MetricTypeID] [int] IDENTITY(1,1) NOT NULL,
	[MetricTypeName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_MetricType] PRIMARY KEY CLUSTERED 
(
	[MetricTypeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_MetricType] UNIQUE NONCLUSTERED 
(
	[MetricTypeName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[ReasonCode]    Script Date: 05/08/2012 09:17:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[ReasonCode]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[ReasonCode](
	[ReasonCodeID] [int] IDENTITY(1,1) NOT NULL,
	[ReasonCode] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_ReasonCode] PRIMARY KEY CLUSTERED 
(
	[ReasonCodeID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_ReasonCode] UNIQUE NONCLUSTERED 
(
	[ReasonCode] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  StoredProcedure [gxp].[usp_CreateEntitlementStatus]    Script Date: 05/08/2012 09:17:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_CreateEntitlementStatus]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'

-- =============================================
-- Author:		Ted Crane
-- Create date: 11/17/2011
-- Description:	Creates an entitlement status 
--              record from a GXP JMS Redepmtion
--              message.
-- =============================================
CREATE PROCEDURE [gxp].[usp_CreateEntitlementStatus] 
	@AppointmentID bigint,
	@CacheXpassAppointmentID bigint,
	@xBandID nvarchar(50) = NULL,
	@GuestID bigint,
	@EntertainmentID bigint,
	@AppointmentReason nvarchar(50),
	@AppointmentStatus nvarchar(50),
	@Timestamp datetime
AS
BEGIN

	SET NOCOUNT ON;

	INSERT INTO [gxp].[EntitlementStatus]
           ([AppointmentID]
           ,[CacheXpassAppointmentID]
           ,[xBandID]
           ,[GuestID]
           ,[EntertainmentID]
           ,[AppointmentReason]
           ,[AppointmentStatus]
           ,[Timestamp])
     VALUES
           (@AppointmentID
           ,@CacheXpassAppointmentID
           ,NULL
           ,@GuestID
           ,@EntertainmentID
           ,@AppointmentReason
           ,@AppointmentStatus
           ,@Timestamp)

END


' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_XbrcPerf_Insert_1]    Script Date: 05/08/2012 09:17:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_XbrcPerf_Insert_1]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Inserts Performance Metric
-- =============================================
CREATE PROCEDURE [dbo].[usp_XbrcPerf_Insert_1] 
	@Name varchar(255),
	@Time varchar(255),
	@Metric varchar(255),
	@Wax varchar(255),
	@Win varchar(255),
	@Wean varchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO [dbo].[XbrcPerf]
           ([name]
           ,[time]
           ,[metric]
           ,[max]
           ,[min]
           ,[mean])
     VALUES
           (@Name
           ,@Time
           ,@Metric
           ,@Wax
           ,@Win
           ,@Wean)


END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_SetDailyReport]    Script Date: 05/08/2012 09:17:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_SetDailyReport]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_SetDailyReport]
    @GuestCount int, 
    @GuestCountTarget int, 
    @Recruited int, 
    @SelectedEntitlements int, 
    @ReportDate varchar(23)
AS
declare @usetime datetime

select @usetime=convert(datetime, @ReportDate);

insert into [dbo].[DailyPilotReport] ( 
    GuestCount 
    ,GuestCountTarget
    ,Recruited 
    ,SelectedEntitlements 
    ,ReportDate
) 
VALUES ( 
    @GuestCount
    ,@GuestCountTarget 
    ,@Recruited
    ,@SelectedEntitlements 
    ,@usetime
);

select max(reportId) from [dbo].[DailyPilotReport];
' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Guest_Create]    Script Date: 05/08/2012 09:17:09 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Guest_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/09/2012
-- Description:	Creates a Guest.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Guest_Create] 
	@GuestID bigint,
	@FirstName nvarchar(200),
	@LastName nvarchar(200),
	@EmailAddress nvarchar(200) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
		
			BEGIN TRANSACTION
			
			INSERT INTO [rdr].[Guest]
			   ([GuestID]
			   ,[FirstName]
			   ,[LastName]
			   ,[EmailAddress])
	       VALUES
			   (@GuestID
			   ,@FirstName
			   ,@LastName
			   ,@EmailAddress)
			
			COMMIT TRANSACTION
		
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
/****** Object:  UserDefinedFunction [dbo].[udf_GetPerfValuesForMetric]    Script Date: 05/08/2012 09:17:10 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[udf_GetPerfValuesForMetric]') AND type in (N'FN', N'IF', N'TF', N'FS', N'FT'))
BEGIN
execute dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Gets the Perf values for a single metric.
-- =============================================
CREATE FUNCTION [dbo].[udf_GetPerfValuesForMetric] 
(
	@Name varchar(255),
	@Metric varchar(255)
)
RETURNS varchar(max)
AS
BEGIN

	DECLARE @Result varchar(max)

	declare @t table ([max] float, [min] float, [mean] float)

	INSERT INTO @t
	SELECT [max],[min],[mean]
	  FROM [EMPOC_XBRC].[dbo].[XbrcPerf]
	  WHERE [metric] = @Metric --''perfEvents''
	  AND [name] = @Name --''Buzz Lightyear''

	declare @maxvar varchar(max)
	declare @minvar varchar(max)
	declare @meanvar varchar(max)
	set @maxvar = ''''
	set @minvar = ''''
	set @meanvar = ''''

	update @t
		set @maxvar = @maxvar + CONVERT(varchar(max),[max]) + '','',
		 @minvar = @minvar + CONVERT(varchar(max),[min]) + '','',
		 @meanvar = @meanvar + CONVERT(varchar(max),[mean]) + '',''

	--select * from @t --no side effects on temp table

	--remove comma
	select @maxvar = SUBSTRING(@maxvar,1,LEN(@maxvar)-1)
	select @minvar = SUBSTRING(@minvar,1,LEN(@minvar)-1)
	select @meanvar = SUBSTRING(@meanvar,1,LEN(@meanvar)-1)

	SELECT Top 100 @Result = t.[metric] + '': { max: ['' + @maxvar + ''],'' +
	''mean: ['' + @meanvar + ''],'' +
	''min: ['' + @minvar + '']}''
	FROM dbo.XbrcPerf t
	where [name] = @Name
	and [metric] = @Metric
	order by [time] desc
	
	RETURN @Result

END
' 
END
GO
/****** Object:  Table [gxp].[BusinessEvent]    Script Date: 05/08/2012 09:17:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BusinessEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[BusinessEvent](
	[BusinessEventID] [int] IDENTITY(1,1) NOT NULL,
	[EventLocationID] [int] NOT NULL,
	[BusinessEventTypeID] [int] NOT NULL,
	[BusinessEventSubTypeID] [int] NOT NULL,
	[ReferenceID] [nvarchar](50) NULL,
	[GuestIdentifier] [nvarchar](50) NULL,
	[GuestID] [bigint] NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[CorrelationID] [uniqueidentifier] NOT NULL,
	[StartTime] [datetime] NULL,
	[EndTime] [datetime] NULL,
	[LocationID] [bigint] NULL,
	[EntertainmentID] [bigint] NULL,
 CONSTRAINT [PK_BusinessEvent] PRIMARY KEY CLUSTERED 
(
	[BusinessEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[Facility]    Script Date: 05/08/2012 09:17:17 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Facility]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Facility](
	[FacilityID] [int] IDENTITY(1,1) NOT NULL,
	[FacilityName] [nvarchar](200) NOT NULL,
	[FacilityTypeID] [int] NULL,
 CONSTRAINT [PK_Facility] PRIMARY KEY CLUSTERED 
(
	[FacilityID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[Event]    Script Date: 05/08/2012 09:17:20 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Event]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Event](
	[EventId] [bigint] IDENTITY(1,1) NOT NULL,
	[GuestID] [bigint] NOT NULL,
	[RideNumber] [int] NOT NULL,
	[xPass] [bit] NOT NULL,
	[FacilityID] [int] NOT NULL,
	[EventTypeID] [int] NOT NULL,
	[ReaderLocation] [nvarchar](50) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
 CONSTRAINT [PK_Event_1] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [gxp].[BlueLaneEvent]    Script Date: 05/08/2012 09:17:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [gxp].[BlueLaneEvent](
	[BlueLaneEventID] [int] NOT NULL,
	[xBandID] [nvarchar](50) NULL,
	[EntertainmentID] [nvarchar](50) NOT NULL,
	[ReasonCodeID] [int] NOT NULL,
	[TapTime] [datetime] NOT NULL,
	[FacilityID] [int] NOT NULL,
 CONSTRAINT [PK_RedemptionEvent] PRIMARY KEY CLUSTERED 
(
	[BlueLaneEventID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestEntitlements]    Script Date: 05/08/2012 09:17:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestEntitlements]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetGuestEntitlements]
@guestId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END
ELSE
select @endtime=convert(datetime, @strEndDate)

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


select b.entertainmentId, 
    [starttime_hour] = DATEPART(HH,dateadd(HH, -4, b.StartTime)), 
    [starttime_mins] = DATEPART(mi,b.StartTime), 
    [endtime_hour] = DATEPART(HH, dateadd(HH, -4, b.EndTime)), 
    [endtime_mins] = DATEPART(mi, b.EndTime), 
    es.AppointmentStatus 
from GXP.BusinessEvent(nolock) as b, 
    GXP.BusinessEventType(nolock) as bet,
    GXP.BusinessEventSubType(nolock) as bst,
    GXP.EntitlementStatus(nolock) as es 
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
    and bet.BusinessEventType = ''BOOK'' 
    and b.GuestID=@guestId
    and es.GuestId = b.GuestId
    and es.Timestamp = b.Timestamp
    and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedOffersets]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedOffersets]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'/****** Object:  StoredProcedure [dbo].[usp_GetSelectedOffersets]    Script Date: 04/22/2012 10:50:31 ******/
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS


DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)



set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
from [dbo].[OffersetWindow] as ow
left join
(
select os.offerset as offerset, isnull(count(distinct b.BusinessEventID), 0) as offersetcount
from GXP.BusinessEvent b (nolock) 
join GXP.BusinessEventType bet(nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = ''BOOK'' 
join (
select b.guestid as guestid, isnull(g1.offerset, ''window4'') as offerset
from  GXP.BusinessEvent(nolock) as b
left join
(
            select guestid, min(table1.label) as offerset 
                from
        (
        select b.GuestId as guestid, 
            min(datepart(HH, dateadd(HH, -4, b.StartTime))) as minh, 
            max(datepart(HH, dateadd(HH, -4, b.StartTime))) as maxh
            from GXP.BusinessEvent(nolock) as b
            where
               dateadd(HH, -4, b.StartTime)  between @starttime  and @EOD_datetime
            group by b.GuestId
        ) 
        as gtable,
                (SELECT o1.*
            FROM [dbo].[OffersetWindow] AS o1
              LEFT OUTER JOIN [dbo].[OffersetWindow] AS o2
                ON (o1.label = o2.label AND o1.dateActive < o2.dateActive)
            WHERE o2.label IS NULL
        ) 
        as table1
        where
                      (gtable.minh between table1.hourStart and table1.hourEnd)
           AND 
           (gtable.maxh between table1.hourStart and table1.hourEnd)
        group by guestid
) as g1 on b.guestid = g1.guestid
where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
    group by b.GuestId, g1.offerset
) as os on b.GuestId = os.guestid
    and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
group by os.offerset
) as x
on x.offerset = ow.label
group by ow.label, x.offersetcount
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetSelectedForDate]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetSelectedForDate]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetSelectedForDate]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedOffersets]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedOffersets]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime;


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
from [dbo].[OffersetWindow] as ow
left join
(select os.offerset as offerset, isnull(count(EntitlementStatusId),0) as offersetcount
from GXP.EntitlementStatus(nolock) as aps,
(
    select distinct(b.guestid), isnull(g1.offerset, ''window4'') as offerset
    from  GXP.BusinessEvent(nolock) as b
    left join
    (
        select guestid, min(table1.label) as offerset
                from
        (
        select b.GuestId as guestid, 
            min(datepart(HH, dateadd(HH, -4, b.StartTime))) as minh, 
            max(datepart(HH, dateadd(HH, -4, b.StartTime))) as maxh
            from GXP.BusinessEvent(nolock) as b
            where
               dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
            group by b.GuestId
        ) 
        as gtable,
                (SELECT o1.*
            FROM [dbo].[OffersetWindow] AS o1
              LEFT OUTER JOIN [dbo].[OffersetWindow] AS o2
                ON (o1.label = o2.label AND o1.dateActive < o2.dateActive)
            WHERE o2.label IS NULL
        ) 
        as table1
        where
                      (gtable.minh between table1.hourStart and table1.hourEnd)
           AND 
           (gtable.maxh between table1.hourStart and table1.hourEnd)
        group by guestid
    ) as g1 on b.guestid = g1.guestid
    where
       dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
    group by b.GuestId, g1.offerset
) as os
where 
appointmentReason = ''STD''
and os.guestId = aps.GuestId
and aps.AppointmentStatus = ''RED''
and aps.Timestamp between @starttime and @endtime
group by os.offerset
) as x
on x.offerset = ow.label
group by ow.label, x.offersetcount
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetRedeemedForCal]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetRedeemedForCal]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetRedeemedForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL
AS

DECLARE @Selected int, @Redeemed int,           
    @starttime datetime, @endtime datetime, @EOD_datetime datetime;

IF @strCutOffDate  is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strCutOffDate)
select @starttime=DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))


set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select [Date] = t.dt, Redemeed = ISNULL(Redeemed,0), Selected = ISNULL(Selected,0) 
from [dbo].[DAYS_OF_YEAR] t
LEFT JOIN 
(


select CAST(CONVERT(CHAR(10),Timestamp,102) AS DATETIME) as [TimestampRedeemed], count(EntitlementStatusID) as [Redeemed]
from GXP.EntitlementStatus(nolock)
where 
appointmentReason = ''STD''
and AppointmentStatus = ''RED''
and Timestamp between @starttime and @EOD_datetime
group by CAST(CONVERT(CHAR(10),Timestamp,102) AS DATETIME)   
) as t1 on t.dt = t1.[TimestampRedeemed]  
LEFT JOIN 
(
select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b, 
	GXP.BusinessEventType(nolock) as bet,
	GXP.BusinessEventSubType(nolock) as bst 
	where b.BusinessEventTypeId= bet.BusinessEventTypeId 
	and bet.BusinessEventType = ''BOOK'' 
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)
) as t2 on t.dt = t2.[TimestampBooked]
where t.dt between @starttime and @endtime
order by t.dt desc
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetPerfValuesForName]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetPerfValuesForName]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/14/2012
-- Description:	Gets the Perf values for a single metric.
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetPerfValuesForName] 
	@Name varchar(255)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @result varchar(max)

	SET @result = ''''


	SELECT @result = @result + [dbo].[udf_GetPerfValuesForMetric](@Name,t.[metric]) + '',''
	FROM (SELECT DISTINCT [metric] 
	 FROM [dbo].[xbrcPerf]
	 WHERE [name] = @Name) as t


	select ''{'' + SUBSTRING(@result,0,LEN(@result)) + ''}''	
	
	
END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReportTodate]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetDailyReportTodate]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROCEDURE [dbo].[usp_GetDailyReportTodate]
@strDate varchar(23)
AS
 
declare @usetime datetime, @recruited int, @EOD_datetime datetime
select @usetime=convert(datetime, @strDate);

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @usetime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select @recruited=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and CONVERT(VARCHAR(10), dateadd(HH, -4, b.Timestamp) ,111) <= CONVERT(VARCHAR(10),@usetime,111) 

select
    0
    ,sum(d1.GuestCount)
    ,sum(d1.GuestCountTarget)
    ,@recruited
    ,sum(d1.SelectedEntitlements )
    ,null
    ,null
from [dbo].[DailyPilotReport] d1,
(
    -- 
    -- get the max create time for EACH day along the way
    -- 
    select distinct(ReportDate), maxcreate=max(createdAt)
        from [dbo].[DailyPilotReport] d2
        where
            CONVERT(VARCHAR(10),@usetime,111) >= CONVERT(VARCHAR(10),ReportDate,111)
    group by ReportDate
) as r2
where
    CONVERT(VARCHAR(10),@usetime,111) >= CONVERT(VARCHAR(10),d1.ReportDate,111)
    and r2.maxcreate = d1.createdAt
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetDailyReport]    Script Date: 05/08/2012 09:17:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetDailyReport]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
CREATE PROCEDURE [dbo].[usp_GetDailyReport]
@strDate varchar(23)
AS
 
declare @usetime datetime, @recruited int, @EOD_datetime datetime
select @usetime=convert(datetime, @strDate);

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @usetime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


-- want all recruited counts for the day
select @recruited=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and CONVERT(VARCHAR(10), dateadd(HH, -4, b.Timestamp) ,111) = CONVERT(VARCHAR(10),@usetime,111) 


select
    d1.reportId
    ,d1.GuestCount
    ,d1.GuestCountTarget
    ,@recruited
    ,d1.SelectedEntitlements 
    ,CONVERT(VARCHAR(10),d1.ReportDate,111)     
    ,d1.createdAt
from [dbo].[DailyPilotReport] d1,
(select max(createdAt) as maxcreate
    from [dbo].[DailyPilotReport] d2
    where
        CONVERT(VARCHAR(10),@usetime,111) = CONVERT(VARCHAR(10),ReportDate,111)
) as r2

where
    CONVERT(VARCHAR(10),@usetime,111) = CONVERT(VARCHAR(10),d1.ReportDate,111)
    and r2.maxcreate = d1.createdAt
' 
END
GO
/****** Object:  StoredProcedure [gxp].[usp_BusinessEvent_Create]    Script Date: 05/08/2012 09:17:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BusinessEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'



-- =============================================
-- Author:		Ted Crane
-- Create date: 03/04/2012
-- Description:	Creates a Business Event
-- =============================================
CREATE PROCEDURE [gxp].[usp_BusinessEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@GuestIdentifier nvarchar(50) = NULL,
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@StartTime nvarchar(50) = NULL,
	@EndTime nvarchar(50) = NULL,
	@EntertainmentID bigint = NULL,
	@LocationID bigint = NULL,
	@BusinessEventId int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0
	
		--If there''s no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DECLARE @EventLocationID int
		DECLARE @BusinessEventTypeID int
		DECLARE @BusinessEventSubTypeID int
		
		SELECT	@EventLocationID = [EventLocationID] 
		FROM	[gxp].[EventLocation]
		WHERE	[EventLocation] = @Location
		
		IF @EventLocationID IS NULL
		BEGIN

			INSERT INTO [gxp].[EventLocation]
				   ([EventLocation])
			VALUES 
					(@Location)
					
			SET @EventLocationID = @@IDENTITY
					
		END
		
		SELECT	@BusinessEventTypeID = [BusinessEventTypeID] 
		FROM	[gxp].[BusinessEventType]
		WHERE	[BusinessEventType] = @BusinessEventType

		IF @BusinessEventTypeID IS NULL
		BEGIN
		
			INSERT INTO [gxp].[BusinessEventType]
				   ([BusinessEventType])
			VALUES (@BusinessEventType)
			
			SET @BusinessEventTypeID = @@IDENTITY
	    
		END

		SELECT	@BusinessEventSubTypeID = [BusinessEventSubTypeID] 
		FROM	[gxp].[BusinessEventSubType]
		WHERE	[BusinessEventSubType] = @BusinessEventSubType

		IF @BusinessEventSubTypeID IS NULL
		BEGIN
		
			INSERT INTO [gxp].[BusinessEventSubType]
				   ([BusinessEventSubType])
			VALUES (@BusinessEventSubType)
			
			SET @BusinessEventSubTypeID = @@IDENTITY
	    
		END

		IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
			--Set to unknown guest
			SET @GuestID = 0
		END 

		INSERT INTO [gxp].[BusinessEvent]
			   ([EventLocationID]
			   ,[BusinessEventTypeID]
			   ,[BusinessEventSubTypeID]
			   ,[ReferenceID]
			   ,[GuestID]
			   ,[GuestIdentifier]
			   ,[Timestamp]
			   ,[CorrelationID]
			   ,[StartTime]
			   ,[EndTime]
			   ,[LocationID]
			   ,[EntertainmentID])
		VALUES
				(@EventLocationID
				,@BusinessEventTypeID
				,@BusinessEventSubTypeID
				,@ReferenceID
				,@GuestID
				,NULL
				,CONVERT(datetime,@Timestamp,127)
				,@CorrelationID
				,CONVERT(datetime,@StartTime,127)
				,CONVERT(datetime,@EndTime,127)
				,@LocationID
				,@EntertainmentID)
	    
		SELECT @BusinessEventId = @@IDENTITY

		IF @InternalTransaction = 1
		BEGIN
			COMMIT TRANSACTION
		END

	END TRY
	BEGIN CATCH
	   
		IF @InternalTransaction = 1
		BEGIN
			ROLLBACK TRANSACTION
		END
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END




' 
END
GO
/****** Object:  Table [rdr].[Metric]    Script Date: 05/08/2012 09:17:36 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[Metric]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[Metric](
	[MetricID] [bigint] IDENTITY(1,1) NOT NULL,
	[FacilityID] [int] NOT NULL,
	[StartTime] [datetime] NOT NULL,
	[EndTime] [datetime] NOT NULL,
	[MetricTypeID] [int] NOT NULL,
	[Guests] [int] NOT NULL,
	[Abandonments] [int] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
 CONSTRAINT [PK_Metric] PRIMARY KEY CLUSTERED 
(
	[MetricID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[LoadEvent]    Script Date: 05/08/2012 09:17:39 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[LoadEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[LoadEvent](
	[EventId] [bigint] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[CarID] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_LoadEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[ExitEvent]    Script Date: 05/08/2012 09:17:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ExitEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[ExitEvent](
	[EventId] [bigint] NOT NULL,
	[WaitTime] [int] NOT NULL,
	[MergeTime] [int] NOT NULL,
	[TotalTime] [int] NOT NULL,
	[CarID] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK_ExitEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[AbandonEvent]    Script Date: 05/08/2012 09:17:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[AbandonEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[AbandonEvent](
	[EventId] [bigint] NOT NULL,
	[LastTransmit] [datetime] NOT NULL,
 CONSTRAINT [PK_AbandonEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  Table [rdr].[ReaderEvent]    Script Date: 05/08/2012 09:17:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[ReaderEvent]') AND type in (N'U'))
BEGIN
CREATE TABLE [rdr].[ReaderEvent](
	[EventId] [bigint] NOT NULL,
	[ReaderLocationID] [nvarchar](200) NOT NULL,
	[ReaderName] [nvarchar](200) NOT NULL,
	[ReaderID] [nvarchar](200) NOT NULL,
	[RFID] [nvarchar](200) NOT NULL,
	[IsWearingPrimaryBand] [bit] NOT NULL,
 CONSTRAINT [PK_ReaderEvent] PRIMARY KEY CLUSTERED 
(
	[EventId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
END
GO
/****** Object:  StoredProcedure [gxp].[usp_BlueLaneEvent_Create]    Script Date: 05/08/2012 09:17:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_BlueLaneEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'
-- =============================================
-- Author:		Ted Crane
-- Create date: 03/03/2012
-- Description:	Creates a Blue Event
-- =============================================
CREATE PROCEDURE [gxp].[usp_BlueLaneEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@GuestIdentifier nvarchar(50) = NULL,
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@xBandID nvarchar(50),
	@EntertainmentID nvarchar(50),
	@ReasonCode nvarchar(50),
	@TapTime datetime,
	@FacilityName nvarchar(200)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @BusinessEventId int
		DECLARE @ReasonCodeID int
		
		SELECT	@FacilityID = [FacilityID] 
		FROM	[rdr].[Facility] 
		WHERE	[FacilityName] = @FacilityName
				
		IF @FacilityID IS NULL
		BEGIN
			INSERT INTO [rdr].[Facility]
				   ([FacilityName]
				   ,[FacilityTypeID])
			VALUES 
					(@FacilityName
					,NULL)

			SET @FacilityID = @@IDENTITY
		END

		SELECT	@ReasonCodeID = [ReasonCodeID] 
		FROM	[gxp].[ReasonCode] 
		WHERE	[ReasonCode] = @ReasonCode
				
		IF @ReasonCodeID IS NULL
		BEGIN
			INSERT INTO [gxp].[ReasonCode]
				   ([ReasonCode])
			VALUES 
					(@ReasonCode)

			SET @ReasonCodeID = @@IDENTITY
		END

		EXECUTE [gxp].[usp_BusinessEvent_Create] 
		   @Location = @Location
		  ,@BusinessEventType = @BusinessEventType
		  ,@BusinessEventSubType = @BusinessEventSubType
		  ,@ReferenceID = NULL
		  ,@GuestID = @GuestID
		  ,@GuestIdentifier = NULL
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationID
		  ,@BusinessEventId = @BusinessEventId OUTPUT

		INSERT INTO [gxp].[BlueLaneEvent]
				   ([BlueLaneEventID]
				   ,[xBandID]
				   ,[EntertainmentID]
				   ,[ReasonCodeID]
				   ,[TapTime]
				   ,[FacilityID])
			 VALUES
				   (@BusinessEventID
				   ,NULL
				   ,@EntertainmentID
				   ,@ReasonCodeID
				   ,@TapTime
				   ,@FacilityID)

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
/****** Object:  StoredProcedure [rdr].[usp_Event_Create]    Script Date: 05/08/2012 09:17:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Event_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/20/2011
-- Description:	Creates an Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--              Changed RideID to RideNumber.
--              Changed Attraction to Facility.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Event_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@EventId int OUTPUT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @InternalTransaction bit
		
		SET @InternalTransaction = 0
	
		--If there''s no transaction create one.
		IF @@TRANCOUNT = 0
		BEGIN
			BEGIN TRANSACTION
			SET @InternalTransaction = 1
		END
		
		DECLARE @FacilityID int
		DECLARE @FacilityTypeID int
		DECLARE @EventTypeID int
		
		SELECT	@FacilityTypeID = [FacilityTypeID] 
		FROM	[rdr].[FacilityType]
		WHERE	[FacilityTypeName] = @FacilityTypeName
		
		IF @FacilityTypeID IS NULL
		BEGIN

			INSERT INTO [rdr].[FacilityType]
				   ([FacilityTypeName])
			VALUES 
					(@FacilityTypeName)
					
			SET @FacilityTypeID = @@IDENTITY
					
		END
		
		SELECT	@FacilityID = [FacilityID] 
		FROM	[rdr].[Facility] 
		WHERE	[FacilityName] = @FacilityName
		AND		[FacilityTypeID] = @FacilityTypeID
				
		IF @FacilityID IS NULL
		BEGIN
			INSERT INTO [rdr].[Facility]
				   ([FacilityName]
				   ,[FacilityTypeID])
			VALUES 
					(@FacilityName
					,@FacilityTypeID)

			SET @FacilityID = @@IDENTITY
			
		END
		
		SELECT	@EventTypeID = [EventTypeID] 
		FROM	[rdr].[EventType]
		WHERE	[EventTypeName] = @EventTypeName

		IF @EventTypeID IS NULL
		BEGIN
		
			INSERT INTO [rdr].[EventType]
				   ([EventTypeName])
			VALUES (@EventTypeName)
			
			SET @EventTypeID = @@IDENTITY
	    
		END


		IF PATINDEX(''%.%'',@Timestamp) = 0
		BEGIN
		
			SET @Timestamp = SUBSTRING(@Timestamp,1,19) + ''.'' + SUBSTRING(@Timestamp,21,3)
		
		END

		DECLARE @RideNumber int

		SELECT @RideNumber = ISNULL(MAX([RideNumber]),0)
		FROM [rdr].[Event] 
		WHERE [GuestID] = @GuestID

		IF @EventTypeName = ''Entry''
		BEGIN
			SET @RideNumber = @RideNumber + 1 
		END

		IF NOT EXISTS(SELECT ''X'' FROM [rdr].[Guest] where [GuestID] = @GuestID)
		BEGIN
			--Set to unknown guest
			SET @GuestID = 0
		END 

		INSERT INTO [rdr].[Event]
			   ([GuestID]
			   ,[xPass]
			   ,[FacilityID]
			   ,[RideNumber]
			   ,[EventTypeID]
			   ,[ReaderLocation]
			   ,[Timestamp])
		SELECT	@GuestID
				,@xPass
				,@FacilityID
				,@RideNumber
				,@EventTypeID
				,@ReaderLocation
				,CONVERT(datetime,@Timestamp,126)
	    
		SELECT @EventId = @@IDENTITY

		IF @InternalTransaction = 1
		BEGIN
			COMMIT TRANSACTION
		END

	END TRY
	BEGIN CATCH
	   
		IF @InternalTransaction = 1
		BEGIN
			ROLLBACK TRANSACTION
		END
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneReasonCodes]    Script Date: 05/08/2012 09:17:50 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneReasonCodes]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

select distinct(ReasonCodeID), count(bluelaneeventid)
from gxp.bluelaneevent ble
where 
    ble.taptime between @starttime and @endtime
group by ble.ReasonCodeID
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetBlueLaneForAttraction]    Script Date: 05/08/2012 09:17:51 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetBlueLaneForAttraction]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttraction]
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @bluelanecount int, @overridecount int,
	@starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);


select @bluelanecount=count(bluelaneeventid)
from gxp.bluelaneevent ble
where 
    ble.entertainmentId = @facilityID
and 
    ble.taptime between @starttime and @endtime
    

select @overridecount=count(entitlementStatusId)
from gxp.EntitlementStatus
where  appointmentReason in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
and entertainmentId = @facilityID
and timestamp between @starttime and @endtime


select (@bluelanecount + @overridecount), @overridecount
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_getGuestWaitTimeFP]    Script Date: 05/08/2012 09:17:51 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_getGuestWaitTimeFP]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_getGuestWaitTimeFP]   
	@GuestID int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

select @starttime=convert(datetime, @strStartDate)
select @endtime=convert(datetime, @strEndDate)


select t1.Timestamp, t1.FacilityName, WaitTime = DATEDIFF(MI, t1.Timestamp, t2.Timestamp)
from(
select e.guestID, e.rideNumber, f.FacilityName, [day] = DATEPART(dd,e.Timestamp), [hour] = DATEPART (HH,e.Timestamp), e.Timestamp
	from rdr.Event e,
        rdr.Facility f,
        rdr.EventType et
	where 		e.EventTypeID = et.EventTypeID
    and et.EventTypeName = ''ENTRY''
    and f.FacilityId = e.FacilityId
	and GuestID = @GuestID
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t1
join (
select e.guestID, e.rideNumber, f.FacilityName, [day] = DATEPART(dd,e.Timestamp), [hour] = DATEPART (HH,e.Timestamp), Timestamp
	from rdr.Event e,
        rdr.Facility f,
        rdr.EventType et
	where 
		e.EventTypeID = et.EventTypeID
    and et.EventTypeName = ''MERGE''
    and f.FacilityId = e.FacilityId
	and GuestID = @GuestID
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t2 on t1.rideNumber = t2.rideNumber		
	and t1.guestID = t2.guestID
	and t1.FacilityName = t2.FacilityName
order by 1,2,3
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestsForAttraction]    Script Date: 05/08/2012 09:17:51 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestsForAttraction]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetGuestsForAttraction]
@facilityId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime = getdate()
END
ELSE
select @endtime=convert(datetime, @strEndDate)


SELECT DISTINCT(e.GuestID), g.EmailAddress, MAX(e.Timestamp) 
FROM [rdr].[Event] e,
    [rdr].[Guest] g,
    [rdr].[Facility] f
    WHERE g.GuestID = e.GuestID
    AND f.FacilityName=@facilityId
    and f.FacilityId = e.FacilityId
    AND e.Timestamp between dateadd(hour, 4, @starttime) AND dateadd(hour, 4, @endtime)
    GROUP BY e.GuestID, g.EmailAddress, e.Timestamp
    ORDER BY MAX(e.Timestamp), e.GuestID
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuestReads]    Script Date: 05/08/2012 09:17:51 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuestReads]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetGuestReads]
@guestId int,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END
ELSE
select @endtime=convert(datetime, @strEndDate)

SELECT e.Timestamp, f.facilityName, "XPASS", ReaderLocation
FROM rdr.Event e,
    rdr.Facility f
WHERE e.GuestID = @guestId
    and f.FacilityId = e.FacilityId
and e.Timestamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetGuest]    Script Date: 05/08/2012 09:17:51 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetGuest]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetGuest]
@guestId int,
@strStartDate varchar(30) = NULL,
@strEndDate varchar(30) = NULL
AS

DECLARE @starttime datetime, @endtime datetime;

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END
ELSE
select @endtime=convert(datetime, @strEndDate)

SELECT DISTINCT(g.GuestID), g.EmailAddress, ''not available''
FROM [rdr].[Guest] g, 
	[rdr].[Event] e
    WHERE 
    e.GuestID = g.GuestID
       AND G.GuestID = @guestId

    and e.Timestamp between dateadd(hour, 4, @starttime) AND dateadd(hour, 4, @endtime)
    GROUP BY g.GuestID, g.EmailAddress
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetExecSummary]    Script Date: 05/08/2012 09:17:51 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetExecSummary]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetExecSummary]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, @PilotParticipants int, 
    @EOD_datetime varchar(30), @starttime datetime, @endtime datetime;


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)


set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime

select @Redeemed=count(*)
from GXP.EntitlementStatus(nolock)
where 
appointmentReason = ''STD''
and AppointmentStatus = ''RED''
and Timestamp between @starttime and @endtime;

select @PilotParticipants=count(distinct(b.GuestId))
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event (nolock)
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t1
left join (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event (nolock)
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL;


select Selected = @Selected,	Redeemed = @Redeemed, 
	PilotParticipants = @PilotParticipants, 
	InQueue = @InQueue
' 
END
GO
/****** Object:  StoredProcedure [dbo].[usp_GetEntitlementSummary]    Script Date: 05/08/2012 09:17:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[usp_GetEntitlementSummary]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'CREATE PROCEDURE [dbo].[usp_GetEntitlementSummary]
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, @InQueue int, @Bluelane int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int


IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEndDate is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEndDate)

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b, 
GXP.BusinessEventType(nolock) as bet
where b.BusinessEventTypeId= bet.BusinessEventTypeId 
and bet.BusinessEventType = ''BOOK'' 
and b.entertainmentId = @facilityId
and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime


select @bluelanecount=count(bluelaneeventid)
from gxp.bluelaneevent ble
where 
    ble.entertainmentId = @facilityID
and 
    ble.taptime between @starttime and @endtime
    
select @overridecount=count(entitlementStatusId)
from gxp.EntitlementStatus
where  appointmentReason in (''SWP'', ''ACS'', ''OTH'', ''OVR'') 
and entertainmentId = @facilityID
and timestamp between @starttime and @endtime

select @Bluelane=(@bluelanecount + @overridecount)

select @Redeemed=count(*)
from GXP.EntitlementStatus(nolock)
where 
appointmentReason = ''STD''
and AppointmentStatus = ''RED''
and entertainmentID=@facilityId
and Timestamp between @starttime and @endtime;


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event (nolock)
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = ''Entry'')
        and FacilityID=@facilityId
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t1
left join (
select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event (nolock)
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in (''Merge'',''Abandon''))
        and FacilityID=@facilityId
		and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL;



select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed, 
	Bluelane = @Bluelane, 
	InQueue = @InQueue
' 
END
GO
/****** Object:  StoredProcedure [rdr].[usp_Metric_Create]    Script Date: 05/08/2012 09:17:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_Metric_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Metrics record.
-- =============================================
CREATE PROCEDURE [rdr].[usp_Metric_Create] 
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@StartTime nvarchar(25),
	@EndTime nvarchar(25),
	@MetricTypeName nvarchar(20),
	@Guests int,
	@Abandonments int,
	@WaitTime int,
	@MergeTime int,
	@TotalTime int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @FacilityID int
		DECLARE @FacilityTypeID int
		DECLARE @MetricTypeID int
		
		SELECT	@FacilityTypeID = [FacilityTypeID] 
		FROM	[rdr].[FacilityType]
		WHERE	[FacilityTypeName] = @FacilityTypeName
		
		IF @FacilityTypeID IS NULL
		BEGIN

			INSERT INTO [rdr].[FacilityType]
				   ([FacilityTypeName])
			VALUES 
					(@FacilityTypeName)
					
			SET @FacilityTypeID = @@IDENTITY
					
		END
		
		SELECT	@FacilityID = [FacilityID] 
		FROM	[rdr].[Facility] 
		WHERE	[FacilityName] = @FacilityName
		AND		[FacilityTypeID] = @FacilityTypeID
				
		IF @FacilityID IS NULL
		BEGIN
			INSERT INTO [rdr].[Facility]
				   ([FacilityName]
				   ,[FacilityTypeID])
			VALUES 
					(@FacilityName
					,@FacilityTypeID)

			SET @FacilityID = @@IDENTITY
			
		END
		
		SELECT	@MetricTypeID = [MetricTypeID] 
		FROM	[rdr].[MetricType]
		WHERE	[MetricTypeName] = @MetricTypeName

		IF @MetricTypeID IS NULL
		BEGIN
		
			INSERT INTO [rdr].[MetricType]
				   ([MetricTypeName])
			VALUES (@MetricTypeName)
			
			SET @MetricTypeID = @@IDENTITY
	    
		END

		IF PATINDEX(''%.%'',@StartTime) = 0
		BEGIN
			SET @StartTime = SUBSTRING(@StartTime,1,19) + ''.'' + SUBSTRING(@StartTime,21,3)
		END

		IF PATINDEX(''%.%'',@EndTime) = 0
		BEGIN
			SET @EndTime = SUBSTRING(@EndTime,1,19) + ''.'' + SUBSTRING(@EndTime,21,3)
		END

		INSERT INTO [rdr].[Metric]
			   ([FacilityID]
			   ,[StartTime]
			   ,[EndTime]
			   ,[MetricTypeID]
			   ,[Guests]
			   ,[Abandonments]
			   ,[WaitTime]
			   ,[MergeTime]
			   ,[TotalTime])
		VALUES	
				(@FacilityID
				,CONVERT(datetime,@StartTime,126)
				,CONVERT(datetime,@EndTime,126)
				,@MetricTypeID
				,@Guests
				,@Abandonments
				,@WaitTime
				,@MergeTime
				,@TotalTime)
				
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
/****** Object:  StoredProcedure [rdr].[usp_ReaderEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ReaderEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 03/20/2012
-- Description:	Creates a Reader Event
-- =============================================
CREATE PROCEDURE [rdr].[usp_ReaderEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@ReaderLocationID nvarchar(200),
	@ReaderName nvarchar(200),
	@ReaderID nvarchar(200),
	@RFID nvarchar(200),
	@IsWearingPrimaryBand bit
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION

			DECLARE @EventId int
		
			EXECUTE [rdr].[usp_Event_Create] 
			@GuestID = @GuestID
			,@xPass = @xPass
			,@FacilityName = @FacilityName
			,@FacilityTypeName = @FacilityTypeName
			,@EventTypeName = @EventTypeName
			,@ReaderLocation = @ReaderLocation
			,@Timestamp = @Timestamp
			,@EventId = @EventId OUTPUT

			INSERT INTO [rdr].[ReaderEvent]
           ([EventId]
           ,[ReaderLocationID]
           ,[ReaderName]
           ,[ReaderID]
           ,[RFID]
           ,[IsWearingPrimaryBand])
			VALUES
           (@EventId
           ,@ReaderLocationID
           ,@ReaderName
           ,@ReaderID
           ,@RFID
           ,@IsWearingPrimaryBand)
	           
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
/****** Object:  StoredProcedure [rdr].[usp_LoadEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_LoadEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Load Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
-- =============================================
CREATE PROCEDURE [rdr].[usp_LoadEvent_Create] 
	 @GuestID bigint
	,@xPass bit
	,@FacilityName nvarchar(20)
	,@FacilityTypeName nvarchar(20)
	,@EventTypeName nvarchar(20)
	,@ReaderLocation nvarchar(20)
	,@Timestamp nvarchar(25)
	,@WaitTime int
	,@MergeTime int
	,@CarID nvarchar(20)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		DECLARE @EventId int

		BEGIN TRANSACTION
			
			EXECUTE [rdr].[usp_Event_Create] 
			   @GuestID = @GuestID
			  ,@xPass = @xPass
			  ,@FacilityName = @FacilityName
			  ,@FacilityTypeName = @FacilityTypeName
			  ,@EventTypeName = @EventTypeName
			  ,@ReaderLocation = @ReaderLocation
			  ,@Timestamp = @Timestamp
			  ,@EventId = @EventId OUTPUT

			INSERT INTO [rdr].[LoadEvent]
				   ([EventId]
				   ,[WaitTime]
				   ,[MergeTime]
				   ,[CarID])
			 VALUES
				   (@EventId
				   ,@WaitTime
				   ,@MergeTime
				   ,@CarID)
		           
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
/****** Object:  StoredProcedure [rdr].[usp_ExitEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_ExitEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Load Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
-- =============================================
CREATE PROCEDURE [rdr].[usp_ExitEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@WaitTime int,
	@MergeTime int,
	@TotalTime int,
	@CarID nvarchar(20)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION

			DECLARE @EventId int
		
			EXECUTE [rdr].[usp_Event_Create] 
			@GuestID = @GuestID
			,@xPass = @xPass
			,@FacilityName = @FacilityName
			,@FacilityTypeName = @FacilityTypeName
			,@EventTypeName = @EventTypeName
			,@ReaderLocation = @ReaderLocation
			,@Timestamp = @Timestamp
			,@EventId = @EventId OUTPUT

			INSERT INTO [rdr].[ExitEvent]
			([EventId]
			,[WaitTime]
			,[MergeTime]
			,[TotalTime]
			,[CarID])
			VALUES
			(@EventId
			,@WaitTime
			,@MergeTime
			,@TotalTime
			,@CarID)
	           
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
/****** Object:  StoredProcedure [rdr].[usp_AbandonEvent_Create]    Script Date: 05/08/2012 09:17:52 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[rdr].[usp_AbandonEvent_Create]') AND type in (N'P', N'PC'))
BEGIN
EXEC dbo.sp_executesql @statement = N'-- =============================================
-- Author:		Ted Crane
-- Create date: 07/21/2011
-- Description:	Creates a Abandon Event
-- Update date: 01/25/2012
-- Author:		Ted Crane
-- Description:	Changed @BandID to @GuestID.
--				Changed @VenueName to @FacilityName.
--				Changed @EventType to @EventTypeName.
-- =============================================
CREATE PROCEDURE [rdr].[usp_AbandonEvent_Create] 
	@GuestID bigint, 
	@xPass bit,
	@FacilityName nvarchar(20),
	@FacilityTypeName nvarchar(20),
	@EventTypeName nvarchar(20),
	@ReaderLocation nvarchar(20),
	@Timestamp nvarchar(25),
	@LastTransmit nvarchar(25)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	DECLARE @EventId bigint

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		EXECUTE [rdr].[usp_Event_Create] 
		   @GuestID = @GuestID
		  ,@xPass = @xPass
		  ,@FacilityName = @FacilityName
		  ,@FacilityTypeName = @FacilityTypeName
		  ,@EventTypeName = @EventTypeName
		  ,@ReaderLocation = @ReaderLocation
		  ,@Timestamp = @Timestamp
		  ,@EventId = @EventId OUTPUT

		IF PATINDEX(''%.%'',@LastTransmit) = 0
		BEGIN
		
			SET @LastTransmit = SUBSTRING(@LastTransmit,1,19) + ''.'' + SUBSTRING(@LastTransmit,21,3)
		
		END
		INSERT INTO [rdr].[AbandonEvent]
			   ([EventId]
			   ,[LastTransmit])
		 VALUES
			   (@EventId
			   ,convert(datetime,@LastTransmit,126))
	           
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
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventSubType]    Script Date: 05/08/2012 09:17:13 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventSubType] FOREIGN KEY([BusinessEventSubTypeID])
REFERENCES [gxp].[BusinessEventSubType] ([BusinessEventSubTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventSubType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventSubType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_BusinessEventType]    Script Date: 05/08/2012 09:17:13 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_BusinessEventType] FOREIGN KEY([BusinessEventTypeID])
REFERENCES [gxp].[BusinessEventType] ([BusinessEventTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_BusinessEventType]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_BusinessEventType]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_EventLocation]    Script Date: 05/08/2012 09:17:13 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_EventLocation] FOREIGN KEY([EventLocationID])
REFERENCES [gxp].[EventLocation] ([EventLocationID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_EventLocation]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_EventLocation]
GO
/****** Object:  ForeignKey [FK_BusinessEvent_Guest]    Script Date: 05/08/2012 09:17:13 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent]  WITH CHECK ADD  CONSTRAINT [FK_BusinessEvent_Guest] FOREIGN KEY([GuestID])
REFERENCES [rdr].[Guest] ([GuestID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BusinessEvent_Guest]') AND parent_object_id = OBJECT_ID(N'[gxp].[BusinessEvent]'))
ALTER TABLE [gxp].[BusinessEvent] CHECK CONSTRAINT [FK_BusinessEvent_Guest]
GO
/****** Object:  ForeignKey [FK_Facility_FacilityType]    Script Date: 05/08/2012 09:17:17 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility]  WITH CHECK ADD  CONSTRAINT [FK_Facility_FacilityType] FOREIGN KEY([FacilityTypeID])
REFERENCES [rdr].[FacilityType] ([FacilityTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Facility_FacilityType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Facility]'))
ALTER TABLE [rdr].[Facility] CHECK CONSTRAINT [FK_Facility_FacilityType]
GO
/****** Object:  ForeignKey [FK_Event_EventType]    Script Date: 05/08/2012 09:17:20 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_EventType] FOREIGN KEY([EventTypeID])
REFERENCES [rdr].[EventType] ([EventTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_EventType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_EventType]
GO
/****** Object:  ForeignKey [FK_Event_Facility]    Script Date: 05/08/2012 09:17:20 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Facility]
GO
/****** Object:  ForeignKey [FK_Event_Guest]    Script Date: 05/08/2012 09:17:20 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event]  WITH CHECK ADD  CONSTRAINT [FK_Event_Guest] FOREIGN KEY([GuestID])
REFERENCES [rdr].[Guest] ([GuestID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Event_Guest]') AND parent_object_id = OBJECT_ID(N'[rdr].[Event]'))
ALTER TABLE [rdr].[Event] CHECK CONSTRAINT [FK_Event_Guest]
GO
/****** Object:  ForeignKey [FK_BlueLaneEvent_BlueLaneEvent]    Script Date: 05/08/2012 09:17:23 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_BlueLaneEvent_BlueLaneEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_BlueLaneEvent_BlueLaneEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_BusinessEvent]    Script Date: 05/08/2012 09:17:23 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_BusinessEvent] FOREIGN KEY([BlueLaneEventID])
REFERENCES [gxp].[BusinessEvent] ([BusinessEventID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_BusinessEvent]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_BusinessEvent]
GO
/****** Object:  ForeignKey [FK_RedemptionEvent_ReasonCode]    Script Date: 05/08/2012 09:17:23 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent]  WITH CHECK ADD  CONSTRAINT [FK_RedemptionEvent_ReasonCode] FOREIGN KEY([ReasonCodeID])
REFERENCES [gxp].[ReasonCode] ([ReasonCodeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[gxp].[FK_RedemptionEvent_ReasonCode]') AND parent_object_id = OBJECT_ID(N'[gxp].[BlueLaneEvent]'))
ALTER TABLE [gxp].[BlueLaneEvent] CHECK CONSTRAINT [FK_RedemptionEvent_ReasonCode]
GO
/****** Object:  ForeignKey [FK_Metric_Facility]    Script Date: 05/08/2012 09:17:36 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_Facility] FOREIGN KEY([FacilityID])
REFERENCES [rdr].[Facility] ([FacilityID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_Facility]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_Facility]
GO
/****** Object:  ForeignKey [FK_Metric_MetricType]    Script Date: 05/08/2012 09:17:36 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric]  WITH CHECK ADD  CONSTRAINT [FK_Metric_MetricType] FOREIGN KEY([MetricTypeID])
REFERENCES [rdr].[MetricType] ([MetricTypeID])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_Metric_MetricType]') AND parent_object_id = OBJECT_ID(N'[rdr].[Metric]'))
ALTER TABLE [rdr].[Metric] CHECK CONSTRAINT [FK_Metric_MetricType]
GO
/****** Object:  ForeignKey [FK_LoadEvent_Event]    Script Date: 05/08/2012 09:17:39 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent]  WITH CHECK ADD  CONSTRAINT [FK_LoadEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_LoadEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[LoadEvent]'))
ALTER TABLE [rdr].[LoadEvent] CHECK CONSTRAINT [FK_LoadEvent_Event]
GO
/****** Object:  ForeignKey [FK_ExitEvent_Event]    Script Date: 05/08/2012 09:17:43 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent]  WITH CHECK ADD  CONSTRAINT [FK_ExitEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ExitEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ExitEvent]'))
ALTER TABLE [rdr].[ExitEvent] CHECK CONSTRAINT [FK_ExitEvent_Event]
GO
/****** Object:  ForeignKey [FK_AbandonEvent_Event]    Script Date: 05/08/2012 09:17:47 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent]  WITH CHECK ADD  CONSTRAINT [FK_AbandonEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_AbandonEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[AbandonEvent]'))
ALTER TABLE [rdr].[AbandonEvent] CHECK CONSTRAINT [FK_AbandonEvent_Event]
GO
/****** Object:  ForeignKey [FK_ReaderEvent_Event]    Script Date: 05/08/2012 09:17:50 ******/
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent]  WITH CHECK ADD  CONSTRAINT [FK_ReaderEvent_Event] FOREIGN KEY([EventId])
REFERENCES [rdr].[Event] ([EventId])
GO
IF  EXISTS (SELECT * FROM sys.foreign_keys WHERE object_id = OBJECT_ID(N'[rdr].[FK_ReaderEvent_Event]') AND parent_object_id = OBJECT_ID(N'[rdr].[ReaderEvent]'))
ALTER TABLE [rdr].[ReaderEvent] CHECK CONSTRAINT [FK_ReaderEvent_Event]
GO

INSERT [dbo].[DAYS_OF_YEAR] (dt) 
SELECT [thedate] FROM [dbo].[ExplodeDates] (
  '2012-01-01'
  ,'2012-12-31') 
  WHERE NOT EXISTS
  (SELECT 'X'
   FROM [dbo].[DAYS_OF_YEAR] d
   WHERE d.dt = [ExplodeDates].thedate)

use [master]

IF NOT EXISTS (SELECT loginname FROM master.dbo.syslogins WHERE name = N'EMUser')
	CREATE LOGIN EMUser WITH PASSWORD = 'Mayhem*23'
GO

USE [Synaps_XBRMS]
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
