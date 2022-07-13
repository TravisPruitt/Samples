USE [msdb]
GO

/****** Object:  Job [Nightly XI Maintenance DBDASH]    Script Date: 07/19/2013 12:33:09 ******/
BEGIN TRANSACTION
DECLARE @ReturnCode INT
SELECT @ReturnCode = 0
/****** Object:  JobCategory [[Uncategorized (Local)]]]    Script Date: 07/19/2013 12:33:10 ******/
IF NOT EXISTS (SELECT name FROM msdb.dbo.syscategories WHERE name=N'[Uncategorized (Local)]' AND category_class=1)
BEGIN
EXEC @ReturnCode = msdb.dbo.sp_add_category @class=N'JOB', @type=N'LOCAL', @name=N'[Uncategorized (Local)]'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback

END

DECLARE @jobId BINARY(16)
EXEC @ReturnCode =  msdb.dbo.sp_add_job @job_name=N'Nightly XI Maintenance DBDASH', 
		@enabled=1, 
		@notify_level_eventlog=0, 
		@notify_level_email=0, 
		@notify_level_netsend=0, 
		@notify_level_page=0, 
		@delete_level=0, 
		@description=N'No description available.', 
		@category_name=N'[Uncategorized (Local)]', 
		@owner_login_name=N'SYNAPSE', @job_id = @jobId OUTPUT
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [set the maintenance flag]    Script Date: 07/19/2013 12:33:11 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'set the maintenance flag', 
		@step_id=1, 
		@cmdexec_success_code=0, 
		@on_success_action=4, 
		@on_success_step_id=2, 
		@on_fail_action=4, 
		@on_fail_step_id=5, 
		@retry_attempts=1, 
		@retry_interval=1, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'declare @UTCDefaultOffset int, @cuttofdate date, @cutofftime datetime

--standard UTC offset
select @cutofftime = GETUTCDATE()
select @UTCDefaultOffset = [dbo].[fn_DaylightSavingsOffset] (getdate()-1)
select @cuttofdate = convert(date,getdate()-1)

--temporary stop ETL - insert row to control table

insert syncControl (TimeStarted, Timestamp, Status, JobTypeID)
select getutcdate(), @cutofftime, 2, 2
', 
		@database_name=N'DBDASH', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [archive previous day data and update statistics]    Script Date: 07/19/2013 12:33:12 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'archive previous day data and update statistics', 
		@step_id=2, 
		@cmdexec_success_code=0, 
		@on_success_action=4, 
		@on_success_step_id=3, 
		@on_fail_action=4, 
		@on_fail_step_id=5, 
		@retry_attempts=2, 
		@retry_interval=5, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'
--standard UTC offset
declare @cuttofdate date
declare @starttime datetime
declare @cutofftime datetime
declare @UTCDefaultOffset int
declare @UTCDLSOffset int
declare @UTCCurrentOffset int
declare @DLSStart smalldatetime
declare @DLSEnd smalldatetime
declare @Offset bigint
declare @controlOffset bigint

set @cutofftime = getutcdate()


--standard UTC offset
select top 1 @UTCDefaultOffset = UTCDefaultOffset 
	from xiFacilities (nolock)
	where parkFacilityID in (80007944)
	
--UTC offset with dajlight savings
set @UTCDLSOffset = @UTCDefaultOffset+1

set @DLSStart = (select dbo.fn_GetDaylightSavingsTimeStart(DATEADD(HH,@UTCDefaultOffset,@cutofftime)))
set @DLSEnd = (select  dbo.fn_GetDaylightSavingsTimeEnd(DATEADD(HH,@UTCDefaultOffset,@cutofftime)))

select @UTCCurrentOffset = 
	case 
		when DATEADD(HH,@UTCDefaultOffset,@cutofftime) between @DLSStart and @DLSEnd then @UTCDLSOffset
		else @UTCDefaultOffset
	end 


select @cuttofdate = convert(date,getdate()-1)


--remove canceled from main cache
delete --select *
from gxp.businesseventPendingNext
	where referenceID in (select referenceID from vw_canceledEntitlements)

--archive expired from working cache
insert gxp.businesseventPendingExpired
select * 
	from gxp.businesseventPending 
	where startTime < dateadd(HOUR, -1*@UTCCurrentOffset, convert(datetime, convert(date,getutcdate())))

--flush expired from working cache
delete --select *
from gxp.businesseventPending 
	where startTime < dateadd(HOUR, -1*@UTCCurrentOffset, convert(datetime, convert(date,getutcdate())))

--insert next 24 hours to working cache from main cache	
insert  gxp.businesseventPending 
select * 
	from gxp.businesseventPendingNext
	where startTime between dateadd(HOUR, -1*@UTCCurrentOffset, convert(datetime, convert(date,getutcdate()))) and dateadd(HOUR, 30, convert(datetime, convert(date,getutcdate())))
	and businesseventID not in (select businesseventID from gxp.businesseventPending)
	
