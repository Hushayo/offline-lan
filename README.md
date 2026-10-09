# OfflineLAN (NeoForge 1.21.1)

NeoForge 1.21.1 mod that lets **non-premium (offline / cracked) accounts join your LAN world**.

Only the **host** needs this mod. Vanilla / unmodded clients can join.

## How it works

When you open a singleplayer world to LAN, the integrated server verifies joining
players against Mojang session servers (`online-mode=true`). Cracked clients have no
valid session, so they get `Failed to verify username!` / `Invalid session`.

This mod flips the integrated server to offline mode via
`MinecraftServer.setUsesAuthentication(false)`, exactly like `online-mode=false`
on a dedicated server.

## How to use

1. Install NeoForge 1.21.1, then drop the mod jar into `mods/`.
2. Open your singleplayer world.
3. `Esc -> Open to LAN` — you will see a new button:
   - `Online Mode: ON` (default, vanilla behaviour, premium only)
   - Click it to switch to `Online Mode: OFF`.
4. `Start LAN World` and give friends your `IP:port` shown in chat.
5. Offline clients on the same network join via `Multiplayer -> Add Server`.

You can also change it while running (requires OP / permission level 3):

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
- Client-only in practice: install on the host. Does nothing useful on a dedicated
  server (use `server.properties` `online-mode=false` there instead).

## Building

CI builds every push/PR with GitHub Actions (`.github/workflows/build.yml`, JDK 21).

Local build:

```bash
./gradlew build
# jar -> build/libs/offlinelan-1.0.0.jar
```

Push a tag `v*` (e.g. `v1.0.0`) to trigger `.github/workflows/release.yml`,
which builds and attaches the jar to a GitHub Release.

## Versions

- Minecraft: `1.21.1`
- NeoForge: `21.1.257` (see `gradle.properties` `neo_version`)
- Java: `21`
- Mod loader: ModDevGradle `2.0.148`

## License

MIT — see `LICENSE`.
