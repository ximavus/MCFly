package fly;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import org.lwjgl.glfw.GLFW;

public class FlyClient implements ClientModInitializer {
	private static final KeyBinding Cosmetic = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.wee", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "category.fly.mystuff"));
	private static final KeyBinding FlyUp = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_SPACE, "category.fly.mystuff"));
	private static final KeyBinding toggleGravity = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.nog", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, "category.fly.mystuff"));
	private static final KeyBinding FlyDown = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.fly.down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "category.fly.mystuff"));
	@Override
	public void onInitializeClient() {
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
			while (Cosmetic.wasPressed()) {
				assert client.world != null;
                assert client.player != null;
				LightningEntity bomb = new LightningEntity(EntityType.LIGHTNING_BOLT, client.world);
				bomb.setPosition(client.player.getPos().offset(client.player.getHorizontalFacing(), 7));
                client.world.addEntity(bomb);
			}
		});
	}
}

