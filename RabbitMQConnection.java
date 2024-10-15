package org.kiran;
import com.rabbitmq.client.*;

public class RabbitMQConnection {

    private static final String AMQP_URL = "amqps://student:XYR4yqc.cxh4zug6vje@rabbitmq-exam.rmq3.cloudamqp.com/mxifnklj";
    private static final String EXCHANGE_NAME = "exchange.1e5f4b8e-e9e6-4df1-a9fb-dc10b5b8efde";
    private static final String QUEUE_NAME = "exam";
    private static final String ROUTING_KEY = "1e5f4b8e-e9e6-4df1-a9fb-dc10b5b8efde";
    private static final String MESSAGE = "Hi CloudAMQP, this was fun!";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        try {
            // Set up the connection to RabbitMQ server
            factory.setUri(AMQP_URL);
            factory.setAutomaticRecoveryEnabled(true); // Enable automatic recovery
            factory.setNetworkRecoveryInterval(5000); // Retry interval

            // Create a connection
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {

                // Declare a durable exchange
                channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);

                // Declare a persistent queue
                channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                // Bind the queue to the exchange using the routing key
                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

                // Publish a persistent message to the exchange
                AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                        .deliveryMode(2) // Make message persistent
                        .build();

                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, properties, MESSAGE.getBytes("UTF-8"));
                System.out.println("Message sent: " + MESSAGE);

                // Delete the exchange and the queue after the message is sent
                channel.queueUnbind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
                channel.queueDelete(QUEUE_NAME);
                channel.exchangeDelete(EXCHANGE_NAME);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
