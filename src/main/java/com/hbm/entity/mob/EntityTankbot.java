package com.hbm.entity.mob;

import java.util.List;

import com.hbm.entity.mob.ai.EntityAIBehemothGun;
import com.hbm.entity.mob.ai.EntityAIMaskmanMinigun;
import com.hbm.entity.mob.ai.EntityAIStepTowardsTarget;
import com.hbm.entity.mob.ai.EntityAITankshell;
import com.hbm.entity.mob.ai.EntityAITest;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import com.hbm.particle.helper.ExplosionCreator;
import com.hbm.util.ParticleUtil;

import api.hbm.entity.IRadiationImmune;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;

public class EntityTankbot extends EntityMob implements IRangedAttackMob, IRadiationImmune {
    private int stepTimer = 0;
	public double headTargetYaw; 
	public double currentHeadYaw; 
	public EntityLivingBase targetEntityLiving;
    private static final IEntitySelector selector = new IEntitySelector() {
		public boolean isEntityApplicable(Entity p_82704_1_) {
			return !(p_82704_1_ instanceof EntityTankbot);
		}
	};

    public EntityTankbot(World p_i1733_1_)
    {
        super(p_i1733_1_);
        this.setSize(0.75F, 1.35F);
        this.getNavigator().setAvoidsWater(true);
        this.deathTime = -50;
        this.isImmuneToFire = true;
        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
        this.tasks.addTask(5, new EntityAIWander(this, 0.2D));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, true, true, selector));
		this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, false));
        this.tasks.addTask(3, new EntityAIStepTowardsTarget(this, 20, 0.2D, 20, 20, 0.6));
        //this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 0.2D, false));
		this.tasks.addTask(4, new EntityAITankshell(this, true, true, 2, 20, 40));
    }
	protected void onDeathUpdate() {
		
		if(this.deathTime == -30) {
			worldObj.playSoundAtEntity(this, "hbm:entity.chopperDamage", 10.0F, 1.0F);
		}
		if(this.deathTime >= -30 && !worldObj.isRemote) {
		ParticleUtil.spawnGasFlame(worldObj, this.posX, this.posY + 1, this.posZ, rand.nextGaussian(), 0.4, rand.nextGaussian());
		}
		if(this.deathTime == -5 && !worldObj.isRemote) {
			worldObj.newExplosion(this, posX, posY, posZ, 10F, true, true);
			ExplosionCreator.composeEffectSmall(worldObj, this.posX, this.posY, this.posZ);
			this.setDead();

		}
		
		super.onDeathUpdate();
	}
	@Override
	protected Entity findPlayerToAttack() {
		if(this.isPotionActive(Potion.blindness)) return null;

		return this.worldObj.getClosestVulnerablePlayerToEntity(this, 64D);
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase p_82196_1_, float p_82196_2_) {
		// TODO Auto-generated method stub
		
	}

	
	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(20, (int) 0);
	}
	
    @Override
    public void setAttackTarget(EntityLivingBase entity) {
        super.setAttackTarget(entity);
        this.dataWatcher.updateObject(20, entity != null ? entity.getEntityId() : 0);
    }
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		
		if(source instanceof EntityDamageSourceIndirect && ((EntityDamageSourceIndirect) source).getSourceOfDamage() instanceof EntityEgg && rand.nextInt(10) == 0) {
			this.experienceValue = 0;
			this.setHealth(0);
			return true;
		}

		if(source.isFireDamage())
			amount = 0;
		if(source.isMagicDamage())
			amount = 0;
		if(source.isProjectile())
			amount *= 0.25F;
		if(source.isExplosion())
			amount *= 0.5F;

		if(amount > 50) {
			amount = 50 + (amount - 50) * 0.25F;
		}

		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(300.0D);
		this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(15.0D);
		this.getEntityAttribute(SharedMonsterAttributes.knockbackResistance).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(140.0D);

	}


    @Override
	protected String getHurtSound()
    {
        return "hbm:entity.cybercrab";
    }
	@Override
	public boolean isAIEnabled() {
		return true;
	}
	
    @Override
    public void onUpdate() {
        super.onUpdate();
    }
    
    public double getMaxTargetRange() {
        return 64;
    }
}
