package tools;

import javafx.scene.control.TextField;

/**
 * Special text field to allows only numbers as input.
 */
public class NumericTextField extends TextField {

	@Override
	public void replaceText(int start, int end, String text) {
		if (validText(text)) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String replacement) {
		if (validText(replacement)) {
			super.replaceSelection(replacement);
		}
	}

	private boolean validText(String text) {
		return text.matches("[0-9]*");
	}
}
