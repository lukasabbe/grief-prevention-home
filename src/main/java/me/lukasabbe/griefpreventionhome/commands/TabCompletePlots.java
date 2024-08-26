package me.lukasabbe.griefpreventionhome.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class TabCompletePlots implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return commandSender.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
