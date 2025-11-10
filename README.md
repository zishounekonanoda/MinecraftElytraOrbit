# ApexOrbitTrails / ElytraTrail2

## English (EN) — 日本語は下にあります！
### Overview
- This plugin recreates Apex Legends–style rank orbit particles while you glide with an Elytra on Paper/Spigot 1.21.x servers.
- Basic usage: run `/orbit` to open the GUI and pick a trail. The default config ships with Diamond, Predator, Master, plus a dummy "TEST" entry for your own presets.
- The `/orbit` GUI handles enabling/disabling trails and switching between Japanese/English without touching commands.

### Supported Versions
- Target servers: Paper / Spigot 1.21 through 1.21.10.
- Newer Minecraft versions will receive builds whenever the maintainer has time to publish them.

### Key Features
- Scoreboard tags defined under `config.yml: groups` decide which players receive which trail.
- `EmitWhen` (FLYING / GLIDING / FALLING / ALL) controls when particles spawn, so you can limit effects to Elytra flight only.
- Dedicated trail-selection and language-selection GUIs with paging for large configs.
- Each player’s language choice is saved to `player_locales.yml`, acting as a lightweight DB so you don’t have to manage anything manually.
- `/orbit "tag|untag" <player>` lets admins manage tags via command. It’s not recommended for self-use—stick to the GUI. Adding a second tag automatically removes the older one.

### Installation
1. Grab the jar from the Releases page (or build it yourself).
2. Drop it into `plugins/`, then restart or run `/reload confirm` (newer Minecraft versions may require a full restart because `/reload` is being phased out).
3. On first boot the plugin creates `config.yml`, `messages_en.yml`, `messages_ja.yml`, and `player_locales.yml` under `plugins/ApexOrbitTrails/`.

### Configuration (`config.yml`)
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
- Multiple effects per group are supported. Each entry controls the particle type (`particle`), spawn count (`count`), offset (`offset`), animation speed (`speed`), and condition (`when`, either GLIDING or ALL). GLIDING restricts emission to Elytra flight, whereas ALL also covers jumps.
- When `particle: REDSTONE`, you can specify `redstone.color` (hex) and `redstone.size`. See the Bukkit API docs for every particle option: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html

### Commands & Permissions
| Command | Description | Permission |
|---------|-------------|------------|
| `/orbit` (no args) | Open the GUI (players only) | `apexorbit.menu` |
| `/orbit reload` | Reload `config.yml` and message files | `apexorbit.admin` |
| `/orbit tag <player> <group>` | Give a player a trail tag (removes the old one) | `apexorbit.admin` |
| `/orbit untag <player> <group>` | Remove a specific tag | `apexorbit.admin` |

Permissions:
- `apexorbit.admin` (default: OP)
- `apexorbit.menu` (default: everyone)

### GUI Usage
- `/orbit` opens the trail GUI.
  - Arrows paginate pages.
  - Barrier deletes every applied trail tag.
  - Player head opens the language GUI.
  - Each trail icon applies the configured group instantly.
- Language GUI:
  - Red banner = English, white banner = Japanese. After selecting, the trail GUI reopens.

### Localization
- Edit `messages_en.yml` / `messages_ja.yml` to change GUI labels or command feedback.
- Players switch languages via the GUI, and their choice is stored in `player_locales.yml` (UUID → language code).
- To add another language you’ll need to ship a new build that bundles `messages_<lang>.yml` and call `LocaleManager#saveDefaultLocale("<lang>")`, plus provide GUI buttons.

### Example scoreboard workflow
1. `scoreboard players tag Steve add PREDATOR`
2. When the player rejoins, Elytra flight permission toggles on automatically.
3. Removing the tag stops the trail and revokes flight in Survival/Adventure modes.

### Troubleshooting
- **Particles never show**: confirm the `particle` option exists and the `when` condition applies to the player’s current state.
- More items will be added as needed.

### FAQ
- **Which versions are supported?** → 1.21 through 1.21.10.
- **How do I add another rank to the GUI?** → Follow the configuration section above and extend `config.yml > groups` with new particle definitions. Refer to the Bukkit API list for valid particle names.
- **How do I add non-default languages (advanced)?** → Bundle `messages_<lang>.yml`, register it in `LocaleManager`, then add language buttons in the GUI.

### Development Tips
- Java 17 + Maven; `ParticleConfig` interprets YAML so you can model complex effects entirely in config.
- GUI slots: 45 (previous), 48 (language), 49 (remove), 53 (next).
- Whenever you add a message key, update every language file to keep parity.

---

## 日本語 (JA)

