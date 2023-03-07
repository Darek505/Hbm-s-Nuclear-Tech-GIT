package com.hbm.dim.duna;

import java.util.Random;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.BlockEnums.EnumStoneType;
import com.hbm.config.GeneralConfig;
import com.hbm.config.WorldConfig;
import com.hbm.world.feature.OreLayer3D;
import com.hbm.world.generator.DungeonToolbox;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class WorldGeneratorDuna implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		//switch (world.provider.dimensionId) {
		if(world.provider instanceof WorldProviderDuna)
		{
			generateMoon(world, random, chunkX * 16, chunkZ * 16);
		}
	}
	private void generateMoon(World world, Random rand, int i, int j) {
		//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.nickelSpawn, 8, 1, 43, ModBlocks.moon_nickel, ModBlocks.moon_rock);
	///	//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.titaniumSpawn, 9, 4, 27, ModBlocks.moon_titanium, ModBlocks.moon_rock);
		//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.lithiumSpawn,  4, 4, 8, ModBlocks.moon_lithium, ModBlocks.moon_rock);
		//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.aluminiumSpawn,  6, 5, 40, ModBlocks.moon_aluminium, ModBlocks.moon_rock);
		//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.hematiteSpawn, 10, 4, 80, ModBlocks.moon_conglomerate, ModBlocks.moon_rock);
		//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.nickelSpawn, 6, 5, 16, ModBlocks.moon_nickel);
		//DungeonToolbox.generateOre(world, rand, i, j, WorldConfig.titaniumSpawn, 6, 5, 8, ModBlocks.moon_titanium);
		//new OreLayer3D(ModBlocks.stone_resource, EnumStoneType.HEMATITE.ordinal());
	}
}