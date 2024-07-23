package teammt.villagerguiapi.adapters.instances;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import teammt.villagerguiapi.adapters.BaseAdapter;
import teammt.villagerguiapi.classes.VillagerInventory;
import teammt.villagerguiapi.classes.VillagerTrade;
import teammt.villagerguiapi.events.VillagerInventoryCloseEvent;
import teammt.villagerguiapi.events.VillagerInventoryModifyEvent;
import teammt.villagerguiapi.events.VillagerInventoryOpenEvent;
import teammt.villagerguiapi.events.VillagerTradeCompleteEvent;
import teammt.villagerguiapi.utils.VersionUtils;

public class MerchantAdapter_next extends BaseAdapter implements Listener {
    private static final Class<?> CRAFT_MERCHANT_CUSTOM_CLASS;
    private static final Constructor<?> CRAFT_MERCHANT_CUSTOM_CONSTRUCTOR;
    private static final Method CRAFT_MERCHANT_CUSTOM_SET_RECIPES_METHOD;

    static {
        String className;
        if (VersionUtils.isCraftBukkitMapped()) {
            className = "org.bukkit.craftbukkit." + VersionUtils.getCraftBukkitPackageVersion() + ".inventory.CraftMerchantCustom";
        } else {
            className = "org.bukkit.craftbukkit.inventory.CraftMerchantCustom";
        }

        try {
            CRAFT_MERCHANT_CUSTOM_CLASS = Class.forName(className);
            CRAFT_MERCHANT_CUSTOM_CONSTRUCTOR = CRAFT_MERCHANT_CUSTOM_CLASS.getConstructor(String.class);
            CRAFT_MERCHANT_CUSTOM_SET_RECIPES_METHOD = CRAFT_MERCHANT_CUSTOM_CLASS.getMethod("setRecipes", List.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final Merchant wrapped;

    public MerchantAdapter_next(VillagerInventory toAdapt) {
        super(toAdapt);
        Bukkit.getServer().getPluginManager().registerEvents(this,
                org.bukkit.plugin.java.JavaPlugin.getProvidingPlugin(VillagerInventory.class));
        try {
            wrapped = (Merchant) CRAFT_MERCHANT_CUSTOM_CONSTRUCTOR.newInstance(toAdapt.getName());
            CRAFT_MERCHANT_CUSTOM_SET_RECIPES_METHOD.invoke(wrapped, toNMSRecipes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void openFor(Player p) {
        p.openMerchant(wrapped, true);
        VillagerInventoryOpenEvent event = new VillagerInventoryOpenEvent(toAdapt, p);
        Bukkit.getPluginManager().callEvent(event);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer().getUniqueId().equals(this.toAdapt.getForWho().getUniqueId())) {
            VillagerInventoryCloseEvent closeEvent = new VillagerInventoryCloseEvent(toAdapt,
                    (Player) event.getPlayer());
            Bukkit.getPluginManager().callEvent(closeEvent);
            HandlerList.unregisterAll(this); // Kill this event listener
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getUniqueId().equals(this.toAdapt.getForWho().getUniqueId())) {
            VillagerInventoryModifyEvent modifyEvent = new VillagerInventoryModifyEvent(toAdapt,
                    (Player) event.getWhoClicked(), event.getCurrentItem());
            Bukkit.getPluginManager().callEvent(modifyEvent);
            if (event.getRawSlot() == -999)
                return;
            if (event.getRawSlot() == 2 && !event.getCurrentItem().getType().equals(Material.AIR)) {
                ItemStack itemOne = this.toAdapt.getForWho().getOpenInventory().getTopInventory().getItem(0);
                ItemStack itemTwo = this.toAdapt.getForWho().getOpenInventory().getTopInventory().getItem(1);
                ItemStack result = event.getCurrentItem();
                VillagerTradeCompleteEvent completeEvent = new VillagerTradeCompleteEvent(toAdapt,
                        (Player) event.getWhoClicked(), new VillagerTrade(itemOne, itemTwo, result, -1));
                Bukkit.getPluginManager().callEvent(completeEvent);
            }
        }
    }

    public List<MerchantRecipe> toNMSRecipes() {
        List<MerchantRecipe> result = new ArrayList<>();
        for (VillagerTrade trd : this.toAdapt.getTrades()) {
            MerchantRecipe toAdd = new MerchantRecipe(trd.getResult(), trd.getMaxUses());
            toAdd.addIngredient(trd.getItemOne());
            if (trd.requiresTwoItems())
                toAdd.addIngredient(trd.getItemTwo());
            result.add(toAdd);
        }

        return result;
    }
}
