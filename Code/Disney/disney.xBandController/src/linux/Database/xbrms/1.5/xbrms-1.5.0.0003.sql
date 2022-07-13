--:setvar databasename XBRMS_FPT2
:setvar previousversion '1.5.0.0002'
:setvar updateversion '1.5.0.0003'

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

/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 11/16/2012 16:10:08 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[OffersetWindow]') AND type in (N'U'))
DROP TABLE [dbo].[OffersetWindow]
GO

/****** Object:  Table [dbo].[OffersetWindow]    Script Date: 11/16/2012 16:10:08 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[OffersetWindow](
	[windowId] [int] IDENTITY(1,1) NOT NULL,
	[label] [varchar](30) NOT NULL,
	[hourStart] [int] NOT NULL,
	[hourEnd] [int] NOT NULL,
	[dateActive] [datetime] NULL,
	[parkFacilityID] [bigint] NULL,
 CONSTRAINT [PK_OffersetWindow] PRIMARY KEY CLUSTERED 
(
	[windowId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[xiFacilities]    Script Date: 11/16/2012 11:02:08 ******/
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[xiFacilities]') AND type in (N'U'))
DROP TABLE [dbo].[xiFacilities]
GO


/****** Object:  Table [dbo].[xiFacilities]    Script Date: 11/16/2012 11:02:08 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

SET ANSI_PADDING ON
GO

CREATE TABLE [dbo].[xiFacilities](
	[fId] [int] IDENTITY(1,1) NOT NULL,
	[facilityId] [varchar](15) NOT NULL,
	[longname] [varchar](60) NOT NULL,
	[shortname] [varchar](15) NOT NULL,
	[parkFacilityID] [bigint] NULL,
	[FacilityConfiguration] [varchar](64) NULL,
	[FacilityConfigurationID] [int] NULL,
	[parkName] [varchar](256) NULL,
PRIMARY KEY CLUSTERED 
(
	[fId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]

GO

SET ANSI_PADDING OFF
GO

/****** Object:  Table [dbo].[xiFacilities]    Script Date: 11/16/2012 13:35:31 ******/
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'136550', N'Monsters, Inc. Laugh Floor', N'Monsters', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'16767263', N'Under the Sea - Journey of the Little Mermaid', N'Mermaid', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'16767276', N'Enchanted Tales with Belle', N'Belle', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010210', N'The Magic Carpets of Aladdin', N'Aladdin', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'136', N'Indiana Jones Epic Stunt Spectacular', N'Epic', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'14888', N'Lights, Motors, Action! Extreme Stunt Show', N'LMA', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'209857', N'Toy Story Mania!', N'Mania', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'320248', N'The American Idol Experience', N'Idol', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'15839605', N'Disney Junior-Live on Stage!', N'Disney Jr', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010151', N'Muppet*Vision 3D', N'Muppets', 80007998, N'Mobile', 2, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010182', N'Rock ''n'' Roller Coaster Starring Aerosmith', N'Coaster', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010193', N'Star Tours ', N'Star Tours', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010207', N'The Great Movie Ride', N'GMR', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010218', N'The Twilight Zone Tower of Terror', N'Tower', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010896', N'Voyage of The Little Mermaid', N'Mermaid', 80007998, N'Standard', 1, N'Disney''s Hollywood Studios')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'15850196', N'Mickey Town Square Theater', N'mickey', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'15850198', N'Princesses Town Square Theater', N'princess', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010114', N'Buzz Lightyears Space Ranger Spin', N'buzz', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010153', N'Jungle Cruise', N'jungle', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010170', N'Mickeys PhilharMagic', N'phil', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010176', N'Peter Pans Flight', N'pan', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010190', N'Space Mountain', N'space', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010192', N'Splash Mountain', N'splash', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010208', N'Haunted Mansion', N'haunted', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010213', N'Adventures of Winnie the Pooh', N'pooh', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'16491297', N'Barnstormer', N'barn', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010129', N'Dumbo', N'dumbo', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'80010110', N'Big Thunder Mountain', N'btm', 80007944, N'Standard', 1, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'320755', N'Celebrate a Dream Come True Parade', N'dream', 80007944, N'Mobile', 2, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'15448884', N'Main Street Electrical Parade', N'mainst', 80007944, N'Mobile', 2, N'Magic Kingdom Park')
INSERT [dbo].[xiFacilities] ([facilityId], [longname], [shortname], [parkFacilityID], [FacilityConfiguration], [FacilityConfigurationID], [parkName]) VALUES (N'82', N'Wishes Nighttime Spectacular', N'wishes', 80007944, N'Mobile', 2, N'Magic Kingdom Park')

IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetBlueLaneForAttraction') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetBlueLaneForAttraction 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetBlueLaneReasonCodes') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetBlueLaneReasonCodes 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetEntitlementAll') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetEntitlementAll 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetEntitlementSummary') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetEntitlementSummary 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetEntitlementSummaryHourly') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetEntitlementSummaryHourly 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetExecSummary') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetExecSummary 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetHourlyRedemptions') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetHourlyRedemptions 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetQueueCountForAttraction') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetQueueCountForAttraction 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetRedeemedForCal') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetRedeemedForCal 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetRedeemedOffersets') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetRedeemedOffersets 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetRedeemedOverrideOffersets') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetRedeemedOverrideOffersets 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetRedeemedOverridesForCal') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetRedeemedOverridesForCal 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetSelectedForDate') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetSelectedForDate 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_GetSelectedOffersets') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_GetSelectedOffersets 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_PreArrivalData') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_PreArrivalData 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_ProjectedData') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_ProjectedData 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_RecruitedDaily') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_RecruitedDaily 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_RecruitedEligible') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_RecruitedEligible 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_RecruitEngagedToDate') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_RecruitEngagedToDate 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_RecruitGetVisits') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_RecruitGetVisits 
	GO  
	
IF EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'usp_RecruitTotalRecruited') and type in (N'P', N'PC')) 
	DROP PROCEDURE usp_RecruitTotalRecruited 
	GO  
	
IF  EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[gxp].[usp_RedemptionEvent_Create]') AND type in (N'P', N'PC'))
DROP PROCEDURE [gxp].[usp_RedemptionEvent_Create]
GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get blue lane count
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/14/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneForAttraction] 
@facilityID int,
@strStartDate varchar(30),
@strEndDate varchar(30)
AS

declare @bluelanecount int, @overridecount int,
	@starttime datetime, @endtime datetime;
select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

-- with this query, we're still going to "lose" overrides on zero value/null r.FacilityID 
SELECT	@overridecount = count(distinct r1.RedemptionEventID)
from gxp.RedemptionEvent r1 (nolock)
join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
	and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID and x.facilityId = @facilityId
LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID 
where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime 
and g.GuestID is NULL

--blue lane counts
select @bluelanecount=count(bluelaneeventid)+@overridecount
from gxp.bluelaneevent ble (nolock)
JOIN [gxp].[BusinessEvent] be (nolock) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
where ble.entertainmentId = @facilityID
and ble.taptime between @starttime and @endtime
and g.GuestID is NULL

select @bluelanecount, @overridecount

GO

-- =============================================
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description:	error/bug in override calculation
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/12/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetBlueLaneReasonCodes]
@strStartDate varchar(30),
@strEndDate varchar(30),
@parkID bigint = NULL 
AS

