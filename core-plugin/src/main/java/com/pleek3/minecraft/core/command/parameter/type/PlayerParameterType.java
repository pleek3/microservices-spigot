package com.pleek3.minecraft.core.command.parameter.type;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PlayerParameterType extends CommandTypeParameter<Player> {
    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Bukkit.getOnlinePlayers().stream().filter(player::canSee).map(HumanEntity::getName)
                .filter(name -> name.toLowerCase().startsWith(source)).collect(Collectors.toList());
    }

    @Override
    public Player parse(Player player, String value) {
        return (value.equalsIgnoreCase("self") ? player : Bukkit.getPlayer(value));
    }
}
