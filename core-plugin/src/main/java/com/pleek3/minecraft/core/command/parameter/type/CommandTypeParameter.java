package com.pleek3.minecraft.core.command.parameter.type;

import com.pleek3.minecraft.core.command.parser.CommandParameterParser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public abstract class CommandTypeParameter<T> implements CommandParameterParser<T> {

    public abstract List<String> tabComplete(Player player, Set<String> flags, String source);

    public boolean shouldLowerCase() {
        return true;
    }

}
