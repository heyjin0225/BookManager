package manager.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

import manager.bean.ExitDTO;
import manager.bean.MemberDTO;
import manager.bean.SeatDTO;
import manager.server.Server;


class SeatA extends JPanel implements ActionListener, Runnable{

	private JLabel seatL;
	private JButton[] jbt1 = new JButton[26];
	private String[] buttonN = new String[26];
	
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	private String id;				//id 나중에 생성자로 전송된 값으로 바꿔주어야함
	private SeatDTO checkDTO = new SeatDTO();
	
	private AdminA adminA;
	//채팅
	
	private ArrayList<String> user_list = new ArrayList<>();
	
	
	public SeatA(String id, AdminA adminA) {
		this.adminA = adminA;
		try {
			socket = new Socket(Server.IP, 9600);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.id = id;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//버튼0 버리기
		jbt1[0] = new JButton();
		
		checkDTO = new SeatDTO();
		checkDTO.setId(id);
		checkDTO.setCommand("NeedSeatList");
		try {
			oos.writeObject(checkDTO);
			oos.flush();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		viewMaker();
		
		for(int i = 0; i < jbt1.length; i++) {
			jbt1[i].addActionListener(this);
		}
		
		//채팅
		//chatB.addActionListener(this);
		
		Thread t = new Thread(this);
		t.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		for(int i = 1; i < jbt1.length; i++) {
			if(e.getSource()==jbt1[i]){		//1. 해당 id가 seat db에 없을경우
				if(jbt1[i].getName().equals("예약중")) {	//2. 이름이 예약중일경우
					String user = jbt1[i].getText().substring(0, jbt1[i].getText().lastIndexOf('가'));
					int result = JOptionPane.showConfirmDialog(this, user+" 내보내시겠습니까?");
					if(result == JOptionPane.OK_OPTION) {
						SeatDTO dto = new SeatDTO();
						dto.setId(user);
						dto.setHere(true);
						dto.setSeatNumber(i);
						dto.setSeatName(buttonN[i]);
						dto.setCommand("DeleteGuest");
						try {
							oos.writeObject(dto);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
		
		
		
		
		//채팅
	}
	private void viewMaker() {
		seatL = new JLabel("좌석 현황");
		seatL.setFont(new Font("돋움",Font.BOLD,25));
		seatL.setBounds(340, 15, 200, 40);
		
		//버튼 이름 지정
		char seatN = 'A';
		int k = 1;
		int y = 5;
		for(int i = 0; i < 5; i++) {
			for(; k <= y; k++) {
				jbt1[k] = new JButton(seatN + " _ " + k);
				buttonN[k] = (seatN + " _ " + k);
			}
			y += 5;
			seatN++;
		}
		
		//위치 지정
		int a = 90;
		int b = 190;
		for(int i = 1; i < 6; i++) {
			jbt1[i].setBounds(40, a, 70, 40);
			jbt1[i+5].setBounds(b, 90, 70, 40);
			jbt1[i+10].setBounds(b, 180, 70, 40);
			jbt1[i+15].setBounds(b, 270, 70, 40);
			jbt1[i+20].setBounds(b, 360, 70, 40);
			a += 70;
			b += 110;
		}
		
		//빈자리 체크를 위해서 넣음
		for(int i = 1; i < jbt1.length; i++) {
			jbt1[i].setName("예약가능");
		}
			
		setBackground(new Color(153,214,234));
		setLayout(null);
		
		//버튼 그리기
		for(int i = 1; i < jbt1.length; i++) {
			add(jbt1[i]);
		}
		
		
		SeatDTO firstDTO = new SeatDTO();
		firstDTO.setCommand("NeedSeatList");
		try {
			oos.writeObject(firstDTO);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setBounds(700, 100, 900, 500);
		
	}
	
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
						for(SeatDTO data : dtoS.getReserved()) {
							jbt1[data.getSeatNumber()].setText(data.getId()+"가 예약함");
							jbt1[data.getSeatNumber()].setName("예약중");
						}
					}else if(dtoS.getCommand().equals("NewGuest")) {
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getId()+"가 예약함");
						jbt1[dtoS.getSeatNumber()].setName("예약중"); 		//빈자리 체크 위해서
					}else if(dtoS.getCommand().equals("IsHere")) {
						System.out.println(dtoS.getId() + " 의  자리가 이미 있는지 검사");
						checkDTO = dtoS;
					}else if(dtoS.getCommand().equals("DeleteGuest")) {
						System.out.println("자리 삭제됨");
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getSeatName());
						jbt1[dtoS.getSeatNumber()].setName("예약가능");		//빈자리 반환
					}
				}
				
				if(dtoE != null) {
					if(dtoE.getExit().equals("Exit")) {
						if(oos != null) oos.close();
						if(ois != null) ois.close();
						if(socket!= null) socket.close();
						break;
					}
				}
			} catch (EOFException e) {	//종료 임시방편
				try {
					if(oos != null)oos.close();
					if(ois != null) ois.close();
					if(socket != null) socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit() {
		ExitDTO dto = new ExitDTO();
		dto.setExit("Exit");
		try {
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}