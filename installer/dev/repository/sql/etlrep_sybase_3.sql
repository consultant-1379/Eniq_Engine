alter table META_TRANSFER_BATCHES add META_COLLECTION_NAME varchar(128) null;
alter table META_TRANSFER_BATCHES add META_COLLECTION_SET_NAME varchar(128) null;
alter table META_TRANSFER_BATCHES add SETTYPE varchar(10) null;

update META_TRANSFER_BATCHES as mtb 
set META_COLLECTION_NAME=(select mc.COLLECTION_NAME from META_COLLECTIONS as mc where mc.COLLECTION_ID=mtb.COLLECTION_ID);

update META_TRANSFER_BATCHES as mtb
set META_COLLECTION_SET_NAME=(select mcs.COLLECTION_SET_NAME from META_COLLECTION_SETS as mcs where mcs.COLLECTION_SET_ID=mtb.COLLECTION_SET_ID);

update META_TRANSFER_BATCHES as mtb 
set SETTYPE=(select mc.SETTYPE from META_COLLECTIONS as mc where mc.COLLECTION_ID=mtb.COLLECTION_ID);

alter table META_TRANSFER_BATCHES DROP PRIMARY KEY;

alter table META_TRANSFER_BATCHES modify META_COLLECTION_NAME not null;
alter table META_TRANSFER_BATCHES modify META_COLLECTION_SET_NAME not null;

alter table META_TRANSFER_BATCHES ADD PRIMARY KEY(META_COLLECTION_NAME, META_COLLECTION_SET_NAME, ID, VERSION_NUMBER);