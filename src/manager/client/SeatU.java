package manager.client;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import manager.bean.ChatDTO;
import manager.bean.ExitDTO;
import manager.bean.MemberDTO;
import manager.bean.SeatDTO;
import manager.server.Server;

class SeatU extends JPanel implements ActionListener, Runnable {

	private JLabel seatL;
	private JButton[] jbt1 = new JButton[26];
	private JButton askB;

	private ChatU chatU;
//	private boolean chatCheck = false;

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Socket socket2;
	private ObjectOutputStream oos2;
	private ObjectInputStream ois2;
	
	private boolean check = false;
	
	private String id; // id 나중에 생성자로 전송된 값으로 바꿔주어야함
	private SeatDTO checkDTO = new SeatDTO();

	public SeatU(String id) {
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

		// 버튼0 버리기
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

		for (int i = 0; i < jbt1.length; i++) {
			jbt1[i].addActionListener(this);
		}

		askB.addActionListener(this);

		Thread t = new Thread(this);
		t.start();

		SeatDTO firstDTO = new SeatDTO();
		firstDTO.setCommand("NeedSeatList");
		try {
			oos.writeObject(firstDTO);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			socket2 = new Socket(Server.IP, 9700);
			oos2 = new ObjectOutputStream(socket2.getOutputStream());
			ois2 = new ObjectInputStream(socket2.getInputStream());
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
//		Thread t = new Thread(this);
//		t.start();
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == askB) {


			// 유저 채팅에는 서버로 Join 보냄 -> 관리자에도 new 시켜줌
			ChatDTO dto = new ChatDTO();
			String msg = JOptionPane.showInputDialog(this, "문의할 내용을 입력하세요");
			if(msg == null)return;
			dto.setId(id);
			dto.setCommand("Join");
			dto.setMsg(msg);
			try {
				oos2.writeObject(dto);
				oos2.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		checkDTO = new SeatDTO();
		checkDTO.setId(id);
		checkDTO.setCommand("IsHere");
		try {
			oos.writeObject(checkDTO);
			oos.flush();
		} catch (IOException e2) {
			e2.printStackTrace();
		} // 지금 접속한 id가 이미 자리예약을 해놨는지 검사위해서 보냄
		

		try {
			Thread.sleep(450);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} // 검사결과 받아오기전에 메인이 먼저 넘기는거 방지

		if (e.getSource() == jbt1[checkDTO.getSeatNumber()]) { // 누른 자리가 checkDTO 의 자리일경우
			System.out.println(checkDTO.getId() + " 예약취소 요청");
			checkDTO.setCommand("DeleteGuest"); // 예약취소요청
			try {
				oos.writeObject(checkDTO);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		if (checkDTO.isHere()) { // 접속중인 유저 ID정보 토대로 seat db에 같은 아이디가 있을경우 true
			JOptionPane.showMessageDialog(this, "이미 자리 예약을 하셨습니다", "경고", 1);
			System.out.println("예약 거부 " + checkDTO.getId() + " 의 자리" + checkDTO.getSeatName());
		} else if (!checkDTO.isHere()) {
			for (int i = 1; i < jbt1.length; i++) {
				if (e.getSource() == jbt1[i]) { // 1. 해당 id가 seat db에 없을경우
					if (jbt1[i].getName().equals("예약중")) { // 2. 이름이 예약중일경우
						JOptionPane.showMessageDialog(this, "빈자리가 아닙니다", "경고", 1);
					} else if (!check){ // 3. 빈자리라고 적힌경우 드디어 허가
						check = true;
						System.out.println(checkDTO.getId() + " 는 자리예약할 수 있는 상태");
						SeatDTO dto = new SeatDTO();
						dto.setId(id);
						dto.setHere(true);
						dto.setSeatNumber(i);
						dto.setSeatName(jbt1[i].getText());
						dto.setCommand("NewGuest");
						System.out.println(checkDTO.getId() + " 에게 " + i + " 번 자리 내줌");
						try {
							oos.writeObject(dto);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(this, "이미 자리 예약을 하셨습니다", "경고", 1);
					}
				}
			}
		}
	}

	private void viewMaker() {
		seatL = new JLabel("좌석 현황");
		seatL.setFont(new Font("돋움", Font.BOLD, 25));
		seatL.setBounds(340, 15, 200, 40);

		// 버튼 이름 지정
		char seatN = 'A';
		int k = 1;
		int y = 5;
		for (int i = 0; i < 5; i++) {
			for (; k <= y; k++) {
				jbt1[k] = new JButton(seatN + " _ " + k);
			}
			y += 5;
			seatN++;
		}

		// 위치 지정
		int a = 90;
		int b = 190;
		for (int i = 1; i < 6; i++) {
			jbt1[i].setBounds(40, a, 70, 40);
			jbt1[i + 5].setBounds(b, 90, 70, 40);
			jbt1[i + 10].setBounds(b, 180, 70, 40);
			jbt1[i + 15].setBounds(b, 270, 70, 40);
			jbt1[i + 20].setBounds(b, 360, 70, 40);
			a += 70;
			b += 110;
		}

		// 빈자리 체크를 위해서 넣음
		for (int i = 1; i < jbt1.length; i++) {
			jbt1[i].setName("예약가능");
		}

		askB = new JButton("문의하기");
		askB.setFont(new Font("돋움", Font.BOLD, 15));
		askB.setBounds(765, 15, 95, 40);
		add(askB);
		add(seatL);

		setBackground(Color.PINK);
		setLayout(null);

		// 버튼 그리기
		for (int i = 1; i < jbt1.length; i++) {
			add(jbt1[i]);
		}
	}

	public void exit() {
//		if (chatU != null) {
//			chatU.exit();
//		}
		
		
		ExitDTO dto = new ExitDTO();
		dto.setExit("Exit");
		try {
			oos2.writeObject(dto);
			oos2.flush();
		
			oos.writeObject(dto);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public void run() {
		SeatDTO dtoS = null;
		ExitDTO dtoE = null;
		Object temp = null;
		while (true) {
			try {
				temp = ois.readObject();

				if (temp.toString().equals("SeatDTO")) {
					dtoS = (SeatDTO) temp;
				} else if (temp.toString().equals("ExitDTO")) {
					dtoE = (ExitDTO) temp;
				}

				if (dtoS != null) {
					if (dtoS.getCommand().equals("NeedSeatList")) {
						for (SeatDTO data : dtoS.getReserved()) {
							jbt1[data.getSeatNumber()].setText(data.getId() + "가 예약함");
							jbt1[data.getSeatNumber()].setName("예약중");
						}
					} else if (dtoS.getCommand().equals("NewGuest")) {
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getId() + "가 예약함");
						jbt1[dtoS.getSeatNumber()].setName("예약중"); // 빈자리 체크 위해서
					} else if (dtoS.getCommand().equals("IsHere")) {
						System.out.println(dtoS.getId() + " 의  자리가 이미 있는지 검사");
						checkDTO = dtoS;
					} else if (dtoS.getCommand().equals("DeleteGuest")) {
						System.out.println("자리 삭제됨");
						check = false;
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getSeatName());
						jbt1[dtoS.getSeatNumber()].setName("예약가능"); // 빈자리 반환

					}
				}

				if (dtoE != null) {
					if (dtoE.getExit().equals("Exit")) {
						if (oos2 != null)
							oos2.close();
						if (ois2 != null)
							ois2.close();
						if(socket2!=null) socket2.close();
						
						if (oos != null)
							oos.close();
						if (ois != null)
							ois.close();
						if(socket!=null) socket.close();
						break;
					}
				}

			} catch (EOFException e) { // 종료 임시방편
				try {
					if (oos != null) oos.close();
					if (ois != null) ois.close();
					if (socket != null) socket.close();
					if (oos2 != null) oos2.close();
					if (ois2 != null) ois2.close();
					if (socket2!=null) socket2.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}

}