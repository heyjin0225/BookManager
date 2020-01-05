package manager.client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import manager.bean.ChatDTO;

public class ChatA extends JFrame implements ActionListener{

	
	private	JTextArea output;
    private	JTextField input;
	private	JButton sendB;
	private	JPanel southP;
	
	private AdminA admin;
	private String linked_user;	// 생성되는 채팅창이 누구와 연결되어있는지
	
	public ChatA(String linked_user, AdminA admin) {
		this.linked_user = linked_user;
		this.admin = admin;
		chat_frame();
		
		//관리자 생성시 채팅핸들러에 등록
		ChatDTO sendDTO = new ChatDTO();
		sendDTO.setId("admin");
		sendDTO.setCommand("SetAdmin");
		admin.sendJoin(sendDTO);
	}
	
	public void chat_frame() {
//		JFrame chatF = new JFrame(dtoC.getId() + "회원님 메시지창");
		setTitle("관리자 채팅");
		
		sendB = new JButton("전송");
		input = new JTextField(26);
		input.requestFocus();
		output = new JTextArea();
		output.setEditable(false);
		
		
		southP = new JPanel(new BorderLayout());
		southP.add("West", input);
		southP.add("East", sendB);
		
		Container con = getContentPane();
		con.add("South", southP);
		con.add("Center", output);
		
		setBounds(1100, 200, 350, 500);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);	//수정 필요
		
		sendB.addActionListener(this);
		
		
	}
	
	//보기버튼 누르면 AdminA 로 전송
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendB) {
			System.out.println("보내기 버튼");
			String msg = input.getText();
			admin.sendMsg(msg, linked_user);	//메시지, 어드민임을 알리고, 연결된 유저, 자기한테만오게
			input.setText("");
		}
	}
	
	public JTextArea getOutput() {
		return output;
	}

	public void setOutput(JTextArea output) {
		this.output = output;
	}

	public JTextField getInput() {
		return input;
	}

	public void setInput(JTextField input) {
		this.input = input;
	}

	public AdminA getAdmin() {
		return admin;
	}

	public void setAdmin(AdminA admin) {
		this.admin = admin;
	}

	public String getLinked_user() {
		return linked_user;
	}

	public void setLinked_user(String linked_user) {
		this.linked_user = linked_user;
	}

	public JButton getSendB() {
		return sendB;
	}

	public void setSendB(JButton sendB) {
		this.sendB = sendB;
	}

}
