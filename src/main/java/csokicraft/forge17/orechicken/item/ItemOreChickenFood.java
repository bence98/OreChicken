package csokicraft.forge17.orechicken.item;

import cpw.mods.fml.relauncher.*;
import csokicraft.forge17.orechicken.*;
import csokicraft.forge17.orechicken.entity.EntityOreChicken;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemOreChickenFood extends ItemOrePart{
	public ItemOreChickenFood() {
		super("chickenfood");
	}
	
	@Override
	public boolean itemInteractionForEntity(ItemStack is, EntityPlayer p, EntityLivingBase ent){
		if(ent instanceof EntityChicken){
			if(!p.getEntityWorld().isRemote){
				enrichChicken(p.getEntityWorld(), is.getItemDamage(), (EntityChicken) ent);
				if(!p.capabilities.isCreativeMode) is.stackSize--;
				if(is.stackSize<=0) is=null;
			}
			return true;
		}
		return super.itemInteractionForEntity(is, p, ent);
	}
	
	protected void enrichChicken(World w, int type, EntityChicken e){
		EntityOreChicken och=new EntityOreChicken(w);
		och.setOreType(type);
		Vec3 pos=e.getPosition(1);
		och.setPosition(pos.xCoord, pos.yCoord, pos.zCoord);
		if(e.hasCustomNameTag())
			och.setCustomNameTag(e.getCustomNameTag());
		
		w.spawnEntityInWorld(och);
		e.setDead();
		
		w.spawnParticle("heart", pos.xCoord, pos.yCoord+1, pos.zCoord, 0, 0, 0);
		w.playSoundAtEntity(och, "mob.zombie.unfect", 1, 1);
	}
}
