package com.integratedmind;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class RedstoneToPrecisionBlock extends Block {
    public static final MapCodec<RedstoneToPrecisionBlock> CODEC = simpleCodec(RedstoneToPrecisionBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public RedstoneToPrecisionBlock() {
        this(Properties.of().strength(3.0f).requiresCorrectToolForDrops());
    }

    public RedstoneToPrecisionBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) return;

        Direction facing = state.getValue(FACING);
        Direction changedDir = Direction.fromDelta(
                neighborPos.getX() - pos.getX(),
                neighborPos.getY() - pos.getY(),
                neighborPos.getZ() - pos.getZ()
        );
        if (changedDir == null || changedDir == facing) return;

        boolean wasPowered = state.getValue(POWERED);

        if (!wasPowered) {
            int power = level.getSignal(pos, changedDir);
            if (power > 0) {
                float value = (power + 1) / 16.0f;
                BlockPos frontPos = pos.relative(facing);
                BlockState frontState = level.getBlockState(frontPos);
                if (frontState.getBlock() instanceof PrecisionMemoryBlock) {
                    if (level.getBlockEntity(frontPos) instanceof PrecisionMemoryBlockEntity pmbe) {
                        pmbe.setValue(value);
                    }
                } else if (frontState.getBlock() instanceof DoctrineWatcherBlock) {
                    if (level.getBlockEntity(frontPos) instanceof DoctrineWatcherBlockEntity dbe) {
                        dbe.setStoredValue(value);
                    }
                }
                level.setBlock(pos, state.setValue(POWERED, true), 3);
            }
        } else {
            boolean anyPower = false;
            for (Direction dir : Direction.values()) {
                if (dir == facing) continue;
                if (level.getSignal(pos, dir) > 0) {
                    anyPower = true;
                    break;
                }
            }
            if (!anyPower) {
                level.setBlock(pos, state.setValue(POWERED, false), 3);
            }
        }
    }
}
