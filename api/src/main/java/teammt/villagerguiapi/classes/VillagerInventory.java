package teammt.villagerguiapi.classes;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import teammt.villagerguiapi.api.AdapterLoader;

@Getter
public class VillagerInventory {
	private final Plugin plugin;

	@Setter
    private List<VillagerTrade> trades = new ArrayList<>();
	@Setter
    private String name = "Sample text";
	@Setter
    private Player forWho;

	public VillagerInventory(Plugin plugin) {
		this.plugin = plugin;
	}

	public VillagerInventory() {
		this(JavaPlugin.getProvidingPlugin(VillagerInventory.class));
	}

	public VillagerInventory(Plugin plugin, List<VillagerTrade> trades, Player forWho) {
		this(plugin);
        this.trades = trades;
		this.forWho = forWho;
	}

	public VillagerInventory(List<VillagerTrade> trades, Player forWho) {
		this(JavaPlugin.getProvidingPlugin(VillagerInventory.class), trades, forWho);
	}

    public void open() {
		AdapterLoader.open(this);
	}
}
