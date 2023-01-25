package com.injir.create_brass_coated.blocks.other;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.relays.encased.AdjustablePulleyTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrassAdjustablePulleyTileEntity extends AdjustablePulleyTileEntity {

	int signal;
	boolean signalChanged;

	public BrassAdjustablePulleyTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		signal = 0;
		setLazyTickRate(40);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("Signal", signal);
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		signal = compound.getInt("Signal");
		super.read(compound, clientPacket);
	}

	public float getModifier() {
		return getModifierForSignal(signal);
	}

	public void neighbourChanged() {
		if (!hasLevel())
			return;
		int power = level.getBestNeighborSignal(worldPosition);
		if (power != signal) 
			signalChanged = true;
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		neighbourChanged();
	}

	@Override
	public void tick() {
		super.tick();
		if (level.isClientSide)
			return;
		if (signalChanged) {
			signalChanged = false;
			analogSignalChanged(level.getBestNeighborSignal(worldPosition));
		}
	}

	protected void analogSignalChanged(int newSignal) {
		detachKinetics();
		removeSource();
		signal = newSignal;
		attachKinetics();
	}

	protected float getModifierForSignal(int newPower) {
		if (newPower == 0)
			return 1;
		return 1 + ((newPower + 1) / 16f);
	}

}
