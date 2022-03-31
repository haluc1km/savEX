package com.saveEX;
// program class, lookup method, and readFile method by Jovane

import java.awt.AWTException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.OutputFormat;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SampleController implements Initializable {
    @FXML private Label lbl, error;
    @FXML private Button button, transcript, startTrans, stopTrans, createTrans;
    @FXML private TextField txtlbl;
    @FXML private TextFlow text1, liveText;
    @FXML private Label ap, fileName;
    private int val;
	private String [] fileParts;
	private static String selectedFilename;	
	private Scanner file;
	private static Stage stg;
	int i = 0;
	boolean stopRec = false;
	
	Program recog = new Program();
	//Changes main scene to the upload file scene
	  public void handleBtn(ActionEvent actionEvent) throws IOException {
		  Main m = new Main();
		  m.changeScene("Second.fxml");
}
	  //changes main scene to create transcript scene
	  public void transBut(ActionEvent actionEvent) throws IOException {
		  Main m = new Main();
		  m.changeScene("transcriptScreen.fxml");
}
	  //back button : stops active recording and takes you back to main page
	  public void backTranscriptSc(ActionEvent actionEvent) throws IOException {
		  stopRec = true;
		  recog.stopRecording();
		  Main m = new Main();
		  m.changeScene("Sample.fxml");
		  
}
	  //back button: takes you from the upload file scene to main scene
	  public void backSecond(ActionEvent actionEvent) throws IOException {
		  Main m = new Main();
		  m.changeScene("Sample.fxml");
}
	  //back button: takes you from file search scene back to upload file scene
	  public void backFile(ActionEvent actionEvent) throws IOException {
		  Main m = new Main();
		  m.changeScene("Second.fxml");

}
	  //button to start transcript recording
	  public void recordStart(ActionEvent actionEvent) throws IOException {
		  Text starting = new Text("Starting recording..." + "\n");
      	liveText.getChildren().add(starting);
		  new Thread(() ->{
				try {
					recog.startRecording();
					recog.beginRecord();

				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}).start();
}
	  //button to stop recording the transcript
	  public void recordStop(ActionEvent actionEvent) throws IOException {
		  stopRec = true;
		  recog.stopRecording();
		  
}
	  //opens the scene and handles selecting the text file
	  public void handleFile(ActionEvent actionEvent) throws IOException {
			try {
				//open file page
					FileChooser fileChooser = new FileChooser();
					File selectedFile = fileChooser.showOpenDialog(stg);
					selectedFilename = selectedFile.toString();
					Main n = new Main();
					  n.changeScene("file.fxml"); 
					
			} catch (NullPointerException e1) {
				//if they dont select a file, it send them back to upload file page
				handleBtn(actionEvent);
				
			}
}
	  //searches file when selected
	  public void lookup(ActionEvent actionEvent) throws IOException {
		  
		  text1.getChildren().clear();

				//reads the file depending on the os type

				if (System.getProperty("os.name").contains("Windows")){
					fileParts = selectedFilename.split("\\Desktop");
				}else{
					fileParts = selectedFilename.split("/Desktop");
				}

				String home = System.getProperty("user.home");



				//if(System.getProperty(os.name).equals())
				//setting the f directory to the users desktop
				File f;
				//file can be uploaded from the Desktop,Downloads, or Documents folder without getting an error
				try {
				 f = new File(home + File.separator + "Desktop" + File.separator + fileParts[1]);
				} catch (ArrayIndexOutOfBoundsException exception) {
					if (System.getProperty("os.name").contains("Windows")){
						fileParts = selectedFilename.split("\\Documents");
					}else{
						fileParts = selectedFilename.split("/Documents");
					}
					try {
						 f = new File(home + File.separator + "Documents" + File.separator + fileParts[1]);
						} catch (ArrayIndexOutOfBoundsException e) {
							if (System.getProperty("os.name").contains("Windows")){
								fileParts = selectedFilename.split("\\Downloads");
							}else{
								fileParts = selectedFilename.split("/Downloads");
							}
						 f = new File(home + File.separator + "Downloads" + File.separator + fileParts[1]);
						}
				}

				
				file = null;
				try {
					file = new Scanner(f);
				} catch (FileNotFoundException e1) {
					//if file isnt found, send them back to the upload file page
					e1.printStackTrace();
					handleBtn(actionEvent);
				}

				 val = 0;
						/*
				 * while look that searches for a specific word input by the user
				 * It will only quit if the user input quit when prompted to
				 */
					  String word = txtlbl.getText();					  
					String line = "";
					while(file.hasNextLine())  	
					{
						 line = file.nextLine();
						 
					
						if(line.indexOf(word) != -1)
						{
							try {
								 readfile(word);
							} catch (FileNotFoundException e1) {
								
								e1.printStackTrace();
							}
							
							//System.out.println(line);
							
							val = 1;

							break;
						}
						else
						{
							val = 0;
							continue;
						}
					}
					if(val == 0)
					{
						Text text0 = new Text("Word does not exist");
		            	text1.getChildren().add(text0);
					}
					}
					
	  public String readfile(String word) throws FileNotFoundException {
	        File transcript1 = new File(selectedFilename);
	        Scanner sc = new Scanner(transcript1);
	        String data = "";
	        String finalResult = "";
	        
	        while(sc.hasNextLine()){
	        	
	            data =data+sc.nextLine();
	            //marks each new line with an arbitary "%" to flag the ending and start of new lines
	            data = data + "%";
	        }
	        if(sc.hasNextLine() == false) {
				 data = data + ("0" + "\n");
				 data = data + "%";
			 }
	        //splits the string to an array with each element starting at the new line("%")
	        String[] lines = data.split("%");

	        //removes the first few unimportant characters from the file
	        data = data.substring(8);
	        List<String> text =new ArrayList(Arrays.asList(lines));

	        //removes an element if the element only contains whitespaces
	        text.removeIf(s -> s.matches("^\\s*$"));
	        text.remove(0);
	        HashMap<String, String> transcriptWithDate = new LinkedHashMap<>();

	        //stores the dates temporarily
	        ArrayList<String> dates = new ArrayList<String>();
	        //stores the subtitles temporarily
	        ArrayList<String> actualsubs = new ArrayList<String>();

	        for (int i = 0; i < text.size(); i++){

	            //removes the numbers at the start of each entry in the file
	            if(text.get(i).matches("^[0-9]*$")&& text.get(i).length()<4){
	                text.remove(i);
	            }
	        }
	        for(int i = 0; i< text.size();i++){

	            //if the index is even the the line contains subtitle information, else it contains date information
	            if(i%2==1){
	                actualsubs.add(text.get(i).trim());
	            }else{
	                dates.add(text.get(i));
	            }
	        }
	        for(int i = 0; i< dates.size()-1;i++){
	            transcriptWithDate.put(dates.get(i), actualsubs.get(i));
	        }
	        for(Map.Entry<String,String> entry : transcriptWithDate.entrySet()){
	        	//add text to the text field 
	            if(entry.getValue().contains(word)){
	            	Text text2 = new Text("This word is found at: "+ entry.getKey() + "\n");
	            	Text text3 = new Text(":::" + entry.getValue() + "\n" + "\n");
	            	text1.getChildren().add(text2);
	            	text1.getChildren().add(text3);
	                finalResult = entry.getValue();
	            }
	        }
	        
	        //the return method, you can then modify the key to store the time in integers instead of a string to do calculations with it
	        return finalResult;
	    }
			
		
	  class Program {
			long start = System.nanoTime();
			int count = 1;
			long previousTime = (long) 0.00;

		    private static Semaphore stopTranslationWithFileSemaphore;

		    AudioConfig audioConfig;
		    SpeechRecognizer recognizer;

		    /**
		     * stores The Result as text.
		     */
		    public String resultAsText = "";

		    /**
		     * The Speech config.
		     */
		    private SpeechConfig speechConfig;

		    /**
		     * Instantiates a new Program.
		     */
		    
		    
		    Program() {
		        //this uses my azure cognitive services subscription to initialize the transcription
		        this.speechConfig = SpeechConfig.fromSubscription("2abb39ac43cd4d77950965052aa02e56", "eastus");
		        this.audioConfig = AudioConfig.fromDefaultSpeakerOutput();
		        this.recognizer = new SpeechRecognizer(speechConfig, audioConfig);
		    }


		    public void beginRecord()throws InterruptedException, ExecutionException, FileNotFoundException{
		        DateFormat dateFormat = new SimpleDateFormat("MM-dd-HH-mm-ss");
		        Date date = new Date();
		        String currentDate = dateFormat.format(date);
		        //saves new transcript as a text file on the desktop

				String home = System.getProperty("user.home");
				 File outputfile = new File(home + File.separator + "Desktop" + File.separator + "transcript-"+currentDate+".txt");
				outputfile.getParentFile().mkdirs();

				PrintWriter pw = new PrintWriter(outputfile);
				pw.append("TRANSCRIPT" + "\n");
				pw.append("\n");


		        // initialize the semaphore.
		        stopTranslationWithFileSemaphore = new Semaphore(0);


		        //prints to the console that it is recognizing whenever it starts to recognize
				
				
				

		        recognizer.recognized.addEventListener((s, e) -> {
		            //if the speech is recognized then start to add text to the text field
		            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
						Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.1), a -> {
							liveText.getChildren().add(new Text( e.getResult().getText() + "\n"));
					}));
					while(stopRec != true) {
					timeline.play();
					break;
					}
					//add number and timestapms to the transcript file
		            	long current = System.nanoTime() - start;
		            	pw.append(count + "\n");
		            	pw.append(getTime(previousTime) + " --> " + getTime(current) + "\n");
		            	previousTime = current;
		                pw.append(e.getResult().getText());
		                pw.append("\n");
		                pw.append("\n");
		                count++;

		            }
		            else if (e.getResult().getReason() == ResultReason.NoMatch) {
		            	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.5), a -> {
							liveText.getChildren().add(new Text("    SPEECH COULD NOT BE RECOGNIZED" + "\n"));
					}));
		            timeline.setCycleCount(1);
					timeline.play();
					
		            }
		        });

		        recognizer.canceled.addEventListener((s, e) -> {
		        	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.5), a -> {
						liveText.getChildren().add(new Text("CANCELED: Reason=" + e.getReason() + "\n"));
				}));
	            timeline.setCycleCount(1);
				timeline.play();

		            if (e.getReason() == CancellationReason.Error) {
		            	Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(.5), a -> {
							liveText.getChildren().add(new Text("CANCELED: ErrorCode=" + e.getErrorCode() + "\n"));
					}));
		            timeline2.setCycleCount(1);
					timeline2.play();
		            }

		            stopTranslationWithFileSemaphore.release();
		        });

		        //stops recognizing once the user clicks the stop recognition button in the file
		        recognizer.sessionStopped.addEventListener((s, e) -> {
		        	Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(.5), a -> {
						liveText.getChildren().add(new Text("\n    Session stopped event." + "\n"));
				}));
	            timeline.setCycleCount(1);
				timeline.play();
		            stopTranslationWithFileSemaphore.release();
		            pw.flush();
		   		 pw.close();
		        });

		        //uses the simple output format to save computation time
		        speechConfig.setOutputFormat(OutputFormat.Simple);

		        // Starts continuous recognition
		        recognizer.startContinuousRecognitionAsync();

		        // Waits for completion.
		        stopTranslationWithFileSemaphore.acquire();
		        // Stops recognition.
		        recognizer.stopContinuousRecognitionAsync().get();
		    }
		    /**
		     * converts nanoseconds to seconds, minutes, and hours
		     * 
		     * @return string of the converted transcript time
		     */
		    private static String getTime(long time){
		    	DecimalFormat df = new DecimalFormat( "#00" );
		    	DecimalFormat df2 = new DecimalFormat( "#00.00" );
		    	
		    	
		        double sec = time / 1_000_000_000.00;
		        double min = (long) (time/ (6*(Math.pow(10, 10))));
		        double h = (long) (time/ (3.6*(Math.pow(10, 12))));
		        if(sec > 60) {
		        	sec = sec - 60*(min);
		        }
		        if(min > 60) {
		        	min = min - 60*(h);
		        }
		        
		        String finalTrans = (df.format(h) + ":" + df.format(min) + ":" + df2.format(sec));
				return finalTrans;
		    }

		    /**
		     * Stop recording.
		     */
		    public void stopRecording(){
		        recognizer.stopContinuousRecognitionAsync();

		    }

		    /**
		     * Get result as text string.
		     *
		     * @return the string
		     */
		    public String getResultAsText(){
		        return resultAsText;
		    }

		    /**
		     * Start recording.
		     */
		    public void startRecording(){
		        recognizer.startContinuousRecognitionAsync();
		    }



		}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
		}

	  

