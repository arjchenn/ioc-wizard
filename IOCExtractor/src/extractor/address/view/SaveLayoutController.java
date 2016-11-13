//This class allows the IOCs to be saved to a text file
package extractor.address.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import extractor.address.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SaveLayoutController {
	@FXML private RadioButton md5Button;
	@FXML private RadioButton shaButton;
	@FXML private RadioButton ipButton;
	@FXML private RadioButton domainButton;
	@FXML private Button saveButton;

	private RootLayoutController rlc;
	private Main main;

	public void setrlc(RootLayoutController rlc){

		this.rlc = rlc;

	}

	public void save() throws IOException{

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

		File outFile = fileChooser.showSaveDialog(new Stage());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsoluteFile()));

		if(md5Button.isSelected()) writer.write(rlc.getmd5Txt().getText());
		if(shaButton.isSelected()) writer.write(rlc.getshaTxt().getText());
		if(ipButton.isSelected()) writer.write(rlc.getipTxt().getText());
		if(domainButton.isSelected()) writer.write(rlc.getdomainTxt().getText());

		writer.close();

		main.hideSave();

	}

	public void setMain(Main main){

		this.main = main;

	}
}
