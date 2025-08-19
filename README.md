# Bamboo
Bamboo is a fast and lightweight schematic library for Minestom.

| Format    | Reading            | Writing |
|-----------|--------------------|---------|
| Sponge V1 | :white_check_mark: | :x:     |
| Sponge V2 | :warning:          | :x:     |
| Sponge V3 | :warning:          | :x:     |
| MCEdit    | :x:                | :x:     |

- :warning: Partial support, most notably tile/block entities and biomes are not supported.

## Install
Bamboo is available on [Maven Central](https://central.sonatype.com/artifact/dev.flavored/bamboo). Replace `<VERSION>` with the latest version.

![Maven Central Version](https://img.shields.io/maven-central/v/dev.flavored/bamboo?style=flat-square)

```kts
dependencies {
    implementation("dev.flavored:bamboo:<VERSION>")
}
```

## Example
The following example loads a schematic from a file, then pastes it into the `instance` at a given position.
```java
import dev.flavored.bamboo.Schematic;
import dev.flavored.bamboo.SchematicReader;

public static void main(String[] args) {
    // ... after setting up an instance ...
    SchematicReader importer = new SchematicReader();
    Schematic schematic = importer.fromPath(new java.nio.Path("example.schem"));
    schematic.paste(instance, new Pos(0.0, 0.0, 0.0));
}
```

## License
Bamboo is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.