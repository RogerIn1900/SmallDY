# SmallDY CI 自动检测说明

## 概述

本项目使用 **GitHub Actions** 作为 CI（持续集成）工具，在每次提交 PR 或推送代码到主分支时自动运行构建、代码质量检查和单元测试，确保代码质量。

配置文件位于：`.github/workflows/android-ci.yml`

---

## 触发条件

| 事件 | 目标分支 | 说明 |
|------|----------|------|
| `pull_request` | `main` / `master` | 向主分支提交 PR 时触发 |
| `push` | `main` / `master` | 直接推送到主分支时触发 |

同一个 PR 连续推送多次时，旧的运行会被自动取消（`cancel-in-progress: true`），避免资源浪费。

---

## 检查项目

CI 流程包含以下 3 项检查，按顺序执行：

### 1. Lint 代码质量检查

```
./gradlew lintDebug
```

- 检查代码中的潜在问题（未使用的资源、性能问题、无障碍问题等）
- 检查结果会作为 artifact 上传（`lint-results-debug.html`）
- 即使检查失败，结果仍会上传，方便排查

### 2. 编译构建检查

```
./gradlew assembleDebug
```

- 编译整个项目的 Debug 版本
- 确保所有代码能正常编译，没有语法错误或依赖问题

### 3. 单元测试

```
./gradlew testDebugUnitTest
```

- 运行 `app/src/test/` 目录下的所有单元测试
- 测试报告会作为 artifact 上传（HTML 格式）
- 即使测试失败，报告仍会上传，方便查看具体失败用例

---

## 运行环境

| 配置项 | 值 |
|--------|-----|
| 运行系统 | `ubuntu-latest` |
| JDK 版本 | 17 (Temurin) |
| Gradle | 通过 `gradle/actions/setup-gradle@v4` 自动配置并缓存 |

---

## 查看结果

### 在 PR 页面查看

每个 PR 底部会显示检查状态：
- **绿色对勾** — 所有检查通过
- **红色叉号** — 有检查失败，点击 "Details" 查看日志

### 下载检测报告

1. 进入 PR 页面，点击 "Checks" 标签
2. 点击 "Build, Lint & Test"
3. 页面底部 "Artifacts" 区域可下载：
   - **lint-results** — Lint 检查的 HTML 报告
   - **test-results** — 单元测试的 HTML 报告

---

## 分支保护规则配置

要强制所有 PR 必须通过 CI 检查才能合并，需要在 GitHub 仓库中设置分支保护规则：

1. 进入仓库页面，点击 **Settings**
2. 左侧菜单选择 **Branches**
3. 点击 **Add branch protection rule**
4. **Branch name pattern** 填写 `main`
5. 勾选以下选项：
   - **Require a pull request before merging** — 禁止直接推送到 main
   - **Require status checks to pass before merging** — 要求 CI 通过
   - 在搜索框中搜索并勾选 `Build, Lint & Test`
   - **Require branches to be up to date before merging**（可选）— 要求分支与 main 保持最新
6. 点击 **Save changes**

---

## 常见问题

### Q: CI 构建失败怎么办？

查看 GitHub Actions 的运行日志，定位失败的步骤：
- **Lint 失败**：查看 lint-results artifact，修复报告中的问题
- **Build 失败**：检查编译错误日志，通常是代码语法或依赖问题
- **Test 失败**：查看 test-results artifact，修复失败的测试用例

### Q: 如何跳过 CI 检查？

在 commit message 中添加 `[skip ci]`，该次提交不会触发 CI。仅建议在修改文档等非代码变更时使用。

### Q: CI 运行时间太长？

Gradle 构建缓存由 `gradle/actions/setup-gradle@v4` 自动管理，首次运行较慢（约 5-10 分钟），后续运行会利用缓存加速（约 2-5 分钟）。
