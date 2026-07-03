package com.integratedmind;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DoctrineWatcherBlockEntity extends BlockEntity {
    private float storedValue = 0.0f;

    public DoctrineWatcherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DOCTRINE_WATCHER.get(), pos, state);
    }

    public float getStoredValue() {
        return storedValue;
    }

    public void setStoredValue(float value) {
        this.storedValue = value;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    public void propagate(Level level) {
        float value = storedValue;
        BlockState state = getBlockState();
        Direction facing = state.getValue(DoctrineWatcherBlock.FACING);
        BlockPos outputPos = worldPosition.relative(facing);

        int range = 32;
        BlockPos minPos = worldPosition.offset(-range, -range, -range);
        BlockPos maxPos = worldPosition.offset(range, range, range);

        for (BlockPos checkPos : BlockPos.betweenClosed(minPos, maxPos)) {
            if (!level.isLoaded(checkPos)) continue;
            BlockState checkState = level.getBlockState(checkPos);
            if (checkState.getBlock() instanceof InputDendriteBlock) {
                if (level.getBlockEntity(checkPos) instanceof InputDendriteBlockEntity idbe) {
                    for (InputDendriteBlockEntity.ConnectionData conn : idbe.getConnections()) {
                        if (conn.outputPos.equals(outputPos)) {
                            Direction inFacing = checkState.getValue(InputDendriteBlock.FACING);
                            BlockPos neuronPos = checkPos.relative(inFacing.getOpposite());
                            BlockState neuronState = level.getBlockState(neuronPos);
                            if (neuronState.getBlock() instanceof NeuronBlock) {
                                if (level.getBlockEntity(neuronPos) instanceof NeuronBlockEntity nbe) {
                                    nbe.setValue(value);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("storedValue", storedValue);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedValue = tag.getFloat("storedValue");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putFloat("storedValue", storedValue);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        handleUpdateTag(pkt.getTag(), registries);
    }
}
