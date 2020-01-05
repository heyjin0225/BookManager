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
	
	private String id; // id ���߿� �����ڷ� ���۵� ������ �ٲ��־����
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

		// ��ư0 ������
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


			// ���� ä�ÿ��� ������ Join ���� -> �����ڿ��� new ������
			ChatDTO dto = new ChatDTO();
			String msg = JOptionPane.showInputDialog(this, "������ ������ �Է��ϼ���");
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
		} // ���� ������ id�� �̹� �ڸ������� �س����� �˻����ؼ� ����
		

		try {
			Thread.sleep(450);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		} // �˻��� �޾ƿ������� ������ ���� �ѱ�°� ����

		if (e.getSource() == jbt1[checkDTO.getSeatNumber()]) { // ���� �ڸ��� checkDTO �� �ڸ��ϰ��
			System.out.println(checkDTO.getId() + " ������� ��û");
			checkDTO.setCommand("DeleteGuest"); // ������ҿ�û
			try {
				oos.writeObject(checkDTO);
				oos.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		if (checkDTO.isHere()) { // �������� ���� ID���� ���� seat db�� ���� ���̵� ������� true
			JOptionPane.showMessageDialog(this, "�̹� �ڸ� ������ �ϼ̽��ϴ�", "���", 1);
			System.out.println("���� �ź� " + checkDTO.getId() + " �� �ڸ�" + checkDTO.getSeatName());
		} else if (!checkDTO.isHere()) {
			for (int i = 1; i < jbt1.length; i++) {
				if (e.getSource() == jbt1[i]) { // 1. �ش� id�� seat db�� �������
					if (jbt1[i].getName().equals("������")) { // 2. �̸��� �������ϰ��
						JOptionPane.showMessageDialog(this, "���ڸ��� �ƴմϴ�", "���", 1);
					} else if (!check){ // 3. ���ڸ���� ������� ���� �㰡
						check = true;
						System.out.println(checkDTO.getId() + " �� �ڸ������� �� �ִ� ����");
						SeatDTO dto = new SeatDTO();
						dto.setId(id);
						dto.setHere(true);
						dto.setSeatNumber(i);
						dto.setSeatName(jbt1[i].getText());
						dto.setCommand("NewGuest");
						System.out.println(checkDTO.getId() + " ���� " + i + " �� �ڸ� ����");
						try {
							oos.writeObject(dto);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(this, "�̹� �ڸ� ������ �ϼ̽��ϴ�", "���", 1);
					}
				}
			}
		}
	}

	private void viewMaker() {
		seatL = new JLabel("�¼� ��Ȳ");
		seatL.setFont(new Font("����", Font.BOLD, 25));
		seatL.setBounds(340, 15, 200, 40);

		// ��ư �̸� ����
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

		// ��ġ ����
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

		// ���ڸ� üũ�� ���ؼ� ����
		for (int i = 1; i < jbt1.length; i++) {
			jbt1[i].setName("���డ��");
		}

		askB = new JButton("�����ϱ�");
		askB.setFont(new Font("����", Font.BOLD, 15));
		askB.setBounds(765, 15, 95, 40);
		add(askB);
		add(seatL);

		setBackground(Color.PINK);
		setLayout(null);

		// ��ư �׸���
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
							jbt1[data.getSeatNumber()].setText(data.getId() + "�� ������");
							jbt1[data.getSeatNumber()].setName("������");
						}
					} else if (dtoS.getCommand().equals("NewGuest")) {
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getId() + "�� ������");
						jbt1[dtoS.getSeatNumber()].setName("������"); // ���ڸ� üũ ���ؼ�
					} else if (dtoS.getCommand().equals("IsHere")) {
						System.out.println(dtoS.getId() + " ��  �ڸ��� �̹� �ִ��� �˻�");
						checkDTO = dtoS;
					} else if (dtoS.getCommand().equals("DeleteGuest")) {
						System.out.println("�ڸ� ������");
						check = false;
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getSeatName());
						jbt1[dtoS.getSeatNumber()].setName("���డ��"); // ���ڸ� ��ȯ

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

			} catch (EOFException e) { // ���� �ӽù���
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