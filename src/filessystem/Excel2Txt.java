package filessystem;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Parser who reads excel files and stores their data in text files. Uses the
 * library jexcel.
 */
public class Excel2Txt {

	public static void convertFile() {

		Workbook workbook = null;
		try {
			File file2 = new File("./../Testdaten/Folds5x2_pp.xls");
			workbook = Workbook.getWorkbook(file2);
			for (int i = 0; i < 5; i++) {
				Sheet sheet = workbook.getSheet(i);
				for (int j = 0; j < 4; j++) {
					Path file = (Path) Paths.get("./../ClusterDaten/ccpp_" + (i * 4 + j + 1) + ".txt");
					BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
					for (int k = 1; k < 9569; k++) {
						writer.write(sheet.getCell(j, k).getContents().replace(",", ".") + ","
								+ sheet.getCell(4, k).getContents().replace(",", ".") + "\n");
					}
					writer.flush();
					writer.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}
}
