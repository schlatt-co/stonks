package dev.tycho.stonks.command;

import dev.tycho.stonks.command.base.CommandBase;
import dev.tycho.stonks.command.company.ListCommandSub;

public class MainCommand extends CommandBase {

  public MainCommand() {
    super("command", new ListCommandSub());
  }
}
