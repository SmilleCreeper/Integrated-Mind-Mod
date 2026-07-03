package com.integratedmind;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(IntegratedMindMod.MODID);

    public static final DeferredItem<Item> BRAIN_UNIT = ITEMS.register("brain_unit",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> AXON_WIRE = ITEMS.register("axon_wire",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> NEURON_HUB = ITEMS.register("neuron_hub",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> SENSORY_RECEPTOR = ITEMS.register("sensory_receptor",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GLANDS_SAC = ITEMS.register("glands_sac",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TUNE_MEMBRANE = ITEMS.register("tune_membrane",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> TIMBRE_MUSCLE = ITEMS.register("timbre_muscle",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> HEART_PUMP = ITEMS.register("heart_pump",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PONDER_KIDNEY = ITEMS.register("ponder_kidney",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> PERFECT_WIRE = ITEMS.register("perfect_wire",
            () -> new Item(new Item.Properties()));
}
