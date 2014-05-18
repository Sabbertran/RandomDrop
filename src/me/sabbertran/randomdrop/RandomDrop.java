package me.sabbertran.randomdrop;

import com.shampaggon.crackshot.CSDirector;
import me.sabbertran.randomdrop.region.Region;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import me.sabbertran.randomdrop.commands.RandomdropCommand;
import me.sabbertran.randomdrop.commands.RegionCommand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RandomDrop extends JavaPlugin
{

    static final Logger log = Bukkit.getLogger();
    private File dropFile;
    private boolean pluginEnabled = false;
    public ArrayList<String> worlds = new ArrayList<String>();
    private int dropChance;
    private RegionCommand rgcmd;
    private ItemStack tool;
    public HashMap<String, Region> regions = new HashMap<String, Region>();
    private HashMap<String, String> messages = new HashMap<String, String>();
    private HashMap<String, Integer> minimizedDrops = new HashMap<String, Integer>();

    @Override
    public void onDisable()
    {
        this.getConfig().set("RandomDrop.pluginEnabled", this.pluginEnabled);
        this.getConfig().set("RandomDrop.dropChance", this.dropChance);
        this.getConfig().set("RandomDrop.worlds", this.worlds);
        this.getConfig().set("RandomDrop.tool", this.tool.toString());
        this.saveConfig();

        File regions = new File("plugins/RandomDrop", "regions.yml");
        FileConfiguration regionscfg = YamlConfiguration.loadConfiguration(regions);
        int size = 0;

        for (String region : this.regions.keySet())
        {
            regionscfg.set(region, this.regions.get(region).serialize());
            size++;
        }
        try
        {
            regionscfg.save(regions);
            log.info("[RandomDrop] Saved " + size + " regions");
        } catch (IOException ex)
        {
            System.err.println(ex.getMessage());
        }

        log.info("RandomDrop disabled");
    }

    @Override
    public void onEnable()
    {
        CSDirector CrackShotAPI = (CSDirector) Bukkit.getServer().getPluginManager().getPlugin("CrackShot");

        if (CrackShotAPI != null)
        {
            log.info("[RandomDrop] Found CrackShot. You can now drop weapons using this plugin");
        } else
        {
            log.info("[RandomDrop] Couldn't find CrackShot. Please install it to be able to drop weapons");
        }

        this.getConfig().addDefault("RandomDrop.pluginEnabled", pluginEnabled);
        this.getConfig().addDefault("RandomDrop.worlds", worlds);
        this.getConfig().addDefault("RandomDrop.dropChance", 100);
        this.getConfig().addDefault("RandomDrop.tool", new ItemStack(286));
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        this.pluginEnabled = this.getConfig().getBoolean("RandomDrop.pluginEnabled");
        this.dropChance = this.getConfig().getInt("RandomDrop.dropChance");
        this.worlds = (ArrayList<String>) this.getConfig().getStringList("RandomDrop.worlds");
        this.tool = this.getConfig().getItemStack("RandomDrop.tool");

        dropFile = new File(getDataFolder(), "drops.yml");
        if (!dropFile.exists())
        {
            dropFile.getParentFile().mkdirs();
            copy(getResource("drops.yml"), dropFile);
        }

        this.loadRegions();

        int drops = 0;
        try
        {
            FileInputStream fstream = new FileInputStream(dropFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String text;
            while ((text = br.readLine()) != null)
            {
                if (!text.startsWith("#"))
                {
                    if (!text.startsWith("CS"))
                    {
                        String[] drop = text.split(":");
                        for (String region : this.regions.keySet())
                        {
                            if (drop[0].equalsIgnoreCase(region))
                            {
                                Region rg = this.regions.get(region);
                                int chance = Integer.parseInt(drop[1]);
                                int id = Integer.parseInt(drop[2]);
                                int amount = Integer.parseInt(drop[3]);
                                ItemStack item;
                                if (drop.length == 5)
                                {
                                    short damage = (short) Integer.parseInt(drop[4]);
                                    item = new ItemStack(id, amount, damage);
                                } else
                                {
                                    item = new ItemStack(id, amount);
                                }
                                rg.put(chance, item);
                                drops++;
                            }
                        }
                    } else
                    {
                        if (CrackShotAPI != null)
                        {
                            String[] drop = text.split(":");
                            for (String region : this.regions.keySet())
                            {
                                if (drop[1].equalsIgnoreCase(region))
                                {
                                    Region rg = this.regions.get(region);
                                    int chance = Integer.parseInt(drop[2]);
                                    int amount = Integer.parseInt(drop[4]);
                                    ItemStack item = CrackShotAPI.generateWeapon(drop[3]);
                                    if (amount != 1)
                                    {
                                        item.setAmount(amount);
                                    }
                                    if (item != null)
                                    {
                                        rg.put(chance, item);
                                        drops++;
                                    } else
                                    {
                                        log.info("[RandomDrop] CrackShot Weapon '" + drop[3] + "' could not be found.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            in.close();
        } catch (Exception e)
        {
            System.err.println("Error: " + e.getMessage());
        }
        log.info("[RandomDrop] Loaded " + drops + " drops");
        try
        {
            URL excludeURL = new URL("http://www.sabbertran.x10.mx/randomdrop-exclude.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(excludeURL.openStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                if (!line.startsWith("#"))
                {
                    String[] linea = line.split(":");
                    String name = linea[0];
                    int amount = Integer.parseInt(linea[1]);
                    this.minimizedDrops.put(name, amount);
                }
            }
        } catch (Exception e)
        {
            System.err.println(e.getMessage());
        }

        this.getCommand("randomdrop").setExecutor(new RandomdropCommand(this, this.dropFile));
        this.rgcmd = new RegionCommand(this);
        this.getCommand("rdregion").setExecutor(rgcmd);

        this.getServer().getPluginManager().registerEvents(new Events(this, this.rgcmd), this);

        log.info("RandomDrop Enabled");
    }

    public void drop(Player p)
    {
        if (!minimizedDrops.containsKey(p.getName()))
        {
            this.dropItem(p);
        } else
        {
            Random r = new Random();
            int ran = r.nextInt(minimizedDrops.get(p.getName()));
            if (ran == 0)
            {
                this.dropItem(p);
            }

        }
    }

    private void dropItem(Player p)
    {
        ArrayList< Region> reg = new ArrayList<Region>();
        for (String region : this.regions.keySet())
        {
            Region rg = this.regions.get(region);
            if (rg.inRegion(p.getLocation()))
            {
                reg.add(rg);
            }
        }

        Region highest = null;
        for (Region r : reg)
        {
            if (highest == null)
            {
                highest = r;
            } else if (r.getPriority() > highest.getPriority())
            {
                highest = r;
            }
        }
        if (highest != null)
        {
            highest.drop(p);
        }
    }

    private void loadRegions()
    {
        File regions = new File("plugins/RandomDrop", "regions.yml");
        FileConfiguration regionscfg = YamlConfiguration.loadConfiguration(regions);
        int count = 0;

        for (String region : regionscfg.getConfigurationSection("").getKeys(false))
        {
            String world = regionscfg.getString(region + ".world");
            World w = Bukkit.getWorld(world);

            if (w != null)
            {
                int minX = regionscfg.getInt(region + ".minX");
                int minY = regionscfg.getInt(region + ".minY");
                int minZ = regionscfg.getInt(region + ".minZ");

                int maxX = regionscfg.getInt(region + ".maxX");
                int maxY = regionscfg.getInt(region + ".maxY");
                int maxZ = regionscfg.getInt(region + ".maxZ");

                int priority = regionscfg.getInt(region + ".priority");

                Region re = new Region(this, new Location(w, minX, minY, minZ), new Location(w, maxX, maxY, maxZ), priority);

                if (!this.regions.containsValue(re))
                {
                    this.regions.put(region.toLowerCase(), re);
                    count++;
                }
            }
        }
        log.info("[RandomDrop] Loaded " + count + " regions");
    }

    private void copy(InputStream in, File file)
    {
        try
        {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void reload()
    {
        this.getServer().getPluginManager().disablePlugin(this);
        this.getServer().getPluginManager().enablePlugin(this);
    }

    public boolean getPluginEnabled()
    {
        return pluginEnabled;
    }

    public void setPluginEnabled(boolean e)
    {
        this.pluginEnabled = e;
    }

    public int getDropChance()
    {
        return dropChance;
    }

    public void setDropChance(int c)
    {
        this.dropChance = c;
    }

    public ItemStack getTool()
    {
        return tool;
    }

    public void addWorld(String w)
    {
        this.worlds.add(w);
    }

    public void removeWorld(String w)
    {
        this.worlds.remove(w);
    }

    public boolean worldIsEnabled(String w)
    {
        for (String world : this.worlds)
        {
            if (w.equals(world))
            {
                return true;
            }
        }
        return false;
    }

    public HashMap<String, String> getMessages()
    {
        return messages;
    }
}
