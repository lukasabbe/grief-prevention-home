package me.lukasabbe.griefpreventionhome.commands;

import me.lukasabbe.griefpreventionhome.GriefPreventionHome;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PlotsCommand implements CommandExecutor, Listener {

    NamespacedKey playerUuidKey = new NamespacedKey(GriefPreventionHome.instance, "player-uuid");
    NamespacedKey plotIdKey = new NamespacedKey(GriefPreventionHome.instance, "plot-id");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        OfflinePlayer player;
        Player senderPlayer = (Player) sender;
        if (args.length != 0){
            player = GriefPrevention.instance.resolvePlayerByName(args[0]);
        }else{
            player = (Player) sender;
        }
        if(!player.hasPlayedBefore()){
            senderPlayer.sendMessage("Can't find that player");
            return true;
        }
        Map<Integer,Claim> map = getClaimMap(player.getUniqueId());
        if(map == null){
            senderPlayer.sendMessage("That player don't have any plots");
            return true;
        }
        Inventory inventory = Bukkit.createInventory(null,27,"Plots");
        for(int i = 0 ; i < map.size() ; i++){
            final Claim claim = map.get(i);
            Location plotLocation = getMiddle(claim.getGreaterBoundaryCorner(), claim.getLesserBoundaryCorner());
            final ItemStack itemStack = new ItemStack(Material.CHEST);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("Plot " + (i+1));
            itemMeta.getPersistentDataContainer().set(playerUuidKey, PersistentDataType.STRING, player.getUniqueId().toString());
            itemMeta.getPersistentDataContainer().set(plotIdKey, PersistentDataType.INTEGER, i);
            itemMeta.setLore(Arrays.asList(
                    "X: " + plotLocation.getX() + " Z: " + plotLocation.getBlockZ(),
                    "Click me to teleport to plot"));
            itemStack.setItemMeta(itemMeta);
            inventory.addItem(itemStack);
        }
        senderPlayer.openInventory(inventory);

        return true;
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null) return;

        if(!event.getView().getTitle().equals("Plots")) return;

        event.setCancelled(true);
        if(event.getCurrentItem() == null) return;
        if(event.getCurrentItem().getItemMeta() == null) return;
        final PersistentDataContainer persistentDataContainer = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
        if(persistentDataContainer.get(playerUuidKey, PersistentDataType.STRING) == null) return;
        Map<Integer, Claim> claims = PlotsCommand.getClaimMap(UUID.fromString(persistentDataContainer.get(playerUuidKey,PersistentDataType.STRING)));
        Claim claim = claims.get(persistentDataContainer.get(plotIdKey,PersistentDataType.INTEGER));
        player.teleport(PlotsCommand.getMiddle(claim.getGreaterBoundaryCorner(), claim.getLesserBoundaryCorner()));
    }

    public static Map<Integer,Claim> getClaimMap(UUID player){
        List<Claim> playerClaims = GriefPrevention.instance.dataStore.getPlayerData(player).getClaims();
        if(playerClaims.isEmpty()){
            return null;
        }
        Map<Integer,Claim> map = new HashMap<>();
        for (int i = 0; i < playerClaims.size();i++){
            map.put(i, playerClaims.get(i));
        }
        return map;
    }

    public static Location getMiddle(Location pos1, Location pos2){
        double x = (pos1.getX() + pos2.getX())/2;
        double z = (pos1.getZ() + pos2.getZ())/2;
        double y = pos1.getWorld().getHighestBlockAt((int)x, (int)z).getY();
        return new Location(pos1.getWorld(),x, y, z);
    }
}
