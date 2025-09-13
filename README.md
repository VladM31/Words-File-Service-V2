# File Service V2

File Service V2 is a Spring Boot-based application designed to handle file uploads, processing, and validation for
various file types, including images and audio files. The service ensures proper handling, validation, and storage of
uploaded files while maintaining high performance and scalability.

## Features

- **Image Upload**:
    - Resizes images to a maximum dimension.
    - Validates image content using custom analyzers (e.g., NSFW detection).
    - Supports multiple image formats: `jpg`, `jpeg`, `png`, `webp`.

- **Audio Upload**:
    - Validates and processes MP3 files.
    - Compresses audio files to a target bitrate if necessary.
    - Ensures proper handling of temporary files during processing.

- **Custom File Name Generation**:
    - Generates unique file names with optional prefixes.
    - Ensures no file name collisions.

- **Temporary File Management**:
    - Automatically deletes temporary files after processing.

## Technologies Used

- **Backend**: Java, Spring Boot
- **Build Tool**: Maven
- **Libraries**:
    - `ws.schild.jave` for audio processing.
    - `javax.imageio` for image processing.
    - Lombok for boilerplate code reduction.

## Project Structure

- `domain/services`: Contains core services for file handling.
- `domain/models`: Defines models such as `UploadResult` and enums like `FileDirectory`.
- `utils`: Utility classes for file and image processing.
- `exceptions`: Custom exceptions for error handling.

## How to Run

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd file-service-v2
   ```

2. **Build the Project**:
   ```bash
   mvn clean install
   ```

3. **Run the Application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the Application**:
   The application will be available at `http://localhost:8080`.

## Configuration

- **Application Properties**:
  Configure the application in `src/main/resources/application.properties`:
  ```properties
  file.upload.directory=/path/to/upload/directory
  file.temp.directory=/path/to/temp/directory
  image.upload.max-dimension=1024
  ```

## API Endpoints

- **Image Upload**:
    - `POST /upload/image`
    - Request: Multipart file upload.
    - Response: JSON with the uploaded file name.

- **Audio Upload**:
    - `POST /upload/audio`
    - Request: Multipart file upload.
    - Response: JSON with the uploaded file name.

## Contribution

Contributions are welcome! Feel free to open issues or submit pull requests.

## License

This project is licensed under the [MIT License](LICENSE).
