/****** Object:  StoredProcedure [tst].[usp_Events_Insert_wLoop]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Part of simulator, inserts events 
-- Modified By: Neal Oman - added wait loop and input parameter.
-- =============================================
Create PROCEDURE [tst].[usp_Events_Insert_wLoop] 
    @wait		   float = 5.0
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	declare @waitstr varchar(12)
	
while (EXISTS(select * from tst.Configuration as c where c.IsExecuting = 1)) 
begin

	EXEC [tst].[usp_Events_Insert]
	   
	
   select @waitstr = '00:00:0'+ CONVERT(varchar(10),@wait);
   WAITFOR DELAY @waitstr;

end

END
GO
