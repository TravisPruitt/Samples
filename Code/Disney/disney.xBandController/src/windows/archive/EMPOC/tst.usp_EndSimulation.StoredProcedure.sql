/****** Object:  StoredProcedure [tst].[usp_EndSimulation]    Script Date: 08/31/2011 14:16:47 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 07/25/2011
-- Description:	Ends a simulation of users through an attraction.
-- =============================================
CREATE PROCEDURE [tst].[usp_EndSimulation] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	UPDATE [tst].[Configuration]
	SET [IsExecuting] = 0
	WHERE [IsExecuting] = 1

END
GO
