# AI-Nap API Documentation

## Overview

AI-Nap uses Google Gemini API for AI-powered sleep analysis and recommendations.

## Base URL

```
https://generativelanguage.googleapis.com/v1beta/models
```

## Authentication

All API requests require an API key:

```
X-Goog-Api-Key: YOUR_API_KEY
```

Set your API key in the `.env` file:
```
GEMINI_API_KEY=your_api_key_here
```

## Endpoints

### 1. Sleep Analysis

**Generate AI Analysis for Sleep Data**

```
POST /gemini-pro:generateContent
```

#### Request Body

```json
{
  "contents": [{
    "parts": [{
      "text": "Analyze this sleep data and provide recommendations:\n\n Sleep Duration: 7 hours\nSleep Quality: 85%\nNumber of Awakenings: 2\nAverage Deep Sleep: 2 hours\n\nProvide health recommendations."
    }]
  }]
}
```

#### Response

```json
{
  "candidates": [{
    "content": {
      "parts": [{
        "text": "Based on your sleep data...\n\n## Sleep Quality Assessment\n- Your 7-hour sleep duration is excellent...\n\n## Recommendations\n1. Maintain consistent sleep schedule...\n"
      }]
    },
    "finishReason": "STOP"
  }]
}
```

### 2. Nap Recommendations

**Get Personalized Nap Recommendations**

```
POST /gemini-pro:generateContent
```

#### Request Body

```json
{
  "contents": [{
    "parts": [{
      "text": "Based on my sleep pattern (last night: 6.5 hours, quality: 75%), suggest optimal nap time and duration for today. My work hours are 9-5."
    }]
  }]
}
```

#### Response

```json
{
  "candidates": [{
    "content": {
      "parts": [{
        "text": "Based on your sleep pattern and schedule, here are my recommendations:\n\n## Optimal Nap Time: 2:30 PM\n## Duration: 20 minutes\n## Expected Benefits: Improved alertness..."
      }]
    }
  }]
}
```

### 3. Sleep Tips Generation

**Generate Personalized Sleep Improvement Tips**

```
POST /gemini-pro:generateContent
```

#### Request Body

```json
{
  "contents": [{
    "parts": [{
      "text": "I've been sleeping 6-7 hours but feel tired. Generate 5 science-backed sleep improvement tips for someone who:\n- Works in tech\n- Uses screen until 11 PM\n- Exercises at 6 AM\n- Has irregular sleep schedule on weekends"
    }]
  }]
}
```

#### Response

```json
{
  "candidates": [{
    "content": {
      "parts": [{
        "text": "Here are 5 personalized sleep tips for your lifestyle:\n\n1. **Blue Light Management**: Implement a 1-hour screen-free period...\n2. **Sleep Consistency**: Maintain consistent wake times..."
      }]
    }
  }]
}
```

## Request Format

All requests follow this format:

```kotlin
val request = GenerateContentRequest(
    contents = listOf(
        Content(
            parts = listOf(
                TextPart(text = "Your query")
            )
        )
    )
)
```

## Response Format

All responses follow this format:

```kotlin
data class GenerateContentResponse(
    val candidates: List<Candidate>
)

data class Candidate(
    val content: Content,
    val finishReason: String
)

data class Content(
    val parts: List<Part>
)

data class TextPart(
    val text: String
)
```

## Error Handling

### Common Error Codes

| Code | Status | Description |
|------|--------|-------------|
| 400 | Bad Request | Invalid request format |
| 401 | Unauthorized | Invalid API key |
| 403 | Forbidden | API key quota exceeded |
| 429 | Rate Limited | Too many requests |
| 500 | Server Error | Internal server error |

### Error Response

```json
{
  "error": {
    "code": 400,
    "message": "Invalid request format",
    "status": "INVALID_ARGUMENT"
  }
}
```

## Rate Limits

- **Requests per minute**: 60
- **Daily quota**: 1,000 requests
- **Max tokens per request**: 4,096

## Best Practices

1. **Input Validation**: Always validate user input before sending to API
2. **Error Handling**: Implement proper error handling and retry logic
3. **Caching**: Cache responses to reduce API calls
4. **Async Operations**: Use coroutines for API calls
5. **Offline Support**: Implement offline mode for when API is unavailable

## Example Usage in Kotlin

```kotlin
// Initialize client
val generativeModel = GenerativeModel(
    modelName = "gemini-pro",
    apiKey = BuildConfig.GEMINI_API_KEY
)

// Generate content
suspend fun analyzeSleep(sleepData: SleepData): String {
    return try {
        val prompt = """
            Analyze this sleep data:
            Duration: ${sleepData.duration}
            Quality: ${sleepData.quality}%
            Recommend improvements.
        """.trimIndent()
        
        val response = generativeModel.generateContent(prompt)
        response.text ?: "No response"
    } catch (e: Exception) {
        handleError(e)
        "Analysis failed"
    }
}
```

## Rate Limit Handling

```kotlin
suspend fun <T> withRetry(
    maxRetries: Int = 3,
    delay: Long = 1000,
    block: suspend () -> T
): T {
    var lastException: Exception? = null
    
    repeat(maxRetries) { attempt ->
        try {
            return block()
        } catch (e: Exception) {
            lastException = e
            if (attempt < maxRetries - 1) {
                delay(delay * (attempt + 1))
            }
        }
    }
    
    throw lastException ?: Exception("Failed after $maxRetries attempts")
}
```

## Useful Resources

- [Google Gemini API Docs](https://ai.google.dev/docs)
- [Gemini Pro Model](https://ai.google.dev/models/gemini-pro)
- [Android Integration Guide](https://ai.google.dev/tutorials/android_quickstart)

## Support

For API-related issues:
- Email: inamkhan744@gmail.com
- Documentation: https://ai.google.dev/docs
- Issues: GitHub Issues

---

**Last Updated**: October 6, 2026
