//Main layout for the UI
package extractor.address.view;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import extractor.address.Main;
import extractor.address.model.IOCObject;
import extractor.address.model.Lookup;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

public class RootLayoutController {

    @FXML private MenuBar bar;
    @FXML private MenuItem refresh;
	@FXML private MenuItem fileOpen;
	@FXML private MenuItem fileSave;
	@FXML private MenuItem textOpen;
	@FXML private MenuItem json;
	@FXML private MenuItem stix;
	@FXML private TextArea md5Txt;
	@FXML private TextArea shaTxt;
	@FXML private TextArea ipTxt;
	@FXML private TextArea domainTxt;
	@FXML private Button md5Button;
	@FXML private Button clearButton;
	@FXML private Label fileLabel;
	@FXML private Label waitLabel;
	@FXML private Label statusLabel;
	@FXML private Button editButton;

	private IOCObject iocs;
    private Main main;
	private LookupService ls;

    //To hold all of the loaded iocs
	private HashSet<String> shas;
	private HashSet<String> domains;
	private HashSet<String> md5s;
	private HashSet<String> ips;

	//private static final String defaultKey = "c495ee6730e1ae0f19e372d22334af10439e242821fb215642e8f938be07144d";
	private String userKey;
	private boolean findStart;

	//newShas contains SHA hashes that have not been looked up yet
	private HashSet<String> newShas;
	//Placeholder for hashes that should be removed from newShas
	private HashSet<String> toRemove;
	//Placeholder for hashes that have been found
	private HashSet<String> foundList;

	public RootLayoutController() throws IOException{

		shas = new HashSet<String>();
		domains = new HashSet<String>();
		md5s = new HashSet<String>();
		ips = new HashSet<String>();
		findStart = false;
		newShas = new HashSet<String>();
		toRemove = new HashSet<String>();
		foundList = new HashSet<String>();

	}

	//Load IOCs from pasted text with the paste layout
	public void textOpen(){

		main.showPaste();

	}

	//Method used by the paste layout to load IOCs from pasted text
	public void textLoad(String input) throws IOException{

		iocs = new IOCObject(input);
		updateUI();

		main.updateDomains();
	}

	//Load IOCs from a file
	public void openFile(){

		try {
			iocs = new IOCObject();
		} catch (IOException e) {
			Alert alert1 = new Alert(AlertType.WARNING);
			alert1.setTitle("Invalid File");
			alert1.setHeaderText("File Invalid");
			alert1.setContentText("Please Select a Valid File");

			alert1.showAndWait();
			e.printStackTrace();
		}

		//Update the file label with the opened files
		updateFilelabel();

		//Add the IOCs from iocfile to the UI
		updateUI();

		main.updateDomains();

	}

