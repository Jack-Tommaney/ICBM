package icbm.classic.client.render.entity;

import icbm.classic.ICBMClassic;
import icbm.classic.content.entity.EntityGrenade;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntityItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGrenade extends Render<EntityGrenade>
{
    private EntityItem entityItem;
    private RenderEntityItem renderEntityItem;

    public RenderGrenade(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
        entityItem = new EntityItem(null);
        renderEntityItem = new RenderEntityItem(renderManagerIn, Minecraft.getMinecraft().getRenderItem());
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    @Override
    public void doRender(EntityGrenade entity, double x, double y, double z, float par8, float par9)
    {
        //Set data
        entityItem.setWorld(entity.world);
        entityItem.setPosition(entity.posX, entity.posY, entity.posZ);
        entityItem.setItem(new ItemStack(ICBMClassic.itemGrenade, 1, entity.explosiveID));

        //render
        renderEntityItem.doRender(entityItem, x, y, z, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGrenade entity)
    {
        return TextureMap.LOCATION_BLOCKS_TEXTURE;
    }
}
