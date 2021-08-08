package com.overseer.upgrade;

import com.overseer.upgrade.enchantments.Glow;

import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

import static com.overseer.upgrade.Main.convertItemName;

public class Upgrade implements Listener {

    Random Rand = new Random();

    @EventHandler
    public void enchantGuiOpen(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && b != null && b.getType() == Material.ENCHANTING_TABLE) {
            e.setCancelled(true);
            Inventory EnchantGui = Bukkit.createInventory(null, 45, "마법 부여");
            ItemStack EnchantKey = new ItemStack(Material.ANVIL);
            ItemMeta Meta = EnchantKey.getItemMeta();
            Meta.setDisplayName("§b아이템 마법 부여");
            EnchantKey.setItemMeta(Meta);
            for (int index = 0; index <= 44; index++) {
                if (index == 31) {
                    EnchantGui.setItem(index, EnchantKey);
                } else if (index != 12 && index != 14) {
                    EnchantGui.setItem(index, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                }
            }
            p.openInventory(EnchantGui);
        }
    }

    @EventHandler
    public void enchantItem(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();
        Inventory EScreen = e.getClickedInventory();
        if (e.getView().getTitle().equals("마법 부여")) {
            if (i != null && i.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                e.setCancelled(true);
            } else {
                if (i != null && i.getType() == Material.ANVIL) {
                    e.setCancelled(true);
                    assert EScreen != null;
                    ItemStack EItem = EScreen.getItem(12);
                    ItemStack EBook = EScreen.getItem(14);
                    if (EItem == null || !EItem.getType().name().contains("SWORD")) {
                        p.sendMessage("§c왼쪽 칸에 마법 부여를 할 올바른 아이템이 있어야 합니다.");
                    } else if (EBook == null || EBook.getType() != Material.ENCHANTED_BOOK || !ChatColor.stripColor(EBook.getItemMeta().getDisplayName()).equals("마법 부여의 책")) {
                        p.sendMessage("§c마법 부여를 하려면 오른쪽 칸에 마법 부여의 책이 필요합니다.");
                    } else {
                        EScreen.remove(EBook);
                        ItemMeta Meta = EItem.getItemMeta();
                        ArrayList<String> Lore = new ArrayList<>();
                        Lore.add("§3" + createModifier());
                        Lore.add("§3" + createModifier());
                        Lore.add("§3" + createModifier());
                        NamespacedKey key = new NamespacedKey(Main.Main, Main.Main.getDescription().getName());
                        Glow glow = new Glow(key);
                        Meta.addEnchant(glow, 1, true);
                        Meta.setLore(Lore);
                        EItem.setItemMeta(Meta);
                        p.sendMessage("§b마법 부여 성공!");
                        p.playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                        EScreen.clear(14);
                    }
                }
            }
        }
    }