declare @starttime datetime, @endtime datetime;
DECLARE @parkIDs table (parkID bigint)

select @starttime=convert(datetime, @strStartDate);
select @endtime=convert(datetime, @strEndDate);

declare @ReasonCodeIDEarly int, @ReasonCodeIDLate int, @ReasonCodeNoXpass int

select @ReasonCodeIDEarly = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'Early'
select @ReasonCodeIDLate = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'Late'
select @ReasonCodeNoXpass = ReasonCodeID from gxp.ReasonCode where ReasonCode = 'No Xpass'

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

select t1.ReasonCodeID, ReasonCodeCount  = t1.ReasonCodeCount + isnull(t2.ReasonCodeCount,0) 
from (
select r.ReasonCodeID, ReasonCodeCount = isnull(t1.blcount, 0)
from gxp.ReasonCode r (nolock)
left join (
select distinct(ble.ReasonCodeID), rc.ReasonCode, blcount=count(bluelaneeventid)
	from gxp.bluelaneevent ble (NOLOCK)
	JOIN[gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
	JOIN gxp.ReasonCode rc (nolock) ON ble.ReasonCodeID = rc.ReasonCodeID
    JOIN [dbo].xiFacilities x (nolock) on x.facilityId = ble.EntertainmentID
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
where ble.taptime between @starttime and @endtime
and x.parkFacilityID in (select parkID from @parkIDs)
and g.GuestID is NULL
group by ble.ReasonCodeID, rc.ReasonCode ) as t1 on r.ReasonCodeID = t1.ReasonCodeID
) as t1

left join (

select ReasonCodeID = case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end,
	 ReasonCodeCount = COUNT(*)
	 from (
 select distinct 
	Offset = case when r1.tapdate > b2.StartTime then datediff(MINUTE,b2.EndTime,r1.tapdate)
	else datediff(MINUTE,b2.StartTime,r1.tapdate)
	end
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
	JOIN [dbo].xiFacilities x on x.facilityId = r1.FacilityID
    LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID 
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.guestID is NULL
  and x.parkFacilityID in (select parkID from @parkIDs)) as t3
  group by case when Offset <= 0 then @ReasonCodeIDEarly
		when Offset > 0 then  @ReasonCodeIDLate
		else 	@ReasonCodeNoXpass
		end
) t2 on t1.ReasonCodeID = t2.ReasonCodeID




GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	get entitlement totals across attractions
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementAll] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int, 
		@Bluelane int, @RedeemedOverrides int, 
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
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
	left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b
				join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK' 
    and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
    and g.GuestID is NULL
	and t2.ReferenceID is NULL
	
select @bluelanecount=count(ble.bluelaneeventid)
	from gxp.bluelaneevent ble
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
	where  
	ble.taptime between @starttime and @endtime
    and g.GuestID is NULL

    
SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
	WHERE	ar.[AppointmentReason] in ('SWP', 'ACS', 'OTH', 'OVR') 
	and		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestID is NULL
	
select @Bluelane=(@bluelanecount + @overridecount)

SELECT	@Redeemed = count(*)
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar WITH(NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st WITH(NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
	WHERE	ar.[AppointmentReason] = 'STD'
	and		st.[AppointmentStatus] = 'RED'
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestID is NULL


select @RedeemedOverrides= count(*)
from gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
where 
    (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    and bl.taptime between @starttime and @endtime
    and g.GuestID is NULL


select Selected = @Selected,
	Redeemed = @Redeemed + @RedeemedOverrides, 
	Bluelane = @Bluelane,
    Overrides = @overridecount

GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	show select to EOD
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummary] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;


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
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK'
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
    and g.GuestID is NULL
	and t2.ReferenceID is NULL
	
select @bluelanecount=count(ble.bluelaneeventid)
	from gxp.bluelaneevent ble
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestID is NULL

    
SELECT	@overridecount = count(r.[RedemptionEventID])
	FROM	[gxp].[RedemptionEvent] r WITH(NOLOCK)
	JOIN	[dbo].xiFacilities x (nolock) on x.facilityId = r.FacilityID and x.facilityId = @facilityId
	JOIN	[gxp].[AppointmentReason] ar ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
	WHERE	ar.[AppointmentReason] in ('SWP', 'ACS', 'OTH', 'OVR') 
	AND		x.facilityId = @facilityID
	AND		DATEADD(HH, -4, r.[TapDate]) between @starttime and @endtime
    and g.GuestID is NULL

SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2  (nolock)on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason
 a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID and x.facilityId = @facilityId
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID 
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestID is NULL 
  
select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID and x.facilityId  = @facilityId and FacilityConfigurationID = 2
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID 
	where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime 
 	and g.GuestID is NULL 
 	and r1.AppointmentStatusID = 1  
 	
 	
select @RedeemedOverrides = 0


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL

select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedMobile, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount

GO

-- =============================================
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	hourly version of entitlementsummary
-- Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetEntitlementSummaryHourly] 
@facilityId varchar(25) = NULL ,
@strStartDate varchar(25) = NULL,
@strEnendtime varchar(25) = NULL 
AS

DECLARE @Selected int, @Redeemed int,  @RedeemedMobile int, @InQueue int, 
    @starttime datetime, @endtime datetime, @select_datetime datetime, @currentTime datetime,
    @bluelanecount int, @overridecount int, @RedeemedOverrides int;

set @currentTime = getdate()

IF @strStartDate is NULL
BEGIN
SET @starttime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
select @starttime=convert(datetime, @strStartDate)


IF @strEnendtime is NULL
BEGIN
SET @endtime =getdate()
END 
ELSE
select @endtime=convert(datetime, @strEnendtime)

--
-- need to get selects to the end of current hour
--
set @select_datetime=DATEADD(SS, 59-DATEPART(SS, @endtime), DATEADD(MI, 59-DATEPART(MI, @endtime), @endtime))

select @Selected=count(b.BusinessEventID)
	from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
     left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (NOLOCK)
				join gxp.BusinessEventType b1 (NOLOCK) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (NOLOCK) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK'
	and b.entertainmentId = @facilityId
	and dateadd(HH, -4, b.StartTime) between @starttime and @select_datetime
    and g.GuestID is NULL
	and t2.ReferenceID is NULL

    
SELECT	@overridecount = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID and a2.AppointmentReason in ('ACS','SWP','OTH','OVR')
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
			select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
			)
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID and x.facilityId = @facilityId
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime   
  and g.GuestID is NULL  

select @bluelanecount=count(ble.bluelaneeventid)+@overridecount
	from gxp.bluelaneevent ble
	JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = ble.[BlueLaneEventID]
    LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
	where  ble.entertainmentId = @facilityID
	and ble.taptime between @starttime and @endtime
    and g.GuestID is NULL


SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (NOLOCK)
	join gxp.RedemptionEvent r2 (NOLOCK) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (NOLOCK) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (NOLOCK) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (NOLOCK) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (NOLOCK) on b1.ReferenceID = b2.ReferenceID 
	join gxp.BusinessEventType bt (NOLOCK) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID and x.facilityId = @facilityId
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID 
  where DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
  and g.GuestID is NULL 
  and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and @currentTime
    

