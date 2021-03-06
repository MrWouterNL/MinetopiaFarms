package nl.mrwouter.minetopiafarms.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.cryptomorin.xseries.XMaterial;

import nl.mrwouter.minetopiafarms.Main;
import nl.mrwouter.minetopiafarms.utils.ItemBuilder;

public class KiesCMD implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;

		p.sendMessage(Main.getMessage("VeranderenVanEenBaan").replaceAll("<Bedrag>",
				"" + Main.getPlugin().getConfig().getInt("KostenVoorEenBaan")));

		Inventory inv = Bukkit.createInventory(null, 9 * 3, Main.getMessage("InventoryTitle"));

		HashMap<String, XMaterial> beroepen = new HashMap<>();
		Main.getPlugin().getConfig().getConfigurationSection("Banen").getKeys(false).stream()
				.filter(beroep -> Main.getPlugin().getConfig().getBoolean("Banen." + beroep + ".Enabled"))
				.forEach(beroep -> beroepen.put(beroep, XMaterial
						.matchXMaterial(Main.getPlugin().getConfig().getString("Banen." + beroep + ".Item")).get()));

		if (beroepen.size() == 0) {
			inv.setItem(13, new ItemBuilder(Material.BARRIER).setName("§4Er zijn geen beroepen beschikbaar!")
					.addLoreLine("§3Activeer beroepen in de config.").toItemStack());
		} else {
			int startingIndex = 14 - beroepen.size(), index = 0;
			for (String beroep : beroepen.keySet()) {
				inv.setItem(startingIndex + index * 2,
						new ItemBuilder(beroepen.get(beroep).parseItem())
								.setName(Main.getMessage("ItemName").replace("<Beroep>", beroep))
								.addLoreLine(Main.getMessage("ItemLore").replace("<Beroep>", beroep.toLowerCase()))
								.addNBTTag(beroep, "mtfarms_beroep")
								.toItemStack());
				index++;
			}
		}

		p.openInventory(inv);

		return true;
	}
}
