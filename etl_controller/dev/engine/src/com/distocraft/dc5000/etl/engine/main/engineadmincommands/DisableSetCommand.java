/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.distocraft.dc5000.etl.engine.main.engineadmincommands;

import com.distocraft.dc5000.etl.engine.main.EngineAdmin;

/**
 * @author eemecoy
 *
 */
public class DisableSetCommand extends Command {

    private String techPackName;

    private String setName;

    private Integer actionNumber;

    /**
     * @param args
     */
    public DisableSetCommand(final String[] args) {
        super(args);

    }

    /* (non-Javadoc)
     * @see com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command#checkAndConvertArgumentTypes()
     */
    @Override
    void checkAndConvertArgumentTypes() throws InvalidArgumentsException {
        techPackName = arguments[1];

        if (arguments.length >= 3) {
            setName = arguments[2];
        }

        if (arguments.length >= 4) {
            try {
                actionNumber = new Integer(arguments[3]);
            } catch (final NumberFormatException e) {
                throw new InvalidArgumentsException("ActionNumber must be of type integer, usage: engine -e "
                        + getUsageMessage());
            }
        }

    }

    /* (non-Javadoc)
     * @see com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command#getCorrectArgumentsLength()
     */
    @Override
    protected int getCorrectArgumentsLength() {
        //not used here, special case, see overridden method, checkNumberOfArguments()
        return 0;
    }

    /* (non-Javadoc)
     * @see com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command#checkNumberOfArguments()
     */
    @Override
    void checkNumberOfArguments() throws InvalidArgumentsException {
        if (arguments.length < 2 || arguments.length > 4) {
            throw new InvalidArgumentsException("Incorrect number of arguments supplied, usage: engine -e "
                    + getUsageMessage());
        }
    }

    /* (non-Javadoc)
     * @see com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command#getUsageMessage()
     */
    @Override
    String getUsageMessage() {
        return "disableSet techPackName(string) " + "or engine -e disableSet techPackName(string) setName(string) "
                + "or engine -e disableSet techPackName(string) setName(string) actionNumber(number)";
    }

    /* (non-Javadoc)
     * @see com.distocraft.dc5000.etl.engine.main.engineadmincommands.Command#performCommand()
     */
    @Override
    public void performCommand() throws Exception {
        final EngineAdmin admin = createNewEngineAdmin();

        if (actionNumber != null) {
            admin.disableAction(techPackName, setName, actionNumber);
        } else if (setName != null) {
            admin.disableSet(techPackName, setName);
        } else {
            admin.disableTechpack(techPackName);
        }

    }
}
