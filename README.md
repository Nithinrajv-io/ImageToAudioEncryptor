IMAGE TO AUDIO ENCRYPTOR AND DECRYPTOR
---

A tool that securely converts images into audio files using grayscale mapping and AES encryption — and back.

---

## 💡 What It Does / Features

- Converts an image to `.wav` file
- Encrypts the image data with AES
- The `.wav` contains audio of the image data that can be decrypted by only having both the audio and the key.
- Supports full decryption of the `.wav` file back to image using a unique key generated and given to the user while encrypting
- A JavaFX GUI for clean and easier use

---

## 🔧 How to Use

### 🔒 Encryption
1. Select an image (JPG/PNG).
2. Choose where to save the output.
3. Click **Encrypt**.
4. It generates:
   - `encrypted_audio.wav`
   - `keyfile.txt`

### 🔓 Decryption
1. Select the `.wav` file.
2. Give the Base64 key from `keyfile.txt`.
3. Click **Decrypt**.
4. Outputs: `decrypted_image.png`
---

### 🚧 Current Limitations (but not permanent)
- The decrypted regenerated image will be of 150px x 150px. This was done to reduce the audio file size due to the current encrypting method.
  But using an equal height and width dimension image as input can quite clearly give the decrypted image with most of the information retained.
- The audio file generated can be sometimes long depending on the image input dimensions.
