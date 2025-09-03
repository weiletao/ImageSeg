import os
import pymysql
from PIL import Image
import io

# 配置数据库连接
db_config = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "root",
    "database": "deepface"
}

# 目标存储文件夹
IMAGE_DIR = "student_images"
os.makedirs(IMAGE_DIR, exist_ok=True)  # 确保目录存在

def save_blob_images():
    """从数据库读取 MEDIUMBLOB 图片并保存为 PNG"""
    connection = pymysql.connect(**db_config)
    cursor = connection.cursor()

    # 查询所有学生的图片
    cursor.execute("SELECT id, image FROM student WHERE image IS NOT NULL")
    rows = cursor.fetchall()

    for student_id, image_blob in rows:
        if not image_blob:
            continue  # 跳过空图片

        # 将 BLOB 转换为 PNG
        image = Image.open(io.BytesIO(image_blob))
        file_path = os.path.join(IMAGE_DIR, f"{student_id}.png")

        # 以 PNG 格式保存
        image.save(file_path, "PNG")
        print(f"✅ 已保存: {file_path}")

    cursor.close()
    connection.close()

# 执行保存
save_blob_images()