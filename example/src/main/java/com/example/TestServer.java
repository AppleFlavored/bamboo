package com.example;

import dev.flavored.bamboo.Schematic;
import dev.flavored.bamboo.SchematicImporter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestServer {
    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

        DimensionType fullBright = DimensionType.builder()
                .ambientLight(2.0f)
                .build();
        DynamicRegistry.Key<DimensionType> dimensionKey = MinecraftServer.getDimensionTypeRegistry().register("test:full_bright", fullBright);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instance = instanceManager.createInstanceContainer(dimensionKey);
        instance.loadChunk(0, 0);

        SchematicImporter importer = new SchematicImporter();
        try {
            Schematic schematic = importer.fromStream(Files.newInputStream(Path.of("test.schematic")));
            schematic.paste(instance, Vec.ZERO);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        eventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(instance);
        });

        eventHandler.addListener(PlayerSpawnEvent.class, event -> {
            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            event.getPlayer().teleport(new Pos(0, 4, 0));
        });

        server.start("0.0.0.0", 25565);
    }
}
