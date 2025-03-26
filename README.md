# 16 BlackJack Android Game

## Project Summary

### Purpose
This Android application is a modern implementation of the classic BlackJack card game. The game features a clean, intuitive interface while maintaining the traditional rules and excitement of BlackJack.

### Technology Stack
- **Language**: Kotlin
- **Platform**: Android
- **Architecture**: MVVM (Model-View-ViewModel)
- **Key Technologies**:
  - Firebase Authentication
  - Google Sign-In Integration
  - Material Design Components
  - Kotlin Coroutines for async operations
  - Gradle with Kotlin DSL
  - Environment-based Configuration Management

### Features
- **Authentication**:
  - Google Sign-In integration
  - Secure user session management
  - Persistent login state
- **Game Features**:
  - Classic BlackJack rules implementation
  - Interactive card dealing animation
  - Real-time game state management
  - Score tracking
  - Betting system
- **User Interface**:
  - Material Design 3 components
  - Responsive layout
  - Dark/Light theme support
  - Intuitive game controls
- **Technical Features**:
  - Secure environment configuration
  - Offline capability
  - Performance optimized
  - Modular architecture

### Security
- **Authentication Security**:
  - OAuth 2.0 implementation for Google Sign-In
  - Secure token management
  - Session handling
- **Data Security**:
  - Environment-based configuration
  - No hardcoded sensitive data
  - Secure API key management
- **Best Practices**:
  - Regular security updates
  - Secure coding standards
  - Input validation
  - Error handling

### Co-Developers
- **Lead Developer**: [Your Name]
   - Iker López Iribas
   - Damià Belles Sampera
- **Contributors**:
   - Sebastián Dos Santos Librandi

## Environment Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 21 or higher
- Git

### Required API Keys
1. **Google API Key**
   - Purpose: Firebase Authentication and Google Sign-In
   - Location: Google Cloud Console
   - Required Scopes: Google Sign-In API

2. **Firebase Web Client ID**
   - Purpose: Firebase Authentication
   - Location: Firebase Console
   - Required for: Google Sign-In integration

### Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/16BlackJack_repo.git
   cd 16BlackJack_repo
   ```

2. **Environment Configuration**
   - Copy `.env.example` to `.env`
   - Fill in the required API keys:
     ```
     GOOGLE_API_KEY=your_google_api_key
     GOOGLE_SITES_API_KEY=your_google_sites_api_key
     FIREBASE_WEB_CLIENT_ID=your_firebase_web_client_id
     ```

3. **Build Configuration**
   - The project uses Gradle with Kotlin DSL
   - Environment variables are automatically copied to assets during build
   - No manual configuration needed after setting up `.env`

4. **Run the Application**
   - Open the project in Android Studio
   - Sync project with Gradle files
   - Run on an emulator or physical device

### Security Best Practices

1. **API Key Management**
   - Never commit `.env` file to version control
   - Use different API keys for development and production
   - Regularly rotate API keys
   - Restrict API key usage in Google Cloud Console

2. **Code Security**
   - All sensitive data is loaded from environment variables
   - No hardcoded credentials in source code
   - Secure error handling and logging
   - Regular security audits

3. **Development Workflow**
   - Use feature branches for development
   - Code review required for sensitive changes
   - Regular security updates
   - Automated security scanning

### Troubleshooting

1. **Build Issues**
   - Ensure JDK 21 is properly installed
   - Verify environment variables are set correctly
   - Check Gradle sync status
   - Clean and rebuild project

2. **Runtime Issues**
   - Verify API keys are valid
   - Check internet connectivity
   - Review logcat for detailed error messages
   - Ensure Firebase configuration is correct

3. **Authentication Issues**
   - Verify Google Sign-In configuration
   - Check SHA-1 fingerprint in Firebase Console
   - Ensure OAuth consent screen is configured
   - Verify package name matches Firebase configuration

### Production Deployment

1. **Pre-deployment Checklist**
   - Update API keys for production
   - Verify all security measures
   - Test on multiple devices
   - Review error handling
   - Check performance metrics

2. **Release Process**
   - Update version numbers
   - Generate signed APK/Bundle
   - Test release build
   - Deploy to Google Play Store

3. **Post-deployment**
   - Monitor crash reports
   - Track user feedback
   - Plan regular updates
   - Maintain security patches

## License
[Your License Here]

## Contributing
[Your Contributing Guidelines Here]

