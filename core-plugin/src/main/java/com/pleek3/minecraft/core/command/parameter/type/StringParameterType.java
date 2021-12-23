package com.pleek3.minecraft.core.command.parameter.type;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class StringParameterType extends CommandTypeParameter<String> {

    @Override
    public List<String> tabComplete(Player player, Set<String> flags, String source) {
        return Collections.emptyList();
    }

    @Override
    public String parse(Player player, String value) {
        return value;
    }
}
