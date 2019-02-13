package com.faceword.nio.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * @Author: zyong
 * @Date: 2018/11/10 15:57
 * @Version 1.0
 */
@Slf4j
@Component
public class RestTemplateConfig {

    /**
     * 通过ClientHttpRequestFactory生成RestTemplate还可以扩展配置RestTemplate
     * @param factory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({ RestTemplate.class })
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        log.info("---------【RestTemplate加载】------------");
        RestTemplate restTemplate = new RestTemplate(factory);

        //处理错误信息
        // restTemplate.setErrorHandler(errorHandler);

        // Converter处理
        List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
        Iterator<HttpMessageConverter<?>> iterator = messageConverters.iterator();
        while (iterator.hasNext()) {
            HttpMessageConverter<?> converter = iterator.next();
            // 默认StringHttpMessageConverter的编码集市ISO-8859-1
            if (converter instanceof StringHttpMessageConverter) {
                // 这里懒得强转类型所以直接remove再add了
                iterator.remove();
                break;
            }
        }
        // 移除完再添加一个StringHttpMessageConverter编码集为UTF-8
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    /**
     * 生成一个ClientHttpRequestFactory并配置相应参数
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean({ ClientHttpRequestFactory.class })
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        log.info("---------【ClientHttpRequestFactory加载】------------");
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // 读取超时时间 单位为ms
        factory.setReadTimeout(15000);

        // 连接超时时间w 单位为ms
        factory.setConnectTimeout(15000);
        return factory;
    }

}

