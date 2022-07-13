-- =============================================
-- Author:		James Francis
-- Create date: 08/20/2011
-- Description:	Get early/late overrides for calendar
-- Update Version: 1.3.1.0001
-- Author:		James Francis
-- Create date: 09/18/2012
-- Description:	zeroed out overrides
-- Update Version: 1.3.1.0018
-- =============================================
CREATE PROCEDURE [dbo].[usp_GetRedeemedOverridesForCal]
@number_days int = 7,
@strCutOffDate varchar(25) = NULL
AS
DECLARE @Selected int, @Redeemed int,           
    @starttime datetime, @endtime datetime, @EOD_datetime datetime,
    @currentDateTime datetime, @currentTime datetime, @EODTime datetime;
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


    select [Date] = t.dt, RedeemedOverrides = 0--ISNULL(RedeemedOverrides,0)
    from [dbo].[DAYS_OF_YEAR] t
    --LEFT JOIN (
    --    select CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME) as [TapTime], count(*) as RedeemedOverrides
    --    from 
    --        gxp.BlueLaneEvent bl
	   --     JOIN [gxp].[BusinessEvent] be WITH(NOLOCK) ON be.[BusinessEventID] = bl.BlueLaneEventID
	   --     JOIN gxp.ReasonCode rc WITH(NOLOCK) ON rc.ReasonCodeId = bl.ReasonCodeId 
    --        JOIN [RDR].[Guest] g WITH(nolock) ON be.GuestID = g.GuestID
    --    where 
    --        (rc.ReasonCode = 'Early' or rc.ReasonCode = 'Late')
    --        and bl.taptime between @starttime and @endtime
    --        and g.GuestType = 'Guest'
    --    GROUP BY  CAST(CONVERT(CHAR(10),bl.taptime,102) AS DATETIME)
    --) as t2 on t.dt = t2.[TapTime]
    where t.dt between @starttime and @endtime
    order by t.dt desc
END
