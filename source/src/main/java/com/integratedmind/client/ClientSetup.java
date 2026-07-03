package com.integratedmind.client;

import com.integratedmind.ModBlockEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = "integrated_mind", value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.PRECISION_MEMORY.get(), PrecisionMemoryRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.NEURON_BLOCK.get(), NeuronBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.INPUT_DENDRITE.get(), InputDendriteRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.DOCTRINE_WATCHER.get(), DoctrineWatcherRenderer::new);
    }
}
