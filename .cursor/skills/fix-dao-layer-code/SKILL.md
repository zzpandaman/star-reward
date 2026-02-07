---
name: fix-dao-layer-code
description: After MyBatis Generator runs, fix Mapper XML to use and instead of or for criteria connection. Use when generator regenerates XML, table structure changes, or user requests fix-dao-layer-code.
---

# Fix DAO Layer Code

Modifies generated Mapper XML so multiple criteria are connected with `and` instead of `or`, enabling clearer business query semantics and easier extension.

## When to Use

- After running `mvn mybatis-generator:generate`
- After table structure changes that regenerate Mapper XML
- When user explicitly requests fix-dao-layer-code

## Steps

1. **Locate Mapper XMLs** in `service/src/main/resources/mapper/*DOMapper.xml` (star-reward) or equivalent path in star-sso.

2. **Find and replace** in each file:
   - In `Example_Where_Clause` sql block: `separator="or"` → `separator="and"`
   - In `Update_By_Example_Where_Clause` sql block: `separator="or"` → `separator="and"`

3. **Pattern**: The blocks contain `<foreach collection="oredCriteria" item="criteria" separator="or">`. Change `separator="or"` to `separator="and"` in both places per file.

## Example

```xml
<!-- Before -->
<foreach collection="oredCriteria" item="criteria" separator="or">

<!-- After -->
<foreach collection="oredCriteria" item="criteria" separator="and">
```

## Scope

Apply to **all** `*DOMapper.xml` files that contain `Example_Where_Clause` or `Update_By_Example_Where_Clause`. Generator overwrites these on each run, so fix must be re-applied after every generation.
