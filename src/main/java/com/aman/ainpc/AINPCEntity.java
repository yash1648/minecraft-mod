package com.aman.ainpc;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;

public class AINPCEntity extends PathfinderMob {

    public AINPCEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);

        this.setHealth(10000);
        this.setPersistenceRequired();

        // Overpowered attributes (safe-set with null check)
        trySetAttribute(Attributes.MAX_HEALTH, 10000);
        trySetAttribute(Attributes.ATTACK_DAMAGE, 100);
        trySetAttribute(Attributes.ARMOR, 100);
        trySetAttribute(Attributes.ARMOR_TOUGHNESS, 50);
        trySetAttribute(Attributes.KNOCKBACK_RESISTANCE, 1.0);
        trySetAttribute(Attributes.FOLLOW_RANGE, 128);
        trySetAttribute(Attributes.MOVEMENT_SPEED, 0.5);
        trySetAttribute(Attributes.SAFE_FALL_DISTANCE, 256);
        trySetAttribute(Attributes.SCALE, 2.5);
    }

    private void trySetAttribute(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
        var instance = this.getAttribute(attribute);
        if (instance != null) {
            instance.setBaseValue(value);
        }
    }

    @Override
    protected void registerGoals() {
        // Target everything that lives
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true));

        // Combat & movement
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.3, false));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 32.0F));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // Completely invincible — takes zero damage
        return false;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        // Deal massive damage with huge knockback
        float damage = 100.0F;
        boolean hurt = target.hurtServer(level, damageSources().mobAttack(this), damage);

        if (hurt && target instanceof LivingEntity living) {
            double knockback = 5.0;
            double dx = living.getX() - this.getX();
            double dz = living.getZ() - this.getZ();
            living.knockback(knockback, dx, dz);
        }

        return hurt;
    }
}
