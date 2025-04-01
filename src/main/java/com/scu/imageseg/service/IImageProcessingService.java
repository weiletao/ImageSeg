package com.scu.imageseg.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wlt
 * @since 2025-03-28
 */
public interface IImageProcessingService {
    BufferedImage mergeImages(String base64Image, String overlayImagePath) throws IOException;
}
