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
import de.luricos.bukkit.WormholeXTreme.Wormhole.logic.StargateHelper;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.Stargate;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.StargateManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.model.StargateShape;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.StargateRestrictions;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.WXPermissions;
import de.luricos.bukkit.WormholeXTreme.Wormhole.permissions.WXPermissions.PermissionType;
import de.luricos.bukkit.WormholeXTreme.Wormhole.player.PlayerOrientation;
import de.luricos.bukkit.WormholeXTreme.Wormhole.player.WormholePlayer;
import de.luricos.bukkit.WormholeXTreme.Wormhole.player.WormholePlayerManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.material.Lever;

import java.util.logging.Level;

/**
 * WormholeXtreme Player Listener.
 * 
 * @author Ben Echols (Lologarithm)
 * @author Dean Bailey (alron)
 */
public class WormholeXTremePlayerListener implements Listener {

    /**
     * Button lever hit.
     * 
     * @param player
     *            the p
     * @param clickedBlock
     *            the clicked
     * @param direction
     *            the direction
     * @return true, if successful
     */
    protected static boolean buttonLeverHit(Player player, Block clickedBlock, BlockFace direction) {
        Stargate stargate = StargateManager.getGateFromBlock(clickedBlock);

        if (stargate != null) {
            // Dial, SignPowered gates logic
            if (WorldUtils.isSameBlock(stargate.getGateDialLeverBlock(), clickedBlock) && ((stargate.isGateSignPowered() && WXPermissions.checkWXPermissions(player, stargate, PermissionType.SIGN)) || (!stargate.isGateSignPowered() && WXPermissions.checkWXPermissions(player, stargate, PermissionType.DIALER)))) {

                WormholePlayerManager.getRegisteredWormholePlayer(player).addStargate(stargate);
                WormholePlayer wormholePlayer = handleGateActivationSwitch(player);

                if (wormholePlayer == null)
                    return true;

                if ((!wormholePlayer.getProperties().hasReceivedRemoteActiveMessage()) && (wormholePlayer.getProperties().hasActivatedStargate())) {
                    if (wormholePlayer.getStargate().getGateName() == null ? stargate.getGateName() != null : !wormholePlayer.getStargate().getGateName().equals(stargate.getGateName())) {
                        // if true gate was lightened up already
                        if (!wormholePlayer.getStargate().isGateActive()) {
                            WXTLogger.prettyLog(Level.FINE, false, "Gate '" + wormholePlayer.getStargate().getGateName() + "' didnt lightened up.");
                        }

                        WXTLogger.prettyLog(Level.FINE, false, "New gate for player '" + player.getName() + "' was set to stargate '" + wormholePlayer.getStargate().getGateName() + "'");
                    }
                } else {
                    WXTLogger.prettyLog(Level.FINE, false, "Gate '" + wormholePlayer.getStargate().getGateName() + "' was remote active for player '" + player.getName() + "': no permission, invalid gate target");
                    wormholePlayer.getProperties().setHasReceivedRemoteActiveMessage(true);
                }

                if ((wormholePlayer.getProperties().hasShutdownGate()) || (wormholePlayer.getProperties().hasReceivedIrisLockMessage()) ||
                    (!wormholePlayer.getProperties().hasActivatedStargate()) || (wormholePlayer.getProperties().hasReceivedRemoteActiveMessage())) {
                    wormholePlayer.removeStargate(stargate);
                }
            } else if (WorldUtils.isSameBlock(stargate.getGateIrisLeverBlock(), clickedBlock) && (!stargate.isGateSignPowered() && WXPermissions.checkWXPermissions(player, stargate, PermissionType.DIALER))) {
                Lever lever = new Lever(clickedBlock.getType(), clickedBlock.getData());
                WXTLogger.prettyLog(Level.FINE, false, "Player '" + player.getName() + "' has triggered the iris lever of gate '" + stargate.getGateName() + "' status is now " + (!lever.isPowered()));
                stargate.toggleIrisActive(true);
            } else if (WorldUtils.isSameBlock(stargate.getGateIrisLeverBlock(), clickedBlock) || WorldUtils.isSameBlock(stargate.getGateDialLeverBlock(), clickedBlock)) {
                player.sendMessage(ConfigManager.MessageStrings.permissionNo.toString());

                // remove stargate from user if no permission found
                WormholePlayerManager.getRegisteredWormholePlayer(player).removeStargate(stargate);
            }

            return true;
        } else {
            if (direction == null) {
                switch (clickedBlock.getData()) {
                    case 1:
                        direction = BlockFace.SOUTH;
                        break;
                    case 2:
                        direction = BlockFace.NORTH;
                        break;
                    case 3:
                        direction = BlockFace.WEST;
                        break;
                    case 4:
                        direction = BlockFace.EAST;
                        break;
                    default:
                        break;
                }

                if (direction == null) {
                    return false;
                }
            }
            // Check to see if player has already run the "build" command.
            StargateShape shape = StargateManager.getPlayerBuilderShape(player);

            Stargate newGate;
            if (shape != null) {
                newGate = StargateHelper.checkStargate(clickedBlock, direction, shape);
            } else {
                WXTLogger.prettyLog(Level.FINEST, false, "Attempting to find any gate shapes!");
                newGate = StargateHelper.checkStargate(clickedBlock, direction);
            }

            if (newGate != null) {
                if (WXPermissions.checkWXPermissions(player, newGate, PermissionType.BUILD) && !StargateRestrictions.isPlayerBuildRestricted(player)) {
                    if (newGate.isGateSignPowered()) {
                        player.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Stargate Design Valid with Sign Nav.");
                        if (newGate.getGateName().equals("")) {
                            player.sendMessage(ConfigManager.MessageStrings.constructNameInvalid.toString() + "\"\"");
                        } else {
                            boolean success = StargateManager.completeStargate(player, newGate);
                            if (success) {
                                player.sendMessage(ConfigManager.MessageStrings.constructSuccess.toString());
                                newGate.getGateDialSign().setLine(0, "-" + newGate.getGateName() + "-");
                                newGate.getGateDialSign().setData(newGate.getGateDialSign().getData());
                                newGate.getGateDialSign().update();
                            } else {
                                player.sendMessage("Stargate constrution failed!?");
                            }
                        }

                    } else {
                        // Print to player that it was successful!
                        player.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Valid Stargate Design! \u00A73:: \u00A7B<required> \u00A76[optional]");
                        player.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Type \'\u00A7F/wxcomplete \u00A7B<name> \u00A76[idc=IDC] [net=NET]\u00A77\' to complete.");

                        // Add gate to unnamed gates.
                        StargateManager.addIncompleteStargate(player.getName(), newGate);
                    }
                    return true;
                } else {
                    if (newGate.isGateSignPowered()) {
                        newGate.resetTeleportSign();
                    }
                    StargateManager.removeIncompleteStargate(player);
                    if (StargateRestrictions.isPlayerBuildRestricted(player)) {
                        player.sendMessage(ConfigManager.MessageStrings.playerBuildCountRestricted.toString());
                    }
                    player.sendMessage(ConfigManager.MessageStrings.permissionNo.toString());
                    return true;
                }
            } else {
                WXTLogger.prettyLog(Level.FINE, false, player.getName() + " has pressed a button or lever but we did not find a valid gate shape");
                //player.sendMessage(ConfigManager.MessageStrings.gateWithInvalidShape.toString() + ConfigManager.MessageStrings.gateWithInvalidShapeAssistance);
            }
        }
        return false;
    }

