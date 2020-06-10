/*
 * Decompiled with CFR 0.149.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package net.minecraft.entity.damage;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.BadRespawnPointDamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class DamageSource {
    public static final DamageSource IN_FIRE = new DamageSource("inFire").setBypassesArmor().setFire();
    public static final DamageSource LIGHTNING_BOLT = new DamageSource("lightningBolt");
    public static final DamageSource ON_FIRE = new DamageSource("onFire").setBypassesArmor().setFire();
    public static final DamageSource LAVA = new DamageSource("lava").setFire();
    public static final DamageSource HOT_FLOOR = new DamageSource("hotFloor").setFire();
    public static final DamageSource IN_WALL = new DamageSource("inWall").setBypassesArmor();
    public static final DamageSource CRAMMING = new DamageSource("cramming").setBypassesArmor();
    public static final DamageSource DROWN = new DamageSource("drown").setBypassesArmor();
    public static final DamageSource STARVE = new DamageSource("starve").setBypassesArmor().setUnblockable();
    public static final DamageSource CACTUS = new DamageSource("cactus");
    public static final DamageSource FALL = new DamageSource("fall").setBypassesArmor();
    public static final DamageSource FLY_INTO_WALL = new DamageSource("flyIntoWall").setBypassesArmor();
    public static final DamageSource OUT_OF_WORLD = new DamageSource("outOfWorld").setBypassesArmor().setOutOfWorld();
    public static final DamageSource GENERIC = new DamageSource("generic").setBypassesArmor();
    public static final DamageSource MAGIC = new DamageSource("magic").setBypassesArmor().setUsesMagic();
    public static final DamageSource WITHER = new DamageSource("wither").setBypassesArmor();
    public static final DamageSource ANVIL = new DamageSource("anvil");
    public static final DamageSource FALLING_BLOCK = new DamageSource("fallingBlock");
    public static final DamageSource DRAGON_BREATH = new DamageSource("dragonBreath").setBypassesArmor();
    public static final DamageSource DRYOUT = new DamageSource("dryout");
    public static final DamageSource SWEET_BERRY_BUSH = new DamageSource("sweetBerryBush");
    private boolean bypassesArmor;
    private boolean outOfWorld;
    private boolean unblockable;
    private float exhaustion = 0.1f;
    private boolean fire;
    private boolean projectile;
    private boolean scaleWithDifficulty;
    private boolean magic;
    private boolean explosive;
    public final String name;

    public static DamageSource sting(LivingEntity arg) {
        return new EntityDamageSource("sting", arg);
    }

    public static DamageSource mob(LivingEntity arg) {
        return new EntityDamageSource("mob", arg);
    }

    public static DamageSource mobProjectile(Entity arg, LivingEntity arg2) {
        return new ProjectileDamageSource("mob", arg, arg2);
    }

    public static DamageSource player(PlayerEntity arg) {
        return new EntityDamageSource("player", arg);
    }

    public static DamageSource arrow(PersistentProjectileEntity arg, @Nullable Entity arg2) {
        return new ProjectileDamageSource("arrow", arg, arg2).setProjectile();
    }

    public static DamageSource trident(Entity arg, @Nullable Entity arg2) {
        return new ProjectileDamageSource("trident", arg, arg2).setProjectile();
    }

    public static DamageSource firework(FireworkRocketEntity arg, @Nullable Entity arg2) {
        return new ProjectileDamageSource("fireworks", arg, arg2).setExplosive();
    }

    public static DamageSource fireball(AbstractFireballEntity arg, @Nullable Entity arg2) {
        if (arg2 == null) {
            return new ProjectileDamageSource("onFire", arg, arg).setFire().setProjectile();
        }
        return new ProjectileDamageSource("fireball", arg, arg2).setFire().setProjectile();
    }

    public static DamageSource witherSkull(WitherSkullEntity arg, Entity arg2) {
        return new ProjectileDamageSource("witherSkull", arg, arg2).setProjectile();
    }

    public static DamageSource thrownProjectile(Entity arg, @Nullable Entity arg2) {
        return new ProjectileDamageSource("thrown", arg, arg2).setProjectile();
    }

    public static DamageSource magic(Entity arg, @Nullable Entity arg2) {
        return new ProjectileDamageSource("indirectMagic", arg, arg2).setBypassesArmor().setUsesMagic();
    }

    public static DamageSource thorns(Entity arg) {
        return new EntityDamageSource("thorns", arg).setThorns().setUsesMagic();
    }

    public static DamageSource explosion(@Nullable Explosion arg) {
        return DamageSource.explosion(arg != null ? arg.getCausingEntity() : null);
    }

    public static DamageSource explosion(@Nullable LivingEntity arg) {
        if (arg != null) {
            return new EntityDamageSource("explosion.player", arg).setScaledWithDifficulty().setExplosive();
        }
        return new DamageSource("explosion").setScaledWithDifficulty().setExplosive();
    }

    public static DamageSource badRespawnPoint() {
        return new BadRespawnPointDamageSource();
    }

    public String toString() {
        return "DamageSource (" + this.name + ")";
    }

    public boolean isProjectile() {
        return this.projectile;
    }

    public DamageSource setProjectile() {
        this.projectile = true;
        return this;
    }

    public boolean isExplosive() {
        return this.explosive;
    }

    public DamageSource setExplosive() {
        this.explosive = true;
        return this;
    }

    public boolean bypassesArmor() {
        return this.bypassesArmor;
    }

    public float getExhaustion() {
        return this.exhaustion;
    }

    public boolean isOutOfWorld() {
        return this.outOfWorld;
    }

    public boolean isUnblockable() {
        return this.unblockable;
    }

    protected DamageSource(String string) {
        this.name = string;
    }

    @Nullable
    public Entity getSource() {
        return this.getAttacker();
    }

    @Nullable
    public Entity getAttacker() {
        return null;
    }

    protected DamageSource setBypassesArmor() {
        this.bypassesArmor = true;
        this.exhaustion = 0.0f;
        return this;
    }

    protected DamageSource setOutOfWorld() {
        this.outOfWorld = true;
        return this;
    }

    protected DamageSource setUnblockable() {
        this.unblockable = true;
        this.exhaustion = 0.0f;
        return this;
    }

    protected DamageSource setFire() {
        this.fire = true;
        return this;
    }

    public Text getDeathMessage(LivingEntity arg) {
        LivingEntity lv = arg.getPrimeAdversary();
        String string = "death.attack." + this.name;
        String string2 = string + ".player";
        if (lv != null) {
            return new TranslatableText(string2, arg.getDisplayName(), lv.getDisplayName());
        }
        return new TranslatableText(string, arg.getDisplayName());
    }

    public boolean isFire() {
        return this.fire;
    }

    public String getName() {
        return this.name;
    }

    public DamageSource setScaledWithDifficulty() {
        this.scaleWithDifficulty = true;
        return this;
    }

    public boolean isScaledWithDifficulty() {
        return this.scaleWithDifficulty;
    }

    public boolean getMagic() {
        return this.magic;
    }

    public DamageSource setUsesMagic() {
        this.magic = true;
        return this;
    }

    public boolean isSourceCreativePlayer() {
        Entity lv = this.getAttacker();
        return lv instanceof PlayerEntity && ((PlayerEntity)lv).abilities.creativeMode;
    }

    @Nullable
    public Vec3d getPosition() {
        return null;
    }
}

