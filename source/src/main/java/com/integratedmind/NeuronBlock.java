package com.integratedmind;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class NeuronBlock extends BaseEntityBlock {
    public static final MapCodec<NeuronBlock> CODEC = simpleCodec(NeuronBlock::new);

    public NeuronBlock() {
        this(Properties.of().strength(3.0f).requiresCorrectToolForDrops());
    }

    public NeuronBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NeuronBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (tryPeerPressureConfig(level, pos, player)) return ItemInteractionResult.SUCCESS;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (tryPeerPressureConfig(level, pos, player)) return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    }

    private static boolean tryPeerPressureConfig(Level level, BlockPos pos, Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof SmartBubbleItem)) return false;
        if (level.isClientSide) return true;
        if (!(level.getBlockEntity(pos) instanceof NeuronBlockEntity nbe)) return false;
        boolean increase = !player.isShiftKeyDown();
        nbe.cyclePeerPressure(increase);
        player.displayClientMessage(
                Component.literal(String.format("Chosen peer pressure = %.5f", nbe.getPeerPressure())),
                true
        );
        return true;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof NeuronBlockEntity nbe) {
            float value = nbe.getValue();
            int signal = (int)Math.ceil(value / 0.0625f) - 1;
            if (signal < 0) return 0;
            if (signal > 15) return 15;
            return signal;
        }
        return 0;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;
        Direction changedDir = Direction.fromDelta(
                neighborPos.getX() - pos.getX(),
                neighborPos.getY() - pos.getY(),
                neighborPos.getZ() - pos.getZ()
        );
        if (changedDir == null) return;

        if (changedDir == Direction.UP) {
            int signal = level.getSignal(pos.relative(Direction.UP), Direction.DOWN);
            if (signal > 0 && level.getBlockEntity(pos) instanceof NeuronBlockEntity nbe) {
                nbe.forwardPass(level);
            }
        } else if (changedDir == Direction.DOWN) {
            int signal = level.getSignal(pos.relative(Direction.DOWN), Direction.UP);
            if (signal > 0 && level.getBlockEntity(pos) instanceof NeuronBlockEntity nbe) {
                nbe.backwardPassOutput(level, nbe.getPeerPressure());
            }
        }
    }
}
