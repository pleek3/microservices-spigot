package com.pleek3.minecraft.core.command.cooldown;

import com.pleek3.minecraft.core.utils.Cooldown;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommandExecutionCooldown {

    public static final int DURATION = 700;
    private Cooldown commandCooldown;

    public CommandExecutionCooldown(boolean set) {
        if (set)
            this.commandCooldown = new Cooldown(DURATION);
    }

    public boolean canExecute() {
        if (this.commandCooldown == null) {
            this.commandCooldown = new Cooldown(DURATION);
            return true;
        }

        if (this.commandCooldown.hasExpired()) {
            this.commandCooldown = new Cooldown(DURATION);
            return true;
        }

        return false;
    }
}
