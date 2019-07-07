import java.io.IOException;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class test {
	public static void main(String[] args) throws IOException {
		Socket socket = null;
		String[] Result = { "Success", "Fail" };
		String key = null;
		String Comparison = null;
		String Prkey = null;
		String Num = null;
		try {
			socket = new Socket("172.30.98.101", 5500);// Server로 연결
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());
			
		 	String inputLine = in.readUTF();// Server에서 받은 데이터저장
			if (inputLine.equals("Connect")) {// 연결
				System.out.println("연결성공");
			while (true) {
			
				inputLine = in.readUTF();// Server로부터 받은 명령어 저장
				Comparison = "fail";
				String Re[] = BlockChain.Block(Num, inputLine, Comparison, Prkey);
				//DB Server 데이터블럭 무결성 검증 및 Server로 부터 받은 Hash 변조유무 확인
				//DB에서 검증이끝나면 마지막 데이터블럭 가져와서 저장
				switch(Re[2]) {// 올바른 해쉬값을 DB Server에 대칭하여 Command 부분 을 가져와 비교
				case "Start" : {
					System.out.println("명령어를 받았습니다");
					Process oProcess = new ProcessBuilder("notepad.exe").start();
					Re[1]=SHA256.SHA256(Re[0]+Re[2]+Re[1]);
					BlockChain.Hash(Re[0], Re[1], Re[2], inputLine);
					out.writeUTF(Re[1]);
					System.out.println("전송했습니다.");
					break;
				}
				case "Sleep":{
					System.out.println("명령어를 받았습니다");
					Process oProcess = new ProcessBuilder("cmd", "/c", "rundll32.exe PowrProf.dll,SetSuspendState 0,1,0").start();
					Re[1]=SHA256.SHA256(Re[0]+Re[2]+Re[1]);
					BlockChain.Hash(Re[0], Re[1], Re[2], inputLine);
					out.writeUTF(Re[1]);
					System.out.println("전송했습니다.");
					break;
				}
				case "Poweroff" :{
					System.out.println("명령어를 받았습니다");
					Process oProcess = new ProcessBuilder("cmd","/c","shutdown /s /f /t 10").start();
					Re[1]=SHA256.SHA256(Re[0]+Re[2]+Re[1]);
					BlockChain.Hash(Re[0], Re[1], Re[2], inputLine);
					out.writeUTF(Re[1]);
					System.out.println("전송했습니다.");
					break;
				}
				case"fail": {
					out.writeUTF("fail");
					Comparison = "fail";
					break;
				}
				}		
			}
			}
			
		} catch (Exception e) {
			System.out.println("서버가 강제종료했습니다.");
		}
	}
}
