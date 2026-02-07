---
name: dao-layer-infrastructure
description: Full DAO infrastructure flow: generator config, run MyBatis Generator, fix-dao-layer-code, buildExample pattern. Use for new tables, table structure changes, or DAO pagination/query refactoring.
---

# DAO Layer Infrastructure

Complete workflow for DAO layer setup and maintenance. Follows `.cursor/rules/domain/dao-layer.mdc`.

## When to Use

- Adding new tables
- Table structure changes (Flyway migration applied)
- DAO pagination or query refactoring

## Workflow

### Step 1: Edit generatorConfig.xml

- Path: `service/src/main/resources/generator/generatorConfig.xml`
- **Uncomment** only the `<table>` elements for tables you need to (re)generate
- **Comment out** all other `<table>` elements with `<!-- ... -->`

### Step 2: Run MyBatis Generator

```bash
cd service
mvn mybatis-generator:generate
```

Must run from `service/` directory.

### Step 3: Fix DAO Layer Code

Apply fix-dao-layer-code to all generated `*DOMapper.xml`:
- In `Example_Where_Clause` and `Update_By_Example_Where_Clause`: change `separator="or"` to `separator="and"`

### Step 4: Implement buildExample(param) and list(param)

For each table:

- **Param**: Add `XxxQueryParam` with **all** queryable fields (domain/model/query/)
- **buildExample(param)**: ONE method per table, maps ALL param fields to Example. Null-safe. **禁止**多处重复构建 Example
- **list(param)**: Single query entry, `selectByExample(buildExample(param))`
- **findByXxx**: Thin wrappers only, `list(Param.builder().xxx().build())`. **禁止**内联 Example 构建
- Use only `example.createCriteria().andXxx()`, no `example.or()`
- Place buildExample and list in RepositoryImpl (private static / private)

## Pagination Query Pattern

When adding listByQuery/countByQuery for paginated queries:

- **QueryRequest** / **QueryCommand** / **QueryParam**: extend `com.star.common.page.PageRequest`, do NOT redeclare page/pageSize
- **RequestAssembler**: use `BeanUtils.copyProperties(request, cmd)` for QueryCommand conversion
- **Assembler.commandToQueryParam**: set page/pageSize with defaults (1, 10) when null or <=0; return param ready for Repository
- **ApplicationService**: call `commandToQueryParam(command)` and use param directly; **禁止** `param.setPage/setPageSize`

See `.cursor/rules/architecture/pagination-query.mdc` for full spec.

## Checklist

- [ ] generatorConfig: only target tables uncommented
- [ ] mvn mybatis-generator:generate executed from service/
- [ ] All *DOMapper.xml: separator or → and
- [ ] Param covers ALL conditional fields
- [ ] buildExample(param) is the ONLY place that constructs Example for that table
- [ ] findByXxx methods only call list(param), no inline example building
- [ ] Pagination: QueryRequest/Command extend PageRequest; defaults in Assembler only; no redundant setPage/setPageSize in ApplicationService
