//The layout for pasting to load IOCs from
package extractor.address.view;

import java.io.IOException;

import extractor.address.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class PasteLayoutController {
	@FXML private TextArea inputText;
	@FXML private Button loadButton;

	private Main main;
	private RootLayoutController rlc;

	//Calls the textLoad method from the root layout, with the contents of this layout's text area
	public void loadText() throws IOException{

		rlc.textLoad(inputText.getText());
		main.hidePaste();

	}

	public void setrlc(RootLayoutController rlc){

		this.rlc = rlc;

	}

	public void setMain(Main main){

		this.main = main;

	}
}
