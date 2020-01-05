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
		System.out.println("Ŭ���̾�Ʈ �����ϴ� �Խ�Ʈ ���� : " + server.getList_main().size());
		this.socket = socket;
		this.server = server;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//-----------------------------------------------------------------������
	/*
	 
		Guest �ϳ� �� �ϳ��� Client �� ��Ʈ������ ����Ǿ�����
		�׸��� Guest�� Server�� Guest List�� ������ �� �� �־ *�ٸ� Guest�� ����� Client���� �����͸� ������ �� ����
		
	*/
	
	@Override
	public void run() {
		ExitDTO dtoE = null;
		MemberDTO dtoM = null;
		BookDTO dtoB = null;
		RentDTO dtoR = null;
		Object temp = null;	//�Ǻ��ϱ��� ��Ʈ�����κ��� ��ü����
		
		while(true) {
			try {
				temp = ois.readObject();
				if(temp.toString().equals("MemberDTO")) {	//�������̵��� toString���� � ��ü���� Ȯ��
					dtoM = (MemberDTO)temp;
				}else if(temp.toString().equals("ExitDTO")) {
					dtoE = (ExitDTO)temp;
				}else if(temp.toString().equals("BookDTO")) {
					dtoB = (BookDTO)temp;
				}else if(temp.toString().equals("RentDTO")) {
					dtoR = (RentDTO)temp;
				}
				//====================================================��ü �з�
				
				if(dtoM != null) {	//MemberDTO ��ü�� ���°��
					if(dtoM.getCommand().equals("Join")) {
						System.out.println("ȸ������ ��û ����");
						if(dtoM != null) {
							MemberDTO resultDTO = new MemberDTO();
							LoginDAO dao = LoginDAO.getInstance();
							resultDTO = dao.joinMember(dtoM);
							resultDTO.setCheck(true);
							resultDTO.setCommand("Join");
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							System.out.println("�߰� �Ϸ�");
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("Member")) {
						System.out.println("��������Ʈ ��ȯ ��û ����");
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
						System.out.println("�˻��� ���� �ʿ�");
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
						System.out.println("�����ǿ��� ���ο� ����Է�");
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
							System.out.println("�߰� �Ϸ�");
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("DeleteMember")) {
						System.out.println("�����ǿ����������");
						if(dtoM!=null) {
							MemberDTO resultDTO = new MemberDTO();
							LoginDAO dao = LoginDAO.getInstance();
							dao.deleteMember(dtoM);
							resultDTO.setCommand("DeleteMember");
							ArrayList<MemberDTO> list = dao.getMembers();
							resultDTO.setList(list);
							this.oos.writeObject(resultDTO);
							this.oos.flush();
							System.out.println("���� �Ϸ�");
							dtoM = null;
						}
					}else if(dtoM.getCommand().equals("SearchMember")) {
						System.out.println("�����ǿ�������˻�");
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
						System.out.println("�����ǿ������������Ʈ");
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
						System.out.println("������ǿ��� ��������");
						MemberDTO resultDTO = new MemberDTO();
						LoginDAO dao = LoginDAO.getInstance();
						dao.userUpdateMember(dtoM);
						resultDTO.setCommand("UserUpdateMember");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoM = null;
					}
				}
				//====================================================MemberDTO ��ü
				
				if(dtoB != null) {
					if(dtoB.getCommand().equals("BookList")) {
						System.out.println("�ϸ���Ʈ ��ȯ ��û ����");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("BookList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoB = null;
					}else if(dtoB.getCommand().equals("AddBook")) {
						System.out.println("å �߰� //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						resultDTO = dao.addBook(dtoB);
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("AddBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						System.out.println("å �߰� �Ϸ�");
						dtoB = null;
					}else if(dtoB.getCommand().equals("UpdateBook")) {
						System.out.println("å ������Ʈ //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						dao.getUpdateBook(dtoB);
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("UpdateBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						System.out.println("å������Ʈ �Ϸ�");
						dtoB=null;
					}else if(dtoB.getCommand().equals("SearchBook")) {
						System.out.println("å �˻� //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("SearchBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoB = null; 
				
					}else if(dtoB.getCommand().equals("DeleteBook")) {
						System.out.println("å ���� //Guest");
						BookDTO resultDTO = new BookDTO();
						BookDAO dao = BookDAO.getInstance();
						dao.deleteBook(dtoB);
						ArrayList<BookDTO> list = dao.getBookList();
						resultDTO.setList(list);
						resultDTO.setCommand("DeleteBook");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						System.out.println("���� �Ϸ�");
						dtoB=null;
					}else if(dtoB.getCommand().equals("FindBook")) {
						BookDAO dao = BookDAO.getInstance();
						dao.findBook(dtoB);
						dtoB.setCommand("FindBook");
						this.oos.writeObject(dtoB);
						this.oos.flush();
						System.out.println("ã�� �Ϸ�");
						dtoB=null;
					}
				}
				//====================================================BookDTO ��ü
				
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
						System.out.println("�����ǿ�����Ʈ����");
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
						System.out.println("������ǿ�����Ʈ");
						RentDTO resultDTO = new RentDTO();
						RentDAO dao = RentDAO.getInstance();
						resultDTO.setList(dao.userRentList(dtoR));
						resultDTO.setCommand("UserRentList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoR = null;
					}else if(dtoR.getCommand().equals("UserDeleteRent")) {
						System.out.println("������ǿ�����Ʈ����");
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
				//====================================================RentDTO ��ü
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("Exit")) {
						System.out.println("�Խ�Ʈ ���� ��û");
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
				//====================================================���� ���� ��ü
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	//-----------------------------------------------------------------Ŭ���̾�Ʈ�κ��� ��û�ް� ���� ó�� run()
	

	//��ο��� ������
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
	
	//��ο��� ������ �ڸ� ����ߴٰ� ����
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
	
	//������ �Ѹ� ���η���ó���Ҷ� *�ַ� ����
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
