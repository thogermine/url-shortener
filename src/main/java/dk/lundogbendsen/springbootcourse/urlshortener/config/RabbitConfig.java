package dk.lundogbendsen.springbootcourse.urlshortener.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "urlshortener.t-x";
    public static final String QUEUE_USERS = "urlshortener.user.q";
    public static final String QUEUE_TOKEN = "urlshortener.token.q";

    @Bean
    public TopicExchange topicExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).build();
    }

    @Bean
    public Queue queueUsers() {
        return QueueBuilder.nonDurable(QUEUE_USERS).build();
    }

    @Bean
    public Binding bindingUsers() {
        return BindingBuilder.bind(queueUsers()).to(topicExchange()).with("urlshortener.user.rk");
    }

    @Bean
    public Queue queueToken() {
        return QueueBuilder.nonDurable(QUEUE_USERS).build();
    }

    @Bean
    public Binding bindingToken() {
        return BindingBuilder.bind(queueToken()).to(topicExchange()).with("urlshortener.token.rk");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
