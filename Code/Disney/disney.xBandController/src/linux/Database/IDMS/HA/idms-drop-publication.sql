use [$(sourcedb)]

-- Dropping the transactional publication

exec sp_droppublication @publication = N'$(publicationname)'
GO

exec sp_replicationdboption @dbname = N'$(sourcedb)', @optname = N'publish', @value = N'false'
GO