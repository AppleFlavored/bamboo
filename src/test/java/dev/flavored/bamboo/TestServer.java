package dev.flavored.bamboo;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.DimensionTypeManager;

import java.nio.file.Path;

public class TestServer {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

        DimensionTypeManager dimensionTypeManager = MinecraftServer.getDimensionTypeManager();
        DimensionType fullBright = DimensionType.builder(NamespaceID.from("test:full_bright"))
                .ambientLight(2.0f)
                .build();
        dimensionTypeManager.addDimension(fullBright);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instance = instanceManager.createInstanceContainer(fullBright);

        Schematic schematic = Bamboo.fromPath(Path.of("test.schematic"), new SchematicOptions());
        schematic.paste(instance, new Pos(0, 0, 0));

        eventHandler.addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(instance);
        });

        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().teleport(new Pos(0, 4, 0));
        });

        server.start("0.0.0.0", 25565);
    }
}
