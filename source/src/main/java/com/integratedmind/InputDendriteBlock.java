package com.integratedmind;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class InputDendriteBlock extends BaseEntityBlock {
    public static final MapCodec<InputDendriteBlock> CODEC = simpleCodec(InputDendriteBlock::new);
    public static final DirectionProperty FACING = net.minecraft.world.level.block.DirectionalBlock.FACING;

    public InputDendriteBlock() {
        this(Properties.of().strength(2.0f).noOcclusion());
    }

    public InputDendriteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        Direction dir = context.getClickedFace();
        return defaultBlockState().setValue(FACING, dir);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        Direction facing = state.getValue(FACING);
        return switch (facing) {
            case UP -> box(0, 0, 0, 16, 4, 16);
            case DOWN -> box(0, 12, 0, 16, 16, 16);
            case NORTH -> box(0, 0, 12, 16, 16, 16);
            case SOUTH -> box(0, 0, 0, 16, 16, 4);
            case WEST -> box(12, 0, 0, 16, 16, 16);
            case EAST -> box(0, 0, 0, 4, 16, 16);
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InputDendriteBlockEntity(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        return handleLinking(level, pos, player);
    }

    public static InteractionResult handleLinking(Level level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof InputDendriteBlockEntity idbe) {
            if (ConnectionHandler.hasPending(player)) {
                BlockPos outputPos = ConnectionHandler.getPending(player);
                float weight = ConnectionHandler.getWeight(player);
                ConnectionHandler.clearPending(player);

                double dx = outputPos.getX() - pos.getX();
                double dz = outputPos.getZ() - pos.getZ();
                double dist = Math.sqrt(dx * dx + dz * dz);
                if (dist > 32) return InteractionResult.FAIL;

                if (idbe.hasConnection(outputPos)) return InteractionResult.FAIL;

                idbe.addConnection(outputPos, weight);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
