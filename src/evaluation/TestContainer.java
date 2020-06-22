package evaluation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import filessystem.Result2Excel;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.WritableWorkbook;
import other.Tests;

/**
 * Container for the test results of the standard tests.
 */
public class TestContainer {

	private Result2Excel writer;
	private List<TestResult> results;
	private List<FindKResult> findResults;
	private AvgResult avgResults;
	private int sampleSize;
	private int pointNumber;
	private int differentK;
	private Tests[] testTypes;
	private String name;
	private String testName;
	private boolean withInitial;

	public TestContainer(int sampleSize, int pointNumber, Tests[] testTypes, int differentK, String name,
			String testName, boolean withInitial) {
		results = new ArrayList<TestResult>();
		this.sampleSize = sampleSize;
		this.pointNumber = pointNumber;
		this.testTypes = testTypes;
		this.differentK = differentK;
		this.name = name;
		this.testName = testName;
		this.withInitial = withInitial;
		writer = new Result2Excel();
	}

	public void addTest(TestResult result) {
		results.add(result);
	}

	public void exportTests() {
		calculateAverage();
		File file = new File("./../TestResults/" + name + testName + ".xls");
		WorkbookSettings settings = new WorkbookSettings();
		settings.setLocale(Locale.ENGLISH);
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file, settings);
			if (withInitial) {
				writer.saveTestResults(workbook, results, avgResults, sampleSize, pointNumber, testTypes, differentK);
			} else {
				writer.saveTestResultsWithoutInitial(workbook, results, avgResults, sampleSize, pointNumber, testTypes,
						differentK);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not create workbook");
		}
	}

	private void calculateAverage() {
		int testNumber = testTypes.length;
		avgResults = new AvgResult(results.size(), differentK, testNumber);
		for (TestResult r : results) {
			for (int i = 0; i < r.getDifferentK(); i++) {
				if (!withInitial && r.getRuntimeTotal(i * testNumber) != 0) {
					for (int j = i * testNumber; j < (i + 1) * testNumber; j++) {
						avgResults.addClustering(j, r.getRuntimeTotal(j), r.getRuntimeBasic(j), r.getIterationTotal(j),
								r.getIterationBasic(j), r.getSilhouette(j));
					}
				} else if (r.getInitialRuntimeTotal(i) != 0) {
					avgResults.setInitial(i, r.getInitialRuntimeTotal(i), r.getInitialRuntimeBasic(i),
							r.getInitialIteration(i));
					for (int j = i * testNumber; j < (i + 1) * testNumber; j++) {
						avgResults.addClustering(j, r.getRuntimeTotal(j), r.getRuntimeBasic(j), r.getIterationTotal(j),
								r.getIterationBasic(j), r.getSilhouette(j));
					}
				}
			}
		}
	}
}