select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID and x.facilityId  = @facilityId and FacilityConfigurationID = 2
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID 
	where DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
	and g.GuestID is NULL 
	and DATEADD(HH, -4, r1.[TapDate]) between convert(date, @starttime) and @currentTime
 	and r1.AppointmentStatusID = 1  

select @RedeemedOverrides=0


SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f on f.FacilityID = e.FacilityID and f.FacilityName = @facilityId
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID 
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge', 'Abandon'))
	and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL

select 
    Available = -1,
    Selected = @Selected,
	Redeemed = @Redeemed+@RedeemedMobile, 
	Bluelane = @bluelanecount,
	InQueue = @InQueue,
    Overrides = @overridecount

GO

-- =============================================
-- Author:      James Francis
-- Create date: 08/20/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0011
-- Author:      James Francis/Amar Terzic
-- Create date: 09/09/2012
-- Description: updated to add overrides for early/late
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	map only back to windows on same day
-- Update Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/01/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetExecSummary]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL
AS
BEGIN
		
DECLARE @Selected int, @Redeemed int, @RedeemedMobile int, @InQueue int, @PilotParticipants int, 
    @EOD_datetime varchar(30), @starttime datetime, @endtime datetime,
    @RedeemedOverrides int, @overridecount int, @BlueLaneCount int, @RedeemedNonStandard int;

DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID


set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)));
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
	JOIN [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
	LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
	left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	where bet.BusinessEventType = 'BOOK'
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	and g.GuestID is NULL
	and parkFacilityID in (select parkID from @parkIDs)
	and t2.ReferenceID is NULL

SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) between @starttime and DATEADD(DD,1,@starttime )
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID	
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
	where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
	and g
.GuestID is NULL 
	and parkFacilityID in (select parkID from @parkIDs)
  
select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
 	where x.FacilityConfigurationID = 2
	and parkFacilityID in (select parkID from @parkIDs)
 	and dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
 	and g.GuestID is NULL 
 	and r1.AppointmentStatusID = 1
  
  
SELECT	@RedeemedNonStandard = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) not between @starttime and DATEADD(DD,1,@starttime )
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID	
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
	where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
	and g.GuestID is NULL 
	and parkFacilityID in (select parkID from @parkIDs)

select @BlueLaneCount = 0


select @RedeemedOverrides = count(distinct r1.RedemptionEventID)+@RedeemedNonStandard
from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	left join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	left join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and b2.BusinessEventTypeID = (
				select BusinessEventTypeID from gxp.BusinessEventType (nolock) where BusinessEventType = 'BOOK'
				)
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID	
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID	
  where dateadd(hh,-4,r1.tapdate) between @starttime and @endtime  
  and g.GuestID is NULL  
  and parkFacilityID in (select parkID from @parkIDs)
  and b2.BusinessEventID is NULL




SELECT	@overridecount = 2*@RedeemedOverrides

select @PilotParticipants=count(distinct(b.GuestId))
	from GXP.BusinessEvent(nolock) as b
	JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID	
	LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
	left join 
	(    -- minus all CHANGE/CANCELS
		select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
			from gxp.BusinessEvent b
			join gxp.BusinessEventType b1 on b.BusinessEventTypeId= b1.BusinessEventTypeId 
				and BusinessEventType = 'CHANGE'
			join gxp.BusinessEventSubType b2 on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
				and BusinessEventSubType = 'CANCEL'
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	WHERE bet.BusinessEventType = 'BOOK' 
	and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
	and g.GuestID is NULL
	and parkFacilityID in (select parkID from @parkIDs)
	and t2.ReferenceID is NULL

SELECT @InQueue = count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f (nolock) on f.FacilityID = e.facilityId 
	join xiFacilities x	(nolock) on f.FacilityName = x.facilityId 
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and parkFacilityID in (select parkID from @parkIDs)	
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)	
    and g.GuestID is NULL
    and e.ReaderLocation <> 'FPP-Merge'
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
	from rdr.Event e (nolock)
	join rdr.Facility f (nolock) on f.FacilityID = e.facilityId 
	join xiFacilities x	(nolock) on f.FacilityName = x.facilityId 
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and parkFacilityID in (select parkID from @parkIDs)	
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
where t2.GuestID is NULL


select Selected = @Selected,	Redeemed = @Redeemed + @RedeemedMobile, 
	PilotParticipants = @PilotParticipants, 
    InQueue = @InQueue, OverrideCount = @overridecount, RedeemedOverrides=@RedeemedOverrides,
    BlueLaneCount=@BlueLaneCount


END
GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Delete facility to metadata table
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/23/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	map redemption back to window
-- Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/07/2012
-- Description:	fixed concept of early/late
-- Version: 1.3.1.0017
-- Author:		Amar Terzic
-- Create date: 11/14/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetHourlyRedemptions]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL  
AS

DECLARE @Selected int, @Redeemed int, @RedeemedMobile int,
    @starttime datetime, @endtime datetime,
    @RedeemedOverrides int
DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

select @Selected=count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
JOIN [dbo].xiFacilities x on x.facilityId = b.EntertainmentID
LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
left join 
(    -- minus all CHANGE/CANCELS
    select b.GuestID, ReferenceID
        from gxp.BusinessEvent b (nolock)
        join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
            and BusinessEventType = 'CHANGE'
        join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
            and BusinessEventSubType = 'CANCEL'
    where dateadd(HH, -4, b.StartTime) between @starttime and @endtime
) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @starttime and @endtime
and g.GuestID is NULL
and t2.ReferenceID is NULL
and x.parkFacilityID in (select parkID from @parkIDs)

SELECT	@Redeemed = count(distinct r1.RedemptionEventID)
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x on x.facilityId = r1.FacilityID
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
	WHERE DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
    and g.GuestID is NULL
	and x.parkFacilityID in (select parkID from @parkIDs)

 select @RedeemedMobile =  count(distinct r1.RedemptionEventID)
   from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	JOIN [dbo].xiFacilities x on x.facilityId = r1.FacilityID
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
 	where DATEADD(HH, -4, b2.StartTime) between @starttime and @endtime 
    and g.GuestID is NULL
 	and r1.AppointmentStatusID = 1
 	and FacilityConfigurationID = 2
 	and x.parkFacilityID in (select parkID from @parkIDs)

select @RedeemedOverrides=0

SELECT @Selected, @Redeemed+@RedeemedMobile
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get current queue count per attraction
-- Update Version: 1.3.1.0001
-- Author:		Amar Terzic
-- Create date: 11/14/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetQueueCountForAttraction]
    @strAttrID varchar(25) = NULL,
    @strCurrentDatetime varchar(25) = NULL
AS
DECLARE @starttime datetime,
    @endtime datetime;

IF @strCurrentDatetime is NULL
    BEGIN
    SET @endtime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
    END 
ELSE
    set @endtime=convert(datetime, @strCurrentDatetime)

set @starttime=dateadd(hour, -3, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))));

