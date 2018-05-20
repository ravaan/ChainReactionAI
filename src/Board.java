
public class Board {
	/* b: 2D signed array. Sign implies player, Magnitude implies number of balls
	 * r,c: Stores dimensions of array
	 * Type: Defines type of block
	 * primed: true if cell can explode in the next turn.
	 * plus_count: Counts p=1 squares
	 * minus_count: Counts p=-1 squares
	 * Type array: Defines type of squares. Necessary?
	 */
	int[][] b;
	int r,c;
	enum Type {
		INTERIOR,SIDE,CORNER 
	}
	boolean[][] primed;
	int plus_count=0,minus_count=0;
	Type[][] type;

	/* m: number of rows
	 * n: number of columns
	 */
	public Board(int m, int n){ 
		r=m-1;
		c=n-1;
		b=new int[m][n];
		primed=new boolean[m][n];
		type = new Type[m][n];

		//Logic to label square type
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				if(i==0 || i==m-1){
					if(j==0 || j==n-1)
						type[i][j] = Type.CORNER;
					else
						type[i][j] = Type.SIDE;
					//System.out.print(type[i][j]+" ");
					continue;
				}
				if(j==0 || j==n-1){
					if(i!=0 && i!=m-1)
						type[i][j] = Type.SIDE;
					//System.out.print(type[i][j]+" ");
					continue;
				}
				type[i][j]=Type.INTERIOR;
				//System.out.print(type[i][j]+" ");
			}
		}
	}

	int[][] getBoard(){
		return b;
	}
	
	Type get_type(int i,int j){
		return type[i][j];
	}
	
	boolean[][] get_primed(){
		return primed;
	}
	
	void setBoard(int[][] board){
		b=board;
	}
	int get_plus_count(){
		return plus_count;
	}
	int get_minus_count(){
		return minus_count;
	}
	void set_plus_count(int pl){
		plus_count = pl;
	}
	void set_minus_count(int mi){
		minus_count = mi;
	}
	
	//Prints the board
	void printBoard(){ 
		System.out.print("\n\t\t\tBOARD");
		System.out.println("\n**********************************************");
		for(int i=0;i<b.length;i++){
			for(int j=0;j<b[0].length;j++){
				System.out.format("%3d ", b[i][j]);
			}
			System.out.println();
		}
		System.out.println("**********************************************");
		System.out.println("Player 1 square count: "+plus_count);
		System.out.println("Player 2 square count: "+minus_count);
	}

	//Checks if square chosen is occupied by enemy
	boolean isOccupied(int row,int col,int p){
		if(b[row][col]*p<0){
			return true;
		}
		return false;
	}

	//Execute move at (row,column) for player p
	void makeMove(int row,int col,int p){ 
		//System.out.println("Working on cell ("+(row+1)+","+(col+1)+") with minus = "+minus_count+", plus= "+plus_count);
		//If cell is occupied,override it and change counts. Condition fulfilled only during chain reactions.
		if(isOccupied(row,col,p)){
			if(p==1){
				plus_count++;
				minus_count--;
			}
			if(p==-1){
				plus_count--;
				minus_count++;
			}
		}

		//If cell is about to blow up, call chainReact
		if(primed(row,col)){
			chainReact(row,col,p);	
			return;
		}


		//Add 1 to the block else
		b[row][col]=(Math.abs(b[row][col])+1)*p;

		if(b[row][col]==1)
			plus_count++;
		else if(b[row][col]==-1)
			minus_count++;

		//Check if cell can blow up in the next turn
		if(primed(row,col))
			primed[row][col]=true;

		//System.out.println("Finished working on cell ("+(row+1)+","+(col+1)+") with minus = "+minus_count+", plus= "+plus_count);
	}

	//Check if p has achieved victory.
	boolean testVictory(int p){
		//Victory if opposite team has lost all squares
		if(plus_count==0 && p==-1)
			return true;
		if(minus_count==0 && p==1)
			return true;
		return false;
	}

	//Compute chain reactions caused by explosion at cell[row][col] by p.
	void chainReact(int row, int col, int p) {
		//System.out.println("Chain reacting on cell ("+(row+1)+","+(col+1)+") with minus = "+minus_count+", plus= "+plus_count);
		if(p==1)
			plus_count--;
		if(p==-1)
			minus_count--;
		b[row][col]=0;
		switch(type[row][col]){
		case CORNER:
			if(row==0 && col==0){
				makeMove(row,col+1,p);
				if(testVictory(p))
					break;
				makeMove(row+1,col,p);
				if(testVictory(p))
					break;
			}
			else if(row==0 && col==c){
				makeMove(row,col-1,p);
				if(testVictory(p))
					break;
				makeMove(row+1,col,p);
				if(testVictory(p))
					break;
			}
			else if(row==r && col==0){
				makeMove(row,col+1,p);
				if(testVictory(p))
					break;
				makeMove(row-1,col,p);
				if(testVictory(p))
					break;
			}
			else if(row==r && col==c){
				makeMove(row,col-1,p);
				if(testVictory(p))
					break;
				makeMove(row-1,col,p);
				if(testVictory(p))
					break;
			}
			break;
		case SIDE:
			if(row==0){
				makeMove(row+1,col,p);
				makeMove(row,col-1,p);
				makeMove(row,col+1,p);
			}
			else if(row==r){
				makeMove(row-1,col,p);
				makeMove(row,col-1,p);
				makeMove(row,col+1,p);

			}
			else if(col==0){
				makeMove(row,col+1,p);
				makeMove(row+1,col,p);
				makeMove(row-1,col,p);

			}
			else if(col==c){
				makeMove(row,col-1,p);
				makeMove(row+1,col,p);
				makeMove(row-1,col,p);
			}
			break;
		case INTERIOR:
			makeMove(row,col-1,p);
			makeMove(row,col+1,p);
			makeMove(row-1,col,p);
			makeMove(row+1,col,p);
			break;
		}
		//System.out.println("Finished chain react on cell ("+(row+1)+","+(col+1)+") with minus = "+minus_count+", plus= "+plus_count);
	}

	boolean primed(int row, int col) {
		//returns true if a square is overloaded
		switch(type[row][col]){
		case CORNER:
			if(Math.abs(b[row][col])==1){
				return true;
			}
			break;
		case SIDE:
			if(Math.abs(b[row][col])==2){
				return true;
			}
			break;
		case INTERIOR:
			if(Math.abs(b[row][col])==3){
				return true;
			}
			break;
		}
		return false;
	}
}