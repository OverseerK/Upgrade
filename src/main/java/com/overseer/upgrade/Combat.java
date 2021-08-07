package com.overseer.upgrade;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

public class Combat implements Listener {

    Random Rand = new Random();

    @EventHandler
    public void enchantedHit(EntityDamageByEntityEvent e) {
        Entity Damager = e.getDamager();
        if (Damager instanceof Player) {
            Player p = (Player) Damager;
            int Modifier = 0;
            int CritChance = 20;
            int CritDamage = 0;
            double Damage = e.getDamage();
            double FinalDamage = 0;
            @Nullable ArrayList<String> Lore = (ArrayList<String>) p.getInventory().getItemInMainHand().getLore();
            if (Lore != null) {
                int Level = 0;
                if (Lore.get(0).contains("★")) {
                    Level = StringUtils.countMatches(Lore.get(0), "★");
                }
                for (String Line : Lore) {
                    if (Line.contains("공격 피해")) {
                        Modifier = Modifier + Integer.parseInt(ChatColor.stripColor(Line).replaceAll("\\D+",""));
                    }
                }
                for (String Line : Lore) {
                    if (Line.contains("치명타 확률")) {
                        CritChance = CritChance + Integer.parseInt(ChatColor.stripColor(Line).replaceAll("\\D+",""));
                    }
                }
                for (String Line : Lore) {
                    if (Line.contains("치명타 피해")) {
                        CritDamage = CritDamage + Integer.parseInt(ChatColor.stripColor(Line).replaceAll("\\D+",""));
                    }
                }
                Damage = Damage * (((Level + 1) * Level / 2) + 100) / 100 * (Modifier + 100) / 100;
                if (Rand.nextInt(100) < CritChance) {
                    FinalDamage = Damage * (CritDamage + 150) / 100;
                    p.sendMessage("§b치명타!");
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                } else {
                    FinalDamage = Damage;
                }
                e.setDamage(FinalDamage);
            }
            p.sendMessage(String.valueOf(FinalDamage));
        }
    }

    @EventHandler
    public void speedModifier(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItem(e.getNewSlot());
        int Modifier = 0;
        if (i != null) {
            @Nullable ArrayList<String> Lore = (ArrayList<String>) i.getLore();
            if (Lore != null) {
                for (String Line : Lore) {
                    if (Line.contains("이동 속도")) {
                        Modifier = Modifier + Integer.parseInt(ChatColor.stripColor(Line).replaceAll("\\D+",""));
                    }
                }
                float WalkSpeed = (float) (0.2 * (Modifier + 100) / 100);
                p.setWalkSpeed(WalkSpeed);
            } else {
                p.setWalkSpeed(0.2f);
            }
        }
    }
}
