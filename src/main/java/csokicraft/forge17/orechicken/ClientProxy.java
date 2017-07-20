package csokicraft.forge17.orechicken;

import java.util.List;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import csokicraft.forge17.orechicken.entity.EntityOreChicken;
import csokicraft.forge17.orechicken.entity.RenderOreChicken;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy{
	@Override
	public void initRenderer(FMLInitializationEvent evt) {
		RenderingRegistry.registerEntityRenderingHandler(EntityOreChicken.class, new RenderOreChicken());
	}

	@Override
	public IMessage onMessage(PacketOreChickenType message, MessageContext ctx){
		World w=Minecraft.getMinecraft().theWorld;
		Vec3 pos=message.pos;
		try{
			List l=w.getEntitiesWithinAABB(EntityOreChicken.class, AxisAlignedBB.getBoundingBox(pos.xCoord-.5, pos.yCoord-.5, pos.zCoord-.5, pos.xCoord+.5, pos.yCoord+.5, pos.zCoord+.5));
			EntityOreChicken ent=(EntityOreChicken) l.get(0);
			ent.setOreType(message.type);
		}catch (Exception e) {
			System.err.println("[OreChickenMod] Couldn't sync entity");
			e.printStackTrace();
		}
		
		return null;
	}
}
