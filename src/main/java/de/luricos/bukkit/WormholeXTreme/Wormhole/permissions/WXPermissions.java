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
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.PermissionsManager.PermissionLevel;

import org.bukkit.entity.Player;

/**
 * The Class WXPermissions.
 * 
 * @author alron
 */
public class WXPermissions {

    /**
     * The Enum PermissionType.
     */
    public static enum PermissionType {

        /** The DAMAGE permission. */
        DAMAGE,
        /** The SIGN permission. */
        SIGN,
        /** The DIALER permission. */
        DIALER,
        /** The BUILD permission. */
        BUILD,
        /** The REMOVE permission. */
        REMOVE,
        /** The USE permission. */
        USE,
        /** The LIST permission. */
        LIST,
        /** The CONFIG permission. */
        CONFIG,
        /** The GO permission. */
        GO,
        /** The COMPASS permission. */
        COMPASS,
        USE_COOLDOWN_GROUP_ONE,
        USE_COOLDOWN_GROUP_TWO,
        USE_COOLDOWN_GROUP_THREE,
        BUILD_RESTRICTION_GROUP_ONE,
        BUILD_RESTRICTION_GROUP_TWO,
        BUILD_RESTRICTION_GROUP_THREE
    }

    /**
     * Check wx permissions.
     * 
     * @param player
     *            the player
     * @param permissiontype
     *            the permissiontype
     * @return true, if successful
     */
    public static boolean checkWXPermissions(final Player player, final PermissionType permissiontype) {
        return checkWXPermissions(player, null, null, permissiontype);
    }

    /**
     * Check wx permisssions.
     * 
     * @param player
     *            the player
     * @param stargate
     *            the stargate
     * @param permissionstype
     *            the permissionstype
     * @return true, if successful
     */
    public static boolean checkWXPermissions(final Player player, final Stargate stargate, final PermissionType permissionstype) {
        return checkWXPermissions(player, stargate, null, permissionstype);
    }

