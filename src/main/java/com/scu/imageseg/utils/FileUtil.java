package com.scu.imageseg.utils;

import com.scu.imageseg.exception.ServiceException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 *  文件工具类 实现文件操作（如文件资源存储到本地文件夹）
 * </p>
 *
 * https://blog.csdn.net/weixin_52065369/article/details/120412307
 *
 * @author 晚风亦是救赎
 * @since 2025-03-22
 */
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class FileUtil {

    private MultipartFile multipartFile;

    public String getResourceSavePath() {
        // 这里需要注意的是ApplicationHome是属于SpringBoot的类
        // 获取项目下resources/static/img路径
//        ApplicationHome applicationHome = new ApplicationHome(this.getClass());

        // 保存目录位置根据项目需求可随意更改
//        return applicationHome.getDir().getParentFile()
//                .getParentFile().getAbsolutePath() + "\\src\\main\\resources\\static\\img";
        return System.getProperty("user.dir") + "/py/img";
    }

    public String saveFile(Long dId) {
        // 给文件重命名
        String fileName = UUID.randomUUID() + "." + multipartFile.getContentType()
                .substring(multipartFile.getContentType().lastIndexOf("/") + 1);
        // 获取保存路径
        String path = getResourceSavePath() + "\\" + dId;
        try {
            File files = new File(path, fileName);
            File parentFile = files.getParentFile();
            if (!parentFile.exists()) {
                if(parentFile.mkdir()) log.info("创建根目录成功！"); else log.error("创建根目录失败！");
            }
            multipartFile.transferTo(files);
            log.info("保存 文件:{} 成功！", path + "\\" + fileName);
        } catch (IOException e) {
            log.error("文件保存失败！");
            throw new ServiceException(214, "文件保存失败！");
        }
        return dId + "/" + fileName; // 返回重命名后的文件名
    }

    /**
     * 将byteArray文件存储到本地路径下
     * @param data
     * @param filePath
     * @throws IOException
     */
    public void saveToFile(byte[] data, String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile(); // 获取父目录

        // 确保目录存在
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                log.error("无法创建目录: {}", parentDir.getAbsolutePath());
                throw new IOException("目录创建失败: " + parentDir.getAbsolutePath());
            }
        }

        // 写入文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage());
            throw e;
        }
    }

}
