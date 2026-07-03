package com.integratedmind;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod(IntegratedMindMod.MODID)
public class IntegratedMindMod {
    public static final String MODID = "integrated_mind";
    private static final Logger LOGGER = LogUtils.getLogger();

    public IntegratedMindMod(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.registerBlockItems();
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTab.CREATIVE_MODE_TABS.register(modEventBus);
        ModEvents.register();
        LOGGER.info("Integrated Mind loaded!");
    }
}
