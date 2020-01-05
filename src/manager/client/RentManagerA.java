

package manager.client;

import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import manager.bean.BookDTO;
import manager.bean.MemberDTO;
import manager.bean.RentDTO;

public class RentManagerA extends JPanel implements ActionListener {
	private JLabel bookNameL, nameL, idL, telL, emailL, startL, endL, overL;
	private JTextField searchT, bookNameT, nameT, idT, telT, emailT, startT, endT, overT;
	private JButton returnB, mailgoB, refreshB, overdueB, searchB, cancelB;
	private JComboBox<String> combo;
	private JTable table;
	private DefaultTableModel model;
	private JScrollPane scroll;
	private Vector<String> vector = new Vector<String>();
	private ArrayList<RentDTO> rlist;
	private ArrayList<MemberDTO> mlist;

	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private SimpleDateFormat dateF = new SimpleDateFormat("YY/MM/dd");

	public RentManagerA(ObjectOutputStream oos, ObjectInputStream ois) {
		this.oos = oos;
		this.ois = ois;

		viewMaker();
		needAllRentList();
		run();

		searchB.addActionListener(this);
		searchT.addActionListener(this);
		mailgoB.addActionListener(this);
		refreshB.addActionListener(this);
		overdueB.addActionListener(this);
		returnB.addActionListener(this);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 1) {
					System.out.println(table.getSelectedRow());
					int row = table.getSelectedRow();
					String code = (String) table.getValueAt(row, 0);
					for (RentDTO data : rlist) {
						if (data.getSeq() == Integer.parseInt(code)) {
							bookNameT.setText(data.getBookName());
							idT.setText(data.getId());
							nameT.setText(data.getName());
							telT.setText(data.getTel());
							emailT.setText(data.getEmail());
							mailgoB.setEnabled(true);
							returnB.setEnabled(true);

							Calendar date = Calendar.getInstance();
							date.setTime(data.getDate());
							date.add(Calendar.DATE, 5);
							String startS = dateF.format(data.getDate());
							String endS = dateF.format(date.getTime());
							Date today = new Date();// ���ó�¥
							Date end = date.getTime();// �ݳ���
							long overdue = end.getDate() - today.getDate();
							// long overdueTime = overdue / (24 * 60 * 60 * 1000);// ��ü�� ���
							String over = null;
							if (overdue > 0) {
								over = overdue + "�� ���ҽ��ϴ�.";
							} else if (overdue == 0) {
								over = "������ �ݳ��� �Դϴ�.";
							} else if (overdue < 0) {
								over = Math.abs(overdue) + "�� ��ü�Ǿ����ϴ�.";
							}

							startT.setText(startS);
							endT.setText(endS);
							overT.setText(over);
						}
					}
				}
			}
		});
	}

	private void needAllRentList() {
		RentDTO dto = new RentDTO();
		dto.setCommand("RentList");

		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == refreshB) {
			mailgoB.setEnabled(false);
			returnB.setEnabled(false);
			bookNameT.setText("");
			idT.setText("");
			nameT.setText("");
			telT.setText("");
			emailT.setText("");
			startT.setText("");
			endT.setText("");
			overT.setText("");
			searchT.setText("");
			
			int get = combo.getSelectedIndex();
			if(get!=0) {
				combo.setSelectedItem("������") ;
			}

			needAllRentList();
			run();
		} else if (e.getSource() == overdueB) {
			mailgoB.setEnabled(false);
			returnB.setEnabled(false);
			bookNameT.setText("");
			idT.setText("");
			nameT.setText("");
			telT.setText("");
			emailT.setText("");
			startT.setText("");
			endT.setText("");
			overT.setText("");

			RentDTO dto = new RentDTO();
			dto.setCommand("OverdueList");

			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
		} else if (e.getSource() == returnB) {
			int row = table.getSelectedRow();
			String bookname = (String) model.getValueAt(row, 1);
			String name = (String) model.getValueAt(row, 2);

			int result = JOptionPane.showConfirmDialog(this, name + "���� �뿩�Ͻ� " + bookname + "�� �ݳ��Ͻðڽ��ϱ�?", "���� �ݳ�",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (result == JOptionPane.OK_OPTION) {
				RentDTO dto = new RentDTO();
				int seq = Integer.parseInt((String) model.getValueAt(row, 0));
				dto.setSeq(seq);
				dto.setCommand("DeleteRent");

				try {
					oos.writeObject(dto);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				run();
				JOptionPane.showMessageDialog(this, "�ݳ��� �Ϸ�Ǿ����ϴ�.");
				mailgoB.setEnabled(false);
				returnB.setEnabled(false);
			}
		} else if (e.getSource() == mailgoB) {
			int row = table.getSelectedRow();
			String bookName = (String) table.getValueAt(row, 1);
			String name = (String) table.getValueAt(row, 3);
			String date = (String) table.getValueAt(row, 6);
			String code = (String) table.getValueAt(row, 0);

			for (RentDTO data : rlist) {
				if (data.getSeq() == Integer.parseInt(code)) {
					Calendar date2 = Calendar.getInstance();
					date2.setTime(data.getDate());
					date2.add(Calendar.DATE, 5);
					Date today = new Date();// ���ó�¥
					Date end = date2.getTime();// �ݳ���
					long overdue = end.getDate() - today.getDate();
					if(overdue < 0) {
						emailSend(name, bookName, dateF.format(end),overdue);
					}else if(overdue>0){
						JOptionPane.showMessageDialog(this, "�ݳ����� ���� �ƴմϴ�.");
						return;
					}else if(overdue == 0) {
						JOptionPane.showMessageDialog(this, "������ �ݳ��� �Դϴ�.");
						return;
					}
				}
			}
			JOptionPane.showMessageDialog(this, "������ ���½��ϴ�.");
			mailgoB.setEnabled(false);
			returnB.setEnabled(false);
		} else if (e.getSource() == searchB || e.getSource() == searchT) {
			RentDTO dto = new RentDTO();
			dto.setCommand("SearchRent");

			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
		} // �˻�
	}

	private void emailSend(String name, String bookName, String date, long overdue) {
		String user = "chicken.eegg@gmail.com";
		String password = "!Itbank123";

		String content = name + "���� �뿩�Ͻ� " + bookName + "�� �뿩�Ⱓ�� " + date + "���� �Դϴ�. \n"+Math.abs(overdue)+"�� ��ü�Ǿ����� ���ѷ� �ݳ� ��Ź�帳�ϴ�.";

		Properties prop = new Properties();
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", 465);
		prop.put("mail.smtp.auth", "true");
		prop.put("mail.smtp.ssl.enable", "true");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

		Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});

		try {
			MimeMessage msg = new MimeMessage(session);// �޼���
			msg.setFrom(new InternetAddress(user));// �������
			msg.addRecipients(Message.RecipientType.TO, user);// �������
			msg.setSubject("å �ݳ� ��û"); // ���� ������ �Է�
			msg.setText(content); // ���� ������ �Է�
			Transport.send(msg); //// ����
			System.out.println("message sent successfully...");
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public void updateList(RentDTO data) throws ParseException {
		Calendar date = Calendar.getInstance();
		date.setTime(data.getDate());
		date.add(Calendar.DATE, 5);
		String overdue = dateF.format(date.getTime());

		Vector<String> v = new Vector<String>();
		v.addElement(data.getSeq() + "");
		v.addElement(data.getName());
		v.addElement(data.getId());
		v.addElement(data.getBookName());
		v.addElement(data.getTel());
		v.addElement(data.getEmail());
		v.addElement(dateF.format(data.getDate()));
		v.addElement(overdue);
		model.addRow(v);
	}

	public void overdueList(RentDTO data) throws ParseException {
		Calendar date = Calendar.getInstance();
		date.setTime(data.getDate());
		date.add(Calendar.DATE, 5);

		Date today = new Date();// ���ó�¥
		Date start = data.getDate();// �뿩��
		Date end = date.getTime();// �ݳ���

		long overdue = end.getTime() - today.getTime();
		long overdueTime = overdue / (24 * 60 * 60 * 1000);// ��ü�� ���

		if (overdueTime < 0) {
			Vector<String> v = new Vector<String>();
			v.addElement(data.getSeq() + "");
			v.addElement(data.getName());
			v.addElement(data.getId());
			v.addElement(data.getBookName());
			v.addElement(data.getTel());
			v.addElement(data.getEmail());
			v.addElement(dateF.format(data.getDate()));
			v.addElement(dateF.format(date.getTime()));
			model.addRow(v);
		}
	}

	public void viewMaker() {
		setLayout(null);

		bookNameL = new JLabel("å�̸�");
		nameL = new JLabel("�̸�");
		idL = new JLabel("���̵�");
		telL = new JLabel("����ó");
		emailL = new JLabel("�̸���");
		startL = new JLabel("�뿩��");
		endL = new JLabel("�ݳ���");
		overL = new JLabel("��ü��");

		searchT = new JTextField();
		bookNameT = new JTextField();
		bookNameT.setEditable(false);
		nameT = new JTextField();
		nameT.setEditable(false);
		idT = new JTextField();
		idT.setEditable(false);
		telT = new JTextField();
		telT.setEditable(false);
		emailT = new JTextField();
		emailT.setEditable(false);
		startT = new JTextField();
		startT.setEditable(false);
		endT = new JTextField();
		endT.setEditable(false);
		overT = new JTextField();
		overT.setEditable(false);

		refreshB = new JButton("���ΰ�ħ");
		returnB = new JButton("�ݳ��ϱ�");
		mailgoB = new JButton("���� ������");
		overdueB = new JButton("��ü���");
		searchB = new JButton("�˻�");

		vector.addElement("�����ڵ�");
		vector.addElement("������");
		vector.addElement("�̸�");
		vector.addElement("ȸ��ID");
		vector.addElement("��ȭ��ȣ");
		vector.addElement("�̸���");
		vector.addElement("�뿩��");
		vector.addElement("�ݳ���");

		model = new DefaultTableModel(vector, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			}
		};
		String book[] = { "������", "�̸�", "���̵�" };
		combo = new JComboBox<String>(book);
		DefaultListCellRenderer dicr = new DefaultListCellRenderer();
		dicr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
		combo.setRenderer(dicr);

		JPanel p = new JPanel();
		p.setLayout(null);

		table = new JTable(model);
		
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
		dtcr.setHorizontalAlignment(JLabel.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(1).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(2).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(3).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(4).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(5).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(6).setCellRenderer(dtcr);
		table.getColumnModel().getColumn(7).setCellRenderer(dtcr);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(60);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(60);
		table.getColumnModel().getColumn(3).setPreferredWidth(60);
		table.getColumnModel().getColumn(4).setPreferredWidth(100);
		table.getColumnModel().getColumn(5).setPreferredWidth(120);
		table.getColumnModel().getColumn(6).setPreferredWidth(60);
		table.getColumnModel().getColumn(7).setPreferredWidth(60);
		
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);

		
		scroll = new JScrollPane(table);

		// ��ġ
		combo.setBounds(25, 20, 80, 25);
		searchT.setBounds(115, 20, 150, 25);
		searchB.setBounds(275, 20, 70, 25);
		scroll.setBounds(25, 55, 600, 375);

		// �г���ġ
		bookNameL.setBounds(10, 45, 50, 15);
		bookNameT.setBounds(10, 60, 330, 25);
		nameL.setBounds(10, 90, 30, 15);
		nameT.setBounds(10, 105, 330, 25);
		idL.setBounds(10, 135, 80, 15);
		idT.setBounds(10, 150, 330, 25);
		telL.setBounds(10, 180, 50, 15);
		telT.setBounds(10, 195, 330, 25);
		emailL.setBounds(10, 225, 60, 15);
		emailT.setBounds(10, 240, 330, 25);

		startL.setBounds(10, 290, 50, 15);
		startT.setBounds(10, 305, 160, 25);
		endL.setBounds(180, 290, 50, 15);
		endT.setBounds(180, 305, 160, 25);
		overL.setBounds(10, 330, 50, 15);
		overT.setBounds(10, 345, 330, 25);

		refreshB.setBounds(240, 10, 100, 25);
		overdueB.setBounds(135, 10, 100, 25);
		returnB.setBounds(240, 395, 100, 25);
		mailgoB.setBounds(10, 395, 110, 25);

		add(combo);
		add(searchT);
		add(searchB);
		add(scroll);
		add(p);

		p.add(bookNameL);
		p.add(bookNameT);
		p.add(nameL);
		p.add(nameT);
		p.add(idL);
		p.add(idT);
		p.add(telL);
		p.add(telT);
		p.add(emailL);
		p.add(emailT);
		p.add(startL);
		p.add(startT);
		p.add(endL);
		p.add(endT);
		p.add(overL);
		p.add(overT);
		p.add(returnB);
		p.add(mailgoB);
		p.add(refreshB);
		p.add(overdueB);

		setBackground(new Color(153,214,234));

		p.setBackground(new Color(153,214,234));
		p.setBounds(640, 10, 360, 420);
	}

	public void run() {
		RentDTO dtoR = null;
		Object temp = null;
		try {
			temp = this.ois.readObject();
			if (temp.toString().equals("RentDTO")) {
				dtoR = (RentDTO) temp;
			}

			if (dtoR != null) {
				if (dtoR.getCommand().equals("RentList")) {
					model.setRowCount(0);
					rlist = dtoR.getList();
					for (RentDTO data : rlist) {
						try {
							updateList(data);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}

				} else if (dtoR.getCommand().equals("OverdueList")) {
					model.setRowCount(0);
					rlist = dtoR.getList();

					for (RentDTO data : rlist) {
						try {
							overdueList(data);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} // for
				} else if (dtoR.getCommand().equals("SearchRent")) {
					if (combo.getSelectedIndex() == 0) {// ������
						model.setRowCount(0);
						rlist = dtoR.getList();
						String[] ar = new String[] { searchT.getText() };
						for (RentDTO data : rlist) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getBookName().matches("(?i).*" + ar[i] + ".*")) {
									try {
										updateList(data);
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} else if (combo.getSelectedIndex() == 1) {// �̸�
						model.setRowCount(0);
						rlist = dtoR.getList();
						String[] ar = new String[] { searchT.getText() };
						for (RentDTO data : rlist) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getName().matches("(?i).*" + ar[i] + ".*")) {
									try {
										updateList(data);
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							}
						}
					} else if (combo.getSelectedIndex() == 2) {// ���̵�
						model.setRowCount(0);
						rlist = dtoR.getList();
						String[] ar = new String[] { searchT.getText() };
						for (RentDTO data : rlist) {
							for (int i = 0; i < ar.length; i++) {
								if (data.getId().matches("(?i).*" + ar[i] + ".*")) {
									try {
										updateList(data);
									} catch (ParseException e) {
										e.printStackTrace();
									}
								}
							}
						}
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

