/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.engine.main.engineadmincommands;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.etl.engine.BaseMock;
import com.distocraft.dc5000.etl.engine.main.EngineAdmin;

/**
 * @author eemecoy
 *
 */
public class DisableSetCommandTest extends BaseMock {

    EngineAdmin mockedEngineAdmin;

    @Before
    public void setUp() {
        mockedEngineAdmin = context.mock(EngineAdmin.class);
    }

    @Test
    public void testPerformCommandForTechPack() throws NumberFormatException, Exception {
        final String techPackName = "techPack";
        final String[] validNumberArguments = new String[] { "disableSet", techPackName };
        final Command command = new StubbedDisableSetCommand(validNumberArguments);
        expectDisableTechPackCommandOnEngineAdmin(techPackName);
        command.validateArguments();
        command.performCommand();
    }

    private void expectDisableTechPackCommandOnEngineAdmin(final String techpackName) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedEngineAdmin).disableTechpack(techpackName);
            }
        });

    }

    @Test
    public void testPerformCommandForSet() throws NumberFormatException, Exception {
        final String techPackName = "techPack";
        final String setName = "set name";
        final String[] validNumberArguments = new String[] { "disableSet", techPackName, setName };
        final Command command = new StubbedDisableSetCommand(validNumberArguments);
        expectDisableSetCommandOnEngineAdmin(techPackName, setName);
        command.validateArguments();
        command.performCommand();
    }

    @Test
    public void testPerformCommandForAction() throws NumberFormatException, Exception {
        final String techPackName = "techPack";
        final String setName = "some set";
        final int actionOrder = 4;
        final String[] validNumberArguments = new String[] { "disableSet", techPackName, setName,
                Integer.toString(actionOrder) };
        final Command command = new StubbedDisableSetCommand(validNumberArguments);
        expectDisableActionCommandOnEngineAdmin(techPackName, setName, actionOrder);
        command.validateArguments();
        command.performCommand();
    }

    private void expectDisableActionCommandOnEngineAdmin(final String techpackName, final String setName,
            final int actionOrder) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedEngineAdmin).disableAction(techpackName, setName, actionOrder);
            }
        });

    }

    private void expectDisableSetCommandOnEngineAdmin(final String techPackName, final String setName) throws Exception {
        context.checking(new Expectations() {
            {
                one(mockedEngineAdmin).disableSet(techPackName, setName);
            }
        });

    }

    @Test
    public void testCheckArgumentsWith5Arguments() {
        final String[] invalidNumberArguments = new String[] { "onearg", "onearg", "onearg", "onearg", "onearg" };
        try {
            new DisableSetCommand(invalidNumberArguments).validateArguments();
            fail("Exception should have been thrown");
        } catch (final InvalidArgumentsException e) {
            assertThat(
                    e.getMessage(),
                    is("Incorrect number of arguments supplied, usage: engine -e disableSet techPackName(string) or engine -e disableSet techPackName(string) setName(string) or engine -e disableSet techPackName(string) setName(string) actionNumber(number)"));
        }
    }

    @Test
    public void testCheckArgumentsWith1Argument() {
        final String[] invalidNumberArguments = new String[] { "onearg" };
        try {
            new DisableSetCommand(invalidNumberArguments).validateArguments();
            fail("Exception should have been thrown");
        } catch (final InvalidArgumentsException e) {
            assertThat(
                    e.getMessage(),
                    is("Incorrect number of arguments supplied, usage: engine -e disableSet techPackName(string) or engine -e disableSet techPackName(string) setName(string) or engine -e disableSet techPackName(string) setName(string) actionNumber(number)"));
        }
    }

    @Test
    public void testCheckArgumentsWithCorrectNumArguments2DoesntThrowException() throws InvalidArgumentsException {

        final String[] validNumberArguments = new String[] { "disableSet", "techPackName" };
        new DisableSetCommand(validNumberArguments).validateArguments();
    }

    @Test
    public void testCheckArgumentsWithCorrectNumArguments3DoesntThrowException() throws InvalidArgumentsException {

        final String[] validNumberArguments = new String[] { "disableSet", "techPackName", "setName" };
        new DisableSetCommand(validNumberArguments).validateArguments();
    }

    @Test
    public void testCheckArgumentsWithCorrectNumArguments4DoesntThrowException() throws InvalidArgumentsException {

        final String[] validNumberArguments = new String[] { "disableSet", "techPackName", "setName", "4" };
        new DisableSetCommand(validNumberArguments).validateArguments();
    }

    @Test
    public void testCheckArgumentsWithCorrectNumArguments4ButTypeOfIntegerIsWrong() {

        final String[] validNumberArguments = new String[] { "disableSet", "techPackName", "setName",
                "this should be a number" };
        try {
            new DisableSetCommand(validNumberArguments).validateArguments();
            fail("Exception should have been thrown");
        } catch (final InvalidArgumentsException e) {
            assertThat(
                    e.getMessage(),
                    is("ActionNumber must be of type integer, usage: engine -e disableSet techPackName(string) or engine -e disableSet techPackName(string) setName(string) or engine -e disableSet techPackName(string) setName(string) actionNumber(number)"));
        }
    }

    class StubbedDisableSetCommand extends DisableSetCommand {

        /**
         * @param args
         */
        public StubbedDisableSetCommand(final String[] args) {
            super(args);
        }

        /* (non-Javadoc)
         * @see com.distocraft.dc5000.etl.engine.main.engineadmincommands.UndisableSetCommand#createNewEngineAdmin()
         */
        @Override
        protected EngineAdmin createNewEngineAdmin() {
            return mockedEngineAdmin;
        }

    }

}
