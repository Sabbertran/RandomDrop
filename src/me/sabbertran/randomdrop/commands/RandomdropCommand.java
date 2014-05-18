package me.sabbertran.randomdrop.commands;

import java.io.File;
import java.util.ArrayList;
import me.sabbertran.randomdrop.RandomDrop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RandomdropCommand implements CommandExecutor {

    RandomDrop main;
    File drops;

    public RandomdropCommand(RandomDrop rd, File d) {
        this.main = rd;
        this.drops = d;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {

        if (args.length == 0) {
            if (sender.hasPermission("randomdrop.randomdrop") || sender.isOp()) {
                if (main.getPluginEnabled()) {
                    sender.sendMessage("RandomDrop is Enabled");
                }
                else {
                    sender.sendMessage("RandomDrop is disabled");
                }
                sender.sendMessage("§bUse §7/randomdrop on/off §bto toggle");
                sender.sendMessage("§bUse §7/randomdrop chance 'chance' §bto change the drop chance");
                sender.sendMessage("§bUse §7/randomdrop world 'worldname' §bto add a world to the enabled worlds");
                sender.sendMessage("§bUse §7/randomdrop remove 'worldname' §bto remove a world from the enabled worlds");
                sender.sendMessage("§bUse §7/randomdrop listworlds §bto list all enabled worlds");
                sender.sendMessage("§bUse §7/randomdrop tool §bto get the RandomDrop tool");
                sender.sendMessage("§bUse §7/randomdrop reload §bto reload the plugin");
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    for (String world : this.main.worlds) {
                        if (p.getWorld().getName().equals(world)) {
                            p.sendMessage("§bDrops are enabled in your world");
                            return true;
                        }
                    }
                    p.sendMessage("§bDrops are disabled in your world");
                }
                else {
                    sender.sendMessage("You have to be a player to see if drops are enabled in your world");
                }


                return true;
            }
            else {
                sender.sendMessage("§cYou don't have permission to use this command");
            }
        }
        else {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("on")) {
                    if (sender.hasPermission("randomdrop.on") || sender.isOp()) {
                        if (!main.getPluginEnabled()) {
                            main.setPluginEnabled(true);
                            sender.sendMessage("§bRandomDrop Enabled");
                            return true;
                        }
                        else {
                            sender.sendMessage("§bRandomDrop is already Enabled.");
                        }
                    }
                    else {
                        sender.sendMessage("§cYou don't have permission to use this command");
                    }
                }
                else {
                    if (args[0].equalsIgnoreCase("off")) {
                        if (sender.hasPermission("randomdrop.off") || sender.isOp()) {
                            if (main.getPluginEnabled()) {
                                main.setPluginEnabled(false);
                                sender.sendMessage("§bRandomDrop disabled");
                                return true;
                            }
                            else {
                                sender.sendMessage("§bRandomDrop is already disabled.");
                            }
                        }
                        else {
                            sender.sendMessage("§cYou don't have permission to use this command");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("reload")) {
                        if (sender.hasPermission("randomdrop.reload") || sender.isOp()) {
                            main.reload();
                            sender.sendMessage("§bSucessfully reloaded");
                        }
                        else {
                            sender.sendMessage("§cYou don't have permission to use this command");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("chance")) {
                        if (sender.hasPermission("randomdrop.chance") || sender.isOp()) {
                            sender.sendMessage("§bCurrent Chance: " + main.getDropChance());
                            sender.sendMessage("§bUse §7/randomdrop chance 'chance' §bto change the drop chance");
                        }
                        else {
                            sender.sendMessage("§cYou don't have permission to use this command");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("listworlds")) {
                        if (sender.hasPermission("randomdrop.listworlds") || sender.isOp()) {
                            sender.sendMessage("§bRandomDrop is currently enabled in §8" + this.main.worlds.size() + " §bworlds:");
                            for (String w : this.main.worlds) {
                                sender.sendMessage("§7" + w);
                            }
                        }
                        else {
                            sender.sendMessage("§cYou don't have permission to use this command");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("tool")) {
                        if (sender.hasPermission("randomdrop.tool") || sender.isOp()) {
                            if (sender instanceof Player) {
                                Player p = (Player) sender;

                                ItemStack is = this.main.getTool();
                                ItemMeta im = is.getItemMeta();

                                ArrayList<String> lore = new ArrayList<String>();
                                lore.add("§aSelect the corners for RandomDrop regions with this");

                                im.setLore(lore);
                                im.setDisplayName("§bRandomDrop Tool");
                                is.setItemMeta(im);

                                p.getInventory().addItem(is);

                                p.sendMessage("§bUse this tool to select the corners for RandomDrop regions");
                            }
                            else {
                                sender.sendMessage("You have to be a player to use this command");
                            }
                        }
                        else {
                            sender.sendMessage("§cYou don't have permission to use this command");
                        }
                    }
                }
            }
            else if (args.length == 2) {
                if (args[0].equalsIgnoreCase("chance")) {
                    if (sender.hasPermission("randomdrop.chance") || sender.isOp()) {
                        int ch = Integer.parseInt(args[1]);
                        main.setDropChance(ch);
                        sender.sendMessage("§bDrop chance set to: §8" + ch);
                    }
                    else {
                        sender.sendMessage("§cYou don't have permission to use this command");
                    }
                }
                else if (args[0].equalsIgnoreCase("addworld")) {
                    if (sender.hasPermission("randomdrop.addworld") || sender.isOp()) {
                        main.addWorld(args[1]);
                        sender.sendMessage("§bSuccessfully added §8" + args[1] + "§b to the enabled worlds.");
                        return true;
                    }
                    else {
                        sender.sendMessage("§cYou don't have permission to use this command");
                    }
                }
                else if (args[0].equalsIgnoreCase("removeworld")) {
                    if (sender.hasPermission("randomdrop.removeworld") || sender.isOp()) {
                        if (this.main.worldIsEnabled(args[1])) {
                            this.main.removeWorld(args[1]);
                            sender.sendMessage("§bSuccessfully removed §8" + args[1] + "§b from the enabled worlds.");
                            return true;
                        }
                        sender.sendMessage("§bRandomDrop is not enabled in §8" + args[1]);
                        return true;
                    }
                    else {
                        sender.sendMessage("§cYou don't have permission to use this command");
                    }
                }
            }
        }
        return true;

    }
}