Setting up Replication for IDMS HA

The distributor is assumed to be configured.

First run idms-create-publication.bat, which requires the following parameters (values are examples):

serverhostname - dev-sql.emtest.local
sourceserver - dev-sql-pub
sourcedb - IDMS_v1_6
destinationserver - dev-sql-sub
destinationdb - IDMS_v1_6
username - dev-sql-pub\Administrator
password - password for username
publicationname - IDMS

Example: idms-create-publication.bat DEV-SQL.emtest.local DEV-SQL IDMS_v1_6_PUB DEV-SQL IDMS_v1_6_SUB DEV-SQL\Administrator Crrctv11 IDMS

This will create the publication and ensure the sourcedb has been enabled to publish. 
Output is written to idms-create-publication.log

Next run idms-create-subscription.bat to create the subscription, which requires the following parameters (values are examples):

serverhostname - dev-sql.emtest.local
sourceserver - dev-sql-pub
sourcedb - IDMS_v1_6
destinationserver - dev-sql-sub
destinationdb - IDMS_v1_6
username - dev-sql-pub\Administrator
password - password for username
publicationname - IDMS

Example: idms-create-subscription.bat DEV-SQL.emtest.local DEV-SQL IDMS_v1_6_PUB DEV-SQL IDMS_v1_6_SUB DEV-SQL\Administrator Crrctv11 IDMS

This script will need to be run, with a different destinationserver for each subscription you want to create.

Dropping Replication for IDMS HA

First, run the idms-drop-subscription2.bat script with the following parameters (values are examples):

serverhostname - dev-sql.emtest.local
publicationname - IDMS
sourcedb - IDMS_v1_6

Example: idms-drop-subscriptions.bat DEV-SQL.emtest.local IDMS IDMS_v1_6_PUB

This will drop all the subscriptions to the specified publisher, on the specified server.

Next, run the idms-drop-publication.bat script with the following parameters (values are examples):

serverhostname - dev-sql.emtest.local
publicationname - IDMS
sourcedb - IDMS_v1_6

This will drop the specified publication, on the specified server.

