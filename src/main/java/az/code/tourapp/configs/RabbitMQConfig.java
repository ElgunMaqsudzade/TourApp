package az.code.tourapp.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

@Profile("!dev")
@Configuration
public class RabbitMQConfig {
    private static final String exchange = "exchange";
    public static final String offer = "offer";
    public static final String subscription = "subscription";
    public static final String offerReply = "offer_reply";


    @Bean
    public Queue queueOffer() {
        return new Queue(offer);
    }

    @Bean
    public Queue queueOfferReply() {
        return new Queue(offerReply);
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
    public Binding bindingOfferReply() {
        return BindingBuilder.bind(queueOfferReply()).to(exchange()).with(offerReply);
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
    public ConnectionFactory connectionFactory() throws URISyntaxException {
        final URI rabbitMqUrl = new URI(System.getenv("CLOUDAMQP_URL"));
        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUri(rabbitMqUrl);
        return factory;
    }

    @Bean
    public RabbitTemplate template() throws URISyntaxException {
        RabbitTemplate temp = new RabbitTemplate(connectionFactory());
        temp.setExchange(exchange);
        temp.setRoutingKey(offer);
        temp.setMessageConverter(converter());
        return temp;
    }
}
