
/*
 * Copyright (C) 2014 Archie L. Cobbs. All rights reserved.
 *
 * $Id$
 */

package org.jsimpledb.cli;

import java.util.SortedMap;
import java.util.TreeMap;

import org.jsimpledb.util.ParseContext;

public final class CommandParser {

    private static final TreeMap<String, Command> COMMANDS = new TreeMap<>();

    static {
        CommandParser.registerCommand(new CountCommand());
        CommandParser.registerCommand(new CreateCommand());
        CommandParser.registerCommand(new DeleteCommand());
        CommandParser.registerCommand(new DupCommand());
        CommandParser.registerCommand(new ExportCommand());
        CommandParser.registerCommand(new GetCommand());
        CommandParser.registerCommand(new HelpCommand());
        CommandParser.registerCommand(new ImportCommand());
        CommandParser.registerCommand(new IterateCommand());
        CommandParser.registerCommand(new LimitCommand());
        CommandParser.registerCommand(new ObjectCommand());
        CommandParser.registerCommand(new PrintCommand());
        CommandParser.registerCommand(new PopCommand());
        CommandParser.registerCommand(new SetAllowNewSchemaCommand());
        CommandParser.registerCommand(new SetSchemaVersionCommand());
        CommandParser.registerCommand(new ShowAllSchemasCommand());
        CommandParser.registerCommand(new ShowSchemaCommand());
        CommandParser.registerCommand(new StackCommand());
        CommandParser.registerCommand(new SwapCommand());
    }

    private CommandParser() {
    }

    public static SortedMap<String, Command> getCommands() {
        return COMMANDS;
    }

    public static void registerCommand(Command command) {
        COMMANDS.put(command.getName(), command);
    }

    public static Action parse(Session session, ParseContext ctx, boolean complete) {
        return new CommandNameParser().parse(session, ctx, complete).parse(session, ctx, complete);
    }

// CommandNameParser

    public static class CommandNameParser implements Parser<Command> {

        @Override
        public Command parse(Session session, ParseContext ctx, boolean complete) {

            // Get specified command
            final String commandName = ctx.matchPrefix("[^\\s;]*").group();

            // Find specified command
            final Command command = CommandParser.COMMANDS.get(commandName);
            if (command == null) {
                throw new ParseException(ctx, "unknown command `" + commandName + "'")
                  .addCompletions(Util.complete(CommandParser.COMMANDS.keySet(), commandName));
            }

            // Done
            return command;
        }
    };
}
