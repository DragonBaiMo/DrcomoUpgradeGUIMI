# `ItemProvider` 接口

> **所有已知子接口**：`SimpleItemProvider`  
> **所有已知实现类**：`AllItemProvider`、`EcoItemProvider`、`ExecutableItemsProvider`、`HeadDatabaseProvider`、`ItemBridgeProvider`、`ItemEditProvider`、`ItemsAdderProvider`、`MMOItemsProvider`、`MultiItemProvider`、`MythicItemProvider`、`NexoProvider`、`NovaItemProvider`、`OraxenProvider`、`SlimefunProvider`

---

## 1. 概览

- **包名**：`io.github.projectunified.uniitem.api`  
- **接口全名**：`io.github.projectunified.uniitem.api.ItemProvider`  
- **简介**：定义了通用的“键 (ItemKey)”与 `ItemStack` 互转及比较的能力，各插件提供者需实现此接口以支持统一的物品管理。

---

## 2. 方法总览（Method Summary）

| 修饰符 & 返回类型                                       | 方法签名                                                                                          | 说明                                                                                                       |
| ------------------------------------------------------ | ------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| `default boolean`                                      | `isSimilar(@NotNull ItemStack item, @NotNull ItemKey key)`                                       | 判断给定的 `ItemStack` 是否与指定的 `ItemKey` 表示的物品“相似”。                                          |
| `boolean`                                              | `isValidKey(@NotNull ItemKey key)`                                                               | 检查此 `ItemProvider` 是否能识别并处理该 `ItemKey`。                                                      |
| `@Nullable ItemStack`                                  | `item(@NotNull ItemKey key)`                                                                     | 根据 `ItemKey` 创建并返回相应的 `ItemStack`；若无对应物品，则返回 `null`。                                 |
| `default @Nullable ItemStack`                          | `item(@NotNull ItemKey key, @NotNull Player player)`                                              | 同 `item(key)`，但可根据指定玩家的上下文（如权限、等级）生成物品；不支持时返回 `null`。                   |
| `@Nullable ItemKey`                                    | `key(@NotNull ItemStack item)`                                                                   | 从给定的 `ItemStack` 中提取并返回其对应的 `ItemKey`；若无法识别，则返回 `null`。                         |
| `default @Nullable ItemStack`                          | `tryItem(@NotNull ItemKey key, @Nullable Player player)`                                          | 先调用 `isValidKey(key)`，若返回 `true` 则创建物品并返回，否则直接返回 `null`；`player` 可为空。         |

---

## 3. 方法详情（Method Details）

### 3.1 `isValidKey`

```java
boolean isValidKey(@NotNull ItemKey key)
````

* **说明**：判断此提供者能否识别并处理给定的 `ItemKey`。
* **参数**：

  * `key`（非空）— 待校验的物品键。
* **返回**：

  * `true` — 可识别该键并可生成对应物品；
  * `false` — 不支持此键。
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html#isValidKey%28io.github.projectunified.uniitem.api.ItemKey%29)

---

### 3.2 `key`

```java
@Nullable ItemKey key(@NotNull org.bukkit.inventory.ItemStack item)
```

* **说明**：从给定的 `ItemStack` 中提取 `ItemKey`。
* **参数**：

  * `item`（非空）— 待解析的物品堆栈。
* **返回**：

  * 对应的 `ItemKey`；
  * 或者 `null`（若无法识别该物品）。
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html#key%28org.bukkit.inventory.ItemStack%29)

---

### 3.3 `item(ItemKey key)`

```java
@Nullable org.bukkit.inventory.ItemStack item(@NotNull ItemKey key)
```

* **说明**：根据 `ItemKey` 创建并返回对应的 `ItemStack`。
* **参数**：

  * `key`（非空）— 用于生成物品的键。
* **返回**：

  * 新的 `ItemStack`；
  * 或者 `null`（若该键无对应物品）。
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html#item%28io.github.projectunified.uniitem.api.ItemKey%29)

---

### 3.4 `item(ItemKey key, Player player)`

```java
default @Nullable org.bukkit.inventory.ItemStack item(
    @NotNull ItemKey key,
    @NotNull org.bukkit.entity.Player player
)
```

* **说明**：基于 `key` 和 `player` 上下文生成物品，允许根据玩家等级、权限或其他信息定制化物品属性。
* **参数**：

  * `key`（非空）— 物品键；
  * `player`（非空）— 玩家实例。
* **返回**：

  * 定制后的 `ItemStack`；
  * 或者 `null`（若不支持或无法生成）。
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html#item%28io.github.projectunified.uniitem.api.ItemKey,org.bukkit.entity.Player%29)

---

### 3.5 `tryItem`

```java
default @Nullable org.bukkit.inventory.ItemStack tryItem(
    @NotNull ItemKey key,
    @Nullable org.bukkit.entity.Player player
)
```

* **说明**：先通过 `isValidKey(key)` 检查键是否合法，若合法则调用 `item(key, player)`（或 `item(key)`），否则直接返回 `null`。
* **参数**：

  * `key`（非空）— 待尝试的物品键；
  * `player`（可空）— 玩家上下文，若为空则调用无 `player` 参数的方法。
* **返回**：

  * 对应的 `ItemStack`；
  * 或者 `null`（若键不合法或生成失败）。
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html#tryItem%28io.github.projectunified.uniitem.api.ItemKey,org.bukkit.entity.Player%29)

---

### 3.6 `isSimilar`

```java
default boolean isSimilar(
    @NotNull org.bukkit.inventory.ItemStack item,
    @NotNull ItemKey key
)
```

* **说明**：判断给定 `ItemStack` 与 `ItemKey` 对应的物品在核心属性上是否“相似”（例如类型、耐久、元数据等）。
* **参数**：

  * `item`（非空）— 待比较的物品堆栈；
  * `key`（非空）— 目标物品键。
* **返回**：

  * `true` — 两者在关键属性上匹配；
  * `false` — 不匹配。
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html#isSimilar%28org.bukkit.inventory.ItemStack,io.github.projectunified.uniitem.api.ItemKey%29)

