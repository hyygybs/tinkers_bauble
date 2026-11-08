package com.hyygybs.tinkers_bauble.modifiers;

import com.hyygybs.tinkers_bauble.TinkersBauble;
import com.ssakura49.sakura_library.event.entity.ItemStackDamageEvent;
import com.ssakura49.sakura_library.event.entity.LivingDamageCalculationEvent;
import com.ssakura49.tinkercuriolib.hook.TCLibHooks;
import com.ssakura49.tinkercuriolib.hook.armor.CurioEquipmentChangeModifierHook;
import com.ssakura49.tinkercuriolib.hook.armor.CurioTakeDamagePreModifierHook;
import com.ssakura49.tinkercuriolib.hook.behavior.CurioAttributeModifierHook;
import com.ssakura49.tinkercuriolib.hook.behavior.CurioFortuneModifierHook;
import com.ssakura49.tinkercuriolib.hook.behavior.CurioGetToolDamageModifierHook;
import com.ssakura49.tinkercuriolib.hook.combat.CurioDamageCalculateModifierHook;
import com.ssakura49.tinkercuriolib.hook.combat.CurioDamageTargetPostModifierHook;
import com.ssakura49.tinkercuriolib.hook.combat.CurioDamageTargetPreModifierHook;
import com.ssakura49.tinkercuriolib.hook.interation.CurioInventoryTickModifierHook;
import com.ssakura49.tinkercuriolib.hook.mining.CurioBreakSpeedModifierHook;
import com.ssakura49.tinkercuriolib.tools.modifier.base.TCLibBaseModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.StaticModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.ToolDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 本模组自定义的饰品特性。
 * 所有特性都挂在工匠戒指/项链上，通过 TinkerCurioLib 的钩子影响佩戴者的手持工具、武器与护甲。
 */
public final class ModTraits {
    public static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(TinkersBauble.MODID);

    public static final StaticModifier<NaturalRepair> NATURAL_REPAIR = MODIFIERS.register("natural_repair", NaturalRepair::new);
    public static final StaticModifier<NaturalIronArmor> NATURAL_IRON_ARMOR = MODIFIERS.register("natural_iron_armor", NaturalIronArmor::new);
    public static final StaticModifier<DenseBlessing> DENSE_BLESSING = MODIFIERS.register("dense_blessing", DenseBlessing::new);
    public static final StaticModifier<TemperateBlessing> TEMPERATE_BLESSING = MODIFIERS.register("temperate_blessing", TemperateBlessing::new);
    public static final StaticModifier<EnduringBlessing> ENDURING_BLESSING = MODIFIERS.register("enduring_blessing", EnduringBlessing::new);
    public static final StaticModifier<Plastify> PLASTIFY = MODIFIERS.register("plastify", Plastify::new);
    public static final StaticModifier<NotHungry> NOT_HUNGRY = MODIFIERS.register("not_hungry", NotHungry::new);
    public static final StaticModifier<Overcoat> OVERCOAT = MODIFIERS.register("overcoat", Overcoat::new);
    public static final StaticModifier<Overload> OVERLOAD = MODIFIERS.register("overload", Overload::new);
    public static final StaticModifier<Overforce> OVERFORCE = MODIFIERS.register("overforce", Overforce::new);
    public static final StaticModifier<Overcapacitor> OVERCAPACITOR = MODIFIERS.register("overcapacitor", Overcapacitor::new);
    public static final StaticModifier<Overgrind> OVERGRIND = MODIFIERS.register("overgrind", Overgrind::new);
    public static final StaticModifier<OverslimeEnergy> OVERSLIME_ENERGY_MODIFIER = MODIFIERS.register("overslime_energy", OverslimeEnergy::new);
    public static final StaticModifier<DepositionEffect> DEPOSITION_EFFECT = MODIFIERS.register("deposition_effect", DepositionEffect::new);
    public static final StaticModifier<Rejuvenate> REJUVENATE = MODIFIERS.register("rejuvenate", Rejuvenate::new);
    public static final StaticModifier<BrittleDurability> BRITTLE_DURABILITY = MODIFIERS.register("brittle_durability", BrittleDurability::new);
    public static final StaticModifier<OverRegen> OVERREGEN = MODIFIERS.register("overregen", OverRegen::new);
    public static final StaticModifier<OverslimeCapacity> OVERSLIME_CAPACITY_MODIFIER = MODIFIERS.register("overslime_capacity", OverslimeCapacity::new);
    public static final StaticModifier<OverEvolve> OVEREVOLVE = MODIFIERS.register("overevolve", OverEvolve::new);
    public static final StaticModifier<Precision> PRECISION = MODIFIERS.register("precision", Precision::new);
    public static final StaticModifier<Rough> ROUGH = MODIFIERS.register("rough", Rough::new);
    public static final StaticModifier<Brutal> BRUTAL = MODIFIERS.register("brutal", Brutal::new);
    public static final StaticModifier<StrongHand> STRONG_HAND = MODIFIERS.register("strong_hand", StrongHand::new);
    public static final StaticModifier<Unyielding> UNYIELDING = MODIFIERS.register("unyielding", Unyielding::new);
    public static final StaticModifier<Suppression> SUPPRESSION = MODIFIERS.register("suppression", Suppression::new);
    public static final StaticModifier<SeaSwim> SEA_SWIM = MODIFIERS.register("sea_swim", SeaSwim::new);
    public static final StaticModifier<Antigravity> ANTIGRAVITY = MODIFIERS.register("antigravity", Antigravity::new);
    public static final StaticModifier<Envenom> ENVENOM = MODIFIERS.register("envenom", Envenom::new);
    public static final StaticModifier<Bloodthirst> BLOODTHIRST = MODIFIERS.register("bloodthirst", Bloodthirst::new);
    public static final StaticModifier<LifeSteal> LIFE_STEAL = MODIFIERS.register("life_steal", LifeSteal::new);
    public static final StaticModifier<GemResistance> GEM_RESISTANCE = MODIFIERS.register("gem_resistance", GemResistance::new);
    public static final StaticModifier<IceArmor> ICE_ARMOR = MODIFIERS.register("ice_armor", IceArmor::new);
    public static final StaticModifier<Fireproof> FIREPROOF = MODIFIERS.register("fireproof", Fireproof::new);
    public static final StaticModifier<Fortune> FORTUNE = MODIFIERS.register("fortune", Fortune::new);
    public static final StaticModifier<Verdant> VERDANT = MODIFIERS.register("verdant", Verdant::new);
    public static final StaticModifier<Flawless> FLAWLESS = MODIFIERS.register("flawless", Flawless::new);
    public static final StaticModifier<Flight> FLIGHT = MODIFIERS.register("flight", Flight::new);
    public static final StaticModifier<WaterBreathing> WATER_BREATHING = MODIFIERS.register("water_breathing", WaterBreathing::new);
    public static final StaticModifier<NightVision> NIGHT_VISION = MODIFIERS.register("night_vision", NightVision::new);
    public static final StaticModifier<Luminous> LUMINOUS = MODIFIERS.register("luminous", Luminous::new);
    public static final StaticModifier<Radiation> RADIATION = MODIFIERS.register("radiation", Radiation::new);
    public static final StaticModifier<Hardness> HARDNESS = MODIFIERS.register("hardness", Hardness::new);
    public static final StaticModifier<Flame> FLAME = MODIFIERS.register("flame", Flame::new);
    public static final StaticModifier<DragonRoar> DRAGON_ROAR = MODIFIERS.register("dragon_roar", DragonRoar::new);
    public static final StaticModifier<LongArm> LONG_ARM = MODIFIERS.register("long_arm", LongArm::new);
    public static final StaticModifier<Silky> SILKY = MODIFIERS.register("silky", Silky::new);
    public static final StaticModifier<Frost> FROST = MODIFIERS.register("frost", Frost::new);

