package com.integratedmind;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

public class DoctrineWatcherBlock extends BaseEntityBlock {
    public static final MapCodec<DoctrineWatcherBlock> CODEC = simpleCodec(DoctrineWatcherBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public DoctrineWatcherBlock() {
        this(Properties.of().strength(3.0f).requiresCorrectToolForDrops());
    }

    public DoctrineWatcherBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DoctrineWatcherBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof DoctrineWatcherBlockEntity dbe) {
            float value = dbe.getStoredValue();
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

        Direction facing = state.getValue(FACING);
        Direction changedDir = Direction.fromDelta(
                neighborPos.getX() - pos.getX(),
                neighborPos.getY() - pos.getY(),
                neighborPos.getZ() - pos.getZ()
        );
        if (changedDir == null) return;

        // Back side: read float input from adjacent block (only if it stores floats)
        if (changedDir == facing.getOpposite()) {
            if (level.getBlockEntity(pos) instanceof DoctrineWatcherBlockEntity dbe) {
                BlockPos backPos = pos.relative(changedDir);
                BlockState backState = level.getBlockState(backPos);
                if (backState.getBlock() instanceof PrecisionMemoryBlock) {
                    if (level.getBlockEntity(backPos) instanceof PrecisionMemoryBlockEntity pmbe) {
                        dbe.setStoredValue(pmbe.getValue());
                    }
                } else if (backState.getBlock() instanceof NeuronBlock) {
                    if (level.getBlockEntity(backPos) instanceof NeuronBlockEntity nbe) {
                        dbe.setStoredValue(nbe.getValue());
                    }
                }
            }
            return;
        }

        // Redstone trigger on rising edge from any non-front side
        boolean wasPowered = state.getValue(POWERED);
        if (!wasPowered) {
            for (Direction dir : Direction.values()) {
                if (dir == facing) continue;
                if (level.getSignal(pos, dir) > 0) {
                    if (level.getBlockEntity(pos) instanceof DoctrineWatcherBlockEntity dbe) {
                        dbe.propagate(level);
                    }
                    level.setBlock(pos, state.setValue(POWERED, true), 3);
                    return;
                }
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
