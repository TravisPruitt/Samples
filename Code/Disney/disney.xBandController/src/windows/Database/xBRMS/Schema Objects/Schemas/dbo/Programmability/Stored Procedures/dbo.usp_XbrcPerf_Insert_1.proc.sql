-- =============================================
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
