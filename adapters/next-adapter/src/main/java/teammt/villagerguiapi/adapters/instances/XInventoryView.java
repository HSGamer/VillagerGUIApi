package teammt.villagerguiapi.adapters.instances;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.Method;

final class XInventoryView {
    private static final Method getTopInventoryMethod;

    static {
        try {
            Class<?> viewClass = Class.forName("org.bukkit.inventory.InventoryView");
            getTopInventoryMethod = viewClass.getMethod("getTopInventory");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Inventory getTopInventory(InventoryView view) {
        try {
            return (Inventory) getTopInventoryMethod.invoke(view);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
