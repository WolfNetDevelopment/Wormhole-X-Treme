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
package de.luricos.bukkit.WormholeXTreme.Wormhole.model;

import de.luricos.bukkit.WormholeXTreme.Wormhole.WormholeXTreme;
import de.luricos.bukkit.WormholeXTreme.Wormhole.config.ConfigManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.logic.StargateHelper;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.PermissionsManager.PermissionLevel;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * WormholeXtreme StargateDBManager.
 * 
 * @author Ben Echols (Lologarithm)
 */
public class StargateDBManager {

    /** The sql_connection. */
    private static Connection wormholeSQLConnection = null;
    /** The Store statement. */
    private static volatile PreparedStatement storeStatement;
    /** The Update gate statement. */
    private static volatile PreparedStatement updateGateStatement;
    /** The Get gate statement. */
    private static volatile PreparedStatement getGateStatement;
    /** The Remove statement. */
    private static volatile PreparedStatement removeStatement;
    /** The Update indv perm statement. */
    private static volatile PreparedStatement updateIndvPermStatement = null;
    /** The Store indv perm statement. */
    private static volatile PreparedStatement storeIndvPermStatement = null;
    /** The Get indv perm statement. */
    private static volatile PreparedStatement getIndvPermStatement = null;
    /** The Get all indv perm statement. */
    private static volatile PreparedStatement getAllIndvPermStatement = null;

