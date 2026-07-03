package com.integratedmind;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ConnectionHandler {
    private static final Map<UUID, BlockPos> PENDING = new HashMap<>();
    private static final Map<UUID, Float> WEIGHTS = new HashMap<>();

    public static void setPending(Player player, BlockPos pos, float weight) {
        PENDING.put(player.getUUID(), pos);
        WEIGHTS.put(player.getUUID(), weight);
    }

    public static boolean hasPending(Player player) {
        return PENDING.containsKey(player.getUUID());
    }

    public static BlockPos getPending(Player player) {
        return PENDING.get(player.getUUID());
    }

    public static float getWeight(Player player) {
        return WEIGHTS.getOrDefault(player.getUUID(), 0.0f);
    }

    public static void clearPending(Player player) {
        PENDING.remove(player.getUUID());
        WEIGHTS.remove(player.getUUID());
    }
}
