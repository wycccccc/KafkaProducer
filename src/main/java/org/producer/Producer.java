package org.producer;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer implements Runnable {
  static final StringSerializer STRING_SERIALIZER = new StringSerializer();
  final KafkaProducer<String, String> kafkaProducer;
  final DataManager dataManager;

  public Producer(Properties prop, DataManager dataManager) {
    kafkaProducer = new KafkaProducer<>(prop, STRING_SERIALIZER, STRING_SERIALIZER);
    this.dataManager = dataManager;
  }

  @Override
  public void run() {
    while (true) {
      var record = dataManager.generateRecord();
      if (record.isEmpty()) break;
      kafkaProducer.send(record.get());
    }
    kafkaProducer.close();
  }
}
