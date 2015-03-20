package snowplows;

public class MultipathLengths {
	public MultipathLengths(int[] plowLengths, int plowMetric){
		this.plowMetric=plowMetric;
		this.pathLengths = plowLengths.clone();
	}
	int plowMetric;
	int[] pathLengths;
}
