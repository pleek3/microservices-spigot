package com.pleek3.minecraft.core.command;

import com.pleek3.minecraft.core.command.cooldown.CommandExecutionCooldown;
import com.pleek3.minecraft.core.command.meta.CommandMeta;
import com.pleek3.minecraft.core.command.meta.SubCommandMeta;
import com.pleek3.minecraft.core.module.model.ModuleAdapter;
import com.pleek3.minecraft.core.utils.Services;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CoreCommand extends org.bukkit.command.Command {

    private static final CommandMap CUSTOM_COMMAND_MAP = new CommandMap(Bukkit.getServer());

    public static Object OLD_COMMAND_MAP = null;

    static {
        try {
            Field commandMapField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            OLD_COMMAND_MAP = commandMapField.get(Bukkit.getServer());


            Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(knownCommandsField, knownCommandsField.getModifiers() & ~Modifier.FINAL);

            knownCommandsField.set(CUSTOM_COMMAND_MAP, knownCommandsField.get(OLD_COMMAND_MAP));
            commandMapField.set(Bukkit.getServer(), CUSTOM_COMMAND_MAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final CommandMeta commandMeta;
    private final ModuleAdapter module;

    protected CoreCommand(ModuleAdapter module, String name) {
        super(name);
        this.commandMeta = new CommandMeta(this);
        this.module = module;
        setAliases(Arrays.asList(Arrays.copyOfRange(this.commandMeta.getCommandAliases(),
                1,
                this.commandMeta.getCommandAliases().length)));
        register();
    }

    public void register() {
        if (CUSTOM_COMMAND_MAP.getCommand(this.getName()) != null) {
            Bukkit.getLogger()
                    .info("Command: " + this.getName() + " double register detected, removing first register");
            try {
                unregisterCommand();
            } catch (Exception ignore) {
            }
        }

        CUSTOM_COMMAND_MAP.register((this.module == null ? "core" : this.module.getModuleData().name()), this);
        Bukkit.broadcastMessage("debug registered");
    }

    public void unregisterCommand() throws NoSuchFieldException, IllegalAccessException {
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        knownCommandsField.setAccessible(true);
        @SuppressWarnings("unchecked") Map<String, Command> knownCommands = (Map<String, Command>) knownCommandsField.get(
                CUSTOM_COMMAND_MAP);

        Command command = knownCommands.get(this.getName());
        if (command != null) {
            command.unregister(CUSTOM_COMMAND_MAP);
            knownCommands.remove(this.getName());
        }
    }

    public void execute(Player player, String[] arguments) {
        if (arguments.length == 0) {
            player.sendMessage(generateDefaultUsage(null, ""));
            return;
        }

        String baseCommandInput = arguments[0];
        SubCommandMeta subCommandMeta = this.commandMeta.getSubCommandMeta(baseCommandInput);

        if (subCommandMeta == null) {
            player.sendMessage(generateDefaultUsage(null, baseCommandInput));
            return;
        }

        String permissionString = subCommandMeta.getPermissionString();

        if (!player.hasPermission(permissionString)) {
            player.sendMessage(this.generateDefaultPermission());

            return;
        }

        CommandExecutionCooldown commandCooldown = Services.getCommandService.getCooldown(player);

        if (!commandCooldown.canExecute()) {
            player.sendMessage("Warte noch einen Moment...");
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String args : arguments) {
            builder.append(args);

            String[] array = Arrays.copyOfRange(arguments, 1, arguments.length);

            if (!this.commandMeta.getSubCommandMeta(builder.toString()).execute(player, array))
                player.sendMessage(generateDefaultUsage(subCommandMeta, baseCommandInput));
            break;

        }
    }

    public boolean canAccess(Player player) {
        if ("".equalsIgnoreCase(this.commandMeta.getCommandPermission()))
            return true;

        return player.hasPermission(this.commandMeta.getCommandPermission());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        execute((Player) commandSender, strings);
        return false;
    }

    public CommandMeta getCommandMeta() {
        return commandMeta;
    }

    /*
    Permission Nachricht in den Properties anpassbar machen.
    Verschiedene Placeholder verwenden:
    $prefix$ → Module Prefix
 */
    private String generateDefaultPermission() {
        return "Dazu hast du keine Berechtigung!";
    }

    /*
    Usage Nachricht in den Properties anpassbar machen.
    Verschiedene Placeholder verwenden:
    $name$ → Command
    $param$ → Command ParameterInfo
    $prefix$ → Module Prefix
 */
    private String generateDefaultUsage(SubCommandMeta subCommand, String label) {
        if (subCommand == null) {
            StringBuilder builder = new StringBuilder();
            AtomicInteger index = new AtomicInteger();

            this.commandMeta.getSubCommandMeta().forEach((s, subCommandMeta) -> {
                builder.append("Verwendung: /")
                        .append(this.getName())
                        .append(" ")
                        .append(subCommandMeta.getDefaultAlias())
                        .append(" ")
                        .append(subCommandMeta.getParameterString().replace("{", "<").replace("}", ">"));
                index.getAndIncrement();
                if (index.get() < this.commandMeta.getSubCommandMeta().size())
                    builder.append("\n");
            });

            return builder.toString();
        }

        return "Verwendung: /" + this.getName() + " " + label + " " + subCommand.getSubCommand()
                .parameters()
                .replace("{", "<")
                .replace("}", ">");
    }

}