--flush expired from main cache
delete 	--select *
from gxp.businesseventPendingNext 
	where startTime < dateadd(HOUR, -1*@UTCCurrentOffset, convert(datetime, convert(date,getutcdate())))





--ETL tables

MERGE EntitlementMasterHistory as TARGET
USING (select * from EntitlementMaster where EntitlementDate <= @cuttofdate) AS SOURCE
on (TARGET.BusinessEventID = SOURCE.BusinessEventID)
WHEN MATCHED 
	THEN UPDATE 
	SET --TARGET.BusinessEventID = SOURCE.BusinessEventID,
	TARGET.ReferenceID = SOURCE.ReferenceID
	,TARGET.GuestID = SOURCE.GuestID
	,TARGET.EntertainmentID = SOURCE.EntertainmentID
	,TARGET.ParentLocationID = SOURCE.ParentLocationID
	,TARGET.EntitlementHour = SOURCE.EntitlementHour
	,TARGET.EntitlementDate = SOURCE.EntitlementDate
	,TARGET.EntitlementStartDateTimeUTC = SOURCE.EntitlementStartDateTimeUTC
	,TARGET.EntitlementEndDateTimeUTC = SOURCE.EntitlementEndDateTimeUTC
	,TARGET.EntitlementCreationDate = SOURCE.EntitlementCreationDate
	,TARGET.OfferSet= SOURCE.OfferSet
	,TARGET.Status = SOURCE.Status
	,TARGET.CreatedDateUTC = SOURCE.CreatedDateUTC
	,TARGET.UpdatedDateUTC = SOURCE.CreatedDateUTC
	,TARGET.SelectedHour = SOURCE.SelectedHour  
	WHEN NOT MATCHED BY TARGET
	THEN INSERT (BusinessEventID,ReferenceID,GuestID,--LocationID,
	EntertainmentID,ParentLocationID, EntitlementHour,EntitlementDate,EntitlementStartDateTimeUTC,
	EntitlementEndDateTimeUTC,EntitlementCreationDate,OfferSet,Status,CreatedDateUTC,UpdatedDateUTC,SelectedHour)
	VALUES(SOURCE.BusinessEventID,SOURCE.ReferenceID,SOURCE.GuestID,--SOURCE.LocationID,
	SOURCE.EntertainmentID,SOURCE.ParentLocationID,SOURCE.EntitlementHour,SOURCE.EntitlementDate,
	SOURCE.EntitlementStartDateTimeUTC,SOURCE.EntitlementEndDateTimeUTC,SOURCE.EntitlementCreationDate,
	SOURCE.OfferSet,SOURCE.Status,SOURCE.CreatedDateUTC,SOURCE.UpdatedDateUTC,SOURCE.SelectedHour);


MERGE RedemptionMasterHistory as TARGET
USING (select * from RedemptionMaster where convert(date,RedemptionTime) <= @cuttofdate) AS SOURCE
on (TARGET.RedemptionEventID = SOURCE.RedemptionEventID)
WHEN MATCHED 
	THEN UPDATE 
	SET 
	TARGET.GuestID = SOURCE.GuestID
	,TARGET.ReferenceID = SOURCE.ReferenceID
	,TARGET.FacilityID = SOURCE.FacilityID
	,TARGET.ParentLocationID = SOURCE.ParentLocationID
	,TARGET.ValidationReason = SOURCE.ValidationReason
	,TARGET.RedemptionReason = SOURCE.RedemptionReason
	,TARGET.ValidationTime = SOURCE.ValidationTime
	,TARGET.RedemptionTime = SOURCE.RedemptionTime
	,TARGET.CreatedDate = SOURCE.CreatedDate
	,TARGET.UpdatedDate = SOURCE.UpdatedDate 
WHEN NOT MATCHED BY TARGET
	THEN INSERT (RedemptionEventID,GuestID,ReferenceID,FacilityID,ParentLocationID,
	ValidationReason,RedemptionReason,ValidationTime,RedemptionTime,CreatedDate,UpdatedDate)
	VALUES(SOURCE.RedemptionEventID,SOURCE.GuestID,SOURCE.ReferenceID,SOURCE.FacilityID,SOURCE.ParentLocationID,
	SOURCE.ValidationReason,SOURCE.RedemptionReason,SOURCE.ValidationTime,SOURCE.RedemptionTime,
	SOURCE.CreatedDate,SOURCE.UpdatedDate);


