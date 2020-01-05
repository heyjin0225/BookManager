package manager.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import manager.bean.BookDTO;

public class BookManagerA extends JPanel implements ActionListener {
	private JLabel listL, codeL, genreL, writerL, publisherL, bookNameL, checkL, photoL, areaL;
	private JTextField books, codes, writers, publishers, bookNames, checks, genres;
	private JButton searchB, insertB, deleteB, updateB, refreshB;
	private JComboBox<String> combo, genre;
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scroll, scroll2;
	private Vector<String> vector = new Vector<String>();
	private ArrayList<BookDTO> list;
	private JTextArea area;
	

	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public BookManagerA(ObjectOutputStream oos, ObjectInputStream ois) {
		this.oos = oos;
		this.ois = ois;

		// 프레임 설정
		viewMaker();

		// 모든 책 리스트 요청
		needAllBookList();

		// 요청 결과 받아옴
		run();

		// 이벤트
		searchB.addActionListener(this);
		books.addActionListener(this);
		insertB.addActionListener(this);
		refreshB.addActionListener(this);
		deleteB.addActionListener(this);
		updateB.addActionListener(this);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					System.out.println(table.getSelectedRow());
					int row = table.getSelectedRow();
					String code = (String) table.getValueAt(row, 0);
					for (BookDTO data : list) {
						if (data.getSeq() == Integer.parseInt(code)) {
							bookNames.setText(data.getBookName());
							codes.setText(data.getSeq() + "");
							writers.setText(data.getWriter());
							publishers.setText(data.getPublisher());
							genres.setText(data.getGenre());
							checks.setText(data.getRentState() == 0 ? "대여가능" : "대여불가능");
							updateB.setEnabled(true);
							deleteB.setEnabled(true);
							insertB.setEnabled(false);//추가
						}
					}
					String bookName = (String) table.getValueAt(row, 1);
					ImageIcon cover = new ImageIcon("image/"+bookName+".jpg");
					ImageIcon text = new ImageIcon("image/이미지 준비중입니다.jpg");
					if(cover.getIconWidth()<100) {
						Image textI = text.getImage();
						Image textIma = textI.getScaledInstance(140, 185, Image.SCALE_SMOOTH);
						ImageIcon textImage = new ImageIcon(textIma);
						photoL.setIcon(textImage);
					}else {
						Image coverI = cover.getImage();
						Image coverIma = coverI.getScaledInstance(140, 185, Image.SCALE_SMOOTH);
						ImageIcon coverImage = new ImageIcon(coverIma);
						photoL.setIcon(coverImage);
						
					}
					
				}else if (e.getClickCount() == 2) {
					System.out.println(table.getSelectedRow());
					int row = table.getSelectedRow();
					String bookName = (String) table.getValueAt(row, 1);
					ImageIcon cover = new ImageIcon("image/"+bookName+".jpg");
					if(cover.getIconWidth()<100) {
						JOptionPane.showMessageDialog(table, "이미지는 준비중입니다.", "표지이미지", JOptionPane.PLAIN_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(table, "", "표지이미지", JOptionPane.PLAIN_MESSAGE,cover);
					}
				}
			}
		});
	}// 생성자

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == searchB || e.getSource() == books) {

			// 서버에게 요청 보내고
			BookDTO dto = new BookDTO();
			dto.setCommand("SearchBook");

			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			// 답변받으려면 수동으로 run 호출이 필요
			run();
			insertB.setEnabled(true);

		} else if (e.getSource() == insertB) {
			int result = JOptionPane.showConfirmDialog(BookManagerA.this, "추가하시겠습니까?", 
					"추가", JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if(result == JOptionPane.OK_OPTION) {
				if(bookNames.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "도서명을 입력해주세요.");
					return;
				}else if(writers.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "저자를 입력해주세요.");
					return;
				}else if(publishers.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "출판사를 입력해주세요.");
					return;
				}else if(genres.getText().equals("")) {
					JOptionPane.showMessageDialog(this, "장르를 입력해주세요.");
					return;
				}
			}
			/*	
			String bookName = JOptionPane.showInputDialog(this, "제목입력", "제목입력창", JOptionPane.QUESTION_MESSAGE);
			if (bookName == null)
				return;
			String writer = JOptionPane.showInputDialog(this, "저자입력", "저자입력창", JOptionPane.QUESTION_MESSAGE);
			if (writer == null)
				return;
			String publisher = JOptionPane.showInputDialog(this, "출판사입력", "출판사입력창", JOptionPane.QUESTION_MESSAGE);
			if (publisher == null)
				return;
			String genre = JOptionPane.showInputDialog(this, "장르입력", "장르입력창", JOptionPane.QUESTION_MESSAGE);
			if (genre == null)
				return;
*/
			BookDTO dto = new BookDTO();
			dto.setBookName(bookNames.getText());
			dto.setWriter(writers.getText());
			dto.setPublisher(publishers.getText());
			dto.setGenre(genres.getText());
			dto.setCommand("AddBook");

			try {
				oos.writeObject(dto);
				System.out.println("보냄");
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		} else if (e.getSource() == deleteB) {
			BookDTO dto = new BookDTO();
			dto.setSeq(Integer.parseInt(codes.getText()));
			dto.setCommand("DeleteBook");
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();

		} else if (e.getSource() == updateB) {
			
			BookDTO dto = new BookDTO();
			dto.setSeq(Integer.parseInt(codes.getText()));
			dto.setBookName(bookNames.getText());
			dto.setWriter(writers.getText());
			dto.setPublisher(publishers.getText());
			dto.setGenre(genres.getText());
			int rentState;
			if (checks.getText().equals("대여가능")) {
				rentState = 0;
			} else
				rentState = 1;
			dto.setRentState(rentState);
			dto.setCommand("UpdateBook");
			
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		} else if (e.getSource() == refreshB) {
			books.setText("");
			bookNames.setText("");
			writers.setText("");
			publishers.setText("");
			genres.setText("");
			checks.setText("");
			photoL.setIcon(null);
			
			int get = combo.getSelectedIndex();
			if(get!=0) {
				combo.setSelectedItem("전체") ;
			}
			
			deleteB.setEnabled(false);
			updateB.setEnabled(false);
			insertB.setEnabled(true);
			
			needAllBookList();
			run();
		}
	}

	public void viewMaker() {
		// 레이아웃
		setLayout(null);

		// JLabel
		listL = new JLabel("도서목록");
		codeL = new JLabel("도서코드");
		genreL = new JLabel("장르");
		writerL = new JLabel("저자");
		publisherL = new JLabel("출판사");
		bookNameL = new JLabel("도서명");
		checkL = new JLabel("대출여부");
		photoL = new JLabel();
		areaL = new JLabel("설명");

		// JButton
		insertB = new JButton("추가");
		updateB = new JButton("수정");
		deleteB = new JButton("삭제");
		searchB = new JButton("검색");
		refreshB = new JButton("새로고침");

		updateB.setEnabled(false);
		deleteB.setEnabled(false);

		// JTextField
		books = new JTextField();
		codes = new JTextField();
		writers = new JTextField();
		publishers = new JTextField();
		bookNames = new JTextField();
		checks = new JTextField();
		genres = new JTextField();
		
		checks.setEditable(false);
		// ComboBox
		String book[] = { "전체", "도서명", "저자", "출판사", "장르" };
		combo = new JComboBox<String>(book);
		DefaultListCellRenderer dicr = new DefaultListCellRenderer();
		dicr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		combo.setRenderer(dicr);


		genre = new JComboBox<String>(book);
		
		//JTextArea
		area = new JTextArea();
		scroll2 = new JScrollPane(area);
		
		//list
		vector.addElement("도서코드");
		vector.addElement("도서명");
		vector.addElement("저자");
		vector.addElement("출판사");
		vector.addElement("장르");
		vector.addElement("대여상태");

		model = new DefaultTableModel(vector, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};

		table = new JTable(model);
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); 
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getColumnModel().getColumn(2).setPreferredWidth(110);
		table.getColumnModel().getColumn(3).setPreferredWidth(90);
		table.getColumnModel().getColumn(4).setPreferredWidth(60);
		table.getColumnModel().getColumn(5).setPreferredWidth(60);
		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scroll = new JScrollPane(table);
		
		

		// 위치
		combo.setBounds(25, 20, 80, 25);
		books.setBounds(115, 20, 150, 25);
		searchB.setBounds(275, 20, 70, 25);
		scroll.setBounds(25, 55, 600, 375);
		//listL.setBounds(60, 75, 80, 30);
		
		
		
		
		
		JPanel p = new JPanel();
		p.setLayout(null);
		
		//codeL.setBounds(560, 30, 60, 25);
		//codes.setBounds(560, 350, 90, 25);
		bookNameL.setBounds(10, 30, 50, 15);
		bookNames.setBounds(10, 45, 330, 25);
		writerL.setBounds(10, 75, 30, 15);
		writers.setBounds(10, 90, 180, 25);
		publisherL.setBounds(10, 120, 50, 15);
		publishers.setBounds(10, 135, 180, 25);
		genreL.setBounds(10, 165, 30, 15);
		genres.setBounds(10, 180, 180, 25);
		checkL.setBounds(10, 210, 60, 15);
		checks.setBounds(10, 225, 180, 25);
		photoL.setBounds(200, 75, 140, 185);
		areaL.setBounds(10, 255, 60, 15);
		scroll2.setBounds(10, 270, 330, 120);
		
		updateB.setBounds(240, 395, 100, 25);
		refreshB.setBounds(240, 10, 100, 25);
		insertB.setBounds(130, 395, 100, 25);
		deleteB.setBounds(10, 395, 100, 25);
		
		
		p.add(genreL);
		p.add(genres);
		p.add(writerL);
		p.add(writers);
		p.add(publisherL);
		p.add(publishers);
		p.add(bookNameL);
		p.add(bookNames);
		p.add(checkL);
		p.add(checks);
		p.add(photoL);
		p.add(areaL);
		p.add(scroll2);
		p.add(updateB);
		p.add(insertB);
		p.add(deleteB);
		p.add(refreshB);
		
		photoL.setOpaque(true);
		photoL.setBackground(Color.WHITE);
		
		
		p.setBackground(new Color(153,214,234));
		p.setBounds(640, 10, 360, 420);
		
		// 추가
		add(combo);
		add(books);
		add(searchB);
		add(scroll);
		add(listL);
		add(codeL);
		add(codes);
		
		add(p);
		

		setBackground(new Color(153,214,234));
		setBounds(700, 100, 900, 500);
		setVisible(true);
	}

	public void needAllBookList() {
		BookDTO dto = new BookDTO();
		dto.setCommand("BookList");

		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void updateList(BookDTO data) {
		Vector<String> v = new Vector<String>();
		v.addElement(data.getSeq() + "");
		v.addElement(data.getBookName());
		v.addElement(data.getWriter());
		v.addElement(data.getPublisher());
		v.addElement(data.getGenre());
		v.addElement(data.getRentState() == 0 ? "대여가능" : "대여불가능");
		model.addRow(v);
	}
	public void run() {
		BookDTO dtoB = null;
		Object temp = null; // 스트림 오브젝트 판별위해서
		try {
			temp = ois.readObject();
			if (temp.toString().equals("BookDTO")) {
				dtoB = (BookDTO) temp;
			}
			if (dtoB != null) {
				if (dtoB.getCommand().equals("BookList") || dtoB.getCommand().equals("AddBook")) {// 책목록띄우기
					model.setRowCount(0);
					list = dtoB.getList();
					for (BookDTO data : list) {
						updateList(data);
					}
				} else if (dtoB.getCommand().equals("UpdateBook")) {
					model.setRowCount(0);
					list = dtoB.getList();
					for (BookDTO data : list) {
						updateList(data);
					}
				} else if (dtoB.getCommand().equals("SearchBook")) {
					if (combo.getSelectedIndex() == 0) {// 전체
						model.setRowCount(0);
						list = dtoB.getList();
						String[] ar = new String[] { books.getText() };
						for (BookDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getBookName().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								} else if (data.getWriter().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								} else if (data.getPublisher().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								} else if (data.getGenre().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					} else if (combo.getSelectedIndex() == 1) {// 도서명
						model.setRowCount(0);
						list = dtoB.getList();
						String[] ar = new String[] { books.getText() };
						for (BookDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getBookName().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					} else if (combo.getSelectedIndex() == 2) {// 저자
						model.setRowCount(0);
						list = dtoB.getList();
						String[] ar = new String[] { books.getText() };
						for (BookDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getWriter().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					} else if (combo.getSelectedIndex() == 3) {// 출판사
						model.setRowCount(0);
						list = dtoB.getList();
						String[] ar = new String[] { books.getText() };
						for (BookDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getPublisher().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					} else if (combo.getSelectedIndex() == 4) {// 장르
						model.setRowCount(0);
						list = dtoB.getList();
						String[] ar = new String[] { books.getText() };
						for (BookDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getGenre().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					}
				} else if (dtoB.getCommand().equals("DeleteBook")) {
					model.setRowCount(0);
					list = dtoB.getList();
					for (BookDTO data : list) {
						updateList(data);
					}
				}

			}
		} catch ( 

		ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}


