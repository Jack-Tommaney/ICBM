package icbm.jiqi;

import icbm.ICBM;
import icbm.ICBMCommonProxy;
import icbm.TYinXing;
import icbm.daodan.EDaoDan;
import icbm.daodan.ItDaoDan;
import icbm.daodan.ItTeBieDaoDan;
import icbm.extend.IMB;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import universalelectricity.Ticker;
import universalelectricity.UniversalElectricity;
import universalelectricity.implement.IRotatable;
import universalelectricity.implement.ITier;
import universalelectricity.network.ConnectionHandler;
import universalelectricity.network.ConnectionHandler.ConnectionType;
import universalelectricity.network.IPacketReceiver;
import universalelectricity.network.ISimpleConnectionHandler;
import universalelectricity.network.PacketManager;
import universalelectricity.prefab.Vector3;

import com.google.common.io.ByteArrayDataInput;

/**
 * This tile entity is for the base of the missile launcher
 * @author Calclavia
 *
 */
public class TFaSheDi extends TileEntity implements IPacketReceiver, IRotatable, ITier, IMB, IInventory, ISidedInventory, ISimpleConnectionHandler
{
    private static final double MISSILE_MAX_DISTANCE = UniversalElectricity.getConfigData(ICBM.CONFIGURATION, "Max Missile Distance", 2000);

	//The missile that this launcher is holding
    public EDaoDan containingMissile = null;
    
    //The connected missile launcher frame
    public TFaSheJia connectedFrame = null;
    
    private ItemStack[] containingItems = new ItemStack[1];
    
    //The tier of this launcher base
    private int tier = 0;
    
    private byte orientation = 3;

	private boolean sendUpdate = false;
    
    public TFaSheDi()
    {
		ConnectionHandler.registerConnectionHandler(this);
    }
    
    /**
     * Returns the number of slots in the inventory.
     */
	@Override
    public int getSizeInventory()
    {
        return this.containingItems.length;
    }