MERGE BlueLaneMasterHistory as TARGET
USING (select * from BlueLaneMaster where convert(date,BlueLineTime) <= @cuttofdate)AS SOURCE
on (TARGET.BlueLaneEventID = SOURCE.BlueLaneEventID)
WHEN MATCHED 
	THEN UPDATE 
	SET 
	TARGET.GuestID = SOURCE.GuestID
	,TARGET.FacilityID = SOURCE.FacilityID
	,TARGET.ParentLocationID = SOURCE.ParentLocationID
	,TARGET.BlueLineCode = SOURCE.BlueLineCode
	,TARGET.BlueLineTime = SOURCE.BlueLineTime
	,TARGET.CreatedDate = SOURCE.CreatedDate
	,TARGET.UpdatedDate = SOURCE.UpdatedDate 	
WHEN NOT MATCHED BY TARGET
	THEN INSERT (BlueLaneEventID,GuestID,FacilityID,ParentLocationID,BlueLineCode,BlueLineTime,CreatedDate,UpdatedDate)
	VALUES(SOURCE.BlueLaneEventID,SOURCE.GuestID,SOURCE.FacilityID,
	SOURCE.ParentLocationID,SOURCE.BlueLineCode,SOURCE.BlueLineTime,
	SOURCE.CreatedDate,SOURCE.UpdatedDate);
	
	
MERGE ParkEntryMasterHistory as TARGET
USING (select * from ParkEntryMaster where EntryDate <= @cuttofdate) 
	AS SOURCE
on (TARGET.PublicID = SOURCE.PublicID
	AND TARGET.ParkEntryAttemptID = SOURCE.ParkEntryAttemptID
	AND TARGET.FacilityID = SOURCE.FacilityID
	AND TARGET.XbrcReferenceNo = SOURCE.XbrcReferenceNo)
WHEN MATCHED 
	THEN UPDATE 
	SET 
	TARGET.PublicID = SOURCE.PublicID
	,TARGET.FacilityID = SOURCE.FacilityID
	,TARGET.ParkEntryAttemptID = SOURCE.ParkEntryAttemptID
	,TARGET.EntryDate = SOURCE.EntryDate
	,TARGET.TappedTime = SOURCE.TappedTime
	,TARGET.HasEnteredTime = SOURCE.HasEnteredTime
	,TARGET.HasEnteredReason = SOURCE.HasEnteredReason
	,TARGET.BlueLaneTime = SOURCE.BlueLaneTime
	,TARGET.BlueLaneReason = SOURCE.BlueLaneReason
	,TARGET.AbandonedTime = SOURCE.AbandonedTime
	,TARGET.AbandonedReason = SOURCE.AbandonedReason
	,TARGET.ReaderName = SOURCE.ReaderName
	,TARGET.ReaderLocation = SOURCE.ReaderLocation
	,TARGET.ReaderSection = SOURCE.ReaderSection
	,TARGET.ReaderDeviceID = SOURCE.ReaderDeviceID
	,TARGET.XbrcReferenceNo = SOURCE.XbrcReferenceNo
	,TARGET.UpdatedTime = SOURCE.UpdatedTime		
WHEN NOT MATCHED BY TARGET
	THEN INSERT (PublicID ,FacilityID ,ParkEntryAttemptID ,EntryDate, TappedTime,HasEnteredTime,HasEnteredReason,BlueLaneTime,BlueLaneReason,
		AbandonedTime,AbandonedReason,ReaderName,ReaderLocation,ReaderSection ,ReaderDeviceID ,XbrcReferenceNo,UpdatedTime)
	VALUES(SOURCE.PublicID ,SOURCE.FacilityID ,SOURCE.ParkEntryAttemptID ,SOURCE.EntryDate, SOURCE.TappedTime, SOURCE.HasEnteredTime, 
	SOURCE.HasEnteredReason , SOURCE.BlueLaneTime , SOURCE.BlueLaneReason , SOURCE.AbandonedTime , SOURCE.AbandonedReason,
	SOURCE.ReaderName,SOURCE.ReaderLocation,SOURCE.ReaderSection ,SOURCE.ReaderDeviceID ,SOURCE.XbrcReferenceNo, SOURCE.UpdatedTime);
		
/***********************************************************************************/
--archive old data
if (select COUNT(distinct t1.BusinessEventID)
	from EntitlementMasterHistory t1 (nolock)
	join EntitlementMaster t2 (nolock) on t1.BusinessEventID = t2.BusinessEventID and t1.EntitlementDate = t2.EntitlementDate
		and t2.EntitlementDate <= @cuttofdate)
	>=
	(select COUNT(distinct t1.BusinessEventID)
	from EntitlementMaster t1 (nolock) 
	where t1.EntitlementDate <= @cuttofdate)
BEGIN	
Print ''We Good!''
delete from EntitlementMaster 
	where EntitlementDate <= @cuttofdate 
END
ELSE
Print ''EntitlementMaster Archival Conflict!''

if (select COUNT(distinct t1.RedemptionEventID)
	from RedemptionMasterHistory t1 (nolock)
	join RedemptionMaster t2 (nolock) on t1.RedemptionEventID = t2.RedemptionEventID
		and convert(date,t2.RedemptionTime) <= @cuttofdate)
	>=
	(select COUNT(distinct t1.RedemptionEventID)
	from RedemptionMaster t1 (nolock) 
	where convert(date,t1.RedemptionTime) <= @cuttofdate)
