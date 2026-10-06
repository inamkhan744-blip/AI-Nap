# AI-Nap Architecture Documentation

## Overview

AI-Nap follows the MVVM (Model-View-ViewModel) architecture pattern with clean architecture principles.

```
┌─────────────────────────────────────┐
│       UI Layer (Activities/Fragments) │
├─────────────────────────────────────┤
│       ViewModel Layer                 │
├─────────────────────────────────────┤
│       Repository Layer                │
├─────────────────────────────────────┤
│   Data Source (Network/Local)       │
├─────────────────────────────────────┤
│       Domain Layer (Use Cases)        │
└─────────────────────────────────────┘
```

## Layer Architecture

### 1. UI Layer
**Responsibility**: Display data and handle user interactions

- **Activities**: App screens and navigation
- **Fragments**: Modular UI components
- **ViewBinding**: Type-safe view access
- **Data Binding**: Automatic UI updates

### 2. ViewModel Layer
**Responsibility**: Manage UI state and handle business logic

```kotlin
class SleepTrackingViewModel(
    private val repository: SleepRepository
) : ViewModel() {
    private val _sleepState = MutableLiveData<SleepState>()
    val sleepState: LiveData<SleepState> = _sleepState
    
    fun startTracking(sleepData: SleepData) {
        viewModelScope.launch {
            _sleepState.value = SleepState.Loading
            try {
                val result = repository.trackSleep(sleepData)
                _sleepState.value = SleepState.Success(result)
            } catch (e: Exception) {
                _sleepState.value = SleepState.Error(e.message)
            }
        }
    }
}
```

### 3. Repository Layer
**Responsibility**: Abstract data sources and provide single source of truth

```kotlin
class SleepRepository(
    private val localDataSource: SleepLocalDataSource,
    private val remoteDataSource: SleepRemoteDataSource
) {
    suspend fun trackSleep(sleepData: SleepData): Result {
        return try {
            val result = remoteDataSource.trackSleep(sleepData)
            localDataSource.cacheSleepData(result)
            result
        } catch (e: NetworkException) {
            // Fallback to local storage
            localDataSource.getSleepData(sleepData.id)
        }
    }
}
```

### 4. Data Source Layer
**Responsibility**: Handle data persistence and network communication

#### Local Data Source
- **Room Database**: Local SQLite persistence
- **SharedPreferences**: Key-value storage
- **Data Caching**: Offline support

#### Remote Data Source
- **Retrofit**: HTTP client
- **Gemini API**: AI services
- **Cloud Sync**: Firebase or custom backend

### 5. Domain Layer
**Responsibility**: Business logic and use cases

```kotlin
class AnalyzeSleepUseCase(
    private val repository: SleepRepository
) {
    suspend operator fun invoke(sleepData: SleepData): Analysis {
        return repository.analyzeSleep(sleepData)
    }
}
```

## Data Flow

### Sleep Tracking Flow

```
User Input (Activity)
    ↓
ViewModel.startTracking()
    ↓
Repository.trackSleep()
    ↓
Data Source (Network + Local)
    ↓
Database Update
    ↓
LiveData Notification
    ↓
UI Update (Observe LiveData)
```

## Key Components

### 1. Room Database

```kotlin
@Entity(tableName = "sleep_records")
data class SleepRecord(
    @PrimaryKey val id: String,
    val date: Long,
    val duration: Int,
    val quality: Float,
    @Embedded val metrics: SleepMetrics
)

@Dao
interface SleepDao {
    @Insert suspend fun insert(record: SleepRecord)
    @Query("SELECT * FROM sleep_records") fun getAllRecords(): Flow<List<SleepRecord>>
}

@Database(entities = [SleepRecord::class], version = 1)
abstract class SleepDatabase : RoomDatabase() {
    abstract fun sleepDao(): SleepDao
}
```

### 2. API Service (Retrofit)

