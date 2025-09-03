import torch
import torch.nn as nn


class CrossFeatureAttention(nn.Module):
    def __init__(self, dim, attn_drop=0.0, proj_drop=0.0):
        super().__init__()
        self.dim = dim
        self.attn_drop = nn.Dropout(attn_drop)
        self.proj_drop = nn.Dropout(proj_drop)

        # Query (Q), Key (K), and Value (V) transformations
        self.query_proj = nn.Linear(dim, dim)
        self.key_proj = nn.Linear(dim, dim)
        self.value_proj = nn.Linear(dim, dim)

        # Output projection
        self.output_proj = nn.Linear(dim, dim)

        # Softmax for attention calculation
        self.softmax = nn.Softmax(dim=-1)

        # AdaptiveCalibrationModule todo
        # # 为了减少Swin-Transform引入的错误，以防CNN受Swin-Transformer的错误引导
        # self.calibration_module = AdaptiveCalibrationModule(in_channels=dim, reduction=16)

    def forward(self, x1, x2):
        """
        Args:
            x1: First feature map (B, H1*W1, C) -> Query (Q)
            x2: Second feature map (B, H2*W2, C) -> Key (K) and Value (V)
        """
        B1, N1, C = x1.shape
        B2, N2, C = x2.shape

        # Project Q, K, V
        Q = self.query_proj(x1)  # (B1, N1, C)
        K = self.key_proj(x2)  # (B2, N2, C)
        V = self.value_proj(x2)  # (B2, N2, C)

        # Compute attention scores (Q * K^T) and apply softmax
        attn_scores = torch.matmul(Q, K.transpose(-2, -1))  # (B1, N1, N2)
        attn_scores = attn_scores / (self.dim ** 0.5)  # Scale by the square root of the dimension

        attn_weights = self.softmax(attn_scores)  # Attention weights (B1, N1, N2)
        attn_weights = self.attn_drop(attn_weights)

        # Compute weighted values (attn_weights * V)
        attended_values = torch.matmul(attn_weights, V)  # (B1, N1, C)

        # Add the attended values to the original query feature map
        output = Q + attended_values  # (B1, N1, C)

        # Apply the final output projection
        output = self.output_proj(output)  # (B1, N1, C)
        output = self.proj_drop(output)

        return output


if __name__ == '__main__':

    # Example usage
    B = 2  # Batch size
    H1, W1 = 4, 4  # Size of the first feature map (H1 * W1)
    H2, W2 = 8, 8  # Size of the second feature map (H2 * W2)
    C = 64  # Number of channels

    # Random feature maps
    x1 = torch.randn(B, H1 * W1, C)  # (B, H1*W1, C)
    x2 = torch.randn(B, H2 * W2, C)  # (B, H2*W2, C)

    # Create the attention model
    attention_model = CrossFeatureAttention(dim=C)

    # Forward pass
    output = attention_model(x1, x2)
    print(output.shape)  # Expected output shape: (B, H1*W1, C)
