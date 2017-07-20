package csokicraft.forge17.orechicken.item;

import java.util.List;

import csokicraft.forge17.orechicken.*;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;

public class ItemOrePart extends Item{
	protected IIcon[] icons = new IIcon[OreChickenTypes.oredicts.length];
	protected String partType;
	
	public ItemOrePart(String type){
		setHasSubtypes(true);
		partType=type;
	}

	@Override
	public void registerIcons(IIconRegister reg){
		for(int i=0;i<icons.length;i++)
			icons[i]=reg.registerIcon(OreChickenMod.MODID+":"+partType+"."+i);
	}
	
	@Override
	public IIcon getIconFromDamage(int i){
		return icons[i];
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		return OreChickenMod.MODID+":item."+partType+"."+is.getItemDamage();
	}
	
	@Override
	public void getSubItems(Item it, CreativeTabs tab, List l){
		for(int i=0;i<icons.length;i++)
			l.add(new ItemStack(it, 1, i));
	}
}
