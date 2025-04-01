package com.scu.imageseg.service.impl;

import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.IImageProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
@Slf4j
@Service
public class ImageProcessingServiceImpl implements IImageProcessingService {
    /**
     * 处理Base64图片并与指定路径的图片合并
     * @param base64Image
     * @param overlayImagePath
     * @return
     */
    @Override
    public BufferedImage mergeImages(String base64Image, String overlayImagePath) throws IOException {
        // 解码 Base64 图片
        BufferedImage baseImage = decodeBase64ToImage(base64Image);
        if (baseImage == null) {
            throw new IOException("无法解析Base64编码的图片");
        }

        // 读取本地 overlay 图片
        File overlayFile = new File(overlayImagePath);
        if (!overlayFile.exists()) {
            throw new FileNotFoundException("服务器上的图片文件未找到: " + overlayImagePath);
        }
        BufferedImage overlayImage = ImageIO.read(overlayFile);

        // 获取 overlayImage 的尺寸
        int width = overlayImage.getWidth();
        int height = overlayImage.getHeight();

        // 调整 baseImage 尺寸，使其匹配 overlayImage
        BufferedImage resizedBaseImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedBaseImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(baseImage, 0, 0, width, height, null);
        g2d.dispose();

        // 创建最终合成图像
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combinedImage.createGraphics();

        // 先绘制 overlayImage（背景）
        g.drawImage(overlayImage, 0, 0, null);

        // 再绘制调整后的 baseImage（前景）
        g.drawImage(resizedBaseImage, 0, 0, null);


        // 释放资源
        g.dispose();

        return combinedImage;
    }


    /**
     * 解码Base64字符串为BufferedImage
     */
    private BufferedImage decodeBase64ToImage(String base64Str) throws IOException {
        String dataPrix = ""; //base64格式前头
        String data = "";//实体部分数据
        if(base64Str==null||"".equals(base64Str)){
            log.error("数据为空！");
            throw new ServiceException(226, "数据为空！");
        }else {
            String [] d = base64Str.split("base64,");//将字符串分成数组
            if(d.length == 2){
                dataPrix = d[0];
                data = d[1];
            }else {
                log.error("数据不合法！");
                throw new ServiceException(227, "数据不合法！");
            }
        }
        String suffix = "";//图片后缀，用以识别哪种格式数据
        //data:image/jpeg;base64,base64编码的jpeg图片数据
        if("data:image/jpeg;".equalsIgnoreCase(dataPrix)){
            suffix = ".jpg";
        }else if("data:image/x-icon;".equalsIgnoreCase(dataPrix)){
            //data:image/x-icon;base64,base64编码的icon图片数据
            suffix = ".ico";
        }else if("data:image/gif;".equalsIgnoreCase(dataPrix)){
            //data:image/gif;base64,base64编码的gif图片数据
            suffix = ".gif";
        }else if("data:image/png;".equalsIgnoreCase(dataPrix)){
            //data:image/png;base64,base64编码的png图片数据
            suffix = ".png";
        }else {
            log.error("图片格式不合法！");
            throw new ServiceException(228, "图片格式不合法！");
        }

        byte[] imageBytes = Base64.getDecoder().decode(data);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bis);
        }

    }


}
