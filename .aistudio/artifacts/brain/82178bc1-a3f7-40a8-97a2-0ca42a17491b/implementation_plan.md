# Implementation Plan - CineVerse Streaming App

CineVerse is a premium Movie and Anime streaming application integrated with a custom backend (`https://anime-hjku.onrender.com`). It provides a seamless experience for discovering trending content, searching for specific titles, and high-quality video playback using Media3 ExoPlayer.

> [!IMPORTANT]
> The `/trending` endpoint currently returns a `401 Unauthorized` error from the upstream provider. I will implement a robust error state for this screen and provide diagnostic information as requested.

### Confirmed Decisions & UX
- **Architecture**: MVVM (Model-View-ViewModel) with Jetpack Compose.
- **Navigation**: Type-safe Navigation Compose using Kotlin Serialization.
- **Networking**: Retrofit + OkHttp with Moshi for JSON parsing.
- **Video Playback**: Media3 ExoPlayer with support for multiple quality options (720p, 1080p, etc.) and track selection.
- **Image Loading**: Coil for efficient image fetching and caching.
- **Visual Theme**: "Luxury Dark" - A deep charcoal background with vibrant accent colors (Gold/Cyan) to give it a premium cinema feel.

## 1. User Experience & Visual Design
- **Home (Trending)**: A vertical scrolling list of trending movies and anime with high-quality posters.
- **Search**: A dedicated search screen with real-time (debounced) search results.
- **Details Screen**: Comprehensive metadata including synopsis, genres, and an episode/season selector for series/anime.
- **Player Screen**: A full-screen immersive video player with:
  - Play/Pause, Seek, and Volume controls.
  - Quality selector menu (using ExoPlayer's `TrackSelectionParameters`).
  - Loading overlays for buffering and source switching.

## 2. Technical Architecture

```unicode
┌──────────────────────────────────────────────────────────┐
│                    Jetpack Compose UI                    │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │ Home Screen  │  │ Detail Screen│  │ Player Screen  │  │
│  └──────┬───────┘  └──────┬───────┘  └───────┬────────┘  │
└─────────┼─────────────────┼──────────────────┼───────────┘
          ▼                 ▼                  ▼
┌──────────────────────────────────────────────────────────┐
│                       ViewModels                         │
│  (State Management, Navigation, Player Lifecycle)        │
└──────────────────────┬───────────────────────────────────┘
                       ▼
┌──────────────────────────────────────────────────────────┐
│                      Repository                          │
│  (Data Orchestration, Error Handling, Normalization)     │
└──────┬───────────────────────────────────────────┬───────┘
       ▼                                           ▼
┌──────────────────────┐          ┌────────────────────────┐
│   Retrofit Service   │          │   Media3 ExoPlayer     │
│ (Custom Backend API) │          │ (Playback Engine)      │
└──────────┬───────────┘          └────────────────────────┘
           ▼
┌──────────────────────┐
│ https://anime-hjku   │
│ .onrender.com        │
└──────────────────────┘
```

## 3. Implementation Steps

### Phase 1: Data Layer & API Integration
- Define Kotlin data classes for all API responses (`TrendingResponse`, `DetailResponse`, `StreamResponse`).
- Create `CineVerseApi` Retrofit interface.
- Implement `MediaRepository` to handle data fetching and error mapping.

### Phase 2: Core UI & Navigation
- Setup `NavHost` with routes for Home, Search, Detail, and Player.
- Implement `HomeViewModel` and `HomeContent` with loading/error/retry states.
- Implement `SearchViewModel` and `SearchScreen`.

### Phase 3: Detail & Playback
- Build the `DetailScreen` with support for Seasons/Episodes.
- Implement the `PlayerScreen` using `AndroidView` to wrap `PlayerView`.
- Integrate `TrackSelectionParameters` to allow users to switch between video qualities.

### Phase 4: Reliability & Polish
- Handle cold starts for the Render backend (displaying "Waking up server..." if needed).
- Refine animations (staggered list reveals, smooth transitions).
- Final verification of all endpoints and error states.
