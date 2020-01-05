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
		System.out.println("ChatU 관리하는 Guest_chat 생성");
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
						
						if(user == null && dtoC.getId().equals("admin")) {	//관리자 접속 시 들어오고, 딱 한번 생성
							this.admin = dtoC.getId();
							this.user = "no";
							System.out.println("관리자 채팅서버 접속함 " + admin);
							//채팅서버 등록만하고 종료
						}
						
						//이 밑은 유저 접속의 경우임
						if(admin == null) {
							this.user = dtoC.getId();
							ChatDTO sendDTO = new ChatDTO();
							sendDTO.setMsg(dtoC.getMsg());
							sendDTO.setId(dtoC.getId());
							sendDTO.setCommand("Join");
							broadcastAdmin(sendDTO); //관리자에게만 보내주어야함
							dtoC = null;
							System.out.println(user);
						}
						//둘중 하나라도 초기화 되어있다면 아무것도 Join을 받더라도 하는일 없음
					}else if(dtoC.getCommand().equals("Msg")) {
						System.out.println("관리자 메시지보냄 2");
						ChatDTO sendDTO = new ChatDTO();
						sendDTO.setId(dtoC.getId());			//각각 아이디 저장
						sendDTO.setMsg(dtoC.getMsg());			//각각 메시지 저장
						if(dtoC.getAdminId() != null) {
							sendDTO.setAdminId(dtoC.getAdminId());		//관리자인지 구별
						}
						sendDTO.setCommand("Msg");
						broadcastAdmin(sendDTO);
						broadcastUser(sendDTO);
						dtoC = null;
					}
					
					if(dtoE != null) {	//신경써야함, 전체종료
						if(dtoE.getExit().equals("ExitChatAll")) {
							System.out.println("admin이 모든 채팅 종료 요청");
							server.getList_chat().remove(this);
							ExitDTO dto = new ExitDTO();
							dto.setExit("ExitChatAll");
							//admin 담을필요없나
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
			}catch (EOFException e) {	//종료 임시방편
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
		for(Guest_chat gc : server.getList_chat()) {	//chat핸들러 모두 참조
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
		for(Guest_chat gc : server.getList_chat()) {	//chat핸들러 모두 참조
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
			if(gc.user != null || gc == this) {	//모든유저 그리고 요청한 AdminA 자기자신만		Admin 두 개 접속시에 다른 Admin가 종료하면 user에서 에러터짐 Admin 접속 1로 제한해야함
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











