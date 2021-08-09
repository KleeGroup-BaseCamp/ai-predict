-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS ERA cascade;
drop sequence IF EXISTS SEQ_ERA;
drop table IF EXISTS FACTION cascade;
drop sequence IF EXISTS SEQ_FACTION;
drop table IF EXISTS HEROE cascade;
drop sequence IF EXISTS SEQ_HEROE;
drop table IF EXISTS IRIS_TRAIN cascade;
drop sequence IF EXISTS SEQ_IRIS_TRAIN;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_ERA
	start with 1000 cache 20; 

create sequence SEQ_FACTION
	start with 1000 cache 20; 

create sequence SEQ_HEROE
	start with 1000 cache 20; 

create sequence SEQ_IRIS_TRAIN
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