    /**
     * Connect db.
     */
    private static void connectDB() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final Exception e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "ERROR: failed to load SQLITE JDBC driver.");
            e.printStackTrace();
            return;
        }

        try {
            if ((wormholeSQLConnection == null) || wormholeSQLConnection.isClosed()) {
                setWormholeSQLConnection(DriverManager.getConnection("jdbc:sqlite:./plugins/WormholeXTreme/WormholeXTremeDB/WormholeXTreme.sqlite", "sa", ""));
                wormholeSQLConnection.setAutoCommit(true);
            } else {
                WXTLogger.prettyLog(Level.SEVERE, false, "WormholeDB already connected.");
            }
        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Failed to intialized internal DB. Stargates will not be saved: " + e.getMessage());
        }
    }

    public static boolean isConnected() {
        if (wormholeSQLConnection != null) {
            try {
                if (wormholeSQLConnection.isClosed())
                    return false;
            } catch (SQLException e) {
                WXTLogger.prettyLog(Level.FINE, false, "DBLink not available.");
                return false;
            }

            return true;
        }

        return false;
    }

    /**
     * Gets the all individual permissions.
     * 
     * @return the concurrent hash map
     */
    public static ConcurrentHashMap<String, PermissionLevel> getAllIndividualPermissions() {
        final ConcurrentHashMap<String, PermissionLevel> perms = new ConcurrentHashMap<String, PermissionLevel>();
        if (!isConnected()) {
            connectDB();
        }

        ResultSet perm = null;
        try {
            if (wormholeSQLConnection.isClosed()) {
                connectDB();
            }

            if (getAllIndvPermStatement == null) {
                getAllIndvPermStatement = wormholeSQLConnection.prepareStatement("SELECT PlayerName, Permission FROM StargateIndividualPermissions;");
            }

            perm = getAllIndvPermStatement.executeQuery();
            while (perm.next()) {
                perms.put(perm.getString("PlayerName"), PermissionLevel.valueOf(perm.getString("Permission")));
            }
        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Error GetAllIndividualPermissions: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                perm.close();
            } catch (final SQLException e) {
                WXTLogger.prettyLog(Level.FINE, false, e.getMessage());
            }
        }
        return perms;
    }

    /**
     * Load stargates.
     * 
     * @param server
     *            the server
     */
    public static void loadStargates(final Server server) {
        if (!isConnected()) {
            connectDB();
        }

        //@TODO: rei-mplement world id search
//        final List<World> worlds = server.getWorlds();
        PreparedStatement stmt = null;
        ResultSet gatesData = null;
        try {
            if (wormholeSQLConnection.isClosed()) {
                connectDB();
            }
            stmt = wormholeSQLConnection.prepareStatement("SELECT * FROM Stargates;");

            gatesData = stmt.executeQuery();
            while (gatesData.next()) {
                final String networkName = gatesData.getString("Network");
                StargateNetwork sn = null;
                if (networkName != null) {
                    sn = StargateManager.getStargateNetwork(networkName);
                    if ((sn == null) && !networkName.equals("")) {
                        sn = StargateManager.addStargateNetwork(networkName);
                    }
                }
                // Is this the best way to retrieve a world?
//                final long worldId = gatesData.getLong("World");
                final String worldName = gatesData.getString("WorldName");
                final String worldEnvironment = gatesData.getString("WorldEnvironment");
                
                World w = null;
//                if (worldName.equals("")) {
//                    for (final World possW : worlds) {
//                        if (possW.getId() == worldId) {
//                            w = possW;
//                            break;
//                        }
//                    }
//                }
                
                if (ConfigManager.isWormholeWorldsSupportEnabled()) {
                    //@TODO: should throw an exception instead
                    if ((WormholeXTreme.getWorldHandler() != null) && !WormholeXTreme.getWorldHandler().loadWorld(worldName)) {
                        WXTLogger.prettyLog(Level.WARNING, true, "World: " + worldName + " is not a Wormhole World, the suggested action is to add it as one. Otherwise disregard this warning.");
                    }
                } else {
                    // this will create a new world if we run in native mode
                    server.createWorld(new WorldCreator(worldName).environment(Environment.valueOf(worldEnvironment)));
                }
                w = server.getWorld(worldName);

                final Stargate s = StargateHelper.parseVersionedData(gatesData.getBytes("GateData"), w, gatesData.getString("Name"), sn);
                if (s != null) {
                    s.setGateId(gatesData.getInt("Id"));
                    s.setGateOwner(gatesData.getString("Owner"));
                    s.setGateMessage(gatesData.getString("Message"));
                    String gateShapeName = gatesData.getString("GateShape");
                    if (gateShapeName == null) {
                        gateShapeName = "Standard";
                    }

                    s.setGateShape(StargateHelper.getStargateShape(gateShapeName));
                    if (sn != null) {
                        sn.getNetworkGateList().add(s);
                        if (s.isGateSignPowered()) {
                            sn.getNetworkSignGateList().add(s);
                            if ((s.getGateDialSign() != null) && (s.getGateDialSignBlock() != null)) {
                                s.tryClickTeleportSign(s.getGateDialSignBlock());
                            }
                        }
                    }
                    StargateManager.addStargate(s);
                } else {
                    WXTLogger.prettyLog(Level.WARNING, true, "Failed to load Stargate '" + sn + "' from DB.");
                }
            }
            gatesData.close();
            stmt.close();

            final ArrayList<Stargate> gateList = StargateManager.getAllGates();
            for (final Stargate s : gateList) {

                if (s.isGateLightsActive() && !s.isGateActive()) {
                    s.lightStargate(false);
                }

                if (s.getGateTempTargetId() >= 0) {
                    // I know this is bad, I am just trying to get this feature out asap.
                    for (final Stargate t : gateList) {
                        if (t.getGateId() == s.getGateTempTargetId()) {
                            s.dialStargate(t, true);
                            break;
                        }
                    }
                }

                if (s.getGateTempSignTarget() >= 0) {
                    // I know this is bad, I am just trying to get this feature out asap.
                    for (final Stargate t : gateList) {
                        if (t.getGateId() == s.getGateTempSignTarget()) {
                            s.setGateDialSignTarget(t);
                            break;
                        }
                    }
                }
            }

            WXTLogger.prettyLog(Level.INFO, false, gateList.size() + " Wormholes loaded from WormholeDB.");

        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Error loading stargates from DB: " + e.getMessage());
        } finally {
            try {
                gatesData.close();
            } catch (final SQLException e) {
                WXTLogger.prettyLog(Level.FINE, false, e.getMessage());
            }
            try {
                stmt.close();
            } catch (final SQLException e) {
                WXTLogger.prettyLog(Level.FINE, false, e.getMessage());
            }
        }
    }

    /**
     * Removes the stargate from sql.
     * 
     * @param s
     *            the s
     */
    protected static void removeStargateFromSQL(final Stargate s) {
        if (!isConnected()) {
            connectDB();
        }

        try {
            if (wormholeSQLConnection.isClosed()) {
                connectDB();
            }
            if (removeStatement == null) {
                removeStatement = wormholeSQLConnection.prepareStatement("DELETE FROM Stargates WHERE name = ?;");
            }

            removeStatement.setString(1, s.getGateName());
            removeStatement.executeUpdate();
        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Error storing stargate to DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets the wormhole sql connection.
     * 
     * @param connection
     *            the new wormhole sql connection
     */
    private static void setWormholeSQLConnection(final Connection connection) {
        StargateDBManager.wormholeSQLConnection = connection;
    }

    /**
     * Shutdown.
     */
    public static void shutdown() {
        try {
            if ((wormholeSQLConnection != null) && (!wormholeSQLConnection.isClosed())) {
                wormholeSQLConnection.close();
                wormholeSQLConnection = null;
                WXTLogger.prettyLog(Level.INFO, false, "WormholeDB shutdown successfull.");
            }
        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, " Failed to shutdown:" + e.getMessage());
        } finally {
            if (wormholeSQLConnection == null) {
                wormholeSQLConnection = null;
                storeStatement = null;
                updateGateStatement = null;
                getGateStatement = null;
                removeStatement = null;
                updateIndvPermStatement = null;
                storeIndvPermStatement = null;
                getIndvPermStatement = null;
                getAllIndvPermStatement = null;                
            }
        }
    }

    /**
     * Stargate to sql.
     * 
     * @param s
     *            the s
     */
    public static void stargateToSQL(final Stargate s) {
        if (!isConnected()) {
            connectDB();
        }
        ResultSet gatesData = null;
        try {
            if (wormholeSQLConnection.isClosed()) {
                connectDB();
            }
            if (getGateStatement == null) {
                getGateStatement = wormholeSQLConnection.prepareStatement("SELECT * FROM Stargates WHERE Name = ?");
            }
            getGateStatement.setString(1, s.getGateName());

            gatesData = getGateStatement.executeQuery();
            if (gatesData.next()) {
                if (updateGateStatement == null) {
                    updateGateStatement = wormholeSQLConnection.prepareStatement("UPDATE Stargates SET GateData = ?, Network = ?, World = ?, WorldName = ?, WorldEnvironment = ?, Owner = ?, GateShape = ?, Message = ? WHERE Id = ?");
                }

                updateGateStatement.setBytes(1, StargateHelper.stargatetoBinary(s));
                if (s.getGateNetwork() != null) {
                    updateGateStatement.setString(2, s.getGateNetwork().getNetworkName());
                } else {
                    updateGateStatement.setString(2, "");
                }
                updateGateStatement.setString(3, s.getGateWorld().getUID().toString());
                updateGateStatement.setString(4, s.getGateWorld().getName());
                updateGateStatement.setString(5, s.getGateWorld().getEnvironment().toString());
                updateGateStatement.setString(6, s.getGateOwner());
                if (s.getGateShape() == null) {
                    updateGateStatement.setString(7, "Standard");
                } else {
                    updateGateStatement.setString(7, s.getGateShape().getShapeName());
                }
                updateGateStatement.setString(8, s.getGateMessage());
                updateGateStatement.setLong(9, s.getGateId());
                updateGateStatement.executeUpdate();
            } else {
                gatesData.close();

                if (storeStatement == null) {
                    storeStatement = wormholeSQLConnection.prepareStatement("INSERT INTO Stargates(Name, GateData, Network, World, WorldName, WorldEnvironment, Owner, GateShape, Message) VALUES ( ? , ? , ? , ? , ? , ?, ?, ?, ? );");
                }

                storeStatement.setString(1, s.getGateName());
                final byte[] data = StargateHelper.stargatetoBinary(s);
                storeStatement.setBytes(2, data);
                if (s.getGateNetwork() != null) {
                    storeStatement.setString(3, s.getGateNetwork().getNetworkName());
                } else {
                    storeStatement.setString(3, "");
                }

                storeStatement.setString(4, s.getGateWorld().getUID().toString());
                storeStatement.setString(5, s.getGateWorld().getName());
                storeStatement.setString(6, s.getGateWorld().getEnvironment().toString());
                storeStatement.setString(7, s.getGateOwner());
                storeStatement.setString(8, s.getGateShape().getShapeName());
                storeStatement.setString(9, s.getGateMessage());
                storeStatement.executeUpdate();

                getGateStatement.setString(1, s.getGateName());
                gatesData = getGateStatement.executeQuery();
                if (gatesData.next()) {
                    s.setGateId(gatesData.getInt("Id"));
                }
            }
        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Error storing stargate to DB: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                gatesData.close();
            } catch (final SQLException e) {
                WXTLogger.prettyLog(Level.FINE, false, e.getMessage());
            }
        }
    }

    /**
     * Store individual permission in db.
     * 
     * @param player
     *            the player
     * @param pl
     *            the pl
     */
    public static void storeIndividualPermissionInDB(final String player, final PermissionLevel pl) {
        if (!isConnected()) {
            connectDB();
        }

        ResultSet perm = null;
        try {
            if (wormholeSQLConnection.isClosed()) {
                connectDB();
            }

            if (getIndvPermStatement == null) {
                getIndvPermStatement = wormholeSQLConnection.prepareStatement("SELECT Permission FROM StargateIndividualPermissions WHERE PlayerName = ?;");
            }

            getIndvPermStatement.setString(1, player);
            perm = getIndvPermStatement.executeQuery();
            if (!perm.next()) {
                if (storeIndvPermStatement == null) {
                    storeIndvPermStatement = wormholeSQLConnection.prepareStatement("INSERT INTO StargateIndividualPermissions ( PlayerName, Permission ) VALUES ( ? , ? );");
                }

                storeIndvPermStatement.setString(1, player);
                storeIndvPermStatement.setString(2, pl.toString());
                storeIndvPermStatement.executeUpdate();
            } else {
                if (updateIndvPermStatement == null) {
                    updateIndvPermStatement = wormholeSQLConnection.prepareStatement("UPDATE StargateIndividualPermissions SET Permission = ? WHERE PlayerName = ?;");
                }

                updateIndvPermStatement.setString(2, player);
                updateIndvPermStatement.setString(1, pl.toString());
                final int modified = updateIndvPermStatement.executeUpdate();

                if (modified != 1) {
                    WXTLogger.prettyLog(Level.SEVERE, false, "Failed to update " + player + " permissions in DB.");
                }
            }
        } catch (final SQLException e) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Error StoreIndividualPermissionInDB : " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                perm.close();
            } catch (final SQLException e) {
                WXTLogger.prettyLog(Level.FINE, false, e.getMessage());
            }
        }
    }
}
