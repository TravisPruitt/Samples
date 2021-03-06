USE [Simulator]
GO
/****** Object:  UserDefinedFunction [sim].[ufn_CalculateSignalStrengh]    Script Date: 12/04/2011 16:56:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/24/2011
-- Description:	Calculate Signal Strength
-- =============================================
CREATE FUNCTION [sim].[ufn_CalculateSignalStrengh] 
(
	@SignalStrengthDelta decimal(18,2),
	@GuestCount int,
	@MovingTowards bit
)
RETURNS int
AS
BEGIN
	DECLARE @Result decimal(18,2)
	
	SET @Result = (@SignalStrengthDelta * @GuestCount) - 90.0;
	
	IF @MovingTowards = 0
	BEGIN
		SET @Result = (40.0 + (@SignalStrengthDelta* @GuestCount)) * -1.0;
	END

    --Clamp signal strength between -40 and -90 Db
    IF @Result > -40.0
    BEGIN
        SET @Result = -40.0;
    END
    else if @Result < -90.0
    BEGIN
        SET @Result = -90.0;
    END
    
    RETURN CAST(@Result as int)

END
GO
