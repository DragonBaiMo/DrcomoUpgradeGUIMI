The plugin requires obtaining the MMOItems item type from an `ItemStack` to match
whitelist entries of format `TYPE;;ID`. The current UniItem `MMOItemsProvider`
API only exposes `id(ItemStack)` without a corresponding method to fetch the type.
It would be helpful for `DrcomoCoreLib` or UniItem to provide:

```
@Nullable String type(@NotNull ItemStack item);
```

so whitelist validation can be accurate without relying on NBT parsing.
