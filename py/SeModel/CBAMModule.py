import torch
import torch.nn as nn
import torch.nn.functional as F

class CBAM(nn.Module):
    def __init__(self, in_channels):
        super(CBAM, self).__init__()

        # Channel Attention
        self.channel_attention = nn.Sequential(
            nn.AdaptiveAvgPool2d(1),  # Global Average Pooling
            nn.Conv2d(in_channels, in_channels // 16, 1, padding=0),  # 1x1 Convolution
            nn.ReLU(inplace=True),
            nn.Conv2d(in_channels // 16, in_channels, 1, padding=0),  # 1x1 Convolution
            nn.Sigmoid()  # Sigmoid to generate attention weights
        )

        # Spatial Attention
        self.spatial_attention = nn.Sequential(
            nn.Conv2d(in_channels, 1, 7, padding=3),  # 7x7 Convolution for spatial attention
            nn.Sigmoid()  # Sigmoid to generate spatial attention map
        )

    def forward(self, x):
        # Step 1: Apply Channel Attention
        channel_attention = self.channel_attention(x)
        x = x * channel_attention  # Apply the channel attention to the input

        # Step 2: Apply Spatial Attention
        spatial_attention = self.spatial_attention(x)
        x = x * spatial_attention  # Apply the spatial attention to the input

        return x


# Example usage of CBAM
if __name__ == '__main__':
    # Example input: Batch size = 2, Channels = 64, Height = 128, Width = 128
    B, C, H,  W = 2, 64, 128, 128
    input_tensor = torch.randn(B, C, H, W)  # Random input feature map (B, C, H, W)

    # Create an instance of CBAM
    cbam = CBAM(in_channels=C)

    # Forward pass through CBAM
    output_tensor = cbam(input_tensor)

    # Print the shapes of the input and output
    print("Input tensor shape:", input_tensor.shape)
    print("Output tensor shape:", output_tensor.shape)
