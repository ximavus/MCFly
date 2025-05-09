package name.modid;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import net.minecraft.util.TimeHelper;

import java.util.Objects;


public class FlyClient implements ClientModInitializer {
	private static final KeyBinding RandomScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.randscreen", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.fly.mystuff"));
	private static final KeyBinding FlyUp = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_K, "category.fly.mystuff"));
	private static final KeyBinding toggleGravity = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.nog", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.fly.mystuff"));
	private static final KeyBinding FlyDown = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "category.fly.mystuff"));
	private static final KeyBinding toggleKillAura = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.aura", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.fly.mystuff"));
	@Override
	public void onInitializeClient() {
		MinecraftClient initializeClient = MinecraftClient.getInstance();
		double attackSpeed = initializeClient.attackCooldown;

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleGravity.wasPressed()) {
                assert client.player != null;
                client.player.setNoGravity(!client.player.hasNoGravity());
			}
			while (FlyUp.wasPressed()) {
				assert client.player != null;
				client.player.addVelocity(0.0, 0.75 ,0.0);
			}
			while (FlyDown.wasPressed()) {
                assert client.player != null;
                client.player.addVelocity(0, -0.75, 0);
			}
			while (toggleKillAura.isPressed()) {
				assert client.player != null;
				assert client.world != null;
				Iterable<Entity> players = client.world.getEntities();
				players.forEach(player -> {
					Packet<ServerPlayPacketListener> packet = PlayerInteractEntityC2SPacket.attack(player, player.isSneaking());
					if (client.player.distanceTo(player) > 3)
						client.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, player.getEyePos());
						Objects.requireNonNull(client.getNetworkHandler()).sendPacket(packet);
                    	try {
							new TimeHelper().wait((long) attackSpeed * 1000);
                    	} catch (InterruptedException e) {
                        	throw new RuntimeException(e);
                    	}
                });
			}
			if (RandomScreen.wasPressed()) {
				HudRenderCallback.EVENT.register((drawContext, tickDeltaManager) -> {
					Matrix4f transformationMatrix = drawContext.getMatrices().peek().getPositionMatrix();
					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
					buffer.vertex(transformationMatrix, 20, 20, 5).color(0xFF414141);
					RenderSystem.setShader(GameRenderer::getPositionColorProgram);
					RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					BufferRenderer.drawWithGlobalProgram(buffer.end());
				});
			}
		});
	}
}