package PZChatGuard;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class PZChatGuard extends JavaPlugin implements Listener {

    private String punishmentMode;
    private String customCommand;
    private double economyCost;
    private Map<String, List<String>> wordRules;
    private Map<String, String> wordReplacements;
    private Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfigurations();
        setupEconomy();

        PluginCommand pzCommand = getCommand("pzchatguard");
        if (pzCommand != null) {
            pzCommand.setExecutor(this);
            pzCommand.setAliases(List.of("pzcg"));
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp.getProvider();
    }

    private void reloadConfigurations() {
        reloadConfig();
        this.punishmentMode = getConfig().getString("punishment_mode", "REPLACE");
        this.customCommand = getConfig().getString("custom_command", "kick %player% Inappropriate language.");
        this.economyCost = getConfig().getDouble("economy_cost", 100);

        reloadWordRules();
    }

    private void reloadWordRules() {
        saveResource("words.yml", false);
        FileConfiguration wordsConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "words.yml"));

        wordRules = new HashMap<>();
        wordReplacements = new HashMap<>();

        List<Map<?, ?>> words = wordsConfig.getMapList("words");
        for (Map<?, ?> wordConfig : words) {
            String word = (String) wordConfig.get("word");
            List<String> customRules = (List<String>) wordConfig.get("customrules");
            String replacement = (String) wordConfig.get("replacement");

            if (word != null && customRules != null) {
                wordRules.put(word, customRules);
                wordReplacements.put(word, replacement);
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        for (Map.Entry<String, List<String>> entry : wordRules.entrySet()) {
            String word = entry.getKey();
            List<String> customRules = entry.getValue();

            for (String rule : customRules) {
                if (matchesCustomRule(message, rule)) {
                    handlePunishment(player, word, message);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private boolean matchesCustomRule(String message, String rule) {
        String regex = rule.replace("*", ".*").replace("?", ".?");
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).find();
    }

    private void handlePunishment(Player player, String word, String message) {
        switch (punishmentMode.toUpperCase()) {
            case "COMMAND":
                String command = customCommand.replace("%player%", player.getName());
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                break;
            case "ECONOMY":
                if (economy != null && economyCost > 0) {
                    economy.withdrawPlayer(player, economyCost);
                    player.sendMessage("§cYou have been fined $" + economyCost + " for inappropriate language.");
                }
                break;
            case "REPLACE":
            default:
                String replacement = wordReplacements.get(word);
                String newMessage = replaceWordsInMessage(message, word, replacement);
                player.sendMessage(newMessage);
                break;
        }
    }

    private String replaceWordsInMessage(String message, String word, String replacement) {
        for (String rule : wordRules.get(word)) {
            String regex = rule.replace("*", ".*").replace("?", ".?");
            message = message.replaceAll(regex, replacement);
        }
        return message;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                sender.sendMessage("§7[PZ ChatGuard] §aDeveloped by NotJunar @ PZ Development");
                sender.sendMessage("§7Discord: dsc.gg/joinjnmc");
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reloadConfigurations();
            sender.sendMessage("§7[PZ ChatGuard] §aConfiguration reloaded successfully.");
            return true;
        }

        return false;
    }
}
