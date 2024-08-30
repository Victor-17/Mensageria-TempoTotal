package com.victornobrega;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.atomic.AtomicLong;

public class Consumidor2 {

    public static void main(String[] args) throws Exception {
        System.out.println("Consumidor no ar");

        String NOVA_FILA = "queue_new";
        AtomicLong timestampPrimeiraMensagem = new AtomicLong();
        AtomicLong timestampUltimaMensagem = new AtomicLong();

        //criando a fábrica de conexões e criando uma conexão
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername("mqadmin");
        connectionFactory.setPassword("Admin123XX_");
        connectionFactory.setHost("localhost");
        Connection conexao = connectionFactory.newConnection();

        //criando um canal e declarando a filaW
        Channel channel = conexao.createChannel();
        channel.basicQos(1);
        // Fila 1
        channel.queueDeclare(NOVA_FILA, false, false, false, null);

        // Fila 2
        //channel.queueDeclare(NOVA_FILA, true, false, false, null);

        // Fila 3
        //channel.queueDeclare(NOVA_FILA, true, false, false, null);

        //Definindo a funcao callback, que será executada quando receber um objeto, no caso a mensagem.
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensagem = new String (delivery.getBody(), "UTF-8");

            String[] mensagemQuebrada = mensagem.split("-");
            long numeroMensagem = Long.parseLong(mensagemQuebrada[1].substring(10));
            if(numeroMensagem == 1) {
                timestampPrimeiraMensagem.set(Long.parseLong(mensagemQuebrada[2]));
            }
            else{
                timestampUltimaMensagem.set(Long.parseLong(mensagemQuebrada[2]));
                System.out.println ("[x] A diferença de tempo entre a mensagem " + "1" + "-" + timestampPrimeiraMensagem + " e a mensagem " + numeroMensagem + "-" + timestampUltimaMensagem + " foi de: "  + (timestampUltimaMensagem.get() - timestampPrimeiraMensagem.get()) + " milissegundos");
            }
            channel.basicAck(delivery.getEnvelope(). getDeliveryTag(), false);
        };

        //Consumindo da fila
        boolean autoAck = false; // ENVIA UM ACK CASO O CONSUMIDOR CAIA, VOLTANDO A MSG PARA A FILA.
        channel.basicConsume (NOVA_FILA, autoAck, deliverCallback, consumerTag -> {});
    }

}
