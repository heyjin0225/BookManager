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
	
	private String num; //인증번호
	private boolean idCheck;//id중복체크
	private boolean pwCheck;//패스워드 체크
	private boolean sendCheck;//이메일 발송 여부 
	private boolean emailCheck;//이메일 인증 여부
	private boolean signCheck;//회원가입 성공여부
	private boolean emptyCheck;//공백 체크
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ArrayList<MemberDTO> list;
	
	public SignM(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
		setTitle("회원가입");
		
		idL = new JLabel("아이디  ");
		idT = new JTextField(10);
		idB = new JButton("중복체크");
		pwL = new JLabel("비밀번호  ");
		pwT = new JPasswordField(20);
		pw2L = new JLabel("재입력");
		pw2T = new JPasswordField(20);
		nameL = new JLabel("이름  ");
		nameT = new JTextField(20);
		telL = new JLabel("전화번호  ");
		telT = new JTextField(20);
		emailL = new JLabel("이메일  ");
		emailT = new JTextField(20);
		checkL = new JLabel("인증번호");
		checkT = new JTextField(20);
		sendB = new JButton("인증번호보내기");
		checkB = new JButton("인증");
		signB = new JButton("가입하기");
		cancelB = new JButton("취소");
		
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
	public void actionPerformed(ActionEvent e) {//보내는쪽
		String pw = new String(pwT.getPassword());
		String pw2 = new String(pw2T.getPassword());
		
		if(e.getSource()==idB||e.getSource()==idT) {
			if(idT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
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
				JOptionPane.showMessageDialog(this, "중복 체크 먼저 해주세요.");
				return;
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력해주세요.");
				return;
			}else if(!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
				return;
			}else if(nameT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "이름을 입력해주세요.");
				return;
			}else if(telT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "전화번호를 입력해주세요.");
				return;
			}else if(emailT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "이메일을 입력해주세요.");
				return;
			}else {
				this.num = randomNum();
				emailSend(num);
			}
			
		}else if(e.getSource()==checkB || e.getSource()==checkT) {
			emailCheck = false;//인증 체크여부
			
			if(idCheck==false) {
				JOptionPane.showMessageDialog(this, "중복 체크 먼저 해주세요.");
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력해주세요.");
				return;
			}else if(!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
			}else if(sendCheck==false) {
				JOptionPane.showMessageDialog(this, "이메일 인증을 해주세요.");
			}else {
				if (num.equals(checkT.getText())) {
					emailCheck = true;
					checkT.setEditable(false);
					JOptionPane.showMessageDialog(this, "인증 성공.");
				} else {
					emailCheck = false;
					JOptionPane.showMessageDialog(this, "인증 실패.");
				}
			}
		}else if(e.getSource()==signB) {
			emptyCheck = false;
			
			if(idCheck==false) {
				JOptionPane.showMessageDialog(this, "중복 체크 먼저 해주세요.");
				return;
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력해주세요.");
				return;
			}else if(!pw.equals(pw2)) {
				JOptionPane.showMessageDialog(this, "비밀번호가 일치하지 않습니다.");
			}else {
				if(emailCheck==false) {
					JOptionPane.showMessageDialog(this, "이메일 인증을 해주세요.");
					return;
				}else {
					if(idT.getText().equals("") || pw.equals("") || pw2.equals("") || nameT.getText().equals("")
							|| emailT.getText().equals("") || telT.getText().equals("")) {
						JOptionPane.showMessageDialog(this, "모든칸을 입력해주세요.");
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
	}//액션이벤트

	
	public void run() {
		
		try {
			MemberDTO dto = (MemberDTO) ois.readObject();//회원가입여부
			if(dto.getCommand().equals("Member")) {
				list = dto.getList();
				if(list!=null) {
					idCheck = false;
					for(MemberDTO data : list) {
						if(data.getId().equals(idT.getText())) {
							idCheck = false;
							JOptionPane.showMessageDialog(this, "중복되는 아이디있음.");
							return;
						}else {
							idCheck = true;
						}
					}//for
					if(idCheck==true) 
						idT.setEditable(false);
						JOptionPane.showMessageDialog(this, "중복 체크 완료.");
						list=null;
				}
			}else if(dto.getCommand().equals("Join")) {
					JOptionPane.showMessageDialog(this, "가입이 완료되었습니다.");
					this.dispose();
				
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void emailSend(String random) {// 인증번호 발송
		String user = "chicken.eegg@gmail.com";
		String password = "!Itbank123";
		num = random;
		String content = "인증번호는 [" + num + "] 입니다.";

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
			MimeMessage msg = new MimeMessage(session);//메세지
			msg.setFrom(new InternetAddress(user));//보낼사람
			msg.addRecipients(Message.RecipientType.TO, user);//받을사람
			msg.setSubject("관리프로그램 인증번호"); // 메일 제목을 입력
			msg.setText(content); // 메일 내용을 입력
			Transport.send(msg); //// 전송
			sendCheck = true;
			JOptionPane.showMessageDialog(this, "이메일을 보냈습니다.");
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	public String randomNum() {// 인증번호 생성
		StringBuffer random = new StringBuffer();
		for (int i = 0; i <= 5; i++) {
			int n = (int) (Math.random() * 10);
			random.append(n);
		}
		return random.toString();
	}
	
	public MemberDTO dataDTO() {// dto에 입력
		
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