    /**
     * Handle gate activation switch.
     * 
     * @param player the player
     * @return WormholePlayer
     */
    protected static WormholePlayer handleGateActivationSwitch(Player player) {
        WormholePlayer wormholePlayer = WormholePlayerManager.getRegisteredWormholePlayer(player);

        if ((wormholePlayer == null) || (wormholePlayer.getStargate() == null))
            return null;

        Stargate currentGate = wormholePlayer.getStargate();

        if (currentGate.isGateActive() || currentGate.isGateLightsActive()) {
            // if the gate has a target shut it down when lever changes state
            if (currentGate.getGateTarget() != null) {
                //Shutdown Stargate
                currentGate.shutdownStargate(true);
                player.sendMessage(String.format(ConfigManager.MessageStrings.gateShutdown.toString(), currentGate.getGateName() + " "));

                wormholePlayer.getProperties().setHasShutdownGate(true);

                //return true;
                return wormholePlayer;
            } else {
                // on lever shutdown hit
                if ((currentGate.getSourceGateName() == null) && (currentGate.isGateActive())) {
                    currentGate.stopActivationTimer();
                    currentGate.setGateActive(false);
                    currentGate.toggleDialLeverState(false);
                    currentGate.lightStargate(false);

                    wormholePlayer.getProperties().setHasActivatedStargate(false);
                    currentGate.setLastUsedBy(wormholePlayer.getPlayer());
                    player.sendMessage(String.format(ConfigManager.MessageStrings.gateDeactivated.toString(), currentGate.getGateName() + " "));

                    //return true;
                    return wormholePlayer;
                } else {
                    if (currentGate.isGateLightsActive() && (!currentGate.isGateActive()) && (currentGate.getLastUsedBy() != wormholePlayer.getName())) {
                        player.sendMessage(ConfigManager.MessageStrings.errorHeader.toString() + "Gate has been activated by '" + currentGate.getLastUsedBy() + "' already.");
                        wormholePlayer.getProperties().setHasReceivedWasActivatedOther(true);
                    } else if (currentGate.isGateLightsActive() && (!currentGate.isGateActive()) && (currentGate.getLastUsedBy().equals(wormholePlayer.getName()))) {
                        currentGate.stopActivationTimer();
                        currentGate.setGateActive(false);
                        currentGate.toggleDialLeverState(false);
                        currentGate.lightStargate(false);

                        wormholePlayer.getProperties().setHasActivatedStargate(false);
                        currentGate.setLastUsedBy(wormholePlayer.getPlayer());
                        player.sendMessage(String.format(ConfigManager.MessageStrings.gateDeactivated.toString(), currentGate.getGateName() + " "));
                    } else {
                        wormholePlayer.getProperties().setHasReceivedRemoteActiveMessage(true);
                        Stargate sourceGate = StargateManager.getStargate(currentGate.getSourceGateName());
                        if (sourceGate != null) {
                            player.sendMessage(String.format(ConfigManager.MessageStrings.gateRemoteActive.toString(), currentGate.getGateName()+" ", " by " + sourceGate.getLastUsedBy()));
                            player.sendMessage(ConfigManager.MessageStrings.errorHeader.toString() + "Detected Wormhole source " + currentGate.getSourceGateName());
                        }
                    }

                    // return false;
                    return wormholePlayer;
                }
            }
        } else {
            if (currentGate.isGateSignPowered()) {
                if (WXPermissions.checkWXPermissions(player, currentGate, PermissionType.SIGN)) {
                    if ((currentGate.getGateDialSign() == null) && (currentGate.getGateDialSignBlock() != null)) {
                        currentGate.tryClickTeleportSign(currentGate.getGateDialSignBlock());
                    }

                    if (currentGate.getGateDialSignTarget() != null) {
                        if (currentGate.dialStargate(currentGate.getGateDialSignTarget(), false)) {
                            player.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Stargates connected!");

                            wormholePlayer.getProperties().setHasActivatedStargate(true);
                            currentGate.setLastUsedBy(player.getName());
                            WXTLogger.prettyLog(Level.FINE, false, "Player '" + currentGate.getLastUsedBy() + "' has activated gate '" + wormholePlayer.getStargate().getGateName() + "'");

                            //return true;
                            return wormholePlayer;
                        } else {
                            Stargate targetGate = StargateManager.getStargate(currentGate.getGateDialSignTarget().getGateName());
                            player.sendMessage(String.format(ConfigManager.MessageStrings.gateRemoteActive.toString(), targetGate.getGateName()+" ", " by " + StargateManager.getStargate(targetGate.getSourceGateName()).getLastUsedBy()));
                            player.sendMessage(ConfigManager.MessageStrings.errorHeader.toString() + "Detected Wormhole source " + targetGate.getSourceGateName());

                            // set has received remote message to true
                            wormholePlayer.getProperties().setHasReceivedRemoteActiveMessage(true);

                            //return false;
                            return wormholePlayer;
                        }
                    } else {
                        // TODO: check if this is still needed to catch cause all sign powered gates have now a target after load.
                        player.sendMessage(ConfigManager.MessageStrings.targetInvalid.toString());

                        wormholePlayer.getProperties().setHasReceivedInvalidTargetMessage(true);

                        //return false;
                        return wormholePlayer;
                    }
                } else {
                    player.sendMessage(ConfigManager.MessageStrings.permissionNo.toString());

                    wormholePlayer.getProperties().setHasReceivedNoPermissionMessage(true);
                    //return false;

                    return wormholePlayer;
                }
            } else {
                //Activate Stargate
                player.sendMessage(String.format(ConfigManager.MessageStrings.gateActivated.toString(), currentGate.getGateName() + " "));
                player.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Chevrons locked! \u00A73:: \u00A7B<required> \u00A76[optional]");
                player.sendMessage(ConfigManager.MessageStrings.normalHeader.toString() + "Type \'\u00A7F/dial \u00A7B<GateName> \u00A76[idc]\u00A77\'");

                StargateManager.addActivatedStargate(currentGate);
                currentGate.startActivationTimer(player);
                currentGate.lightStargate(true);

                wormholePlayer.getProperties().setHasActivatedStargate(true);

                currentGate.setLastUsedBy(player.getName());

                //return true;
                return wormholePlayer;
            }
        }
    }

