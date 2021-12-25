package com.pleek3.test;

import com.pleek3.minecraft.core.annotations.Command;
import com.pleek3.minecraft.core.annotations.SubCommand;
import com.pleek3.minecraft.core.command.CoreCommand;
import org.bukkit.entity.Player;

@Command(commands = {"test", "te"}, permission = "test.command", description = "test command")
public class TestCommand extends CoreCommand {

    public TestCommand() {
        super(null, "test");
    }

    @SubCommand(command = {"send", "s"}, parameters = "{Message}")
    public void test(Player player, String value) {
        player.sendMessage("Test: " + value);
    }

    @SubCommand(command = {"create", "erstellen"}, parameters = "{Name}")
    public void create(Player player, String value) {
        player.sendMessage("Create: " + value);
    }

    @SubCommand(command = {"delete", "löschen"}, parameters = "{Name}")
    public void delete(Player player, String value) {
        player.sendMessage("Delete: " + value);
    }

    @SubCommand(command = {"hallo spieler"}, parameters = "{Spieler}")
    public void helloPlayer(Player player, Player target) {
        player.sendMessage("Hallo " + target.getName());
    }
}
