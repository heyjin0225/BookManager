package manager.client;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import manager.bean.MemberDTO;

public class SignM extends JFrame implements ActionListener{
	private JLabel idL, pwL, pw2L, nameL, telL, emailL, checkL;
	private JTextField idT, nameT, telT, emailT, checkT;
	private JPasswordField pwT, pw2T;
	private JButton idB, sendB, checkB, signB, cancelB ;
	
	private String num; //������ȣ
	private boolean idCheck;//id�ߺ�üũ
	private boolean pwCheck;//�н����� üũ
	private boolean sendCheck;//�̸��� �߼� ���� 
	private boolean emailCheck;//�̸��� ���� ����
	private boolean signCheck;//ȸ������ ��������
	private boolean emptyCheck;//���� üũ
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ArrayList<MemberDTO> list;
	
	public SignM(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
		setTitle("ȸ������");
		
		idL = new JLabel("���̵�  ");
		idT = new JTextField(10);
		idB = new JButton("�ߺ�üũ");
		pwL = new JLabel("��й�ȣ  ");
		pwT = new JPasswordField(20);
		pw2L = new JLabel("���Է�");
		pw2T = new JPasswordField(20);
		nameL = new JLabel("�̸�  ");
		nameT = new JTextField(20);
		telL = new JLabel("��ȭ��ȣ  ");
		telT = new JTextField(20);
		emailL = new JLabel("�̸���  ");
		emailT = new JTextField(20);
		checkL = new JLabel("������ȣ");
		checkT = new JTextField(20);
		sendB = new JButton("������ȣ������");
		checkB = new JButton("����");
		signB = new JButton("�����ϱ�");
		cancelB = new JButton("���");
		
		idL.setBounds(10, 10, 60, 25);
		idT.setBounds(80, 10, 210, 25);
		idB.setBounds(10, 45, 280, 25);
		pwL.setBounds(10, 80, 60, 25);
		pwT.setBounds(80, 80, 210, 25);
		pw2L.setBounds(10, 115, 60, 25);
		pw2T.setBounds(80, 115, 210, 25);
		nameL.setBounds(10, 150, 60, 25);
		nameT.setBounds(80, 150, 210, 25);
		telL.setBounds(10, 185, 60, 25);
		telT.setBounds(80, 185, 210, 25);
		emailL.setBounds(10, 220, 60, 25);
		emailT.setBounds(80, 220, 210, 25);
		sendB.setBounds(10, 255, 280, 25);
		checkL.setBounds(10, 290, 60, 25);
		checkT.setBounds(80, 290, 210, 25);
		checkB.setBounds(10, 325, 280, 25);
		signB.setBounds(10, 360, 135, 50);
		cancelB.setBounds(150, 360, 135, 50);
		
		Container con = getContentPane();
		con.setLayout(null);
		con.add(idL); con.add(idT); 
		con.add(pwL); con.add(pwT);
		con.add(pw2L); con.add(pw2T);
		con.add(nameL); con.add(nameT);
		con.add(telL); con.add(telT);
		con.add(emailL); con.add(emailT);
		con.add(checkL); con.add(checkT);
		con.add(sendB); con.add(idB);
		con.add(checkB); con.add(signB);
		con.add(cancelB);
		
		setBounds(700, 200, 300, 450);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		
		idT.addActionListener(this);
		idB.addActionListener(this);
		sendB.addActionListener(this);
		checkT.addActionListener(this);
		checkB.addActionListener(this);
		signB.addActionListener(this);
		cancelB.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {//��������
		String pw = new String(pwT.getPassword());
		String pw2 = new String(pw2T.getPassword());
		
		if(e.getSource()==idB||e.getSource()==idT) {
			if(idT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "���̵� �Է����ּ���.");
				return;
			}else {
				MemberDTO dto = new MemberDTO();
				dto.setCommand("Member");
				
				try {
					oos.writeObject(dto);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				run();
			}
		}else if(e.getSource()==sendB) {
			if(idCheck==false) {
				JOptionPane.showMessageDialog(this, "�ߺ� üũ ���� ���ּ���.");
				return;
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� �Է����ּ���.");
				return;
			}else if(!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
				return;
			}else if(nameT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "�̸��� �Է����ּ���.");
				return;
			}else if(telT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "��ȭ��ȣ�� �Է����ּ���.");
				return;
			}else if(emailT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "�̸����� �Է����ּ���.");
				return;
			}else {
				this.num = randomNum();
				emailSend(num);
			}
			
		}else if(e.getSource()==checkB || e.getSource()==checkT) {
			emailCheck = false;//���� üũ����
			
			if(idCheck==false) {
				JOptionPane.showMessageDialog(this, "�ߺ� üũ ���� ���ּ���.");
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� �Է����ּ���.");
				return;
			}else if(!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
			}else if(sendCheck==false) {
				JOptionPane.showMessageDialog(this, "�̸��� ������ ���ּ���.");
			}else {
				if (num.equals(checkT.getText())) {
					emailCheck = true;
					checkT.setEditable(false);
					JOptionPane.showMessageDialog(this, "���� ����.");
				} else {
					emailCheck = false;
					JOptionPane.showMessageDialog(this, "���� ����.");
				}
			}
		}else if(e.getSource()==signB) {
			emptyCheck = false;
			
			if(idCheck==false) {
				JOptionPane.showMessageDialog(this, "�ߺ� üũ ���� ���ּ���.");
				return;
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� �Է����ּ���.");
				return;
			}else if(!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
			}else {
				if(emailCheck==false) {
					JOptionPane.showMessageDialog(this, "�̸��� ������ ���ּ���.");
					return;
				}else {
					if(idT.getText().equals("") || pw.equals("") || pw2.equals("") || nameT.getText().equals("")
							|| emailT.getText().equals("") || telT.getText().equals("")) {
						JOptionPane.showMessageDialog(this, "���ĭ�� �Է����ּ���.");
						return;
					}else {
						MemberDTO dto = dataDTO();
						dto.setCommand("Join");
						try {
							oos.writeObject(dto);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						run();
						
					}
				}
			}
		}else if(e.getSource()==cancelB) {
			this.dispose();
		}
	}//�׼��̺�Ʈ

	
	public void run() {
		
		try {
			MemberDTO dto = (MemberDTO) ois.readObject();//ȸ�����Կ���
			if(dto.getCommand().equals("Member")) {
				list = dto.getList();
				if(list!=null) {
					idCheck = false;
					for(MemberDTO data : list) {
						if(data.getId().equals(idT.getText())) {
							idCheck = false;
							JOptionPane.showMessageDialog(this, "�ߺ��Ǵ� ���̵�����.");
							return;
						}else {
							idCheck = true;
						}
					}//for
					if(idCheck==true) 
						idT.setEditable(false);
						JOptionPane.showMessageDialog(this, "�ߺ� üũ �Ϸ�.");
						list=null;
				}
			}else if(dto.getCommand().equals("Join")) {
					JOptionPane.showMessageDialog(this, "������ �Ϸ�Ǿ����ϴ�.");
					this.dispose();
				
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void emailSend(String random) {// ������ȣ �߼�
		String user = "chicken.eegg@gmail.com";
		String password = "!Itbank123";
		num = random;
		String content = "������ȣ�� [" + num + "] �Դϴ�.";

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
			MimeMessage msg = new MimeMessage(session);//�޼���
			msg.setFrom(new InternetAddress(user));//�������
			msg.addRecipients(Message.RecipientType.TO, user);//�������
			msg.setSubject("�������α׷� ������ȣ"); // ���� ������ �Է�
			msg.setText(content); // ���� ������ �Է�
			Transport.send(msg); //// ����
			sendCheck = true;
			JOptionPane.showMessageDialog(this, "�̸����� ���½��ϴ�.");
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public String randomNum() {// ������ȣ ����
		StringBuffer random = new StringBuffer();
		for (int i = 0; i <= 5; i++) {
			int n = (int) (Math.random() * 10);
			random.append(n);
		}
		return random.toString();
	}
	
	public MemberDTO dataDTO() {// dto�� �Է�
		
		MemberDTO dto = new MemberDTO();
		String id = idT.getText();
		String pw = new String(pwT.getPassword());
		String name = nameT.getText();
		String tel = telT.getText();
		String email = emailT.getText();

		dto.setId(id);
		dto.setPw(pw);
		dto.setName(name);
		dto.setTel(tel);
		dto.setEmail(email);

		return dto;
	}

	
	
}
