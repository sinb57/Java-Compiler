package WindowIDE;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.*;

public class MainFrame extends JFrame {
	ArrayList<MyFile> list = new ArrayList<MyFile>();
	JTextArea resultWindow;
	JTabbedPane pane;

	MainFrame() {
		this.setLayout(new BorderLayout(0, 1));

		// 메뉴바 생성
		createMenu();

		// 탭팬 생성
		pane = new JTabbedPane();
		this.add(pane, BorderLayout.CENTER);

		// resultWindow 생성
		resultWindow = new JTextArea(10, 50);
		this.add(new JScrollPane(resultWindow), BorderLayout.SOUTH);

		// 기본 설정
		setTitle("Compiler");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 800);
		setVisible(true);
	}

	void createMenu() {
		JMenuItem item[] = new JMenuItem[6];
		String itemName[] = { "Open", "Close", "Save", "SaveAs", "Quit", "Compile" };

		// 메뉴 바
		JMenuBar mb = new JMenuBar();

		// 메뉴 (메뉴바의 컴포넌트)
		JMenu fileMenu = new JMenu("File");
		JMenu runMenu = new JMenu("Run");

		// fileMenu 컴포넌트 구성
		for (int i = 0; i < 6; i++) {
			item[i] = new JMenuItem(itemName[i]);
			item[i].addActionListener(new myActionListener());
		}

		// fileMenu 컴포넌트 별 단축키 생성
		item[0].setAccelerator(KeyStroke.getKeyStroke('T', KeyEvent.CTRL_MASK)); // Open tab
		item[1].setAccelerator(KeyStroke.getKeyStroke('W', KeyEvent.CTRL_MASK)); // Close tab
		item[2].setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_MASK)); // Save
		item[3].setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK)); // Save As
		item[4].setAccelerator(KeyStroke.getKeyStroke('Q', KeyEvent.CTRL_MASK)); // Quit
		item[5].setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK)); // Compile

		// 메뉴바에 컴포넌트 부착
		for (int i = 0; i < 5; i++)
			fileMenu.add(item[i]);
		runMenu.add(item[5]);

		// 메뉴 바 색상 설정 및 부착
		mb.add(fileMenu);
		mb.add(runMenu);
		mb.setBackground(Color.ORANGE);
		this.setJMenuBar(mb);
	}

	class myActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String str = e.getActionCommand();
			Commander cmd = new Commander();
			int i = pane.getSelectedIndex();

			if (str.equals("Open")) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new FileNameExtensionFilter("java Files", "java"));
				int index = list.size();

				if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
					resultWindow.setText("Open Failed");
					return;
				}

				// 선택한 파일의 경로와 이름
				String path = chooser.getSelectedFile().getPath();
				String name = chooser.getSelectedFile().getName();

				// 파일 중복 오픈 방지
				for (MyFile f : list) {
					if (path.equals(f.getPath())) {
						resultWindow.setText("File is Duplicated");
						return;
					}
				}

				// list 추
				list.add(new MyFile(path, name));
				cmd.open(path);
				list.get(index).setEditWindow(cmd.popResult());
				pane.addTab(name, new JScrollPane(list.get(index).getEditWindow()));
				index++;

			} else if (str.equals("Close")) {
				try {
					pane.remove(i);
					list.remove(i);
				} catch (Exception ex) {
					return;
				}

			} else if (str.equals("Save")) {
				// 확인 다이얼로그 출력
				int result = JOptionPane.showConfirmDialog(null, "저장하시겠습니까?", "Save", JOptionPane.YES_NO_OPTION);

				// "예"를 선택한 경우만 아래 코드 작동
				if (result == JOptionPane.YES_OPTION)
					try {
						MyFile mf = list.get(pane.getSelectedIndex()); // 현재 선택한 탭팬에 해당하는 리스트의 요소를 'mf'가 가리킴
						// (open한 파일이 없을 경우 예외 발생)
						File f = new File(mf.getPath()); // 'f'가 'mf'의 경로에 해당하는 파일을 가리킴
						BufferedWriter bw = new BufferedWriter(new FileWriter(f)); // 파일 출력을 위한 객체 생성
						String text = mf.getEditWindow().getText(); // 파일에 출력할 문자열을 윈도우로부터 가져옴
						bw.write(text, 0, text.length()); // 파일 'f'에 변경된 내용을 출력
						bw.flush(); // 버퍼 비우기
						bw.close(); // 종료
					} catch (Exception ex) {
						// 예외처리
						JOptionPane.showMessageDialog(null, "파일을 선택하세요", "오류", JOptionPane.ERROR_MESSAGE);
						return;
					}

			} else if (str.equals("SaveAs")) {
				JFileChooser chooser = new JFileChooser();

				// 파일 찾기 다이얼로그 출력
				int ret = chooser.showSaveDialog(null);
				try {
					// "확인"을 선택한 경우만 아래 코드 작동
					if (ret == JFileChooser.APPROVE_OPTION) {
						MyFile mf = list.get(pane.getSelectedIndex()); // 현재 선택한 탭팬에 해당하는 리스트의 요소를 'mf'가 가리킴(open한 파일이
						// 없을 경우 예외 발생)
						String path = chooser.getSelectedFile().getPath(); // 파일 찾기 다이얼로그의 파일 경로를 'path'가 가리킴
						File f = new File(path); // 파일 'f'가 'mf'의 경로에 해당하는 파일을 가리킴
						if (f.exists()) { // 파일 'f'가 이미 존재할 경우, 에러 다이얼로그 출력
							JOptionPane.showMessageDialog(null, "이미 존재하는 파일입니다", "오류", JOptionPane.ERROR_MESSAGE);
							return;
						}
						// 파일이 존재하지 않을 경우, 정상적인 파일 출력
						BufferedWriter bw = new BufferedWriter(new FileWriter(f));
						String text = mf.getEditWindow().getText();
						bw.write(text, 0, text.length());
						bw.flush();
						bw.close();
					}
				} catch (ArrayIndexOutOfBoundsException e1) {
					// Open한 파일이 없을 경우, 예외 처리
					JOptionPane.showMessageDialog(null, "파일을 선택하세요", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				} catch (IOException e2) {
					// 버퍼 출력에 대한 예외 처리
					JOptionPane.showMessageDialog(null, "알수 없는 오류", "오류", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			else if (str.equals("Compile"))
				try {
					resultWindow.setText(cmd.compile(list.get(i).getPath()));
				} catch (Exception ex) {
					return;
				}

			else if (str.equals("Quit"))
				System.exit(0);

		}
	}

	public static void main(String args[]) {
		new MainFrame();
	}
}
