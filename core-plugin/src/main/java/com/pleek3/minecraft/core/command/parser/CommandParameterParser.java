package com.pleek3.minecraft.core.command.parser;

import org.bukkit.entity.Player;

public interface CommandParameterParser<T> {

    T parse(Player player, String value);

}
