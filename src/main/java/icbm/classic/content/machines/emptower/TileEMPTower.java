package icbm.classic.content.machines.emptower;

import com.builtbroken.jlib.data.vector.IPos3D;
import com.builtbroken.mc.api.tile.access.IGuiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTile;
import com.builtbroken.mc.api.tile.multiblock.IMultiTileHost;
import com.builtbroken.mc.api.tile.provider.IInventoryProvider;
import com.builtbroken.mc.core.network.IPacketIDReceiver;
import com.builtbroken.mc.core.network.packet.PacketTile;
import com.builtbroken.mc.core.network.packet.PacketType;
import com.builtbroken.mc.framework.multiblock.EnumMultiblock;
import com.builtbroken.mc.framework.multiblock.MultiBlockHelper;
import com.builtbroken.mc.imp.transform.vector.Pos;
import com.builtbroken.mc.prefab.gui.ContainerDummy;
import com.builtbroken.mc.prefab.inventory.ExternalInventory;
import icbm.classic.ICBMClassic;
import icbm.classic.client.ICBMSounds;
import icbm.classic.content.explosive.blast.BlastEMP;
import icbm.classic.prefab.item.TilePoweredMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.HashMap;

public class TileEMPTower extends TilePoweredMachine implements IMultiTileHost, IPacketIDReceiver, IGuiTile, IInventoryProvider<ExternalInventory>
{
    // The maximum possible radius for the EMP to strike
    public static final int MAX_RADIUS = 150;

    public static HashMap<IPos3D, String> tileMapCache = new HashMap();

    static
    {
        tileMapCache.put(new Pos(0, 1, 0), EnumMultiblock.TILE.getTileName());
    }

    public float rotation = 0;
    private float rotationDelta;

    // The EMP mode. 0 = All, 1 = Missiles Only, 2 = Electricity Only
    public byte empMode = 0;

    private int cooldownTicks = 0;

    // The EMP explosion radius
    public int empRadius = 60;

    private boolean _destroyingStructure = false;

    private ExternalInventory inventory;

    public TileEMPTower()
    {
        //super("empTower", Material.iron);
        //this.itemBlock = ItemBlockBase.class;
        //this.hardness = 10f;
        //this.resistance = 10f;
        //this.isOpaque = false;
    }

    @Override
    public ExternalInventory getInventory()
    {
        if (inventory == null)
        {
            inventory = new ExternalInventory(this, 2);
        }
        return inventory;
    }

    @Override
    public void update()
    {
        super.update();

        if (!isReady())
        {
            cooldownTicks--;
        }
        else if (world.isBlockIndirectlyGettingPowered(getPos()) > 0)
        {
            fire();
        }

        if (ticks % 20 == 0 && getEnergy() > 0)
        {
            ICBMSounds.MACHINE_HUM.play(world, xi(), yi(), zi(), 0.5F, 0.85F * getEnergy() / getEnergyBufferSize(), true);
            sendDescPacket();
        }

        rotationDelta = (float) (Math.pow(getEnergy() / getEnergyBufferSize(), 2) * 0.5);
        rotation += rotationDelta;
        if (rotation > 360)
        {
            rotation = 0;
        }
    }