SELECT count(distinct t1.GuestID) 
from (
select e.guestID, RideNumber, e.FacilityID, FacilityName
	from rdr.Event e (nolock)
	join rdr.Facility f (nolock) on f.FacilityID = e.facilityId 
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)	
    and g.GuestID is NULL
    and e.ReaderLocation <> 'FPP-Merge'
) as t1
left join (
select e.guestID, RideNumber, e.FacilityID
	from rdr.Event e (nolock)
	join rdr.Facility f (nolock) on f.FacilityID = e.facilityId 
	LEFT JOIN guestFilter g (nolock) ON e.GuestID = g.GuestID
	where xPass = 1
	and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
		--make sure sticky queue guests don't accumulate
		--and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
		and TimeStamp between dateadd(hour, 3, @endtime) and dateadd(hour, 4, @endtime)
    and g.GuestID is NULL
) as t2 on t1.RideNumber = t2.RideNumber		
	and t1.guestID = t2.guestID
	and t1.facilityID = t2.facilityID
    and t1.FacilityName = @strAttrID
where t2.GuestID is NULL


/*
rdr.Facility f,
(
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e (nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName = 'Entry')
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
    ) as t1
    left join (
    -- get list of guests that have already merged
    select guestID, RideNumber, facilityID, xPass, [day] = DATEPART(dd,Timestamp), [hour] = DATEPART (HH,Timestamp), Timestamp
        from rdr.Event e(nolock)
        join rdr.BandType bt on bt.BandTypeID = e.BandTypeID and bt.BandTypeName='Guest'
        where xPass = 1
        and EventTypeID in (select EventTypeID from rdr.EventType where EventTypeName in ('Merge','Abandon'))
            and TimeStamp between dateadd(hour, 4, @starttime) and dateadd(hour, 4, @endtime)
        and guestID >= 407 and GuestID not in (971, 1162)
) as t2 on t1.RideNumber = t2.RideNumber		
    and t1.guestID = t2.guestID
    and t1.facilityID = t2.facilityID
-- this just returns the ones that haven't merged yet    
where t2.GuestID is NULL
and t1.facilityID = f.facilityID
    and f.FacilityName = @strAttrID;
*/
GO
-- =============================================

-- =============================================
-- Update date: 08/20/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0007
-- Description:	Change to use RedemptionEvent.
-- Update date: 08/24/2012
-- Updated by:	Ted Crane
-- Update Version: 1.3.0.0011
-- Description:	Test band exclusion
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/05/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL,
@parkID bigint = NULL 

AS
BEGIN
	DECLARE @Selected int, @Redeemed int, @starttime datetime, @endtime datetime, @EOD_datetime datetime;
	DECLARE @parkIDs table (parkID bigint)

	IF @strCutOffDate  is NULL
	BEGIN
	SET @endtime =getdate()
	END 
	ELSE
	select @endtime=convert(datetime, @strCutOffDate)
	select @starttime=DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

	set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
	set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
	set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
	set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select [Date] = t.dt, Redemeed = ISNULL(Redeemed,0), Selected = ISNULL(Selected,0) 
	from [dbo].[DAYS_OF_YEAR] t
	LEFT JOIN 
	(

	--REPLACEMENT CODE
select CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME) as [TimestampRedeemed], 
	count(r.[RedemptionEventID]) as [Redeemed]
	FROM	[gxp].[RedemptionEvent] r (NOLOCK)
	JOIN	[gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
	JOIN	[gxp].[AppointmentReason] ar (NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
	JOIN	[gxp].[AppointmentStatus] st (NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
    JOIN	[dbo].xiFacilities x (NOLOCK) on x.facilityId = r.FacilityID
	LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID
	where ar.[AppointmentReason] = 'STD'
	AND	  st.[AppointmentStatus] = 'RED'
	AND   r.[TapDate] BETWEEN @starttime and @endtime
    and g.GuestID is NULL
    and x.parkFacilityID in (select parkID from @parkIDs )
	group by CAST(CONVERT(CHAR(10),r.[TapDate],102) AS DATETIME)   
	) as t1 on t.dt = t1.[TimestampRedeemed]  
	LEFT JOIN 
	(
        select CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME) as [TimestampBooked], 
            Selected=count(b.BusinessEventID)
        from GXP.BusinessEvent (nolock) as b
        JOIN  [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
        LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
        left join 
        (    -- minus all CHANGE/CANCELS
            select b.GuestID, ReferenceID
            from gxp.BusinessEvent b (nolock)
            join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
                and BusinessEventType = 'CHANGE'
            join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
                and BusinessEventSubType = 'CANCEL'
            where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
        ) as it2 on b.GuestID = it2.GuestID and b.ReferenceID = it2.ReferenceID
        where bet.BusinessEventType = 'BOOK'
        and dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
        and g.GuestID is NULL 
        and it2.ReferenceID is NULL
        group by CAST(CONVERT(CHAR(10),dateadd(HH, -4, b.StartTime),102) AS DATETIME)	
	) as t2 on t.dt = t2.[TimestampBooked]
	where t.dt between @starttime and @endtime
	order by t.dt desc

END


GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	
-- Update Version: 1.3.1.0009
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description: mapped redemptions back to their bookings
-- Update Version: 1.3.1.0016
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description: missed a date offset in redemptions
-- Update Version: 1.3.1.0017
-- Author:		James Francis
-- Create date: 09/12/2012
-- Description: missed a date offset in redemptions
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/02/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS
BEGIN
	
DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime
DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

set @parkEndOfDay = NULL
select @parkEndOfDay = MAX(DATEADD(HH, hourEnd/100,@starttime))
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select (ow.label), sum(isnull(t1.offersetcount, 0)) as offersetcount
	from [dbo].[OffersetWindow] ow (nolock) 
	left join (
	select os.offerset as offerset, isnull(count(distinct r1.[RedemptionEventID]),0) as offersetcount, x.parkFacilityID
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID	
	JOIN (
		select distinct(b.guestid), isnull(g1.offerset, 'window4') as offerset, g1.parkFacilityID
		from  GXP.BusinessEvent(nolock) as b
		JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
		join
		(
			select guestid, min(table1.label) as offerset, table1.parkFacilityID
			from(
			select b.GuestId as guestid, 
		convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh,
        x.parkFacilityID 
				from GXP.BusinessEvent(nolock) as b
                JOIN [dbo].xiFacilities (nolock) x on x.facilityId = b.EntertainmentID	 
                LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
                left join 
					(    -- minus all CHANGE/CANCELS
						select b.GuestID, ReferenceID
							from gxp.BusinessEvent b (nolock)
							join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId and BusinessEventType = 'CHANGE'
							join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
								and BusinessEventSubType = 'CANCEL'
						where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
					) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
				where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
                and g.GuestID is NULL 
                and t2.ReferenceID is NULL 
                and x.parkFacilityID in (select parkID from @parkIDs)
				group by b.GuestId, parkFacilityID
			) as gtable
			join (
			select * from  [dbo].[OffersetWindow] (nolock) 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            and parkFacilityID in (select parkID from @parkIDs)
            ) as table1 on (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND (gtable.maxh between table1.hourStart and table1.hourEnd)
			   AND gtable.parkFacilityID = table1.parkFacilityID
			group by guestid, table1.parkFacilityID
		) as g1 on b.guestid = g1.guestid
		and x.parkFacilityID = g1.parkFacilityID
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset, g1.parkFacilityID
	) as os on os.guestId = b2.GuestId
	and x.parkFacilityID = os.parkFacilityID
	where dateadd(hh,-4,r1.[TapDate]) between @starttime and @endtime
	and os.parkFacilityID in (select parkID from @parkIDs)
	group by os.offerset, x.parkFacilityID
	) as t1
	on t1.offerset = ow.label 
	and t1.parkFacilityID = ow.parkFacilityID
	where ow.parkFacilityID in (select parkID from @parkIDs)
	and convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
	group by ow.label

END
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get Guest list for subway diagram reader/touch point 
-- Update Version: 1.3.1.0002
-- Author:		Amar Terzic
-- Create date: 11/03/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverrideOffersets]  
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime
DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


	select distinct(ow.label), isnull(x.offersetcount, 0) as offersetcount
	from [dbo].[OffersetWindow] ow (nolock) 
	left join
	(select os.offerset as offerset, isnull(count(distinct r1.[RedemptionEventID]),0) as offersetcount
	 from gxp.RedemptionEvent r1 (nolock)
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID 
			and r1.AppointmentStatusID = 1 
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'	
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID	
 	JOIN (
		select distinct(b.guestid), isnull(g1.offerset, 'window4') as offerset
		from  GXP.BusinessEvent(nolock) as b
		join
		(
			select guestid, min(table1.label) as offerset
					from
			(
			select b.GuestId as guestid, 
		convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh 
				from GXP.BusinessEvent(nolock) as b
                LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
                left join 
					(    -- minus all CHANGE/CANCELS
						select b.GuestID, ReferenceID
							from gxp.BusinessEvent b (nolock)
							join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
								and BusinessEventType = 'CHANGE'
							join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
								and BusinessEventSubType = 'CANCEL'
						where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
					) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
				where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
                and g.GuestID is NULL
                and t2.ReferenceID is NULL 
				group by b.GuestId
			) as gtable
			join (
			select * from  [dbo].[OffersetWindow] (nolock) 
            where convert(datetime, left(CONVERT(varchar, dateActive, 121), 10)) 
            = convert(datetime, left(CONVERT(varchar,@starttime, 121), 10))
            ) as table1 on (gtable.minh between table1.hourStart and table1.hourEnd)
			   AND (gtable.maxh between table1.hourStart and table1.hourEnd)
			group by guestid
		) as g1 on b.guestid = g1.guestid
		where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		group by b.GuestId, g1.offerset
	) as os on os.guestId = b2.GuestId
	where dateadd(hh,-4,r1.[TapDate]) between @starttime and @endtime
	and x.parkFacilityID in (select parkID from @parkIDs)
--	and x.FacilityConfigurationID = 2
  	group by os.offerset
	) as x
	on x.offerset = ow.label
	group by ow.label, x.offersetcount
GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get early/late overrides for calendar
-- Update Version: 1.3.1.0001
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/05/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL,
@parkID bigint = NULL 
AS
DECLARE @Selected int, @Redeemed int,           
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @currentDateTime datetime, @currentTime datetime, @EODTime datetime;
    DECLARE @parkIDs table (parkID bigint)
    
BEGIN
    select @currentDateTime = GETDATE()
    select @currentTime = convert(datetime, '1900-01-01 ' + right('0'+CONVERT(varchar,datepart(HH,@currentDateTime)),2) +':'+
                            right('0'+CONVERT(varchar,datepart(MI,@currentDateTime)),2)+':'+
                            right('0'+CONVERT(varchar,datepart(SS,@currentDateTime)),2))
                            
    select @EODTime = convert(datetime, '1900-01-01 ' + '23:59:59')

    IF @strCutOffDate  is NULL
    BEGIN
    SET @endtime =@currentDateTime
    END 
    ELSE
    select @endtime = case when convert(date, @strCutOffDate) = CONVERT(date,@currentDateTime) then convert(date, @strCutOffDate) + @currentTime
                        else convert(date, @strCutOffDate) + @EODTime
                        end
    select @starttime = DATEADD(DD, -@number_days+1, convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10))))

    select @EOD_datetime = convert(date, @endtime) + @EODTime

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

    select [Date] = t.dt, RedeemedOverrides = 0--ISNULL(RedeemedOverrides,0)
    from [dbo].[DAYS_OF_YEAR] t
    where t.dt between @starttime and @endtime
    order by t.dt desc
