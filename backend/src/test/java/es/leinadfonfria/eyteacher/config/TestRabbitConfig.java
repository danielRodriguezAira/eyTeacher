package es.leinadfonfria.eyteacher.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Provides a mock AMQP {@link ConnectionFactory} for tests that load the full application context,
 * preventing real RabbitMQ connection attempts during test execution.
 */
@TestConfiguration
public class TestRabbitConfig {

    /**
     * Returns a {@link CachingConnectionFactory} backed by a Mockito-mocked AMQP
     * {@link com.rabbitmq.client.ConnectionFactory}, so no broker is required at test time.
     *
     * @return A {@link ConnectionFactory} that never attempts a real network connection.
     * @throws Exception If mock setup fails.
     */
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() throws Exception {
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channel.isOpen()).thenReturn(true);

        Connection connection = Mockito.mock(Connection.class);
        Mockito.when(connection.isOpen()).thenReturn(true);
        Mockito.when(connection.createChannel()).thenReturn(channel);

        com.rabbitmq.client.ConnectionFactory rabbitFactory = Mockito.mock(com.rabbitmq.client.ConnectionFactory.class);
        Mockito.when(rabbitFactory.newConnection(Mockito.any(java.util.concurrent.ExecutorService.class), Mockito.anyString()))
                .thenReturn(connection);

        CachingConnectionFactory factory = new CachingConnectionFactory(rabbitFactory);
        factory.setHost("localhost");
        return factory;
    }
}