    /**
     * Returns the stack in slot i
     */
	@Override
    public ItemStack getStackInSlot(int par1)
    {
        return this.containingItems[par1];
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
	@Override
    public ItemStack decrStackSize(int par1, int par2)
    {		
        if (this.containingItems[par1] != null)
        {
            ItemStack var3;

            if (this.containingItems[par1].stackSize <= par2)
            {
                var3 = this.containingItems[par1];
                this.containingItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.containingItems[par1].splitStack(par2);

                if (this.containingItems[par1].stackSize == 0)
                {
                    this.containingItems[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
	@Override
    public ItemStack getStackInSlotOnClosing(int par1)
    {
        if (this.containingItems[par1] != null)
        {
            ItemStack var2 = this.containingItems[par1];
            this.containingItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
	@Override
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
    {
        this.containingItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
	@Override
    public String getInvName()
    {
        return "Launcher Platform";
    }

    /**
     * Allows the entity to update its state. Overridden in most subclasses, e.g. the mob spawner uses this to count
     * ticks and creates a new spawn inside its implementation.
     */
    @Override
	public void updateEntity()
    {
    	super.updateEntity();
    	
    	if(this.connectedFrame == null)
    	{
        	for(byte i = 2; i < 6; i++)
        	{
        		Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
        		position.modifyPositionFromSide(ForgeDirection.getOrientation(i));
        		
        		TileEntity tileEntity = this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ());
        		
        		if(tileEntity instanceof TFaSheJia)
            	{
            		this.connectedFrame = (TFaSheJia)tileEntity;
            		this.connectedFrame.setDirection(Vector3.getOrientationFromSide(ForgeDirection.getOrientation(i), ForgeDirection.NORTH));
            	}
        	}
    	}
    	else
        {
        	if(this.connectedFrame.isInvalid())
        	{
        		this.connectedFrame = null;
        	}
        	else if(this.sendUpdate || Ticker.inGameTicks % (20*30) == 0 && this.connectedFrame != null && !this.worldObj.isRemote)
        	{
				PacketManager.sendTileEntityPacket(this.connectedFrame, "ICBM", (byte)this.connectedFrame.getDirection().ordinal(), this.connectedFrame.getTier());
        	}
        }
    	
    	if(!this.worldObj.isRemote)
		{
	    	this.setMissile();
	    	
	        if(this.sendUpdate  || Ticker.inGameTicks % (20*30) == 0)
	        {
	        	PacketManager.sendTileEntityPacket(this, "ICBM", this.orientation, this.tier);
	        	this.sendUpdate = false;
	        }
		}
	}
    
    private void setMissile()
    {
    	if (this.containingItems[0] != null)
        {
            if (this.containingItems[0].getItem() instanceof ItDaoDan)
            {
                int missileId = this.containingItems[0].getItemDamage();

                if(this.containingItems[0].getItem() instanceof ItTeBieDaoDan && missileId > 0)
        		{
        			missileId+= 100;
        		}
                
            	if(containingMissile == null)
            	{
        			Vector3 position = new Vector3((this.xCoord+0.5F), (this.yCoord+2), (this.zCoord+0.5F));
                    this.containingMissile = new EDaoDan(this.worldObj, position, Vector3.get(this), missileId);
                    this.worldObj.spawnEntityInWorld(this.containingMissile);
                    return;
            	}
            	else if(this.containingMissile.missileID ==  missileId)
            	{
            		return;
            	}
            }
        }
    	
		if(this.containingMissile != null)
		{
			this.containingMissile.setDead();
		}
		
		this.containingMissile = null;
	}

	@Override
	public void handelConnection(ConnectionType type, Object... data)
	{
		if(type == ConnectionType.LOGIN_SERVER)
		{
        	this.sendUpdate = true;
		}
	}
	
	@Override
	public void handlePacketData(NetworkManager network, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		try
        {
            this.orientation = dataStream.readByte();
            this.tier = dataStream.readInt();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
	}
    
    /**
     * Launches the missile
     * @param target - The target in which the missile will land in
     */
    public void launchMissile(Vector3 target)
    {
    	//Apply inaccuracy
    	float inaccuracy;
    	
    	if(this.connectedFrame != null)
    	{
    		inaccuracy = (float)this.connectedFrame.getInaccuracy();
    	}
    	else
    	{
    		inaccuracy = 30F;
    	}
    	
    	inaccuracy *= (float)Math.random();
    	
    	if(Math.random() > 0.5F)
    	{
    		inaccuracy *= -1;
    	}
    	    	
    	target.x += inaccuracy;
    	target.z += inaccuracy;
    	
		this.decrStackSize(0, 1);
        this.containingMissile.launchMissile(target);
        this.containingMissile = null;    
    }
    
    //Checks if the missile target is in range
    public boolean isInRange(Vector3 target)
    {
    	if(target != null)
    	{
    		if(isTooClose(target))
    		{
    			return false;
    		}
    		
    		if(!isTooFar(target))
    		{
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    //Is the target too close?
    public boolean isTooClose(Vector3 target)
    {
    	//Check if it is greater than the minimum range
		if(Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 8)
		{
			return true;
		}
		
		return false;
    }
    
    //Is the target too far?
    public boolean isTooFar(Vector3 target)
    {
    	//Checks if it is greater than the maximum range for the launcher base
    	if(this.tier == 0)
    	{
    		if(Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 200)
    		{
    			return false;
    		}
    	}
    	else if(this.tier == 1)
    	{
    		if(Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < 500)
    		{
    			return false;
    		}
    	}
    	else if(this.tier == 2)
    	{
    		if(Vector3.distance(new Vector3(this.xCoord, 0, this.zCoord), new Vector3(target.x, 0, target.z)) < MISSILE_MAX_DISTANCE)
    		{
    			return false;
    		}
    	}
    	
		return true;
    }

    /**
     * Reads a tile entity from NBT.
     */
    @Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.readFromNBT(par1NBTTagCompound);
    	
    	NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
    	
    	this.tier = par1NBTTagCompound.getInteger("tier");
    	this.orientation = par1NBTTagCompound.getByte("facingDirection");
    	
        this.containingItems = new ItemStack[this.getSizeInventory()];

        for (int var3 = 0; var3 < var2.tagCount(); ++var3)
        {
            NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
            byte var5 = var4.getByte("Slot");

            if (var5 >= 0 && var5 < this.containingItems.length)
            {
                this.containingItems[var5] = ItemStack.loadItemStackFromNBT(var4);
            }
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    @Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
    	super.writeToNBT(par1NBTTagCompound);
    	
    	par1NBTTagCompound.setInteger("tier", this.tier);
    	par1NBTTagCompound.setByte("facingDirection", this.orientation);
    	
    	NBTTagList var2 = new NBTTagList();

        for (int var3 = 0; var3 < this.containingItems.length; ++var3)
        {
            if (this.containingItems[var3] != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte)var3);
                this.containingItems[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        
        par1NBTTagCompound.setTag("Items", var2);
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    @Override
	public int getInventoryStackLimit()
    {
        return 1;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    @Override
	public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
	public void openChest() {}

    @Override
	public void closeChest() {}

    @Override
    public int getStartInventorySide(ForgeDirection side)
    {
        return 0;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side)
    {
        return 1;
    }

	@Override
	public int getTier()
	{
		return this.tier;
	}

	@Override
	public void setTier(int tier)
	{
		this.tier = tier;
	}

	@Override
	public boolean onActivated(EntityPlayer par5EntityPlayer)
	{		
		if(par5EntityPlayer.inventory.getCurrentItem() != null && this.getStackInSlot(0) == null)
		{
			if(par5EntityPlayer.inventory.getCurrentItem().getItem() instanceof ItDaoDan)
			{
				this.setInventorySlotContents(0, par5EntityPlayer.inventory.getCurrentItem());
				par5EntityPlayer.inventory.setInventorySlotContents(par5EntityPlayer.inventory.currentItem, null);
				return true;
			}
		}
		
		par5EntityPlayer.openGui(ICBM.instance, ICBMCommonProxy.GUI_LAUNCHER_BASE, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		return true;
	}

	@Override
	public void onCreate(Vector3 position)
	{
		if(this.orientation == 3 || this.orientation == 2)
		{		
			//Left
			this.worldObj.setBlockWithNotify(position.intX()+1, position.intY(), position.intZ(), ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX()+1, position.intY(), position.intZ())).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX()+1, position.intY()+1, position.intZ(), ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX()+1, position.intY()+1, position.intZ())).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX()+1, position.intY()+2, position.intZ(), ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX()+1, position.intY()+2, position.intZ())).setMainBlock(position);
			//Right
			this.worldObj.setBlockWithNotify(position.intX()-1, position.intY(), position.intZ(), ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX()-1, position.intY(), position.intZ())).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX()-1, position.intY()+1, position.intZ(), ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX()-1, position.intY()+1, position.intZ())).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX()-1, position.intY()+2, position.intZ(), ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX()-1, position.intY()+2, position.intZ())).setMainBlock(position);
		}
		else
		{
			//Left
			this.worldObj.setBlockWithNotify(position.intX(), position.intY(), position.intZ()+1, ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ()+1)).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX(), position.intY()+1, position.intZ()+1, ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY()+1, position.intZ()+1)).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX(), position.intY()+2, position.intZ()+1, ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY()+2, position.intZ()+1)).setMainBlock(position);
			//Right
			this.worldObj.setBlockWithNotify(position.intX(), position.intY(), position.intZ()-1, ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY(), position.intZ()-1)).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX(), position.intY()+1, position.intZ()-1, ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY()+1, position.intZ()-1)).setMainBlock(position);
			this.worldObj.setBlockWithNotify(position.intX(), position.intY()+2, position.intZ()-1, ICBM.blockYinXing.blockID);
			((TYinXing)this.worldObj.getBlockTileEntity(position.intX(), position.intY()+2, position.intZ()-1)).setMainBlock(position);
		}
	}
	
	@Override
	public void onDestroy(TileEntity callingBlock)
	{
		Vector3 position = new Vector3(this.xCoord, this.yCoord, this.zCoord);
		
		if(this.orientation == 3 || this.orientation == 2)
		{
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x+1, (int)position.y, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x+1, (int)position.y+1, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x+1, (int)position.y+2, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x-1, (int)position.y, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x-1, (int)position.y+1, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x-1, (int)position.y+2, (int)position.z, 0);
		}
		else
		{
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y, (int)position.z, 0);
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y, (int)position.z+1, 0);
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y+1, (int)position.z+1, 0);
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y+2, (int)position.z+1, 0);
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y, (int)position.z-1, 0);
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y+1, (int)position.z-1, 0);
			this.worldObj.setBlockWithNotify((int)position.x, (int)position.y+2, (int)position.z-1, 0);
		}
		
		if(this.containingMissile != null)
    	{
			this.containingMissile.setDead();
    	}
	}

	
	@Override
	public ForgeDirection getDirection()
	{
		return ForgeDirection.getOrientation(this.orientation);
	}

	@Override
	public void setDirection(ForgeDirection facingDirection) 
	{
		this.orientation = (byte) facingDirection.ordinal();
	}


}
