USE [master]
GO

IF  EXISTS (SELECT name FROM sys.databases WHERE name = N'$(databasename)')
DROP DATABASE [$(databasename)]
GO

CREATE DATABASE [$(databasename)] ON  PRIMARY 
( NAME = N'$(databasename)', FILENAME = N'C:\Data\$(databasename)\$(databasename).mdf' , SIZE = 1024MB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024MB )
 LOG ON 
( NAME = N'$(databasename)_log', FILENAME = N'C:\Data\$(databasename)\$(databasename)_1.ldf' , SIZE = 1024MB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
GO
ALTER DATABASE [$(databasename)] SET COMPATIBILITY_LEVEL = 100
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [$(databasename)].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [$(databasename)] SET ANSI_NULL_DEFAULT OFF
GO
ALTER DATABASE [$(databasename)] SET ANSI_NULLS OFF
GO
ALTER DATABASE [$(databasename)] SET ANSI_PADDING OFF
GO
ALTER DATABASE [$(databasename)] SET ANSI_WARNINGS OFF
GO
ALTER DATABASE [$(databasename)] SET ARITHABORT OFF
GO
ALTER DATABASE [$(databasename)] SET AUTO_CLOSE OFF
GO
ALTER DATABASE [$(databasename)] SET AUTO_CREATE_STATISTICS ON
GO
ALTER DATABASE [$(databasename)] SET AUTO_SHRINK OFF
GO
ALTER DATABASE [$(databasename)] SET AUTO_UPDATE_STATISTICS ON
GO
ALTER DATABASE [$(databasename)] SET CURSOR_CLOSE_ON_COMMIT OFF
GO
ALTER DATABASE [$(databasename)] SET CURSOR_DEFAULT  GLOBAL
GO
ALTER DATABASE [$(databasename)] SET CONCAT_NULL_YIELDS_NULL OFF
GO
ALTER DATABASE [$(databasename)] SET NUMERIC_ROUNDABORT OFF
GO
ALTER DATABASE [$(databasename)] SET QUOTED_IDENTIFIER OFF
GO
ALTER DATABASE [$(databasename)] SET RECURSIVE_TRIGGERS OFF
GO
ALTER DATABASE [$(databasename)] SET  DISABLE_BROKER
GO
ALTER DATABASE [$(databasename)] SET AUTO_UPDATE_STATISTICS_ASYNC OFF
GO
ALTER DATABASE [$(databasename)] SET DATE_CORRELATION_OPTIMIZATION OFF
GO
ALTER DATABASE [$(databasename)] SET TRUSTWORTHY OFF
GO
ALTER DATABASE [$(databasename)] SET ALLOW_SNAPSHOT_ISOLATION OFF
GO
ALTER DATABASE [$(databasename)] SET PARAMETERIZATION SIMPLE
GO
ALTER DATABASE [$(databasename)] SET READ_COMMITTED_SNAPSHOT OFF
GO
ALTER DATABASE [$(databasename)] SET HONOR_BROKER_PRIORITY OFF
GO
ALTER DATABASE [$(databasename)] SET  READ_WRITE
GO
ALTER DATABASE [$(databasename)] SET RECOVERY FULL
GO
ALTER DATABASE [$(databasename)] SET  MULTI_USER
GO
ALTER DATABASE [$(databasename)] SET PAGE_VERIFY CHECKSUM
GO
ALTER DATABASE [$(databasename)] SET DB_CHAINING OFF
GO
EXEC sys.sp_db_vardecimal_storage_format N'IDMS', N'ON'
GO
USE [$(databasename)]
GO
/****** Object:  User [EMUser]    Script Date: 03/02/2012 13:07:24 ******/
CREATE USER [EMUser] FOR LOGIN [EMUser] WITH DEFAULT_SCHEMA=[dbo]
GO
/****** Object:  Schema [stg]    Script Date: 03/02/2012 13:07:24 ******/
CREATE SCHEMA [stg] AUTHORIZATION [dbo]
GO
/****** Object:  Schema [demo]    Script Date: 03/02/2012 13:07:24 ******/
CREATE SCHEMA [demo] AUTHORIZATION [dbo]
GO
/****** Object:  Table [dbo].[IDMS_Type]    Script Date: 03/02/2012 13:07:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[IDMS_Type](
	[IDMSTypeId] [int] IDENTITY(1,1) NOT NULL,
	[IDMSTypeName] [nvarchar](50) NULL,
	[IDMSTypeValue] [nvarchar](50) NULL,
	[IDMSkey] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_IDMS_Type] PRIMARY KEY CLUSTERED 
(
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [demo].[demo]    Script Date: 03/02/2012 13:07:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [demo].[demo](
	[demoId] [int] IDENTITY(1,1) NOT NULL,
	[demoDescription] [nvarchar](50) NOT NULL,
	[demoOrder] [int] NOT NULL,
 CONSTRAINT [PK_Demo] PRIMARY KEY CLUSTERED 
(
	[demoId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY],
 CONSTRAINT [AK_demo] UNIQUE NONCLUSTERED 
(
	[demoDescription] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_scheduledItem]    Script Date: 03/02/2012 13:07:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_scheduledItem](
	[guest_scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[scheduledItemId] [bigint] NULL,
	[isOwner] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[guest_scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[xband]    Script Date: 03/02/2012 13:07:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[xband](
	[xbandId] [bigint] IDENTITY(1,1) NOT NULL,
	[bandId] [nvarchar](200) NULL,
	[longRangeId] [nvarchar](200) NULL,
	[tapId] [nvarchar](200) NULL,
	[secureId] [nvarchar](200) NULL,
	[UID] [nvarchar](200) NULL,
	[bandFriendlyName] [nvarchar](50) NULL,
	[printedName] [nvarchar](255) NULL,
	[active] [bit] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_xband] PRIMARY KEY CLUSTERED 
(
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_xband_bandid] ON [dbo].[xband] 
(
	[bandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_xband_longerangeid] ON [dbo].[xband] 
(
	[longRangeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_xband_tapid] ON [dbo].[xband] 
(
	[tapId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [dbo].[usp_RethrowError]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 01/25/2012
-- Description:	Rethrows an Error.
-- =============================================
CREATE PROCEDURE [dbo].[usp_RethrowError] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Return if there is no error information to retrieve.
    IF ERROR_NUMBER() IS NULL
        RETURN;

    DECLARE 
        @ErrorMessage    NVARCHAR(4000),
        @ErrorNumber     INT,
        @ErrorSeverity   INT,
        @ErrorState      INT,
        @ErrorLine       INT,
        @ErrorProcedure  NVARCHAR(200);

    -- Assign variables to error-handling functions that 
    -- capture information for RAISERROR.
    SELECT 
        @ErrorNumber = ERROR_NUMBER(),
        @ErrorSeverity = ERROR_SEVERITY(),
        @ErrorState = ERROR_STATE(),
        @ErrorLine = ERROR_LINE(),
        @ErrorProcedure = ISNULL(ERROR_PROCEDURE(), '-');

    -- Build the message string that will contain original
    -- error information.
    SELECT @ErrorMessage = 
        N'Error %d, Level %d, State %d, Procedure %s, Line %d, ' + 
            'Message: '+ ERROR_MESSAGE();

    -- Raise an error: msg_str parameter of RAISERROR will contain
    -- the original error information.
    RAISERROR 
        (
        @ErrorMessage, 
        @ErrorSeverity, 
        1,               
        @ErrorNumber,    -- parameter: original error number.
        @ErrorSeverity,  -- parameter: original error severity.
        @ErrorState,     -- parameter: original error state.
        @ErrorProcedure, -- parameter: original error procedure name.
        @ErrorLine       -- parameter: original error line number.
        );

END
GO
/****** Object:  Table [dbo].[scheduleItemDetail]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[scheduleItemDetail](
	[itemDetailId] [bigint] IDENTITY(1,1) NOT NULL,
	[scheduledItemId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduleItemDetail] PRIMARY KEY CLUSTERED 
(
	[itemDetailId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[scheduledItem]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[scheduledItem](
	[scheduledItemId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[externalId] [nvarchar](50) NULL,
	[IDMSTypeId] [int] NULL,
	[startDateTime] [datetime] NULL,
	[endDateTime] [datetime] NULL,
	[name] [nvarchar](200) NULL,
	[location] [nvarchar](200) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [nvarchar](200) NULL,
 CONSTRAINT [PK_scheduledItem] PRIMARY KEY CLUSTERED 
(
	[scheduledItemId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [demo].[usp_GetDemos]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 01/31/2012
-- Description:	Gets the demos.
-- =============================================
CREATE PROCEDURE [demo].[usp_GetDemos] 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		SELECT [demoId]
			  ,[demoDescription]
		  FROM [demo].[demo]
		  ORDER BY [demoOrder]

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  StoredProcedure [demo].[usp_ReorderDemo]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 02/01/2012
-- Description:	Reorders the demos inserting
--              the indicated demo in the order.
-- =============================================
CREATE PROCEDURE [demo].[usp_ReorderDemo] 
	@demoId int
	,@demoOrder int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY
	
		BEGIN TRANSACTION
		
		UPDATE [demo].[demo]
		SET		[demoOrder] = [demoOrder] + 1
		WHERE [demoOrder] >= @demoOrder

		UPDATE [demo].[demo]
		SET		[demoOrder] = @demoOrder
		WHERE [demoId] >= @demoId

		COMMIT TRANSACTION

	END TRY
	BEGIN CATCH
	   
	   ROLLBACK TRANSACTION
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  Table [dbo].[guest]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest](
	[guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[IDMSID] [uniqueidentifier] ROWGUIDCOL  NOT NULL,
	[IDMSTypeId] [int] NULL,
	[lastName] [nvarchar](200) NULL,
	[firstName] [nvarchar](200) NULL,
	[middleName] [nvarchar](200) NULL,
	[title] [nvarchar](50) NULL,
	[suffix] [nvarchar](50) NULL,
	[DOB] [date] NULL,
	[VisitCount] [int] NULL,
	[AvatarName] [nvarchar](50) NULL,
	[active] [bit] NULL,
	[emailAddress] [nvarchar](200) NULL,
	[parentEmail] [nvarchar](200) NULL,
	[countryCode] [nvarchar](3) NULL,
	[languageCode] [nvarchar](3) NULL,
	[gender] [nvarchar](1) NULL,
	[userName] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_emailAddress] ON [dbo].[guest] 
(
	[emailAddress] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_firstname] ON [dbo].[guest] 
(
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_lastname] ON [dbo].[guest] 
(
	[lastName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_lastname_firstname] ON [dbo].[guest] 
(
	[lastName] ASC,
	[firstName] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [demo].[usp_CreateDemo]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 01/31/2012
-- Description:	Create a demo record
-- =============================================
CREATE PROCEDURE [demo].[usp_CreateDemo] 
	@demoDescription nvarchar(50)
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	BEGIN TRY

		--Demo to end
		DECLARE @demoOrder int

		SELECT @demoOrder = ISNULL(MAX([demoOrder]),0) + 1
		FROM	[demo].[demo]

		INSERT INTO [demo].[demo]
				   ([demoDescription]
				   ,[demoOrder])
			 VALUES
				   (@demoDescription
				   ,@demoOrder)

	END TRY
	BEGIN CATCH
	   
        -- Call the procedure to raise the original error.
        EXEC usp_RethrowError;

	END CATCH	   

END
GO
/****** Object:  Table [dbo].[source_system_link]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[source_system_link](
	[sourceTypeId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[sourceSystemIdValue] [nvarchar](200) NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_source_type] PRIMARY KEY CLUSTERED 
(
	[sourceTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE UNIQUE NONCLUSTERED INDEX [AK_source_system_link] ON [dbo].[source_system_link] 
(
	[guestId] ASC,
	[IDMSTypeId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_source_system_link_sourceSystemIdValue] ON [dbo].[source_system_link] 
(
	[sourceSystemIdValue] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[party]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[party](
	[partyId] [bigint] IDENTITY(1,1) NOT NULL,
	[primaryGuestId] [bigint] NULL,
	[partyName] [nvarchar](200) NULL,
	[count] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party] PRIMARY KEY CLUSTERED 
(
	[partyId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [demo].[demo_guest]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [demo].[demo_guest](
	[guestId] [bigint] NOT NULL,
	[demoId] [int] NOT NULL,
	[xbandId] [bigint] NOT NULL,
 CONSTRAINT [PK_demo_guest] PRIMARY KEY CLUSTERED 
(
	[guestId] ASC,
	[demoId] ASC,
	[xbandId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_xband]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_xband](
	[guest_xband_id] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[xbandId] [bigint] NOT NULL,
	[createdBy] [nvarchar](200) NOT NULL,
	[createdDate] [datetime] NOT NULL,
	[updatedBy] [nvarchar](200) NOT NULL,
	[updatedDate] [datetime] NOT NULL,
	[active] [bit] NOT NULL,
 CONSTRAINT [PK_guest_xband] PRIMARY KEY CLUSTERED 
(
	[guest_xband_id] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
CREATE NONCLUSTERED INDEX [IX_guest_xband_guestid] ON [dbo].[guest_xband] 
(
	[guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_phone]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_phone](
	[guest_phoneId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[extension] [nvarchar](50) NULL,
	[phonenumber] [nvarchar](50) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_phone] PRIMARY KEY CLUSTERED 
(
	[guest_phoneId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[guest_address]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[guest_address](
	[guest_addressId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NOT NULL,
	[IDMStypeId] [int] NULL,
	[address1] [nvarchar](200) NULL,
	[address2] [nvarchar](200) NULL,
	[address3] [nvarchar](200) NULL,
	[city] [nvarchar](100) NULL,
	[state] [nvarchar](3) NULL,
	[countryCode] [nvarchar](3) NULL,
	[postalCode] [nvarchar](12) NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_guest_info] PRIMARY KEY CLUSTERED 
(
	[guest_addressId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[celebration]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[celebration](
	[celebrationId] [bigint] IDENTITY(1,1) NOT NULL,
	[guestId] [bigint] NULL,
	[name] [nvarchar](200) NULL,
	[message] [nvarchar](max) NULL,
	[dateStart] [date] NULL,
	[dateEnd] [date] NULL,
	[active] [bit] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_celebration] PRIMARY KEY CLUSTERED 
(
	[celebrationId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  StoredProcedure [demo].[usp_SetupWalkthrough]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 1/11/2012
-- Description:	Setups up the bands 
--              for a walkthrough.
-- =============================================
CREATE PROCEDURE [demo].[usp_SetupWalkthrough] 
	@WalkthroughID int 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	INSERT INTO [$(databasename)].[dbo].[guest_xband]
			   ([guestId]
			   ,[xbandId]
			   ,[createdBy]
			   ,[createdDate]
			   ,[updatedBy]
			   ,[updatedDate]
			   ,[active])
	SELECT gw.[guestid]
			,gw.[xbandid]
			,g.[createdBy]
			,g.[createdDate]
			,g.[updatedby]
			,g.[updatedDate]
			,1
	FROM [demo].[guest_walkthrough] gw
	JOIN [dbo].[guest] g ON g.[guestid] = gw.[guestid]
	WHERE gw.[walkthroughid] = @WalkthroughID

END
GO
/****** Object:  StoredProcedure [demo].[usp_RemoveWalkthrough]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 1/11/2012
-- Description:	Setups up the bands 
--              for a walkthrough.
-- =============================================
CREATE PROCEDURE [demo].[usp_RemoveWalkthrough] 
	@WalkthroughID int 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DELETE FROM [dbo].[guest_xband] 
	WHERE [guestid] IN (SELECT [guestid] FROM [demo].[guest_walkthrough] WHERE [walkthroughid] = @WalkthroughID)

	DELETE FROM [dbo].[guest_xband] 
	WHERE [xbandid] IN (SELECT [xbandid] FROM [demo].[guest_walkthrough] WHERE [walkthroughid] = @WalkthroughID)

END
GO
/****** Object:  StoredProcedure [demo].[usp_GetWalkthroughGuests]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 1\9\2012
-- Description:	Reprovisions a band
-- =============================================
CREATE PROCEDURE [demo].[usp_GetWalkthroughGuests] 
	@WalkthroughID int 
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	SELECT	 gw.[guestid]
			,g.[Lastname]
			,g.[FirstName]
			,gw.[walkthroughid]
			,gw.[xbandid]
			,s1.[sourceSystemIdValue] as [xid]
			,s2.[sourceSystemIdValue] as [gxplinkid]
			,x.[tapid]
			,x.[bandid]
	FROM [demo].[guest_walkthrough] gw
	JOIN [dbo].[guest] g ON g.[guestid] = gw.[guestid]
	JOIN [dbo].[xband] x on x.[xbandid] = gw.[xbandid]
	LEFT OUTER JOIN [dbo].[source_system_link] s1 ON s1.[guestid] = gw.[guestid] 
		AND s1.[IDMSTypeID] = 19
	LEFT OUTER JOIN [dbo].[source_system_link] s2 ON s2.[guestid] = gw.[guestid] 
		AND s2.[IDMSTypeID] = 10
	WHERE gw.[walkthroughid] = @WalkthroughID


END
GO
/****** Object:  StoredProcedure [dbo].[usp_CreateTestUser]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		Ted Crane
-- Create date: 11/22/2011
-- Description:	
-- =============================================
CREATE PROCEDURE [dbo].[usp_CreateTestUser] 
	-- Add the parameters for the stored procedure here
	@NumberOfUsers int
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

	DECLARE @FirstNamesCount int
	DECLARE @LastNamesCount int
	DECLARE @FirstNameID int
	DECLARE @LastNameID int
	DECLARE @LastName nvarchar(200)
	DECLARE @FirstName nvarchar(200)
	DECLARE @GuestID bigint
	DECLARE @xBandID bigint
	DECLARE @BandID nvarchar(16)
	DECLARE @TapID nvarchar(16)
	DECLARE @LongRangeID nvarchar(16)
	DECLARE @IntVal bigint
	
	DECLARE @Index int

	SET @Index = 0
	
	SELECT @FirstNamesCount = COUNT(*) FROM [dbo].[FirstNames]
	
	SELECT @LastNamesCount = COUNT(*) FROM [dbo].[LastNames]
	
	WHILE @Index < @NumberOfUsers 
	BEGIN
	
		SET @FirstNameID = CAST(RAND() * @FirstNamesCount as int) + 1
		SET @LastNameID = CAST(RAND() * @LastNamesCount as int) + 1
		
		SELECT @FirstName = [Name] FROM [dbo].[FirstNames] WHERE [ID] = @FirstNameID
		SELECT @LastName = [Name] FROM [dbo].[LastNames] WHERE [ID] = @LastNameID
		
		IF @FirstName IS NULL
		BEGIN
			print 'First name ID not found'
		END
	
		IF @LastName IS NULL
		BEGIN
			print 'Last name ID not found'
		END
		
		DECLARE @HexString nvarchar(16)
		DECLARE @IntValue bigint
		SET @HexString = '0123456789ABCDEF'
		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @BandID = 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @LongRangeID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   'FF' +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		Select @IntVal = CAST(RAND() * POWER(2.0,63.0) as bigint)

		SELECT @TapID = 
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 15.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 14.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 13.0 ) as bigint) )  % 16 + 1, 1 ) +
			   --SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 12.0 ) as bigint) )  % 16 + 1, 1 ) +
			   'F0' + 
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 11.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 10.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 9.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / CAST( POWER( 16.0 , 8.0 ) as bigint) )  % 16 + 1, 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 7 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 6 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 5 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 4 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 3 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 2 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 1 ) ) % 16 + 1 , 1 ) +
			   SUBSTRING( @HexString , ( @IntVal / POWER( 16 , 0 ) ) % 16 + 1 , 1 )

		IF NOT EXISTS (SELECT 'X' FROM [dbo].[xband] where [longRangeId] = @LongRangeID OR [tapId] = @tapID)
		BEGIN
			BEGIN TRANSACTION

			BEGIN TRY
					
				INSERT INTO [dbo].[xband]
				   ([bandId]
				   ,[longRangeId]
				   ,[tapId]
				   ,[secureId]
				   ,[UID]
				   ,[bandFriendlyName]
				   ,[printedName]
				   ,[active]
				   ,[createdBy]
				   ,[createdDate]
				   ,[updatedBy]
				   ,[updatedDate])
				VALUES
				   (@BandID
				   ,@LongRangeID
				   ,@TapID
				   ,NULL
				   ,NULL
				   ,@FirstName + ' ' + @LastName + '''s band'
				   ,NULL
				   ,1
				   ,'simulator'
				   ,GETUTCDATE()
				   ,'simulator'
				   ,GETUTCDATE())

				SELECT @xBandID = @@IDENTITY
			
				INSERT INTO [dbo].[guest]
				   ([IDMSID]
				   ,[IDMSTypeId]
				   ,[lastName]
				   ,[firstName]
				   ,[DOB]
				   ,[VisitCount]
				   ,[AvatarName]
				   ,[active]
				   ,[createdBy]
				   ,[createdDate]
				   ,[updatedBy]
				   ,[updatedDate])
				VALUES
				   (NEWID()
				   ,9
				   ,@FirstName
				   ,@LastName
				   ,NULL
				   ,CAST(RAND() * 100 as int)
				   ,NULL
				   ,1
				   ,'simulator'
				   ,GETUTCDATE()
				   ,'simulator'
				   ,GETUTCDATE())

				SELECT @GuestID = @@IDENTITY
				
				INSERT INTO [$(databasename)].[dbo].[guest_xband]
					   ([guestId]
					   ,[xbandId]
					   ,[createdBy]
					   ,[createdDate]
					   ,[updatedBy]
					   ,[updatedDate]
					   ,[active])
				 VALUES
					   (@GuestID
					   ,@xBandID
					   ,'simulator'
					   ,GETUTCDATE()
					   ,'simulator'
					   ,GETUTCDATE()
					   ,1)

			INSERT INTO [dbo].[source_system_link]
					   ([guestId]
					   ,[sourceSystemIdValue]
					   ,[IDMSTypeId]
					   ,[createdBy]
					   ,[createdDate]
					   ,[updatedBy]
					   ,[updatedDate])
				 VALUES
					   (@GuestId
					   ,REPLACE(CAST(NEWID() as nvarchar(200)),'-','')
					   ,9
					   ,'simulator'
					   ,GETUTCDATE()
					   ,'simulator'
					   ,GETUTCDATE())
			
				INSERT INTO [dbo].[xband_type]
						   ([xbandId]
						   ,[isPhysical])
					 VALUES
						   (@xBandID
						   ,0)

				COMMIT TRANSACTION
				
			END TRY
			BEGIN CATCH
			
				ROLLBACK TRANSACTION

			END CATCH
			
		
			SET @Index = @Index + 1
		END

	END

END
GO
/****** Object:  Table [dbo].[party_guest]    Script Date: 03/02/2012 13:07:34 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[party_guest](
	[party_guestId] [bigint] IDENTITY(1,1) NOT NULL,
	[partyId] [bigint] NULL,
	[guestId] [bigint] NULL,
	[IDMSTypeId] [int] NULL,
	[createdBy] [nvarchar](200) NULL,
	[createdDate] [datetime] NULL,
	[updatedBy] [nvarchar](200) NULL,
	[updatedDate] [datetime] NULL,
 CONSTRAINT [PK_party_guest] PRIMARY KEY CLUSTERED 
(
	[party_guestId] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Default [DF_guest_GGID]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest] ADD  CONSTRAINT [DF_guest_GGID]  DEFAULT (newid()) FOR [IDMSID]
GO
/****** Object:  ForeignKey [FK_guest_IDMS_Type]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest]  WITH CHECK ADD  CONSTRAINT [FK_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest] CHECK CONSTRAINT [FK_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_system_id_IDMS_Type]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[source_system_link]  WITH CHECK ADD  CONSTRAINT [FK_source_system_id_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_system_id_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_source_type_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[source_system_link]  WITH CHECK ADD  CONSTRAINT [FK_source_type_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[source_system_link] CHECK CONSTRAINT [FK_source_type_guest]
GO
/****** Object:  ForeignKey [FK_party_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[party]  WITH CHECK ADD  CONSTRAINT [FK_party_guest] FOREIGN KEY([primaryGuestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[party] CHECK CONSTRAINT [FK_party_guest]
GO
/****** Object:  ForeignKey [FK_demo_guest_Demo]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [demo].[demo_guest]  WITH CHECK ADD  CONSTRAINT [FK_demo_guest_Demo] FOREIGN KEY([demoId])
REFERENCES [demo].[demo] ([demoId])
GO
ALTER TABLE [demo].[demo_guest] CHECK CONSTRAINT [FK_demo_guest_Demo]
GO
/****** Object:  ForeignKey [FK_demo_guest_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [demo].[demo_guest]  WITH CHECK ADD  CONSTRAINT [FK_demo_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [demo].[demo_guest] CHECK CONSTRAINT [FK_demo_guest_guest]
GO
/****** Object:  ForeignKey [FK_demo_guest_xband]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [demo].[demo_guest]  WITH CHECK ADD  CONSTRAINT [FK_demo_guest_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
ALTER TABLE [demo].[demo_guest] CHECK CONSTRAINT [FK_demo_guest_xband]
GO
/****** Object:  ForeignKey [FK_guest_xband_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest_xband]  WITH CHECK ADD  CONSTRAINT [FK_guest_xband_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_guest]
GO
/****** Object:  ForeignKey [FK_guest_xband_xband]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest_xband]  WITH CHECK ADD  CONSTRAINT [FK_guest_xband_xband] FOREIGN KEY([xbandId])
REFERENCES [dbo].[xband] ([xbandId])
GO
ALTER TABLE [dbo].[guest_xband] CHECK CONSTRAINT [FK_guest_xband_xband]
GO
/****** Object:  ForeignKey [FK_guest_phone_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_guest]
GO
/****** Object:  ForeignKey [FK_guest_phone_IDMS_Type]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest_phone]  WITH CHECK ADD  CONSTRAINT [FK_guest_phone_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest_phone] CHECK CONSTRAINT [FK_guest_phone_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_guest_info_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_guest]
GO
/****** Object:  ForeignKey [FK_guest_info_IDMS_Type]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[guest_address]  WITH CHECK ADD  CONSTRAINT [FK_guest_info_IDMS_Type] FOREIGN KEY([IDMStypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[guest_address] CHECK CONSTRAINT [FK_guest_info_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_celebration_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_guest]
GO
/****** Object:  ForeignKey [FK_celebration_IDMS_Type]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[celebration]  WITH CHECK ADD  CONSTRAINT [FK_celebration_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[celebration] CHECK CONSTRAINT [FK_celebration_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_guest]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_guest] FOREIGN KEY([guestId])
REFERENCES [dbo].[guest] ([guestId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_guest]
GO
/****** Object:  ForeignKey [FK_party_guest_IDMS_Type]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_IDMS_Type] FOREIGN KEY([IDMSTypeId])
REFERENCES [dbo].[IDMS_Type] ([IDMSTypeId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_IDMS_Type]
GO
/****** Object:  ForeignKey [FK_party_guest_party]    Script Date: 03/02/2012 13:07:34 ******/
ALTER TABLE [dbo].[party_guest]  WITH CHECK ADD  CONSTRAINT [FK_party_guest_party] FOREIGN KEY([partyId])
REFERENCES [dbo].[party] ([partyId])
GO
ALTER TABLE [dbo].[party_guest] CHECK CONSTRAINT [FK_party_guest_party]
GO
