# FTC 比赛 22438 团队代码仓库

欢迎来到 FTC 比赛 22438 团队的代码仓库！请按照以下说明配置开发环境。

## 环境配置

### 1. Git 和 GitHub 配置
- 安装 Git 并完成基础配置
- 绑定您的 GitHub 账号（建议使用 SSH 密钥）

### 2. SSH 连接问题解决

如果遇到 SSH 连接错误：
bash
ssh: connect to host github.com port 22: Connection refused


**解决方案：**

1. **创建 SSH 配置文件**
    - 路径：`C:\Users\[你的用户名]\.ssh\config`
    - 示例（用户名为 Anna）：`C:\Users\Anna\.ssh\config`
    - 如果文件不存在，请新建一个**无后缀名**的文本文件

2. **添加以下配置内容：**

   Host github.com
   Hostname ssh.github.com
   Port 443


3. **详细教程参考：**  
   [GitHub SSH 连接问题解决方案](https://annachengdesu.github.io/post/problem-solved/github-ssh-connection-refuse/)

## 开发规范

- 及时拉取最新代码（`git pull`）
- 提交前测试功能完整性
- 编写清晰的提交信息
- 遇到问题先查阅 GitHub Issues

## 快速开始

git clone [仓库地址]

Android studio配置好版本控制（git）

开始开发...


## 获取帮助

- 查阅 GitHub 官方文档
- 在团队群内讨论技术问题
- 利用互联网搜索解决方案


