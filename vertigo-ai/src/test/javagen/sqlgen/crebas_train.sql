-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS ERA cascade;
drop sequence IF EXISTS SEQ_ERA;
drop table IF EXISTS EVENT_TYPE_TRAIN cascade;
drop sequence IF EXISTS SEQ_EVENT_TYPE_TRAIN;
drop table IF EXISTS FACTION cascade;
drop sequence IF EXISTS SEQ_FACTION;
drop table IF EXISTS FACTION_COUNT cascade;
drop sequence IF EXISTS SEQ_FACTION_COUNT;
drop table IF EXISTS HEROE cascade;
drop sequence IF EXISTS SEQ_HEROE;
drop table IF EXISTS IRIS_TRAIN cascade;
drop sequence IF EXISTS SEQ_IRIS_TRAIN;
drop table IF EXISTS LOCATION_TRAIN cascade;
drop sequence IF EXISTS SEQ_LOCATION_TRAIN;
drop table IF EXISTS LOG_FEATURE_TRAIN cascade;
drop sequence IF EXISTS SEQ_LOG_FEATURE_TRAIN;
drop table IF EXISTS RESOURCE_TYPE_TRAIN cascade;
drop sequence IF EXISTS SEQ_RESOURCE_TYPE_TRAIN;
drop table IF EXISTS SEVERITY_TYPE_TRAIN cascade;
drop sequence IF EXISTS SEQ_SEVERITY_TYPE_TRAIN;
drop table IF EXISTS TELSTRA_TRAIN cascade;
drop sequence IF EXISTS SEQ_TELSTRA_TRAIN;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_ERA
	start with 1000 cache 20; 

create sequence SEQ_EVENT_TYPE_TRAIN
	start with 1000 cache 20; 

create sequence SEQ_FACTION
	start with 1000 cache 20; 

create sequence SEQ_FACTION_COUNT
	start with 1000 cache 20; 

create sequence SEQ_HEROE
	start with 1000 cache 20; 

create sequence SEQ_IRIS_TRAIN
	start with 1000 cache 20; 

create sequence SEQ_LOCATION_TRAIN
	start with 1000 cache 20; 

create sequence SEQ_LOG_FEATURE_TRAIN
	start with 1000 cache 20; 

create sequence SEQ_RESOURCE_TYPE_TRAIN
	start with 1000 cache 20; 

create sequence SEQ_SEVERITY_TYPE_TRAIN
	start with 1000 cache 20; 

create sequence SEQ_TELSTRA_TRAIN
	start with 1000 cache 20; 


-- ============================================================
--   Table : ERA                                        
-- ============================================================
create table ERA
(
    ID          	 NUMERIC     	not null,
    ERA_ID      	 NUMERIC     	,
    ERA_NAME    	 TEXT        	not null,
    constraint PK_ERA primary key (ID)
);

comment on column ERA.ID is
'ID';

comment on column ERA.ERA_ID is
'Era ID';

comment on column ERA.ERA_NAME is
'Name';

-- ============================================================
--   Table : EVENT_TYPE_TRAIN                                        
-- ============================================================
create table EVENT_TYPE_TRAIN
(
    ID          	 NUMERIC     	not null,
    CODE        	 NUMERIC     	,
    EVENT_TYPE  	 TEXT        	not null,
    constraint PK_EVENT_TYPE_TRAIN primary key (ID)
);

comment on column EVENT_TYPE_TRAIN.ID is
'ID';

comment on column EVENT_TYPE_TRAIN.CODE is
'Code';

comment on column EVENT_TYPE_TRAIN.EVENT_TYPE is
'Event Type';

-- ============================================================
--   Table : FACTION                                        
-- ============================================================
create table FACTION
(
    ID          	 NUMERIC     	not null,
    FACTION_ID  	 NUMERIC     	,
    FACTION_NAME	 TEXT        	not null,
    ERA         	 NUMERIC     	not null,
    constraint PK_FACTION primary key (ID)
);

comment on column FACTION.ID is
'ID';

comment on column FACTION.FACTION_ID is
'Faction ID';

comment on column FACTION.FACTION_NAME is
'Name';

comment on column FACTION.ERA is
'Era ID';

-- ============================================================
--   Table : FACTION_COUNT                                        
-- ============================================================
create table FACTION_COUNT
(
    ID          	 NUMERIC     	not null,
    FACTION_NAME	 TEXT        	not null,
    COUNT_FACTION_NAME	 NUMERIC     	not null,
    constraint PK_FACTION_COUNT primary key (ID)
);

