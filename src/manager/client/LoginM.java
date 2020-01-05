package manager.client;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import manager.bean.ExitDTO;
import manager.bean.MemberDTO;
import manager.server.Server;


public class LoginM extends JFrame implements ActionListener{
	
	private ImageIcon title;
	private JLabel titleL;
	private JLabel idL ,pwL;
	private JTextField idT;
	private JPasswordField pwT;
	private JButton loginB,signB,findIdB,findPwB,exitB;
	
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private ArrayList<MemberDTO> list;
	private boolean check;
	

	public LoginM() {
		setTitle("도서관리 프로그램");
		
		title = new ImageIcon("lib/Title.jpg");
		titleL = new JLabel(title);
		titleL.setBounds(0,0,JFrame.WIDTH, JFrame.HEIGHT);
		
		idL = new JLabel("아이디",JLabel.CENTER);
		idT = new JTextField("", 15);
		
		pwL = new JLabel("비밀번호",JLabel.CENTER);
		pwT = new JPasswordField("",15); 
		//pwT.setEchoChar('*');
		
		loginB = new JButton("로그인");
		signB = new JButton("회원가입");
		findIdB = new JButton("아이디찾기");
		findPwB = new JButton("비밀번호찾기");
		exitB = new JButton("종료");

		JPanel jp1 = new JPanel();
		jp1.add(idL); jp1.add(idT);
		jp1.add(pwL); jp1.add(pwT);
		
		JPanel jp2 = new JPanel(new GridLayout(1, 5,5,5));
		jp2.setSize(JFrame.WIDTH, 200);
		jp2.add(loginB); jp2.add(signB);
		jp2.add(findIdB); jp2.add(findPwB); 
		jp2.add(exitB);
		
		Container con = getContentPane();
		con.setLayout(new FlowLayout());
		con.add(titleL);
		con.add(jp1); con.add(jp2);
		
		setBounds(700, 200, 600, 450);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(LoginM.this, "종료하시겠습니까?", 
															"프로그램종료", JOptionPane.OK_CANCEL_OPTION, 
															JOptionPane.QUESTION_MESSAGE);
				if(result==JOptionPane.OK_OPTION) {
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
				}
			}
		});
		
		pwT.addActionListener(this);
		loginB.addActionListener(this);
		signB.addActionListener(this);
		findIdB.addActionListener(this);
		findPwB.addActionListener(this);
		exitB.addActionListener(this);
		
	}//생성자

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exitB) {
			int result = JOptionPane.showConfirmDialog(LoginM.this, "종료하시겠습니까?", 
					"게임종료", JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if(result==JOptionPane.OK_OPTION) {
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
			}
		}else if(e.getSource()==loginB || e.getSource()==pwT) {
			String pw = new String(pwT.getPassword());
			if(idT.getText().equals("")) {
				JOptionPane.showMessageDialog(this, "아이디를 입력해주세요.");
				return;
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "비밀번호를 입력해주세요.");
				return;
			}
			
			MemberDTO dto = new MemberDTO();
			dto.setCommand("Member");
			
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			run();
			
		}else if(e.getSource()==signB) {
			new SignM(socket,oos,ois);
		}else if(e.getSource()==findIdB) {
			new FindIdM(socket, oos, ois);
		}else if(e.getSource()==findPwB) {
			new FindPwM(socket, oos, ois);
		}
	}
	
	private void service() {
		
		try {
			socket = new Socket(Server.IP, 9500);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		//받을때
		
		try {
			MemberDTO dto = (MemberDTO) ois.readObject();//회원가입여부
			if(dto.getCommand().equals("Member")) {
				list = dto.getList();
				if(list!=null) System.out.println("리스트 받음");
				else System.out.println("리스트 못받음");
				check = true;//없는아이디
				String pw = new String(pwT.getPassword());//패스워드값
				
				for(MemberDTO data : list) {
					if(data.getId().equals(idT.getText())) {
						if(data.getPw().equals(pw)) {
							if(data.getId().equals("admin")) {
								JOptionPane.showMessageDialog(this, "관리자 로그인.");
								new AdminA(oos,ois, data.getId());
								this.dispose();
								return;
							}else {
								JOptionPane.showMessageDialog(this, "사용자 로그인.");
								new GuestU(oos,ois, data);
								this.dispose();
								return;
							}
							
						}else {
							JOptionPane.showMessageDialog(this, "비밀번호를 다시 입력하세요.");
							return;
						}
					}else {
						check = false;
					}
				}//for
				if(check==false) JOptionPane.showMessageDialog(this, "없는 아이디 입니다.");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new LoginM().service();
	}


}

