package com.integratedmind;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class SmartBubbleBlock extends Block {
    public static final MapCodec<SmartBubbleBlock> CODEC = simpleCodec(SmartBubbleBlock::new);

    public SmartBubbleBlock() {
        this(Properties.of().strength(1.0f).noOcclusion());
    }

    public SmartBubbleBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
