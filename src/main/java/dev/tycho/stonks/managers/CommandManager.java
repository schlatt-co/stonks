package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.command.chat.CompanyChatCommand;
import dev.tycho.stonks.command.chat.CompanyChatReplyCommand;
import dev.tycho.stonks.command.stonks.StonksCommand;
import dev.tycho.stonks.util.Util;

public class CommandManager extends SpigotModule {

  private static CommandManager instance;

  private final StonksCommand stonksCommand = new StonksCommand();

  public CommandManager(Stonks stonks) {
    super("Command Manager", stonks);
    instance = this;
  }

  @Override
  public void addCommands() {
    addCommand("cc", new CompanyChatCommand());
    addCommand("ccr", new CompanyChatReplyCommand());
  }

  public static CommandManager getInstance() {
    if (!Util.isCalledInternally()) {
      throw new RuntimeException("Improper use of internal stonks classes.");
    }
    return instance;
  }

  /**
   * Registers a Stonks CommandSub.
   * @param alias The name of the CommandSub.
   * @param commandSub The CommandSub object.
   * @return true if this operation overwrote an existing command sub, otherwise false.
   */
  public boolean registerStonksCommand(String alias, CommandSub commandSub) {
    return stonksCommand.addSubCommand(alias, commandSub);
  }

}
