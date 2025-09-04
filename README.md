# 乳腺超声图像分割智能辅助诊断系统

## 项目简介
本项目是一个 **Java + Python 混合架构** 的智能诊断系统，主要功能为 **乳腺超声图像的分割与智能辅助诊断**。  

- **Python 端**  
  - 使用自行构建和训练的乳腺超声图像分割网络权重  
  - 提供图像推理服务（分割）  
- **Java 端（Spring Boot 2）**  
  - 集成 Python 分割服务  
  - 提供基于神经网络的智能辅助诊断后端服务  
- **通信机制**  
  - Java 与 Python 服务通过 **RabbitMQ** 消息队列进行交互  

该项目已获得相关 **软件著作权证书**。

---

## 技术栈
- **后端框架**：Spring Boot 2 (Java)
- **深度学习框架**：PyTorch (Python)
- **通信**：RabbitMQ
- **服务框架**：FastAPI + Uvicorn (Python)

---

## 环境准备

### 1. 克隆项目
```bash
git clone <项目地址>
cd <项目根目录>
```

### 2. 部署 Python 服务
进入 `py` 目录并创建 Conda 环境：
```bash
cd py
conda env create -f environment.yml
conda activate <environment_name>
```

启动 Python 服务（需先配置 RabbitMQ 并修改源码中的账号密码）：
```bash
uvicorn main:app --reload
```

### 3. 部署 Java 服务
返回项目根目录，启动 Spring Boot 后端服务：
```bash
cd ..
mvn clean package
java -cp target/<生成的jar包名>.jar com.scu.imageseg.ImageSegApplication
```

或直接在 IDE 中运行：
```
src/main/java/com/scu/imageseg/ImageSegApplication.java
```

---

## RabbitMQ 配置
1. 安装并启动 RabbitMQ  
2. 修改 Python 服务和 Java 服务代码中的 MQ 账号与密码保持一致  
3. 确保两个服务均能正常访问 RabbitMQ 服务器  

---

## 项目结构
```
├── py/                     # Python 端代码（深度学习推理服务）
│   ├── environment.yml      # Python 环境依赖
│   ├── main.py              # FastAPI 主入口
│   └── ...                  # 训练权重与推理代码
├── src/main/java/com/scu/imageseg/ # Java Spring Boot 后端服务
│   └── ImageSegApplication.java
└── README.md
```

---

## 运行流程
1. 启动 **RabbitMQ** 服务  
2. 启动 **Python 服务**（提供图像分割能力）  
3. 启动 **Java Spring Boot 服务**（调用 Python 推理服务，构建诊断后端）  

---

## 许可证
本项目已取得相关 **软件著作权证书**。如需使用或二次开发，请遵循授权协议。  