	//Uses the lookup class to get MD5 format hashes from SHA format hashes
	public void find() throws Exception{

		//If findStart is true, the lookup service is running and should be stopped
		if(findStart) {ls.cancel(); return;}

		//Set the virustotal user key if it hasn't been set yet
		boolean checkKey = true;
		do{
			checkKey = setKey();
		}while(checkKey);
		
		if(userKey == null) return;

		//Creates a new LookupService to retrieve MD5 hashes on a new thread
		ls = new LookupService();

		//If the service is cancelled, an alert is displayed accordingly and the UI returns to normal
		ls.setOnCancelled(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	for(String x : toRemove) newShas.remove(x);
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Cancelled");
				alert.setContentText("Found " + foundList.size());
				alert.setHeaderText("Cancelled");
				alert.showAndWait();

        		toRemove.clear();
            	foundList.clear();
				statusLabel.textProperty().unbind();
				waitLabel.textProperty().unbind();
				md5Txt.textProperty().unbind();
				waitLabel.setText("");
				statusLabel.setText("");
				md5Button.setText("Get MD5 From SHA With VirusTotal");
				findStart = false;
		        bar.setDisable(false);
		        clearButton.setDisable(false);
            }
		});

		//If the service fails due to a connection problem, an alert is displayed accordingly and the UI returns to normal
		ls.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	for(String x : toRemove) newShas.remove(x);
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Failed");
				alert.setContentText("Found " + foundList.size());
				alert.setHeaderText("Failed. Connection Problem?");
				alert.showAndWait();

        		toRemove.clear();
            	foundList.clear();
				statusLabel.textProperty().unbind();
				waitLabel.textProperty().unbind();
				md5Txt.textProperty().unbind();
				waitLabel.setText("");
				statusLabel.setText("");
				md5Button.setText("Get MD5 From SHA With VirusTotal");
				findStart = false;
		        bar.setDisable(false);
		        clearButton.setDisable(false);
            }
		});

		//If the service succeeds, an alert is displayed accordingly and the UI returns to normal
		ls.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
            	if(newShas.isEmpty()){
            		Alert alert = new Alert(AlertType.INFORMATION);
    				alert.setTitle("Nothing New to Find");
    				alert.setHeaderText("Nothing new to find");
    				alert.showAndWait();
            	}else{
            		for(String x : toRemove) newShas.remove(x);
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("MD5s Found");
					alert.setContentText("Found " + foundList.size());
					alert.setHeaderText("MD5s Found");
					alert.showAndWait();
				}

        		toRemove.clear();
            	foundList.clear();
				statusLabel.textProperty().unbind();
				waitLabel.textProperty().unbind();
				md5Txt.textProperty().unbind();
				waitLabel.setText("");
				statusLabel.setText("");
				md5Button.setText("Get MD5 From SHA With VirusTotal");
				findStart = false;
		        bar.setDisable(false);
		        clearButton.setDisable(false);
            }
		});

		//The LookupService is started and other UI functions are disabled as necessary. The button to call this method will now cancel the lookup service.
		//The md5 text area, and ratelabel will be updated from the service
        bar.setDisable(true);
        clearButton.setDisable(true);
        md5Button.setText("Cancel");
		findStart = true;
		waitLabel.textProperty().bind(ls.timeBinder);
		statusLabel.textProperty().bind(ls.statusBinder);
		md5Txt.textProperty().bind(ls.md5List);
        ls.start();

	}

	//Clears the IOCs
	public void clear(){

		domainTxt.setText("");
		md5Txt.setText("");
		ipTxt.setText("");
		shaTxt.setText("");
		iocs = null;
		shas = new HashSet<String>();
		domains = new HashSet<String>();
		md5s = new HashSet<String>();
		ips = new HashSet<String>();
		fileLabel.setText("");
		main.clearDomains();
		newShas.clear();

	}

	//Allows the VirusTotal API key for find MD5 hashes to be set
	private boolean setKey(){

		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("API Key");
		dialog.setHeaderText("Enter a valid VirusTotal API Key.\n(A key may be acquired by creating a free VirusTotal account)");
		dialog.setContentText("");
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()){
		    if(result.get().replaceAll("\\b[a-fA-F0-9]{64}\\b","").isEmpty() && !result.get().isEmpty()) {
		    	userKey = result.get();
		    	return false;
		    }
		    else{
		    	Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Invalid Key");
				alert.setHeaderText("Invalid Key");
				alert.setContentText("Invalid Key");

				alert.showAndWait();
				return true;
		    }
		}else return false;


	}

	//Updates the label to keep track of which files have been opened
	private void updateFilelabel(){

		if (fileLabel.getText() == "") {
			fileLabel.setText("Files: ");
			Iterator<File> iter1 = iocs.getFiles().iterator();
			File x = iter1.next();
			fileLabel.setText(fileLabel.getText() + " " + x.toPath().getFileName().toString());

			while (iter1.hasNext()) fileLabel.setText(fileLabel.getText() + ", " + iter1.next().toPath().getFileName().toString());

		}else{
			for (File file : iocs.getFiles()) fileLabel.setText(fileLabel.getText() + ", " + file.toPath().getFileName().toString());
		}

	}

	//Updates the UI when the IOCs lists change
	private void updateUI(){

		for (String x : iocs.getMd5s()) md5s.add(x.toLowerCase());

		for (String x : iocs.getSha1s()){
			shas.add(x.toLowerCase());
			newShas.add(x.toLowerCase());
		}

		for (String x : iocs.getSha256s()){
			shas.add(x.toLowerCase());
			newShas.add(x.toLowerCase());
		}

		for (String x : iocs.getIpv4s()) ips.add(x.toLowerCase());

		for (String x : iocs.getDomains()) domains.add(x.toLowerCase());

		shaTxt.clear();
		for (String x : shas) shaTxt.appendText(x + "\n");

		md5Txt.clear();
		for (String x : md5s) md5Txt.appendText(x + "\n");

		ipTxt.clear();
		for (String x : ips) ipTxt.appendText(x + "\n");

		domainTxt.clear();
		for (String x : domains) domainTxt.appendText(x + "\n");

	}

	//To change the API key
	public void refresh(){

		userKey = null;

		setKey();

	}

	public void saveFile(){
		main.showSave();
	}

	public void editDomains(){
		main.showDomains();
	}

	public void createJson(){
		main.showFeed();
	}

	public void createStix(){
		main.showStix();
	}

	public void setMain(Main main){
		this.main = main;
	}

	public TextArea getmd5Txt() {
		return md5Txt;
	}

	public TextArea getshaTxt() {
		return shaTxt;
	}

	public TextArea getipTxt() {
		return ipTxt;
	}

	public TextArea getdomainTxt() {
		return domainTxt;
	}

	public HashSet<String> getDomains() {
		return domains;
	}

	//Service to allow MD5 hashes to be retrieved from the Virustotal API on a different thread while the UI is continuously updated.
	private class LookupService extends Service<Void> {

		//The appropriate UI elements will be bound to these variables
        public StringProperty md5List;
        public StringProperty statusBinder;
        public StringProperty timeBinder;
        //The total number of hashses that will be looked up
        private final Double tries;

        LookupService(){

        	String nextmd5List = new String();
			for (String y : md5s) nextmd5List = nextmd5List.concat(y + "\n");
        	md5List = new SimpleStringProperty(this, "");
        	md5List.set(nextmd5List);
        	statusBinder = new SimpleStringProperty(this, "");
        	timeBinder = new SimpleStringProperty(this, "");
        	tries = new Double(newShas.size());

        }

        protected Task<Void> createTask() {
            return new Task<Void>() {
                protected Void call() throws Exception {
                	Long start = System.currentTimeMillis();
            		Double count = 0.0;
            		String next = new String();

    				for(String x : newShas){
    					//The service will end if it's been cancelled
    					if(isCancelled()) return null;

    					//Update the rateLabel to show which hash is being looked up
    					Platform.runLater(new Runnable(){

							@Override
							public void run() {
								statusBinder.set("Looking up " + x);

							}

    					});

    					//Calculates the remaining time to look up the hashes
    					int eta = (int)Math.ceil((tries - count) / 4);

    					do{
    						//Every iteration, the elapsed time is shown in the UI
    						Platform.runLater(new Runnable(){

								@Override
								public void run() {
									timeBinder.set(Long.toString(new Long((System.currentTimeMillis() - start)/1000).intValue()));

								}

	    					});

    						//Try to lookup the next hash in the list of SHAs with the Lookup class. If the lookup fails, so will the service.
    						next = Lookup.getmd5(x, userKey);

    						//If the lookup succeeds in returning something
    						if(!(next == null)){
        						final String found = new String(next);

        						//If the lookup returns a non-empty string, the MD5 was found and the UI updates accordingly
    							if(!next.isEmpty()) {
    								Platform.runLater(new Runnable(){

    									@Override
    									public void run() {
    										statusBinder.set("Found: " + found + " From " + x + ". ETA: " + eta + " minute(s).");

    									}

    		    					});

    								md5s.add(next);
    								foundList.add(x);

    								String nextmd5List = new String();
    								for (String y : md5s) nextmd5List = nextmd5List.concat(y + "\n");

    								md5List.set(nextmd5List);

    							//If the lookup returns an empty string, the MD5 was not found and the UI updates accordingly
    							}else{

    								Platform.runLater(new Runnable(){

    									@Override
    									public void run() {
    										statusBinder.set("MD5 not found for " + x + ". ETA: " + eta + " minute(s).");

    									}

    		    					});

    							}
    						//If nothing is returned by the lookup, then the request rate limit for the API must have been reached, and the UI updates accordingly.
    						}else {
    							Platform.runLater(new Runnable(){

									@Override
									public void run() {
										statusBinder.set("Request rate limit reached" + ". ETA: " + eta + " minute(s).");

									}
		    					});
    						}

    					//Continue looking up the hash until a string is returned by the lookup
    					}while(next == null);

    					toRemove.add(x);
    					count++;
    				}

    				return null;
                }
            };
        }
    }

}
