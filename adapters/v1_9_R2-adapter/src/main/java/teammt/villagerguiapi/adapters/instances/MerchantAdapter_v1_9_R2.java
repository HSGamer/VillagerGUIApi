package teammt.villagerguiapi.adapters.instances;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import teammt.villagerguiapi.adapters.BaseAdapter;
import teammt.villagerguiapi.classes.VillagerInventory;
import teammt.villagerguiapi.classes.VillagerTrade;
import teammt.villagerguiapi.events.VillagerInventoryCloseEvent;
import teammt.villagerguiapi.events.VillagerInventoryModifyEvent;
import teammt.villagerguiapi.events.VillagerInventoryOpenEvent;
import teammt.villagerguiapi.events.VillagerTradeCompleteEvent;
import net.minecraft.server.v1_9_R2.ChatComponentText;
import net.minecraft.server.v1_9_R2.EntityHuman;
import net.minecraft.server.v1_9_R2.IChatBaseComponent;
import net.minecraft.server.v1_9_R2.IMerchant;
import net.minecraft.server.v1_9_R2.MerchantRecipe;
import net.minecraft.server.v1_9_R2.MerchantRecipeList;

public class MerchantAdapter_v1_9_R2 extends BaseAdapter implements IMerchant {

	public MerchantAdapter_v1_9_R2(VillagerInventory toAdapt) {
		super(toAdapt);
	}

	@Override
	public void a(MerchantRecipe arg0) {
		VillagerTrade trd = new VillagerTrade(CraftItemStack.asBukkitCopy(arg0.getBuyItem1()),
				CraftItemStack.asBukkitCopy(arg0.getBuyItem2()), CraftItemStack.asBukkitCopy(arg0.getBuyItem3()),
				arg0.f());
		VillagerTradeCompleteEvent event = new VillagerTradeCompleteEvent(toAdapt, toAdapt.getForWho(), trd);
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public void a(net.minecraft.server.v1_9_R2.ItemStack arg0) {
		VillagerInventoryModifyEvent event = new VillagerInventoryModifyEvent(toAdapt, toAdapt.getForWho(),
				CraftItemStack.asBukkitCopy(arg0));
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public MerchantRecipeList getOffers(EntityHuman arg0) {
		MerchantRecipeList rpl = new MerchantRecipeList();
		for (VillagerTrade trd : toAdapt.getTrades()) {
			MerchantRecipe toAdd = null;

			net.minecraft.server.v1_9_R2.ItemStack itm1 = CraftItemStack.asNMSCopy(trd.getItemOne());
			net.minecraft.server.v1_9_R2.ItemStack itm2 = null;
			net.minecraft.server.v1_9_R2.ItemStack result = CraftItemStack.asNMSCopy(trd.getResult());
			if (trd.requiresTwoItems())
				itm2 = CraftItemStack.asNMSCopy(trd.getItemTwo());
			else
				itm2 = CraftItemStack.asNMSCopy(new ItemStack(Material.AIR));

			toAdd = new MerchantRecipe(itm1, itm2, result, 0, trd.getMaxUses());

			rpl.add(toAdd);
		}
		return rpl;
	}

	@Override
	public IChatBaseComponent getScoreboardDisplayName() {
		return new ChatComponentText(toAdapt.getName());
	}

	@Override
	public void openFor(Player p) {
		((CraftPlayer) p).getHandle().openTrade(this);
		VillagerInventoryOpenEvent event = new VillagerInventoryOpenEvent(toAdapt, toAdapt.getForWho());
		Bukkit.getServer().getPluginManager().callEvent(event);
	}

	@Override
	public void setTradingPlayer(EntityHuman arg0) {
		if (arg0 == null) {
			VillagerInventoryCloseEvent event = new VillagerInventoryCloseEvent(toAdapt, toAdapt.getForWho());
			Bukkit.getServer().getPluginManager().callEvent(event);
		}
	}

	@Override
	public EntityHuman getTrader() {
		return ((CraftPlayer) toAdapt.getForWho()).getHandle();
	}

}
