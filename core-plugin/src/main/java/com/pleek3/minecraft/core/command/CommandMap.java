package com.pleek3.minecraft.core.command;

import com.google.common.collect.ImmutableSet;
import com.pleek3.minecraft.core.command.meta.CommandMeta;
import com.pleek3.minecraft.core.command.meta.SubCommandMeta;
import com.pleek3.minecraft.core.utils.Services;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandMap extends SimpleCommandMap {

    public CommandMap(Server server) {
        super(server);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine) {
        return this.tabComplete(sender, cmdLine, null);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmdLine, Location location) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        Player player = (Player) sender;

        boolean doneHere;

        Set<String> completions = new HashSet<>();
        Set<String[]> multipleArgumentList = new HashSet<>();

        int index = cmdLine.split(" ").length; //run integer to get current location of our string
        String inputString = cmdLine.toLowerCase();
        String[] input = cmdLine.split(" ");
        int spaceIndex = cmdLine.indexOf(" ");

        for (final CoreCommand coreCommand : Services.getCommandService.getCommands()) {
            if (!coreCommand.canAccess(player)) continue;

            CommandMeta meta = coreCommand.getCommandMeta();

            for (final String command : meta.getCommandAliases()) {
                if (spaceIndex < 0 && inputString.length() < command.length() && StringUtils.startsWithIgnoreCase(
                        command,
                        inputString)) {  //If we are in the main command (no space), we add the all commands to the completion
                    completions.add("/" + command.toLowerCase());
                }

                //check if our input starts with command
                if (!inputString.startsWith(command.toLowerCase() + " ")) {
                    continue;
                }

                //iterate over all subcommands and checks if player can access this
                for (SubCommandMeta subCommand : coreCommand.getCommandMeta().getSubCommandMeta().values()) {
                    if (!subCommand.canAccess(player)) {
                        continue;
                    }
                    //We search all aliases from the subcommand and see if it is a single subcommand (create, delete) or multiple (option set, option remove)
                    for (String subCommandAlias : subCommand.getAliases()) {
                        String[] argumentSplit = subCommandAlias.split(" ");
                        if (argumentSplit.length > 1) {
                            multipleArgumentList.add(argumentSplit);

                            // hallo server
                            // hallo spieler

                            // 0 == hallo; 1 == subcommand

                            completions.add(argumentSplit[0]);
                        } else
                            completions.add(subCommandAlias);
                    }

                    //We make a copy of our completions list and iterate through it
                    for (String next : new ArrayList<>(completions)) {
                        String current = input[index - 1]; //We always want the first element of the array

                        //We check if our current element does not match the next entry of the completion list and is not our main command
                        if ((!current.startsWith(next) && !current.equalsIgnoreCase(command))) {
                            completions.remove(next);
                        }

                        //We look if our current element is a SubCommand with multiple arguments(e.g. option set)
                        if (hasMultipleArguments(current, multipleArgumentList)) {
                            //We look at all the arguments of the SubCommand and see if the first element is our next entry.... If not, we remove this
                            for (String[] multipleArg : multipleArgumentList) {
                                if (!multipleArg[0].startsWith(next)) {
                                    completions.remove(next);
                                }
                                //If the first element is our next entry, we remove it and add all further SubCommand arguments to the completion list
                                completions.remove(next);
                                completions.addAll(Arrays.asList(Arrays.copyOfRange(multipleArg,
                                        1,
                                        multipleArg.length)));
                            }
                        }
                    }
                }
            }
        }

        List<String> completionList = new ArrayList<>(completions);
        completionList.sort(Comparator.comparingInt(String::length));

        return completionList;
    }

    public boolean hasMultipleArguments(String value, Set<String[]> lis) {
        for (String[] array : lis)
            return (array[0].startsWith(value));

        return false;
    }


    public static List<String> tabCompleteParameter(Player sender, String parameter, Class<?> transformTo, String[] tabCompleteFlags) {
        if (!Services.getCommandService.getCommandTypeParameters().containsKey(transformTo)) {
            return (new ArrayList<>());
        }

        return Services.getCommandService.getParameter(transformTo)
                .tabComplete(sender, ImmutableSet.copyOf(tabCompleteFlags), parameter);
    }


}
