# OfflineLAN — LAN offline-mode for every loader

Lets **non-premium (offline / cracked) accounts join your LAN world**.
Only the **host** needs the mod. Vanilla clients can join.

## How it works

When you open a singleplayer world to LAN, the integrated server verifies joining
players against Mojang session servers (`online-mode=true`). Cracked clients have no
valid session, so they get `Failed to verify username!` / `Invalid session`.

This mod flips the integrated server to offline mode (like `online-mode=false`
on a dedicated server), so offline-mode clients on the same network can join.

## How to use

1. Install the jar matching your **loader + Minecraft version** (see table below).
2. Open your singleplayer world.
3. `Esc -> Open to LAN` — you will see a new button:
   - `Online Mode: ON` (default, vanilla behaviour, premium only)
   - Click it to switch to `Online Mode: OFF`.
4. `Start LAN World` and give friends your `IP:port` shown in chat.
5. Offline clients on the same network join via `Multiplayer -> Add Server`.

While running (requires OP / permission level 3):

```
/online-mode off     # allow offline/cracked LAN clients
/online-mode on      # premium only (default)
/online-mode status  # show current state
```

## Notes / caveats

- Defaults to `ON` every time the server starts, so you must deliberately turn it `OFF`.
- With online-mode `OFF`, players are identified by locally-generated offline UUIDs.
  A player who previously joined with it `ON` may appear as a "new" player (fresh
  inventory/position). Turning it back `ON` restores their premium UUID data.
  The host is unaffected (host data lives in `level.dat`).
- Only use on trusted networks (LAN, e4mc, port-forward with whitelist, etc.).
  Anyone who can reach the port can join when auth is off. Keep backups.
- Forge/NeoForge note: the mod does nothing on a dedicated server (use
  `server.properties` `online-mode=false` there instead).

## Downloads

One GitHub Release contains **every jar**: `offlinelan-<loader>-<mc>-<version>.jar`.

- Releases: https://github.com/Hushayo/offline-lan/releases
- Pick the file matching your setup, e.g. `offlinelan-neoforge-1.21.1-1.1.0.jar`
  or `offlinelan-fabric-1.20.1-1.1.0.jar`. Fabric also needs
  [Fabric API](https://modrinth.com/mod/fabric-api) for its version.

### Supported versions

| Loader | Minecraft versions |
|---|---|
| NeoForge | 1.20.4 – 1.20.6, 1.21 – 1.21.11, 26.1 – 26.3 |
| Forge | 1.19.2 – 1.19.4, 1.20 – 1.20.6 |
| Fabric (+ Fabric API) | 1.20 – 1.21.11 |

Out of scope for now: anything older than 1.19 (different GUI/event APIs and
Java toolchains — a legacy port would need its own codebase). The repo is
structured so old-version modules can be added later (`forge/`, `fabric/`,
`neoforge/` are independent Gradle projects).

## Building

CI builds **every loader × version** on each push/PR (`.github/workflows/build.yml`).
Pushing a tag `v*` (e.g. `v1.1.0`) rebuilds everything and attaches all jars to
a single GitHub Release (`.github/workflows/release.yml`).

Local builds (defaults: NeoForge 1.21.1 / Forge 1.20.1 / Fabric 1.21.1):

```bash
cd neoforge && ./gradlew build   # -> neoforge/build/libs/offlinelan-1.1.0.jar
cd forge && ./gradlew build      # -> forge/build/libs/offlinelan-1.1.0.jar
cd fabric && ./gradlew build     # -> fabric/build/libs/offlinelan-1.1.0.jar
```

Other versions via properties, e.g.:

```bash
cd neoforge && ./gradlew build -Pminecraft_version='1.20.4' -Pminecraft_version_range='[1.20.4]' \
  -Pneo_version='20.4.251' -Pparchment_minecraft_version='1.20.4' \
  -Pparchment_mappings_version='2024.04.14' -Pjava_version='17'
```

See each matrix entry in `build.yml` for the exact property sets.

## Layout

- `neoforge/` — NeoForge module (ModDevGradle, MojMap + Parchment)
- `forge/` — Forge module (ForgeGradle 6, official mappings)
- `fabric/` — Fabric module (Loom, official Mojang mappings, Fabric API)
- `.github/workflows/` — matrix build + unified release

## License

MIT — see `LICENSE`.
