# Bamboo
Bamboo is a fast and lightweight schematic library for Minestom.

| Format    | Reading            | Writing |
|-----------|--------------------|---------|
| Sponge V1 | :white_check_mark: | :x:     |
| Sponge V2 | :warning:          | :x:     |
| Sponge V3 | :x:                | :x:     |
| MCEdit    | :x:                | :x:     |

## Install
Bamboo is available on [Maven Central](https://central.sonatype.com/artifact/dev.flavored/bamboo). Replace `<VERSION>` with the latest version.

```kts
dependencies {
    implementation("dev.flavored:bamboo:<VERSION>")
}
```

## Example
The following example loads a schematic from a file, then pastes it into the `instance` at a given position.
```java
SchematicReader importer = new SchematicReader();
Schematic schematic = importer.fromPath(new java.nio.Path("example.schematic"));
schematic.paste(instance, new Pos(0.0, 0.0, 0.0));
```

Bamboo also supports loading schematics from an NBT compound tag or from an input stream.
```java
importer.fromNBT(nbtCompound);
importer.fromStream(inputStream);
```

## License
Bamboo is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.