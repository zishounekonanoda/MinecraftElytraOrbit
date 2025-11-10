# ApexOrbitTrails / ElytraTrail2

## English (EN)

### Overview
- Minecraft Paper/Spigot (1.21.x) plugin that recreates Apex Legends rank trails while gliding with Elytra.
- Grants `allowFlight` and spawns configurable particle effects for players tagged via scoreboard.
- `/orbit` command + GUIs allow trail selection/removal, language switching, and config reloads.

### Supported Versions & Build
- Server: Paper / Spigot 1.21 – 1.21.10 (Java 17).
- `pom.xml` exposes `paper.api.version`; override with `mvn "-Dpaper.api.version=1.21.7-R0.1-SNAPSHOT" clean package`.
- Because 1.21.x APIs are compatible, one JAR built for any 1.21.x API works across the entire range.

### Key Features
- `config.yml`’s `groups` define scoreboard tags and their particle stacks.
- `EmitWhen` (FLYING / GLIDING / FALLING / ALL) decides when effects fire.
- Trail selection GUI with pagination, removal button, and language button; language GUI persists per-player preference (`player_locales.yml`).
- `/orbit tag|untag|menu|reload` command suite with proper permissions and tab complete.

### Installation
1. Build or download `apex-orbit-trails-1.0.0.jar`.
2. Drop it into `plugins/`, restart or `/reload confirm`.
3. Edit the generated config/messages under `plugins/ApexOrbitTrails/`.

### Configuration snippet
```yaml
tick_period: 1
groups:
  PREDATOR:
    effects:
      - particle: REDSTONE
        count: 20
        offset: 0.2
        speed: 0.05
        when: GLIDING
        redstone:
          color: "#FF0000"
          size: 1
      - particle: FLAME
        count: 5
        offset: 0.2
        speed: 0.01
        when: GLIDING
```
- List multiple entries under `groups.<TAG>.effects`.
- REDSTONE supports `redstone.color` (hex) and `redstone.size`.
- If `effects` is missing, legacy single-effect keys are still supported.

### Commands & Permissions
| Command | Description | Permission |
|---------|-------------|------------|
| `/orbit` | Open GUI (players only) | `apexorbit.menu` |
| `/orbit menu` `/orbit gui` | Force-open GUI | `apexorbit.menu` |
| `/orbit reload` | Reload config and messages | `apexorbit.admin` |
| `/orbit tag <player> <group>` | Assign trail tag (removes others) | `apexorbit.admin` |
| `/orbit untag <player> <group>` | Remove specific tag; disables flight if none remain | `apexorbit.admin` |

Permissions:
- `apexorbit.admin` (default: OP)
- `apexorbit.menu` (default: true)

### GUI Usage
- Trail GUI (opened via `/orbit`):
  - Arrows: paginate.
  - Barrier: remove trail tag(s).
  - Player head: open language GUI.
  - Trail icons: apply group immediately.
- Language GUI:
  - Red banner = English, white banner = Japanese. Returns to Trail GUI after selection.

### Localization
- Edit `messages_en.yml` / `messages_ja.yml`.
- Per-player language stored in `player_locales.yml`.
- Add new locales by bundling `messages_<lang>.yml` and registering via `LocaleManager#saveDefaultLocale`.

### Example scoreboard workflow
1. `scoreboard players tag Steve add PREDATOR`
2. Player rejoins → Glide trail + flight granted.
3. Removing all tags resets trails and disables flight outside creative/spectator.

### Troubleshooting
- **No particles**: ensure `particle` key exists and `EmitWhen` condition is met.
- **Flight doesn’t enable**: check other plugins changing `allowFlight` or player gamemode.
- **Garbled JP text**: save message files in UTF-8.
- **Build fails**: targeted Paper API version might not exist yet; wait for publication or force update with `-U`.

### FAQ
- **One JAR for all 1.21.x?** Yes—compile against any 1.21.x API and reuse.
- **Add new ranks to GUI?** Extend `config.yml` and optionally update `GuiManager#getMaterialForGroup`.
- **Add new languages?** Add `messages_<lang>.yml`, register in `LocaleManager`, add buttons in GUI.

### Development Tips
- Java 17 + Maven; `ParticleConfig` interprets YAML so complex effects live in config only.
- GUI slots: 45 (prev), 48 (language), 49 (remove), 53 (next).
- Always update both language files when adding message keys.

---

## 日本語 (JA)

### 概要
- Apex Legends のランク軌道風パーティクルを Elytra 滑空中に再現する Paper/Spigot (1.21.x) 向けプラグインです。
- スコアボードタグを持つプレイヤーに対し、設定されたパーティクルを周期的に生成しつつ飛行許可を自動付与します。
- `/orbit` コマンドと GUI で軌道の付け外し・言語切替・設定リロードを操作できます。

### 対応バージョン / ビルド
- 対応サーバー: Paper / Spigot 1.21 ～ 1.21.10。
- JDK 17 でコンパイル。`pom.xml` の `paper.api.version` を差し替えればターゲット API を変更可能です。
- 1.21.x 系は API が互換のため、任意の 1.21.x API でビルドした単一 JAR を全 1.21.x サーバーで使用できます。

