# Reward Service API 接口文档

## 概述

本文档描述 Reward Service（奖励服务）的 RESTful API 接口设计。Reward Service 负责管理任务模板、任务执行记录和商品信息。

**基础路径**: `/api/reward`

**统一响应格式**:

```json
{
  "success": true,
  "data": { ... },
  "message": "操作成功"
}
```

**错误响应格式**:

```json
{
  "success": false,
  "error": "错误信息"
}
```

---

## 一、任务模板相关接口

### 1.1 获取所有任务模板

**接口**: `GET /api/reward/task-templates`

**说明**: 获取所有任务模板（包括预设和自定义）

**请求参数**: 无

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": 1,
        "templateNo": "TMP001",
        "name": "结构化",
        "description": "进行结构化学习",
        "isPreset": true,
        "createTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 1
  }
}
```

**响应类型**:

```typescript
interface GetTaskTemplatesResponse {
  data: TaskTemplate[];
  total: number;
}

interface TaskTemplate {
  id: number;
  templateNo: string;
  name: string;
  description: string;
  isPreset: boolean;
  createTime: string;
}
```

---

### 1.2 创建任务模板

**接口**: `POST /api/reward/task-templates`

**说明**: 创建新的任务模板（仅支持自定义任务）

**请求体**:

```json
{
  "name": "新任务",
  "description": "任务描述"
}
```

**请求类型**:

```typescript
interface CreateTaskTemplateRequest {
  name: string;
  description: string;
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 2,
      "templateNo": "TMP002",
      "name": "新任务",
      "description": "任务描述",
      "isPreset": false,
      "createTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务规则**:
- `name` 和 `description` 必填
- 自动生成 `templateNo`
- `isPreset` 默认为 `false`

---

### 1.3 更新任务模板

**接口**: `PUT /api/reward/task-templates/:id`

**说明**: 更新任务模板（仅支持自定义任务）

**路径参数**:
- `id`: 任务模板ID

**请求体**:

```json
{
  "name": "更新后的任务名称",
  "description": "更新后的描述"
}
```

**请求类型**:

```typescript
interface UpdateTaskTemplateRequest {
  name?: string;
  description?: string;
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 2,
      "templateNo": "TMP002",
      "name": "更新后的任务名称",
      "description": "更新后的描述",
      "isPreset": false,
      "updateTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务规则**:
- 预设任务（`isPreset = true`）不允许更新
- 至少提供一个更新字段

---

### 1.4 删除任务模板

**接口**: `DELETE /api/reward/task-templates/:id`

**说明**: 删除任务模板（仅支持自定义任务）

**路径参数**:
- `id`: 任务模板ID

**响应示例**:

```json
{
  "success": true,
  "message": "任务模板删除成功"
}
```

**业务规则**:
- 预设任务（`isPreset = true`）不允许删除
- 如果存在使用该模板的任务执行记录，不允许删除
- 使用逻辑删除（`is_deleted = 1`）

**错误响应**:

```json
{
  "success": false,
  "error": "Cannot delete task template that has execution records"
}
```

---

## 二、任务执行相关接口

### 2.1 获取所有任务执行记录

**接口**: `GET /api/reward/task-executions`

**说明**: 获取所有任务执行记录

**请求参数**: 无

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": 1,
        "executionNo": "EXE001",
        "userId": "user123",
        "taskTemplateId": 1,
        "taskName": "结构化",
        "startTime": 1704067200,
        "endTime": null,
        "pausedTime": null,
        "totalPausedDuration": 0,
        "actualDuration": null,
        "actualReward": 0,
        "status": "running",
        "createTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 1
  }
}
```

**响应类型**:

```typescript
interface GetTaskExecutionsResponse {
  data: TaskExecution[];
  total: number;
}

interface TaskExecution {
  id: number;
  executionNo: string;
  userId: string;
  taskTemplateId: number;
  taskName: string;
  startTime: number; // 秒级时间戳
  endTime?: number; // 秒级时间戳
  pausedTime?: number; // 秒级时间戳
  totalPausedDuration: number; // 秒
  actualDuration?: number; // 分钟
  actualReward: number;
  status: 'running' | 'paused' | 'completed' | 'cancelled';
  createTime: string;
}
```

---

### 2.2 开始任务

**接口**: `POST /api/reward/task-executions/start`

**说明**: 开始一个新的任务执行

**请求体**:

```json
{
  "taskTemplateId": 1
}
```

**请求类型**:

```typescript
interface StartTaskRequest {
  taskTemplateId: number;
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 1,
      "executionNo": "EXE001",
      "userId": "user123",
      "taskTemplateId": 1,
      "taskName": "结构化",
      "startTime": 1704067200,
      "totalPausedDuration": 0,
      "actualReward": 0,
      "status": "running"
    }
  }
}
```

**业务规则**:
- 一次只能执行一个任务（不能同时有 `running` 或 `paused` 状态的任务）
- 自动生成 `executionNo`
- `startTime` 设置为当前时间戳（秒级）
- `status` 初始化为 `running`

**错误响应**:

```json
{
  "success": false,
  "error": "一次只能执行一个任务！请先完成当前任务。"
}
```

---

### 2.3 暂停任务

**接口**: `POST /api/reward/task-executions/:id/pause`

**说明**: 暂停正在运行的任务

**路径参数**:
- `id`: 任务执行记录ID

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 1,
      "status": "paused",
      "pausedTime": 1704067800
    }
  }
}
```

**业务规则**:
- 只有 `running` 状态的任务可以暂停
- `pausedTime` 设置为当前时间戳（秒级）
- `status` 更新为 `paused`

**错误响应**:

```json
{
  "success": false,
  "error": "Task is not running"
}
```

---

### 2.4 恢复任务

**接口**: `POST /api/reward/task-executions/:id/resume`

**说明**: 恢复已暂停的任务

**路径参数**:
- `id`: 任务执行记录ID

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 1,
      "status": "running",
      "pausedTime": null,
      "totalPausedDuration": 300
    }
  }
}
```

