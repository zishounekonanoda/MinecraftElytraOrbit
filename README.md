# ApexOrbitTrails / ElytraTrail2
Bring Apex Legends-style rank orbits to Elytra glides. This page is written like a wiki so you can hand it to server owners or players as-is.

---

## English (EN)
> The Japanese guide below is the source of truth. This section is a direct translation so both languages stay in sync.

### Overview
- This plugin recreates Apex Legends-style rank orbit particles around Elytra gliders for Paper / Spigot 1.21.x.
- Day-to-day use is simple: run `/orbit`, open the GUI, and click the trail you want. The default config ships with DIAMOND, PREDATOR, MASTER, plus a “TEST” orbit meant for customization practice.
- The `/orbit` GUI also lets you remove the active orbit and switch between Japanese and English.

### Supported Versions
- Servers: Paper / Spigot 1.21 – 1.21.10.
- Newer Minecraft versions will receive releases whenever time allows.

### Core Features
- Tag resolution is driven by `config.yml: groups`, so you decide exactly which named trail each player can pick.
- `EmitWhen` (FLYING / GLIDING / FALLING / ALL) controls when every particle entry should spawn.
- A paginated trail-selection GUI and a language-selection GUI are included by default.
- Locale preferences are written to `player_locales.yml` per player—no external database is required.
- `/orbit tag/untag <player> <group>` can be used for manual assignments. Applying a new tag automatically removes any previous orbit.

### Installation Steps
1. Download the latest jar from Releases (or build it yourself with Maven).
2. Drop it into the server’s `plugins/` folder and restart. (`/reload confirm` still works on some builds, but Mojang now disables `/reload` on the newest versions, so a reboot may be required.)
3. On first boot the plugin creates `plugins/ApexOrbitTrails/config.yml`, both language files, and `player_locales.yml`.

### 5. Creating or Editing Trails
- Read through the available fields first. Everything under `config.yml > groups` is a “tag”. The tag name becomes the GUI button label, and the plugin handles remembering who owns what via the YAML files—restarts won’t wipe progress.

**Config sample**

```yaml
tick_period: 1
groups:
  PREDATOR:
    MATERIAL: NETHERITE_INGOT
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

**Meaning of each option**

- `MATERIAL:` chooses the GUI icon item (example: `MATERIAL:DIAMOND`). Missing or invalid entries fall back to `GLOWSTONE_DUST`.
- `effects:` is a list; stack as many particle definitions as you like.
  - `particle` – Bukkit/Spigot particle enum name.
  - `count` – Particles spawned per tick.
  - `offset` – XYZ spread (0.2 = tight, 1.0 = wide).
  - `speed` – Particle animation speed.
  - `when` – When to emit:
    | Value | Meaning |
    |-------|---------|
    | `GLIDING` | Only while Elytra-gliding. |
    | `FLYING`  | When the player is actively flying (creative-style flight). |
    | `FALLING` | While airborne but neither flying nor gliding. |
    | `ALL`     | Any airborne state. |
  - `redstone` – Extra settings for `particle: REDSTONE` (hex `color` + `size`).
- `tick_period` (root) sets how often the task runs. `1` = every tick. Raise it to lighten server load.

## How to Build a New Orbit
1. Copy an existing group and rename it (example: `MYTHIC`).
2. Change `MATERIAL` to the icon you want. [All valid names are listed here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html).
3. Adjust the `effects` list to taste. [Particle names live here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).
4. Run `/orbit reload` to add it to the GUI instantly.

### Commands & Permissions
| Command | Description | Permission |
|---------|-------------|------------|
| `/orbit` (no args) | Open the GUI (players only). | `apexorbit.menu` |
| `/orbit reload` | Reload `config.yml` and both language files. | `apexorbit.admin` |
| `/orbit tag <player> <group>` | Give the player a tag (older tags are removed first). | `apexorbit.admin` |
| `/orbit untag <player> <group>` | Remove the specified tag. | `apexorbit.admin` |

Permissions:
- `apexorbit.menu` – granted to everyone by default.
- `apexorbit.admin` – OP by default.

### GUI Usage
- `/orbit` opens the main menu.
  - **Arrows** – paginate when you have more than 45 entries.
  - **Barrier** – remove every orbit tag from yourself.
  - **Player head** – open the language selector.
  - **Trail icons** – instant apply of the matching group from `config.yml`.
- Language GUI:
  - Red banner = English, White banner = Japanese. After picking, the main GUI reopens in that language.

### Localization
- Edit `messages_en.yml` / `messages_ja.yml` to change GUI strings or command output.
- Players pick their preferred language inside the GUI; the choice is stored as `UUID -> language code` in `player_locales.yml`.
- To add another language, ship `messages_<lang>.yml`, implement `LocaleManager#saveDefaultLocale("<lang>")`, and add a language button.

