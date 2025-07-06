package com.imagecrypto;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;

import java.io.File;
import java.util.Base64;

public class GUI {

    // File selection variables
    private File selectedImageFile;
    private File selectedWavFile;
    private File selectedSaveDirectory;

    // Status UI elements
    private final ProgressIndicator loader = new ProgressIndicator();
    private final Label finalStatusIcon = new Label();
    private final Label imageStatusIcon = new Label();
    private final Label saveStatusIcon = new Label();
    private final Label wavStatusIcon = new Label();
    private final Label statusLabel = new Label();

    // Input field
    private final TextField keyInputField = new TextField();

    // Buttons (declared here for enabling/disabling)
    private Button encryptBtn;
    private Button decryptBtn;

    public Pane createContent(Window window) {

        // --- Buttons ---
        Button browseImageBtn = new Button("Browse Image for Encryption");
        Button chooseSaveBtn = new Button("Choose Save Location");
        encryptBtn = new Button("Encrypt");

        Button browseWavBtn = new Button("Browse Encrypted WAV");
        decryptBtn = new Button("Decrypt");

        // --- Layouts ---
        HBox browseImageRow = new HBox(10, browseImageBtn, imageStatusIcon);
        HBox saveLocationRow = new HBox(10, chooseSaveBtn, saveStatusIcon);
        HBox browseWavRow = new HBox(10, browseWavBtn, wavStatusIcon);
        HBox loadingBox = new HBox(10, loader, finalStatusIcon);

        loader.setVisible(false);
        finalStatusIcon.setText("");
        keyInputField.setPromptText("Enter Base64 Decryption Key");

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f4f4f4;");

        // --- Actions ---
        browseImageBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Image File");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png")
            );
            selectedImageFile = fileChooser.showOpenDialog(window);
            if (selectedImageFile != null) {
                updateStatus("📷 Image selected: " + selectedImageFile.getName());
                imageStatusIcon.setText("✅");
            } else {
                updateStatus("❌ No image selected.");
                imageStatusIcon.setText("❌");
            }
        });

        chooseSaveBtn.setOnAction(e -> {
            DirectoryChooser dirChooser = new DirectoryChooser();
            dirChooser.setTitle("Select Save Location");
            selectedSaveDirectory = dirChooser.showDialog(window);
            if (selectedSaveDirectory != null) {
                updateStatus("📁 Save location: " + selectedSaveDirectory.getAbsolutePath());
                saveStatusIcon.setText("✅");
            } else {
                updateStatus("❌ No save location selected.");
                saveStatusIcon.setText("❌");
            }
        });

        browseWavBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select WAV File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("WAV Files", "*.wav")
            );
            selectedWavFile = fileChooser.showOpenDialog(window);
            if (selectedWavFile != null) {
                updateStatus("🎵 WAV selected: " + selectedWavFile.getName());
                wavStatusIcon.setText("✅");
            } else {
                updateStatus("❌ No WAV file selected.");
                wavStatusIcon.setText("❌");
            }
        });

        encryptBtn.setOnAction(e -> handleEncrypt());
        decryptBtn.setOnAction(e -> handleDecrypt());

        // --- Final UI Assembly ---
        layout.getChildren().addAll(
                new Label("🔐 Encryption"), browseImageRow, saveLocationRow, encryptBtn,
                new Separator(),
                new Label("🔓 Decryption"), browseWavRow, keyInputField, decryptBtn,
                new Separator(),
                loadingBox,
                statusLabel
        );

        return layout;
    }

    private void updateStatus(String message) {
        statusLabel.setText(statusLabel.getText() + "\n" + message);
    }

    private void handleEncrypt() {
        if (selectedImageFile == null || selectedSaveDirectory == null) {
            updateStatus("❌ Please select an image and a save location.");
            return;
        }

        loader.setVisible(true);
        finalStatusIcon.setText("");
        encryptBtn.setDisable(true);
        updateStatus("🔄 Starting encryption...");

        Task<Void> encryptTask = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    int[][] grayscale = ImageToGrayscale.convert(
                            selectedImageFile.getAbsolutePath(),
                            selectedSaveDirectory.getAbsolutePath()
                    );

                    byte[] flat = EncryptionAndEmbedding.flattenedGrayscale(grayscale);
                    byte[] key = EncryptionAndEmbedding.generateRandomKey();
                    byte[] encrypted = EncryptionAndEmbedding.encrypt(flat, key);

                    String keyPath = selectedSaveDirectory.getAbsolutePath() + "/keyfile.txt";
                    EncryptionAndEmbedding.keyFile(key, keyPath);

                    byte[] audioData = GrayscaleToAudio.convertGrayscaleToAudio(grayscale);
                    String audioPath = selectedSaveDirectory.getAbsolutePath() + "/encrypted_audio.wav";
                    File outFile = new File(audioPath);
                    if (outFile.exists()) outFile.delete();  // Ensure old content is gone
                    GrayscaleToAudio.writeWAVWithEncryption(audioData, encrypted, audioPath);

                    Platform.runLater(() -> {
                        updateStatus("✅ Encryption completed.\nSaved:\n• " + audioPath + "\n• " + keyPath);
                        finalStatusIcon.setText("✅");
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        updateStatus("❌ Encryption failed: " + e.getMessage());
                        finalStatusIcon.setText("❌");
                    });
                } finally {
                    Platform.runLater(() -> {
                        loader.setVisible(false);
                        encryptBtn.setDisable(false);
                    });
                }
                return null;
            }
        };

        new Thread(encryptTask).start();
    }

    private void handleDecrypt() {
        if (selectedWavFile == null || keyInputField.getText().trim().isEmpty()) {
            updateStatus("❌ Please select WAV file and enter the Base64 key.");
            return;
        }

        loader.setVisible(true);
        finalStatusIcon.setText("");
        decryptBtn.setDisable(true);
        updateStatus("🔄 Starting decryption...");

        Task<Void> decryptTask = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    byte[] encryptedData = Decryption.extractedData(selectedWavFile.getAbsolutePath());

                    byte[] keyBytes = Base64.getDecoder().decode(keyInputField.getText().trim());
                    byte[] decryptedData = Decryption.keyRecognition(encryptedData, keyBytes);

                    int[][] grayscale = Decryption.reconstructed_2D(decryptedData);
                    String imageOutputPath = selectedWavFile.getParent() + "/decrypted_image.png";
                    Decryption.createImage(grayscale, imageOutputPath);

                    Platform.runLater(() -> {
                        updateStatus("✅ Decryption successful.\nImage saved: " + imageOutputPath);
                        finalStatusIcon.setText("✅");
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> {
                        updateStatus("❌ Decryption failed: " + e.getMessage());
                        finalStatusIcon.setText("❌");
                    });
                } finally {
                    Platform.runLater(() -> {
                        loader.setVisible(false);
                        decryptBtn.setDisable(false);
                    });
                }
                return null;
            }
        };

        new Thread(decryptTask).start();
    }
}
