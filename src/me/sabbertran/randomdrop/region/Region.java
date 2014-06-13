package me.sabbertran.randomdrop.region;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import me.sabbertran.randomdrop.RandomDrop;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Region implements ConfigurationSerializable
{

    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private World world;
    private int priority;
    private RandomDrop main;
    private ArrayList<ItemStack> Chance1 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance2 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance3 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance4 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance5 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance6 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance7 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance8 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance9 = new ArrayList<ItemStack>();
    private ArrayList<ItemStack> Chance10 = new ArrayList<ItemStack>();

    public Region(RandomDrop rd, Location loc1, Location loc2, int priority)
    {
        this.main = rd;

        this.minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        this.minY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        this.minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        this.maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        this.maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        this.maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        this.world = loc1.getWorld();

        this.priority = priority;
    }

    public void drop(Player p)
    {
        Random r = new Random();
        if (r.nextInt(main.getDropChance()) == 0)
        {
            double x = p.getLocation().getBlockX() + r.nextInt(20) - 10;
            double y = p.getLocation().getBlockY() + 2;
            double z = p.getLocation().getBlockZ() + r.nextInt(20) - 10;

            Location loc = new Location(p.getWorld(), x, y, z);

            int chance = r.nextInt(54);
            if (chance >= 0 && chance <= 9)
            {
                //Chance 1
                if (!this.Chance1.isEmpty())
                {
                    int item = r.nextInt(this.Chance1.size());
                    p.getWorld().dropItem(loc, this.Chance1.get(item));
                }
            } else if (chance >= 10 && chance <= 18)
            {
                //Chance 2
                if (!this.Chance2.isEmpty())
                {
                    int item = r.nextInt(this.Chance2.size());
                    p.getWorld().dropItem(loc, this.Chance2.get(item));
                }
            } else if (chance >= 19 && chance <= 26)
            {
                //Chance 3
                if (!this.Chance3.isEmpty())
                {
                    int item = r.nextInt(this.Chance3.size());
                    p.getWorld().dropItem(loc, this.Chance3.get(item));
                }
            } else if (chance >= 27 && chance <= 32)
            {
                //Chance 4
                if (!this.Chance4.isEmpty())
                {
                    int item = r.nextInt(this.Chance4.size());
                    p.getWorld().dropItem(loc, this.Chance4.get(item));
                }
            } else if (chance >= 33 && chance <= 38)
            {
                //Chance 5
                if (!this.Chance5.isEmpty())
                {
                    int item = r.nextInt(this.Chance5.size());
                    p.getWorld().dropItem(loc, this.Chance5.get(item));
                }
            } else if (chance >= 39 && chance <= 43)
            {
                //Chance 6
                if (!this.Chance6.isEmpty())
                {
                    int item = r.nextInt(this.Chance6.size());
                    p.getWorld().dropItem(loc, this.Chance6.get(item));
                }
            } else if (chance >= 44 && chance <= 47)
            {
                //Chance 7
                if (!this.Chance7.isEmpty())
                {
                    int item = r.nextInt(this.Chance7.size());
                    p.getWorld().dropItem(loc, this.Chance7.get(item));
                }
            } else if (chance >= 48 && chance <= 50)
            {
                //Chance 8
                if (!this.Chance8.isEmpty())
                {
                    int item = r.nextInt(this.Chance8.size());
                    p.getWorld().dropItem(loc, this.Chance8.get(item));
                }
            } else if (chance >= 51 && chance <= 52)
            {
                //Chance 9
                if (!this.Chance9.isEmpty())
                {
                    int item = r.nextInt(this.Chance9.size());
                    p.getWorld().dropItem(loc, this.Chance9.get(item));
                }
            } else if (chance == 53)
            {
                //Chance 10
                if (!this.Chance10.isEmpty())
                {
                    int item = r.nextInt(this.Chance10.size());
                    p.getWorld().dropItem(loc, this.Chance10.get(item));
                }
            }
        }
    }

    public boolean inRegion(Location loc)
    {
        if (loc.getWorld() != this.world)
        {
            return false;
        }
        if ((loc.getBlockX() >= this.minX) && (loc.getBlockX() <= this.maxX) && (loc.getBlockY() >= this.minY) && (loc.getBlockY() <= this.maxY) && (loc.getBlockZ() >= this.minZ) && (loc.getBlockZ() <= this.maxZ))
        {
            return true;
        }
        return false;
    }

    public void put(int chance, ItemStack is)
    {
        if (chance == 1)
        {
            Chance1.add(is);
        } else if (chance == 2)
        {
            Chance2.add(is);
        } else if (chance == 3)
        {
            Chance3.add(is);
        } else if (chance == 4)
        {
            Chance4.add(is);
        } else if (chance == 5)
        {
            Chance5.add(is);
        } else if (chance == 6)
        {
            Chance6.add(is);
        } else if (chance == 7)
        {
            Chance7.add(is);
        } else if (chance == 8)
        {
            Chance8.add(is);
        } else if (chance == 9)
        {
            Chance9.add(is);
        } else if (chance == 10)
        {
            Chance10.add(is);
        }
    }

    @Override
    public Map<String, Object> serialize()
    {
        Map<String, Object> o = new HashMap<String, Object>();
        o.put("minX", this.minX);
        o.put("minY", this.minY);
        o.put("minZ", this.minZ);

        o.put("maxX", this.maxX);
        o.put("maxY", this.maxY);
        o.put("maxZ", this.maxZ);

        o.put("world", this.world.getName());

        o.put("priority", this.priority);

        return o;
    }

    public ArrayList getChance1()
    {
        return this.Chance1;
    }

    public ArrayList getChance2()
    {
        return this.Chance2;
    }

    public ArrayList getChance3()
    {
        return this.Chance3;
    }

    public ArrayList getChance4()
    {
        return this.Chance4;
    }

    public ArrayList getChance5()
    {
        return this.Chance5;
    }

    public ArrayList getChance6()
    {
        return this.Chance6;
    }

    public ArrayList getChance7()
    {
        return this.Chance7;
    }

    public ArrayList getChance8()
    {
        return this.Chance8;
    }

    public ArrayList getChance9()
    {
        return this.Chance9;
    }

    public ArrayList getChance10()
    {
        return this.Chance10;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setPriority(int p)
    {
        this.priority = p;
    }
}