BEGIN	
Print ''We Good!''
delete from RedemptionMaster
	where convert(date,RedemptionTime) <= @cuttofdate
END
ELSE
Print ''RedemptionMaster Archival Conflict!''

if (select COUNT(distinct t1.BlueLaneEventID)
	from BlueLaneMasterHistory t1 (nolock)
	join BlueLaneMaster t2 (nolock) on t1.BlueLaneEventID = t2.BlueLaneEventID
		and convert(date,t2.BlueLineTime) <= @cuttofdate)
	>=
	(select COUNT(distinct t1.BlueLaneEventID)
	from BlueLaneMaster t1 (nolock) 
	where convert(date,t1.BlueLineTime) <= @cuttofdate)
BEGIN	
Print ''We Good!''
delete from BlueLaneMaster 
	where convert(date,BlueLineTime) <= @cuttofdate
END
ELSE
Print ''BlueLaneMaster Archival Conflict!''

if (select COUNT(*)
	from ParkEntryMasterHistory t1 (nolock)
	join ParkEntryMaster t2 (nolock) on t1.PublicID = t2.PublicID
	AND t1.ParkEntryAttemptID = t2.ParkEntryAttemptID
	AND t1.FacilityID = t2.FacilityID
	AND t1.XbrcReferenceNo = t2.XbrcReferenceNo
	AND t1.EntryDate = t2.EntryDate
		and t2.EntryDate <= @cuttofdate)
	>=
	(select COUNT(*)
	from ParkEntryMaster t1 (nolock) 
	where EntryDate <= @cuttofdate)
BEGIN	
Print ''We Good!''
delete from ParkEntryMaster 
	where EntryDate <= @cuttofdate 
END
ELSE
Print ''ParkEntryMaster Archival Conflict!''

--remove canceled entitlemetns
delete from EntitlementMaster where Status = 0

--reindex and update statistics

DBCC DBREINDEX ("EntitlementMaster", " ", 70);
DBCC DBREINDEX ("RedemptionMaster", " ", 70);
DBCC DBREINDEX ("BlueLaneMaster", " ", 70);
DBCC DBREINDEX ("ParkEntryMaster", " ", 70);

update statistics EntitlementMaster;
update statistics RedemptionMaster;
update statistics BlueLaneMaster;
update statistics ParkEntryMaster;
/***********************************************************************************/

--JMS Listener Tables
--truncate table BusinessEventHistory
insert BusinessEventHistory
select * 
	from gxp.BusinessEvent (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,isnull(StartTime,''1900-01-01 0:00:00''))) <= @cuttofdate	

--truncate table RedemptionEventHistory	
insert RedemptionEventHistory
select * 
	from gxp.RedemptionEvent (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,TapDate)) <= @cuttofdate	

--truncate table BlueLaneEventHistory
insert BlueLaneEventHistory
select * 
	from gxp.BlueLaneEvent (nolock)
	where convert(date,TapTime) <= @cuttofdate
	
	
	
insert ParkEntryEventHistory(ParkEntryEventID
,EventTypeID
,FacilityID
,PublicId
,ParkEntryAttemptID
,ReaderName
,ReaderLocation
,ReaderSection
,ReaderDeviceId
,TimeStamp
,ReasonID
,XbrcReferenceNo
,Sequence
,CreatedDate
)
select *
	from rdr.ParkEntryEvent
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 	


--clear out archived events

--declare @UTCCurrentOffset int, @cuttofdate date

----standard UTC offset

if (select COUNT(distinct BlueLaneEventID)
	from BlueLaneEventHistory (nolock) 
	where convert(date,TapTime) = @cuttofdate)
	=
	(select COUNT(distinct BlueLaneEventID)
	from gxp.BlueLaneEvent (nolock)
	where convert(date,TapTime) = @cuttofdate)
BEGIN	
Print ''We Good!''
delete from gxp.BlueLaneEvent 
	where convert(date,TapTime) <= @cuttofdate
END
ELSE
Print ''BlueLaneEvent Archival Conflict!''	

if (select COUNT(distinct RedemptionEventID)
	from RedemptionEventHistory (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,TapDate)) = @cuttofdate	)
	=
	(select COUNT(distinct RedemptionEventID)
	from gxp.RedemptionEvent (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,TapDate)) = @cuttofdate	)
BEGIN	
Print ''We Good!''
delete from gxp.RedemptionEvent
where convert(date,dateadd(HH,@UTCCurrentOffset,TapDate)) <= @cuttofdate 
END
ELSE
Print ''RedemptionEvent Archival Conflict!''

