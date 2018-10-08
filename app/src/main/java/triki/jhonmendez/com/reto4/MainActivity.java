package triki.jhonmendez.com.reto4;
import triki.jhonmendez.com.reto4.TicTacToeGame.DifficultyLevel;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {


    private static final int DIALOG_QUIT_ID = 1;
    private static final int DIALOG_ABOUT_ID = 2;

    private static TicTacToeGame mGame;

    static TextView mInfoTextView;
    static TextView mHuman_win;
    static TextView mComputer_win;
    static TextView mTie;
    static int nHumanWin;
    static int nComputerWin;
    static int nTie;

    private boolean turn;
    private boolean mGameOver;
    private boolean mSoundOn;
    int selected;

    static MediaPlayer mHumanMediaPlayer;
    static MediaPlayer mComputerMediaPlayer;

    private SharedPreferences mPrefs;
    private static BoardView mBoardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGame = new TicTacToeGame();
        mBoardView = findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        mInfoTextView = findViewById(R.id.information);

        mHuman_win = findViewById(R.id.human);
        mComputer_win = findViewById(R.id.computer);
        mTie = findViewById(R.id.tie);

        nComputerWin = 0;
        nHumanWin = 0;
        nTie = 0;

        mGameOver = false;
        mSoundOn = true;
        turn = true;
        selected = 2;

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        // Restore the scores
        nHumanWin = mPrefs.getInt("mHumanWins", 0);
        nComputerWin = mPrefs.getInt("mComputerWins", 0);
        nTie = mPrefs.getInt("mTies", 0);


        if (savedInstanceState == null) {
            startNewGame();
        }else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            nHumanWin = savedInstanceState.getInt("mHumanWins");
            nComputerWin = savedInstanceState.getInt("mComputerWins");
            nTie = savedInstanceState.getInt("mTies");
            turn = savedInstanceState.getBoolean("turn");
            selected = savedInstanceState.getInt("selected");
            if (selected == 0){
                mGame.setDifficultyLevel(DifficultyLevel.Easy);
            }else if (selected == 1){
                mGame.setDifficultyLevel(DifficultyLevel.Harder);
            }else{
                mGame.setDifficultyLevel(DifficultyLevel.Expert);
            }
            if(mGameOver){
                lockBoard();
            }
        }
        displayScores();
        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        // Restore the scores from the persistent preference data source
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        String difficultyLevel = mPrefs.getString("difficulty_level", getResources().getString(R.string.difficulty_harder));
        if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
        else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
        else
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        // menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // startNewGame();
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                return true;
            case R.id.setting:
                startActivityForResult(new Intent(this, Settings.class), 0);
                return true;
            case R.id.reset_score:
                nComputerWin = 0;
                nHumanWin = 0;
                nTie = 0;
                displayScores();
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                break;
            case R.id.quit:
                showDialog(DIALOG_QUIT_ID);
                break;
        }
        return false;
    }

    private void startNewGame(){
        mGame.clearBoard();
        mBoardView.invalidate();   // Redraw the board
        mBoardView.setOnTouchListener(mTouchListener);
        if (turn) {
            mInfoTextView.setText(R.string.first_human);
            turn = false;
        } else {
            try{
                SystemClock.sleep(500);
                if(mSoundOn)
                    mComputerMediaPlayer.start();
            }catch(Exception e){
                e.printStackTrace();
            }
            setMove(TicTacToeGame.COMPUTER_PLAYER, getComputerMove());
            MainActivity.mInfoTextView.setText(R.string.turn_human);
            turn = true;
        }
    }

    public static boolean setMove(char player, int location) {
        boolean move_done = mGame.setMove(player, location);
        if(move_done){
            mBoardView.invalidate();
        }
        return move_done;
    }

    public static int checkForWinner() {
        return mGame.checkForWinner();
    }

    public static void lockBoard() {
        mBoardView.setOnTouchListener(null);
    }

    public static int getComputerMove() {
        return mGame.getComputerMove();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch(id) {
            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.activity_about,null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
                break;
            case DIALOG_QUIT_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.quit_question)
                        .setCancelable(false)
                        .setPositiveButton(	R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        MainActivity.this.finish();
                                    }
                                })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
        }
        return dialog;
    }

    // Listen for touches on the board
    private OnTouchListener mTouchListener = new OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if(mSoundOn)
                mHumanMediaPlayer.start();
            boolean move_done = setMove(TicTacToeGame.HUMAN_PLAYER, pos);
            int winner = checkForWinner();
            if(winner==0 && move_done){
                mInfoTextView.setText(R.string.turn_computer);
                int move = getComputerMove();
                try{
                    SystemClock.sleep(500);
                    if(mSoundOn)
                        mComputerMediaPlayer.start();
                }catch(Exception e){
                    e.printStackTrace();
                }
                setMove(TicTacToeGame.COMPUTER_PLAYER,move);
                winner = checkForWinner();
            }
            if(winner == 0){
                mInfoTextView.setText(R.string.turn_human);
            }else if (winner == 1){
                mInfoTextView.setText(R.string.result_tie);
                nTie++;
                mTie.setText("Empates: "+ nTie);
                lockBoard();
            }else if (winner == 2){
                nHumanWin++;
                mHuman_win.setText("Tú: "+ nHumanWin);
                String defaultMessage = getResources().getString(R.string.result_human_wins);
                mInfoTextView.setText(mPrefs.getString("victory_message", defaultMessage));
                lockBoard();
            }else{
                mInfoTextView.setText(R.string.result_computer_wins);
                nComputerWin++;
                mComputer_win.setText("Andriod: "+ nComputerWin);
                lockBoard();
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.x_sound);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.o_sound);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int winner = checkForWinner();
        if (winner==0){
            mGameOver=false;
        }else{
            mGameOver=true;
        }
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", Integer.valueOf(nHumanWin));
        outState.putInt("mComputerWins", Integer.valueOf(nComputerWin));
        outState.putInt("mTies", Integer.valueOf(nTie));
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("turn", turn);
        outState.putInt("selected", selected);
    }

    private void displayScores() {
        mHuman_win.setText("Tú: "+Integer.toString(nHumanWin));
        mComputer_win.setText("Máquina: "+Integer.toString(nComputerWin));
        mTie.setText("Empates: "+Integer.toString(nTie));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mGame.setBoardState(savedInstanceState.getCharArray("board"));
        mGameOver = savedInstanceState.getBoolean("mGameOver");
        mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
        nHumanWin = savedInstanceState.getInt("mHumanWins");
        nComputerWin = savedInstanceState.getInt("mComputerWins");
        nTie = savedInstanceState.getInt("mTies");
        turn = savedInstanceState.getBoolean("turn");
        selected = savedInstanceState.getInt("selected");
        mSoundOn = savedInstanceState.getBoolean("mSoundOn");
        if (selected == 0){
            mGame.setDifficultyLevel(DifficultyLevel.Easy);
        }else if (selected == 1){
            mGame.setDifficultyLevel(DifficultyLevel.Harder);
        }else{
            mGame.setDifficultyLevel(DifficultyLevel.Expert);
        }
        if(mGameOver){
            lockBoard();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save the current scores
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mHumanWins", nHumanWin);
        ed.putInt("mComputerWins", nComputerWin);
        ed.putInt("mTies", nTie);
        ed.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            String difficultyLevel = mPrefs.getString("difficulty_level", getResources().getString(R.string.difficulty_harder));

            if (difficultyLevel.equals(getResources().getString(R.string.difficulty_easy)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
            else if (difficultyLevel.equals(getResources().getString(R.string.difficulty_harder)))
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
            else
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
        }
    }

}
