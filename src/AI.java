public class AI {
	int r=9,c=6;
	int evaluate(Board b,int p){
		int[][] board = b.getBoard();
		//w1 is the difference between cell blocks held
		//w2 is the difference between primed blocks held
		//w3 is the difference between sum of balls held
		//w4 returns a coefficient representing rectillinear adjacency. More adjacency is better.
		//w5 returns a coefficient representing aggression and vulnerability (How close are explosives of the opponent)
		int explosive_owner=0;
		int w1 = 4*p*(b.get_plus_count() - b.get_minus_count());
		int w2=0;
		int w3=0;
		int w4=0;
		int w5=0;
		boolean[][] primed = b.get_primed();
		for(int i=0;i<r;i++){
			for(int j=0;j<c;j++){
				if(board[i][j]!=0) {
					w3 = w3 + 4*p*board[i][j];

					switch(b.get_type(i, j)){
					case CORNER:

						if(primed[i][j]==true) {
							explosive_owner = board[i][j]/Math.abs(board[i][j]);
							w2=w2+4*p*explosive_owner; //4 added if p is the same as explosive_owner
						}
						//2 is added if explosive_owner is p and board cell owner is not p
						if(i==0 && j==0){
							//1 is added if you have an adjacency, -1 if other person has an adjacency
							if(board[i][j]*board[i+1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i][j+1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);

							if(primed[i][j]==true){
								if(board[i][j+1]!=0 && board[i][j+1]/Math.abs(board[i][j+1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i+1][j]!=0 && board[i+1][j]/Math.abs(board[i+1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
							}
						}

						else if(i==0 && j==c-1){
							if(board[i][j]*board[i][j-1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i+1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);

							if(primed[i][j]==true){
								if(board[i][j-1]!=0 && board[i][j-1]/Math.abs(board[i][j-1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i+1][j]!=0 && board[i+1][j]/Math.abs(board[i+1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
							}
						}

						else if(i==r-1 && j==0){
							if(board[i][j]*board[i][j+1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i-1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);

							if(primed[i][j]==true) {
								if(board[i][j+1]!=0 && board[i][j+1]/Math.abs(board[i][j+1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i-1][j]!=0 && board[i-1][j]/Math.abs(board[i-1][j])!= explosive_owner)	
									w5 = w5 + 2*explosive_owner*p;
							}
						}

						else if(i==r-1 && j==c-1){
							if(board[i][j]*board[i][j-1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i-1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);

							if(primed[i][j]==true) {
								if(board[i][j-1]!=0 && board[i][j-1]/Math.abs(board[i][j-1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i-1][j]!=0 && board[i-1][j]/Math.abs(board[i-1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
							}
						}
						break;
					case SIDE:
						if(primed[i][j]==true) {
							explosive_owner = board[i][j]/Math.abs(board[i][j]);
							w2=w2+4*p*explosive_owner; //1 added if p is the same as explosive_owner
						}
						if(i==0){
							if(board[i][j]*board[i+1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i][j-1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i][j+1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							
							if(primed[i][j]==true) {
								if(board[i+1][j]!=0 && board[i+1][j]/Math.abs(board[i+1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;

								if(board[i][j-1]!=0 && board[i][j-1]/Math.abs(board[i][j-1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;

								if( board[i][j+1]!=0 && board[i][j+1]/Math.abs(board[i][j+1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
							}
						}
						else if(i==r-1){
							if(board[i][j]*board[i-1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i][j-1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i][j+1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
														
							if(primed[i][j]==true) {
								if(board[i-1][j]!=0 && board[i-1][j]/Math.abs(board[i-1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;

								if(board[i][j-1]!=0 && board[i][j-1]/Math.abs(board[i][j-1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;

								if(board[i][j+1]!=0 && board[i][j+1]/Math.abs(board[i][j+1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;

							}
						}
						else if(j==0){
							if(board[i][j]*board[i][j+1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i+1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i-1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							
							if(primed[i][j]==true){
								if(board[i][j+1]!=0 && board[i][j+1]/Math.abs(board[i][j+1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i+1][j]!=0 && board[i+1][j]/Math.abs(board[i+1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i-1][j]!=0 && board[i-1][j]/Math.abs(board[i-1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
							}
						}
						else if(j==c-1){
							if(board[i][j]*board[i][j-1]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i+1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							if(board[i][j]*board[i-1][j]>0)
								w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
							
							if(primed[i][j]==true){
								if(board[i][j-1]!=0 && board[i][j-1]/Math.abs(board[i][j-1])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i+1][j]!=0 && board[i+1][j]/Math.abs(board[i+1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
								if(board[i-1][j]!=0 && board[i-1][j]/Math.abs(board[i-1][j])!= explosive_owner)
									w5 = w5 + 2*explosive_owner*p;
							}
						}


						break;
					case INTERIOR:
						if(board[i][j]*board[i][j-1]>0)
							w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
						if(board[i][j]*board[i][j+1]>0)
							w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
						if(board[i][j]*board[i+1][j]>0)
							w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
						if(board[i][j]*board[i-1][j]>0)
							w4 = w4 + 2*p*board[i][j]/Math.abs(board[i][j]);
						
						if(primed[i][j]==true) {
							explosive_owner = board[i][j]/Math.abs(board[i][j]);
							w2=w2+4*p*explosive_owner; //1 added if p is the same as explosive_owner

							if(board[i][j-1]!=0 && board[i][j-1]/Math.abs(board[i][j-1])!= explosive_owner)
								w5 = w5 + 2*explosive_owner*p;

							if(board[i][j+1]!=0 && board[i][j+1]/Math.abs(board[i][j+1])!= explosive_owner)
								w5 = w5 + 2*explosive_owner*p;

							if(board[i+1][j]!=0 && board[i+1][j]/Math.abs(board[i+1][j])!= explosive_owner)
								w5 = w5 + 2*explosive_owner*p;

							if(board[i-1][j]!=0 && board[i-1][j]/Math.abs(board[i-1][j])!= explosive_owner)
								w5 = w5 + 2*explosive_owner*p;
						}
					}
					break;
				}

			}
		}



		//System.out.println("SCORES for player "+p+":\n\t1. Cell diff="+w1+"\n\t2. Primed diff="+w2+"\n\t3. Ball sum diff = "+w3+"\n\t4. Adjacency="+w4+"\n\t5. Attackability="+w5);
		int sum = w1+w2+w3+w4+w5;
		System.out.println("Score for player "+p+"= "+(sum));
		return sum;
	}

	int minimax(Board b, int p, int depth, boolean isMax){
		int score = evaluate(b,p);
		int[][] board = b.getBoard();
		if(depth>6 || score==100 || score==-100)
			return score;
		int best = -1000;
		if (isMax)
		{
			for (int i = 0; i<r; i++)
			{
				for (int j = 0; j<c; j++)
				{
					//Make move
					best = Math.max(best, minimax(b, p, depth+1, !isMax) );
					//Undo move
				}
			}
			return best;
		}

		// If this is minimizer's move
		else
		{
			best = 1000;

			// Traverse all cells
			for (int i = 0; i<r; i++)
			{
				for (int j = 0; j<c; j++)
				{
					// Make the move
					best = Math.min(best, minimax(b, p, depth+1, !isMax));
					// Undo the move
				}
			}
			return best;
		}
	}

	int findBestMove(Board b,int ro,int co,int p){
		r=ro;
		c=co;
		return 11;
	}
}