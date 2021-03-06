/****** Object:  StoredProcedure [tst].[usp_Simulation_Update]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/26/2011
-- Description:	Determines if Queue Simulation for a Configuration is completed
-- =============================================
CREATE PROCEDURE [tst].[usp_Simulation_Update] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	UPDATE [tst].[Configuration]
	SET		 [IsExecuting] = 0
			,[LastUpdated] = GETDATE()
	WHERE [IsExecuting] = 1
	AND NOT EXISTS
	(SELECT 'X'
	 FROM [tst].[Guest] g
	 WHERE g.[ConfigurationID] = [Configuration].[ConfigurationID]
	 AND DATEDIFF(SECOND,DATEADD(SECOND,g.[TotalTime],[Configuration].[StartTime]),GETDATE()) < 0)		
		
END
GO
