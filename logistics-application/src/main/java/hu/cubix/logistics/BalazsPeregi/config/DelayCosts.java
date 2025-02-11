package hu.cubix.logistics.BalazsPeregi.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@ConfigurationProperties(prefix = "decrease")
@Component
public class DelayCosts {
	private List<String> decreasesByTime = new ArrayList<>();

	private List<Decrease> decreases = new ArrayList<>();

	public List<Decrease> getDecreases() {
		return decreases;
	}

	public static class Decrease {

		private int from;
		private int to;
		private int decreasePercentage;

		public Decrease(int from, int to, int decreasePercentage) {
			this.from = from;
			this.to = to;
			this.decreasePercentage = decreasePercentage;
		}

		public int getFrom() {
			return from;
		}

		public int getTo() {
			return to;
		}

		public int getDecreasePercentage() {
			return decreasePercentage;
		}

		public static Decrease fromString(String property) {
			String[] splittedDecrease = property.split(";");
			return new Decrease(Integer.parseInt(splittedDecrease[0]),
					Integer.parseInt(splittedDecrease[1]) == -1 ? Integer.MAX_VALUE
							: Integer.parseInt(splittedDecrease[1]),
					Integer.parseInt(splittedDecrease[2]));
		}
	}

	public void setDecreasesByTime(List<String> decreasesByTime) {
		this.decreasesByTime = decreasesByTime;
	}

	@PostConstruct
	public void init() {
		for (String property : decreasesByTime) {
			decreases.add(Decrease.fromString(property));
		}
	}
}
