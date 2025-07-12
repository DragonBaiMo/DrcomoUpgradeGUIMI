# io.github.projectunified.uniitem.mmoitems 包开发文档

## 1. 包概述

* **包名**：`io.github.projectunified.uniitem.mmoitems` ([Project Unified][1])
* **功能描述**：提供与 MMOItems 插件的集成实现，基于 `SimpleItemProvider` 接口，通过 `MMOItemsProvider` 获取和转换 MMOItems 中定义的物品。

## 2. 类结构

| 类名                 | 说明                                         |                        |
| ------------------ | ------------------------------------------ | ---------------------- |
| `MMOItemsProvider` | 实现了 `SimpleItemProvider`，用于与 MMOItems 插件交互 | ([Project Unified][1]) |

## 3. MMOItemsProvider 类

```java
public class MMOItemsProvider extends Object implements io.github.projectunified.uniitem.api.SimpleItemProvider
```

继承自 `java.lang.Object`，实现接口 `SimpleItemProvider`（并间接实现 `ItemProvider`）。 ([Project Unified][2])

### 3.1 构造方法

| 构造方法签名                      | 说明                              |                        |
| --------------------------- | ------------------------------- | ---------------------- |
| `public MMOItemsProvider()` | 默认构造，初始化 `MMOItemsProvider` 实例。 | ([Project Unified][2]) |

### 3.2 方法一览

#### 静态方法

| 方法签名                                  | 说明                     |                        |
| ------------------------------------- | ---------------------- | ---------------------- |
| `public static boolean isAvailable()` | 检查 MMOItems 插件是否已加载并可用 | ([Project Unified][2]) |

#### 实例方法

| 方法签名                                                                                                          | 说明                                                     |                        |
| ------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------ | ---------------------- |
| `@Nullable String id(@NotNull org.bukkit.inventory.ItemStack item)`                                           | 根据传入的 `ItemStack` 返回其在 MMOItems 中的物品 ID；不存在时返回 `null`。 | ([Project Unified][2]) |
| `@Nullable org.bukkit.inventory.ItemStack item(@NotNull String id)`                                           | 根据物品 ID 从 MMOItems 中获取对应的 `ItemStack`；不存在时返回 `null`。   | ([Project Unified][2]) |
| `@Nullable org.bukkit.inventory.ItemStack item(@NotNull String id, @NotNull org.bukkit.entity.Player player)` | 根据物品 ID 和玩家上下文获取 `ItemStack` 实例；不存在时返回 `null`。         | ([Project Unified][2]) |
| `@NotNull String type()`                                                                                      | 返回提供者类型标识（在接口中定义，通常为 `"MMOItems"`）。                    | ([Project Unified][2]) |

---

*以上内容均根据 UniItem 2.2.1 API 文档提取。*

[1]: https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/mmoitems/package-summary.html "io.github.projectunified.uniitem.mmoitems (UniItem 2.2.1 API)"
[2]: https://projectunified.github.io/UniItem/io/github/projectunified/uniitem/mmoitems/MMOItemsProvider.html "MMOItemsProvider (UniItem 2.2.1 API)"
