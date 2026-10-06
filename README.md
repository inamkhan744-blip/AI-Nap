# AI-Nap 🤖😴

An AI-powered sleep tracking and nap management Android application that uses machine learning to optimize your rest patterns.

## Features ✨

- **Smart Nap Detection**: AI-powered algorithms detect and track your sleep patterns
- **Sleep Analytics**: Detailed insights into sleep quality and duration
- **Personalized Recommendations**: Get AI-generated suggestions for better sleep
- **Sleep Schedule Management**: Organize and track your sleep schedules
- **Notification System**: Smart alerts for optimal nap times
- **Dark Mode Support**: Easy on the eyes with built-in dark theme
- **Offline Support**: Works without internet connection
- **Cloud Sync**: Sync your data across devices
- **Sleep History**: Track your sleep patterns over time
- **Integration Ready**: Ready to integrate with wearables and health apps

## Tech Stack 🛠️

- **Language**: Kotlin
- **Framework**: Android
- **Build System**: Gradle
- **Architecture**: MVVM with Clean Architecture
- **API Integration**: Google Gemini AI

## Requirements 📋

- Android API 24+
- Android Studio Arctic Fox or later
- Gradle 7.0+
- JDK 11+

## Installation 📦

1. Clone the repository:
```bash
git clone https://github.com/inamkhan744-blip/AI-Nap.git
cd AI-Nap
```

2. Create a `.env` file in the root directory:
```bash
cp .env.example .env
```

3. Add your API keys to `.env`:
```
GEMINI_API_KEY=your_api_key_here
```

4. Build and run:
```bash
./gradlew build
./gradlew installDebug
```

## Project Structure 📁

```
AI-Nap/
├── app/                    # Main application module
├── .github/                # GitHub workflows and CI/CD
├── gradle/                 # Gradle wrapper
├── build.gradle.kts        # Project build configuration
├── settings.gradle.kts     # Project settings
├── .env.example           # Environment variables template
└── metadata.json          # Project metadata
```

## API Keys Setup 🔑

This project uses Google Gemini API for AI features. To set up:

1. Get your API key from [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Add it to your `.env` file
3. The app will load it at runtime

## Usage 🚀

1. Launch the app on your Android device
2. Allow necessary permissions (location, health data)
3. Set up your sleep profile
4. Start tracking your sleep and naps
5. View analytics and get AI recommendations

## Development 👨‍💻

### Building the Project
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

## Contributing 🤝

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Known Issues 🐛

- None reported yet. Please report any issues you find!

## Roadmap 🗺️

- [ ] Wearable device integration
- [ ] Real-time sleep quality monitoring
- [ ] Advanced sleep cycle analysis
- [ ] Multi-language support
- [ ] Offline mode improvements
- [ ] Social features for sleep tracking communities

## License 📄

This project is licensed under the MIT License - see the LICENSE file for details.

## Support 💬

For support, email inamkhan744@gmail.com or open an issue on GitHub.

## Changelog 📝

### Version 1.0.0
- Initial release
- Basic sleep tracking
- AI-powered recommendations
- Android app with Kotlin

---

**Made with ❤️ by inamkhan744-blip**
