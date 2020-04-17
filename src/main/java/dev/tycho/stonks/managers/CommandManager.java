package dev.tycho.stonks.managers;

import dev.tycho.stonks.Stonks;
import dev.tycho.stonks.command.base.CommandSub;
import dev.tycho.stonks.command.chat.CompanyChatCommand;
import dev.tycho.stonks.command.chat.CompanyChatReplyCommand;
import dev.tycho.stonks.command.stonks.StonksCommand;

import java.util.Objects;

public class CommandManager extends SpigotModule {
  private static CommandManager instance;
  public static CommandManager getInstance() {
    return instance;
  }

  private StonksCommand stonksCommand;

  public CommandManager(Stonks stonks) {
    super("Command Manager", stonks);
    instance = this;

    stonksCommand = new StonksCommand();
    Objects.requireNonNull(plugin.getCommand("cc")).setExecutor(new CompanyChatCommand());
    Objects.requireNonNull(plugin.getCommand("ccr")).setExecutor(new CompanyChatReplyCommand());
  }

  // Returns true if overwritten
  // False if just created
  public boolean registerStonksCommand(String alias, CommandSub commandSub) {
    return stonksCommand.addSubCommand(alias, commandSub);
  }

}
