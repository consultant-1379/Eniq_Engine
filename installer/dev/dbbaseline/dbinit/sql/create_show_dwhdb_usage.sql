IF(SELECT COUNT(*) FROM sys.sysprocedure WHERE proc_name = 'show_db_usage')>0 THEN
    DROP PROCEDURE show_db_usage;
END IF;

CREATE PROCEDURE show_db_usage()
    RESULT ( logged_datetime timestamp, mainspace_used_percentage int, connection_count int, other_versions varchar(50))
    ON EXCEPTION RESUME
BEGIN
    DECLARE mainspace UNSIGNED BIGINT;
    DECLARE mainspace_used UNSIGNED BIGINT;
    DECLARE tempspace UNSIGNED BIGINT;
    DECLARE tempspace_used UNSIGNED BIGINT;
    DECLARE mainspace_used_percentage NUMERIC;
    DECLARE connection_count INT;
    DECLARE other_versions VARCHAR(50);

    CALL SP_IQSPACEUSED(mainspace,mainspace_used,tempspace,tempspace_used);
    SET mainspace_used_percentage = ROUND((mainspace_used*100/mainspace),0);
    SET connection_count = DB_PROPERTY('ConnCount');
    SET other_versions = (SELECT stat.Value FROM sp_iqstatus() AS stat WHERE name = ' Other Versions:');

    SELECT DATEFORMAT(NOW(), 'yyyy-mm-dd hh:nn:ss') AS logged_datetime, CAST(mainspace_used_percentage AS INT) AS mainspace_used_percentage, connection_count, other_versions;
END;