package com.scu.imageseg.service.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;

import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.scu.imageseg.entity.DiagnosisReport;
import com.scu.imageseg.entity.DiagnosisRequest;
import com.scu.imageseg.entity.DiagnosisSubmit;
import com.scu.imageseg.service.IImageProcessingService;
import com.scu.imageseg.service.IPdfReportService;
import com.scu.imageseg.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.element.Table;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Slf4j
@Service
public class PdfReportServiceImpl implements IPdfReportService {

    private static final String rootPath = new FileUtil().getResourceSavePath();
    @Autowired
    private IImageProcessingService iImageProcessingService;

//    @Override
//    public byte[] generatePdf(DiagnosisReport diagnosisReport) {
//        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
//            DiagnosisRequest diagnosis = diagnosisReport.getDiagnosisRequest();
//            DiagnosisSubmit diagnosisSubmit = diagnosisReport.getDiagnosisSubmit();
//
//            PdfWriter writer = new PdfWriter(outputStream);
//            PdfDocument pdfDocument = new PdfDocument(writer);
//            Document document = new Document(pdfDocument);
//
//            // 标题
//            document.add(new Paragraph("诊断报告")
//                    .setFontSize(20)
//                    .setBold()
//                    .setTextAlignment(TextAlignment.CENTER));
//
//            document.add(new Paragraph("诊断 ID: " + diagnosis.getId()));
//
//            // 添加患者信息
//            document.add(new Paragraph("患者信息"));
//            document.add(new Paragraph("姓名: " + diagnosis.getName()));
//            document.add(new Paragraph("出生日期: " + diagnosis.getBirthDate()));
//            document.add(new Paragraph("联系电话: " + diagnosis.getPhone()));
//
//            // 添加病情描述
//            document.add(new Paragraph("病情描述"));
//            document.add(new Paragraph("症状: " + diagnosis.getSymptoms()));
//            document.add(new Paragraph("症状起始时间: " + diagnosis.getStartTime()));
//            document.add(new Paragraph("症状持续时间: " + diagnosis.getDuration()));
//
//            // 添加病史信息
//            document.add(new Paragraph("病史信息"));
//            document.add(new Paragraph("既往病史: " + diagnosis.getMedicalHistory()));
//            document.add(new Paragraph("家族病史: " + diagnosis.getFamilyHistory()));
//            document.add(new Paragraph("其他检查结果: " + diagnosis.getCurrentTreatment()));
//            document.add(new Paragraph("当前治疗: " + diagnosis.getCurrentTreatment()));
//
//            // 添加诊断请求
//            document.add(new Paragraph("诊断请求"));
//            document.add(new Paragraph("急诊诊断: " + diagnosis.getEmergencyDiagnosis()));
//            document.add(new Paragraph("特别关注: " + diagnosis.getSpecialAttention()));
//
//            // 添加超声图像诊断及智能分割图像
//            document.add(new Paragraph("智能超声图像诊断"));
//            int index1 = 1;
//            for(DiagnosisSubmit.ImageData imageData : diagnosisSubmit.getImages()){
//                // 添加标题
//                document.add(new Paragraph("图像" + index1 + "："));
//                /** 处理原超声图像 */
//                // 将原图像与框选png图像叠加
//                String originGraphPath = rootPath + "\\" + imageData.getSegmentedImage().replace("_segmentation/", "/").replace("/", "\\");
//                // 得到叠加后图像
//                BufferedImage bufferedImage = iImageProcessingService.mergeImages(imageData.getOriginalImage(), originGraphPath);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                ImageIO.write(bufferedImage,"png", byteArrayOutputStream);
//                Image img1 = new Image(ImageDataFactory.create(byteArrayOutputStream.toByteArray())).scaleToFit(300, 300);
//                // 将叠加后图像输入pdf文档
//                document.add(img1);
//                /** 处理分割图像 */
//                // 分割图像路径
//                String segmentedImagePath = rootPath + "\\" + imageData.getSegmentedImage().replace("/", "\\");
//                // 输入pdf文档
//                Image img2 = new Image(ImageDataFactory.create(segmentedImagePath)).scaleToFit(300, 300);
//                document.add(img2);
//                // 图像索引自增
//                index1++;
//            }
//
//            // 病灶区域框选 （双重循环对应图像集合与框选区域集合）
//            document.add(new Paragraph("病灶框选"));
//            int index2 = 1;
//            for (List<DiagnosisSubmit.Region> area: diagnosisSubmit.getRegions()){
//                document.add(new Paragraph("图像" + index2 + "的病灶区域："));
//                int index = 1;
//                for (DiagnosisSubmit.Region region: area){
//                    document.add(new Paragraph("区域" + index + "：X: " + region.getX() + ", Y: " + region.getY() +
//                            ", 宽: " + region.getWidth() + ", 高: " + region.getHeight()));
//                    index++;
//                }
//                index2++;
//            }
//
//            // 添加医生诊断意见
//            document.add(new Paragraph("申请信息"));
//            document.add(new Paragraph("申请时间: " + diagnosis.getCreatedAt()));
//            document.add(new Paragraph("诊断时间: " + diagnosis.getUpdatedAt()));
//
//            // 添加医生诊断意见
//            document.add(new Paragraph("医生诊断意见"));
//            document.add(new Paragraph("诊断意见: " + diagnosisSubmit.getOpinion()));
//            document.add(new Paragraph("治疗建议: " + diagnosisSubmit.getSuggestion()));
//
//            document.close();
//            return outputStream.toByteArray();
//
//        } catch (Exception e) {
//            throw new RuntimeException("生成 PDF 失败", e);
//        }
//
//    }
    @Override
    public byte[] generatePdf(DiagnosisReport diagnosisReport) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            DiagnosisRequest diagnosis = diagnosisReport.getDiagnosisRequest();
            DiagnosisSubmit diagnosisSubmit = diagnosisReport.getDiagnosisSubmit();

            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // 加载支持中文的字体
            FontProgram fontProgram = FontProgramFactory.createFont("C:/Windows/Fonts/simsun.ttc,0");
            PdfFont chineseFont = PdfFontFactory.createFont(fontProgram, PdfEncodings.IDENTITY_H);
//            PdfFont chineseFont = PdfFontFactory.createFont("STSongStd-Light", PdfEncodings.IDENTITY_H, true);