**业务规则**:
- 只有 `paused` 状态的任务可以恢复
- 计算暂停时长：`(当前时间 - pausedTime)`，累加到 `totalPausedDuration`
- `pausedTime` 清空
- `status` 更新为 `running`

**错误响应**:

```json
{
  "success": false,
  "error": "Task is not paused"
}
```

---

### 2.5 完成任务

**接口**: `POST /api/reward/task-executions/:id/complete`

**说明**: 完成任务并计算积分

**路径参数**:
- `id`: 任务执行记录ID

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 1,
      "status": "completed",
      "endTime": 1704070800,
      "actualDuration": 60,
      "actualReward": 60
    },
    "reward": 60
  }
}
```

**业务规则**:
- 计算实际时长：`(当前时间 - startTime - totalPausedDuration) / 60`（分钟）
- 计算积分：`actualDuration * 积分/分钟`（默认 1 积分/分钟）
- `endTime` 设置为当前时间戳（秒级）
- `status` 更新为 `completed`
- **需要调用 user-service 增加积分**：
  - `POST /api/user/points/add` - 增加积分
  - `POST /api/user/point-records` - 创建积分记录

**响应类型**:

```typescript
interface CompleteTaskResponse {
  data: TaskExecution;
  reward: number;
}
```

**错误响应**:

```json
{
  "success": false,
  "error": "Task execution not found"
}
```

---

### 2.6 取消任务

**接口**: `POST /api/reward/task-executions/:id/cancel`

**说明**: 取消任务（不获得积分）

**路径参数**:
- `id`: 任务执行记录ID

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 1,
      "status": "cancelled",
      "endTime": 1704070800,
      "actualReward": 0
    }
  }
}
```

**业务规则**:
- `endTime` 设置为当前时间戳（秒级）
- `actualReward` 设置为 0
- `status` 更新为 `cancelled`
- 不调用 user-service 增加积分

---

## 三、商品相关接口

### 3.1 获取所有商品

**接口**: `GET /api/reward/products`

**说明**: 获取所有商品（包括预设和自定义）

**请求参数**: 无

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": [
      {
        "id": 1,
        "productNo": "PRD001",
        "name": "黄金",
        "description": "兑换黄金",
        "price": 4.8,
        "minQuantity": 0.01,
        "unit": "g",
        "isPreset": true,
        "createTime": "2024-01-01T00:00:00"
      }
    ],
    "total": 1
  }
}
```

**响应类型**:

```typescript
interface GetProductsResponse {
  data: Product[];
  total: number;
}