```kotlin
interface GeminiApiService {
    @POST("gemini-pro:generateContent")
    suspend fun generateContent(
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}
```

### 3. State Management

```kotlin
sealed class SleepState {
    object Loading : SleepState()
    data class Success(val data: SleepData) : SleepState()
    data class Error(val message: String) : SleepState()
}
```

## Dependency Injection

Using Hilt for dependency injection:

```kotlin
@HiltViewModel
class SleepTrackingViewModel @Inject constructor(
    private val repository: SleepRepository
) : ViewModel()

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideSleepRepository(
        localDataSource: SleepLocalDataSource,
        remoteDataSource: SleepRemoteDataSource
    ): SleepRepository {
        return SleepRepository(localDataSource, remoteDataSource)
    }
}
```

## Concurrency Model

### Coroutines Usage

```kotlin
viewModelScope.launch {
    val sleepData = withContext(Dispatchers.IO) {
        repository.getSleepData()
    }
    _state.value = sleepData
}
```

### Flow for Streams

```kotlin
val sleepRecords: Flow<List<SleepRecord>> = 
    repository.getSleepRecordsStream()
        .map { records ->
            records.sortedByDescending { it.date }
        }
```

## Database Schema

```
┌──────────────────────┐
│   SleepRecords       │
├──────────────────────┤
│ id (PK)              │
│ date                 │
│ duration             │
│ quality              │
│ deep_sleep_duration  │
│ awakenings           │
│ sync_status          │
└──────────────────────┘

┌──────────────────────┐
│   NapSessions        │
├──────────────────────┤
│ id (PK)              │
│ start_time           │
│ end_time             │
│ quality              │
│ record_id (FK)       │
└──────────────────────┘

┌──────────────────────┐
│   Recommendations    │
├──────────────────────┤
│ id (PK)              │
│ record_id (FK)       │
│ recommendation_text  │
│ created_at           │
│ dismissed            │
└──────────────────────┘
```

## Error Handling Strategy

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

// Usage in Repository
suspend fun <T> safeCall(
    apiCall: suspend () -> T
): Result<T> = try {
    Result.Success(apiCall())
} catch (e: IOException) {
    Result.Error(NetworkException(e))
} catch (e: Exception) {
    Result.Error(GenericException(e))
}
```

## Network Strategy

### Offline Support

```kotlin
class CachedRepository(
    private val apiService: ApiService,
    private val database: AppDatabase
) {
    suspend fun getSleepData(): Result<List<SleepData>> {
        return try {
            val data = apiService.fetchSleepData()
            database.insert(data)
            Result.Success(data)
        } catch (e: NetworkException) {
            val cached = database.getSleepData()
            Result.Success(cached)
        }
    }
}
```

### Sync Strategy

- **Automatic sync**: Every 15 minutes if connected
- **Manual sync**: User-initiated refresh
- **Background sync**: WorkManager for periodic sync
- **Conflict resolution**: Last-write-wins

## Performance Considerations

1. **Database Indexing**: Fast queries
2. **Pagination**: Load data in chunks
3. **Caching**: Reduce API calls
4. **Lazy Loading**: Load UI components on demand
5. **Image Optimization**: Compress images
6. **ANR Prevention**: Long operations on background threads

## Security Architecture

```
┌─────────────────────────────────────┐
│   Encryption Layer                   │
├─────────────────────────────────────┤
│   API Key Management (Encrypted)    │
├─────────────────────────────────────┤
│   Secure Preferences                 │
├─────────────────────────────────────┤
│   HTTPS Only Communications         │
├─────────────────────────────────────┤
│   Certificate Pinning                │
└─────────────────────────────────────┘
```

## Module Structure

```
app/
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── models/
│   └── usecase/
├── presentation/
│   ├── ui/
│   └── viewmodel/
├── di/
└── utils/
```

---

**Architecture Version**: 1.0
**Last Updated**: October 6, 2026
