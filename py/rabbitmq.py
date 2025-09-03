import pika
import threading
import json

class RabbitMQ:
    def __init__(self, queue_name='default_queue', host='localhost', port=5672):
        self.queue_name = queue_name
        self.host = host
        self.port = port
        self.connection = None
        self.channel = None

    def connect(self):
        """建立连接并创建通道"""
        credentials = pika.PlainCredentials('wlt', '123456')
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(host=self.host, port=self.port, credentials=credentials))
        self.channel = self.connection.channel()
        self.channel.queue_declare(queue=self.queue_name, durable=True)

    def publish_message(self, message, reply_to=None, correlation_id=None):
        """发送消息到队列，支持 RPC 回调"""
        if not self.channel:
            self.connect()

        properties = pika.BasicProperties(
            delivery_mode=2,  # 让消息持久化
            correlation_id=correlation_id,  # 追踪请求
            reply_to=reply_to  # 指定回调队列
        )

        self.channel.basic_publish(
            exchange='',
            routing_key=self.queue_name,
            body=message,
            properties=properties
        )

    def consume_messages(self, callback):
        """监听队列，支持 RPC 方式"""

        def _consume():
            if not self.channel:
                self.connect()

            def _callback(ch, method, properties, body):
                message = body.decode()
                print(f"Received message: {message}")

                # 处理消息逻辑
                response = callback(message)

                # 发送 RPC 响应到回调队列
                if properties.reply_to:
                    ch.basic_publish(
                        exchange='',
                        routing_key=properties.reply_to,
                        body=json.dumps(response),
                        properties=pika.BasicProperties(correlation_id=properties.correlation_id)
                    )

                ch.basic_ack(delivery_tag=method.delivery_tag)  # 确认消息处理

            self.channel.basic_consume(queue=self.queue_name, on_message_callback=_callback)
            print(f"[*] Waiting for messages on '{self.queue_name}'. To exit, press CTRL+C")
            self.channel.start_consuming()

        thread = threading.Thread(target=_consume, daemon=True)
        thread.start()

