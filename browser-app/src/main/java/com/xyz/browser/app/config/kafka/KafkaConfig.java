package com.xyz.browser.app.config.kafka;

import com.xyz.browser.app.modular.kafka.ContractMsgListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${BOOTSTRAP_SERVERS_CONFIG}")
    private String bootStrapServersConfig;

    @Value("${GROUP_ID_CONFIG}")
    private String groupIdConfig;

    @Value("${ENABLE_AUTO_COMMIT_CONFIG}")
    private boolean enableAutoCommitConfig;

    @Value("${AUTO_COMMIT_INTERVAL_MS_CONFIG}")
    private Integer autoCommitIntervalMsConfig;

    @Value("${SESSION_TIMEOUT_MS_CONFIG}")
    private String sessionTimeoutMsConfig;

    @Value("${AUTO_STARTUP}")
    private boolean autoStartup;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServersConfig);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupIdConfig);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommitConfig);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMsConfig);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMsConfig);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setAutoStartup(autoStartup);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public ContractMsgListener myMessageListener(){
        return new ContractMsgListener();
    }


    @Bean
    public ContainerProperties containerProperties(){
        Pattern topicPattern = Pattern.compile("VNSContractParse"); //匹配满足正则的topic
        ContainerProperties containerProperties = new ContainerProperties(topicPattern);//订阅满足正则表达式的topic
        containerProperties.setMessageListener(myMessageListener());//订阅的topic的消息用myMessageListener去处理
        return containerProperties;
    }

    @Bean("kafkaMessageListenerContainer")
    public KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer(){
        return new KafkaMessageListenerContainer<>(consumerFactory(),containerProperties());
    }


}
