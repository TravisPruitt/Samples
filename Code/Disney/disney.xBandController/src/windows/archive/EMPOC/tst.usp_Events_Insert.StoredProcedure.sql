/****** Object:  StoredProcedure [tst].[usp_Events_Insert]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Inserts Events
-- =============================================
CREATE PROCEDURE [tst].[usp_Events_Insert] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	EXEC [tst].[usp_EntryEvents_Insert]
	   
	EXEC [tst].[usp_InQueueEvents_Insert]
	
	EXEC [tst].[usp_MergeEvents_Insert]

	EXEC [tst].[usp_LoadEvents_Insert]

	EXEC [tst].[usp_ExitEvents_Insert]

	EXEC [tst].[usp_Metrics_Insert]
	
	EXEC [tst].[usp_Simulation_Update]

END
GO
