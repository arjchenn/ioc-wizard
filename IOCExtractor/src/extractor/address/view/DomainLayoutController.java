//This class is the layout for the editable domain checklist
package extractor.address.view;

import org.controlsfx.control.CheckListView;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DomainLayoutController {

	@FXML private CheckListView<String> checkListView;
	@FXML private Button deselectButton;
	@FXML private Button selectButton;

	private RootLayoutController rlc;

	public DomainLayoutController (){

	}

	//Fill the checklistview with the domain list from the root layout
	public void fill(){

		checkListView.setItems(FXCollections.observableArrayList(rlc.getDomains()));

        checkListView.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
		     public void onChanged(ListChangeListener.Change<? extends String> c) {
		         ObservableList<String> newList = checkListView.getCheckModel().getCheckedItems();
		         rlc.getdomainTxt().clear();
		         for (String x : newList) rlc.getdomainTxt().appendText(x + "\n");
		     }
		 });

	}

	public void clearList(){

		checkListView.getItems().clear();

	}

	public void deselectAll(){

		checkListView.getCheckModel().clearChecks();

	}

	public void selectAll(){

		checkListView.getCheckModel().checkAll();

	}

	public void setrlc(RootLayoutController rlc){

		this.rlc = rlc;
		fill();

	}


}
