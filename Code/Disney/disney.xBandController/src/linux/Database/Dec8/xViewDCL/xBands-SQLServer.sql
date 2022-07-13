use IDMS;

DELETE FROM guest_xband WHERE createdBy = 'Dec 9 Lab';

DELETE FROM xband WHERE createdBy = 'Dec 9 Lab';

DELETE FROM guest WHERE createdBy = 'Dec 9 Lab';

SET IDENTITY_INSERT [dbo].[guest] ON

INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33132781,'Burton','William',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33132780,'Burton','Lynette',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33132782,'Burton','Marie',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33132783,'Burton','Jack',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33991709,'Hodges','Julie',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33870251,'Staggs','Tom',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33680619,'Holz','Karl',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(33182143,'Franklin','Nick',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES(32703357,'Crofton','Meg',NEWID(),9, NULL, 0, NULL, 1,'Dec 9 Lab' ,GETUTCDATE() ,'Dec 9 Lab' ,GETUTCDATE());


--INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
--VALUES (3213300,'SG83',NULL,NEWID(),9,NULL,0,NULL,1, 'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
--INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
--VALUES (3213301,'SG84',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
--INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
--VALUES (3213302,'SG85',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
--INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213304,'SG88',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213305,'SG89',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213306,'SG90',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213307,'SG92',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213309,'SG94',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213310,'SG95',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213311,'SG96',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213312,'SG97',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213313,'SG98',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213314,'SG99',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213315,'SG100',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213316,'SG101',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213317,'SG102',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213318,'SG103',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213319,'SG104',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213320,'SG105',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213321,'SG106',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213322,'SG107',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213323,'SG108',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213324,'SG109',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213325,'SG110',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213326,'SG111',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213327,'SG112',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213328,'SG113',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213329,'SG114',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213330,'SG115',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213331,'SG118',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213332,'SG119',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213333,'SG120',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213334,'SG121',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213335,'SG122',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213336,'SG123',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213337,'SG124',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213338,'SG125',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213339,'SG127',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213340,'SG131',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213341,'SG132',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213342,'SG133',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213343,'SG134',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213344,'SG136',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213345,'SG137',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213346,'SG138',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213347,'SG139',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213348,'SG140',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213349,'SG142',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213350,'Vellon','Manny',NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213351,'SG144',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213352,'SG145',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213353,'SG146',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213354,'SG147',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213355,'SG148',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213356,'SG149',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213357,'SG150',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213358,'SG151',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213359,'SG154',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213360,'SG155',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213361,'SG159',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (32133562,'SG176',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213363,'SG177',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213364,'SG178',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213365,'SG179',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213366,'SG180',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213367,'SG183',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213368,'SG186',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213369,'SG187',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[guest] ([GuestID],[lastName],[firstName],[IDMSID],[IDMSTypeId],[DOB],[VisitCount],[AvatarName],[active],[createdBy],[createdDate],[updatedBy],[updatedDate])
VALUES (3213370,'SG188',NULL,NEWID(),9,NULL,0,NULL,1,'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());

SET IDENTITY_INSERT [dbo].[guest] OFF;

INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG83','8029223A934B04','2749d2273f',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG84','8029223A935904','2749d22740',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG85','8029223A946D04','2749d22741',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG86','8029223A935204','2749d22742',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG88','8029223A956804','2749d22744',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG89','8029223A954704','2749d22745',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG90','8029223A943A04','2749d22746',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG92','8029223A954304','2749d22748',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
--INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
--VALUES ('SG93','8029223A936804','2749d22749',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG94','8029223A953B04','2749d2274a',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG95','8029223A955904','2749d2274b',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG96','8029223A943404','2749d2274c',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG97','8029223A955304','2749d2274d',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG98','8029223A933C04','2749d2274e',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG99','8029223A936F04','2749d2274f',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG100','8029223A945204','2749d22750',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG101','8029223A943C04','2749d22751',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG102','8029223A943904','2749d22752',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG103','8029223A956104','2749d22753',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG104','8029223A954604','2749d22754',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG105','8029223A944A04','2749d22755',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG106','8029223A943504','2749d22756',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG107','8029223A944F04','2749d22757',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG108','8029223A945304','2749d22758',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG109','8029223A944004','2749d22759',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG110','8029223A954204','2749d2275a',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG111','8029223A954404','2749d2275b',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG112','8029223A944D04','2749d2275c',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG113','8029223A946A04','2749d2275d',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG114','8029223A956A04','2749d2275e',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG115','8029223A944404','2749d2275f',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG118','8029223A956C04','2749d22782',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG119','8029223A944604','2749d22783',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG120','8029223A956B04','2749d22784',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG121','8029223A953804','2749d22785',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG122','8029223A955504','2749d22786',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG123','8029223A944804','2749d22787',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG124','8029223A943604','2749d22788',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG125','8029223A937304','2749d22789',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG127','8029223A954D04','2749d2278b',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG131','8029223A955B04','2749d2278f',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG132','8029223A954104','2749d22790',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG133','8029223A943E04','2749d22791',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG134','8029223A955A04','2749d22792',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG136','8029223A936E04','2749d22794',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG137','8029223A953604','2749d22795',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG138','8029223A955004','2749d22796',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG139','8029223A937004','2749d22797',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG140','8029223A943F04','2749d22798',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG142','8029223A944704','2749d2279a',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG143','8029223A943804','2749d2279b',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG144','8029223A953A04','2749d2279c',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG145','8029223A955C04','2749d2279d',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG146','8029223A944504','2749d2279e',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG147','8029223A955F04','2749d2279f',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG148','8029223A953E04','2749d227a0',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG149','8029223A936B04','2749d227a1',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG150','8029223A953F04','2749d227a2',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG151','8029223A956004','2749d227a3',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG154','8029223A956D04','2749d227a6',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG155','8029223A937404','2749d227a7',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG159','8029223A956904','2749d227ab',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG176','8029223A906004','2749d227bc',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG177','8029223A8F7A04','2749d227bd',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG178','8029223A903504','2749d227be',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG179','8029223A8F7604','2749d227bf',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG180','8029223A903B04','2749d227c0',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG183','8029223A905C04','2749d227c3',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG186','8029223A8F7504','2749d227c6',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG187','8029223A907804','2749d227c7',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());
INSERT INTO [dbo].[xband] (BandID,TapID,LongRangeID, Active, createdBy, createdDate, updatedBy, updatedDate) 
VALUES ('SG188','8029223A944C04','2749d227c8',1,  'Dec 9 Lab', GETUTCDATE(), 'Dec 9 Lab', GETUTCDATE());

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT   g.GuestID,x.xbandID,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM	[dbo].[xband] x
JOIN	[dbo].[guest] g ON g.[lastName] = x.[BandID]
WHERE	x.[LongRangeID] NOT IN ('2749d2273f','2749d22740','2749d22741','2749d22742','2749d22744','2749d22745','2749d22746','2749d22748','2749d2279b','2749d227a0');

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33132781,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG83';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33132780,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG84';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33132782,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG85';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33132783,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG145';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33991709,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG88';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33870251,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG89';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33680619,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG90';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 33182143,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG92';

--MEG CROFTON
INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 32703357,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG148';

INSERT INTO [dbo].[guest_xband] ([guestId],[xbandId],[createdBy],[createdDate],[updatedBy],[updatedDate],[active])
SELECT 3213350,x.xbandid,'Dec 9 Lab',GETUTCDATE(),'Dec 9 Lab',GETUTCDATE(),1
FROM [dbo].[xband] x
WHERE x.[bandid] = 'SG143';
