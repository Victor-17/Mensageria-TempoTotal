package com.victornobrega;

import com.rabbitmq.client.*;

import java.sql.Timestamp;

// Classe consumidora/produtora de mensagens.
public class Consumidor {
    public static void main(String[] args) throws Exception {
        System.out.println("Consumidor no ar");

        String NOME_FILA = "queue_message";
        String NOVA_FILA = "queue_new";

        //criando a fábrica de conexões e criando uma conexão
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("mqadmin");
        connectionFactory.setPassword("Admin123XX_");
        connectionFactory.setHost("localhost");
        Connection conexao = connectionFactory.newConnection();

        //criando um canal e declarando a fila
        Channel channel = conexao.createChannel();
        channel.basicQos(1);
        // FILA 1
        //channel.queueDeclare(NOME_FILA, false, false, false, null);

        // FILA2
        //channel.queueDeclare(NOME_FILA, true, false, false, null);

        // FILA 3
        channel.queueDeclare(NOME_FILA, true, false, false, null);

        //Definindo a funcao callback, que será executada quando receber um objeto, no caso a mensagem.
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensagem = new String (delivery.getBody(), "UTF-8");

            // Obtém o timestamp atual
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long millisecondsConsumidor = timestamp.getTime();
            String[] millisecondsProdutor = mensagem.split("-");
            long diferencaMilliseconds = millisecondsConsumidor - Long.parseLong(millisecondsProdutor[2]);
            long numeroMensagem = Long.parseLong(millisecondsProdutor[1].substring(10));

            System.out.println(millisecondsConsumidor);
            System.out.println ("[x] O tempo gasto da mensagem " + numeroMensagem + "-" + millisecondsProdutor[2] + " ser consumida foi de: " + diferencaMilliseconds + " milissegundos");
            channel.basicAck(delivery.getEnvelope(). getDeliveryTag(), false);

            //Condicional para pegar apenas a primeira e a última mensagem.
            if(numeroMensagem == 1 || numeroMensagem == 1000000){
                //Declarando a nova fila 1 e publicando a mensagem;
                channel.queueDeclare(NOVA_FILA, false, false, false, null);
                channel.basicPublish("", NOVA_FILA,  null, mensagem.getBytes());

                //Declarando a nova fila 2 e publicando a mensagem;
                //channel.queueDeclare(NOVA_FILA, true, false, false, null);
                //channel.basicPublish("", NOVA_FILA,  null, mensagem.getBytes());

                //Declarando a nova fila 3 e publicando a mensagem;
                //channel.queueDeclare(NOVA_FILA, true, false, false, null);
                //channel.basicPublish("", NOVA_FILA, MessageProperties.PERSISTENT_TEXT_PLAIN, mensagem.getBytes());
            }
        };

        //Consumindo da fila
        boolean autoAck = false; // ENVIA UM ACK CASO O CONSUMIDOR CAIA, VOLTANDO A MSG PARA A FILA.
        channel.basicConsume (NOME_FILA, autoAck, deliverCallback, consumerTag -> {});
    }
}
