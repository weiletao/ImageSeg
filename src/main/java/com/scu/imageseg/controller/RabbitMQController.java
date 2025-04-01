package com.scu.imageseg.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.tools.json.JSONUtil;
import com.scu.imageseg.config.RabbitMQConfig;
import com.scu.imageseg.entity.ImageFile;
import com.scu.imageseg.entity.JSONResult;
import com.scu.imageseg.exception.ServiceException;
import com.scu.imageseg.service.IImageFileService;
import com.scu.imageseg.utils.RabbitMQUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wlt
 * @since 2025-03-26
 */
@Slf4j
@RestController
public class RabbitMQController {
    @Autowired
    RabbitMQUtil rabbitMQUtil;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private ConnectionFactory connectionFactory;
    @Autowired
    private IImageFileService iImageFileService;

    private static final String REPLY_QUEUE_PREFIX = "reply_queue_";  // 回调队列前缀

    /**
     * 增加新消息队列（每个消息队列对应着一个智能图像分割模型）
     * @param queueName
     * @param routingKey
     * @return
     */
    @GetMapping("/jwtFilter/addMessageQueue")
    public JSONResult<Object> addMessageQueue(@RequestParam String queueName, @RequestParam String routingKey){
        try {
            log.info("正在尝试增加队列{}", queueName);
            rabbitMQUtil.addQueue(queueName, routingKey);
            return new JSONResult<>("Queue [" + queueName + "] added with routing key [" + routingKey + "]");
        }catch (Exception e){
            log.error("增加新消息队列失败！");
            throw new ServiceException(220, "增加新消息队列失败！");
        }

    }

    /**
     * 删除已有消息队列
     * @param queueName
     * @return
     */
    @PutMapping("/jwtFilter/deleteMessageQueue")
    public JSONResult<Object> deleteMessageQueue(@RequestParam String queueName){
        try {
            log.info("正在尝试删除队列{}", queueName);
            if (rabbitMQUtil.deleteQueue(queueName)) return new JSONResult<>("Queue [" + queueName + "] successfully deleted !");
        }catch (Exception e){
            log.error("删除消息队列失败！");
            throw new ServiceException(221, "删除消息队列失败！");
        }
        throw new ServiceException(221, "删除消息队列失败！");
    }

    /**
     * todo 待扩展接口 实现智能分割
     * todo 后期实现根据用户id调取数据库 得到管理员为其分配的模型 进行超声分割 （即model1不是自己设置的）
     * @param diagnosisId
     * @return
     */
    @GetMapping("/jwtFilter/segmentation")
    public JSONResult<Object> segmentation(@RequestParam("diagnosis_id") Long diagnosisId){
        // todo 现在先自己定义模型名，之后要从用户id中来
        String modelName = "model1";

        log.info(diagnosisId.toString());
        String correlationId = UUID.randomUUID().toString();  // 生成唯一ID
        String replyQueue = REPLY_QUEUE_PREFIX + modelName + "_" + correlationId;  // 每个请求单独创建回调队列

        /** 创建回调队列（临时）*/
        rabbitTemplate.execute(channel -> {
            channel.queueDeclare(replyQueue, false, true, true, null);
            return null;
        });

        /** 构造消息 */
        MessageProperties properties = new MessageProperties();
        properties.setCorrelationId(correlationId);
        properties.setReplyTo(replyQueue);  // 设置回调队列
        Message message = new Message(diagnosisId.toString().getBytes(), properties);

        /** 发送消息 */
        rabbitMQUtil.createAndBindQueue(modelName, modelName);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_TOPICS_INFORM, modelName, message);

        /** 阻塞等待回调结果 */
        // 设置超时时间，防止无限阻塞
        long timeout = 20_000; // 20秒超时
        BlockingQueue<String> responseQueue = new ArrayBlockingQueue<>(1);
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames(replyQueue);

        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message) {
                responseQueue.offer(new String(message.getBody()));
            }
        };
        container.setMessageListener(messageListener);
        container.start();
        try {
            String response = responseQueue.poll(timeout, TimeUnit.MILLISECONDS);  // 等待最多20秒
            container.stop(); // 关闭监听
            if (response != null) {
                log.info(response);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response);  // 解析 JSON 字符串
                if(jsonNode.has("images")){
                    // 在数据库中获取对应的图片路径List
                    QueryWrapper<ImageFile> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("diagnosis_request_id", diagnosisId);
                    List<ImageFile> imageFiles = iImageFileService.list(queryWrapper);
                    List<String> paths = new ArrayList<>();
                    for(ImageFile imageFile:imageFiles){
                        paths.add(imageFile.getFilePath().replace("/", "_segmentation/"));
                    }
//                    List images = objectMapper.convertValue(jsonNode.get("images"), List.class);
                    return new JSONResult<>(paths);
                }else {
                    log.error("智能分割JSON处理异常！");
                    throw new Exception();
                }

            } else {
                log.error("智能分割超时，请稍后重试！");
                throw new Exception();
            }
        }catch (Exception e){
            log.error("智能分割失败！");
            throw new ServiceException(222, "智能分割失败！");
        }


    }
}
