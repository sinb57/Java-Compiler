package WindowIDE;

import javax.swing.JTextArea;

public class MyFile {
	private String path = null;
	private String name = null;
	private JTextArea editWindow = new JTextArea(50, 50);

	MyFile(String path, String name) {
		this.path = path;
		this.name = name;
	}

	void setEditWindow(String data) {
		this.editWindow.setText(data);
	}

	JTextArea getEditWindow() {
		return editWindow;
	}

	String getPath() {
		return path;
	}

	String getName() {
		return name;
	}
}