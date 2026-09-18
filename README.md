# CS61B Spring 2025 — Labs, Homeworks, and Projects

我的 UC Berkeley [CS 61B Data Structures (Spring 2025)](https://sp25.datastructur.es/)
自学完成情况。所有 starter 文件来自官方 skeleton 仓库
（`Berkeley-CS61B/skeleton-sp25`，经 `git pull skeleton main` 工作流），
实现代码与测试均为独立完成。

## 完成状态

| 作业 | 内容 | 状态 |
|------|------|------|
| Lab 1 | Setup（修 `Arithmetic.sum`） | ✅ 2/2 测试通过 |
| HW 0A | Java 基础练习（三角/索引/口吃/象限 + 3 个手算输出） | ✅ 4/4 |
| HW 0B | List/Map 练习 + Dessert 类 | ✅ 12/12 |
| Lab 2 | Bomb Lab（调试破解 3 个 phase 密码） | ✅ 3/3 |
| Lab 3 | Debugging（冒险游戏 6 处 bug 修复 + Puzzle 谜题） | ✅ 5/5 |
| Lab 4 | Git 练习（Part 1.1 + Part 2 三个隐藏密码） | ✅ 代码完成，magic word 需 Beacon 表单 ⚠️ |
| Lab 5 | Proj 1 反思 | ✅ self_reflection 已填，magic word 需现场 lab ⚠️ |
| Lab 6 | BSTMap（BST 版 Map61B + 速度对比） | ✅ 12/12 |
| Lab 7 | MyHashMap（哈希表 + 5 种桶类型测试 + resize） | ✅ 57/57 |
| Lab 8 | LLRB 红黑树（精确旋转/翻转计数） | ✅ 8/8 |
| Lab 9 | Proj 3 引导（可选，0 分） | Task 1 完成，其余任务为 GUI 练习（可选） |
| Lab 10 | Tetris（可选，0 分） | ✅ 5 个方法全部实现，消行/计分验证通过 |
| Lab 11 | 比较排序（Heap/Merge/Quick + 3-scan partition） | ✅ 5/5 |
| HW 2 | Percolation（Union-Find + Monte Carlo 阈值 0.59） | ✅ 6/6 |
| Proj 0 | 2048（GameLogic：移动/合并/tilt 四方向） | ✅ 63/63 |
| Proj 1A | LinkedListDeque61B（循环哨兵双向链表） | ✅ 12/12 |
| Proj 1B | ArrayDeque61B（循环数组）+ Maximizer + GuitarString | ✅ 17/17 |
| Proj 2A | Ngordnet NGrams（TimeSeries + NGramMap + Handlers） | ✅ 6/6 |
| Proj 2B | Ngordnet Wordnet（WordNet 图 + HyponymsHandler + k≠0） | ✅ 6/6 |
| Proj 3 | BYOW（世界生成 + 菜单 + 交互 + HUD + 存档 + 视野光照） | ✅ 编译 + 生成/存档验证通过，DESIGN.md 见设计文档 |
| Disjoint Sets Lab | 并查集练习 | 无 starter 文件（纯概念题，见课程网站） |

⚠️ 说明：Lab 4/Lab 5 的 magic word 需要通过伯克利的 Beacon/现场 lab
获取，非注册学生无法完成，已在对应文件中留占位说明。

## 数据文件

Proj 2A/2B 需要 Ngordnet 数据（约 143MB），未包含在仓库中。
下载方式见 [Proj 2A spec](https://sp25.datastructur.es/projects/proj2a/)，
解压后按以下结构放置：

```
proj2a/data/ngrams/*.csv
proj2a/data/wordnet/*.txt
proj2b/data/  （同结构，或手动复制）
```

## 构建与测试

- JDK 17+（课程要求）
- 依赖 jar 来自官方 `library-sp25` 仓库（clone 到 `../library-sp25`）
- 命令行跑测试：`bash ../run_tests.sh <作业目录> <TestClass>...`
  （`run_tests.sh` 用 JUnit Platform Launcher 编译并运行指定测试类）
- 图形作业（Proj 3、Lab 10）运行 `main` 方法启动窗口

## 学术诚信说明

本仓库为私人学习用途。伯克利课程政策禁止公开分享作业代码，
本仓库保持 **私有**。请不要把你的版本上传到公开仓库。
