/****** Object:  Table [med].[MediaFile]    Script Date: 08/31/2011 14:16:48 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [med].[MediaFile](
	[MediaFileID] [int] IDENTITY(1,1) NOT NULL,
	[Timestamp] [datetime] NOT NULL,
	[FilePath] [nvarchar](256) NOT NULL,
	[MediaFileStatusID] [int] NOT NULL,
 CONSTRAINT [PK_MediaFile] PRIMARY KEY CLUSTERED 
(
	[MediaFileID] ASC
)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [med].[MediaFile]  WITH CHECK ADD  CONSTRAINT [FK_MediaFile_MediaFileStatus] FOREIGN KEY([MediaFileStatusID])
REFERENCES [med].[MediaFileStatus] ([MediaFileStatusID])
GO
ALTER TABLE [med].[MediaFile] CHECK CONSTRAINT [FK_MediaFile_MediaFileStatus]
GO
