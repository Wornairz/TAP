package dev.wornairz.tap.kafka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;

import dev.wornairz.tap.ethereum.EthereumBlocksQueue;

public class EthereumSourceTask extends SourceTask {

	private final String OFFSET_KEY = "Ethereum WSS";
	private String kafkaTopic;
	private EthereumBlocksQueue queue;
	private Long count;

	@Override
	public String version() {
		return "1";
	}

	@Override
	public void start(Map<String, String> props) {
		kafkaTopic = props.get("topic");
		queue = EthereumBlocksQueue.getInstance();
		count = 0L;
	}

	@Override
	public List<SourceRecord> poll() throws InterruptedException {
		//The configuration map is passed by the SourceConnector
		List<SourceRecord> records = new ArrayList<>();
		while (!queue.isEmpty()) {
			String block = queue.remove();
			SourceRecord record = new SourceRecord(offsetKey(OFFSET_KEY), offsetValue(count++), kafkaTopic,
					Schema.STRING_SCHEMA, block);
			records.add(record);
		}
		return records;
	}

	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
	}

	private Map<String, String> offsetKey(String wss) {
		return Collections.singletonMap("wss", wss);
	}

	private Map<String, Long> offsetValue(Long pos) {
		return Collections.singletonMap("position", pos);
	}
}
