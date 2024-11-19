package com.hbm.dim.trait;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import com.hbm.config.SpaceConfig;
import com.hbm.dim.CelestialBody;
import com.hbm.dim.trait.CBT_War.ProjectileType;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import scala.reflect.internal.Trees.This;

public class CBT_War extends CelestialBodyTrait {
	
	//when you engage with one planet or the other, they are at war.
	//both planets will have this file to dictate the engagment.
	//that way they can share generic values to dictate who has what. 
	//grins
	
	//effects are all client side. these variables dictate shit like health and shield or whatevs
	
	public int health;
	public int shield;
	
	public static List<Projectile> projectiles;
	
	public CBT_War() {
		this.health = 100;
		this.shield = 0;
		this.projectiles = new ArrayList<>();
	}

	public CBT_War(int health, int shield) {
		this.health = health;
		this.shield = shield;
		this.projectiles = new ArrayList<>();
	}
	
	public void launchProjectile(Projectile proj) {
		projectiles.add(proj);
	}
	public void launchProjectile(float traveltime, int size, int damage, float x, double y, double z, ProjectileType type) {
		Projectile projectile = new Projectile(traveltime, size, damage, x, y, z, type);
		projectiles.add(projectile);
	}

	public void split(World world, int amount, Projectile projectile, ProjectileType type) {
        CBT_War war = CelestialBody.getTrait(world, CBT_War.class);
                       
                //currently kind of temp, there might be a better way to generalize this
    	   if (projectile.getTravel() <= 0) {
    		   for (int j = 0; j < amount; j++) {
    			   	float rand = Minecraft.getMinecraft().theWorld.rand.nextFloat();
            		war.launchProjectile(Math.abs(20 + j * 10), 
                    projectile.getSize(), 
                    projectile.getDamage(), 
                    (float) (projectile.getTranslateX() * rand * j), 
                    projectile.getTranslateY(), 
                    projectile.getTranslateZ() * rand * j, 
                    type);
    		   }
            war.destroyProjectile(projectile); 
            
        }
	}
	
	public void destroyProjectile(Projectile proj) {
		projectiles.remove(proj);
	}

    public static List<Projectile> getProjectiles() {
        return projectiles;
    }
	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setInteger("health", health);
		nbt.setInteger("shield", shield);
		
