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

/**
 * Container for the test results of the tests to determine the number of
 * distributions.
 */
public class FindKContainer {

	private Result2Excel writer;
	private List<FindKResult> findResults;
	private int pointNumber;
	private int differentK;
	private String name;

	public FindKContainer(int pointNumber, String name, int differentK) {
		findResults = new ArrayList<FindKResult>();
		this.pointNumber = pointNumber;
		this.differentK = differentK;
		this.name = name;
		writer = new Result2Excel();
	}

	public void addTest(FindKResult result) {
		findResults.add(result);
	}

	public void exportFindKTests() {
		File file = new File("./../TestResults/" + name + "FindK.xls");
		WorkbookSettings settings = new WorkbookSettings();
		settings.setLocale(Locale.ENGLISH);
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file, settings);
			writer.saveFindKTestResults(workbook, findResults, pointNumber, differentK);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Could not create workbook");
		}
	}
}
