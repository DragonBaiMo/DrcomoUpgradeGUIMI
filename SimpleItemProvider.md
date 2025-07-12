# `SimpleItemProvider` 接口

> **继承接口**：[`ItemProvider`](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html)  
> **已知实现类**：`ExecutableItemsProvider`、`HeadDatabaseProvider`、`ItemBridgeProvider`、`ItemEditProvider`、`ItemsAdderProvider`、`MMOItemsProvider`、`MythicItemProvider`、`NexoProvider`、`NovaItemProvider`、`OraxenProvider`、`SlimefunProvider`

---

## 1. 概览

- **包名**：`io.github.projectunified.uniitem.api`  
- **接口全名**：`io.github.projectunified.uniitem.api.SimpleItemProvider`  
- **简介**：在 `ItemProvider` 基础上，增加以字符串 ID（而非通用 ItemKey）进行物品识别与生成的方法，简化多数场景的使用。

---

## 2. 方法总览（Method Summary）

| 修饰符 & 返回类型                                      | 方法签名                                                                                      | 说明                                                       |
| ----------------------------------------------------- | --------------------------------------------------------------------------------------------- | ---------------------------------------------------------- |
| `@NotNull String`                                     | `type()`                                                                                      | 返回提供者类型标识（如 `"MMOITEMS"`）。                    |
| `@Nullable String`                                    | `id(@NotNull ItemStack item)`                                                                 | 将给定 `ItemStack` 映射到其在此提供者中的字符串 ID；失败返回 `null`。 |
| `@Nullable ItemStack`                                 | `item(@NotNull String id)`                                                                    | 根据字符串 ID 创建并返回物品；ID 不存在时返回 `null`。     |
| `default @Nullable ItemStack`                         | `item(@NotNull String id, @NotNull Player player)`                                            | 同 `item(id)`，但可基于指定玩家上下文生成定制化物品。       |
| `default boolean`                                     | `isValidKey(@NotNull ItemKey key)`                                                            | 检查通用 `ItemKey` 是否可由此提供者解析。                  |
| `default @Nullable ItemStack`                         | `item(@NotNull ItemKey key)`                                                                  | 将通用 `ItemKey` 转为 `ItemStack`；失败返回 `null`。       |
| `default @Nullable ItemStack`                         | `item(@NotNull ItemKey key, @NotNull Player player)`                                          | 同 `item(key)`，但可基于玩家生成定制化物品。                |
| `default @Nullable ItemKey`                           | `key(@NotNull ItemStack item)`                                                                | 将 `ItemStack` 转为通用 `ItemKey`；无法识别时返回 `null`。 |

---

## 3. 继承自 `ItemProvider` 的默认方法

此接口继承自 [`ItemProvider`](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/ItemProvider.html)，并保留以下默认实现：

- `default boolean isSimilar(@NotNull ItemStack item, @NotNull ItemKey key)`  
- `default @Nullable ItemStack tryItem(@NotNull ItemKey key, @Nullable Player player)`

---

## 4. 方法详情（Method Details）

### 4.1 `type`

```java
@NotNull
@NotNull String type()
```

* **说明**：返回此提供者的唯一类型标识，用于注册与选择。
* **实现接口**：`SimpleItemProvider.type()`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#type%28%29)

---

### 4.2 `id`

```java
@Nullable
@Nullable String id(@NotNull org.bukkit.inventory.ItemStack item)
```

* **说明**：将输入的 `ItemStack` 转换为此提供者所使用的字符串 ID，用于反向查找与对比。
* **参数**：

  * `item`（非空）— 待解析的 `ItemStack` 对象。
* **返回**：

  * 对应的字符串 ID；
  * 若不是此提供者管理的物品或无法识别，则返回 `null`。
* **实现接口**：`SimpleItemProvider.id(ItemStack)`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#id%28org.bukkit.inventory.ItemStack%29)

---

### 4.3 `item(String id)`

```java
@Nullable
@Nullable org.bukkit.inventory.ItemStack item(@NotNull String id)
```

* **说明**：根据提供的字符串 ID 创建并返回对应的 `ItemStack`。
* **参数**：

  * `id`（非空）— 目标物品的字符串标识。
* **返回**：

  * 新的 `ItemStack`；
  * 若 ID 无效或未注册，则返回 `null`。
* **实现接口**：`SimpleItemProvider.item(String)`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#item%28java.lang.String%29)

---

### 4.4 `item(String id, Player player)`

```java
default @Nullable
org.bukkit.inventory.ItemStack item(
    @NotNull String id,
    @NotNull org.bukkit.entity.Player player
)
```

* **说明**：同 `item(String)`，但可利用 `player` 上下文（如等级、权限）生成个性化物品。
* **参数**：

  * `id`（非空）— 物品字符串 ID；
  * `player`（非空）— 玩家实例。
* **返回**：

  * 定制化的 `ItemStack`；
  * 若不支持或生成失败，则返回 `null`。
* **实现接口**：`SimpleItemProvider.item(String, Player)`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#item%28java.lang.String,org.bukkit.entity.Player%29)

---

### 4.5 `isValidKey`

```java
default boolean isValidKey(@NotNull ItemKey key)
```

* **说明**：检查通用 `ItemKey` 是否适用于此提供者。
* **参数**：

  * `key`（非空）— 待校验的通用物品键。
* **返回**：

  * `true` — 此提供者可识别并生成对应物品；
  * `false` — 不支持该键。
* **来源接口**：继承自 `ItemProvider`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#isValidKey%28io.github.projectunified.uniitem.api.ItemKey%29)

---

### 4.6 `item(ItemKey key)`

```java
default @Nullable
org.bukkit.inventory.ItemStack item(@NotNull ItemKey key)
```

* **说明**：将通用 `ItemKey` 转为 `ItemStack`，简化调用。
* **参数**：

  * `key`（非空）— 通用物品键。
* **返回**：

  * 新的 `ItemStack`；
  * 若无法识别则返回 `null`。
* **来源接口**：继承并重载自 `ItemProvider.item(ItemKey)`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#item%28io.github.projectunified.uniitem.api.ItemKey%29)

---

### 4.7 `item(ItemKey key, Player player)`

```java
default @Nullable
org.bukkit.inventory.ItemStack item(
    @NotNull ItemKey key,
    @NotNull org.bukkit.entity.Player player
)
```

* **说明**：基于通用键和玩家上下文生成物品，统一接口风格。
* **参数**：

  * `key`（非空）— 通用物品键；
  * `player`（非空）— 玩家实例。
* **返回**：

  * 定制化 `ItemStack`；
  * 或 `null`（不支持或失败）。
* **来源接口**：继承自 `ItemProvider.item(ItemKey, Player)`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#item%28io.github.projectunified.uniitem.api.ItemKey,org.bukkit.entity.Player%29)

---

### 4.8 `key`

```java
default @Nullable
ItemKey key(@NotNull org.bukkit.inventory.ItemStack item)
```

* **说明**：将 `ItemStack` 转为通用 `ItemKey`，方便跨提供者通用处理。
* **参数**：

  * `item`（非空）— 待解析物品堆栈。
* **返回**：

  * 对应 `ItemKey`；
  * 或 `null`（无法识别）。
* **来源接口**：继承自 `ItemProvider.key(ItemStack)`
* **链接**：[方法文档](https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/api/SimpleItemProvider.html#key%28org.bukkit.inventory.ItemStack%29)