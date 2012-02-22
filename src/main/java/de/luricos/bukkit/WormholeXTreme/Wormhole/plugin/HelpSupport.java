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

import me.taylorkelly.help.Help;

import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * The Class HelpPlugin.
 * 
 * @author alron
 */
public class HelpSupport {

    /**
     * Check help version.
     * 
     * @param version
     *            the version
     */
    private static void checkHelpVersion(final String version) {
        if (!version.startsWith("0.2")) {
            WXTLogger.prettyLog(Level.WARNING, false, "Not a supported version of Help. Recommended is 0.2.x");
        }
    }

    /**
     * Disable help.
     */
    public static void disableHelp() {
        if (WormholeXTreme.getHelp() != null) {
            WormholeXTreme.setHelp(null);
            WXTLogger.prettyLog(Level.INFO, false, "Detached from Help plugin.");
        }
    }

    /**
     * Setup help.
     */
    public static void enableHelp() {
        if (!ConfigManager.getHelpSupportDisable()) {
            if (WormholeXTreme.getHelp() == null) {
                final Plugin helptest = WormholeXTreme.getThisPlugin().getServer().getPluginManager().getPlugin("Help");
                if (helptest != null) {
                    final String version = helptest.getDescription().getVersion();
                    checkHelpVersion(version);
                    try {
                        WormholeXTreme.setHelp(((Help) helptest));
                        WXTLogger.prettyLog(Level.INFO, false, "Attached to Help version " + version);
                    } catch (final ClassCastException e) {
                        WXTLogger.prettyLog(Level.WARNING, false, "Failed to get cast to Help: " + e.getMessage());
                    }
                } else {
                    WXTLogger.prettyLog(Level.INFO, false, "Help Plugin not yet available - there will be no Help integration until loaded.");
                }
            }
        } else {
            WXTLogger.prettyLog(Level.INFO, false, "Help Plugin support disabled via settings.txt.");
        }
    }

