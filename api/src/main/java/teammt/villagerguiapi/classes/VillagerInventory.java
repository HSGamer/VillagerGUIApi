package teammt.villagerguiapi.classes;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import teammt.villagerguiapi.api.AdapterLoader;

@Setter
@Getter
public class VillagerInventory {
	private List<VillagerTrade> trades = new ArrayList<>();
	private String name = "Sample text";
	private Player forWho;

	public VillagerInventory(List<VillagerTrade> trades, Player forWho) {
		this.trades = trades;
		this.forWho = forWho;
	}

	public VillagerInventory() {
	}

    public void open() {
		AdapterLoader.open(this);
	}
}
