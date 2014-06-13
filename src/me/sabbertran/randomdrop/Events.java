package me.sabbertran.randomdrop;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import me.sabbertran.randomdrop.commands.RegionCommand;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener
{

    private RandomDrop main;
    private RegionCommand rgcmd;

    public Events(RandomDrop rd, RegionCommand rgcmd)
    {
        this.main = rd;
        this.rgcmd = rgcmd;
    }

    @EventHandler
    public boolean PlayerMoveEvent(PlayerMoveEvent ev)
    {
        Player p = (Player) ev.getPlayer();

        if (main.getPluginEnabled())
        {
            for (String world : this.main.worlds)
            {
                if (p.getWorld().getName().equals(world))
                {
                    main.drop(p);
                    return true;
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean PositionSelectEvent(PlayerInteractEvent ev)
    {
        Player p = ev.getPlayer();

        if (p.hasPermission("randomdrop.region.postool") || p.isOp())
        {
            ItemStack is = this.main.getTool();
            ItemMeta im = is.getItemMeta();

            ArrayList<String> lore = new ArrayList<String>();
            lore.add("§aSelect the corners for RandomDrop regions with this");

            im.setLore(lore);
            im.setDisplayName("§bRandomDrop Tool");
            is.setItemMeta(im);

            if (ev.getItem() != null)
            {
                if (ev.getItem().equals(is))
                {
                    if (ev.getAction() == Action.LEFT_CLICK_BLOCK)
                    {
                        ev.setCancelled(true);
                        this.rgcmd.locs1.put(p.getName(), ev.getClickedBlock().getLocation());
                        p.sendMessage("First position set");
                        return true;
                    } else if (ev.getAction() == Action.RIGHT_CLICK_BLOCK)
                    {
                        ev.setCancelled(true);
                        this.rgcmd.locs2.put(p.getName(), ev.getClickedBlock().getLocation());
                        p.sendMessage("Second position set");
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @EventHandler
    public boolean onCraftItem(CraftItemEvent ev)
    {
        Player p = (Player) ev.getWhoClicked();
        ItemStack item = ev.getCurrentItem();
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemStack leggins = new ItemStack(Material.IRON_LEGGINGS);
        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        if (item.equals(helmet) || item.equals(chestplate) || item.equals(leggins) || item.equals(boots))
        {
            ev.setCancelled(true);
        }
        return true;
    }

}
