package icbm.classic.prefab.tile;

import com.builtbroken.jlib.data.network.IByteBufWriter;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import icbm.classic.ICBMClassic;
import icbm.classic.api.EnumTier;
import icbm.classic.api.data.IWorldPosition;
import icbm.classic.lib.NBTConstants;
import icbm.classic.config.ConfigIC2;
import icbm.classic.lib.network.IPacket;
import icbm.classic.lib.network.IPacketIDReceiver;
import icbm.classic.lib.network.packet.PacketTile;
import icbm.classic.mods.ic2.IC2Proxy;
import icbm.classic.prefab.gui.IPlayerUsing;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Dark(DarkGuardsman, Robert) on 1/9/2017.
 */
@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2"),
        @Optional.Interface(iface = "ic2.api.tile.IEnergyStorage", modid = "ic2")
})
public class TileMachine extends TileEntity implements IPacketIDReceiver, IWorldPosition, IPlayerUsing, ITickable, IByteBufWriter, IGuiTile, IEnergySink {

    public static final int DESC_PACKET_ID = -1;
    /**
     * Toggle to send a {@link #getUpdatePacket()} on the next tick, keep in mind only do this for render data.
     * if the data is not used by the renderer then send it at the time it is needed. For example, GUI data
     * should be sent to only GUI users and not everyone.
     */
    protected boolean updateClient = false;

    protected int ticks = -1;

    // Cache until block state can be updated
    public EnumTier _tier = EnumTier.ONE;

    List<PlayerEntity> playersWithGUI = new ArrayList();

    @Override
    public void update() {

        //Trigger first tick
        if (ticks == -1)
            onFirstTick();

        //Increase tick
        ticks++;
        if (ticks >= Integer.MAX_VALUE - 1)
            ticks = 0;

        if (isServer()) {

            //Sync client(s) if needed
            if (updateClient) {
                updateClient = false;
                sendDescPacket();
            }

            //Sync GUI data to client(s)
            if (ticks % 3 == 0 && getPlayersUsing().size() > 0) {

                PacketTile packet = getGUIPacket();

                if (packet != null)
                    sendPacketToGuiUsers(packet);

            }

        }

    }

