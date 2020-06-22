package filessystem;

import java.util.List;

import evaluation.AvgResult;
import evaluation.FindKResult;
import evaluation.TestResult;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import other.Tests;
import tools.Number2ExcelColumn;

/**
 * This class contains operations to store the test results in excel files. Uses
 * the library jexcel.
 */
public class Result2Excel {

	private WritableSheet[] worksheeds;

	@SuppressWarnings("deprecation") // works fine
	public void saveTestResults(WritableWorkbook workbook, List<TestResult> results, AvgResult avgResult,
			int sampleSize, int pointNumber, Tests[] testTypes, int differentK) {
		try {
			// Create sheets
			workbook.createSheet("TotalRuntimes", 0);
			workbook.createSheet("TotalIterations", 1);
			workbook.createSheet("Silhouettes", 2);
			workbook.createSheet("BasicRuntimes", 3);
			workbook.createSheet("BasicIterations", 4);
			worksheeds = new WritableSheet[5];
			for (int i = 0; i < 5; i++) {
				worksheeds[i] = workbook.getSheet(i);
			}

			// Create fonts
			WritableFont font = new WritableFont(WritableFont.ARIAL, 12);
			WritableCellFormat description = new WritableCellFormat(font);
			description.setWrap(true);
			description.setAlignment(Alignment.CENTRE);
			description.setVerticalAlignment(VerticalAlignment.CENTRE);
			description.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			WritableFont font2 = new WritableFont(WritableFont.ARIAL, 12);
			WritableCellFormat data = new WritableCellFormat(font2);
			data.setAlignment(Alignment.LEFT);
			data.setWrap(true);
			data.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat dataMid = new WritableCellFormat(font2);
			dataMid.setAlignment(Alignment.CENTRE);
			dataMid.setWrap(true);
			dataMid.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableFont font3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
			WritableCellFormat title = new WritableCellFormat(font3);
			title.setAlignment(Alignment.LEFT);
			WritableCellFormat gray = new WritableCellFormat(font2);
			gray.setBackground(Colour.GRAY_25);
			// for indicating clusterings with empty clusters
			int colourFlag = 0;
			WritableCellFormat[] colours = new WritableCellFormat[5];
			colours[0] = new WritableCellFormat(font2);
			colours[0].setAlignment(Alignment.LEFT);
			colours[0].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[0].setWrap(true);
			colours[1] = new WritableCellFormat(font2);
			colours[1].setBackground(Colour.PALE_BLUE);
			colours[1].setAlignment(Alignment.LEFT);
			colours[1].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[1].setWrap(true);
			colours[2] = new WritableCellFormat(font2);
			colours[2].setBackground(Colour.LIGHT_BLUE);
			colours[2].setAlignment(Alignment.LEFT);
			colours[2].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[2].setWrap(true);
			colours[3] = new WritableCellFormat(font2);
			colours[3].setBackground(Colour.BLUE);
			colours[3].setAlignment(Alignment.LEFT);
			colours[3].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[3].setWrap(true);
			colours[4] = new WritableCellFormat(font2);
			colours[4].setBackground(Colour.DARK_BLUE);
			colours[4].setAlignment(Alignment.LEFT);
			colours[4].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[4].setWrap(true);
			// for indicating tests that took more than 1000 iterations
			int colourFlag2 = 0;
			int colourFlag3 = 0;
			WritableCellFormat[] colours2 = new WritableCellFormat[6];
			colours2[0] = new WritableCellFormat(font2);
			colours2[0].setAlignment(Alignment.LEFT);
			colours2[0].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[0].setWrap(true);
			colours2[1] = new WritableCellFormat(font2);
			colours2[1].setBackground(Colour.LIGHT_GREEN);
			colours2[1].setAlignment(Alignment.LEFT);
			colours2[1].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[1].setWrap(true);
			colours2[2] = new WritableCellFormat(font2);
			colours2[2].setBackground(Colour.BRIGHT_GREEN);
			colours2[2].setAlignment(Alignment.LEFT);
			colours2[2].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[2].setWrap(true);
			colours2[3] = new WritableCellFormat(font2);
			colours2[3].setBackground(Colour.SEA_GREEN);
			colours2[3].setAlignment(Alignment.LEFT);
			colours2[3].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[3].setWrap(true);
			colours2[4] = new WritableCellFormat(font2);
			colours2[4].setBackground(Colour.GREEN);
			colours2[4].setAlignment(Alignment.LEFT);
			colours2[4].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[4].setWrap(true);
			colours2[5] = new WritableCellFormat(font2);
			colours2[5].setBackground(Colour.DARK_GREEN);
			colours2[5].setAlignment(Alignment.LEFT);
			colours2[5].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[5].setWrap(true);

			// set cell format and size
			for (int i = 0; i < differentK * (testTypes.length + 3) + 1; i++) {
				worksheeds[0].setColumnView(i, 12, data);
				worksheeds[1].setColumnView(i, 12, data);
				worksheeds[2].setColumnView(i, 12, data);
				worksheeds[3].setColumnView(i, 12, data);
				worksheeds[4].setColumnView(i, 12, data);
			}

			// merge sheet description cells
			mergeAll(0, 0, differentK * (testTypes.length + 3), 0);
			mergeAll(0, 2, 0, 3);
			// merge k-description cells
			for (int i = 0; i < differentK; i++) {
				mergeAll(i * (testTypes.length + 3) + 2, 2, (i + 1) * (testTypes.length + 3), 2);
			}

			// name titles
			writeLabel(0, 0, 0,
					"Total runtimes - units in milliseconds; " + results.size() + " different data sets with "
							+ pointNumber + " points; each value is the average of " + sampleSize + " tests",
					title);
			writeLabel(1, 0, 0,
					"Number of total iterations of each test - " + results.size() + " different data sets with "
							+ pointNumber + " points; each value is the average of " + sampleSize + " tests",
					title);
			writeLabel(2, 0, 0,
					"Silhouette-values of the resulting clusterings - " + results.size() + " different data sets with "
							+ pointNumber + " points; each value is the average of " + sampleSize + " tests",
					title);
			writeLabel(3, 0, 0,
					"Runtimes of basic algorithm - units in milliseconds; " + results.size()
							+ " different data sets with " + pointNumber + " points; each value is the average of "
							+ sampleSize + " tests",
					title);
			writeLabel(4, 0, 0,
					"Number of iterations of the basic algorithm of each test - " + results.size()
							+ " different data sets with " + pointNumber + " points; each value is the average of "
							+ sampleSize + " tests",
					title);
			writeAllLabel(0, 2, "Data Set #", description);

			// name description cells
			for (int i = 0; i < differentK; i++) {
				writeAllLabel(i * (testTypes.length + 3) + 2, 2, "k : " + testTypes[0].getDescription() + (i + 1),
						description);
				writeAllLabel(i * (testTypes.length + 3) + 2, 3, "1)\nInitial", description);
				for (int j = 0; j < testTypes.length; j++) {
					writeAllLabel(i * (testTypes.length + 3) + 3 + j, 3, testTypes[j].getName(), description);
				}
				writeAllLabel((i + 1) * (testTypes.length + 3), 3, "AVG", description);
			}
			writeAllLabel(0, 6 + results.size(), "AVG", description);
			worksheeds[0].addCell(new Label(0, 7 + results.size(), "DIF", description));
			worksheeds[1].addCell(new Label(0, 7 + results.size(), "DIF", description));
			worksheeds[0].addCell(new Label(0, 8 + results.size(), "QUO", description));
			worksheeds[1].addCell(new Label(0, 8 + results.size(), "QUO", description));

			// gray rows
			for (int i = 0; i < differentK * (testTypes.length + 3) + 1; i++) {
				writeAllLabel(i, 4, "", gray);
			}
			for (int i = 0; i < differentK * (testTypes.length + 3) + 1; i++) {
				writeAllLabel(i, results.size() + 5, "", gray);
			}
			// gray columns
			for (int i = 0; i < differentK; i++) {
				for (int j = 2; j < 7 + results.size(); j++) {
					writeAllLabel(i * (testTypes.length + 3) + 1, j, "", gray);
				}
			}
			for (int i = 0; i < differentK; i++) {
				worksheeds[0].addCell(new Label(i * (testTypes.length + 3) + 1, 7 + results.size(), "", gray));
				worksheeds[1].addCell(new Label(i * (testTypes.length + 3) + 1, 7 + results.size(), "", gray));
				worksheeds[0].addCell(new Label(i * (testTypes.length + 3) + 1, 8 + results.size(), "", gray));
				worksheeds[1].addCell(new Label(i * (testTypes.length + 3) + 1, 8 + results.size(), "", gray));
			}

			// fill with data
			for (int i = 0; i < results.size(); i++) {
				TestResult result = results.get(i);
				writeAllNumber(0, 5 + i, i + 1, dataMid);
				for (int k = 0; k < result.getDifferentK(); k++) {
					writeNumber(0, k * (testTypes.length + 3) + 2, i + 5, result.getInitialRuntimeTotal(k));
					writeNumber(1, k * (testTypes.length + 3) + 2, i + 5, result.getInitialIteration(k));
					writeLabel(2, k * (testTypes.length + 3) + 2, i + 5, "XXX");
					writeNumber(3, k * (testTypes.length + 3) + 2, i + 5, result.getInitialRuntimeBasic(k));
					writeNumber(4, k * (testTypes.length + 3) + 2, i + 5, result.getInitialIteration(k));
					for (int j = 0; j < testTypes.length; j++) {
						writeNumber(0, k * (testTypes.length + 3) + j + 3, i + 5,
								result.getRuntimeTotal(k * testTypes.length + j));
						// tests with more than 1000 iterations get a green
						// background; the green tone is based on how many times
						// 1000 iterations were exceeded
						int colourResult2 = Math.min(5, result.getMaxIterationsTotal(k * testTypes.length + j));
						colourFlag2 = Math.max(colourResult2, colourFlag2);
						writeNumber(1, k * (testTypes.length + 3) + j + 3, i + 5,
								result.getIterationTotal(k * testTypes.length + j), colours2[colourResult2]);
						// silhouettes of clusterings with empty clusters get a
						// blue background; the blue tone is based on the number
						// of empty clusters
						int colourResult = result.getEmptyClusters(k * testTypes.length + j);
						colourFlag = Math.max(colourFlag, colourResult);
						writeNumber(2, k * (testTypes.length + 3) + j + 3, i + 5,
								result.getSilhouette(k * testTypes.length + j), colours[colourResult]);
						writeNumber(3, k * (testTypes.length + 3) + j + 3, i + 5,
								result.getRuntimeBasic(k * testTypes.length + j));
						int colourResult3 = Math.min(5, result.getMaxIterationsBasic(k * testTypes.length + j));
						colourFlag3 = Math.max(colourResult3, colourFlag3);
						writeNumber(4, k * (testTypes.length + 3) + j + 3, i + 5,
								result.getIterationBasic(k * testTypes.length + j), colours2[colourResult3]);
					}
					writeNumber(0, (k + 1) * (testTypes.length + 3), i + 5, result.getAvgRuntimeTotal(k), title);
					writeNumber(1, (k + 1) * (testTypes.length + 3), i + 5, result.getAvgIterationTotal(k), title);
					writeNumber(2, (k + 1) * (testTypes.length + 3), i + 5, result.getAvgSilhouette(k), title);
					writeNumber(3, (k + 1) * (testTypes.length + 3), i + 5, result.getAvgRuntimeBasic(k), title);
					writeNumber(4, (k + 1) * (testTypes.length + 3), i + 5, result.getAvgIterationBasic(k), title);
				}
				for (int k = result.getDifferentK(); k < differentK; k++) {
					for (int j = 0; j < testTypes.length + 2; j++) {
						writeAllLabel(k * (testTypes.length + 3) + j + 2, i + 5, "-");
					}
				}
			}

			// write average values
			for (int k = 0; k < differentK; k++) {
				writeNumber(0, k * (testTypes.length + 3) + 2, 6 + results.size(), avgResult.getInitialRuntimeTotal(k),
						title);
				writeNumber(1, k * (testTypes.length + 3) + 2, 6 + results.size(), avgResult.getInitialIteration(k),
						title);
				writeLabel(2, k * (testTypes.length + 3) + 2, 6 + results.size(), "XXX", title);
				writeNumber(3, k * (testTypes.length + 3) + 2, 6 + results.size(), avgResult.getInitialRuntimeBasic(k),
						title);
				writeNumber(4, k * (testTypes.length + 3) + 2, 6 + results.size(), avgResult.getInitialIteration(k),
						title);
				for (int j = 0; j < testTypes.length; j++) {
					writeNumber(0, k * (testTypes.length + 3) + j + 3, 6 + results.size(),
							avgResult.getRuntimeTotal(k * testTypes.length + j), title);
					writeNumber(1, k * (testTypes.length + 3) + j + 3, 6 + results.size(),
							avgResult.getIterationTotal(k * testTypes.length + j), title);
					writeNumber(2, k * (testTypes.length + 3) + j + 3, 6 + results.size(),
							avgResult.getSilhouette(k * testTypes.length + j), title);
					writeNumber(3, k * (testTypes.length + 3) + j + 3, 6 + results.size(),
							avgResult.getRuntimeBasic(k * testTypes.length + j), title);
					writeNumber(4, k * (testTypes.length + 3) + j + 3, 6 + results.size(),
							avgResult.getIterationBasic(k * testTypes.length + j), title);
				}
				writeAllLabel((k + 1) * (testTypes.length + 3), 6 + results.size(), "XXX", title);
			}

			// write formulas
			for (int k = 0; k < differentK; k++) {
				for (int j = 0; j < testTypes.length + 1; j++) {
					worksheeds[0].addCell(new Formula(k * (testTypes.length + 3) + j + 2, 7 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2) + (7 + results.size())
									+ "-BasicRuntimes!"
									+ Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2)
									+ (7 + results.size()),
							title));
					worksheeds[1].addCell(new Formula(k * (testTypes.length + 3) + j + 2, 7 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2) + (7 + results.size())
									+ "-BasicIterations!"
									+ Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2)
									+ (7 + results.size()),
							title));
					worksheeds[0].addCell(new Formula(k * (testTypes.length + 3) + j + 2, 8 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2) + (8 + results.size())
									+ "*100/" + Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2)
									+ (7 + results.size()),
							title));
					worksheeds[1].addCell(new Formula(k * (testTypes.length + 3) + j + 2, 8 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2) + (8 + results.size())
									+ "*100/" + Number2ExcelColumn.getColumn(k * (testTypes.length + 3) + j + 2)
									+ (7 + results.size()),
							title));
				}
				writeLabel(0, (k + 1) * (testTypes.length + 3), 7 + results.size(), "XXX", title);
				writeLabel(1, (k + 1) * (testTypes.length + 3), 7 + results.size(), "XXX", title);
				writeLabel(0, (k + 1) * (testTypes.length + 3), 8 + results.size(), "XXX", title);
				writeLabel(1, (k + 1) * (testTypes.length + 3), 8 + results.size(), "XXX", title);
			}

			// colour description
			if (colourFlag > 0) {
				for (int i = 0; i < (int) ((differentK * (testTypes.length + 3) + 3) / (colourFlag + 4)); i++) {
					writeLabel(2, i * (colourFlag + 4), results.size() + 8, "Number of empty clusters:", description);
					writeLabel(2, i * (colourFlag + 4) + 1, results.size() + 8, "No empty clusters in all tests",
							colours[0]);
					for (int j = 1; j < colourFlag + 1; j++) {
						writeLabel(2, i * (colourFlag + 4) + 1 + j, results.size() + 8,
								j + " empty Clusters in at least one test", colours[j]);
					}
				}
			}
			if (colourFlag2 > 0) {
				for (int i = 0; i < (int) ((differentK * (testTypes.length + 3) + 3) / (colourFlag2 + 4)); i++) {
					writeLabel(1, i * (colourFlag2 + 4), results.size() + 10, "More than 1000 iterations:",
							description);
					writeLabel(1, i * (colourFlag2 + 4) + 1, results.size() + 10, "0x", colours2[0]);
					for (int j = 1; j < Math.min(colourFlag2 + 1, 5); j++) {
						writeLabel(1, i * (colourFlag2 + 4) + 1 + j, results.size() + 10, j + "x", colours2[j]);
					}
					if (colourFlag2 == 5) {
						writeLabel(1, i * (colourFlag2 + 4) + 6, results.size() + 10, ">5x", colours2[5]);
					}
				}
			}
			if (colourFlag3 > 0) {
				for (int i = 0; i < (int) ((differentK * (testTypes.length + 3) + 3) / (colourFlag3 + 4)); i++) {
					writeLabel(4, i * (colourFlag3 + 4), results.size() + 8, "More than 1000 iterations:", description);
					writeLabel(4, i * (colourFlag3 + 4) + 1, results.size() + 8, "0x", colours2[0]);
					for (int j = 1; j < Math.min(colourFlag3 + 1, 5); j++) {
						writeLabel(4, i * (colourFlag3 + 4) + 1 + j, results.size() + 8, j + "x", colours2[j]);
					}
					if (colourFlag3 == 5) {
						writeLabel(4, i * (colourFlag3 + 4) + 6, results.size() + 8, ">5x", colours2[5]);
					}
				}
			}

			workbook.write();
			workbook.close();
		} catch (Exception e) {
			System.err.println("Failed writing excel file");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation") // works fine
	public void saveTestResultsWithoutInitial(WritableWorkbook workbook, List<TestResult> results, AvgResult avgResult,
			int sampleSize, int pointNumber, Tests[] testTypes, int differentK) {
		try {
			// Create sheets
			workbook.createSheet("TotalRuntimes", 0);
			workbook.createSheet("TotalIterations", 1);
			workbook.createSheet("Silhouettes", 2);
			workbook.createSheet("BasicRuntimes", 3);
			workbook.createSheet("BasicIterations", 4);
			worksheeds = new WritableSheet[5];
			for (int i = 0; i < 5; i++) {
				worksheeds[i] = workbook.getSheet(i);
			}

			// Create fonts
			WritableFont font = new WritableFont(WritableFont.ARIAL, 12);
			WritableCellFormat description = new WritableCellFormat(font);
			description.setWrap(true);
			description.setAlignment(Alignment.CENTRE);
			description.setVerticalAlignment(VerticalAlignment.CENTRE);
			description.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			WritableFont font2 = new WritableFont(WritableFont.ARIAL, 12);
			WritableCellFormat data = new WritableCellFormat(font2);
			data.setAlignment(Alignment.LEFT);
			data.setWrap(true);
			data.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat dataMid = new WritableCellFormat(font2);
			dataMid.setAlignment(Alignment.CENTRE);
			dataMid.setWrap(true);
			dataMid.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableFont font3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
			WritableCellFormat title = new WritableCellFormat(font3);
			title.setAlignment(Alignment.LEFT);
			WritableCellFormat gray = new WritableCellFormat(font2);
			gray.setBackground(Colour.GRAY_25);
			// for indicating clusterings with empty clusters
			int colourFlag = 0;
			WritableCellFormat[] colours = new WritableCellFormat[5];
			colours[0] = new WritableCellFormat(font2);
			colours[0].setAlignment(Alignment.LEFT);
			colours[0].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[0].setWrap(true);
			colours[1] = new WritableCellFormat(font2);
			colours[1].setBackground(Colour.PALE_BLUE);
			colours[1].setAlignment(Alignment.LEFT);
			colours[1].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[1].setWrap(true);
			colours[2] = new WritableCellFormat(font2);
			colours[2].setBackground(Colour.LIGHT_BLUE);
			colours[2].setAlignment(Alignment.LEFT);
			colours[2].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[2].setWrap(true);
			colours[3] = new WritableCellFormat(font2);
			colours[3].setBackground(Colour.BLUE);
			colours[3].setAlignment(Alignment.LEFT);
			colours[3].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[3].setWrap(true);
			colours[4] = new WritableCellFormat(font2);
			colours[4].setBackground(Colour.DARK_BLUE);
			colours[4].setAlignment(Alignment.LEFT);
			colours[4].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours[4].setWrap(true);
			// for indicating tests that took more than 1000 iterations
			int colourFlag2 = 0;
			int colourFlag3 = 0;
			WritableCellFormat[] colours2 = new WritableCellFormat[6];
			colours2[0] = new WritableCellFormat(font2);
			colours2[0].setAlignment(Alignment.LEFT);
			colours2[0].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[0].setWrap(true);
			colours2[1] = new WritableCellFormat(font2);
			colours2[1].setBackground(Colour.LIGHT_GREEN);
			colours2[1].setAlignment(Alignment.LEFT);
			colours2[1].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[1].setWrap(true);
			colours2[2] = new WritableCellFormat(font2);
			colours2[2].setBackground(Colour.BRIGHT_GREEN);
			colours2[2].setAlignment(Alignment.LEFT);
			colours2[2].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[2].setWrap(true);
			colours2[3] = new WritableCellFormat(font2);
			colours2[3].setBackground(Colour.SEA_GREEN);
			colours2[3].setAlignment(Alignment.LEFT);
			colours2[3].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[3].setWrap(true);
			colours2[4] = new WritableCellFormat(font2);
			colours2[4].setBackground(Colour.GREEN);
			colours2[4].setAlignment(Alignment.LEFT);
			colours2[4].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[4].setWrap(true);
			colours2[5] = new WritableCellFormat(font2);
			colours2[5].setBackground(Colour.DARK_GREEN);
			colours2[5].setAlignment(Alignment.LEFT);
			colours2[5].setVerticalAlignment(VerticalAlignment.CENTRE);
			colours2[5].setWrap(true);

			// set cell format and size
			for (int i = 0; i < differentK * (testTypes.length + 2) + 1; i++) {
				worksheeds[0].setColumnView(i, 12, data);
				worksheeds[1].setColumnView(i, 12, data);
				worksheeds[2].setColumnView(i, 12, data);
				worksheeds[3].setColumnView(i, 12, data);
				worksheeds[4].setColumnView(i, 12, data);
			}

			// merge sheet description cells
			mergeAll(0, 0, differentK * (testTypes.length + 2), 0);
			mergeAll(0, 2, 0, 3);
			// merge k-description cells
			for (int i = 0; i < differentK; i++) {
				mergeAll(i * (testTypes.length + 2) + 2, 2, (i + 1) * (testTypes.length + 2), 2);
			}

			// name titles
			writeLabel(0, 0, 0,
					"Total runtimes - units in milliseconds; " + results.size() + " different data sets with "
							+ pointNumber + " points; each value is the average of " + sampleSize + " tests",
					title);
			writeLabel(1, 0, 0,
					"Number of total iterations of each test - " + results.size() + " different data sets with "
							+ pointNumber + " points; each value is the average of " + sampleSize + " tests",
					title);
			writeLabel(2, 0, 0,
					"Silhouette-values of the resulting clusterings - " + results.size() + " different data sets with "
							+ pointNumber + " points; each value is the average of " + sampleSize + " tests",
					title);
			writeLabel(3, 0, 0,
					"Runtimes of basic algorithm - units in milliseconds; " + results.size()
							+ " different data sets with " + pointNumber + " points; each value is the average of "
							+ sampleSize + " tests",
					title);
			writeLabel(4, 0, 0,
					"Number of iterations of the basic algorithm of each test - " + results.size()
							+ " different data sets with " + pointNumber + " points; each value is the average of "
							+ sampleSize + " tests",
					title);
			writeAllLabel(0, 2, "Data Set #", description);

			// name description cells
			for (int i = 0; i < differentK; i++) {
				writeAllLabel(i * (testTypes.length + 2) + 2, 2, "k : " + testTypes[0].getDescription() + (i + 1),
						description);
				for (int j = 0; j < testTypes.length; j++) {
					writeAllLabel(i * (testTypes.length + 2) + 2 + j, 3, testTypes[j].getName(), description);
				}
				writeAllLabel((i + 1) * (testTypes.length + 2), 3, "AVG", description);
			}
			writeAllLabel(0, 6 + results.size(), "AVG", description);
			worksheeds[0].addCell(new Label(0, 7 + results.size(), "DIF", description));
			worksheeds[1].addCell(new Label(0, 7 + results.size(), "DIF", description));
			worksheeds[0].addCell(new Label(0, 8 + results.size(), "QUO", description));
			worksheeds[1].addCell(new Label(0, 8 + results.size(), "QUO", description));

			// gray rows
			for (int i = 0; i < differentK * (testTypes.length + 2) + 1; i++) {
				writeAllLabel(i, 4, "", gray);
			}
			for (int i = 0; i < differentK * (testTypes.length + 2) + 1; i++) {
				writeAllLabel(i, results.size() + 5, "", gray);
			}
			// gray columns
			for (int i = 0; i < differentK; i++) {
				for (int j = 2; j < 7 + results.size(); j++) {
					writeAllLabel(i * (testTypes.length + 2) + 1, j, "", gray);
				}
			}
			for (int i = 0; i < differentK; i++) {
				worksheeds[0].addCell(new Label(i * (testTypes.length + 2) + 1, 7 + results.size(), "", gray));
				worksheeds[1].addCell(new Label(i * (testTypes.length + 2) + 1, 7 + results.size(), "", gray));
				worksheeds[0].addCell(new Label(i * (testTypes.length + 2) + 1, 8 + results.size(), "", gray));
				worksheeds[1].addCell(new Label(i * (testTypes.length + 2) + 1, 8 + results.size(), "", gray));
			}

			// fill with data
			for (int i = 0; i < results.size(); i++) {
				TestResult result = results.get(i);
				writeAllNumber(0, 5 + i, i + 1, dataMid);
				for (int k = 0; k < result.getDifferentK(); k++) {
					for (int j = 0; j < testTypes.length; j++) {
						writeNumber(0, k * (testTypes.length + 2) + j + 2, i + 5,
								result.getRuntimeTotal(k * testTypes.length + j));
						// tests with more than 1000 iterations get a green
						// background; the green tone is based on how many times
						// 1000 iterations were exceeded
						int colourResult2 = Math.min(5, result.getMaxIterationsTotal(k * testTypes.length + j));
						colourFlag2 = Math.max(colourResult2, colourFlag2);
						writeNumber(1, k * (testTypes.length + 2) + j + 2, i + 5,
								result.getIterationTotal(k * testTypes.length + j), colours2[colourResult2]);
						// silhouettes of clusterings with empty clusters get a
						// blue background; the blue tone is based on the number
						// of empty clusters
						int colourResult = result.getEmptyClusters(k * testTypes.length + j);
						colourFlag = Math.max(colourFlag, colourResult);
						writeNumber(2, k * (testTypes.length + 2) + j + 2, i + 5,
								result.getSilhouette(k * testTypes.length + j), colours[colourResult]);
						writeNumber(3, k * (testTypes.length + 2) + j + 2, i + 5,
								result.getRuntimeBasic(k * testTypes.length + j));
						int colourResult3 = Math.min(5, result.getMaxIterationsBasic(k * testTypes.length + j));
						colourFlag3 = Math.max(colourResult3, colourFlag3);
						writeNumber(4, k * (testTypes.length + 2) + j + 2, i + 5,
								result.getIterationBasic(k * testTypes.length + j), colours2[colourResult3]);
					}
					writeNumber(0, (k + 1) * (testTypes.length + 2), i + 5, result.getAvgRuntimeTotal(k), title);
					writeNumber(1, (k + 1) * (testTypes.length + 2), i + 5, result.getAvgIterationTotal(k), title);
					writeNumber(2, (k + 1) * (testTypes.length + 2), i + 5, result.getAvgSilhouette(k), title);
					writeNumber(3, (k + 1) * (testTypes.length + 2), i + 5, result.getAvgRuntimeBasic(k), title);
					writeNumber(4, (k + 1) * (testTypes.length + 2), i + 5, result.getAvgIterationBasic(k), title);
				}
				for (int k = result.getDifferentK(); k < differentK; k++) {
					for (int j = 0; j < testTypes.length + 1; j++) {
						writeAllLabel(k * (testTypes.length + 2) + j + 2, i + 5, "-");
					}
				}
			}

			// write average values
			for (int k = 0; k < differentK; k++) {
				for (int j = 0; j < testTypes.length; j++) {
					writeNumber(0, k * (testTypes.length + 2) + j + 2, 6 + results.size(),
							avgResult.getRuntimeTotal(k * testTypes.length + j), title);
					writeNumber(1, k * (testTypes.length + 2) + j + 2, 6 + results.size(),
							avgResult.getIterationTotal(k * testTypes.length + j), title);
					writeNumber(2, k * (testTypes.length + 2) + j + 2, 6 + results.size(),
							avgResult.getSilhouette(k * testTypes.length + j), title);
					writeNumber(3, k * (testTypes.length + 2) + j + 2, 6 + results.size(),
							avgResult.getRuntimeBasic(k * testTypes.length + j), title);
					writeNumber(4, k * (testTypes.length + 2) + j + 2, 6 + results.size(),
							avgResult.getIterationBasic(k * testTypes.length + j), title);
				}
				writeAllLabel((k + 1) * (testTypes.length + 2), 6 + results.size(), "XXX", title);
			}

			// write formulas
			for (int k = 0; k < differentK; k++) {
				for (int j = 0; j < testTypes.length; j++) {
					worksheeds[0].addCell(new Formula(k * (testTypes.length + 2) + j + 2, 7 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2) + (7 + results.size())
									+ "-BasicRuntimes!"
									+ Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2)
									+ (7 + results.size()),
							title));
					worksheeds[1].addCell(new Formula(k * (testTypes.length + 2) + j + 2, 7 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2) + (7 + results.size())
									+ "-BasicIterations!"
									+ Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2)
									+ (7 + results.size()),
							title));
					worksheeds[0].addCell(new Formula(k * (testTypes.length + 2) + j + 2, 8 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2) + (8 + results.size())
									+ "*100/" + Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2)
									+ (7 + results.size()),
							title));
					worksheeds[1].addCell(new Formula(k * (testTypes.length + 2) + j + 2, 8 + results.size(),
							Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2) + (8 + results.size())
									+ "*100/" + Number2ExcelColumn.getColumn(k * (testTypes.length + 2) + j + 2)
									+ (7 + results.size()),
							title));
				}
				writeLabel(0, (k + 1) * (testTypes.length + 2), 7 + results.size(), "XXX", title);
				writeLabel(1, (k + 1) * (testTypes.length + 2), 7 + results.size(), "XXX", title);
				writeLabel(0, (k + 1) * (testTypes.length + 2), 8 + results.size(), "XXX", title);
				writeLabel(1, (k + 1) * (testTypes.length + 2), 8 + results.size(), "XXX", title);
			}

			// colour description
			if (colourFlag > 0) {
				for (int i = 0; i < (int) ((differentK * (testTypes.length + 2) + 3) / (colourFlag + 4)); i++) {
					writeLabel(2, i * (colourFlag + 4), results.size() + 8, "Number of empty clusters:", description);
					writeLabel(2, i * (colourFlag + 4) + 1, results.size() + 8, "No empty clusters in all tests",
							colours[0]);
					for (int j = 1; j < colourFlag + 1; j++) {
						writeLabel(2, i * (colourFlag + 4) + 1 + j, results.size() + 8,
								j + " empty Clusters in at least one test", colours[j]);
					}
				}
			}
			if (colourFlag2 > 0) {
				for (int i = 0; i < (int) ((differentK * (testTypes.length + 2) + 3) / (colourFlag2 + 4)); i++) {
					writeLabel(1, i * (colourFlag2 + 4), results.size() + 10, "More than 1000 iterations:",
							description);
					writeLabel(1, i * (colourFlag2 + 4) + 1, results.size() + 10, "0x", colours2[0]);
					for (int j = 1; j < Math.min(colourFlag2 + 1, 5); j++) {
						writeLabel(1, i * (colourFlag2 + 4) + 1 + j, results.size() + 10, j + "x", colours2[j]);
					}
					if (colourFlag2 == 5) {
						writeLabel(1, i * (colourFlag2 + 4) + 6, results.size() + 10, ">5x", colours2[5]);
					}
				}
			}
			if (colourFlag3 > 0) {
				for (int i = 0; i < (int) ((differentK * (testTypes.length + 2) + 3) / (colourFlag3 + 4)); i++) {
					writeLabel(4, i * (colourFlag3 + 4), results.size() + 8, "More than 1000 iterations:", description);
					writeLabel(4, i * (colourFlag3 + 4) + 1, results.size() + 8, "0x", colours2[0]);
					for (int j = 1; j < Math.min(colourFlag3 + 1, 5); j++) {
						writeLabel(4, i * (colourFlag3 + 4) + 1 + j, results.size() + 8, j + "x", colours2[j]);
					}
					if (colourFlag3 == 5) {
						writeLabel(4, i * (colourFlag3 + 4) + 6, results.size() + 8, ">5x", colours2[5]);
					}
				}
			}

			workbook.write();
			workbook.close();
		} catch (Exception e) {
			System.err.println("Failed writing excel file");
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation") // works fine
	public void saveFindKTestResults(WritableWorkbook workbook, List<FindKResult> results, int pointNumber,
			int differentK) {
		try {
			// Create sheets
			workbook.createSheet("FindK", 0);
			WritableSheet sheet = workbook.getSheet(0);

			// Create fonts
			WritableFont font = new WritableFont(WritableFont.ARIAL, 12);
			WritableCellFormat description = new WritableCellFormat(font);
			description.setWrap(false);
			description.setAlignment(Alignment.CENTRE);
			description.setVerticalAlignment(VerticalAlignment.CENTRE);
			description.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
			WritableFont font2 = new WritableFont(WritableFont.ARIAL, 12);
			WritableCellFormat data = new WritableCellFormat(font2);
			data.setAlignment(Alignment.LEFT);
			data.setWrap(true);
			data.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableCellFormat dataMid = new WritableCellFormat(font2);
			dataMid.setAlignment(Alignment.CENTRE);
			dataMid.setWrap(true);
			dataMid.setVerticalAlignment(VerticalAlignment.CENTRE);
			WritableFont font3 = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
			WritableCellFormat title = new WritableCellFormat(font3);
			title.setAlignment(Alignment.LEFT);
			WritableCellFormat gray = new WritableCellFormat(font2);
			gray.setBackground(Colour.GRAY_25);

			// set cell format and size
			for (int i = 0; i < differentK * 3 + 3; i++) {
				sheet.setColumnView(i, 12, data);
			}

			// merge sheet description cells
			sheet.mergeCells(0, 0, differentK * 3 + 2, 0);
			for (int i = 0; i < differentK; i++) {
				sheet.mergeCells(i * 3 + 3, 2, i * 3 + 5, 2);
			}
			sheet.mergeCells(0, 2, 0, 3);
			sheet.mergeCells(1, 2, 1, 3);

			// name titles
			sheet.addCell(new Label(0, 0,
					"FindK Test; " + results.size() + " different data sets with " + pointNumber + " points", title));
			sheet.addCell(new Label(0, 2, "Data Set #", description));
			sheet.addCell(new Label(1, 2, "d", description));
			for (int i = 0; i < differentK; i++) {
				sheet.addCell(new Label(i * 3 + 3, 3, "Estim. k", description));
				sheet.addCell(new Label(i * 3 + 4, 3, "Hits", description));
				sheet.addCell(new Label(i * 3 + 5, 3, "Silhouette", description));
			}

			// gray row
			for (int i = 0; i < differentK * 3 + 3; i++) {
				sheet.addCell(new Label(i, 4, "", gray));
			}
			// gray column
			for (int j = 2; j < 5 + results.size(); j++) {
				sheet.addCell(new Label(2, j, "", gray));
			}

			// name columns
			for (int i = 0; i < differentK; i++) {
				sheet.addCell(new Label(3 + i * 3, 2, "Initial k: " + (i + 1), description));
			}

			// fill with data
			for (int i = 0; i < results.size(); i++) {
				FindKResult result = results.get(i);
				sheet.addCell(new Number(0, 5 + i, i + 1, dataMid));
				sheet.addCell(new Number(1, 5 + i, i / 20 + 2, dataMid));
				for (int j = 0; j < differentK; j++) {
					sheet.addCell(new Number(3 + j * 3, i + 5, result.getOptimalK(j)));
					sheet.addCell(new Number(3 + j * 3 + 1, i + 5, result.getHits(j)));
					sheet.addCell(new Number(3 + j * 3 + 2, i + 5, result.getSilhouette(j)));
				}
			}

			workbook.write();
			workbook.close();
		} catch (

		Exception e)

		{
			System.err.println("Failed writing excel file");
			e.printStackTrace();
		}

	}

	private void writeNumber(int ws, int c, int r, double val) {
		try {
			worksheeds[ws].addCell(new Number(c, r, val));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing number");
		}
	}

	private void writeNumber(int ws, int c, int r, double val, WritableCellFormat cf) {
		try {
			worksheeds[ws].addCell(new Number(c, r, val, cf));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing number");
		}
	}

	private void writeAllNumber(int c, int r, double val, WritableCellFormat cf) {
		try {
			for (WritableSheet ws : worksheeds) {
				ws.addCell(new Number(c, r, val, cf));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing numbers");
		}
	}

	private void writeLabel(int ws, int c, int r, String val) {
		try {
			worksheeds[ws].addCell(new Label(c, r, val));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing label");
		}
	}

	private void writeLabel(int ws, int c, int r, String val, WritableCellFormat cf) {
		try {
			worksheeds[ws].addCell(new Label(c, r, val, cf));
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing label");
		}
	}

	private void writeAllLabel(int c, int r, String val) {
		try {
			for (WritableSheet ws : worksheeds) {
				ws.addCell(new Label(c, r, val));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing labels");
		}
	}

	private void writeAllLabel(int c, int r, String val, WritableCellFormat cf) {
		try {
			for (WritableSheet ws : worksheeds) {
				ws.addCell(new Label(c, r, val, cf));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed writing labels");
		}
	}

	private void mergeAll(int c1, int r1, int c2, int r2) {
		try {
			for (WritableSheet ws : worksheeds) {
				ws.mergeCells(c1, r1, c2, r2);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Failed merging cells");
		}
	}
}
