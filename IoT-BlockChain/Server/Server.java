import java.io.DataInputStream;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(5500);
			System.out.println(" ____________\n" + "| PC 연결 대기중 |\n" + "------------->\n");
			Socket pcS = serverSocket.accept();
			System.out.println(" _________________\n" + "| PC가 연결 성공 했습니다 |\n" + "-------------------\n");
			System.out.println(" _____________\n" + "| 단말기 연결 대기중 |\n" + "--------------->\n");
			while (true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println(" __________________\n" + "| 단말기 연결 성공 했습니다 |\n" + "-------------------\n");
				new MultiThread(clientSocket, pcS).start();
			}
		} catch (Exception e) {
			System.out.println("서버가 강제 종료 했습니다.");
		}
	}
}

class MultiThread extends Thread {
	Socket clientSocket = null;
	Socket pcS = null;
	String[] Command = { "Sleep", "Start", "Poweroff", "Disconnect" };
	public static String Cnt;
	String Comparison = null;
	String key = null;
	String Num = null;
	String Prkey = null;

	public MultiThread(Socket clientSocket, Socket pcS) {
		this.clientSocket = clientSocket;
		this.pcS = pcS;
	}

	public void run() {
		try {

			DataInputStream in = new DataInputStream(clientSocket.getInputStream());// Android에서 받는 데이터
			DataInputStream pcin = new DataInputStream(pcS.getInputStream());// PC에서 받는 데이터
			DataOutputStream pcout = new DataOutputStream(pcS.getOutputStream());// PC로 주는 데이터
			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());// 안드로이드로 주는 데이터
			System.out.println("----------------\n" + "|PC와 단말기 연결 성공|\n" + "----------------\n");
			pcout.writeUTF("Connect");
			System.out.println("--------------------\n" + "| 단말기 명령어 입력중... |\n" + "--------------------\n");

			String inputLine = in.readUTF();
			
			if (inputLine.equals("Connect")) { // 입력받은 명령어 비교 부분
				System.out.println("연결성공");

				while (true) { // 연결이 됬을시 while 반복문을 통해서 추후 명령어 실행 가능하게하는 부분
					String pcLine = null;

					inputLine = in.readUTF(); // 추후에 받을 명령어 저장
					MultiThread.sleep(3000);
					Comparison = "fail";
					String Re[] = BlockChain.Block(Num, inputLine, Comparison, Prkey);
					// 블록체인 체크 DB Server 무결성 체크 Android로부터 받은 데이터블럭 같이 체크
					switch (Re[2]) {// DB Server에서 무결성 검증후 완료된 마지막 블럭중 Command 부분 비교
					case "Start": {

						System.out.println("《PC가 실행중입니다.....》");
						Re[1]=SHA256.SHA256(Re[0]+Re[2]+Re[1]);// SHA256 암호호 하여 Hash 키값저장
						BlockChain.Hash(Re[0], Re[1], Re[2], inputLine); //데이터블럭 DB Server에 저장
						pcout.writeUTF(Re[1]); //Hash key 값 PC로 전송

						// Check 요응로 넣어둠
						pcLine = pcin.readUTF();// PC 응답 받는 부분
						String pRe[] = BlockChain.Block(Num, pcLine, Comparison, Prkey);// DB에 저장된 블럭들 변수저장
						
						System.out.println("Check");
						if(pcLine.equals(pRe[1])) {// DB에서 가져온 Hash key 값과  PC로 부터 받은 Hash key 값 비교
							System.out.println(" PC가 실행 되었습니다");
							pRe[1]=SHA256.SHA256(pRe[0]+pRe[2]+pRe[1]);// 다시 암호화
							BlockChain.Hash(pRe[0], pRe[1], pRe[2], pcLine);// 데이터블럭 DB에 저장
							out.writeUTF(pRe[1]);// Android 에게전달
						}else {
							System.out.println("수상한 명령어 입니다");
							out.writeUTF("fail");
						}
						break;
					}// 이하 코드는 상위 코드와 동일 하다.
					case "Disconnect":{
						System.out.println("《PC가 종료중입니다....》");
						pcout.writeUTF(Command[3]);
						pcLine = pcin.readUTF();
						if(pcLine.equals("Success")) 
						{
							System.out.println("《PC 종료 성공》");
							out.writeUTF("종료했습니다.");
							System.out.println("《단말기한테 성공 전송》");
						}
						else if(pcLine.equals("Fail"))
						{
							System.out.println("PC가 종료하지 못했습니다.");
							out.writeUTF("종료에 실패했습니다.");
						}	
						break;
					}
					case "Sleep":{
						System.out.println("《PC가 절전중입니다.....》");
						Re[1]=SHA256.SHA256(Re[0]+Re[2]+Re[1]);
						BlockChain.Hash(Re[0], Re[1], Re[2], inputLine);
						pcout.writeUTF(Re[1]);

						// Check 요응로 넣어둠
						pcLine = pcin.readUTF();
						String pRe[] = BlockChain.Block(Num, pcLine, Comparison, Prkey);
						
						System.out.println("Check");
						if(pcLine.equals(pRe[1])) {
							System.out.println(" PC가 절전 되었습니다");
							pRe[1]=SHA256.SHA256(pRe[0]+pRe[2]+pRe[1]);
							BlockChain.Hash(pRe[0], pRe[1], pRe[2], pcLine);
							out.writeUTF(pRe[1]);
						}else {
							System.out.println("수상한 명령어 입니다");
							out.writeUTF("fail");
						}
						break;
					}
					case "Poweroff":{
						System.out.println("《PC가 종료중입니다.....》");
						Re[1]=SHA256.SHA256(Re[0]+Re[2]+Re[1]);
						BlockChain.Hash(Re[0], Re[1], Re[2], inputLine);
						pcout.writeUTF(Re[1]);

						// Check 요응로 넣어둠
						pcLine = pcin.readUTF();
						String pRe[] = BlockChain.Block(Num, pcLine, Comparison, Prkey);
						
						System.out.println("Check");
						if(pcLine.equals(pRe[1])) {
							System.out.println(" PC가 종료 되었습니다");
							pRe[1]=SHA256.SHA256(pRe[0]+pRe[2]+pRe[1]);
							BlockChain.Hash(pRe[0], pRe[1], pRe[2], pcLine);
							out.writeUTF(pRe[1]);
						}else {
							System.out.println("수상한 명령어 입니다");
							out.writeUTF("fail");
							Comparison = "fail";
						}
						break;
					}
					case "fail": {
						out.writeUTF("fail");
						Comparison = "fail";
						break;
					}
					}
				}
			} else
				System.out.println("연결실패 재연결 바랍니다.");

		} catch (Exception e) {
			System.out.println("단말기가 강제종료했습니다.");
			System.out.println(" ___________________\n" + "| 단말기 재연결 요청중.... |\n" + "--------------------->\n");
		}
	}
}
