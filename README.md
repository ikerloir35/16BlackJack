# Droid Blackjack Game

## Environment Setup

This project uses environment variables to manage sensitive configuration data like API keys. Follow these steps to set up your development environment:

### 1. Environment Variables Setup

1. Copy the example environment file:
   ```bash
   cp .env.example .env
   ```

2. Edit the `.env` file with your actual API keys:
   ```
   # Google API Keys
   GOOGLE_API_KEY=your_google_api_key_here
   GOOGLE_SITES_API_KEY=your_google_sites_api_key_here

   # Firebase Configuration
   FIREBASE_WEB_CLIENT_ID=your_firebase_web_client_id_here
   ```

### 2. Required API Keys

You'll need the following API keys:

1. **Google API Key**
   - Used for Google services integration
   - Get it from the [Google Cloud Console](https://console.cloud.google.com)

2. **Google Sites API Key**
   - Used for accessing Google Sites content
   - Get it from the [Google Cloud Console](https://console.cloud.google.com)

3. **Firebase Web Client ID**
   - Used for Firebase Authentication
   - Get it from your Firebase project settings
   - Format: `[PROJECT_NUMBER]-[HASH].apps.googleusercontent.com`

### 3. Security Notes

- Never commit the `.env` file to version control
- The `.env` file is already in `.gitignore`
- Keep your API keys secure and rotate them if they are ever exposed
- Use different API keys for development and production environments

### 4. Build Process

The build process automatically:
1. Copies the `.env` file to the app's assets directory
2. Makes the environment variables available to the app at runtime

### 5. Troubleshooting

If you encounter issues:

1. **Missing Environment Variables**
   - Ensure the `.env` file exists in the project root
   - Check that all required variables are set
   - Verify the file format (no spaces around the `=` sign)

2. **Build Failures**
   - Clean and rebuild the project
   - Check that the `.env` file is properly formatted
   - Verify file permissions

3. **Runtime Errors**
   - Check the logcat for specific error messages
   - Verify that the Config class is properly initialized
   - Ensure all required API keys are valid

### 6. Development Workflow

1. When starting development:
   ```bash
   # Clone the repository
   git clone [repository-url]
   
   # Set up environment variables
   cp .env.example .env
   # Edit .env with your keys
   
   # Build and run
   ./gradlew assembleDebug
   ```

2. When adding new environment variables:
   - Add them to `.env.example`
   - Add them to the `Config` class
   - Update this README

### 7. Production Deployment

For production deployment:
1. Use different API keys for production
2. Set up proper key restrictions in Google Cloud Console
3. Consider using a secure key management service
4. Follow the principle of least privilege for API access

## Contributing

No public contribution Allowed

