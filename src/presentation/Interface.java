package presentation;



import java.io.File;

import businessLogic.TextHandler;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import javafx.scene.control.TextArea;
import javafx.scene.control.CheckBox;
import javafx.beans.value.ObservableValue;

import businessLogic.TextObject;

/**
 * Interface is the class which creates all
 * windows, buttons etc.
 * Interface contains the main class and starts the application.
 * It reacts to inputs like clicks, and calls the business layer (TextHandler)
 * to process the data, as well as displaying the (decrypted)
 * texts it receives from the layers beneath.
 * 
 * 
 * @author fritz
 *
 */
public class Interface extends Application {
	
	/*The transfer text object that gets passed between layers.*/
	static TextObject to;
	
	/*The text field where the user writes her text.*/
	static TextArea userTextField;
	
	public static void main(String[] args) {
		
		to = new TextObject();

		SetEncryption("AES", "CTR", "NoPadding", 192);

		setText("Hi there. This isSHal9000".getBytes());

        launch(args);
    }
    
    /* (non-Javadoc)
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @SuppressWarnings("restriction")
	@Override
    public void start(final Stage primaryStage) {
        primaryStage.setTitle("KryptPad BETA");
        userTextField = new TextArea(); /*Initialize text field.*/
        
        //Load button
        Button loadButton = new Button();
        loadButton.setText("Load");        
        loadButton.setOnAction(new EventHandler<ActionEvent>() {
 
            public void handle(ActionEvent event) {
                loadFile(primaryStage);
            }
        });
        
        
        //Save button
        Button saveButton = new Button();
        saveButton.setText("Save");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {
 
            public void handle(ActionEvent event) {
                System.out.println("accessible text: "+ userTextField.getText());
                setText(userTextField.getText().getBytes());
                save();
            }
        });
        
       
        //SaveAs button
        Button saveAsButton = new Button();
        saveAsButton.setText("Save as...");
        saveAsButton.setOnAction(new EventHandler<ActionEvent>()
        {
 
            public void handle(ActionEvent event)
            {
                System.out.println("accessible text: "+ userTextField.getText());
                setText(userTextField.getText().getBytes());
                saveAs();
            }
        });
        
        //CheckBox for toggling line break
        final CheckBox wrappingBox = new CheckBox("line break");
        wrappingBox.selectedProperty().addListener(new ChangeListener<Boolean>()
        {
        	public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
        	{
        		userTextField.setWrapText(new_val ? true : false);
        	}
        }
        );
              
        //grid that contains the fields/buttons               
        GridPane grid = new GridPane();

        grid.setPadding(new Insets(25, 25, 25, 25));
        
        HBox box = new HBox();
        box.getChildren().add(saveButton);
        box.getChildren().add(saveAsButton);
        box.getChildren().add(loadButton);
        box.getChildren().add(wrappingBox);

        grid.add(box, 1, 2);
        grid.add(userTextField, 1, 1);

        Scene scene = new Scene(grid, 500, 575);
        
        
        
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	
	public Interface()
	{
		/*A function that loads default values at the
		start of the programme was planned, but not implemented*/
		TextHandler.loadDefaultValues(); 
	}
	
	/**
	 * Writes encryption params into the transfer Object to.
	 * @param algorithm
	 * @param blockCipherMode
	 * @param padding
	 * @param keyLength
	 */
	public static void SetEncryption(String algorithm, String blockCipherMode, String padding, int keyLength)
	{
		to.encryption = algorithm;
		to.blockCipherMode = blockCipherMode;
		to.padding = padding;
		to.keyLength = keyLength;
		
	}
	
	public static void setText(byte[] text)
	{
		to.text = text;
	}
	
	/**
	 * Tries to save the text with the previous settings.
	 * If the file was not saved before in this session,
	 * the "saveAs" dialogue opens up.
	 */
	public static void save()
	{
		if(TextHandler.lastFilePath!=null)
		{
			System.out.println("Using last file path: "+ TextHandler.lastFilePath);
			TextHandler.saveAs(TextHandler.lastFilePath, "", to);
		}
		else saveAs();
	}
	
	/**
	 * Opens a window that allows the user to choose
	 * a filename and all the encryption settings.
	 * The save button applies the encryption and
	 * persists the text as a file.
	 * Does allow invalid setting combinations, so user
	 * knowledge is required (or trial and error).
	 */
	@SuppressWarnings("restriction")
	public static void saveAs()
	{		
		final Stage stage = new Stage();
		final TextField filenameTextField = new TextField("filename.txt");
		final TextField password= new TextField("");

		
		//Dropdown menus for encryption settings
		final ChoiceBox encryptionCB = new ChoiceBox(FXCollections.observableArrayList(
			    "AES", "DES", "RSA", "ARC4", "", "Invalid Value")
			);
		final ChoiceBox blockCipherCB = new ChoiceBox(FXCollections.observableArrayList(
			    "ECB", "CBC", "CTR", "OFB", "CTS", "GCM", "NONE", "", "Invalid Value")
			);
		final ChoiceBox<String> paddingCB = new ChoiceBox<String>(FXCollections.observableArrayList(
			    "NoPadding", "PKCS7Padding", "ZeroBytePadding", "", "Invalid Value")
			);
		final ChoiceBox keylengthCB = new ChoiceBox(FXCollections.observableArrayList(
			    "40", "64", "128", "192", "256", "1024", "4096", "", "Invalid Value")
			);
		
		final ChoiceBox pbeModeCB = new ChoiceBox(FXCollections.observableArrayList(
			    "PBEWithSHAAND128BitAES-CBC-BC", "PBEWithMD5AndDES", "PBEWithSHAAnd40BitRC4", "Invalid Value")
			);
		final ChoiceBox digestCB = new ChoiceBox(FXCollections.observableArrayList(
			    "SHA1", "MD5", "SHA224", "SHA256", "SHA384", "SHA512")
			);
		
		
		//The grid that contains the fields and buttons
		final GridPane grid = new GridPane();
		
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        //add the text field/drop down menus to the grid
		grid.add(filenameTextField, 1, 1);
		grid.add(encryptionCB, 1, 2);
		grid.add(blockCipherCB, 1, 3);
		grid.add(paddingCB, 1, 4);
		grid.add(keylengthCB, 1, 5);
		grid.add(digestCB, 1, 6);
		
		//The save button starts the encryption and file writing process with the chosen settings
		Button saveButton2 = new Button();
        saveButton2.setText("Save");
        saveButton2.setOnAction(new EventHandler<ActionEvent>()
        {
 
            public void handle(ActionEvent event)
            {
            	System.out.println("password: " + password.getText());
            	
            	if(!password.getText().equals("")) //only if a password has been chosen, the PBE mode will be set
            	{
            		to.password = password.getText();
            		to.pbeMode = pbeModeCB.getValue().toString();
            	}
            
            	if(digestCB.getValue()!=null)
            	{
            		to.digest = digestCB.getValue().toString();
            		System.out.println("using digest (Interface)");            	
            	}
            	
            	//reading values from choice boxes and writing them into the transfer textObject
            	SetEncryption(encryptionCB.getValue().toString(), blockCipherCB.getValue().toString(), paddingCB.getValue().toString(), Integer.parseInt(keylengthCB.getValue().toString()));
            	System.out.println("textObject keylength: "+ to.keyLength);
            	//All files are saved in this directory
            	String path = "/home/fritz/";
        		to.name = filenameTextField.getText();
        		
        		TextHandler.saveAs(path, to.name, to);
        		
        		stage.close();
            }
            
        }
        );
        
		grid.add(saveButton2, 2, 3);
		
		//Checking this CheckBox reveals PBE Options
		final CheckBox enablePBE = new CheckBox("use a password");
        enablePBE.selectedProperty().addListener(new ChangeListener<Boolean>()
        {
        	public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val)
        	{
        		if(new_val ? true : false)
        		{
        			password.setText("password12345678");
        			grid.add(password, 1, 8);
        			grid.add(pbeModeCB, 1, 9);
        		}
        		else
        		{
        			//TO DO: remove PBE Options when box is unchecked.
        		}
        	}
        }
        );
        grid.add(enablePBE, 1, 7);
        
		Scene scene = new Scene(grid, 300, 275);
		stage.setScene(scene);
		stage.show();
		
	}
	
	
	
	/**
	 * Opens a file chooser window to select a text file,
	 * then load it.
	 * Automatically opens another windows, either for entering
	 * a password or for choosing a key file.
	 * @param stage The stage from where the window is opened.
	 */
	public static void loadFile(Stage stage)
	{
		final FileChooser fileChooser = new FileChooser();
	
		File file = fileChooser.showOpenDialog(stage);
		if(file!=null)
		{
            	String path = file.getAbsolutePath();
            	System.out.println("Opening File " + path);
        		String filename = "";
        		to = TextHandler.load(path+filename);
        		System.out.println("PBE Mode: " + to.pbeMode);
        		
        		//if the text file doesn't contain PBE information, it is assumed that a key file is needed.
        		if(to.pbeMode==null||to.pbeMode.equals(""))
        		{
        			loadKey(stage);
        		}
        		else passwordDialogue();
		}        
	}
	
	/**
	 * Opens a file chooser window to load a key file,
	 * then starts the decryption process and
	 * writes the decrypted text to the text field.
	 * Can be a .key or a .privatekey file.
	 * @param stage The stage from where the window is opened.
	 */
	public static void loadKey(Stage stage)
	{
		final FileChooser fileChooser = new FileChooser();
	
		File file = fileChooser.showOpenDialog(stage);
		if(file!=null)
		{
            	String path = file.getAbsolutePath();
            	System.out.println("Opening Key " + path);
        		String filename = "";
        		
        		to.symmetricKey = TextHandler.loadKey(path+filename); // Does not actually have to be a symmetric key.
        		TextHandler.decrypt(to);
        		System.out.println("Decrypted text after loading: "+new String(to.text));
        		userTextField.setText(new String(to.text));
		}        
	}
	
	/**
	 * A simple text input field dialogue for a password (for PBE).
	 * The "Decrypt"-button starts the decryption process and
	 * writes the decrypted text to the text field.
	 */
	public static void passwordDialogue()
	{
		final Stage stage = new Stage();
		final TextField passwordTextField = new TextField("enter password");
		GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
		grid.add(passwordTextField, 1, 1);
		
		Button decryptButton = new Button();
        decryptButton.setText("Decrypt");
        decryptButton.setOnAction(new EventHandler<ActionEvent>() {
 
            public void handle(ActionEvent event) {
            	to.password = passwordTextField.getText();
            	TextHandler.decrypt(to);
        		System.out.println("Decrypted text after loading: "+new String(to.text));
        		userTextField.setText(new String(to.text));
        		stage.close();
            }
        });
        
        
        grid.add(decryptButton, 2, 2);
		
		Scene scene = new Scene(grid, 300, 275);
		stage.setScene(scene);
		stage.show();
	}

}
