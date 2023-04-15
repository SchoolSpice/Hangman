import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.Group;


public class GameController {

	private final ExecutorService executorService;
	private final Game game;	
	
	public GameController(Game game) {
		this.game = game;
		executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	@FXML
	private VBox board;
	@FXML
	private Label statusLabel;
	@FXML
	private Label enterALetterLabel;
	@FXML
	private Label userInputLabel;
	@FXML
	private TextField textField;
	@FXML
	private TextArea textArea;
	@FXML
	private Label wordDisplayLabel;

	private Group group;
	private Label titleLabel;


    public void initialize() throws IOException {
		System.out.println("in initialize");
		Label titleLabel = new Label("Hangman");
		titleLabel.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-alignment: center;");
		board.getChildren().add(0, titleLabel);
		group=new Group();
		board.getChildren().add(group);
		wordDisplayLabel = new Label();
		board.getChildren().add(wordDisplayLabel);
		drawHangman();
		addTextBoxListener();
		setUpStatusLabelBindings();
		wordDisplayLabel.textProperty().bind(game.wordDisplayProperty());
		wordDisplayLabel.setStyle("-fx-font-size: 40px;");
	}

	private void addTextBoxListener() {
		textField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
				if(newValue.length() > 0 && newValue.matches("[a-zA-Z]+") && !isDuplicate(newValue.charAt(0))) {
					textArea.setText(textArea.getText() + textField.getText());
					System.out.print(newValue);
					game.makeMove(newValue);
					drawHangman();
					textField.clear();
					
				}
				else{
					textField.clear();
				}
			}
		});

		// textArea.textProperty().addListener(new ChangeListener<String>() {
		// 	@Override
		// 	public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
		// 		if(newValue.length() > 0) {
		// 			System.out.print(newValue);
		// 			game.makeMove(newValue);
		// 			//textField.clear();
		// 		}
		// 	}
		// });
	}

	private Boolean isDuplicate(char letter){
		String inputValue = textArea.getText();
		char[] compare = inputValue.toCharArray();

		for(int i = 0; i < inputValue.length(); i++){
			if(compare[i] == letter){
				return true;
			}
		}
		return false;
	}

	private void setUpStatusLabelBindings() {

		System.out.println("in setUpStatusLabelBindings");
		statusLabel.textProperty().bind(Bindings.format("%s", game.gameStatusProperty()));
		enterALetterLabel.textProperty().bind(Bindings.format("%s", "Enter a letter:"));
		userInputLabel.textProperty().bind(Bindings.format("%s", "User Input:"));
		/*	Bindings.when(
					game.currentPlayerProperty().isNotNull()
			).then(
				Bindings.format("To play: %s", game.currentPlayerProperty())
			).otherwise(
				""
			)
		);
		*/
	}
	int counter=0;
	private void drawHangman() {

		Line x1=new Line(0,50,150,50);
        Line y1=new Line(0,50,0,300);
        Line y2=new Line(105,50,105,100);
        Line x2=new Line(-75,300,160,300);
        Circle head = new Circle();
        head.setRadius(15);
        // System.out.println(y2.getEndY());
        head.setCenterX(y2.getEndX());
        head.setCenterY(y2.getEndY());
		// System.out.println(wordDisplayLabel.getText()+"is display word "+wordDisplayLabel.getText().length()+" and "+ game.getAnswer().length()+" answer is"+game.getAnswer());
		String display=wordDisplayLabel.getText();
		display=display.replaceAll(Pattern.quote(String.valueOf(" ")), "");
		display=display.replaceAll(Pattern.quote(String.valueOf("_")), "");
		// System.out.println(display+"is display word "+display.length()+" and "+ game.getAnswer().length()+" answer is"+game.getAnswer());


		Line rightHand=new Line(105, 120, 155, 155);
		Line body=new Line(105, 110, 105, 210);
		Line leftHand=new Line(105, 120, 55, 155);

		Line rightLeg=new Line(105, 210, 155, 245);
		Line leftLeg=new Line(105, 210, 55, 245);
		int wordPercentage=0;

		x1.getStyleClass().add("grainy");
		y1.getStyleClass().add("grainy");
		y2.getStyleClass().add("grainy");
		x2.getStyleClass().add("grainy");
		head.getStyleClass().add("grainy");
		rightHand.getStyleClass().add("grainy");
		leftHand.getStyleClass().add("grainy");
		body.getStyleClass().add("grainy");
		rightLeg.getStyleClass().add("grainy");
		leftLeg.getStyleClass().add("grainy");

		if(wordDisplayLabel.getText()!=null){

			wordPercentage=((display.length()*100)/game.getAnswer().length());
			// System.out.println(wordPercentage+" is the word match percentage");
		}
		if((counter==0)){
			group.getChildren().clear();
			y2.setStartX(105);
			y2.setStartY(50);
			y2.setEndX(105);
			y2.setEndY(100);

			// head.setCenterX(y2.getEndX());
			// head.setCenterY(y2.getEndY());

			// leftHand.setStartX(105);
			// leftHand.setStartY(175);
			// leftHand.setEndX(55);
			// leftHand.setEndY(210);

			// rightHand.setStartX(105);
			// rightHand.setStartY(175);
			// rightHand.setEndX(155);
			// rightHand.setEndY(210);

			// body.setStartX(105);
			// body.setStartY(165);
			// body.setEndX(105);
			// body.setEndY(265);

			// rightLeg.setStartX(105);
			// rightLeg.setStartY(265);
			// rightLeg.setEndX(155);
			// rightLeg.setEndY(300);

			// leftLeg.setStartX(105);
			// leftLeg.setStartY(265);
			// leftLeg.setEndX(55);
			// leftLeg.setEndY(300);
			// group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body,leftHand,rightLeg,leftLeg);
			group.getChildren().addAll(x2,x1,y1,y2);

			counter++;
		}else if(statusLabel.getText().equals("You won!")){
			System.out.println("You Won");
			group.getChildren().clear();
			y2.setStartX(105);
			y2.setStartY(50);
			y2.setEndX(105);
			y2.setEndY(100);

			body.setStartX(105);
			body.setStartY(165);
			body.setEndX(105);
			body.setEndY(265);

			head.setCenterX(body.getStartX());
			head.setCenterY(body.getStartY()-10);

			leftHand.setStartX(105);
			leftHand.setStartY(190);
			leftHand.setEndX(55);
			leftHand.setEndY(150);

			rightHand.setStartX(105);
			rightHand.setStartY(190);
			rightHand.setEndX(155);
			rightHand.setEndY(150);

			rightLeg.setStartX(105);
			rightLeg.setStartY(265);
			rightLeg.setEndX(155);
			rightLeg.setEndY(300);

			leftLeg.setStartX(105);
			leftLeg.setStartY(265);
			leftLeg.setEndX(55);
			leftLeg.setEndY(300);

			group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body,leftHand,rightLeg,leftLeg);

		}else if(statusLabel.getText().equals("Bad guess...")){
			group.getChildren().clear();
			if(game.getMoves()==1){
				group.getChildren().addAll(x2,x1,y1,y2,head);
			}else if(game.getMoves()==2){
				group.getChildren().addAll(x2,x1,y1,y2,head,body);
			}
			else if(game.getMoves()==3){
				group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body);
			}
			else if(game.getMoves()==4){
				group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body,leftHand);
			}
			else if(game.getMoves()==5){
				group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body,leftHand,rightLeg);
			}
		}else if(statusLabel.getText().equals("Game over!")){
			group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body,leftHand,rightLeg,leftLeg);
		}else if(statusLabel.getText().equals("Game on, let's go!")){
			group.getChildren().clear();
			System.out.println("Game on, let's go!");
			counter=0;
			drawHangman();
		}
		textArea.setPrefSize(225, 100);
		textArea.setEditable(false);
		textArea.setWrapText(true);

	}
		
	@FXML 
	private void newHangman() {
		textArea.clear();
		counter=0;
		drawHangman();
		game.reset();
	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}
