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
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class NeuronBlockEntity extends BlockEntity {
    private float value = 0.0f;
    private float computedValue = 0.0f;
    private float peerPressure = 0.001f;

    private static final float[] PEER_PRESSURE_OPTIONS = {0.1f, 0.01f, 0.001f, 0.0001f, 0.00001f};

    public NeuronBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.NEURON_BLOCK.get(), pos, state);
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }
    }

    public float getPeerPressure() {
        return peerPressure;
    }

    public void cyclePeerPressure(boolean increase) {
        int currentIndex = -1;
        for (int i = 0; i < PEER_PRESSURE_OPTIONS.length; i++) {
            if (Math.abs(PEER_PRESSURE_OPTIONS[i] - peerPressure) < 0.000001f) {
                currentIndex = i;
                break;
            }
        }
        if (currentIndex == -1) currentIndex = 2;

        if (increase) {
            currentIndex = (currentIndex + 1) % PEER_PRESSURE_OPTIONS.length;
        } else {
            currentIndex = (currentIndex - 1 + PEER_PRESSURE_OPTIONS.length) % PEER_PRESSURE_OPTIONS.length;
        }
        peerPressure = PEER_PRESSURE_OPTIONS[currentIndex];
        setChanged();
    }

    public void forwardPass(Level level) {
        for (Direction side : Direction.values()) {
            BlockPos dendritePos = worldPosition.relative(side);
            BlockState dendriteState = level.getBlockState(dendritePos);
            if (dendriteState.getBlock() instanceof InputDendriteBlock) {
                if (level.getBlockEntity(dendritePos) instanceof InputDendriteBlockEntity idbe) {
                    float sum = idbe.compute(level);
                    this.computedValue = sum;
                    setValue(sum);
                    return;
                }
            }
        }
    }

    public void backwardPassOutput(Level level, float learningRate) {
        for (Direction side : Direction.values()) {
            BlockPos dendritePos = worldPosition.relative(side);
            BlockState dendriteState = level.getBlockState(dendritePos);
            if (dendriteState.getBlock() instanceof InputDendriteBlock) {
                if (level.getBlockEntity(dendritePos) instanceof InputDendriteBlockEntity idbe) {
                    float forwardOutput = idbe.computeForward(level);
                    float error = this.value - forwardOutput;
                    idbe.backpropagate(level, error, learningRate);
                    return;
                }
            }
        }
    }

    public void backwardPassHidden(Level level, float assignedError, float learningRate) {
        for (Direction side : Direction.values()) {
            BlockPos dendritePos = worldPosition.relative(side);
            BlockState dendriteState = level.getBlockState(dendritePos);
            if (dendriteState.getBlock() instanceof InputDendriteBlock) {
                if (level.getBlockEntity(dendritePos) instanceof InputDendriteBlockEntity idbe) {
                    idbe.computeForward(level);
                    idbe.backpropagate(level, assignedError, learningRate);
                    return;
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putFloat("value", value);
        tag.putFloat("computedValue", computedValue);
        tag.putFloat("peerPressure", peerPressure);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        value = tag.getFloat("value");
        computedValue = tag.getFloat("computedValue");
        peerPressure = tag.getFloat("peerPressure");
        if (peerPressure == 0.0f) peerPressure = 0.001f;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putFloat("value", value);
        tag.putFloat("computedValue", computedValue);
        tag.putFloat("peerPressure", peerPressure);
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
