package dev.tycho.stonks.command.base;

import dev.tycho.stonks.command.base.validators.ArgumentValidator;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ModularCommandSub extends CommandSub {
  final ArgumentValidator[] arguments;

  protected ModularCommandSub(ArgumentValidator argument, ArgumentValidator... arguments) {
    this.arguments = new ArgumentValidator[arguments.length + 1];
    this.arguments[0] = argument;
    for (int i = 1; i < this.arguments.length; i++) {
      this.arguments[i] = arguments[i - 1];
    }
  }

  public final void onCommand(Player player, String alias, String[] args) {
    //If there aren't enough arguments then send the correct usage and return

    //Validate each argument
    for (int i = 0; i < arguments.length; i++) {
      ArgumentValidator argument = arguments[i];
      if (i >= args.length - 1) {
        //We don't have enough args
        if (argument.isOptional()) {
          break;
        } else {
          sendCorrectUsage(player, alias);
          return;
        }
      }


      //If this is the last argument and it wants concatenated strings
      String argString;
      if (i == arguments.length - 1 && argument.concatIfLastArg()) {
        argString = concatArgs(i + 1, args);
      } else {
        argString = args[i + 1];
      }
      if (!argument.provide(argString)) {
        sendMessage(player, argument.getUsage());
        return;
      }
    }

    //At this point, all are validated and have a value
    execute(player);
  }

  private final void sendCorrectUsage(Player player, String alias) {
    String usage = "Correct usage:";
    for (ArgumentValidator argument : arguments) {
      usage += " " + argument.getUsage();
    }
  }


  protected final <T> T getArgument(String name) {
    for (ArgumentValidator argument : arguments) {
      if (argument.getName().equals(name)) {
        return (T) argument.get();
      }
    }
    throw new IllegalArgumentException("Argument with name not found");
  }

  public abstract void execute(Player player);

  @Override
  public List<String> onTabComplete(CommandSender sender, String alias, String[] args) {
    return null;
  }

}
