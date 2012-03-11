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
import de.luricos.bukkit.WormholeXTreme.Wormhole.logic.StargateUpdateRunnable;
import de.luricos.bukkit.WormholeXTreme.Wormhole.logic.StargateUpdateRunnable.ActionToTake;
import de.luricos.bukkit.WormholeXTreme.Wormhole.player.WormholePlayer;
import de.luricos.bukkit.WormholeXTreme.Wormhole.player.WormholePlayerManager;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WXTLogger;
import de.luricos.bukkit.WormholeXTreme.Wormhole.utils.WorldUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * WormholeXtreme Stargate Class/Instance.
 * 
 * @author Ben Echols (Lologarithm)
 * @author Dean Bailey (alron)
 * 
 */
public class Stargate {

    /** The Loaded version, used to determine what version of parser to use. */
    private byte loadedVersion = -1;
    /** The Gate id. */
    private long gateId = -1;
    /** Name of this gate, used to index and target. */
    private String gateName = "";
    /** Message dispalyed to teleported users */
    private String gateMessage = "";
    /** GateName that dialed in */
    private String gateSourceName = null;
    /** Name of person who made the gate. */
    private String gateOwner = null;
    /** Last used by player */
    private String lastUsedBy = null;
    /** Network gate is connected to. */
    private StargateNetwork gateNetwork;
    /**
     * The gateshape that this gate uses.
     * This affects woosh depth and later materials
     */
    private StargateShape gateShape;
    /** The world this stargate is associated with. */
    private World gateWorld;
    /** Is this stargate already active? Can be active remotely and have no target of its own. */
    private boolean gateActive = false;
    /** Has this stargate been recently active?. */
    private boolean gateRecentlyActive = false;
    /** The direction that the stargate faces. */
    private BlockFace gateFacing;
    /** Is the stargate already lit up?. */
    private boolean gateLightsActive = false;
    /** Is activated through sign destination?. */
    private boolean gateSignPowered;
    /** The gate redstone powered. */
    private boolean gateRedstonePowered;
    /** The stargate that is being targeted by this gate. */
    private Stargate gateTarget = null;
    /** The current target on the sign, only used if gateSignPowered is true. */
    private Stargate gateDialSignTarget;
    /** Temp target id to store when loading gates. */
    private long gateTempSignTarget = -1;
    /** The network index the sign is pointing at. */
    private int gateDialSignIndex = 0;
    /** The temporary target stargate id. */
    private long gateTempTargetId = -1;
    /** The Iris deactivation code. */
    private String gateIrisDeactivationCode = "";
    /** Is the iris Active?. */
    private boolean gateIrisActive = false;
    /** The iris default setting. */
    private boolean gateIrisDefaultActive = false;
    /** The Teleport sign, used for selection of stargate target. */
    private Sign gateDialSign;
    /** The location to teleport players to. */
    private Location gatePlayerTeleportLocation;
    /** The location to teleport minecarts to. */
    private Location gateMinecartTeleportLocation;
    /** Location of the Button/Lever that activates this gate. */
    private Block gateDialLeverBlock;
    /** Location of the Button/Lever that activates the iris. */
    private Block gateIrisLeverBlock;
    /** The Teleport sign block. */
    private Block gateDialSignBlock;
    /** Block that toggle the activation state of the gate if nearby redstone is activated. */
    private Block gateRedstoneDialActivationBlock;
    /** Block that will toggle sign target when redstone nearby is activated. */
    private Block gateRedstoneSignActivationBlock;
    /** The gate redstone gate activated block. */
    private Block gateRedstoneGateActivatedBlock;
    /** The Name block holder. Where we place the stargate name sign. */
    private Block gateNameBlockHolder;
    /** The gate activate scheduler task id. */
    private int gateActivateTaskId;
    /** The gate stablish wormhole task id */
    private int gateEstablishWormholeTaskId;
    /** The gate shutdown scheduler task id. */
    private int gateShutdownTaskId;
    /** The gate after shutdown scheduler task id. */
    private int gateAfterShutdownTaskId;
    /** The gate animation step 3d. */
    private int gateAnimationStep3D = 1;
    /** The gate animation step 2d. */
    private int gateAnimationStep2D = 0;
    /** The animation removing. */
    private boolean gateAnimationRemoving = false;
    /** The current_lighting_iteration. */
    private int gateLightingCurrentIteration = 0;
    /** List of all blocks contained in this stargate, including buttons and levers. */
    private final ArrayList<Location> gateStructureBlocks = new ArrayList<Location>();
    /** List of all blocks that that are part of the "portal". */
    private final ArrayList<Location> gatePortalBlocks = new ArrayList<Location>();
    /** List of all blocks that turn on when gate is active. */
    private final ArrayList<ArrayList<Location>> gateLightBlocks = new ArrayList<ArrayList<Location>>();
    /** List of all blocks that woosh in order when gate is active. */
    private final ArrayList<ArrayList<Location>> gateWooshBlocks = new ArrayList<ArrayList<Location>>();
    /** The Animated blocks. */
    private final ArrayList<Block> gateAnimatedBlocks = new ArrayList<Block>();
    /** The gate_order. */
    private final HashMap<Integer, Stargate> gateSignOrder = new HashMap<Integer, Stargate>();
    /** The gate custom. */
    private boolean gateCustom = false;
    /** The gate custom structure material. */
    private Material gateCustomStructureMaterial = null;
    /** The gate custom portal material. */
    private Material gateCustomPortalMaterial = null;
    /** The gate custom light material. */
    private Material gateCustomLightMaterial = null;
    /** The gate custom iris material. */
    private Material gateCustomIrisMaterial = null;
    /** The gate custom woosh ticks. */
    private int gateCustomWooshTicks = -1;
    /** The gate custom light ticks. */
    private int gateCustomLightTicks = -1;
    /** The gate custom woosh depth. */
    private int gateCustomWooshDepth = -1;
    /** The gate custom woosh depth squared. */
    private int gateCustomWooshDepthSquared = -1;
    /** set to true if gate lights finished animation */
    private boolean gateChevronsLocked = false;
    /** set to true after dial to prevent target deactivation of dial gates */
    private boolean gateEstablishedWormhole = false;
    

    /**
     * Instantiates a new stargate.
     */
    public Stargate() {
    }

    /**
     * Animate opening.
     */
    public void animateOpening() {
        final Material wooshMaterial = isGateCustom()
                ? getGateCustomPortalMaterial()
                : getGateShape() != null
                ? getGateShape().getShapePortalMaterial()
                : Material.STATIONARY_WATER;
        final int wooshDepth = isGateCustom()
                ? getGateCustomWooshDepth()
                : getGateShape() != null
                ? getGateShape().getShapeWooshDepth()
                : 0;

        if ((getGateWooshBlocks() != null) && (getGateWooshBlocks().size() > 0)) {
            final ArrayList<Location> wooshBlockStep = getGateWooshBlocks().get(getGateAnimationStep3D());
            if (!isGateAnimationRemoving()) {
                if (wooshBlockStep != null) {
                    for (final Location l : wooshBlockStep) {
                        final Block b = getGateWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                        getGateAnimatedBlocks().add(b);
                        StargateManager.getOpeningAnimationBlocks().put(l, b);
                        b.setType(wooshMaterial);
                    }

                    WXTLogger.prettyLog(Level.FINER, false, getGateName() + " Woosh Adding: " + getGateAnimationStep3D() + " Woosh Block Size: " + wooshBlockStep.size());
                }

                if (getGateWooshBlocks().size() == getGateAnimationStep3D() + 1) {
                    setGateAnimationRemoving(true);
                } else {
                    setGateAnimationStep3D(getGateAnimationStep3D() + 1);
                }
                WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH), isGateCustom()
                        ? getGateCustomWooshTicks()
                        : getGateShape() != null
                        ? getGateShape().getShapeWooshTicks()
                        : 2);
            } else {
                // remove in reverse order, if block is not a portal block!
                if (wooshBlockStep != null) {
                    for (final Location l : wooshBlockStep) {
                        final Block b = getGateWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());

                        //@TODO: oldcode
                        //StargateManager.getOpeningAnimationBlocks().remove(l, b);
                        
                        StargateManager.getOpeningAnimationBlocks().remove(l);
                        getGateAnimatedBlocks().remove(b);
                        if (!StargateManager.isBlockInGate(b)) {
                            b.setType(Material.AIR);
                        }
                    }
                    WXTLogger.prettyLog(Level.FINER, false, getGateName() + " Woosh Removing: " + getGateAnimationStep3D() + " Woosh Block Size: " + wooshBlockStep.size());
                }

