IF(SELECT COUNT(*) FROM sys.sysevent WHERE event_name = 'check_dwhdb_size_normal')>0 THEN
    DROP EVENT check_dwhdb_size_normal;
END IF;

IF(SELECT COUNT(*) FROM sys.sysevent WHERE event_name = 'check_dwhdb_size_panic')>0 THEN
    DROP EVENT check_dwhdb_size_panic;
END IF;


CREATE EVENT check_dwhdb_size_normal
    SCHEDULE sched_check_dwhdb_size_normal START TIME '00:00 AM' EVERY 15 MINUTES
    ENABLE
HANDLER
BEGIN
    DECLARE mainspace_used_percentage INT;
    DECLARE conncount UNSIGNED INT;
    DECLARE otherversions VARCHAR(50);
    DECLARE logged_datetime TIMESTAMP;

    DECLARE LOCAL TEMPORARY TABLE temp_dwhdb_usage (
        logged_datetime TIMESTAMP,
        mainspace_used_percentage INT,
        connection_count INT,
        other_versions VARCHAR(50)
    );

    INSERT INTO temp_dwhdb_usage LOCATION 'dwhdb.dwhdb' 'SELECT logged_datetime, mainspace_used_percentage, connection_count, other_versions FROM show_db_usage()';
    
    SET mainspace_used_percentage = (SELECT mainspace_used_percentage FROM temp_dwhdb_usage);
    SET conncount = (SELECT connection_count FROM temp_dwhdb_usage);
    SET otherversions = (SELECT other_versions FROM temp_dwhdb_usage);
    SET logged_datetime = (SELECT logged_datetime FROM temp_dwhdb_usage);

    IF (85.0 <= mainspace_used_percentage) THEN
        CALL xp_cmdshell('echo ' || logged_datetime || ' WARNING: DWHDB is almost full. ConnCount: ' || conncount || ' MainSpaceUsed: ' || mainspace_used_percentage ||'% OtherVersions: ' || otherversions || ' >> /eniq/log/sw_log/iq/dwhdb_usage.log');
    END IF;

    IF (90.0 <= mainspace_used_percentage) THEN
        CALL xp_cmdshell('engine -e changeProfile NoLoads "Warning: DWHDB is almost full. Engine is set to NoLoads profile."');
        ALTER EVENT check_dwhdb_size_panic ENABLE;
    ELSE
        ALTER EVENT check_dwhdb_size_panic DISABLE;
    END IF;

END;

CREATE EVENT check_dwhdb_size_panic
    SCHEDULE sched_check_dwhdb_size_panic START TIME '00:00 AM' EVERY 1 MINUTES
    DISABLE
HANDLER
BEGIN
    DECLARE mainspace_used_percentage INT;
    DECLARE conncount UNSIGNED INT;
    DECLARE otherversions VARCHAR(50);
    DECLARE logged_datetime TIMESTAMP;

    DECLARE LOCAL TEMPORARY TABLE temp_dwhdb_usage (
        logged_datetime TIMESTAMP,
        mainspace_used_percentage INT,
        connection_count INT,
        other_versions VARCHAR(50)
    );

    INSERT INTO temp_dwhdb_usage LOCATION 'dwhdb.dwhdb' 'SELECT logged_datetime, mainspace_used_percentage, connection_count, other_versions FROM show_db_usage()';
    
    SET mainspace_used_percentage = (SELECT mainspace_used_percentage FROM temp_dwhdb_usage);
    SET conncount = (SELECT connection_count FROM temp_dwhdb_usage);
    SET otherversions = (SELECT other_versions FROM temp_dwhdb_usage);
    SET logged_datetime = (SELECT logged_datetime FROM temp_dwhdb_usage);

    IF (95.0 <= mainspace_used_percentage) THEN
		CALL xp_cmdshell('echo ' || logged_datetime || ' SEVERE: DWHDB is full! ConnCount: ' || conncount || ' MainSpaceUsed: ' || mainspace_used_percentage ||'% OtherVersions: ' || otherversions || ' >> /eniq/log/sw_log/iq/dwhdb_usage.log');
		CALL xp_cmdshell('echo 1 >> /tmp/dwhdb_full');
        CALL xp_cmdshell('/eniq/admin/bin/eniq_service_start_stop.bsh -s engine -a maint');
		ALTER EVENT check_dwhdb_size_panic DISABLE;
    ELSE
		CALL xp_cmdshell('echo ' || logged_datetime || ' WARNING: DWHDB is almost full. ConnCount: ' || conncount || ' MainSpaceUsed: ' || mainspace_used_percentage ||'% OtherVersions: ' || otherversions || ' >> /eniq/log/sw_log/iq/dwhdb_usage.log');
    END IF
END;
