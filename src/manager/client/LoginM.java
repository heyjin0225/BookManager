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
		setTitle("�������� ���α׷�");
		
		title = new ImageIcon("lib/Title.jpg");
		titleL = new JLabel(title);
		titleL.setBounds(0,0,JFrame.WIDTH, JFrame.HEIGHT);
		
		idL = new JLabel("���̵�",JLabel.CENTER);
		idT = new JTextField("", 15);
		
		pwL = new JLabel("��й�ȣ",JLabel.CENTER);
		pwT = new JPasswordField("",15); 
		//pwT.setEchoChar('*');
		
		loginB = new JButton("�α���");
		signB = new JButton("ȸ������");
		findIdB = new JButton("���̵�ã��");
		findPwB = new JButton("��й�ȣã��");
		exitB = new JButton("����");

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
				int result = JOptionPane.showConfirmDialog(LoginM.this, "�����Ͻðڽ��ϱ�?", 
															"���α׷�����", JOptionPane.OK_CANCEL_OPTION, 
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
		
	}//������

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exitB) {
			int result = JOptionPane.showConfirmDialog(LoginM.this, "�����Ͻðڽ��ϱ�?", 
					"��������", JOptionPane.OK_CANCEL_OPTION, 
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
				JOptionPane.showMessageDialog(this, "���̵� �Է����ּ���.");
				return;
			}else if(pw.equals("")) {
				JOptionPane.showMessageDialog(this, "��й�ȣ�� �Է����ּ���.");
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
		//������
		
		try {
			MemberDTO dto = (MemberDTO) ois.readObject();//ȸ�����Կ���
			if(dto.getCommand().equals("Member")) {
				list = dto.getList();
				if(list!=null) System.out.println("����Ʈ ����");
				else System.out.println("����Ʈ ������");
				check = true;//���¾��̵�
				String pw = new String(pwT.getPassword());//�н����尪
				
				for(MemberDTO data : list) {
					if(data.getId().equals(idT.getText())) {
						if(data.getPw().equals(pw)) {
							if(data.getId().equals("admin")) {
								JOptionPane.showMessageDialog(this, "������ �α���.");
								new AdminA(oos,ois, data.getId());
								this.dispose();
								return;
							}else {
								JOptionPane.showMessageDialog(this, "����� �α���.");
								new GuestU(oos,ois, data);
								this.dispose();
								return;
							}
							
						}else {
							JOptionPane.showMessageDialog(this, "��й�ȣ�� �ٽ� �Է��ϼ���.");
							return;
						}
					}else {
						check = false;
					}
				}//for
				if(check==false) JOptionPane.showMessageDialog(this, "���� ���̵� �Դϴ�.");
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

