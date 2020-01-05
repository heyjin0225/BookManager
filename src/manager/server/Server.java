package manager.server;

import java.net.ServerSocket;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import manager.bean.SeatDTO;

public class Server {
	public static final String IP = "192.168.0.29";
	
	private ServerSocket main_ss;
	private ServerSocket seat_ss;
	private ServerSocket chat_ss;
	
	private ArrayList<Guest_main> list_main = new ArrayList<>();
	private ArrayList<Guest_seat> list_seat = new ArrayList<>();
	private ArrayList<Guest_chat> list_chat = new ArrayList<>();
	private ArrayList<SeatDTO> reserved = new ArrayList<>();

	public Server() {
		System.out.println("서버준비완료");
		try {
			main_ss = new ServerSocket(9500);
			seat_ss = new ServerSocket(9600);
			chat_ss = new ServerSocket(9700);
		} catch (IOException io) {
			io.printStackTrace();
		}
		while(true) {
			try {
				Socket socket = main_ss.accept();
				Guest_main guest = new Guest_main(socket, this);//쓰레드임
				list_main.add(guest);
				guest.start();
				
				Runnable seat_run = new Runnable(){
					@Override
					public void run() {
						while(true) {
							try {
							Socket seat_socket = seat_ss.accept();
							Guest_seat guest_seat = new Guest_seat(seat_socket, Server.this);
							list_seat.add(guest_seat);
							guest_seat.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}	
				};
				Thread server_seat = new Thread(seat_run);
				server_seat.start();
				
				
				Runnable chat_run = new Runnable(){
					@Override
					public void run() {
						while(true) {
							try {
								Socket chat_socket = chat_ss.accept();
								Guest_chat guest_chat = new Guest_chat(chat_socket, Server.this);
								list_chat.add(guest_chat);
								guest_chat.start();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}	
				};
				Thread server_chat = new Thread(chat_run);
				server_chat.start();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//접속중인 모든 클라이언트 리스트 리턴
	public ArrayList<Guest_main> getList_main(){
		return list_main;
	}
	
	//-----------------------------------------------------------     자리예약↓       ---------------
	public ArrayList<Guest_seat> getSeatList(){
		return list_seat;
	}
	
	//자리 예약중인 모든 리스트 리턴
	public ArrayList<SeatDTO> getReserved() {
		return reserved;
	}
	
	//자리 예약하고 서버에 추가 
	public void addReserved(SeatDTO dto) {
		reserved.add(dto);
	}
	
	//자리 예약 취소 요청
	public void removeReserved(SeatDTO dto) {
		for(int i = 0; i < reserved.size(); i++) {
			if(reserved.get(i).getId().equals(dto.getId())){
				reserved.remove(i);
			}
		}
	}
	
	
	//-----------------------------------------------------------     채팅↓       ---------------               
	public ArrayList<Guest_chat> getList_chat() {
		return list_chat;
	}

	public void setList_chat(ArrayList<Guest_chat> list_chat) {
		this.list_chat = list_chat;
	}
	
	//-----------------------------------------------------------     Exit종료↓      ----------------------------------------------------------- 
	public void exit(String id) {
		
	}
	
	//-----------------------------------------------------------     메인↓      ----------------------------------------------------------- 
	public static void main(String[] args) {
		new Server();
	}
	
}
