package cn.blockforge.generated.mod6854d293;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图书管理员高等级附魔书交易刷新。
 *
 * 全部玩法逻辑由 EnchantBookFactoryMixin 完成（附魔书等级恒取该附魔的最高等级），
 * 这里只负责在启动日志里确认模组已加载。
 */
public final class GeneratedMod implements ModInitializer {
    public static final String MOD_ID = "mod_6854d293";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("[{}] 已启用：图书管理员的附魔书将总是以最高等级刷新（定价与刷新沿用原版机制）。", MOD_ID);
    }
}