### 概要
- このプラグインは、Apex Legends のランク軌道風パーティクルを Elytra 滑空中に再現する Paper/Spigot (1.21.x) 向けプラグインです。
- 簡単な使用方法は、コマンド`/orbit`でGUIを開き、軌道を選択します。初期設定ではダイヤモンド、プレデター、マスターの三つと、カスタマイズ向けにダミーのテスト軌道を用意してあります。
- `/orbit` コマンドの GUI で軌道の付け外し・言語切替(日本語と英語)・を操作できます。

### 対応バージョン 
- 対応サーバー: Paper / Spigot 1.21 ～ 1.21.10。
- 製作者の気が向けば永続的に次バージョンもリリースします。

### 主な機能
- タグ判定 (`config.yml: groups`) により対象プレイヤーを決定。
- `EmitWhen` (FLYING / GLIDING / FALLING / ALL) 条件に応じてパーティクルを生成。
- 軌道 選択 GUI と言語選択 GUI があります。ページング機能も付いており、スロットの最大数を超過した場合に発現します。
- プレイヤーごとの言語設定を `player_locales.yml` に永続化。DB扱いなのでどうでもよいです。
- `/orbit "tag/untag" [名前]`を使用すると手動でプレイヤーに軌道を付与します。※自分に対しては非推奨。GUIを推奨します。また、同時に2個つけると古いものから外されます。

### インストール手順
1. リリース からファイルを取得してください。
2. サーバー `plugins/` に配置し、再起動または `/reload confirm`(最新バージョンではreloadが廃止されているようなので、再起動が必要な場合があります)
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
- 複数エフェクト対応。また、パーティクルごとに生成量を細かく変更できます。`particle`でパーティクルの種類, `count`で一度に生成される量, `offset`で高さの調整, `speed`でパーティクルのアニメーションスピード, `when`では[GLIDING]か[ALL]を設定できます。[ALL]ではジャンプしている間常にパーティクルが生成されますが、[GLIDING]にすることでエリトラの飛行中のみに制限できます。
- また、`particle: REDSTONE` の場合は `redstone.color` (HEX) と `redstone.size` を指定可能。パーティクルの詳しい説明はbukkit apiのページを参照してください：https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html

### コマンド / 権限
| コマンド | 説明 | 権限 |
|----------|------|------|
| `/orbit` (引数なし) | GUI 表示（プレイヤーのみ） | `apexorbit.menu` |
| `/orbit reload` | `config.yml` とメッセージをリロード | `apexorbit.admin` |
| `/orbit tag <player> <group>` | プレイヤーにタグ付与（古いものから自動削除） | `apexorbit.admin` |
| `/orbit untag <player> <group>` | 指定タグを削除。 | `apexorbit.admin` |

権限:
- `apexorbit.admin` (default: OP)
- `apexorbit.menu` (default: 全員)

### GUI の使い方
- `/orbit` で Trail GUI を開く。
  - 矢印: ページ送り。
  - バリア: 現在の軌道タグを全削除。
  - プレイヤーヘッド: 言語 GUI を開く。
  - 各アイコン: configで設定した軌道を即時適用。
- 言語 GUI:
  - 赤バナー: 英語。白バナー: 日本語。選択後は Trail GUI を再表示。

### ローカライズ
- `messages_en.yml` / `messages_ja.yml` で GUI やコマンド出力を編集可能。
- プレイヤー言語は GUI で切替、`player_locales.yml` に UUID→言語コードとして保存。
- 新言語を追加する場合は、ソースコードから別途ビルドしてください。 `messages_<lang>.yml` を同梱し、`LocaleManager` に `saveDefaultLocale("<lang>")` を追記します。

### スコアボードタグ運用例
1. `scoreboard players tag <player> add PREDATOR` などでタグ付与。
2. プレイヤーがログインすると自動で Elytra の飛行許可が有効化。
3. タグ削除時に Trail も停止し、サバイバル/アドベンチャーでは飛行許可が無効化されます。

### トラブルシューティング
- **パーティクルが出ない**: `config` に `particle` があるか、`when` 条件を満たしているか確認。
- 随時追加予定

### よくある質問
- **対応バージョンは何ですか？** → 1.21 ～ 1.21.10 に対応しています。
- **GUI に新しいランクを追加するには？** → 上記の開設を参考に、`config.yml` の `groups` にパーティクルを追加してください。パーティクルの名前はbukkit apiのサイトで確認できます：https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html

- **デフォルト以外の言語を追加したい。(高度)** → `messages_<lang>.yml` 追加 → `LocaleManager` に登録 → GUI ボタン追加。
