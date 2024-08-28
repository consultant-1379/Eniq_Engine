insert into REPOSITORYTABLES (table_qualifier, creator, tname, table_type, remarks) values ('repdb','dwhrep','Aggregation',      'TABLE', null);
insert into REPOSITORYTABLES (table_qualifier, creator, tname, table_type, remarks) values ('repdb','dwhrep','AggregationRule',  'TABLE', null);
insert into REPOSITORYTABLES (table_qualifier, creator, tname, table_type, remarks) values ('repdb','dwhrep','MeasurementColumn','TABLE', null);
insert into REPOSITORYTABLES (table_qualifier, creator, tname, table_type, remarks) values ('repdb','dwhrep','DataItem',         'TABLE', null);

insert into LOG_AGGREGATIONSTATUS (TIMELEVEL, DATADATE, DATE_ID, INITIAL_AGGREGATION, STATUS, DESCRIPTION, ROWCOUNT, AGGREGATIONSCOPE, LAST_AGGREGATION, LOOPCOUNT, THRESHOLD, AGGREGATION, TYPENAME) values ('DAYBH', '2010-11-23', '2010-11-23', null, 'BLOCKED', null, 10, 'DAY', null, 1, '2010-11-23 00:00:00', 'DC_E_CPP_VCLTP_DAYBH_VCLTP', 'DC_E_CPP_VCLTP');