    /**
     * Check wx permissions.
     * 
     * @param player
     *            the player
     * @param stargate
     *            the stargate
     * @param network
     *            the network
     * @param permissiontype
     *            the permissiontype
     * @return true, if successful
     */
    private static boolean checkWXPermissions(final Player player, final Stargate stargate, final String network, final PermissionType permissiontype) {
        if (player == null) {
            return false;
        }
        if (player.isOp()) {
            switch (permissiontype) {
                case DAMAGE:
                case REMOVE:
                case CONFIG:
                case GO:
                case SIGN:
                case DIALER:
                case USE:
                case LIST:
                case COMPASS:
                case BUILD:
                    return true;
                default:
                    return false;
            }
        } else if (!ConfigManager.getPermissionsSupportDisable() && (WormholeXTreme.getPermissionsEx() != null)) {

            if (ConfigManager.getSimplePermissions()) {
                switch (permissiontype) {
                    case LIST:
                        return (SimplePermission.CONFIG.checkPermission(player) || SimplePermission.USE.checkPermission(player));
                    case GO:
                    case CONFIG:
                        return SimplePermission.CONFIG.checkPermission(player);
                    case DAMAGE:
                    case REMOVE:
                        return (SimplePermission.REMOVE.checkPermission(player) || SimplePermission.CONFIG.checkPermission(player));
                    case COMPASS:
                    case SIGN:
                    case DIALER:
                    case USE:
                        return SimplePermission.USE.checkPermission(player);
                    case BUILD:
                        return SimplePermission.BUILD.checkPermission(player);
                    default:
                        return false;
                }
            } else {
                String networkName = "Public";
                switch (permissiontype) {
                    case LIST:
                        return (ComplexPermission.LIST.checkPermission(player) || ComplexPermission.CONFIG.checkPermission(player));
                    case CONFIG:
                        return ComplexPermission.CONFIG.checkPermission(player);
                    case GO:
                        return ComplexPermission.GO.checkPermission(player);
                    case COMPASS:
                        return ComplexPermission.USE_COMPASS.checkPermission(player);
                    case DAMAGE:
                    case REMOVE:
                        return (ComplexPermission.CONFIG.checkPermission(player) || ComplexPermission.REMOVE_ALL.checkPermission(player) || ComplexPermission.REMOVE_OWN.checkPermission(player, stargate));
                    case SIGN:
                        if ((stargate != null) && (stargate.getGateNetwork() != null)) {
                            networkName = stargate.getGateNetwork().getNetworkName();
                        }
                        return ((ComplexPermission.USE_SIGN.checkPermission(player) && (networkName.equals("Public") || (!networkName.equals("Public") && ComplexPermission.NETWORK_USE.checkPermission(player, networkName)))));
                    case DIALER:
                        if ((stargate != null) && (stargate.getGateNetwork() != null)) {
                            networkName = stargate.getGateNetwork().getNetworkName();
                        }
                        return ((ComplexPermission.USE_DIALER.checkPermission(player) && (networkName.equals("Public") || (!networkName.equals("Public") && ComplexPermission.NETWORK_USE.checkPermission(player, networkName)))));
                    case USE:
                        if ((stargate != null) && (stargate.getGateNetwork() != null)) {
                            networkName = stargate.getGateNetwork().getNetworkName();
                        }
                        return (((ComplexPermission.USE_SIGN.checkPermission(player) && (networkName.equals("Public") || (!networkName.equals("Public") && ComplexPermission.NETWORK_USE.checkPermission(player, networkName)))) || (ComplexPermission.USE_DIALER.checkPermission(player) && (networkName.equals("Public") || (!networkName.equals("Public") && ComplexPermission.NETWORK_USE.checkPermission(player, networkName))))));
                    case BUILD:
                        if (stargate != null) {
                            if (stargate.getGateNetwork() != null) {
                                networkName = stargate.getGateNetwork().getNetworkName();
                            }
                        } else {
                            if (network != null) {
                                networkName = network;
                            }
                        }
                        return ((ComplexPermission.BUILD.checkPermission(player) && (networkName.equals("Public") || (!networkName.equals("Public") && ComplexPermission.NETWORK_BUILD.checkPermission(player, networkName)))));
                    case USE_COOLDOWN_GROUP_ONE:
                        return ComplexPermission.USE_COOLDOWN_GROUP_ONE.checkPermission(player);
                    case USE_COOLDOWN_GROUP_TWO:
                        return ComplexPermission.USE_COOLDOWN_GROUP_TWO.checkPermission(player);
                    case USE_COOLDOWN_GROUP_THREE:
                        return ComplexPermission.USE_COOLDOWN_GROUP_THREE.checkPermission(player);
                    case BUILD_RESTRICTION_GROUP_ONE:
                        return ComplexPermission.BUILD_RESTRICTION_GROUP_ONE.checkPermission(player);
                    case BUILD_RESTRICTION_GROUP_TWO:
                        return ComplexPermission.BUILD_RESTRICTION_GROUP_TWO.checkPermission(player);
                    case BUILD_RESTRICTION_GROUP_THREE:
                        return ComplexPermission.BUILD_RESTRICTION_GROUP_THREE.checkPermission(player);
                    default:
                        return false;
                }
            }
        } else {
            if (stargate != null) {
                PermissionLevel lvl = null;
                switch (permissiontype) {
                    case DAMAGE:
                    case REMOVE:
                    case CONFIG:
                    case GO:
                        lvl = PermissionsManager.getPermissionLevel(player, stargate);
                        return (lvl == PermissionLevel.WORMHOLE_FULL_PERMISSION);
                    case SIGN:
                    case DIALER:
                    case USE:
                    case LIST:
                    case COMPASS:
                        lvl = PermissionsManager.getPermissionLevel(player, stargate);
                        return (lvl == PermissionLevel.WORMHOLE_CREATE_PERMISSION) || (lvl == PermissionLevel.WORMHOLE_USE_PERMISSION) || (lvl == PermissionLevel.WORMHOLE_FULL_PERMISSION);
                    case BUILD:
                        lvl = PermissionsManager.getPermissionLevel(player, stargate);
                        return (lvl == PermissionLevel.WORMHOLE_CREATE_PERMISSION) || (lvl == PermissionLevel.WORMHOLE_FULL_PERMISSION);
                    default:
                        return false;

                }
            }
        }
        return false;
    }

    /**
     * Check wx permissions.
     * 
     * @param player
     *            the player
     * @param network
     *            the network
     * @param permissiontype
     *            the permissiontype
     * @return true, if successful
     */
    public static boolean checkWXPermissions(final Player player, final String network, final PermissionType permissiontype) {
        return checkWXPermissions(player, null, network, permissiontype);
    }
}
