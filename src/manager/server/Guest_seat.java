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
		System.out.println("seat �����ϴ� Seat�Խ�Ʈ ���� : " + server.getSeatList().size());
		this.socket = socket;
		this.server = server;
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//-----------------------------------------------------------------������
	
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
						dao.newGuest(dtoS);	//DB�� �ڸ� ���� �߰�
						resultDTO = dtoS;	//������ �����  resultDTO ����
						server.addReserved(resultDTO);	//������ �ڸ� �߰��ߴٰ� ���
						resultDTO.setReserved(server.getReserved());	//�߰��� ������ ����Ʈ�� resultDTO�� ��������
						resultDTO.setCommand("NewGuest");	//���� �� Ŭ���̾�Ʈ���� ������ �غ�
						broadcastAll(resultDTO);	//��ο��� �ڸ� �������� ����
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
						dao.deleteGuest(dtoS);		//DB�� ���̵� ���ϸ鼭 �ش� �Խ�Ʈ ����
						server.removeReserved(dtoS);	//������ ���̵� ���ϸ鼭 ���� ����Ʈ���� �ش� �Խ�Ʈ����� ����Ʈ���� ���ŵ�
						resultDTO.setReserved(server.getReserved());	//���ŵ� ����Ʈ�� �޾Ƽ� ��� Ŭ���̾�Ʈ���� �Ѹ�
						resultDTO.setCommand("DeleteGuest");
						broadcastAll(resultDTO);	//��ο��� ����
						dtoS = null;
					}
				}
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("Exit")) {
						System.out.println("seat�Խ�Ʈ ���� ��û");
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
			}catch (EOFException e) {	//���� �ӽù���
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
	
	//��ο��� ������, �������α׷������� *�� �Ⱦ���
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
		
		//��ο��� ������ �ڸ� ����ߴٰ� ����
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
		
		//������ �Ѹ� ���η���ó���Ҷ� *�ַ� ����
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
