
-- Enabling the replication database
use master
exec sp_replicationdboption @dbname = N'$(sourcedb)', @optname = N'publish', @value = N'true'
GO

exec [$(sourcedb)].sys.sp_addlogreader_agent 
	@job_login = N'$(username)', 
	@job_password = N'$(password)', 
	@publisher_security_mode = 1
GO
exec [$(sourcedb)].sys.sp_addqreader_agent 
	@job_login = null, 
	@job_password = null, 
	@frompublisher = 1
GO
-- Adding the transactional publication
use [$(sourcedb)]
exec sp_addpublication @publication = N'$(publicationname)', 
	@description = N'Transactional publication of database ''$(sourcedb)'' from Publisher ''$(sourceserver)''.', 
	@sync_method = N'concurrent', 
	@retention = 0, 
	@allow_push = N'true', 
	@allow_pull = N'true', 
	@allow_anonymous = N'true', 
	@enabled_for_internet = N'false', 
	@snapshot_in_defaultfolder = N'true', 
	@compress_snapshot = N'false', 
	@ftp_port = 21, 
	@ftp_login = N'anonymous', 
	@allow_subscription_copy = N'false', 
	@add_to_active_directory = N'false', 
	@repl_freq = N'continuous', 
	@status = N'active', 
	@independent_agent = N'true', 
	@immediate_sync = N'true', 
	@allow_sync_tran = N'false', 
	@autogen_sync_procs = N'false', 
	@allow_queued_tran = N'false', 
	@allow_dts = N'false', 
	@replicate_ddl = 1, 
	@allow_initialize_from_backup = N'false', 
	@enabled_for_p2p = N'false', 
	@enabled_for_het_sub = N'false'
GO


exec sp_addpublication_snapshot @publication = N'$(publicationname)', 
	@frequency_type = 1, 
	@frequency_interval = 0, 
	@frequency_relative_interval = 0, 
	@frequency_recurrence_factor = 0, 
	@frequency_subday = 0, 
	@frequency_subday_interval = 0, 
	@active_start_time_of_day = 0, 
	@active_end_time_of_day = 235959, 
	@active_start_date = 0, 
	@active_end_date = 0, 
	@job_login = N'$(username)', 
	@job_password = N'$(password)', 
	@publisher_security_mode = 1


exec sp_grant_publication_access @publication = N'$(publicationname)', @login = N'sa'
GO
exec sp_grant_publication_access @publication = N'$(publicationname)', @login = N'NT AUTHORITY\SYSTEM'
GO
exec sp_grant_publication_access @publication = N'$(publicationname)', @login = N'$(username)'
GO
exec sp_grant_publication_access @publication = N'$(publicationname)', @login = N'NT SERVICE\SQLSERVERAGENT'
GO
exec sp_grant_publication_access @publication = N'$(publicationname)', @login = N'NT SERVICE\MSSQLSERVER'
GO
exec sp_grant_publication_access @publication = N'$(publicationname)', @login = N'distributor_admin'
GO