    /** 自定义树皮覆层的持久数据键（TConstruct 物品存在持久数据中） */
    private static final ResourceLocation BARK_KEY = new ResourceLocation(TinkersBauble.MODID, "barkskin");
    /** 普通物品上的树皮覆层键 */
    private static final String BARK_TAG = TinkersBauble.MODID + ":barkskin";
    /** Apothic Attributes 的箭矢伤害倍率属性 */
    private static final ResourceLocation ARROW_DAMAGE_ID = new ResourceLocation("attributeslib", "arrow_damage");
    /** 匠魂工具倍率所在的根 NBT 键 */
    private static final String MULTIPLIERS_TAG = "tic_multipliers";
    private static final String DRAW_SPEED_STAT = "tconstruct:draw_speed";
    private static final String VELOCITY_STAT = "tconstruct:velocity";
    /** 黏液进化对手持匠魂弓的拉弓速度与远程初速度的加成 */
    private static final float BOW_STAT_BONUS = 0.06f;
    private static final ResourceLocation BOW_BOOST_FLAG = new ResourceLocation(TinkersBauble.MODID, "overevolve_boost");
    private static final ResourceLocation BOW_BOOST_DRAW = new ResourceLocation(TinkersBauble.MODID, "overevolve_draw_speed");
    private static final ResourceLocation BOW_BOOST_VELOCITY = new ResourceLocation(TinkersBauble.MODID, "overevolve_velocity");
    /** 精准临时注入的匠魂“丝触”强化 */
    private static final ModifierId SILKY_ID = new ModifierId("tconstruct:silky");
    private static final ResourceLocation SILKY_FLAG = new ResourceLocation(TinkersBauble.MODID, "precision_silky");
    /** 强手临时注入的匠魂“快速装填”强化 */
    private static final ModifierId QUICK_CHARGE_ID = new ModifierId("tconstruct:quick_charge");
    private static final ResourceLocation QUICK_CHARGE_FLAG = new ResourceLocation(TinkersBauble.MODID, "strong_hand_quick_charge");
    /** 粗暴临时剥离目标护甲时使用的属性修饰符 */
    private static final String BRUTAL_ARMOR_KEY = "brutal_armor";
    /** 每级黏液覆层强化提供的黏液覆层上限 */
    private static final int OVERSLIME_CAPACITY_PER_LEVEL = 50;
    /** 黏液覆盖提供的黏液覆层上限 */
    private static final int OVERCOAT_CAPACITY = 50;
    /** 黏液扩容每级提供的黏液覆层上限 */
    private static final int OVERLOAD_CAPACITY = 200;
    /** 黏液统治提供的黏液覆层上限 */
    private static final int OVERFORCE_CAPACITY = 1000;
    /** 黏液电容提供的黏液覆层上限 */
    private static final int OVERCAPACITOR_CAPACITY = 100;
    /** 黏液打磨提供的黏液覆层上限 */
    private static final int OVERGRIND_CAPACITY = 100;
    /** 黏液电容：每点黏液覆层上限对应的 FE 存储 */
    private static final int FE_PER_OVERSLIME_CAPACITY = 100;
    /** 黏液电容：每点黏液覆层消耗的 FE */
    private static final int FE_PER_OVERSLIME = 200;
    /** 临时注入的匠魂“黏液覆层”与“黏液之友”强化 */
    private static final ModifierId OVERSLIME_ID = new ModifierId("tconstruct:overslime");
    private static final ModifierId OVERSLIME_FRIEND_ID = new ModifierId("tconstruct:overslime_friend");
    /** 记录注入的黏液覆层容量等级、基础强化与“黏液之友”标记 */
    private static final ResourceLocation OVERSLIME_INJECT_KEY = new ResourceLocation(TinkersBauble.MODID, "overslime_injected");
    private static final ResourceLocation OVERSLIME_BASE_FLAG = new ResourceLocation(TinkersBauble.MODID, "overslime_base");
    private static final ResourceLocation OVERSLIME_FRIEND_FLAG = new ResourceLocation(TinkersBauble.MODID, "overslime_friend");
    /** 黏液电容注入的 FE 存储强化标记 */
    private static final ResourceLocation OVERSLIME_ENERGY_FLAG = new ResourceLocation(TinkersBauble.MODID, "overslime_energy");
    /** 飞行特性的玩家持久数据键与持续时间（30 秒） */
    private static final String FLIGHT_EXPIRY_KEY = TinkersBauble.MODID + ":flight_expiry";
    private static final String FLIGHT_GRANTED_KEY = TinkersBauble.MODID + ":flight_granted";
    private static final long FLIGHT_DURATION_TICKS = 30L * 20L;

    private ModTraits() {
    }

    public static void register(IEventBus modBus) {
        MODIFIERS.register(modBus);
        MinecraftForge.EVENT_BUS.addListener(ModTraits::onEntityTeleport);
        MinecraftForge.EVENT_BUS.addListener(ModTraits::onCriticalHit);
        MinecraftForge.EVENT_BUS.addListener(ModTraits::onPlayerTick);
    }

    // ======================================================================
    // 特性实现
    // ======================================================================

