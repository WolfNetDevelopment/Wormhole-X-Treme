/*
 * Wormhole X-Treme Plugin for Bukkit
 * Copyright (C) 2011 Lycano <https://github.com/lycano/Wormhole-X-Treme/>
 *
 * Copyright (C) 2011 Ben Echols
 *                    Dean Bailey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.luricos.bukkit.WormholeXTreme.Wormhole.listeners;

import de.luricos.bukkit.WormholeXTreme.Wormhole.config.ConfigManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.plugin.HelpSupport;
import de.luricos.bukkit.WormholeXTreme.Wormhole.plugin.PermissionsSupport;
import de.luricos.bukkit.WormholeXTreme.Wormhole.plugin.WormholeWorldsSupport;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * WormholeXTreme Server Listener.
 *
 * @author Ben Echols (Lologarithm)
 * @author Dean Bailey (alron)
 */
public class WormholeXTremeServerListener implements Listener {

    /* (non-Javadoc)
     * @see org.bukkit.event.server.ServerListener#onPluginDisabled(org.bukkit.event.server.PluginEvent)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(final PluginDisableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("PermissionsEx") && !ConfigManager.getPermissionsSupportDisable()) {
            PermissionsSupport.disablePermissions();
        } else if (event.getPlugin().getDescription().getName().equals("Help") && !ConfigManager.getHelpSupportDisable()) {
            HelpSupport.disableHelp();
        } else if (event.getPlugin().getDescription().getName().equals("WormholeXTremeWorlds") && ConfigManager.isWormholeWorldsSupportEnabled()) {
            WormholeWorldsSupport.disableWormholeWorlds();
        } else if (event.getPlugin().getDescription().getName().equals("PermissionsBukkit") && !ConfigManager.getPermissionsSupportDisable()) {
        	PermissionsSupport.disablePermissions();
        } else if (event.getPlugin().getDescription().getName().equals("bPermissions") && !ConfigManager.getPermissionsSupportDisable()) {
        	PermissionsSupport.disablePermissions();
        }
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.server.ServerListener#onPluginEnabled(org.bukkit.event.server.PluginEvent)
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginEnable(final PluginEnableEvent event) {
        if (event.getPlugin().getDescription().getName().equals("PermissionsEx") && !ConfigManager.getPermissionsSupportDisable()) {
            PermissionsSupport.enablePermissions();
        } else if (event.getPlugin().getDescription().getName().equals("Help") && !ConfigManager.getHelpSupportDisable()) {
            HelpSupport.enableHelp();
        } else if (event.getPlugin().getDescription().getName().equals("PermissionsBukkit") && !ConfigManager.getPermissionsSupportDisable()) {
        	PermissionsSupport.enablePermissions();
        } else if (event.getPlugin().getDescription().getName().equals("bPermissions") && !ConfigManager.getPermissionsSupportDisable()) {
        	PermissionsSupport.enablePermissions();
        }
//        else if (event.getPlugin().getDescription().getName().equals("WormholeXTremeWorlds") && ConfigManager.isWormholeWorldsSupportEnabled()) {
//            WormholeWorldsSupport.enableWormholeWorlds();
//        }
    }
}
