package org.producer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;

public class DataManager {
  private final String topic;
  private final long records;
  private final int recordSize;
  private final AtomicLong produced = new AtomicLong(0);

  public DataManager(String topic, long records, int recordSize) {
    this.topic = topic;
    this.records = records;
    this.recordSize = Math.max(recordSize, 1);
  }

  public Optional<ProducerRecord<String, String>> generateRecord() {
    long num = produced.getAndIncrement();
    if (num >= records) return Optional.empty();
    return Optional.of(
        new ProducerRecord<>(
            topic,
            null,
            String.format("key-%010d", num),
            String.format("value-%010d", num),
            List.of(
                new Header() {
                  @Override
                  public String key() {
                    return "";
                  }

                  @Override
                  public byte[] value() {
                    return new byte[recordSize];
                  }
                })));
  }
}
