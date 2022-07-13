USE [master]
GO

IF  EXISTS (SELECT name FROM sys.databases WHERE name = N'$(databasename)' )
DROP DATABASE [$(databasename)]
GO

CREATE DATABASE [$(databasename)] ON  PRIMARY 
( NAME = N'$(databasename)', FILENAME = N'$(dbdirectory)\$(databasename).mdf' , SIZE = 1024MB , MAXSIZE = UNLIMITED, FILEGROWTH = 1024MB )
 LOG ON 
( NAME = N'$(databasename)_log', FILENAME = N'$(dbdirectory)\$(databasename)_1.ldf' , SIZE = 1024MB , MAXSIZE = 2048GB , FILEGROWTH = 10%)
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
EXEC sys.sp_db_vardecimal_storage_format N'$(databasename)', N'ON'
GO

IF NOT EXISTS (SELECT loginname FROM master.dbo.syslogins WHERE name = N'EMUser')
CREATE LOGIN EMUser WITH PASSWORD = 'Mayhem!23'
GO

USE [$(databasename)]
GO

IF  EXISTS (SELECT * FROM sys.database_principals WHERE name = N'EMUser')
DROP USER [EMUser]
GO

CREATE USER EMUser FOR LOGIN EMUser WITH DEFAULT_SCHEMA = dbo
GO

sp_addrolemember @rolename = 'db_datareader', @membername = 'EMUser'
GO
sp_addrolemember @rolename = 'db_datawriter', @membername = 'EMUser'
GO

GRANT CONNECT TO [EMUser] AS [dbo]
GO

GRANT EXECUTE TO [EMUser] AS [dbo]
GO
