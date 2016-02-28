--------------------------------------------------------
--  DDL for Table TABLE1
--------------------------------------------------------

  CREATE TABLE "CS307"."TABLE1" 
   (	"USERNAME" VARCHAR2(20 BYTE) DEFAULT User, 
	"IP_ADDRESS" VARCHAR2(15 BYTE) DEFAULT 0, 
	"SENSOR_NAME" VARCHAR2(20 BYTE), 
	"CONFIG" NUMBER(1,0) DEFAULT 1, 
	"DETECTION" NUMBER(1,0) DEFAULT 0, 
	"S_TYPE" VARCHAR2(5 BYTE), 
	"S_LOWER" FLOAT(5) DEFAULT 0, 
	"S_UPPER" FLOAT(5) DEFAULT 0, 
	"DETECTION_DATE" DATE, 
	"TIME_STAMP" TIMESTAMP (6)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;

   COMMENT ON COLUMN "CS307"."TABLE1"."USERNAME" IS 'Username for the owner of the sensor';
   COMMENT ON COLUMN "CS307"."TABLE1"."IP_ADDRESS" IS 'IP address of the sensor';
   COMMENT ON COLUMN "CS307"."TABLE1"."SENSOR_NAME" IS 'User defined name for the sensor';
   COMMENT ON COLUMN "CS307"."TABLE1"."S_TYPE" IS 'LIGHT for Light sensor; AUDIO for audio sensor; CAMER for camera sensor';
   COMMENT ON COLUMN "CS307"."TABLE1"."S_LOWER" IS 'Sensor lower threshold';
   COMMENT ON COLUMN "CS307"."TABLE1"."S_UPPER" IS 'Sensor upper threshold';
   COMMENT ON COLUMN "CS307"."TABLE1"."DETECTION_DATE" IS 'Date of detection if it is a detection';
   COMMENT ON COLUMN "CS307"."TABLE1"."TIME_STAMP" IS 'Time stamp of detection if it''s a detection';
--------------------------------------------------------
--  DDL for Index TABLE1_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "CS307"."TABLE1_PK" ON "CS307"."TABLE1" ("IP_ADDRESS") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM" ;
--------------------------------------------------------
--  Constraints for Table TABLE1
--------------------------------------------------------

  ALTER TABLE "CS307"."TABLE1" MODIFY ("S_UPPER" NOT NULL ENABLE);
  ALTER TABLE "CS307"."TABLE1" MODIFY ("S_LOWER" NOT NULL ENABLE);
  ALTER TABLE "CS307"."TABLE1" ADD CONSTRAINT "TABLE1_PK" PRIMARY KEY ("IP_ADDRESS")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "SYSTEM"  ENABLE;
  ALTER TABLE "CS307"."TABLE1" MODIFY ("S_TYPE" NOT NULL ENABLE);
  ALTER TABLE "CS307"."TABLE1" MODIFY ("SENSOR_NAME" NOT NULL ENABLE);
  ALTER TABLE "CS307"."TABLE1" MODIFY ("IP_ADDRESS" NOT NULL ENABLE);
  ALTER TABLE "CS307"."TABLE1" MODIFY ("USERNAME" NOT NULL ENABLE);
