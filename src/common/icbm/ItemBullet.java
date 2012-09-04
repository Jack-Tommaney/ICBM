package icbm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.ItemStack;

public class ItemBullet extends ICBMItem
{
	public ItemBullet(String name, int par1, int par2) 
	{
		super(name, par1, par2, CreativeTabs.tabCombat);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
        this.maxStackSize = 16;
	}

	@Override
	public int getMetadata(int damage)
    {
        return damage;
    }
	 
	@Override
	public String getItemNameIS(ItemStack itemstack)
    {
    	if(itemstack.getItemDamage() == 0)
    	{	
    		return "Conventional Bullet";
    	}
    	else if(itemstack.getItemDamage() == 1)
    	{
    		return "Antimatter Bullet";
    	}
        return "Railgun Bullet";
    }

    @Override
	public int getIconFromDamage(int i)
    {
        return this.iconIndex+i;
    }

    @Override
	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
    	for(int i = 0; i < 2; i++)
        {
    		par3List.add(new ItemStack(this, 1, i));
        }
    }
}