### 主な機能
- スコアボードタグ判定 (`config.yml: groups`) により対象プレイヤーを決定。
- `EmitWhen` (FLYING / GLIDING / FALLING / ALL) 条件に応じて粒子を生成。
- Trail 選択 GUI と言語選択 GUI を提供。ページング／削除／言語ボタンを配置。
- プレイヤーごとの言語設定を `player_locales.yml` に永続化。
- `/orbit` コマンドでタグ付与・削除・GUI 表示・設定リロードを実装。

### インストール手順
1. リリース JAR もしくは `target/apex-orbit-trails-1.0.0.jar` を取得。
2. サーバー `plugins/` に配置し、再起動または `/reload confirm`。
3. 初回起動で `config.yml`, `messages_en.yml`, `messages_ja.yml`, `player_locales.yml` が `plugins/ApexOrbitTrails/` に生成されます。

### 設定 (`config.yml`)
```yaml
tick_period: 1
groups:
  PREDATOR:
    effects:
      - particle: REDSTONE
        count: 20
        offset: 0.2
        speed: 0.05
        when: GLIDING
        redstone:
          color: "#FF0000"
          size: 1
      - particle: FLAME
        count: 5
        offset: 0.2
        speed: 0.01
        when: GLIDING
```
- `groups.<TAG>.effects` に複数エフェクトを列挙。`particle`, `count`, `offset`, `speed`, `when` などを設定します。
- `particle: REDSTONE` の場合は `redstone.color` (HEX) と `redstone.size` を指定可能。
- `effects` を省略した場合はレガシー形式として `groups.<TAG>` 直下のキーを 1 つのエフェクトとして扱います。

### コマンド / 権限
| コマンド | 説明 | 権限 |
|----------|------|------|
| `/orbit` (引数なし) | GUI 表示（プレイヤーのみ） | `apexorbit.menu` |
| `/orbit menu` / `/orbit gui` | GUI を即時表示 | `apexorbit.menu` |
| `/orbit reload` | `config.yml` とメッセージをリロード | `apexorbit.admin` |
| `/orbit tag <player> <group>` | プレイヤーにタグ付与（他タグは自動削除） | `apexorbit.admin` |
| `/orbit untag <player> <group>` | 指定タグを削除。残タグが無い場合は飛行許可を無効化 | `apexorbit.admin` |

権限:
- `apexorbit.admin` (default: OP)
- `apexorbit.menu` (default: true)

### GUI の使い方
- `/orbit` で Trail GUI を開く。
  - 矢印: ページ送り。
  - バリア: 現在の軌道タグを全削除。
  - プレイヤーヘッド: 言語 GUI を開く。
  - 各アイコン: 対応グループの Trail を即時適用。
- 言語 GUI:
  - 赤バナー: 英語。白バナー: 日本語。選択後は Trail GUI を再表示。

### ローカライズ
- `messages_en.yml` / `messages_ja.yml` で GUI やコマンド出力を編集可能。
- プレイヤー言語は GUI で切替、`player_locales.yml` に UUID→言語コードとして保存。
- 新言語を追加する場合は `messages_<lang>.yml` を同梱し、`LocaleManager` に `saveDefaultLocale("<lang>")` を追記します。

### スコアボードタグ運用例
1. `scoreboard players tag <player> add PREDATOR` などでタグ付与。
2. プレイヤーがログインすると自動で Elytra の飛行許可が有効化。
3. タグ削除時に Trail も停止し、サバイバル/アドベンチャーでは飛行許可が無効化されます。

### トラブルシューティング
- **パーティクルが出ない**: `groups.<TAG>.effects` に `particle` があるか、`when` 条件を満たしているか確認。
- **タグを付けても飛べない**: 他プラグインが `allowFlight` を上書きしていないか、ゲームモードがサバイバル/アドベンチャーか確認。
- **文字化け**: `messages_ja.yml` を UTF-8 で保存してください。
- **コンパイル不可**: 指定した Paper API 版が未公開の場合は依存解決に失敗します。公開後に再ビルドしてください。

### よくある質問
- **1 つの JAR で 1.21 ～ 1.21.10 に対応できますか？** → はい。1.21 系の Paper API は後方互換です。
- **GUI に新しいランクを追加するには？** → `config.yml` の `groups` に追加し、必要なら `GuiManager#getMaterialForGroup` などを拡張。
- **デフォルト以外の言語を追加したい。** → `messages_<lang>.yml` 追加 → `LocaleManager` に登録 → GUI ボタン追加。

### 開発 Tips
- Java 17 + Maven。`ParticleConfig` が YAML を解釈するので、複雑な効果も設定ファイルだけで完結します。
- GUI のスロット配置は `GuiManager` の固定値（48:言語、49:バリア、45/53:矢印）を参照。
- メッセージは `LocaleManager#getString` を経由して取得するため、新規キーを追加するときは全言語ファイルの同期を忘れないでください。
