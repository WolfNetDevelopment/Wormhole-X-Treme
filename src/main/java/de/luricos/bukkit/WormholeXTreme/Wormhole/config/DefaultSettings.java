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
package de.luricos.bukkit.WormholeXTreme.Wormhole.config;

import de.luricos.bukkit.WormholeXTreme.Wormhole.config.ConfigManager.ConfigKeys;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.PermissionsManager.PermissionLevel;

/**
 * The Class DefaultSettings.
 * Based on class "SettingsList" from MinecartMania by Afforess.
 */
public class DefaultSettings {

    /** The Constant config. */
    final static Setting[] config = {
        new Setting(ConfigKeys.TIMEOUT_ACTIVATE, 30, "Number of seconds after a gate is activated, but before dialing before timing out.", "WormholeXTreme"),
        new Setting(ConfigKeys.TIMEOUT_SHUTDOWN, 38, "Number of seconds after a gate is dialed before automatically shutdown. With 0 timeout a gate won't shutdown until something goes through the gate.", "WormholeXTreme"),
        new Setting(ConfigKeys.BUILD_RESTRICTION_ENABLED, false, "Enable build count restrictions. Reuires complex permissions.", "WormholeXTreme"),
        new Setting(ConfigKeys.BUILD_RESTRICTION_GROUP_ONE, 1, "Total number of stargates a member of build restriction group one can build.", "WormholeXTreme"),
        new Setting(ConfigKeys.BUILD_RESTRICTION_GROUP_TWO, 2, "Total number of stargates a member of build restriction group two can build.", "WormholeXTreme"),
        new Setting(ConfigKeys.BUILD_RESTRICTION_GROUP_THREE, 3, "Total number of stargates a member of build restriction group three can build.", "WormholeXTreme"),
        new Setting(ConfigKeys.USE_COOLDOWN_ENABLED, false, "Enable Cooldown timers on stargate usage. Timer only activates on passage through wormholes. Requires complex permissions enabled.", "WormholeXTreme"),
        new Setting(ConfigKeys.USE_COOLDOWN_GROUP_ONE, 120, "Cooldown time in seconds between stargate use for members of use cooldown group one.", "WormholeXTreme"),
        new Setting(ConfigKeys.USE_COOLDOWN_GROUP_TWO, 60, "Cooldown time in seconds between stargate use for members of use cooldown group two.", "WormholeXTreme"),
        new Setting(ConfigKeys.USE_COOLDOWN_GROUP_THREE, 30, "Cooldown time in seconds between stargate use for members of use cooldown group three.", "WormholeXTreme"),
        new Setting(ConfigKeys.BUILT_IN_PERMISSIONS_ENABLED, false, "This should be set to true if you want the built in permissions enabled. This setting does nothing if you have Permissions plugin installed.", "WormholeXTreme"),
        new Setting(ConfigKeys.BUILT_IN_DEFAULT_PERMISSION_LEVEL, PermissionLevel.WORMHOLE_USE_PERMISSION, "If built in permissions are being used, this is the default level of control users (non-ops) have.", "WormholeXTreme"),
        new Setting(ConfigKeys.PERMISSIONS_SUPPORT_DISABLE, false, "If set to true, Permissions plugin will not be attached to evem if available.", "WormholeXTreme"),
        new Setting(ConfigKeys.SIMPLE_PERMISSIONS, false, "If using Permissions plugin based permissions, setting this to true switches WormholeXTreme to use an extremely simplified permissions set ('wormhole.simple.use', 'wormhole.simple.build', 'wormhole.simple.config', and 'wormhole.simple.remove').", "WormholeXTreme"),
        new Setting(ConfigKeys.SUPER_PERM_ENABLE, false, "Set to true to enable support for Bukkits build in PermissionsAPI.", "WormholeXTreme"),
        new Setting(ConfigKeys.WORMHOLE_USE_IS_TELEPORT, false, "The wormhole.use (or wormhole.simple.use) permission means that a user can teleport through gate. When false a user will be able to teleport but not activate a gate. When true only users with wormhole.use (or wormhole.simple.use) can even teleport.", "WormholeXTreme"),
        new Setting(ConfigKeys.HELP_SUPPORT_DISABLE, false, "If set to true, Help plugin will not be attached to even if available.", "WormholeXTreme"),
        new Setting(ConfigKeys.WORLDS_SUPPORT_ENABLED, false, "If set to true, the Wormhole X-Treme will offload all of its Chunk and World loading functionality to Wormhole Extreme Worlds.", "WormholeXTreme"),
        new Setting(ConfigKeys.LOG_LEVEL, "INFO", "Log level to use for minecraft logging purposes. Values are SEVERE, WARNING, INFO, CONFIG, FINE, FINER, and FINEST. In order of least to most logging output.", "WormholeXTreme"),
        new Setting(ConfigKeys.SHOW_GATE_WELCOME_MESSAGE, true, "If set to true, the player will receive a welcome message after arrival", "WormholeXTreme"),
        new Setting(ConfigKeys.USE_EVENT_OR_TP_TRANSPORT, true, "If set to true, the player will be transported via event handling. If you use plugins that can overwrite onPlayerMove() then you may want to change this to false", "WormholeXTreme"),
        new Setting(ConfigKeys.WORMHOLE_KICKBACK_BLOCK_COUNT, 2, "Set the amount of blocks the player will be kicked back if the target's gate is active. Default is 2. Set to 0 to disable this feature.", "WormholeXTreme")};
}
