package com.integratedmind;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(IntegratedMindMod.MODID);

    public static final DeferredBlock<RedstoneToPrecisionBlock> REDSTONE_TO_PRECISION = BLOCKS.register("redstone_to_precision",
            (Supplier<RedstoneToPrecisionBlock>) () -> new RedstoneToPrecisionBlock());

    public static final DeferredBlock<PrecisionMemoryBlock> PRECISION_MEMORY = BLOCKS.register("precision_memory",
            (Supplier<PrecisionMemoryBlock>) () -> new PrecisionMemoryBlock());

    public static final DeferredBlock<NeuronBlock> NEURON_BLOCK = BLOCKS.register("neuron_block",
            (Supplier<NeuronBlock>) () -> new NeuronBlock());

    public static final DeferredBlock<InputDendriteBlock> INPUT_DENDRITE = BLOCKS.register("input_dendrite",
            (Supplier<InputDendriteBlock>) () -> new InputDendriteBlock());

    public static final DeferredBlock<OutputDendriteBlock> OUTPUT_DENDRITE = BLOCKS.register("output_dendrite",
            (Supplier<OutputDendriteBlock>) () -> new OutputDendriteBlock());

    public static final DeferredBlock<DoctrineWatcherBlock> DOCTRINE_WATCHER = BLOCKS.register("doctrine_watcher",
            (Supplier<DoctrineWatcherBlock>) () -> new DoctrineWatcherBlock());

    public static final DeferredBlock<SmartBubbleBlock> SMART_BUBBLE = BLOCKS.register("smart_bubble",
            (Supplier<SmartBubbleBlock>) () -> new SmartBubbleBlock());

    public static void registerBlockItems() {
        ModItems.ITEMS.register("redstone_to_precision", () -> new BlockItem(REDSTONE_TO_PRECISION.get(), new Item.Properties()));
        ModItems.ITEMS.register("precision_memory", () -> new BlockItem(PRECISION_MEMORY.get(), new Item.Properties()));
        ModItems.ITEMS.register("neuron_block", () -> new BlockItem(NEURON_BLOCK.get(), new Item.Properties()));
        ModItems.ITEMS.register("input_dendrite", () -> new BlockItem(INPUT_DENDRITE.get(), new Item.Properties()));
        ModItems.ITEMS.register("output_dendrite", () -> new BlockItem(OUTPUT_DENDRITE.get(), new Item.Properties()));
        ModItems.ITEMS.register("doctrine_watcher", () -> new BlockItem(DOCTRINE_WATCHER.get(), new Item.Properties()));
        ModItems.ITEMS.register("smart_bubble", () -> new SmartBubbleItem(SMART_BUBBLE.get(), new Item.Properties()));
    }
}
