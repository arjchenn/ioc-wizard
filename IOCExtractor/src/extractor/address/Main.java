//Main class to load the UI
package extractor.address;

import java.io.IOException;

import extractor.address.view.DomainLayoutController;
import extractor.address.view.JsonLayoutController;
import extractor.address.view.PasteLayoutController;
import extractor.address.view.RootLayoutController;
import extractor.address.view.SaveLayoutController;
import extractor.address.view.StixLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

	private Stage primaryStage;
	private Stage saveStage;
	private Stage domainStage;
	private Stage jsonStage;
	private Stage pasteStage;
	private Stage stixStage;
	private BorderPane rootLayout;
	private BorderPane saveLayout;
	private BorderPane domainLayout;
	private BorderPane jsonLayout;
	private BorderPane pasteLayout;
	private BorderPane stixLayout;
	private SaveLayoutController saveLayoutController;
	private RootLayoutController rootLayoutController;
	private DomainLayoutController domainLayoutController;
	private JsonLayoutController jsonLayoutController;
	private PasteLayoutController pasteLayoutController;
	private StixLayoutController stixLayoutController;
	String outStream;

	@Override
	public void start(Stage primaryStage) throws IOException {

			this.primaryStage = primaryStage;
			this.primaryStage.setTitle("IOC Wizard");

        	this.saveStage = new Stage();
        	this.saveStage.setTitle("Save File");

        	this.domainStage = new Stage();
        	this.domainStage.setTitle("Edit Domains");

        	this.jsonStage = new Stage();
        	this.jsonStage.setTitle("Create Feed");

        	this.pasteStage = new Stage();
        	this.pasteStage.setTitle("Paste Text");

        	this.stixStage = new Stage();
        	this.stixStage.setTitle("Create STIX File");
        	initRootLayout();

	}

	public String getOutStream() {
		return outStream;
	}

	private void initRootLayout() {
		try {
			// Load root layout from fxml file.
            	FXMLLoader rootLoader = new FXMLLoader();
            	rootLoader.setLocation(Main.class.getResource("view/RootLayout.fxml"));
            	rootLayout = (BorderPane) rootLoader.load();

            	// Show the scene containing the root layout.
            	Scene scene = new Scene(rootLayout);
            	primaryStage.setScene(scene);
            	primaryStage.setResizable(false);
            	primaryStage.show();
            	rootLayoutController = rootLoader.getController();
            	rootLayoutController.setMain(this);

            	//Load the layout for saving files from the FXML
            	FXMLLoader saveLoader = new FXMLLoader();
            	saveLoader.setLocation(Main.class.getResource("view/SaveLayout.fxml"));
            	saveLayout = (BorderPane) saveLoader.load();

            	// Show the scene containing the save layout.
            	Scene scene2 = new Scene(saveLayout);
            	saveStage.setScene(scene2);
            	saveLayoutController = saveLoader.getController();
            	saveLayoutController.setMain(this);

            	//Load the layout for the domain list
            	FXMLLoader domainLoader = new FXMLLoader(Main.class.getResource("view/DomainLayout.fxml"));
            	domainLayout = (BorderPane) domainLoader.load();

            	// Show the scene containing the domain layout.
            	Scene scene3 = new Scene(domainLayout);
            	domainStage.setScene(scene3);
            	domainStage.setResizable(false);
            	domainLayoutController = domainLoader.getController();
    			domainLayoutController.setrlc(rootLayoutController);

    			//Load the layout for creating feeds in json format
            	FXMLLoader jsonLoader = new FXMLLoader();
            	jsonLoader.setLocation(Main.class.getResource("view/JsonLayout.fxml"));
            	jsonLayout = (BorderPane) jsonLoader.load();

            	// Show the scene containing the json layout.
            	Scene scene4 = new Scene(jsonLayout);
            	jsonStage.setScene(scene4);
            	jsonStage.setResizable(false);
            	jsonLayoutController = jsonLoader.getController();
            	jsonLayoutController.setMain(this);

            	//Load the layout for pasting text with IOCs
            	FXMLLoader pasteLoader = new FXMLLoader();
    			pasteLoader.setLocation(Main.class.getResource("view/PasteLayout.fxml"));
    			pasteLayout = (BorderPane) pasteLoader.load();

            	// Show the scene containing the paste layout.
            	Scene scene5 = new Scene(pasteLayout);
            	pasteStage.setScene(scene5);
            	pasteLayoutController = pasteLoader.getController();
            	pasteLayoutController.setMain(this);

            	//Load the layout for creating a STIX file
            	FXMLLoader stixLoader = new FXMLLoader();
            	stixLoader.setLocation(Main.class.getResource("view/StixLayout.fxml"));
            	stixLayout = (BorderPane) stixLoader.load();

            	// Show the scene containing the stix layout.
            	Scene scene6 = new Scene(stixLayout);
            	stixStage.setScene(scene6);
            	stixStage.setResizable(false);
            	stixLayoutController = stixLoader.getController();
            	stixLayoutController.setMain(this);

        	} catch (IOException e) {
            	e.printStackTrace();
        	}
    }

	public void showSave(){

		saveLayoutController.setrlc(rootLayoutController);
		saveStage.show();
		saveStage.toFront();

	}

	public void hideSave(){

		saveStage.hide();

	}

	public void showDomains(){

		domainStage.show();
		domainStage.toFront();

	}

	public void updateDomains(){

		domainLayoutController.fill();


	}

	public void clearDomains(){

		domainLayoutController.clearList();

	}

	public void showFeed(){

		jsonLayoutController.setrlc(rootLayoutController);
		jsonStage.show();

	}

	public void hideFeed(){

		jsonStage.hide();

	}

	public void showPaste(){

		pasteLayoutController.setrlc(rootLayoutController);
		pasteStage.show();

	}

	public void hidePaste(){

		pasteStage.hide();

	}

	public void showStix(){

		stixLayoutController.setrlc(rootLayoutController);
		stixStage.show();

	}

	public void hideStix(){

		stixStage.hide();

	}

	public Stage getPrimaryStage() {

        return primaryStage;

    }

	public static void main(String[] args) {

		launch(args);

	}
}
