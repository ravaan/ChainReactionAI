import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/* Shit i want to do:
 * 1. An infinite undo button (reverse chain reaction) [is any information lost while chain reacting?]
 * 2. If yes, use a movelog to do the undos.
 * 3. Store game data in a file to reload as and when required.
 * 4. Analyze game data in a file to understand blunders and so on using board evaluation
 * 8. How does dimensions affect play?
 * 9. Use primed in Board.java code to ease things
 */

public class Driver {

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		//Dimensions
		int row_dim =9;
		int col_dim =6;

		//9x6 standard board
		Board b = new Board(row_dim,col_dim);

		//Shows board
		b.printBoard();

		/*'p' stands for player
		 * 'move' takes 2-digit number from user (row digit followed by column digit
		 * 'r' stores row digit
		 * 'c' stores column digit
		 * 'exit' stores exit condition for the game (aka victory)
		 * 'moves' measures number of moves played till now (needed for victory condition)
		 */
		int p=-1,move,r,c;
		boolean exit=false;
		int moves=0;
		//Remark - 2 moves make up one move cycle (When a move cycle is completed, both players have played equal number of times)
		
		//The turn keeps alternating due to changes made to p. Each run of the loop represents half a move
		do{ 
			p=p*(-1);
			System.out.print("Player "+(int)(p*(-0.5)+1.5)+" turn (0 to exit): ");
			try {
				//Get the move
				move = Integer.parseInt(br.readLine());

			} catch (NumberFormatException e) {
				System.out.println("Error: Not a valid number");
				//ERROR condition for a player: revert the p value
				p=p*(-1);
				continue;
			}

			//Get row index
			r = move/10; 
			//Get column index
			c = move%10; 
			
			//Player chooses to leave the game
			if(move==0){
				exit=true;
				continue;
			}

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

			System.out.println("\n");
			b.printBoard();
			System.out.println("Move count: "+moves);
			//Check for victory after one turn cycle
			if(b.testVictory(p) && moves>2){ 
				System.out.println("Player "+(int)(p*(-0.5)+1.5)+" WINS!\n\n");
				exit=true;
				continue;
			}
		}while(!exit);
	}
}