    /** 自然修复：每秒（5%×等级）概率恢复手持工具/武器与所穿戴护甲各 1% 耐久 */
    public static class NaturalRepair extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity)) {
                return;
            }
            float chance = 0.05f * entry.getLevel();
            RandomSource random = entity.getRandom();
            for (ItemStack gear : gear(entity)) {
                if (!isTool(gear) || random.nextFloat() >= chance) {
                    continue;
                }
                int max = maxDurability(gear);
                repair(gear, Math.max(1, Math.round(max * 0.01f)));
            }
        }
    }

    /** 自然铁衣：装备失去耐久时额外失去 1 点，为另一件装备补充树皮覆层 */
    public static class NaturalIronArmor extends TCLibBaseModifier implements CurioGetToolDamageModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TOOL_DAMAGE);
        }

        @Override
        public void onCurioGetToolDamage(IToolStackView tool, ModifierEntry entry, LivingEntity entity, ItemStackDamageEvent event) {
            ItemStack damaged = event.getStack();
            int amount = event.getAmount();
            if (amount <= 0 || !isWornGear(entity, damaged)) {
                return;
            }
            // 先消耗该装备自身的树皮覆层
            int remaining = consumeBark(damaged, amount);
            if (remaining <= 0) {
                event.setAmount(0);
                return;
            }
            // 额外失去 1 点耐久，为另一件装备补充树皮覆层
            List<ItemStack> others = new ArrayList<>();
            for (ItemStack gear : gear(entity)) {
                if (isTool(gear) && !ItemStack.matches(gear, damaged)) {
                    others.add(gear);
                }
            }
            if (others.isEmpty()) {
                event.setAmount(remaining);
                return;
            }
            ItemStack target = others.get(entity.getRandom().nextInt(others.size()));
            addBark(target, 1 + entry.getLevel());
            event.setAmount(remaining + 1);
        }
    }

    /** 致密祝福：减少 33% 耐久损失 */
    public static class DenseBlessing extends TCLibBaseModifier implements CurioGetToolDamageModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TOOL_DAMAGE);
        }

        @Override
        public void onCurioGetToolDamage(IToolStackView tool, ModifierEntry entry, LivingEntity entity, ItemStackDamageEvent event) {
            int amount = event.getAmount();
            if (amount <= 0 || !isWornGear(entity, event.getStack())) {
                return;
            }
            event.setAmount(amount - Math.round(amount * 0.33f));
        }
    }

    /** 寒暑温变：极冷 +8 挖掘速度、+4 攻击速度；极热 +16% 抗性 */
    public static class TemperateBlessing extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioBreakSpeedModifierHook,
                       CurioTakeDamagePreModifierHook, CurioEquipmentChangeModifierHook {
        private static final double ATTACK_SPEED_BONUS = 4.0;
        private static final float MINING_SPEED_BONUS = 8.0f;
        private static final float RESISTANCE = 0.16f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_BREAK_SPEED);
            builder.addHook(this, TCLibHooks.CURIO_TAKE_DAMAGE_PRE);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        private static String key(SlotContext context) {
            return "temperate:" + context.identifier() + ":" + context.index();
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide) {
                return;
            }
            applyAttackSpeed(entity, key(context), isCold(entity) ? ATTACK_SPEED_BONUS : 0);
        }

        @Override
        public void onCurioBreakSpeed(IToolStackView tool, ModifierEntry entry, PlayerEvent.BreakSpeed event, Player player) {
            if (isCold(player)) {
                event.setNewSpeed(event.getNewSpeed() + MINING_SPEED_BONUS);
            }
        }

        @Override
        public void onCurioTakeDamagePre(IToolStackView tool, ModifierEntry entry, LivingHurtEvent event, LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
            if (isHot(entity)) {
                event.setAmount(event.getAmount() * (1 - RESISTANCE));
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide) {
                applyAttackSpeed(entity, key(context), 0);
            }
        }
    }

    /** 持久祝福：+6 挖掘速度、+2 盔甲韧性 */
    public static class EnduringBlessing extends TCLibBaseModifier
            implements CurioBreakSpeedModifierHook, CurioAttributeModifierHook {
        private static final float MINING_SPEED_BONUS = 6.0f;
        private static final double ARMOR_TOUGHNESS_BONUS = 2.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_BREAK_SPEED);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void onCurioBreakSpeed(IToolStackView tool, ModifierEntry entry, PlayerEvent.BreakSpeed event, Player player) {
            event.setNewSpeed(event.getNewSpeed() + MINING_SPEED_BONUS);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "enduring_blessing", Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_BONUS, AttributeModifier.Operation.ADDITION);
        }
    }

    /** 可塑化：每级 +5% 近战伤害、+0.25 弹射物力量、+1 盔甲韧性，25% 概率不消耗耐久 */
    public static class Plastify extends TCLibBaseModifier
            implements CurioAttributeModifierHook, CurioDamageCalculateModifierHook, CurioGetToolDamageModifierHook {
        private static final float NO_DAMAGE_CHANCE = 0.25f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
            builder.addHook(this, TCLibHooks.CURIO_CALCULATE_DAMAGE);
            builder.addHook(this, TCLibHooks.CURIO_TOOL_DAMAGE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            int level = entry.getLevel();
            addAttribute(consumer, uuid, "plastify_toughness", Attributes.ARMOR_TOUGHNESS, level, AttributeModifier.Operation.ADDITION);

            Attribute arrowDamage = ForgeRegistries.ATTRIBUTES.getValue(ARROW_DAMAGE_ID);
            if (arrowDamage != null) {
                // 每点弹射物力量换算为 10% 箭矢伤害
                addAttribute(consumer, uuid, "plastify_projectile", arrowDamage, 0.25 * level * 0.1, AttributeModifier.Operation.ADDITION);
            }
        }

        @Override
        public void onCurioCalculateDamage(IToolStackView tool, ModifierEntry entry, LivingDamageCalculationEvent event,
                                           LivingEntity attacker, LivingEntity target) {
            event.setDamage(event.getDamage() * (1 + 0.05f * entry.getLevel()));
        }

        @Override
        public void onCurioGetToolDamage(IToolStackView tool, ModifierEntry entry, LivingEntity entity, ItemStackDamageEvent event) {
            if (event.getAmount() > 0 && isWornGear(entity, event.getStack())
                    && entity.getRandom().nextFloat() < NO_DAMAGE_CHANCE) {
                event.setAmount(0);
            }
        }
    }

    /** 不饿：每分钟恢复 4 点饥饿值与 4.8 点饱和度 */
    public static class NotHungry extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !(entity instanceof Player player)) {
                return;
            }
            if (entity.level().getGameTime() % 1200 != 0) {
                return;
            }
            // 饱和度 = 饥饿值 × 饱和度系数 × 2
            player.getFoodData().eat(4, 0.6f);
        }
    }

    /** 黏液覆盖：每秒（5%×等级）概率为装备补充 10 点黏液覆层，并获得 50 点黏液覆层上限 */
    public static class Overcoat extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        private static final int AMOUNT = 10;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity) || entity.getRandom().nextFloat() >= 0.05f * entry.getLevel()) {
                return;
            }
            for (ItemStack gear : gear(entity)) {
                if (supportsOverslime(gear)) {
                    addOverslime(gear, AMOUNT, overslimeCapacity(gear));
                }
            }
        }
    }

    /** 黏液扩容：每级使装备获得 200 点黏液覆层上限，并解除黏液覆层的排异 */
    public static class Overload extends TCLibBaseModifier {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
        }
    }

    /** 黏液统治：每秒（5%×等级）概率按耐久度 ×0.25 补充黏液覆层，并获得 1000 点黏液覆层上限 */
    public static class Overforce extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity) || entity.getRandom().nextFloat() >= 0.05f * entry.getLevel()) {
                return;
            }
            for (ItemStack gear : gear(entity)) {
                if (supportsOverslime(gear)) {
                    addOverslime(gear, (int) Math.floor(maxDurability(gear) * 0.25f), overslimeCapacity(gear));
                }
            }
        }
    }

    /** 黏液电容：为装备提供 100 点黏液覆层上限与 100 倍上限的 FE 存储，每秒每级把 200FE 转化为 1 点黏液覆层 */
    public static class Overcapacitor extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity)) {
                return;
            }
            int level = entry.getLevel();
            for (ItemStack gear : gear(entity)) {
                if (!supportsOverslime(gear)) {
                    continue;
                }
                ToolStack gearTool = ToolStack.from(gear);
                int energy = ToolEnergyCapability.getEnergy(gearTool);
                int count = Math.min(level, energy / FE_PER_OVERSLIME);
                if (count <= 0) {
                    continue;
                }
                int capacity = overslimeCapacity(gear);
                int current = getOverslime(gear);
                int added = Math.min(count, capacity - current);
                if (added <= 0) {
                    continue;
                }
                addOverslime(gear, added, capacity);
                ToolEnergyCapability.setEnergy(gearTool, energy - FE_PER_OVERSLIME * added);
            }
        }
    }

    /** 黏液打磨：提供 100 点黏液覆层上限并解除排异，每秒 15% 概率修复 1 点耐久，修复时 33% 概率消耗 1 点黏液覆层 */
    public static class Overgrind extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        private static final float REPAIR_CHANCE = 0.15f;
        private static final float OVERSLIME_COST_CHANCE = 0.33f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity)) {
                return;
            }
            for (ItemStack gear : gear(entity)) {
                if (!isTool(gear) || damage(gear) <= 0 || entity.getRandom().nextFloat() >= REPAIR_CHANCE) {
                    continue;
                }
                setDamage(gear, damage(gear) - 1);
                if (supportsOverslime(gear) && getOverslime(gear) > 0
                        && entity.getRandom().nextFloat() < OVERSLIME_COST_CHANCE) {
                    ToolStack gearTool = ToolStack.from(gear);
                    OverslimeModule.INSTANCE.removeAmount(gearTool, 1);
                }
            }
        }
    }

    /** 隐藏的内部强化：按黏液覆层上限为工具提供 100 倍上限的 FE 存储（供黏液电容动态注入） */
    public static class OverslimeEnergy extends TCLibBaseModifier implements ToolStatsModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, ModifierHooks.TOOL_STATS);
        }

        @Override
        public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
            ToolEnergyCapability.MAX_STAT.add(builder,
                    builder.getStat(OverslimeModule.OVERSLIME_STAT) * FE_PER_OVERSLIME_CAPACITY);
        }
    }

    /** 沉积效应：自 72 格往下，每级每降低 1 格使装备耐久消耗 -0.417%，最高 -80% */
    public static class DepositionEffect extends TCLibBaseModifier implements CurioGetToolDamageModifierHook {
        private static final float START_Y = 72.0f;
        private static final float REDUCTION_PER_BLOCK = 0.00417f;
        private static final float MAX_REDUCTION = 0.8f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TOOL_DAMAGE);
        }

        @Override
        public void onCurioGetToolDamage(IToolStackView tool, ModifierEntry entry, LivingEntity entity, ItemStackDamageEvent event) {
            int amount = event.getAmount();
            if (amount <= 0 || !isWornGear(entity, event.getStack())) {
                return;
            }
            float depth = (float) (START_Y - entity.getY());
            if (depth <= 0) {
                return;
            }
            float reduction = Math.min(MAX_REDUCTION, depth * REDUCTION_PER_BLOCK * entry.getLevel());
            event.setAmount(reduceByFraction(amount, reduction, entity.getRandom()));
        }
    }

    /** 回春：每分钟恢复 10% 已损失生命值 */
    public static class Rejuvenate extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || entity.level().getGameTime() % 1200 != 0) {
                return;
            }
            float missing = entity.getMaxHealth() - entity.getHealth();
            if (missing > 0) {
                entity.heal(missing * 0.1f * entry.getLevel());
            }
        }
    }

    /** 脆弱耐久：装备受损时（1-70%^等级）概率少消耗 1 点耐久，（1-95%^等级）概率多消耗 1 点 */
    public static class BrittleDurability extends TCLibBaseModifier implements CurioGetToolDamageModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TOOL_DAMAGE);
        }

        @Override
        public void onCurioGetToolDamage(IToolStackView tool, ModifierEntry entry, LivingEntity entity, ItemStackDamageEvent event) {
            int amount = event.getAmount();
            if (amount <= 0 || !isWornGear(entity, event.getStack())) {
                return;
            }
            double level = entry.getLevel();
            if (entity.getRandom().nextFloat() < 1 - Math.pow(0.70, level)) {
                amount -= 1;
            }
            if (entity.getRandom().nextFloat() < 1 - Math.pow(0.95, level)) {
                amount += 1;
            }
            event.setAmount(Math.max(0, amount));
        }
    }

    /** 按比例削减耐久消耗，小数部分按概率进位 */
    private static int reduceByFraction(int amount, float reduction, RandomSource random) {
        float factor = Math.max(0, 1 - reduction);
        int reduced = (int) Math.floor(amount * factor);
        if (random.nextFloat() < amount * factor - reduced) {
            reduced++;
        }
        return Math.max(0, reduced);
    }

    /** 黏液自生：每秒（5%×等级）概率恢复 25% 黏液覆层 */
    public static class OverRegen extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        private static final int BASE_CAPACITY = 250;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity) || entity.getRandom().nextFloat() >= 0.05f * entry.getLevel()) {
                return;
            }
            for (ItemStack gear : gear(entity)) {
                if (!supportsOverslime(gear)) {
                    continue;
                }
                int current = getOverslime(gear);
                if (current <= 0) {
                    continue;
                }
                int capacity = overslimeCapacity(gear, BASE_CAPACITY + 750);
                addOverslime(gear, Math.round(current * 0.25f), capacity);
            }
        }
    }

    /** 隐藏的内部强化：每级为工具提供 50 点黏液覆层上限（供黏液覆盖/扩容/统治动态注入） */
    public static class OverslimeCapacity extends TCLibBaseModifier implements ToolStatsModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, ModifierHooks.TOOL_STATS);
        }

        @Override
        public void addToolStats(IToolContext context, ModifierEntry entry, ModifierStatsBuilder builder) {
            OverslimeModule.OVERSLIME_STAT.add(builder, OVERSLIME_CAPACITY_PER_LEVEL * entry.getLevel());
        }
    }

    /** 黏液进化：装备有黏液覆层时 +20% 挖掘速度、+15% 攻击速度、+0.06 远程初速度与拉弓速度、-5% 受到伤害 */
    public static class OverEvolve extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioBreakSpeedModifierHook, CurioTakeDamagePreModifierHook,
                       CurioEquipmentChangeModifierHook {
        private static final float MINING_SPEED_MULTIPLIER = 1.2f;
        private static final double ATTACK_SPEED_MULTIPLIER = 0.15;
        private static final float DAMAGE_REDUCTION = 0.05f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_BREAK_SPEED);
            builder.addHook(this, TCLibHooks.CURIO_TAKE_DAMAGE_PRE);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        private static String key(SlotContext context) {
            return "overevolve:" + context.identifier() + ":" + context.index();
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide) {
                return;
            }
            boolean active = anyGearHasOverslime(entity);
            applyAttackSpeedMultiplier(entity, key(context), active ? ATTACK_SPEED_MULTIPLIER : 0);
            if (entity instanceof Player player) {
                updateBowStatBoosts(player, active);
            }
        }

        @Override
        public void onCurioBreakSpeed(IToolStackView tool, ModifierEntry entry, PlayerEvent.BreakSpeed event, Player player) {
            if (anyGearHasOverslime(player)) {
                event.setNewSpeed(event.getNewSpeed() * MINING_SPEED_MULTIPLIER);
            }
        }

        @Override
        public void onCurioTakeDamagePre(IToolStackView tool, ModifierEntry entry, LivingHurtEvent event, LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
            if (anyGearHasOverslime(entity)) {
                event.setAmount(event.getAmount() * (1 - DAMAGE_REDUCTION));
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide) {
                applyAttackSpeedMultiplier(entity, key(context), 0);
                if (entity instanceof Player player) {
                    updateBowStatBoosts(player, false);
                }
            }
        }
    }

    // ======================================================================
    // 宝石特性
    // ======================================================================

    /** 精准：手持的匠魂工具临时获得精准采集 */
    public static class Precision extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioEquipmentChangeModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (!entity.level().isClientSide && entity instanceof Player player) {
                updateHeldInjection(player, SILKY_ID, SILKY_FLAG, true, false, true);
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide && entity instanceof Player player) {
                updateHeldInjection(player, SILKY_ID, SILKY_FLAG, false, false, true);
            }
        }
    }

    /** 粗糙：+2 护甲 */
    public static class Rough extends TCLibBaseModifier implements CurioAttributeModifierHook {
        private static final double ARMOR_BONUS = 2.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "gem_rough_armor", Attributes.ARMOR, ARMOR_BONUS, AttributeModifier.Operation.ADDITION);
        }
    }

    /** 粗暴：攻击无视目标护甲 */
    public static class Brutal extends TCLibBaseModifier
            implements CurioDamageTargetPreModifierHook, CurioDamageTargetPostModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_PRE);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        private static UUID armorModifierId(LivingEntity target) {
            return UUID.nameUUIDFromBytes((target.getUUID() + ":" + BRUTAL_ARMOR_KEY).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }

        @Override
        public void onDamageTargetPre(IToolStackView tool, ModifierEntry entry, LivingHurtEvent event, LivingEntity attacker, LivingEntity target) {
            if (target.level().isClientSide) {
                return;
            }
            AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
            if (armor == null) {
                return;
            }
            UUID id = armorModifierId(target);
            armor.removeModifier(id);
            double value = armor.getValue();
            if (value > 0) {
                armor.addTransientModifier(new AttributeModifier(id, "tinkers_bauble:brutal_armor", -value, AttributeModifier.Operation.ADDITION));
            }
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            AttributeInstance armor = target.getAttribute(Attributes.ARMOR);
            if (armor != null) {
                armor.removeModifier(armorModifierId(target));
            }
        }
    }

    /** 强手：手持的匠魂弓临时获得快速装填，提升拉弓速度 */
    public static class StrongHand extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioEquipmentChangeModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (!entity.level().isClientSide && entity instanceof Player player) {
                updateHeldInjection(player, QUICK_CHARGE_ID, QUICK_CHARGE_FLAG, true, true, false);
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide && entity instanceof Player player) {
                updateHeldInjection(player, QUICK_CHARGE_ID, QUICK_CHARGE_FLAG, false, true, false);
            }
        }
    }

    /** 不退：+2 击退抗性 */
    public static class Unyielding extends TCLibBaseModifier implements CurioAttributeModifierHook {
        private static final double KNOCKBACK_RESISTANCE_BONUS = 0.2;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "gem_unyielding", Attributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_BONUS, AttributeModifier.Operation.ADDITION);
        }
    }

    /** 抑影：阻止周围实体传送 */
    public static class Suppression extends TCLibBaseModifier {
    }

    /** 海泳：提升穿戴者的游泳速度 */
    public static class SeaSwim extends TCLibBaseModifier implements CurioAttributeModifierHook {
        private static final double SWIM_SPEED_BONUS = 0.5;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "gem_sea_swim", ForgeMod.SWIM_SPEED.get(), SWIM_SPEED_BONUS, AttributeModifier.Operation.ADDITION);
        }
    }

    /** 反重力：获得缓降效果 */
    public static class Antigravity extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioEquipmentChangeModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (!entity.level().isClientSide) {
                grantInfiniteEffect(entity, MobEffects.SLOW_FALLING);
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide) {
                removeInfiniteEffect(entity, MobEffects.SLOW_FALLING);
            }
        }
    }

    /** 淬毒：每次攻击有 15% 概率使目标中毒 5~10 秒 */
    public static class Envenom extends TCLibBaseModifier implements CurioDamageTargetPostModifierHook {
        private static final float CHANCE = 0.15f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            if (target.level().isClientSide) {
                return;
            }
            RandomSource random = target.getRandom();
            if (random.nextFloat() < CHANCE) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 100 + random.nextInt(101), 0));
            }
        }
    }

    /** 汲血：每次攻击按造成伤害的 5% 治疗自身 */
    public static class Bloodthirst extends TCLibBaseModifier implements CurioDamageTargetPostModifierHook {
        private static final float HEAL_RATIO = 0.05f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            if (!attacker.level().isClientSide && event.getAmount() > 0) {
                attacker.heal(event.getAmount() * HEAL_RATIO);
            }
        }
    }

    /** 生命吸取：每次攻击按造成伤害的 50% 治疗自身 */
    public static class LifeSteal extends TCLibBaseModifier implements CurioDamageTargetPostModifierHook {
        private static final float HEAL_RATIO = 0.5f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            if (!attacker.level().isClientSide && event.getAmount() > 0) {
                attacker.heal(event.getAmount() * HEAL_RATIO);
            }
        }
    }

    /** 抵抗：每秒有 5% 概率获得 5 秒抗性提升效果 */
    public static class GemResistance extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        private static final float CHANCE = 0.05f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity)) {
                return;
            }
            if (entity.getRandom().nextFloat() < CHANCE) {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0));
            }
        }
    }

    /** 冰甲：受到攻击时有 5% 概率不受到伤害 */
    public static class IceArmor extends TCLibBaseModifier implements CurioTakeDamagePreModifierHook {
        private static final float CHANCE = 0.05f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TAKE_DAMAGE_PRE);
        }

        @Override
        public void onCurioTakeDamagePre(IToolStackView tool, ModifierEntry entry, LivingHurtEvent event, LivingEntity entity, net.minecraft.world.damagesource.DamageSource source) {
            if (entity.level().isClientSide || event.getAmount() <= 0) {
                return;
            }
            if (entity.getRandom().nextFloat() < CHANCE) {
                event.setAmount(0);
            }
        }
    }

    /** 抗火：获得无限的抗火效果 */
    public static class Fireproof extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioEquipmentChangeModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (!entity.level().isClientSide) {
                grantInfiniteEffect(entity, MobEffects.FIRE_RESISTANCE);
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide) {
                removeInfiniteEffect(entity, MobEffects.FIRE_RESISTANCE);
            }
        }
    }

    /** 时运：佩戴者破坏方块时获得相当于特性等级的时运 */
    public static class Fortune extends TCLibBaseModifier implements CurioFortuneModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_FORTUNE);
        }

        @Override
        public int onCurioUpdateFortune(IToolStackView tool, ModifierEntry entry, SlotContext context,
                                        LootContext lootContext, ItemStack stack, int fortune) {
            return fortune + entry.getLevel();
        }
    }

    /** 翠绿：增加 8 点护甲韧性 */
    public static class Verdant extends TCLibBaseModifier implements CurioAttributeModifierHook {
        private static final double ARMOR_TOUGHNESS_BONUS = 8.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "verdant_toughness", Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_BONUS,
                    AttributeModifier.Operation.ADDITION);
        }
    }

    /** 无瑕：增加 1 个能力槽，并获得 4 点护甲与 2 点护甲韧性 */
    public static class Flawless extends TCLibBaseModifier
            implements CurioAttributeModifierHook, VolatileDataModifierHook {
        private static final double ARMOR_BONUS = 4.0;
        private static final double ARMOR_TOUGHNESS_BONUS = 2.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
            builder.addHook(this, ModifierHooks.VOLATILE_DATA);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "flawless_armor", Attributes.ARMOR, ARMOR_BONUS,
                    AttributeModifier.Operation.ADDITION);
            addAttribute(consumer, uuid, "flawless_toughness", Attributes.ARMOR_TOUGHNESS, ARMOR_TOUGHNESS_BONUS,
                    AttributeModifier.Operation.ADDITION);
        }

        @Override
        public void addVolatileData(IToolContext context, ModifierEntry entry, ToolDataNBT volatileData) {
            volatileData.addSlots(SlotType.ABILITY, entry.getLevel());
        }
    }

    /** 细腻：增加 1 个升级槽 */
    public static class Silky extends TCLibBaseModifier implements VolatileDataModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, ModifierHooks.VOLATILE_DATA);
        }

        @Override
        public void addVolatileData(IToolContext context, ModifierEntry entry, ToolDataNBT volatileData) {
            volatileData.addSlots(SlotType.UPGRADE, entry.getLevel());
        }
    }

    /** 寒冰：每次攻击使目标冻伤 5 秒 */
    public static class Frost extends TCLibBaseModifier implements CurioDamageTargetPostModifierHook {
        private static final int FREEZE_TICKS = 100;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            if (attacker.level().isClientSide) {
                return;
            }
            int required = target.getTicksRequiredToFreeze();
            target.setTicksFrozen(Math.max(target.getTicksFrozen(), required) + FREEZE_TICKS * entry.getLevel());
        }
    }

    /** 飞行：允许佩戴者获得 30 秒创造模式飞行时间（实际逻辑在玩家 tick 中处理） */
    public static class Flight extends TCLibBaseModifier {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
        }
    }

    /** 水肺：获得无限的水下呼吸效果 */
    public static class WaterBreathing extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioEquipmentChangeModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (!entity.level().isClientSide) {
                grantInfiniteEffect(entity, MobEffects.WATER_BREATHING);
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide) {
                removeInfiniteEffect(entity, MobEffects.WATER_BREATHING);
            }
        }
    }

    /** 夜视：获得无限的夜视效果 */
    public static class NightVision extends TCLibBaseModifier
            implements CurioInventoryTickModifierHook, CurioEquipmentChangeModifierHook {
        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
            builder.addHook(this, TCLibHooks.CURIO_EQUIPMENT_CHANGE);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (!entity.level().isClientSide) {
                grantInfiniteEffect(entity, MobEffects.NIGHT_VISION);
            }
        }

        @Override
        public void onCurioUnequip(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack, ItemStack newStack) {
            if (!entity.level().isClientSide) {
                removeInfiniteEffect(entity, MobEffects.NIGHT_VISION);
            }
        }
    }

    /** 发光：使周围实体获得无限发光效果 */
    public static class Luminous extends TCLibBaseModifier implements CurioInventoryTickModifierHook {
        private static final double RANGE = 8.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_TICK);
        }

        @Override
        public void onCurioTick(IToolStackView tool, ModifierEntry entry, SlotContext context, LivingEntity entity, ItemStack stack) {
            if (entity.level().isClientSide || !isSecond(entity)) {
                return;
            }
            for (Entity other : entity.level().getEntities(entity, entity.getBoundingBox().inflate(RANGE))) {
                if (other instanceof LivingEntity living) {
                    grantInfiniteEffect(living, MobEffects.GLOWING);
                }
            }
        }
    }

    /** 辐射：每次攻击有 50% 概率使目标凋零 5~10 秒 */
    public static class Radiation extends TCLibBaseModifier implements CurioDamageTargetPostModifierHook {
        private static final float CHANCE = 0.5f;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            if (target.level().isClientSide) {
                return;
            }
            RandomSource random = target.getRandom();
            if (random.nextFloat() < CHANCE) {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 100 + random.nextInt(101), 0));
            }
        }
    }

    /** 强硬：+1 攻击伤害 */
    public static class Hardness extends TCLibBaseModifier implements CurioAttributeModifierHook {
        private static final double ATTACK_DAMAGE_BONUS = 1.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "gem_hardness_attack", Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_BONUS, AttributeModifier.Operation.ADDITION);
        }
    }

    /** 烈火：每次攻击点燃目标 5 秒 */
    public static class Flame extends TCLibBaseModifier implements CurioDamageTargetPostModifierHook {
        private static final int FIRE_SECONDS = 5;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_DAMAGE_TARGET_POST);
        }

        @Override
        public void onCurioDamageTargetPost(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity attacker, LivingEntity target) {
            if (!target.level().isClientSide) {
                target.setSecondsOnFire(FIRE_SECONDS);
            }
        }
    }

    /** 龙吼：暴击伤害 +10% */
    public static class DragonRoar extends TCLibBaseModifier {
    }

    /** 长手：+1 触及距离 */
    public static class LongArm extends TCLibBaseModifier implements CurioAttributeModifierHook {
        private static final double REACH_BONUS = 1.0;

        @Override
        protected void registerHooks(ModuleHookMap.Builder builder) {
            super.registerHooks(builder);
            builder.addHook(this, TCLibHooks.CURIO_ATTRIBUTE);
        }

        @Override
        public void modifyCurioAttribute(IToolStackView tool, ModifierEntry entry, SlotContext context, UUID uuid,
                                         java.util.function.BiConsumer<Attribute, AttributeModifier> consumer) {
            addAttribute(consumer, uuid, "gem_long_arm_block", ForgeMod.BLOCK_REACH.get(), REACH_BONUS, AttributeModifier.Operation.ADDITION);
            addAttribute(consumer, uuid, "gem_long_arm_entity", ForgeMod.ENTITY_REACH.get(), REACH_BONUS, AttributeModifier.Operation.ADDITION);
        }
    }

    // ======================================================================
    // 通用工具方法
    // ======================================================================

    private static boolean isSecond(LivingEntity entity) {
        return entity.level().getGameTime() % 20 == 0;
    }

    /** 对主副手工具临时注入强化，其余位置的工具则还原 */
    private static void updateHeldInjection(Player player, ModifierId id, ResourceLocation flag, boolean active, boolean drawSpeedOnly, boolean onlyIfAbsent) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (active) {
            applyInjected(main, id, flag, drawSpeedOnly, onlyIfAbsent);
            if (!off.isEmpty() && off != main) {
                applyInjected(off, id, flag, drawSpeedOnly, onlyIfAbsent);
            }
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack != main && stack != off) {
                revertInjected(stack, id, flag);
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            revertInjected(stack, id, flag);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack != main && stack != off) {
                revertInjected(stack, id, flag);
            }
        }
    }

    private static void applyInjected(ItemStack stack, ModifierId id, ResourceLocation flag, boolean drawSpeedOnly, boolean onlyIfAbsent) {
        if (stack.isEmpty() || !ToolStack.isInitialized(stack)) {
            return;
        }
        if (drawSpeedOnly && !ToolStats.DRAW_SPEED.supports(stack.getItem())) {
            return;
        }
        ToolStack tool = ToolStack.from(stack);
        if (tool.getPersistentData().getBoolean(flag)) {
            return;
        }
        if (onlyIfAbsent && tool.getModifiers().getLevel(id) > 0) {
            return;
        }
        tool.addModifier(id, 1);
        tool.getPersistentData().putBoolean(flag, true);
    }

    private static void revertInjected(ItemStack stack, ModifierId id, ResourceLocation flag) {
        if (stack.isEmpty() || !ToolStack.isInitialized(stack)) {
            return;
        }
        ToolStack tool = ToolStack.from(stack);
        if (!tool.getPersistentData().getBoolean(flag)) {
            return;
        }
        tool.removeModifier(id, 1);
        tool.getPersistentData().remove(flag);
    }

    /** 判断实体所佩戴的饰品是否带有指定特性 */
    private static boolean hasCurioTrait(LivingEntity entity, ModifierId id) {
        for (ItemStack stack : curioStacks(entity)) {
            if (!stack.isEmpty() && ToolStack.isInitialized(stack) && ToolStack.from(stack).getModifiers().getLevel(id) > 0) {
                return true;
            }
        }
        return false;
    }

    private static void grantInfiniteEffect(LivingEntity entity, MobEffect effect) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current == null || current.getDuration() > 0) {
            entity.addEffect(new MobEffectInstance(effect, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        }
    }

    private static void removeInfiniteEffect(LivingEntity entity, MobEffect effect) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current != null && current.getDuration() == MobEffectInstance.INFINITE_DURATION) {
            entity.removeEffect(effect);
        }
    }

    /** 抑影：阻止附近佩戴者周围的实体传送 */
    private static void onEntityTeleport(EntityTeleportEvent event) {
        if (event.isCanceled() || event instanceof EntityTeleportEvent.EnderPearl) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity) || entity.level().isClientSide) {
            return;
        }
        Level level = entity.level();
        for (Player player : level.players()) {
            if (player == entity || player.distanceToSqr(entity) > 64.0) {
                continue;
            }
            if (hasCurioTrait(player, SUPPRESSION.getId())) {
                event.setCanceled(true);
                return;
            }
        }
    }

    /** 龙吼：佩戴者暴击时提高暴击伤害 */
    private static void onCriticalHit(CriticalHitEvent event) {
        Player player = event.getEntity();
        if (event.isVanillaCritical() && hasCurioTrait(player, DRAGON_ROAR.getId())) {
            event.setDamageModifier(event.getDamageModifier() * 1.1f);
        }
    }

    private static List<ItemStack> gear(LivingEntity entity) {
        List<ItemStack> stacks = new ArrayList<>(6);
        stacks.add(entity.getMainHandItem());
        stacks.add(entity.getOffhandItem());
        stacks.add(entity.getItemBySlot(EquipmentSlot.HEAD));
        stacks.add(entity.getItemBySlot(EquipmentSlot.CHEST));
        stacks.add(entity.getItemBySlot(EquipmentSlot.LEGS));
        stacks.add(entity.getItemBySlot(EquipmentSlot.FEET));
        return stacks;
    }

    private static List<ItemStack> curioStacks(LivingEntity entity) {
        List<ItemStack> stacks = new ArrayList<>();
        CuriosApi.getCuriosInventory(entity).ifPresent(handler -> {
            for (ICurioStacksHandler handlerEntry : handler.getCurios().values()) {
                IDynamicStackHandler slotHandler = handlerEntry.getStacks();
                for (int i = 0; i < slotHandler.getSlots(); i++) {
                    stacks.add(slotHandler.getStackInSlot(i));
                }
            }
        });
        return stacks;
    }

    private static boolean isWornGear(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (ItemStack gear : gear(entity)) {
            if (ItemStack.matches(gear, stack)) {
                return true;
            }
        }
        return false;
    }

    /** 有耐久且可被修复的装备 */
    private static boolean isTool(ItemStack stack) {
        return !stack.isEmpty() && (ToolStack.isInitialized(stack) || stack.getMaxDamage() > 0);
    }

    private static int maxDurability(ItemStack stack) {
        if (ToolStack.isInitialized(stack)) {
            return Math.max(1, ToolStack.from(stack).getStats().getInt(ToolStats.DURABILITY));
        }
        return Math.max(1, stack.getMaxDamage());
    }

    private static int damage(ItemStack stack) {
        if (ToolStack.isInitialized(stack)) {
            return ToolStack.from(stack).getDamage();
        }
        return stack.getDamageValue();
    }

    private static void setDamage(ItemStack stack, int value) {
        int clamped = Math.max(0, Math.min(maxDurability(stack) - 1, value));
        if (ToolStack.isInitialized(stack)) {
            ToolStack.from(stack).setDamage(clamped);
        } else {
            stack.setDamageValue(clamped);
        }
    }

    private static void repair(ItemStack stack, int amount) {
        setDamage(stack, damage(stack) - amount);
    }

    // ---- 树皮覆层 ----

    private static int getBark(ItemStack stack) {
        if (ToolStack.isInitialized(stack)) {
            return ToolStack.from(stack).getPersistentData().getInt(BARK_KEY);
        }
        CompoundTag tag = stack.getTag();
        return tag == null ? 0 : tag.getInt(BARK_TAG);
    }

    private static void setBark(ItemStack stack, int value) {
        if (ToolStack.isInitialized(stack)) {
            ToolStack.from(stack).getPersistentData().putInt(BARK_KEY, value);
        } else {
            stack.getOrCreateTag().putInt(BARK_TAG, value);
        }
    }

    private static void addBark(ItemStack stack, int amount) {
        if (!isTool(stack) || amount <= 0) {
            return;
        }
        setBark(stack, Math.min(100 + maxDurability(stack), getBark(stack) + amount));
    }

    /** 消耗树皮覆层抵消耐久损失，返回未被抵消的损失量 */
    private static int consumeBark(ItemStack stack, int amount) {
        int current = getBark(stack);
        if (current <= 0 || amount <= 0) {
            return amount;
        }
        int absorbed = Math.min(current, amount);
        setBark(stack, current - absorbed);
        return amount - absorbed;
    }

    // ---- 黏液覆层 ----

    private static boolean supportsOverslime(ItemStack stack) {
        return ToolStack.isInitialized(stack);
    }

    private static int getOverslime(ItemStack stack) {
        return supportsOverslime(stack) ? OverslimeModule.INSTANCE.getAmount(ToolStack.from(stack)) : 0;
    }

    private static int overslimeCapacity(ItemStack stack) {
        return supportsOverslime(stack) ? OverslimeModule.getCapacity(ToolStack.from(stack)) : 0;
    }

    private static int overslimeCapacity(ItemStack stack, int minimum) {
        return Math.max(minimum, overslimeCapacity(stack));
    }

    private static void addOverslime(ItemStack stack, int amount, int capacity) {
        if (!supportsOverslime(stack) || amount <= 0) {
            return;
        }
        ToolStack tool = ToolStack.from(stack);
        int current = OverslimeModule.INSTANCE.getAmount(tool);
        OverslimeModule.INSTANCE.setAmountRaw(tool.getPersistentData(), Math.min(capacity, current + amount));
    }

    /** 饰品提供的黏液覆层上限（非黏液装备也能获得） */
    private static int curioOverslimeCapacity(LivingEntity entity) {
        int total = 0;
        for (ItemStack stack : curioStacks(entity)) {
            if (!ToolStack.isInitialized(stack)) {
                continue;
            }
            ToolStack tool = ToolStack.from(stack);
            if (tool.getModifierLevel(OVERCOAT.getId()) > 0) {
                total += OVERCOAT_CAPACITY;
            }
            total += OVERLOAD_CAPACITY * tool.getModifierLevel(OVERLOAD.getId());
            if (tool.getModifierLevel(OVERFORCE.getId()) > 0) {
                total += OVERFORCE_CAPACITY;
            }
            if (tool.getModifierLevel(OVERCAPACITOR.getId()) > 0) {
                total += OVERCAPACITOR_CAPACITY;
            }
            if (tool.getModifierLevel(OVERGRIND.getId()) > 0) {
                total += OVERGRIND_CAPACITY;
            }
        }
        return total;
    }

    /** 是否存在解除黏液覆层排异的饰品特性 */
    private static boolean curioRemovesOverslimeRejection(LivingEntity entity) {
        for (ItemStack stack : curioStacks(entity)) {
            if (!ToolStack.isInitialized(stack)) {
                continue;
            }
            ToolStack tool = ToolStack.from(stack);
            if (tool.getModifierLevel(OVERLOAD.getId()) > 0 || tool.getModifierLevel(OVERFORCE.getId()) > 0
                    || tool.getModifierLevel(OVERGRIND.getId()) > 0) {
                return true;
            }
        }
        return false;
    }

    /** 是否存在提供 FE 存储的饰品特性 */
    private static boolean curioProvidesEnergyStorage(LivingEntity entity) {
        for (ItemStack stack : curioStacks(entity)) {
            if (ToolStack.isInitialized(stack) && ToolStack.from(stack).getModifierLevel(OVERCAPACITOR.getId()) > 0) {
                return true;
            }
        }
        return false;
    }

    /** 为主副手与所穿护甲同步黏液覆层容量，其余背包物品恢复原状 */
    private static void updateOverslimeSupport(LivingEntity entity) {
        int targetLevel = curioOverslimeCapacity(entity) / OVERSLIME_CAPACITY_PER_LEVEL;
        boolean removeRejection = targetLevel > 0 && curioRemovesOverslimeRejection(entity);
        boolean energyStorage = targetLevel > 0 && curioProvidesEnergyStorage(entity);
        for (ItemStack stack : gear(entity)) {
            updateOverslimeInjection(stack, targetLevel, removeRejection, energyStorage);
        }
        if (entity instanceof Player player) {
            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            for (ItemStack stack : player.getInventory().items) {
                if (stack != main && stack != off) {
                    updateOverslimeInjection(stack, 0, false, false);
                }
            }
        }
    }

    /** 注入/移除黏液覆层容量强化与基础黏液覆层，并维护“黏液之友”标记 */
    private static void updateOverslimeInjection(ItemStack stack, int targetCapacityLevels, boolean overslimeFriend,
                                                 boolean energyStorage) {
        if (stack.isEmpty() || !ToolStack.isInitialized(stack)) {
            return;
        }
        ToolStack tool = ToolStack.from(stack);
        int injected = tool.getPersistentData().getInt(OVERSLIME_INJECT_KEY);
        if (targetCapacityLevels > 0) {
            // 非黏液装备没有黏液覆层强化，先注入基础强化以获得耐久护盾
            boolean hadNaturalBase = !tool.getPersistentData().getBoolean(OVERSLIME_BASE_FLAG)
                    && tool.getModifierLevel(OVERSLIME_ID) > 0;
            if (!hadNaturalBase && tool.getModifierLevel(OVERSLIME_ID) <= 0) {
                tool.addModifier(OVERSLIME_ID, 1);
                tool.getPersistentData().putBoolean(OVERSLIME_BASE_FLAG, true);
            }
            // 基础强化固定提供 50 点上限，其余容量由隐藏强化按等级补齐
            int capacityLevels = hadNaturalBase ? targetCapacityLevels : targetCapacityLevels - 1;
            if (capacityLevels > injected) {
                tool.addModifier(OVERSLIME_CAPACITY_MODIFIER.getId(), capacityLevels - injected);
                tool.getPersistentData().putInt(OVERSLIME_INJECT_KEY, capacityLevels);
            } else if (capacityLevels < injected) {
                tool.removeModifier(OVERSLIME_CAPACITY_MODIFIER.getId(), injected - capacityLevels);
                if (capacityLevels > 0) {
                    tool.getPersistentData().putInt(OVERSLIME_INJECT_KEY, capacityLevels);
                } else {
                    tool.getPersistentData().remove(OVERSLIME_INJECT_KEY);
                }
            }
        } else {
            if (injected > 0) {
                tool.removeModifier(OVERSLIME_CAPACITY_MODIFIER.getId(), injected);
                tool.getPersistentData().remove(OVERSLIME_INJECT_KEY);
            }
            if (tool.getPersistentData().getBoolean(OVERSLIME_BASE_FLAG)) {
                tool.removeModifier(OVERSLIME_ID, 1);
                tool.getPersistentData().remove(OVERSLIME_BASE_FLAG);
            }
        }
        boolean flagged = tool.getPersistentData().getBoolean(OVERSLIME_FRIEND_FLAG);
        if (targetCapacityLevels > 0 && overslimeFriend) {
            if (!flagged) {
                tool.addModifier(OVERSLIME_FRIEND_ID, 1);
                tool.getPersistentData().putBoolean(OVERSLIME_FRIEND_FLAG, true);
            }
        } else if (flagged) {
            tool.removeModifier(OVERSLIME_FRIEND_ID, 1);
            tool.getPersistentData().remove(OVERSLIME_FRIEND_FLAG);
        }
        boolean energyFlagged = tool.getPersistentData().getBoolean(OVERSLIME_ENERGY_FLAG);
        if (targetCapacityLevels > 0 && energyStorage) {
            if (!energyFlagged) {
                tool.addModifier(OVERSLIME_ENERGY_MODIFIER.getId(), 1);
                tool.getPersistentData().putBoolean(OVERSLIME_ENERGY_FLAG, true);
            }
        } else if (energyFlagged) {
            tool.removeModifier(OVERSLIME_ENERGY_MODIFIER.getId(), 1);
            tool.getPersistentData().remove(OVERSLIME_ENERGY_FLAG);
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) {
            return;
        }
        updateOverslimeSupport(event.player);
        updateCreativeFlight(event.player);
    }

    /** 飞行特性：在地面充能后提供最长 30 秒的创造模式飞行，摘下后立即收回 */
    private static void updateCreativeFlight(Player player) {
        boolean hasFlight = hasCurioTrait(player, FLIGHT.getId());
        CompoundTag data = player.getPersistentData();
        boolean granted = data.getBoolean(FLIGHT_GRANTED_KEY);
        long gameTime = player.level().getGameTime();
        if (hasFlight) {
            if (player.onGround() || !granted) {
                data.putLong(FLIGHT_EXPIRY_KEY, gameTime + FLIGHT_DURATION_TICKS);
            }
            if (gameTime < data.getLong(FLIGHT_EXPIRY_KEY)) {
                if (!player.getAbilities().mayfly) {
                    player.getAbilities().mayfly = true;
                    player.onUpdateAbilities();
                }
                data.putBoolean(FLIGHT_GRANTED_KEY, true);
                return;
            }
        }
        if (granted) {
            if (!player.isCreative() && !player.isSpectator()) {
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
                player.onUpdateAbilities();
            }
            data.remove(FLIGHT_GRANTED_KEY);
            data.remove(FLIGHT_EXPIRY_KEY);
        }
    }

    private static boolean anyGearHasOverslime(LivingEntity entity) {
        for (ItemStack stack : gear(entity)) {
            if (getOverslime(stack) > 0) {
                return true;
            }
        }
        return false;
    }

    // ---- 匠魂弓的拉弓速度与远程初速度 ----

    /** 为手持的匠魂弓追加/移除拉弓速度与远程初速度，其余位置的弓还原 */
    private static void updateBowStatBoosts(Player player, boolean boost) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        if (boost) {
            applyBowStatBoost(main);
            if (!off.isEmpty() && off != main) {
                applyBowStatBoost(off);
            }
        } else {
            revertBowStatBoost(main);
            revertBowStatBoost(off);
        }
        for (ItemStack stack : player.getInventory().items) {
            if (stack != main && stack != off) {
                revertBowStatBoost(stack);
            }
        }
        for (ItemStack stack : player.getInventory().armor) {
            revertBowStatBoost(stack);
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack != main && stack != off) {
                revertBowStatBoost(stack);
            }
        }
    }

    private static void applyBowStatBoost(ItemStack stack) {
        if (stack.isEmpty() || !ToolStack.isInitialized(stack) || !ToolStats.DRAW_SPEED.supports(stack.getItem())) {
            return;
        }
        ToolStack tool = ToolStack.from(stack);
        IModDataView data = tool.getPersistentData();
        if (data.getBoolean(BOW_BOOST_FLAG)) {
            return;
        }
        CompoundTag root = stack.getTag();
        if (root == null) {
            return;
        }
        CompoundTag multipliers = root.getCompound(MULTIPLIERS_TAG);
        float drawSpeed = multipliers.contains(DRAW_SPEED_STAT) ? multipliers.getFloat(DRAW_SPEED_STAT) : 1.0f;
        float velocity = multipliers.contains(VELOCITY_STAT) ? multipliers.getFloat(VELOCITY_STAT) : 1.0f;
        tool.getPersistentData().putFloat(BOW_BOOST_DRAW, drawSpeed);
        tool.getPersistentData().putFloat(BOW_BOOST_VELOCITY, velocity);
        multipliers.putFloat(DRAW_SPEED_STAT, drawSpeed + BOW_STAT_BONUS);
        multipliers.putFloat(VELOCITY_STAT, velocity + BOW_STAT_BONUS);
        root.put(MULTIPLIERS_TAG, multipliers);
        tool.getPersistentData().putBoolean(BOW_BOOST_FLAG, true);
    }

    private static void revertBowStatBoost(ItemStack stack) {
        if (stack.isEmpty() || !ToolStack.isInitialized(stack)) {
            return;
        }
        ToolStack tool = ToolStack.from(stack);
        IModDataView data = tool.getPersistentData();
        if (!data.getBoolean(BOW_BOOST_FLAG)) {
            return;
        }
        CompoundTag root = stack.getTag();
        if (root == null) {
            return;
        }
        CompoundTag multipliers = root.getCompound(MULTIPLIERS_TAG);
        multipliers.putFloat(DRAW_SPEED_STAT, data.getFloat(BOW_BOOST_DRAW));
        multipliers.putFloat(VELOCITY_STAT, data.getFloat(BOW_BOOST_VELOCITY));
        root.put(MULTIPLIERS_TAG, multipliers);
        tool.getPersistentData().remove(BOW_BOOST_FLAG);
        tool.getPersistentData().remove(BOW_BOOST_DRAW);
        tool.getPersistentData().remove(BOW_BOOST_VELOCITY);
    }

    // ---- 环境温度 ----

    private static float temperature(LivingEntity entity) {
        return entity.level().getBiome(entity.blockPosition()).value().getBaseTemperature();
    }

    private static boolean isCold(LivingEntity entity) {
        return temperature(entity) < 0.5f;
    }

    private static boolean isHot(LivingEntity entity) {
        return temperature(entity) > 1.0f;
    }

    // ---- 属性 ----

    private static UUID modifierUUID(LivingEntity entity, String key) {
        return UUID.nameUUIDFromBytes((entity.getUUID() + "|" + key).getBytes(StandardCharsets.UTF_8));
    }

    private static void applyAttackSpeed(LivingEntity entity, String key, double amount) {
        if (!(entity instanceof Player player)) {
            return;
        }
        AttributeInstance instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance == null) {
            return;
        }
        UUID uuid = modifierUUID(player, key);
        instance.removeModifier(uuid);
        if (amount != 0) {
            instance.addTransientModifier(new AttributeModifier(uuid, "tinkers_bauble." + key, amount, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void applyAttackSpeedMultiplier(LivingEntity entity, String key, double amount) {
        if (!(entity instanceof Player player)) {
            return;
        }
        AttributeInstance instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance == null) {
            return;
        }
        UUID uuid = modifierUUID(player, key);
        instance.removeModifier(uuid);
        if (amount != 0) {
            instance.addTransientModifier(new AttributeModifier(uuid, "tinkers_bauble." + key, amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    private static void addAttribute(java.util.function.BiConsumer<Attribute, AttributeModifier> consumer, UUID uuid,
                                     String name, Attribute attribute, double amount, AttributeModifier.Operation operation) {
        if (attribute == null || amount == 0) {
            return;
        }
        UUID derived = UUID.nameUUIDFromBytes((uuid + "|" + name).getBytes(StandardCharsets.UTF_8));
        consumer.accept(attribute, new AttributeModifier(derived, "tinkers_bauble." + name, amount, operation));
    }
}
