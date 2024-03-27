package com.betaTest.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.betaTest.utils.ApplicationContextHolder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  错误日志收集处理( 注释的地方是redis锁, 防止同一时间内, 同一种错误被频繁处理)
 * </p>
 *
 * @author LMX
 * @date 2024/03/27/10:52
 */
@Data
public class SendErrorMsgAppender extends UnsynchronizedAppenderBase<LoggingEvent> {

    private String pattern;

    private PatternLayout layout;

    private Level nowLevel = Level.ERROR;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void append(LoggingEvent eventObject) {
        if (eventObject != null && eventObject.getLevel().isGreaterOrEqual(nowLevel)) {
            // 请求地址,消息串
            String url = "", lockKey = "";
            StringBuilder sendMessage = new StringBuilder();
//            DistributedLock distributedLock = null;
            try {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();
                    url = request.getRequestURI();
                    lockKey = url;
                    logger.info("url:{}", url);
                } else {
                    //没有地址的错误日志不发送直接存储到日志文件即可,避免企业微信页面展示过多的错误信息
                    lockKey = eventObject.getFormattedMessage();
                    if (StringUtils.isBlank(lockKey)) {
                        lockKey = eventObject.getLoggerName();
                        logger.info("lockKey:{}", lockKey);
                    }
                }
//                distributedLock = ApplicationContextHolder.getBean(DistributedLock.class);
                Environment bean = ApplicationContextHolder.getBean(Environment.class);
                Integer leaseTime = Integer.valueOf(bean.getProperty("qyWeChat.leaseTime", "180"));
                //锁
//                boolean isLock = distributedLock.tryLock(lockKey, TimeUnit.SECONDS, 1, leaseTime);
//                if (!isLock) {
//                    return;
//                }
                String active = "";
                String serverName = "服务名称";
                active = bean.getProperty("spring.profiles.active");
                sendMessage.append("项目名称: " + serverName + "\n");
                sendMessage.append("环境: " + active + "\n");
                if (StringUtils.isNotBlank(url)) {
                    sendMessage.append("请求地址url: " + url + "\n");
                }
                String errorMessage = layout.doLayout(eventObject);
                sendMessage.append(errorMessage);
                //获取地址发送消息
                String webHook = bean.getProperty("qyWeChat.webHook");
                if (StringUtils.isNotBlank(webHook)) {
//                    toWechat(webHook, sendMessage.toString());
                }

            } catch (Exception e) {
                logger.warn("异常消息通知异常", e);
//                if (distributedLock != null && StringUtils.isNotEmpty(lockKey)) {
//                    distributedLock.unlock(lockKey);
//                }
            }
        }
    }
}
