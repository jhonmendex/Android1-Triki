package triki.jhonmendez.com.reto4;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class TicTacToeGame {
    private char mBoard[] = {'1','2','3','4','5','6','7','8','9'};
    final static int BOARD_SIZE = 9;

    // Nivel de dificultad
    public enum DifficultyLevel {Easy, Harder, Expert};
    // Nivel de dificultad actual
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
    //Representacion de jugadas del jugador en el tablero
    public static final char HUMAN_PLAYER = 'X';
    //Representacion de jugadas del computador en el tablero
    public static final char COMPUTER_PLAYER = 'O';
    //Representacion de casillas disponibles en el tablero
    public static final	char OPEN_SPOT	= '	';
    //Numero aleatorio para siguiente movimiento del computador
    private Random mRand;

    public TicTacToeGame() {
        // Semilla para el generador de numeros aleatorios
        mRand = new Random();
    }

    private void displayBoard()	{
        System.out.println();
        System.out.println(mBoard[0] + " | " + mBoard[1] + " | " + mBoard[2]);
        System.out.println("-----------");
        System.out.println(mBoard[3] + " | " + mBoard[4] + " | " + mBoard[5]);
        System.out.println("-----------");
        System.out.println(mBoard[6] + " | " + mBoard[7] + " | " + mBoard[8]);
        System.out.println();
    }

    // checkForWinner() funcion que retorna  Return
    //  0 si no hay un ganador o empate
    //  1 si es un empate
    //  2 si X gano
    //  3 si O gano
    public int checkForWinner() {

        // Verifica ganador en horizontales
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER)
                return 3;
        }

        // Verifica ganador en verticales
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER)
                return 2;
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER)
                return 3;
        }

        // Verifica ganador en verticales
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER))
            return 2;
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER))
            return 3;

        // Verifica si hay empate
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Si hay una casilla sin movimiento ni humano ni del computador aun no hay ganador ni empate
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER)
                return 0;
        }

        // Si ningun caso anterior se presenta, entonces es un empate
        return 1;
    }
    //getComputerMove(). Retorna
    //Se obtiene la casilla donde jugara el computador dependiendo de la dificultad
    public int getComputerMove()
    {
        int move;
        //Si la dificultad es facil
        if(mDifficultyLevel == DifficultyLevel.Easy){
            //Se calcula un movimiento aleatorio disponible
            do
            {
                move = mRand.nextInt(BOARD_SIZE);
            } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
            //Si la dificultad es dificil
        }else if(mDifficultyLevel == DifficultyLevel.Harder){
            // Se evalua si hay un movimiento para que el computador gane
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    char curr = mBoard[i];
                    mBoard[i] = COMPUTER_PLAYER;
                    if (checkForWinner() == 3) {
                        System.out.println("Computer is moving to " + (i + 1));
                        return i;
                    }
                    else
                        mBoard[i] = curr;
                }
            }
            // De lo contrario de genera un movimiento aleatorio
            do
            {
                move = mRand.nextInt(BOARD_SIZE);
            } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
            //Si la dificultad es experto
        }else{
            // Se evalua si hay un movimiento para que el computador gane
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    char curr = mBoard[i];
                    mBoard[i] = COMPUTER_PLAYER;
                    if (checkForWinner() == 3) {
                        System.out.println("Computer is moving to " + (i + 1));
                        return i;
                    }
                    else
                        mBoard[i] = curr;
                }
            }
            // Se evalua si hay un movimiento del computador para evitar que el humano gane
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                    char curr = mBoard[i];   // Save the current number
                    mBoard[i] = HUMAN_PLAYER;
                    if (checkForWinner() == 2) {
                        mBoard[i] = COMPUTER_PLAYER;
                        setMove(COMPUTER_PLAYER, i);
                        System.out.println("Computer is moving to " + (i + 1));
                        return i;
                    }
                    else
                        mBoard[i] = curr;
                }
            }
            //De lo contrario genera un movimiento aleatorio disponible
            do
            {
                move = mRand.nextInt(BOARD_SIZE);
            } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);
        }
        System.out.println("Computer is moving to " + (move + 1));

        setMove(COMPUTER_PLAYER, move);
        return move;
    }
    //clearBoard()
    //Limpia el tablero
    public void clearBoard(){
        for(int i = 0; i<BOARD_SIZE; i++){
            mBoard[i]=OPEN_SPOT;
        }
    }
    //setMove()
    //Guarda en el tablero un movimiento de un jugador(Humano/computador) en una posicion determinada
    //Retorna falso si la casilla esta ocupada, de lo contrario retorna verdadero
    public boolean	setMove(char player,int	location){
        if(mBoard[location]==OPEN_SPOT){
            if(player == HUMAN_PLAYER){
                mBoard[location]=HUMAN_PLAYER;
            }else{
                mBoard[location]=COMPUTER_PLAYER;
            }
            return true;
        }
        return false;
    }

    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }

    public void setDifficultyLevel(DifficultyLevel mDifficultyLevel) {
        this.mDifficultyLevel = mDifficultyLevel;
    }

    public char getBoardOccupant(int i) {
        return mBoard[i];
    }

    public char[] getBoardState() {
        return mBoard;
    }

    public void setBoardState(char[] charArray) {
        mBoard = charArray;
    }

}
