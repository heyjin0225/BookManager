package manager.client;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import manager.bean.ChatDTO;
import manager.bean.ExitDTO;
import manager.server.Server;

public class AdminA extends JFrame implements Runnable{
	
	private BookManagerA bookmanagerA;
	private UserManagerA usermanagerA;
	private RentManagerA rentmanagerA;
	private SeatA seatA;
	private JTabbedPane jtp = new JTabbedPane(SwingConstants.TOP);
	
	private Socket chat_socket;
	private ObjectOutputStream oos_chat;
	private ObjectInputStream ois_chat;
	
	private String id;
    
	private ArrayList<ChatA> admin_list = new ArrayList<>();	//
	
	
	public AdminA(ObjectOutputStream oos, ObjectInputStream ois, String id) {
		
		this.id = id;
		
		bookmanagerA = new BookManagerA(oos,ois);
		usermanagerA = new UserManagerA(oos,ois);
		rentmanagerA = new RentManagerA(oos,ois);
		seatA = new SeatA(id, this);
		
		
		jtp = new JTabbedPane();
		jtp.addTab("��������",bookmanagerA);
		jtp.addTab("ȸ������",usermanagerA);
		jtp.addTab("�뿩����",rentmanagerA);
		jtp.addTab("�ڸ�����",seatA);
		
		add(jtp);
		jtp.setBackground(Color.GRAY);
		setBounds(300, 200, 1000, 500);
		setVisible(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		
		jtp.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(jtp.getSelectedIndex() == 3);//�Ǵ��������� �߻��ϴ� �̺�Ʈ�ۼ�
			}
		});
		
		chat_server();
		
		//�̺�Ʈ
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(AdminA.this, "�����Ͻðڽ��ϱ�?", 
															"���α׷�����", JOptionPane.OK_CANCEL_OPTION, 
															JOptionPane.QUESTION_MESSAGE);
				if(result==JOptionPane.OK_OPTION) {
					if(oos == null || ois == null)System.exit(0);
					try {
						seatA.exit();
						exit();
						
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
		setLayout(null);
	}
	
	public void chat_server() {
		try {
			chat_socket = new Socket(Server.IP, 9700);
			oos_chat = new ObjectOutputStream(chat_socket.getOutputStream());
			ois_chat = new ObjectInputStream(chat_socket.getInputStream());
		} catch (UnknownHostException e3) {
			e3.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		}
	
		//�ڵ鷯���� ���� �����Ȱ��� �������� �˸�
		ChatDTO dto = new ChatDTO();
		dto.setId(this.id);
		dto.setCommand("Join");
		try {
			oos_chat.writeObject(dto);
			oos_chat.flush();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		Thread t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		ExitDTO dtoE = null;
		ChatDTO dtoC = null;
		Object temp = null;
		while(true) {
			try {
				temp = ois_chat.readObject();
				
				if(temp.toString().equals("ChatDTO")) {
					dtoC = (ChatDTO)temp;
				}else if(temp.toString().equals("ExitDTO")) {
					dtoE = (ExitDTO)temp;
				}
				
				if(dtoC != null) {
					if(dtoC.getCommand().equals("Join")){
						/*
						JOptionPane.showMessageDialog(this, dtoC.getId() + "ȸ������ ä�ÿ�û", "�˸�", 1);
						ChatA chatA = new ChatA(dtoC.getId(), this);
						admin_list.add(chatA);	//ä�� ������ �ּ� ���	0�� user 1�� qwe		xXXX ������ ChatA �ּҰ� ����
						user_list.add(dtoC.getId());//									  ������ ChatU �ּҰ� ����
						
						chatA.getOutput().append(dtoC.getId() + "�� ����\n");	
					*/
						JOptionPane.showMessageDialog(this,dtoC.getMsg(),dtoC.getId()+"���� �޼���",1);
					}else if(dtoC.getCommand().equals("Msg")) {
						//�ӼӸ� �з�
						for(ChatA data : admin_list) {
							if(dtoC.getId().equals(data.getLinked_user())) {
								System.out.println("�޽��� ���� ���� �̸� " + data.getLinked_user());
								String msgId = dtoC.getId();
								if(dtoC.getAdminId() != null) {
									msgId = dtoC.getAdminId();
								}
								data.getOutput().append("["+ msgId +"] " + dtoC.getMsg()+"\n");
							}
						}
						
					}
				}
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("ExitChatAll")) {
						if(oos_chat != null)oos_chat.close();
						if(ois_chat != null)ois_chat.close();
						if(chat_socket != null)chat_socket.close();
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch (EOFException e) {	//���� �ӽù���
				try {
					if(oos_chat != null) oos_chat.close();
					if(ois_chat != null) ois_chat.close();
					if(chat_socket != null) chat_socket.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendMsg(String msg, String linked_user) {
		
		ChatDTO sendMsg = new ChatDTO();
		sendMsg.setMsg(msg);
		sendMsg.setId(linked_user);
		sendMsg.setAdminId(id);
		sendMsg.setCommand("Msg");
		System.out.println("������ �޽������� 1");
		try {
			oos_chat.writeObject(sendMsg);
			oos_chat.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//������ ���� ���ӽ�
	public void sendJoin(ChatDTO sendDTO) {
		try {
			oos_chat.writeObject(sendDTO);
			oos_chat.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exit() {
		ExitDTO dto = new ExitDTO();
		dto.setExit("ExitChatAll");
		try {
			oos_chat.writeObject(dto);
			oos_chat.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
}

