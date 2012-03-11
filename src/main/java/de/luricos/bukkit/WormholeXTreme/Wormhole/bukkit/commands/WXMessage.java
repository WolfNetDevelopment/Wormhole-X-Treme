package de.luricos.bukkit.WormholeXTreme.Wormhole.bukkit.commands;

import de.luricos.bukkit.WormholeXTreme.Wormhole.config.ConfigManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.Stargate;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.StargateManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.WXPermissions;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.WXPermissions.PermissionType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WXMessage implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String[] a = CommandUtilities.commandEscaper(args);
		if (a.length >= 1) {
			if (StargateManager.isStargate(a[0])) {
				Stargate s = StargateManager.getStargate(a[0]);
				if ((!CommandUtilities.playerCheck(sender)) || (WXPermissions.checkWXPermissions((Player)sender, WXPermissions.PermissionType.CONFIG)) || ((s.getGateOwner() != null) && (s.getGateOwner().equals(((Player)sender).getName())))) {
					if (a.length >= 2) {
						if (a[1].equals("-clear")) {
							s.setGateMessage(ConfigManager.MessageStrings.playerUsedStargate.toString());
						} else {
							s.setGateMessage(a[1]);
						}
					}
					sender.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Welcome message for gate " + s.getGateName() + " is: " + s.getGateMessage());
				} else {
					sender.sendMessage(ConfigManager.MessageStrings.permissionNo.toString());
				}
			} else {
				sender.sendMessage(ConfigManager.MessageStrings.errorHeader.toString() + "Invalid Stargate: " + a[0]);
			}
			return true;
	    }
		return false;
	}
}