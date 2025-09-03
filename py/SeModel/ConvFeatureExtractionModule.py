import torch
import torch.nn as nn


class ComplexDilatedConvModule(nn.Module):
    def __init__(self, in_channels, out_channels):
        super(ComplexDilatedConvModule, self).__init__()

        # 第一层卷积（标准卷积）提取全局特征
        self.conv1 = nn.Conv2d(in_channels, out_channels, kernel_size=3, stride=1, padding=1)
        self.bn1 = nn.BatchNorm2d(out_channels)
        self.relu = nn.ReLU(inplace=True)
        # self.cbam1 = CBAM(out_channels)  # Apply CBAM after first conv

        # 第二层扩张卷积（dilated convolution）提取细节特征，增加感受野
        self.dilated_conv1 = nn.Conv2d(out_channels, out_channels, kernel_size=3, stride=1, padding=2, dilation=2)
        self.bn2 = nn.BatchNorm2d(out_channels)
        # self.cbam2 = CBAM(out_channels)  # Apply CBAM after dilated conv

        # 第三层深度可分卷积（depthwise separable convolution）提取更细粒度的特征
        self.depthwise_conv = nn.Conv2d(out_channels, out_channels, kernel_size=3, stride=1, padding=1, groups=out_channels)
        self.bn3 = nn.BatchNorm2d(out_channels)
        # self.cbam3 = CBAM(out_channels)  # Apply CBAM after depthwise conv

        # 第四层扩张卷积进一步扩大感受野
        self.dilated_conv2 = nn.Conv2d(out_channels, out_channels, kernel_size=3, stride=1, padding=3, dilation=3)
        self.bn4 = nn.BatchNorm2d(out_channels)
        # self.cbam4 = CBAM(out_channels)  # Apply CBAM after second dilated conv

        # 第五层卷积（标准卷积）用于提取更深层次的特征
        self.conv2 = nn.Conv2d(out_channels, out_channels, kernel_size=3, stride=1, padding=1)
        self.bn5 = nn.BatchNorm2d(out_channels)
        # self.cbam5 = CBAM(out_channels)  # Apply CBAM after last conv

        # 残差连接
        self.residual = nn.Conv2d(in_channels, out_channels, kernel_size=1, stride=1, padding=0)

        # 卷积输出通道数
        self.output_channels = out_channels

    def forward(self, x):
        # 通过第一层卷积提取全局特征
        x1 = self.conv1(x)
        x1 = self.bn1(x1)
        # x1 = self.cbam1(x1)  # Apply CBAM
        x1 = self.relu(x1)

        # 通过扩张卷积提取更大感受野的细节特征
        x2 = self.dilated_conv1(x1)
        x2 = self.bn2(x2)
        # x2 = self.cbam2(x2)  # Apply CBAM
        x2 = self.relu(x2)

        # 通过深度可分卷积提取细粒度特征
        x3 = self.depthwise_conv(x2)
        x3 = self.bn3(x3)
        # x3 = self.cbam3(x3)  # Apply CBAM
        x3 = self.relu(x3)

        # 通过第二层扩张卷积进一步提取大范围特征
        x4 = self.dilated_conv2(x3)
        x4 = self.bn4(x4)
        # x4 = self.cbam4(x4)  # Apply CBAM
        x4 = self.relu(x4)

        # 通过最后一层卷积提取更深层次的特征
        x5 = self.conv2(x4)
        x5 = self.bn5(x5)
        # x5 = self.cbam5(x5)  # Apply CBAM
        x5 = self.relu(x5)

        # 添加残差连接
        residual = self.residual(x)
        output = x5 + residual

        return output


if __name__ == '__main__':
    # Example usage
    batch_size, channels, height, width = 4, 32, 128, 128,
    x = torch.randn(batch_size, channels, height, width)  # Sample input feature map

    conv_layer = ComplexDilatedConvModule(in_channels=channels, out_channels=channels * 2)
    output = conv_layer(x)

    print(output.shape)  # Should output (batch_size, 2 * channels, height, width)
