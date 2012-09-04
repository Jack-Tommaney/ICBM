package icbm.explosions;

import icbm.EntityGravityBlock;
import icbm.EntityProceduralExplosion;
import icbm.ParticleSpawner;

import java.util.List;

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MathHelper;
import net.minecraft.src.World;
import universalelectricity.Vector3;

public class ExplosiveRedmatter extends Explosive
{	
	public static final int MAX_RADIUS = 37;
	private static final int MAX_TAKE_BLOCKS = 15;
	
	public ExplosiveRedmatter(String name, int ID, int tier)
	{
		super(name, ID, tier);
	}

	//Sonic Explosion is a procedural explosive
	@Override
	public boolean doExplosion(World worldObj, Vector3 position, Entity explosionSource, int explosionMetadata, int callCount)
	{
		if(worldObj.isRemote)
		{
			//Spawn red matter particle
			ParticleSpawner.spawnParticle("smoke", worldObj, position, 0.15F, 0F, 0F, 10F, 2F);
			ParticleSpawner.spawnParticle("smoke", worldObj, Vector3.add(position, 0.5), 0.15F, 0F, 0F, 7F, 2F);
			ParticleSpawner.spawnParticle("smoke", worldObj, Vector3.add(position, -0.5), 0.15F, 0F, 0F, 7F, 2F);
			ParticleSpawner.spawnParticle("smoke", worldObj, Vector3.add(position, new Vector3(0.5, 0, -0.5)), 0.15F, 0F, 0F, 7F, 2F);
			ParticleSpawner.spawnParticle("smoke", worldObj, Vector3.add(position, new Vector3(-0.5, 0, 0.5)), 0.15F, 0F, 0F, 7F, 2F);
		}
		
		//Try to find and grab some blocks to orbit
		Vector3 currentPos;
		int blockID;
		int metadata;
		double dist;
		int takenBlocks = 0;
		
		for(int r = 1; r < MAX_RADIUS; r ++)
		{
			for(int x = -r; x < r; x++)
			{
				for(int y = -r; y < r; y++)
				{
					for(int z = -r; z < r; z++)
					{
						dist = MathHelper.sqrt_double((x*x + y*y + z*z));
						
						if(dist > r || dist < r-2) continue;
						
						currentPos = new Vector3(position.x + x, position.y + y, position.z + z);
						blockID = worldObj.getBlockId(currentPos.intX(), currentPos.intY(), currentPos.intZ());
						
						if(blockID == 0 || blockID == Block.bedrock.blockID) continue;
						
						if(dist < r - 1 || worldObj.rand.nextInt(10) > 5)
    					{
							metadata = worldObj.getBlockMetadata(currentPos.intX(), currentPos.intY(), currentPos.intZ());
							
							worldObj.setBlockWithNotify(currentPos.intX(), currentPos.intY(), currentPos.intZ(), 0);
							
							if(blockID == Block.waterMoving.blockID || blockID == Block.lavaMoving.blockID || blockID == Block.waterStill.blockID || blockID == Block.lavaStill.blockID) continue;
							
							if(worldObj.rand.nextInt(10) > 3)
							{
								currentPos.add(0.5D);
								
								if(!worldObj.isRemote)
								{
									EntityGravityBlock entity = new EntityGravityBlock(worldObj, currentPos, blockID, metadata);
									worldObj.spawnEntityInWorld(entity);
									entity.yawChange = 50*worldObj.rand.nextFloat();
									entity.pitchChange = 50*worldObj.rand.nextFloat();
								}
							}
							
							takenBlocks++;
							if(takenBlocks > MAX_TAKE_BLOCKS) break;
    					}
					}
					if(takenBlocks > MAX_TAKE_BLOCKS) break;
				}
				if(takenBlocks > MAX_TAKE_BLOCKS) break;
			}
			if(takenBlocks > MAX_TAKE_BLOCKS) break;
		}

		//Make the blocks controlled by this red matter orbit around it
		int radius = MAX_RADIUS;
		AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        List<Entity> allEntities = worldObj.getEntitiesWithinAABB(Entity.class, bounds);
    	boolean explosionCreated = false;
        
        for(Entity entity : allEntities)
        {            
        	if(entity instanceof EntityProceduralExplosion) continue;
        	
        	if(entity instanceof EntityPlayer)
        	{
        		if(((EntityPlayer)entity).capabilities.isCreativeMode) continue;
        	}
        	
			double xDifference = entity.posX - position.x;
			double yDifference = entity.posY - position.y;
        	double zDifference = entity.posZ - position.z;
        	            	
        	int r = MAX_RADIUS;
        	if(xDifference < 0) r = (int)-MAX_RADIUS;
        	
        	entity.motionX -= (r-xDifference) * 0.02*worldObj.rand.nextFloat();
        	
        	r = MAX_RADIUS;
        	if(entity.posY > position.y) r = -MAX_RADIUS;
            entity.motionY += (r-yDifference) * 0.02*worldObj.rand.nextFloat();
            
            r = (int)MAX_RADIUS;
            if(zDifference < 0) r = (int)-MAX_RADIUS;
            
            entity.motionZ -= (r-zDifference) * 0.02*worldObj.rand.nextFloat();
            
            if(Vector3.distance(new Vector3(entity.posX, entity.posY, entity.posZ), position) < 4)
            {
            	if(!explosionCreated)
            	{
            		worldObj.createExplosion(explosionSource, entity.posX, entity.posY, entity.posZ, 3.0F);
            		explosionCreated = true;
            	}
            	
            	if(entity instanceof EntityGravityBlock || entity instanceof EntityItem)
            	{
            		entity.setDead();
            	}
            }
        }

		worldObj.playSoundEffect(position.x, position.y, position.z, "icbm.redmatter", 4.0F, (1.0F + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 0.2F) * 1F);

        return true;
	}
	
	/**
	 * The interval in ticks before the next procedural call of this explosive
	 * @return - Return -1 if this explosive does not need proceudral calls
	 */
	@Override
	public int proceduralInterval() { return 5; }
}