                // If this is the last step to animate, we now add all the portal blocks in.
                if (getGateAnimationStep3D() == 1) {
                    setGateAnimationRemoving(false);
                    if (isGateLightsActive() && isGateActive()) {
                        fillGateInterior(wooshMaterial.getId());
                    }
                } else {
                    setGateAnimationStep3D(getGateAnimationStep3D() - 1);
                    WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH), isGateCustom()
                            ? getGateCustomWooshTicks()
                            : getGateShape() != null
                            ? getGateShape().getShapeWooshTicks()
                            : 2);
                }
            }
        } else {
            if ((getGateAnimationStep2D() == 0) && (wooshDepth > 0)) {
                for (final Location block : getGatePortalBlocks()) {
                    final Block r = getGateWorld().getBlockAt(block.getBlockX(), block.getBlockY(), block.getBlockZ()).getRelative(getGateFacing());
                    r.setType(wooshMaterial);
                    getGateAnimatedBlocks().add(r);
                    StargateManager.getOpeningAnimationBlocks().put(r.getLocation(), r);
                }
                setGateAnimationStep2D(getGateAnimationStep2D() + 1);
                WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH), 4);
            } else if (getGateAnimationStep2D() < wooshDepth) {
                final int size = getGateAnimatedBlocks().size();
                final int start = getGatePortalBlocks().size();
                for (int i = (size - start); i < size; i++) {
                    final Block b = getGateAnimatedBlocks().get(i);
                    final Block r = b.getRelative(getGateFacing());
                    r.setType(wooshMaterial);
                    getGateAnimatedBlocks().add(r);
                    StargateManager.getOpeningAnimationBlocks().put(r.getLocation(), r);
                }
                setGateAnimationStep2D(getGateAnimationStep2D() + 1);
                if (getGateAnimationStep2D() == wooshDepth) {
                    WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH), 8);
                } else {
                    WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH), 4);
                }
            } else if (getGateAnimationStep2D() >= wooshDepth) {
                for (int i = 0; i < getGatePortalBlocks().size(); i++) {
                    final int index = getGateAnimatedBlocks().size() - 1;
                    if (index >= 0) {
                        final Block b = getGateAnimatedBlocks().get(index);
                        b.setType(Material.AIR);
                        getGateAnimatedBlocks().remove(index);
                        StargateManager.getOpeningAnimationBlocks().remove(b.getLocation());
                    }
                }
                if (getGateAnimationStep2D() < ((wooshDepth * 2) - 1)) {
                    setGateAnimationStep2D(getGateAnimationStep2D() + 1);
                    WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH), 3);
                } else {
                    setGateAnimationStep2D(0);
                    if (isGateActive()) {
                        fillGateInterior(wooshMaterial.getId());
                    }
                }
            }
        }
    }

    /**
     * Complete gate.
     * 
     * @param name
     *            the name
     * @param idc
     *            the idc
     */
    void completeGate(final String name, final String idc) {
        setGateName(name);

        // 1. Setup Name Sign
        if (getGateNameBlockHolder() != null) {
            setupGateSign(true);
        }
        // 2. Set up Iris stuff
        setIrisDeactivationCode(idc);

        if (isGateRedstonePowered()) {
            setupRedstoneGateActivatedLever(true);
            if (isGateSignPowered()) {
                setupRedstoneDialWire(true);
                setupRedstoneSignDialWire(true);
            }
        }
    }

    /**
     * Delete gate blocks.
     */
    public void deleteGateBlocks() {
        for (final Location bc : getGateStructureBlocks()) {
            final Block b = getGateWorld().getBlockAt(bc.getBlockX(), bc.getBlockY(), bc.getBlockZ());
            b.setType(Material.AIR);
        }
    }

    /**
     * Delete portal blocks.
     */
    public void deletePortalBlocks() {
        for (final Location bc : getGatePortalBlocks()) {
            final Block b = getGateWorld().getBlockAt(bc.getBlockX(), bc.getBlockY(), bc.getBlockZ());
            b.setType(Material.AIR);
        }
    }

    /**
     * Delete teleport sign.
     */
    public void deleteTeleportSign() {
        if ((getGateDialSignBlock() != null) && (getGateDialSign() != null)) {
            final Block teleportSign = getGateDialSignBlock().getRelative(getGateFacing());
            teleportSign.setType(Material.AIR);
        }
    }

    /**
     * This method activates the current stargate as if it had just been dialed.
     * This includes filling the event horizon, canceling any other shutdown events,
     * scheduling the shutdown time and scheduling the WOOSH if enabled.
     * Failed task schedules will cause gate to not activate, fill, or animate.
     */
    private boolean dialStargate() {
        WorldUtils.scheduleChunkLoad(getGatePlayerTeleportLocation().getBlock());
        if (getGateShutdownTaskId() > 0) {
            WormholeXTreme.getScheduler().cancelTask(getGateShutdownTaskId());
        }
        
        if (getGateAfterShutdownTaskId() > 0) {
            WormholeXTreme.getScheduler().cancelTask(getGateAfterShutdownTaskId());
        }

        final int timeout = ConfigManager.getTimeoutShutdown() * 20;
        if (timeout > 0) {
            setGateShutdownTaskId(WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.SHUTDOWN), timeout));
            WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" ShutdownTaskID \"" + getGateShutdownTaskId() + "\" created.");
            if (getGateShutdownTaskId() == -1) {
                shutdownStargate(true);
                WXTLogger.prettyLog(Level.SEVERE, false, "Failed to schedule wormhole shutdown timeout: " + timeout + " Received task id of -1. Wormhole forced closed NOW.");
            }
        }

        if ((getGateShutdownTaskId() > 0) || (timeout == 0)) {
            if (!isGateActive()) {
                setGateActive(true);
                toggleDialLeverState(false);
                toggleRedstoneGateActivatedPower();
                setGateRecentlyActive(false);
            }
            
            if (!isGateLightsActive()) {
                // This function lights, wooshes, and then adds portal material
                lightStargate(true);
            } else {
                setGateChevronsLocked(true);
            }
            
            return true;
        } else {
            WXTLogger.prettyLog(Level.WARNING, false, "No wormhole. No visual events.");
        }
        
        return false;
    }

    /**
     * This method takes in a remote stargate and dials it if it is not active.
     * 
     * @param target
     *            the target stargate
     * @param force
     *            true to force dial the stargate, false to properly check if target gate is not active.
     * @return True if successful, False if remote target is already Active or if there is a failure scheduling stargate
     *         shutdowns.
     */
    public boolean dialStargate(final Stargate target, final boolean force) {
        if (getGateActivateTaskId() > 0) {
            WormholeXTreme.getScheduler().cancelTask(getGateActivateTaskId());
        }

        if (!target.isGateLightsActive() || force) {
            setGateTarget(target);
            
            if (getGateTarget() == null) {
                WXTLogger.prettyLog(Level.WARNING, false, "Target lost! Closing local wormhole for safety percussions.");
                shutdownStargate(true);
                return false;
            }

            this.dialStargate();
            this.getGateTarget().dialStargate();
            
            this.getGateTarget().setSourceGateName(this.getGateName());
            
            this.establishWormhole();
            
            if ((isGateActive()) && (getGateTarget().isGateActive())) {
                return true;
            } else if ((isGateActive()) && (!getGateTarget().isGateActive())) {
                shutdownStargate(true);
                WXTLogger.prettyLog(Level.WARNING, false, "Far wormhole failed to open. Closing local wormhole for safety sake.");
            } else if ((!isGateActive()) && (getGateTarget().isGateActive())) {
                target.shutdownStargate(true);
                WXTLogger.prettyLog(Level.WARNING, false, "Local wormhole failed to open. Closing far end wormhole for safety sake.");
            }
        }

        return false;
    }
    
    /**
     * Establish wormhole between gates
     */
    public void establishWormhole() {
        if (getGateEstablishWormholeTaskId() > 0) {
            WXTLogger.prettyLog(Level.FINER, false, "Wormhole \"" + getGateName() + "\" EstablishWormholeTaskIdID \"" + getGateEstablishWormholeTaskId() + "\" cancelled.");
            WormholeXTreme.getScheduler().cancelTask(getGateEstablishWormholeTaskId());
        }
        
        if ((this.getGateTarget() == null) || (!this.isGateActive()))
            return;

        WXTLogger.prettyLog(Level.FINER, false, "Trying to establish link between '" + this.getGateName() + "' and '" + this.getGateTarget().getGateName() +"'");
        if (this.getGateTarget().getGateChevronsLocked()) {
            this.setWormholeEstablished(true);
            this.getGateTarget().setWormholeEstablished(true);
            WXTLogger.prettyLog(Level.FINER, false, "Chevrons locked on both sides. Starting thread ANIMATE_WOOSH.");
            WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ANIMATE_WOOSH));
            WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this.getGateTarget(), ActionToTake.ANIMATE_WOOSH));
        } else {
            WXTLogger.prettyLog(Level.FINER, false, "Chevrons where not locked on both sides. Restarting thread.");
            setGateEstablishWormholeTaskId(WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.ESTABLISH_WORMHOLE)));
        }
    }

    /**
     * Fill gate interior.
     * 
     * @param typeId
     *            the type id
     */
    public void fillGateInterior(final int typeId) {
        for (final Location bc : getGatePortalBlocks()) {
            final Block b = getGateWorld().getBlockAt(bc.getBlockX(), bc.getBlockY(), bc.getBlockZ());
            b.setTypeId(typeId, false);
        }
    }