interface Product {
  id: number;
  productNo: string;
  name: string;
  description: string;
  price: number;
  minQuantity: number;
  unit?: string;
  isPreset: boolean;
  createTime: string;
}
```

---

### 3.2 创建商品

**接口**: `POST /api/reward/products`

**说明**: 创建新的商品（仅支持自定义商品）

**请求体**:

```json
{
  "name": "新商品",
  "description": "商品描述",
  "price": 10,
  "minQuantity": 1,
  "unit": "个"
}
```

**请求类型**:

```typescript
interface CreateProductRequest {
  name: string;
  description: string;
  price: number;
  minQuantity: number;
  unit?: string;
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 2,
      "productNo": "PRD002",
      "name": "新商品",
      "description": "商品描述",
      "price": 10,
      "minQuantity": 1,
      "unit": "个",
      "isPreset": false,
      "createTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务规则**:
- `name`、`description`、`price`、`minQuantity` 必填
- 自动生成 `productNo`
- `isPreset` 默认为 `false`
- `price` 表示每 `minQuantity` 单位所需的积分

---

### 3.3 更新商品

**接口**: `PUT /api/reward/products/:id`

**说明**: 更新商品（仅支持自定义商品）

**路径参数**:
- `id`: 商品ID

**请求体**:

```json
{
  "name": "更新后的商品名称",
  "description": "更新后的描述",
  "price": 12,
  "minQuantity": 2,
  "unit": "个"
}
```

**请求类型**:

```typescript
interface UpdateProductRequest {
  name?: string;
  description?: string;
  price?: number;
  minQuantity?: number;
  unit?: string;
}
```

**响应示例**:

```json
{
  "success": true,
  "data": {
    "data": {
      "id": 2,
      "productNo": "PRD002",
      "name": "更新后的商品名称",
      "description": "更新后的描述",
      "price": 12,
      "minQuantity": 2,
      "unit": "个",
      "isPreset": false,
      "updateTime": "2024-01-01T00:00:00"
    }
  }
}
```

**业务规则**:
- 预设商品（`isPreset = true`）不允许更新
- 至少提供一个更新字段

---

### 3.4 删除商品

**接口**: `DELETE /api/reward/products/:id`

**说明**: 删除商品（仅支持自定义商品）

**路径参数**:
- `id`: 商品ID

**响应示例**:

```json
{
  "success": true,
  "message": "商品删除成功"
}
```

**业务规则**:
- 预设商品（`isPreset = true`）不允许删除
- 删除前需检查：
  - 是否有兑换记录（通过 user-service 查询 `user_point_records`）
  - 背包中是否存在该商品（通过 user-service 查询 `user_inventory`）
- 使用逻辑删除（`is_deleted = 1`）

**错误响应**:

```json
{
  "success": false,
  "error": "Cannot delete product that has exchange records"
}
```

或

```json
{
  "success": false,
  "error": "Cannot delete product that exists in inventory"
}
```

---

## 四、服务间调用

### 4.1 调用 user-service

Reward Service 需要调用 user-service 的以下接口：

#### 4.1.1 增加积分

**接口**: `POST /api/user/points/add`

**说明**: 完成任务时增加用户积分（内部接口）

**请求体**:

```json
{
  "userId": "user123",
  "amount": 60,
  "description": "完成任务: 结构化 (60分钟)"
}
```

#### 4.1.2 创建积分记录

**接口**: `POST /api/user/point-records`

**说明**: 创建积分记录（内部接口）

**请求体**:

```json
{
  "userId": "user123",
  "type": "earn",
  "amount": 60,
  "description": "完成任务: 结构化 (60分钟)",
  "timestamp": 1704070800,
  "relatedId": "EXE001"
}
```

---

## 五、错误码规范

### 5.1 业务错误

| 错误信息 | 说明 |
|---------|------|
| Task template not found | 任务模板不存在 |
| Cannot update preset task template | 不能更新预设任务模板 |
| Cannot delete task template that has execution records | 不能删除有执行记录的任务模板 |
| Task execution not found | 任务执行记录不存在 |
| Task is not running | 任务未运行 |
| Task is not paused | 任务未暂停 |
| 一次只能执行一个任务！请先完成当前任务。 | 已有任务在执行中 |
| Product not found | 商品不存在 |
| Cannot update preset product | 不能更新预设商品 |
| Cannot delete product that has exchange records | 不能删除有兑换记录的商品 |
| Cannot delete product that exists in inventory | 不能删除背包中存在的商品 |

### 5.2 系统错误

| HTTP状态码 | 说明 |
|-----------|------|
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 六、接口调用示例

### 6.1 使用 curl

```bash
# 获取所有任务模板
curl -X GET http://localhost:8080/api/reward/task-templates

# 创建任务模板
curl -X POST http://localhost:8080/api/reward/task-templates \
  -H "Content-Type: application/json" \
  -d '{
    "name": "新任务",
    "description": "任务描述"
  }'

# 开始任务
curl -X POST http://localhost:8080/api/reward/task-executions/start \
  -H "Content-Type: application/json" \
  -d '{
    "taskTemplateId": 1
  }'

# 完成任务
curl -X POST http://localhost:8080/api/reward/task-executions/1/complete

# 获取所有商品
curl -X GET http://localhost:8080/api/reward/products
```

### 6.2 使用 TypeScript/JavaScript

```typescript
// 配置 API 客户端
import { configureApi } from './api/config';

configureApi({
  baseURL: 'http://localhost:8080'
});

// 调用 API
import { TaskTemplateAPI, TaskExecutionAPI, ProductAPI } from './api';

// 获取任务模板
const templates = await TaskTemplateAPI.getTaskTemplates();

// 创建任务模板
const newTemplate = await TaskTemplateAPI.createTaskTemplate({
  name: '新任务',
  description: '任务描述'
});

// 开始任务
const execution = await TaskExecutionAPI.startTask({
  taskTemplateId: 1
});

// 完成任务
const result = await TaskExecutionAPI.completeTask(execution.data.data.id);
```

---

## 七、版本历史

- **v1.0.0** (2024-01-01): 初始版本
  - 任务模板 CRUD 接口
  - 任务执行管理接口
  - 商品 CRUD 接口
