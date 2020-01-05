package manager.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;

import manager.bean.ChatDTO;
import manager.bean.ExitDTO;
import manager.client.ChatA;
import manager.client.ChatU;


public class Guest_chat extends Thread{
	
	private Server server;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private String admin = null;
	private String user = null;
	
	public Guest_chat(Socket socket, Server server) {
		System.out.println("ChatU �����ϴ� Guest_chat ����");
		this.server = server;
		this.socket = socket;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
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
						
						if(user == null && dtoC.getId().equals("admin")) {	//������ ���� �� ������, �� �ѹ� ����
							this.admin = dtoC.getId();
							this.user = "no";
							System.out.println("������ ä�ü��� ������ " + admin);
							//ä�ü��� ��ϸ��ϰ� ����
						}
						
						//�� ���� ���� ������ �����
						if(admin == null) {
							this.user = dtoC.getId();
							ChatDTO sendDTO = new ChatDTO();
							sendDTO.setMsg(dtoC.getMsg());
							sendDTO.setId(dtoC.getId());
							sendDTO.setCommand("Join");
							broadcastAdmin(sendDTO); //�����ڿ��Ը� �����־����
							dtoC = null;
							System.out.println(user);
						}
						//���� �ϳ��� �ʱ�ȭ �Ǿ��ִٸ� �ƹ��͵� Join�� �޴��� �ϴ��� ����
					}else if(dtoC.getCommand().equals("Msg")) {
						System.out.println("������ �޽������� 2");
						ChatDTO sendDTO = new ChatDTO();
						sendDTO.setId(dtoC.getId());			//���� ���̵� ����
						sendDTO.setMsg(dtoC.getMsg());			//���� �޽��� ����
						if(dtoC.getAdminId() != null) {
							sendDTO.setAdminId(dtoC.getAdminId());		//���������� ����
						}
						sendDTO.setCommand("Msg");
						broadcastAdmin(sendDTO);
						broadcastUser(sendDTO);
						dtoC = null;
					}
					
					if(dtoE != null) {	//�Ű�����, ��ü����
						if(dtoE.getExit().equals("ExitChatAll")) {
							System.out.println("admin�� ��� ä�� ���� ��û");
							server.getList_chat().remove(this);
							ExitDTO dto = new ExitDTO();
							dto.setExit("ExitChatAll");
							//admin �����ʿ����
//							broadcastAll(dto);
							
							dto.setExit("ExitChatAll");
							oos.writeObject(dto);
							oos.flush();
							
							oos.close();
							ois.close();
							socket.close();
							break;
						}else if(dtoE.getExit().equals("Exit")) {
							if(this.admin == null) {
								server.getList_chat().remove(this);
								
								oos.close();
								ois.close();
								socket.close();
								break;
							}
						}
					}
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}catch (EOFException e) {	//���� �ӽù���
				try {
					if(oos != null) oos.close();
					if(ois != null) ois.close();
					if(socket != null) socket.close();
					break;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
		}	
	}
	
	
	public void broadcastAdmin(ChatDTO sendDTO) {
		for(Guest_chat gc : server.getList_chat()) {
			if(gc.admin != null && gc.admin.equals("admin")){
				try {
					gc.oos.writeObject(sendDTO);
					gc.oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void broadcastUser(ChatDTO sendDTO) {
		for(Guest_chat gc : server.getList_chat()) {	//chat�ڵ鷯 ��� ����
			if(gc.getUser() != null && gc.getUser().equals(sendDTO.getId())) {
				try {
					gc.oos.writeObject(sendDTO);
					gc.oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void broadcastAdmin(ExitDTO sendDTO) {
		for(Guest_chat gc : server.getList_chat()) {
			if(gc.admin != null && gc.admin.equals("admin")){
				try {
					gc.oos.writeObject(sendDTO);
					gc.oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	public void broadcastUser(ExitDTO sendDTO) {
		for(Guest_chat gc : server.getList_chat()) {	//chat�ڵ鷯 ��� ����
			if(gc.getUser() != null && gc.getUser().equals(sendDTO.getId())) {
				try {
					gc.oos.writeObject(sendDTO);
					gc.oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void broadcastAll(ExitDTO sendDTO) {
		for(Guest_chat gc : server.getList_chat()) {
			if(gc.user != null || gc == this) {	//������� �׸��� ��û�� AdminA �ڱ��ڽŸ�		Admin �� �� ���ӽÿ� �ٸ� Admin�� �����ϸ� user���� �������� Admin ���� 1�� �����ؾ���
				try {
					gc.oos.writeObject(sendDTO);
					gc.oos.flush();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}

	public void broadcastAll(ChatDTO sendDTO) {
		for(Guest_chat gc : server.getList_chat()) {
			try {
				gc.oos.writeObject(sendDTO);
				gc.oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
}











