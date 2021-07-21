package com.jdawg3636.icbm.common;

import com.jdawg3636.icbm.ICBMReference;
import com.jdawg3636.icbm.common.block.emp_tower.TEREMPTower;
import com.jdawg3636.icbm.common.block.emp_tower.TileEMPTower;
import com.jdawg3636.icbm.common.block.launcher_control_panel.ScreenLauncherControlPanel;
import com.jdawg3636.icbm.common.block.launcher_control_panel.TileLauncherControlPanel;
import com.jdawg3636.icbm.common.entity.EntityPrimedExplosivesRenderer;
import com.jdawg3636.icbm.common.entity.EntityMissileRenderer;
import com.jdawg3636.icbm.common.block.launcher_platform.ScreenLauncherPlatform;
import com.jdawg3636.icbm.common.reg.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy extends CommonProxy {

    public void onClientSetupEvent(FMLClientSetupEvent event) {

        // Set Render Layers
        RenderTypeLookup.setRenderLayer(BlockReg.GLASS_BUTTON.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.GLASS_PRESSURE_PLATE.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.LAUNCHER_PLATFORM_T3.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.LAUNCHER_SUPPORT_FRAME_T1.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.LAUNCHER_SUPPORT_FRAME_T2.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.LAUNCHER_SUPPORT_FRAME_T3.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.REINFORCED_GLASS.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.SPIKES.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.SPIKES_POISON.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(BlockReg.SPIKES_FIRE.get(), RenderType.cutout());

        // Register Container Screens
        ScreenManager.register(ContainerReg.LAUNCHER_PLATFORM_T1.get(), ScreenLauncherPlatform::new);
        ScreenManager.register(ContainerReg.LAUNCHER_PLATFORM_T2.get(), ScreenLauncherPlatform::new);
        ScreenManager.register(ContainerReg.LAUNCHER_PLATFORM_T3.get(), ScreenLauncherPlatform::new);

        // Register Explosives Entity Rendering Handlers
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_CONDENSED.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_CONDENSED.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_SHRAPNEL.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_SHRAPNEL.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_INCENDIARY.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_INCENDIARY.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_DEBILITATION.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_DEBILITATION.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_CHEMICAL.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_CHEMICAL.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_ANVIL.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_ANVIL.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_REPULSIVE.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_REPULSIVE.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_ATTRACTIVE.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_ATTRACTIVE.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_NIGHTMARE.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_NIGHTMARE.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_FRAGMENTATION.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_FRAGMENTATION.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_CONTAGIOUS.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_CONTAGIOUS.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_SONIC.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_SONIC.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_BREACHING.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_BREACHING.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_REJUVENATION.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_REJUVENATION.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_THERMOBARIC.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_THERMOBARIC.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_NUCLEAR.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_NUCLEAR.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_EMP.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_EMP.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_EXOTHERMIC.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_EXOTHERMIC.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_ENDOTHERMIC.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_ENDOTHERMIC.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_ANTIGRAVITATIONAL.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_ANTIGRAVITATIONAL.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_ENDER.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_ENDER.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_HYPERSONIC.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_HYPERSONIC.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_ANTIMATTER.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_ANTIMATTER.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.EXPLOSIVES_REDMATTER.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.EXPLOSIVES_REDMATTER.get().defaultBlockState()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.S_MINE.get(), (manager) -> new EntityPrimedExplosivesRenderer(manager, BlockReg.S_MINE.get().defaultBlockState()));

        // Register Missile Entity Rendering Handlers
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_MODULE.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_MODULE.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_CONVENTIONAL.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_CONVENTIONAL.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_SHRAPNEL.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_SHRAPNEL.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_INCENDIARY.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_INCENDIARY.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_DEBILITATION.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_DEBILITATION.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_CHEMICAL.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_CHEMICAL.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ANVIL.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ANVIL.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_REPULSIVE.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_REPULSIVE.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ATTRACTIVE.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ATTRACTIVE.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_NIGHTMARE.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_NIGHTMARE.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_FRAGMENTATION.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_FRAGMENTATION.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_CONTAGIOUS.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_CONTAGIOUS.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_SONIC.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_SONIC.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_BREACHING.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_BREACHING.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_REJUVENATION.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_REJUVENATION.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_THERMOBARIC.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_THERMOBARIC.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_NUCLEAR.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_NUCLEAR.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_EMP.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_EMP.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_EXOTHERMIC.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_EXOTHERMIC.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ENDOTHERMIC.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ENDOTHERMIC.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ANTIGRAVITATIONAL.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ANTIGRAVITATIONAL.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ENDER.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ENDER.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_HYPERSONIC.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_HYPERSONIC.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ANTIMATTER.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ANTIMATTER.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_REDMATTER.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_REDMATTER.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_HOMING.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_HOMING.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_ANTIBALLISTIC.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_ANTIBALLISTIC.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_CLUSTER.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_CLUSTER.get().getDefaultInstance()));
        RenderingRegistry.registerEntityRenderingHandler(EntityReg.MISSILE_CLUSTER_NUCLEAR.get(), (manager) -> new EntityMissileRenderer(manager, ItemReg.MISSILE_CLUSTER_NUCLEAR.get().getDefaultInstance()));

        // Register Tile Entity Renderers
        ClientRegistry.bindTileEntityRenderer((TileEntityType<? extends TileEMPTower>) TileReg.EMP_TOWER.get(), TEREMPTower::new);

    }

    public void onModelRegistryEvent(final ModelRegistryEvent event) {
        ModelLoader.addSpecialModel(MODEL_EMP_TOWER_CLOCKWISE);
        ModelLoader.addSpecialModel(MODEL_EMP_TOWER_COUNTERCLOCKWISE);
        ModelLoader.addSpecialModel(MODEL_EMP_TOWER_STATIC);
    }

    public static final ResourceLocation MODEL_EMP_TOWER_CLOCKWISE = new ResourceLocation(ICBMReference.MODID + ":block/emp_tower_clockwise");
    public static final ResourceLocation MODEL_EMP_TOWER_COUNTERCLOCKWISE = new ResourceLocation(ICBMReference.MODID + ":block/emp_tower_counterclockwise");
    public static final ResourceLocation MODEL_EMP_TOWER_STATIC = new ResourceLocation(ICBMReference.MODID + ":block/emp_tower_static");

    public void setScreenLauncherControlPanel(TileLauncherControlPanel tileEntity) {
        Minecraft.getInstance().setScreen(new ScreenLauncherControlPanel(tileEntity));
    }

    public void updateScreenLauncherControlPanel() {
        if (Minecraft.getInstance().screen instanceof ScreenLauncherControlPanel) {
            ((ScreenLauncherControlPanel)Minecraft.getInstance().screen).updateGui();
        }
    }

}
