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
package de.luricos.bukkit.WormholeXTreme.Wormhole.plugin;

import de.luricos.bukkit.WormholeXTreme.Wormhole.WormholeXTreme;
import de.luricos.bukkit.WormholeXTreme.Wormhole.config.ConfigManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.logging.Level;

/**
 * The Class PermissionsSupport.
 *
 * @author alron
 */
public class PermissionsSupport {

    /**
     * Check permissions version.
     *
     * @param version
     *            the version
     */
    private static void checkPermissionsVersion(final String version) {
        Double ver = Double.parseDouble(version);
        if (ver < 1.8) {
            WXTLogger.prettyLog(Level.WARNING, false, "Not supported version of PermissionsEx. Recommended is at least 1.8");
        }
    }

    /**
     * Disable permissions.
     */
    public static void disablePermissions() {
        if (WormholeXTreme.getPermissionsEx() != null) {
            WormholeXTreme.setPermissionsEx(null);
            WXTLogger.prettyLog(Level.INFO, false, "Detached from Permissions plugin.");
        } else if (WormholeXTreme.getPermBukkit()) {
        	WormholeXTreme.setPermBukkit(false);
        	WXTLogger.prettyLog(Level.INFO, false, "Detached from Permissions plugin.");
        }
    }

    /**
     * Setup permissions.
     */
    public static void enablePermissions() {
        if (!ConfigManager.getPermissionsSupportDisable()) {
            if (WormholeXTreme.getPermissionsEx() == null) {
                final Plugin test = WormholeXTreme.getThisPlugin().getServer().getPluginManager().getPlugin("PermissionsEx");
                final Plugin test2 = WormholeXTreme.getThisPlugin().getServer().getPluginManager().getPlugin("PermissionsBukkit");
                if ((test != null) && (Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsEx"))) {
                    final String v = test.getDescription().getVersion();
                    checkPermissionsVersion(v);
                    try {
                        WormholeXTreme.setPermissionsEx(PermissionsEx.getPermissionManager());
                        WXTLogger.prettyLog(Level.INFO, false, "Attached to PermissionsEx version " + v);
                        if (ConfigManager.getSimplePermissions()) {
                            WXTLogger.prettyLog(Level.INFO, false, "Simple Permissions Enabled");
                        } else {
                            WXTLogger.prettyLog(Level.INFO, false, "Complex Permissions Enabled");
                        }
                    } catch (final ClassCastException e) {
                        WXTLogger.prettyLog(Level.WARNING, false, "Failed to get Permissions Handler. Defaulting to built-in permissions.");
                    }
                } else if ((test2 != null) && (Bukkit.getServer().getPluginManager().isPluginEnabled("PermissionsBukkit"))) {
                	WormholeXTreme.setPermBukkit(true);
                	WXTLogger.prettyLog(Level.INFO, false, "Attached to PermissionsBukkit");
                	if (ConfigManager.getSimplePermissions()) {
                        WXTLogger.prettyLog(Level.INFO, false, "Simple Permissions Enabled");
                    } else {
                        WXTLogger.prettyLog(Level.INFO, false, "Complex Permissions Enabled");
                    }
                } else {
                    WXTLogger.prettyLog(Level.INFO, false, "Permission Plugin not yet available. Defaulting to built-in permissions until Permissions is loaded.");
                }
            }
        } else {
            WXTLogger.prettyLog(Level.INFO, false, "Permission Plugin support disabled via settings.txt.");
        }
    }
}
