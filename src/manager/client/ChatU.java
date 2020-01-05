package manager.client;

import javax.swing.*;

import manager.bean.ChatDTO;
import manager.bean.ExitDTO;
import manager.server.Server;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatU extends JFrame implements Runnable, ActionListener{
	
	
	private static final long serialVersionUID = 623792110965844918L;
	
	private JTextArea output;
	private JTextField input;
	private JButton sendB;
	
	private JPanel southP;
	
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Socket socket;
	
	private String id;
	
	private SeatU seatU;		//ä��â ī���ö�����
	
	public ChatU(String id, SeatU seatU) {
		this.id = id;
		this.seatU = seatU;
		
		
		setTitle("ä��");
		
		sendB = new JButton("����");
		input = new JTextField(26);
		input.requestFocus();
		output = new JTextArea();
		output.setEditable(false);
		
		
		southP = new JPanel(new BorderLayout());
		southP.add("West", input);
		southP.add("East", sendB);
		
		Container con = this.getContentPane();
		con.add("South", southP);
		con.add("Center", output);
		
		setBounds(1100, 200, 350, 500);
		setVisible(false);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		sendB.addActionListener(this);
		
		service();
		
		this.addWindowListener(new WindowAdapter() {
			
			//���� ��������
			
			@Override
			public void windowOpened(WindowEvent e) {
				input.requestFocus(); // â�̿����� �۾��°����� ��Ŀ��������
			}
		});
	}
	
	public void service() {
		try {
			socket = new Socket(Server.IP, 9700);
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread t = new Thread(this);
		t.start();
		
		
		//���� ä�ÿ��� ������ Join ���� -> �����ڿ��� new ������
		ChatDTO dto = new ChatDTO();
		String msg = JOptionPane.showInputDialog(this, "������ ������ �Է��ϼ���");
		dto.setId(id);
		dto.setCommand("Join");
		dto.setMsg(msg);
		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == sendB) {
			System.out.println("������ ��ư");
			String msg = input.getText();
			try {
				ChatDTO dto = new ChatDTO();
				dto.setId(id);
				dto.setMsg(msg);
				dto.setCommand("Msg");
				oos.writeObject(dto);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			input.setText("");
		}
	}	
	
	public void exit() {
		ExitDTO dto = new ExitDTO();
		dto.setExit("Exit");
		dto.setId(id);
		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		ChatDTO dtoC = null;
		ExitDTO dtoE = null;
		Object temp = null;
		while(true) {
			try {
				temp = ois.readObject();
				if(temp.toString().equals("ChatDTO")) {
					dtoC = (ChatDTO)temp;
				}else if(temp.toString().equals("ExitDTO")) {
					dtoE = (ExitDTO)temp;
				}
				
				if(dtoC != null) {
					if(dtoC.getCommand().equals("Join")) {
						output.append(dtoC.getId() + "�� ����\n");
						System.out.println("���ι���");
					}else if(dtoC.getCommand().equals("Msg")) {
						System.out.println("�޽��� ������ ������");
						String msgId = dtoC.getId();
						if(dtoC.getAdminId() != null) {
							msgId = dtoC.getAdminId();
						}
						output.append("["+ msgId +"] " + dtoC.getMsg()+"\n");
					}
				}
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("Exit")) {		//this user����
						if(oos != null) oos.close();
						if(ois != null) ois.close();
						if(socket != null) socket.close();
						break;
					}else if(dtoE.getExit().equals("ExitChatAll")) {	//admin ����
						if(oos != null) oos.close();
						if(ois != null) ois.close();
						if(socket != null) socket.close();
//						seatU.setChatCheck(false);
						this.setVisible(false);
						this.dispose();
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