    /**
     * Register help commands.
     */
    public static void registerHelpCommands() {
        if ((WormholeXTreme.getHelp() != null) && !ConfigManager.getHelpSupportDisable()) {
            final String[] cp = new String[]{"wormhole.use.sign", "wormhole.use.dialer", "wormhole.use.compass",
                "wormhole.remove.own", "wormhole.remove.all", "wormhole.build", "wormhole.config", "wormhole.list",
                "wormhole.go"};

            final String[] sp = new String[]{"wormhole.simple.use", "wormhole.simple.build", "wormhole.simple.config",
                "wormhole.simple.remove"};

            String dial;
            String wxidc;
            String[] wxforce;
            String wxcompass;
            String wxcomplete;
            String[] wxremove;
            String[] wxlist;
            String wxgo;
            String wxbuild;
            String wxbuildlist;
            String wxreload;
            String wxstatus;
            String wormhole;
            if (WormholeXTreme.getPermissionsEx() != null) {
                if (ConfigManager.getSimplePermissions()) {
                    dial = sp[0];
                    wxidc = sp[2];
                    wxforce = new String[]{sp[2], sp[3]};
                    wxcompass = sp[0];
                    wxcomplete = sp[1];
                    wxremove = new String[]{sp[3]};
                    wxlist = new String[]{sp[0], sp[2]};
                    wxgo = sp[2];
                    wxbuild = sp[2];
                    wxbuildlist = sp[2];
                    wxstatus = sp[2];
                    wxreload = sp[2];
                    wormhole = sp[2];
                } else {
                    dial = cp[1];
                    wxidc = cp[6];
                    wxforce = new String[]{cp[4], cp[6]};
                    wxcompass = cp[2];
                    wxcomplete = cp[5];
                    wxremove = new String[]{cp[3], cp[4]};
                    wxlist = new String[]{cp[6], cp[7]};
                    wxgo = cp[8];
                    wxbuild = cp[6];
                    wxbuildlist = cp[6];
                    wxstatus = cp[6];
                    wxreload = cp[6];
                    wormhole = cp[6];
                }
            } else {
                dial = "";
                wxidc = "OP";
                wxforce = new String[]{"OP"};
                wxcompass = "OP";
                wxcomplete = "OP";
                wxremove = new String[]{""};
                wxlist = new String[]{"OP"};
                wxgo = "OP";
                wxbuild = "OP";
                wxbuildlist = "OP";
                wxstatus = "OP";
                wxreload = "OP";
                wormhole = "OP";
            }

            WormholeXTreme.getHelp().registerCommand("dial [stargate] <idc>", "Dial [stargate] and optionally unlock <idc>", WormholeXTreme.getThisPlugin(), true, dial);
            WormholeXTreme.getHelp().registerCommand("wxidc [stargate] <idc|-clear>", "Display [stargate] idc, optionally set <idc> or <-clear> idc", WormholeXTreme.getThisPlugin(), wxidc);
            WormholeXTreme.getHelp().registerCommand("wxforce [stargate|-all]", "Forcefully close and drop iris on either [-all] or just one [stargate]", WormholeXTreme.getThisPlugin(), wxforce);
            WormholeXTreme.getHelp().registerCommand("wxcompass", "Point compass at nearest Stargate", WormholeXTreme.getThisPlugin(), wxcompass);
            WormholeXTreme.getHelp().registerCommand("wxcomplete [stargate] <idc=[idc]> <net=[net]>", "Complete [stargate] construction, optional [idc] and [net]", WormholeXTreme.getThisPlugin(), true, wxcomplete);
            WormholeXTreme.getHelp().registerCommand("wxremove [stargate] <-all>", "Remove a [stargate], optionally destroy <-all> its blocks", WormholeXTreme.getThisPlugin(), wxremove);
            WormholeXTreme.getHelp().registerCommand("wxlist", "List all stargates", WormholeXTreme.getThisPlugin(), wxlist);
            WormholeXTreme.getHelp().registerCommand("wxgo [stargate]", "Teleport to [stargate]", WormholeXTreme.getThisPlugin(), wxgo);
            WormholeXTreme.getHelp().registerCommand("wxbuild [gateshape]", "Automaticially build a stargate in the specified [gateshape]", WormholeXTreme.getThisPlugin(), wxbuild);
            WormholeXTreme.getHelp().registerCommand("wxbuildlist", "List available gate shapes", WormholeXTreme.getThisPlugin(), wxbuildlist);
            WormholeXTreme.getHelp().registerCommand("wxreload now", "Reload WormholeXTreme", WormholeXTreme.getThisPlugin(), wxreload);
            WormholeXTreme.getHelp().registerCommand("wxstatus all", "Show system status", WormholeXTreme.getThisPlugin(), wxstatus);
            WormholeXTreme.getHelp().registerCommand("wormhole", "Wormhole administration and configuration command", WormholeXTreme.getThisPlugin(), true, wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole owner [stargate] <owner>", "Display owner of [stargate], optionally change <owner>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole portalmaterial [stargate] <material>", "Display portalmaterial on [stargate], optionally change <material>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole irismaterial [stargate] <material>", "Display irismaterial on [stargate], optionally change <material>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole lightmaterial [stargate] <material>", "Display lightmaterial on [stargate], optionally change <material>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole redstone [stargate] <boolean>", "Display redstone status on [stargate], optionally change via <boolean>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole custom [stargate] <boolean>", "Display custom status on [stargate], optionally change via <boolean>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole shutdown_timeout <timeout>", "Display shutdown timeout, optionally change <timeout>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole activate_timeout <timeout>", "Display activation timeout, optionally change <timeout>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole simple <boolean>", "Display simple permissions, optionally change via <boolean>", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole toggle_gwm", "Toggle gate welcome message enable/disable", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole show_gwm", "Show current gate welcome message status", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole toggle_transport", "Toggle transportation method TELEPORT/EVENT", WormholeXTreme.getThisPlugin(), wormhole);
            WormholeXTreme.getHelp().registerCommand("wormhole show_transport", "Show current transportation method", WormholeXTreme.getThisPlugin(), wormhole);
        }
    }
}
