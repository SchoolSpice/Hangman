import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
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
	private VBox board ;
	@FXML
	private Label statusLabel ;
	@FXML
	private Label enterALetterLabel ;
	@FXML
	private Label userInputLabel ;
	@FXML
	private TextField textField ;
	@FXML
	private TextArea textArea ;
	@FXML
	private Label wordDisplayLabel ;


    public void initialize() throws IOException {
		System.out.println("in initialize");
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
				if(newValue.length() > 0) {
					textArea.setText(textArea.getText() + textField.getText());
					System.out.print(newValue);
					game.makeMove(newValue);
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

	private void drawHangman() {

		Line x1=new Line(0,50,150,50);
        Line y1=new Line(0,50,0,300);
        Line y2=new Line(105,50,105,100);
        Line x2=new Line(-75,300,150,300);
        Circle head = new Circle();
        head.setRadius(15);
        System.out.println(y2.getEndY());
        head.setCenterX(y2.getEndX());
        head.setCenterY(y2.getEndY());


        Line rightHand=new Line(105, 120, 155, 155);
        Line body=new Line(105, 110, 105, 210);
        Line leftHand=new Line(105, 120, 55, 155);

        Line rightLeg=new Line(105, 210, 155, 245);
        Line leftLeg=new Line(105, 210, 55, 245);

        Group group=new Group();
        group.getChildren().addAll(x2,x1,y1,y2,head,rightHand,body,leftHand,rightLeg,leftLeg);

        board.getChildren().add(group);

		wordDisplayLabel = new Label();
    	board.getChildren().add(wordDisplayLabel);

		textArea.setPrefSize(225, 100);
		textArea.setEditable(false);
		textArea.setWrapText(true);

	}
		
	@FXML 
	private void newHangman() {
		textArea.clear();
		game.reset();
	}

	@FXML
	private void quit() {
		board.getScene().getWindow().hide();
	}

}
