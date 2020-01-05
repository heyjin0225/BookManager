package manager.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

import manager.bean.BookDTO;
import manager.bean.ExitDTO;
import manager.bean.MemberDTO;
import manager.bean.RentDTO;

class MyPageU extends JPanel implements ActionListener{
	private JFrame updateF;
	private JLabel rentL,idL,pwL,nameL,telL,emailL;
	private JTextField idT,pwT,nameT,telT,emailT;
	private JTable jtable2;
	private DefaultTableModel model;
	private Vector<String> vector = new Vector<String>();
	private ArrayList<RentDTO> rlist = new ArrayList<>();
	private JButton returnbookB, updateinfoB,   updateB2, dropB;
	private JScrollPane scroll;
	
	private JButton newB;//c
	
	private ObjectOutputStream oos;	
	private ObjectInputStream ois;
	
		
	private MemberDTO mydto = new MemberDTO();
	private BookDTO dto_for = new BookDTO();
	private SimpleDateFormat dateF = new SimpleDateFormat("YY/MM/dd");
	
	private String id;
	private GuestU g;
	
	public MyPageU(ObjectOutputStream oos, ObjectInputStream ois, MemberDTO data, GuestU g) {
		this.id = data.getId();
		this.oos = oos;
		this.ois = ois;
		this.g = g;
		
		updateRlist();
		run();
		System.out.println("ù �� ����??");
		
		
		
		viewMaker();
		
		mydto = data;
		System.out.println(mydto.getName());
		
		
		
		
		//�̺�Ʈ
		returnbookB.addActionListener(this);
		updateinfoB.addActionListener(this);
		newB.addActionListener(this);
		jtable2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				returnbookB.setEnabled(true);
			}
		});
		
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==returnbookB) {
			RentDTO dto = new RentDTO();
			
			updateRlist();
			run();
			System.out.println("@rlist =========="+rlist);
			int seq = 0;
			int row = jtable2.getSelectedRow();
			String bookName = (String)model.getValueAt(row, 0);
			for(RentDTO data : rlist) {
				System.out.println(data.getId());
				if(data.getId().equals(id)) {
					if(data.getBookName().equals(bookName)) {
						seq = data.getSeq();
					}
				}
			}
			g.setCounting(g.getCounting()-1);
			System.out.println("@@@@@@seq@@@@@" + seq);
			dto.setSeq(seq);
			dto.setCommand("UserDeleteRent");
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			returnbookB.setEnabled(false);
			updateRlist();
			run();
			model.setRowCount(0);
			for(RentDTO data : rlist) {
				updateList(data);
			}
			
			RentDTO dto1 = new RentDTO();
			dto1.setId(id);
			dto1.setCommand("Counting");
			try {
				oos.writeObject(dto1);
				oos.flush();
				dto1 = (RentDTO)ois.readObject();
				dto1.setCounting(dto1.getCounting());
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
			
			
			
		}else if(e.getSource()==updateinfoB) {
			System.out.println("ȸ���������� ��ư Ŭ��!");
						
			try {
				MemberDTO dto = new MemberDTO();
				dto.setCommand("Member");
				oos.writeObject(dto);
				oos.flush();
				run();
				viewmaker2(mydto);
				idT.setText(mydto.getId());
				pwT.setText(mydto.getPw());
				nameT.setText(mydto.getName());
				telT.setText(mydto.getTel());
				emailT.setText(mydto.getEmail());
				mydto.setCommand("NeedMember");
				idT.setEditable(false);
				oos.writeObject(mydto);
				oos.flush();
				run();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			
			
			
		}else if(e.getSource()==updateB2) {
			System.out.println("������ư ����!!!!!!!!!!!111");
						
			try {
				MemberDTO mydto_t = new MemberDTO();
				
				mydto_t.setId(idT.getText());
				mydto_t.setPw(pwT.getText());
				mydto_t.setName(nameT.getText());
				mydto_t.setTel(telT.getText());
				mydto_t.setEmail(emailT.getText());
				mydto_t.setCommand("UserUpdateMember");
				
				oos.writeObject(mydto_t);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		}else if(e.getSource()==dropB) {
			System.out.println("Ż���ư ����!!!!!!!!!!!111");
			int result = JOptionPane.showConfirmDialog(this, "Ż���Ͻðڽ��ϱ�?", 
					"���α׷�����", JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if(result==JOptionPane.OK_OPTION) {
				MemberDTO dtoD = new MemberDTO();
				dtoD.setSeq(mydto.getSeq());
				dtoD.setCommand("DeleteMember");
				try {
					oos.writeObject(dtoD);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				//dasdasdasdasdasdasdasd
				g.getSeatP().exit();
				ExitDTO dto = new ExitDTO();
				
				dto.setExit("Exit");
				try {
					oos.writeObject(dto);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
				
				run();
			}
		}else if(e.getSource()==newB) {
			returnbookB.setEnabled(false);
			updateRlist();
			run();
			model.setRowCount(0);
			for(RentDTO data : rlist) {
				updateList(data);
			}
			
			RentDTO dto = new RentDTO();
			dto.setId(id);
			dto.setCommand("Counting");
			try {
				oos.writeObject(dto);
				oos.flush();
				dto = (RentDTO)ois.readObject();
				g.setCounting(g.getCounting());
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		
		
	}
	
	private void updateRlist() {
		RentDTO runDTO = new RentDTO();
		runDTO.setId(id);
		runDTO.setCommand("UserRentList");
		try {
			oos.writeObject(runDTO);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void viewMaker() {
		//Ÿ��Ʋ
		vector.addElement("������");
		vector.addElement("����");
		vector.addElement("���ǻ�");
		vector.addElement("�帣");
		vector.addElement("�뿩��");
		vector.addElement("�ݳ���");
		vector.addElement("��ü��");
		
		model = new DefaultTableModel(vector,0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
			
		System.out.println("��");
		for(RentDTO data : rlist) {
			updateList(data);
		}
		
		
		rentL = new JLabel("�뿩 ���");
		rentL.setFont(new Font("����",Font.BOLD,25));
		rentL.setBounds(330, 5, 200, 50);
		
		newB = new JButton("���ΰ�ħ");
		newB.setBounds(730, 60, 120, 40);
		
		returnbookB = new JButton("�ݳ�");
		returnbookB.setBounds(730, 120, 120, 40);
		returnbookB.setEnabled(false);
		updateinfoB = new JButton("ȸ����������");
		updateinfoB.setBounds(730, 180, 120, 45);
				
		
		jtable2 = new JTable(model);
		jtable2.setBounds(70, 140, 550, 300);
		
		scroll = new JScrollPane(jtable2);
		scroll.setBounds(60, 60, 650, 350);
		//��ũ�ѹ� �׻� ���� ��
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);//���� ��ũ�ѹ�

		
		add(rentL);
		add(newB);
		add(returnbookB);
		add(updateinfoB);
		add(scroll);
		
				
		setBackground(Color.PINK);
		setLayout(null);
	}
	
	
	
	public void viewmaker2(MemberDTO dto) {
		updateF = new JFrame();
		
		idL = new JLabel("         ID �Է� : ");
		idL.setFont(new Font("����",Font.BOLD,15));
		idL.setBounds(70, 30, 130, 20);
		
		pwL = new JLabel("Password �Է� : ");
		pwL.setFont(new Font("����",Font.BOLD,15));
		pwL.setBounds(70, 100, 130, 20);
		
		nameL = new JLabel("       �̸� �Է� : ");
		nameL.setFont(new Font("����",Font.BOLD,15));
		nameL.setBounds(70, 170, 130, 20);

		telL = new JLabel("  ��ȭ��ȣ �Է� : ");
		telL.setFont(new Font("����",Font.BOLD,15));
		telL.setBounds(70, 240, 130, 20);
		
		emailL = new JLabel("      email �Է� : ");
		emailL.setFont(new Font("����",Font.BOLD,15));
		emailL.setBounds(70, 310, 130, 20);
		
		idT = new JTextField(20);
		idT.setBounds(200, 25, 150, 30);
		
		pwT = new JTextField(20);
		pwT.setBounds(200, 95, 150, 30);
		
		nameT = new JTextField(20);
		nameT.setBounds(200, 165, 150, 30);
		
		telT = new JTextField(20);
		telT.setBounds(200, 235, 150, 30);
		
		emailT = new JTextField(20);
		emailT.setBounds(200, 305, 150, 30);
		
		updateB2 = new JButton("����");
		updateB2.setBounds(130,380,90,40);
		
		dropB = new JButton("Ż��");
		dropB.setBounds(270,380,90,40);
		
		
		Container con = updateF.getContentPane();
		con.setBackground(Color.PINK);
		con.setLayout(null);
		
		
		
		
		con.add(idL);		con.add(pwL);
		con.add(nameL);		con.add(telL);
		con.add(emailL);	con.add(idT);
		con.add(pwT);		con.add(nameT);
		con.add(telT);		con.add(emailT);
		con.add(updateB2);	con.add(dropB);
		
		
		updateF.setBounds(700, 100, 500, 500);
		updateF.setVisible(true);
		updateF.setLayout(null);
		
		//�̺�Ʈ
		updateB2.addActionListener(this);
		dropB.addActionListener(this);
		
	}
	
	private void updateList(RentDTO dataDTO) {
		
		dto_for.setSeq(dataDTO.getSeq());
		System.out.println(dataDTO.getSeq());
		dto_for.setCommand("FindBook");
		try {
			oos.writeObject(dto_for);
			oos.flush();
					
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		run();
				
		Calendar date = Calendar.getInstance();
        date.setTime(dataDTO.getDate());
        date.add(Calendar.DATE, 5);
        String overdue = dateF.format(date.getTime());
		
        Date today = new Date();//���ó�¥
        Date start = dataDTO.getDate();//�뿩��
        Date end  = date.getTime();//�ݳ���
        
        long overdue2 = end.getDate() - today.getDate();
        
        
        
		Vector<String> v = new Vector<String>();
		
		v.addElement(dataDTO.getBookName());
		v.addElement(dto_for.getWriter());
		v.addElement(dto_for.getPublisher());
		v.addElement(dto_for.getGenre());
		v.addElement(dateF.format(dataDTO.getDate()));
		v.addElement(overdue);
		v.addElement(overdue2 + "");
		model.addRow(v);
		
		
	}
	
	
	private void run() {
		BookDTO dtoB = null;
		MemberDTO dtoM = null;
		RentDTO dtoR = null;
		Object temp = null;	//��Ʈ�� ������Ʈ �Ǻ����ؼ�
		System.out.println("run ����");
		
		try {			
			temp = ois.readObject();	
			if(temp.toString().equals("MemberDTO")) {		
				dtoM = (MemberDTO)temp;
			}else if(temp.toString().equals("BookDTO")) {
				dtoB = (BookDTO)temp;
			}else if(temp.toString().equals("RentDTO")) {
				dtoR = (RentDTO)temp;
			}
			
			if(dtoR != null) {
				if(dtoR.getCommand().equals("UserRentList")) {
					System.out.println(dtoR.getBookName());
					rlist = dtoR.getList();
					
				}else if(dtoR.getCommand().equals("DeleteRent")) {
					System.out.println(dtoR.getBookName());
				}
			}
			
			if(dtoB != null) {
				if(dtoB.getCommand().equals("FindBook")) {
					System.out.println("BOOK�޾ƿ�!!!@@@@@@@@@@@@@");
					dto_for = dtoB;
					dtoB = null;
				}
			}
			
			if(dtoM != null) { 
				if(dtoM.getCommand().equals("NeedMember")) {	
					System.out.println("����޾ƿ�");
					dtoM = mydto;
					
					idT.setText(dtoM.getId());
					pwT.setText(dtoM.getPw());
					nameT.setText(dtoM.getName());
					telT.setText(dtoM.getTel());
					emailT.setText(dtoM.getEmail());
								
					System.out.println("�̸���  :   "+dtoM.getEmail());	
					dtoM = null;
							
				}else if(dtoM.getCommand().equals("UserUpdateMember")) {
					System.out.println("������Ʈ ��� �ҷ���");
					
					dtoM = mydto;
					dtoM.setId(idT.getText());
					dtoM.setPw(pwT.getText());
					dtoM.setName(nameT.getText());
					dtoM.setTel(telT.getText());
					dtoM.setEmail(emailT.getText());
					
					System.out.println("@@@@@@������@@@@@");
					JOptionPane.showMessageDialog(this, "�����Ϸ�.");
					updateF.setVisible(false);
					dtoM = null;
					
					
				}/*else if(dtoM.getCommand().equals("DeleteMember")) {
					
					JOptionPane.showMessageDialog(this, "Ż��Ϸ�.");
					if(oos == null || ois == null)System.exit(0);
					try {
						ExitDTO dto = new ExitDTO();
						dto.setExit("Exit");
						oos.writeObject(dto);
						oos.flush();
						System.exit(0);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					dtoM = null;*/
				else if(dtoM.getCommand().equals("Member")) {
					ArrayList<MemberDTO> list = dtoM.getList();
					for(MemberDTO data : list) {
						if(data.getId().equals(this.id)) {
							mydto = data;
						}
					}
				}
				
				
				
			}
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}
}
