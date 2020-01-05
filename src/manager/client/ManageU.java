package manager.client;

import java.awt.Color;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import manager.bean.BookDTO;
import manager.bean.MemberDTO;
import manager.bean.RentDTO;
import manager.dao.BookDAO;
import manager.server.Guest_main;


class ManageU extends JPanel implements ActionListener{
	
	private JLabel jp1L;
	private JTextField findT; 
	private JComboBox<String> kindCombo;
	private JTable jtable1;
	private DefaultTableModel model;
	private Vector<String> vector = new Vector<String>();
	private ArrayList<BookDTO> alist;
	private JButton searchB, newB;
	private JScrollPane scroll, scroll2;
	
	private JButton rentB = new JButton("�뿩�ϱ�");
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
//	private int counting = 0;
	
	private MemberDTO rent_user = new MemberDTO();
	
	private ArrayList<Integer> seq = new ArrayList<>();
	
	private String id;
	
	private JLabel listL, codeL, genreL, writerL, publisherL, bookNameL, checkL, photoL, areaL;
	private JTextField books, codes, writers, publishers, bookNames, checks, genres;
	
	private JTextArea area;
	
	private GuestU g;
	
	public ManageU(ObjectOutputStream oos, ObjectInputStream ois, String id, GuestU g) {
		this.oos = oos;
		this.ois = ois;
		this.id = id;
		this.g = g;
		//����Ʈ ��û
		needAllBookList();
		
		//��û �޾ƿ�
		run();
		
		//�г� ����
		viewMaker();
		
		//�̺�Ʈ
		findT.addActionListener(this);
		searchB.addActionListener(this);
		newB.addActionListener(this);
		rentB.addActionListener(this);
		
		jtable1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					System.out.println(jtable1.getSelectedRow());
					int row = jtable1.getSelectedRow();
					String code = (String) jtable1.getValueAt(row, 0);
					for (BookDTO data : alist) {
						if (data.getBookName().equals(code)) {
							bookNames.setText(data.getBookName());
							writers.setText(data.getWriter());
							publishers.setText(data.getPublisher());
							genres.setText(data.getGenre());
							checks.setText(data.getRentState() == 0 ? "�뿩����" : "�뿩�Ұ���");
						}
					}
					String bookName = (String) jtable1.getValueAt(row, 0);
					ImageIcon cover = new ImageIcon("image/"+bookName+".jpg");
					ImageIcon text = new ImageIcon("image/�̹��� �غ����Դϴ�.jpg");
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
					System.out.println(jtable1.getSelectedRow());
					int row = jtable1.getSelectedRow();
					String bookName = (String) jtable1.getValueAt(row, 0);
					ImageIcon cover = new ImageIcon("image/"+bookName+".jpg");
					if(cover.getIconWidth()<100) {
						JOptionPane.showMessageDialog(jtable1, "�̹����� �غ����Դϴ�.", "ǥ���̹���", JOptionPane.PLAIN_MESSAGE);
					}else {
						JOptionPane.showMessageDialog(jtable1, "", "ǥ���̹���", JOptionPane.PLAIN_MESSAGE,cover);
					}
				}
			}
		});
	}
	
	@Override
	 public void actionPerformed(ActionEvent e) {
		if(e.getSource()==searchB|| e.getSource()==findT) {
			BookDTO dto = new BookDTO();
			dto.setCommand("SearchBook");
			
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			run();
								
			
				
		}else if(e.getSource()==newB) {
			books.setText("");
			bookNames.setText("");
			writers.setText("");
			publishers.setText("");
			genres.setText("");
			checks.setText("");
			photoL.setIcon(null);
			
			int get = kindCombo.getSelectedIndex();
			if(get!=0) {
				kindCombo.setSelectedItem("��ü") ;
			}
			
			//����Ʈ ��û
			needAllBookList();
			
			//��û �޾ƿ�
			run();
			
			model.setRowCount(0);
			for(BookDTO data : alist) {
				updateList(data);
			}
		}else if(e.getSource() == rentB) {
			int result = JOptionPane.showConfirmDialog(this, "�뿩�Ͻðڽ��ϱ�?", 
					"�뿩���", JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if(result==JOptionPane.OK_OPTION) {
				System.out.println(jtable1.getValueAt(jtable1.getSelectedRow(), 0));
				
				if(g.getCounting() >= 3) {
					JOptionPane.showMessageDialog(this, "3�Ǳ����� �뿩�����մϴ�", "�˸�", 1);
					return;
				}
			
				if(jtable1.getValueAt(jtable1.getSelectedRow(), 4).equals("�뿩����")) {
					try {
						rent_user = new MemberDTO();
						rent_user.setId(id);
						rent_user.setCommand("NeedMember");
						oos.writeObject(rent_user);
						oos.flush();
						run();
						
						RentDTO sendDTO_c = new RentDTO();
						System.out.println("��Ʈ���� ���̵�" + rent_user.getId());
						sendDTO_c.setId(rent_user.getId());
						
						//�뿩���ɰ��� ī����
						sendDTO_c.setCommand("Counting");
						oos.writeObject(sendDTO_c);
						oos.flush();
						run();
						
						if(g.getCounting() < 3) {	//�ش������� ����å�� 4�� ������ ���
							System.out.println("å������ ����");
							RentDTO sendDTO = new RentDTO();
							sendDTO.setSeq(seq.get(jtable1.getSelectedRow()));
							sendDTO.setId(rent_user.getId());
							sendDTO.setName(rent_user.getName());
							sendDTO.setBookName((String)jtable1.getValueAt(jtable1.getSelectedRow(), 0));
							sendDTO.setTel(rent_user.getTel());
							sendDTO.setEmail(rent_user.getEmail());
							//date�� db���� ����
							sendDTO.setCommand("NeedRent");
							System.out.println(sendDTO.getCommand());
							oos.writeObject(sendDTO);
							oos.flush();
							run();
							
							g.setCounting(g.getCounting()+1);
							
							needAllBookList();
							run();
							model.setRowCount(0);
							for(BookDTO data : alist) {
								updateList(data);
							}
							JOptionPane.showMessageDialog(this, "�뿩�Ϸ�", "�˸�", 1);
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}else if(jtable1.getValueAt(jtable1.getSelectedRow(), 4).equals("�뿩�Ұ�")){
					JOptionPane.showMessageDialog(this, "�뿩���Դϴ�", "�˸�", 1);
				}
				
			}
			//����Ʈ ��û
			needAllBookList();
			
			//��û �޾ƿ�
			run();
			
			model.setRowCount(0);
			for(BookDTO data : alist) {
				updateList(data);
			}
			
		}	
	}


	private void viewMaker() {
		listL = new JLabel("�������");
		codeL = new JLabel("�����ڵ�");
		genreL = new JLabel("�帣");
		writerL = new JLabel("����");
		publisherL = new JLabel("���ǻ�");
		bookNameL = new JLabel("������");
		checkL = new JLabel("���⿩��");
		photoL = new JLabel();
		areaL = new JLabel("����");
		
		books = new JTextField();
		codes = new JTextField();
		writers = new JTextField();
		publishers = new JTextField();
		bookNames = new JTextField();
		checks = new JTextField();
		genres = new JTextField();
		
		
		
		
		books.setEditable(false);
		codes.setEditable(false);
		writers.setEditable(false);
		publishers.setEditable(false);
		bookNames.setEditable(false);
		checks.setEditable(false);
		genres.setEditable(false);
		
		area = new JTextArea();
		scroll2 = new JScrollPane(area);
		
		                                
		                                
		                                
		                                
		                                
		                                
		
		
		
		
		
		
		vector.addElement("������");
		vector.addElement("����");
		vector.addElement("���ǻ�");
		vector.addElement("�帣");
		vector.addElement("�뿩����");
		
		
		
		model = new DefaultTableModel(vector,0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		
		for(BookDTO data : alist) {
			updateList(data);
		}
		
		jtable1 = new JTable(model);
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); 
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		jtable1.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		
		jtable1.getColumnModel().getColumn(0).setPreferredWidth(200);
		jtable1.getColumnModel().getColumn(1).setPreferredWidth(110);
		jtable1.getColumnModel().getColumn(2).setPreferredWidth(90);
		jtable1.getColumnModel().getColumn(3).setPreferredWidth(60);
		jtable1.getColumnModel().getColumn(4).setPreferredWidth(60);
		
		
		
		findT = new JTextField(20);
		findT.setBounds(115, 20, 150, 25);
		
		kindCombo = new JComboBox<String>();
		DefaultListCellRenderer dicr = new DefaultListCellRenderer();
		dicr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		kindCombo.setRenderer(dicr);
		kindCombo.addItem("��ü");
		kindCombo.addItem("������");
		kindCombo.addItem("����");
		kindCombo.addItem("���ǻ�");
		kindCombo.addItem("�帣");
				
		kindCombo.setBounds(25, 20, 80, 25);
						
		scroll = new JScrollPane(jtable1);
		scroll.setBounds(25, 55, 600, 375);
		//��ũ�ѹ� �׻� ���� ��
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);//���� ��ũ�ѹ�
		//scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);//���� ��ũ�ѹ�
		
		jtable1.getTableHeader().setReorderingAllowed(false);//�÷���ġ �̵��Ұ�
		jtable1.getTableHeader().setResizingAllowed(false);	 //�÷������� ����
		
		searchB = new JButton("�˻�");
		searchB.setBounds(275, 20, 70, 25);
		
		rentB = new JButton("�뿩");
		rentB.setBounds(240, 395, 100, 25);
		
		newB = new JButton("���ΰ�ħ");
		
		JPanel p = new JPanel();
		p.setLayout(null);
		
		
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
		newB.setBounds(240, 10, 100, 25);
		
		
		photoL.setOpaque(true);
		photoL.setBackground(Color.WHITE);
		
		
		p.setBackground(Color.PINK);
		p.setBounds(640, 10, 360, 420);
		
		add(p);
		add(findT);
		add(kindCombo);
		add(scroll);
		add(searchB);
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
		p.add(rentB);
		p.add(newB);
		setBackground(Color.PINK);
		setLayout(null);	
	}
	
	private void needAllBookList() {
		BookDTO dto = new BookDTO();
		dto.setCommand("BookList");
		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	private void updateList(BookDTO data) {
		Vector<String> v = new Vector<String>();
		v.addElement(data.getBookName());
		v.addElement(data.getWriter());
		v.addElement(data.getPublisher());
		v.addElement(data.getGenre());
		v.addElement(data.getRentState()==0?"�뿩����" : "�뿩�Ұ�");
		
		model.addRow(v);
	}
	
	private void run() {
		BookDTO dtoB = null;
		MemberDTO dtoM = null;
		RentDTO dtoR = null;
		Object temp = null;	//��Ʈ�� ������Ʈ �Ǻ����ؼ�
					
		try {
			temp = ois.readObject();	//��Ʈ������ �޾ƿ� DTO�� temp�� ����
			if(temp.toString().equals("BookDTO")) {	//�츮�� DTO���� ��� toString�� �ڱ��̸����� �������̵��߰�,  �� �߿� BookDTO���	
				dtoB = (BookDTO)temp;
//				System.out.println("ĳ���ÿϷ�");
			}else if(temp.toString().equals("MemberDTO")) {
				dtoM = (MemberDTO)temp;
			}else if(temp.toString().equals("RentDTO")) {
				dtoR = (RentDTO)temp;
			}
			
			if(dtoB != null) { //dtoB �޾ƿԴٸ�
				if(dtoB.getCommand().equals("BookList")) {	//��찡 ��Ź�� BookList ��û��  Guest -> BookDAO -> Guest -> ����� ����
					//System.out.println("�ϸ���Ʈ �޾ƿ�");
					alist = dtoB.getList();	//dtoB�� list���� ��� å ����� ����
					for(int i = 0; i < alist.size(); i++) {
						seq.add(alist.get(i).getSeq());
					}
					//System.out.println("ù��° alist = "+alist);
					//System.out.println("-----------------");
					dtoB = null;	//dtoB ������ ���� �� ������, �ѹ� ����� dtoB�� ��������
					//System.out.println("�ϸ���Ʈ �޾ƿ�end");
					
				}else if(dtoB.getCommand().equals("AddBook")) {	//��찡 ��û �����Ͱ� ������� ������ å�� �߰� ������� �������� ������
					System.out.println("�ϸ���Ʈ �߰� å��� ����");
					alist = dtoB.getList();
					Vector<String> v = new Vector<>(); 
					v.addElement(dtoB.getBookName());
					v.addElement(dtoB.getWriter());
					v.addElement(dtoB.getPublisher());
					v.addElement(dtoB.getGenre());
					v.addElement(dtoB.getRentState()==0 ? "0": "1");
					
					model.addRow(v);
					dtoB = null;
				}else if(dtoB.getCommand().equals("SearchBook")) {
					if(kindCombo.getSelectedItem()=="��ü") {
						model.setRowCount(0);
						alist = dtoB.getList();
						String ar[] = new String[] { findT.getText() };
						for(BookDTO data : alist) {
							for(int i=0; i<ar.length; i++) {
								if(data.getBookName().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}else if (data.getWriter().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								} else if (data.getPublisher().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								} else if (data.getGenre().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
						findT.setText("");
					}else if(kindCombo.getSelectedItem()=="������") {
						model.setRowCount(0);
						alist = dtoB.getList();
						String ar[] = new String[] { findT.getText() };
						for(BookDTO data : alist) {
							for(int i=0; i<ar.length; i++) {
								if(data.getBookName().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
						findT.setText("");
					}else if(kindCombo.getSelectedItem()=="����") {
						model.setRowCount(0);
						alist = dtoB.getList();
						String ar[] = new String[] { findT.getText() };
						for(BookDTO data : alist) {
							for(int i=0; i<ar.length; i++) {
								if(data.getWriter().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
						findT.setText("");
					}else if(kindCombo.getSelectedItem()=="���ǻ�") {
						model.setRowCount(0);
						alist = dtoB.getList();
						String ar[] = new String[] { findT.getText() };
						for(BookDTO data : alist) {
							for(int i=0; i<ar.length; i++) {
								if(data.getPublisher().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
						findT.setText("");
					}else if(kindCombo.getSelectedItem()=="�帣") {
						model.setRowCount(0);
						alist = dtoB.getList();
						String ar[] = new String[] { findT.getText() };
						for(BookDTO data : alist) {
							for(int i=0; i<ar.length; i++) {
								if(data.getGenre().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
						findT.setText("");
					}//�帣if��  ������
				}
			}
			
			if(dtoM != null) {
				if(dtoM.getCommand().equals("NeedMember")) {
					
					rent_user = dtoM;
					dtoM = null;
				}
			}
			
			if(dtoR != null) {
				/*if(dtoR.getCommand().equals("Counting")) {
					System.out.println("ī���� �޾ƿ�");
					g.setCounting(dtoR.getCounting()); 
					dtoR = null;
				}else */if(dtoR.getCommand().equals("NeedRent")) {
					System.out.println("å ��Ʈ�Ϸ�");
				}
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}