        NBTTagCompound projectilesTag = new NBTTagCompound();
        for (int i = 0; i < projectiles.size(); i++) {
        	NBTTagCompound projTag = new NBTTagCompound();
            projectiles.get(i).writeToNBT(projTag);
            projectilesTag.setTag("projectile" + i, projTag);
        }
        nbt.setTag("projectiles", projectilesTag);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		shield = nbt.getInteger("shield");
		health = nbt.getInteger("health");
	    NBTTagCompound projectilesTag = nbt.getCompoundTag("projectiles");
	    projectiles = new ArrayList<>();
	    for (int i = 0; projectilesTag.hasKey("projectile" + i); i++) {
	        NBTTagCompound projTag = projectilesTag.getCompoundTag("projectile" + i);
	        Projectile projectile = new Projectile();
	        projectile.readFromNBT(projTag);
	        projectiles.add(projectile);
	    }	
	}

	@Override
	public void writeToBytes(ByteBuf buf) {
		buf.writeInt(health);
		buf.writeInt(shield);
        buf.writeInt(projectiles.size());
        for (Projectile projectile : projectiles) {
            projectile.writeToBytes(buf);
        }
	}

	@Override
	public void readFromBytes(ByteBuf buf) {
		shield = buf.readInt();
		health = buf.readInt();
        int numProjectiles = buf.readInt();
        projectiles = new ArrayList<>(numProjectiles);
        for (int i = 0; i < numProjectiles; i++) {
            Projectile projectile = new Projectile();
            projectile.readFromBytes(buf);
            projectiles.add(projectile);
        }

	}
	public static class Projectile {
	    private float traveltime;
	    private int size;
	    private int damage;
	    private int animtime;
	    private float flashtime;
	    private double translateX;
	    private double translateY;
	    private double translateZ;
	    private ProjectileType type; // New field for projectile type

	    public Projectile() {
	        this.animtime = 0;
	        this.flashtime = 0;
	        this.type = ProjectileType.MEDIUM; // Default type
	    }

	    public Projectile(float traveltime, int size, int damage, float posX, double posY, double posZ, ProjectileType type) {
	        this.traveltime = traveltime;
	        this.size = size;
	        this.damage = damage;
	        this.animtime = 0;
	        this.flashtime = 0;
	        this.translateX = posX;
	        this.translateY = posY;
	        this.translateZ = posZ;
	        this.type = type;
	    }

	    public void writeToNBT(NBTTagCompound nbt) {
	        nbt.setInteger("damage", damage);
	        nbt.setFloat("traveltime", traveltime);
	        nbt.setInteger("size", size);
	        nbt.setDouble("translateX", translateX);
	        nbt.setDouble("translateY", translateY);
	        nbt.setDouble("translateZ", translateZ);
	        nbt.setInteger("animtime", animtime);
	        nbt.setString("type", type.name()); // Serialize the type
	    }

	    public void readFromNBT(NBTTagCompound nbt) {
	        damage = nbt.getInteger("damage");
	        traveltime = nbt.getFloat("traveltime");
	        size = nbt.getInteger("size");
	        translateX = nbt.getDouble("translateX");
	        translateY = nbt.getDouble("translateY");
	        translateZ = nbt.getDouble("translateZ");
	        animtime = nbt.getInteger("animtime");
	        type = ProjectileType.valueOf(nbt.getString("type"));
	    }

	    public void writeToBytes(ByteBuf buf) {
	        buf.writeInt(damage);
	        buf.writeFloat(traveltime);
	        buf.writeInt(size);
	        buf.writeDouble(translateX);
	        buf.writeDouble(translateY);
	        buf.writeDouble(translateZ);
	        buf.writeInt(animtime);
	        buf.writeByte(type.ordinal()); 
	    }

	    public void readFromBytes(ByteBuf buf) {
	        damage = buf.readInt();
	        traveltime = buf.readFloat();
	        size = buf.readInt();
	        translateX = buf.readDouble();
	        translateY = buf.readDouble();
	        translateZ = buf.readDouble();
	        animtime = buf.readInt();
	        type = ProjectileType.values()[buf.readByte()];
	    }

	    // Getter and setter for the type
	    public ProjectileType getType() {
	        return type;
	    }

	    public void setType(ProjectileType type) {
	        this.type = type;
	    }

	    public float getFlashtime() {
	        return flashtime;
	    }

	    public void update() {

	        if (traveltime > 0) {
	            traveltime--;
	        } else {
		            traveltime = 0;
		        	if(this.getType() != ProjectileType.SPLITSHOT) {
		            animtime = Math.min(100, animtime + 1);	
	        	}
	        }
	    }

	    public void impact() {
	    	
	        flashtime += 0.1f;
	        flashtime = Math.min(100.0f, flashtime + 0.1f * (100.0f - flashtime) * 0.15f);

	        if (flashtime >= 100) {
	            flashtime = 100;
	        }

	    }


	    public int getSize() {
	        return size;
	    }

	    public void setSize(int size) {
	        this.size = size;
	    }

	    public int getDamage() {
	        return damage;
	    }

	    public float getTravel() {
	        return traveltime;
	    }

	    public void setTravel(float travel) {
	        this.traveltime = travel;
	    }

	    public void setDamage(int damage) {
	        this.damage = damage;
	    }

	    public int getAnimtime() {
	        return animtime;
	    }

	    public void setAnimtime(int animtime) {
	        this.animtime = animtime;
	    }

	    public void setFlashtime(float flashtime) {
	        this.flashtime = flashtime;
	    }
	    public double getTranslateX() {
	        return translateX;
	    }

	    public double getTranslateY() {
	        return translateY;
	    }

	    public double getTranslateZ() {
	        return translateZ;
	    }
	}
	public enum ProjectileType {
		SMALL,
		MEDIUM,
		HUGE,
		INCENDIARY,
		NUCLEAR,
		SPLITSHOT
	    // Add more types as needed
	}
}