END

GO

-- =============================================
-- Author:		James Francis
-- Create date: 09/10/2011
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/04/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedForDate]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime
DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)

select count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
join GXP.BusinessEventType(nolock) as bet on b.BusinessEventTypeId= bet.BusinessEventTypeId 
											and bet.BusinessEventType = 'BOOK' 
JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock) 
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
and g.GuestID is NULL
and t2.ReferenceID is NULL
and x.parkFacilityID in (select parkID from @parkIDs) 


GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0015
-- Author:		James Francis
-- Create date: 09/11/2012
-- Description:	filter out cancels
-- Update Version: 1.3.1.0016
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/04/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetSelectedOffersets]
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS

DECLARE @starttime datetime, @endtime datetime, @EOD_datetime datetime, @parkEndOfDay datetime
DECLARE @parkIDs table (parkID bigint)

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

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

select @parkEndOfDay = DATEADD(HH, hourEnd/100,@starttime)
	from OffersetWindow 
	where dateActive = @starttime
	and label = 'window4'
	

set @EOD_datetime=convert(datetime,(select LEFT(convert(varchar, @endtime, 121), 10)))
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime)
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime)
set @EOD_datetime=dateadd(second, 59,@EOD_datetime)


select ow.label, sum(isnull(x.offersetcount, 0)) as offersetcount
	from [dbo].[OffersetWindow] ow (nolock)
	left join