comment on column FACTION_COUNT.ID is
'ID';

comment on column FACTION_COUNT.FACTION_NAME is
'Name';

comment on column FACTION_COUNT.COUNT_FACTION_NAME is
'Heroes per faction';

-- ============================================================
--   Table : HEROE                                        
-- ============================================================
create table HEROE
(
    ID          	 NUMERIC     	not null,
    HEROE_NAME  	 TEXT        	not null,
    JOB         	 TEXT        	not null,
    FACTION     	 NUMERIC     	not null,
    constraint PK_HEROE primary key (ID)
);

comment on column HEROE.ID is
'ID';

comment on column HEROE.HEROE_NAME is
'Name';

comment on column HEROE.JOB is
'Job';

comment on column HEROE.FACTION is
'Faction ID';

-- ============================================================
--   Table : IRIS_TRAIN                                        
-- ============================================================
create table IRIS_TRAIN
(
    ID          	 NUMERIC     	not null,
    SEPAL_LENGTH	 NUMERIC(5,2)	not null,
    SEPAL_WIDTH 	 NUMERIC(5,2)	not null,
    PETAL_LENGTH	 NUMERIC(5,2)	not null,
    PETAL_WIDTH 	 NUMERIC(5,2)	not null,
    VARIETY     	 TEXT        	not null,
    constraint PK_IRIS_TRAIN primary key (ID)
);

comment on column IRIS_TRAIN.ID is
'ID';

comment on column IRIS_TRAIN.SEPAL_LENGTH is
'Sepal Lenght';

comment on column IRIS_TRAIN.SEPAL_WIDTH is
'Sepal Width';

comment on column IRIS_TRAIN.PETAL_LENGTH is
'Petal Lenght';

comment on column IRIS_TRAIN.PETAL_WIDTH is
'Petal Width';

comment on column IRIS_TRAIN.VARIETY is
'Label';

-- ============================================================
--   Table : LOCATION_TRAIN                                        
-- ============================================================
create table LOCATION_TRAIN
(
    ID          	 NUMERIC     	not null,
    CODE        	 NUMERIC     	,
    LOCATION    	 TEXT        	not null,
    SEVERITY_FAULT	 NUMERIC     	not null,
    constraint PK_LOCATION_TRAIN primary key (ID)
);

comment on column LOCATION_TRAIN.ID is
'ID';

comment on column LOCATION_TRAIN.CODE is
'Code';

comment on column LOCATION_TRAIN.LOCATION is
'Location';

comment on column LOCATION_TRAIN.SEVERITY_FAULT is
'Severity Fault';

-- ============================================================
--   Table : LOG_FEATURE_TRAIN                                        
-- ============================================================
create table LOG_FEATURE_TRAIN
(
    ID          	 NUMERIC     	not null,
    CODE        	 NUMERIC     	,
    LOG_FEATURE 	 TEXT        	not null,
    VOLUME      	 NUMERIC     	not null,
    constraint PK_LOG_FEATURE_TRAIN primary key (ID)
);

comment on column LOG_FEATURE_TRAIN.ID is
'ID';

comment on column LOG_FEATURE_TRAIN.CODE is
'Code';

comment on column LOG_FEATURE_TRAIN.LOG_FEATURE is
'Log Feature';

comment on column LOG_FEATURE_TRAIN.VOLUME is
'Volume';

-- ============================================================
--   Table : RESOURCE_TYPE_TRAIN                                        
-- ============================================================
create table RESOURCE_TYPE_TRAIN
(
    ID          	 NUMERIC     	not null,
    CODE        	 NUMERIC     	,
    RESOURCE_TYPE	 TEXT        	not null,
    constraint PK_RESOURCE_TYPE_TRAIN primary key (ID)
);

comment on column RESOURCE_TYPE_TRAIN.ID is
'ID';

comment on column RESOURCE_TYPE_TRAIN.CODE is
'Code';

comment on column RESOURCE_TYPE_TRAIN.RESOURCE_TYPE is
'Resource Type';

-- ============================================================
--   Table : SEVERITY_TYPE_TRAIN                                        
-- ============================================================
create table SEVERITY_TYPE_TRAIN
(
    ID          	 NUMERIC     	not null,
    CODE        	 NUMERIC     	,
    SEVERITY_TYPE	 TEXT        	not null,
    constraint PK_SEVERITY_TYPE_TRAIN primary key (ID)
);

comment on column SEVERITY_TYPE_TRAIN.ID is
'ID';

comment on column SEVERITY_TYPE_TRAIN.CODE is
'Code';

