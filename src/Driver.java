import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class Driver {
	
/* Shit  I wanna do:
 * 1. Enforce connectedness as good
 * 2. Enforce chains of explosions as good,provided not in danger.
 * 3. Ignore squares which are not at distance 1 or 2 from enemy or distance 1 from yourself.
 * 4. Enforce compact connectedness as better than lean connectedness
 * 5. Try learning heuristic with simulations

 * 1. An infinite undo button (reverse chain reaction) [is any information lost while chain reacting?]
 * 2. If yes, use a movelog to do the undos.
 * 3. Reload games as and when required.
 * 4. Analyze game data in a file to understand blunders and so on using board evaluation
 * 5. How does dimensions affect play?
 */
	//IO Code
	static BufferedWriter writer;
	static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {
		/* VARIABLE DESCRIPTION:
		 * exit: Controls exit condition for menu
		 * choice: User's menu choice
		 */
		boolean exit=false;
		int choice;


		do{
			System.out.println("\n*****************CHAIN REACTION************");
			System.out.println("1. Start new game");
			System.out.println("2. Load a game");
			System.out.println("3. Simulate a game");
			System.out.println("4. Exit");
			System.out.println("*******************************************");
			System.out.print("\n\n Enter choice(1,2,3): ");
			try{
				choice = Integer.parseInt(br.readLine());
			}catch(NumberFormatException num){
				System.out.println("Error: Not a Number! \n");
				continue;
			}
			switch(choice){
			case 1:
				Board b=new Board(9,6);
				startGame(-1,new ArrayList<Integer>(0),9,6,b,0,0);
				break;
			case 2:
				loadGame();
				break;
			case 3:
				simulateGame(9,6);
				break;
			case 4: 
				exit =true;
				break;
			default: System.out.println("Error: Invalid Choice! \n");
			}
		}while(!exit);
		System.out.println("Exiting...");
	}

	private static void simulateGame(int row_dim,int col_dim) throws NumberFormatException, IOException {
		System.out.print("Player 1 is (0-random)(1-smart): ");
		int p1_type = Integer.parseInt(br.readLine());
		System.out.print("Player 2 is (0-random)(1-smart): ");
		int p2_type = Integer.parseInt(br.readLine());
		Board b=new Board(row_dim,col_dim);
		int[] choices = new int[row_dim*col_dim];
		int r_1=1,c_1=1,p=-1,move=0,r=0,c=0;
		boolean exit=false;
		ArrayList<Integer> movelog = new ArrayList<Integer>();
		int moves=movelog.size();
		AI robo=null;
		if(p1_type==1 || p2_type==1)
			robo = new AI();
				
		for(int i=0;i<choices.length;i++){
			if(c_1==7){
				c_1=1;
				r_1++;
			}
			choices[i] = r_1*10 + c_1;
			c_1++;
		}

		//The turn keeps alternating due to changes made to p. Each run of the loop represents half a move
		do{ 
			p=p*(-1);
			//Right now, only handling random player cases
			if((p1_type==0 && p==1) || (p2_type==0 && p==-1))
				move = choices[new Random().nextInt(choices.length)];
			else if((p1_type==1 && p==1) || (p2_type==0 && p==-1))
				move = robo.findBestMove(b.getBoard(), row_dim, col_dim, p);
						
			//Get row index
			r = move/10; 
			//Get column index
			c = move%10; 

			//Check if square is already occupied. Invalid in case of a direct move
			if(b.isOccupied(r-1,c-1,p)) {
				p=p*(-1);	
				continue;
			}

			//Make the move
			b.makeMove(r-1,c-1,p);

			//Move has occurred at this point, so increment moves
			moves++;
			System.out.println("MOVE: "+moves+": "+move);
			System.out.println("MOVELOG: "+movelog);
			movelog.ensureCapacity(moves);
			movelog.add(move);

			//Check for victory after one turn cycle
			if(b.testVictory(p) && moves>2){ 
				System.out.println("Player "+(int)(p*(-0.5)+1.5)+" WINS!\n\n");
				exit=true;
				b.printBoard();
				System.out.println(movelog);
				continue;
			}
		}while(!exit);
		
		
	}

	//Load a game. Not working currently
	private static void loadGame() throws IOException {
		System.out.print("Enter save file name (without extension): ");
		String fname = br.readLine();
		List<String> lines = Files.readAllLines(Paths.get(fname+".txt"));
		System.out.println(lines);
		int p = Integer.parseInt(lines.get(0).trim());
		int l=0,m=0;
		int[][] c=new int[9][6];
		System.out.println("P is "+p);
		for(int i=1;i<=9;i++){
			StringTokenizer tokenizer = new StringTokenizer(lines.get(i).trim(), " ");
			while(tokenizer.hasMoreTokens())
			{
				if(m==6){
					m=0;
					l++;
				}
				System.out.println(l+","+m+": "+tokenizer.nextToken());
				c[l][m]=Integer.parseInt(tokenizer.nextToken());	        
				m++;
			}
		}
		Board b =new Board(9,6);
		b.setBoard(c);
		b.printBoard();

	}

	/*Starts a fresh game
	 * p1: Starting player. p1=-1 for first player's move
	 * a1: List of moves already played
	 * row_dim, col_dim: Board dimensions
	 * pl: Plus player's cell counts (do these auto later)
	 * mi: Minus player's cell counts
	 */
	private static void startGame(int p1,ArrayList<Integer> a1,int row_dim,int col_dim,Board b1,int pl,int mi) throws IOException {
		/* VARIABLE DESCRIPTIONS
		 * exit: Defines exit condition for loop
		 * p: Current player (1 or -1)
		 * move: User input for move. Two-digit number (row, then column)
		 * r,c: Board dimensions
		 * savename: File name for saving game
		 * movelog: List of moves already played
		 * b: Board object
		 */
		boolean exit=false;
		int p=p1,move,r=row_dim,c=col_dim;
		String savename;
		ArrayList<Integer> movelog = a1;
		int moves=movelog.size();
		Board b = b1;
		
		//Initial settings
		b.printBoard();
		b.set_minus_count(mi);
		b.set_plus_count(pl);

		//The turn keeps alternating due to changes made to p. Each run of the loop represents half a move
		do{ 
			p=p*(-1);
			System.out.print("Player "+(int)(p*(-0.5)+1.5)+" turn (1:Save|2:Load|3:Exit): ");
			try {
				//Get the move
				move = Integer.parseInt(br.readLine());

			} catch (NumberFormatException e) {
				System.out.println("Error: Not a valid number");
				//ERROR condition for a player: revert the p value
				p=p*(-1);
				continue;
			}

			//Saving game file. We need to store p,board and movelog (counts are optional)
			if(move==1){
				System.out.print("Enter savefile name: ");
				savename = br.readLine();
				writer = new BufferedWriter(new FileWriter(savename+".txt"));
				writer.write(p+" ");
				writer.newLine();
				int temp[][] =b.getBoard();
				for(int i=0;i<row_dim;i++){
					for(int j=0;j<col_dim;j++){
						writer.write(temp[i][j]+" ");
					}
					writer.newLine();
				}
				writer.write(b.get_plus_count()+" "+b.get_minus_count());
				writer.newLine();
				for(int i:movelog)
					writer.write(i+" ");
				writer.newLine();
				writer.close();
				p=p*(-1);
				continue;
			}
			
			if(move==2){
				loadGame();
				exit=true;
				continue;
			}

			//Player chooses to leave the game
			if(move==3){
				exit=true;
				continue;
			}
			
			//Get row index
			r = move/10; 
			//Get column index
			c = move%10; 
			
			//Ensure that the move is within the board limits (1...9,1...6)
			if(r<1 || r>row_dim || c<1 || c>col_dim){ 
				System.out.println("Error: Move out of bounds");
				p=p*(-1);
				continue;
			}

			//Check if square is already occupied. Invalid in case of a direct move
			if(b.isOccupied(r-1,c-1,p)) {
				System.out.println("Error: Square already occupied");
				p=p*(-1);
				continue;
			}

			//Make the move
			b.makeMove(r-1,c-1,p);

			//Move has occurred at this point, so increment moves
			moves++;
			movelog.ensureCapacity(moves);
			movelog.add(move);

			System.out.println("\n");
			b.printBoard();
			System.out.println("Move count: "+moves);
			System.out.println("Move log: "+movelog);
			//Check for victory after one turn cycle
			if(b.testVictory(p) && moves>2){ 
				System.out.println("Player "+(int)(p*(-0.5)+1.5)+" WINS!\n\n");
				exit=true;
				continue;
			}
		}while(!exit);

	}
}