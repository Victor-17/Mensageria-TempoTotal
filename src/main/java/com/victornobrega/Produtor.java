package com.victornobrega;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import java.sql.Timestamp;
/**
 * Classe responsavel por enviar itens à fila
 */
public class Produtor {

    public static void main(String[] args) throws Exception{
        //Criacao de uma factory de conexao, responsavel por criar as conexoes
        ConnectionFactory connectionFactory = new ConnectionFactory();

        //configurações do Qeue Menager
        connectionFactory.setUsername("mqadmin");
        connectionFactory.setPassword("Admin123XX_");
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);

        String NOME_FILA = "queue_message";
        try(
                //criação de uma conexão
                Connection connection = connectionFactory.newConnection();
                //criando um canal na conexão
                Channel channel = connection.createChannel()) {

                // Fila 1
                channel.queueDeclare(NOME_FILA, false, false, false, null);

                // Fila 2
                //channel.queueDeclare(NOME_FILA, true, false, false, null);

                // Fila 3
                //channel.queueDeclare(NOME_FILA, true, false, false, null);

                for(int i = 1; i <= 1000000; i++){
                    // Obtém o timestamp atual
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    // Obtém os milissegundos desde a época
                    long millisecondsProdutor = timestamp.getTime();
                    String mensagem = "NUMERO_MENSAGEM-TIMESTAMP " + i + "-" + millisecondsProdutor;

                    //publicando uma mensagem na fila 1
                    channel.basicPublish("", NOME_FILA, null, mensagem.getBytes());

                    //publicando uma mensagem na fila 2
                    //channel.basicPublish("", NOME_FILA, null, mensagem.getBytes());

                    //publicando uma mensagem na fila 3
                    //channel.basicPublish("", NOME_FILA, MessageProperties.PERSISTENT_TEXT_PLAIN, mensagem.getBytes());
                    System.out.println ("[x] Enviado '" + mensagem + "'");
                }
        }
    }
}
