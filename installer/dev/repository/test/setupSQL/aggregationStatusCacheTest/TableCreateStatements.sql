CREATE TABLE DATAITEM (
DATAFORMATID          VARCHAR(100),
DATANAME              VARCHAR(128),
COLNUMBER             NUMERIC(11),
DATAID                VARCHAR(255),
PROCESS_INSTRUCTION   VARCHAR(128),
DATATYPE              VARCHAR(50), 
DATASIZE              INTEGER, 
DATASCALE             INTEGER
); 

CREATE TABLE REPOSITORYTABLES (
table_qualifier       VARCHAR(50),
creator               VARCHAR(50),
tname                 VARCHAR(50),
table_type            VARCHAR(50),
remarks               VARCHAR(50)
);

create table Aggregation (
   AGGREGATION          varchar(255) not null,
   VERSIONID            varchar(128) not null,
   AGGREGATIONSET       varchar(100) null,
   AGGREGATIONGROUP     varchar(100) null,
   REAGGREGATIONSET     varchar(100) null,
   REAGGREGATIONGROUP   varchar(100) null,
   GROUPORDER           integer      null,
   AGGREGATIONORDER     integer      null,
   AGGREGATIONTYPE      varchar(50)  null,
   AGGREGATIONSCOPE     varchar(50)  null       
);


create table AggregationRule (
   AGGREGATION          varchar(255) not null,
   VERSIONID            varchar(128) not null,
   RULEID               integer      not null,
   TARGET_TYPE          varchar(50)  null,
   TARGET_LEVEL         varchar(50)  null,
   TARGET_TABLE         varchar(255) null,
   TARGET_MTABLEID      varchar(255) null,
   SOURCE_TYPE          varchar(50)  null,
   SOURCE_LEVEL         varchar(50)  null,
   SOURCE_TABLE         varchar(255) null,
   SOURCE_MTABLEID      varchar(255) null,
   RULETYPE             varchar(50)  null,
   AGGREGATIONSCOPE     varchar(50)  null,
   BHTYPE		varchar(50)  null 
);

create table MeasurementColumn (
   MTABLEID             varchar(255) not null,
   DATANAME             varchar(128) not null,
   COLNUMBER            numeric(9)   null,
   DATATYPE             varchar(50)  null,
   DATASIZE             integer      null,
   DATASCALE            integer      null,
   UNIQUEVALUE          numeric(9)   null,
   NULLABLE             integer      null,
   INDEXES              varchar(20)  null,
   DESCRIPTION          varchar(32000) null,
   DATAID               varchar(255) null,
   RELEASEID            varchar(50)  null,
   UNIQUEKEY            integer      null
);

create table LOG_AGGREGATIONSTATUS(
	TIMELEVEL					varchar(10) 	null,
	DATADATE					date 				null,
	DATE_ID						date				null,
	INITIAL_AGGREGATION	timestamp 	null,
	STATUS						varchar(16) 	null,
	DESCRIPTION				varchar(250) null,
	ROWCOUNT					integer			null,
	AGGREGATIONSCOPE		varchar(50)	null,
	LAST_AGGREGATION		timestamp		null,
	LOOPCOUNT					integer			null,
	THRESHOLD               timestamp null,
	AGGREGATION				varchar(255)	null,
	TYPENAME					varchar(255)	null
);
