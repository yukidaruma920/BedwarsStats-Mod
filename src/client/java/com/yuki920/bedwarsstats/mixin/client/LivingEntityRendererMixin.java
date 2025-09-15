package com.yuki920.bedwarsstats.mixin.client;

import com.yuki920.bedwarsstats.PrestigeFormatter;
import com.yuki920.bedwarsstats.cache.PlayerStarCache;
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @ModifyVariable(
        method = "renderLabelIfPresent(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(value = "HEAD"),
        argsOnly = true
    )
    private Text modifyLabelText(Text originalText, T entity) {
        // Only run for players
        if (!(entity instanceof AbstractClientPlayerEntity playerEntity)) {
            return originalText;
        }

        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
        if (!config.nametag.showNametagLevel) {
            return originalText;
        }

        Integer stars = PlayerStarCache.getStars(playerEntity.getUuid());
        if (stars != null) {
            String starTextStr = "§bBed Wars Level: " + PrestigeFormatter.formatPrestige(stars);
            MutableText starText = Text.literal(starTextStr);

            // Combine the new text with the original player name, with a newline in between.
            return starText.append("\n").append(originalText);
        }

        return originalText;
    }
}
