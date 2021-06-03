package com.jonbore.kafka.process;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class Produce {
    private final Producer<String, String> kafkaProdcer;
    public final static String TOPIC = "bo_test";

    private Produce() {
        kafkaProdcer = createKafkaProducer();
    }

    private Producer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "192.168.81.43:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    void produce() {
        for (int i = 0; i < 2000; i++) {
            final String key = "key" + i;
            String data = "{\"home_page_url\":" + i + ",\"code\":\"MES\",\"level\":12,\"data_hub_oper_time\":1620353520297,\"description\":\"622826202106272383\",\"is_manage\":0,\"sys_type_id\":\"HB2FD8B4B2B2F4D978E27BD2CA9AADBAF\",\"update_date\":1604457988000,\"item_order\":1,\"logout_url\":\"http://127.0.0.1:1762/#/login\",\"img_name\":\"mesSystem.svg\",\"name\":\"MES系统\",\"id\":\"H580B0D23F0784CAB97E" + i + "57903AA50170A\",\"create_date\":1551834445000,\"data_hub_id\":\"3288DBCC66BE4E3B99322B4256E5A6F2\",\"is_publish\":\"1.0\"}";
            kafkaProdcer.send(new ProducerRecord<>(TOPIC, key, data));
        }
        kafkaProdcer.flush();
    }

    public static void main(String[] args) {
        Produce produce = new Produce();
        produce.produce();
    }
}
