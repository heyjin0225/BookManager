package manager.client;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import manager.bean.MemberDTO;

public class FindPwM extends JFrame implements ActionListener {
	private JLabel titleL, idL, emailL;
	private JTextField idT, emailT;
	private JButton findB,cancelB;
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ArrayList<MemberDTO> list;

	public FindPwM(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
		setTitle("비밀번호찾기");
		
		titleL = new JLabel("비밀번호찾기");
		idL = new JLabel("아이디를 입력해주세요.");
		idT = new JTextField(20);
		emailL = new JLabel("이메일을 입력해주세요.");
		emailT = new JTextField(20);
		findB = new JButton("찾기");
		cancelB = new JButton("취소");
		
		JPanel jp1= new JPanel();
		jp1.add(titleL);
		
		JPanel jp2 = new JPanel();
		jp2.add(idL);
		jp2.add(idT);
		
		JPanel jp3 = new JPanel();
		jp3.add(emailL);
		jp3.add(emailT);
		
		JPanel jp4 = new JPanel();
		jp4.add(findB);
		jp4.add(cancelB);
		
		Container con = getContentPane();
		con.setLayout(new GridLayout(4, 1));
		con.add(jp1);
		con.add(jp2);
		con.add(jp3);
		con.add(jp4);
		
		
		setBounds(700, 200, 300, 450);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		findB.addActionListener(this);
		cancelB.addActionListener(this);
	
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==findB) {
			MemberDTO dto = new MemberDTO();
			dto.setCommand("Member");
			
			try {
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			run();
			
		}else if(e.getSource()==cancelB) {
			this.dispose();
		}
	}
	
	public void run() {
		try {
			MemberDTO dto  = (MemberDTO)ois.readObject();
			boolean check = false;
			if(dto.getCommand().equals("Member")) {
				list = dto.getList();
				if(list!=null) {
					for(MemberDTO data : list) {
						System.out.println(data.getId());
						if(data.getId().equals(idT.getText())) {
							if(data.getEmail().equals(emailT.getText())) {
								JOptionPane.showMessageDialog(this, "비밀번호는 "+data.getPw()+"입니다.");
								check = true;
							}else {
								JOptionPane.showMessageDialog(this, "이메일이 틀렸습니다.");
								check =false;
								return;
							}
						}
					}
					if(check==false) {
						JOptionPane.showMessageDialog(this, "찾는 아이디가 없습니다.");
						
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
