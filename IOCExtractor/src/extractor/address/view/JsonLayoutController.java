//This class is the layout for feeds from IOCs in json format
package extractor.address.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import extractor.address.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class JsonLayoutController {

	@FXML private Button createButton;
	@FXML private TextField feedName;
	@FXML private TextField feedDisplayName;
	@FXML private TextField feedProvider;
	@FXML private TextField feedSummary;
	@FXML private TextField techData;
	@FXML private TextField reportName;
	@FXML private RadioButton includeMD5;
	@FXML private RadioButton includeIPv4;
	@FXML private RadioButton includeDomain;

	private RootLayoutController rlc;
	private Main main;

	public void setrlc(RootLayoutController rlc) {

		this.rlc = rlc;

	}

	//Produces the required md5 hash for the feed
	private String genID(){

		String hasher = new String();
		if(includeIPv4.isSelected()) hasher = hasher.concat(rlc.getipTxt().getText());
		if(includeDomain.isSelected()) hasher = hasher.concat(rlc.getdomainTxt().getText());
		if(includeMD5.isSelected()) hasher = hasher.concat(rlc.getmd5Txt().getText());

		return DigestUtils.md5Hex(hasher);

	}

	public void createFeed() throws IOException{

		//Displays a warning if any fields in the UI are empty
		if(feedName.getText().isEmpty() || feedDisplayName.getText().isEmpty() || feedProvider.getText().isEmpty()
				|| feedSummary.getText().isEmpty() || techData.getText().isEmpty() || reportName.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Fields empty");
			alert.setHeaderText("Empty fields");
			alert.setContentText("Please Fill in fields");

			alert.showAndWait();

			return;
		}

		//Users sets filepath of feed
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

		File outFile = fileChooser.showSaveDialog(new Stage());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsolutePath()));

		//The feed requires a timestamp
		Long timeStamp = new Long(System.currentTimeMillis() / 1000L);

		//The map and arraylist objects will hold the various components of the feed and will later be converted into a json object with the proper formatting
		LinkedHashMap<Object, Object> output = new LinkedHashMap<Object, Object>();
		LinkedHashMap<Object, Object> feedInfo = new LinkedHashMap<Object, Object>();

		feedInfo.put("provider_url", feedProvider.getText());
		feedInfo.put("display_name", feedDisplayName.getText());
		feedInfo.put("name", feedName.getText());
		feedInfo.put("tech_data", techData.getText());
		feedInfo.put("summary", feedSummary.getText());

		output.put("feedinfo", feedInfo);

		ArrayList<Object> reports = new ArrayList<Object>();
		LinkedHashMap<Object, Object> report1 = new LinkedHashMap<Object, Object>();

		report1.put("title", reportName.getText());
		report1.put("timestamp", timeStamp);

		//The appropriate data is taken from the root layout and added to the json object with the proper formatting
		LinkedHashMap<Object, Object> iocs = new LinkedHashMap<Object, Object>();
		ArrayList<Object> ipv4 = new ArrayList<Object>();
		ArrayList<Object> domain = new ArrayList<Object>();
		ArrayList<Object> md5 = new ArrayList<Object>();

		if(includeIPv4.isSelected() && !rlc.getipTxt().getText().isEmpty()){
			String[] x = rlc.getipTxt().getText().split("[\\n]+");
			for(String i : x) ipv4.add(i);
			iocs.put("ipv4", ipv4);
		}

		if(includeDomain.isSelected() && !rlc.getdomainTxt().getText().isEmpty()){
			String[] x = rlc.getdomainTxt().getText().split("[\\n]+");
			for(String i : x) domain.add(i);
			iocs.put("dns", domain);
		}

		if(includeMD5.isSelected() && !rlc.getmd5Txt().getText().isEmpty()){
			String[] x = rlc.getmd5Txt().getText().split("[\\n]+");
			for(String i : x) md5.add(i);
			iocs.put("md5", md5);
		}

		report1.put("iocs", iocs);
		report1.put("score", 100);
		report1.put("link", feedProvider.getText());
		report1.put("id",  genID());
		reports.add(report1);

		//the components of the feed are in one list and then converted to json and written to the output file
		output.put("reports", reports);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String pretty = gson.toJson(output);
		System.out.println(pretty);

		writer.write(pretty);
		writer.close();

		main.hideFeed();
	}

	public void setMain(Main main){

		this.main = main;

	}
}
