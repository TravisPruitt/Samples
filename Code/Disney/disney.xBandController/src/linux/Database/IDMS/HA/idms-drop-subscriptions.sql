use [$(sourcedb)]

-- Dropping the transactional articles

exec sp_dropsubscription @publication = N'$(publicationname)', 
	@subscriber = N'all', @article=N'all'