if (select COUNT(distinct BusinessEventID)
	from BusinessEventHistory (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,isnull(StartTime,''1900-01-01 0:00:00''))) = @cuttofdate)
	=
	(select COUNT(distinct BusinessEventID)
	from gxp.BusinessEvent (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,isnull(StartTime,''1900-01-01 0:00:00''))) = @cuttofdate)
BEGIN	
Print ''We Good!''
delete from gxp.BusinessEvent
		where convert(date,dateadd(HH,@UTCCurrentOffset,isnull(StartTime,''1900-01-01 0:00:00''))) <= @cuttofdate		
		and BusinessEventID not in (select RedemptionEventID from gxp.RedemptionEvent)
		and BusinessEventID not in (select BlueLaneEventID from gxp.BlueLaneEvent)
		
END
ELSE
Print ''BusinessEvent Archival Conflict!''



if (select COUNT(distinct ParkEntryEventID)
	from ParkEntryEventHistory (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) = @cuttofdate	)
	>=
	(select COUNT(distinct ParkEntryEventID)
	from rdr.ParkEntryEvent (nolock)
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) = @cuttofdate	)
BEGIN	
Print ''We Good!''
delete from rdr.ParkEntryEvent
where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


	
END
ELSE
Print ''ParkEntryEvent Archival Conflict!''

--update attribute tables - either MERGE command or truncate/insert
/*
gxp.AppointmentReason
gxp.AppointmentStatus
gxp.BusinessEventSubType
gxp.BusinessEventType
gxp.ReasonCode
*/

--reindex and update statistics

DBCC DBREINDEX ("gxp.BusinessEvent", " ", 70);
DBCC DBREINDEX ("gxp.RedemptionEvent", " ", 70);
DBCC DBREINDEX ("gxp.BlueLaneEvent", " ", 70);
DBCC DBREINDEX ("rdr.ParkEntryEvent", " ", 70);


update statistics gxp.BusinessEvent;
update statistics gxp.RedemptionEvent;
update statistics gxp.BlueLaneEvent;
update statistics rdr.ParkEntryEvent;

/*********************************************************************************************/
/*********************************************************************************************/

--JMS Listener Landing Tables

--archive records

insert EventHistory (EventId
,GuestID
,RideNumber
,xPass
,FacilityID
,EventTypeID
,ReaderLocation
,Timestamp
,BandTypeID
,RawMessage
,CreatedDate
)
select *
	from rdr.Event 
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


insert ReaderEventHistory (EventId
,ReaderLocationID
,ReaderName
,ReaderID
,IsWearingPrimaryBand
,Confidence
)
select t1.*
	from rdr.ReaderEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 

insert AbandonEventHistory (EventId
,LastTransmit
)
select t1.*
	from rdr.AbandonEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


insert LoadEventHistory (EventId
,WaitTime
,MergeTime
,CarID
)
select t1.*
	from rdr.LoadEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


insert ExitEventHistory (EventId
,WaitTime
,MergeTime
,TotalTime
,CarID
)
select t1.*
	from rdr.ExitEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


insert InVehicleEventHistory (EventId
,VehicleId
,AttractionId
,LocationId
,Confidence
,Sequence
)
select t1.*
	from rdr.InVehicleEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


insert GuestStatesHistory (GuestID
,xPass
,FacilityID
,RideID_Entry
,EntryTime
,RideID_Abandon
,AbandonTime
,RideID_Merge
,MergeTime
,MergeWaitTimeInMinutes
,RideID_Load
,LoadTime
,LoadWaitTimeInMinutes
,RideID_VBA
,VBATime
,RideID_Exit
,ExitTime
,ExitWaitTimeInMinutes
,LastUpdateEventTypeID
,UpdatedTime
)

select *
	from GuestStates
	where convert(date,dateadd(HH,@UTCCurrentOffset,UpdatedTime)) <= @cuttofdate 

--remove records

delete t1
	from rdr.ReaderEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 

delete t1
	from rdr.AbandonEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 

delete t1
	from rdr.LoadEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 

delete t1
	from rdr.ExitEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 


delete t1
	from rdr.InVehicleEvent t1
	join rdr.Event t2 on t1.EventId = t2.EventId
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 

delete from rdr.Event 
	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 

delete from GuestStates
	where convert(date,dateadd(HH,@UTCCurrentOffset,UpdatedTime)) <= @cuttofdate 
	
	
--delete from rdr.ParkEntryEvent
--	where convert(date,dateadd(HH,@UTCCurrentOffset,TimeStamp)) <= @cuttofdate 	

truncate table rdr.Metric

DBCC DBREINDEX ("rdr.Event", " ", 70);
DBCC DBREINDEX ("rdr.ReaderEvent", " ", 70);
DBCC DBREINDEX ("rdr.LoadEvent", " ", 70);
DBCC DBREINDEX ("rdr.ExitEvent", " ", 70);
DBCC DBREINDEX ("rdr.InVehicleEvent", " ", 70);
DBCC DBREINDEX ("rdr.AbandonEvent", " ", 70);
DBCC DBREINDEX ("GuestStates", " ", 70);


