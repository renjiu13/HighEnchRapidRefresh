package cn.blockforge.generated.mod6854d293.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 图书管理员高等级附魔书：
 *
 * 原版 TradeOffers.EnchantBookFactory.create 的流程是——
 *   1. 在所有"可作为附魔书出售"的附魔里随机挑一种（本模组不改动，保持全随机、不做筛选）；
 *   2. 用 MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel())
 *      在最低~最高等级之间随机出这本书的等级；
 *   3. 按等级用原版公式定价（2 + 随机(5 + 等级*10) + 等级*3，宝藏附魔翻倍，上限 64 绿宝石）。
 *
 * 本 mixin 只拦截第 2 步：随机等级改为恒取该附魔允许的最高等级（Sharpness V、
 * Efficiency V、Unbreaking III、Protection IV……即优先呈现四级/五级效果）。
 * 第 1、3 步与交易次数、经验、价格公式全部保留原版，所以高等级书对应原版公式下
 * 的较高价格，刷新机制也仍是原版每天两次的自动补货——平衡不破坏，反馈走原版
 * 交易界面的附魔书提示。
 */
@Mixin(TradeOffers.EnchantBookFactory.class)
public class EnchantBookFactoryMixin {

    /**
     * 拦截原版里的 MathHelper.nextInt(random, minLevel, maxLevel) 这次调用，
     * 直接返回上界（= enchantment.getMaxLevel()），不再随机压等级。
     */
    @Redirect(
        method = "create",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"
        )
    )
    private int alwaysUseMaxEnchantmentLevel(Random random, int min, int max) {
        return max;
    }
}
