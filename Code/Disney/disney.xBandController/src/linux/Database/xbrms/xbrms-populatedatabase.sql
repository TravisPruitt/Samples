use [$(databasename)]

/****** Object:  Table [rdr].[MetricType]    Script Date: 02/14/2012 18:05:12 ******/
SET IDENTITY_INSERT [rdr].[MetricType] ON
GO
INSERT [rdr].[MetricType] ([MetricTypeID], [MetricTypeName]) VALUES (1, N'Stand By'),(2, N'xPass')
GO
SET IDENTITY_INSERT [rdr].[MetricType] OFF
GO
/****** Object:  Table [rdr].[FacilityType]    Script Date: 02/14/2012 18:05:12 ******/
SET IDENTITY_INSERT [rdr].[FacilityType] ON
GO
INSERT [rdr].[FacilityType] ([FacilityTypeID], [FacilityTypeName]) VALUES (1, N'ATTRACTIONMODEL')
GO
SET IDENTITY_INSERT [rdr].[FacilityType] OFF
GO
/****** Object:  Table [rdr].[EventType]    Script Date: 02/14/2012 18:05:12 ******/
SET IDENTITY_INSERT [rdr].[EventType] ON
GO
INSERT [rdr].[EventType] ([EventTypeID], [EventTypeName]) VALUES (1, N'ENTRY'),(2, N'MERGE'),(3, N'INQUEUE'),(4, N'LOAD'),(5, N'EXIT'),(6, N'ABANDON')
GO
SET IDENTITY_INSERT [rdr].[EventType] OFF
GO
/****** Object:  Table [dbo].[config]    Script Date: 02/14/2012 18:05:12 ******/
INSERT [dbo].[config] ([class], [property], [value]) VALUES (N'MSConfig', N'guestxviewcachetimesec', N'1800'),(N'MSConfig', N'relevantguesteventstimesec', N'300'),(N'MSConfig', N'relevantguesteventstimesecmax', N'7200'),(N'MSConfig', N'xviewurl', N'http://localhost:8090/Xview'),(N'ServiceLocator', N'service_implementor_suffix', N'Imp'),(N'XBRMSConfig', N'jmsbroker', N'10.75.3.57:2506'),(N'XBRMSConfig', N'jmspassword', N'Administrator'),(N'XBRMSConfig', N'jmsuser', N'Administrator'),(N'XBRMSConfig', N'jmsxbrcdiscoverytopic', N'com.synapse.xbrc'),(N'XBRMSConfig', N'simulation', N'false')
GO
INSERT INTO [rdr].[Guest] ([GuestID],[FirstName],[LastName],[EmailAddress])
VALUES (0,N'Unknown', N'Unknown', N'Uknown')
GO 

