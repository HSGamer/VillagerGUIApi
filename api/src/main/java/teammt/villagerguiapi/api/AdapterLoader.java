package teammt.villagerguiapi.api;

import lombok.Getter;
import org.bukkit.Bukkit;
import teammt.villagerguiapi.adapters.BaseAdapter;
import teammt.villagerguiapi.classes.VillagerInventory;
import teammt.villagerguiapi.utils.VersionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AdapterLoader {

    @Getter
    private static Class<? extends BaseAdapter> adapterClass;

    public static void init() {
        String versionId;
        if (VersionUtils.isAtLeast(14)) {
            versionId = "next";
        } else {
            String v = Bukkit.getServer().getClass().getPackage().getName();
            v = v.substring(v.lastIndexOf('.') + 1);
            if (!v.equals("craftbukkit"))
                versionId = v;
            else {
                String result = "UNK";
                InputStream stream = Bukkit.class.getClassLoader()
                        .getResourceAsStream("META-INF/maven/org.bukkit/bukkit/pom.properties");
                Properties properties = new Properties();
                if (stream != null) {
                    try {
                        properties.load(stream);
                        result = properties.getProperty("version");
                        result = "v" + result.split("-")[0].replace('.', '_');
                    } catch (IOException ignored) {
                    }
                }
                versionId = result;
            }
        }

        Class<? extends BaseAdapter> clazz;
        try {
            Class<?> c = Class.forName("teammt.villagerguiapi.adapters.instances.MerchantAdapter_" + versionId);
            clazz = c.asSubclass(BaseAdapter.class);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find adapter for " + Bukkit.getVersion(), e);
        }
        adapterClass = clazz;
    }

    public static void open(VillagerInventory inv) {
        if (adapterClass == null) {
            throw new RuntimeException("AdapterLoader has not been initialized");
        }

        try {
            adapterClass.getConstructor(VillagerInventory.class).newInstance(inv).openFor(inv.getForWho());
        } catch (Exception e) {
            throw new RuntimeException("Could not open inventory", e);
        }
    }
}