-- Adding the transactional articles
use [$(sourcedb)]
exec sp_addarticle @publication = N'$(publicationname)', @article = N'celebration', @source_owner = N'dbo', @source_object = N'celebration', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'celebration', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dbocelebration]', @del_cmd = N'CALL [dbo].[sp_MSdel_dbocelebration]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dbocelebration]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'celebration_guest', @source_owner = N'dbo', @source_object = N'celebration_guest', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'none', @destination_table = N'celebration_guest', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dbocelebration_guest]', @del_cmd = N'CALL [dbo].[sp_MSdel_dbocelebration_guest]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dbocelebration_guest]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'guest', @source_owner = N'dbo', @source_object = N'guest', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'guest', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboguest]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboguest]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboguest]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'guest_xband', @source_owner = N'dbo', @source_object = N'guest_xband', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'none', @destination_table = N'guest_xband', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboguest_xband]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboguest_xband]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboguest_xband]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'IDMS_Type', @source_owner = N'dbo', @source_object = N'IDMS_Type', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'IDMS_Type', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboIDMS_Type]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboIDMS_Type]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboIDMS_Type]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'party', @source_owner = N'dbo', @source_object = N'party', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'party', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboparty]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboparty]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboparty]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'party_guest', @source_owner = N'dbo', @source_object = N'party_guest', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'party_guest', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboparty_guest]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboparty_guest]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboparty_guest]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'schema_version', @source_owner = N'dbo', @source_object = N'schema_version', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'schema_version', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboschema_version]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboschema_version]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboschema_version]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'source_system_link', @source_owner = N'dbo', @source_object = N'source_system_link', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'none', @destination_table = N'source_system_link', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dbosource_system_link]', @del_cmd = N'CALL [dbo].[sp_MSdel_dbosource_system_link]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dbosource_system_link]'
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'ufn_GetGuestId', @source_owner = N'dbo', @source_object = N'ufn_GetGuestId', @type = N'func schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'ufn_GetGuestId', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_create', @source_owner = N'dbo', @source_object = N'usp_celebration_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_create', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_delete', @source_owner = N'dbo', @source_object = N'usp_celebration_delete', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_delete', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_guest_add', @source_owner = N'dbo', @source_object = N'usp_celebration_guest_add', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_guest_add', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_guest_delete', @source_owner = N'dbo', @source_object = N'usp_celebration_guest_delete', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_guest_delete', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_guest_update', @source_owner = N'dbo', @source_object = N'usp_celebration_guest_update', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_guest_update', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_retrieve', @source_owner = N'dbo', @source_object = N'usp_celebration_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_retrieve', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_retrieve_by_identifier', @source_owner = N'dbo', @source_object = N'usp_celebration_retrieve_by_identifier', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_retrieve_by_identifier', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_celebration_update', @source_owner = N'dbo', @source_object = N'usp_celebration_update', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_celebration_update', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_create', @source_owner = N'dbo', @source_object = N'usp_guest_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_create', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_data_retrieve', @source_owner = N'dbo', @source_object = N'usp_guest_data_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_data_retrieve', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_identifiers_retrieve', @source_owner = N'dbo', @source_object = N'usp_guest_identifiers_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_identifiers_retrieve', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_name_retrieve', @source_owner = N'dbo', @source_object = N'usp_guest_name_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_name_retrieve', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_retrieve', @source_owner = N'dbo', @source_object = N'usp_guest_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_retrieve', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_retrieve_by_email', @source_owner = N'dbo', @source_object = N'usp_guest_retrieve_by_email', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_retrieve_by_email', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_search', @source_owner = N'dbo', @source_object = N'usp_guest_search', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_search', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_update', @source_owner = N'dbo', @source_object = N'usp_guest_update', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_update', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_xband_create', @source_owner = N'dbo', @source_object = N'usp_guest_xband_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_xband_create', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_xband_delete', @source_owner = N'dbo', @source_object = N'usp_guest_xband_delete', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_xband_delete', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_guest_xband_update', @source_owner = N'dbo', @source_object = N'usp_guest_xband_update', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_guest_xband_update', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_IDMSHello', @source_owner = N'dbo', @source_object = N'usp_IDMSHello', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_IDMSHello', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_party_create', @source_owner = N'dbo', @source_object = N'usp_party_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_party_create', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_party_guest_create', @source_owner = N'dbo', @source_object = N'usp_party_guest_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_party_guest_create', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_party_retrieve', @source_owner = N'dbo', @source_object = N'usp_party_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_party_retrieve', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_party_retrieve_by_name', @source_owner = N'dbo', @source_object = N'usp_party_retrieve_by_name', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_party_retrieve_by_name', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_party_update', @source_owner = N'dbo', @source_object = N'usp_party_update', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_party_update', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_RethrowError', @source_owner = N'dbo', @source_object = N'usp_RethrowError', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_RethrowError', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_schema_version_retrieve', @source_owner = N'dbo', @source_object = N'usp_schema_version_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_schema_version_retrieve', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_source_system_link_create', @source_owner = N'dbo', @source_object = N'usp_source_system_link_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_source_system_link_create', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_source_system_link_delete', @source_owner = N'dbo', @source_object = N'usp_source_system_link_delete', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_source_system_link_delete', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_source_system_link_retrieve', @source_owner = N'dbo', @source_object = N'usp_source_system_link_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_source_system_link_retrieve', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xband_assign', @source_owner = N'dbo', @source_object = N'usp_xband_assign', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xband_assign', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xband_assign_by_identifier', @source_owner = N'dbo', @source_object = N'usp_xband_assign_by_identifier', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xband_assign_by_identifier', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xband_create', @source_owner = N'dbo', @source_object = N'usp_xband_create', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xband_create', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xband_retrieve', @source_owner = N'dbo', @source_object = N'usp_xband_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xband_retrieve', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xband_unassign', @source_owner = N'dbo', @source_object = N'usp_xband_unassign', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xband_unassign', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xband_update', @source_owner = N'dbo', @source_object = N'usp_xband_update', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xband_update', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'usp_xbands_retrieve', @source_owner = N'dbo', @source_object = N'usp_xbands_retrieve', @type = N'proc schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'usp_xbands_retrieve', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_celebration', @source_owner = N'dbo', @source_object = N'vw_celebration', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_celebration', @destination_owner = N'dbo', @status = 16
GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_celebration_guest', @source_owner = N'dbo', @source_object = N'vw_celebration_guest', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_celebration_guest', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_eligible_guests', @source_owner = N'dbo', @source_object = N'vw_eligible_guests', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_eligible_guests', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_guest_xband', @source_owner = N'dbo', @source_object = N'vw_guest_xband', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_guest_xband', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_registered_guests', @source_owner = N'dbo', @source_object = N'vw_registered_guests', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_registered_guests', @destination_owner = N'dbo', @status = 16
--GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_test_guest', @source_owner = N'dbo', @source_object = N'vw_test_guest', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_test_guest', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_xband', @source_owner = N'dbo', @source_object = N'vw_xband', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_xband', @destination_owner = N'dbo', @status = 16
GO

--exec sp_addarticle @publication = N'$(publicationname)', @article = N'vw_xi_guest', @source_owner = N'dbo', @source_object = N'vw_xi_guest', @type = N'view schema only', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x0000000008000001, @destination_table = N'vw_xi_guest', @destination_owner = N'dbo', @status = 16
--GO

exec sp_addarticle @publication = N'$(publicationname)', @article = N'xband', @source_owner = N'dbo', @source_object = N'xband', @type = N'logbased', @description = N'', @creation_script = N'', @pre_creation_cmd = N'drop', @schema_option = 0x000000000803509F, @identityrangemanagementoption = N'manual', @destination_table = N'xband', @destination_owner = N'dbo', @status = 24, @vertical_partition = N'false', @ins_cmd = N'CALL [dbo].[sp_MSins_dboxband]', @del_cmd = N'CALL [dbo].[sp_MSdel_dboxband]', @upd_cmd = N'SCALL [dbo].[sp_MSupd_dboxband]'
GO

exec sp_startpublication_snapshot @publication = N'$(publicationname)'