package manager.server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import manager.bean.BookDTO;
import manager.bean.ExitDTO;
import manager.bean.SeatDTO;
import manager.dao.SeatDAO;

public class Guest_seat extends Thread{
	
	private Server server;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	public Guest_seat(Socket socket, Server server) {
		System.out.println("seat 관리하는 Seat게스트 생성 : " + server.getSeatList().size());
		this.socket = socket;
		this.server = server;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//-----------------------------------------------------------------생성자
	
	@Override
	public void run() {
		ExitDTO dtoE = null;
		SeatDTO dtoS = null;
		Object temp = null;
		while(true) {
			try {
				temp = ois.readObject();
				if(temp.toString().equals("SeatDTO")) {
					dtoS = (SeatDTO)temp;
				}else if(temp.toString().equals("ExitDTO")) {
					dtoE = (ExitDTO)temp;
				}
				if(dtoS != null) {
					if(dtoS.getCommand().equals("NeedSeatList")) {
						SeatDTO resultDTO = new SeatDTO();
						resultDTO.setReserved(server.getReserved());
						resultDTO.setCommand("NeedSeatList");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoS = null;
					}else if(dtoS.getCommand().equals("NewGuest")){
						SeatDTO resultDTO = new SeatDTO();
						SeatDAO dao = SeatDAO.getInstance();
						dao.newGuest(dtoS);	//DB에 자리 정보 추가
						resultDTO = dtoS;	//서버에 담겨질  resultDTO 생성
						server.addReserved(resultDTO);	//서버에 자리 추가했다고 등록
						resultDTO.setReserved(server.getReserved());	//추가된 서버의 리스트를 resultDTO에 갱신해줌
						resultDTO.setCommand("NewGuest");	//이제 각 클라이언트에게 보내줄 준비
						broadcastAll(resultDTO);	//모두에게 자리 나갔음을 전송
						dtoS = null;
					}else if(dtoS.getCommand().equals("IsHere")) {
						SeatDTO resultDTO = new SeatDTO();
						SeatDAO dao = SeatDAO.getInstance();
						resultDTO = dao.searchId(dtoS);
						resultDTO.setCommand("IsHere");
						this.oos.writeObject(resultDTO);
						this.oos.flush();
						dtoS = null;
					}else if(dtoS.getCommand().equals("DeleteGuest")) {
						SeatDTO resultDTO = new SeatDTO();
						resultDTO.setSeatNumber(dtoS.getSeatNumber());
						resultDTO.setSeatName(dtoS.getSeatName());
						SeatDAO dao = SeatDAO.getInstance();
						dao.deleteGuest(dtoS);		//DB에 아이디 비교하면서 해당 게스트 지움
						server.removeReserved(dtoS);	//서버에 아이디 비교하면서 서버 리스트에서 해당 게스트지우고 리스트까지 갱신됨
						resultDTO.setReserved(server.getReserved());	//갱신된 리스트를 받아서 모든 클라이언트에게 뿌림
						resultDTO.setCommand("DeleteGuest");
						broadcastAll(resultDTO);	//모두에게 전달
						dtoS = null;
					}
				}
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("Exit")) {
						System.out.println("seat게스트 종료 요청");
						ExitDTO dto = new ExitDTO();
						dto.setExit("Exit");
						broadcast(dto);
						server.getSeatList().remove(this);
						oos.close();
						ois.close();
						socket.close();
						break;
					}
				}
				
			}catch(ClassNotFoundException e) {
				e.printStackTrace();
			}catch (EOFException e) {	//종료 임시방편
				try {
					if(oos != null)oos.close();
					if(ois != null) ois.close();
					if(socket != null) socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	
	//모두에게 보내기, 관리프로그램에서는 *잘 안쓸듯
		public void broadcastAll (BookDTO resultDTO) {
			for(Guest_seat g : server.getSeatList()) {
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
			for(Guest_seat g : server.getSeatList()) {
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
			for(Guest_seat g : server.getSeatList()) {
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
