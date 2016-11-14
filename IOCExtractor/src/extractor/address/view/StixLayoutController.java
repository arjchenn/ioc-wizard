//This class is the layout for feeds from IOCs in json format
package extractor.address.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.UUID;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.mitre.cybox.common_2.HashListType;
import org.mitre.cybox.common_2.HashType;
import org.mitre.cybox.common_2.SimpleHashValueType;
import org.mitre.cybox.cybox_2.ObjectType;
import org.mitre.cybox.cybox_2.Observable;
import org.mitre.cybox.default_vocabularies_2.HashNameVocab10;
import org.mitre.cybox.objects.FileObjectType;
import org.mitre.stix.common_1.IndicatorBaseType;
import org.mitre.stix.indicator_2.Indicator;
import org.mitre.stix.stix_1.IndicatorsType;
import org.mitre.stix.stix_1.STIXPackage;
import org.xml.sax.SAXException;

import extractor.address.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class StixLayoutController {

	@FXML private Button createButton;
	@FXML private TextField name;

	private RootLayoutController rlc;
	private Main main;

	public void setrlc(RootLayoutController rlc) {

		this.rlc = rlc;

	}

	@SuppressWarnings("serial")
	public void createStix() throws IOException, DatatypeConfigurationException{

		//Displays a warning if the name field is empty
		if(name.getText().isEmpty())
		{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Fields empty");
			alert.setHeaderText("Empty fields");
			alert.setContentText("Please Fill in name");

			alert.showAndWait();

			return;
		}

		//User sets filepath
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("STIX files (*.stix)", "*.stix");
		fileChooser.getExtensionFilters().add(extFilter);

		File outFile = fileChooser.showSaveDialog(new Stage());
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile.getAbsolutePath()));

		String[] iocs = null;

		ArrayList<String> sha256 = new ArrayList<String>();
		ArrayList<String> sha1 = new ArrayList<String>();
		ArrayList<String> md5 = new ArrayList<String>();
		//Initialize a list of indicators
		ArrayList<IndicatorBaseType> indList = new ArrayList<IndicatorBaseType>();

		//Get the iocs from the root layout
		iocs = rlc.getshaTxt().getText().split("[\\n]+");
		for(String i : iocs) {
			if(i.length() == 40) sha1.add(i);
			if(i.length() == 64) sha256.add(i);
		}

		iocs = rlc.getmd5Txt().getText().split("[\\n]+");
		for(String i : iocs) md5.add(i);

		//Create indicators for each of the md5s
		for(String i : md5){
			XMLGregorianCalendar now = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));

			FileObjectType fileObject = new FileObjectType()
					.withHashes(new HashListType(new ArrayList<HashType>() {
						{
							add(new HashType()
									.withType(new HashNameVocab10().withValue("MD5"))
									.withSimpleHashValue(new SimpleHashValueType().withValue(i)));
						}
					}));

			ObjectType obj = new ObjectType().withProperties(fileObject)
					.withId(new QName("file-" + UUID.randomUUID().toString(), "example"));

			Observable observable = new Observable().withId(new QName("observable-"
					+ UUID.randomUUID().toString(), "example"));

			observable.setObject(obj);

			final Indicator indicator = new Indicator()
					.withId(new QName("http://example.com/", "indicator-" + UUID.randomUUID().toString(), "example"))
					.withTimestamp(now)
					.withTitle("MD5 for " + name.getText())
					.withObservable(observable);

			indList.add(indicator);
		}

		//Create indicators for each of the sha256s
		for(String i : sha256){
			XMLGregorianCalendar now = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));

			FileObjectType fileObject = new FileObjectType()
					.withHashes(new HashListType(new ArrayList<HashType>() {
						{
							add(new HashType()
									.withType(new HashNameVocab10().withValue("SHA256"))
									.withSimpleHashValue(new SimpleHashValueType().withValue(i)));
						}
					}));

			ObjectType obj = new ObjectType().withProperties(fileObject)
					.withId(new QName("file-" + UUID.randomUUID().toString(), "example"));

			Observable observable = new Observable().withId(new QName("observable-"
					+ UUID.randomUUID().toString(), "example"));

			observable.setObject(obj);

			final Indicator indicator = new Indicator()
					.withId(new QName("http://example.com/", "indicator-" + UUID.randomUUID().toString(), "example"))
					.withTimestamp(now)
					.withTitle("SHA256 for " + name.getText())
					.withObservable(observable);

			indList.add(indicator);
		}

		//Create indicators for each of the sha1s
		for(String i : sha1){
			XMLGregorianCalendar now = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));

			FileObjectType fileObject = new FileObjectType()
					.withHashes(new HashListType(new ArrayList<HashType>() {
						{
							add(new HashType()
									.withType(new HashNameVocab10().withValue("SHA1"))
									.withSimpleHashValue(new SimpleHashValueType().withValue(i)));
						}
					}));

			ObjectType obj = new ObjectType().withProperties(fileObject)
					.withId(new QName("file-" + UUID.randomUUID().toString(), "example"));

			Observable observable = new Observable().withId(new QName("observable-" + UUID.randomUUID().toString(), "example"));

			observable.setObject(obj);

			final Indicator indicator = new Indicator()
					.withId(new QName("http://example.com/", "indicator-" + UUID.randomUUID().toString(), "example"))
					.withTimestamp(now)
					.withTitle("SHA1 for " + name.getText())
					.withObservable(observable);

			indList.add(indicator);
		}

		//Create indicators object from the indicator list
		IndicatorsType indicators = new IndicatorsType(indList);

		XMLGregorianCalendar now = DatatypeFactory.newInstance()
				.newXMLGregorianCalendar(new GregorianCalendar(TimeZone.getTimeZone("UTC")));

		//Create a STIX package from the indicators and write it to the file in the appropriate XML format
		STIXPackage stixPackage = new STIXPackage()
				.withIndicators(indicators)
				.withVersion("1.2")
				.withTimestamp(now)
				.withId(new QName("package-" + UUID.randomUUID().toString(), "example"));

		writer.write(stixPackage.toXMLString(true));
		writer.close();

		try {
			System.out.println("Validates: " + stixPackage.validate());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		main.hideStix();
	}

	public void setMain(Main main){

		this.main = main;

	}
}
