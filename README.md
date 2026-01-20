# ExilegionLogo

A Hytale server plugin that displays the Exilegion server logo in the bottom-left corner of players' screens.

## Features

- Persistent logo display in the bottom-left corner
- Integrates with MultipleHUD for compatibility with other HUD mods
- Automatically shows logo when players connect

## Requirements

- Hytale Server (Java 25)
- [MultipleHUD](https://github.com/Buuz135/MultipleHUD) v1.0.2+

## Installation

1. Download the latest `ExilegionLogo-x.x.x.jar` from releases
2. Place in your server's `mods` folder
3. Ensure MultipleHUD is also installed
4. Restart the server

## Configuration

The logo position and size can be adjusted by modifying `Common/UI/Custom/Hud/Logo/ExilegionLogo.ui`:

```
Group #ExilegionLogo {
  Anchor: (Bottom: 10, Left: 10, Width: 150, Height: 82);
  Background: "ExilegionLogo.png";
}
```

## Building

```bash
./gradlew build
```

The built JAR will be in `build/libs/`.

## License

All rights reserved - Exilegion
