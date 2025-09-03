import json
import os
import torch
from torch.utils.data import DataLoader
import numpy as np
from tqdm import tqdm
import cv2
from data_loader import LoadData
from SeModel.CNNSwinTransformerNet import CNNSwinTransformerNet

device = torch.device('cuda' if torch.cuda.is_available() else 'cpu')


class ModelWithMetrics:
    def __init__(self, data_dir, output_dir):
        # 配置参数
        self.config = self.load_config('SeModel/config/config.json')
        print(self.config)
        self.checkpoint_path = 'SeModel/snapshot/best_model.pth'
        self.data_dir = data_dir
        self.output_dir = output_dir
        self.data_list_dir = data_dir
        self.model = CNNSwinTransformerNet(self.config).to(device)  # 创建模型
        self.model.load_from(self.config)  # 加载预训练参数
        self.model = self.load_model(self.checkpoint_path)  # 加载最佳权重

        # 创建输出目录
        os.makedirs(self.output_dir, exist_ok=True)

    def load_model(self, checkpoint_path):
        """加载模型权重"""
        self.model.load_state_dict(torch.load(checkpoint_path))  # 加载模型权重
        self.model.to(device)
        self.model.eval()  # 设置为评估模式
        return self.model

    def evaluate(self, test_loader, image_info):
        """使用模型进行图像分割"""
        self.model.eval()
        with torch.no_grad():
            for i_batch, sampled_batch in tqdm(enumerate(test_loader), desc="Testing", total=len(test_loader)):
                image_batch, image_name = sampled_batch['image'], sampled_batch['case_name']
                image_batch = image_batch.to(device)

                # 使用模型进行图像分割
                outputs = self.model(image_batch)

                # 获取分割结果 (假设是softmax后的结果)
                pred = torch.argmax(outputs, dim=1)  # 输出的维度是 [B, C, H, W]

                # 保存预测结果
                output = pred.squeeze(0).cpu().numpy() * 255  # 转为图像保存
                output = output.astype(np.uint8)

                # todo 获取原始图像的尺寸 (H, W)
                w = image_info[image_name[0]]['width']
                h = image_info[image_name[0]]['height']

                # 调整预测图像为原始图像的尺寸
                output_resized = cv2.resize(output, (w, h), interpolation=cv2.INTER_NEAREST)

                # 保存调整后的预测结果
                finalname = os.path.join(self.output_dir, image_name[0])
                cv2.imwrite(finalname, output_resized)

    def load_data(self, test_data_dir, list_dir, split='Test_Images'):
        """加载超声图像数据"""
        # 初始化 image_info 为一个字典
        image_info = {}

        # 遍历 base_dir 获取所有图片名称和分辨率
        for img_name in os.listdir(test_data_dir):
            img_path = os.path.join(test_data_dir, img_name)
            img = cv2.imread(img_path)  # 使用 OpenCV 读取图片
            if img is not None:
                height, width = img.shape[:2]  # 获取图片分辨率

                # 将图片信息以键值对形式加入 image_info
                image_info[img_name] = {'height': height, 'width': width}

        data = LoadData(base_dir=test_data_dir, list_dir=list_dir, split=split)
        data_loader = DataLoader(data, batch_size=1, shuffle=False, num_workers=4, pin_memory=True)
        return data_loader, image_info

    def load_config(self, config_file):
        """加载配置文件"""
        with open(config_file, 'r') as f:
            return json.load(f)

    def image_segmentation(self):
        """生成分割图像"""
        # 加载测试数据
        data_loader, image_info = self.load_data(self.data_dir, self.data_list_dir)

        # 在测试集上评估模型
        self.evaluate(data_loader, image_info)

        return os.listdir(self.output_dir)


if __name__ == '__main__':

    data_dir = 'test/Test_Images'

    output_dir = 'test/Test_Images' + '_segmentation'

    # 创建ModelWithMetrics实例
    model_with_metrics = ModelWithMetrics(data_dir, output_dir)

    # 生成分割图像
    model_with_metrics.image_segmentation()
