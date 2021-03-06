/****** Object:  Table [med].[MediaFileStatus]    Script Date: 08/31/2011 14:16:43 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [med].[MediaFileStatus](
	[MediaFileStatusID] [int] IDENTITY(1,1) NOT NULL,
	[MediaFileStatusName] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_MediaFileStatus] PRIMARY KEY CLUSTERED 
(
	[MediaFileStatusID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