(
select label = windowId, offersetcount = SUM(entitlementCount)--, guestCount = COUNT(distinct guestID)
from (
    select [Date], guestid, windowId = min(windowId), minh, maxh, entitlementCount, t1.parkFacilityID
    from OffersetWindow o (nolock) 
    join (
        select [Date] = convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId as guestid, entitlementCount = COUNT(distinct BusinessEventID),
        convert(int, convert(varchar, min(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, min(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as minh, 
        convert(int, convert(varchar, max(datepart(HH, dateadd(HH, -4, b.StartTime))))+left(convert(varchar, max(datepart(MI, dateadd(HH, -4, b.StartTime))))+'00',2)) as maxh,
        x.parkFacilityID
        from GXP.BusinessEvent b (nolock)
        join GXP.BusinessEventType bet (nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId  and bet.BusinessEventType = 'BOOK' 
        JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
        LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
        left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock) 
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @starttime and @EOD_datetime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
        where dateadd(HH, -4, b.StartTime) between @starttime and @parkEndOfDay
         and g.GuestID is NULL
       	and t2.ReferenceID is NULL 
       	and x.parkFacilityID in (select parkID from @parkIDs)
        group by convert(date, dateadd(HH, -4, b.StartTime)), b.GuestId, x.parkFacilityID
    ) as t1
    on t1.minh between o.hourStart and o.hourEnd
	and t1.maxh between o.hourStart and o.hourEnd
    and convert(datetime, o.dateActive, 110
) = convert(datetime, @starttime, 110)
    and o.parkFacilityID = t1.parkFacilityID
	group by [Date], guestid, minh, maxh, entitlementCount, t1.parkFacilityID
) as t2
group by windowId
)
as x
on x.label = ow.windowId
where convert(datetime, ow.dateActive, 110) = convert(datetime, @starttime, 110)
and ow.parkFacilityID in (select parkID from @parkIDs)
group by ow.label


GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	get pre-arrival numbers for recruiting
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	test band exclusion
-- Update Version: 1.3.1.0011
-- Create date: 09/10/2012
-- Description:	remove cancels
-- Update Version: 1.3.1.0015
-- Update Version: 1.3.1.0018
-- Author:		Amar Terzic
-- Create date: 11/03/2012
-- Description:	Multi-park support 
-- Update Version: 1.4.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_PreArrivalData]
    @sUseDate varchar(40),
    @sProgramStartDate varchar(40),
	@parkID bigint = NULL 
AS
    DECLARE @currentDate datetime, @programStartDate datetime, @EOD_datetime datetime
	DECLARE @parkIDs table (parkID bigint)
	
set @currentDate = convert(datetime, @sUseDate)

IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, '2012-08-01')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);

select dtDiff, guestCount = count(*) from (
 
select distinct(b.guestID),DATEDIFF(day, b.TimeStamp,  MIN(b.StartTime))  as dtDiff
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [RDR].[Guest] g (nolock) ON b.GuestID = g.GuestID
    JOIN [dbo].xiFacilities x on x.facilityId = b.EntertainmentID    
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID,  BusinessEventType, BusinessEventSubType
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
	where bet.BusinessEventType = 'BOOK'
	and dateadd(HH, -4, b.StartTime) between @programStartDate and dateadd(DD, 7, @EOD_datetime)
	and g.GuestType = 'Guest'
	and t2.ReferenceID is NULL
	and x.parkFacilityID in (select parkID from @parkIDs)
	group by b.guestID, b.StartTime, b.Timestamp) as t1
	group by dtDiff
	order by dtDiff

GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_ProjectedData]
@strUseDate varchar(25) = NULL
AS
DECLARE @currentTime datetime, @Selected int, @Redeemed int,
 @RedeemedOverrides int, @SelectedAllDay int, @dayStart datetime, @dayEnd datetime
IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

set @dayStart=convert(datetime,(select LEFT(convert(varchar, @currentTime, 121), 10)));

set @dayEnd=@dayStart
set @dayEnd=dateadd(hour, 23, @dayEnd );
set @dayEnd=dateadd(minute, 59, @dayEnd);
set @dayEnd=dateadd(second, 59, @dayEnd);


select @SelectedAllDay = count(b.BusinessEventID)
from GXP.BusinessEvent(nolock) as b
JOIN [GXP].[BusinessEventType] bet (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock) 
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
and g.guestID is NULL
and t2.ReferenceID is NULL

select @Selected = count(b.BusinessEventID)
from GXP.BusinessEvent b (nolock) 
JOIN [GXP].[BusinessEventType] bet (nolock) on b.BusinessEventTypeId= bet.BusinessEventTypeId 
LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
   left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock) 
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE bet.BusinessEventType = 'BOOK' 
and dateadd(HH, -4, b.StartTime) between @dayStart and @currentTime
and g.guestID is NULL
and t2.ReferenceID is NULL

SELECT	@Redeemed = count(*)
FROM	[gxp].[RedemptionEvent] r (NOLOCK)
JOIN	[gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = r.[RedemptionEventID]
JOIN	[gxp].[AppointmentReason] ar (NOLOCK) ON ar.[AppointmentReasonID] = r.[AppointmentReasonID]
JOIN	[gxp].[AppointmentStatus] st (NOLOCK) ON st.[AppointmentStatusID] = r.[AppointmentStatusID]
LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
WHERE	ar.[AppointmentReason] = 'STD'
and		st.[AppointmentStatus] = 'RED'
AND		DATEADD(HH, -4, r.[TapDate]) between @dayStart and @currentTime
and g.guestID is NULL


select @RedeemedOverrides = count(*)
from gxp.BlueLaneEvent bl
	JOIN [gxp].[BusinessEvent] be (NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	JOIN gxp.ReasonCode rc (NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
	LEFT JOIN guestFilter g (nolock) ON be.GuestID = g.GuestID 
where 
   (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    and bl.taptime between @dayStart and @currentTime
    and g.guestID is NULL



select @Selected, @Redeemed + @RedeemedOverrides, @SelectedAllDay

GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruited daily
-- Update Version: 1.3.1.0002
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/10/2012
-- Description:	recruited daily
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/03/2012
-- Description:	Multi-park support 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedDaily]
    @sUseDate varchar(40) , -- ignored at this point, legacy, for java
    @sProgramStartDate varchar(40),
	@parkID bigint = NULL 
AS

DECLARE @programStartDate datetime, @EOD_datetime datetime
DECLARE @parkIDs table (parkID bigint)


SELECT @programStartDate=convert(datetime, left([value], 19), 126) FROM [dbo].[config]
    WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'

set @EOD_datetime=@programStartDate
set @EOD_datetime=dateadd(hour, 23, @EOD_datetime);
set @EOD_datetime=dateadd(minute, 59,@EOD_datetime);
set @EOD_datetime=dateadd(second, 59,@EOD_datetime);



IF @sProgramStartDate is NULL
BEGIN
SET @programStartDate =convert(datetime, '2012-08-30')
END 
ELSE
select @programStartDate=convert(datetime, @sProgramStartDate)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

select [Date] = left(CONVERT(CHAR(10),t.dt, 120), 10), [RecruitCount] = ISNULL(recruitcount,0)
    from [dbo].[DAYS_OF_YEAR] t
    LEFT JOIN (

 select convert(date, b.timestamp) as [Timestamp],
        count(distinct(b.GuestID)) as recruitcount 
    from GXP.BusinessEvent(nolock) as b
    JOIN [GXP].[BusinessEventType] bet  (nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
--    JOIN [dbo].xiFacilities x on x.facilityId = b.EntertainmentID 
    LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID 
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
			where b.StartTime between dateadd(HH, 4, @programStartDate) and dateadd(HH, 4, dateadd(DD, 7, @EOD_datetime))
		) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID    
    WHERE bet.BusinessEventType = 'BOOK' 
    and b.StartTime between dateadd(HH, 4, @programStartDate) and dateadd(HH, 4, dateadd(DD, 7, @EOD_datetime))
    and g.GuestID is NULL
	and t2.ReferenceID is NULL    
--	and x.parkFacilityID in (select parkID from @parkIDs)
    group by convert(date, b.timestamp) 
    ) as t2 on t.dt = t2.[Timestamp]
    where t.dt between  dateadd(DD, -14, @programStartDate) and dateadd(DD, 5, @programStartDate) 
    order by t.dt asc

GO
-- =============================================
-- Author:		James Francis
-- Create date: 08/26/2012
-- Description:	recruited eligible count
-- Update Version: 1.3.1.0011
-- Author:		Amar Terzic
-- Create date: 11/12/2012
-- Description:	Multi-park support 
-- Update Version: 1.4.1.0001
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitedEligible]
AS
BEGIN

declare @startdate date
declare @demoDays int

select @startdate = convert(date, left(value,10)) 
from config where property = 'DATA_START_DATE'

set @demoDays = 9

select count(distinct b.guestID)
from gxp.BusinessEvent b
left join 
					(    -- minus all CHANGE/CANCELS
						select b.GuestID, ReferenceID
							from gxp.BusinessEvent b (nolock)
							join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
								and BusinessEventType = 'CHANGE'
							join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
								and BusinessEventSubType = 'CANCEL'
					) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
WHERE dateadd(HH, -4, b.StartTime) between @startdate and DATEADD(DD,@demoDays, @startdate)
END

GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	recruitment -- changed entirely -- window set to 7 days, based off program start date
-- Update Version: 1.3.1.0013
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- Author:		Amar Terzic
-- Create date: 11/13/2012
-- Description:	Multi-park support and guest filter
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitEngagedToDate] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL
AS
-- left interface the same for now
DECLARE @Selected int, @Redeemed int, @dayStart datetime, @dayEnd datetime

set @dayStart=convert(datetime,
    (
        select LEFT([value] , 10) 
        FROM [dbo].[config] 
        WHERE [property] = 'DATA_START_DATE' and [class] ='XiConfig'
    )
)

set @dayEnd=DATEADD(DD, 5, @dayStart)
set @dayEnd=dateadd(hour, 23, @dayEnd )
set @dayEnd=dateadd(minute, 59, @dayEnd)
set @dayEnd=dateadd(second, 59, @dayEnd)

select FacilityID = case when t1.parkFacilityID IS NOT NULL and t2.parkFacilityID IS NOT NULL then 80007798 --WDW Resort
	else ISNULL(t1.parkFacilityID, t2.parkFacilityID)
	end, 
	GuestCount = COUNT(distinct ISNULL(t1.guestID, t2.guestID))
from (
select x.parkFacilityID, Ddate=convert(date, dateadd(HH, -4, b.StartTime)), b.guestID 
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
    LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
    LEFT JOIN 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
	) as ti2 on b.GuestID = ti2.GuestID and b.ReferenceID = ti2.ReferenceID
    where bet.BusinessEventType = 'BOOK' 
    and b.StartTime between dateadd(HH, 4, @dayStart) and dateadd(HH, 4, @dayEnd)
    and g.guestID is NULL
 	and ti2.ReferenceID is NULL 
 	and x.parkFacilityID = 80007944 --MK
 	) as t1
