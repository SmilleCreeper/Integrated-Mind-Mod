package com.integratedmind;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, IntegratedMindMod.MODID);

    public static final Supplier<BlockEntityType<PrecisionMemoryBlockEntity>> PRECISION_MEMORY =
            BLOCK_ENTITIES.register("precision_memory",
                    () -> BlockEntityType.Builder.of(PrecisionMemoryBlockEntity::new, ModBlocks.PRECISION_MEMORY.get()).build(null));

    public static final Supplier<BlockEntityType<NeuronBlockEntity>> NEURON_BLOCK =
            BLOCK_ENTITIES.register("neuron_block",
                    () -> BlockEntityType.Builder.of(NeuronBlockEntity::new, ModBlocks.NEURON_BLOCK.get()).build(null));

    public static final Supplier<BlockEntityType<InputDendriteBlockEntity>> INPUT_DENDRITE =
            BLOCK_ENTITIES.register("input_dendrite",
                    () -> BlockEntityType.Builder.of(InputDendriteBlockEntity::new, ModBlocks.INPUT_DENDRITE.get()).build(null));

    public static final Supplier<BlockEntityType<DoctrineWatcherBlockEntity>> DOCTRINE_WATCHER =
            BLOCK_ENTITIES.register("doctrine_watcher",
                    () -> BlockEntityType.Builder.of(DoctrineWatcherBlockEntity::new, ModBlocks.DOCTRINE_WATCHER.get()).build(null));
}
