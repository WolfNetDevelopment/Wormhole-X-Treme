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
package de.luricos.bukkit.WormholeXTreme.Wormhole.permissions;

import de.luricos.bukkit.WormholeXTreme.Wormhole.WormholeXTreme;
import de.luricos.bukkit.WormholeXTreme.Wormhole.config.ConfigManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;

import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * The Enum SimplePermission.
 *
 * @author alron
 */
enum SimplePermission {

    /** The USE. */
    USE("wormhole.simple.use"),
    /** The BUILD. */
    BUILD("wormhole.simple.build"),
    /** The REMOVE. */
    REMOVE("wormhole.simple.remove"),
    /** The CONFIG. */
    CONFIG("wormhole.simple.config");
    /** The simple permission node. */
    private final String simplePermissionNode;
    /** The Constant simplePermissionMap. */
    private static final Map<String, SimplePermission> simplePermissionMap = new HashMap<String, SimplePermission>();

    static {
        for (final SimplePermission simplePermission : EnumSet.allOf(SimplePermission.class)) {
            simplePermissionMap.put(simplePermission.simplePermissionNode, simplePermission);
        }
    }

    /**
     * From simple permission node.
     *
     * @param simplePermissionNode
     *            the simple permission node
     * @return the simple permission
     */
    public static SimplePermission fromSimplePermissionNode(final String simplePermissionNode) // NO_UCD
    {
        return simplePermissionMap.get(simplePermissionNode);
    }

    /**
     * Instantiates a new simple permission.
     *
     * @param simplePermissionNode
     *            the simple permission node
     */
    private SimplePermission(final String simplePermissionNode) {
        this.simplePermissionNode = simplePermissionNode;
    }

    /**
     * Check permission.
     *
     * @param player
     *            the player
     * @return true, if successful
     */
    protected boolean checkPermission(Player player) {
    	boolean permEx = false;
    	boolean permBuk = false;
    	if (WormholeXTreme.getPermissionsEx() != null)
    		permEx = true;
    	if (WormholeXTreme.getPermBukkit())
    		permBuk = true;

        if ((player != null) && !ConfigManager.getPermissionsSupportDisable() && (permEx || permBuk) && ConfigManager.getSimplePermissions()) {
            if (permEx && WormholeXTreme.getPermissionsEx().has(player, simplePermissionNode)) {
                WXTLogger.prettyLog(Level.FINE, false, "Player: " + player.getName() + "\" granted simple \"" + toString() + "\" permissions.");
                return true;
            } else if (permBuk && player.hasPermission(simplePermissionNode)) {
            	WXTLogger.prettyLog(Level.FINE, false, "Player: " + player.getName() + "\" granted simple \"" + toString() + "\" permissions.");
            	return true;
            }
            WXTLogger.prettyLog(Level.FINE, false, "Player: " + player.getName() + "\" denied simple \"" + toString() + "\" permissions.");

        }
        return false;
    }

    /**
     * Gets the simple permission.
     *
     * @return the simple permission
     */
    public String getSimplePermission() {
        return simplePermissionNode;
    }
}
