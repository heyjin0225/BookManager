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
	
	private String id;				//id ���߿� �����ڷ� ���۵� ������ �ٲ��־����
	private SeatDTO checkDTO = new SeatDTO();
	
	private AdminA adminA;
	//ä��
	
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
		
		//��ư0 ������
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
		
		//ä��
		//chatB.addActionListener(this);
		
		Thread t = new Thread(this);
		t.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
	
		for(int i = 1; i < jbt1.length; i++) {
			if(e.getSource()==jbt1[i]){		//1. �ش� id�� seat db�� �������
				if(jbt1[i].getName().equals("������")) {	//2. �̸��� �������ϰ��
					String user = jbt1[i].getText().substring(0, jbt1[i].getText().lastIndexOf('��'));
					int result = JOptionPane.showConfirmDialog(this, user+" �������ðڽ��ϱ�?");
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
		
		
		
		
		//ä��
	}
	private void viewMaker() {
		seatL = new JLabel("�¼� ��Ȳ");
		seatL.setFont(new Font("����",Font.BOLD,25));
		seatL.setBounds(340, 15, 200, 40);
		
		//��ư �̸� ����
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
		
		//��ġ ����
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
		
		//���ڸ� üũ�� ���ؼ� ����
		for(int i = 1; i < jbt1.length; i++) {
			jbt1[i].setName("���డ��");
		}
			
		setBackground(new Color(153,214,234));
		setLayout(null);
		
		//��ư �׸���
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
							jbt1[data.getSeatNumber()].setText(data.getId()+"�� ������");
							jbt1[data.getSeatNumber()].setName("������");
						}
					}else if(dtoS.getCommand().equals("NewGuest")) {
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getId()+"�� ������");
						jbt1[dtoS.getSeatNumber()].setName("������"); 		//���ڸ� üũ ���ؼ�
					}else if(dtoS.getCommand().equals("IsHere")) {
						System.out.println(dtoS.getId() + " ��  �ڸ��� �̹� �ִ��� �˻�");
						checkDTO = dtoS;
					}else if(dtoS.getCommand().equals("DeleteGuest")) {
						System.out.println("�ڸ� ������");
						jbt1[dtoS.getSeatNumber()].setText(dtoS.getSeatName());
						jbt1[dtoS.getSeatNumber()].setName("���డ��");		//���ڸ� ��ȯ
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
			} catch (EOFException e) {	//���� �ӽù���
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