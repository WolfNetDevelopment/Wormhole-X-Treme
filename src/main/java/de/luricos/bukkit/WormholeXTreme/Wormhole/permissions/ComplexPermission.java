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
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.Stargate;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;

import org.bukkit.entity.Player;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * The Enum ComplexPermission.
 *
 * @author alron
 */
enum ComplexPermission {

    /** Sign Dialer Use */
    USE_SIGN("wormhole.use.sign"),
    /** Normal Dial Use */
    USE_DIALER("wormhole.use.dialer"),
    /** Compass Use */
    USE_COMPASS("wormhole.use.compass"),
    /** Use Cooldown Group One */
    USE_COOLDOWN_GROUP_ONE("wormhole.cooldown.groupone"),
    /** Use Cooldown Group two. */
    USE_COOLDOWN_GROUP_TWO("wormhole.cooldown.grouptwo"),
    /** Use Cooldown Group three. */
    USE_COOLDOWN_GROUP_THREE("wormhole.cooldown.groupthree"),
    /** Remove Own */
    REMOVE_OWN("wormhole.remove.own"),
    /** Remove All */
    REMOVE_ALL("wormhole.remove.all"),
    /** Build */
    BUILD("wormhole.build"),
    /** Build Restriction Group one. */
    BUILD_RESTRICTION_GROUP_ONE("wormhole.build.groupone"),
    /** Build Restriction Group two. */
    BUILD_RESTRICTION_GROUP_TWO("wormhole.build.grouptwo"),
    /** Build Restriction Group three. */
    BUILD_RESTRICTION_GROUP_THREE("wormhole.build.groupthree"),
    /** Config */
    CONFIG("wormhole.config"),
    /** List */
    LIST("wormhole.list"),
    /** Use Network */
    NETWORK_USE("wormhole.network.use."),
    /** Build Network. */
    NETWORK_BUILD("wormhole.network.build."),
    /** Go. */
    GO("wormhole.go");
    /** The complex permission node. */
    private final String complexPermissionNode;
    /** The Constant complexPermissionMap. */
    private static final Map<String, ComplexPermission> complexPermissionMap = new HashMap<String, ComplexPermission>();

    static {
        for (final ComplexPermission type : EnumSet.allOf(ComplexPermission.class)) {
            complexPermissionMap.put(type.complexPermissionNode, type);
        }
    }

    /**
     * From complex permission node.
     *
     * @param complexPermissionNode
     *            the complex permission node
     * @return the complex permission
     */
    public static ComplexPermission fromComplexPermissionNode(final String complexPermissionNode) // NO_UCD
    {
        return complexPermissionMap.get(complexPermissionNode);
    }

    /**
     * Instantiates a new complex permission.
     *
     * @param complexPermissionNode
     *            the complex permission node
     */
    private ComplexPermission(final String complexPermissionNode) {
        this.complexPermissionNode = complexPermissionNode;
    }

    /**
     * Check permission.
     *
     * @param player
     *            the player
     * @return true, if successful
     */
    protected boolean checkPermission(final Player player) {
        return checkPermission(player, null, null);
    }

    /**
     * Check permission.
     *
     * @param player
     *            the player
     * @param stargate
     *            the stargate
     * @return true, if successful
     */
    protected boolean checkPermission(final Player player, final Stargate stargate) {
        return checkPermission(player, stargate, null);
    }

    /**
     * Check permission.
     *
     * @param player the player
     * @param stargate the gate
     * @param networkName the network name
     * @return true, if successful
     */
    public boolean checkPermission(final Player player, final Stargate stargate, final String networkName) {
    	boolean permEx = false;
    	boolean permBuk = false;
    	if (WormholeXTreme.getPermissionsEx() != null)
    		permEx = true;
    	if (WormholeXTreme.getPermBukkit())
    		permBuk = true;

        if ((player != null) && (permEx || permBuk) && !ConfigManager.getSimplePermissions()) {
            boolean allowed = false;

            if (permEx) {
	            switch (this) {
	                case NETWORK_USE:
	                case NETWORK_BUILD:
	                    allowed = networkName != null && WormholeXTreme.getPermissionsEx().has(player, complexPermissionNode + networkName);
	                    break;
	                case REMOVE_OWN:
	                    allowed = ((stargate != null) && (stargate.getGateOwner() != null) && stargate.getGateOwner().equals(player.getName()) && WormholeXTreme.getPermissionsEx().has(player, complexPermissionNode));
	                    break;
	                default:
	                    allowed = WormholeXTreme.getPermissionsEx().has(player, complexPermissionNode);
	                    break;
	            }
            } else if (permBuk) {
            	switch (this) {
                case NETWORK_USE:
                case NETWORK_BUILD:
                    allowed = networkName != null && player.hasPermission(complexPermissionNode + networkName);
                    break;
                case REMOVE_OWN:
                    allowed = ((stargate != null) && (stargate.getGateOwner() != null) && stargate.getGateOwner().equals(player.getName()) && player.hasPermission(complexPermissionNode));
                    break;
                default:
                    allowed = player.hasPermission(complexPermissionNode);
                    break;
            }
            }

            if (allowed) {
                WXTLogger.prettyLog(Level.FINE, false, "Player: '" + player.getName() + "' granted complex \"" + toString() + "\" permission" + (networkName != null
                        ? " on network \"" + networkName + "\""
                        : "") + ".");
                return true;
            }

            WXTLogger.prettyLog(Level.FINE, false, "Player: '" + player.getName() + "' denied complex \"" + toString() + "\" permission" + (networkName != null
                    ? " on network \"" + networkName + "\""
                    : "") + ".");
        }
        return false;
    }

    /**
     * Check permission.
     *
     * @param player
     *            the player
     * @param networkName
     *            the network name
     * @return true, if successful
     */
    protected boolean checkPermission(final Player player, final String networkName) {
        return checkPermission(player, null, networkName);
    }

    /**
     * Gets the complex permission.
     *
     * @return the complex permission
     */
    public String getComplexPermission() {
        return complexPermissionNode;
    }
}
