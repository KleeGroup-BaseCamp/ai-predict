-- ============================================================
--   SGBD      		  :  H2                     
-- ============================================================

-- ============================================================
--   Drop                                       
-- ============================================================
drop table IF EXISTS IRIS_TRAIN cascade;
drop sequence IF EXISTS SEQ_IRIS_TRAIN;




-- ============================================================
--   Sequences                                      
-- ============================================================
create sequence SEQ_IRIS_TRAIN
	start with 1000 cache 20; 


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



