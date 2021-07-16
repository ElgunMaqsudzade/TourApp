package az.code.tourapp.configs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "spring.rabbitmq.template", ignoreInvalidFields = true)
public class RabbitMQConfig {
    private String exchange;
    public String offer = "offer";
    public String subscription = "subscription";

    @Bean
    public Queue queueOffer() {
        return new Queue(offer);
    }

    @Bean
    public Queue queueSub() {
        return new Queue(subscription);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding bindingOffer() {
        return BindingBuilder.bind(queueOffer()).to(exchange()).with(offer);
    }

    @Bean
    public Binding bindingSubscription() {
        return BindingBuilder.bind(queueSub()).to(exchange()).with(subscription);
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate temp = new RabbitTemplate(connectionFactory);
        temp.setExchange(exchange);
        temp.setRoutingKey(offer);
        temp.setMessageConverter(converter());
        return temp;
    }
}
