package manager.client;

import java.awt.Color;
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
import manager.bean.MemberDTO;

public class UserManagerA extends JPanel implements ActionListener{
	private JLabel idL, nameL ,pwL, telL, emailL, listL, searchL, areaL;
	private JTextField guests , ids, names, pws, tels, emails, codes;
	private JButton searchB, deleteB, updateB, overviewB, refreshB; 
	private JComboBox<String> guest;
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scroll, scroll2;
	private Vector<String> vector = new Vector<String>();
	private ArrayList<MemberDTO> list;
	private JTextArea area;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public UserManagerA(ObjectOutputStream oos, ObjectInputStream ois) {
		this.oos = oos;
		this.ois = ois;
		
		//프레임 설정
		viewMaker();
		
		//모든 멤버리스트 요청
		needAllMemberList();
		
		//요청 결과 받아온다
		run();
		
		//이벤트
		searchB.addActionListener(this);
		deleteB.addActionListener(this);
		updateB.addActionListener(this);
		overviewB.addActionListener(this);
		refreshB.addActionListener(this);
		guests.addActionListener(this);
		//clearB.addActionListener(this);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					System.out.println(table.getSelectedRow());
					int row = table.getSelectedRow();
					String code = (String) table.getValueAt(row, 0);
					for(MemberDTO data : list) {
						if(data.getSeq()==Integer.parseInt(code)) {
							codes.setText(data.getSeq()+"");
							ids.setText(data.getId());
							names.setText(data.getName());
							pws.setText(data.getPw());
							tels.setText(data.getTel());
							emails.setText(data.getEmail());
							updateB.setEnabled(true);
							deleteB.setEnabled(true);
							
						}
						
					}
				}
			}
		});
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == searchB || e.getSource()==guests) {
			MemberDTO dto = new MemberDTO();
			dto.setCommand("SearchMember");
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
		
		}else if(e.getSource()==deleteB) {
			MemberDTO dto = new MemberDTO();
			dto.setSeq(Integer.parseInt(codes.getText()));
			dto.setCommand("DeleteMember");
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		}else if(e.getSource()==updateB) {
			MemberDTO dto = new MemberDTO();
			dto.setSeq(Integer.parseInt(codes.getText()));
			dto.setId(ids.getText());
			dto.setPw(pws.getText());
			dto.setName(names.getText());
			dto.setTel(tels.getText());
			dto.setEmail(emails.getText());
			dto.setCommand("UpdateMember");
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		}else if(e.getSource()==overviewB) {
			MemberDTO dto = new MemberDTO();
			dto.setCommand("SearchMember");
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		}else if (e.getSource()==refreshB) {
			guests.setText("");
			ids.setText("");
			names.setText("");
			pws.setText("");
			tels.setText("");
			emails.setText("");
			
			int get = guest.getSelectedIndex();
			if(get!=0) {
				guest.setSelectedItem("ID") ;
			}
			
			deleteB.setEnabled(false);
			updateB.setEnabled(false);
			
			needAllMemberList();
			run();
		}
		
	}

	private void viewMaker() {
		setLayout(null);
		
		//JLabel
		idL = new JLabel("ID");
		pwL = new JLabel("비밀번호");
		nameL = new JLabel("이름");
		telL = new JLabel("연락처");
		emailL = new JLabel("이메일");
		listL = new JLabel("회원목록");
		searchL = new JLabel("검색");
		areaL = new JLabel("메모사항");

		
		//JButton
		updateB = new JButton("수정");
		deleteB = new JButton("삭제");
		searchB = new JButton("검색");
		overviewB = new JButton("전체보기");
		refreshB = new JButton("새로고침");
		
		//JTextField
		ids = new JTextField(); 
		names = new JTextField();
		pws = new JTextField();
		tels = new JTextField();
		emails = new JTextField();
		guests = new JTextField();
		codes = new JTextField();
		
		ids.setEditable(false);
		
		//combobox
		String info[] = {"ID","이  름","연락처","이메일"};
		guest = new JComboBox<String>(info);
		DefaultListCellRenderer dicr = new DefaultListCellRenderer();
		dicr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		guest.setRenderer(dicr);
		
		area = new JTextArea();
		scroll2 = new JScrollPane(area);
		
		//list
		vector.addElement("회원코드");
		vector.addElement("ID");
		vector.addElement("비밀번호");
		vector.addElement("이름");
		vector.addElement("연락처");
		vector.addElement("이메일");
		
		model = new DefaultTableModel(vector,0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		
		table = new JTable(model);
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(1).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(2).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(3).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(4).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(5).setCellRenderer(dtcr);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(60);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setPreferredWidth(120);
		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scroll = new JScrollPane(table);
		
		//위치
		guest.setBounds(25, 20, 80, 25);
		guests.setBounds(115, 20, 150, 25);
		searchB.setBounds(275, 20, 70, 25);
		scroll.setBounds(25, 55, 600, 375);
		
		JPanel p = new JPanel();
		p.setLayout(null);

		idL.setBounds(10, 30, 50, 15);
		ids.setBounds(10, 45, 330, 25);
		nameL.setBounds(10, 75, 30, 15);
		names.setBounds(10, 90, 330, 25);
		pwL.setBounds(10, 120, 80, 15);
		pws.setBounds(10, 135, 330, 25);
		telL.setBounds(10, 165, 50, 15);
		tels.setBounds(10, 180, 330, 25);
		emailL.setBounds(10, 210, 60, 15);
		emails.setBounds(10, 225, 330, 25);
		areaL.setBounds(10, 255, 60, 15);
		scroll2.setBounds(10, 270, 330, 120);
		
		
		//searchL.setBounds(100,100,30,25);
		//listL.setBounds(60, 75, 70, 25);
		updateB.setBounds(240, 395, 100, 25);
		refreshB.setBounds(240, 10, 100, 25);
		deleteB.setBounds(10, 395, 100, 25);
		//clearB.setBounds(490, 385, 130, 25);
		//overviewB.setBounds(490, 350, 130, 25);
		
		
		add(guest);		add(guests);
		add(overviewB);	add(searchB);	
		add(scroll);	add(listL);		
		add(searchL);	add(p);
		
		p.add(idL);		p.add(ids);		
		p.add(nameL);	p.add(names);	
		p.add(pwL);		p.add(pws);
		p.add(telL);	p.add(tels);	
		p.add(emailL);	p.add(emails);	
		p.add(updateB);	p.add(refreshB);
		p.add(deleteB);	p.add(scroll2);
		p.add(areaL);	

		
		p.setBackground(new Color(153,214,234));
		p.setBounds(640, 10, 360, 420);
		
		setBackground(new Color(153,214,234));
		setBounds(700,100,900,500);
		setVisible(true);
		
	}

	private void needAllMemberList() {
		MemberDTO dto = new MemberDTO();
		dto.setCommand("Member");
		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateList(MemberDTO data) {
		Vector<String> v = new Vector<String>();
		v.addElement(data.getSeq()+"");
		v.addElement(data.getId());
		v.addElement(data.getPw());
		v.addElement(data.getName());
		v.addElement(data.getTel());
		v.addElement(data.getEmail());
		model.addRow(v);
	}

	
	
	public void run() {
		MemberDTO dtoM = null;
		Object temp = null;
		try {
			temp = ois.readObject();
			if(temp.toString().equals("MemberDTO")) {
				dtoM = (MemberDTO) temp;
			}
			if(dtoM !=null) {
				if(dtoM.getCommand().equals("Member")||dtoM.getCommand().equals("AddMember")) {
					model.setRowCount(0);
					list = dtoM.getList();
					for(MemberDTO data : list) {
						updateList(data);
					}
				}else if(dtoM.getCommand().equals("UpdateMember")) {
					model.setRowCount(0);
					list = dtoM.getList();
					for(MemberDTO data : list) {
						updateList(data);
					}
				}else if(dtoM.getCommand().equals("SearchMember")) {
					if(guest.getSelectedIndex()==0) {//아이디
						model.setRowCount(0);
						list = dtoM.getList();
						String[] ar = new String[] { guests.getText() };
						for (MemberDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getId().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					}else if(guest.getSelectedIndex()==1) {//이름
						model.setRowCount(0);
						list = dtoM.getList();
						String[] ar = new String[] { guests.getText() };
						for (MemberDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getName().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					}else if(guest.getSelectedIndex()==2) {//연락처
						model.setRowCount(0);
						list = dtoM.getList();
						String[] ar = new String[] { guests.getText() };
						for (MemberDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getTel().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					}else if(guest.getSelectedIndex()==3) {//이메일
						model.setRowCount(0);
						list = dtoM.getList();
						String[] ar = new String[] { guests.getText() };
						for (MemberDTO data : list) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getEmail().matches("(?i).*" + ar[i] + ".*")) {
									updateList(data);
								}
							}
						}
					}
				}else if(dtoM.getCommand().equals("DeleteMember")) {
					model.setRowCount(0);
					list = dtoM.getList();
					for(MemberDTO data : list) {
						updateList(data);
					}
				}
				
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

