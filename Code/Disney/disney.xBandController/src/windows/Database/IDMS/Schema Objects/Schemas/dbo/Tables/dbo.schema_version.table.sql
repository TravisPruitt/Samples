CREATE TABLE [dbo].[schema_version] (
    [schema_version_id] INT          IDENTITY (1, 1) NOT NULL,
    [version]           VARCHAR (12) NOT NULL,
    [script_name]       VARCHAR (50) NOT NULL,
    [date_applied]      DATETIME     NOT NULL
);

