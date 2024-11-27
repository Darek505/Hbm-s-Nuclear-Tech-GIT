package com.hbm.render.tileentity;

import org.lwjgl.opengl.GL11;

import com.hbm.blocks.BlockDummyable;
import com.hbm.main.ResourceManager;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.hbm.render.util.BeamPronter.EnumWaveType;
import com.hbm.tileentity.machine.TileEntityDysonReceiver;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

public class RenderDysonReceiver extends TileEntitySpecialRenderer {

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
		GL11.glPushMatrix();
		{

			GL11.glTranslated(x + 0.5D, y, z + 0.5D);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_CULL_FACE);
			
			TileEntityDysonReceiver receiver = (TileEntityDysonReceiver) tileEntity;
			
			switch(tileEntity.getBlockMetadata() - BlockDummyable.offset) {
			case 2: GL11.glRotatef(0, 0F, 1F, 0F); break;
			case 4: GL11.glRotatef(90, 0F, 1F, 0F); break;
			case 3: GL11.glRotatef(180, 0F, 1F, 0F); break;
			case 5: GL11.glRotatef(270, 0F, 1F, 0F); break;
			}
			
			GL11.glShadeModel(GL11.GL_SMOOTH);
			bindTexture(ResourceManager.dyson_receiver_tex);
			ResourceManager.dyson_receiver.renderPart("DysonReceiver");

			float t = tileEntity.getWorldObj().getTotalWorldTime() + f;

			GL11.glTranslatef(0.0F, 1.0F, 0.0F);

			GL11.glPushMatrix();
			{
				GL11.glRotatef(t, 0, 0, 1);
				GL11.glTranslated(0.0F, -1.0F, 0.0F);
				ResourceManager.dyson_receiver.renderPart("Coil1");
				ResourceManager.dyson_receiver.renderPart("Coil3");
			}
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			{
				GL11.glRotatef(t, 0, 0, -1);
				GL11.glTranslated(0.0F, -1.0F, 0.0F);
				ResourceManager.dyson_receiver.renderPart("Coil2");
			}
			GL11.glPopMatrix();

			int length = receiver.beamLength;
			int color = 0xff8800;

			if(receiver.swarmCount > 0) {
				GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
				GL11.glDisable(GL11.GL_LIGHTING);

				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240F, 240F);
				
				BeamPronter.prontBeamwithDepth(Vec3.createVectorHelper(0, 0, length + 1), EnumWaveType.SPIRAL, EnumBeamType.SOLID, color, color, 0, 1, 0F, 2, 0.4F, 0.5F);
				BeamPronter.prontBeamwithDepth(Vec3.createVectorHelper(0, 0, length + 1), EnumWaveType.RANDOM, EnumBeamType.SOLID, color, color, (int)(tileEntity.getWorldObj().getTotalWorldTime() % 1000), (length / 2) + 1, 0.0625F, 2, 0.4F, 0.5F);
				
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glPopAttrib();
			}

			GL11.glShadeModel(GL11.GL_FLAT);
			
		}
		GL11.glPopMatrix();
	}
	
}
