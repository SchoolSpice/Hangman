import javafx.beans.Observable;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Game {

	private String answer;
	private String tmpAnswer;
	private String[] letterAndPosArray;
	private ArrayList<String> words = new ArrayList<String>(0);
	private Boolean letterEntered;
	private int moves;
	private int index;
	private final ReadOnlyObjectWrapper<GameStatus> gameStatus;
	private ObjectProperty<Boolean> gameState = new ReadOnlyObjectWrapper<Boolean>();
	private final ReadOnlyObjectWrapper<String> wordDisplay;

	public enum GameStatus {
		GAME_OVER {
			@Override
			public String toString() {
				return "Game over!";
			}
		},
		BAD_GUESS {
			@Override
			public String toString() { return "Bad guess..."; }
		},
		GOOD_GUESS {
			@Override
			public String toString() {
				return "Good guess!";
			}
		},
		WON {
			@Override
			public String toString() {
				return "You won!";
			}
		},
		OPEN {
			@Override
			public String toString() {
				return "Game on, let's go!";
			}
		},
		ERROR{
			@Override
			public String toString(){
				return "Error! Please try again!";
			}
		}
	}

	public Game() {
		gameStatus = new ReadOnlyObjectWrapper<GameStatus>(this, "gameStatus", GameStatus.OPEN);
		gameStatus.addListener(new ChangeListener<GameStatus>() {
			@Override
			public void changed(ObservableValue<? extends GameStatus> observable,
								GameStatus oldValue, GameStatus newValue) {
				if (gameStatus.get() != GameStatus.OPEN) {
					log("in Game: in changed");
					//currentPlayer.set(null);
				}
			}

		});
		readFile();
		setRandomWord();
		prepTmpAnswer();
		prepLetterAndPosArray();
		moves = 0;

		gameState.setValue(true); // initial state
		letterEntered = false; //flag that user entered something

		//word display shows the word they need to guess with blanks being filled
		wordDisplay = new ReadOnlyObjectWrapper<String>(this, "wordDisplay", getInitialWordDisplay());
		createGameStatusBinding();
	}

	private void createGameStatusBinding() {
		List<Observable> allObservableThings = new ArrayList<>();
		ObjectBinding<GameStatus> gameStatusBinding = new ObjectBinding<GameStatus>() {
			{
				super.bind(gameState);
			}
			@Override
			public GameStatus computeValue() {
				log("in computeValue");
				GameStatus check = checkForWinner(index);
				if(check != null ) {
					return check;
				}
				if(tmpAnswer.trim().length() == 0 && !letterEntered) {
					log("new game");
					return GameStatus.OPEN;
				}else if (moves == numOfTries()) {
					log("game over in binding");
					return GameStatus.GAME_OVER;
				}else if (index == -1 && moves <= 5) {
					moves++;
					log("bad guess moves: " + moves);
					return GameStatus.BAD_GUESS;
					//printHangman();
				} else if (index != -1) {
					log("good guess");
					return GameStatus.GOOD_GUESS;
				} else {
					log("error");
					return GameStatus.ERROR;
				}
			}
		};
		gameStatus.bind(gameStatusBinding);
	}

	public ReadOnlyObjectProperty<String> wordDisplayProperty() {
	    return wordDisplay.getReadOnlyProperty();
	}

	public ReadOnlyObjectProperty<GameStatus> gameStatusProperty() {
		return gameStatus.getReadOnlyProperty();
	}
	public GameStatus getGameStatus() {
		return gameStatus.get();
	}

	private void readFile() {
		try {
			File file = new File("words.txt");
			Scanner scanner = new Scanner(file);
			String wordFromFile = "";
			while (scanner.hasNextLine()) {
				wordFromFile = scanner.nextLine().trim();
				this.words.add(wordFromFile);
			}
		} catch (FileNotFoundException ex) {
			log("\nunable to read file. answer is now apple");
		}

	}

	private void setRandomWord() {
		// int idx = (int) (Math.random() * words.length);
		int index = (int) (Math.random() * words.size());
		if (words.size() == 0) {
			answer = "apple";
		} else {
			answer = words.get(index);
		}
		log("\nin setRandomWord: answer = " + answer);
	}

	private void prepTmpAnswer() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < answer.length(); i++) {
			sb.append(" ");
		}
		tmpAnswer = sb.toString();
	}

	private void prepLetterAndPosArray() {
		letterAndPosArray = new String[answer.length()];
		for(int i = 0; i < answer.length(); i++) {
			letterAndPosArray[i] = answer.substring(i,i+1);
		}
	}

	private String getInitialWordDisplay() {
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < tmpAnswer.length(); i++) {
	        if (tmpAnswer.charAt(i) != ' ') {
	            sb.append(tmpAnswer.charAt(i)).append(' ');
	        } else {
	            sb.append("_ ");
	        }
	    }
	    return sb.toString().trim();
	}

	private List<Integer> getValidIndices(String input) {
	    List<Integer> indices = new ArrayList<>();
	    for (int i = 0; i < letterAndPosArray.length; i++) {
	        if (letterAndPosArray[i].equals(input)) {
	            indices.add(i);
	            letterAndPosArray[i] = "";
	        }
	    }
	    return indices;
	}

	private int update(String input) {
	    List<Integer> indices = getValidIndices(input);
	    if (!indices.isEmpty()) {
	        StringBuilder sb = new StringBuilder(tmpAnswer);
	        for (int index : indices) {
	            sb.setCharAt(index, input.charAt(0));
	        }
	        tmpAnswer = sb.toString();
	        wordDisplay.set(getInitialWordDisplay());
	        return indices.size();
	    } else {
	        return -1;
	    }
	}

	private void drawHangmanFrame() {
	}

	public void makeMove(String letter) {
		log("\nin makeMove: " + letter.toLowerCase());
		index = update(letter.toLowerCase());
		letterEntered = true; //flag to start bad/good guess 
		// this will toggle the state of the game
		gameState.setValue(!gameState.getValue());
	}

	public void reset() {
		// reset the game state
	    gameState.setValue(true);
	    // reset other game variables
	    moves = 0;
	    index = -1;
	    letterEntered = false;
	    setRandomWord();
	    prepTmpAnswer();
	    prepLetterAndPosArray();
	    getInitialWordDisplay();
	    wordDisplay.set(getInitialWordDisplay());
	    createGameStatusBinding();
	}

	private int numOfTries() {
		return 5; // TODO, fix me
	}

	public static void log(String s) {
		System.out.println(s);
	}

	private GameStatus checkForWinner(int status) {
		log("in checkForWinner");
		if(moves >= numOfTries()){
				tmpAnswer = answer;
				wordDisplay.set(getInitialWordDisplay());
				return GameStatus.GAME_OVER;
		}
		else if(tmpAnswer.equals(answer)) {
			log("won");
			return GameStatus.WON;
		}
		else {
			return null;
		}
	}
}