update statistics rdr.Event
update statistics rdr.ReaderEvent
update statistics rdr.LoadEvent
update statistics rdr.ExitEvent
update statistics rdr.InVehicleEvent
update statistics rdr.AbandonEvent
update statistics GuestStates
update statistics gxp.businesseventPendingNext', 
		@database_name=N'DBDASH', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [refresh history tables]    Script Date: 07/19/2013 12:33:12 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'refresh history tables', 
		@step_id=3, 
		@cmdexec_success_code=0, 
		@on_success_action=4, 
		@on_success_step_id=4, 
		@on_fail_action=4, 
		@on_fail_step_id=5, 
		@retry_attempts=2, 
		@retry_interval=5, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'truncate table RedemptionsHistory30Days 
insert RedemptionsHistory30Days (MetricType,
EntitlementDate,
FacilityID,
ParkID,
SelectedHour,
Selected,
Redeemed,
OfferSet)

--redemptions w/o entitlement
select MetricType = ''RedNoEnt'', EntitlementDate = CONVERT(date,RedemptionTime), 
	FacilityID, t1.ParentLocationID,
	SelectedHour = DATEPART(HH,RedemptionTime),	
	Selected = 0,
	Redeemed = COUNT(distinct RedemptionEventID),
	OfferSet = 0
	from RedemptionMasterHistory t1 (nolock) 
	left join EntitlementMasterHistory t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where t2.ReferenceID is NULL
	and g1.guestID is NULL
	group by CONVERT(date,RedemptionTime), FacilityID, t1.ParentLocationID,
	DATEPART(HH,RedemptionTime)
	order by 1,2,3


insert RedemptionsHistory30Days(MetricType,
EntitlementDate,
FacilityID,
ParkID,
SelectedHour,
Selected,
Redeemed,
OfferSet)

--redemptions

select MetricType = ISNULL(t2.MetricType, t1.MetricType),
t1.EntitlementDate,
t1.FacilityID,
t1.ParkID,
t1.SelectedHour,
t1.Selected,
Redeemed = isnull(t2.Redeemed,0),
t1.OfferSet
from (
select  MetricType = ''Red'', EntitlementDate, 
	FacilityID = EntertainmentID,
	ParkID = ParentLocationID, 
	SelectedHour = case when LEN(EntitlementHour) = 4 then left(EntitlementHour,2)
		ELSE left(EntitlementHour,1)
		end,
	OfferSet,	
	Selected = COUNT(distinct BusinessEventID)
	from  EntitlementMasterHistory t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	--where EntitlementDate = convert(date,getdate())
	where  Status = 1
	and g1.guestID is NULL
	group by EntitlementDate, EntertainmentID, ParentLocationID, OfferSet,
	case when LEN(EntitlementHour) = 4 then left(EntitlementHour,2)
		ELSE left(EntitlementHour,1)
		end) as t1
left join (
select MetricType = case 
	when ValidationReason in (''ACS'',''SWP'',''OTH'',''OVR'') then ''RedOvr''
	else ''Red''
	end,
EntitlementDate, FacilityID = isnull(FacilityID, EnterTainmentID), 
	ParkID = isnull(t1.ParentLocationID, t2.ParentLocationID),
	SelectedHour = case when LEN(EntitlementHour) = 4 then left(EntitlementHour,2)
		ELSE left(EntitlementHour,1)
		end, 
	Redeemed = COUNT(distinct RedemptionEventID),
	OfferSet
	from  EntitlementMasterHistory t2 (nolock)
	join RedemptionMasterHistory t1 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	and Status = 1
	left join guestFilter g1 on g1.guestID = t2.GuestID
	where g1.guestID is NULL
	group by case 
	when ValidationReason in (''ACS'',''SWP'',''OTH'',''OVR'') then ''RedOvr''
	else ''Red''
	end,
	EntitlementDate, 
	isnull(FacilityID, EnterTainmentID), 
	isnull(t1.ParentLocationID, t2.ParentLocationID),
		case when LEN(EntitlementHour) = 4 then left(EntitlementHour,2)
		else left(EntitlementHour,1)
		end,
		OfferSet) as t2 on t1.ParkID = t2.ParkID
			and t1.EntitlementDate = t2.EntitlementDate
			and t1.FacilityID = t2.FacilityID
			and t1.SelectedHour = t2.SelectedHour
			and t1.OfferSet = t2.OfferSet
			

