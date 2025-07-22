## DrcomoUpgradeGUIMI 快速上手指
### 📜 功能简介

**DrcomoUpgradeGUIMI** 是一款专为 `MMOItems` 设计的物品升级辅助插件。它提供了一个清爽、直观的图形化界面（GUI），旨在帮助玩家安全、高效地将手中旧版本的自定义物品，一键升级为服务器上的最新版本，解决了服务器物品迭代后玩家更新困难的核心痛点。

### ✨ 功能特性

  * **可视化升级界面**：玩家通过简单的拖拽操作即可完成升级，无需记忆任何复杂指令。
  * **强大的白名单系统**：管理员可通过配置文件，精准控制允许升级的物品类型或具体 ID，杜绝误操作风险。
  * **专为 MMOItems 设计**：深度集成 `MMOItems`，自动识别物品的类型与 ID 并获取最新版本进行替换。
  * **自动、安全的物品处理**：关闭界面时自动处理，升级后的物品若背包已满，将安全掉落在玩家脚下。
  * **高度可定制**：从界面标题、布局、装饰物品到所有提示消息，均可通过配置文件进行个性化定制。

### ⌨️ 指令与权限

| 指令 | 别名 | 权限节点 | 功能描述 | 默认权限 |
| :--- | :--- | :--- | :--- | :--- |
| `/drcomoupgrade open` | `/upitemgui` | `drcomoupgrade.use` | 为玩家打开物品升级 GUI。 | `true` (所有人) |
| `/drcomoupgrade reload` | 无 | `drcomoupgrade.reload` | 重载插件的 `config.yml` 与 `lang.yml` 配置文件。 | `op` (仅管理员) |

### ⚙️ 配置说明 (`config.yml`)

插件所有核心功能均通过 `config.yml` 文件进行配置。修改后使用 `/drcomoupgrade reload` 可立即生效。

#### `whitelist` (白名单)

用于定义哪些物品可以被升级。支持两种匹配模式，满足其一即可：

  * `type`: 按 `MMOItems` 的**类型**进行宽泛匹配。在此列表中的所有该类型物品都可升级。
  * `id`: 按 `MMOItems` 的**精确ID**进行匹配。格式为 `类型:ID`（或 `类型;;ID`），大小写不敏感。

#### `gui` (界面)

定制升级窗口的外观。

  * `title`: 界面标题，支持 `&` 颜色代码。
  * `size`: 界面大小，必须为9的倍数（如27, 36, 54）。
  * `decorative-slots`: 定义不可交互的装饰性槽位。
      * **键**：一个由方括号包裹、逗号分隔的槽位编号列表（0-53），如 `"[0,1,2,8]"`。
      * **值**：定义物品的样式，支持 `material`, `name`, `lore`, `custom-model-data`, `flags` 等属性。
  * `close-button-slot`: “关闭”按钮的槽位编号。
  * `close-button`: “关闭”按钮的物品样式，配置项同上。

#### `debug-level` (日志等级)

控制后台日志的详细程度。

  * 可选值: `INFO` (默认), `DEBUG` (用于排查问题), `WARN`, `NONE`。

### 🛠️ 配置示例

这是一份包含了大部分常用功能的标准 `config.yml` 配置，你可以此为基础进行修改。

```yaml
# config.yml

# 白名单配置：只允许特定的两种物品升级
whitelist:
  type: [] # 建议将此项留空，使用更安全的 id 匹配
  id:
    - "CLASS:铸刃者情报"      # 格式为 '类型:ID'
    - "SWORD:远古英雄之剑"

# GUI界面配置
gui:
  title: "&1&l史诗物品升级"
  size: 36
  # 定义上下两行和左右两列为装饰边框
  decorative-slots:
    "[0,1,2,3,4,5,6,7,8]":
      material: BLUE_STAINED_GLASS_PANE
      name: " "
    "[27,28,29,30,31,32,33,34,35]":
      material: BLUE_STAINED_GLASS_PANE
      name: " "
    "[9,18]":
      material: LIGHT_BLUE_STAINED_GLASS_PANE
      name: " "
    "[17,26]":
      material: LIGHT_BLUE_STAINED_GLASS_PANE
      name: " "
      
  # 将关闭按钮设置在右下角
  close-button-slot: 31
  close-button:
    material: BARRIER
    name: "&c&l关闭并升级"
    lore:
      - "&7点击后将处理所有放入的物品。"
      - "&c此过程不可逆！"
    flags:
      - "HIDE_ATTRIBUTES"

# 默认日志等级
debug-level: INFO
```

### 💬 语言文件 (`lang.yml`)

所有玩家可见的消息都储存在 `lang.yml` 文件中，你可以自由修改文本内容，支持 `&` 颜色代码。

```yaml
# lang.yml

invalid-item: "&c该物品无法放入更新界面！"
upgrade-success: "&a物品更新成功"
reload-complete: "&a配置已重载。"
open-success: "&a已打开升级界面。"
only-player: "&c只有玩家可以使用此指令。"
no-permission: "&c你没有权限执行此指令。"
```