//    /**
//     * Fill gate interior.
//     * 
//     * @param material
//     *            the material
//     */
//    public void fillGateInterior(final Material material)
//    {
//        for (final Location bc : getGatePortalBlocks())
//        {
//            final Block b = getGateWorld().getBlockAt(bc.getBlockX(), bc.getBlockY(), bc.getBlockZ());
//            b.setType(material);
//        }
//    }
    
    private int getGateEstablishWormholeTaskId() {
        return gateEstablishWormholeTaskId;
    }
    
    /**
     * Gets the gate activate task id.
     * 
     * @return the gate activate task id
     */
    private int getGateActivateTaskId() {
        return gateActivateTaskId;
    }

    /**
     * Gets the gate after shutdown task id.
     * 
     * @return the gate after shutdown task id
     */
    private int getGateAfterShutdownTaskId() {
        return gateAfterShutdownTaskId;
    }

    /**
     * Gets the gate animated blocks.
     * 
     * @return the gate animated blocks
     */
    private ArrayList<Block> getGateAnimatedBlocks() {
        return gateAnimatedBlocks;
    }

    /**
     * Gets the gate animation step 2d.
     * 
     * @return the gate animation step 2d
     */
    public int getGateAnimationStep2D() {
        return gateAnimationStep2D;
    }

    /**
     * Gets the gate animation step.
     * 
     * @return the gate animation step
     */
    private int getGateAnimationStep3D() {
        return gateAnimationStep3D;
    }

    /**
     * Gets the gate custom iris material.
     * 
     * @return the gate custom iris material
     */
    public Material getGateCustomIrisMaterial() {
        return gateCustomIrisMaterial;
    }

    /**
     * Gets the gate custom light material.
     * 
     * @return the gate custom light material
     */
    public Material getGateCustomLightMaterial() {
        return gateCustomLightMaterial;
    }

    /**
     * Gets the gate custom light ticks.
     * 
     * @return the gate custom light ticks
     */
    public int getGateCustomLightTicks() {
        return gateCustomLightTicks;
    }

    /**
     * Gets the gate custom portal material.
     * 
     * @return the gate custom portal material
     */
    public Material getGateCustomPortalMaterial() {
        return gateCustomPortalMaterial;
    }

    /**
     * Gets the gate custom structure material.
     * 
     * @return the gate custom structure material
     */
    public Material getGateCustomStructureMaterial() {
        return gateCustomStructureMaterial;
    }

    /**
     * Gets the gate custom woosh depth.
     * 
     * @return the gate custom woosh depth
     */
    public int getGateCustomWooshDepth() {
        return gateCustomWooshDepth;
    }

    /**
     * Gets the gate custom woosh depth squared.
     * 
     * @return the gate custom woosh depth squared
     */
    public int getGateCustomWooshDepthSquared() {
        return gateCustomWooshDepthSquared;
    }

    /**
     * Gets the gate custom woosh ticks.
     * 
     * @return the gate custom woosh ticks
     */
    public int getGateCustomWooshTicks() {
        return gateCustomWooshTicks;
    }

    /**
     * Gets the gate activation block.
     * 
     * @return the gate activation block
     */
    public Block getGateDialLeverBlock() {
        return gateDialLeverBlock;
    }

    /**
     * Gets the gate teleport sign.
     * 
     * @return the gate teleport sign
     */
    public synchronized Sign getGateDialSign() {
        return gateDialSign;
    }

    /**
     * Gets the gate teleport sign block.
     * 
     * @return the gate teleport sign block
     */
    public synchronized Block getGateDialSignBlock() {
        return gateDialSignBlock;
    }

    /**
     * Gets the gate sign index.
     * 
     * @return the gate sign index
     */
    public synchronized int getGateDialSignIndex() {
        return gateDialSignIndex;
    }

    /**
     * Gets the gate sign target.
     * 
     * @return the gate sign target
     */
    public Stargate getGateDialSignTarget() {
        return gateDialSignTarget;
    }

    /**
     * Gets the gate facing.
     * 
     * @return the gate facing
     */
    public BlockFace getGateFacing() {
        return gateFacing;
    }

    /**
     * Gets the gate id.
     * 
     * @return the gate id
     */
    public long getGateId() {
        return gateId;
    }

    /**
     * Gets the gate iris deactivation code.
     * 
     * @return the gate iris deactivation code
     */
    public String getGateIrisDeactivationCode() {
        return gateIrisDeactivationCode;
    }

    /**
     * Gets the gate iris activation block.
     * 
     * @return the gate iris activation block
     */
    public Block getGateIrisLeverBlock() {
        return gateIrisLeverBlock;
    }

    /**
     * Gets the gate light blocks.
     * 
     * @return the gate light blocks
     */
    public ArrayList<ArrayList<Location>> getGateLightBlocks() {
        return gateLightBlocks;
    }

    /**
     * Gets the gate lighting current iteration.
     * 
     * @return the gate lighting current iteration
     */
    private int getGateLightingCurrentIteration() {
        return gateLightingCurrentIteration;
    }

    /**
     * Gets the gate minecart teleport location.
     * 
     * @return the gate minecart teleport location
     */
    public Location getGateMinecartTeleportLocation() {
        return gateMinecartTeleportLocation;
    }

    /**
     * Gets the gate name.
     * 
     * @return the gate name
     */
    public String getGateName() {
        return gateName;
    }
    
    /**
     * Gets the welcome message
     * 
     * @return the welcome message
     */
    public String getGateMessage() {
    	return this.gateMessage;
    }

    /**
     * Gets the gate name block holder.
     * 
     * @return the gate name block holder
     */
    public Block getGateNameBlockHolder() {
        return gateNameBlockHolder;
    }

    /**
     * Gets the gate network.
     * 
     * @return the gate network
     */
    public StargateNetwork getGateNetwork() {
        return gateNetwork;
    }

    /**
     * Gets the gate owner.
     * 
     * @return the gate owner
     */
    public String getGateOwner() {
        return gateOwner;
    }

    /**
     * Gets the gate teleport location.
     * 
     * @return the gate teleport location
     */
    public Location getGatePlayerTeleportLocation() {
        return gatePlayerTeleportLocation;
    }

    /**
     * Gets the gate portal blocks.
     * 
     * @return the gate portal blocks
     */
    public ArrayList<Location> getGatePortalBlocks() {
        return gatePortalBlocks;
    }

    /**
     * Gets the gate redstone activation block.
     * 
     * @return the gate redstone activation block
     */
    public Block getGateRedstoneDialActivationBlock() {
        return gateRedstoneDialActivationBlock;
    }

    /**
     * Gets the gate redstone gate activated block.
     * 
     * @return the gate redstone gate activated block
     */
    public Block getGateRedstoneGateActivatedBlock() {
        return gateRedstoneGateActivatedBlock;
    }

    /**
     * Gets the gate redstone dial change block.
     * 
     * @return the gate redstone dial change block
     */
    public Block getGateRedstoneSignActivationBlock() {
        return gateRedstoneSignActivationBlock;
    }

    /**
     * Gets the gate shape.
     * 
     * @return the gate shape
     */
    public StargateShape getGateShape() {
        return gateShape;
    }

    /**
     * Gets the gate shutdown task id.
     * 
     * @return the gate shutdown task id
     */
    private int getGateShutdownTaskId() {
        return gateShutdownTaskId;
    }

    /**
     * Gets the gate sign order.
     * 
     * @return the gate sign order
     */
    private HashMap<Integer, Stargate> getGateSignOrder() {
        return gateSignOrder;
    }

    /**
     * Gets the gate structure blocks.
     * 
     * @return the gate structure blocks
     */
    public ArrayList<Location> getGateStructureBlocks() {
        return gateStructureBlocks;
    }

    /**
     * Gets the gate target.
     * 
     * @return the gate target
     */
    public Stargate getGateTarget() {
        return gateTarget;
    }

    /**
     * Gets the gate temp sign target.
     * 
     * @return the gate temp sign target
     */
    long getGateTempSignTarget() {
        return gateTempSignTarget;
    }

    /**
     * Gets the gate temp target id.
     * 
     * @return the gate temp target id
     */
    long getGateTempTargetId() {
        return gateTempTargetId;
    }

    /**
     * Gets the gate woosh blocks.
     * 
     * @return the gate woosh blocks
     */
    public ArrayList<ArrayList<Location>> getGateWooshBlocks() {
        return gateWooshBlocks;
    }

    /**
     * Gets the gate world.
     * 
     * @return the gate world
     */
    public World getGateWorld() {
        return gateWorld;
    }

    /**
     * Gets the loaded version.
     * 
     * @return the loaded version
     */
    public byte getLoadedVersion() {
        return loadedVersion;
    }

    /**
     * Checks if is gate active.
     * 
     * @return true, if is gate active
     */
    public boolean isGateActive() {
        return this.gateActive;
    }
    
    /**
     * Return chevronsLocked status
     * 
     * @return true if gate is lightened up
     */
    public boolean getGateChevronsLocked() {
        return this.gateChevronsLocked;
    }

    /**
     * Checks if is gate animation removing.
     * 
     * @return true, if is gate animation removing
     */
    private boolean isGateAnimationRemoving() {
        return gateAnimationRemoving;
    }

    /**
     * Checks if is gate custom.
     * 
     * @return true, if is gate custom
     */
    public boolean isGateCustom() {
        return gateCustom;
    }

    /**
     * Checks if is gate iris active.
     * 
     * @return true, if is gate iris active
     */
    public boolean isGateIrisActive() {
        return gateIrisActive;
    }

    /**
     * Checks if is gate iris default active.
     * 
     * @return true, if is gate iris default active
     */
    private boolean isGateIrisDefaultActive() {
        return gateIrisDefaultActive;
    }

    /**
     * Checks if is gate lit.
     * 
     * @return true, if is gate lit
     */
    public boolean isGateLightsActive() {
        return gateLightsActive;
    }

    /**
     * Checks if is gate recently active.
     * 
     * @return true, if is gate recently active
     */
    public boolean isGateRecentlyActive() {
        return gateRecentlyActive;
    }

    /**
     * Checks if is gate redstone powered.
     * 
     * @return true, if is gate redstone powered
     */
    public boolean isGateRedstonePowered() {
        return gateRedstonePowered;
    }

    /**#
     * Checks if is gate sign powered.
     * 
     * @return true, if is gate sign powered
     */
    public boolean isGateSignPowered() {
        return gateSignPowered;
    }
    
    public boolean isWormholeEstablished() {
        return gateEstablishedWormhole;
    }
    
    /**
     * Set Wormhole established connection status
     * @param established
     */
    public void setWormholeEstablished(boolean established) {
        this.gateEstablishedWormhole = established;
    }

    /**
     * Light or darken stargate and kick off woosh animation on active stargates.
     * 
     * @param on
     *            true to light, false to darken.
     */
    public void lightStargate(final boolean on) {
        if (on) {
            WXTLogger.prettyLog(Level.FINER, false, "Lighting up Order: " + getGateLightingCurrentIteration());
            if (getGateLightingCurrentIteration() == 0) {
                this.setGateLightsActive(true);
                this.setGateChevronsLocked(false);
            } else if (!isGateLightsActive()) {
                this.lightStargate(false);
                this.setGateLightingCurrentIteration(0);
                return;
            }
            
            this.setGateLightingCurrentIteration(getGateLightingCurrentIteration() + 1);
            // Light up blocks
            if (this.getGateLightBlocks() != null) {
                if ((this.getGateLightBlocks().size() > 0) && (this.getGateLightBlocks().get(this.getGateLightingCurrentIteration()) != null)) {
                    for (final Location l : this.getGateLightBlocks().get(this.getGateLightingCurrentIteration())) {
                        final Block b = this.getGateWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                        b.setType(this.isGateCustom()
                                ? this.getGateCustomLightMaterial()
                                : this.getGateShape() != null
                                ? this.getGateShape().getShapeLightMaterial()
                                : Material.GLOWSTONE);
                    }
                }

                if (this.getGateLightingCurrentIteration() >= this.getGateLightBlocks().size() - 1) {
                    // Reset back to start
                    this.setGateLightingCurrentIteration(0);
                    this.setGateChevronsLocked(true);
                } else {
                    // Keep lighting
                    WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.LIGHTUP), this.isGateCustom()
                            ? this.getGateCustomLightTicks()
                            : this.getGateShape() != null
                            ? this.getGateShape().getShapeLightTicks()
                            : 2);
                }
            }
        } else {
            this.setGateLightsActive(false);
            this.setGateChevronsLocked(false);
            
            // Remove Light Up Blocks
            if (this.getGateLightBlocks() != null) {
                for (int i = 0; i < this.getGateLightBlocks().size(); i++) {
                    if (this.getGateLightBlocks().get(i) != null) {
                        for (final Location l : this.getGateLightBlocks().get(i)) {
                            final Block b = this.getGateWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                            b.setType(this.isGateCustom()
                                    ? this.getGateCustomStructureMaterial()
                                    : this.getGateShape() != null
                                    ? this.getGateShape().getShapeStructureMaterial()
                                    : Material.OBSIDIAN);
                        }
                    }
                }
            }
        }
    }

    /**
     * Reset sign.
     * 
     * @param teleportSign
     *            the teleport sign
     */
    public void resetSign(final boolean teleportSign) {
        if (!teleportSign)
            return;

        getGateDialSignBlock().setTypeIdAndData(Material.WALL_SIGN.getId(), WorldUtils.getSignFacingByteFromBlockFace(getGateFacing()), false);
        setGateDialSign((Sign) getGateDialSignBlock().getState());
        getGateDialSign().setLine(0, getGateName());
        if (getGateNetwork() != null) {
            getGateDialSign().setLine(1, getGateNetwork().getNetworkName());
        } else {
            getGateDialSign().setLine(1, "");
        }
        getGateDialSign().setLine(2, "");
        getGateDialSign().setLine(3, "");
        getGateDialSign().update(true);
    }

    /**
     * Reset teleport sign.
     */
    public void resetTeleportSign() {
        if ((getGateDialSignBlock() != null) && (getGateDialSign() != null)) {
            getGateDialSignBlock().setTypeId(0);
            WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.DIAL_SIGN_RESET), 2);
        }
    }

    /**
     * Sets the gate activate task id.
     * 
     * @param gateActivateTaskId the new gate activate task id
     */
    private void setGateActivateTaskId(final int gateActivateTaskId) {
        this.gateActivateTaskId = gateActivateTaskId;
    }
    
    /**
     * Set the gate establish wormhole task id
     * 
     * @param gateEstablishWormholeTaskId the new gate activate task id
     */
    private void setGateEstablishWormholeTaskId(int gateEstablishWormholeTaskId) {
        this.gateEstablishWormholeTaskId = gateEstablishWormholeTaskId;
    }

    /**
     * Sets the gate active.
     * 
     * @param gateActive
     *            the new gate active
     */
    public void setGateActive(final boolean gateActive) {
        this.gateActive = gateActive;
    }

    /**
     * Sets the gate after shutdown task id.
     * 
     * @param gateAfterShutdownTaskId
     *            the new gate after shutdown task id
     */
    private void setGateAfterShutdownTaskId(final int gateAfterShutdownTaskId) {
        this.gateAfterShutdownTaskId = gateAfterShutdownTaskId;
    }

    /**
     * Sets the gate animation removing.
     * 
     * @param gateAnimationRemoving
     *            the new gate animation removing
     */
    private void setGateAnimationRemoving(final boolean gateAnimationRemoving) {
        this.gateAnimationRemoving = gateAnimationRemoving;
    }

    /**
     * Sets the gate animation step 2d.
     * 
     * @param gateAnimationStep2D
     *            the new gate animation step 2d
     */
    public void setGateAnimationStep2D(final int gateAnimationStep2D) {
        this.gateAnimationStep2D = gateAnimationStep2D;
    }

    /**
     * Sets the gate animation step.
     * 
     * @param gateAnimationStep3D
     *            the new gate animation step
     */
    private void setGateAnimationStep3D(final int gateAnimationStep3D) {
        this.gateAnimationStep3D = gateAnimationStep3D;
    }

    /**
     * Sets the gate custom.
     * 
     * @param gateCustom
     *            the new gate custom
     */
    public void setGateCustom(final boolean gateCustom) {
        this.gateCustom = gateCustom;
    }

    /**
     * Sets the gate custom iris material.
     * 
     * @param gateCustomIrisMaterial
     *            the new gate custom iris material
     */
    public void setGateCustomIrisMaterial(final Material gateCustomIrisMaterial) {
        this.gateCustomIrisMaterial = gateCustomIrisMaterial;
    }

    /**
     * Sets the gate custom light material.
     * 
     * @param gateCustomLightMaterial
     *            the new gate custom light material
     */
    public void setGateCustomLightMaterial(final Material gateCustomLightMaterial) {
        this.gateCustomLightMaterial = gateCustomLightMaterial;
    }

    /**
     * Sets the gate custom light ticks.
     * 
     * @param gateCustomLightTicks
     *            the new gate custom light ticks
     */
    public void setGateCustomLightTicks(final int gateCustomLightTicks) {
        this.gateCustomLightTicks = gateCustomLightTicks;
    }

    /**
     * Sets the gate custom portal material.
     * 
     * @param gateCustomPortalMaterial
     *            the new gate custom portal material
     */
    public void setGateCustomPortalMaterial(final Material gateCustomPortalMaterial) {
        this.gateCustomPortalMaterial = gateCustomPortalMaterial;
    }

    /**
     * Sets the gate custom structure material.
     * 
     * @param gateCustomStructureMaterial
     *            the new gate custom structure material
     */
    public void setGateCustomStructureMaterial(final Material gateCustomStructureMaterial) {
        this.gateCustomStructureMaterial = gateCustomStructureMaterial;
    }

    /**
     * Sets the gate custom woosh depth.
     * 
     * @param gateCustomWooshDepth the new gate custom woosh depth
     */
    public void setGateCustomWooshDepth(final int gateCustomWooshDepth) {
        this.gateCustomWooshDepth = gateCustomWooshDepth;
    }

    /**
     * Sets the gate custom woosh depth squared.
     * 
     * @param gateCustomWooshDepthSquared the new gate custom woosh depth squared
     */
    public void setGateCustomWooshDepthSquared(final int gateCustomWooshDepthSquared) {
        this.gateCustomWooshDepthSquared = gateCustomWooshDepthSquared;
    }

    /**
     * Sets the gate custom woosh ticks.
     * 
     * @param gateCustomWooshTicks
     *            the new gate custom woosh ticks
     */
    public void setGateCustomWooshTicks(final int gateCustomWooshTicks) {
        this.gateCustomWooshTicks = gateCustomWooshTicks;
    }

    /**
     * Sets the gate activation block.
     * 
     * @param gateDialLeverBlock
     *            the new gate dial lever block
     */
    public void setGateDialLeverBlock(final Block gateDialLeverBlock) {
        this.gateDialLeverBlock = gateDialLeverBlock;
    }

    /**
     * Sets the gate teleport sign.
     * 
     * @param gateDialSign
     *            the new gate dial sign
     */
    public synchronized void setGateDialSign(final Sign gateDialSign) {
        this.gateDialSign = gateDialSign;
    }

    /**
     * Sets the gate teleport sign block.
     * 
     * @param gateDialSignBlock
     *            the new gate dial sign block
     */
    public synchronized void setGateDialSignBlock(final Block gateDialSignBlock) {
        this.gateDialSignBlock = gateDialSignBlock;
    }

    /**
     * Sets the gate sign index.
     * 
     * @param gateDialSignIndex
     *            the new gate dial sign index
     */
    public synchronized void setGateDialSignIndex(final int gateDialSignIndex) {
        this.gateDialSignIndex = gateDialSignIndex;
    }

    /**
     * Sets the gate sign target.
     * 
     * @param gateDialSignTarget
     *            the new gate dial sign target
     */
    protected void setGateDialSignTarget(final Stargate gateDialSignTarget) {
        this.gateDialSignTarget = gateDialSignTarget;
    }

    /**
     * Sets the gate facing.
     * 
     * @param gateFacing
     *            the new gate facing
     */
    public void setGateFacing(final BlockFace gateFacing) {
        this.gateFacing = gateFacing;
    }

    /**
     * Sets the gate id.
     * 
     * @param gateId
     *            the new gate id
     */
    void setGateId(final long gateId) {
        this.gateId = gateId;
    }

    /**
     * Sets the gate iris active.
     * 
     * @param gateIrisActive
     *            the new gate iris active
     */
    public void setGateIrisActive(final boolean gateIrisActive) {
        this.gateIrisActive = gateIrisActive;
    }

    /**
     * Sets the gate iris deactivation code.
     * 
     * @param gateIrisDeactivationCode
     *            the new gate iris deactivation code
     */
    public void setGateIrisDeactivationCode(final String gateIrisDeactivationCode) {
        this.gateIrisDeactivationCode = gateIrisDeactivationCode;
    }
    
    /**
     * Sets the gate welcome message<br/>
     * Placeholder:<br/>
     * <ul>
     *   <li>%name% - Name of the teleported</li>
     *   <li>%gate% - Name of the gate</li>
     *   <li>%owner% - Name of the owner</li>
     * </ul>
     */
    public void setGateMessage(final String gateMessage) {
    	this.gateMessage = gateMessage;
    }

    /**
     * Sets the gate iris default active.
     * 
     * @param gateIrisDefaultActive
     *            the new gate iris default active
     */
    public void setGateIrisDefaultActive(final boolean gateIrisDefaultActive) {
        this.gateIrisDefaultActive = gateIrisDefaultActive;
    }

    /**
     * Sets the gate iris activation block.
     * 
     * @param gateIrisLeverBlock
     *            the new gate iris lever block
     */
    public void setGateIrisLeverBlock(final Block gateIrisLeverBlock) {
        this.gateIrisLeverBlock = gateIrisLeverBlock;
    }

    /**
     * Sets the gate lighting current iteration.
     * 
     * @param gateLightingCurrentIteration
     *            the new gate lighting current iteration
     */
    private void setGateLightingCurrentIteration(final int gateLightingCurrentIteration) {
        this.gateLightingCurrentIteration = gateLightingCurrentIteration;
    }

    /**
     * Sets the gate lit.
     * 
     * @param gateLightsActive
     *            the new gate lights active
     */
    public void setGateLightsActive(final boolean gateLightsActive) {
        this.gateLightsActive = gateLightsActive;
    }

    /**
     * Sets the gate minecart teleport location.
     * 
     * @param gateMinecartTeleportLocation
     *            the new gate minecart teleport location
     */
    public void setGateMinecartTeleportLocation(final Location gateMinecartTeleportLocation) {
        this.gateMinecartTeleportLocation = gateMinecartTeleportLocation;
    }

    /**
     * Sets the gate name.
     * 
     * @param gateName
     *            the new gate name
     */
    public void setGateName(final String gateName) {
        this.gateName = gateName;
    }

    /**
     * Sets the gate name block holder.
     * 
     * @param gateNameBlockHolder
     *            the new gate name block holder
     */
    public void setGateNameBlockHolder(final Block gateNameBlockHolder) {
        this.gateNameBlockHolder = gateNameBlockHolder;
    }

    /**
     * Sets the gate network.
     * 
     * @param gateNetwork
     *            the new gate network
     */
    public void setGateNetwork(final StargateNetwork gateNetwork) {
        this.gateNetwork = gateNetwork;
    }

    /**
     * Sets the gate owner.
     * 
     * @param gateOwner
     *            the new gate owner
     */
    public void setGateOwner(final String gateOwner) {
        this.gateOwner = gateOwner;
    }

    /**
     * Sets the gate teleport location.
     * 
     * @param gatePlayerTeleportLocation
     *            the new gate player teleport location
     */
    public void setGatePlayerTeleportLocation(final Location gatePlayerTeleportLocation) {
        this.gatePlayerTeleportLocation = gatePlayerTeleportLocation;
    }

    /**
     * Sets the gate recently active.
     * 
     * @param gateRecentlyActive
     *            the new gate recently active
     */
    private void setGateRecentlyActive(final boolean gateRecentlyActive) {
        this.gateRecentlyActive = gateRecentlyActive;
    }

    /**
     * Sets the gate redstone activation block.
     * 
     * @param gateRedstoneDialActivationBlock
     *            the new gate redstone dial activation block
     */
    public void setGateRedstoneDialActivationBlock(final Block gateRedstoneDialActivationBlock) {
        this.gateRedstoneDialActivationBlock = gateRedstoneDialActivationBlock;
    }

    /**
     * Sets the gate redstone gate activated block.
     * 
     * @param gateRedstoneGateActivatedBlock
     *            the new gate redstone gate activated block
     */
    public void setGateRedstoneGateActivatedBlock(final Block gateRedstoneGateActivatedBlock) {
        this.gateRedstoneGateActivatedBlock = gateRedstoneGateActivatedBlock;
    }

    /**
     * Sets the gate redstone powered.
     * 
     * @param gateRedstonePowered
     *            the new gate redstone powered
     */
    public void setGateRedstonePowered(final boolean gateRedstonePowered) {
        this.gateRedstonePowered = gateRedstonePowered;
    }

    /**
     * Sets the gate redstone dial change block.
     * 
     * @param gateRedstoneSignActivationBlock
     *            the new gate redstone sign activation block
     */
    public void setGateRedstoneSignActivationBlock(final Block gateRedstoneSignActivationBlock) {
        this.gateRedstoneSignActivationBlock = gateRedstoneSignActivationBlock;
    }

    /**
     * Sets the gate shape.
     * 
     * @param gateShape
     *            the new gate shape
     */
    public void setGateShape(final StargateShape gateShape) {
        this.gateShape = gateShape;
    }

    /**
     * Sets the gate shutdown task id.
     * 
     * @param gateShutdownTaskId
     *            the new gate shutdown task id
     */
    private void setGateShutdownTaskId(final int gateShutdownTaskId) {
        this.gateShutdownTaskId = gateShutdownTaskId;
    }

    /**
     * Sets the gate sign powered.
     * 
     * @param gateSignPowered
     *            the new gate sign powered
     */
    public void setGateSignPowered(final boolean gateSignPowered) {
        this.gateSignPowered = gateSignPowered;
    }

    /**
     * Sets the gate target.
     * 
     * @param gateTarget
     *            the new gate target
     */
    private void setGateTarget(final Stargate gateTarget) {
        this.gateTarget = gateTarget;
    }

    /**
     * Sets the gate temp sign target.
     * 
     * @param gateTempSignTarget
     *            the new gate temp sign target
     */
    public void setGateTempSignTarget(final long gateTempSignTarget) {
        this.gateTempSignTarget = gateTempSignTarget;
    }

    /**
     * Sets the gate temp target id.
     * 
     * @param gateTempTargetId
     *            the new gate temp target id
     */
    public void setGateTempTargetId(final long gateTempTargetId) {
        this.gateTempTargetId = gateTempTargetId;
    }

    /**
     * Sets the gate world.
     * 
     * @param gateWorld
     *            the new gate world
     */
    public void setGateWorld(final World gateWorld) {
        this.gateWorld = gateWorld;
    }
    
    public String getLastUsedBy() {
        return this.lastUsedBy;
    }
    
    public void setLastUsedBy(Player player) {
        this.setLastUsedBy(player.getName());
    }
    
    public void setLastUsedBy(String playerName) {
        this.lastUsedBy = playerName;
    }
    
    public String getSourceGateName() {
        return this.gateSourceName;
    }
    
    public void setSourceGateName(String gateName) {
        this.gateSourceName = gateName;
    }
    
    public void setGateChevronsLocked(boolean locked) {
        this.gateChevronsLocked = locked;
    }
    
    public boolean gateChevronsLocked() {
        return this.gateChevronsLocked;
    }    

    /**
     * Sets the iris deactivation code.
     * 
     * @param idc
     *            the idc
     */
    public void setIrisDeactivationCode(final String idc) {
        // If empty string make sure to make lever area air instead of lever.
        if ((idc != null) && !idc.equals("")) {
            setGateIrisDeactivationCode(idc);
            setupIrisLever(true);
        } else {
            setIrisState(false);
            setupIrisLever(false);
            setGateIrisDeactivationCode("");
        }
    }

    /**
     * This method sets the iris state and toggles the iris lever.
     * Smart enough to know if the gate is active and set the proper
     * material in its interior.
     * 
     * @param irisactive
     *            true for iris on, false for off.
     */
    private void setIrisState(final boolean irisactive) {
        setGateIrisActive(irisactive);
        fillGateInterior(isGateIrisActive()
                ? isGateCustom()
                ? getGateCustomIrisMaterial().getId()
                : getGateShape() != null
                ? getGateShape().getShapeIrisMaterial().getId()
                : 1
                : isGateActive()
                ? isGateCustom()
                ? getGateCustomPortalMaterial().getId()
                : getGateShape() != null
                ? getGateShape().getShapePortalMaterial().getId()
                : 9
                : 0);
        if ((getGateIrisLeverBlock() != null) && (getGateIrisLeverBlock().getTypeId() == 69)) {
            getGateIrisLeverBlock().setData(WorldUtils.getLeverToggleByte(getGateIrisLeverBlock().getData(), isGateIrisActive()));
        }
    }

    /**
     * Sets the loaded version.
     * 
     * @param loadedVersion
     *            the new loaded version
     */
    public void setLoadedVersion(final byte loadedVersion) {
        this.loadedVersion = loadedVersion;
    }

    /**
     * Setup or remove gate name sign.
     * 
     * @param create
     *            true to create, false to destroy
     */
    public void setupGateSign(final boolean create) {
        if (getGateNameBlockHolder() != null) {
            if (create) {
                final Block nameSign = getGateNameBlockHolder().getRelative(getGateFacing());
                getGateStructureBlocks().add(nameSign.getLocation());
                nameSign.setTypeIdAndData(Material.WALL_SIGN.getId(), WorldUtils.getSignFacingByteFromBlockFace(getGateFacing()), false);
                final Sign sign = (Sign) nameSign.getState();
                sign.setLine(0, "-" + getGateName() + "-");

                if (getGateNetwork() != null) {
                    sign.setLine(1, "N:" + getGateNetwork().getNetworkName());
                }

                if (getGateOwner() != null) {
                    sign.setLine(2, "O:" + getGateOwner());
                }
                sign.update(true);

            } else {
                final Block nameSign = getGateNameBlockHolder().getRelative(getGateFacing());
                if (nameSign.getType().equals(Material.WALL_SIGN)) {
                    getGateStructureBlocks().remove(nameSign.getLocation());
                    nameSign.setTypeId(0);
                }
            }
        }
    }

    /**
     * Setup or remove IRIS control lever.
     * 
     * @param create
     *            true for create, false for destroy.
     */
    public void setupIrisLever(final boolean create) {
        if ((getGateIrisLeverBlock() == null) && (getGateShape() != null) && !(getGateShape() instanceof Stargate3DShape)) {
            setGateIrisLeverBlock(getGateDialLeverBlock().getRelative(BlockFace.DOWN));
        }
        if (getGateIrisLeverBlock() != null) {
            if (create) {
                getGateStructureBlocks().add(getGateIrisLeverBlock().getLocation());
                getGateIrisLeverBlock().setTypeIdAndData(Material.LEVER.getId(), WorldUtils.getLeverFacingByteFromBlockFace(getGateFacing()), false);
            } else {
                if (getGateIrisLeverBlock().getType().equals(Material.LEVER)) {
                    getGateStructureBlocks().remove(getGateIrisLeverBlock().getLocation());
                    getGateIrisLeverBlock().setTypeId(0);
                }
            }
        }
    }

    /**
     * Sets the up redstone connections (create or delete).
     * 
     * @param create
     *            true to create redstone connections, false to delete.
     */
    public void setupRedstone(final boolean create) {
        if (isGateSignPowered()) {
            setupRedstoneDialWire(create);
            setupRedstoneSignDialWire(create);
        }
        setupRedstoneGateActivatedLever(create);
    }

    /**
     * Sets the up redstone dial wire.
     * 
     * @param create
     *            the new redstone dial wire
     */
    private void setupRedstoneDialWire(final boolean create) {
        if (getGateRedstoneDialActivationBlock() != null) {
            if (create) {
                getGateStructureBlocks().add(getGateRedstoneDialActivationBlock().getLocation());
                getGateRedstoneDialActivationBlock().setTypeId(55);
            } else {
                if (getGateRedstoneGateActivatedBlock().getTypeId() == 55) {
                    getGateStructureBlocks().remove(getGateRedstoneDialActivationBlock().getLocation());
                    getGateRedstoneDialActivationBlock().setTypeId(0);
                }
            }
        }
    }

    /**
     * Sets the up redstone gate activated Lever.
     * 
     * @param create
     *            the new redstone gate activated lever
     */
    private void setupRedstoneGateActivatedLever(final boolean create) {
        if (getGateRedstoneGateActivatedBlock() != null) {
            if (create) {
                getGateStructureBlocks().add(getGateRedstoneGateActivatedBlock().getLocation());
                getGateRedstoneGateActivatedBlock().setTypeIdAndData(Material.LEVER.getId(), (byte) 0x5, false);
            } else {
                if (getGateRedstoneGateActivatedBlock().getType().equals(Material.LEVER)) {
                    getGateStructureBlocks().remove(getGateRedstoneGateActivatedBlock().getLocation());
                    getGateRedstoneGateActivatedBlock().setTypeId(0);
                }
            }
        }
    }

    /**
     * Sets the up redstone sign dial wire.
     * 
     * @param create
     *            the new redstone sign dial wire
     */
    private void setupRedstoneSignDialWire(final boolean create) {
        if (getGateRedstoneSignActivationBlock() != null) {
            if (create) {
                getGateStructureBlocks().add(getGateRedstoneSignActivationBlock().getLocation());
                getGateRedstoneSignActivationBlock().setTypeId(55);
            } else {
                if (getGateRedstoneGateActivatedBlock().getTypeId() == 55) {
                    getGateStructureBlocks().remove(getGateRedstoneSignActivationBlock().getLocation());
                    getGateRedstoneSignActivationBlock().setTypeId(0);
                }
            }
        }
    }

    /**
     * Shutdown stargate.
     * 
     * @param timer
     *            true if we want to spawn after shutdown timer.
     */
    public void shutdownStargate(final boolean timer) {
        if (this.getGateShutdownTaskId() > 0) {
            WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" ShutdownTaskID \"" + getGateShutdownTaskId() + "\" cancelled.");
            WormholeXTreme.getScheduler().cancelTask(getGateShutdownTaskId());
            this.setGateShutdownTaskId(-1);
        }

        if (this.getGateTarget() != null) {
            this.getGateTarget().shutdownStargate(true);
            this.getGateTarget().setSourceGateName(null);
        }

        this.setGateTarget(null);
        if (timer) {
            this.setGateRecentlyActive(true);
        }
        
        this.setGateActive(false);
        this.lightStargate(false);
        this.setWormholeEstablished(false);
        this.setSourceGateName(null);
        
        this.toggleDialLeverState(false);
        this.toggleRedstoneGateActivatedPower();
        
        // Only set back to air if iris isn't on.
        // If the iris should be on, we will make it that way.
        if (this.isGateIrisDefaultActive()) {
            this.setIrisState(isGateIrisDefaultActive());
        } else if (!this.isGateIrisActive()) {
            this.fillGateInterior(0);
        }

        if (timer) {
            this.startAfterShutdownTimer();
        }

        WorldUtils.scheduleChunkUnload(getGatePlayerTeleportLocation().getBlock());
        
        // remove activated Stargate
        StargateManager.removeActivatedStargate(this.getGateName());
        if (WormholePlayerManager.findPlayerByGateName(this.getGateName()) != null)
            WormholePlayerManager.findPlayerByGateName(this.getGateName()).removeStargate(this.getGateName());
    }

    /**
     * Start activation timer.
     * 
     * @param p
     *            the p
     */
    public void startActivationTimer(final Player p) {
        if (getGateActivateTaskId() > 0) {
            WormholeXTreme.getScheduler().cancelTask(getGateActivateTaskId());
        }

        final int timeout = ConfigManager.getTimeoutActivate() * 20;
        setGateActivateTaskId(WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.DEACTIVATE), timeout));
        WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" ActivateTaskID \"" + getGateActivateTaskId() + "\" created.");
    }

    /**
     * After shutdown of stargate, spawn off task to set RecentActive = false;
     * This way we can depend on RecentActive for gate fire/lava protection.
     */
    private void startAfterShutdownTimer() {
        if (getGateAfterShutdownTaskId() > 0) {
            WormholeXTreme.getScheduler().cancelTask(getGateAfterShutdownTaskId());
        }
        final int timeout = 60;
        setGateAfterShutdownTaskId(WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.AFTERSHUTDOWN), timeout));
        WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" AfterShutdownTaskID \"" + getGateAfterShutdownTaskId() + "\" created.");
        if (getGateAfterShutdownTaskId() == -1) {
            WXTLogger.prettyLog(Level.SEVERE, false, "Failed to schdule wormhole after shutdown, received task id of -1.");
            setGateRecentlyActive(false);
        }
    }

    /**
     * Stop activation timer.
     * 
     */
    public void stopActivationTimer() {
        if (getGateActivateTaskId() > 0) {
            WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" ActivateTaskID \"" + getGateActivateTaskId() + "\" cancelled.");
            WormholeXTreme.getScheduler().cancelTask(getGateActivateTaskId());
            setGateActivateTaskId(-1);
        }
    }

    /**
     * After shutdown stargate.
     */
    public void stopAfterShutdownTimer() {
        if (getGateAfterShutdownTaskId() > 0) {
            WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" AfterShutdownTaskID \"" + getGateAfterShutdownTaskId() + "\" cancelled.");
            WormholeXTreme.getScheduler().cancelTask(getGateAfterShutdownTaskId());
            setGateAfterShutdownTaskId(-1);
        }
        setGateRecentlyActive(false);
    }

    /**
     * Teleport sign clicked.
     */
    public void dialSignClicked() {
        this.dialSignClicked(null);
    }

    public void dialSignClicked(Action eventAction) {
        synchronized (getGateNetwork().getNetworkGateLock()) {
            //@TODO check if this is still needed
            getGateDialSignBlock().setTypeIdAndData(Material.WALL_SIGN.getId(), WorldUtils.getSignFacingByteFromBlockFace(getGateFacing()), false);
            setGateDialSign((Sign) getGateDialSignBlock().getState());
            getGateDialSign().setLine(0, "-" + getGateName() + "-");
            
            String lineMarkerS = ">" + ChatColor.GREEN;
            String lineMarkerE = ChatColor.BLACK + "<";

            if (getGateNetwork().getNetworkSignGateList().size() == 0) {
                getGateDialSign().setLine(1, "");
                getGateDialSign().setLine(2, ChatColor.DARK_RED + "No Other Gates" + ChatColor.BLACK);
                getGateDialSign().setLine(3, "");
                getGateDialSign().update();
                setGateDialSignTarget(null);
                return;
            }

            if (getGateDialSignIndex() == -1) {
                setGateDialSignIndex(0);
            }
            
            
            int direction = 1;
            if ((eventAction != null) && (eventAction.equals(Action.RIGHT_CLICK_BLOCK))) {
                direction = -1;
            }
            
            getGateSignOrder().clear();
            int orderIndex = 1;

            // fetch only three lines
            for (int i = 0; i < 3; i++) {
                if (getGateDialSignIndex() == getGateNetwork().getNetworkSignGateList().size()) {
                    setGateDialSignIndex(0);
                }

                if (getGateDialSignIndex() < 0) {
                    setGateDialSignIndex(getGateNetwork().getNetworkSignGateList().size() - 1);
                }

                if (getGateNetwork().getNetworkSignGateList().get(getGateDialSignIndex()).getGateName().equals(getGateName())) {
                    setGateDialSignIndex(getGateDialSignIndex() + direction);

                    if (getGateDialSignIndex() == getGateNetwork().getNetworkSignGateList().size()) {
                        setGateDialSignIndex(0);
                    }
                }

                if (getGateDialSignIndex() >= 0) {
                    getGateSignOrder().put(orderIndex, getGateNetwork().getNetworkSignGateList().get(getGateDialSignIndex()));
                    orderIndex++;

                    setGateDialSignIndex(getGateDialSignIndex() + direction);
                }
            }

            String line1 = "";
            String line2 = lineMarkerS + getGateSignOrder().get(2).getGateName() + lineMarkerE;
            String line3 = "";
            String lineTemp = "";

            if (getGateNetwork().getNetworkSignGateList().size() > 2) {
                line1 = getGateSignOrder().get(1).getGateName();
                line3 = getGateSignOrder().get(3).getGateName();
            }

            setGateDialSignTarget(getGateSignOrder().get(2));
            setGateDialSignIndex(getGateNetwork().getNetworkSignGateList().indexOf(getGateSignOrder().get(2)));

            // switch line1 with line3 if order reach end (smooth flip)
            if (direction == -1) {
                lineTemp = line1;
                line1 = line3;
                line3 = lineTemp;
            }

            getGateDialSign().setLine(1, line1);
            getGateDialSign().setLine(2, line2);
            getGateDialSign().setLine(3, line3);
            getGateDialSign().update(true);
        }

        // deprecated code
        // getGateTeleportSign().setData(getGateTeleportSign().getData());
    }

    /**
     * Timeout stargate.
     */
    public void timeoutStargate() {
        if (getGateActivateTaskId() > 0) {
            WXTLogger.prettyLog(Level.FINE, false, "Wormhole \"" + getGateName() + "\" ActivateTaskID \"" + getGateActivateTaskId() + "\" timed out.");
            setGateActivateTaskId(-1);
        }

        WormholePlayer wormholePlayer = WormholePlayerManager.getRegisteredWormholePlayer(this.getLastUsedBy());
        
        // Only send a message if the gate was still in the remotely activated gates list.
        // Make sure to reset iris if it should be on.
        if (isGateIrisDefaultActive()) {
            this.setIrisState(isGateIrisDefaultActive());
        }

        if (this.isGateLightsActive()) {
            this.lightStargate(false);
        }

        if (wormholePlayer != null) {
            wormholePlayer.sendMessage("Gate: " + getGateName() + " timed out and deactivated.");
            wormholePlayer.removeStargate(this.getGateName());
        }
        
        // remove activated stargate from list after timeout
        StargateManager.removeActivatedStargate(this.getGateName());
    }

    /**
     * Set the dial button and lever block state based on gate activation status.
     * 
     * @param regenerate
     *            true, to replace missing activation lever.
     */
    public void toggleDialLeverState(final boolean regenerate) {
        if (getGateDialLeverBlock() != null) {
            if (isGateActive()) {
                WorldUtils.scheduleChunkLoad(getGateDialLeverBlock());
            }
            
            Material material = getGateDialLeverBlock().getType();
            if (regenerate) {
                getGateDialLeverBlock().setTypeIdAndData(Material.LEVER.getId(), WorldUtils.getLeverFacingByteFromBlockFace(getGateFacing()), false);
                material = getGateDialLeverBlock().getType();
            }
            
            final byte leverState = getGateDialLeverBlock().getData();
            switch (material) {
                case STONE_BUTTON:
                    getGateDialLeverBlock().setType(Material.LEVER);
                    getGateDialLeverBlock().setData(leverState);
                    WXTLogger.prettyLog(Level.FINE, false, "Automaticially replaced Button on gate \"" + getGateName() + "\" with Lever.");
                    getGateDialLeverBlock().setData(WorldUtils.getLeverToggleByte(leverState, isGateActive()));
                    break;
                case LEVER:
                    getGateDialLeverBlock().setData(WorldUtils.getLeverToggleByte(leverState, isGateActive()));
                    break;
                default:
                    break;
            }
            
            if (!isGateActive()) {
                WorldUtils.scheduleChunkUnload(getGateDialLeverBlock());
            }
            
            WXTLogger.prettyLog(Level.FINE, false, "Dial Button Lever Gate: \"" + getGateName() + "\" Material: \"" + material.toString() + "\" State: \"" + leverState + "\"");
        }
    }

    /**
     * Toggle the iris state.
     * 
     * @param setDefault
     *            true to set the toggled state as the default state.
     */
    public void toggleIrisActive(final boolean setDefault) {
        setGateIrisActive(!isGateIrisActive());
        setIrisState(isGateIrisActive());
        if (setDefault) {
            setGateIrisDefaultActive(isGateIrisActive());
        }
    }

    /**
     * Toggle redstone gate activated power.
     */
    private void toggleRedstoneGateActivatedPower() {
        if (isGateRedstonePowered() && (getGateRedstoneGateActivatedBlock() != null) && (getGateRedstoneGateActivatedBlock().getTypeId() == 69)) {
            final byte leverState = getGateRedstoneGateActivatedBlock().getData();
            getGateRedstoneGateActivatedBlock().setData(WorldUtils.getLeverToggleByte(leverState, isGateActive()));
        }
    }

    /**
     * Try click teleport sign.
     * 
     * @param clickedBlock the clicked block
     * @return true, if successful
     */
    public boolean tryClickTeleportSign(Block clickedBlock) {
        return this.tryClickTeleportSign(clickedBlock, Action.LEFT_CLICK_BLOCK, null);
    }
    
    public boolean tryClickTeleportSign(Block clickedBlock, Action eventAction) {
        return this.tryClickTeleportSign(clickedBlock, eventAction, null);
    }
    
    public boolean tryClickTeleportSign(Block clickedBlock, Action eventAction, String triggeredByPlayer) {
        if ((getGateDialSign() == null) && (getGateDialSignBlock() != null)) {
            if (getGateDialSignBlock().getType().equals(Material.WALL_SIGN)) {
                setGateDialSignIndex(-1);

                //@TODO can be removed after long term test, only nuke sign on load
                if (eventAction == null) {
                    getGateDialSignBlock().setTypeId(0); //nuke sign
                }

                WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.DIAL_SIGN_CLICK, eventAction));
            }
        } else if (WorldUtils.isSameBlock(clickedBlock, getGateDialSignBlock())) {
            //@TODO can be removed after long term test, only nuke sign on load
            if (eventAction == null) {
                getGateDialSignBlock().setTypeId(0); //nuke sign
            }

            WormholeXTreme.getScheduler().scheduleSyncDelayedTask(WormholeXTreme.getThisPlugin(), new StargateUpdateRunnable(this, ActionToTake.DIAL_SIGN_CLICK, eventAction));
            return true;
        }

        return false;        
    }
}