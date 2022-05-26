package breadmakesense;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class MainWindowFX {

	static Stage stage;

	static BorderPane bPane;
	static Scene mainScene;
	static HBox menuHBox;
	static VBox clickerVBox;
	static VBox shopVBox;

	static Label nBreads;
	static Label bSecond;

	static Button donation;
	static Button ascend;
	static Button ranking;

	static Button[] itemsButtons;

	static Label topLabel;

	static TextArea infoTextArea;

	static String[] itemsInfo;
	static String[] itemsNames;

	public void inicialize() {

		stage = new Stage();

		bPane = new BorderPane();
		mainScene = new Scene(bPane, 1280, 720);
		mainScene.getStylesheets().add(getClass().getResource("style.css").toString());
		stage.getIcons().add(new Image("assets//bread.png"));

		itemsNames = new String[] { "WORKER", "BREAD TREE", "BREAD FARM", "FACTORY" };

		inferiorMenu();
		shop();
		clickableBread();
		textArea();
		topLabel();

		MainWindowLogic.downloadServerData();
		refreshAllItemButtons();

		MainWindowLogic.initalizeAutoClickTimer();
		MainWindowLogic.initalizeUploadDataTimer();

		stage.setTitle("Bread Make Sense");

		stage.setScene(mainScene);

		DonationWindow.inicialize();
		AscendWindow.inicialize();
		RankingWindow.inicialize();

		stage.show();

	}

	public void topLabel() {
		topLabel = new Label("User: " + LoginWindowLogic.username + " Ascend value: "
				+ String.format("%.2f", MainWindowLogic.ascend) + "% Breads per click: " + MainWindowLogic.breadsClick);
		topLabel.setPadding(new Insets(10));
		bPane.setTop(topLabel);
	}

	public static void refreshTopLabel() {
		topLabel.setText("User: " + LoginWindowLogic.username + " Ascend value: "
				+ String.format("%.2f", MainWindowLogic.ascend) + "% Breads per click: " + MainWindowLogic.breadsClick);
	}

	public void clickableBread() {

		nBreads = new Label();
		nBreads.setFont(new Font("Arial", 20));

		bSecond = new Label();
		bSecond.setFont(new Font("Arial", 16));

		bSecond.setText(String.format("Breads/s: %.2f", MainWindowLogic.breadsPerSecond));
		nBreads.setText("Number of breads: " + MainWindowLogic.breads);

		ImageView bread = new ImageView("assets//bread.png");

		clickerVBox = new VBox();

		bread.setOnMouseClicked(e -> {
			MainWindowLogic.addClick();
			refreshBreads();
		});

		clickerVBox.setAlignment(Pos.CENTER);
		clickerVBox.getChildren().addAll(nBreads, bSecond, bread);
		clickerVBox.setPrefWidth(400);

		bPane.setLeft(clickerVBox);

	}

	public void textArea() {

		itemsInfo = new String[MainWindowLogic.items.length];

		itemsInfo[0] = "A minum wage worker that is not into any type of pressure!\n"
				+ "It's not much, but it's honest work.\n\n"
				+ "<<Please help me>> - A happy worker\n\nProduces honest work.\n\n"
				+ "Your clicks produce +1 for every 5 buildings of this type.";
		itemsInfo[1] = "Breads come from trees.\nYou didn't know that?\n\n" + "<<>> - A bread tree\n\n"
				+ "Produces 4 times more breads than a worker.\n\n"
				+ "Your clicks produce +1 for every 5 buildings of this type.";
		itemsInfo[2] = "Maybe you think that we collect wheat in order to make bread.\n"
				+ "Well, no, this farm produces pure bread from the bread plant.\n\n"
				+ "<<Photosynthesis>> - A bread plant\n\n" + "Produces 4 times more breads than a bread tree.\n\n"
				+ "Your clicks produce +1 for every 5 buildings of this type.";
		itemsInfo[3] = "A bread factory, the peak of enginering, so cool.\n"
				+ "It has no workers in case you were wondering.\n\n"
				+ "<<Brrrrrrrrrrr>> - The machines inside the factory\n\n"
				+ "Produces 4 times more breads than a bread farm.\n\n"
				+ "Your clicks produce +1 for every 5 buildings of this type.";

		infoTextArea = new TextArea();
		// This way the user can't write on the text area
		infoTextArea.setEditable(false);
		bPane.setCenter(infoTextArea);

		itemsButtons[0].setOnMouseEntered(e -> {
			infoTextArea.setText(itemsInfo[0]);
		});

		itemsButtons[1].setOnMouseEntered(e -> {
			infoTextArea.setText(itemsInfo[1]);
		});

		itemsButtons[2].setOnMouseEntered(e -> {
			infoTextArea.setText(itemsInfo[2]);
		});

		itemsButtons[3].setOnMouseEntered(e -> {
			infoTextArea.setText(itemsInfo[3]);
		});

	}

	public void shop() {

		shopVBox = new VBox();

		itemsButtons = new Button[4];

		itemsButtons[0] = new Button(
				MainWindowLogic.items[0] + " " + itemsNames[0] + "\n" + MainWindowLogic.itemsPrice[0]);
		itemsButtons[1] = new Button(
				MainWindowLogic.items[1] + " " + itemsNames[1] + "\n" + MainWindowLogic.itemsPrice[1]);
		itemsButtons[2] = new Button(
				MainWindowLogic.items[2] + " " + itemsNames[2] + "\n" + MainWindowLogic.itemsPrice[2]);
		itemsButtons[3] = new Button(
				MainWindowLogic.items[3] + " " + itemsNames[3] + "\n" + MainWindowLogic.itemsPrice[3]);

		for (Button b : itemsButtons) {
			b.setPrefWidth(200);
			b.setAlignment(Pos.CENTER_LEFT);
		}

		itemsButtons[0].setOnAction(e -> {
			if (!MainWindowLogic.buyItem((byte) 0))
				infoTextArea.setText("You don't have enough breads!");
			itemsButtons[0]
					.setText(MainWindowLogic.items[0] + " " + itemsNames[0] + "\n" + MainWindowLogic.itemsPrice[0]);
			refreshBreadsSecond();
			refreshTopLabel();
		});

		itemsButtons[1].setOnAction(e -> {
			if (!MainWindowLogic.buyItem((byte) 1))
				infoTextArea.setText("You don't have enough breads!");
			itemsButtons[1]
					.setText(MainWindowLogic.items[1] + " " + itemsNames[1] + "\n" + MainWindowLogic.itemsPrice[1]);
			refreshBreadsSecond();
			refreshTopLabel();
		});

		itemsButtons[2].setOnAction(e -> {
			if (!MainWindowLogic.buyItem((byte) 2))
				infoTextArea.setText("You don't have enough breads!");
			itemsButtons[2]
					.setText(MainWindowLogic.items[2] + " " + itemsNames[2] + "\n" + MainWindowLogic.itemsPrice[2]);
			refreshBreadsSecond();
			refreshTopLabel();
		});

		itemsButtons[3].setOnAction(e -> {
			if (!MainWindowLogic.buyItem((byte) 3))
				infoTextArea.setText("You don't have enough breads!");
			itemsButtons[3]
					.setText(MainWindowLogic.items[3] + " " + itemsNames[3] + "\n" + MainWindowLogic.itemsPrice[3]);
			refreshBreadsSecond();
			refreshTopLabel();
		});

		Label shopText = new Label("Shop");
		shopText.setFont(new Font("Arial", 22));

		VBox.setMargin(shopText, new Insets(0, 0, 20, 0));

		shopVBox.getChildren().addAll(shopText, itemsButtons[0], itemsButtons[1], itemsButtons[2], itemsButtons[3]);
		shopVBox.setAlignment(Pos.CENTER);
		bPane.setRight(shopVBox);

	}

	public static void refreshBreads() {
		nBreads.setText("Number of breads: " + (long) MainWindowLogic.breads);
	}

	public static void refreshBreadsSecond() {
		MainWindowLogic.calculateBreadsSecond();
		bSecond.setText(String.format("Breads/s: %.2f", MainWindowLogic.breadsPerSecond));
	}

	public static void refreshAllItemButtons() {
		for (int i = 0; i < itemsButtons.length ; i++) {
			itemsButtons[i].setText(MainWindowLogic.items[i] + " " + itemsNames[i] + "\n" + MainWindowLogic.itemsPrice[i]);
		}
	}

	public void inferiorMenu() {

		menuHBox = new HBox();

		donation = new Button("DONATION");
		ascend = new Button("ASCEND");
		ranking = new Button("RANKING");

		donation.setOnAction(e -> DonationWindow.show());
		ascend.setOnAction(e -> AscendWindow.show());
		ranking.setOnAction(e -> RankingWindow.show());

		menuHBox.getChildren().addAll(donation, ascend, ranking);
		menuHBox.setAlignment(Pos.CENTER);
		bPane.setBottom(menuHBox);

	}

}
