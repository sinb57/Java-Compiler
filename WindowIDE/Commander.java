package WindowIDE;

import java.io.BufferedReader;
import java.io.*;
import java.util.LinkedList;

public class Commander {
	private Process oProcess = null;
	private LinkedList<String> list = new LinkedList<String>();
	private String s = null;
	private String result = "";

	// 명령어 list를 초기화하는 메소드
	public void init() {
		list.clear();
		list.add("cmd");
		list.add("/c");
	}

	// 현재 result에 저장된 값을 pop하는 메소드
	public String popResult() {
		String str = result;
		result = "";
		return str;
	}

	// 파일의 내용을 반환하는 메소드
	public void open(String path) {
		this.init();
		list.add("type");
		list.add(path);
		this.start();
	}

	// 파일을 컴파일하는 메소드
	public String compile(String path) {
		this.init();
		list.add("javac");
		list.add("-d");
		list.add((new File(path)).getParent());
		list.add((new File(path)).getPath());
		if (this.start() == 0)
			result = "compile success";
		return this.popResult();
	}

	// 현재 작성된 명령어 list를 실행하는 메소드
	private int start() {
		try {
			ProcessBuilder builder = new ProcessBuilder(list);
			oProcess = builder.start();

			BufferedReader stdOut = new BufferedReader(new InputStreamReader(oProcess.getInputStream(), "utf-8"));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(oProcess.getErrorStream(), "utf-8"));
			while ((s = stdOut.readLine()) != null)
				result += s + "\n";
			while ((s = stdError.readLine()) != null)
				result += s + "\n";

		} catch (IOException e) {
			System.out.println(this.popResult());
			result = e.getMessage();
		}
		return oProcess.exitValue();
	}

}
