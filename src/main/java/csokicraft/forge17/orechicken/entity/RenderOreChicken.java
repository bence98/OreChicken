package csokicraft.forge17.orechicken.entity;

import csokicraft.forge17.orechicken.OreChickenMod;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;

public class RenderOreChicken extends RenderChicken{
	private static final ResourceLocation textureFallback = new ResourceLocation(OreChickenMod.MODID+":textures/entity/orechicken.0.png");

	public RenderOreChicken(ModelBase model, float shadow){
		super(model, shadow);
	}
	
	public RenderOreChicken(){
		this(new ModelChicken(), .55f);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityChicken ent){
		return this.getEntityTexture((EntityOreChicken) ent);
	}
	
	protected ResourceLocation getEntityTexture(EntityOreChicken ent){
		return new ResourceLocation(OreChickenMod.MODID+":textures/entity/orechicken."+ent.getOreType()+".png");
	}
}
