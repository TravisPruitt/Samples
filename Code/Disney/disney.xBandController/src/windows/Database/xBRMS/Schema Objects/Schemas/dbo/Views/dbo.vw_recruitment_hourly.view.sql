CREATE VIEW [dbo].[vw_recruitment_hourly]
AS
SELECT CONVERT(NVARCHAR(20),DATEADD(HOUR,-4,
	 DATEADD(MINUTE,-DATEPART(MINUTE, be.[CreatedDate]),
	DATEADD(SECOND,-DATEPART(SECOND, be.[CreatedDate]),
	DATEADD(Millisecond,-DATEPART(MILLISECOND, be.[CreatedDate]),be.[CreatedDate]))))) [RecruitmentDate]
	,COUNT(DISTINCT be.[GuestID]) [Guests]
  FROM [gxp].[BusinessEvent] be WITH(NOLOCK)
  JOIN [gxp].[BusinessEventType] bet WITH(NOLOCK) ON bet.BusinessEventTypeID = be.[BusinessEventTypeID]
  JOIN [rdr].[Guest] g WITH(NOLOCK) on g.[GuestID] = be.[GuestID]
  WHERE CONVERT(date,be.[StartTime]) BETWEEN '2012-09-18' AND '2012-09-24'
  AND bet.[BusinessEventType] = 'BOOK'
  AND NOT EXISTS
  (SELECT 'X'
   FROM [gxp].[BusinessEvent] be1 WITH(NOLOCK) 
   JOIN [gxp].[BusinessEventType] bet1 WITH(NOLOCK) 
	ON bet1.BusinessEventTypeID = be1.[BusinessEventTypeID]
   JOIN [gxp].[BusinessEventSubType] best1 WITH(NOLOCK) 
	ON best1.BusinessEventSubTypeID = be1.[BusinessEventSubTypeID]
   WHERE be1.ReferenceID = be.[ReferenceID]
   AND be1.[BusinessEventID] <> be.[BusinessEventID]
   AND bet1.[BusinessEventType] = 'CHANGE'
   AND best1.[BusinessEventSubType] = 'CANCEL')
  GROUP BY 	DATEADD(HOUR,-4,
	DATEADD(MINUTE,-DATEPART(MINUTE, be.[CreatedDate]),
	DATEADD(SECOND,-DATEPART(SECOND, be.[CreatedDate]),
	DATEADD(Millisecond,-DATEPART(MILLISECOND, be.[CreatedDate]),be.[CreatedDate]))))