    protected void onFirstTick() {
        //TODO remove set state hack when Forge patches TE access in get drops
        if (getBlockState().func_235901_b_(BlockICBM.TIER_PROP))
            world.setBlockState(pos, getBlockState().with(BlockICBM.TIER_PROP, getTier()));
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void sendDescPacket() {
        PacketTile packetTile = getDescPacket();
        if (packetTile != null)
            ICBMClassic.packetHandler.sendToAllAround(packetTile, this);
    }

    @Override
    public void deserializeNBT(CompoundNBT compound) {
        super.deserializeNBT(compound);
        _tier = EnumTier.get(compound.getInt(NBTConstants.TIER));
    }

    @Override
    public CompoundNBT writeToNBT(CompoundNBT compound) {
        compound.putInt(NBTConstants.TIER, _tier.ordinal());
        return super.write(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return writeToNBT(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public PacketTile getDescPacket() {
        PacketTile packetTile = new PacketTile("desc", DESC_PACKET_ID, this);
        packetTile.addData(this); //Should call back to IByteBufWriter
        return packetTile;
    }

    public void sendPacketToGuiUsers(IPacket packet) {

        if (packet != null) {

            Iterator<PlayerEntity> it = getPlayersUsing().iterator();

            while (it.hasNext()) {

                final PlayerEntity player = it.next();

                if (player instanceof ServerPlayerEntity && isValidGuiUser(player))
                    ICBMClassic.packetHandler.sendToPlayer(packet, (ServerPlayerEntity) player);
                else
                    it.remove();

            }

        }

    }

    protected boolean isValidGuiUser(PlayerEntity player) {
        return player.openContainer != null;
    }

    @Override
    public final List<PlayerEntity> getPlayersUsing() {
        return playersWithGUI;
    }

    @Override
    public boolean read(ByteBuf buf, int id, PlayerEntity player, IPacket type) {

        if (isClient()) {
            if (id == DESC_PACKET_ID) {
                readDescPacket(buf);
                return true;
            }
        }

        return false;

    }

    @Override
    public final ByteBuf writeBytes(ByteBuf var1) {
        //Using this as a redirect for the desc packet
        writeDescPacket(var1);
        return var1;
    }

    public void writeDescPacket(ByteBuf buf) {
        buf.writeInt(_tier.ordinal());
    }

    public void readDescPacket(ByteBuf buf) {
        _tier = EnumTier.get(buf.readInt());
    }

    /**
     * Packet sent to GUI users
     *
     * @return
     */
    protected PacketTile getGUIPacket() {
        return getDescPacket();
    }

    public boolean isServer() {
        return world != null && !world.isRemote;
    }

    public boolean isClient() {
        return world != null && world.isRemote;
    }

    public Direction getRotation() {

        BlockState state = getBlockState();
        if (state.func_235901_b_(BlockICBM.ROTATION_PROP))
            return state.get(BlockICBM.ROTATION_PROP);

        return Direction.NORTH;

    }

    public void setRotation(Direction facingDirection) {

        //Only update if state has changed
        if (facingDirection != getRotation()) {

            //Update block state
            BlockState state = getBlockState();

            if (state.func_235901_b_(BlockICBM.ROTATION_PROP))
                world.setBlockState(pos, getBlockState().with(BlockICBM.ROTATION_PROP, facingDirection));

        }

    }

    public EnumTier getTier() {
        return _tier;
    }

    public void setTier(EnumTier tier) {

        if (tier != getTier()) {

            this._tier = tier;

            if (isServer()) {

                BlockState state = getBlockState();

                if (state.func_235901_b_(BlockICBM.TIER_PROP))
                    world.setBlockState(pos, state.with(BlockICBM.TIER_PROP, tier));

            }

        }

    }

    public BlockState getBlockState() {
        return world.getBlockState(getPos());
    }

    @Override
    public World world() {
        return getWorld();
    }

    @Override
    public double z() {
        return getPos().getZ();
    }

    @Override
    public double x() {
        return getPos().getX();
    }

    @Override
    public double y() {
        return getPos().getY();
    }


    /**
     * Gets the position of the other tile entity relative to this tile entity
     *
     * @param other - tile entity
     * @return position
     */
    protected final BlockPos getRelativePosition(TileEntity other) {
        return other.getPos().subtract(this.getPos());
    }

    @Override
    public boolean openGui(PlayerEntity player, int requestedID) {
        player.openGui(ICBMClassic.INSTANCE, requestedID, world, xi(), yi(), zi());
        return true;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double getDemandedEnergy() {

        if (!ConfigIC2.DISABLED && getCapability(CapabilityEnergy.ENERGY, null).isPresent()) {

            IEnergyStorage energyStorage = getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

            if (energyStorage != null) {
                int need = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                return need / ConfigIC2.FROM_IC2;
            }

        }

        return 0;

    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSinkTier() {
        return !ConfigIC2.DISABLED ? 4 : 0;
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double injectEnergy(Direction directionFrom, double amount, double voltage) {

        if (!ConfigIC2.DISABLED && getCapability(CapabilityEnergy.ENERGY, null).isPresent()) {

            IEnergyStorage energyStorage = getCapability(CapabilityEnergy.ENERGY, null).orElse(null);

            if (energyStorage != null) {
                int energy = (int) Math.floor(amount * ConfigIC2.FROM_IC2);
                int received = energyStorage.receiveEnergy(energy, false);
                return amount - (received / ConfigIC2.FROM_IC2);
            }

        }

        return amount;

    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
        return !ConfigIC2.DISABLED && getCapability(CapabilityEnergy.ENERGY, side).isPresent();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        IC2Proxy.INSTANCE.onTileInvalidate(this);
    }

    @Override
    public void validate() {
        super.validate();
        IC2Proxy.INSTANCE.onTileValidate(this);
    }

}