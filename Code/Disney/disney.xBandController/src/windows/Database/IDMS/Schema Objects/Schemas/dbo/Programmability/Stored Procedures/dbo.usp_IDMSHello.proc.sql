-- =============================================
-- Author:		Robert
-- Create date: 3/21/2012
-- Description:	A Ping Hello
-- =============================================
CREATE PROCEDURE [dbo].[usp_IDMSHello] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	SELECT N'HELLO';
END
