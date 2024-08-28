/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.engine.main;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import com.distocraft.dc5000.common.StaticProperties;
import com.distocraft.dc5000.etl.engine.common.EngineConstants;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ActivateSetInPriorityQueueCommand;import com.distocraft.dc5000.etl.engine.main.engineadmincommands.GetProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeAggregationStatusCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeProfileAndWaitCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ChangeSetPriorityInPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.DisableSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.EnableSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.GiveEngineCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.HoldPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.HoldSetInPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.LockExecutionProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.LoggingStatusCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.PrintSlotInfoCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RefreshDBLookupsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RefreshTransformationsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadAggregationCacheCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadAlarmCacheCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadConfigCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadLoggingCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ReloadProfilesCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RemoveSetFromPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.RestartPriorityQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowDisabledSetsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowSetsInExecutionSlotsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShowSetsInQueueCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShutdownForcefulCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.ShutdownSlowCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartAndWaitSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartSetCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartSetInEngineCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StartSetsCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StatusCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.StopOrShutdownFastCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.UnlockExecutionProfileCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.UpdateTransformationCommand;
import com.distocraft.dc5000.etl.engine.main.engineadmincommands.UpdateThresholdLimit;
import com.distocraft.dc5000.etl.engine.main.exceptions.NoSuchCommandException;

/**
 * @author eemecoy
 *
 */
public class EngineAdminTest {

    private final static Map<String, Class<? extends Command>> commandToClassMap = new HashMap<String, Class<? extends Command>>();

    @BeforeClass
    public static void setUpClass() {
        commandToClassMap.put("stop", StopOrShutdownFastCommand.class);
        commandToClassMap.put("shutdown_fast", StopOrShutdownFastCommand.class);
        commandToClassMap.put("status", StatusCommand.class);
        commandToClassMap.put("shutdown_forceful", ShutdownForcefulCommand.class);
        commandToClassMap.put("shutdown_slow", ShutdownSlowCommand.class);
        commandToClassMap.put("startSetInEngine", StartSetInEngineCommand.class);
        commandToClassMap.put("startSet", StartSetCommand.class);
        commandToClassMap.put("startAndWaitSet", StartAndWaitSetCommand.class);
        commandToClassMap.put("giveEngineCommand", GiveEngineCommand.class);
        commandToClassMap.put("startSets", StartSetsCommand.class);
        commandToClassMap.put("changeProfile", ChangeProfileCommand.class);
        commandToClassMap.put("changeProfileAndWait", ChangeProfileAndWaitCommand.class);
        commandToClassMap.put("reloadProfiles", ReloadProfilesCommand.class);
        commandToClassMap.put("loggingStatus", LoggingStatusCommand.class);
        commandToClassMap.put("holdPriorityQueue", HoldPriorityQueueCommand.class);
        commandToClassMap.put("restartPriorityQueue", RestartPriorityQueueCommand.class);
        commandToClassMap.put("reloadConfig", ReloadConfigCommand.class);
        commandToClassMap.put("reloadAggregationCache", ReloadAggregationCacheCommand.class);
        commandToClassMap.put("reloadLogging", ReloadLoggingCommand.class);
        commandToClassMap.put("reloadAlarmCache", ReloadAlarmCacheCommand.class);
        commandToClassMap.put("showSetsInQueue", ShowSetsInQueueCommand.class);
        commandToClassMap.put("queue", ShowSetsInQueueCommand.class);
        commandToClassMap.put("showSetsInExecutionSlots", ShowSetsInExecutionSlotsCommand.class);
        commandToClassMap.put("slots", ShowSetsInExecutionSlotsCommand.class);
        commandToClassMap.put("removeSetFromPriorityQueue", RemoveSetFromPriorityQueueCommand.class);
        commandToClassMap.put("changeSetPriorityInPriorityQueue", ChangeSetPriorityInPriorityQueueCommand.class);
        commandToClassMap.put("activateSetInPriorityQueue", ActivateSetInPriorityQueueCommand.class);
        commandToClassMap.put("holdSetInPriorityQueue", HoldSetInPriorityQueueCommand.class);
        commandToClassMap.put("changeAggregationStatus", ChangeAggregationStatusCommand.class);
        commandToClassMap.put("unLockExecutionprofile", UnlockExecutionProfileCommand.class);
        commandToClassMap.put("lockExecutionprofile", LockExecutionProfileCommand.class);
        commandToClassMap.put("refreshDBLookups", RefreshDBLookupsCommand.class);
        commandToClassMap.put("refreshTransformations", RefreshTransformationsCommand.class);
        commandToClassMap.put("updateTransformation", UpdateTransformationCommand.class);
        commandToClassMap.put("disableSet", DisableSetCommand.class);
        commandToClassMap.put("enableSet", EnableSetCommand.class);
        commandToClassMap.put("showDisabledSets", ShowDisabledSetsCommand.class);
        commandToClassMap.put("printSlotInfo", PrintSlotInfoCommand.class);
        commandToClassMap.put("currentProfile", GetProfileCommand.class);    
        commandToClassMap.put("updatethresholdLimit", UpdateThresholdLimit.class);
    	
    	}

    @Test
    public void testExceptionThrownWhenNoSuchCommand() throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        final String dummyCommandName = "aDummyCommand";
        try {
            EngineAdmin.createCommand(dummyCommandName, null);
            fail("Exception should have been thrown");
        } catch (final NoSuchCommandException e) {
            assertThat(e.getMessage(), is("Invalid command entered: " + dummyCommandName));
        }
    }

    @Test
    public void testCorrectCommandIsCreated() throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException,
            NoSuchCommandException {

        for (final String command : commandToClassMap.keySet()) {
            final Command commandCreated = EngineAdmin.createCommand(command, new String[] { "commandName" });

            final Object expectedClass = commandToClassMap.get(command);
            assertThat(commandCreated.getClass(), is(expectedClass));
        }
    }  

  @Test
  public void testUpdateThresholdProperty() {

    try {

      final Properties prop = new Properties();
      StaticProperties.giveProperties(prop);

      EngineAdmin testInstance = new EngineAdmin() {

        protected void getProperties() {
          // do nothing for test.
        }

        protected void setStaticProperty(final int numberOfMinutes, String name) {
          prop.setProperty(name, Integer.toString(numberOfMinutes));
        }

        protected void refreshStaticProperties() throws Exception {
          // do nothing for test.
        }
      };

      int testInt = 150;

      String name = EngineConstants.THRESHOLD_NAME;

      testInstance.updateThresholdProperty(testInt);

      int result = Integer.parseInt(prop.getProperty(name));

      assertEquals("The 2 values should be equal", testInt, result);

    } catch (Exception exc) {

      fail("Updating the threshold property should not fail: " + exc.toString());

    }
  }

}
