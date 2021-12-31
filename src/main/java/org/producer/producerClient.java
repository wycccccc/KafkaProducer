package org.producer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.UnixStyleUsageFormatter;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.apache.kafka.clients.producer.ProducerConfig;

public class producerClient {
  public static void main(String[] args) {
    var param = parseArgument(new Argument(), args);

    // Setting producer configs
    final Properties prop = new Properties();
    prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, param.brokers);
    prop.put(ProducerConfig.ACKS_CONFIG, "0");
    prop.put(ProducerConfig.LINGER_MS_CONFIG, "100");

    // Initialize
    var dataManager = new DataManager(param.topic, param.records, param.recordSize);
    var service = Executors.newFixedThreadPool(param.producerThreads);

    // Run producers
    IntStream.range(0, param.producerThreads)
        .forEach(i -> service.execute(new Producer(prop, dataManager)));
    service.shutdown();
  }

  private static <T> T parseArgument(T newArgument, String[] args) {
    JCommander jc = JCommander.newBuilder().addObject(newArgument).build();
    jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));
    try {
      jc.parse(args);
    } catch (ParameterException pe) {
      var sb = new StringBuilder();
      jc.getUsageFormatter().usage(sb);
      throw new ParameterException(pe.getMessage() + "\n" + sb);
    }
    return newArgument;
  }

  private static class Argument {
    @Parameter(
        names = {"--brokers"},
        description = "String: kafka brokers to connect to",
        required = true)
    String brokers;

    @Parameter(
        names = {"--topic"},
        description = "String: kafka topic name")
    String topic = "testing";

    @Parameter(
        names = {"--records"},
        description = "Long: number of records to produce")
    long records = 10000L;

    @Parameter(
        names = {"--recordSize"},
        description = "int: size in byte per record")
    int recordSize = 1024;

    @Parameter(
        names = "--producer.threads",
        description = "int: number of threads to produce records",
        hidden = true)
    int producerThreads = 10;
  }
}