### Troubleshooting & FAQ
- **Particles never spawn** – Ensure the tag contains a `particle` entry and that the player satisfies the `when` condition.
- **Icons never change** – Check for typos in `MATERIAL:`. Invalid names revert to `GLOWSTONE_DUST` and log a warning.
- **Which versions are supported?** – Paper / Spigot 1.21 through 1.21.10.
- **How do I add more trails to the GUI?** – Add a new group under `config.yml > groups`, name it, configure particles, and reload. You can also duplicate the sample and only change the name.
- Particle enum names are documented on the [Bukkit API reference](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).

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

### 5. 新しい軌道の作り方

- まず、各項目を理解しておく必要があります。

`config.yml の中にあるgroups` に書いた各ブロックが「タグ」です。タグ名はそのまま GUI のボタン名になり、どのプレイヤーがどのタグを持っているかはプラグインが自動管理します。ymlに保存されるため、鯖を再起動しても付与されたままになります。

**configのサンプル**

```yaml
tick_period: 1
groups:
  PREDATOR:
    MATERIAL: NETHERITE_INGOT
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

**各項目の意味**

- `MATERIAL:` で GUI アイコンに使うアイテムを指定（例: `MATERIAL:DIAMOND`）。未設定・誤入力時は `GLOWSTONE_DUST` が使われます。
- `effects:` は複数行でパーティクルを重ねられます。
  - `particle` ... 使いたいパーティクル名。
  - `count` ... 1 回に出す粒子の数。
  - `offset` ... 粒子の広がり具合（0.2 なら狭く、1.0 なら広く）。
  - `speed` ... 動きの速さ。小さいとゆっくり、大きいと速い。
  - `when` ... いつ出すか。以下から 1 つ選択:
    | 値 | 説明 |
    |----|------|
    | `GLIDING` | エリトラ 滑空中のみ発生。 |
    | `FLYING`  | クリエなどの飛行状態で発生。 |
    | `FALLING` | 落下中（滑空や飛行ではないとき）。 |
    | `ALL`     | 空中なら常に発生。 |
  - `redstone` ... `particle: REDSTONE` のときに色 (`color`) と太さ (`size`) を指定。
- `tick_period` は更新頻度。1tick（=1）で最も滑らか。数値を上げると負荷軽減。



## 作り方
1. 既存タグをコピーして名前を変える（例: `MYTHIC`）。
2. `MATERIAL` で好きなアイコンに変更。[名前はこのリンクを参照してください](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)
3. `effects` を好みのパーティクルに調整。既述の仕方はサンプルを参考にする上、[名前はこのリンクを参照にしてください](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html)
4. `/orbit reload` で GUI に追加されます。

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

### 7. GUI の使い方
- `/orbit` で軌道 GUI を開きます。
  - **矢印** ... ページ送り。
  - **バリア** ... 付与済みタグを全削除し、軌道を止める。
  - **プレイヤーヘッド** ... 言語選択 GUI を開く。
  - **各アイコン** ... `config.yml` のタグを即時適用。
- 言語 GUI:
  - 赤バナー = 英語、白バナー = 日本語。選択後に軌道 GUI が選択言語で再表示されます。

### ローカライズ
- `messages_en.yml` / `messages_ja.yml` で GUI やコマンド出力を編集可能。
- プレイヤー言語は GUI で切替、`player_locales.yml` に UUID→言語コードとして保存。
- 新言語を追加する場合は、ソースコードから別途ビルドしてください。 `messages_<lang>.yml` を同梱し、`LocaleManager` に `saveDefaultLocale("<lang>")` を追記します。

### トラブルシューティング & よくある質問
- **パーティクルが出ない**: `config` に `particle` があるか、`when` 条件を満たしているか確認。
- **アイコンが変わらない** - `MATERIAL:` のスペルを確認。誤字があると `GLOWSTONE_DUST` に戻り、コンソールに警告が出ます。
- **対応バージョンは何ですか？** → 1.21 ～ 1.21.10 に対応しています。
- **GUI に新しい軌道を追加するには？** → 上記の解説を参考に、`config.yml` の `groups` に名前を追加してください。**また、サンプルを参考に名前だけを変えれば作ることもできます**
パーティクルの名前はbukkit apiのサイトで確認できます：https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html

---
Happy gliding, and feel free to suggest or contribute new orbit ideas!
