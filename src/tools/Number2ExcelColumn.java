package tools;

/**
 * Transforms an enumeration of columns (starting with 0) into the naming scheme
 * of an excel-tabular with letters (A-Z,AA-AZ,BA-BZ,...).
 */
public class Number2ExcelColumn {

	public static String getColumn(int columnNumber) {
		int c1 = columnNumber / 26;
		int c2 = columnNumber % 26;
		return getCharacter(c1 - 1) + getCharacter(c2);
	}

	private static String getCharacter(int number) {
		switch (number) {
		case -1:
			return "";
		case 0:
			return "A";
		case 1:
			return "B";
		case 2:
			return "C";
		case 3:
			return "D";
		case 4:
			return "E";
		case 5:
			return "F";
		case 6:
			return "G";
		case 7:
			return "H";
		case 8:
			return "I";
		case 9:
			return "J";
		case 10:
			return "K";
		case 11:
			return "L";
		case 12:
			return "M";
		case 13:
			return "N";
		case 14:
			return "O";
		case 15:
			return "P";
		case 16:
			return "Q";
		case 17:
			return "R";
		case 18:
			return "S";
		case 19:
			return "T";
		case 20:
			return "U";
		case 21:
			return "V";
		case 22:
			return "W";
		case 23:
			return "X";
		case 24:
			return "Y";
		case 25:
			return "Z";
		default:
			return null;
		}
	}
}
