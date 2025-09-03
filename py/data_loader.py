import os
from torchvision import transforms
from PIL import Image
from torch.utils.data import Dataset

# 定义图像预处理
transform = transforms.Compose([
    transforms.ToTensor()
])


class LoadData(Dataset):
    def __init__(self, base_dir, list_dir, split):
        self.split = split
        # 读取样本列表（每行包含图像文件名）
        self.sample_list = os.listdir(base_dir) # open(os.path.join(list_dir, self.split + '.txt')).readlines()
        self.data_dir = base_dir

    def __len__(self):
        # 返回样本数量
        return len(self.sample_list)

    def __getitem__(self, idx):
        # 获取当前样本的文件名
        image_name = self.sample_list[idx].strip()  # 去掉换行符
        image_path = os.path.join(self.data_dir, image_name)

        # 加载图像为RGB格式（3通道）
        image = Image.open(image_path).convert('RGB')
        image = image.resize((224, 224))  # 调整图像大小

        # 使用 ToTensor 转换图像为tensor格式
        image = transform(image)  # 将图像转换为Tensor

        # 创建样本字典
        sample = {'image': image}

        # 将样本的文件名加入字典
        sample['case_name'] = image_name

        # 返回样本
        return sample
