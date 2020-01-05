package manager.server;

import java.net.*;

import java.util.ArrayList;

import manager.bean.BookDTO;
import manager.bean.ExitDTO;
import manager.bean.MemberDTO;
import manager.bean.RentDTO;
import manager.bean.SeatDTO;
import manager.dao.BookDAO;
import manager.dao.LoginDAO;
import manager.dao.RentDAO;
import manager.dao.SeatDAO;

import java.io.*;



public class Guest_main extends Thread{
	
	private Server server;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	
	public Guest_main(Socket socket, Server server) {
		System.out.println("클라이언트 관리하는 게스트 생성 : " + server.getList_main().size());
		this.socket = socket;
		this.server = server;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//-----------------------------------------------------------------생성자
	/*
	 
		Guest 하나 당 하나의 Client 와 스트림으로 연결되어있음
		그리고 Guest는 Server의 Guest List를 참조해 올 수 있어서 *다른 Guest와 연결된 Client에게 데이터를 전송할 수 있음
		
	*/
	
	@Override
	public void run() {
		ExitDTO dtoE = null;
		MemberDTO dtoM = null;
		BookDTO dtoB = null;
		RentDTO dtoR = null;
		Object temp = null;	//판별하기전 스트림으로부터 객체저장
		
		while(true) {
			try {
				temp = ois.readObject();
				if(temp.toString().equals("MemberDTO")) {	//오버라이딩한 toString으로 어떤 객체인지 확인
					dtoM = (MemberDTO)temp;
				}else if(temp.toString().equals("ExitDTO")) {
					dtoE = (ExitDTO)temp;
				}else if(temp.toString().equals("BookDTO")) {
					dtoB = (BookDTO)temp;
				}else if(temp.toString().equals("RentDTO")) {
					dtoR = (RentDTO)temp;
				}
				//====================================================객체 분류
				
				if(dtoM != null) {	//MemberDTO 객체가 들어온경우
					if(dtoM.getCommand().equals("Join")) {
						System.out.println("회원가입 요청 실행");
						if(dtoM != null) {
							MemberDTO resultDTO = new MemberDTO();
							LoginDAO dao = LoginDAO.getInstance();
							resultDTO = dao.joinMember(dtoM);
							resultDTO.setCheck(true);
							resultDTO.setCommand("Join");
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							System.out.println("추가 완료");
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("Member")) {
						System.out.println("유저리스트 반환 요청 실행");
						if(dtoM != null) {
							LoginDAO dao = LoginDAO.getInstance();
							ArrayList<MemberDTO> list = dao.getMembers();
							MemberDTO resultDTO = new MemberDTO();
							resultDTO.setList(list);
							resultDTO.setCommand("Member");
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("NeedMember")) {
						System.out.println("검색한 유저 필요");
						if(dtoM != null) {
							MemberDTO resultDTO = new MemberDTO();
							LoginDAO dao = LoginDAO.getInstance();
							resultDTO = dao.needMember(dtoM);
							resultDTO.setCommand("NeedMember");
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("AddMember")) {
						System.out.println("관리탭에서 새로운 멤버입력");
						if(dtoM != null) {
							MemberDTO resultDTO = new MemberDTO();
							LoginDAO dao = LoginDAO.getInstance();
							resultDTO = dao.joinMember(dtoM);
							resultDTO.setCheck(true);
							resultDTO.setCommand("AddMember");
							ArrayList<MemberDTO> list = dao.getMembers();
							resultDTO.setList(list);
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							System.out.println("추가 완료");
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("DeleteMember")) {
						System.out.println("관리탭에서멤버삭제");
						if(dtoM!=null) {
							MemberDTO resultDTO = new MemberDTO();
							LoginDAO dao = LoginDAO.getInstance();
							dao.deleteMember(dtoM);
							resultDTO.setCommand("DeleteMember");
							ArrayList<MemberDTO> list = dao.getMembers();
							resultDTO.setList(list);
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							System.out.println("삭제 완료");
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("SearchMember")) {
						System.out.println("관리탭에서멤버검색");
						if(dtoM != null) {
							LoginDAO dao = LoginDAO.getInstance();
							ArrayList<MemberDTO> list = dao.getMembers();
							MemberDTO resultDTO = new MemberDTO();
							resultDTO.setList(list);
							resultDTO.setCommand("SearchMember");
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("UpdateMember")) {
						System.out.println("관리탭에서멤버업데이트");
						MemberDTO resultDTO = new MemberDTO();
						LoginDAO dao = LoginDAO.getInstance();
						dao.updateMember(dtoM);
						ArrayList<MemberDTO> list = dao.getMembers();
						resultDTO.setList(list);
						resultDTO.setCommand("UpdateMember");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoM = null;
					}else if(dtoM.getCommand().equals("UserUpdateMember")) {
						System.out.println("사용자탭에서 정보수정");
						MemberDTO resultDTO = new MemberDTO();
						LoginDAO dao = LoginDAO.getInstance();
						dao.userUpdateMember(dtoM);
						resultDTO.setCommand("UserUpdateMember");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoM = null;
					}
				}
				//====================================================MemberDTO 객체
				
				if(dtoB != null) {
					if(dtoB.getCommand().equals("BookList")) {
						System.out.println("북리스트 반환 요청 실행");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("BookList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoB = null;
					}else if(dtoB.getCommand().equals("AddBook")) {
						System.out.println("책 추가 //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						resultDTO = dao.addBook(dtoB);
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("AddBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						System.out.println("책 추가 완료");
						dtoB = null;
					}else if(dtoB.getCommand().equals("UpdateBook")) {
						System.out.println("책 업데이트 //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						dao.getUpdateBook(dtoB);
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("UpdateBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						System.out.println("책업데이트 완료");
						dtoB=null;
					}else if(dtoB.getCommand().equals("SearchBook")) {
						System.out.println("책 검색 //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("SearchBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoB = null; 
				
					}else if(dtoB.getCommand().equals("DeleteBook")) {
						System.out.println("책 삭제 //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						dao.deleteBook(dtoB);
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("DeleteBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						System.out.println("삭제 완료");
						dtoB=null;
					}else if(dtoB.getCommand().equals("FindBook")) {
						BookDAO dao = BookDAO.getInstance();
						dao.findBook(dtoB);
						dtoB.setCommand("FindBook");
						this.oos.writeObject(dtoB);
						this.oos.flush();
						System.out.println("찾기 완료");
						dtoB=null;
					}
				}
				//====================================================BookDTO 객체
				
				if(dtoR != null) {
					if(dtoR.getCommand().equals("NeedRent")){
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						dao.addRent(dtoR);
						resultDTO.setCommand("NeedRent");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
						
					}else if(dtoR.getCommand().equals("Counting")) {
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						resultDTO.setCounting(dao.counthing(dtoR));
						resultDTO.setCommand("Counting");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("RentList")) {
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						ArrayList<RentDTO> list = dao.getRentList();
						resultDTO.setList(list);
						resultDTO.setCommand("RentList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("OverdueList")) {
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						ArrayList<RentDTO> list = dao.getRentList();
						resultDTO.setList(list);
						resultDTO.setCommand("OverdueList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("DeleteRent")) {
						System.out.println("관리탭에서렌트삭제");
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						dao.deleteRent(dtoR);
						ArrayList<RentDTO> list = dao.getRentList();
						resultDTO.setList(list);
						resultDTO.setCommand("RentList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("UserRentList")) {
						System.out.println("사용자탭에서렌트");
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						resultDTO.setList(dao.userRentList(dtoR));
						resultDTO.setCommand("UserRentList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("UserDeleteRent")) {
						System.out.println("사용자탭에서렌트삭제");
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						dao.deleteRent(dtoR);
						ArrayList<RentDTO> list = dao.getRentList();
						resultDTO.setList(list);
						resultDTO.setCommand("UserRentList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("SearchRent")) {
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						ArrayList<RentDTO> list = dao.getRentList();
						resultDTO.setList(list);
						resultDTO.setCommand("SearchRent");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}
				}
				//====================================================RentDTO 객체
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("Exit")) {
						System.out.println("게스트 종료 요청");
						ExitDTO dto = new ExitDTO();
						dto.setExit("Exit");
						broadcast(dto);
						server.getList_main().remove(this);
						oos.close();
						ois.close();
						socket.close();
						break;
					}
				}
				//====================================================연결 종료 객체
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	//-----------------------------------------------------------------클라이언트로부터 요청받고 로직 처리 run()
	

	//모두에게 보내기
	public void broadcastAll (BookDTO resultDTO) {
		for(Guest_main g : server.getList_main()) {
			try {
				g.oos.writeObject(resultDTO);
				g.oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//모두에게 누군가 자리 등록했다고 보냄
	public void broadcastAll(SeatDTO resultDTO) {
		for(Guest_main g : server.getList_main()) {
			try {
				System.out.println(g.oos);
				g.oos.writeObject(resultDTO);
				g.oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//접속자 한명 개인로직처리할때 *주로 쓸듯
	public void broadcast(ExitDTO sendDTO) {
		for(Guest_main g : server.getList_main()) {
			if(g == this) {
				try {
					g.oos.writeObject(sendDTO);
					g.oos.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
