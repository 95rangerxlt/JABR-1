package org.jabst.jabs;

import javafx.event.EventHandler;//this activates when a button is pressed
import javafx.geometry.Insets;//insets = padding
import javafx.scene.control.*;//buttons, labels  etc.
import javafx.scene.layout.VBox;//layout manager
import javafx.scene.Scene;//area inside stage
import javafx.stage.WindowEvent;//when window closes
import javafx.stage.Stage;//window

public class SuperUserGUI {
    public static void display(SessionManager session) {

        /* Data getters */
        DatabaseManager dbm = session.getDatabaseManager();

        /* Setup window elements */
        Stage window = new Stage();
        VBox root = new VBox();//layout manager
        ComboBox<Business> cbBusSelect = new ComboBox<Business>();
        Label lbBusSelect = new Label("Delete a business");
        
        lbBusSelect.setLabelFor(cbBusSelect);
        
        cbBusSelect.getItems().addAll(dbm.getBusiness("default_business"));
        root.getChildren().addAll(cbBusSelect);
        /* Root spacing and padding */

        
        /* Close window on request */
        window.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.out.println("Business Menu window Closed");
            }
        });

        /* Window visual setup */
        Scene scene = new Scene(root, 300, 200);//create area inside window
        root.setSpacing(2);
        root.setPadding(new Insets(3.0, 3.0, 3.0, 3.0));
        window.setTitle("JABST: Superuser Menu");//text at the top of the window
        window.setScene(scene);//add scene to window
        window.showAndWait();//put the window on the desktop
    }
}