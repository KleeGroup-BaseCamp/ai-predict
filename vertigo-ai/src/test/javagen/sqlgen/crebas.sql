-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS ERA cascade;
drop sequence IF EXISTS SEQ_ERA;
drop table IF EXISTS EVENT_TYPE cascade;
drop sequence IF EXISTS SEQ_EVENT_TYPE;
drop table IF EXISTS FACTION cascade;
drop sequence IF EXISTS SEQ_FACTION;
drop table IF EXISTS HEROE cascade;
drop sequence IF EXISTS SEQ_HEROE;
drop table IF EXISTS IRIS cascade;
drop sequence IF EXISTS SEQ_IRIS;
drop table IF EXISTS LOCATION cascade;
drop sequence IF EXISTS SEQ_LOCATION;
drop table IF EXISTS LOG_FEATURE cascade;
drop sequence IF EXISTS SEQ_LOG_FEATURE;
drop table IF EXISTS RESOURCE_TYPE cascade;
drop sequence IF EXISTS SEQ_RESOURCE_TYPE;
drop table IF EXISTS SEVERITY_TYPE cascade;
drop sequence IF EXISTS SEQ_SEVERITY_TYPE;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_ERA
	start with 1000 cache 20; 

create sequence SEQ_EVENT_TYPE
	start with 1000 cache 20; 

create sequence SEQ_FACTION
	start with 1000 cache 20; 

create sequence SEQ_HEROE
	start with 1000 cache 20; 

create sequence SEQ_IRIS
	start with 1000 cache 20; 

create sequence SEQ_LOCATION
	start with 1000 cache 20; 

create sequence SEQ_LOG_FEATURE
	start with 1000 cache 20; 

create sequence SEQ_RESOURCE_TYPE
	start with 1000 cache 20; 

create sequence SEQ_SEVERITY_TYPE
	start with 1000 cache 20; 


-- ============================================================
--   Table : ERA                                        
-- ============================================================
create table ERA
(
    ID          	 NUMERIC     	not null,
    NAME        	 TEXT        	not null,
    constraint PK_ERA primary key (ID)
);

comment on column ERA.ID is
'ID';

comment on column ERA.NAME is
'Name';

-- ============================================================
--   Table : EVENT_TYPE                                        
-- ============================================================
create table EVENT_TYPE
(
    ID          	 NUMERIC     	not null,
    EVENT_TYPE  	 TEXT        	not null,
    constraint PK_EVENT_TYPE primary key (ID)
);

comment on column EVENT_TYPE.ID is
'ID';

comment on column EVENT_TYPE.EVENT_TYPE is
'Event Type';

-- ============================================================
--   Table : FACTION                                        
-- ============================================================
create table FACTION
(
    ID          	 NUMERIC     	not null,
    NAME        	 TEXT        	not null,
    ERA         	 NUMERIC     	not null,
    constraint PK_FACTION primary key (ID)
);

comment on column FACTION.ID is
'ID';

comment on column FACTION.NAME is
'Name';

comment on column FACTION.ERA is
'Era ID';

-- ============================================================
--   Table : HEROE                                        
-- ============================================================
create table HEROE
(
    ID          	 NUMERIC     	not null,
    NAME        	 TEXT        	not null,
    JOB         	 TEXT        	not null,
    FACTION     	 NUMERIC     	not null,
    constraint PK_HEROE primary key (ID)
);

comment on column HEROE.ID is
'ID';

comment on column HEROE.NAME is
'Name';

comment on column HEROE.JOB is
'Job';

comment on column HEROE.FACTION is
'Faction ID';

-- ============================================================
--   Table : IRIS                                        
-- ============================================================
create table IRIS
(
    ID          	 NUMERIC     	not null,
    SEPAL_LENGTH	 NUMERIC(5,2)	not null,
    SEPAL_WIDTH 	 NUMERIC(5,2)	not null,
    PETAL_LENGTH	 NUMERIC(5,2)	not null,
    PETAL_WIDTH 	 NUMERIC(5,2)	not null,
    VARIETY     	 TEXT        	not null,
    constraint PK_IRIS primary key (ID)
);

comment on column IRIS.ID is
'ID';

comment on column IRIS.SEPAL_LENGTH is
'Sepal Lenght';

comment on column IRIS.SEPAL_WIDTH is
'Sepal Width';

comment on column IRIS.PETAL_LENGTH is
'Petal Lenght';

comment on column IRIS.PETAL_WIDTH is
'Petal Width';

comment on column IRIS.VARIETY is
'Label';

-- ============================================================
--   Table : LOCATION                                        
-- ============================================================
create table LOCATION
(
    ID          	 NUMERIC     	not null,
    LOCATION    	 TEXT        	not null,
    SEVERITY_FAULT	 NUMERIC     	not null,
    constraint PK_LOCATION primary key (ID)
);

comment on column LOCATION.ID is
'ID';

comment on column LOCATION.LOCATION is
'Location';

comment on column LOCATION.SEVERITY_FAULT is
'Severity Fault';

-- ============================================================
--   Table : LOG_FEATURE                                        
-- ============================================================
create table LOG_FEATURE
(
    ID          	 NUMERIC     	not null,
    LOG_FEATURE 	 TEXT        	not null,
    VOLUME      	 NUMERIC     	not null,
    constraint PK_LOG_FEATURE primary key (ID)
);

comment on column LOG_FEATURE.ID is
'ID';

comment on column LOG_FEATURE.LOG_FEATURE is
'Log Feature';

comment on column LOG_FEATURE.VOLUME is
'Volume';

-- ============================================================
--   Table : RESOURCE_TYPE                                        
-- ============================================================
create table RESOURCE_TYPE
(
    ID          	 NUMERIC     	not null,
    RESOURCE_TYPE	 TEXT        	not null,
    constraint PK_RESOURCE_TYPE primary key (ID)
);

comment on column RESOURCE_TYPE.ID is
'ID';

comment on column RESOURCE_TYPE.RESOURCE_TYPE is
'Resource Type';

-- ============================================================
--   Table : SEVERITY_TYPE                                        
-- ============================================================
create table SEVERITY_TYPE
(
    ID          	 NUMERIC     	not null,
    SEVERITY_TYPE	 TEXT        	not null,
    constraint PK_SEVERITY_TYPE primary key (ID)
);

comment on column SEVERITY_TYPE.ID is
'ID';

comment on column SEVERITY_TYPE.SEVERITY_TYPE is
'Severity Type';



