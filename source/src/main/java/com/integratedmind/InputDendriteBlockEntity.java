package com.integratedmind;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class InputDendriteBlockEntity extends BlockEntity {
    private final List<ConnectionData> connections = new ArrayList<>();
    public InputDendriteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.INPUT_DENDRITE.get(), pos, state);
    }

    public boolean hasConnection(BlockPos outputPos) {
        for (ConnectionData c : connections) {
            if (c.outputPos.equals(outputPos)) return true;
        }
        return false;
    }

    public void addConnection(BlockPos outputPos, float weight) {
        if (hasConnection(outputPos)) return;
        connections.add(new ConnectionData(outputPos, weight));
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public List<ConnectionData> getConnections() {
        return connections;
    }

    public float compute(Level level) {
        float sum = computeForward(level);
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
        return sum;
    }

    public float computeForward(Level level) {
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof InputDendriteBlock)) return 0.0f;
        Direction facing = state.getValue(InputDendriteBlock.FACING);
        BlockPos neuronPos = worldPosition.relative(facing.getOpposite());

        float sum = 0.0f;
        for (ConnectionData conn : connections) {
            BlockPos outputPos = conn.outputPos;
            BlockState outputState = level.getBlockState(outputPos);
            if (!(outputState.getBlock() instanceof OutputDendriteBlock)) continue;

            Direction outFacing = outputState.getValue(OutputDendriteBlock.FACING);
            BlockPos sourceNeuronPos = outputPos.relative(outFacing.getOpposite());
            BlockState sourceState = level.getBlockState(sourceNeuronPos);

            float sourceValue = 0.0f;
            if (sourceState.getBlock() instanceof NeuronBlock) {
                if (level.getBlockEntity(sourceNeuronPos) instanceof NeuronBlockEntity nbe) {
                    sourceValue = nbe.getValue();
                }
            } else if (sourceState.getBlock() instanceof PrecisionMemoryBlock) {
                if (level.getBlockEntity(sourceNeuronPos) instanceof PrecisionMemoryBlockEntity pmbe) {
                    sourceValue = pmbe.getValue();
                }
            }

            conn.valueBefore = sourceValue;
            conn.valueAfter = sourceValue * conn.weight;
            sum += conn.valueAfter;
        }
        return sum;
    }

    public void backpropagate(Level level, float outputError, float learningRate) {
        for (ConnectionData conn : connections) {
            BlockPos outputPos = conn.outputPos;
            BlockState outputState = level.getBlockState(outputPos);
            if (!(outputState.getBlock() instanceof OutputDendriteBlock)) continue;

            float inputValue = conn.valueBefore;
            boolean isPerfectWire = Math.abs(conn.weight - 1.0f) < 0.001f;
            float oldWeight = conn.weight;

            if (!isPerfectWire) {
                conn.weight += learningRate * outputError * inputValue;
            }

            float sourceError = oldWeight * outputError;

            Direction outFacing = outputState.getValue(OutputDendriteBlock.FACING);
            BlockPos sourcePos = outputPos.relative(outFacing.getOpposite());
            BlockState sourceState = level.getBlockState(sourcePos);

            if (sourceState.getBlock() instanceof NeuronBlock) {
                if (level.getBlockEntity(sourcePos) instanceof NeuronBlockEntity nbe) {
                    nbe.backwardPassHidden(level, sourceError, learningRate);
                }
            }
        }
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag list = new ListTag();
        for (ConnectionData conn : connections) {
            CompoundTag ct = new CompoundTag();
            ct.putInt("ox", conn.outputPos.getX());
            ct.putInt("oy", conn.outputPos.getY());
            ct.putInt("oz", conn.outputPos.getZ());
            ct.putFloat("weight", conn.weight);
            ct.putFloat("valueBefore", conn.valueBefore);
            ct.putFloat("valueAfter", conn.valueAfter);
            list.add(ct);
        }
        tag.put("connections", list);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        connections.clear();
        ListTag list = tag.getList("connections", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag ct = list.getCompound(i);
            BlockPos outputPos = new BlockPos(ct.getInt("ox"), ct.getInt("oy"), ct.getInt("oz"));
            ConnectionData conn = new ConnectionData(outputPos, ct.getFloat("weight"));
            conn.valueBefore = ct.getFloat("valueBefore");
            conn.valueAfter = ct.getFloat("valueAfter");
            connections.add(conn);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        ListTag list = new ListTag();
        for (ConnectionData conn : connections) {
            CompoundTag ct = new CompoundTag();
            ct.putInt("ox", conn.outputPos.getX());
            ct.putInt("oy", conn.outputPos.getY());
            ct.putInt("oz", conn.outputPos.getZ());
            ct.putFloat("weight", conn.weight);
            ct.putFloat("valueBefore", conn.valueBefore);
            ct.putFloat("valueAfter", conn.valueAfter);
            list.add(ct);
        }
        tag.put("connections", list);
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

    public static class ConnectionData {
        public final BlockPos outputPos;
        public float weight;
        public float valueBefore;
        public float valueAfter;

        public ConnectionData(BlockPos outputPos, float weight) {
            this.outputPos = outputPos;
            this.weight = weight;
        }
    }
}