truncate table BlueLanesHistory30Days
insert BlueLanesHistory30Days
(MetricType,
BlueLineCode,
ReasonCodeID,
EntitlementDate,
FacilityID,
ParentLocationID,
SelectedHour,
Redeemed)
--blue lanes
select MetricType = ''BlueLane'', BlueLineCode, ReasonCodeID, EntitlementDate = CONVERT(date,BlueLineTime), 
	FacilityID, t1.ParentLocationID,
	SelectedHour = DATEPART(HH,BlueLineTime),	
	Redeemed = COUNT(distinct BlueLaneEventID)
	from BlueLaneMasterHistory t1 (nolock) 
	join gxp.ReasonCode t2 on t1.BlueLineCode = t2.ReasonCode
	left join guestFilter g1 on g1.guestID = t1.GuestID
	and g1.guestID is NULL
	group by CONVERT(date,BlueLineTime), FacilityID, t1.ParentLocationID,
	DATEPART(HH,BlueLineTime),BlueLineCode, ReasonCodeID
	order by 4,5
		
truncate table RecruitmentDetailHistory30Days
insert RecruitmentDetailHistory30Days(
MetricType,
EntitlementDate,
EntertainmentID,
ParentLocationID,
BookedGuests,
RedeemedGuests,
BookedEntitlements,
RedeemedEntitlements)
select MetricType = ''RecruitPreArival'', EntitlementCreationDate, FacilityID = 0, ParkID = t1.ParentLocationID, 
	BookedGuests = COUNT(distinct T1.GuestID),
	0,0,0
	from  EntitlementMasterHistory t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID	
	where Status = 1
	and g1.guestID is NULL
	group by EntitlementCreationDate, t1.ParentLocationID
	order by 1,2,3	
	
	
truncate table RecruitmentHistory30Days
insert RecruitmentHistory30Days(MetricType,
EntitlementDate,
FacilityID,
ParkID,
BookedGuests,
RedeemedGuests)
select t1.MetricType, t1.EntitlementDate, t1.EntertainmentID, t1.ParentLocationID, 
	BookedGuests, RedeemedGuests = ISNULL(RedeemedGuests,0)
from (
select  MetricType = ''Visits'', EntitlementDate, EntertainmentID = 0,
	t1.ParentLocationID, 
	BookedGuests = COUNT(distinct T1.GuestID)
	from  EntitlementMasterHistory t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where Status = 1
	and EntitlementDate > convert(date,getdate()-6)
	and g1.guestID is NULL
	group by EntitlementDate,
	t1.ParentLocationID) as t1
left join (
select EntitlementDate,	t1.ParentLocationID, 
	RedeemedGuests = COUNT(distinct T1.GuestID)
	from  EntitlementMasterHistory t1 (nolock) 
	join RedemptionMasterHistory t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where Status = 1
	and EntitlementDate > convert(date,getdate()-6)
	and g1.guestID is NULL
	group by EntitlementDate, 
	t1.ParentLocationID) as t2 
	on t1.EntitlementDate = t2.EntitlementDate
	and t1.ParentLocationID = t2.ParentLocationID
order by 1,2,3


insert RecruitmentHistory30Days(MetricType,
EntitlementDate,
FacilityID,
ParkID,
BookedGuests,
RedeemedGuests,
PreArrival)
select t1.MetricType, t1.EntitlementDate, t1.EntertainmentID, t1.ParentLocationID, 
	BookedGuests, RedeemedGuests = ISNULL(RedeemedGuests,0), t1.PreArrival
from (
select  MetricType = ''PreArrival'', EntitlementDate, EntertainmentID = 0,
	t1.ParentLocationID, 
	BookedGuests = COUNT(distinct T1.GuestID),
	PreArrival = DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)
	from  EntitlementMasterHistory t1 (nolock) 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where Status = 1
	and EntitlementDate > convert(date,getdate()-6)
	and g1.guestID is NULL
	group by EntitlementDate,
	t1.ParentLocationID,
	DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)) as t1
left join (
select EntitlementDate,	t1.ParentLocationID, 
	RedeemedGuests = COUNT(distinct T1.GuestID),
	PreArrival = DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)
	from  EntitlementMasterHistory t1 (nolock) 
	join RedemptionMasterHistory t2 (nolock) on t1.GuestID = t2.GuestID
	and t1.ReferenceID = t2.ReferenceID 
	left join guestFilter g1 on g1.guestID = t1.GuestID
	where Status = 1
	and EntitlementDate > convert(date,getdate()-6)
	and g1.guestID is NULL
	group by EntitlementDate, 
	t1.ParentLocationID,
	DATEDIFF(DD, EntitlementDate, EntitlementCreationDate)) as t2 
	on t1.EntitlementDate = t2.EntitlementDate
	and t1.ParentLocationID = t2.ParentLocationID
	and t1.PreArrival = t2.PreArrival 
order by 1,2,3


truncate table bubblesCache

insert bubblesCache
select GuestID = t.GuestID, 
	''M'' =case when t1.ParentLocationID is NULL then ''0'' else ''1'' end,
	''E'' = case when t2.ParentLocationID is NULL then ''0'' else ''1'' end,
	''H'' = case when t3.ParentLocationID is NULL then ''0'' else ''1'' end,
	''A'' = case when t4.ParentLocationID is NULL then ''0'' else ''1'' end