    @EventHandler
    public void enchantClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("마법 부여")) {
            Player p = (Player) e.getPlayer();
            Inventory EScreen = e.getInventory();
            ItemStack EItem = EScreen.getItem(12);
            ItemStack EBook = EScreen.getItem(14);
            if (EItem != null) {
                p.getInventory().addItem(EItem);
                EScreen.clear(12);
            }
            if (EBook != null) {
                p.getInventory().addItem(EBook);
                EScreen.clear(14);
            }
        }
    }

    public String createModifier() {
        String Modifier;
        int ModifierSelector = Rand.nextInt(3);
        int MinLevel;
        int MaxLevel;
        switch (ModifierSelector) {
            case 0:
                Modifier = "공격 피해";
                MinLevel = 2;
                MaxLevel = 10;
                break;
            case 1:
                Modifier = "치명타 확률";
                MinLevel = 2;
                MaxLevel = 10;
                break;
            case 2:
                Modifier = "치명타 피해량";
                MinLevel = 4;
                MaxLevel = 20;
                break;
            default:
                Modifier = "null";
                MinLevel = 0;
                MaxLevel = 100;
        }
        int FinalLevel = Rand.nextInt(MaxLevel - MinLevel) + MinLevel;
        return Modifier + " +" + FinalLevel + "%";
    }

    @EventHandler
    public void upgradeGuiOpen(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK && b != null && b.getType() == Material.ANVIL) {
            e.setCancelled(true);
            Inventory UpgradeGui = Bukkit.createInventory(null, 45, "무기 강화");
            ItemStack UpgradeInformation = new ItemStack(Material.MOJANG_BANNER_PATTERN);
            ItemMeta Meta = UpgradeInformation.getItemMeta();
            Meta.setDisplayName("§d무기 강화 정보 확인 (필수!)");
            Meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            UpgradeInformation.setItemMeta(Meta);
            ItemStack UpgradeKey = new ItemStack(Material.ANVIL);
            Meta = UpgradeKey.getItemMeta();
            Meta.setDisplayName("§b무기 강화");
            UpgradeKey.setItemMeta(Meta);
            for (int index = 0; index <= 44; index++) {
                if (index == 30) {
                    UpgradeGui.setItem(index, UpgradeInformation);
                } else if (index == 32) {
                    UpgradeGui.setItem(index, UpgradeKey);
                } else if (index != 12 && index != 14) {
                    UpgradeGui.setItem(index, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                }
            }
            p.openInventory(UpgradeGui);
        }
    }

    @EventHandler
    public void upgradeItem(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack i = e.getCurrentItem();
        Inventory UScreen = e.getClickedInventory();
        assert UScreen != null;
        if (e.getView().getTitle().equals("무기 강화")) {
            if (i != null && i.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                e.setCancelled(true);
            } else if (i != null && i.getType() == Material.ANVIL) {
                e.setCancelled(true);
                ItemStack UItem = UScreen.getItem(12);
                ItemStack UBook = UScreen.getItem(14);
                if (UItem == null || !UItem.getType().name().contains("SWORD")) {
                    p.sendMessage("§c왼쪽 칸에 강화를 할 올바른 아이템이 있어야 합니다.");
                } else if (UItem.getItemMeta().getLore() == null) {
                    p.sendMessage("§c강화를 하려면 먼저 마법 부여를 완료해야 합니다.");
                } else {
                    if (UBook == null || UBook.getType() != Material.KNOWLEDGE_BOOK || !ChatColor.stripColor(UBook.getItemMeta().getDisplayName()).equals("강화의 책")) {
                        p.sendMessage("§c강화를 하려면 오른쪽 칸에 강화의 책이 필요합니다.");
                    } else {
                        ItemMeta UItemMeta = UItem.getItemMeta();
                        ArrayList<String> Lore = (ArrayList<String>) UItemMeta.getLore();
                        int ULevel = StringUtils.countMatches(UItemMeta.getLore().get(0), "★");
                        int RandInt = Rand.nextInt(100);
                        switch (ULevel) {
                            case 0:
                                Lore.add(0, "§e★☆☆☆☆☆☆☆☆☆");
                                p.sendMessage("§b무기 강화 성공! (0 -> 1강)");
                                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                break;
                            case 1:
                                if (RandInt >= 5) {
                                    Lore.set(0, "§e★★☆☆☆☆☆☆☆☆");
                                    p.sendMessage("§b무기 강화 성공! (1 -> 2강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else {
                                    p.sendMessage("§f무기 강화 실패... (1강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 2:
                                if (RandInt >= 10) {
                                    Lore.set(0, "§e★★★☆☆☆☆☆☆☆");
                                    p.sendMessage("§b무기 강화 성공! (2 -> 3강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else {
                                    p.sendMessage("§f무기 강화 실패... (2강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 3:
                                if (RandInt >= 20) {
                                    Lore.set(0, "§e★★★★☆☆☆☆☆☆");
                                    p.sendMessage("§b무기 강화 성공! (3 -> 4강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else if (RandInt >= 5) {
                                    p.sendMessage("§f무기 강화 실패... (3강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    Lore.set(0, "§e★★☆☆☆☆☆☆☆☆");
                                    p.sendMessage("§e무기 강화 실패! (3 -> 2강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 4:
                                if (RandInt >= 30) {
                                    Lore.set(0, "§e★★★★★☆☆☆☆☆");
                                    p.sendMessage("§b무기 강화 성공! (4 -> 5강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else if (RandInt >= 10) {
                                    p.sendMessage("§f무기 강화 실패... (4강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    Lore.set(0, "§e★★★☆☆☆☆☆☆☆");
                                    p.sendMessage("§e무기 강화 실패! (4 -> 3강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 5:
                                if (RandInt >= 40) {
                                    Lore.set(0, "§e★★★★★★☆☆☆☆");
                                    p.sendMessage("§b무기 강화 성공! (5 -> 6강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else if (RandInt >= 10) {
                                    p.sendMessage("§f무기 강화 실패... (5강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    Lore.set(0, "§e★★★★☆☆☆☆☆☆");
                                    p.sendMessage("§e무기 강화 실패! (5 -> 4강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 6:
                                if (RandInt >= 50) {
                                    Lore.set(0, "§e★★★★★★★☆☆☆");
                                    p.sendMessage("§b무기 강화 성공! (6 -> 7강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else if (RandInt >= 20) {
                                    p.sendMessage("§f무기 강화 실패... (6강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else if (RandInt >= 5) {
                                    Lore.set(0, "§e★★★★★☆☆☆☆☆");
                                    p.sendMessage("§e무기 강화 실패! (6 -> 5강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    UScreen.clear(12);
                                    p.sendMessage("§c무기 강화에 실패해 아이템이 파괴되었습니다!");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 7:
                                if (RandInt >= 60) {
                                    Lore.set(0, "§e★★★★★★★★☆☆");
                                    p.sendMessage("§b무기 강화 성공! (7 -> 8강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_USE, 1, 1);
                                } else if (RandInt >= 35) {
                                    p.sendMessage("§f무기 강화 실패... (7강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else if (RandInt >= 15) {
                                    Lore.set(0, "§e★★★★★★☆☆☆☆");
                                    p.sendMessage("§e무기 강화 실패! (7 -> 6강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    UScreen.clear(12);
                                    p.sendMessage("§c무기 강화에 실패해 아이템이 파괴되었습니다!");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                }
                                break;
                            case 8:
                                if (RandInt >= 60) {
                                    Lore.set(0, "§e★★★★★★★★★☆");
                                    p.sendMessage("§b무기 강화 성공! (8 -> 9강)");
                                    Bukkit.broadcastMessage("§d" + p.getName() + "님이 9강 무기 강화에 성공하셨습니다!");
                                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                                } else if (RandInt >= 40) {
                                    p.sendMessage("§f무기 강화 실패... (8강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else if (RandInt >= 20) {
                                    Lore.set(0, "§e★★★★★★★☆☆☆");
                                    p.sendMessage("§e무기 강화 실패! (8 -> 7강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    UScreen.clear(12);
                                    p.sendMessage("§c무기 강화에 실패해 아이템이 파괴되었습니다!");
                                    Bukkit.broadcastMessage("§d" + p.getName() + "님의 8강 검이 파괴되었습니다! X를 눌러 조의를 표해주세요.");
                                    p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                                }
                                break;
                            case 9:
                                if (RandInt >= 60) {
                                    Lore.set(0, "§e★★★★★★★★★★");
                                    p.sendMessage("§b무기 강화 성공! (9 -> MAX)");
                                    if (UItem.getType() == Material.NETHERITE_SWORD) {
                                        UItem.setType(Material.NETHERITE_SWORD);
                                        p.sendMessage("§b무기의 재질 등급이 한 단계 상승하였습니다!");
                                    } else if (UItem.getType() == Material.DIAMOND_SWORD) {
                                        UItem.setType(Material.NETHERITE_SWORD);
                                        p.sendMessage("§b무기의 재질 등급이 한 단계 상승하였습니다!");
                                    } else if (UItem.getType() == Material.IRON_SWORD) {
                                        UItem.setType(Material.DIAMOND_SWORD);
                                        p.sendMessage("§b무기의 재질 등급이 한 단계 상승하였습니다!");
                                    }
                                    Bukkit.broadcastMessage("§d" + p.getName() + "님이 10강 무기 강화에 성공하셨습니다!");
                                    p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1, 1);
                                } else if (RandInt >= 50) {
                                    p.sendMessage("§f무기 강화 실패... (9강 유지)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else if (RandInt >= 40) {
                                    Lore.set(0, "§e★★★★★★★★☆☆");
                                    p.sendMessage("§e무기 강화 실패! (9 -> 8강)");
                                    p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1, 1);
                                } else {
                                    UScreen.clear(12);
                                    p.sendMessage("§c무기 강화에 실패해 아이템이 파괴되었습니다!");
                                    Bukkit.broadcastMessage("§d" + p.getName() + "님의 9강 검이 파괴되었습니다! X를 눌러 조의를 표해주세요.");
                                    p.playSound(p.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                                }
                                break;
                        }
                        UItemMeta.setLore(Lore);
                        UItem.setItemMeta(UItemMeta);
                        UScreen.clear(14);
                    }
                }
            } else if (i != null && i.getType() == Material.MOJANG_BANNER_PATTERN) {
                e.setCancelled(true);
                ItemStack UItem = UScreen.getItem(12);
                ItemStack UInformation = UScreen.getItem(30);
                assert UInformation != null;
                ItemMeta Meta = UInformation.getItemMeta();
                ArrayList<String> Lore = new ArrayList<>();
                if (UItem == null || !UItem.getType().name().contains("SWORD")) {
                    Lore.add("§c올바른 아이템이 아닙니다.");
                } else if (UItem.getItemMeta().getLore() == null) {
                    Lore.add("§c강화를 하려면 먼저 마법 부여를 완료해야 합니다.");
                } else {
                    ItemMeta UItemMeta = UItem.getItemMeta();
                    int ULevel = StringUtils.countMatches(UItemMeta.getLore().get(0), "★");
                    switch (ULevel) {
                        case 0:
                            Lore.add("§f+0 -> +1 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 100%");
                            Lore.add("§d강화 성공 시 공격력 +1% 증가, 총 +1%");
                            break;
                        case 1:
                            Lore.add("§f+1 -> +2 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 95%");
                            Lore.add("§f유지 확률: 5%");
                            Lore.add("§d강화 성공 시 공격력 +2% 증가, 총 +3%");
                            break;
                        case 2:
                            Lore.add("§f+2 -> +3 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 90%");
                            Lore.add("§f유지 확률: 10%");
                            Lore.add("§d강화 성공 시 공격력 +3% 증가, 총 +6%");
                            break;
                        case 3:
                            Lore.add("§f+3 -> +4 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 80%");
                            Lore.add("§f유지 확률: 15%");
                            Lore.add("§e하락 확률: 5%");
                            Lore.add("§d강화 성공 시 공격력 +4% 증가, 총 +10%");
                            break;
                        case 4:
                            Lore.add("§f+4 -> +5 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 70%");
                            Lore.add("§f유지 확률: 20%");
                            Lore.add("§e하락 확률: 10%");
                            Lore.add("§d강화 성공 시 공격력 +5% 증가, 총 +15%");
                            break;
                        case 5:
                            Lore.add("§f+5 -> +6 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 60%");
                            Lore.add("§f유지 확률: 30%");
                            Lore.add("§e하락 확률: 10%");
                            Lore.add("§d강화 성공 시 공격력 +6% 증가, 총 +21%");
                            break;
                        case 6:
                            Lore.add("§f+6 -> +7 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 50%");
                            Lore.add("§f유지 확률: 30%");
                            Lore.add("§e하락 확률: 15%");
                            Lore.add("§c파괴 확률: 5%");
                            Lore.add("§d강화 성공 시 공격력 +7% 증가, 총 +28%");
                            break;
                        case 7:
                            Lore.add("§f+7 -> +8 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 40%");
                            Lore.add("§f유지 확률: 25%");
                            Lore.add("§e하락 확률: 20%");
                            Lore.add("§c파괴 확률: 15%");
                            Lore.add("§d강화 성공 시 공격력 +8% 증가, 총 +36%");
                            break;
                        case 8:
                            Lore.add("§f+8 -> +9 " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 40%");
                            Lore.add("§f유지 확률: 20%");
                            Lore.add("§e하락 확률: 20%");
                            Lore.add("§c파괴 확률: 20%");
                            Lore.add("§d강화 성공 시 공격력 +9% 증가, 총 +45%");
                            break;
                        case 9:
                            Lore.add("§f+9 -> +MAX " + convertItemName(UItem.getType()) + " 강화 중");
                            Lore.add("§b성공 확률: 40%");
                            Lore.add("§f유지 확률: 10%");
                            Lore.add("§e하락 확률: 10%");
                            Lore.add("§c파괴 확률: 40%");
                            Lore.add("§d강화 성공 시 공격력 +10% 증가, 총 +55%");
                            Lore.add("§d추가로 검 등급 한 단계 상승");
                            break;
                        case 10:
                            Lore.add("§c이미 최대 레벨입니다.");
                            break;
                    }
                }
                Meta.setLore(Lore);
                UInformation.setItemMeta(Meta);
            }
        }
    }

    @EventHandler
    public void upgradeClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("무기 강화")) {
            Player p = (Player) e.getPlayer();
            Inventory UScreen = e.getInventory();
            ItemStack UItem = UScreen.getItem(12);
            ItemStack UBook = UScreen.getItem(14);
            if (UItem != null) {
                p.getInventory().addItem(UItem);
                UScreen.clear(12);
            }
            if (UBook != null) {
                p.getInventory().addItem(UBook);
                UScreen.clear(14);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity().getPlayer();
        assert p != null;
        for (ItemStack i : p.getInventory().getContents()) {
            if (i != null && i.getItemMeta().hasEnchant(Enchantment.VANISHING_CURSE)) {
                Bukkit.broadcastMessage("§d" + p.getName() + "의 " + convertItemName(i.getType()) + "이(가) 산산조각 났습니다!");
            }
        }
    }

}