            // 标题
            document.add(new Paragraph("诊断报告")
                    .setFont(chineseFont)
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // 诊断 ID
            document.add(new Paragraph("诊断 ID: " + diagnosis.getId()).setFont(chineseFont));

            // 添加患者信息表格
            document.add(new Paragraph("患者信息").setFont(chineseFont).setBold().setFontSize(14).setMarginTop(10));

            Table patientTable = new Table(2);
            patientTable.addCell(new Cell().add(new Paragraph("姓名").setFont(chineseFont)));
            patientTable.addCell(new Cell().add(new Paragraph(diagnosis.getName()).setFont(chineseFont)));
            patientTable.addCell(new Cell().add(new Paragraph("出生日期").setFont(chineseFont)));
            patientTable.addCell(new Cell().add(new Paragraph(diagnosis.getBirthDate().toString()).setFont(chineseFont)));
            patientTable.addCell(new Cell().add(new Paragraph("联系电话").setFont(chineseFont)));
            patientTable.addCell(new Cell().add(new Paragraph(diagnosis.getPhone()).setFont(chineseFont)));
            document.add(patientTable);

            // 病情描述
            document.add(new Paragraph("病情描述").setFont(chineseFont).setBold().setFontSize(14).setMarginTop(10));
            document.add(new Paragraph("症状: " + diagnosis.getSymptoms()).setFont(chineseFont));
            document.add(new Paragraph("症状起始时间: " + diagnosis.getStartTime()).setFont(chineseFont));
            document.add(new Paragraph("症状持续时间: " + diagnosis.getDuration()).setFont(chineseFont));

            // 诊断图像部分
            document.add(new Paragraph("智能超声图像诊断").setFont(chineseFont).setBold().setFontSize(14).setMarginTop(10));
            int index1 = 1;
            // 获取 PDF 页面可用宽度
            float maxTableWidth = pdfDocument.getDefaultPageSize().getWidth() * 0.9f; // 最大宽度 = 页面宽度的 90%
            float maxImageWidth = maxTableWidth / 2 - 10; // 每张图片最多占一半，留些间距
            float maxImageHeight = 300; // 设定最大高度，防止图片过长

            for (DiagnosisSubmit.ImageData imageData : diagnosisSubmit.getImages()) {
                document.add(new Paragraph("图像 " + index1 + "：").setFont(chineseFont).setBold());

                // 处理原超声图像
                String originGraphPath = rootPath + "\\" + imageData.getSegmentedImage().replace("_segmentation/", "/").replace("/", "\\");
                BufferedImage bufferedImage = iImageProcessingService.mergeImages(imageData.getOriginalImage(), originGraphPath);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
                Image img1 = new Image(ImageDataFactory.create(byteArrayOutputStream.toByteArray()));

                // 处理分割图像
                String segmentedImagePath = rootPath + "\\" + imageData.getSegmentedImage().replace("/", "\\");
                Image img2 = new Image(ImageDataFactory.create(segmentedImagePath));

                // 按比例缩放图片
                img1.scaleToFit(maxImageWidth, maxImageHeight);
                img2.scaleToFit(maxImageWidth, maxImageHeight);

                // 创建两列的表格
                Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1})); // 1:1 等宽列
                table.setWidth(maxTableWidth); // 设置表格宽度
                table.setHorizontalAlignment(HorizontalAlignment.CENTER);

                // 添加图片到表格
                table.addCell(new Cell().add(img1).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(img2).setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER));

                document.add(table);
                index1++;
            }

            // 病灶区域框选
            document.add(new Paragraph("病灶框选").setFont(chineseFont).setBold().setFontSize(14).setMarginTop(10));
            int index2 = 1;
            for (List<DiagnosisSubmit.Region> area : diagnosisSubmit.getRegions()) {
                document.add(new Paragraph("图像 " + index2 + " 的病灶区域：").setFont(chineseFont).setBold());

                Table regionTable = new Table(new float[]{1, 1, 1, 1});
                regionTable.addHeaderCell(new Cell().add(new Paragraph("区域").setFont(chineseFont).setBold()));
                regionTable.addHeaderCell(new Cell().add(new Paragraph("X").setFont(chineseFont).setBold()));
                regionTable.addHeaderCell(new Cell().add(new Paragraph("Y").setFont(chineseFont).setBold()));
                regionTable.addHeaderCell(new Cell().add(new Paragraph("宽 × 高").setFont(chineseFont).setBold()));

                int index = 1;
                for (DiagnosisSubmit.Region region : area) {
                    regionTable.addCell(new Cell().add(new Paragraph(String.valueOf(index)).setFont(chineseFont)));
                    regionTable.addCell(new Cell().add(new Paragraph(String.valueOf(region.getX())).setFont(chineseFont)));
                    regionTable.addCell(new Cell().add(new Paragraph(String.valueOf(region.getY())).setFont(chineseFont)));
                    regionTable.addCell(new Cell().add(new Paragraph(region.getWidth() + " × " + region.getHeight()).setFont(chineseFont)));
                    index++;
                }

                document.add(regionTable);
                index2++;
            }

            // 医生诊断意见
            document.add(new Paragraph("医生诊断意见").setFont(chineseFont).setBold().setFontSize(14).setMarginTop(10));
            document.add(new Paragraph("诊断意见: " + diagnosisSubmit.getOpinion()).setFont(chineseFont));
            document.add(new Paragraph("治疗建议: " + diagnosisSubmit.getSuggestion()).setFont(chineseFont));

            // 关闭文档
            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("生成 PDF 失败", e);
        }
    }
}
