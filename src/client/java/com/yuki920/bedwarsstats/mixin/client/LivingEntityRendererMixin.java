package com.yuki920.bedwarsstats.mixin.client;

import com.yuki920.bedwarsstats.PrestigeFormatter;
import com.yuki920.bedwarsstats.cache.PlayerStarCache;
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> {

    protected LivingEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(method = "renderLabelIfPresent(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    private void onRenderLabel(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Only run for players
        if (!(entity instanceof AbstractClientPlayerEntity playerEntity)) {
            return;
        }

        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
        if (!config.nametag.showNametagLevel) {
            return;
        }

        if (playerEntity.isSneaking()) {
            return;
        }

        Integer stars = PlayerStarCache.getStars(playerEntity.getUuid());
        if (stars != null) {
            String starTextStr = "§bBed Wars Level: " + PrestigeFormatter.formatPrestige(stars);
            Text starText = Text.literal(starTextStr);

            float height = playerEntity.getHeight() + 0.75f;
            matrices.push();
            matrices.translate(0.0D, height, 0.0D);
            matrices.multiply(this.dispatcher.getRotation());
            matrices.scale(-0.025F, -0.025F, 0.025F);

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();
            TextRenderer textRenderer = this.getTextRenderer();
            float x = -textRenderer.getWidth(starText) / 2.0f;

            textRenderer.draw(starText, x, 0, 0xFFFFFF, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.SEE_THROUGH, 0, light);
            textRenderer.draw(starText, x, 0, 0xFFFFFF, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

            matrices.pop();
        }
    }
}