full join (
select x.parkFacilityID, Ddate=convert(date, dateadd(HH, -4, b.StartTime)), b.guestID 
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
    LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
    LEFT JOIN 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
	) as ti2 on b.GuestID = ti2.GuestID and b.ReferenceID = ti2.ReferenceID
    where bet.BusinessEventType = 'BOOK' 
    and b.StartTime between dateadd(HH, 4, @dayStart) and dateadd(HH, 4, @dayEnd)
    and g.guestID is NULL
 	and ti2.ReferenceID is NULL 
 	and x.parkFacilityID = 80007998 -- HS
 	) as t2 on t1.GuestID = t2.GuestID
	group by 
	case when t1.parkFacilityID IS NOT NULL and t2.parkFacilityID IS NOT NULL
 then 80007798 --WDW Resort
	else ISNULL(t1.parkFacilityID, t2.parkFacilityID)
	end	


GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 08/24/2012
-- Description:	recruitment -- get visiits selected/redeemed at least one
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 09/05/2012
-- Description:	recruitment -- changed entirely -- window set to 7 days, based off program start date
-- Update Version: 1.3.1.0013
-- Author:		James Francis
-- Create date: 09/06/2012
-- Description:	taptime utc fix
-- Update Version: 1.3.1.0014
-- Author:		Amar Terzic
-- Create date: 11/05/2012
-- Description:	Multi-park support and guest filter 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitGetVisits] 
@strStartDate varchar(25) = NULL,
@strEndDate varchar(25) = NULL,
@parkID bigint = NULL 
AS
-- left interface the same for now
DECLARE @Selected int, @Redeemed int, @dayStart datetime, @dayEnd datetime
DECLARE @parkIDs table (parkID bigint)

set @dayStart=convert(datetime,
    (
        select LEFT([value] , 10) 
        FROM [dbo].[config] 
        WHERE [property] = 'DATA_START_DATE' and [class] ='XiConfig'
    )
)

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID

set @dayEnd=DATEADD(DD, 5, @dayStart)
set @dayEnd=dateadd(hour, 23, @dayEnd )
set @dayEnd=dateadd(minute, 59, @dayEnd)
set @dayEnd=dateadd(second, 59, @dayEnd)

select Ddate=isnull(t3.Ddate, t2.Ddate), Selections=isnull(t3.GuestCount,0), Redemptions=isnull(t2.GuestCount,0)
FROM
(
select Ddate=t1.DDate, GuestCount=COUNT(distinct(t1.GuestID))
FROM (
    select DDate=convert(date, DATEADD(HH, -4, r1.[TapDate])), GuestID=b1.guestID 
	from gxp.RedemptionEvent r1 (nolock)
	join gxp.RedemptionEvent r2 (nolock) on r1.CacheXpassAppointmentID = r2.CacheXpassAppointmentID
		and r1.AppointmentStatusID = 1 and r2.AppointmentStatusID = 2
	join gxp.AppointmentReason a1 (nolock) on a1.AppointmentReasonID = r1.AppointmentReasonID
	join gxp.AppointmentReason a2 (nolock) on a2.AppointmentReasonID = r2.AppointmentReasonID
	join gxp.BusinessEvent b1 (nolock) on b1.BusinessEventID = r1.RedemptionEventID
	join gxp.BusinessEvent b2 (nolock) on b1.ReferenceID = b2.ReferenceID and dateadd(HH,-4,b2.StartTime) between @dayStart and @dayEnd
	join gxp.BusinessEventType bt (nolock) on b2.BusinessEventTypeID = bt.BusinessEventTypeID and BusinessEventType = 'BOOK'
	JOIN [dbo].xiFacilities x (nolock) on x.facilityId = r1.FacilityID
	LEFT JOIN guestFilter g (nolock) ON b1.GuestID = g.GuestID
	where dateadd(hh,-4,r1.tapdate) between @dayStart and @dayEnd 
	and x.parkFacilityID in (select parkID from @parkIDs)
	and g.guestID is NULL
	group by convert(date, DATEADD(HH, -4, r1.[TapDate])), b1.guestID
) as t1
group by t1.DDate
) as t2
full join
(
    select Ddate=convert(date, dateadd(HH, -4, b.StartTime)), GuestCount=count(distinct(b.guestID))
    from GXP.BusinessEvent b (nolock)
    JOIN [GXP].[BusinessEventType] bet WITH(nolock)ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
    JOIN [dbo].xiFacilities x (nolock) on x.facilityId = b.EntertainmentID
	LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
    left join 
		(    -- minus all CHANGE/CANCELS
			select b.GuestID, ReferenceID
				from gxp.BusinessEvent b (nolock)
				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
					and BusinessEventType = 'CHANGE'
				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
					and BusinessEventSubType = 'CANCEL'
	) as ti2 on b.GuestID = ti2.GuestID and b.ReferenceID = ti2.ReferenceID
    where bet.BusinessEventType = 'BOOK' 
    and dateadd(HH, -4, b.StartTime) between @dayStart and @dayEnd
    and g.guestID is NULL
 	and ti2.ReferenceID is NULL 
 	and x.parkFacilityID in (select parkID from @parkIDs)
    group by convert(date, dateadd(HH, -4, b.StartTime))
)as t3 on t2.Ddate = t3.Ddate
group by isnull(t3.Ddate, t2.Ddate), t2.GuestCount, t3.GuestCount

