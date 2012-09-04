package icbm.models;

import net.minecraft.src.Entity;
import net.minecraft.src.ModelBase;
import net.minecraft.src.ModelRenderer;

public class ModelLauncher1Base extends ModelBase
{
  //fields
    ModelRenderer Shape1;
    ModelRenderer Shape6;
    ModelRenderer Shape2;
    ModelRenderer Shape3;
  
  public ModelLauncher1Base()
  {
    textureWidth = 128;
    textureHeight = 128;
    
      Shape1 = new ModelRenderer(this, 0, 111);
      Shape1.addBox(0F, 0F, 0F, 16, 1, 16);
      Shape1.setRotationPoint(-8F, 23F, -8F);
      Shape1.setTextureSize(128, 128);
      Shape1.mirror = true;
      setRotation(Shape1, 0F, 0F, 0F);
      Shape6 = new ModelRenderer(this, 0, 111);
      Shape6.addBox(0F, 0F, 0F, 16, 1, 16);
      Shape6.setRotationPoint(-8F, 19F, -8F);
      Shape6.setTextureSize(128, 128);
      Shape6.mirror = true;
      setRotation(Shape6, 0F, 0F, 0F);
      Shape2 = new ModelRenderer(this, 63, 0);
      Shape2.addBox(0F, 0F, 0F, 2, 4, 2);
      Shape2.setRotationPoint(-8F, 20F, -1F);
      Shape2.setTextureSize(128, 128);
      Shape2.mirror = true;
      setRotation(Shape2, 0F, 0F, 0F);
      Shape3 = new ModelRenderer(this, 63, 0);
      Shape3.addBox(0F, 0F, 0F, 2, 4, 2);
      Shape3.setRotationPoint(6F, 20F, -1F);
      Shape3.setTextureSize(128, 128);
      Shape3.mirror = true;
      setRotation(Shape3, 0F, 0F, 0F);
  }
  
  @Override
public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Shape1.render(f5);
    Shape6.render(f5);
    Shape2.render(f5);
    Shape3.render(f5);
  }
  
  public void render(float f5)
  {
    Shape1.render(f5);
    Shape6.render(f5);
    Shape2.render(f5);
    Shape3.render(f5);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
  @Override
public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.setRotationAngles(f, f1, f2, f3, f4, f5);
  }

}
