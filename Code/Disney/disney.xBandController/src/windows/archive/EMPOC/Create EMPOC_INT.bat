sqlcmd -S em-database.synapsedev.com -E -i "EMPOC_INT.Database.sql"
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i "EM-DATABASE DBService.User.sql"
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i "EMUser.User.sql"
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i med.Schema.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.Schema.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.Schema.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.Configuration.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.Guest.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i med.MediaFileStatus.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i med.MediaFile.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.Attraction.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.EventType.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.Event.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.AbandonEvent.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.ExitEvent.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.LoadEvent.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.MetricType.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.Metric.Table.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i med.usp_MediaFile_Create.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i med.usp_MediaFile_GetReceived.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i med.usp_MediaFile_Sent.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_Event_Create.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_AbandonEvent_Create.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_Event_GetMediaEvents.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_ExitEvent_Create.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getAttractionsList.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getAvgSBQueueWait.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getAvgXPQueueWait.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getEstSBQueueWait.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getEstXPQueueWait.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getParkGuestCount.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getSBArrivalRate.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getSBGuestsServedCount.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getSBQueueCount.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getXPArrivalRate.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getXPGuestsServedCount.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_getXPQueueCountPL.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_LoadEvent_Create.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i rdr.usp_Metric_Create.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_EndSimulation.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_Simulation_Update.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_SeedGuests.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_StartSimulation.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_EntryEvents_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_ExitEvents_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_InQueueEvents_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_LoadEvents_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_MergeEvents_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_Metrics_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_Events_Insert.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_Events_Insert_wLoop.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i tst.usp_SeedTestData.StoredProcedure.sql
sqlcmd -S em-database.synapsedev.com -E -d EMPOC_INT -i InitializeData.sql