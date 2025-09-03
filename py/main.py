import os

from fastapi import FastAPI

from SeModel.ModelWithMetrics import ModelWithMetrics
from rabbitmq import RabbitMQ

os.environ["KMP_DUPLICATE_LIB_OK"] = "TRUE"

app = FastAPI()

rabbitmq = RabbitMQ(queue_name="model1")

baseDir = "img"


@app.on_event("startup")
def startup_event():
    """应用启动时，连接 RabbitMQ 并监听队列"""
    rabbitmq.connect()

    def process_message(message):
        print(f"Received: {message}")
        # 在这里处理消息逻辑

        # 创建ModelWithMetrics实例
        data_dir = baseDir + '/' + message
        output_dir = baseDir + '/' + message + "_segmentation"
        model_with_metrics = ModelWithMetrics(data_dir, output_dir)

        # 生成分割图像
        model_with_metrics.image_segmentation()

        # 获取 output_dir 目录下所有文件名，并拼接 URL
        if os.path.exists(output_dir):
            file_names = os.listdir(output_dir)  # 读取文件列表
            file_urls = [f"{message}_segmentation/{file}" for file in file_names]
        else:
            file_urls = []

        # 返回 JSON 结果
        return {
            "code": 200,
            "status": "success",
            "message": "Segmentation completed",
            "diagnosis_id": message,
            "images": file_urls
        }

    rabbitmq.consume_messages(process_message)

@app.on_event("shutdown")
def shutdown_event():
    """应用关闭时断开连接"""
    if rabbitmq.connection:
        rabbitmq.connection.close()

@app.post("/send/")
def send_message(message: str):
    """发送消息到 RabbitMQ"""
    rabbitmq.publish_message(message)
    return {"status": "Message sent"}

@app.get("/")
async def root():
    return {"message": "Hello World"}


@app.get("/hello/{name}")
async def say_hello(name: str):
    return {"message": f"Hello {name}"}


