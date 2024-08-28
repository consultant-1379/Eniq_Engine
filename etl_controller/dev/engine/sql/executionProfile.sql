
insert into etlrep.META_EXECUTION_SLOT_PROFILE
 (PROFILE_NAME, PROFILE_ID, ACTIVE_FLAG) values (
  'Normal', '0', 'Y')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '0', 'Slot1', '0', 'adapter,Adapter,Aggregator,Alarm,Install,Loader,Mediation,Topology')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '0', 'Slot2', '1', 'adapter,Adapter,Aggregator,Alarm,Install,Loader,Mediation,Topology')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '0', 'Slot3', '2', 'adapter,Adapter,Aggregator,Alarm,Install,Loader,Mediation,Topology')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '0', 'Slot4', '3', 'Partition,Service,Support')

insert into etlrep.META_EXECUTION_SLOT_PROFILE
 (PROFILE_NAME, PROFILE_ID, ACTIVE_FLAG) values (
  'NoLoads', '1', 'N')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '1', 'Slot1', '4', 'Support,Install,Partition')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '1', 'Slot2', '5', 'Support')

insert into etlrep.META_EXECUTION_SLOT
 (PROFILE_ID, SLOT_NAME, SLOT_ID, ACCEPTED_SET_TYPES) values (
  '1', 'Slot3', '6', 'Support')