comment on column SEVERITY_TYPE_TRAIN.SEVERITY_TYPE is
'Severity Type';

-- ============================================================
--   Table : TELSTRA_TRAIN                                        
-- ============================================================
create table TELSTRA_TRAIN
(
    ID          	 NUMERIC     	not null,
    CODE        	 NUMERIC     	,
    CODE_COUNT  	 NUMERIC     	,
    LOG_FEATURE 	 TEXT        	,
    VOLUME      	 NUMERIC     	,
    SEVERITY_FAULT	 NUMERIC     	,
    WIN_LOCATION_VOLUME_SUM	 NUMERIC     	,
    WIN_LOCATION_VOLUME_AVG	 NUMERIC     	,
    WIN_LOCATION_VOLUME_MIN	 NUMERIC     	,
    WIN_LOCATION_VOLUME_MAX	 NUMERIC     	,
    WIN_LOCATION_VOLUME_COUNT	 NUMERIC     	,
    COUNT_FEATURE_204	 NUMERIC     	,
    COUNT_FEATURE_205	 NUMERIC     	,
    SEVERITY_TYPE	 TEXT        	,
    SUM_FEATURE_204_VOLUME	 NUMERIC     	,
    SUM_FEATURE_205_VOLUME	 NUMERIC     	,
    AVG_FEATURE_204_VOLUME	 NUMERIC     	,
    AVG_FEATURE_205_VOLUME	 NUMERIC     	,
    MIN_FEATURE_204_VOLUME	 NUMERIC     	,
    MIN_FEATURE_205_VOLUME	 NUMERIC     	,
    MAX_FEATURE_204_VOLUME	 NUMERIC     	,
    MAX_FEATURE_205_VOLUME	 NUMERIC     	,
    COUNT_FEATURE_204_VOLUME	 NUMERIC     	,
    COUNT_FEATURE_205_VOLUME	 NUMERIC     	,
    constraint PK_TELSTRA_TRAIN primary key (ID)
);

comment on column TELSTRA_TRAIN.ID is
'ID';

comment on column TELSTRA_TRAIN.CODE is
'Code';

comment on column TELSTRA_TRAIN.CODE_COUNT is
'code_count';

comment on column TELSTRA_TRAIN.LOG_FEATURE is
'Log Feature';

comment on column TELSTRA_TRAIN.VOLUME is
'Volume';

comment on column TELSTRA_TRAIN.SEVERITY_FAULT is
'Severity Fault';

comment on column TELSTRA_TRAIN.WIN_LOCATION_VOLUME_SUM is
'win_location_volume_sum';

comment on column TELSTRA_TRAIN.WIN_LOCATION_VOLUME_AVG is
'win_location_volume_avg';

comment on column TELSTRA_TRAIN.WIN_LOCATION_VOLUME_MIN is
'win_location_volume_min';

comment on column TELSTRA_TRAIN.WIN_LOCATION_VOLUME_MAX is
'win_location_volume_max';

comment on column TELSTRA_TRAIN.WIN_LOCATION_VOLUME_COUNT is
'win_location_volume_count';

comment on column TELSTRA_TRAIN.COUNT_FEATURE_204 is
'count_feature_204';

comment on column TELSTRA_TRAIN.COUNT_FEATURE_205 is
'count_feature_205';

comment on column TELSTRA_TRAIN.SEVERITY_TYPE is
'severity_type';

comment on column TELSTRA_TRAIN.SUM_FEATURE_204_VOLUME is
'sum_feature_204_volume';

comment on column TELSTRA_TRAIN.SUM_FEATURE_205_VOLUME is
'sum_feature_205_volume';

comment on column TELSTRA_TRAIN.AVG_FEATURE_204_VOLUME is
'avg_feature_204_volume';

comment on column TELSTRA_TRAIN.AVG_FEATURE_205_VOLUME is
'avg_feature_205_volume';

comment on column TELSTRA_TRAIN.MIN_FEATURE_204_VOLUME is
'min_feature_204_volume';

comment on column TELSTRA_TRAIN.MIN_FEATURE_205_VOLUME is
'min_feature_205_volume';

comment on column TELSTRA_TRAIN.MAX_FEATURE_204_VOLUME is
'max_feature_204_volume';

comment on column TELSTRA_TRAIN.MAX_FEATURE_205_VOLUME is
'max_feature_205_volume';

comment on column TELSTRA_TRAIN.COUNT_FEATURE_204_VOLUME is
'count_feature_204_volume';

comment on column TELSTRA_TRAIN.COUNT_FEATURE_205_VOLUME is
'count_feature_205_volume';



