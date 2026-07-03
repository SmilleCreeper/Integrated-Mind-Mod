package com.integratedmind;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

public class OutputDendriteBlock extends Block {
    public static final MapCodec<OutputDendriteBlock> CODEC = simpleCodec(OutputDendriteBlock::new);
    public static final DirectionProperty FACING = DirectionalBlock.FACING;
    private static final Random RNG = new Random();

    public OutputDendriteBlock() {
        this(Properties.of().strength(2.0f).noOcclusion());
    }

    public OutputDendriteBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
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

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            if (stack.is(ModItems.AXON_WIRE.get())) {
                ConnectionHandler.setPending(player, pos, RNG.nextFloat() * 2.0f - 1.0f);
                return ItemInteractionResult.SUCCESS;
            }
            if (stack.is(ModItems.PERFECT_WIRE.get())) {
                ConnectionHandler.setPending(player, pos, 1.0f);
                return ItemInteractionResult.SUCCESS;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
