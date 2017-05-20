package org.jabst.jabs;

import javafx.event.ActionEvent;//type of event
import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.geometry.Insets;//insets = padding
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.layout.HBox;
import javafx.scene.Scene;//area inside stage
import javafx.stage.WindowEvent;//when window closes
import javafx.stage.Stage;//window

import java.sql.SQLException;//for error handling

public class SuperUserGUI {

    private static DatabaseManager dbm;


    public static void display(SessionManager session) {
        /* Data getters */
        dbm = session.getDatabaseManager();

        /* Commons elements */
        Insets ins = new Insets(3.0, 3.0, 3.0, 3.0);
        
        /* Setup window elements */
        VBox root = new VBox();//layout manager
            HBox hbDeleteBus = new HBox();
                ComboBox<Business> cbBusSelect = new ComboBox<Business>();
                updateCombobox(cbBusSelect);
                /*cbBusSelect.getItems().addAll(
                    dbm.getBusiness("default_business")
                );*/
                Label lbBusSelect = new Label("Delete business");
                lbBusSelect.setLabelFor(cbBusSelect);
                lbBusSelect.setPadding(ins);
                Button btDelBusiness = new Button("Delete...");
                btDelBusiness.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        boolean delete = ConfirmGUI.display(
                            "Are you sure you want to delete business\n '"
                            +cbBusSelect.getValue()
                            +"'?. \nThere is no way to undo this!"
                        );
                        if (delete) {
                            try {
                                dbm.deleteBusiness(
                                    cbBusSelect.getValue()
                                );
                                SuperUserGUI.updateCombobox(cbBusSelect);
                            } catch (SQLException sqle) { 
                                /* TODO: Error reporting */
                            }
                        }
                        
                    }
                });
            hbDeleteBus.getChildren().addAll(
                lbBusSelect, cbBusSelect, btDelBusiness
            );
            hbDeleteBus.setSpacing(5);
            
            HBox hbNewBus = new HBox();
            Button btNewBus = new Button("Create...");
            btNewBus.setOnAction(new EventHandler<ActionEvent>()  {
                @Override
                public void handle(ActionEvent e) {
                    CreateBusinessGUI.display(session);
                    updateCombobox(cbBusSelect);
                }
            });
            Label lbNewBus = new Label("Create business");
            lbNewBus.setLabelFor(btNewBus);
            lbNewBus.setPadding(ins);
            hbNewBus.getChildren().addAll(lbNewBus, btNewBus);
        root.getChildren().addAll(hbDeleteBus, hbNewBus);
        /* Root spacing and padding */
        root.setSpacing(2);
        root.setPadding(new Insets(5.0, 0.0, 5.0, 10.0));
        
        Scene scene = new Scene(root, 400, 200);//create area inside window
        Stage window = new Stage();
        /* Close window on request */
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Business Menu window Closed");
            }
        });
        /* Window visual setup */
        window.setTitle("JABST: Superuser Menu");//text at the top of the window
        window.setScene(scene);//add scene to window
        window.showAndWait();//put the window on the desktop
    }
    
    static void updateCombobox(ComboBox<Business> cb) {
        cb.getItems().clear();
        cb.getItems().setAll(dbm.getAllBusinesses());
    }
}

