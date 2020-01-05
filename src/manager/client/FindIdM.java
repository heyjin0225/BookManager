package manager.client;

import java.awt.BorderLayout;
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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import manager.bean.MemberDTO;

public class FindIdM  extends JFrame implements ActionListener{
	
	private JLabel titleL, emailL;
	private JTextField emailT;
	private JButton findB,cancelB;
	
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ArrayList<MemberDTO> list;
	
	public FindIdM(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
		setTitle("아이디찾기");
		
		titleL = new JLabel("아이디찾기");
		emailL = new JLabel("이메일을 입력해주세요.");
		emailT = new JTextField(20);
		findB = new JButton("찾기");
		cancelB = new JButton("취소");
		
		JPanel jp1= new JPanel();
		jp1.add(titleL);
		
		JPanel jp2= new JPanel();
		jp2.add(emailL);
		
		JPanel jp3= new JPanel();
		jp2.add(emailT);
		
		JPanel jp4= new JPanel();
		jp4.add(findB);
		jp4.add(cancelB);
		
		Container con = getContentPane();
		con.setLayout(new GridLayout(4,1));
		con.add(jp1);
		con.add(jp2);
		con.add(jp3);
		con.add(jp4);
		
		setBounds(700, 200, 300, 420);
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
			MemberDTO dto = (MemberDTO) ois.readObject();//회원가입여부
			boolean check = false;
			if(dto.getCommand().equals("Member")) {
				list = dto.getList();
				System.out.println(list);
				if(list!=null) {
					for(MemberDTO data : list) {
						System.out.println(data.getEmail());
						if(data.getEmail().equals(emailT.getText())) {
							JOptionPane.showMessageDialog(this, "아이디는 "+data.getId()+"입니다.");
							check = true;
						}else {
							check =false;
						}
					}//for
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
