package me.sabbertran.randomdrop.commands;

import me.sabbertran.randomdrop.region.Region;
import java.util.HashMap;
import me.sabbertran.randomdrop.RandomDrop;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegionCommand implements CommandExecutor {

    public HashMap<String, Location> locs1 = new HashMap<String, Location>();
    public HashMap<String, Location> locs2 = new HashMap<String, Location>();
    RandomDrop main;

    public RegionCommand(RandomDrop rd) {
        this.main = rd;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Location loc = p.getLocation();

            if (args.length == 0) {
                if (p.hasPermission("randomdrop.region") || p.isOp()) {
                    p.sendMessage("§bUse §7/rdregion pos1/pos2 §bto set the corners for a region");
                    p.sendMessage("§bUse §7/rdregion create 'name' §bto create a region with the given name");
                    p.sendMessage("§bUse §7/rdregion remove 'name' §bto remove the region with the given name");
                    p.sendMessage("§bUse §7/rdregion list §bto list all regions");
                    p.sendMessage("§bUse §7/rdregion priority 'region' 'priority' §bto change the priority of a region");
                    p.sendMessage("§bUse §7/rdregion expand 'amount' 'u/d/n/e/s/w' §bto expand your selection in the given direction");
                    p.sendMessage("§bUse §7/rdregion expand vert §bto expand the selection from Y-Level 0 to 256");

                    String msg = "";
                    for (String region : this.main.regions.keySet()) {
                        Region rg = this.main.regions.get(region);
                        if (rg.inRegion(p.getLocation())) {
                            msg = msg + region + ", ";
                        }
                    }
                    p.sendMessage("§bYou are in regions: §7" + msg);
                } else {
                    p.sendMessage("§cYou don't have permission to use this command");
                }
                return true;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("pos1")) {
                    if (p.hasPermission("randomdrop.region.pos1") || p.isOp()) {
                        this.locs1.put(p.getName(), loc);
                        p.sendMessage("§bFirst position set");
                        return true;
                    } else {
                        p.sendMessage("§cYou don't have permission to use this command");
                    }
                } else if (args[0].equalsIgnoreCase("pos2")) {
                    if (p.hasPermission("randomdrop.region.pos2") || p.isOp()) {
                        this.locs2.put(p.getName(), loc);
                        p.sendMessage("§bSecond position set");
                        return true;
                    } else {
                        p.sendMessage("§cYou don't have permission to use this command");
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (p.hasPermission("randomdrop.region.list") || p.isOp()) {
                        p.sendMessage("§bCurrently there are §8" + this.main.regions.size() + "§b regions:");
                        String rgs = "";
                        for (String rg : this.main.regions.keySet()) {
                            rgs = rgs + rg + ", ";
                        }
                        String msg = rgs;
                        StringBuilder sb = new StringBuilder(rgs);
                        if (rgs.length() != 0) {
                            sb.deleteCharAt(rgs.length() - 1);
                            sb.deleteCharAt(rgs.length() - 2);
                            msg = sb.toString();
                        }
                        p.sendMessage("§7" + msg);
                    } else {
                        p.sendMessage("§cYou don't have permission to use this command");
                    }
                }
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (p.hasPermission("randomdrop.region.create") || p.isOp()) {
                    if (args.length == 2) {
                        Location loc1 = this.locs1.get(p.getName());
                        Location loc2 = this.locs2.get(p.getName());
                        if (loc1 == null || loc2 == null) {
                            p.sendMessage("§bYou have to set the locations first");
                            return true;
                        } else if (loc1.getWorld() != loc2.getWorld()) {
                            p.sendMessage("§bThe locations have to be in the same world");
                            return true;
                        } else {
                            String name = args[1].toLowerCase();

                            if (this.main.regions.containsKey(name)) {
                                p.sendMessage("§bRegion §8" + name + " §balready exists.");
                                return true;
                            }
                            this.main.regions.put(name, new Region(this.main, loc1, loc2, 0));
                            p.sendMessage("§bRegion §8" + name + " §bsuccessfully created");
                            return true;
                        }
                    } else {
                        p.sendMessage("§bUse §7/rdregion create 'name' §bto create a region");
                        return true;
                    }
                } else {
                    p.sendMessage("§cYou don't have permission to use this command");
                }
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (p.hasPermission("randomdrop.region.remove") || p.isOp()) {
                    if (args.length == 2) {
                        this.main.regions.remove(args[1]);
                        p.sendMessage("§bSuccessfully removed region §8" + args[1]);
                    }
                } else {
                    p.sendMessage("§cYou don't have permission to use this command");
                }
            }
            if (args[0].equalsIgnoreCase("priority")) {
                if (p.hasPermission("randomdrop.region.priority") || sender.isOp()) {
                    if (args.length == 3) {
                        if (this.main.regions.containsKey(args[1])) {
                            Region reg = this.main.regions.get(args[1]);
                            reg.setPriority(Integer.parseInt(args[2]));
                            p.sendMessage("§bPriority of region §8" + args[1].toLowerCase() + " §bset to §7 " + args[2]);
                            return true;
                        } else {
                            p.sendMessage("§bRegion §8" + args[1].toLowerCase() + " §bcould not be found");
                            return true;
                        }
                    } else {
                        p.sendMessage("§bUse §7/rdregion priority 'region' 'priority' §bto change the priority of a region");
                        return true;
                    }
                } else {
                    p.sendMessage("§cYou don't have permission to use this command");
                    return true;
                }
            }
            if (args[0].equalsIgnoreCase("expand")) {
                if (p.hasPermission("randomdrop.region.expand") || p.isOp()) {
                    if (args.length == 3) {
                        Location loc1 = locs1.get(p.getName());
                        Location loc2 = locs2.get(p.getName());
                        if (args[2].equalsIgnoreCase("u")) {
                            if (loc1.getBlockY() > loc2.getBlockY()) {
                                int y = loc1.getBlockY() + Integer.parseInt(args[1]);
                                loc1.setY(y);
                                locs1.put(p.getName(), loc1);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            } else {
                                int y = loc2.getBlockY() + Integer.parseInt(args[1]);
                                loc2.setY(y);
                                locs2.put(p.getName(), loc2);
                                p.sendMessage("§bSelection expanded §7" + args[2] + " §bblocks");
                                return true;
                            }
                        } else if (args[2].equalsIgnoreCase("d")) {
                            if (loc1.getBlockY() < loc2.getBlockY()) {
                                int y = loc1.getBlockY() - Integer.parseInt(args[1]);
                                loc1.setY(y);
                                locs1.put(p.getName(), loc1);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            } else {
                                int y = loc2.getBlockY() - Integer.parseInt(args[1]);
                                loc2.setY(y);
                                locs2.put(p.getName(), loc2);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            }
                        } else if (args[2].equalsIgnoreCase("n")) {
                            if (loc1.getBlockZ() < loc2.getBlockZ()) {
                                int z = loc1.getBlockZ() - Integer.parseInt(args[1]);
                                loc1.setZ(z);
                                locs1.put(p.getName(), loc1);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            } else {
                                int z = loc2.getBlockZ() - Integer.parseInt(args[1]);
                                loc2.setZ(z);
                                locs2.put(p.getName(), loc2);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            }
                        } else if (args[2].equalsIgnoreCase("e")) {
                            if (loc1.getBlockX() > loc2.getBlockX()) {
                                int x = loc1.getBlockX() + Integer.parseInt(args[1]);
                                loc1.setX(x);
                                locs1.put(p.getName(), loc1);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            } else {
                                int x = loc2.getBlockX() + Integer.parseInt(args[1]);
                                loc2.setX(x);
                                locs2.put(p.getName(), loc2);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            }
                        } else if (args[2].equalsIgnoreCase("s")) {
                            if (loc1.getBlockZ() > loc2.getBlockZ()) {
                                int z = loc1.getBlockZ() + Integer.parseInt(args[1]);
                                loc1.setZ(z);
                                locs1.put(p.getName(), loc1);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            } else {
                                int z = loc2.getBlockZ() + Integer.parseInt(args[1]);
                                loc2.setZ(z);
                                locs2.put(p.getName(), loc2);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            }
                        } else if (args[2].equalsIgnoreCase("w")) {
                            if (loc1.getBlockX() < loc2.getBlockX()) {
                                int x = loc1.getBlockX() - Integer.parseInt(args[1]);
                                loc1.setX(x);
                                locs1.put(p.getName(), loc1);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            } else {
                                int x = loc2.getBlockX() - Integer.parseInt(args[1]);
                                loc2.setX(x);
                                locs2.put(p.getName(), loc2);
                                p.sendMessage("§bSelection expanded §7" + args[1] + " §bblocks");
                                return true;
                            }
                        }
                    } else if (args.length == 2) {
                        Location loc1 = locs1.get(p.getName());
                        Location loc2 = locs2.get(p.getName());
                        if (args[1].equalsIgnoreCase("vert")) {
                            loc1.setY(0);
                            loc2.setY(256);
                            locs1.put(p.getName(), loc1);
                            locs2.put(p.getName(), loc2);
                            p.sendMessage("§bSelection expanded");
                            return true;
                        }
                    } else {
                        p.sendMessage("§bUse §7/rdregion expand 'amount' 'u/d/n/e/s/w' §bto expand your selection in the given direction");
                        p.sendMessage("§bUse §7/rdregion expand vert §bto expand the selection from Y-Level 0 to 256");
                        return true;
                    }
                } else {
                    p.sendMessage("§cYou don't have permission to use this command");
                    return true;
                }
            }

        } else {
            sender.sendMessage("§bYou have to be a player to perform this command");
            return true;
        }
        return true;
    }
}