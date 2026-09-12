package cn.blockforge.generated.mod6854d293.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 图书管理员高等级附魔书 + 低价封顶：
 *
 * 原版 TradeOffers.EnchantBookFactory.create 的流程——
 *   1. 随机挑一种附魔（不改动，保持全随机）；
 *   2. 用 MathHelper.nextInt(random, min, max) 随机出等级；
 *   3. 按等级定价（2 + random(5 + 等级*10) + 等级*3，宝藏附魔翻倍，上限 64）。
 *
 * 本 mixin 拦截第 2 步（恒取最高等级）并在返回前封顶价格：
 *   - 等级 = enchantment.getMaxLevel()（Sharpness V、Efficiency V、Protection IV…）
 *   - 绿宝石价格 > 19 时封顶为 19，确保所有交易 ≤ 19 绿宝石
 *
 * 附魔种类、刷新机制、库存、经验与原版交易界面反馈全部保留。
 */
@Mixin(TradeOffers.EnchantBookFactory.class)
public class EnchantBookFactoryMixin {

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

    @Inject(method = "create", at = @At("RETURN"), cancellable = true)
    private void capPriceUnder20(CallbackInfoReturnable<TradeOffer> cir) {
        TradeOffer original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        ItemStack firstBuy = original.getOriginalFirstBuyItem();
        if (firstBuy.getCount() <= 19) {
            return;
        }
        ItemStack cappedBuy = firstBuy.copy();
        cappedBuy.setCount(19);
        cir.setReturnValue(new TradeOffer(
            cappedBuy,
            original.getSecondBuyItem().orElse(ItemStack.EMPTY),
            original.getSellItem(),
            original.getUses(),
            original.getMaxUses(),
            original.getMerchantExperience(),
            original.getPriceMultiplier(),
            original.getDemandBonus()
        ));
    }
}
