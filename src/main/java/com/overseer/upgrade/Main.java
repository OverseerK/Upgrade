package com.overseer.upgrade;

import com.overseer.upgrade.datas.ConvertItem_KO_KR;
import com.overseer.upgrade.enchantments.Glow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    static Plugin Main;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Main = this;
        System.out.println("[Upgrade] 활성화됨.");
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new Combat(), this);
        Bukkit.getPluginManager().registerEvents(new Upgrade(), this);
        registerGlow();
        initItemConvert();
        ItemStack EBook = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta Meta = EBook.getItemMeta();
        NamespacedKey key = new NamespacedKey(this, getDescription().getName());
        Glow glow = new Glow(key);
        Meta.addEnchant(glow, 1, true);
        Meta.setDisplayName("§3마법 부여의 책");
        EBook.setItemMeta(Meta);
        ShapedRecipe EBookRecipe = new ShapedRecipe(new NamespacedKey(this, "book_of_enchant"), EBook);
        EBookRecipe.shape("LLL", "LBL", "LLL");
        EBookRecipe.setIngredient('L', Material.LAPIS_LAZULI);
        EBookRecipe.setIngredient('B', Material.BOOK);
        Bukkit.addRecipe(EBookRecipe);
        ItemStack UBook = new ItemStack(Material.KNOWLEDGE_BOOK);
        Meta = UBook.getItemMeta();
        Meta.addEnchant(glow, 1, true);
        Meta.setDisplayName("§d강화의 책");
        UBook.setItemMeta(Meta);
        ShapedRecipe UBookRecipe = new ShapedRecipe(new NamespacedKey(this, "book_of_upgrade"), UBook);
        UBookRecipe.shape("GAG", "ABA", "GAG");
        UBookRecipe.setIngredient('G', Material.GHAST_TEAR);
        UBookRecipe.setIngredient('A', Material.AMETHYST_SHARD);
        UBookRecipe.setIngredient('B', Material.BOOK);
        Bukkit.addRecipe(UBookRecipe);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("[Upgrade] 비활성화됨.");
    }

    public void registerGlow() {
        try {
            Field f = Enchantment.class.getDeclaredField("acceptingNew");
            f.setAccessible(true);
            f.set(null, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NamespacedKey key = new NamespacedKey(this, getDescription().getName());
            Glow glow = new Glow(key);
            Enchantment.registerEnchantment(glow);
        } catch (IllegalArgumentException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Material, String> itemConvert_ko_kr = new HashMap<>();

    public static String convertItemName(Material data) {
        if (itemConvert_ko_kr.get(data) != null) {
            return itemConvert_ko_kr.get(data);
        } else {
            System.err.println("데이터 에러 : " + data.name());
        }
        return null;
    }

    public void initItemConvert() {
        for (Material m : Material.values()) {
            itemConvert_ko_kr.put(m, ConvertItem_KO_KR.getInstance().getObject(m.name().toLowerCase()));
        }
    }
}