    @Override
    public boolean read(ByteBuf data, int id, EntityPlayer player, PacketType type)
    {
        if (!super.read(data, id, player, type))
        {
            switch (id)
            {
                case 0:
                {
                    setEnergy(data.readInt());
                    empRadius = data.readInt();
                    empMode = data.readByte();
                    return true;
                }
                case 1:
                {
                    empRadius = data.readInt();
                    return true;
                }
                case 2:
                {
                    empMode = data.readByte();
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public int getEnergyBufferSize()
    {
        return Math.max(3000000 * (this.empRadius / MAX_RADIUS), 1000000);
    }

    @Override
    public PacketTile getDescPacket()
    {
        return new PacketTile(this, 0, getEnergy(), this.empRadius, this.empMode);
    }

    /** Reads a tile entity from NBT. */
    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);

        this.empRadius = par1NBTTagCompound.getInteger("empRadius");
        this.empMode = par1NBTTagCompound.getByte("empMode");
    }

    /** Writes a tile entity to NBT. */
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("empRadius", this.empRadius);
        par1NBTTagCompound.setByte("empMode", this.empMode);
        return super.writeToNBT(par1NBTTagCompound);
    }

    //@Callback(limit = 1)
    public boolean fire()
    {
        if (this.checkExtract())
        {
            if (isReady())
            {
                switch (this.empMode)
                {
                    default:
                        new BlastEMP(world, null, this.xi() + 0.5, this.yi() + 1.2, this.zi() + 0.5, this.empRadius).setEffectBlocks().setEffectEntities().explode();
                        break;
                    case 1:
                        new BlastEMP(world, null, this.xi() + 0.5, this.yi() + 1.2, this.zi() + 0.5, this.empRadius).setEffectEntities().explode();
                        break;
                    case 2:
                        new BlastEMP(world, null, this.xi() + 0.5, this.yi() + 1.2, this.zi() + 0.5, this.empRadius).setEffectBlocks().explode();
                        break;
                }
                this.extractEnergy();
                this.cooldownTicks = getMaxCooldown();
                return true;
            }
        }
        return false;
    }

    //@Override
    protected boolean onPlayerRightClick(EntityPlayer player, int side, Pos hit)
    {
        if (isServer())
        {
            openGui(player, ICBMClassic.INSTANCE);
        }
        return true;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }

    //@Callback
    public boolean isReady()
    {
        return getCooldown() <= 0;
    }

    //@Callback
    public int getCooldown()
    {
        return cooldownTicks;
    }

    //@Callback
    public int getMaxCooldown()
    {
        return 120;
    }

    //==========================================
    //==== Multi-Block code
    //=========================================

    //@Override
    public void firstTick()
    {
        //super.firstTick();
        MultiBlockHelper.buildMultiBlock(world, this, true, true);
    }

    @Override
    public void onMultiTileAdded(IMultiTile tileMulti)
    {
        if (tileMulti instanceof TileEntity)
        {
            if (tileMapCache.containsKey(new Pos((TileEntity) this).sub(new Pos((TileEntity) tileMulti))))
            {
                tileMulti.setHost(this);
            }
        }
    }

    @Override
    public boolean onMultiTileBroken(IMultiTile tileMulti, Object source, boolean harvest)
    {
        if (!_destroyingStructure && tileMulti instanceof TileEntity)
        {
            Pos pos = new Pos((TileEntity) tileMulti).sub(new Pos((TileEntity) this));

            if (tileMapCache.containsKey(pos))
            {
                MultiBlockHelper.destroyMultiBlockStructure(this, harvest, true, true);
                return true;
            }
        }
        return false;
    }

    /*

    @Override
    public boolean canPlaceBlockAt()
    {
        return super.canPlaceBlockAt() && oldWorld().getBlock(xi(), yi() + 1, zi()).isReplaceable(oldWorld(), xi(), yi() + 1, zi());
    }

    @Override
    public boolean canPlaceBlockOnSide(ForgeDirection side)
    {
        return side == ForgeDirection.UP && canPlaceBlockAt();
    }
    */

    //@Override
    public boolean removeByPlayer(EntityPlayer player, boolean willHarvest)
    {
        MultiBlockHelper.destroyMultiBlockStructure(this, false, true, false);
        //TODO return super.removeByPlayer(player, willHarvest);
        return true;
    }

    @Override
    public void onTileInvalidate(IMultiTile tileMulti)
    {

    }

    @Override
    public boolean onMultiTileActivated(IMultiTile tile, EntityPlayer player, EnumHand hand, EnumFacing side, float xHit, float yHit, float zHit)
    {
        //TODO return this.onPlayerRightClick(player, side, new Pos(xHit, yHit, zHit));
        return true;
    }

    @Override
    public void onMultiTileClicked(IMultiTile tile, EntityPlayer player)
    {

    }

    @Override
    public HashMap<IPos3D, String> getLayoutOfMultiBlock()
    {
        return tileMapCache;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player)
    {
        return new ContainerDummy();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player)
    {
        return new GuiEMPTower(this);
    }
}
