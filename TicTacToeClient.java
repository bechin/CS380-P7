//bechin
import java.net.Socket;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;

public class TicTacToeClient{

	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Scanner kb = new Scanner(System.in);

	public static void main(String[] args)throws IOException{
		new TicTacToeClient();
	}

	public TicTacToeClient(){
		try{
			socket = new Socket("cs380.codebank.xyz", 38007);
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(new ConnectMessage(getUsername()));
			System.out.println("\nNew Game!\n");
			out.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
			Message message;
		dance:	while(true){
				message = (Message)in.readObject();
				switch(message.getType()){
					case BOARD:
						BoardMessage bm = (BoardMessage)message;
						if(bm.getStatus()!=BoardMessage.Status.IN_PROGRESS)
							break dance;
						printBoard(bm.getBoard());
						printChoices(bm.getBoard());
						int position = 0;
						while(!(position > 0 && position < 10)){
							System.out.print("Select a space: ");
							position = kb.nextInt();
							kb.nextLine();
						}
						byte row = (byte)((position-1)/3);
						byte col = (byte)((position-1)%3);
						System.out.println();
						out.writeObject(new MoveMessage(row, col));
						break;
					case ERROR:
						System.out.println(((ErrorMessage)message).getError());
				}
			}
			BoardMessage bm = (BoardMessage)message;
			printBoard(bm.getBoard());
			System.out.println(bm.getStatus());
		}
		catch(Exception e){
			System.out.println("Exception!!!");
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void printChoices(byte[][] board){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(board[i][j]==0)
					System.out.print(3*i+j+1);
				else
					System.out.print(' ');
				if(j != 2)
					System.out.print("|");
			}
			if(i != 2)
				System.out.println("\n-+-+-");
		}
		System.out.println("\n");
	}

	private void printBoard(byte[][] board){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				char c = board[i][j] == 0? ' ':(board[i][j] == 1? 'X':'O');
				System.out.print(c);
				if(j != 2)
					System.out.print("|");
			}
			if(i != 2)
				System.out.println("\n-+-+-");
		}
		System.out.println("\n");
	}

	private String getUsername(){
		System.out.print("Please enter a username: ");
		return kb.nextLine();
	}

}
