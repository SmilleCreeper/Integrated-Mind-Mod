package com.integratedmind;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, IntegratedMindMod.MODID);

    public static final Supplier<CreativeModeTab> INTEGRATED_MIND_TAB = CREATIVE_MODE_TABS.register("integrated_mind",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.BRAIN_UNIT.get()))
                    .title(Component.translatable("creativetab.integrated_mind"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BRAIN_UNIT.get());
                        output.accept(ModItems.AXON_WIRE.get());
                        output.accept(ModItems.NEURON_HUB.get());
                        output.accept(ModItems.SENSORY_RECEPTOR.get());
                        output.accept(ModItems.GLANDS_SAC.get());
                        output.accept(ModItems.TUNE_MEMBRANE.get());
                        output.accept(ModItems.TIMBRE_MUSCLE.get());
                        output.accept(ModItems.HEART_PUMP.get());
                        output.accept(ModItems.PONDER_KIDNEY.get());
                        output.accept(ModBlocks.REDSTONE_TO_PRECISION);
                        output.accept(ModBlocks.PRECISION_MEMORY);
                        output.accept(ModBlocks.NEURON_BLOCK);
                        output.accept(ModBlocks.INPUT_DENDRITE);
                        output.accept(ModBlocks.OUTPUT_DENDRITE);
                        output.accept(ModItems.PERFECT_WIRE.get());
                        output.accept(ModBlocks.DOCTRINE_WATCHER);
                        output.accept(ModBlocks.SMART_BUBBLE);
                    })
                    .build());
}