GO

-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	get projected data
-- Update Version: 1.3.1.0011
-- Author:		James Francis
-- Create date: 08/20/2012
-- Description:	cancel filtered out
-- Update Version: 1.3.1.0015
-- Author:		Amar Terzic
-- Create date: 11/05/2012
-- Description:	Multi-park support 
-- Update Version: 1.5.0.0003
-- =============================================
CREATE PROCEDURE [dbo].[usp_RecruitTotalRecruited]
@strUseDate varchar(25) = NULL,
@parkID bigint = NULL 
AS

DECLARE 
@currentTime datetime, @Recruited int, @Target int,
@startDateStr varchar(30), @dayStart datetime, @dayEnd datetime
DECLARE @parkIDs table (parkID bigint)

IF @strUseDate is NULL
BEGIN
    SET @currentTime = convert(datetime,(select LEFT(convert(varchar, getdate(), 121), 10)))
END 
ELSE
    SET @currentTime=convert(datetime, @strUseDate) 

SELECT @startDateStr=[value] FROM [dbo].[config] WHERE [property] = 'DATA_START_DATE' and [class] = 'XiConfig'
set @dayStart=convert(datetime,(select LEFT(@startDateStr, 10)));

if @parkID is NULL
BEGIN
insert @parkIDs 
select distinct parkFacilityID from  xiFacilities
END
ELSE
insert @parkIDs
select @parkID


set @dayEnd=DATEADD(DD, 7, @dayStart);
set @dayEnd=DATEADD(hour, 23, @dayEnd );
set @dayEnd=DATEADD(minute, 59, @dayEnd);
set @dayEnd=DATEADD(second, 59, @dayEnd);

-- don't need this -- can add up engaged numbers in client
--
--select @Recruited = count(distinct(b.GuestID))
--	from GXP.BusinessEvent(nolock) as b 
--	JOIN [GXP].[BusinessEventType] bet (nolock) ON b.BusinessEventTypeId= bet.BusinessEventTypeId 
----	JOIN [dbo].xiFacilities (nolock) x on x.facilityId = b.EntertainmentID
----	JOIN @parkIDs p1 on p1.parkID = x.parkFacilityID
--	LEFT JOIN guestFilter g (nolock) ON b.GuestID = g.GuestID
--	LEFT JOIN 
--		(    -- minus all CHANGE/CANCELS
--			select b.GuestID, ReferenceID
--				from gxp.BusinessEvent b (nolock)
--				join gxp.BusinessEventType b1 (nolock) on b.BusinessEventTypeId= b1.BusinessEventTypeId 
--					and BusinessEventType = 'CHANGE'
--				join gxp.BusinessEventSubType b2 (nolock) on b.BusinessEventSubTypeID= b2.BusinessEventSubTypeID 
--			) as t2 on b.GuestID = t2.GuestID and b.ReferenceID = t2.ReferenceID
--	where bet.BusinessEventType = 'BOOK' 
--	and b.StartTime between DATEADD(HH, 4, @dayStart) and DATEADD(HH, 4, @dayEnd)
--	and g.guestID is NULL
--	and t2.ReferenceID is NULL
----	and x.parkFacilityID in  (select parkFacilityID  from parkIDs)--(80007798, 80007944, 80007998)--(select distinct parkID from @parkIDs)

SELECT @Target=[value]
FROM [dbo].[config]
WHERE [property] = 'RECRUIT_TARGET' and [class] = 'XiConfig'

-- recruited
-- target 
-- eligible
--select @Recruited, @Target
select 0, @Target
GO

-- =============================================
-- Author:		Ted Crane
-- Create date: 07/19/2012
-- Description:	Creates a Redemption Event.
-- Version: 1.3.0.0007
-- Author:		Amar Terzic
-- Create date: 11/02/2012
-- Description:	Multi-park support 
-- Update Version: 1.4.1.0001
-- =============================================
CREATE PROCEDURE [gxp].[usp_RedemptionEvent_Create] 
	@Location nvarchar(50),
	@BusinessEventType nvarchar(50),
	@BusinessEventSubType nvarchar(50),
	@ReferenceID nvarchar(50),
	@GuestID bigint,
	@Timestamp nvarchar(50),
	@CorrelationID nvarchar(50),
	@FacilityName nvarchar(50),
	@AppointmentReason nvarchar(50),
	@AppointmentStatus nvarchar(50),
	@AppointmentID bigint,
	@CacheXpassAppointmentID bigint,
	@TapDate nvarchar(50)	
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	BEGIN TRY
	
		BEGIN TRANSACTION
		
		DECLARE @BusinessEventId int
		DECLARE @AppointmentReasonID int
		DECLARE @AppointmentStatusID int
		DECLARE @FacilityID int		
		
		--Should make this a stored procedure
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

		SELECT	 @AppointmentReasonID = [AppointmentReasonID] 
		FROM	[gxp].[AppointmentReason] 
		WHERE	[AppointmentReason] = @AppointmentReason
				
		IF  @AppointmentReasonID IS NULL
		BEGIN
			INSERT INTO [gxp].[AppointmentReason]
				   ([AppointmentReason])
			VALUES 
					(@AppointmentReason)

			SET @AppointmentReasonID = @@IDENTITY
		END

		SELECT	 @AppointmentStatusID = [AppointmentStatusID] 
		FROM	[gxp].[AppointmentStatus] 
		WHERE	[AppointmentStatus] = @AppointmentStatus
				
		IF  @AppointmentStatusID IS NULL
		BEGIN
			INSERT INTO [gxp].[AppointmentStatus]
				   ([AppointmentStatus])
			VALUES 
					(@AppointmentStatus)

			SET @AppointmentStatusID = @@IDENTITY
		END
		
		EXECUTE [gxp].[usp_BusinessEvent_Create] 
		   @Location = @Location
		  ,@BusinessEventType = @BusinessEventType
		  ,@BusinessEventSubType = @BusinessEventSubType
		  ,@ReferenceID = @ReferenceID
		  ,@GuestID = @GuestID
		  ,@GuestIdentifier = NULL -- This is secure ID of the band, don't store.
		  ,@Timestamp = @Timestamp
		  ,@CorrelationID = @CorrelationID
		  ,@BusinessEventId = @BusinessEventId OUTPUT

		INSERT INTO [gxp].[RedemptionEvent]
           ([RedemptionEventID]
           ,[AppointmentStatusID]
           ,[AppointmentReasonID]
           ,[FacilityID]
           ,[AppointmentID]
           ,[CacheXpassAppointmentID]
           ,[TapDate])
		VALUES
		   (@BusinessEventID
		   ,@AppointmentStatusID
		   ,@AppointmentReasonID
		   ,@FacilityName
		   ,@AppointmentID
		   ,@CacheXpassAppointmentID
		   ,CONVERT(datetime,@TapDate,126))

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
		ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END

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
                           ,'xbrms-' + $(updateversion) + '.sql'
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
