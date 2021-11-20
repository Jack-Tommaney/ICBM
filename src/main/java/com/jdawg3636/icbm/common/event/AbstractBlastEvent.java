package com.jdawg3636.icbm.common.event;

import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Supplier;

/**
 * Fired whenever an ICBM explosive is detonated
 * Server-Side Only
 * Handled in com.jdawg3636.icbm.common.event.ICBMEvents
 */
@Cancelable
public abstract class AbstractBlastEvent extends Event {

    public enum Type {
        EXPLOSIVES,
        EXPLOSIVES_MINECART,
        PLATFORM_MISSILE,
        CRUISE_MISSILE,
        HANDHELD_ROCKET,
        GRENADE
    }

    private final BlockPos blastPosition;
    private final ServerWorld blastWorld;
    private final AbstractBlastEvent.Type blastType;
    private final Supplier<SoundEvent> soundEvent;
    private final float soundEventRangeMultiplier; // 1.0F = Radius of 16 blocks

    /**
     * Don't call this directly; use AbstractBlastEvent.fire() instead (pass the appropriate blast's constructor as a parameter)
     */
    public AbstractBlastEvent(BlockPos blastPosition, ServerWorld blastWorld, AbstractBlastEvent.Type blastType) {
        this(blastPosition, blastWorld, blastType, null);
    }

    /**
     * Don't call this directly; use AbstractBlastEvent.fire() instead (pass the appropriate blast's constructor as a parameter)
     */
    public AbstractBlastEvent(BlockPos blastPosition, ServerWorld blastWorld, AbstractBlastEvent.Type blastType, Supplier<SoundEvent> soundEvent) {
        this(blastPosition, blastWorld, blastType, soundEvent, 1.0F);
    }

    /**
     * Don't call this directly; use AbstractBlastEvent.fire() instead (pass the appropriate blast's constructor as a parameter)
     */
    public AbstractBlastEvent(BlockPos blastPosition, ServerWorld blastWorld, AbstractBlastEvent.Type blastType, Supplier<SoundEvent> soundEvent, float soundEventRangeMultiplier) {
        this.blastPosition = blastPosition;
        this.blastWorld = blastWorld;
        this.blastType = blastType;
        this.soundEvent = soundEvent;
        this.soundEventRangeMultiplier = soundEventRangeMultiplier;
    }

    /**
     * Utility method to construct a blast event, fire it, and execute the blast if the event is not cancelled.
     * todo: include access to this in API when/if one is created
     * @param blastEventProvider Blast Event Constructor
     * @return Blast was Executed Successfully
     */
    public static boolean fire(BlastEventProvider blastEventProvider, BlockPos blastPosition, ServerWorld blastWorld, AbstractBlastEvent.Type blastType) {
        AbstractBlastEvent blastEvent = blastEventProvider.getBlastEvent(blastPosition, blastWorld, blastType);
        boolean eventCancelled = MinecraftForge.EVENT_BUS.post(blastEvent);
        boolean success = false;
        if(!eventCancelled) {
            success = blastEvent.executeBlast();
        }
        return success;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    public BlockPos getBlastPosition() {
        return blastPosition;
    }

    public ServerWorld getBlastWorld() {
        return blastWorld;
    }

    public AbstractBlastEvent.Type getBlastType() {
        return blastType;
    }

    public Supplier<SoundEvent> getSoundEvent() {
        return soundEvent;
    }

    public float getSoundEventRangeMultiplier() {
        return soundEventRangeMultiplier;
    }

    /**
     * Don't call this directly; use AbstractBlastEvent.fire() instead (pass the appropriate blast's constructor as a parameter)
     */
    public abstract boolean executeBlast();

    /**
     * Functional Interface to represent a subclass constructor
     */
    @FunctionalInterface
    public interface BlastEventProvider {
        AbstractBlastEvent getBlastEvent(BlockPos blastPosition, ServerWorld blastWorld, AbstractBlastEvent.Type blastType);
    }

}