from (
select distinct GuestID
	from dbo.EntitlementMasterHistory
	where Status = 1
	and EntitlementDate >= CONVERT(date,getdate()-5)) as t
left join (	
select distinct ParentLocationID, GuestID
	from dbo.EntitlementMasterHistory
	where ParentLocationID = 80007944
	and Status = 1
	and EntitlementDate >= CONVERT(date,getdate()-5)) as t1 on t.GuestID = t1.GuestID
left join (
select distinct ParentLocationID, GuestID
	from dbo.EntitlementMasterHistory
	where ParentLocationID = 80007838 --Epcot
	and Status = 1
	and EntitlementDate >= CONVERT(date,getdate()-5)) as t2 on t.GuestID = t2.GuestID
left join (
select distinct ParentLocationID, GuestID
	from dbo.EntitlementMasterHistory
	where ParentLocationID = 80007998 --HS
	and Status = 1
	and EntitlementDate >= CONVERT(date,getdate()-5)) as t3 on t.GuestID = t3.GuestID
	left join (
select distinct ParentLocationID, GuestID
	from dbo.EntitlementMasterHistory
	where ParentLocationID = 80007823 --AK
	and Status = 1
	and EntitlementDate >= CONVERT(date,getdate()-5)) as t4 on t.GuestID = t4.GuestID
	
truncate table bubblesHistory30Days 	
insert bubblesHistory30Days (GuestID,
M,
E,
H,
A,
ParkVisitCode)	
select *, ParkVisitCode = convert(int,''1''+M+E+H+A) 
from bubblesCache


--sync table
insert syncControlHistory30Days
select * 
	from syncControl (nolock) 
	where Timestamp < convert(date, GETUTCDATE()-1)


delete 
	from syncControlHistory30Days
	where Timestamp < convert(date, GETUTCDATE()-30)

	
delete	
	from syncControl --(nolock) 
	where Timestamp < convert(date, GETUTCDATE()-1)
	and JobID <> 1
	', 
		@database_name=N'DBDASH', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [Generate Batch Files - 30 Days Plus]    Script Date: 07/19/2013 12:33:12 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'Generate Batch Files - 30 Days Plus', 
		@step_id=4, 
		@cmdexec_success_code=0, 
		@on_success_action=4, 
		@on_success_step_id=5, 
		@on_fail_action=4, 
		@on_fail_step_id=5, 
		@retry_attempts=1, 
		@retry_interval=5, 
		@os_run_priority=0, @subsystem=N'SSIS', 
		@command=N'/FILE "C:\Data\BatchJobSSIS\xiNightlyBatchProcess.dtsx" /CHECKPOINTING OFF /REPORTING E', 
		@database_name=N'master', 
		@flags=0, 
		@proxy_name=N'SSIS Proxy'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
/****** Object:  Step [remove the maintenance flag]    Script Date: 07/19/2013 12:33:12 ******/
EXEC @ReturnCode = msdb.dbo.sp_add_jobstep @job_id=@jobId, @step_name=N'remove the maintenance flag', 
		@step_id=5, 
		@cmdexec_success_code=0, 
		@on_success_action=1, 
		@on_success_step_id=0, 
		@on_fail_action=2, 
		@on_fail_step_id=0, 
		@retry_attempts=1, 
		@retry_interval=1, 
		@os_run_priority=0, @subsystem=N'TSQL', 
		@command=N'update syncControl
set TimeCompleted = getutcdate(), Status = 1
where JobID = (select isnull(max(JobID),0) from syncControl (nolock) where JobTypeID = 2)


', 
		@database_name=N'DBDASH', 
		@flags=0
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
EXEC @ReturnCode = msdb.dbo.sp_update_job @job_id = @jobId, @start_step_id = 1
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
EXEC @ReturnCode = msdb.dbo.sp_add_jobschedule @job_id=@jobId, @name=N'Nightly XI Job', 
		@enabled=1, 
		@freq_type=4, 
		@freq_interval=1, 
		@freq_subday_type=1, 
		@freq_subday_interval=0, 
		@freq_relative_interval=0, 
		@freq_recurrence_factor=0, 
		@active_start_date=20130628, 
		@active_end_date=99991231, 
		@active_start_time=13000, 
		@active_end_time=235959, 
		@schedule_uid=N'97febd29-d0f0-44bf-aa80-cffa954d8e64'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
EXEC @ReturnCode = msdb.dbo.sp_add_jobserver @job_id = @jobId, @server_name = N'(local)'
IF (@@ERROR <> 0 OR @ReturnCode <> 0) GOTO QuitWithRollback
COMMIT TRANSACTION
GOTO EndSave
QuitWithRollback:
    IF (@@TRANCOUNT > 0) ROLLBACK TRANSACTION
EndSave:

GO