    /**
     * Handle player interact event.
     * 
     * @param event
     *            the event
     * @return true, if successful
     */
    protected static boolean handlePlayerInteractEvent(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        Action clickedBlockAction = event.getAction();
        Player player = event.getPlayer();

        if ((clickedBlock != null) && ((clickedBlock.getType().equals(Material.STONE_BUTTON)) || (clickedBlock.getType().equals(Material.LEVER)))) {
            if (buttonLeverHit(player, clickedBlock, null)) {
                return true;
            }
        } else if ((clickedBlock != null) && (clickedBlock.getType().equals(Material.WALL_SIGN))) {
            Stargate stargate = StargateManager.getGateFromBlock(clickedBlock);
            if (stargate != null) {
                if (WXPermissions.checkWXPermissions(player, stargate, PermissionType.SIGN)) {
                    stargate.setLastUsedBy(player.getName());
                    if (stargate.tryClickTeleportSign(clickedBlock, clickedBlockAction)) {
                        return true;
                    }
                } else {
                    player.sendMessage(ConfigManager.MessageStrings.permissionNo.toString());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Handle player move event.
     * 
     * @param event
     *            the event
     * @return true, if successful
     */
    protected WormholePlayer handlePlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location toLocFinal = event.getTo();
        Block gateBlockFinal = toLocFinal.getWorld().getBlockAt(toLocFinal.getBlockX(), toLocFinal.getBlockY(), toLocFinal.getBlockZ());

        Stargate stargate = StargateManager.getGateFromBlock(gateBlockFinal);

        if ((stargate != null) && (stargate.isGateActive()) && (stargate.getGateTarget() != null) && (gateBlockFinal.getTypeId() == (stargate.isGateCustom()
                ? stargate.getGateCustomPortalMaterial().getId()
                : stargate.getGateShape() != null
                ? stargate.getGateShape().getShapePortalMaterial().getId()
                : Material.STATIONARY_WATER.getId())) 
                && (!WormholePlayerManager.getRegisteredWormholePlayer(player).getProperties(stargate).hasUsedStargate())) {

            WormholePlayer wormholePlayer = WormholePlayerManager.getRegisteredWormholePlayer(player);

            // add stargate to player if its not in there
            wormholePlayer.addStargate(stargate);
            wormholePlayer.setCurrentGateName(stargate.getGateName());

            String gateNetwork;
            if (stargate.getGateNetwork() != null) {
                gateNetwork = stargate.getGateNetwork().getNetworkName();
            } else {
                gateNetwork = "Public";
            }

            WXTLogger.prettyLog(Level.FINE, false, "Player in gate: " + stargate.getGateName() + " gate Active: " + stargate.isGateActive() + " Target Gate: " + stargate.getGateTarget().getGateName() + " Network: " + gateNetwork);


            // Teleport logic
            if (!wormholePlayer.getProperties().hasReceivedIrisLockMessage()) {
                if (ConfigManager.getWormholeUseIsTeleport() && ((stargate.isGateSignPowered() && !WXPermissions.checkWXPermissions(player, stargate, PermissionType.SIGN)) || (!stargate.isGateSignPowered() && !WXPermissions.checkWXPermissions(player, stargate, PermissionType.DIALER)))) {
                    player.sendMessage(ConfigManager.MessageStrings.permissionNo.toString());

                    wormholePlayer.getProperties().setHasPermission(false);
                    return wormholePlayer;
                }

                if (ConfigManager.isUseCooldownEnabled()) {
                    if (StargateRestrictions.isPlayerUseCooldown(player)) {
                        player.sendMessage(ConfigManager.MessageStrings.playerUseCooldownRestricted.toString());
                        player.sendMessage(ConfigManager.MessageStrings.playerUseCooldownWaitTime.toString() + StargateRestrictions.checkPlayerUseCooldownRemaining(player));

                        // set cooldown state for entity
                        wormholePlayer.getProperties().setHasUseCooldown(true);

                        return wormholePlayer;
                    } else {
                        StargateRestrictions.addPlayerUseCooldown(player);
                    }
                }

                if ((stargate.getGateTarget().isGateIrisActive()) && (!wormholePlayer.getProperties().hasReceivedIrisLockMessage())) {
                    player.sendMessage(ConfigManager.MessageStrings.errorHeader.toString() + "Remote Iris is locked!");
                    wormholePlayer.getProperties().setHasReceivedIrisLockMessage(true);
                    int wkbCount = ConfigManager.getWormholeKickbackBlockCount();

                    if (wkbCount > 0) {
                        // teleport the player back two steps
                        // Against zero
                        // X North/South
                        // Y Up/Down
                        // Z West/East
                        // yaw 0 West (+z)
                        // yaw 90 North (-x)
                        // yaw 180 East (-z)
                        // yaw 270 South (+x)
                        player.setNoDamageTicks(5);

                        // get kickback direction
                        PlayerOrientation direction = wormholePlayer.getKickBackDirection(wormholePlayer.getStargate().getGateFacing().getOppositeFace());

                        double pLocX = wormholePlayer.getPlayer().getLocation().getX();
                        double pLocY = wormholePlayer.getPlayer().getLocation().getY();
                        double pLocZ = wormholePlayer.getPlayer().getLocation().getZ();

                        WXTLogger.prettyLog(Level.FINE, false, "PlayerOrientation: " + direction.getName());
                        WXTLogger.prettyLog(Level.FINE, false, "old X:"+pLocX+", Y:"+pLocY+", Z:"+pLocZ);

                        // if needed move them far away
                        switch (direction) {
                            case NORTH:
                                WXTLogger.prettyLog(Level.FINE, false, "NORTH: " + pLocX + " - 2 = " + (pLocX-2d));
                                pLocX -= (double) wkbCount;
                                break;
                            case SOUTH:
                                WXTLogger.prettyLog(Level.FINE, false, "SOUTH: " + pLocX + " + 2 = " + (pLocX+2d));
                                pLocX += (double) wkbCount;
                                break;                        
                            case EAST:
                                WXTLogger.prettyLog(Level.FINE, false, "EAST: " + pLocZ + " - 2 = " + (pLocZ-2d));
                                pLocZ -= (double) wkbCount;
                                break;
                            case WEST:
                                WXTLogger.prettyLog(Level.FINE, false, "WEST: " + pLocZ + " + 2 = " + (pLocZ+2d));
                                pLocY += (double) wkbCount;
                                break;
                        }

                        // find new location for player
                        Location newLoc = new Location(player.getWorld(), pLocX, pLocY, pLocZ, player.getLocation().getYaw(), player.getLocation().getPitch());
                        pLocY = player.getWorld().getHighestBlockYAt(newLoc);

                        if (ConfigManager.getGateTransportMethod()) {
                            event.setTo(newLoc);
                            WXTLogger.prettyLog(Level.FINE, false, "Player was kicked back via event");
                        } else {
                            player.teleport(newLoc);
                            WXTLogger.prettyLog(Level.FINE, false, "Player was kicked back via teleport");

                        }

                        WXTLogger.prettyLog(Level.FINE, false, "new X:"+pLocX+", Y:"+pLocY+", Z:"+pLocZ);
                    }

                    return wormholePlayer;
                }

                Location target = stargate.getGateTarget().getGatePlayerTeleportLocation();
                player.setNoDamageTicks(5);
                if (ConfigManager.getGateTransportMethod()) {
                    event.setTo(target);
                    WXTLogger.prettyLog(Level.FINE, false, "Player was transported via event");
                } else {
                    player.teleport(target);
                    WXTLogger.prettyLog(Level.FINE, false, "Player was transported via teleport");
                }

                if ((target != stargate.getGatePlayerTeleportLocation()) && (!wormholePlayer.getProperties().hasUsedStargate())) {
                    WXTLogger.prettyLog(Level.INFO, false, player.getName() + " used wormhole: " + stargate.getGateName() + " to go to: " + stargate.getGateTarget().getGateName());
                    wormholePlayer.getProperties().setHasUsedStargate(true);
                }

                if (ConfigManager.getTimeoutShutdown() == 0) {
                    stargate.shutdownStargate(true);
                }
            } else {
                WXTLogger.prettyLog(Level.FINE, false, "Player '" + player.getName() + "' has received IRISLOCK_MESSASGE unlocking player.");
                wormholePlayer.removeStargate(stargate);
            }

            return wormholePlayer;
        } else if ((stargate != null) && (WormholePlayerManager.getRegisteredWormholePlayer(player).getProperties(stargate).hasReachedDestination())) {
            // @TODO: can be used for later stargate monitoring
            WXTLogger.prettyLog(Level.FINE, false, "Player '" + player.getName() + "' has safely reached destination.");
            WormholePlayerManager.getRegisteredWormholePlayer(player).removeStargate(stargate);
        }

        return null;
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.player.PlayerListener#onPlayerBucketEmpty(org.bukkit.event.player.PlayerBucketEmptyEvent)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!event.isCancelled()) {
            Stargate stargate = StargateManager.getGateFromBlock(event.getBlockClicked());
            if ((stargate != null) || StargateManager.isBlockInGate(event.getBlockClicked())) {
                event.setCancelled(true);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.player.PlayerListener#onPlayerBucketFill(org.bukkit.event.player.PlayerBucketFillEvent)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!event.isCancelled()) {
            Stargate stargate = StargateManager.getGateFromBlock(event.getBlockClicked());
            if ((stargate != null) || StargateManager.isBlockInGate(event.getBlockClicked())) {
                event.setCancelled(true);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.player.PlayerListener#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            WXTLogger.prettyLog(Level.FINE, false, "InteractEvent was canceled");
            return;
        }
        
        if (event.getClickedBlock() != null) {
            WXTLogger.prettyLog(Level.FINE, false, "Caught Player: \"" + event.getPlayer().getName() + "\" Event Name: \"" + event.getEventName() + "\" Action Type: \"" + event.getAction().toString() + "\" Event Block Type: \"" + event.getClickedBlock().getType().toString() + "\" Event World: \"" + event.getClickedBlock().getWorld().toString() + "\" Event Block: " + event.getClickedBlock().toString() + "\"");

            if (handlePlayerInteractEvent(event)) {
                event.setCancelled(true);
                WXTLogger.prettyLog(Level.FINE, false, "Cancelled Player: \"" + event.getPlayer().getName() + "\" Event Name: \"" + event.getEventName() + "\" Action Type: \"" + event.getAction().toString() + "\" Event Block Type: \"" + event.getClickedBlock().getType().toString() + "\" Event World: \"" + event.getClickedBlock().getWorld().toString() + "\" Event Block: " + event.getClickedBlock().toString() + "\"");
            }
        } else {
            WXTLogger.prettyLog(Level.FINE, false, "Caught and ignored Player: \"" + event.getPlayer().getName() + "\" Event type: \"" + event.getEventName() + "\"");
        }
    }

    /* (non-Javadoc)
     * @see org.bukkit.event.player.PlayerListener#onPlayerMove(org.bukkit.event.player.PlayerMoveEvent)
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        WormholePlayer wormholePlayer = handlePlayerMoveEvent(event);
        Player player = event.getPlayer();

        if (!WormholePlayerManager.isRegistered(player))
            return;

        if ((wormholePlayer != null) && (wormholePlayer.getProperties() != null) && (wormholePlayer.getProperties().hasUsedStargate())) {
            if (ConfigManager.isGateArrivalWelcomeMessageEnabled()) {
            	String message = (!wormholePlayer.getStargate().getGateTarget().getGateMessage().equals("")) ? ConfigManager.MessageStrings.normalHeader + wormholePlayer.getStargate().getGateTarget().getGateMessage() : ConfigManager.MessageStrings.playerUsedStargate.toString();
            	player.sendMessage(message.replace("%name%", player.getDisplayName())
            			           .replace("%gate%", wormholePlayer.getStargate().getGateTarget().getGateName())
            			           .replace("%owner%", wormholePlayer.getStargate().getGateTarget().getGateOwner()));
                /*player.sendMessage(
                        String.format(ConfigManager.MessageStrings.playerUsedStargate.toString(),
                        "Gate " + wormholePlayer.getStargate().getGateTarget().getGateName(),
                        " - created by " + wormholePlayer.getStargate().getGateTarget().getGateOwner()));*/
                WXTLogger.prettyLog(Level.FINE, false, "has received SHOW_GATE_WELCOME_MESSAGE");
            } else{
                WXTLogger.prettyLog(Level.FINE, false, "has disabled SHOW_GATE_WELCOME_MESSAGE");
            }

            wormholePlayer.getProperties().setHasReachedDestination(true);
        }
    }

    /**
     * Called when a player joins a server
     *
     * @param event Relevant event details
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // wrap the player to WormholePlayer
        Player player = event.getPlayer();

        WXTLogger.prettyLog(Level.FINE, false, "Player '" + player.getName() + "' joined the server. Adding player to keyring.");
        WormholePlayerManager.registerPlayer(player);
    }

    /**
     * Called when a player leaves a server
     *
     * @param event Relevant event details
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // remove the player form WormholePlayer keyring
        Player player = event.getPlayer();

        WXTLogger.prettyLog(Level.FINE, false, "Player '" + player.getName() + "' has quit. Removing player from keyring.");
        WormholePlayerManager.unregisterPlayer(player);
    }

    /**
     * Called when a player gets kicked from the server
     *
     * @param event Relevant event details
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerKick(PlayerKickEvent event) {
        // remove the player form WormholePlayer keyring
        Player player = event.getPlayer();

        WXTLogger.prettyLog(Level.FINE, false, "Player '" + player.getName() + "' was kicked. Removing player from keyring.");
        WormholePlayerManager.unregisterPlayer(player);
    }
}
