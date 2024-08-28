#!/bin/bash

/dc/java/bin/java -classpath lib/testhelper_5-0-0b14.jar:lib/dbunit-2.1_dc.jar:lib/engine_5-0-0b235.jar:lib/jconn3.jar:lib/repository_5-0-0b85.jar com.distocraft.dc5000.etl.TestHelper $1 $2 $3
